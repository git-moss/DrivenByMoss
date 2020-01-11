// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push;

import de.mossgrabers.controller.push.command.continuous.ConfigurePitchbendCommand;
import de.mossgrabers.controller.push.command.continuous.MastertrackTouchCommand;
import de.mossgrabers.controller.push.command.pitchbend.TouchstripCommand;
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
import de.mossgrabers.controller.push.command.trigger.PlayPositionKnobCommand;
import de.mossgrabers.controller.push.command.trigger.PushBrowserCommand;
import de.mossgrabers.controller.push.command.trigger.PushCursorCommand;
import de.mossgrabers.controller.push.command.trigger.PushMetronomeCommand;
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
import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.Push2Display;
import de.mossgrabers.controller.push.controller.PushColorManager;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.AccentMode;
import de.mossgrabers.controller.push.mode.AutomationMode;
import de.mossgrabers.controller.push.mode.ConfigurationMode;
import de.mossgrabers.controller.push.mode.FixedMode;
import de.mossgrabers.controller.push.mode.FrameMode;
import de.mossgrabers.controller.push.mode.GrooveMode;
import de.mossgrabers.controller.push.mode.InfoMode;
import de.mossgrabers.controller.push.mode.MarkersMode;
import de.mossgrabers.controller.push.mode.MetronomeMode;
import de.mossgrabers.controller.push.mode.NoteMode;
import de.mossgrabers.controller.push.mode.NoteRepeatMode;
import de.mossgrabers.controller.push.mode.NoteViewSelectMode;
import de.mossgrabers.controller.push.mode.QuantizeMode;
import de.mossgrabers.controller.push.mode.RibbonMode;
import de.mossgrabers.controller.push.mode.ScaleLayoutMode;
import de.mossgrabers.controller.push.mode.ScalesMode;
import de.mossgrabers.controller.push.mode.SessionMode;
import de.mossgrabers.controller.push.mode.SessionViewSelectMode;
import de.mossgrabers.controller.push.mode.SetupMode;
import de.mossgrabers.controller.push.mode.device.DeviceBrowserMode;
import de.mossgrabers.controller.push.mode.device.DeviceChainsMode;
import de.mossgrabers.controller.push.mode.device.DeviceLayerMode;
import de.mossgrabers.controller.push.mode.device.DeviceLayerModePan;
import de.mossgrabers.controller.push.mode.device.DeviceLayerModeSend;
import de.mossgrabers.controller.push.mode.device.DeviceLayerModeVolume;
import de.mossgrabers.controller.push.mode.device.DeviceParamsMode;
import de.mossgrabers.controller.push.mode.device.UserParamsMode;
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
import de.mossgrabers.framework.command.aftertouch.AftertouchAbstractViewCommand;
import de.mossgrabers.framework.command.continuous.FootswitchCommand;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.continuous.MasterVolumeCommand;
import de.mossgrabers.framework.command.core.NopCommand;
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
import de.mossgrabers.framework.command.trigger.mode.KnobRowTouchModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.track.AddTrackCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.DefaultValueChanger;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISceneBank;
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
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.TransposeView;
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
    private final boolean isPush2;
    private ISceneBank    sceneBank64;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     * @param isPush2 True if Push 2
     */
    public PushControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings, final boolean isPush2)
    {
        super (factory, host, globalSettings, documentSettings);

        this.isPush2 = isPush2;
        this.colorManager = new PushColorManager (isPush2);
        this.valueChanger = new DefaultValueChanger (1024, 10, 1);
        this.configuration = new PushConfiguration (host, this.valueChanger, factory.getArpeggiatorModes (), isPush2);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        final PushControlSurface surface = this.getSurface ();
        this.updateMode (surface.getModeManager ().getActiveOrTempModeId ());

        final de.mossgrabers.framework.command.core.PitchbendCommand pitchbendCommand = surface.getContinuous (ContinuousID.TOUCHSTRIP).getPitchbendCommand ();
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
        this.sceneBank64 = this.model.createSceneBank (64);

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
                modeManager.setActiveMode (Modes.MASTER);
            else if (modeManager.isActiveOrTempMode (Modes.MASTER))
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
        final PushControlSurface surface = new PushControlSurface (this.host, this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);

        if (this.isPush2)
            surface.addGraphicsDisplay (new Push2Display (this.host, this.valueChanger.getUpperBound (), this.configuration));
        else
            surface.addTextDisplay (new Push1Display (this.host, this.valueChanger.getUpperBound (), output, this.configuration));

        surface.getModeManager ().setDefaultMode (Modes.TRACK);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final PushControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.registerMode (Modes.TRACK, new TrackMode (surface, this.model));
        modeManager.registerMode (Modes.TRACK_DETAILS, new TrackDetailsMode (surface, this.model));
        modeManager.registerMode (Modes.VOLUME, new VolumeMode (surface, this.model));
        modeManager.registerMode (Modes.PAN, new PanMode (surface, this.model));
        modeManager.registerMode (Modes.CROSSFADER, new CrossfaderMode (surface, this.model));

        final SendMode modeSend = new SendMode (surface, this.model);
        modeManager.registerMode (Modes.SEND1, modeSend);
        modeManager.registerMode (Modes.SEND2, modeSend);
        modeManager.registerMode (Modes.SEND3, modeSend);
        modeManager.registerMode (Modes.SEND4, modeSend);
        modeManager.registerMode (Modes.SEND5, modeSend);
        modeManager.registerMode (Modes.SEND6, modeSend);
        modeManager.registerMode (Modes.SEND7, modeSend);
        modeManager.registerMode (Modes.SEND8, modeSend);

        modeManager.registerMode (Modes.MASTER, new MasterMode (surface, this.model, false));
        modeManager.registerMode (Modes.MASTER_TEMP, new MasterMode (surface, this.model, true));

        modeManager.registerMode (Modes.DEVICE_PARAMS, new DeviceParamsMode (surface, this.model));
        modeManager.registerMode (Modes.DEVICE_CHAINS, new DeviceChainsMode (surface, this.model));
        modeManager.registerMode (Modes.DEVICE_LAYER, new DeviceLayerMode ("Layer", surface, this.model));
        modeManager.registerMode (Modes.DEVICE_LAYER_VOLUME, new DeviceLayerModeVolume (surface, this.model));
        modeManager.registerMode (Modes.DEVICE_LAYER_PAN, new DeviceLayerModePan (surface, this.model));
        final DeviceLayerModeSend modeLayerSend = new DeviceLayerModeSend (surface, this.model);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND1, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND2, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND3, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND4, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND5, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND6, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND7, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_SEND8, modeLayerSend);
        modeManager.registerMode (Modes.DEVICE_LAYER_DETAILS, new LayerDetailsMode (surface, this.model));
        modeManager.registerMode (Modes.BROWSER, new DeviceBrowserMode (surface, this.model));

        modeManager.registerMode (Modes.CLIP, new ClipMode (surface, this.model));
        modeManager.registerMode (Modes.NOTE, new NoteMode (surface, this.model));
        modeManager.registerMode (Modes.FRAME, new FrameMode (surface, this.model));

        modeManager.registerMode (Modes.GROOVE, new GrooveMode (surface, this.model));
        modeManager.registerMode (Modes.REC_ARM, new QuantizeMode (surface, this.model));
        modeManager.registerMode (Modes.ACCENT, new AccentMode (surface, this.model));

        modeManager.registerMode (Modes.SCALES, new ScalesMode (surface, this.model));
        modeManager.registerMode (Modes.SCALE_LAYOUT, new ScaleLayoutMode (surface, this.model));
        modeManager.registerMode (Modes.FIXED, new FixedMode (surface, this.model));
        modeManager.registerMode (Modes.RIBBON, new RibbonMode (surface, this.model));
        modeManager.registerMode (Modes.VIEW_SELECT, new NoteViewSelectMode (surface, this.model));

        modeManager.registerMode (Modes.AUTOMATION, new AutomationMode (surface, this.model));
        modeManager.registerMode (Modes.TRANSPORT, new MetronomeMode (surface, this.model));

        modeManager.registerMode (Modes.MARKERS, new MarkersMode (surface, this.model));

        if (this.host.hasUserParameters ())
            modeManager.registerMode (Modes.USER, new UserParamsMode (surface, this.model));

        if (this.isPush2)
        {
            modeManager.registerMode (Modes.SETUP, new SetupMode (surface, this.model));
            modeManager.registerMode (Modes.INFO, new InfoMode (surface, this.model));
        }
        else
            modeManager.registerMode (Modes.CONFIGURATION, new ConfigurationMode (surface, this.model));

        modeManager.registerMode (Modes.SESSION, new SessionMode (surface, this.model, this.sceneBank64));
        modeManager.registerMode (Modes.SESSION_VIEW_SELECT, new SessionViewSelectMode (surface, this.model));

        modeManager.registerMode (Modes.REPEAT_NOTE, new NoteRepeatMode (surface, this.model));
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
        this.configuration.addSettingObserver (PushConfiguration.RIBBON_MODE_NOTE_REPEAT, this::updateRibbonMode);
        this.configuration.addSettingObserver (AbstractConfiguration.NOTEREPEAT_ACTIVE, this::updateRibbonMode);
        this.configuration.addSettingObserver (PushConfiguration.DEBUG_MODE, () -> {
            final ModeManager modeManager = surface.getModeManager ();
            final Modes debugMode = this.configuration.getDebugMode ();
            if (modeManager.getMode (debugMode) != null)
                modeManager.setActiveMode (debugMode);
            else
                this.host.error ("Mode " + debugMode + " not registered.");
        });

        if (this.isPush2)
            this.configuration.addSettingObserver (PushConfiguration.DEBUG_WINDOW, this.getSurface ().getGraphicsDisplay ()::showDebugWindow);

        this.configuration.addSettingObserver (PushConfiguration.DISPLAY_SCENES_CLIPS, () -> {
            if (Views.isSessionView (this.getSurface ().getViewManager ().getActiveViewId ()))
            {
                final ModeManager modeManager = this.getSurface ().getModeManager ();
                if (modeManager.isActiveMode (Modes.SESSION))
                    modeManager.restoreMode ();
                else
                    modeManager.setActiveMode (Modes.SESSION);
            }
        });

        this.configuration.addSettingObserver (PushConfiguration.SESSION_VIEW, () -> {
            final ViewManager viewManager = this.getSurface ().getViewManager ();
            if (!Views.isSessionView (viewManager.getActiveViewId ()))
                return;
            if (this.configuration.isScenesClipViewSelected ())
                viewManager.setActiveView (Views.SCENE_PLAY);
            else
                viewManager.setActiveView (Views.SESSION);
        });

        this.configuration.addSettingObserver (AbstractConfiguration.KNOB_SPEED_NORMAL, () -> this.valueChanger.setFractionValue (this.configuration.getKnobSpeedNormal ()));
        this.configuration.addSettingObserver (AbstractConfiguration.KNOB_SPEED_SLOW, () -> this.valueChanger.setSlowFractionValue (this.configuration.getKnobSpeedSlow ()));

        this.createScaleObservers (this.configuration);
        this.createNoteRepeatObservers (this.configuration, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final PushControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.PLAY, new PlayView (surface, this.model));
        viewManager.registerView (Views.PIANO, new PianoView (surface, this.model));
        viewManager.registerView (Views.PRG_CHANGE, new PrgChangeView (surface, this.model));
        viewManager.registerView (Views.CLIP, new ClipView (surface, this.model));
        viewManager.registerView (Views.COLOR, new ColorView (surface, this.model));

        viewManager.registerView (Views.SESSION, new SessionView (surface, this.model));
        viewManager.registerView (Views.SEQUENCER, new SequencerView (surface, this.model));
        viewManager.registerView (Views.POLY_SEQUENCER, new PolySequencerView (surface, this.model, true));
        viewManager.registerView (Views.DRUM, new DrumView (surface, this.model));
        viewManager.registerView (Views.DRUM4, new DrumView4 (surface, this.model));
        viewManager.registerView (Views.DRUM8, new DrumView8 (surface, this.model));
        viewManager.registerView (Views.RAINDROPS, new RaindropsView (surface, this.model));
        viewManager.registerView (Views.SCENE_PLAY, new ScenePlayView (surface, this.model, this.sceneBank64));

        viewManager.registerView (Views.DRUM64, new DrumView64 (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final PushControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final ModeManager modeManager = surface.getModeManager ();

        final ITransport t = this.model.getTransport ();

        this.addButton (ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_PLAY, t::isPlaying, PushColorManager.PUSH_BUTTON_STATE_PLAY_ON, PushColorManager.PUSH_BUTTON_STATE_PLAY_HI);

        this.addButton (ButtonID.RECORD, "Record", new RecordCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_RECORD, () -> {

            if (this.isRecordShifted (surface))
                return t.isLauncherOverdub () ? 3 : 2;
            return t.isRecording () ? 1 : 0;

        }, PushColorManager.PUSH_BUTTON_STATE_REC_ON, PushColorManager.PUSH_BUTTON_STATE_REC_HI, PushColorManager.PUSH_BUTTON_STATE_OVR_ON, PushColorManager.PUSH_BUTTON_STATE_OVR_HI);

        this.addButton (ButtonID.NEW, "New", new NewCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_NEW);
        this.addButton (ButtonID.FIXED_LENGTH, "Fixed Length", new FixedLengthCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_FIXED_LENGTH, () -> modeManager.isActiveOrTempMode (Modes.FIXED));
        this.addButton (ButtonID.DUPLICATE, "Duplicate", new DuplicateCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_DUPLICATE);
        this.addButton (ButtonID.QUANTIZE, "Quantize", new PushQuantizeCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_QUANTIZE);
        this.addButton (ButtonID.DELETE, "Delete", new DeleteCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_DELETE);
        this.addButton (ButtonID.DOUBLE, "Double Loop", new DoubleCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_DOUBLE);
        this.addButton (ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_UNDO);

        this.addButton (ButtonID.AUTOMATION, "Automate", new AutomationCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_AUTOMATION, () -> {

            if (this.isRecordShifted (surface))
                return t.isWritingClipLauncherAutomation () ? 3 : 2;
            return t.isWritingArrangerAutomation () ? 1 : 0;

        }, PushColorManager.PUSH_BUTTON_STATE_REC_ON, PushColorManager.PUSH_BUTTON_STATE_REC_HI, PushColorManager.PUSH_BUTTON_STATE_OVR_ON, PushColorManager.PUSH_BUTTON_STATE_OVR_HI);

        this.addButton (ButtonID.TRACK, this.isPush2 ? "Mix" : "Track", new TrackCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_TRACK, () -> this.isPush2 ? Modes.isMixMode (modeManager.getActiveOrTempModeId ()) : modeManager.isActiveOrTempMode (Modes.TRACK));
        this.addButton (ButtonID.DEVICE, "Device", new DeviceCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_DEVICE, () -> Modes.isDeviceMode (modeManager.getActiveOrTempModeId ()));
        this.addButton (ButtonID.BROWSE, "Browse", new PushBrowserCommand (Modes.BROWSER, this.model, surface), PushControlSurface.PUSH_BUTTON_BROWSE, () -> modeManager.isActiveOrTempMode (Modes.BROWSER));
        this.addButton (ButtonID.CLIP, "Clip", new ClipCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_CLIP, () -> modeManager.isActiveOrTempMode (Modes.CLIP));

        for (int i = 0; i < 8; i++)
        {
            final ButtonID row1ButtonID = ButtonID.get (ButtonID.ROW1_1, i);
            this.addButton (row1ButtonID, "Row 1: " + (i + 1), new ButtonRowModeCommand<> (0, i, this.model, surface), PushControlSurface.PUSH_BUTTON_ROW1_1 + i, () -> this.getModeColor (row1ButtonID));
            final ButtonID row2ButtonID = ButtonID.get (ButtonID.ROW2_1, i);
            this.addButton (row2ButtonID, "Row 2: " + (i + 1), new ButtonRowModeCommand<> (1, i, this.model, surface), PushControlSurface.PUSH_BUTTON_ROW2_1 + i, () -> this.getModeColor (row2ButtonID));
            final ButtonID sceneButtonID = ButtonID.get (ButtonID.SCENE1, i);
            this.addButton (sceneButtonID, "Scene " + (i + 1), new ViewButtonCommand<> (sceneButtonID, this.model, surface), PushControlSurface.PUSH_BUTTON_SCENE1 + 7 - i, () -> this.getViewColor (sceneButtonID));
        }

        this.addButton (ButtonID.SHIFT, "Shift", new ShiftCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_SHIFT);
        this.addButton (ButtonID.SELECT, "Select", new SelectCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_SELECT);
        this.addButton (ButtonID.TAP_TEMPO, "Tap Tempo", new TapTempoCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_TAP);
        this.addButton (ButtonID.METRONOME, "Metronome", new PushMetronomeCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_METRONOME, t::isMetronomeOn);
        this.addButton (ButtonID.MASTERTRACK, "Mastertrack", new MastertrackCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_MASTER, () -> Modes.isMasterMode (modeManager.getActiveOrTempModeId ()));
        this.addButton (ButtonID.PAGE_LEFT, "Page Left", new PageLeftCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_DEVICE_LEFT, () -> {

            if (viewManager.isActiveView (Views.SESSION))
                return this.model.getCurrentTrackBank ().canScrollPageBackwards ();
            final View activeView = viewManager.getActiveView ();
            final INoteClip clip = activeView instanceof AbstractSequencerView && !(activeView instanceof ClipView) ? ((AbstractSequencerView<?, ?>) activeView).getClip () : null;
            return clip != null && clip.doesExist () && clip.canScrollStepsBackwards ();

        }, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        this.addButton (ButtonID.PAGE_RIGHT, "Page Right", new PageRightCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_DEVICE_RIGHT, () -> {

            if (viewManager.isActiveView (Views.SESSION))
                return this.model.getCurrentTrackBank ().canScrollPageForwards ();
            final View activeView = viewManager.getActiveView ();
            final INoteClip clip = activeView instanceof AbstractSequencerView && !(activeView instanceof ClipView) ? ((AbstractSequencerView<?, ?>) activeView).getClip () : null;
            return clip != null && clip.doesExist () && clip.canScrollStepsForwards ();

        }, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);

        this.addButton (ButtonID.MUTE, "Mute", new MuteCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_MUTE, this::getMuteState, PushColorManager.PUSH_BUTTON_STATE_MUTE_ON, PushColorManager.PUSH_BUTTON_STATE_MUTE_HI);
        this.addButton (ButtonID.SOLO, "Solo", new SoloCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_SOLO, this::getSoloState, PushColorManager.PUSH_BUTTON_STATE_SOLO_ON, PushColorManager.PUSH_BUTTON_STATE_SOLO_HI);
        this.addButton (ButtonID.SCALES, "Scale", new ScalesCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_SCALES, () -> modeManager.isActiveOrTempMode (Modes.SCALES));
        this.addButton (ButtonID.ACCENT, "Accent", new AccentCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_ACCENT, this.configuration::isAccentActive);
        this.addButton (ButtonID.ADD_EFFECT, "Add Device", new AddEffectCommand<> (Modes.BROWSER, this.model, surface), PushControlSurface.PUSH_BUTTON_ADD_EFFECT);
        this.addButton (ButtonID.ADD_TRACK, "Add Track", new AddTrackCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_ADD_TRACK);
        this.addButton (ButtonID.NOTE, "Note", new SelectPlayViewCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_NOTE, () -> !Views.isSessionView (viewManager.getActiveViewId ()));

        final PushCursorCommand cursorDownCommand = new PushCursorCommand (this.sceneBank64, Direction.DOWN, this.model, surface);
        this.addButton (ButtonID.ARROW_DOWN, "Down", cursorDownCommand, PushControlSurface.PUSH_BUTTON_DOWN, cursorDownCommand::canScroll, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        final PushCursorCommand cursorUpCommand = new PushCursorCommand (this.sceneBank64, Direction.UP, this.model, surface);
        this.addButton (ButtonID.ARROW_UP, "Up", cursorUpCommand, PushControlSurface.PUSH_BUTTON_UP, cursorUpCommand::canScroll, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        final PushCursorCommand cursorLeftCommand = new PushCursorCommand (this.sceneBank64, Direction.LEFT, this.model, surface);
        this.addButton (ButtonID.ARROW_LEFT, "Left", cursorLeftCommand, PushControlSurface.PUSH_BUTTON_LEFT, cursorLeftCommand::canScroll, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        final PushCursorCommand cursorRightCommand = new PushCursorCommand (this.sceneBank64, Direction.RIGHT, this.model, surface);
        this.addButton (ButtonID.ARROW_RIGHT, "Right", cursorRightCommand, PushControlSurface.PUSH_BUTTON_RIGHT, cursorRightCommand::canScroll, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);

        this.addButton (ButtonID.OCTAVE_DOWN, "Octave Down", new OctaveCommand (false, this.model, surface), PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, () -> {
            final View activeView = viewManager.getActiveView ();
            return activeView instanceof TransposeView && ((TransposeView) activeView).isOctaveDownButtonOn ();
        }, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);
        this.addButton (ButtonID.OCTAVE_UP, "Octave Up", new OctaveCommand (true, this.model, surface), PushControlSurface.PUSH_BUTTON_OCTAVE_UP, () -> {
            final View activeView = viewManager.getActiveView ();
            return activeView instanceof TransposeView && ((TransposeView) activeView).isOctaveUpButtonOn ();
        }, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON);

        if (this.isPush2)
        {
            this.addButton (ButtonID.LAYOUT, "Layout", new LayoutCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_LAYOUT);
            this.addButton (ButtonID.SETUP, "Setup", new SetupCommand (this.isPush2, this.model, surface), PushControlSurface.PUSH_BUTTON_SETUP, () -> modeManager.isActiveOrTempMode (Modes.SETUP));
            this.addButton (ButtonID.CONVERT, "Convert", new ConvertCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_CONVERT, () -> {
                if (!this.model.canConvertClip ())
                    return 0;
                return surface.getButton (ButtonID.CONVERT).isPressed () ? 2 : 1;
            }, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);
            this.addButton (ButtonID.USER, "User", this.host.hasUserParameters () ? new ModeSelectCommand<> (this.model, surface, Modes.USER) : NopCommand.INSTANCE, PushControlSurface.PUSH_BUTTON_USER_MODE, () -> this.host.hasUserParameters () && modeManager.isActiveOrTempMode (Modes.USER));
        }
        else
        {
            this.addButton (ButtonID.VOLUME, "Volume", new VolumeCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_VOLUME, () -> modeManager.isActiveOrTempMode (Modes.VOLUME, Modes.CROSSFADER));
            this.addButton (ButtonID.PAN_SEND, "Pan/Send", new PanSendCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_PAN_SEND, () -> modeManager.isActiveOrTempMode (Modes.PAN) || Modes.isSendMode (modeManager.getActiveOrTempModeId ()));
            this.addButton (ButtonID.SETUP, "User", new SetupCommand (this.isPush2, this.model, surface), PushControlSurface.PUSH_BUTTON_USER_MODE, () -> modeManager.isActiveOrTempMode (Modes.SETUP));
        }

        this.addButton (ButtonID.STOP_CLIP, "Stop Clip", new StopAllClipsCommand<> (this.model, surface), PushControlSurface.PUSH_BUTTON_STOP_CLIP, () -> surface.isPressed (ButtonID.STOP_CLIP), PushColorManager.PUSH_BUTTON_STATE_STOP_ON, PushColorManager.PUSH_BUTTON_STATE_STOP_HI);
        this.addButton (ButtonID.SESSION, "Session", new SelectSessionViewCommand (this.model, surface), PushControlSurface.PUSH_BUTTON_SESSION, () -> Views.isSessionView (viewManager.getActiveViewId ()));
        this.addButton (ButtonID.REPEAT, "Repeat", new NoteRepeatCommand<> (this.model, surface, true), PushControlSurface.PUSH_BUTTON_REPEAT, this.configuration::isNoteRepeatActive);
        this.addButton (ButtonID.FOOTSWITCH2, "Foot Controller", new FootswitchCommand<> (this.model, surface), PushControlSurface.PUSH_FOOTSWITCH2);
    }


    /** {@inheritDoc} */
    @SuppressWarnings(
    {
        "rawtypes",
        "unchecked"
    })
    @Override
    protected void registerContinuousCommands ()
    {
        final PushControlSurface surface = this.getSurface ();
        final IMidiInput input = surface.getMidiInput ();

        for (int i = 0; i < 8; i++)
        {
            final IHwRelativeKnob knob = this.addRelativeKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + i, new KnobRowModeCommand<> (i, this.model, surface), PushControlSurface.PUSH_KNOB1 + i);
            knob.bindTouch (new KnobRowTouchModeCommand<> (i, this.model, surface), input, BindType.NOTE, PushControlSurface.PUSH_KNOB1_TOUCH + i);
        }

        final IHwRelativeKnob knobMaster = this.addRelativeKnob (ContinuousID.MASTER_KNOB, "Master", new MasterVolumeCommand<> (this.model, surface), PushControlSurface.PUSH_KNOB9);
        knobMaster.bindTouch (new MastertrackTouchCommand (this.model, surface), input, BindType.NOTE, PushControlSurface.PUSH_KNOB9_TOUCH);

        final RasteredKnobCommand tempoCommand = new RasteredKnobCommand (this.model, surface);
        final IHwRelativeKnob knobTempo = this.addRelativeKnob (ContinuousID.TEMPO, "Tempo", tempoCommand, PushControlSurface.PUSH_SMALL_KNOB1);
        knobTempo.bindTouch (tempoCommand, input, BindType.NOTE, PushControlSurface.PUSH_SMALL_KNOB1_TOUCH);

        final PlayPositionKnobCommand playPositionCommand = new PlayPositionKnobCommand (this.model, surface);
        final IHwRelativeKnob knobPlayPosition = this.addRelativeKnob (ContinuousID.PLAY_POSITION, "Play Position", playPositionCommand, PushControlSurface.PUSH_SMALL_KNOB2);
        knobPlayPosition.bindTouch (playPositionCommand, input, BindType.NOTE, PushControlSurface.PUSH_SMALL_KNOB2_TOUCH);

        final ViewManager viewManager = surface.getViewManager ();

        final Views [] views =
        {
            Views.PLAY,
            Views.PIANO,
            Views.DRUM,
            Views.DRUM64
        };
        for (final Views viewID: views)
        {
            final AbstractView view = (AbstractView) viewManager.getView (viewID);
            view.registerAftertouchCommand (new AftertouchAbstractViewCommand<> (view, this.model, surface));
        }

        final IHwFader touchstrip = this.addFader (ContinuousID.TOUCHSTRIP, "Touchstrip", new TouchstripCommand (this.model, surface));
        touchstrip.bindTouch (new ConfigurePitchbendCommand (this.model, surface), input, BindType.NOTE, PushControlSurface.PUSH_RIBBON_TOUCH);
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final PushControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (33.25, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD2).setBounds (48.75, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD3).setBounds (64.5, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD4).setBounds (80.0, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD5).setBounds (95.5, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD6).setBounds (110.75, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD7).setBounds (126.75, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD8).setBounds (142.75, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD9).setBounds (33.25, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD10).setBounds (48.75, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD11).setBounds (64.5, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD12).setBounds (80.0, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD13).setBounds (95.5, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD14).setBounds (110.75, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD15).setBounds (126.75, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD16).setBounds (142.75, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD17).setBounds (33.25, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD18).setBounds (48.75, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD19).setBounds (64.5, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD20).setBounds (80.0, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD21).setBounds (95.5, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD22).setBounds (110.75, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD23).setBounds (126.75, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD24).setBounds (142.75, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD25).setBounds (33.25, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD26).setBounds (48.75, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD27).setBounds (64.5, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD28).setBounds (80.0, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD29).setBounds (95.5, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD30).setBounds (110.75, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD31).setBounds (126.75, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD32).setBounds (142.75, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD33).setBounds (33.25, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD34).setBounds (48.75, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD35).setBounds (64.5, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD36).setBounds (80.0, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD37).setBounds (95.5, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD38).setBounds (110.75, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD39).setBounds (126.75, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD40).setBounds (142.75, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD41).setBounds (33.25, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD42).setBounds (48.75, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD43).setBounds (64.5, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD44).setBounds (80.0, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD45).setBounds (95.5, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD46).setBounds (110.75, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD47).setBounds (126.75, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD48).setBounds (142.75, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD49).setBounds (33.25, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD50).setBounds (48.75, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD51).setBounds (64.5, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD52).setBounds (80.0, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD53).setBounds (95.5, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD54).setBounds (110.75, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD55).setBounds (126.75, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD56).setBounds (142.75, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD57).setBounds (33.25, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD58).setBounds (48.75, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD59).setBounds (64.5, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD60).setBounds (80.0, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD61).setBounds (95.5, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD62).setBounds (110.75, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD63).setBounds (126.75, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD64).setBounds (142.75, 59.25, 12.75, 10.0);

        if (this.isPush2)
        {
            surface.getGraphicsDisplay ().getHardwareDisplay ().setBounds (32.75, 28.0, 123.5, 21.0);

            surface.getButton (ButtonID.PLAY).setBounds (4.75, 142.75, 10.0, 8.5);
            surface.getButton (ButtonID.RECORD).setBounds (4.75, 132.25, 10.0, 8.5);
            surface.getButton (ButtonID.NEW).setBounds (4.75, 121.75, 10.0, 8.5);
            surface.getButton (ButtonID.FIXED_LENGTH).setBounds (4.75, 90.25, 10.0, 8.5);
            surface.getButton (ButtonID.DUPLICATE).setBounds (4.75, 111.25, 10.0, 8.5);
            surface.getButton (ButtonID.QUANTIZE).setBounds (4.75, 79.75, 10.0, 8.5);
            surface.getButton (ButtonID.DELETE).setBounds (4.5, 28.25, 10.0, 10.0);
            surface.getButton (ButtonID.DOUBLE).setBounds (4.75, 69.25, 10.0, 8.5);
            surface.getButton (ButtonID.UNDO).setBounds (4.5, 39.5, 10.0, 10.0);
            surface.getButton (ButtonID.AUTOMATION).setBounds (4.75, 100.75, 10.0, 8.5);
            surface.getButton (ButtonID.TRACK).setBounds (185.5, 30.0, 10.0, 8.75);
            surface.getButton (ButtonID.DEVICE).setBounds (173.5, 30.0, 10.0, 8.75);
            surface.getButton (ButtonID.BROWSE).setBounds (173.5, 40.75, 10.0, 8.75);
            surface.getButton (ButtonID.CLIP).setBounds (185.5, 40.75, 10.0, 8.75);

            surface.getButton (ButtonID.ROW1_1).setBounds (33.5, 51.25, 13.0, 5.5);
            surface.getButton (ButtonID.ROW2_1).setBounds (34.0, 20.25, 13.0, 5.5);
            surface.getButton (ButtonID.SCENE1).setBounds (159.75, 59.25, 10.0, 10.0);
            surface.getButton (ButtonID.ROW1_2).setBounds (49.0, 51.25, 13.0, 5.5);
            surface.getButton (ButtonID.ROW2_2).setBounds (49.5, 20.25, 13.0, 5.5);
            surface.getButton (ButtonID.SCENE2).setBounds (159.75, 70.75, 10.0, 10.0);
            surface.getButton (ButtonID.ROW1_3).setBounds (64.75, 51.25, 13.0, 5.5);
            surface.getButton (ButtonID.ROW2_3).setBounds (65.25, 20.25, 13.0, 5.5);
            surface.getButton (ButtonID.SCENE3).setBounds (159.75, 82.0, 10.0, 10.0);
            surface.getButton (ButtonID.ROW1_4).setBounds (80.25, 51.25, 13.0, 5.5);
            surface.getButton (ButtonID.ROW2_4).setBounds (80.75, 20.25, 13.0, 5.5);
            surface.getButton (ButtonID.SCENE4).setBounds (159.75, 93.75, 10.0, 10.0);
            surface.getButton (ButtonID.ROW1_5).setBounds (95.75, 51.25, 13.0, 5.5);
            surface.getButton (ButtonID.ROW2_5).setBounds (96.25, 20.25, 13.0, 5.5);
            surface.getButton (ButtonID.SCENE5).setBounds (159.75, 105.75, 10.0, 10.0);
            surface.getButton (ButtonID.ROW1_6).setBounds (111.25, 51.25, 13.0, 5.5);
            surface.getButton (ButtonID.ROW2_6).setBounds (111.75, 20.25, 13.0, 5.5);
            surface.getButton (ButtonID.SCENE6).setBounds (159.75, 118.25, 10.0, 10.0);
            surface.getButton (ButtonID.ROW1_7).setBounds (127.0, 51.25, 13.0, 5.5);
            surface.getButton (ButtonID.ROW2_7).setBounds (127.5, 20.25, 13.0, 5.5);
            surface.getButton (ButtonID.SCENE7).setBounds (159.75, 130.0, 10.0, 10.0);
            surface.getButton (ButtonID.ROW1_8).setBounds (142.5, 51.25, 13.0, 5.5);
            surface.getButton (ButtonID.ROW2_8).setBounds (143.0, 20.25, 13.0, 5.5);
            surface.getButton (ButtonID.SCENE8).setBounds (159.75, 141.75, 10.0, 10.0);

            surface.getButton (ButtonID.SHIFT).setBounds (173.5, 145.0, 10.0, 6.0);
            surface.getButton (ButtonID.SELECT).setBounds (185.5, 145.0, 10.0, 6.0);
            surface.getButton (ButtonID.TAP_TEMPO).setBounds (4.5, 20.25, 11.25, 5.5);
            surface.getButton (ButtonID.METRONOME).setBounds (17.5, 20.25, 11.25, 5.5);
            surface.getButton (ButtonID.MASTERTRACK).setBounds (159.75, 51.5, 10.0, 5.0);
            surface.getButton (ButtonID.PAGE_LEFT).setBounds (173.0, 131.0, 10.0, 6.25);
            surface.getButton (ButtonID.PAGE_RIGHT).setBounds (185.75, 131.0, 10.0, 6.25);
            surface.getButton (ButtonID.MUTE).setBounds (4.5, 51.0, 8.25, 5.5);
            surface.getButton (ButtonID.SOLO).setBounds (12.5, 51.0, 8.25, 5.5);
            surface.getButton (ButtonID.SCALES).setBounds (173.5, 105.75, 10.0, 6.25);
            surface.getButton (ButtonID.ACCENT).setBounds (185.5, 93.75, 10.0, 6.25);
            surface.getButton (ButtonID.ADD_EFFECT).setBounds (160.0, 30.0, 10.0, 8.75);
            surface.getButton (ButtonID.ADD_TRACK).setBounds (160.0, 40.75, 10.0, 8.75);
            surface.getButton (ButtonID.NOTE).setBounds (173.5, 113.75, 10.0, 6.25);
            surface.getButton (ButtonID.ARROW_DOWN).setBounds (181.5, 61.75, 6.0, 9.25);
            surface.getButton (ButtonID.ARROW_UP).setBounds (181.5, 51.75, 6.0, 9.25);
            surface.getButton (ButtonID.ARROW_LEFT).setBounds (173.5, 59.0, 7.25, 5.75);
            surface.getButton (ButtonID.ARROW_RIGHT).setBounds (187.75, 58.75, 7.25, 5.75);
            surface.getButton (ButtonID.OCTAVE_DOWN).setBounds (180.25, 137.25, 10.0, 6.25);
            surface.getButton (ButtonID.OCTAVE_UP).setBounds (179.75, 124.5, 10.0, 6.25);
            surface.getButton (ButtonID.LAYOUT).setBounds (185.5, 105.75, 10.0, 6.25);
            surface.getButton (ButtonID.SETUP).setBounds (173.5, 20.75, 10.0, 6.25);
            surface.getButton (ButtonID.STOP_CLIP).setBounds (21.0, 51.0, 8.25, 5.5);
            surface.getButton (ButtonID.SESSION).setBounds (185.5, 113.75, 10.0, 6.25);
            surface.getButton (ButtonID.REPEAT).setBounds (173.5, 93.75, 10.0, 6.25);
            surface.getButton (ButtonID.CONVERT).setBounds (4.75, 58.75, 10.0, 8.5);
            surface.getButton (ButtonID.USER).setBounds (185.5, 20.5, 10.0, 6.25);
            surface.getButton (ButtonID.FOOTSWITCH2).setBounds (160.0, 1.0, 12.0, 8.25);

            surface.getContinuous (ContinuousID.KNOB1).setBounds (34.75, 5.75, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB2).setBounds (50.25, 5.75, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB3).setBounds (65.75, 5.75, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB4).setBounds (81.25, 5.75, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB5).setBounds (96.75, 5.75, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB6).setBounds (112.25, 5.75, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB7).setBounds (127.75, 5.75, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB8).setBounds (143.25, 5.75, 10.0, 10.0);
            surface.getContinuous (ContinuousID.MASTER_KNOB).setBounds (180.0, 5.75, 10.0, 10.0);

            surface.getContinuous (ContinuousID.TEMPO).setBounds (4.0, 5.75, 10.0, 10.0);
            surface.getContinuous (ContinuousID.PLAY_POSITION).setBounds (17.75, 5.75, 10.0, 10.0);
            surface.getContinuous (ContinuousID.TOUCHSTRIP).setBounds (17.75, 58.5, 12.0, 93.0);
        }
        else
        {
            surface.getTextDisplay ().getHardwareDisplay ().setBounds (32.5, 23.25, 123.75, 17.0);

            surface.getButton (ButtonID.SETUP).setBounds (185.5, 58.0, 10.0, 6.25);
            surface.getButton (ButtonID.ACCENT).setBounds (185.38487435513716, 65.78321712343, 10.0, 6.366389099167296);
            surface.getButton (ButtonID.ADD_EFFECT).setBounds (174.33869721660602, 93.94370160488351, 10.0, 5.912187736563206);
            surface.getButton (ButtonID.ADD_TRACK).setBounds (185.38487435513716, 93.94370160488351, 10.0, 5.912187736563206);
            surface.getButton (ButtonID.ARROW_DOWN).setBounds (182.32355717118574, 135.12462514765426, 5.912187736563204, 9.242997728993183);
            surface.getButton (ButtonID.ARROW_LEFT).setBounds (174.33869721660602, 132.24801651782818, 7.274791824375465, 5.760787282361847);
            surface.getButton (ButtonID.ARROW_RIGHT).setBounds (188.60970402962622, 132.0966160636268, 7.274791824375465, 5.760787282361847);
            surface.getButton (ButtonID.ARROW_UP).setBounds (182.32355717118574, 124.9807947161627, 5.912187736563204, 9.242997728993183);
            surface.getButton (ButtonID.AUTOMATION).setBounds (3.9828477685945423, 101.75094041477499, 10.0, 6.0);
            surface.getButton (ButtonID.BROWSE).setBounds (185.38487435513716, 32.631962620823586, 10.0, 6.0);
            surface.getButton (ButtonID.CLIP).setBounds (185.38487435513716, 24.796891814839974, 10.0, 6.0);
            surface.getButton (ButtonID.DELETE).setBounds (3.9828477685945423, 65.86767055416846, 10.0, 6.0);
            surface.getButton (ButtonID.DEVICE).setBounds (174.11462454438796, 32.631962620823586, 10.0, 6.0);
            surface.getButton (ButtonID.DOUBLE).setBounds (3.9828477685945423, 73.94073976052265, 10.0, 6.0);
            surface.getButton (ButtonID.DUPLICATE).setBounds (3.9828477685945423, 108.95374891266061, 10.0, 6.0);
            surface.getButton (ButtonID.FIXED_LENGTH).setBounds (3.9828477685945423, 93.76485835884236, 10.0, 6.0);
            surface.getButton (ButtonID.MASTERTRACK).setBounds (159.75883347701458, 44.09358805454295, 10.0, 5.003785011355033);
            surface.getButton (ButtonID.METRONOME).setBounds (3.9828477685945423, 33.49555626044754, 10.0, 5.003785011355033);
            surface.getButton (ButtonID.MUTE).setBounds (174.11462454438796, 50.945972611696554, 10.0, 6.366389099167296);
            surface.getButton (ButtonID.NEW).setBounds (3.9828477685945423, 117.27727105546369, 10.0, 10.0);
            surface.getButton (ButtonID.NOTE).setBounds (174.33869721660602, 101.96792567755568, 10.0, 5.912187736563206);
            surface.getButton (ButtonID.OCTAVE_DOWN).setBounds (174.11462454438796, 73.35323983349807, 10.0, 6.366389099167296);
            surface.getButton (ButtonID.OCTAVE_UP).setBounds (185.38487435513716, 73.35323983349807, 10.0, 6.366389099167296);
            surface.getButton (ButtonID.PAGE_LEFT).setBounds (174.11462454438796, 44.09358805454292, 10.0, 6.366389099167296);
            surface.getButton (ButtonID.PAGE_RIGHT).setBounds (185.38487435513716, 44.09358805454292, 10.0, 6.366389099167296);
            surface.getButton (ButtonID.PAN_SEND).setBounds (185.38487435513716, 17.09278823980427, 10.0, 6.0);
            surface.getButton (ButtonID.PLAY).setBounds (3.9828477685945423, 141.5252797892068, 10.0, 10.0);
            surface.getButton (ButtonID.QUANTIZE).setBounds (3.9828477685945423, 81.7016671564154, 10.0, 6.0);
            surface.getButton (ButtonID.RECORD).setBounds (3.9828477685945423, 129.39864829363117, 10.0, 10.0);
            surface.getButton (ButtonID.REPEAT).setBounds (174.11462454438796, 65.78321712343, 10.0, 6.366389099167296);
            surface.getButton (ButtonID.ROW1_1).setBounds (32.57639592970192, 43.44862211964502, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW1_2).setBounds (48.17669873061028, 43.44862211964502, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW1_3).setBounds (63.73763741342633, 43.44862211964502, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW1_4).setBounds (79.10781152394877, 43.44862211964502, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW1_5).setBounds (95.76791750426686, 43.44862211964502, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW1_6).setBounds (110.68389025218505, 43.44862211964502, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW1_7).setBounds (126.22060486232871, 43.44862211964502, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW1_8).setBounds (141.97230811743864, 43.44862211964502, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW2_1).setBounds (32.57639592970192, 51.6242466465187, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW2_2).setBounds (48.17669873061028, 51.6242466465187, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW2_3).setBounds (63.73763741342633, 51.6242466465187, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW2_4).setBounds (79.10781152394877, 51.6242466465187, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW2_5).setBounds (95.76791750426686, 51.6242466465187, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW2_6).setBounds (110.68389025218505, 51.6242466465187, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW2_7).setBounds (126.22060486232871, 51.6242466465187, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.ROW2_8).setBounds (141.97230811743864, 51.6242466465187, 13.633610900832712, 5.457986373959121);
            surface.getButton (ButtonID.SCALES).setBounds (174.11462454438796, 58.0617939591608, 10.0, 6.366389099167296);
            surface.getButton (ButtonID.SCENE1).setBounds (159.75883347701458, 57.98306572297601, 10.0, 10.0);
            surface.getButton (ButtonID.SCENE2).setBounds (159.75883347701458, 70.24650251328632, 10.0, 10.0);
            surface.getButton (ButtonID.SCENE3).setBounds (159.75883347701458, 82.05573794099264, 10.0, 10.0);
            surface.getButton (ButtonID.SCENE4).setBounds (159.75883347701458, 93.5621724602962, 10.0, 10.0);
            surface.getButton (ButtonID.SCENE5).setBounds (159.75883347701458, 105.67420879640525, 10.0, 10.0);
            surface.getButton (ButtonID.SCENE6).setBounds (159.75883347701458, 118.12841015900918, 10.0, 10.0);
            surface.getButton (ButtonID.SCENE7).setBounds (159.75883347701458, 129.93764558671543, 10.0, 10.0);
            surface.getButton (ButtonID.SCENE8).setBounds (159.75883347701458, 141.74688101442194, 10.0, 10.0);
            surface.getButton (ButtonID.SELECT).setBounds (174.33869721660602, 109.80138517793415, 10.0, 5.912187736563206);
            surface.getButton (ButtonID.SESSION).setBounds (185.38487435513716, 101.96792567755568, 10.0, 5.912187736563206);
            surface.getButton (ButtonID.SHIFT).setBounds (185.38487435513716, 109.80138517793415, 10.0, 5.912187736563206);
            surface.getButton (ButtonID.SOLO).setBounds (185.38487435513716, 50.945972611696554, 10.0, 6.366389099167296);
            surface.getButton (ButtonID.STOP_CLIP).setBounds (159.75883347701458, 51.2487735200992, 10.0, 5.003785011355033);
            surface.getButton (ButtonID.TAP_TEMPO).setBounds (3.9828477685945423, 21.837721286942664, 10.0, 10.0);
            surface.getButton (ButtonID.TRACK).setBounds (174.11462454438796, 24.796891814839974, 10.0, 6.0);
            surface.getButton (ButtonID.UNDO).setBounds (3.9828477685945423, 58.423360817640344, 10.0, 6.0);
            surface.getButton (ButtonID.VOLUME).setBounds (174.11462454438796, 17.09278823980427, 10.0, 6.0);
            surface.getButton (ButtonID.FOOTSWITCH2).setBounds (4.0, 6.0, 10.0, 10.0);

            surface.getContinuous (ContinuousID.KNOB1).setBounds (34.771069269783915, 5.655526992287918, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB2).setBounds (49.71638991176253, 5.655526992287918, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB3).setBounds (65.9326061279787, 5.655526992287918, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB4).setBounds (81.17584937122217, 5.655526992287918, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB5).setBounds (97.9326061279789, 5.655526992287918, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB6).setBounds (112.04752310150776, 5.655526992287918, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB7).setBounds (128.21593830334197, 5.655526992287918, 10.0, 10.0);
            surface.getContinuous (ContinuousID.KNOB8).setBounds (143.5578406169669, 5.655526992287918, 10.0, 10.0);
            surface.getContinuous (ContinuousID.MASTER_KNOB).setBounds (159.75, 5.75, 10.0, 10.0);

            surface.getContinuous (ContinuousID.TEMPO).setBounds (4.0, 43.5, 10.0, 10.0);
            surface.getContinuous (ContinuousID.PLAY_POSITION).setBounds (17.75, 43.5, 10.0, 10.0);
            surface.getContinuous (ContinuousID.TOUCHSTRIP).setBounds (17.75, 58.5, 12.0, 93.0);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final PushControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActiveView (this.configuration.getDefaultNoteView ());

        surface.sendPressureMode (true);
        surface.getMidiOutput ().sendSysex (DeviceInquiry.createQuery ());

        if (this.isPush2)
            surface.updateColorPalette ();
    }


    /**
     * Called when a new view is selected.
     */
    private void onViewChange ()
    {
        final PushControlSurface surface = this.getSurface ();

        // Update ribbon mode
        if (surface.getViewManager ().isActiveView (Views.SESSION))
            surface.setRibbonMode (PushControlSurface.PUSH_RIBBON_PAN);
        else
            this.updateRibbonMode ();

        this.updateIndication (null);
    }


    private void updateMode (final Modes mode)
    {
        if (mode != null)
            this.updateIndication (mode);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Modes mode)
    {
        if (this.currentMode != null && this.currentMode == mode)
            return;

        if (mode != null)
            this.currentMode = mode;

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final PushControlSurface surface = this.getSurface ();
        final boolean isSession = surface.getViewManager ().isActiveView (Views.SESSION);
        final boolean isEffect = this.model.isEffectTrackBankActive ();
        final boolean isTrackMode = Modes.TRACK == this.currentMode;
        final boolean isVolume = Modes.VOLUME == this.currentMode;
        final boolean isPan = Modes.PAN == this.currentMode;
        final boolean isDevice = Modes.isDeviceMode (this.currentMode) || Modes.isLayerMode (this.currentMode);
        final boolean isUserMode = Modes.USER == this.currentMode;

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
                sendBank.getItem (j).setIndication (!isEffect && (this.currentMode.ordinal () - Modes.SEND1.ordinal () == j || hasTrackSel));

            if (tbe != null)
            {
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (isEffect);
                fxTrack.setPanIndication (isEffect && isPan);
            }

            parameterBank.getItem (i).setIndication (isDevice);
        }

        if (this.host.hasUserParameters ())
        {
            final IParameterBank userParameterBank = this.model.getUserParameterBank ();
            for (int i = 0; i < userParameterBank.getPageSize (); i++)
                userParameterBank.getItem (i).setIndication (isUserMode);
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
        if (!viewManager.isActiveView (Views.SESSION))
        {
            final ITrack selectedTrack = this.model.getSelectedTrack ();
            if (selectedTrack != null)
            {
                final Views preferredView = viewManager.getPreferredView (selectedTrack.getPosition ());
                viewManager.setActiveView (preferredView == null ? this.configuration.getDefaultNoteView () : preferredView);
            }
        }

        if (modeManager.isActiveOrTempMode (Modes.MASTER))
            modeManager.setActiveMode (Modes.TRACK);

        if (viewManager.isActiveView (Views.PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.resetDrumOctave ();
        if (viewManager.isActiveView (Views.DRUM))
            viewManager.getView (Views.DRUM).updateNoteMapping ();
    }


    private void updateRibbonMode ()
    {
        final PushControlSurface surface = this.getSurface ();
        surface.setRibbonValue (0);

        final int ribbonNoteRepeat = this.configuration.getRibbonNoteRepeat ();
        if (this.configuration.isNoteRepeatActive () && ribbonNoteRepeat > PushConfiguration.NOTE_REPEAT_OFF)
        {
            surface.setRibbonMode (PushControlSurface.PUSH_RIBBON_DISCRETE);
            return;
        }

        final int ribbonMode = this.configuration.getRibbonMode ();
        if (ribbonMode == PushConfiguration.RIBBON_MODE_CC || ribbonMode == PushConfiguration.RIBBON_MODE_FADER)
            surface.setRibbonMode (PushControlSurface.PUSH_RIBBON_VOLUME);
        else
            surface.setRibbonMode (PushControlSurface.PUSH_RIBBON_PITCHBEND);
    }


    private boolean getMuteState ()
    {
        final PushControlSurface surface = this.getSurface ();
        if (this.isPush2)
        {
            final ModeManager modeManager = surface.getModeManager ();
            if (modeManager.isActiveOrTempMode (Modes.DEVICE_LAYER))
            {
                final ICursorDevice cd = this.model.getCursorDevice ();
                final IChannel layer = cd.getLayerOrDrumPadBank ().getSelectedItem ();
                return layer != null && layer.isMute ();
            }
            final ITrack selTrack = modeManager.isActiveOrTempMode (Modes.MASTER) ? this.model.getMasterTrack () : this.model.getSelectedTrack ();
            return selTrack != null && selTrack.isMute ();
        }
        return surface.getConfiguration ().isMuteState ();
    }


    private boolean getSoloState ()
    {
        final PushControlSurface surface = this.getSurface ();
        if (this.isPush2)
        {
            final ModeManager modeManager = surface.getModeManager ();
            if (modeManager.isActiveOrTempMode (Modes.DEVICE_LAYER))
            {
                final ICursorDevice cd = this.model.getCursorDevice ();
                final IChannel layer = cd.getLayerOrDrumPadBank ().getSelectedItem ();
                return layer != null && layer.isSolo ();
            }
            final ITrack selTrack = modeManager.isActiveOrTempMode (Modes.MASTER) ? this.model.getMasterTrack () : this.model.getSelectedTrack ();
            return selTrack != null && selTrack.isSolo ();
        }
        return surface.getConfiguration ().isSoloState ();
    }
}
