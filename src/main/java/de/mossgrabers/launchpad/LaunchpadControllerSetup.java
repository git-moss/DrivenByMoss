// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad;

import de.mossgrabers.framework.StringUtils;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.aftertouch.AftertouchAbstractPlayViewCommand;
import de.mossgrabers.framework.command.trigger.CursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.application.DeleteCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.launchpad.command.continuous.FaderCommand;
import de.mossgrabers.launchpad.command.trigger.ClickCommand;
import de.mossgrabers.launchpad.command.trigger.DoubleCommand;
import de.mossgrabers.launchpad.command.trigger.LPSceneCommand;
import de.mossgrabers.launchpad.command.trigger.LaunchpadCursorCommand;
import de.mossgrabers.launchpad.command.trigger.MuteCommand;
import de.mossgrabers.launchpad.command.trigger.PanCommand;
import de.mossgrabers.launchpad.command.trigger.RecordArmCommand;
import de.mossgrabers.launchpad.command.trigger.SelectDeviceViewCommand;
import de.mossgrabers.launchpad.command.trigger.SelectNoteViewCommand;
import de.mossgrabers.launchpad.command.trigger.SelectSessionViewCommand;
import de.mossgrabers.launchpad.command.trigger.SendsCommand;
import de.mossgrabers.launchpad.command.trigger.ShiftCommand;
import de.mossgrabers.launchpad.command.trigger.SoloCommand;
import de.mossgrabers.launchpad.command.trigger.StopClipCommand;
import de.mossgrabers.launchpad.command.trigger.TrackSelectCommand;
import de.mossgrabers.launchpad.command.trigger.VolumeCommand;
import de.mossgrabers.launchpad.controller.LaunchpadColors;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.launchpad.controller.LaunchpadScales;
import de.mossgrabers.launchpad.mode.Modes;
import de.mossgrabers.launchpad.mode.MuteMode;
import de.mossgrabers.launchpad.mode.PanMode;
import de.mossgrabers.launchpad.mode.RecArmMode;
import de.mossgrabers.launchpad.mode.SendMode;
import de.mossgrabers.launchpad.mode.SoloMode;
import de.mossgrabers.launchpad.mode.StopClipMode;
import de.mossgrabers.launchpad.mode.TrackMode;
import de.mossgrabers.launchpad.mode.VolumeMode;
import de.mossgrabers.launchpad.view.BrowserView;
import de.mossgrabers.launchpad.view.DeviceView;
import de.mossgrabers.launchpad.view.DrumView;
import de.mossgrabers.launchpad.view.DrumView4;
import de.mossgrabers.launchpad.view.DrumView64;
import de.mossgrabers.launchpad.view.DrumView8;
import de.mossgrabers.launchpad.view.PanView;
import de.mossgrabers.launchpad.view.PlayView;
import de.mossgrabers.launchpad.view.RaindropsView;
import de.mossgrabers.launchpad.view.SendsView;
import de.mossgrabers.launchpad.view.SequencerView;
import de.mossgrabers.launchpad.view.SessionView;
import de.mossgrabers.launchpad.view.ShiftView;
import de.mossgrabers.launchpad.view.Views;
import de.mossgrabers.launchpad.view.VolumeView;


/**
 * Bitwig Studio extension to support the Novation Launchpad Pro and Launchpad MkII controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadControllerSetup extends AbstractControllerSetup<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private final boolean isPro;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     * @param isPro True if Launchpad Pro
     */
    public LaunchpadControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings, final boolean isPro)
    {
        super (factory, host, settings);
        this.isPro = isPro;
        this.colorManager = new ColorManager ();
        LaunchpadColors.addColors (this.colorManager);
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new LaunchpadConfiguration (this.valueChanger, isPro);
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
        this.scales = new LaunchpadScales (this.valueChanger, 36, 100, 8, 8);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, 8, 8, 8, 16, 16, true, -1, -1, -1, -1);
        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.addTrackSelectionObserver (this::handleTrackChange);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput (this.isPro ? "Novation Launchpad Pro" : "Novation Launchpad MkII",
                "80????" /* Note off */, "90????" /* Note on */);
        final LaunchpadControlSurface surface = new LaunchpadControlSurface (this.model.getHost (), this.colorManager, this.configuration, output, input, this.isPro);
        this.surfaces.add (surface);
        surface.setDisplay (new DummyDisplay (this.host));
        surface.setLaunchpadToStandalone ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        this.getSurface ().getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> this.updateIndication ());
        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.registerMode (Modes.MODE_REC_ARM, new RecArmMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_TRACK_SELECT, new TrackMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_MUTE, new MuteMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_SOLO, new SoloMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_PAN, new PanMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_SENDS, new SendMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_STOP_CLIP, new StopClipMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.VIEW_BROWSER, new BrowserView (surface, this.model));
        viewManager.registerView (Views.VIEW_DEVICE, new DeviceView (surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM, new DrumView (surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM4, new DrumView4 (surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM8, new DrumView8 (surface, this.model));
        viewManager.registerView (Views.VIEW_PAN, new PanView (surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM64, new DrumView64 (surface, this.model));
        viewManager.registerView (Views.VIEW_PLAY, new PlayView (surface, this.model));
        viewManager.registerView (Views.VIEW_RAINDROPS, new RaindropsView (surface, this.model));
        viewManager.registerView (Views.VIEW_SENDS, new SendsView (surface, this.model));
        viewManager.registerView (Views.VIEW_SEQUENCER, new SequencerView (surface, this.model));
        viewManager.registerView (Views.VIEW_SESSION, new SessionView (surface, this.model));
        viewManager.registerView (Views.VIEW_VOLUME, new VolumeView (surface, this.model));
        viewManager.registerView (Views.VIEW_SHIFT, new ShiftView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerTriggerCommand (Commands.COMMAND_SHIFT, new ShiftCommand (this.model, surface));
        surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SHIFT, Commands.COMMAND_SHIFT);
        surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_MKII_BUTTON_USER, Commands.COMMAND_SHIFT);

        this.addTriggerCommand (Commands.COMMAND_METRONOME, LaunchpadControlSurface.LAUNCHPAD_BUTTON_CLICK, new ClickCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_UNDO, LaunchpadControlSurface.LAUNCHPAD_BUTTON_UNDO, new UndoCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DELETE, LaunchpadControlSurface.LAUNCHPAD_BUTTON_DELETE, new DeleteCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_QUANTIZE, LaunchpadControlSurface.LAUNCHPAD_BUTTON_QUANTIZE, new QuantizeCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DUPLICATE, LaunchpadControlSurface.LAUNCHPAD_BUTTON_DUPLICATE, new ToggleLoopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DOUBLE, LaunchpadControlSurface.LAUNCHPAD_BUTTON_DOUBLE, new DoubleCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_RECORD, LaunchpadControlSurface.LAUNCHPAD_BUTTON_RECORD, new RecordCommand<> (this.model, surface));

        viewManager.registerTriggerCommand (Commands.COMMAND_PLAY, new PlayCommand<> (this.model, surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_NEW, new NewCommand<> (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_SELECT_SESSION_VIEW, surface.getSessionButton (), new SelectSessionViewCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_SELECT_PLAY_VIEW, surface.getNoteButton (), new SelectNoteViewCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DEVICE, surface.getDeviceButton (), new SelectDeviceViewCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_REC_ARM, LaunchpadControlSurface.LAUNCHPAD_BUTTON_REC_ARM, new RecordArmCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_TRACK, LaunchpadControlSurface.LAUNCHPAD_BUTTON_TRACK, new TrackSelectCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_MUTE, LaunchpadControlSurface.LAUNCHPAD_BUTTON_MUTE, new MuteCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_SOLO, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SOLO, new SoloCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_VOLUME, LaunchpadControlSurface.LAUNCHPAD_BUTTON_VOLUME, new VolumeCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_PAN_SEND, LaunchpadControlSurface.LAUNCHPAD_BUTTON_PAN, new PanCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_SENDS, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SENDS, new SendsCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_STOP_CLIP, LaunchpadControlSurface.LAUNCHPAD_BUTTON_STOP_CLIP, new StopClipCommand (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_ARROW_DOWN, surface.getDownButtonId (), new LaunchpadCursorCommand (Direction.DOWN, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_UP, surface.getUpButtonId (), new LaunchpadCursorCommand (Direction.UP, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_LEFT, surface.getLeftButtonId (), new LaunchpadCursorCommand (Direction.LEFT, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_RIGHT, surface.getRightButtonId (), new LaunchpadCursorCommand (Direction.RIGHT, this.model, surface));

        if (this.isPro)
        {
            this.addTriggerCommand (Commands.COMMAND_SCENE1, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, new LPSceneCommand (7, this.model, surface));
            this.addTriggerCommand (Commands.COMMAND_SCENE2, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, new LPSceneCommand (6, this.model, surface));
            this.addTriggerCommand (Commands.COMMAND_SCENE3, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, new LPSceneCommand (5, this.model, surface));
            this.addTriggerCommand (Commands.COMMAND_SCENE4, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, new LPSceneCommand (4, this.model, surface));
            this.addTriggerCommand (Commands.COMMAND_SCENE5, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, new LPSceneCommand (3, this.model, surface));
            this.addTriggerCommand (Commands.COMMAND_SCENE6, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, new LPSceneCommand (2, this.model, surface));
            this.addTriggerCommand (Commands.COMMAND_SCENE7, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, new LPSceneCommand (1, this.model, surface));
            this.addTriggerCommand (Commands.COMMAND_SCENE8, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, new LPSceneCommand (0, this.model, surface));
        }
        else
        {
            this.addNoteCommand (Commands.COMMAND_SCENE1, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, new LPSceneCommand (7, this.model, surface));
            this.addNoteCommand (Commands.COMMAND_SCENE2, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, new LPSceneCommand (6, this.model, surface));
            this.addNoteCommand (Commands.COMMAND_SCENE3, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, new LPSceneCommand (5, this.model, surface));
            this.addNoteCommand (Commands.COMMAND_SCENE4, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, new LPSceneCommand (4, this.model, surface));
            this.addNoteCommand (Commands.COMMAND_SCENE5, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, new LPSceneCommand (3, this.model, surface));
            this.addNoteCommand (Commands.COMMAND_SCENE6, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, new LPSceneCommand (2, this.model, surface));
            this.addNoteCommand (Commands.COMMAND_SCENE7, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, new LPSceneCommand (1, this.model, surface));
            this.addNoteCommand (Commands.COMMAND_SCENE8, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, new LPSceneCommand (0, this.model, surface));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        for (int i = 0; i < 8; i++)
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_KNOB1.intValue () + i), LaunchpadControlSurface.LAUNCHPAD_FADER_1 + i, new FaderCommand (i, this.model, surface));
        final PlayView playView = (PlayView) surface.getViewManager ().getView (Views.VIEW_PLAY);
        playView.registerAftertouchCommand (new AftertouchAbstractPlayViewCommand<> (playView, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void startup ()
    {
        this.host.scheduleTask ( () -> this.getSurface ().getViewManager ().setActiveView (Views.VIEW_PLAY), 100);
    }


    private void updateButtons ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final View activeView = viewManager.getActiveView ();
        if (activeView != null)
        {
            ((LaunchpadCursorCommand) activeView.getTriggerCommand (Commands.COMMAND_ARROW_DOWN)).updateArrows ();
            ((SceneView) activeView).updateSceneButtons ();
        }

        if (!this.isPro)
        {
            surface.setButton (LaunchpadControlSurface.LAUNCHPAD_MKII_BUTTON_USER, surface.isUserPressed () ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
            return;
        }

        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_PRO_BUTTON_USER, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);

        final boolean isShift = surface.isShiftPressed ();
        final ITrack selTrack = this.model.getCurrentTrackBank ().getSelectedTrack ();
        final int index = selTrack == null ? -1 : selTrack.getIndex ();

        final ModeManager modeManager = surface.getModeManager ();

        final ITransport transport = this.model.getTransport ();

        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SHIFT, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_CLICK, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING : transport.isMetronomeOn () ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_UNDO, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DELETE, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_QUANTIZE, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DUPLICATE, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DOUBLE, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        final boolean flipRecord = surface.getConfiguration ().isFlipRecord ();
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_RECORD, isShift && !flipRecord || !isShift && flipRecord ? transport.isLauncherOverdub () ? LaunchpadColors.LAUNCHPAD_COLOR_ROSE : LaunchpadColors.LAUNCHPAD_COLOR_RED_AMBER : transport.isRecording () ? LaunchpadColors.LAUNCHPAD_COLOR_RED_HI : LaunchpadColors.LAUNCHPAD_COLOR_RED_LO);

        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_REC_ARM, modeManager.isActiveMode (Modes.MODE_REC_ARM) ? LaunchpadColors.LAUNCHPAD_COLOR_RED : index == 0 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_TRACK, modeManager.isActiveMode (Modes.MODE_TRACK_SELECT) ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN : index == 1 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_MUTE, modeManager.isActiveMode (Modes.MODE_MUTE) ? LaunchpadColors.LAUNCHPAD_COLOR_YELLOW : index == 2 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SOLO, modeManager.isActiveMode (Modes.MODE_SOLO) ? LaunchpadColors.LAUNCHPAD_COLOR_BLUE : index == 3 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_VOLUME, viewManager.isActiveView (Views.VIEW_VOLUME) ? LaunchpadColors.LAUNCHPAD_COLOR_CYAN : index == 4 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_PAN, viewManager.isActiveView (Views.VIEW_PAN) ? LaunchpadColors.LAUNCHPAD_COLOR_SKY : index == 5 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SENDS, viewManager.isActiveView (Views.VIEW_SENDS) ? LaunchpadColors.LAUNCHPAD_COLOR_ORCHID : index == 6 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_STOP_CLIP, modeManager.isActiveMode (Modes.MODE_STOP_CLIP) ? LaunchpadColors.LAUNCHPAD_COLOR_ROSE : index == 7 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);

        // Update the front LED with the color of the current track
        final ITrack track = index == -1 ? null : this.model.getCurrentTrackBank ().getTrack (index);
        final int color = track != null && track.doesExist () ? this.colorManager.getColor (BitwigColors.getColorIndex (track.getColor ())) : 0;
        surface.sendLaunchpadSysEx ("0A 63 " + StringUtils.toHexStr (color));
    }


    private void updateIndication ()
    {
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        final boolean isVolume = viewManager.isActiveView (Views.VIEW_VOLUME);
        final boolean isPan = viewManager.isActiveView (Views.VIEW_PAN);
        final boolean isSends = viewManager.isActiveView (Views.VIEW_SENDS);
        final boolean isDevice = viewManager.isActiveView (Views.VIEW_DEVICE);

        final ITrackBank tb = this.model.getTrackBank ();
        final IChannelBank tbe = this.model.getEffectTrackBank ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final View view = viewManager.getActiveView ();
        final int selSend = view instanceof SendsView ? ((SendsView) view).getSelectedSend () : -1;
        final boolean isSession = view instanceof SessionView && !isVolume && !isPan && !isSends;

        final boolean isEffect = this.model.isEffectTrackBankActive ();

        tb.setIndication (!isEffect && isSession);
        tbe.setIndication (isEffect && isSession);

        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getTrack (i);
            track.setVolumeIndication (!isEffect && isVolume);
            track.setPanIndication (!isEffect && isPan);
            for (int j = 0; j < 8; j++)
                track.getSend (j).setIndication (!isEffect && isSends && selSend == j);

            final ITrack fxTrack = tbe.getTrack (i);
            fxTrack.setVolumeIndication (isEffect && isVolume);
            fxTrack.setPanIndication (isEffect && isPan);

            cursorDevice.indicateParameter (i, isDevice);
        }
    }


    /**
     * Handle a track selection change.
     *
     * @param index The index of the track
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final int index, final boolean isSelected)
    {
        if (!isSelected)
            return;

        // Recall last used view (if we are not in session mode)
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        if (!viewManager.isActiveView (Views.VIEW_SESSION))
        {
            final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedTrack ();
            if (selectedTrack != null)
            {
                final Integer preferredView = viewManager.getPreferredView (selectedTrack.getPosition ());
                viewManager.setActiveView (preferredView == null ? Views.VIEW_PLAY : preferredView);
            }
        }

        if (viewManager.isActiveView (Views.VIEW_PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.setDrumOctave (0);
        if (viewManager.isActiveView (Views.VIEW_DRUM))
            viewManager.getView (Views.VIEW_DRUM).updateNoteMapping ();
    }
}
