// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc;

import de.mossgrabers.controller.apc.command.trigger.APCBrowserCommand;
import de.mossgrabers.controller.apc.command.trigger.APCQuantizeCommand;
import de.mossgrabers.controller.apc.command.trigger.APCRecordCommand;
import de.mossgrabers.controller.apc.command.trigger.SelectTrackSendOrClipLengthCommand;
import de.mossgrabers.controller.apc.command.trigger.SendModeCommand;
import de.mossgrabers.controller.apc.command.trigger.SessionRecordCommand;
import de.mossgrabers.controller.apc.command.trigger.StopAllClipsOrBrowseCommand;
import de.mossgrabers.controller.apc.controller.APCColors;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.controller.apc.mode.BrowserMode;
import de.mossgrabers.controller.apc.mode.PanMode;
import de.mossgrabers.controller.apc.mode.SendMode;
import de.mossgrabers.controller.apc.view.DrumView;
import de.mossgrabers.controller.apc.view.PlayView;
import de.mossgrabers.controller.apc.view.RaindropsView;
import de.mossgrabers.controller.apc.view.SequencerView;
import de.mossgrabers.controller.apc.view.SessionView;
import de.mossgrabers.controller.apc.view.ShiftView;
import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.SceneCommand;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.continuous.CrossfaderCommand;
import de.mossgrabers.framework.command.continuous.FaderAbsoluteCommand;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.continuous.MasterFaderAbsoluteCommand;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;
import de.mossgrabers.framework.command.continuous.TempoCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand.Panels;
import de.mossgrabers.framework.command.trigger.application.PanelLayoutCommand;
import de.mossgrabers.framework.command.trigger.application.RedoCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.StopClipCommand;
import de.mossgrabers.framework.command.trigger.device.DeviceLayerLeftCommand;
import de.mossgrabers.framework.command.trigger.device.DeviceLayerRightCommand;
import de.mossgrabers.framework.command.trigger.device.DeviceOnOffCommand;
import de.mossgrabers.framework.command.trigger.device.DeviceParamsKnobRowCommand;
import de.mossgrabers.framework.command.trigger.device.SelectNextDeviceOrParamPageCommand;
import de.mossgrabers.framework.command.trigger.device.SelectPreviousDeviceOrParamPageCommand;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.track.CrossfadeModeCommand;
import de.mossgrabers.framework.command.trigger.track.MasterCommand;
import de.mossgrabers.framework.command.trigger.track.MuteCommand;
import de.mossgrabers.framework.command.trigger.track.RecArmCommand;
import de.mossgrabers.framework.command.trigger.track.SoloCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.command.trigger.view.ToggleShiftViewCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Akai APC40 mkI and APC40 mkII controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
     * @param isMkII True if is mkII
     */
    public APCControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings, final boolean isMkII)
    {
        super (factory, host, globalSettings, documentSettings);
        this.isMkII = isMkII;
        this.colorManager = new ColorManager ();
        APCColors.addColors (this.colorManager, isMkII);
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new APCConfiguration (host, this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushSurfaces ();
        this.updateButtons ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 76, 8, 5);
        this.scales.setDrumDefaultOffset (12);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setNumScenes (5);
        ms.setNumDrumPadLayers (12);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
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
        final IMidiInput input = midiAccess.createInput (this.isMkII ? "Akai APC40 mkII" : "Akai APC40",
                "B040??" /* Sustainpedal */);
        final APCControlSurface surface = new APCControlSurface (this.host, this.colorManager, this.configuration, output, input, this.isMkII);
        this.surfaces.add (surface);
        surface.setDisplay (new DummyDisplay (this.host));
        for (int i = 0; i < 8; i++)
            surface.setLED (APCControlSurface.APC_KNOB_DEVICE_KNOB_LED_1 + i, 1);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        final APCControlSurface surface = this.getSurface ();
        surface.getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> this.updateMode (null));
        surface.getModeManager ().addModeListener ( (previousModeId, activeModeId) -> this.updateMode (activeModeId));
        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final APCControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.registerMode (Modes.MODE_PAN, new PanMode (surface, this.model));
        for (int i = 0; i < 8; i++)
            modeManager.registerMode (Modes.get (Modes.MODE_SEND1, i), new SendMode (surface, this.model, i));
        modeManager.registerMode (Modes.MODE_BROWSER, new BrowserMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final APCControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.VIEW_PLAY, new PlayView (surface, this.model));
        viewManager.registerView (Views.VIEW_SESSION, new SessionView (surface, this.model));
        viewManager.registerView (Views.VIEW_SEQUENCER, new SequencerView (surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM, new DrumView (surface, this.model));
        viewManager.registerView (Views.VIEW_RAINDROPS, new RaindropsView (surface, this.model));
        viewManager.registerView (Views.VIEW_SHIFT, new ShiftView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final APCControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        this.addTriggerCommand (TriggerCommandID.SHIFT, APCControlSurface.APC_BUTTON_SHIFT, new ToggleShiftViewCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.PLAY, APCControlSurface.APC_BUTTON_PLAY, new PlayCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.RECORD, APCControlSurface.APC_BUTTON_RECORD, new APCRecordCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.TAP_TEMPO, APCControlSurface.APC_BUTTON_TAP_TEMPO, new TapTempoCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.QUANTIZE, APCControlSurface.APC_BUTTON_REC_QUANT, new APCQuantizeCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.PAN_SEND, APCControlSurface.APC_BUTTON_PAN, new ModeSelectCommand<> (this.model, surface, Modes.MODE_PAN));
        this.addTriggerCommand (TriggerCommandID.MASTERTRACK, APCControlSurface.APC_BUTTON_MASTER, new MasterCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.STOP_ALL_CLIPS, APCControlSurface.APC_BUTTON_STOP_ALL_CLIPS, new StopAllClipsOrBrowseCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SEND1, APCControlSurface.APC_BUTTON_SEND_A, new SendModeCommand (0, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SEND2, APCControlSurface.APC_BUTTON_SEND_B, new SendModeCommand (1, this.model, surface));

        for (int i = 0; i < 8; i++)
        {
            final TriggerCommandID selectCommand = TriggerCommandID.get (TriggerCommandID.ROW1_1, i);
            final TriggerCommandID soloCommand = TriggerCommandID.get (TriggerCommandID.ROW2_1, i);
            final TriggerCommandID muteCommand = TriggerCommandID.get (TriggerCommandID.ROW3_1, i);
            final TriggerCommandID recArmCommand = TriggerCommandID.get (TriggerCommandID.ROW4_1, i);
            final TriggerCommandID crossfadeCommand = TriggerCommandID.get (TriggerCommandID.ROW5_1, i);
            final TriggerCommandID stopClipCommand = TriggerCommandID.get (TriggerCommandID.ROW6_1, i);

            viewManager.registerTriggerCommand (selectCommand, new SelectTrackSendOrClipLengthCommand (i, this.model, surface));
            viewManager.registerTriggerCommand (soloCommand, new SoloCommand<> (i, this.model, surface));
            viewManager.registerTriggerCommand (muteCommand, new MuteCommand<> (i, this.model, surface));
            viewManager.registerTriggerCommand (recArmCommand, new RecArmCommand<> (i, this.model, surface));
            viewManager.registerTriggerCommand (crossfadeCommand, new CrossfadeModeCommand<> (i, this.model, surface));
            viewManager.registerTriggerCommand (stopClipCommand, new StopClipCommand<> (i, this.model, surface));
            surface.assignTriggerCommand (i, APCControlSurface.APC_BUTTON_TRACK_SELECTION, selectCommand);
            surface.assignTriggerCommand (i, APCControlSurface.APC_BUTTON_SOLO, soloCommand);
            surface.assignTriggerCommand (i, APCControlSurface.APC_BUTTON_ACTIVATOR, muteCommand);
            surface.assignTriggerCommand (i, APCControlSurface.APC_BUTTON_RECORD_ARM, recArmCommand);
            surface.assignTriggerCommand (i, APCControlSurface.APC_BUTTON_A_B, crossfadeCommand);
            surface.assignTriggerCommand (i, APCControlSurface.APC_BUTTON_CLIP_STOP, stopClipCommand);
        }

        viewManager.registerTriggerCommand (TriggerCommandID.DEVICE_LEFT, new DeviceLayerLeftCommand<> (this.model, surface));
        viewManager.registerTriggerCommand (TriggerCommandID.DEVICE_RIGHT, new DeviceLayerRightCommand<> (this.model, surface));
        viewManager.registerTriggerCommand (TriggerCommandID.CLIP, new SessionRecordCommand (this.model, surface));
        viewManager.registerTriggerCommand (TriggerCommandID.METRONOME, new MetronomeCommand<> (this.model, surface));
        viewManager.registerTriggerCommand (TriggerCommandID.NUDGE_MINUS, new RedoCommand<> (this.model, surface));
        viewManager.registerTriggerCommand (TriggerCommandID.NUDGE_PLUS, new UndoCommand<> (this.model, surface));
        viewManager.registerTriggerCommand (TriggerCommandID.LAYOUT, new PanelLayoutCommand<> (this.model, surface));
        viewManager.registerTriggerCommand (TriggerCommandID.DEVICE_ON_OFF, new DeviceOnOffCommand<> (this.model, surface));
        viewManager.registerTriggerCommand (TriggerCommandID.TOGGLE_DEVICES_PANE, new PaneCommand<> (Panels.DEVICE, this.model, surface));
        if (this.isMkII)
        {
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_SESSION, TriggerCommandID.CLIP);
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_SEND_C, TriggerCommandID.METRONOME);
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_NUDGE_MINUS, TriggerCommandID.NUDGE_MINUS);
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_NUDGE_PLUS, TriggerCommandID.NUDGE_PLUS);
            // Detail View
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_METRONOME, TriggerCommandID.LAYOUT);
            // Device on/off
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_DETAIL_VIEW, TriggerCommandID.DEVICE_ON_OFF);
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_CLIP_TRACK, TriggerCommandID.DEVICE_LEFT);
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_DEVICE_ON_OFF, TriggerCommandID.DEVICE_RIGHT);
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_MIDI_OVERDUB, TriggerCommandID.TOGGLE_DEVICES_PANE);
        }
        else
        {
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_MIDI_OVERDUB, TriggerCommandID.CLIP);
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_METRONOME, TriggerCommandID.METRONOME);
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_NUDGE_PLUS, TriggerCommandID.NUDGE_MINUS);
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_NUDGE_MINUS, TriggerCommandID.NUDGE_PLUS);
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_DETAIL_VIEW, TriggerCommandID.LAYOUT);
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_DEVICE_ON_OFF, TriggerCommandID.DEVICE_ON_OFF);
            surface.assignTriggerCommand (APCControlSurface.APC_BUTTON_CLIP_TRACK, TriggerCommandID.TOGGLE_DEVICES_PANE);

            this.addTriggerCommand (TriggerCommandID.STOP, APCControlSurface.APC_BUTTON_STOP, new StopCommand<> (this.model, surface));
            this.addTriggerCommand (TriggerCommandID.SEND3, APCControlSurface.APC_BUTTON_SEND_C, new SendModeCommand (2, this.model, surface));
            this.addTriggerCommand (TriggerCommandID.NEW, APCControlSurface.APC_FOOTSWITCH_2, new NewCommand<> (this.model, surface));
        }

        this.addTriggerCommand (TriggerCommandID.BROWSE, APCControlSurface.APC_BUTTON_BANK, new APCBrowserCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.BANK_LEFT, APCControlSurface.APC_BUTTON_DEVICE_LEFT, new SelectPreviousDeviceOrParamPageCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.BANK_RIGHT, APCControlSurface.APC_BUTTON_DEVICE_RIGHT, new SelectNextDeviceOrParamPageCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_DOWN, surface.getDownTriggerId (), new CursorCommand<> (Direction.DOWN, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_UP, surface.getUpTriggerId (), new CursorCommand<> (Direction.UP, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_LEFT, surface.getLeftTriggerId (), new CursorCommand<> (Direction.LEFT, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_RIGHT, surface.getRightTriggerId (), new CursorCommand<> (Direction.RIGHT, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SCENE1, APCControlSurface.APC_BUTTON_SCENE_LAUNCH_1, new SceneCommand<> (0, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SCENE2, APCControlSurface.APC_BUTTON_SCENE_LAUNCH_2, new SceneCommand<> (1, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SCENE3, APCControlSurface.APC_BUTTON_SCENE_LAUNCH_3, new SceneCommand<> (2, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SCENE4, APCControlSurface.APC_BUTTON_SCENE_LAUNCH_4, new SceneCommand<> (3, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SCENE5, APCControlSurface.APC_BUTTON_SCENE_LAUNCH_5, new SceneCommand<> (4, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final APCControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        this.addContinuousCommand (ContinuousCommandID.MASTER_KNOB, APCControlSurface.APC_KNOB_MASTER_LEVEL, new MasterFaderAbsoluteCommand<> (this.model, surface));
        this.addContinuousCommand (ContinuousCommandID.PLAY_POSITION, APCControlSurface.APC_KNOB_CUE_LEVEL, new PlayPositionCommand<> (this.model, surface));
        this.addContinuousCommand (ContinuousCommandID.CROSSFADER, APCControlSurface.APC_KNOB_CROSSFADER, new CrossfaderCommand<> (this.model, surface));

        for (int i = 0; i < 8; i++)
        {
            final ContinuousCommandID faderCommand = ContinuousCommandID.get (ContinuousCommandID.FADER1, i);
            viewManager.registerContinuousCommand (faderCommand, new FaderAbsoluteCommand<> (i, this.model, surface));
            surface.assignContinuousCommand (i, APCControlSurface.APC_KNOB_TRACK_LEVEL, faderCommand);

            final ContinuousCommandID knobCommand = ContinuousCommandID.get (ContinuousCommandID.KNOB1, i);
            viewManager.registerContinuousCommand (knobCommand, new KnobRowModeCommand<> (i, this.model, surface));
            surface.assignContinuousCommand (APCControlSurface.APC_KNOB_TRACK_KNOB_1 + i, knobCommand);

            final ContinuousCommandID deviceKnobCommand = ContinuousCommandID.get (ContinuousCommandID.DEVICE_KNOB1, i);
            viewManager.registerContinuousCommand (deviceKnobCommand, new DeviceParamsKnobRowCommand<> (i, this.model, surface));
            surface.assignContinuousCommand (APCControlSurface.APC_KNOB_DEVICE_KNOB_1 + i, deviceKnobCommand);
        }

        if (this.isMkII)
            this.addContinuousCommand (ContinuousCommandID.TEMPO, APCControlSurface.APC_KNOB_TEMPO, new TempoCommand<> (this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final APCControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActiveMode (Modes.MODE_PAN);
        surface.getViewManager ().setActiveView (Views.VIEW_PLAY);
    }


    private void updateButtons ()
    {
        final APCControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final View activeView = viewManager.getActiveView ();
        if (activeView != null)
        {
            ((CursorCommand<?, ?>) activeView.getTriggerCommand (TriggerCommandID.ARROW_DOWN)).updateArrows ();
            ((SceneView) activeView).updateSceneButtons ();
        }

        final boolean isShift = surface.isShiftPressed ();
        final boolean isSendA = surface.isPressed (APCControlSurface.APC_BUTTON_SEND_A);

        final ITransport t = this.model.getTransport ();
        surface.updateTrigger (APCControlSurface.APC_BUTTON_PLAY, t.isPlaying () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        surface.updateTrigger (APCControlSurface.APC_BUTTON_RECORD, t.isRecording () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);

        // Activator, Solo, Record Arm
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selTrack = tb.getSelectedItem ();
        final int selIndex = selTrack == null ? -1 : selTrack.getIndex ();
        final int clipLength = surface.getConfiguration ().getNewClipLength ();
        final ModeManager modeManager = surface.getModeManager ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            final boolean trackExists = track.doesExist ();

            boolean isOn;
            if (isShift)
                isOn = i == clipLength;
            else
                isOn = isSendA ? modeManager.isActiveOrTempMode (Modes.get (Modes.MODE_SEND1, i)) : i == selIndex;
            surface.updateTrigger (i, APCControlSurface.APC_BUTTON_TRACK_SELECTION, isOn ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (i, APCControlSurface.APC_BUTTON_SOLO, trackExists && getSoloButtonState (isShift, track) ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (i, APCControlSurface.APC_BUTTON_ACTIVATOR, trackExists && getMuteButtonState (isShift, track) ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);

            if (this.isMkII)
            {
                surface.updateTrigger (i, APCControlSurface.APC_BUTTON_A_B, getCrossfadeButtonColor (track, trackExists));
                surface.updateTrigger (i, APCControlSurface.APC_BUTTON_RECORD_ARM, trackExists && track.isRecArm () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
            }
            else
            {
                if (isShift)
                    surface.updateTrigger (i, APCControlSurface.APC_BUTTON_RECORD_ARM, getCrossfadeButtonColor (track, trackExists));
                else
                    surface.updateTrigger (i, APCControlSurface.APC_BUTTON_RECORD_ARM, trackExists && track.isRecArm () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
            }
        }
        surface.updateTrigger (APCControlSurface.APC_BUTTON_MASTER, this.model.getMasterTrack ().isSelected () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);

        final ICursorDevice device = this.model.getCursorDevice ();

        if (this.isMkII)
        {
            surface.updateTrigger (APCControlSurface.APC_BUTTON_SESSION, t.isLauncherOverdub () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_SEND_C, t.isMetronomeOn () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);

            surface.updateTrigger (APCControlSurface.APC_BUTTON_DETAIL_VIEW, device.isEnabled () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_REC_QUANT, ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_MIDI_OVERDUB, ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_METRONOME, ColorManager.BUTTON_STATE_OFF);

            surface.updateTrigger (APCControlSurface.APC_BUTTON_CLIP_TRACK, ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_DEVICE_ON_OFF, ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_DEVICE_LEFT, ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_DEVICE_RIGHT, ColorManager.BUTTON_STATE_OFF);

            surface.updateTrigger (APCControlSurface.APC_BUTTON_BANK, this.model.getBrowser ().isActive () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        }
        else
        {
            surface.updateTrigger (APCControlSurface.APC_BUTTON_DETAIL_VIEW, ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_REC_QUANT, ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_MIDI_OVERDUB, t.isLauncherOverdub () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_METRONOME, t.isMetronomeOn () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);

            surface.updateTrigger (APCControlSurface.APC_BUTTON_CLIP_TRACK, ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_DEVICE_ON_OFF, device.isEnabled () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_DEVICE_LEFT, ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_DEVICE_RIGHT, ColorManager.BUTTON_STATE_OFF);
        }

        this.updateDeviceKnobs ();
    }


    private static String getCrossfadeButtonColor (final ITrack track, final boolean trackExists)
    {
        if (!trackExists)
            return ColorManager.BUTTON_STATE_OFF;

        final String crossfadeMode = track.getCrossfadeMode ();
        if ("AB".equals (crossfadeMode))
            return ColorManager.BUTTON_STATE_OFF;

        return "A".equals (crossfadeMode) ? ColorManager.BUTTON_STATE_ON : APCColors.BUTTON_STATE_BLINK;
    }


    private static boolean getMuteButtonState (final boolean isShift, final ITrack track)
    {
        return isShift ? track.isMonitor () : !track.isMute ();
    }


    private static boolean getSoloButtonState (final boolean isShift, final ITrack track)
    {
        return isShift ? track.isAutoMonitor () : track.isSolo ();
    }


    private void updateMode (final Modes mode)
    {
        final APCControlSurface surface = this.getSurface ();
        final Modes m = mode == null ? surface.getModeManager ().getActiveOrTempModeId () : mode;
        this.updateIndication (m);
        surface.updateTrigger (APCControlSurface.APC_BUTTON_PAN, Modes.MODE_PAN.equals (m) ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        if (surface.isMkII ())
        {
            surface.updateTrigger (APCControlSurface.APC_BUTTON_SEND_A, Modes.MODE_SEND1.equals (m) || Modes.MODE_SEND3.equals (m) || Modes.MODE_SEND5.equals (m) || Modes.MODE_SEND7.equals (m) ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_SEND_B, Modes.MODE_SEND2.equals (m) || Modes.MODE_SEND4.equals (m) || Modes.MODE_SEND6.equals (m) || Modes.MODE_SEND8.equals (m) ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        }
        else
        {
            surface.updateTrigger (APCControlSurface.APC_BUTTON_SEND_A, Modes.MODE_SEND1.equals (m) || Modes.MODE_SEND4.equals (m) || Modes.MODE_SEND7.equals (m) ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_SEND_B, Modes.MODE_SEND2.equals (m) || Modes.MODE_SEND5.equals (m) || Modes.MODE_SEND8.equals (m) ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
            surface.updateTrigger (APCControlSurface.APC_BUTTON_SEND_C, Modes.MODE_SEND3.equals (m) || Modes.MODE_SEND6.equals (m) ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        }
    }


    private void updateDeviceKnobs ()
    {
        final APCControlSurface surface = this.getSurface ();
        final View view = surface.getViewManager ().getActiveView ();
        if (view == null)
            return;

        final IParameterBank parameterBank = this.model.getCursorDevice ().getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final ContinuousCommandID deviceKnobCommand = ContinuousCommandID.get (ContinuousCommandID.DEVICE_KNOB1, i);
            if (!((DeviceParamsKnobRowCommand<?, ?>) view.getContinuousCommand (deviceKnobCommand)).isKnobMoving ())
                surface.setLED (APCControlSurface.APC_KNOB_DEVICE_KNOB_1 + i, parameterBank.getItem (i).getValue ());
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Modes mode)
    {
        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final APCControlSurface surface = this.getSurface ();
        final boolean isSession = surface.getViewManager ().isActiveView (Views.VIEW_SESSION);
        final boolean isEffect = this.model.isEffectTrackBankActive ();
        final boolean isPan = Modes.MODE_PAN.equals (mode);
        final boolean isShift = surface.isShiftPressed ();

        tb.setIndication (!isEffect && (isSession || isShift));
        if (tbe != null)
            tbe.setIndication (isEffect && (isSession || isShift));

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (!isEffect);
            track.setPanIndication (!isEffect && isPan);
            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < 8; j++)
                sendBank.getItem (j).setIndication (!isEffect && (Modes.MODE_SEND1.equals (mode) && j == 0 || Modes.MODE_SEND2.equals (mode) && j == 1 || Modes.MODE_SEND3.equals (mode) && j == 2 || Modes.MODE_SEND4.equals (mode) && j == 3 || Modes.MODE_SEND5.equals (mode) && j == 4 || Modes.MODE_SEND6.equals (mode) && j == 5 || Modes.MODE_SEND7.equals (mode) && j == 6 || Modes.MODE_SEND8.equals (mode) && j == 7));

            if (tbe != null)
            {
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (isEffect);
                fxTrack.setPanIndication (isEffect && isPan);
            }

            parameterBank.getItem (i).setIndication (true);
        }
    }


    /**
     * Handle a track selection change.
     *
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final boolean isSelected)
    {
        if (!isSelected)
            return;

        final APCControlSurface surface = this.getSurface ();
        // Recall last used view (if we are not in session mode)
        final ViewManager viewManager = surface.getViewManager ();
        if (!viewManager.isActiveView (Views.VIEW_SESSION))
        {
            final ITrack selectedTrack = this.model.getSelectedTrack ();
            if (selectedTrack != null)
            {
                final Views preferredView = viewManager.getPreferredView (selectedTrack.getPosition ());
                viewManager.setActiveView (preferredView == null ? Views.VIEW_PLAY : preferredView);
            }
        }

        if (viewManager.isActiveView (Views.VIEW_PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.resetDrumOctave ();
        if (viewManager.isActiveView (Views.VIEW_DRUM))
            viewManager.getView (Views.VIEW_DRUM).updateNoteMapping ();
    }
}
