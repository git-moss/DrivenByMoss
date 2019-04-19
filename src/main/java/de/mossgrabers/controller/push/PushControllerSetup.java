// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push;

import de.mossgrabers.controller.push.command.continuous.ConfigurePitchbendCommand;
import de.mossgrabers.controller.push.command.continuous.MastertrackTouchCommand;
import de.mossgrabers.controller.push.command.continuous.SmallKnobTouchCommand;
import de.mossgrabers.controller.push.command.pitchbend.PitchbendCommand;
import de.mossgrabers.controller.push.command.pitchbend.PitchbendSessionCommand;
import de.mossgrabers.controller.push.command.trigger.AccentCommand;
import de.mossgrabers.controller.push.command.trigger.AutomationCommand;
import de.mossgrabers.controller.push.command.trigger.ClipCommand;
import de.mossgrabers.controller.push.command.trigger.DeviceCommand;
import de.mossgrabers.controller.push.command.trigger.FixedLengthCommand;
import de.mossgrabers.controller.push.command.trigger.LayoutCommand;
import de.mossgrabers.controller.push.command.trigger.MastertrackCommand;
import de.mossgrabers.controller.push.command.trigger.MuteCommand;
import de.mossgrabers.controller.push.command.trigger.OctaveCommand;
import de.mossgrabers.controller.push.command.trigger.PageLeftCommand;
import de.mossgrabers.controller.push.command.trigger.PageRightCommand;
import de.mossgrabers.controller.push.command.trigger.PanSendCommand;
import de.mossgrabers.controller.push.command.trigger.PushBrowserCommand;
import de.mossgrabers.controller.push.command.trigger.PushQuantizeCommand;
import de.mossgrabers.controller.push.command.trigger.RasteredKnobCommand;
import de.mossgrabers.controller.push.command.trigger.ScalesCommand;
import de.mossgrabers.controller.push.command.trigger.SelectCommand;
import de.mossgrabers.controller.push.command.trigger.SelectPlayViewCommand;
import de.mossgrabers.controller.push.command.trigger.SelectSessionViewCommand;
import de.mossgrabers.controller.push.command.trigger.SetupCommand;
import de.mossgrabers.controller.push.command.trigger.ShiftCommand;
import de.mossgrabers.controller.push.command.trigger.SoloCommand;
import de.mossgrabers.controller.push.command.trigger.TrackCommand;
import de.mossgrabers.controller.push.command.trigger.VolumeCommand;
import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.PushDisplay;
import de.mossgrabers.controller.push.mode.AccentMode;
import de.mossgrabers.controller.push.mode.AutomationMode;
import de.mossgrabers.controller.push.mode.ConfigurationMode;
import de.mossgrabers.controller.push.mode.FixedMode;
import de.mossgrabers.controller.push.mode.FrameMode;
import de.mossgrabers.controller.push.mode.GrooveMode;
import de.mossgrabers.controller.push.mode.InfoMode;
import de.mossgrabers.controller.push.mode.MarkersMode;
import de.mossgrabers.controller.push.mode.NoteMode;
import de.mossgrabers.controller.push.mode.NoteRepeatMode;
import de.mossgrabers.controller.push.mode.NoteViewSelectMode;
import de.mossgrabers.controller.push.mode.RibbonMode;
import de.mossgrabers.controller.push.mode.ScaleLayoutMode;
import de.mossgrabers.controller.push.mode.ScalesMode;
import de.mossgrabers.controller.push.mode.SessionMode;
import de.mossgrabers.controller.push.mode.SessionViewSelectMode;
import de.mossgrabers.controller.push.mode.SetupMode;
import de.mossgrabers.controller.push.mode.TransportMode;
import de.mossgrabers.controller.push.mode.device.DeviceBrowserMode;
import de.mossgrabers.controller.push.mode.device.DeviceLayerMode;
import de.mossgrabers.controller.push.mode.device.DeviceLayerModePan;
import de.mossgrabers.controller.push.mode.device.DeviceLayerModeSend;
import de.mossgrabers.controller.push.mode.device.DeviceLayerModeVolume;
import de.mossgrabers.controller.push.mode.device.DeviceParamsMode;
import de.mossgrabers.controller.push.mode.track.ClipMode;
import de.mossgrabers.controller.push.mode.track.CrossfaderMode;
import de.mossgrabers.controller.push.mode.track.LayerDetailsMode;
import de.mossgrabers.controller.push.mode.track.MasterMode;
import de.mossgrabers.controller.push.mode.track.PanMode;
import de.mossgrabers.controller.push.mode.track.SendMode;
import de.mossgrabers.controller.push.mode.track.TrackDetailsMode;
import de.mossgrabers.controller.push.mode.track.TrackMode;
import de.mossgrabers.controller.push.mode.track.VolumeMode;
import de.mossgrabers.controller.push.view.ClipView;
import de.mossgrabers.controller.push.view.ColorView;
import de.mossgrabers.controller.push.view.DrumView;
import de.mossgrabers.controller.push.view.DrumView4;
import de.mossgrabers.controller.push.view.DrumView64;
import de.mossgrabers.controller.push.view.DrumView8;
import de.mossgrabers.controller.push.view.PianoView;
import de.mossgrabers.controller.push.view.PlayView;
import de.mossgrabers.controller.push.view.PolySequencerView;
import de.mossgrabers.controller.push.view.PrgChangeView;
import de.mossgrabers.controller.push.view.RaindropsView;
import de.mossgrabers.controller.push.view.ScenePlayView;
import de.mossgrabers.controller.push.view.SequencerView;
import de.mossgrabers.controller.push.view.SessionView;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.SceneCommand;
import de.mossgrabers.framework.command.aftertouch.AftertouchAbstractPlayViewCommand;
import de.mossgrabers.framework.command.continuous.FootswitchCommand;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.continuous.MasterVolumeCommand;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.application.DeleteCommand;
import de.mossgrabers.framework.command.trigger.application.DuplicateCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.ConvertCommand;
import de.mossgrabers.framework.command.trigger.clip.DoubleCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.NoteRepeatCommand;
import de.mossgrabers.framework.command.trigger.clip.StopAllClipsCommand;
import de.mossgrabers.framework.command.trigger.device.AddEffectCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.command.trigger.mode.KnobRowTouchModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.track.AddTrackCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.DeviceInquiry;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Ableton Push 1 and Push 2 controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushControllerSetup extends AbstractControllerSetup<PushControlSurface, PushConfiguration>
{
    protected final boolean isPush2;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     * @param isPush2 True if Push 2
     */
    public PushControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings, final boolean isPush2)
    {
        super (factory, host, settings);
        this.isPush2 = isPush2;
        this.colorManager = new ColorManager ();
        PushColors.addColors (this.colorManager, isPush2);
        this.valueChanger = new DefaultValueChanger (1024, 10, 1);
        this.configuration = new PushConfiguration (host, this.valueChanger, isPush2);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushSurfaces ();

        this.updateButtons ();
        final PushControlSurface surface = this.getSurface ();
        this.updateMode (surface.getModeManager ().getActiveOrTempModeId ());

        final View activeView = surface.getViewManager ().getActiveView ();
        if (activeView == null)
            return;
        final de.mossgrabers.framework.command.core.PitchbendCommand pitchbendCommand = activeView.getPitchbendCommand ();
        if (pitchbendCommand != null)
            pitchbendCommand.updateValue ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        if (this.isPush2)
        {
            ms.setNumFilterColumnEntries (48);
            ms.setNumResults (48);
        }
        ms.setNumMarkers (8);
        ms.setHasFlatTrackList (false);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
        this.model.getMasterTrack ().addSelectionObserver ( (index, isSelected) -> {
            final PushControlSurface surface = this.getSurface ();
            final ModeManager modeManager = surface.getModeManager ();
            if (isSelected)
                modeManager.setActiveMode (Modes.MODE_MASTER);
            else if (modeManager.isActiveOrTempMode (Modes.MODE_MASTER))
                modeManager.restoreMode ();
        });
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Pads", "80????" /* Note off */,
                "90????" /* Note on */, "B040??" /* Sustainpedal */);
        final PushControlSurface surface = new PushControlSurface (this.model.getHost (), this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);
        surface.setDisplay (this.createDisplay (output));
        surface.getModeManager ().setDefaultMode (Modes.MODE_TRACK);
    }


    protected PushDisplay createDisplay (final IMidiOutput output)
    {
        return new PushDisplay (this.model.getHost (), this.isPush2, this.valueChanger.getUpperBound (), output, this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final PushControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_PAN, new PanMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_CROSSFADER, new CrossfaderMode (surface, this.model));

        final SendMode modeSend = new SendMode (surface, this.model);
        modeManager.registerMode (Modes.MODE_SEND1, modeSend);
        modeManager.registerMode (Modes.MODE_SEND2, modeSend);
        modeManager.registerMode (Modes.MODE_SEND3, modeSend);
        modeManager.registerMode (Modes.MODE_SEND4, modeSend);
        modeManager.registerMode (Modes.MODE_SEND5, modeSend);
        modeManager.registerMode (Modes.MODE_SEND6, modeSend);
        modeManager.registerMode (Modes.MODE_SEND7, modeSend);
        modeManager.registerMode (Modes.MODE_SEND8, modeSend);

        modeManager.registerMode (Modes.MODE_MASTER, new MasterMode (surface, this.model, false));
        modeManager.registerMode (Modes.MODE_MASTER_TEMP, new MasterMode (surface, this.model, true));

        modeManager.registerMode (Modes.MODE_TRACK, new TrackMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_TRACK_DETAILS, new TrackDetailsMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_DEVICE_LAYER_DETAILS, new LayerDetailsMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_CLIP, new ClipMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_NOTE, new NoteMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_FRAME, new FrameMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_SCALES, new ScalesMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_SCALE_LAYOUT, new ScaleLayoutMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_ACCENT, new AccentMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_FIXED, new FixedMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_RIBBON, new RibbonMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_GROOVE, new GrooveMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_VIEW_SELECT, new NoteViewSelectMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_MARKERS, new MarkersMode (surface, this.model));

        modeManager.registerMode (Modes.MODE_AUTOMATION, new AutomationMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_TRANSPORT, new TransportMode (surface, this.model));

        modeManager.registerMode (Modes.MODE_DEVICE_PARAMS, new DeviceParamsMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_DEVICE_LAYER, new DeviceLayerMode ("Layer", surface, this.model));

        modeManager.registerMode (Modes.MODE_BROWSER, new DeviceBrowserMode (surface, this.model));

        modeManager.registerMode (Modes.MODE_DEVICE_LAYER_VOLUME, new DeviceLayerModeVolume (surface, this.model));
        modeManager.registerMode (Modes.MODE_DEVICE_LAYER_PAN, new DeviceLayerModePan (surface, this.model));
        final DeviceLayerModeSend modeLayerSend = new DeviceLayerModeSend (surface, this.model);
        modeManager.registerMode (Modes.MODE_DEVICE_LAYER_SEND1, modeLayerSend);
        modeManager.registerMode (Modes.MODE_DEVICE_LAYER_SEND2, modeLayerSend);
        modeManager.registerMode (Modes.MODE_DEVICE_LAYER_SEND3, modeLayerSend);
        modeManager.registerMode (Modes.MODE_DEVICE_LAYER_SEND4, modeLayerSend);
        modeManager.registerMode (Modes.MODE_DEVICE_LAYER_SEND5, modeLayerSend);
        modeManager.registerMode (Modes.MODE_DEVICE_LAYER_SEND6, modeLayerSend);
        modeManager.registerMode (Modes.MODE_DEVICE_LAYER_SEND7, modeLayerSend);
        modeManager.registerMode (Modes.MODE_DEVICE_LAYER_SEND8, modeLayerSend);

        if (this.isPush2)
        {
            modeManager.registerMode (Modes.MODE_SETUP, new SetupMode (surface, this.model));
            modeManager.registerMode (Modes.MODE_INFO, new InfoMode (surface, this.model));
        }
        else
            modeManager.registerMode (Modes.MODE_CONFIGURATION, new ConfigurationMode (surface, this.model));

        if (this.host.hasClips ())
        {
            modeManager.registerMode (Modes.MODE_SESSION, new SessionMode (surface, this.model));
            modeManager.registerMode (Modes.MODE_SESSION_VIEW_SELECT, new SessionViewSelectMode (surface, this.model));
        }

        if (this.host.hasRepeat ())
            modeManager.registerMode (Modes.MODE_REPEAT_NOTE, new NoteRepeatMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        final PushControlSurface surface = this.getSurface ();
        if (this.configuration.isPush2 ())
        {
            this.configuration.addSettingObserver (PushConfiguration.DISPLAY_BRIGHTNESS, surface::sendDisplayBrightness);
            this.configuration.addSettingObserver (PushConfiguration.LED_BRIGHTNESS, surface::sendLEDBrightness);
            this.configuration.addSettingObserver (PushConfiguration.PAD_SENSITIVITY, () -> {
                surface.sendPadVelocityCurve ();
                surface.sendPadThreshold ();
            });
            this.configuration.addSettingObserver (PushConfiguration.PAD_GAIN, () -> {
                surface.sendPadVelocityCurve ();
                surface.sendPadThreshold ();
            });
            this.configuration.addSettingObserver (PushConfiguration.PAD_DYNAMICS, () -> {
                surface.sendPadVelocityCurve ();
                surface.sendPadThreshold ();
            });
        }
        else
        {
            this.configuration.addSettingObserver (PushConfiguration.VELOCITY_CURVE, surface::sendPadSensitivity);
            this.configuration.addSettingObserver (PushConfiguration.PAD_THRESHOLD, surface::sendPadSensitivity);
        }

        surface.getModeManager ().addModeListener ( (oldMode, newMode) -> this.updateMode (newMode));
        surface.getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> this.onViewChange ());

        this.configuration.addSettingObserver (PushConfiguration.RIBBON_MODE, this::updateRibbonMode);
        this.configuration.addSettingObserver (PushConfiguration.DEBUG_MODE, () -> {
            final ModeManager modeManager = surface.getModeManager ();
            final Integer debugMode = this.configuration.getDebugMode ();
            if (modeManager.getMode (debugMode) != null)
                modeManager.setActiveMode (debugMode);
            else
                this.host.error ("Mode " + debugMode + " not registered.");
        });
        this.configuration.addSettingObserver (PushConfiguration.DEBUG_WINDOW, this.getSurface ().getDisplay ()::showDebugWindow);

        this.configuration.addSettingObserver (PushConfiguration.DISPLAY_SCENES_CLIPS, () -> {
            if (Views.isSessionView (this.getSurface ().getViewManager ().getActiveViewId ()))
            {
                final ModeManager modeManager = this.getSurface ().getModeManager ();
                if (modeManager.isActiveMode (Modes.MODE_SESSION))
                    modeManager.restoreMode ();
                else
                    modeManager.setActiveMode (Modes.MODE_SESSION);
            }
        });

        this.configuration.addSettingObserver (PushConfiguration.SESSION_VIEW, () -> {
            final ViewManager viewManager = this.getSurface ().getViewManager ();
            if (!Views.isSessionView (viewManager.getActiveViewId ()))
                return;
            if (this.configuration.isScenesClipViewSelected ())
                viewManager.setActiveView (Views.VIEW_SCENE_PLAY);
            else
                viewManager.setActiveView (Views.VIEW_SESSION);
        });

        this.configuration.addSettingObserver (AbstractConfiguration.KNOB_SPEED_NORMAL, () -> this.valueChanger.setFractionValue (this.configuration.getKnobSpeedNormal ()));
        this.configuration.addSettingObserver (AbstractConfiguration.KNOB_SPEED_SLOW, () -> this.valueChanger.setSlowFractionValue (this.configuration.getKnobSpeedSlow ()));

        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final PushControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.VIEW_PLAY, new PlayView (surface, this.model));
        viewManager.registerView (Views.VIEW_PIANO, new PianoView (surface, this.model));
        viewManager.registerView (Views.VIEW_PRG_CHANGE, new PrgChangeView (surface, this.model));
        viewManager.registerView (Views.VIEW_CLIP, new ClipView (surface, this.model));
        viewManager.registerView (Views.VIEW_COLOR, new ColorView (surface, this.model));

        if (this.host.hasClips ())
        {
            viewManager.registerView (Views.VIEW_SESSION, new SessionView (surface, this.model));
            viewManager.registerView (Views.VIEW_SEQUENCER, new SequencerView (surface, this.model));
            viewManager.registerView (Views.VIEW_POLY_SEQUENCER, new PolySequencerView (surface, this.model, true));
            viewManager.registerView (Views.VIEW_DRUM, new DrumView (surface, this.model));
            viewManager.registerView (Views.VIEW_DRUM4, new DrumView4 (surface, this.model));
            viewManager.registerView (Views.VIEW_DRUM8, new DrumView8 (surface, this.model));
            viewManager.registerView (Views.VIEW_RAINDROPS, new RaindropsView (surface, this.model));
            viewManager.registerView (Views.VIEW_SCENE_PLAY, new ScenePlayView (surface, this.model));
        }

        viewManager.registerView (Views.VIEW_DRUM64, new DrumView64 (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final PushControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        this.addTriggerCommand (Commands.COMMAND_PLAY, PushControlSurface.PUSH_BUTTON_PLAY, new PlayCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_RECORD, PushControlSurface.PUSH_BUTTON_RECORD, new RecordCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_NEW, PushControlSurface.PUSH_BUTTON_NEW, new NewCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DUPLICATE, PushControlSurface.PUSH_BUTTON_DUPLICATE, new DuplicateCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_AUTOMATION, PushControlSurface.PUSH_BUTTON_AUTOMATION, new AutomationCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FIXED_LENGTH, PushControlSurface.PUSH_BUTTON_FIXED_LENGTH, new FixedLengthCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_QUANTIZE, PushControlSurface.PUSH_BUTTON_QUANTIZE, new PushQuantizeCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DELETE, PushControlSurface.PUSH_BUTTON_DELETE, new DeleteCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DOUBLE, PushControlSurface.PUSH_BUTTON_DOUBLE, new DoubleCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_UNDO, PushControlSurface.PUSH_BUTTON_UNDO, new UndoCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DEVICE, PushControlSurface.PUSH_BUTTON_DEVICE, new DeviceCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_BROWSE, PushControlSurface.PUSH_BUTTON_BROWSE, new PushBrowserCommand (Modes.MODE_BROWSER, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_TRACK, PushControlSurface.PUSH_BUTTON_TRACK, new TrackCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_CLIP, PushControlSurface.PUSH_BUTTON_CLIP, new ClipCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_VOLUME, PushControlSurface.PUSH_BUTTON_VOLUME, new VolumeCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_PAN_SEND, PushControlSurface.PUSH_BUTTON_PAN_SEND, new PanSendCommand (this.model, surface));

        for (int i = 0; i < 8; i++)
        {
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW1_1.intValue () + i), PushControlSurface.PUSH_BUTTON_ROW1_1 + i, new ButtonRowModeCommand<> (0, i, this.model, surface));
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW2_1.intValue () + i), PushControlSurface.PUSH_BUTTON_ROW2_1 + i, new ButtonRowModeCommand<> (1, i, this.model, surface));
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_SCENE1.intValue () + i), PushControlSurface.PUSH_BUTTON_SCENE1 + i, new SceneCommand<> (i, this.model, surface));
        }

        this.addTriggerCommand (Commands.COMMAND_SHIFT, PushControlSurface.PUSH_BUTTON_SHIFT, new ShiftCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_LAYOUT, PushControlSurface.PUSH_BUTTON_LAYOUT, new LayoutCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_SELECT, PushControlSurface.PUSH_BUTTON_SELECT, new SelectCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_TAP_TEMPO, PushControlSurface.PUSH_BUTTON_TAP, new TapTempoCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_METRONOME, PushControlSurface.PUSH_BUTTON_METRONOME, new MetronomeCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_MASTERTRACK, PushControlSurface.PUSH_BUTTON_MASTER, new MastertrackCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_PAGE_LEFT, PushControlSurface.PUSH_BUTTON_DEVICE_LEFT, new PageLeftCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_PAGE_RIGHT, PushControlSurface.PUSH_BUTTON_DEVICE_RIGHT, new PageRightCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_MUTE, PushControlSurface.PUSH_BUTTON_MUTE, new MuteCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_SOLO, PushControlSurface.PUSH_BUTTON_SOLO, new SoloCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_SCALES, PushControlSurface.PUSH_BUTTON_SCALES, new ScalesCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ACCENT, PushControlSurface.PUSH_BUTTON_ACCENT, new AccentCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ADD_EFFECT, PushControlSurface.PUSH_BUTTON_ADD_EFFECT, new AddEffectCommand<> (Modes.MODE_BROWSER, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ADD_TRACK, PushControlSurface.PUSH_BUTTON_ADD_TRACK, new AddTrackCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_SELECT_PLAY_VIEW, PushControlSurface.PUSH_BUTTON_NOTE, new SelectPlayViewCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_DOWN, PushControlSurface.PUSH_BUTTON_DOWN, new CursorCommand<> (Direction.DOWN, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_UP, PushControlSurface.PUSH_BUTTON_UP, new CursorCommand<> (Direction.UP, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_LEFT, PushControlSurface.PUSH_BUTTON_LEFT, new CursorCommand<> (Direction.LEFT, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_RIGHT, PushControlSurface.PUSH_BUTTON_RIGHT, new CursorCommand<> (Direction.RIGHT, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_OCTAVE_DOWN, PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, new OctaveCommand (false, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_OCTAVE_UP, PushControlSurface.PUSH_BUTTON_OCTAVE_UP, new OctaveCommand (true, this.model, surface));

        viewManager.registerTriggerCommand (Commands.COMMAND_SETUP, new SetupCommand (this.isPush2, this.model, surface));
        if (this.isPush2)
        {
            surface.assignTriggerCommand (PushControlSurface.PUSH_BUTTON_SETUP, Commands.COMMAND_SETUP);
            this.addTriggerCommand (Commands.COMMAND_CONVERT, PushControlSurface.PUSH_BUTTON_CONVERT, new ConvertCommand<> (this.model, surface));
        }
        else
            surface.assignTriggerCommand (PushControlSurface.PUSH_BUTTON_USER_MODE, Commands.COMMAND_SETUP);

        if (this.host.hasClips ())
        {
            this.addTriggerCommand (Commands.COMMAND_STOP_CLIP, PushControlSurface.PUSH_BUTTON_CLIP_STOP, new StopAllClipsCommand<> (this.model, surface));
            this.addTriggerCommand (Commands.COMMAND_SELECT_SESSION_VIEW, PushControlSurface.PUSH_BUTTON_SESSION, new SelectSessionViewCommand (this.model, surface));
        }
        else
            this.addTriggerCommand (Commands.COMMAND_SELECT_SESSION_VIEW, PushControlSurface.PUSH_BUTTON_SESSION, new NopCommand<> (this.model, surface));

        final TriggerCommand repeatCommand = this.host.hasRepeat () ? new NoteRepeatCommand<> (this.model, surface) : new NopCommand<> (this.model, surface);
        this.addTriggerCommand (Commands.COMMAND_REPEAT, PushControlSurface.PUSH_BUTTON_REPEAT, repeatCommand);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final PushControlSurface surface = this.getSurface ();
        for (int i = 0; i < 8; i++)
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_KNOB1.intValue () + i), PushControlSurface.PUSH_KNOB1 + i, new KnobRowModeCommand<> (i, this.model, surface));

        this.addContinuousCommand (Commands.CONT_COMMAND_MASTER_KNOB, PushControlSurface.PUSH_KNOB9, new MasterVolumeCommand<> (this.model, surface));
        this.addContinuousCommand (Commands.CONT_COMMAND_TEMPO, PushControlSurface.PUSH_SMALL_KNOB1, new RasteredKnobCommand (this.model, surface));
        this.addContinuousCommand (Commands.CONT_COMMAND_PLAY_POSITION, PushControlSurface.PUSH_SMALL_KNOB2, new PlayPositionCommand<> (this.model, surface));
        this.addContinuousCommand (Commands.COMMAND_FOOTSWITCH, PushControlSurface.PUSH_FOOTSWITCH2, new FootswitchCommand<> (this.model, surface));

        this.addNoteCommand (Commands.CONT_COMMAND_KNOB1_TOUCH, PushControlSurface.PUSH_KNOB1_TOUCH, new KnobRowTouchModeCommand<> (0, this.model, surface));
        this.addNoteCommand (Commands.CONT_COMMAND_KNOB2_TOUCH, PushControlSurface.PUSH_KNOB2_TOUCH, new KnobRowTouchModeCommand<> (1, this.model, surface));
        this.addNoteCommand (Commands.CONT_COMMAND_KNOB3_TOUCH, PushControlSurface.PUSH_KNOB3_TOUCH, new KnobRowTouchModeCommand<> (2, this.model, surface));
        this.addNoteCommand (Commands.CONT_COMMAND_KNOB4_TOUCH, PushControlSurface.PUSH_KNOB4_TOUCH, new KnobRowTouchModeCommand<> (3, this.model, surface));
        this.addNoteCommand (Commands.CONT_COMMAND_KNOB5_TOUCH, PushControlSurface.PUSH_KNOB5_TOUCH, new KnobRowTouchModeCommand<> (4, this.model, surface));
        this.addNoteCommand (Commands.CONT_COMMAND_KNOB6_TOUCH, PushControlSurface.PUSH_KNOB6_TOUCH, new KnobRowTouchModeCommand<> (5, this.model, surface));
        this.addNoteCommand (Commands.CONT_COMMAND_KNOB7_TOUCH, PushControlSurface.PUSH_KNOB7_TOUCH, new KnobRowTouchModeCommand<> (6, this.model, surface));
        this.addNoteCommand (Commands.CONT_COMMAND_KNOB8_TOUCH, PushControlSurface.PUSH_KNOB8_TOUCH, new KnobRowTouchModeCommand<> (7, this.model, surface));
        this.addNoteCommand (Commands.CONT_COMMAND_TEMPO_TOUCH, PushControlSurface.PUSH_SMALL_KNOB1_TOUCH, new SmallKnobTouchCommand (this.model, surface, true));
        this.addNoteCommand (Commands.CONT_COMMAND_PLAYCURSOR_TOUCH, PushControlSurface.PUSH_SMALL_KNOB2_TOUCH, new SmallKnobTouchCommand (this.model, surface, false));
        this.addNoteCommand (Commands.CONT_COMMAND_CONFIGURE_PITCHBEND, PushControlSurface.PUSH_RIBBON_TOUCH, new ConfigurePitchbendCommand (this.model, surface));
        this.addNoteCommand (Commands.CONT_COMMAND_MASTERTRACK_TOUCH, PushControlSurface.PUSH_KNOB9_TOUCH, new MastertrackTouchCommand (this.model, surface));

        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerPitchbendCommand (new PitchbendCommand (this.model, surface));

        final PlayView playView = (PlayView) viewManager.getView (Views.VIEW_PLAY);
        playView.registerAftertouchCommand (new AftertouchAbstractPlayViewCommand<> (playView, this.model, surface));
        final PlayView pianoView = (PlayView) viewManager.getView (Views.VIEW_PIANO);
        pianoView.registerAftertouchCommand (new AftertouchAbstractPlayViewCommand<> (pianoView, this.model, surface));

        if (this.host.hasClips ())
        {
            viewManager.getView (Views.VIEW_SESSION).registerPitchbendCommand (new PitchbendSessionCommand (this.model, surface));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final PushControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActiveView (this.configuration.getDefaultNoteView ());

        surface.sendPressureMode (true);
        surface.getOutput ().sendSysex (DeviceInquiry.createQuery ());
    }


    /**
     * Called when a new view is selected.
     */
    private void onViewChange ()
    {
        final PushControlSurface surface = this.getSurface ();
        surface.updateButtons ();

        // Update ribbon mode
        if (surface.getViewManager ().isActiveView (Views.VIEW_SESSION))
            surface.setRibbonMode (PushControlSurface.PUSH_RIBBON_PAN);
        else
            this.updateRibbonMode ();

        this.updateIndication (null);
    }


    /**
     * Update all buttons, except the ones controlled by the views. Refreshed on flush.
     */
    private void updateButtons ()
    {
        final ITransport t = this.model.getTransport ();
        final PushControlSurface surface = this.getSurface ();
        surface.updateButton (PushControlSurface.PUSH_BUTTON_METRONOME, t.isMetronomeOn () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (PushControlSurface.PUSH_BUTTON_PLAY, t.isPlaying () ? PushColors.PUSH_BUTTON_STATE_PLAY_HI : PushColors.PUSH_BUTTON_STATE_PLAY_ON);

        final boolean isShift = surface.isShiftPressed ();
        final boolean isFlipRecord = this.configuration.isFlipRecord ();
        final boolean isRecordShifted = isShift && !isFlipRecord || !isShift && isFlipRecord;
        final boolean isWriting = isRecordShifted ? t.isWritingClipLauncherAutomation () : t.isWritingArrangerAutomation ();
        surface.updateButton (PushControlSurface.PUSH_BUTTON_AUTOMATION, isWriting ? PushColors.PUSH_BUTTON_STATE_REC_HI : PushColors.PUSH_BUTTON_STATE_REC_ON);
        surface.updateButton (PushControlSurface.PUSH_BUTTON_RECORD, isRecordShifted ? t.isLauncherOverdub () ? PushColors.PUSH_BUTTON_STATE_OVR_HI : PushColors.PUSH_BUTTON_STATE_OVR_ON : t.isRecording () ? PushColors.PUSH_BUTTON_STATE_REC_HI : PushColors.PUSH_BUTTON_STATE_REC_ON);

        String repeatState = ColorManager.BUTTON_STATE_OFF;
        if (this.host.hasRepeat ())
        {
            final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
            if (selectedTrack != null)
                repeatState = selectedTrack.isNoteRepeat () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON;
        }
        surface.updateButton (PushControlSurface.PUSH_BUTTON_REPEAT, repeatState);
        surface.updateButton (PushControlSurface.PUSH_BUTTON_ACCENT, this.configuration.isAccentActive () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);

        final PushConfiguration config = surface.getConfiguration ();
        if (this.isPush2)
        {
            final ModeManager modeManager = surface.getModeManager ();
            if (modeManager.isActiveOrTempMode (Modes.MODE_DEVICE_LAYER))
            {
                final ICursorDevice cd = this.model.getCursorDevice ();
                final IChannel layer = cd.getLayerOrDrumPadBank ().getSelectedItem ();
                surface.updateButton (PushControlSurface.PUSH_BUTTON_MUTE, layer != null && layer.isMute () ? PushColors.PUSH_BUTTON_STATE_MUTE_HI : PushColors.PUSH_BUTTON_STATE_MUTE_ON);
                surface.updateButton (PushControlSurface.PUSH_BUTTON_SOLO, layer != null && layer.isSolo () ? PushColors.PUSH_BUTTON_STATE_SOLO_HI : PushColors.PUSH_BUTTON_STATE_SOLO_ON);
            }
            else
            {
                final ITrack selTrack = modeManager.isActiveOrTempMode (Modes.MODE_MASTER) ? this.model.getMasterTrack () : this.model.getSelectedTrack ();
                surface.updateButton (PushControlSurface.PUSH_BUTTON_MUTE, selTrack != null && selTrack.isMute () ? PushColors.PUSH_BUTTON_STATE_MUTE_HI : PushColors.PUSH_BUTTON_STATE_MUTE_ON);
                surface.updateButton (PushControlSurface.PUSH_BUTTON_SOLO, selTrack != null && selTrack.isSolo () ? PushColors.PUSH_BUTTON_STATE_SOLO_HI : PushColors.PUSH_BUTTON_STATE_SOLO_ON);
            }

            surface.updateButton (PushControlSurface.PUSH_BUTTON_CONVERT, this.model.canConvertClip () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        }
        else
        {
            final boolean isMuteState = config.isMuteState ();
            surface.updateButton (PushControlSurface.PUSH_BUTTON_MUTE, isMuteState ? PushColors.PUSH_BUTTON_STATE_MUTE_HI : PushColors.PUSH_BUTTON_STATE_MUTE_ON);
            surface.updateButton (PushControlSurface.PUSH_BUTTON_SOLO, !isMuteState ? PushColors.PUSH_BUTTON_STATE_SOLO_HI : PushColors.PUSH_BUTTON_STATE_SOLO_ON);
        }

        final ViewManager viewManager = surface.getViewManager ();
        final boolean isSessionView = Views.isSessionView (viewManager.getActiveViewId ());
        surface.updateButton (PushControlSurface.PUSH_BUTTON_NOTE, isSessionView ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_HI);
        if (this.host.hasClips ())
        {
            surface.updateButton (PushControlSurface.PUSH_BUTTON_CLIP_STOP, surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP) ? PushColors.PUSH_BUTTON_STATE_STOP_HI : PushColors.PUSH_BUTTON_STATE_STOP_ON);
            surface.updateButton (PushControlSurface.PUSH_BUTTON_SESSION, isSessionView ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        }
        else
        {
            surface.updateButton (PushControlSurface.PUSH_BUTTON_CLIP_STOP, ColorManager.BUTTON_STATE_OFF);
            surface.updateButton (PushControlSurface.PUSH_BUTTON_SESSION, ColorManager.BUTTON_STATE_OFF);
        }

        surface.updateButton (PushControlSurface.PUSH_BUTTON_ACCENT, config.isAccentActive () ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);

        final View activeView = viewManager.getActiveView ();
        if (activeView != null)
        {
            ((CursorCommand<?, ?>) activeView.getTriggerCommand (Commands.COMMAND_ARROW_DOWN)).updateArrows ();
            ((SceneView) activeView).updateSceneButtons ();
        }

        final INoteClip clip = activeView instanceof AbstractSequencerView && !(activeView instanceof ClipView) ? ((AbstractSequencerView<?, ?>) activeView).getClip () : null;
        surface.updateButton (PushControlSurface.PUSH_BUTTON_DEVICE_LEFT, clip != null && clip.canScrollStepsBackwards () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        surface.updateButton (PushControlSurface.PUSH_BUTTON_DEVICE_RIGHT, clip != null && clip.canScrollStepsForwards () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
    }


    private void updateMode (final Integer mode)
    {
        if (mode == null)
            return;

        this.updateIndication (mode);

        final boolean isMasterOn = Modes.MODE_MASTER.equals (mode) || Modes.MODE_MASTER_TEMP.equals (mode) || Modes.MODE_FRAME.equals (mode);
        final boolean isVolumeOn = Modes.MODE_VOLUME.equals (mode) || Modes.MODE_CROSSFADER.equals (mode);
        final boolean isPanOn = mode.intValue () >= Modes.MODE_PAN.intValue () && mode.intValue () <= Modes.MODE_SEND8.intValue ();
        final boolean isDeviceOn = Modes.isDeviceMode (mode);
        boolean isMixOn = Modes.MODE_TRACK.equals (mode);
        if (this.isPush2)
            isMixOn = isMixOn || isVolumeOn || isPanOn;

        final PushControlSurface surface = this.getSurface ();
        surface.updateButton (PushControlSurface.PUSH_BUTTON_MASTER, isMasterOn ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (PushControlSurface.PUSH_BUTTON_TRACK, isMixOn ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (PushControlSurface.PUSH_BUTTON_VOLUME, isVolumeOn ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (PushControlSurface.PUSH_BUTTON_PAN_SEND, isPanOn ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (PushControlSurface.PUSH_BUTTON_DEVICE, isDeviceOn ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (PushControlSurface.PUSH_BUTTON_SCALES, Modes.MODE_SCALES.equals (mode) ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (PushControlSurface.PUSH_BUTTON_FIXED_LENGTH, Modes.MODE_FIXED.equals (mode) ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (PushControlSurface.PUSH_BUTTON_BROWSE, Modes.MODE_BROWSER.equals (mode) ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        surface.updateButton (PushControlSurface.PUSH_BUTTON_CLIP, Modes.MODE_CLIP.equals (mode) ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);

        if (this.isPush2)
            surface.updateButton (PushControlSurface.PUSH_BUTTON_SETUP, Modes.MODE_SETUP.equals (mode) ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Integer mode)
    {
        if (this.currentMode != null && this.currentMode.equals (mode))
            return;

        if (mode != null)
            this.currentMode = mode;

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final PushControlSurface surface = this.getSurface ();
        final boolean isSession = surface.getViewManager ().isActiveView (Views.VIEW_SESSION);
        final boolean isEffect = this.model.isEffectTrackBankActive ();
        final boolean isTrackMode = Modes.MODE_TRACK.equals (this.currentMode);
        final boolean isVolume = Modes.MODE_VOLUME.equals (this.currentMode);
        final boolean isPan = Modes.MODE_PAN.equals (this.currentMode);
        final boolean isDevice = Modes.isDeviceMode (this.currentMode) || Modes.isLayerMode (this.currentMode);

        tb.setIndication (!isEffect && isSession);
        if (tbe != null)
            tbe.setIndication (isEffect && isSession);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final ITrack selectedTrack = tb.getSelectedItem ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        for (int i = 0; i < tb.getPageSize (); i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i && isTrackMode;
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (!isEffect && (isVolume || hasTrackSel));
            track.setPanIndication (!isEffect && (isPan || hasTrackSel));

            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < sendBank.getPageSize (); j++)
                sendBank.getItem (j).setIndication (!isEffect && (this.currentMode.intValue () - Modes.MODE_SEND1.intValue () == j || hasTrackSel));

            if (tbe != null)
            {
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (isEffect);
                fxTrack.setPanIndication (isEffect && isPan);
            }

            parameterBank.getItem (i).setIndication (isDevice);
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

        final PushControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final ModeManager modeManager = surface.getModeManager ();

        // Recall last used view (if we are not in session mode)
        if (!viewManager.isActiveView (Views.VIEW_SESSION))
        {
            final ITrack selectedTrack = this.model.getSelectedTrack ();
            if (selectedTrack != null)
            {
                final Integer preferredView = viewManager.getPreferredView (selectedTrack.getPosition ());
                viewManager.setActiveView (preferredView == null ? this.configuration.getDefaultNoteView () : preferredView);
            }
        }

        if (modeManager.isActiveOrTempMode (Modes.MODE_MASTER))
            modeManager.setActiveMode (Modes.MODE_TRACK);

        if (viewManager.isActiveView (Views.VIEW_PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.resetDrumOctave ();
        if (viewManager.isActiveView (Views.VIEW_DRUM))
            viewManager.getView (Views.VIEW_DRUM).updateNoteMapping ();
    }


    private void updateRibbonMode ()
    {
        final PushControlSurface surface = this.getSurface ();
        surface.setRibbonValue (0);

        switch (this.configuration.getRibbonMode ())
        {
            case PushConfiguration.RIBBON_MODE_CC:
            case PushConfiguration.RIBBON_MODE_FADER:
                surface.setRibbonMode (PushControlSurface.PUSH_RIBBON_VOLUME);
                break;

            default:
                surface.setRibbonMode (PushControlSurface.PUSH_RIBBON_PITCHBEND);
                break;
        }
    }
}
