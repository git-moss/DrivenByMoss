// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.LatestTaskExecutor;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;

import java.util.ArrayList;
import java.util.Arrays;
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
    protected static final int                          BUTTON_STATE_INTERVAL = 400;
    protected static final int                          NUM_NOTES             = 128;
    protected static final int                          NUM_BUTTONS           = 256;

    protected final IHost                               host;
    protected final C                                   configuration;
    protected final ColorManager                        colorManager;
    protected final IMidiOutput                         output;
    protected final IMidiInput                          input;

    protected final ViewManager                         viewManager           = new ViewManager ();
    protected final ModeManager                         modeManager           = new ModeManager ();

    protected int                                       selectButtonId        = -1;
    protected int                                       shiftButtonId         = -1;
    protected int                                       deleteButtonId        = -1;
    protected int                                       soloButtonId          = -1;
    protected int                                       muteButtonId          = -1;
    protected int                                       leftButtonId          = -1;
    protected int                                       rightButtonId         = -1;
    protected int                                       upButtonId            = -1;
    protected int                                       downButtonId          = -1;

    private final int []                                buttons;
    protected final ButtonEvent []                      buttonStates;
    private final int []                                noteVelocities;
    protected final boolean []                          buttonConsumed;

    private final List<int []>                          buttonCache;

    protected final int []                              gridNotes;

    protected Display                                   display;
    protected final PadGrid                             pads;
    protected final Map<Integer, Map<Integer, Integer>> triggerCommands       = new HashMap<> ();
    protected final Map<Integer, Map<Integer, Integer>> continuousCommands    = new HashMap<> ();
    protected final Map<Integer, Integer>               noteCommands          = new HashMap<> ();

    private final boolean []                            gridNoteConsumed;
    private final ButtonEvent []                        gridNoteStates;
    private final int []                                gridNoteVelocities;
    private int []                                      keyTranslationTable;

    private final LatestTaskExecutor                    flushExecutor         = new LatestTaskExecutor ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param configuration The configuration
     * @param colorManager
     * @param output The midi output
     * @param input The midi input
     * @param padGrid The pads if any, may be null
     * @param buttons All midi CC which should be treated as a button
     */
    public AbstractControlSurface (final IHost host, final C configuration, final ColorManager colorManager, final IMidiOutput output, final IMidiInput input, final PadGrid padGrid, final int [] buttons)
    {
        this.host = host;
        this.configuration = configuration;
        this.colorManager = colorManager;
        this.pads = padGrid;

        this.output = output;
        this.input = input;
        if (this.input != null)
            this.input.setMidiCallback (this::handleMidi);

        this.gridNotes = new int [64];

        // Button related
        this.buttons = buttons;
        this.buttonStates = new ButtonEvent [NUM_BUTTONS];
        this.buttonConsumed = new boolean [NUM_BUTTONS];
        if (this.buttons != null)
        {
            for (final int button: this.buttons)
            {
                this.buttonStates[button] = ButtonEvent.UP;
                this.buttonConsumed[button] = false;
            }
        }

        // Optimisation for button LED updates, cache 128 possible note values on
        // all 16 midi channels
        this.buttonCache = new ArrayList<> (NUM_BUTTONS);
        for (int i = 0; i < NUM_BUTTONS; i++)
        {
            final int [] channels = new int [16];
            Arrays.fill (channels, -1);
            this.buttonCache.add (channels);
        }

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
            this.gridNotes[i] = 36 + i;
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


    /**
     * Get all midi CC which should be treated as a button.
     *
     * @return The button midi CCs
     */
    public int [] getButtons ()
    {
        return this.buttons;
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
    public void assignTriggerCommand (final int midiCC, final Integer commandID)
    {
        this.assignTriggerCommand (midiCC, 0, commandID);
    }


    /** {@inheritDoc} */
    @Override
    public void assignTriggerCommand (final int midiCC, final int midiChannel, final Integer commandID)
    {
        this.triggerCommands.computeIfAbsent (Integer.valueOf (midiCC), k -> new HashMap<> ()).put (Integer.valueOf (midiChannel), commandID);
    }


    /** {@inheritDoc} */
    @Override
    public Integer getTriggerCommand (final int midiCC)
    {
        return this.getTriggerCommand (midiCC, 0);
    }


    /** {@inheritDoc} */
    @Override
    public Integer getTriggerCommand (final int midiCC, final int midiChannel)
    {
        final Map<Integer, Integer> channelMap = this.triggerCommands.get (Integer.valueOf (midiCC));
        return channelMap == null ? null : channelMap.get (Integer.valueOf (midiChannel));
    }


    /** {@inheritDoc} */
    @Override
    public void assignContinuousCommand (final int midiCC, final Integer commandID)
    {
        this.assignContinuousCommand (midiCC, 0, commandID);
    }


    /** {@inheritDoc} */
    @Override
    public void assignContinuousCommand (final int midiCC, final int midiChannel, final Integer commandID)
    {
        Map<Integer, Integer> channelMap = this.continuousCommands.get (Integer.valueOf (midiCC));
        if (channelMap == null)
        {
            channelMap = new HashMap<> ();
            this.continuousCommands.put (Integer.valueOf (midiCC), channelMap);
        }
        channelMap.put (Integer.valueOf (midiChannel), commandID);
    }


    /** {@inheritDoc} */
    @Override
    public Integer getContinuousCommand (final int midiCC)
    {
        return this.getContinuousCommand (midiCC, 0);
    }


    /** {@inheritDoc} */
    @Override
    public Integer getContinuousCommand (final int midiCC, final int midiChannel)
    {
        final Map<Integer, Integer> channelMap = this.continuousCommands.get (Integer.valueOf (midiCC));
        return channelMap == null ? null : channelMap.get (Integer.valueOf (midiChannel));
    }


    /** {@inheritDoc} */
    @Override
    public void assignNoteCommand (final int midiNote, final Integer commandID)
    {
        this.noteCommands.put (Integer.valueOf (midiNote), commandID);
    }


    /** {@inheritDoc} */
    @Override
    public Integer getNoteCommand (final int midiNote)
    {
        return this.noteCommands.get (Integer.valueOf (midiNote));
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGridNote (final int note)
    {
        if (this.gridNotes.length > 0)
            return note >= this.gridNotes[0] && note <= this.gridNotes[this.gridNotes.length - 1];
        return false;
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
    public boolean isPressed (final int button)
    {
        if (button == -1)
            return false;
        if (this.buttonStates[button] == null)
        {
            this.errorln ("Unregistered button: " + button);
            return false;
        }
        switch (this.buttonStates[button])
        {
            case DOWN:
            case LONG:
                return true;
            default:
                return false;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getShiftButtonId ()
    {
        return this.shiftButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectButtonId ()
    {
        return this.selectButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getDeleteButtonId ()
    {
        return this.deleteButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getMuteButtonId ()
    {
        return this.muteButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getSoloButtonId ()
    {
        return this.soloButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getLeftButtonId ()
    {
        return this.leftButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getRightButtonId ()
    {
        return this.rightButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getUpButtonId ()
    {
        return this.upButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getDownButtonId ()
    {
        return this.downButtonId;
    }


    /** {@inheritDoc} */
    @Override
    public int getSceneButton (final int index)
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void updateButton (final int button, final int value)
    {
        if (this.buttonCache.get (button)[0] == value)
            return;
        this.setButton (button, value);
        this.buttonCache.get (button)[0] = value;
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtonEx (final int button, final int channel, final int value)
    {
        if (this.buttonCache.get (button)[channel] == value)
            return;
        this.setButtonEx (button, channel, value);
        this.buttonCache.get (button)[channel] = value;
    }


    /** {@inheritDoc} */
    @Override
    public void updateButton (final int button, final String colorID)
    {
        this.updateButton (button, this.colorManager.getColor (colorID));
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtonEx (final int button, final int channel, final String colorID)
    {
        this.updateButtonEx (button, channel, this.colorManager.getColor (colorID));
    }


    /** {@inheritDoc} */
    @Override
    public void setButton (final int button, final int state)
    {
        this.setButtonEx (button, 0, state);
    }


    /** {@inheritDoc} */
    @Override
    public void setButton (final int button, final String colorID)
    {
        this.setButton (button, this.colorManager.getColor (colorID));
    }


    /** {@inheritDoc} */
    @Override
    public void setButtonEx (final int button, final int channel, final String colorID)
    {
        this.setButtonEx (button, channel, this.colorManager.getColor (colorID));
    }


    /** {@inheritDoc} */
    @Override
    public void setButtonEx (final int button, final int channel, final int state)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void clearButtonCache (final int button)
    {
        this.buttonCache.get (button)[0] = -1;
    }


    /** {@inheritDoc} */
    @Override
    public void clearButtonCache ()
    {
        for (int i = 0; i < NUM_BUTTONS; i++)
            this.buttonCache.get (i)[0] = -1;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isButton (final int cc)
    {
        return this.buttonStates[cc] != null;
    }


    /** {@inheritDoc} */
    @Override
    public void setButtonConsumed (final int buttonID)
    {
        this.buttonConsumed[buttonID] = true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isButtonConsumed (final int buttonID)
    {
        return this.buttonConsumed[buttonID];
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

        for (final int button: this.getButtons ())
            this.setButton (button, 0);

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
        this.gridNoteStates[note] = velocity > 0 ? ButtonEvent.DOWN : ButtonEvent.UP;
        if (velocity > 0)
            this.gridNoteVelocities[note] = velocity;
        if (this.gridNoteStates[note] == ButtonEvent.DOWN)
            this.scheduleTask ( () -> this.checkGridNoteState (note), AbstractControlSurface.BUTTON_STATE_INTERVAL);

        // If consumed flag is set ignore the UP event
        if (this.gridNoteStates[note] == ButtonEvent.UP && this.gridNoteConsumed[note])
        {
            this.gridNoteConsumed[note] = false;
            return;
        }

        final View view = this.viewManager.getActiveView ();
        if (view != null)
            view.onGridNote (note, velocity);
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
        if (this.isButton (cc))
        {
            this.buttonStates[cc] = value > 0 ? ButtonEvent.DOWN : ButtonEvent.UP;

            if (this.buttonStates[cc] == ButtonEvent.DOWN)
                this.scheduleTask ( () -> this.checkButtonState (cc), AbstractControlSurface.BUTTON_STATE_INTERVAL);

            // If consumed flag is set ignore the UP event
            if (this.buttonStates[cc] == ButtonEvent.UP && this.buttonConsumed[cc])
            {
                this.buttonConsumed[cc] = false;
                return;
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

        final Integer commandID = this.getNoteCommand (note);
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

        Integer commandID = this.getTriggerCommand (cc, channel);
        if (commandID != null)
        {
            final ButtonEvent event = this.isButton (cc) ? this.buttonStates[cc] : null;
            view.executeTriggerCommand (commandID, event);
            return;
        }

        commandID = this.getContinuousCommand (cc, channel);
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
     * @param buttonID The button CC to check
     */
    protected void checkButtonState (final int buttonID)
    {
        if (this.buttonStates[buttonID] != ButtonEvent.DOWN)
            return;

        this.buttonStates[buttonID] = ButtonEvent.LONG;
        this.handleCCEvent (0, buttonID, 127);
    }
}