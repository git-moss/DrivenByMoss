// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc;

import de.mossgrabers.controller.akai.apc.command.continuous.APCPlayPositionCommand;
import de.mossgrabers.controller.akai.apc.command.continuous.APCTempoCommand;
import de.mossgrabers.controller.akai.apc.command.trigger.APCBrowserCommand;
import de.mossgrabers.controller.akai.apc.command.trigger.APCCursorCommand;
import de.mossgrabers.controller.akai.apc.command.trigger.APCQuantizeCommand;
import de.mossgrabers.controller.akai.apc.command.trigger.APCRecordCommand;
import de.mossgrabers.controller.akai.apc.command.trigger.APCStopClipCommand;
import de.mossgrabers.controller.akai.apc.command.trigger.APCTapTempoCommand;
import de.mossgrabers.controller.akai.apc.command.trigger.SelectTrackSendOrClipLengthCommand;
import de.mossgrabers.controller.akai.apc.command.trigger.SendModeCommand;
import de.mossgrabers.controller.akai.apc.command.trigger.SessionRecordCommand;
import de.mossgrabers.controller.akai.apc.command.trigger.StopAllClipsOrBrowseCommand;
import de.mossgrabers.controller.akai.apc.controller.APCColorManager;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.controller.akai.apc.controller.APCScales;
import de.mossgrabers.controller.akai.apc.mode.BrowserMode;
import de.mossgrabers.controller.akai.apc.mode.NoteMode;
import de.mossgrabers.controller.akai.apc.mode.PanMode;
import de.mossgrabers.controller.akai.apc.mode.SendMode;
import de.mossgrabers.controller.akai.apc.mode.UserMode;
import de.mossgrabers.controller.akai.apc.view.DrumView;
import de.mossgrabers.controller.akai.apc.view.PlayView;
import de.mossgrabers.controller.akai.apc.view.RaindropsView;
import de.mossgrabers.controller.akai.apc.view.SequencerView;
import de.mossgrabers.controller.akai.apc.view.SessionView;
import de.mossgrabers.controller.akai.apc.view.ShiftView;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.FootswitchCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand.Panels;
import de.mossgrabers.framework.command.trigger.application.PanelLayoutCommand;
import de.mossgrabers.framework.command.trigger.application.RedoCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.device.DeviceLayerLeftCommand;
import de.mossgrabers.framework.command.trigger.device.DeviceLayerRightCommand;
import de.mossgrabers.framework.command.trigger.device.DeviceOnOffCommand;
import de.mossgrabers.framework.command.trigger.device.SelectNextDeviceOrParamPageCommand;
import de.mossgrabers.framework.command.trigger.device.SelectPreviousDeviceOrParamPageCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.track.CrossfadeModeCommand;
import de.mossgrabers.framework.command.trigger.track.MasterCommand;
import de.mossgrabers.framework.command.trigger.track.MuteCommand;
import de.mossgrabers.framework.command.trigger.track.RecArmCommand;
import de.mossgrabers.framework.command.trigger.track.SoloCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.view.FeatureGroupButtonColorSupplier;
import de.mossgrabers.framework.command.trigger.view.ToggleShiftViewCommand;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.Timeout;
import de.mossgrabers.framework.view.TempoView;
import de.mossgrabers.framework.view.Views;

import java.util.Optional;


/**
 * Support for the Akai APC40 mkI and APC40 mkII controllers.
 *
 * @author Jürgen Moßgraber
 */
public class APCControllerSetup extends AbstractControllerSetup<APCControlSurface, APCConfiguration>
{
    private final boolean isMkII;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     * @param isMkII True if is MkII
     */
    public APCControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings, final boolean isMkII)
    {
        super (factory, host, globalSettings, documentSettings);

        this.isMkII = isMkII;
        this.colorManager = new APCColorManager (isMkII);
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new APCConfiguration (host, this.valueChanger, factory.getArpeggiatorModes (), isMkII);
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new APCScales (this.valueChanger);
        this.scales.setDrumDefaultOffset (12);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setNumScenes (5);
        ms.setNumDrumPadLayers (12);
        ms.setNumMarkers (8);
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Pads", "80????" /* Note off */,
                "90????" /* Note on */);
        final APCControlSurface surface = new APCControlSurface (this.host, this.colorManager, this.configuration, output, input, this.isMkII);
        this.surfaces.add (surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        final APCControlSurface surface = this.getSurface ();

        this.createScaleObservers (this.configuration);
        this.createNoteRepeatObservers (this.configuration, surface);

        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.activateBrowserObserver (Modes.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final APCControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.register (Modes.PAN, new PanMode (surface, this.model));
        for (int i = 0; i < 8; i++)
            modeManager.register (Modes.get (Modes.SEND1, i), new SendMode (surface, this.model, i));
        modeManager.register (Modes.NOTE, new NoteMode (surface, this.model));
        modeManager.register (Modes.USER, new UserMode (surface, this.model));
        modeManager.register (Modes.BROWSER, new BrowserMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final APCControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.register (Views.PLAY, new PlayView (surface, this.model));
        viewManager.register (Views.SESSION, new SessionView (surface, this.model));
        viewManager.register (Views.SEQUENCER, new SequencerView (surface, this.model));
        viewManager.register (Views.DRUM, new DrumView (surface, this.model));
        viewManager.register (Views.RAINDROPS, new RaindropsView (surface, this.model));
        viewManager.register (Views.SHIFT, new ShiftView (surface, this.model));
        viewManager.register (Views.TEMPO, new TempoView<> (surface, this.model, this.isMkII ? APCColorManager.APC_MKII_COLOR_BLUE : APCColorManager.APC_COLOR_GREEN, this.isMkII ? APCColorManager.APC_MKII_COLOR_WHITE : APCColorManager.APC_COLOR_YELLOW, APCColorManager.APC_COLOR_BLACK));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final APCControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final ModeManager modeManager = surface.getModeManager ();
        final ITransport t = this.model.getTransport ();

        this.addButton (ButtonID.SHIFT, "SHIFT", new ToggleShiftViewCommand<> (this.model, surface), APCControlSurface.APC_BUTTON_SHIFT);
        this.addButton (ButtonID.PLAY, "PLAY", new PlayCommand<> (this.model, surface), APCControlSurface.APC_BUTTON_PLAY, t::isPlaying, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        this.addButton (ButtonID.RECORD, "RECORD", new APCRecordCommand (this.model, surface), APCControlSurface.APC_BUTTON_RECORD, t::isRecording, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        this.addButton (ButtonID.TAP_TEMPO, "Tempo", new APCTapTempoCommand (this.model, surface), APCControlSurface.APC_BUTTON_TAP_TEMPO);
        this.addButton (ButtonID.QUANTIZE, this.isMkII ? "DEV.LOCK" : "REC QUANTIZATION", new APCQuantizeCommand (this.model, surface), APCControlSurface.APC_BUTTON_REC_QUANT, () -> surface.isPressed (ButtonID.QUANTIZE) ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        this.addButton (ButtonID.MASTERTRACK, "Master", new MasterCommand<> (this.model, surface), APCControlSurface.APC_BUTTON_MASTER, this.model.getMasterTrack ()::isSelected, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        // Note: the stop-all-clips button has no LED
        this.addButton (ButtonID.STOP_ALL_CLIPS, "STOP CLIPS", new StopAllClipsOrBrowseCommand (this.model, surface), APCControlSurface.APC_BUTTON_STOP_ALL_CLIPS, () -> surface.isPressed (ButtonID.STOP_ALL_CLIPS) ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        this.addButton (ButtonID.PAN_SEND, "PAN", new ModeSelectCommand<> (this.model, surface, Modes.PAN), APCControlSurface.APC_BUTTON_PAN, () -> modeManager.isActive (Modes.PAN), ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);

        if (this.isMkII)
        {
            this.addButton (ButtonID.SEND1, "SENDS", new ModeMultiSelectCommand<> (this.model, surface, Modes.SEND1, Modes.SEND2, Modes.SEND3, Modes.SEND4, Modes.SEND5, Modes.SEND6, Modes.SEND7, Modes.SEND8), APCControlSurface.APC_BUTTON_SEND_A, () -> Modes.isSendMode (modeManager.getActiveID ()), ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
            this.addButton (ButtonID.SEND2, "USER", new ModeSelectCommand<> (this.model, surface, Modes.USER)
            {
                @Override
                protected void displayMode ()
                {
                    ((UserMode) this.modeManager.get (Modes.USER)).displayPageName ();
                }
            }, APCControlSurface.APC_BUTTON_SEND_B, () -> modeManager.isActive (Modes.USER), ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        }
        else
        {
            this.addButton (ButtonID.SEND1, "Send A", new SendModeCommand (0, this.model, surface), APCControlSurface.APC_BUTTON_SEND_A, () -> modeManager.isActive (Modes.SEND1, Modes.SEND4, Modes.SEND7), ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
            this.addButton (ButtonID.SEND2, "Send B", new SendModeCommand (1, this.model, surface), APCControlSurface.APC_BUTTON_SEND_B, () -> modeManager.isActive (Modes.SEND2, Modes.SEND5, Modes.SEND8), ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
            this.addButton (ButtonID.SEND3, "SEND C", new SendModeCommand (2, this.model, surface), APCControlSurface.APC_BUTTON_SEND_C, () -> modeManager.isActive (Modes.SEND3, Modes.SEND6), ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        }

        for (int i = 0; i < 8; i++)
        {
            final int index = i;

            this.addButton (ButtonID.get (ButtonID.ROW1_1, i), "Select " + (i + 1), new SelectTrackSendOrClipLengthCommand (i, this.model, surface), i, APCControlSurface.APC_BUTTON_TRACK_SELECTION, () -> this.getButtonState (index, APCControlSurface.APC_BUTTON_TRACK_SELECTION) ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
            this.addButton (ButtonID.get (ButtonID.ROW2_1, i), "Solo " + (i + 1), new SoloCommand<> (i, this.model, surface), i, APCControlSurface.APC_BUTTON_SOLO, () -> this.getButtonState (index, APCControlSurface.APC_BUTTON_SOLO) ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
            this.addButton (ButtonID.get (ButtonID.ROW3_1, i), (this.isMkII ? "Mute " : "Activator ") + (i + 1), new MuteCommand<> (i, this.model, surface), i, APCControlSurface.APC_BUTTON_ACTIVATOR, () -> this.getButtonState (index, APCControlSurface.APC_BUTTON_ACTIVATOR) ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
            this.addButton (ButtonID.get (ButtonID.ROW4_1, i), "Arm " + (i + 1), new RecArmCommand<> (i, this.model, surface), i, APCControlSurface.APC_BUTTON_RECORD_ARM, () -> this.getButtonState (index, APCControlSurface.APC_BUTTON_RECORD_ARM) ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);

            if (this.isMkII)
                this.addButton (ButtonID.get (ButtonID.ROW5_1, i), "X-fade " + (i + 1), new CrossfadeModeCommand<> (i, this.model, surface), i, APCControlSurface.APC_BUTTON_A_B, () -> this.getCrossfadeButtonColor (index), ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON, APCColorManager.BUTTON_STATE_BLINK);

            final ButtonID stopButtonID = ButtonID.get (ButtonID.ROW6_1, i);
            final APCStopClipCommand apcStopClipCommand = new APCStopClipCommand (i, this.model, surface);
            this.addButton (stopButtonID, "Stop " + (i + 1), apcStopClipCommand, i, APCControlSurface.APC_BUTTON_CLIP_STOP, () -> apcStopClipCommand.getButtonColor (stopButtonID), ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        }

        if (this.isMkII)
        {
            final DeviceLayerLeftCommand<APCControlSurface, APCConfiguration> deviceLayerLeftCommand = new DeviceLayerLeftCommand<> (this.model, surface);
            this.addButton (ButtonID.DEVICE_LEFT, "<- DEVICE", deviceLayerLeftCommand, APCControlSurface.APC_BUTTON_CLIP_TRACK, () -> deviceLayerLeftCommand.canExecute () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
            final DeviceLayerRightCommand<APCControlSurface, APCConfiguration> deviceLayerRightCommand = new DeviceLayerRightCommand<> (this.model, surface);
            this.addButton (ButtonID.DEVICE_RIGHT, "DEVICE ->", deviceLayerRightCommand, APCControlSurface.APC_BUTTON_DEVICE_ON_OFF, () -> deviceLayerRightCommand.canExecute () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
            this.addButton (ButtonID.BROWSE, "BANK", new APCBrowserCommand (this.model, surface), APCControlSurface.APC_BUTTON_BANK, this.model.getBrowser ()::isActive, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        }
        else
        {
            this.addButton (ButtonID.STOP, "STOP", new StopCommand<> (this.model, surface), APCControlSurface.APC_BUTTON_STOP, () -> !t.isPlaying (), ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        }

        this.addButton (ButtonID.CLIP, this.isMkII ? "SESSION" : "MIDI OVERDUB", new SessionRecordCommand (this.model, surface), this.isMkII ? APCControlSurface.APC_BUTTON_SESSION : APCControlSurface.APC_BUTTON_MIDI_OVERDUB, t::isLauncherOverdub, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        this.addButton (ButtonID.METRONOME, "METRONOME", new MetronomeCommand<> (this.model, surface, false), this.isMkII ? APCControlSurface.APC_BUTTON_SEND_C : APCControlSurface.APC_BUTTON_METRONOME, t::isMetronomeOn, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        this.addButton (ButtonID.NUDGE_PLUS, "NUDGE+", new RedoCommand<> (this.model, surface), this.isMkII ? APCControlSurface.APC_BUTTON_NUDGE_MINUS : APCControlSurface.APC_BUTTON_NUDGE_PLUS);
        this.addButton (ButtonID.NUDGE_MINUS, "NUDGE-", new UndoCommand<> (this.model, surface), this.isMkII ? APCControlSurface.APC_BUTTON_NUDGE_PLUS : APCControlSurface.APC_BUTTON_NUDGE_MINUS);
        this.addButton (ButtonID.DEVICE_ON_OFF, "DEV. ON/OFF", new DeviceOnOffCommand<> (this.model, surface), this.isMkII ? APCControlSurface.APC_BUTTON_DETAIL_VIEW : APCControlSurface.APC_BUTTON_DEVICE_ON_OFF, this.model.getCursorDevice ()::isEnabled, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        this.addButton (ButtonID.TOGGLE_DEVICES_PANE, this.isMkII ? "CLIP/DEV.VIEW" : "CLIP/TRACK", new PaneCommand<> (Panels.DEVICE, this.model, surface), this.isMkII ? APCControlSurface.APC_BUTTON_MIDI_OVERDUB : APCControlSurface.APC_BUTTON_CLIP_TRACK, () -> surface.isPressed (ButtonID.TOGGLE_DEVICES_PANE) ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        this.addButton (ButtonID.LAYOUT, "DETAIL VIEW", new PanelLayoutCommand<> (this.model, surface), this.isMkII ? APCControlSurface.APC_BUTTON_METRONOME : APCControlSurface.APC_BUTTON_DETAIL_VIEW, () -> !surface.isShiftPressed () && this.model.getCursorDevice ().isWindowOpen () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);

        final SelectPreviousDeviceOrParamPageCommand<APCControlSurface, APCConfiguration> selectPreviousDeviceOrParamPageCommand = new SelectPreviousDeviceOrParamPageCommand<> (this.model, surface);
        this.addButton (ButtonID.BANK_LEFT, "<- BANK", selectPreviousDeviceOrParamPageCommand, APCControlSurface.APC_BUTTON_DEVICE_LEFT, () -> selectPreviousDeviceOrParamPageCommand.canExecute () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        final SelectNextDeviceOrParamPageCommand<APCControlSurface, APCConfiguration> selectNextDeviceOrParamPageCommand = new SelectNextDeviceOrParamPageCommand<> (this.model, surface);
        this.addButton (ButtonID.BANK_RIGHT, "BANK ->", selectNextDeviceOrParamPageCommand, APCControlSurface.APC_BUTTON_DEVICE_RIGHT, () -> selectNextDeviceOrParamPageCommand.canExecute () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);

        this.addButton (ButtonID.ARROW_DOWN, "Arrow Down", new APCCursorCommand (Direction.DOWN, this.model, surface), APCControlSurface.APC_BUTTON_DOWN);
        this.addButton (ButtonID.ARROW_UP, "Arrow Up", new APCCursorCommand (Direction.UP, this.model, surface), APCControlSurface.APC_BUTTON_UP);
        this.addButton (ButtonID.ARROW_LEFT, "Arrow Left", new APCCursorCommand (Direction.LEFT, this.model, surface), APCControlSurface.APC_BUTTON_LEFT);
        this.addButton (ButtonID.ARROW_RIGHT, "Arrow Right", new APCCursorCommand (Direction.RIGHT, this.model, surface), APCControlSurface.APC_BUTTON_RIGHT);

        for (int i = 0; i < 5; i++)
        {
            final ButtonID sceneButtonID = ButtonID.get (ButtonID.SCENE1, i);
            this.addButton (sceneButtonID, "Scene " + (i + 1), new ViewButtonCommand<> (sceneButtonID, surface), APCControlSurface.APC_BUTTON_SCENE_LAUNCH_1 + i, new FeatureGroupButtonColorSupplier (viewManager, sceneButtonID));
        }

        this.addButton (ButtonID.FOOTSWITCH1, "Foot Controller 1", new FootswitchCommand<> (this.model, surface, 0), APCControlSurface.APC_FOOTSWITCH_1);
        if (!this.isMkII)
            this.addButton (ButtonID.FOOTSWITCH2, "Foot Controller 2", new FootswitchCommand<> (this.model, surface, 1), APCControlSurface.APC_FOOTSWITCH_2);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final APCControlSurface surface = this.getSurface ();

        this.addFader (ContinuousID.FADER_MASTER, "Master", null, BindType.CC, APCControlSurface.APC_KNOB_MASTER_LEVEL).bind (this.model.getMasterTrack ().getVolumeParameter ());
        this.addFader (ContinuousID.CROSSFADER, "Crossfader", null, BindType.CC, APCControlSurface.APC_KNOB_CROSSFADER, false).bind (this.model.getTransport ().getCrossfadeParameter ());

        final Timeout timeout = ((APCTapTempoCommand) surface.getButton (ButtonID.TAP_TEMPO).getCommand ()).getTimeout ();
        this.addRelativeKnob (ContinuousID.PLAY_POSITION, "Play Position", new APCPlayPositionCommand (this.model, surface, timeout), APCControlSurface.APC_KNOB_CUE_LEVEL);

        for (int i = 0; i < 8; i++)
        {
            this.addFader (ContinuousID.get (ContinuousID.FADER1, i), "Fader" + (i + 1), null, BindType.CC, i, APCControlSurface.APC_KNOB_TRACK_LEVEL).setIndexInGroup (i);

            final IHwAbsoluteKnob channelKnob = this.addAbsoluteKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1), new KnobRowModeCommand<> (i, this.model, surface), APCControlSurface.APC_KNOB_TRACK_KNOB_1 + i);
            final IHwAbsoluteKnob deviceKnob = this.addAbsoluteKnob (ContinuousID.get (ContinuousID.DEVICE_KNOB1, i), "Device Knob " + (i + 1), null, APCControlSurface.APC_KNOB_DEVICE_KNOB_1 + i);

            channelKnob.setIndexInGroup (i);
            deviceKnob.setIndexInGroup (i);

            // Disable take over mode for the knobs since due to their LED updates they act like
            // motor faders
            channelKnob.disableTakeOver ();
            deviceKnob.disableTakeOver ();
        }

        new TrackVolumeMode<> (surface, this.model, true, ContinuousID.createSequentialList (ContinuousID.FADER1, 8)).onActivate ();
        new ParameterMode<> (surface, this.model, true, ContinuousID.createSequentialList (ContinuousID.DEVICE_KNOB1, 8)).onActivate ();

        if (this.isMkII)
            this.addRelativeKnob (ContinuousID.TEMPO, "Tempo", new APCTempoCommand (this.model, surface, timeout), APCControlSurface.APC_KNOB_TEMPO);
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final APCControlSurface surface = this.getSurface ();

        if (this.isMkII)
        {
            surface.getButton (ButtonID.NUDGE_MINUS).setBounds (624.0, 132.0, 33.25, 15.25);
            surface.getButton (ButtonID.NUDGE_PLUS).setBounds (686.0, 132.0, 33.25, 15.25);

            surface.getButton (ButtonID.METRONOME).setBounds (624.0, 96.0, 33.25, 15.25);
            surface.getButton (ButtonID.CLIP).setBounds (747.75, 59.25, 32.5, 20.0);
            surface.getButton (ButtonID.LAYOUT).setBounds (747.75, 319.75, 33.25, 15.25);
            surface.getButton (ButtonID.TOGGLE_DEVICES_PANE).setBounds (686.0, 319.75, 33.25, 15.25);

            surface.getButton (ButtonID.DEVICE_ON_OFF).setBounds (562.25, 319.75, 33.25, 15.25);

            surface.getButton (ButtonID.PAD1).setBounds (13.0, 194.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD2).setBounds (74.25, 194.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD3).setBounds (135.25, 194.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD4).setBounds (196.5, 194.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD5).setBounds (257.75, 194.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD6).setBounds (318.75, 194.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD7).setBounds (380.0, 194.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD8).setBounds (441.0, 194.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD9).setBounds (12.0, 165.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD10).setBounds (73.25, 165.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD11).setBounds (134.5, 165.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD12).setBounds (196.0, 165.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD13).setBounds (257.25, 165.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD14).setBounds (318.5, 165.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD15).setBounds (379.75, 165.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD16).setBounds (441.0, 165.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD17).setBounds (11.75, 135.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD18).setBounds (73.25, 135.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD19).setBounds (135.0, 135.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD20).setBounds (196.5, 135.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD21).setBounds (258.0, 135.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD22).setBounds (319.5, 135.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD23).setBounds (381.25, 135.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD24).setBounds (441.0, 137.5, 52.25, 18.5);
            surface.getButton (ButtonID.PAD25).setBounds (12.5, 108.75, 52.25, 18.5);
            surface.getButton (ButtonID.PAD26).setBounds (73.75, 108.75, 52.25, 18.5);
            surface.getButton (ButtonID.PAD27).setBounds (135.25, 108.75, 52.25, 18.5);
            surface.getButton (ButtonID.PAD28).setBounds (196.5, 108.75, 52.25, 18.5);
            surface.getButton (ButtonID.PAD29).setBounds (257.75, 108.75, 52.25, 18.5);
            surface.getButton (ButtonID.PAD30).setBounds (319.25, 108.75, 52.25, 18.5);
            surface.getButton (ButtonID.PAD31).setBounds (380.5, 108.75, 52.25, 18.5);
            surface.getButton (ButtonID.PAD32).setBounds (441.0, 108.75, 52.25, 18.5);
            surface.getButton (ButtonID.PAD33).setBounds (12.75, 78.0, 52.25, 18.5);
            surface.getButton (ButtonID.PAD34).setBounds (74.25, 78.0, 52.25, 18.5);
            surface.getButton (ButtonID.PAD35).setBounds (135.5, 78.0, 52.25, 18.5);
            surface.getButton (ButtonID.PAD36).setBounds (196.75, 78.0, 52.25, 18.5);
            surface.getButton (ButtonID.PAD37).setBounds (256.5, 78.0, 52.25, 18.5);
            surface.getButton (ButtonID.PAD38).setBounds (318.0, 78.0, 52.25, 18.5);
            surface.getButton (ButtonID.PAD39).setBounds (379.5, 78.0, 52.25, 18.5);
            surface.getButton (ButtonID.PAD40).setBounds (441.0, 78.0, 52.25, 18.5);

            surface.getButton (ButtonID.SHIFT).setBounds (686.0, 360.5, 33.25, 15.25);
            surface.getButton (ButtonID.PLAY).setBounds (624.0, 59.25, 32.5, 20.0);
            surface.getButton (ButtonID.RECORD).setBounds (686.0, 59.25, 32.5, 20.0);
            surface.getButton (ButtonID.TAP_TEMPO).setBounds (686.0, 96.0, 33.25, 15.25);

            surface.getButton (ButtonID.QUANTIZE).setBounds (624.0, 319.75, 33.25, 15.25);
            surface.getButton (ButtonID.PAN_SEND).setBounds (562.25, 64.0, 33.25, 15.25);
            surface.getButton (ButtonID.SEND1).setBounds (562.25, 96.0, 33.25, 15.25);
            surface.getButton (ButtonID.SEND2).setBounds (562.25, 132.0, 33.25, 15.25);

            surface.getButton (ButtonID.ARROW_UP).setBounds (581.25, 361.75, 33.25, 25.0);
            surface.getButton (ButtonID.ARROW_DOWN).setBounds (581.5, 386.25, 33.25, 25.0);
            surface.getButton (ButtonID.ARROW_LEFT).setBounds (562.5, 361.75, 17.0, 49.25);
            surface.getButton (ButtonID.ARROW_RIGHT).setBounds (616.0, 361.75, 17.0, 49.25);

            surface.getButton (ButtonID.SCENE1).setBounds (500.75, 78.0, 36.0, 18.5);
            surface.getButton (ButtonID.SCENE2).setBounds (500.75, 108.75, 36.0, 18.5);
            surface.getButton (ButtonID.SCENE3).setBounds (500.75, 137.5, 36.0, 18.5);
            surface.getButton (ButtonID.SCENE4).setBounds (500.75, 165.5, 36.0, 18.5);
            surface.getButton (ButtonID.SCENE5).setBounds (500.75, 194.5, 36.0, 18.5);

            surface.getButton (ButtonID.FOOTSWITCH1).setBounds (670, 1.25, 37.5, 21.0);

            surface.getButton (ButtonID.STOP_ALL_CLIPS).setBounds (502.25, 227.25, 33.25, 18.75);
            surface.getButton (ButtonID.MASTERTRACK).setBounds (500.75, 263.0, 36.0, 15.75);

            surface.getButton (ButtonID.ROW1_1).setBounds (12.5, 262.5, 50.5, 15.75);
            surface.getButton (ButtonID.ROW1_2).setBounds (74.0, 262.5, 50.5, 15.75);
            surface.getButton (ButtonID.ROW1_3).setBounds (135.25, 262.5, 50.5, 15.75);
            surface.getButton (ButtonID.ROW1_4).setBounds (196.75, 262.5, 50.5, 15.75);
            surface.getButton (ButtonID.ROW1_5).setBounds (258.25, 262.5, 50.5, 15.75);
            surface.getButton (ButtonID.ROW1_6).setBounds (319.5, 262.5, 50.5, 15.75);
            surface.getButton (ButtonID.ROW1_7).setBounds (381.0, 262.5, 50.5, 15.75);
            surface.getButton (ButtonID.ROW1_8).setBounds (442.5, 262.5, 50.5, 15.75);
            surface.getButton (ButtonID.ROW2_1).setBounds (14.0, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW2_2).setBounds (75.25, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW2_3).setBounds (136.5, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW2_4).setBounds (197.5, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW2_5).setBounds (258.75, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW2_6).setBounds (320.0, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW2_7).setBounds (381.25, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW2_8).setBounds (442.5, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW3_1).setBounds (14.25, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW3_2).setBounds (75.5, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW3_3).setBounds (136.5, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW3_4).setBounds (197.75, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW3_5).setBounds (259.0, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW3_6).setBounds (320.0, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW3_7).setBounds (381.25, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW3_8).setBounds (442.5, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW4_1).setBounds (44.5, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW4_2).setBounds (105.75, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW4_3).setBounds (167.0, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW4_4).setBounds (228.25, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW4_5).setBounds (289.5, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW4_6).setBounds (350.75, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW4_7).setBounds (411.75, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW4_8).setBounds (473.0, 317.75, 19.5, 18.0);
            surface.getButton (ButtonID.ROW5_1).setBounds (44.75, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW5_2).setBounds (106.0, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW5_3).setBounds (167.25, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW5_4).setBounds (228.25, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW5_5).setBounds (289.5, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW5_6).setBounds (350.75, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW5_7).setBounds (411.75, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW5_8).setBounds (473.0, 291.5, 19.5, 18.0);
            surface.getButton (ButtonID.ROW6_1).setBounds (21.25, 227.25, 31.5, 18.75);
            surface.getButton (ButtonID.ROW6_2).setBounds (82.75, 227.25, 31.5, 18.75);
            surface.getButton (ButtonID.ROW6_3).setBounds (144.25, 227.25, 31.5, 18.75);
            surface.getButton (ButtonID.ROW6_4).setBounds (205.75, 227.25, 31.5, 18.75);
            surface.getButton (ButtonID.ROW6_5).setBounds (267.0, 227.25, 31.5, 18.75);
            surface.getButton (ButtonID.ROW6_6).setBounds (328.5, 227.25, 31.5, 18.75);
            surface.getButton (ButtonID.ROW6_7).setBounds (390.0, 227.25, 31.5, 18.75);
            surface.getButton (ButtonID.ROW6_8).setBounds (451.5, 227.25, 31.5, 18.75);

            surface.getButton (ButtonID.BANK_LEFT).setBounds (686.0, 289.75, 33.25, 15.25);
            surface.getButton (ButtonID.BANK_RIGHT).setBounds (747.75, 289.75, 33.25, 15.25);

            surface.getButton (ButtonID.DEVICE_LEFT).setBounds (562.25, 289.75, 33.25, 15.25);
            surface.getButton (ButtonID.DEVICE_RIGHT).setBounds (624.0, 289.75, 33.25, 15.25);
            surface.getButton (ButtonID.BROWSE).setBounds (747.75, 359.75, 33.25, 15.25);

            surface.getContinuous (ContinuousID.FADER_MASTER).setBounds (500.25, 348.5, 40.5, 115.0);
            surface.getContinuous (ContinuousID.PLAY_POSITION).setBounds (497.75, 293.75, 40.25, 37.75);
            surface.getContinuous (ContinuousID.CROSSFADER).setBounds (651.25, 419.5, 104.0, 50.0);
            surface.getContinuous (ContinuousID.FADER1).setBounds (19.75, 348.5, 40.5, 115.0);
            surface.getContinuous (ContinuousID.KNOB1).setBounds (16.75, 19.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB1).setBounds (560.25, 173.75, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER2).setBounds (80.75, 348.5, 40.5, 115.0);
            surface.getContinuous (ContinuousID.KNOB2).setBounds (78.5, 19.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB2).setBounds (620.75, 173.75, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER3).setBounds (141.5, 348.5, 40.5, 115.0);
            surface.getContinuous (ContinuousID.KNOB3).setBounds (140.25, 19.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB3).setBounds (682.5, 173.75, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER4).setBounds (202.5, 348.5, 40.5, 115.0);
            surface.getContinuous (ContinuousID.KNOB4).setBounds (202.0, 19.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB4).setBounds (744.25, 173.75, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER5).setBounds (263.5, 348.5, 40.5, 115.0);
            surface.getContinuous (ContinuousID.KNOB5).setBounds (263.5, 19.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB5).setBounds (560.25, 235.75, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER6).setBounds (324.25, 348.5, 40.5, 115.0);
            surface.getContinuous (ContinuousID.KNOB6).setBounds (325.25, 19.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB6).setBounds (620.75, 235.75, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER7).setBounds (385.25, 348.5, 40.5, 115.0);
            surface.getContinuous (ContinuousID.KNOB7).setBounds (387.0, 19.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB7).setBounds (682.5, 235.75, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER8).setBounds (446.25, 348.5, 40.5, 115.0);
            surface.getContinuous (ContinuousID.KNOB8).setBounds (448.75, 19.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB8).setBounds (744.25, 235.75, 40.25, 37.75);

            surface.getContinuous (ContinuousID.TEMPO).setBounds (743.25, 106.75, 40.25, 37.75);
        }
        else
        {
            surface.getButton (ButtonID.PAD1).setBounds (33.5, 228.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD2).setBounds (83.75, 228.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD3).setBounds (132.75, 228.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD4).setBounds (180.25, 228.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD5).setBounds (229.0, 228.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD6).setBounds (278.25, 228.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD7).setBounds (327.25, 228.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD8).setBounds (376.25, 228.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD9).setBounds (33.5, 184.75, 33.0, 32.0);
            surface.getButton (ButtonID.PAD10).setBounds (83.75, 184.75, 33.0, 32.0);
            surface.getButton (ButtonID.PAD11).setBounds (132.75, 184.75, 33.0, 32.0);
            surface.getButton (ButtonID.PAD12).setBounds (180.25, 184.75, 33.0, 32.0);
            surface.getButton (ButtonID.PAD13).setBounds (229.0, 184.75, 33.0, 32.0);
            surface.getButton (ButtonID.PAD14).setBounds (278.25, 184.75, 33.0, 32.0);
            surface.getButton (ButtonID.PAD15).setBounds (327.25, 184.75, 33.0, 32.0);
            surface.getButton (ButtonID.PAD16).setBounds (376.25, 184.75, 33.0, 32.0);
            surface.getButton (ButtonID.PAD17).setBounds (33.5, 138.5, 33.0, 32.0);
            surface.getButton (ButtonID.PAD18).setBounds (83.75, 138.5, 33.0, 32.0);
            surface.getButton (ButtonID.PAD19).setBounds (132.75, 138.5, 33.0, 32.0);
            surface.getButton (ButtonID.PAD20).setBounds (180.25, 138.5, 33.0, 32.0);
            surface.getButton (ButtonID.PAD21).setBounds (229.0, 138.5, 33.0, 32.0);
            surface.getButton (ButtonID.PAD22).setBounds (278.25, 138.5, 33.0, 32.0);
            surface.getButton (ButtonID.PAD23).setBounds (327.25, 138.5, 33.0, 32.0);
            surface.getButton (ButtonID.PAD24).setBounds (376.25, 138.5, 33.0, 32.0);
            surface.getButton (ButtonID.PAD25).setBounds (33.5, 94.25, 33.0, 32.0);
            surface.getButton (ButtonID.PAD26).setBounds (83.75, 94.25, 33.0, 32.0);
            surface.getButton (ButtonID.PAD27).setBounds (132.75, 94.25, 33.0, 32.0);
            surface.getButton (ButtonID.PAD28).setBounds (180.25, 94.25, 33.0, 32.0);
            surface.getButton (ButtonID.PAD29).setBounds (229.0, 94.25, 33.0, 32.0);
            surface.getButton (ButtonID.PAD30).setBounds (278.25, 94.25, 33.0, 32.0);
            surface.getButton (ButtonID.PAD31).setBounds (327.25, 94.25, 33.0, 32.0);
            surface.getButton (ButtonID.PAD32).setBounds (376.25, 94.25, 33.0, 32.0);
            surface.getButton (ButtonID.PAD33).setBounds (33.5, 51.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD34).setBounds (83.75, 51.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD35).setBounds (132.75, 51.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD36).setBounds (180.25, 51.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD37).setBounds (229.0, 51.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD38).setBounds (278.25, 51.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD39).setBounds (327.25, 51.0, 33.0, 32.0);
            surface.getButton (ButtonID.PAD40).setBounds (376.25, 51.0, 33.0, 32.0);

            surface.getButton (ButtonID.ROW1_1).setBounds (33.5, 338.5, 33.0, 32.0);
            surface.getButton (ButtonID.ROW2_1).setBounds (33.5, 421.5, 34.0, 18.0);
            surface.getButton (ButtonID.ROW3_1).setBounds (33.5, 393.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW4_1).setBounds (33.5, 449.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW6_1).setBounds (33.5, 279.75, 33.0, 32.0);
            surface.getButton (ButtonID.ROW1_2).setBounds (83.75, 338.5, 33.0, 32.0);
            surface.getButton (ButtonID.ROW2_2).setBounds (83.75, 421.5, 34.0, 18.0);
            surface.getButton (ButtonID.ROW3_2).setBounds (83.75, 393.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW4_2).setBounds (83.75, 449.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW6_2).setBounds (83.75, 279.75, 33.0, 32.0);
            surface.getButton (ButtonID.ROW1_3).setBounds (132.75, 338.5, 33.0, 32.0);
            surface.getButton (ButtonID.ROW2_3).setBounds (132.75, 421.5, 34.0, 18.0);
            surface.getButton (ButtonID.ROW3_3).setBounds (132.75, 393.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW4_3).setBounds (132.75, 449.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW6_3).setBounds (132.75, 279.75, 33.0, 32.0);
            surface.getButton (ButtonID.ROW1_4).setBounds (180.25, 338.5, 33.0, 32.0);
            surface.getButton (ButtonID.ROW2_4).setBounds (180.25, 421.5, 34.0, 18.0);
            surface.getButton (ButtonID.ROW3_4).setBounds (180.25, 393.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW4_4).setBounds (180.25, 449.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW6_4).setBounds (180.25, 279.75, 33.0, 32.0);
            surface.getButton (ButtonID.ROW1_5).setBounds (229.0, 338.5, 33.0, 32.0);
            surface.getButton (ButtonID.ROW2_5).setBounds (229.0, 421.5, 34.0, 18.0);
            surface.getButton (ButtonID.ROW3_5).setBounds (229.0, 393.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW4_5).setBounds (229.0, 449.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW6_5).setBounds (229.0, 279.75, 33.0, 32.0);
            surface.getButton (ButtonID.ROW1_6).setBounds (278.25, 338.5, 33.0, 32.0);
            surface.getButton (ButtonID.ROW2_6).setBounds (278.25, 421.5, 34.0, 18.0);
            surface.getButton (ButtonID.ROW3_6).setBounds (278.25, 393.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW4_6).setBounds (278.25, 449.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW6_6).setBounds (278.25, 279.75, 33.0, 32.0);
            surface.getButton (ButtonID.ROW1_7).setBounds (327.25, 338.5, 33.0, 32.0);
            surface.getButton (ButtonID.ROW2_7).setBounds (327.25, 421.5, 34.0, 18.0);
            surface.getButton (ButtonID.ROW3_7).setBounds (327.25, 393.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW4_7).setBounds (327.25, 449.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW6_7).setBounds (327.25, 279.75, 33.0, 32.0);
            surface.getButton (ButtonID.ROW1_8).setBounds (376.25, 338.5, 33.0, 32.0);
            surface.getButton (ButtonID.ROW2_8).setBounds (376.25, 421.5, 34.0, 18.0);
            surface.getButton (ButtonID.ROW3_8).setBounds (376.25, 393.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW4_8).setBounds (376.25, 449.25, 34.0, 18.0);
            surface.getButton (ButtonID.ROW6_8).setBounds (376.25, 279.75, 33.0, 32.0);

            surface.getButton (ButtonID.CLIP).setBounds (651.75, 489.25, 33.25, 15.25);
            surface.getButton (ButtonID.METRONOME).setBounds (723.5, 489.25, 33.25, 15.25);
            surface.getButton (ButtonID.NUDGE_MINUS).setBounds (660.0, 270.75, 33.25, 15.0);
            surface.getButton (ButtonID.NUDGE_PLUS).setBounds (716.0, 270.75, 33.25, 15.0);
            surface.getButton (ButtonID.DEVICE_ON_OFF).setBounds (579.75, 448.75, 33.25, 15.25);
            surface.getButton (ButtonID.TOGGLE_DEVICES_PANE).setBounds (508.25, 448.75, 33.25, 15.25);
            surface.getButton (ButtonID.LAYOUT).setBounds (508.25, 489.25, 33.25, 15.25);
            surface.getButton (ButtonID.BANK_LEFT).setBounds (651.75, 448.75, 33.25, 15.25);
            surface.getButton (ButtonID.BANK_RIGHT).setBounds (723.5, 448.75, 33.25, 15.25);
            surface.getButton (ButtonID.ARROW_DOWN).setBounds (582.0, 233.5, 33.25, 25.0);
            surface.getButton (ButtonID.ARROW_UP).setBounds (582.25, 256.75, 33.25, 25.0);
            surface.getButton (ButtonID.ARROW_LEFT).setBounds (565.0, 233.5, 15.75, 48.25);
            surface.getButton (ButtonID.ARROW_RIGHT).setBounds (616.75, 233.5, 15.75, 48.25);
            surface.getButton (ButtonID.SCENE1).setBounds (428.25, 51.0, 33.0, 32.0);
            surface.getButton (ButtonID.SCENE2).setBounds (428.25, 94.25, 33.0, 32.0);
            surface.getButton (ButtonID.SCENE3).setBounds (428.25, 138.5, 33.0, 32.0);
            surface.getButton (ButtonID.SCENE4).setBounds (428.25, 184.75, 33.0, 32.0);
            surface.getButton (ButtonID.SCENE5).setBounds (428.25, 228.0, 33.0, 32.0);

            surface.getButton (ButtonID.FOOTSWITCH1).setBounds (670, 1.25, 37.5, 21.0);
            surface.getButton (ButtonID.FOOTSWITCH2).setBounds (715.5, 1.25, 37.5, 21.0);

            surface.getButton (ButtonID.SHIFT).setBounds (508.25, 270.75, 33.25, 15.0);
            surface.getButton (ButtonID.PLAY).setBounds (551.0, 536.75, 32.5, 20.0);
            surface.getButton (ButtonID.STOP).setBounds (611.0, 537.5, 32.5, 20.0);
            surface.getButton (ButtonID.RECORD).setBounds (671.0, 536.75, 32.5, 20.0);
            surface.getButton (ButtonID.TAP_TEMPO).setBounds (687.5, 229.75, 33.25, 20.5);
            surface.getButton (ButtonID.QUANTIZE).setBounds (579.75, 489.25, 33.25, 15.25);
            surface.getButton (ButtonID.MASTERTRACK).setBounds (428.25, 338.5, 33.0, 32.0);
            surface.getButton (ButtonID.STOP_ALL_CLIPS).setBounds (428.25, 279.75, 33.0, 32.0);
            surface.getButton (ButtonID.PAN_SEND).setBounds (508.25, 183.25, 33.25, 15.25);
            surface.getButton (ButtonID.SEND1).setBounds (579.75, 183.25, 33.25, 15.25);
            surface.getButton (ButtonID.SEND2).setBounds (651.75, 183.25, 33.25, 15.25);
            surface.getButton (ButtonID.SEND3).setBounds (723.5, 183.25, 33.25, 15.25);

            surface.getContinuous (ContinuousID.FADER_MASTER).setBounds (428.25, 497.25, 38.25, 124.0);
            surface.getContinuous (ContinuousID.PLAY_POSITION).setBounds (428.25, 412.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.CROSSFADER).setBounds (564.5, 572.75, 135.25, 50.0);
            surface.getContinuous (ContinuousID.FADER1).setBounds (33.5, 497.25, 38.25, 124.0);
            surface.getContinuous (ContinuousID.KNOB1).setBounds (508.25, 44.5, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB1).setBounds (508.25, 315.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER2).setBounds (83.75, 497.25, 38.25, 124.0);
            surface.getContinuous (ContinuousID.KNOB2).setBounds (579.75, 44.5, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB2).setBounds (579.75, 315.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER3).setBounds (132.75, 497.25, 38.25, 124.0);
            surface.getContinuous (ContinuousID.KNOB3).setBounds (651.75, 44.5, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB3).setBounds (651.75, 315.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER4).setBounds (180.25, 497.25, 38.25, 124.0);
            surface.getContinuous (ContinuousID.KNOB4).setBounds (723.5, 44.5, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB4).setBounds (723.5, 315.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER5).setBounds (229.0, 497.25, 38.25, 124.0);
            surface.getContinuous (ContinuousID.KNOB5).setBounds (508.25, 120.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB5).setBounds (508.25, 389.5, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER6).setBounds (278.25, 497.25, 38.25, 124.0);
            surface.getContinuous (ContinuousID.KNOB6).setBounds (579.75, 120.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB6).setBounds (579.75, 389.5, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER7).setBounds (327.25, 497.25, 38.25, 124.0);
            surface.getContinuous (ContinuousID.KNOB7).setBounds (651.75, 120.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB7).setBounds (651.75, 389.5, 40.25, 37.75);
            surface.getContinuous (ContinuousID.FADER8).setBounds (376.25, 497.25, 38.25, 124.0);
            surface.getContinuous (ContinuousID.KNOB8).setBounds (723.5, 120.0, 40.25, 37.75);
            surface.getContinuous (ContinuousID.DEVICE_KNOB8).setBounds (723.5, 389.5, 40.25, 37.75);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final APCControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActive (Modes.PAN);
        surface.getViewManager ().setActive (this.configuration.shouldStartWithSessionView () ? Views.SESSION : this.configuration.getPreferredNoteView ());
    }


    private int getCrossfadeButtonColor (final int index)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        if (!track.doesExist ())
            return 0;

        final String crossfadeMode = track.getCrossfadeParameter ().getDisplayedValue ();
        if ("AB".equals (crossfadeMode))
            return 0;
        return "A".equals (crossfadeMode) ? 1 : 2;
    }


    private static boolean getMuteButtonState (final boolean isShift, final ITrack track)
    {
        return isShift ? track.isMonitor () : !track.isMute ();
    }


    private static boolean getSoloButtonState (final boolean isShift, final ITrack track)
    {
        return isShift ? track.isAutoMonitor () : track.isSolo ();
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        final APCControlSurface surface = this.getSurface ();
        final IView view = surface.getViewManager ().getActive ();
        if (view == null)
            return;

        final IParameterBank parameterBank = this.model.getCursorDevice ().getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter item = parameterBank.getItem (i);
            surface.setLED (APCControlSurface.APC_KNOB_DEVICE_KNOB_LED_1 + i, item.doesExist () ? APCControlSurface.LED_MODE_SINGLE : APCControlSurface.LED_MODE_VOLUME);
            surface.setLED (APCControlSurface.APC_KNOB_DEVICE_KNOB_1 + i, item.doesExist () ? item.getValue () : 0);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.FOOTSWITCH1 || buttonID == ButtonID.FOOTSWITCH2)
            return BindType.CC;
        return BindType.NOTE;
    }


    private boolean getButtonState (final int index, final int button)
    {
        // Activator, Solo, Record Arm

        final APCControlSurface surface = this.getSurface ();
        final int clipLength = surface.getConfiguration ().getNewClipLength ();
        final ModeManager modeManager = surface.getModeManager ();

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getItem (index);
        final boolean trackExists = track.doesExist ();

        final boolean isShift = surface.isShiftPressed ();

        switch (button)
        {
            case APCControlSurface.APC_BUTTON_TRACK_SELECTION:
                if (isShift)
                    return index == clipLength;

                final Optional<ITrack> selTrack = tb.getSelectedItem ();
                final int selIndex = selTrack.isEmpty () ? -1 : selTrack.get ().getIndex ();

                // Handle user mode selection
                if (surface.isMkII () && surface.isPressed (ButtonID.SEND2))
                {
                    final IParameterBank userParameterBank = this.model.getUserParameterBank ();
                    final int pageSize = userParameterBank.getPageSize ();
                    final int selectedPage = userParameterBank.getScrollPosition () / pageSize;
                    return selectedPage == index;
                }

                // Handle send mode selection
                return surface.isPressed (ButtonID.SEND1) ? modeManager.isActive (Modes.get (Modes.SEND1, index)) : index == selIndex;

            case APCControlSurface.APC_BUTTON_SOLO:
                return trackExists && getSoloButtonState (isShift, track);

            case APCControlSurface.APC_BUTTON_ACTIVATOR:
                return trackExists && getMuteButtonState (isShift, track);

            case APCControlSurface.APC_BUTTON_RECORD_ARM:
                if (isShift)
                    return this.getCrossfadeButtonColor (index) > 0;
                return trackExists && track.isRecArm ();

            default:
                return false;
        }
    }
}
