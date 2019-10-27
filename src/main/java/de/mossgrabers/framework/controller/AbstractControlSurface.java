// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.ContinuousInfo;
import de.mossgrabers.framework.utils.LatestTaskExecutor;
import de.mossgrabers.framework.utils.TriggerInfo;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
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
    protected static final int                                      NUM_INFOS             = 256;

    protected final IHost                                           host;
    protected final C                                               configuration;
    protected final ColorManager                                    colorManager;
    protected final IMidiOutput                                     output;
    protected final IMidiInput                                      input;

    protected final ViewManager                                     viewManager           = new ViewManager ();
    protected final ModeManager                                     modeManager           = new ModeManager ();

    protected int                                                   defaultMidiChannel    = 0;

    protected final EnumMap<ButtonID, Integer>                      buttonIDs             = new EnumMap<> (ButtonID.class);
    private final TriggerInfo [] []                                 triggerInfos          = new TriggerInfo [16] [NUM_INFOS];
    private final ContinuousInfo [] []                              continuousInfos       = new ContinuousInfo [16] [NUM_INFOS];
    private final int []                                            noteVelocities;

    protected List<ITextDisplay>                                    textDisplays          = new ArrayList<> (1);
    protected List<IGraphicDisplay>                                 graphicsDisplays      = new ArrayList<> (1);

    protected final PadGrid                                         pads;
    protected final Map<Integer, Map<Integer, TriggerCommandID>>    triggerCommands       = new HashMap<> ();
    protected final Map<Integer, Map<Integer, ContinuousCommandID>> continuousCommands    = new HashMap<> ();
    protected final Map<Integer, TriggerCommandID>                  noteCommands          = new HashMap<> ();

    private final boolean []                                        gridNoteConsumed;
    private final ButtonEvent []                                    gridNoteStates;
    private final int []                                            gridNoteVelocities;
    private int []                                                  keyTranslationTable;

    private final LatestTaskExecutor                                flushExecutor         = new LatestTaskExecutor ();
    private final DummyDisplay                                      dummyDisplay;


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

        this.dummyDisplay = new DummyDisplay (host);

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
    public IDisplay getDisplay ()
    {
        if (this.graphicsDisplays.isEmpty ())
            return this.getTextDisplay (0);
        return this.getGraphicsDisplay ();
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay getTextDisplay ()
    {
        return this.getTextDisplay (0);
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay getTextDisplay (final int index)
    {
        if (index >= this.textDisplays.size ())
            return this.dummyDisplay;
        return this.textDisplays.get (index);
    }


    /** {@inheritDoc} */
    @Override
    public IGraphicDisplay getGraphicsDisplay ()
    {
        return this.getGraphicsDisplay (0);
    }


    /** {@inheritDoc} */
    @Override
    public IGraphicDisplay getGraphicsDisplay (final int index)
    {
        return this.graphicsDisplays.get (index);
    }


    /** {@inheritDoc} */
    @Override
    public void addTextDisplay (final ITextDisplay display)
    {
        this.textDisplays.add (display);
    }


    /** {@inheritDoc} */
    @Override
    public void addGraphicsDisplay (final IGraphicDisplay display)
    {
        this.graphicsDisplays.add (display);
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
        this.assignTriggerCommand (this.defaultMidiChannel, cc, commandID);
    }


    /** {@inheritDoc} */
    @Override
    public void assignTriggerCommand (final int channel, final int cc, final TriggerCommandID commandID)
    {
        this.triggerInfos[channel][cc] = new TriggerInfo ();
        this.triggerCommands.computeIfAbsent (Integer.valueOf (cc), k -> new HashMap<> ()).put (Integer.valueOf (channel), commandID);
    }


    /** {@inheritDoc} */
    @Override
    public TriggerCommandID getTriggerCommand (final int cc)
    {
        return this.getTriggerCommand (this.defaultMidiChannel, cc);
    }


    /** {@inheritDoc} */
    @Override
    public TriggerCommandID getTriggerCommand (final int channel, final int cc)
    {
        final Map<Integer, TriggerCommandID> channelMap = this.triggerCommands.get (Integer.valueOf (cc));
        return channelMap == null ? null : channelMap.get (Integer.valueOf (channel));
    }


    /** {@inheritDoc} */
    @Override
    public void assignContinuousCommand (final int cc, final ContinuousCommandID commandID)
    {
        this.assignContinuousCommand (this.defaultMidiChannel, cc, commandID);
    }


    /** {@inheritDoc} */
    @Override
    public void assignContinuousCommand (final int channel, final int cc, final ContinuousCommandID commandID)
    {
        this.continuousInfos[channel][cc] = new ContinuousInfo ();
        this.continuousCommands.computeIfAbsent (Integer.valueOf (cc), k -> new HashMap<> ()).put (Integer.valueOf (channel), commandID);
    }


    /** {@inheritDoc} */
    @Override
    public ContinuousCommandID getContinuousCommand (final int cc)
    {
        return this.getContinuousCommand (this.defaultMidiChannel, cc);
    }


    /** {@inheritDoc} */
    @Override
    public ContinuousCommandID getContinuousCommand (final int channel, final int cc)
    {
        final Map<Integer, ContinuousCommandID> channelMap = this.continuousCommands.get (Integer.valueOf (cc));
        return channelMap == null ? null : channelMap.get (Integer.valueOf (channel));
    }


    /** {@inheritDoc} */
    @Override
    public void assignNoteCommand (final int note, final TriggerCommandID commandID)
    {
        this.noteCommands.put (Integer.valueOf (note), commandID);
    }


    /** {@inheritDoc} */
    @Override
    public TriggerCommandID getNoteCommand (final int note)
    {
        return this.noteCommands.get (Integer.valueOf (note));
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
        final INoteInput defaultNoteInput = this.input.getDefaultNoteInput ();
        if (defaultNoteInput != null)
            defaultNoteInput.setKeyTranslationTable (t);
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
        final INoteInput defaultNoteInput = this.input.getDefaultNoteInput ();
        if (defaultNoteInput != null)
            defaultNoteInput.setVelocityTranslationTable (t);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShiftPressed ()
    {
        return this.isPressed (ButtonID.SHIFT);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelectPressed ()
    {
        return this.isPressed (ButtonID.SELECT);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isDeletePressed ()
    {
        return this.isPressed (ButtonID.DELETE);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSoloPressed ()
    {
        return this.isPressed (ButtonID.SOLO);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMutePressed ()
    {
        return this.isPressed (ButtonID.MUTE);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPressed (final ButtonID buttonID)
    {
        final Integer cc = this.buttonIDs.get (buttonID);
        return cc != null && this.isPressed (cc.intValue ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPressed (final int cc)
    {
        return this.isPressed (this.defaultMidiChannel, cc);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPressed (final int channel, final int cc)
    {
        final TriggerInfo info = this.getTriggerInfo (channel, cc);
        if (info == null)
            return false;
        return info.getState () != ButtonEvent.UP;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLongPressed (final int cc)
    {
        return this.isLongPressed (this.defaultMidiChannel, cc);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLongPressed (final int channel, final int cc)
    {
        final TriggerInfo info = this.getTriggerInfo (channel, cc);
        if (info == null)
            return false;
        return info.getState () == ButtonEvent.LONG;
    }


    /** {@inheritDoc} */
    @Override
    public int getTriggerId (final ButtonID trigger)
    {
        final Integer cc = this.buttonIDs.get (trigger);
        return cc == null ? -1 : cc.intValue ();
    }


    protected void setTriggerId (final ButtonID trigger, final int cc)
    {
        this.buttonIDs.put (trigger, Integer.valueOf (cc));
    }


    /** {@inheritDoc} */
    @Override
    public int getSceneTrigger (final int index)
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void updateTrigger (final int cc, final String colorID)
    {
        this.updateTrigger (cc, this.colorManager.getColor (colorID));
    }


    /** {@inheritDoc} */
    @Override
    public void updateTrigger (final int cc, final int value)
    {
        this.updateTrigger (this.defaultMidiChannel, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateTrigger (final int channel, final int cc, final String colorID)
    {
        this.updateTrigger (channel, cc, this.colorManager.getColor (colorID));
    }


    /** {@inheritDoc} */
    @Override
    public void updateTrigger (final int channel, final int cc, final int value)
    {
        final TriggerInfo info = this.getTriggerInfo (channel, cc);
        if (info == null || info.getLedValue () == value)
            return;
        this.setTrigger (channel, cc, value);
        info.setLedValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int cc, final int state)
    {
        this.setTrigger (this.defaultMidiChannel, cc, state);
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int cc, final String colorID)
    {
        this.setTrigger (cc, this.colorManager.getColor (colorID));
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int channel, final int cc, final String colorID)
    {
        this.setTrigger (channel, cc, this.colorManager.getColor (colorID));
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int channel, final int cc, final int state)
    {
        // Overwrite to support trigger LEDs
    }


    /** {@inheritDoc} */
    @Override
    public void setContinuous (final int channel, final int cc, final int value)
    {
        // Overwrite to support continuous LEDs/motors
    }


    /** {@inheritDoc} */
    @Override
    public void clearTriggerCache ()
    {
        for (int channel = 0; channel < 16; channel++)
        {
            for (int cc = 0; cc < NUM_INFOS; cc++)
            {
                if (this.triggerInfos[channel][cc] != null)
                    this.triggerInfos[channel][cc].setLedValue (-1);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void clearTriggerCache (final int cc)
    {
        this.clearTriggerCache (this.defaultMidiChannel, cc);
    }


    /** {@inheritDoc} */
    @Override
    public void clearTriggerCache (final int channel, final int cc)
    {
        final TriggerInfo info = this.getTriggerInfo (channel, cc);
        if (info != null)
            info.setLedValue (-1);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTrigger (final int cc)
    {
        return this.isTrigger (this.defaultMidiChannel, cc);
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
        this.setTriggerConsumed (this.defaultMidiChannel, cc);
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
        return this.isTriggerConsumed (this.defaultMidiChannel, cc);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTriggerConsumed (final int channel, final int cc)
    {
        final TriggerInfo info = this.getTriggerInfo (channel, cc);
        return info != null && info.isConsumed ();
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
                    this.setTrigger (channel, cc, 0);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateContinuous (final int cc, final int value)
    {
        this.updateContinuous (this.defaultMidiChannel, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateContinuous (final int channel, final int cc, final int value)
    {
        final ContinuousInfo info = this.getContinuousInfo (channel, cc);
        if (info == null || info.getValue () == value)
            return;
        this.setContinuous (channel, cc, value);
        info.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setContinuous (final int cc, final int state)
    {
        this.setContinuous (this.defaultMidiChannel, cc, state);
    }


    /** {@inheritDoc} */
    @Override
    public void clearContinuousCache ()
    {
        for (int channel = 0; channel < 16; channel++)
        {
            for (int cc = 0; cc < NUM_INFOS; cc++)
            {
                if (this.continuousInfos[channel][cc] != null)
                    this.continuousInfos[channel][cc].setValue (-1);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void clearContinuousCache (final int cc)
    {
        this.clearContinuousCache (this.defaultMidiChannel, cc);
    }


    /** {@inheritDoc} */
    @Override
    public void clearContinuousCache (final int channel, final int cc)
    {
        final ContinuousInfo info = this.getContinuousInfo (channel, cc);
        if (info != null)
            info.setValue (-1);
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

        this.textDisplays.forEach (IDisplay::shutdown);
        this.graphicsDisplays.forEach (IDisplay::shutdown);
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

            // Program Change
            case 0xC0:
                this.handleProgramChange (channel, data1, data2);
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


    /**
     * Handle program change.
     *
     * @param channel The MIDI channel
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    protected void handleProgramChange (final int channel, final int data1, final int data2)
    {
        // Intentionally empty
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

        final TriggerCommandID triggerCommandID = this.getTriggerCommand (channel, cc);
        if (triggerCommandID != null)
        {
            final ButtonEvent event = this.triggerInfos[channel][cc] == null ? null : this.triggerInfos[channel][cc].getState ();
            view.executeTriggerCommand (triggerCommandID, event);
            return;
        }

        final ContinuousCommandID commandID = this.getContinuousCommand (channel, cc);
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
        this.textDisplays.forEach (ITextDisplay::flush);
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
     * @param cc The button CC to check
     */
    protected void checkButtonState (final int channel, final int cc)
    {
        final TriggerInfo info = this.getTriggerInfo (channel, cc);
        if (info == null || info.getState () != ButtonEvent.DOWN)
            return;
        info.setState (ButtonEvent.LONG);
        this.handleCCEvent (channel, cc, 127);
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