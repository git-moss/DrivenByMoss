// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii;

import de.mossgrabers.controller.slmkiii.command.continuous.VolumeFaderCommand;
import de.mossgrabers.controller.slmkiii.command.trigger.ButtonAreaCommand;
import de.mossgrabers.controller.slmkiii.command.trigger.DeviceModeCommand;
import de.mossgrabers.controller.slmkiii.command.trigger.TrackModeCommand;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIScales;
import de.mossgrabers.controller.slmkiii.mode.BaseMode;
import de.mossgrabers.controller.slmkiii.mode.BrowserMode;
import de.mossgrabers.controller.slmkiii.mode.OptionsMode;
import de.mossgrabers.controller.slmkiii.mode.SequencerResolutionMode;
import de.mossgrabers.controller.slmkiii.mode.device.ParametersMode;
import de.mossgrabers.controller.slmkiii.mode.device.UserMode;
import de.mossgrabers.controller.slmkiii.mode.track.PanMode;
import de.mossgrabers.controller.slmkiii.mode.track.SendMode;
import de.mossgrabers.controller.slmkiii.mode.track.TrackMode;
import de.mossgrabers.controller.slmkiii.mode.track.VolumeMode;
import de.mossgrabers.controller.slmkiii.view.ColorView;
import de.mossgrabers.controller.slmkiii.view.DrumView;
import de.mossgrabers.controller.slmkiii.view.SessionView;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.ShiftCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.OutputID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.valuechanger.DefaultValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.DeviceInquiry;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Novation SLMkIII controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIIControllerSetup extends AbstractControllerSetup<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    // @formatter:off
    private static final int [] DRUM_MATRIX =
    {
        0,  1,  2,  3,  4,  5,  6,  7,
        8,  9, 10, 11, 12, 13, 14, 15,
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
    public SLMkIIIControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new SLMkIIIColorManager ();
        this.valueChanger = new DefaultValueChanger (1024, 8, 1);
        this.configuration = new SLMkIIIConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new SLMkIIIScales (this.valueChanger, 36, 52, 8, 2);
        this.scales.setDrumMatrix (DRUM_MATRIX);
        this.scales.setDrumNoteEnd (52);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (true);
        ms.setNumScenes (2);
        ms.setNumSends (8);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput keyboardInput = midiAccess.createInput (1, "Keyboard", "8?????", "9?????", "B?????", "D?????", "E?????");
        final IHost hostProxy = this.model.getHost ();
        final IMidiInput input = midiAccess.createInput ("Pads", "8?????", "9?????");
        final SLMkIIIControlSurface surface = new SLMkIIIControlSurface (hostProxy, this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);

        surface.addPianoKeyboard (49, keyboardInput, true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final SLMkIIIControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.registerMode (Modes.TRACK, new TrackMode (surface, this.model));
        modeManager.registerMode (Modes.VOLUME, new VolumeMode (surface, this.model));
        modeManager.registerMode (Modes.PAN, new PanMode (surface, this.model));
        for (int i = 0; i < 8; i++)
            modeManager.registerMode (Modes.get (Modes.SEND1, i), new SendMode (i, surface, this.model));
        modeManager.registerMode (Modes.DEVICE_PARAMS, new ParametersMode (surface, this.model));
        modeManager.registerMode (Modes.BROWSER, new BrowserMode (surface, this.model));
        modeManager.registerMode (Modes.USER, new UserMode (surface, this.model));

        modeManager.registerMode (Modes.FUNCTIONS, new OptionsMode (surface, this.model));
        modeManager.registerMode (Modes.GROOVE, new SequencerResolutionMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final SLMkIIIControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        viewManager.registerView (Views.SESSION, new SessionView (surface, this.model));
        viewManager.registerView (Views.DRUM, new DrumView (surface, this.model));
        viewManager.registerView (Views.COLOR, new ColorView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        this.createScaleObservers (this.configuration);

        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.getSurface ().getModeManager ().addModeListener ( (oldMode, newMode) -> this.updateIndication (newMode));

        this.activateBrowserObserver (Modes.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final SLMkIIIControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        final ViewManager viewManager = surface.getViewManager ();
        final ITransport t = this.model.getTransport ();
        final ITrackBank tb = this.model.getTrackBank ();

        final WindCommand<SLMkIIIControlSurface, SLMkIIIConfiguration> rewindCommand = new WindCommand<> (this.model, surface, false);
        final WindCommand<SLMkIIIControlSurface, SLMkIIIConfiguration> forwardCommand = new WindCommand<> (this.model, surface, true);
        this.addButton (ButtonID.REWIND, "<<", rewindCommand, 15, SLMkIIIControlSurface.MKIII_TRANSPORT_REWIND, () -> rewindCommand.isRewinding () ? 1 : 0, SLMkIIIColorManager.BUTTON_STATE_WIND_ON, SLMkIIIColorManager.BUTTON_STATE_WIND_HI);
        this.addButton (ButtonID.FORWARD, ">>", forwardCommand, 15, SLMkIIIControlSurface.MKIII_TRANSPORT_FORWARD, () -> forwardCommand.isForwarding () ? 1 : 0, SLMkIIIColorManager.BUTTON_STATE_WIND_ON, SLMkIIIColorManager.BUTTON_STATE_WIND_HI);
        this.addButton (ButtonID.LOOP, "Loop", new ToggleLoopCommand<> (this.model, surface), 15, SLMkIIIControlSurface.MKIII_TRANSPORT_LOOP, () -> t.isLoop () ? 1 : 0, SLMkIIIColorManager.BUTTON_STATE_LOOP_ON, SLMkIIIColorManager.BUTTON_STATE_LOOP_HI);
        this.addButton (ButtonID.STOP, "Stop", new StopCommand<> (this.model, surface), 15, SLMkIIIControlSurface.MKIII_TRANSPORT_STOP, () -> !t.isPlaying () ? 1 : 0, SLMkIIIColorManager.BUTTON_STATE_STOP_ON, SLMkIIIColorManager.BUTTON_STATE_STOP_HI);
        this.addButton (ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface), 15, SLMkIIIControlSurface.MKIII_TRANSPORT_PLAY, () -> t.isPlaying () ? 1 : 0, SLMkIIIColorManager.BUTTON_STATE_PLAY_ON, SLMkIIIColorManager.BUTTON_STATE_PLAY_HI);
        this.addButton (ButtonID.RECORD, "Record", new RecordCommand<> (this.model, surface), 15, SLMkIIIControlSurface.MKIII_TRANSPORT_RECORD, () -> {
            final boolean isOn = this.isRecordShifted (surface) ? t.isLauncherOverdub () : t.isRecording ();
            return isOn ? 1 : 0;
        }, SLMkIIIColorManager.BUTTON_STATE_REC_ON, SLMkIIIColorManager.BUTTON_STATE_REC_HI, SLMkIIIColorManager.BUTTON_STATE_OVR_ON, SLMkIIIColorManager.BUTTON_STATE_OVR_HI);

        for (int i = 0; i < 8; i++)
        {
            final int index = i;

            final ButtonID buttonID = ButtonID.get (ButtonID.ROW1_1, i);
            this.addButton (buttonID, "Select " + (i + 1), new ButtonRowModeCommand<> (0, i, this.model, surface), 15, SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_1 + i, () -> {

                final Mode mode = modeManager.getActiveOrTempMode ();
                return mode == null ? 0 : mode.getButtonColor (buttonID);

            });
            this.addButton (ButtonID.get (ButtonID.ROW2_1, i), "Mute/Monitor " + (i + 1), new ButtonAreaCommand (0, i, this.model, surface), 15, SLMkIIIControlSurface.MKIII_BUTTON_ROW1_1 + i, () -> {

                final ITrack track = tb.getItem (index);
                if (!track.doesExist ())
                    return SLMkIIIColorManager.SLMKIII_BLACK;
                if (surface.isMuteSolo ())
                    return track.isMute () ? SLMkIIIColorManager.SLMKIII_ORANGE : SLMkIIIColorManager.SLMKIII_ORANGE_HALF;
                return track.isMonitor () ? SLMkIIIColorManager.SLMKIII_GREEN : SLMkIIIColorManager.SLMKIII_GREEN_HALF;

            });
            this.addButton (ButtonID.get (ButtonID.ROW3_1, i), "Solo/Arm" + (i + 1), new ButtonAreaCommand (1, i, this.model, surface), 15, SLMkIIIControlSurface.MKIII_BUTTON_ROW2_1 + i, () -> {

                final ITrack track = tb.getItem (index);
                if (!track.doesExist ())
                    return SLMkIIIColorManager.SLMKIII_BLACK;
                if (surface.isMuteSolo ())
                    return track.isSolo () ? SLMkIIIColorManager.SLMKIII_YELLOW : SLMkIIIColorManager.SLMKIII_YELLOW_HALF;
                return track.isRecArm () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
            });
        }

        this.addButton (ButtonID.ARROW_UP, "Up", new DeviceModeCommand (this.model, surface), 15, SLMkIIIControlSurface.MKIII_DISPLAY_UP, () -> getDeviceModeColor (modeManager));
        this.addButton (ButtonID.ARROW_DOWN, "Down", new TrackModeCommand (this.model, surface), 15, SLMkIIIControlSurface.MKIII_DISPLAY_DOWN, () -> getTrackModeColor (modeManager));

        this.addButton (ButtonID.SHIFT, "Shift", new ShiftCommand<> (this.model, surface), 15, SLMkIIIControlSurface.MKIII_SHIFT);
        this.addButton (ButtonID.USER, "Options", new ModeSelectCommand<> (this.model, surface, Modes.FUNCTIONS, true), 15, SLMkIIIControlSurface.MKIII_OPTIONS, () -> modeManager.isActiveOrTempMode (Modes.FUNCTIONS) ? SLMkIIIColorManager.SLMKIII_DARK_BROWN : SLMkIIIColorManager.SLMKIII_DARK_GREY);

        this.addButton (ButtonID.OCTAVE_UP, "Up", (event, value) -> {
            if (event == ButtonEvent.UP)
                surface.toggleMuteSolo ();
        }, 15, SLMkIIIControlSurface.MKIII_BUTTONS_UP, () -> surface.isMuteSolo () ? SLMkIIIColorManager.SLMKIII_ORANGE : SLMkIIIColorManager.SLMKIII_ORANGE_HALF);
        this.addButton (ButtonID.OCTAVE_DOWN, "Down", (event, value) -> {
            if (event == ButtonEvent.UP)
                surface.toggleMuteSolo ();
        }, 15, SLMkIIIControlSurface.MKIII_BUTTONS_DOWN, () -> !surface.isMuteSolo () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF);

        final CursorCommand<SLMkIIIControlSurface, SLMkIIIConfiguration> cursorLeftCommand = new CursorCommand<> (Direction.LEFT, this.model, surface);
        this.addButton (ButtonID.ARROW_LEFT, "Left", cursorLeftCommand, 15, SLMkIIIControlSurface.MKIII_TRACK_LEFT, () -> getCursorColor (modeManager, cursorLeftCommand));
        final CursorCommand<SLMkIIIControlSurface, SLMkIIIConfiguration> cursorRightCommand = new CursorCommand<> (Direction.RIGHT, this.model, surface);
        this.addButton (ButtonID.ARROW_RIGHT, "Right", cursorRightCommand, 15, SLMkIIIControlSurface.MKIII_TRACK_RIGHT, () -> getCursorColor (modeManager, cursorRightCommand));

        for (int i = 0; i < 2; i++)
        {
            final ButtonID sceneButtonID = ButtonID.get (ButtonID.SCENE1, i);
            this.addButton (sceneButtonID, "Scene " + (i + 1), new ViewButtonCommand<> (sceneButtonID, surface), 15, SLMkIIIControlSurface.MKIII_SCENE_1 + i, () -> {
                final View activeView = viewManager.getActiveView ();
                return activeView != null ? activeView.getButtonColor (sceneButtonID) : 0;
            });
        }

        this.addButton (ButtonID.SCENE7, "Scene Up", (event, value) -> {
            if (event != ButtonEvent.DOWN)
                return;
            if (viewManager.isActiveView (Views.SESSION))
                this.model.getSceneBank ().scrollBackwards ();
            else if (viewManager.isActiveView (Views.DRUM))
            {
                final DrumView drumView = (DrumView) viewManager.getView (Views.DRUM);
                if (drumView.isPlayMode ())
                    drumView.onOctaveUp (ButtonEvent.DOWN);
                else
                    drumView.onLeft (ButtonEvent.DOWN);
            }
            else if (viewManager.isActiveView (Views.COLOR))
                ((ColorView) viewManager.getView (Views.COLOR)).setFlip (false);
        }, 15, SLMkIIIControlSurface.MKIII_SCENE_UP, this::getSceneUpColor);

        this.addButton (ButtonID.SCENE8, "Scene Down", (event, value) -> {
            if (event != ButtonEvent.DOWN)
                return;
            if (viewManager.isActiveView (Views.SESSION))
                this.model.getSceneBank ().scrollForwards ();
            else if (viewManager.isActiveView (Views.DRUM))
            {
                final DrumView drumView = (DrumView) viewManager.getView (Views.DRUM);
                if (drumView.isPlayMode ())
                    drumView.onOctaveDown (ButtonEvent.DOWN);
                else
                    drumView.onRight (ButtonEvent.DOWN);
            }
            else if (viewManager.isActiveView (Views.COLOR))
                ((ColorView) viewManager.getView (Views.COLOR)).setFlip (true);
        }, 15, SLMkIIIControlSurface.MKIII_SCENE_DOWN, this::getSceneDownColor);

        this.addButton (ButtonID.SESSION, "Grid", (event, value) -> {
            if (event != ButtonEvent.DOWN)
                return;
            viewManager.setActiveView (viewManager.isActiveView (Views.SESSION) ? Views.DRUM : Views.SESSION);
            this.getSurface ().getDisplay ().notify (viewManager.isActiveView (Views.SESSION) ? "Session" : "Sequencer");
        }, 15, SLMkIIIControlSurface.MKIII_GRID, () -> viewManager.isActiveView (Views.SESSION) ? SLMkIIIColorManager.SLMKIII_GREEN : SLMkIIIColorManager.SLMKIII_BLUE);

        this.addButton (ButtonID.DUPLICATE, "Duplicate", NopCommand.INSTANCE, 15, SLMkIIIControlSurface.MKIII_DUPLICATE, () -> surface.isPressed (ButtonID.DUPLICATE) ? SLMkIIIColorManager.SLMKIII_AMBER : SLMkIIIColorManager.SLMKIII_AMBER_HALF);
        this.addButton (ButtonID.DELETE, "Clear", NopCommand.INSTANCE, 15, SLMkIIIControlSurface.MKIII_CLEAR, () -> surface.isPressed (ButtonID.DELETE) ? SLMkIIIColorManager.SLMKIII_AMBER : SLMkIIIColorManager.SLMKIII_AMBER_HALF);

        final SLMkIIIDisplay display = surface.getDisplay ();
        for (int i = 0; i < 8; i++)
        {
            final int index = i;
            surface.createLight (OutputID.get (OutputID.LED1, i), () -> {

                final ITrack track = tb.getItem (index);
                return track.getColor ().dim (this.valueChanger.toNormalizedValue (track.getVolume ()));

            }, color -> display.setFaderLEDColor (SLMkIIIControlSurface.MKIII_FADER_LED_1 + index, color));

            surface.createLight (OutputID.get (OutputID.LED_RING1, i), () -> {

                // Note: On mode change the color does not change if the value is the same,
                // let's ignore that since it is only visible in the simulation GUI
                final Mode mode = modeManager.getActiveOrTempMode ();
                if (mode == null)
                    return 0;
                final int value = mode.getKnobValue (index);
                return this.valueChanger.toMidiValue (value);

            }, color -> surface.setTrigger (SLMkIIIControlSurface.MKIII_KNOB_1 + index, color), state -> {

                // On the device, the send value is displayed on the display as a knob
                // On the simulation GUI represent it as a dimmed color of the mode
                final BaseMode mode = (BaseMode) modeManager.getActiveOrTempMode ();
                if (mode == null)
                    return ColorEx.BLACK;
                final ColorEx c = this.colorManager.getColor (mode.getModeColor (), null);
                return c.dim (this.valueChanger.toNormalizedValue (this.valueChanger.toDAWValue (state)));

            }, null);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final SLMkIIIControlSurface surface = this.getSurface ();
        for (int i = 0; i < 8; i++)
        {
            this.addRelativeKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1), new KnobRowModeCommand<> (i, this.model, surface), BindType.CC, 15, SLMkIIIControlSurface.MKIII_KNOB_1 + i);
            this.addFader (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), new VolumeFaderCommand (i, this.model, surface), BindType.CC, 15, SLMkIIIControlSurface.MKIII_FADER_1 + i);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final SLMkIIIControlSurface surface = this.getSurface ();

        surface.getContinuous (ContinuousID.MODULATION_WHEEL).setBounds (74.5, 204.0, 26.5, 143.75);
        surface.getContinuous (ContinuousID.PITCHBEND_WHEEL).setBounds (32.25, 204.0, 26.5, 143.75);

        surface.getPianoKeyboard ().setBounds (129.5, 201.0, 834.0, 157.0);

        surface.getTextDisplay ().getHardwareDisplay ().setBounds (266.25, 54.75, 280.5, 46.25);

        surface.getButton (ButtonID.PAD1).setBounds (267.5, 158.25, 24.25, 22.5);
        surface.getButton (ButtonID.PAD2).setBounds (297.0, 158.25, 24.25, 22.5);
        surface.getButton (ButtonID.PAD3).setBounds (326.5, 158.25, 24.25, 22.5);
        surface.getButton (ButtonID.PAD4).setBounds (356.0, 158.25, 24.25, 22.5);
        surface.getButton (ButtonID.PAD5).setBounds (385.5, 158.25, 24.25, 22.5);
        surface.getButton (ButtonID.PAD6).setBounds (415.0, 158.25, 24.25, 22.5);
        surface.getButton (ButtonID.PAD7).setBounds (444.5, 158.25, 24.25, 22.5);
        surface.getButton (ButtonID.PAD8).setBounds (474.0, 158.25, 24.25, 22.5);
        surface.getButton (ButtonID.PAD9).setBounds (267.75, 130.5, 24.25, 22.5);
        surface.getButton (ButtonID.PAD10).setBounds (297.25, 130.5, 24.25, 22.5);
        surface.getButton (ButtonID.PAD11).setBounds (326.75, 130.5, 24.25, 22.5);
        surface.getButton (ButtonID.PAD12).setBounds (356.25, 130.5, 24.25, 22.5);
        surface.getButton (ButtonID.PAD13).setBounds (385.75, 130.5, 24.25, 22.5);
        surface.getButton (ButtonID.PAD14).setBounds (415.0, 130.5, 24.25, 22.5);
        surface.getButton (ButtonID.PAD15).setBounds (444.5, 130.5, 24.25, 22.5);
        surface.getButton (ButtonID.PAD16).setBounds (474.0, 130.5, 24.25, 22.5);
        surface.getButton (ButtonID.REWIND).setBounds (815.5, 162.25, 21.0, 18.5);
        surface.getButton (ButtonID.FORWARD).setBounds (840.5, 162.25, 21.0, 18.5);
        surface.getButton (ButtonID.LOOP).setBounds (915.25, 162.25, 21.0, 18.5);
        surface.getButton (ButtonID.STOP).setBounds (865.25, 162.25, 21.0, 18.5);
        surface.getButton (ButtonID.PLAY).setBounds (890.25, 162.25, 21.0, 18.5);
        surface.getButton (ButtonID.RECORD).setBounds (940.25, 162.25, 21.0, 18.5);
        surface.getButton (ButtonID.ROW1_1).setBounds (268.5, 111.25, 23.75, 12.75);
        surface.getButton (ButtonID.ROW2_1).setBounds (568.0, 61.0, 22.0, 12.75);
        surface.getButton (ButtonID.ROW3_1).setBounds (568.0, 82.75, 22.0, 12.75);
        surface.getButton (ButtonID.ROW1_2).setBounds (297.75, 111.25, 23.75, 12.75);
        surface.getButton (ButtonID.ROW2_2).setBounds (598.0, 61.0, 22.0, 12.75);
        surface.getButton (ButtonID.ROW3_2).setBounds (598.0, 82.75, 22.0, 12.75);
        surface.getButton (ButtonID.ROW1_3).setBounds (327.0, 111.25, 23.75, 12.75);
        surface.getButton (ButtonID.ROW2_3).setBounds (628.0, 61.0, 22.0, 12.75);
        surface.getButton (ButtonID.ROW3_3).setBounds (628.0, 82.75, 22.0, 12.75);
        surface.getButton (ButtonID.ROW1_4).setBounds (356.25, 111.25, 23.75, 12.75);
        surface.getButton (ButtonID.ROW2_4).setBounds (658.0, 61.0, 22.0, 12.75);
        surface.getButton (ButtonID.ROW3_4).setBounds (658.0, 82.75, 22.0, 12.75);
        surface.getButton (ButtonID.ROW1_5).setBounds (385.5, 111.25, 23.75, 12.75);
        surface.getButton (ButtonID.ROW2_5).setBounds (688.0, 61.0, 22.0, 12.75);
        surface.getButton (ButtonID.ROW3_5).setBounds (688.0, 82.75, 22.0, 12.75);
        surface.getButton (ButtonID.ROW1_6).setBounds (414.75, 111.25, 23.75, 12.75);
        surface.getButton (ButtonID.ROW2_6).setBounds (718.0, 61.0, 22.0, 12.75);
        surface.getButton (ButtonID.ROW3_6).setBounds (718.0, 82.75, 22.0, 12.75);
        surface.getButton (ButtonID.ROW1_7).setBounds (444.25, 111.25, 23.75, 12.75);
        surface.getButton (ButtonID.ROW2_7).setBounds (748.0, 61.0, 22.0, 12.75);
        surface.getButton (ButtonID.ROW3_7).setBounds (748.0, 82.75, 22.0, 12.75);
        surface.getButton (ButtonID.ROW1_8).setBounds (473.5, 111.25, 23.75, 12.75);
        surface.getButton (ButtonID.ROW2_8).setBounds (778.0, 61.0, 22.0, 12.75);
        surface.getButton (ButtonID.ROW3_8).setBounds (778.0, 82.75, 22.0, 12.75);
        surface.getButton (ButtonID.ARROW_UP).setBounds (233.0, 62.25, 28.5, 12.75);
        surface.getButton (ButtonID.ARROW_DOWN).setBounds (233.0, 82.75, 28.5, 12.75);
        surface.getButton (ButtonID.SHIFT).setBounds (32.25, 63.0, 27.25, 13.75);
        surface.getButton (ButtonID.USER).setBounds (508.25, 111.25, 24.25, 12.75);
        surface.getButton (ButtonID.OCTAVE_UP).setBounds (811.25, 61.0, 27.25, 13.75);
        surface.getButton (ButtonID.OCTAVE_DOWN).setBounds (811.25, 82.75, 27.25, 13.75);
        surface.getButton (ButtonID.ARROW_LEFT).setBounds (32.25, 164.0, 27.25, 13.75);
        surface.getButton (ButtonID.ARROW_RIGHT).setBounds (74.5, 164.0, 27.25, 13.75);
        surface.getButton (ButtonID.SCENE1).setBounds (508.25, 130.5, 24.25, 22.5);
        surface.getButton (ButtonID.SCENE2).setBounds (508.25, 158.25, 24.25, 22.5);
        surface.getButton (ButtonID.SCENE7).setBounds (233.0, 129.5, 29.25, 22.5);
        surface.getButton (ButtonID.SCENE8).setBounds (233.0, 158.25, 29.25, 22.5);
        surface.getButton (ButtonID.SESSION).setBounds (233.0, 111.25, 29.5, 12.75);
        surface.getButton (ButtonID.DUPLICATE).setBounds (32.25, 126.75, 27.25, 13.75);
        surface.getButton (ButtonID.DELETE).setBounds (32.25, 144.5, 27.25, 13.75);

        surface.getContinuous (ContinuousID.KNOB1).setBounds (267.25, 10.25, 24.5, 23.75);
        surface.getContinuous (ContinuousID.FADER1).setBounds (568.0, 126.75, 21.25, 54.0);
        surface.getContinuous (ContinuousID.KNOB2).setBounds (296.75, 10.25, 24.5, 23.75);
        surface.getContinuous (ContinuousID.FADER2).setBounds (598.0, 126.75, 21.25, 54.0);
        surface.getContinuous (ContinuousID.KNOB3).setBounds (326.5, 10.25, 24.5, 23.75);
        surface.getContinuous (ContinuousID.FADER3).setBounds (628.0, 126.75, 21.25, 54.0);
        surface.getContinuous (ContinuousID.KNOB4).setBounds (356.0, 10.25, 24.5, 23.75);
        surface.getContinuous (ContinuousID.FADER4).setBounds (658.0, 126.75, 21.25, 54.0);
        surface.getContinuous (ContinuousID.KNOB5).setBounds (385.5, 10.25, 24.5, 23.75);
        surface.getContinuous (ContinuousID.FADER5).setBounds (688.0, 126.75, 21.25, 54.0);
        surface.getContinuous (ContinuousID.KNOB6).setBounds (415.25, 10.25, 24.5, 23.75);
        surface.getContinuous (ContinuousID.FADER6).setBounds (718.0, 126.75, 21.25, 54.0);
        surface.getContinuous (ContinuousID.KNOB7).setBounds (444.75, 10.25, 24.5, 23.75);
        surface.getContinuous (ContinuousID.FADER7).setBounds (748.0, 126.75, 21.25, 54.0);
        surface.getContinuous (ContinuousID.KNOB8).setBounds (474.25, 10.25, 24.5, 23.75);
        surface.getContinuous (ContinuousID.FADER8).setBounds (778.0, 126.75, 21.25, 54.0);

        surface.getLight (OutputID.LED1).setBounds (568.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED1).setBounds (568.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED2).setBounds (598.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED2).setBounds (598.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED3).setBounds (628.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED3).setBounds (628.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED4).setBounds (658.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED4).setBounds (658.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED5).setBounds (688.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED5).setBounds (688.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED6).setBounds (718.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED6).setBounds (718.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED7).setBounds (748.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED7).setBounds (748.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED8).setBounds (778.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED8).setBounds (778.0, 109.5, 21.25, 10.0);
        surface.getLight (OutputID.LED_RING1).setBounds (269.0, 35.75, 22.25, 9.25);
        surface.getLight (OutputID.LED_RING2).setBounds (298.5, 35.75, 22.25, 9.25);
        surface.getLight (OutputID.LED_RING3).setBounds (328.25, 35.75, 22.25, 9.25);
        surface.getLight (OutputID.LED_RING4).setBounds (357.75, 35.75, 22.25, 9.25);
        surface.getLight (OutputID.LED_RING5).setBounds (387.25, 35.75, 22.25, 9.25);
        surface.getLight (OutputID.LED_RING6).setBounds (416.75, 35.75, 22.25, 9.25);
        surface.getLight (OutputID.LED_RING7).setBounds (446.5, 35.75, 22.25, 9.25);
        surface.getLight (OutputID.LED_RING8).setBounds (476.0, 35.75, 22.25, 9.25);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final SLMkIIIControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActiveView (Views.SESSION);

        final ModeManager modeManager = surface.getModeManager ();
        modeManager.setActiveMode (Modes.TRACK);

        this.host.scheduleTask ( () -> surface.getMidiOutput ().sendSysex (DeviceInquiry.createQuery ()), 1000);
    }


    private int getSceneDownColor ()
    {
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        if (viewManager.isActiveView (Views.SESSION))
            return this.model.getSceneBank ().canScrollForwards () ? SLMkIIIColorManager.SLMKIII_GREEN : SLMkIIIColorManager.SLMKIII_BLACK;
        else if (viewManager.isActiveView (Views.DRUM))
            return ((DrumView) viewManager.getView (Views.DRUM)).isPlayMode () ? SLMkIIIColorManager.SLMKIII_BLUE : SLMkIIIColorManager.SLMKIII_SKY_BLUE;
        else if (viewManager.isActiveView (Views.COLOR))
            return !((ColorView) viewManager.getView (Views.COLOR)).isFlip () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_BLACK;
        return SLMkIIIColorManager.SLMKIII_BLACK;
    }


    private int getSceneUpColor ()
    {
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        if (viewManager.isActiveView (Views.SESSION))
            return this.model.getSceneBank ().canScrollBackwards () ? SLMkIIIColorManager.SLMKIII_GREEN : SLMkIIIColorManager.SLMKIII_BLACK;
        else if (viewManager.isActiveView (Views.DRUM))
            return ((DrumView) viewManager.getView (Views.DRUM)).isPlayMode () ? SLMkIIIColorManager.SLMKIII_BLUE : SLMkIIIColorManager.SLMKIII_SKY_BLUE;
        else if (viewManager.isActiveView (Views.COLOR))
            return ((ColorView) viewManager.getView (Views.COLOR)).isFlip () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_BLACK;
        return SLMkIIIColorManager.SLMKIII_BLACK;
    }


    private static int getDeviceModeColor (final ModeManager modeManager)
    {
        if (modeManager.isActiveMode (Modes.DEVICE_PARAMS))
        {
            if (((ParametersMode) modeManager.getMode (Modes.DEVICE_PARAMS)).isShowDevices ())
                return SLMkIIIColorManager.SLMKIII_MINT;
            return SLMkIIIColorManager.SLMKIII_PURPLE;
        }
        return SLMkIIIColorManager.SLMKIII_WHITE_HALF;
    }


    private static int getTrackModeColor (final ModeManager modeManager)
    {
        if (modeManager.isActiveMode (Modes.TRACK))
            return SLMkIIIColorManager.SLMKIII_GREEN;
        if (modeManager.isActiveMode (Modes.VOLUME))
            return SLMkIIIColorManager.SLMKIII_BLUE;
        if (modeManager.isActiveMode (Modes.PAN))
            return SLMkIIIColorManager.SLMKIII_ORANGE;
        if (Modes.isSendMode (modeManager.getActiveModeId ()))
            return SLMkIIIColorManager.SLMKIII_YELLOW;

        return SLMkIIIColorManager.SLMKIII_WHITE_HALF;
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Modes mode)
    {
        if (this.currentMode != null && this.currentMode.equals (mode))
            return;

        if (mode != null)
            this.currentMode = mode;

        final ITrackBank tb = this.model.getTrackBank ();
        final SLMkIIIControlSurface surface = this.getSurface ();
        final boolean isSession = surface.getViewManager ().isActiveView (Views.SESSION);
        final boolean isTrackMode = Modes.TRACK.equals (this.currentMode);
        final boolean isPan = Modes.PAN.equals (this.currentMode);
        final boolean isDevice = Modes.isDeviceMode (this.currentMode) || Modes.isLayerMode (this.currentMode);

        tb.setIndication (isSession);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final ITrack selectedTrack = tb.getSelectedItem ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        for (int i = 0; i < tb.getPageSize (); i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i && isTrackMode;
            final ITrack track = tb.getItem (i);
            // Alayes true since faders are always active
            track.setVolumeIndication (true);
            track.setPanIndication (isPan || hasTrackSel);

            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < sendBank.getPageSize (); j++)
                sendBank.getItem (j).setIndication (this.currentMode.ordinal () - Modes.SEND1.ordinal () == j || hasTrackSel);

            parameterBank.getItem (i).setIndication (isDevice);
        }
    }


    /**
     * Get the color index for the cursor keys.
     *
     * @param modeManager The mode manager
     * @param cursorCommand The cursor command
     * @return The color index
     */
    private static int getCursorColor (final ModeManager modeManager, final CursorCommand<SLMkIIIControlSurface, SLMkIIIConfiguration> cursorCommand)
    {
        if (!cursorCommand.canScroll ())
            return SLMkIIIColorManager.SLMKIII_BLACK;

        if (Modes.isTrackMode (modeManager.getActiveModeId ()))
            return SLMkIIIColorManager.SLMKIII_GREEN_HALF;

        if (modeManager.isActiveMode (Modes.DEVICE_PARAMS))
        {
            if (((ParametersMode) modeManager.getMode (Modes.DEVICE_PARAMS)).isShowDevices ())
                return SLMkIIIColorManager.SLMKIII_MINT_HALF;
            return SLMkIIIColorManager.SLMKIII_PURPLE_HALF;
        }

        return SLMkIIIColorManager.SLMKIII_BLACK;
    }
}
