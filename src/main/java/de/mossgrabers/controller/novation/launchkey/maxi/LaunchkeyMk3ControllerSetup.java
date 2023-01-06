// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi;

import de.mossgrabers.controller.novation.launchkey.maxi.command.trigger.ButtonAreaCommand;
import de.mossgrabers.controller.novation.launchkey.maxi.command.trigger.DeviceLockCommand;
import de.mossgrabers.controller.novation.launchkey.maxi.command.trigger.LaunchkeyMk3PlayCommand;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3Display;
import de.mossgrabers.controller.novation.launchkey.maxi.mode.CustomMode;
import de.mossgrabers.controller.novation.launchkey.maxi.mode.LaunchkeyMk3PanoramaMode;
import de.mossgrabers.controller.novation.launchkey.maxi.mode.LaunchkeyMk3ParameterMode;
import de.mossgrabers.controller.novation.launchkey.maxi.mode.LaunchkeyMk3SendMode;
import de.mossgrabers.controller.novation.launchkey.maxi.mode.LaunchkeyMk3VolumeMode;
import de.mossgrabers.controller.novation.launchkey.maxi.view.BrowserView;
import de.mossgrabers.controller.novation.launchkey.maxi.view.DeviceConfigView;
import de.mossgrabers.controller.novation.launchkey.maxi.view.DrumConfigView;
import de.mossgrabers.controller.novation.launchkey.maxi.view.DrumView;
import de.mossgrabers.controller.novation.launchkey.maxi.view.PadModeSelectView;
import de.mossgrabers.controller.novation.launchkey.maxi.view.SessionView;
import de.mossgrabers.controller.novation.launchkey.maxi.view.UserPadView;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.track.SelectPrevNextTrackCommand;
import de.mossgrabers.framework.command.trigger.transport.ConfiguredRecordCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.view.FeatureGroupButtonColorSupplier;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.OutputID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.controller.hardware.IHwLight;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.DummyView;
import de.mossgrabers.framework.view.Views;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;


/**
 * Support for the Novation Launchkey Mk3 controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchkeyMk3ControllerSetup extends AbstractControllerSetup<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    private static final String             NAME_CUSTOM_1     = "Custom 1";
    private static final String             NAME_CUSTOM_2     = "Custom 2";
    private static final String             NAME_CUSTOM_3     = "Custom 3";
    private static final String             NAME_CUSTOM_4     = "Custom 4";

    private static final List<ContinuousID> DEFAULT_FADER_IDS = ContinuousID.createSequentialList (ContinuousID.FADER1, 8);

    // @formatter:off
    private static final int [] DRUM_MATRIX =
    {
        0,  1,  2,  3,  8,  9, 10, 11,
        4,  5,  6,  7, 12, 13, 14, 15,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1
    };
    // @formatter:on


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public LaunchkeyMk3ControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new LaunchkeyMk3ColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new LaunchkeyMk3Configuration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 52, 8, 2);
        this.scales.setDrumMatrix (DRUM_MATRIX);
        this.scales.setDrumNoteEnd (52);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (this.configuration.areMasterTracksIncluded ());
        ms.setNumParamPages (16);
        ms.setNumScenes (2);
        ms.setNumSends (2);
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
        this.model.getTrackBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();

        // A bit tricky to exclude only channel 16...
        final List<String> filters = new ArrayList<> ();
        for (int i = 0; i < 15; i++)
        {
            filters.add (StringUtils.toHexStr (MidiConstants.CMD_NOTE_OFF + i) + "????");
            filters.add (StringUtils.toHexStr (MidiConstants.CMD_NOTE_ON + i) + "????");
        }
        final IMidiInput input = midiAccess.createInput ("Pads", filters.toArray (new String [filters.size ()]));
        final IMidiInput inputKeys = midiAccess.createInput (1, "Keyboard", "8?????" /* Note off */,
                "9?????" /* Note on */, "A?????" /* Polyphonic After-touch */,
                "B?01??" /* Modulation */, "C?????" /* Program change */,
                "B?40??" /* Sustain pedal */, "D?????" /* Channel After-touch */,
                "E?????" /* Pitch-bend */);

        final LaunchkeyMk3ControlSurface surface = new LaunchkeyMk3ControlSurface (this.host, this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);
        surface.addTextDisplay (new LaunchkeyMk3Display (this.host, output));
        surface.addPianoKeyboard (25, inputKeys, true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.createScaleObservers (this.configuration);
        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.activateBrowserObserver (Views.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final LaunchkeyMk3ControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.register (Modes.VOLUME, new LaunchkeyMk3VolumeMode (surface, this.model, AbstractParameterMode.DEFAULT_KNOB_IDS));
        modeManager.register (Modes.PAN, new LaunchkeyMk3PanoramaMode (surface, this.model, AbstractParameterMode.DEFAULT_KNOB_IDS));
        modeManager.register (Modes.SEND1, new LaunchkeyMk3SendMode (0, surface, this.model, AbstractParameterMode.DEFAULT_KNOB_IDS));
        modeManager.register (Modes.SEND2, new LaunchkeyMk3SendMode (1, surface, this.model, AbstractParameterMode.DEFAULT_KNOB_IDS));
        modeManager.register (Modes.DEVICE_PARAMS, new LaunchkeyMk3ParameterMode (surface, this.model, AbstractParameterMode.DEFAULT_KNOB_IDS));
        // Layer send X IDs are used for custom modes
        modeManager.register (Modes.DEVICE_LAYER_SEND1, new CustomMode (1, surface, this.model));
        modeManager.register (Modes.DEVICE_LAYER_SEND2, new CustomMode (2, surface, this.model));
        modeManager.register (Modes.DEVICE_LAYER_SEND3, new CustomMode (3, surface, this.model));
        modeManager.register (Modes.DEVICE_LAYER_SEND4, new CustomMode (4, surface, this.model));

        final ModeManager faderModeManager = surface.getFaderModeManager ();
        faderModeManager.register (Modes.DEVICE_PARAMS, new LaunchkeyMk3ParameterMode (surface, this.model, DEFAULT_FADER_IDS));
        faderModeManager.register (Modes.VOLUME, new LaunchkeyMk3VolumeMode (surface, this.model, DEFAULT_FADER_IDS));
        faderModeManager.register (Modes.SEND1, new LaunchkeyMk3SendMode (0, surface, this.model, DEFAULT_FADER_IDS));
        faderModeManager.register (Modes.SEND2, new LaunchkeyMk3SendMode (1, surface, this.model, DEFAULT_FADER_IDS));
        faderModeManager.register (Modes.DEVICE_LAYER_SEND1, new CustomMode (1, surface, this.model));
        faderModeManager.register (Modes.DEVICE_LAYER_SEND2, new CustomMode (2, surface, this.model));
        faderModeManager.register (Modes.DEVICE_LAYER_SEND3, new CustomMode (3, surface, this.model));
        faderModeManager.register (Modes.DEVICE_LAYER_SEND4, new CustomMode (4, surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final LaunchkeyMk3ControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        viewManager.register (Views.SESSION, new SessionView (surface, this.model));
        viewManager.register (Views.CONTROL, new PadModeSelectView (surface, this.model));
        viewManager.register (Views.DRUM, new DrumView (surface, this.model));
        viewManager.register (Views.PLAY, new UserPadView (surface, this.model));
        viewManager.register (Views.SHIFT, new DrumConfigView (surface, this.model));
        viewManager.register (Views.DEVICE, new DeviceConfigView (surface, this.model));
        viewManager.register (Views.BROWSER, new BrowserView (surface, this.model));

        viewManager.register (Views.DUMMY1, new DummyView<> ("Scale Chords", surface, this.model));
        viewManager.register (Views.DUMMY2, new DummyView<> ("User Chords", surface, this.model));
        viewManager.register (Views.DUMMY3, new DummyView<> (NAME_CUSTOM_1, surface, this.model));
        viewManager.register (Views.DUMMY4, new DummyView<> (NAME_CUSTOM_2, surface, this.model));
        viewManager.register (Views.DUMMY5, new DummyView<> (NAME_CUSTOM_3, surface, this.model));
        viewManager.register (Views.DUMMY6, new DummyView<> (NAME_CUSTOM_4, surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final LaunchkeyMk3ControlSurface surface = this.getSurface ();
        final ITransport t = this.model.getTransport ();
        final ViewManager viewManager = surface.getViewManager ();

        this.addButton (ButtonID.SHIFT, "Shift", NopCommand.INSTANCE, LaunchkeyMk3ControlSurface.LAUNCHKEY_SHIFT);

        this.addButton (ButtonID.PLAY, "Play", new LaunchkeyMk3PlayCommand (this.model, surface), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_PLAY, t::isPlaying);
        this.addButton (ButtonID.STOP, "Stop", new StopCommand<> (this.model, surface), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_STOP, () -> !t.isPlaying ());
        final ConfiguredRecordCommand<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration> recordCommand = new ConfiguredRecordCommand<> (this.model, surface);
        this.addButton (ButtonID.RECORD, "Record", recordCommand, 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_RECORD, (BooleanSupplier) recordCommand::isLit);
        this.addButton (ButtonID.REPEAT, "Repeat", new ToggleLoopCommand<> (this.model, surface), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_LOOP, t::isLoop);

        this.addButton (ButtonID.NEW, "Capture MIDI", new NewCommand<> (this.model, surface), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_CAPTURE_MIDI);
        this.addButton (ButtonID.QUANTIZE, "Quantize", new QuantizeCommand<> (this.model, surface), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_QUANTIZE);
        this.addButton (ButtonID.METRONOME, "Metronome", new MetronomeCommand<> (this.model, surface, true), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_CLICK, t::isMetronomeOn);
        this.addButton (ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_UNDO);

        this.addDummyButton (ButtonID.F3, 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_DEVICE_SELECT);
        this.addButton (ButtonID.TOGGLE_DEVICE, "Device Lock", new DeviceLockCommand (this.model, surface), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_DEVICE_LOCK, () -> this.model.getCursorDevice ().isPinned () ? 127 : 40);

        this.addButton (ButtonID.MOVE_TRACK_LEFT, "Previous", new SelectPrevNextTrackCommand<> (this.model, surface, true), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_TRACK_LEFT);
        this.addButton (ButtonID.MOVE_TRACK_RIGHT, "Next", new SelectPrevNextTrackCommand<> (this.model, surface, false), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_TRACK_RIGHT);

        // Scene buttons
        this.addButton (ButtonID.SCENE1, "Scene 1", new ViewButtonCommand<> (ButtonID.SCENE1, surface), LaunchkeyMk3ControlSurface.LAUNCHKEY_SCENE1, new FeatureGroupButtonColorSupplier (viewManager, ButtonID.SCENE1));
        this.addButton (ButtonID.SCENE2, "Scene 2", new ViewButtonCommand<> (ButtonID.SCENE2, surface), LaunchkeyMk3ControlSurface.LAUNCHKEY_SCENE2, new FeatureGroupButtonColorSupplier (viewManager, ButtonID.SCENE2));
        this.addButton (surface, ButtonID.ARROW_UP, "Up", new ViewButtonCommand<> (ButtonID.ARROW_UP, surface), 15, 0, LaunchkeyMk3ControlSurface.LAUNCHKEY_ARROW_UP, -1, true, new FeatureGroupButtonColorSupplier (viewManager, ButtonID.ARROW_UP));
        this.addButton (surface, ButtonID.ARROW_DOWN, "Down", new ViewButtonCommand<> (ButtonID.ARROW_DOWN, surface), 15, 0, LaunchkeyMk3ControlSurface.LAUNCHKEY_ARROW_DOWN, -1, true, new FeatureGroupButtonColorSupplier (viewManager, ButtonID.ARROW_DOWN));
        // Ignore redundant messages sent by arrow up/down buttons
        this.addDummyButton (ButtonID.F1, 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_SCENE1);
        this.addDummyButton (ButtonID.F2, 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_SCENE2);

        // View selection with Pads in Shift mode
        this.createViewButton (ButtonID.ROW2_1, OutputID.LED_RING1, "Session", Views.SESSION, LaunchkeyMk3ControlSurface.PAD_MODE_SESSION);
        this.createViewButton (ButtonID.ROW2_2, OutputID.LED_RING2, "Drum", Views.DRUM, LaunchkeyMk3ControlSurface.PAD_MODE_DRUM);
        this.createViewButton (ButtonID.ROW2_3, OutputID.LED_RING3, "Scale Chords", Views.DUMMY1, LaunchkeyMk3ControlSurface.PAD_MODE_SCALE_CHORDS);
        this.createViewButton (ButtonID.ROW2_4, OutputID.LED_RING4, "User Chords", Views.DUMMY2, LaunchkeyMk3ControlSurface.PAD_MODE_USER_CHORDS);
        this.createViewButton (ButtonID.ROW2_5, OutputID.LED_RING5, NAME_CUSTOM_1, Views.DUMMY3, LaunchkeyMk3ControlSurface.PAD_MODE_CUSTOM_MODE0);
        this.createViewButton (ButtonID.ROW2_6, OutputID.LED_RING6, NAME_CUSTOM_2, Views.DUMMY4, LaunchkeyMk3ControlSurface.PAD_MODE_CUSTOM_MODE1);
        this.createViewButton (ButtonID.ROW2_7, OutputID.LED_RING7, NAME_CUSTOM_3, Views.DUMMY5, LaunchkeyMk3ControlSurface.PAD_MODE_CUSTOM_MODE2);
        this.createViewButton (ButtonID.ROW2_8, OutputID.LED_RING8, NAME_CUSTOM_4, Views.DUMMY6, LaunchkeyMk3ControlSurface.PAD_MODE_CUSTOM_MODE3);
        this.createViewButton (ButtonID.DEVICE, OutputID.LED_RING9, "Device Select", Views.DEVICE, LaunchkeyMk3ControlSurface.PAD_MODE_DEVICE_SELECT);
        this.createViewButton (ButtonID.BROWSE, OutputID.LED_RING10, "Browser", Views.BROWSER, LaunchkeyMk3ControlSurface.PAD_MODE_NAVIGATION);

        // Knob mode selection with Pads in Shift mode
        this.createModeButton (ButtonID.ROW1_1, OutputID.LED1, "Device", Modes.DEVICE_PARAMS, LaunchkeyMk3ControlSurface.KNOB_MODE_PARAMS);
        this.createModeButton (ButtonID.ROW1_2, OutputID.LED2, "Volume", Modes.VOLUME, LaunchkeyMk3ControlSurface.KNOB_MODE_VOLUME);
        this.createModeButton (ButtonID.ROW1_3, OutputID.LED3, "Pan", Modes.PAN, LaunchkeyMk3ControlSurface.KNOB_MODE_PAN);
        this.createModeButton (ButtonID.ROW1_4, OutputID.LED4, "Send 1", Modes.SEND1, LaunchkeyMk3ControlSurface.KNOB_MODE_SEND1);
        this.createModeButton (ButtonID.ROW1_5, OutputID.LED5, "Send 2", Modes.SEND2, LaunchkeyMk3ControlSurface.KNOB_MODE_SEND2);
        this.createModeButton (ButtonID.ROW1_6, OutputID.LED6, NAME_CUSTOM_1, Modes.DEVICE_LAYER_SEND1, LaunchkeyMk3ControlSurface.KNOB_MODE_CUSTOM1);
        this.createModeButton (ButtonID.ROW1_7, OutputID.LED7, NAME_CUSTOM_2, Modes.DEVICE_LAYER_SEND2, LaunchkeyMk3ControlSurface.KNOB_MODE_CUSTOM2);
        this.createModeButton (ButtonID.ROW1_8, OutputID.LED8, NAME_CUSTOM_3, Modes.DEVICE_LAYER_SEND3, LaunchkeyMk3ControlSurface.KNOB_MODE_CUSTOM3);
        this.createModeButton (ButtonID.USER, OutputID.LED9, NAME_CUSTOM_4, Modes.DEVICE_LAYER_SEND4, LaunchkeyMk3ControlSurface.KNOB_MODE_CUSTOM4);

        // Fader mode selection with fader buttons in Shift mode
        this.createFaderModeButton (ButtonID.ROW3_1, OutputID.LIGHT_GUIDE1, "Device", Modes.DEVICE_PARAMS, LaunchkeyMk3ControlSurface.FADER_MODE_PARAMS);
        this.createFaderModeButton (ButtonID.ROW3_2, OutputID.LIGHT_GUIDE2, "Volume", Modes.VOLUME, LaunchkeyMk3ControlSurface.FADER_MODE_VOLUME);
        this.createFaderModeButton (ButtonID.ROW3_3, OutputID.LIGHT_GUIDE3, "Send 1", Modes.SEND1, LaunchkeyMk3ControlSurface.FADER_MODE_SEND1);
        this.createFaderModeButton (ButtonID.ROW3_4, OutputID.LIGHT_GUIDE4, "Send 2", Modes.SEND2, LaunchkeyMk3ControlSurface.FADER_MODE_SEND2);
        this.createFaderModeButton (ButtonID.ROW3_5, OutputID.LIGHT_GUIDE5, NAME_CUSTOM_1, Modes.DEVICE_LAYER_SEND1, LaunchkeyMk3ControlSurface.FADER_MODE_CUSTOM1);
        this.createFaderModeButton (ButtonID.ROW3_6, OutputID.LIGHT_GUIDE6, NAME_CUSTOM_2, Modes.DEVICE_LAYER_SEND2, LaunchkeyMk3ControlSurface.FADER_MODE_CUSTOM2);
        this.createFaderModeButton (ButtonID.ROW3_7, OutputID.LIGHT_GUIDE7, NAME_CUSTOM_3, Modes.DEVICE_LAYER_SEND3, LaunchkeyMk3ControlSurface.FADER_MODE_CUSTOM3);
        this.createFaderModeButton (ButtonID.ROW3_8, OutputID.LIGHT_GUIDE8, NAME_CUSTOM_4, Modes.DEVICE_LAYER_SEND4, LaunchkeyMk3ControlSurface.FADER_MODE_CUSTOM4);

        // Fader mode buttons
        for (int i = 0; i < 8; i++)
        {
            final ButtonID row1ButtonID = ButtonID.get (ButtonID.ROW_SELECT_1, i);
            final IHwButton button = surface.createButton (row1ButtonID, "Select " + (i + 1));
            final ButtonAreaCommand command = new ButtonAreaCommand (i, this.model, surface);
            button.bind (command);

            final IMidiInput midiInput = surface.getMidiInput ();
            final BindType triggerBindType = this.getTriggerBindType (row1ButtonID);
            final int midiControl = LaunchkeyMk3ControlSurface.LAUNCHKEY_SELECT1 + i;
            button.bind (midiInput, triggerBindType, 15, midiControl);

            final IntSupplier supplier = command::getButtonColor;
            surface.createLight (null, supplier::getAsInt, color -> surface.setTrigger (color >= 0x1000 ? 2 : 0, midiControl, color >= 0x1000 ? color - 0x1000 : color), state -> this.colorManager.getColor (state >= 0x1000 ? state - 0x1000 : state, row1ButtonID), button);
        }

        this.addButton (surface, ButtonID.MASTERTRACK, "Toggle Select/RecArm", (event, velocity) -> {

            if (event == ButtonEvent.DOWN)
                ButtonAreaCommand.toggleSelect ();

        }, 15, 0, LaunchkeyMk3ControlSurface.LAUNCHKEY_TOGGLE_SELECT, -1, true, () -> ButtonAreaCommand.isSelect () ? 40 : 127);

        // Online state
        this.addButton (ButtonID.CONTROL, "DAW Online", (event, velocity) -> surface.setDAWConnected (velocity > 0), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_DAW_ONLINE, surface::isDAWConnected);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        for (int i = 0; i < 8; i++)
        {
            this.addAbsoluteKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1), null, BindType.CC, 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_KNOB_1 + i).setIndexInGroup (i);
            this.addFader (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), null, BindType.CC, 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_FADER_1 + i).setIndexInGroup (i);
        }

        this.addFader (ContinuousID.FADER_MASTER, "Master Fader", null, BindType.CC, 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_FADER_MASTER).bind (this.model.getMasterTrack ().getVolumeParameter ());
    }


    private void createViewButton (final ButtonID buttonID, final OutputID outputID, final String label, final Views view, final int viewIndex)
    {
        final LaunchkeyMk3ControlSurface surface = this.getSurface ();
        final ViewMultiSelectCommand<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration> viewSelectCommand = new ViewMultiSelectCommand<> (this.model, surface, view);
        this.addButton (surface, buttonID, label, (event, velocity) -> {
            viewSelectCommand.executeNormal (event);
            if (event == ButtonEvent.DOWN)
                surface.getPadGrid ().setView (view);
        }, 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_VIEW_SELECT, viewIndex, false, null);
        final IHwLight light = surface.createLight (outputID, () -> surface.getViewManager ().isActive (view) ? ColorEx.ORANGE : ColorEx.DARK_ORANGE, color -> {
            // Intentionally empty
        });
        surface.getButton (buttonID).addLight (light);
    }


    private void createModeButton (final ButtonID buttonID, final OutputID outputID, final String label, final Modes mode, final int modeIndex)
    {
        final LaunchkeyMk3ControlSurface surface = this.getSurface ();
        final ModeSelectCommand<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration> modeSelectCommand = new ModeSelectCommand<> (this.model, surface, mode);
        this.addButton (surface, buttonID, label, (event, velocity) -> modeSelectCommand.executeNormal (event), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_MODE_SELECT, modeIndex, false, null);
        final IHwLight light = surface.createLight (outputID, () -> surface.getModeManager ().isActive (mode) ? ColorEx.GREEN : ColorEx.DARK_GREEN, color -> {
            // Intentionally empty
        });
        surface.getButton (buttonID).addLight (light);
    }


    private void createFaderModeButton (final ButtonID buttonID, final OutputID outputID, final String label, final Modes mode, final int modeIndex)
    {
        final LaunchkeyMk3ControlSurface surface = this.getSurface ();
        final ModeSelectCommand<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration> modeSelectCommand = new ModeSelectCommand<> (surface.getFaderModeManager (), this.model, surface, mode);
        this.addButton (surface, buttonID, label, (event, velocity) -> modeSelectCommand.executeNormal (event), 15, LaunchkeyMk3ControlSurface.LAUNCHKEY_FADER_SELECT, modeIndex, false, null);
        final IHwLight light = surface.createLight (outputID, () -> surface.getFaderModeManager ().isActive (mode) ? ColorEx.BLUE : ColorEx.DARK_BLUE, color -> {
            // Intentionally empty
        });
        surface.getButton (buttonID).addLight (light);
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.CONTROL)
            return BindType.NOTE;
        return super.getTriggerBindType (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final LaunchkeyMk3ControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (193.25, 114.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD2).setBounds (246.75, 114.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD3).setBounds (300.75, 114.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD4).setBounds (354.5, 114.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD5).setBounds (407.75, 114.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD6).setBounds (462.25, 114.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD7).setBounds (516.0, 114.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD8).setBounds (569.75, 114.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD9).setBounds (193.25, 62.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD10).setBounds (246.75, 62.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD11).setBounds (300.75, 62.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD12).setBounds (354.5, 62.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD13).setBounds (407.75, 62.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD14).setBounds (462.25, 62.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD15).setBounds (516.0, 62.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD16).setBounds (569.75, 62.5, 47.0, 46.5);

        surface.getButton (ButtonID.PAD17).setBounds (192.0, 228.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD18).setBounds (245.5, 228.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD19).setBounds (299.5, 228.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD20).setBounds (353.25, 228.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD21).setBounds (406.5, 228.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD22).setBounds (461.0, 228.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD23).setBounds (514.75, 228.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD24).setBounds (568.5, 228.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD25).setBounds (192.0, 175.0, 47.0, 46.5);
        surface.getButton (ButtonID.PAD26).setBounds (245.5, 175.0, 47.0, 46.5);
        surface.getButton (ButtonID.PAD27).setBounds (299.5, 175.0, 47.0, 46.5);
        surface.getButton (ButtonID.PAD28).setBounds (353.25, 175.0, 47.0, 46.5);
        surface.getButton (ButtonID.PAD29).setBounds (406.5, 175.0, 47.0, 46.5);
        surface.getButton (ButtonID.PAD30).setBounds (461.0, 175.0, 47.0, 46.5);
        surface.getButton (ButtonID.PAD31).setBounds (514.75, 175.0, 47.0, 46.5);
        surface.getButton (ButtonID.PAD32).setBounds (568.5, 175.0, 47.0, 46.5);

        surface.getButton (ButtonID.PAD33).setBounds (192.0, 342.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD34).setBounds (245.5, 342.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD35).setBounds (299.5, 342.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD36).setBounds (353.25, 342.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD37).setBounds (406.5, 342.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD38).setBounds (461.0, 342.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD39).setBounds (514.75, 342.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD40).setBounds (568.5, 342.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD41).setBounds (192.0, 287.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD42).setBounds (245.5, 287.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD43).setBounds (299.5, 287.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD44).setBounds (353.25, 287.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD45).setBounds (406.5, 287.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD46).setBounds (461.0, 287.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD47).setBounds (514.75, 287.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD48).setBounds (568.5, 287.5, 47.0, 46.5);

        surface.getButton (ButtonID.SHIFT).setBounds (131.0, 410.75, 38.5, 19.25);
        surface.getButton (ButtonID.MOVE_TRACK_LEFT).setBounds (131.0, 438.25, 38.5, 19.25);
        surface.getButton (ButtonID.MOVE_TRACK_RIGHT).setBounds (180.75, 438.0, 38.5, 19.25);
        surface.getButton (ButtonID.BROWSE).setBounds (180.75, 410.75, 38.5, 19.25);

        surface.getButton (ButtonID.PLAY).setBounds (1096.0, 366.5, 62.5, 24.0);
        surface.getButton (ButtonID.STOP).setBounds (1171.75, 366.5, 62.5, 24.0);
        surface.getButton (ButtonID.RECORD).setBounds (1247.5, 366.5, 62.5, 24.0);
        surface.getButton (ButtonID.REPEAT).setBounds (1323.0, 366.5, 62.5, 24.0);
        surface.getButton (ButtonID.NEW).setBounds (1096.0, 330.0, 62.5, 24.0);
        surface.getButton (ButtonID.QUANTIZE).setBounds (1171.75, 330.0, 62.5, 24.0);
        surface.getButton (ButtonID.METRONOME).setBounds (1247.5, 330.0, 62.5, 24.0);
        surface.getButton (ButtonID.UNDO).setBounds (1323.0, 330.0, 62.5, 24.0);

        surface.getButton (ButtonID.SCENE1).setBounds (623.5, 62.5, 47.0, 46.5);
        surface.getButton (ButtonID.SCENE2).setBounds (623.5, 114.75, 47.0, 46.5);
        surface.getButton (ButtonID.ARROW_UP).setBounds (137.75, 62.5, 47.0, 46.5);
        surface.getButton (ButtonID.ARROW_DOWN).setBounds (137.75, 114.75, 47.0, 46.5);

        surface.getButton (ButtonID.ROW3_1).setBounds (647.5, 213.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW3_2).setBounds (699.5, 213.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW3_3).setBounds (755.5, 213.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW3_4).setBounds (807.5, 213.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW3_5).setBounds (861.5, 213.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW3_6).setBounds (915.5, 213.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW3_7).setBounds (969.5, 213.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW3_8).setBounds (1023.5, 213.0, 47.0, 46.5);

        surface.getButton (ButtonID.ROW1_1).setBounds (647.5, 277.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW1_2).setBounds (699.5, 277.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW1_3).setBounds (755.5, 277.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW1_4).setBounds (807.5, 277.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW1_5).setBounds (861.5, 277.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW1_6).setBounds (915.5, 277.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW1_7).setBounds (969.5, 277.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW1_8).setBounds (1023.5, 277.0, 47.0, 46.5);
        surface.getButton (ButtonID.USER).setBounds (1077.5, 277.0, 47.0, 46.5);

        surface.getButton (ButtonID.ROW2_1).setBounds (647.5, 341.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW2_2).setBounds (699.5, 341.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW2_3).setBounds (755.5, 341.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW2_4).setBounds (807.5, 341.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW2_5).setBounds (861.5, 341.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW2_6).setBounds (915.5, 341.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW2_7).setBounds (969.5, 341.0, 47.0, 46.5);
        surface.getButton (ButtonID.ROW2_8).setBounds (1023.5, 341.0, 47.0, 46.5);

        surface.getButton (ButtonID.CONTROL).setBounds (1337.25, 14.0, 49.75, 29.0);

        surface.getButton (ButtonID.TOGGLE_DEVICE).setBounds (625.0, 16.25, 116.0, 23.75);
        surface.getButton (ButtonID.DEVICE).setBounds (755.75, 16.25, 116.0, 23.75);

        surface.getButton (ButtonID.ROW_SELECT_1).setBounds (730, 166.5, 38.5, 27.5);
        surface.getButton (ButtonID.ROW_SELECT_2).setBounds (779, 166.5, 38.5, 27.5);
        surface.getButton (ButtonID.ROW_SELECT_3).setBounds (828, 166.5, 38.5, 27.5);
        surface.getButton (ButtonID.ROW_SELECT_4).setBounds (877, 166.5, 38.5, 27.5);
        surface.getButton (ButtonID.ROW_SELECT_5).setBounds (926, 166.5, 38.5, 27.5);
        surface.getButton (ButtonID.ROW_SELECT_6).setBounds (975, 166.5, 38.5, 27.5);
        surface.getButton (ButtonID.ROW_SELECT_7).setBounds (1024.25, 166.5, 38.5, 27.5);
        surface.getButton (ButtonID.ROW_SELECT_8).setBounds (1073.25, 166.5, 38.5, 27.5);
        surface.getButton (ButtonID.MASTERTRACK).setBounds (1122.25, 166.5, 38.5, 27.5);

        surface.getContinuous (ContinuousID.FADER1).setBounds (730.0, 64.25, 38.5, 92.25);
        surface.getContinuous (ContinuousID.FADER2).setBounds (779.0, 64.25, 38.5, 92.25);
        surface.getContinuous (ContinuousID.FADER3).setBounds (828.0, 64.25, 38.5, 92.25);
        surface.getContinuous (ContinuousID.FADER4).setBounds (877.0, 64.25, 38.5, 92.25);
        surface.getContinuous (ContinuousID.FADER5).setBounds (926.0, 64.25, 38.5, 92.25);
        surface.getContinuous (ContinuousID.FADER6).setBounds (975.0, 64.25, 38.5, 92.25);
        surface.getContinuous (ContinuousID.FADER7).setBounds (1024.25, 64.25, 38.5, 92.25);
        surface.getContinuous (ContinuousID.FADER8).setBounds (1073.25, 64.25, 38.5, 92.25);
        surface.getContinuous (ContinuousID.FADER_MASTER).setBounds (1122.25, 64.25, 38.5, 92.25);

        surface.getContinuous (ContinuousID.KNOB1).setBounds (201.25, 15.0, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB2).setBounds (255.0, 15.0, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB3).setBounds (309.0, 15.0, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB4).setBounds (362.75, 15.0, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB5).setBounds (416.75, 15.0, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB6).setBounds (470.5, 15.0, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB7).setBounds (524.5, 15.0, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB8).setBounds (578.25, 15.0, 25.5, 25.0);

        surface.getContinuous (ContinuousID.MODULATION_WHEEL).setBounds (187.25, 485.5, 31.75, 98.0);
        surface.getContinuous (ContinuousID.PITCHBEND_WHEEL).setBounds (134.25, 485.5, 31.75, 98.0);

        surface.getPianoKeyboard ().setBounds (277.0, 414.25, 726.75, 175.75);
        surface.getTextDisplay ().getHardwareDisplay ().setBounds (4.25, 14.0, 106.0, 562.75);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final LaunchkeyMk3ControlSurface surface = this.getSurface ();

        surface.getPadGrid ().setView (Views.SESSION);
        surface.getViewManager ().setActive (Views.SESSION);
        surface.getModeManager ().setActive (Modes.PAN);
        surface.getFaderModeManager ().setActive (Modes.VOLUME);
        surface.setKnobMode (LaunchkeyMk3ControlSurface.KNOB_MODE_VOLUME);
        surface.setPadMode (LaunchkeyMk3ControlSurface.PAD_MODE_SESSION);

        // Switch the Launchkey to DAW mode and wait until it is ready
        this.host.scheduleTask ( () -> {

            surface.setLaunchpadToDAW (true);
            this.waitForConnection ();

        }, 1000);
    }


    /**
     * Check if the DAW mode is ready in the Launchkey.
     */
    private void waitForConnection ()
    {
        final LaunchkeyMk3ControlSurface surface = this.getSurface ();
        if (surface.isDAWConnected ())
        {
            this.startup2 ();
            return;
        }

        this.host.scheduleTask (this::waitForConnection, 200);
    }


    /**
     * DAW mode is ready. Update all states on the device.
     */
    private void startup2 ()
    {
        final LaunchkeyMk3ControlSurface surface = this.getSurface ();

        // Sync modes to device
        final IMidiOutput midiOutput = surface.getMidiOutput ();
        midiOutput.sendCCEx (15, LaunchkeyMk3ControlSurface.LAUNCHKEY_MODE_SELECT, LaunchkeyMk3ControlSurface.KNOB_MODE_PAN);
        midiOutput.sendCCEx (15, LaunchkeyMk3ControlSurface.LAUNCHKEY_FADER_SELECT, LaunchkeyMk3ControlSurface.FADER_MODE_VOLUME);

        // Flush display and LEDs
        surface.forceFlush ();
    }
}
