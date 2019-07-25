// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.ContinuousInfo;
import de.mossgrabers.framework.utils.LatestTaskExecutor;
import de.mossgrabers.framework.utils.TriggerInfo;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Abstract implementation of a Control Surface.
 *
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractControlSurface<C extends Configuration> implements IControlSurface<C>
{
    protected static final int                                      BUTTON_STATE_INTERVAL = 400;
    protected static final int                                      NUM_NOTES             = 128;
    protected static final int                                      NUM_BUTTONS           = 256;

    protected final IHost                                           host;
    protected final C                                               configuration;
    protected final ColorManager                                    colorManager;
    protected final IMidiOutput                                     output;
    protected final IMidiInput                                      input;

    protected final ViewManager                                     viewManager           = new ViewManager ();
    protected final ModeManager                                     modeManager           = new ModeManager ();

    protected int                                                   selectButtonId        = -1;
    protected int                                                   shiftButtonId         = -1;
    protected int                                                   deleteButtonId        = -1;
    protected int                                                   soloButtonId          = -1;
    protected int                                                   muteButtonId          = -1;
    protected int                                                   leftButtonId          = -1;
    protected int                                                   rightButtonId         = -1;
    protected int                                                   upButtonId            = -1;
    protected int                                                   downButtonId          = -1;

    private final TriggerInfo [] []                                 triggerInfos          = new TriggerInfo [16] [NUM_BUTTONS];
    private final ContinuousInfo [] []                              continuousInfos       = new ContinuousInfo [16] [NUM_BUTTONS];
    private final int []                                            noteVelocities;

    protected Display                                               display;
    protected final PadGrid                                         pads;
    protected final Map<Integer, Map<Integer, TriggerCommandID>>    triggerCommands       = new HashMap<> ();
    protected final Map<Integer, Map<Integer, ContinuousCommandID>> continuousCommands    = new HashMap<> ();
    protected final Map<Integer, TriggerCommandID>                  noteCommands          = new HashMap<> ();

    private final boolean []                                        gridNoteConsumed;
    private final ButtonEvent []                                    gridNoteStates;
    private final int []                                            gridNoteVelocities;
    private int []                                                  keyTranslationTable;

    private final LatestTaskExecutor                                flushExecutor         = new LatestTaskExecutor ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param configuration The configuration
     * @param colorManager
     * @param output The midi output
     * @param input The midi input
     * @param padGrid The pads if any, may be null
     */
    public AbstractControlSurface (final IHost host, final C configuration, final ColorManager colorManager, final IMidiOutput output, final IMidiInput input, final PadGrid padGrid)
    {
        this.host = host;
        this.configuration = configuration;
        this.colorManager = colorManager;
        this.pads = padGrid;

        this.output = output;
        this.input = input;
        if (this.input != null)
            this.input.setMidiCallback (this::handleMidi);

        // Notes
        this.noteVelocities = new int [NUM_NOTES];

        // Grid notes
        this.gridNoteConsumed = new boolean [NUM_NOTES];
        Arrays.fill (this.gridNoteConsumed, false);
        final int size = 8 * 8;
        this.gridNoteStates = new ButtonEvent [NUM_NOTES];
        this.gridNoteVelocities = new int [NUM_NOTES];
        for (int i = 0; i < size; i++)
        {
            this.gridNoteStates[i] = ButtonEvent.UP;
            this.gridNoteVelocities[i] = 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public ViewManager getViewManager ()
    {
        return this.viewManager;
    }


    /** {@inheritDoc} */
    @Override
    public ModeManager getModeManager ()
    {
        return this.modeManager;
    }


    /** {@inheritDoc} */
    @Override
    public C getConfiguration ()
    {
        return this.configuration;
    }


    /** {@inheritDoc} */
    @Override
    public Display getDisplay ()
    {
        return this.display;
    }


    /** {@inheritDoc} */
    @Override
    public void setDisplay (final Display display)
    {
        this.display = display;
    }


    /** {@inheritDoc} */
    @Override
    public PadGrid getPadGrid ()
    {
        return this.pads;
    }


    /** {@inheritDoc} */
    @Override
    public IMidiOutput getOutput ()
    {
        return this.output;
    }


    /** {@inheritDoc} */
    @Override
    public IMidiInput getInput ()
    {
        return this.input;
    }


    /** {@inheritDoc} */
    @Override
    public void assignTriggerCommand (final int cc, final TriggerCommandID commandID)
    {
        this.assignTriggerCommand (cc, 0, commandID);
    }


    /** {@inheritDoc} */
    @Override
    public void assignTriggerCommand (final int cc, final int channel, final TriggerCommandID commandID)
    {
        this.triggerInfos[channel][cc] = new TriggerInfo ();
        this.triggerCommands.computeIfAbsent (Integer.valueOf (cc), k -> new HashMap<> ()).put (Integer.valueOf (channel), commandID);
    }


    /** {@inheritDoc} */
    @Override
    public TriggerCommandID getTriggerCommand (final int midiCC)
    {
        return this.getTriggerCommand (midiCC, 0);
    }


    /** {@inheritDoc} */
    @Override
    public TriggerCommandID getTriggerCommand (final int midiCC, final int midiChannel)
    {
        final Map<Integer, TriggerCommandID> channelMap = this.triggerCommands.get (Integer.valueOf (midiCC));
        return channelMap == null ? null : channelMap.get (Integer.valueOf (midiChannel));
    }


    /** {@inheritDoc} */
    @Override
    public void assignContinuousCommand (final int cc, final ContinuousCommandID commandID)
    {
        this.assignContinuousCommand (cc, 0, commandID);
    }


    /** {@inheritDoc} */
    @Override
    public void assignContinuousCommand (final int cc, final int channel, final ContinuousCommandID commandID)
    {
        this.continuousInfos[channel][cc] = new ContinuousInfo ();
        this.continuousCommands.computeIfAbsent (Integer.valueOf (cc), k -> new HashMap<> ()).put (Integer.valueOf (channel), commandID);
    }


    /** {@inheritDoc} */
    @Override
    public ContinuousCommandID getContinuousCommand (final int midiCC)
    {
        return this.getContinuousCommand (midiCC, 0);
    }


    /** {@inheritDoc} */
    @Override
    public ContinuousCommandID getContinuousCommand (final int midiCC, final int midiChannel)
    {
        final Map<Integer, ContinuousCommandID> channelMap = this.continuousCommands.get (Integer.valueOf (midiCC));
        return channelMap == null ? null : channelMap.get (Integer.valueOf (midiChannel));
    }


    /** {@inheritDoc} */
    @Override
    public void assignNoteCommand (final int midiNote, final TriggerCommandID commandID)
    {
        this.noteCommands.put (Integer.valueOf (midiNote), commandID);
    }


    /** {@inheritDoc} */
    @Override
    public TriggerCommandID getNoteCommand (final int midiNote)
    {
        return this.noteCommands.get (Integer.valueOf (midiNote));
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGridNote (final int note)
    {
        return this.pads != null && this.pads.isGridNote (note);
    }


    /** {@inheritDoc} */
    @Override
    public void setKeyTranslationTable (final int [] table)
    {
        this.keyTranslationTable = table;
        if (this.input == null)
            return;
        final Integer [] t = new Integer [table.length];
        for (int i = 0; i < table.length; i++)
            t[i] = Integer.valueOf (table[i]);
        this.input.setKeyTranslationTable (t);
    }


    /** {@inheritDoc} */
    @Override
    public int [] getKeyTranslationTable ()
    {
        return this.keyTranslationTable;
    }


    /** {@inheritDoc} */
    @Override
    public void setVelocityTranslationTable (final int [] table)
    {
        if (this.input == null)
            return;
        final Integer [] t = new Integer [table.length];
        for (int i = 0; i < table.length; i++)
            t[i] = Integer.valueOf (table[i]);
        this.input.setVelocityTranslationTable (t);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelectPressed ()
    {
        return this.isPressed (this.selectButtonId);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShiftPressed ()
    {
        return this.isPressed (this.shiftButtonId);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isDeletePressed ()
    {
        return this.isPressed (this.deleteButtonId);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSoloPressed ()
    {
        return this.isPressed (this.soloButtonId);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMutePressed ()
    {
        return this.isPressed (this.muteButtonId);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPressed (final int cc)
    {
        return this.isPressed (0, cc);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPressed (final int channel, final int button)
    {
        final TriggerInfo buttonInfo = this.getTriggerInfo (channel, button);
        if (buttonInfo == null)
            return false;
        return buttonInfo.getState () != ButtonEvent.UP;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLongPressed (final int button)
    {
        return isLongPressed (0, button);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLongPressed (final int channel, final int button)
    {
        final TriggerInfo buttonInfo = this.getTriggerInfo (channel, button);
        if (buttonInfo == null)
            return false;
        return buttonInfo.getState () == ButtonEvent.LONG;
    }


    /** {@inheritDoc} */
    @Override
    public int getShiftTriggerId ()
    {
        return this.shiftButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectTriggerId ()
    {
        return this.selectButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getDeleteTriggerId ()
    {
        return this.deleteButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getMuteTriggerId ()
    {
        return this.muteButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getSoloTriggerId ()
    {
        return this.soloButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getLeftTriggerId ()
    {
        return this.leftButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getRightTriggerId ()
    {
        return this.rightButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getUpTriggerId ()
    {
        return this.upButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getDownTriggerId ()
    {
        return this.downButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getSceneTrigger (final int index)
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void updateTrigger (final int button, final String colorID)
    {
        this.updateTrigger (button, this.colorManager.getColor (colorID));
    }


    /** {@inheritDoc} */
    @Override
    public void updateTrigger (final int button, final int value)
    {
        this.updateTrigger (button, 0, value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateTrigger (final int button, final int channel, final String colorID)
    {
        this.updateTrigger (button, channel, this.colorManager.getColor (colorID));
    }


    /** {@inheritDoc} */
    @Override
    public void updateTrigger (final int button, final int channel, final int value)
    {
        final TriggerInfo buttonInfo = this.getTriggerInfo (channel, button);
        if (buttonInfo == null || buttonInfo.getLedValue () == value)
            return;
        this.setTrigger (button, channel, value);
        buttonInfo.setLedValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int button, final int state)
    {
        this.setTrigger (button, 0, state);
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int button, final String colorID)
    {
        this.setTrigger (button, this.colorManager.getColor (colorID));
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int button, final int channel, final String colorID)
    {
        this.setTrigger (button, channel, this.colorManager.getColor (colorID));
    }


    /** {@inheritDoc} */
    @Override
    public void clearTriggerCache (final int button)
    {
        this.clearTriggerCache (0, button);
    }


    /** {@inheritDoc} */
    @Override
    public void clearTriggerCache (final int channel, final int button)
    {
        final TriggerInfo buttonInfo = this.getTriggerInfo (channel, button);
        if (buttonInfo != null)
            buttonInfo.setLedValue (-1);
    }


    /** {@inheritDoc} */
    @Override
    public void clearFullTriggerCache ()
    {
        this.clearFullTriggerCache (0);
    }


    /** {@inheritDoc} */
    @Override
    public void clearFullTriggerCache (final int channel)
    {
        for (int i = 0; i < NUM_BUTTONS; i++)
        {
            if (this.triggerInfos[channel][i] != null)
                this.triggerInfos[channel][i].setLedValue (-1);
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTrigger (final int cc)
    {
        return this.isTrigger (0, cc);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTrigger (final int channel, final int cc)
    {
        return this.triggerInfos[channel][cc] != null;
    }


    /** {@inheritDoc} */
    @Override
    public void setTriggerConsumed (final int cc)
    {
        this.setTriggerConsumed (0, cc);
    }


    /** {@inheritDoc} */
    @Override
    public void setTriggerConsumed (final int channel, final int cc)
    {
        final TriggerInfo triggerInfo = this.getTriggerInfo (channel, cc);
        if (triggerInfo != null)
            triggerInfo.setConsumed (true);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTriggerConsumed (final int cc)
    {
        return this.isTriggerConsumed (0, cc);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTriggerConsumed (final int channel, final int cc)
    {
        final TriggerInfo triggerInfo = this.getTriggerInfo (channel, cc);
        return triggerInfo != null && triggerInfo.isConsumed ();
    }


    /** {@inheritDoc} */
    @Override
    public void turnOffTriggers ()
    {
        for (int channel = 0; channel < 16; channel++)
        {
            for (int cc = 0; cc < 128; cc++)
            {
                if (this.isTrigger (channel, cc))
                    this.setTrigger (cc, channel, 0);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateContinuous (final int cc, final int value)
    {
        this.updateContinuous (cc, 0, value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateContinuous (final int cc, final int channel, final int value)
    {
        final ContinuousInfo triggerInfo = this.getContinuousInfo (channel, cc);
        if (triggerInfo == null || triggerInfo.getValue () == value)
            return;
        this.setContinuous (cc, channel, value);
        triggerInfo.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setContinuous (final int cc, final int state)
    {
        this.setContinuous (cc, 0, state);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushExecutor.execute ( () -> {
            try
            {
                this.scheduledFlush ();
                this.redrawGrid ();
            }
            catch (final RuntimeException ex)
            {
                this.host.error ("Crash during flush.", ex);
            }
        });
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.flushExecutor.shutdown ();

        this.turnOffTriggers ();

        if (this.pads != null)
            this.pads.turnOff ();

        if (this.display != null)
            this.display.shutdown ();
    }


    /**
     * Handle received midi data.
     *
     * @param status The midi status byte
     * @param data1 The midi data byte 1
     * @param data2 The midi data byte 2
     */
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        final int code = status & 0xF0;
        final int channel = status & 0xF;

        switch (code)
        {
            // Note off
            case 0x80:
                this.handleNote (data1, 0);
                break;

            // Note on
            case 0x90:
                this.handleNote (data1, data2);
                break;

            // Polyphonic Aftertouch
            case 0xA0:
                this.handlePolyAftertouch (data1, data2);
                break;

            // CC
            case 0xB0:
                this.handleCC (channel, data1, data2);
                break;

            // Channel Aftertouch
            case 0xD0:
                this.handleChannelAftertouch (data1);
                break;

            // Pitch Bend
            case 0xE0:
                this.handlePitchBend (channel, data1, data2);
                break;

            default:
                this.host.println ("Unhandled midi status: " + status);
                break;
        }
    }


    /**
     * Handle a note event
     *
     * @param note The note
     * @param velocity The velocity
     */
    protected void handleNote (final int note, final int velocity)
    {
        if (this.isGridNote (note))
            this.handleGridNote (note, velocity);
        else
            this.handleNoteEvent (note, velocity);
    }


    /**
     * Handle pitch bend.
     *
     * @param channel The MIDI channel
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    protected void handlePitchBend (final int channel, final int data1, final int data2)
    {
        final View view = this.viewManager.getActiveView ();
        if (view != null)
            view.executePitchbendCommand (channel, data1, data2);
    }


    /**
     * Handle channel aftertouch.
     *
     * @param data1 First data byte
     */
    protected void handleChannelAftertouch (final int data1)
    {
        final View view = this.viewManager.getActiveView ();
        if (view != null)
            view.executeAftertouchCommand (-1, data1);
    }


    /**
     * Handle poly aftertouch.
     *
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    protected void handlePolyAftertouch (final int data1, final int data2)
    {
        final View view = this.viewManager.getActiveView ();
        if (view != null)
            view.executeAftertouchCommand (data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void scheduleTask (final Runnable callback, final long delay)
    {
        this.host.scheduleTask ( () -> {
            try
            {
                callback.run ();
            }
            catch (final RuntimeException ex)
            {
                this.host.error ("Could not execute scheduled task.", ex);
            }
        }, delay);
    }


    /** {@inheritDoc} */
    @Override
    public void println (final String message)
    {
        this.host.println (message);
    }


    /** {@inheritDoc} */
    @Override
    public void errorln (final String message)
    {
        this.host.error (message);
    }


    /** {@inheritDoc} */
    @Override
    public void sendMidiEvent (final int status, final int data1, final int data2)
    {
        this.input.sendRawMidiEvent (status, data1, data2);
    }


    /**
     * Handle a midi note which belongs to the grid.
     *
     * @param note The midi note
     * @param velocity The velocity of the note
     */
    protected void handleGridNote (final int note, final int velocity)
    {
        final int gridNote = this.pads.translateToGrid (note);

        this.gridNoteStates[gridNote] = velocity > 0 ? ButtonEvent.DOWN : ButtonEvent.UP;
        if (velocity > 0)
            this.gridNoteVelocities[gridNote] = velocity;
        if (this.gridNoteStates[gridNote] == ButtonEvent.DOWN)
            this.scheduleTask ( () -> this.checkGridNoteState (gridNote), AbstractControlSurface.BUTTON_STATE_INTERVAL);

        // If consumed flag is set ignore the UP event
        if (this.gridNoteStates[gridNote] == ButtonEvent.UP && this.gridNoteConsumed[gridNote])
        {
            this.gridNoteConsumed[gridNote] = false;
            return;
        }

        final View view = this.viewManager.getActiveView ();
        if (view != null)
            view.onGridNote (gridNote, velocity);
    }


    private void checkGridNoteState (final int note)
    {
        if (this.gridNoteStates[note] != ButtonEvent.DOWN)
            return;

        this.gridNoteStates[note] = ButtonEvent.LONG;

        final View view = this.viewManager.getActiveView ();
        if (view != null)
            view.onGridNoteLongPress (note);
    }


    /**
     * Set a grid note as consumed.
     *
     * @param note The note to set
     */
    public void setGridNoteConsumed (final int note)
    {
        this.gridNoteConsumed[note] = true;
    }


    /**
     * Get the grid note velocity of a note on the grid.
     *
     * @param note The note
     * @return The velocity
     */
    public int getGridNoteVelocity (final int note)
    {
        return this.gridNoteVelocities[note];
    }


    /**
     * Get the velocity of a pressed note.
     *
     * @param note The note
     * @return The velocity, 0 if currently not pressed
     */
    public int getNoteVelocity (final int note)
    {
        return this.noteVelocities[note];
    }


    /**
     * Handle non-grid midi CC like buttons and knobs.
     *
     * @param channel The midi channel
     * @param cc The CC
     * @param value The value
     */
    protected void handleCC (final int channel, final int cc, final int value)
    {
        if (this.triggerInfos[channel][cc] != null)
        {
            if (value > 0)
            {
                this.triggerInfos[channel][cc].setState (ButtonEvent.DOWN);
                this.scheduleTask ( () -> this.checkButtonState (channel, cc), AbstractControlSurface.BUTTON_STATE_INTERVAL);
            }
            else
            {
                this.triggerInfos[channel][cc].setState (ButtonEvent.UP);

                // If consumed flag is set ignore the UP event
                if (this.triggerInfos[channel][cc].isConsumed ())
                {
                    this.triggerInfos[channel][cc].setConsumed (false);
                    return;
                }
            }
        }

        this.handleCCEvent (channel, cc, value);
    }


    /**
     * Handle note events.
     *
     * @param note The midi note
     * @param velocity The velocity
     */
    protected void handleNoteEvent (final int note, final int velocity)
    {
        this.noteVelocities[note] = velocity;

        final View view = this.viewManager.getActiveView ();
        if (view == null)
            return;

        final TriggerCommandID commandID = this.getNoteCommand (note);
        if (commandID != null)
        {
            view.executeNoteCommand (commandID, velocity);
            return;
        }

        this.println ("Unsupported Midi Note: " + note);
    }


    /**
     * Override in subclass with buttons array usage.
     *
     * @param channel The midi channel
     * @param cc The midi CC
     * @param value The value
     */
    protected void handleCCEvent (final int channel, final int cc, final int value)
    {
        final View view = this.viewManager.getActiveView ();
        if (view == null)
            return;

        final TriggerCommandID triggerCommandID = this.getTriggerCommand (cc, channel);
        if (triggerCommandID != null)
        {
            final ButtonEvent event = this.triggerInfos[channel][cc] == null ? null : this.triggerInfos[channel][cc].getState ();
            view.executeTriggerCommand (triggerCommandID, event);
            return;
        }

        final ContinuousCommandID commandID = this.getContinuousCommand (cc, channel);
        if (commandID != null)
        {
            view.executeContinuousCommand (commandID, value);
            return;
        }

        this.println ("Unsupported Midi CC: " + cc);
    }


    /**
     * Delayed flush.
     */
    protected void scheduledFlush ()
    {
        final View view = this.viewManager.getActiveView ();
        if (view != null)
            view.updateControlSurface ();
        if (this.display != null)
            this.display.flush ();
    }


    /**
     * Redraws the grid for the active view.
     */
    protected void redrawGrid ()
    {
        final View view = this.viewManager.getActiveView ();
        if (view == null)
            return;
        view.drawGrid ();
        if (this.pads != null)
            this.pads.flush ();
    }


    /**
     * If the state of the given button is still down, the state is set to long and an event gets
     * fired.
     * 
     * @param channel The MIDI channel
     * @param button The button CC to check
     */
    protected void checkButtonState (final int channel, final int button)
    {
        final TriggerInfo buttonInfo = this.getTriggerInfo (channel, button);
        if (buttonInfo == null || buttonInfo.getState () != ButtonEvent.DOWN)
            return;
        buttonInfo.setState (ButtonEvent.LONG);
        this.handleCCEvent (channel, button, 127);
    }


    private TriggerInfo getTriggerInfo (final int channel, final int cc)
    {
        if (channel < 0 || cc < 0)
            return null;

        if (this.triggerInfos[channel][cc] == null)
        {
            this.errorln ("Unregistered CC trigger: " + cc);
            return null;
        }
        return this.triggerInfos[channel][cc];
    }


    private ContinuousInfo getContinuousInfo (final int channel, final int cc)
    {
        if (channel < 0 || cc < 0)
            return null;

        if (this.continuousInfos[channel][cc] == null)
        {
            this.errorln ("Unregistered CC continuous: " + cc);
            return null;
        }
        return this.continuousInfos[channel][cc];
    }
}