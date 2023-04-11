// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.grid.ILightGuide;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.controller.hardware.IHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwLight;
import de.mossgrabers.framework.controller.hardware.IHwPianoKeyboard;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.hardware.IHwSurfaceFactory;
import de.mossgrabers.framework.controller.valuechanger.ISensitivityCallback;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.graphics.IBitmap;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;


/**
 * Abstract implementation of a Control Surface.
 *
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractControlSurface<C extends Configuration> implements IControlSurface<C>
{
    private static final String                           SHOULD_BE_HANDLED_IN_FRAMEWORK = " should be handled in framework...";

    protected static final int                            BUTTON_STATE_INTERVAL          = 500;
    protected static final int                            NUM_NOTES                      = 128;
    protected static final int                            NUM_INFOS                      = 256;

    protected final IHost                                 host;
    protected final IHwSurfaceFactory                     surfaceFactory;
    protected final C                                     configuration;
    protected final ColorManager                          colorManager;
    protected final IMidiOutput                           output;
    protected final IMidiInput                            input;

    protected final int                                   surfaceID;

    protected final ViewManager                           viewManager                    = new ViewManager ();
    protected final ModeManager                           modeManager                    = new ModeManager ();

    protected int                                         defaultMidiChannel             = 0;

    private final Map<ContinuousID, IHwContinuousControl> continuous                     = new EnumMap<> (ContinuousID.class);
    private final Map<ButtonID, IHwButton>                buttons                        = new EnumMap<> (ButtonID.class);
    private final Map<OutputID, IHwLight>                 lights                         = new EnumMap<> (OutputID.class);
    protected List<ITextDisplay>                          textDisplays                   = new ArrayList<> (1);
    protected List<IGraphicDisplay>                       graphicsDisplays               = new ArrayList<> (1);

    protected final IPadGrid                              padGrid;
    protected ILightGuide                                 lightGuide;

    private int []                                        keyTranslationTable;

    private final DummyDisplay                            dummyDisplay;
    private IHwPianoKeyboard                              pianoKeyboard;

    private final Object                                  updateCounterLock              = new Object ();
    private int                                           updateCounter                  = 0;

    private boolean                                       knobSensitivityIsSlow          = false;
    private final List<ISensitivityCallback>              knobSensitivityObservers       = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param configuration The configuration
     * @param colorManager
     * @param output The MIDI output
     * @param input The MIDI input
     * @param padGrid The pads if any, may be null
     * @param width The physical width of the controller device in millimeter
     * @param height The physical height of the controller device in millimeter
     */
    protected AbstractControlSurface (final IHost host, final C configuration, final ColorManager colorManager, final IMidiOutput output, final IMidiInput input, final IPadGrid padGrid, final double width, final double height)
    {
        this (0, host, configuration, colorManager, output, input, padGrid, width, height);
    }


    /**
     * Constructor.
     *
     * @param surfaceID The ID of the surface
     * @param host The host
     * @param configuration The configuration
     * @param colorManager
     * @param output The MIDI output
     * @param input The MIDI input
     * @param padGrid The pads if any, may be null
     * @param width The physical width of the controller device in mm
     * @param height The physical height of the controller device in mm
     */
    protected AbstractControlSurface (final int surfaceID, final IHost host, final C configuration, final ColorManager colorManager, final IMidiOutput output, final IMidiInput input, final IPadGrid padGrid, final double width, final double height)
    {
        this (surfaceID, host, configuration, colorManager, output, input, padGrid, null, width, height);
    }


    /**
     * Constructor.
     *
     * @param surfaceID The ID of the surface
     * @param host The host
     * @param configuration The configuration
     * @param colorManager
     * @param output The MIDI output
     * @param input The MIDI input
     * @param padGrid The pads if any, may be null
     * @param lightGuide The light guide
     * @param width The physical width of the controller device in mm
     * @param height The physical height of the controller device in mm
     */
    protected AbstractControlSurface (final int surfaceID, final IHost host, final C configuration, final ColorManager colorManager, final IMidiOutput output, final IMidiInput input, final IPadGrid padGrid, final ILightGuide lightGuide, final double width, final double height)
    {
        this.surfaceID = surfaceID;

        this.host = host;
        this.configuration = configuration;
        this.colorManager = colorManager;
        this.padGrid = padGrid;
        this.lightGuide = lightGuide;

        this.surfaceFactory = host.createSurfaceFactory (width, height);

        this.dummyDisplay = new DummyDisplay (host);

        this.output = output;
        this.input = input;
        if (this.input != null)
            this.input.setMidiCallback (this::handleMidi);

        this.createPads ();
        this.createLightGuide ();
    }


    /**
     * Create all pads for the grid and bind them to the MIDI input.
     */
    protected void createPads ()
    {
        if (this.padGrid == null)
            return;

        final int size = this.padGrid.getRows () * this.padGrid.getCols ();
        final int startNote = this.padGrid.getStartNote ();
        for (int i = 0; i < size; i++)
        {
            final int note = startNote + i;

            final ButtonID buttonID = ButtonID.get (ButtonID.PAD1, i);
            final IHwButton pad = this.createButton (buttonID, "P " + (i + 1));
            pad.addLight (this.surfaceFactory.createLight (this.surfaceID, null, () -> this.padGrid.getLightInfo (note).getEncoded (), state -> this.padGrid.sendState (note), colorIndex -> this.colorManager.getColor (colorIndex, buttonID), pad));
            final int [] translated = this.padGrid.translateToController (note);
            pad.bind (this.input, BindType.NOTE, translated[0], translated[1]);
            pad.bind ( (event, velocity) -> this.handleGridNote (event, note, velocity));
        }
    }


    /**
     * Create all lights for the light guide.
     */
    protected void createLightGuide ()
    {
        if (this.lightGuide == null)
            return;

        final int size = this.lightGuide.getCols ();
        for (int i = 0; i < size; i++)
        {
            final int note = this.lightGuide.getStartNote () + i;
            this.createLight (OutputID.get (OutputID.LIGHT_GUIDE1, i), () -> this.lightGuide.getLightInfo (note).getEncoded (), state -> this.lightGuide.sendState (note), colorIndex -> this.colorManager.getColor (colorIndex, null), null);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void rebindGrid ()
    {
        final int size = this.padGrid.getRows () * this.padGrid.getCols ();
        final int startNote = this.padGrid.getStartNote ();
        for (int i = 0; i < size; i++)
        {
            final int note = startNote + i;

            final IHwButton pad = this.getButton (ButtonID.get (ButtonID.PAD1, i));
            final int [] translated = this.padGrid.translateToController (note);
            pad.bind (this.input, BindType.NOTE, translated[0], translated[1]);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void unbindGrid ()
    {
        final int size = this.padGrid.getRows () * this.padGrid.getCols ();
        for (int i = 0; i < size; i++)
            this.getButton (ButtonID.get (ButtonID.PAD1, i)).unbind ();
    }


    /** {@inheritDoc} */
    @Override
    public int getSurfaceID ()
    {
        return this.surfaceID;
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
        display.setHardwareDisplay (this.surfaceFactory.createTextDisplay (this.surfaceID, OutputID.get (OutputID.DISPLAY1, this.textDisplays.size ()), display.getNoOfLines ()));
        this.textDisplays.add (display);
    }


    /** {@inheritDoc} */
    @Override
    public void addGraphicsDisplay (final IGraphicDisplay display)
    {
        final IBitmap bitmap = display.getImage ();
        display.setHardwareDisplay (this.surfaceFactory.createGraphicsDisplay (this.surfaceID, OutputID.DISPLAY1, bitmap));
        this.graphicsDisplays.add (display);
    }


    /** {@inheritDoc} */
    @Override
    public void addPianoKeyboard (final int numKeys, final IMidiInput keyboardInput, final boolean addWheels)
    {
        this.pianoKeyboard = this.surfaceFactory.createPianoKeyboard (this.surfaceID, numKeys);
        this.pianoKeyboard.bind (keyboardInput);

        if (!addWheels)
            return;

        final IHwFader modulationWheel = this.createFader (ContinuousID.MODULATION_WHEEL, "Modulation", true);
        modulationWheel.bind (keyboardInput, BindType.CC, 1);

        final IHwFader pitchbendWheel = this.createFader (ContinuousID.PITCHBEND_WHEEL, "Pitchbend", true);
        pitchbendWheel.bind (keyboardInput, BindType.PITCHBEND, 0);
    }


    /** {@inheritDoc} */
    @Override
    public IHwPianoKeyboard getPianoKeyboard ()
    {
        return this.pianoKeyboard;
    }


    /** {@inheritDoc} */
    @Override
    public IPadGrid getPadGrid ()
    {
        return this.padGrid;
    }


    /** {@inheritDoc} */
    @Override
    public ILightGuide getLightGuide ()
    {
        return this.lightGuide;
    }


    /** {@inheritDoc} */
    @Override
    public IMidiOutput getMidiOutput ()
    {
        return this.output;
    }


    /** {@inheritDoc} */
    @Override
    public IMidiInput getMidiInput ()
    {
        return this.input;
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
    public Map<ButtonID, IHwButton> getButtons ()
    {
        return new EnumMap<> (this.buttons);
    }


    /** {@inheritDoc} */
    @Override
    public IHwButton getButton (final ButtonID buttonID)
    {
        return this.buttons.get (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public IHwContinuousControl getContinuous (final ContinuousID continuousID)
    {
        return this.continuous.get (continuousID);
    }


    /** {@inheritDoc} */
    @Override
    public List<IHwRelativeKnob> getRelativeKnobs ()
    {
        final List<IHwRelativeKnob> relativeKnobs = new ArrayList<> ();

        this.continuous.forEach ( (id, control) -> {
            if (control instanceof final IHwRelativeKnob knob)
                relativeKnobs.add (knob);
        });

        return relativeKnobs;
    }


    /** {@inheritDoc} */
    @Override
    public IHwLight getLight (final OutputID outputID)
    {
        return this.lights.get (outputID);
    }


    /** {@inheritDoc} */
    @Override
    public Collection<IHwLight> getLights ()
    {
        return this.lights.values ();
    }


    /** {@inheritDoc} */
    @Override
    public void unbindAllInputControls ()
    {
        for (final IHwContinuousControl control: this.continuous.values ())
            control.unbind ();
        for (final IHwButton button: this.buttons.values ())
            button.unbind ();
    }


    /** {@inheritDoc} */
    @Override
    public void rebindAllInputControls ()
    {
        for (final IHwContinuousControl control: this.continuous.values ())
            control.rebind ();
        for (final IHwButton button: this.buttons.values ())
            button.rebind ();
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
        final IHwButton button = this.buttons.get (buttonID);
        return button != null && button.isPressed ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLongPressed (final ButtonID buttonID)
    {
        final IHwButton button = this.buttons.get (buttonID);
        return button != null && button.isLongPressed ();
    }


    /** {@inheritDoc} */
    @Override
    public IHwButton createButton (final ButtonID buttonID, final String label)
    {
        final IHwButton button = this.surfaceFactory.createButton (this.surfaceID, buttonID, label);
        this.buttons.put (buttonID, button);
        return button;
    }


    /** {@inheritDoc} */
    @Override
    public IHwLight createLight (final OutputID outputID, final Supplier<ColorEx> supplier, final Consumer<ColorEx> sendConsumer)
    {
        final IHwLight light = this.surfaceFactory.createLight (this.surfaceID, outputID, supplier, sendConsumer);
        if (outputID != null)
            this.lights.put (outputID, light);
        return light;
    }


    /** {@inheritDoc} */
    @Override
    public IHwLight createLight (final OutputID outputID, final IntSupplier supplier, final IntConsumer sendConsumer, final IntFunction<ColorEx> stateToColorFunction, final IHwButton button)
    {
        final IHwLight light = this.surfaceFactory.createLight (this.surfaceID, outputID, supplier, sendConsumer, stateToColorFunction, button);
        if (outputID != null)
            this.lights.put (outputID, light);
        return light;
    }


    /** {@inheritDoc} */
    @Override
    public IHwFader createFader (final ContinuousID faderID, final String label, final boolean isVertical)
    {
        final IHwFader fader = this.surfaceFactory.createFader (this.surfaceID, faderID, label, isVertical);
        this.continuous.put (faderID, fader);
        return fader;
    }


    /** {@inheritDoc} */
    @Override
    public IHwAbsoluteKnob createAbsoluteKnob (final ContinuousID knobID, final String label)
    {
        final IHwAbsoluteKnob knob = this.surfaceFactory.createAbsoluteKnob (this.surfaceID, knobID, label);
        this.continuous.put (knobID, knob);
        return knob;
    }


    /** {@inheritDoc} */
    @Override
    public IHwRelativeKnob createRelativeKnob (final ContinuousID knobID, final String label)
    {
        final IHwRelativeKnob knob = this.surfaceFactory.createRelativeKnob (this.surfaceID, knobID, label);
        this.continuous.put (knobID, knob);
        return knob;
    }


    /** {@inheritDoc} */
    @Override
    public IHwRelativeKnob createRelativeKnob (final ContinuousID knobID, final String label, final RelativeEncoding encoding)
    {
        final IHwRelativeKnob knob = this.surfaceFactory.createRelativeKnob (this.surfaceID, knobID, label, encoding);
        this.continuous.put (knobID, knob);
        return knob;
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int cc, final int state)
    {
        this.setTrigger (this.defaultMidiChannel, cc, state);
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int channel, final int cc, final int state)
    {
        this.setTrigger (null, channel, cc, state);
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final BindType bindType, final int channel, final int cc, final int value)
    {
        if (bindType == BindType.NOTE)
            this.output.sendNoteEx (channel, cc, value);
        else
            this.output.sendCCEx (channel, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void setTriggerConsumed (final ButtonID buttonID)
    {
        final IHwButton button = this.buttons.get (buttonID);
        if (button != null)
            button.setConsumed ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTriggerConsumed (final ButtonID buttonID)
    {
        final IHwButton button = this.buttons.get (buttonID);
        return button != null && button.isConsumed ();
    }


    /** {@inheritDoc} */
    @Override
    public void turnOffTriggers ()
    {
        this.buttons.values ().forEach (button -> {
            final IHwLight light = button.getLight ();
            if (light != null)
                light.turnOff ();
        });

        this.continuous.forEach ( (id, control) -> control.turnOff ());

        this.surfaceFactory.flush ();
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        synchronized (this.updateCounterLock)
        {
            this.updateCounter++;
            this.scheduleTask (this::flushHandler, 1);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void forceFlush ()
    {
        // Flush all text displays. No need for graphics displays since they are refreshed anyway on
        // an interval
        this.textDisplays.forEach (ITextDisplay::forceFlush);

        // Refresh all button LEDs, includes pad grid
        this.buttons.forEach ( (id, button) -> {
            final IHwLight light = button.getLight ();
            if (light != null)
                light.forceFlush ();
        });

        // Refresh all knob/fader LEDs
        this.continuous.forEach ( (id, control) -> control.forceFlush ());

        // Flush additional lights which are not assigned to a button
        this.lights.forEach ( (outputID, light) -> light.forceFlush ());

        if (this.lightGuide != null)
            this.lightGuide.forceFlush ();
    }


    protected void flushHandler ()
    {
        synchronized (this.updateCounterLock)
        {
            if (this.updateCounter == 0)
                return;
        }

        try
        {
            this.internalFlushHandler ();
        }
        catch (final RuntimeException ex)
        {
            this.host.error ("Crash during flush.", ex);
        }

        synchronized (this.updateCounterLock)
        {
            if (this.updateCounter > 1)
            {
                this.updateCounter = 1;
                this.scheduleTask (this::flushHandler, 1);
            }
            else
                this.updateCounter = 0;
        }
    }


    protected void internalFlushHandler ()
    {
        this.updateViewControls ();
        this.updateGrid ();
        this.flushHardware ();
    }


    /** {@inheritDoc} */
    @Override
    public void clearCache ()
    {
        this.surfaceFactory.clearCache ();
    }


    /** {@inheritDoc} */
    @Override
    public final synchronized void shutdown ()
    {
        this.internalShutdown ();
        this.flushHardware ();
    }


    /**
     * Turn off LEDs, clear the display, etc.
     */
    protected void internalShutdown ()
    {
        this.turnOffTriggers ();

        if (this.padGrid != null)
            this.padGrid.turnOff ();

        this.textDisplays.forEach (IDisplay::shutdown);
        this.graphicsDisplays.forEach (IDisplay::shutdown);
    }


    /**
     * Handle received MIDI data.
     *
     * @param status The MIDI status byte
     * @param data1 The MIDI data byte 1
     * @param data2 The MIDI data byte 2
     */
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        final int code = status & 0xF0;
        final int channel = status & 0xF;

        switch (code)
        {
            case MidiConstants.CMD_NOTE_OFF:
                this.handleNoteOff (data1, data2);
                break;

            case MidiConstants.CMD_NOTE_ON:
                this.handleNoteOn (data1, data2);
                break;

            case MidiConstants.CMD_POLY_AFTERTOUCH:
                this.handlePolyAftertouch (data1, data2);
                break;

            case MidiConstants.CMD_CC:
                this.handleCC (data1, data2);
                break;

            case MidiConstants.CMD_PROGRAM_CHANGE:
                this.handleProgramChange (channel, data1, data2);
                break;

            case MidiConstants.CMD_CHANNEL_AFTERTOUCH:
                this.handleChannelAftertouch (data1);
                break;

            case MidiConstants.CMD_PITCHBEND:
                this.handlePitchbend (data1, data2);
                break;

            default:
                this.host.error ("Unhandled MIDI status: " + status);
                break;
        }
    }


    /**
     * Handle CC command.
     *
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    protected void handleCC (final int data1, final int data2)
    {
        // Handled by bind framework
        this.host.error ("CC " + data1 + SHOULD_BE_HANDLED_IN_FRAMEWORK);
    }


    /**
     * Handle pitchbend command.
     *
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    protected void handlePitchbend (final int data1, final int data2)
    {
        this.host.error ("Pitchbend" + SHOULD_BE_HANDLED_IN_FRAMEWORK);
    }


    /**
     * Handle a note off command.
     *
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    protected void handleNoteOff (final int data1, final int data2)
    {
        // Handled by bind framework.
        // Since Bitwig 4.1 notes are also sent to this method no matter if bound or not, therefore
        // do not show the warning message any more...
    }


    /**
     * Handle a note on command.
     *
     * @param data1 First data byte
     * @param data2 Second data byte
     */
    protected void handleNoteOn (final int data1, final int data2)
    {
        // Handled by bind framework.
        // Since Bitwig 4.1 notes are also sent to this method no matter if bound or not, therefore
        // do not show the warning message any more...
    }


    /**
     * Handle channel aftertouch.
     *
     * @param data1 First data byte
     */
    protected void handleChannelAftertouch (final int data1)
    {
        final IView view = this.viewManager.getActive ();
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
        final IView view = this.viewManager.getActive ();
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


    /** {@inheritDoc} */
    @Override
    public IHwSurfaceFactory getSurfaceFactory ()
    {
        return this.surfaceFactory;
    }


    /**
     * Get the host.
     *
     * @return The host
     */
    public IHost getHost ()
    {
        return this.host;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isKnobSensitivitySlow ()
    {
        return this.knobSensitivityIsSlow;
    }


    /** {@inheritDoc} */
    @Override
    public void setKnobSensitivityIsSlow (final boolean knobSensitivityIsSlow)
    {
        this.knobSensitivityIsSlow = knobSensitivityIsSlow;

        this.knobSensitivityObservers.forEach (ISensitivityCallback::knobSensitivityHasChanged);
    }


    /** {@inheritDoc} */
    @Override
    public void addKnobSensitivityObserver (final ISensitivityCallback observer)
    {
        this.knobSensitivityObservers.add (observer);
    }


    /**
     * Handle a MIDI note which belongs to the grid.
     *
     * @param event The button event
     * @param note The MIDI note (already transformed to the grid)
     * @param velocity The velocity of the note
     */
    protected void handleGridNote (final ButtonEvent event, final int note, final int velocity)
    {
        final IView view = this.viewManager.getActive ();
        if (view == null)
            return;
        if (event == ButtonEvent.LONG)
            view.onGridNoteLongPress (note);
        else
            view.onGridNote (note, velocity);
    }


    /**
     * Delayed flush.
     */
    protected void updateViewControls ()
    {
        final IView view = this.viewManager.getActive ();
        if (view != null)
            view.updateControlSurface ();
    }


    /**
     * Redraws the grid for the active view.
     */
    protected void updateGrid ()
    {
        final IView view = this.viewManager.getActive ();
        if (view != null)
            view.drawGrid ();
    }


    /**
     * Flush all changes to the hardware.
     */
    protected void flushHardware ()
    {
        this.textDisplays.forEach (ITextDisplay::flush);
        this.surfaceFactory.flush ();
        this.continuous.values ().forEach (IHwContinuousControl::update);
    }
}