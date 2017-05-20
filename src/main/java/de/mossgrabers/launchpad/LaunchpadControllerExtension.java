// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.SceneCommand;
import de.mossgrabers.framework.command.aftertouch.AftertouchAbstractPlayViewCommand;
import de.mossgrabers.framework.command.trigger.CursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.DeleteCommand;
import de.mossgrabers.framework.command.trigger.NewCommand;
import de.mossgrabers.framework.command.trigger.PlayCommand;
import de.mossgrabers.framework.command.trigger.RecordCommand;
import de.mossgrabers.framework.command.trigger.UndoCommand;
import de.mossgrabers.framework.controller.AbstractControllerExtension;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.EffectTrackBankProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.TransportProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.midi.MidiInput;
import de.mossgrabers.framework.midi.MidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.launchpad.command.continuous.FaderCommand;
import de.mossgrabers.launchpad.command.trigger.ClickCommand;
import de.mossgrabers.launchpad.command.trigger.DoubleCommand;
import de.mossgrabers.launchpad.command.trigger.DuplicateCommand;
import de.mossgrabers.launchpad.command.trigger.LaunchpadCursorCommand;
import de.mossgrabers.launchpad.command.trigger.MuteCommand;
import de.mossgrabers.launchpad.command.trigger.PanCommand;
import de.mossgrabers.launchpad.command.trigger.QuantizeCommand;
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
import de.mossgrabers.launchpad.controller.LaunchpadMidiInput;
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

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Bitwig Studio extension to support the Novation Launchpad Pro and Launchpad MkII controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadControllerExtension extends AbstractControllerExtension<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private final boolean isPro;


    /**
     * Constructor.
     *
     * @param extensionDefinition The extension definition
     * @param host The Bitwig host
     * @param isPro True if Launchpad Pro
     */
    protected LaunchpadControllerExtension (final LaunchpadControllerExtensionDefinition extensionDefinition, final ControllerHost host, final boolean isPro)
    {
        super (extensionDefinition, host);
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
        this.surface.flush ();
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
        this.model = new Model (this.getHost (), this.colorManager, this.valueChanger, this.scales, 8, 8, 8, 16, 16, true, -1, -1, -1, -1);

        final TrackBankProxy trackBank = this.model.getTrackBank ();
        trackBank.addTrackSelectionObserver (this::handleTrackChange);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final ControllerHost host = this.getHost ();
        final MidiOutput output = new MidiOutput (host);
        final MidiInput input = new LaunchpadMidiInput (this.isPro);
        this.surface = new LaunchpadControlSurface (host, this.colorManager, this.configuration, output, input, this.isPro);
        this.surface.setDisplay (new DummyDisplay (host));
        this.surface.setLaunchpadToStandalone ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        this.surface.getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> this.updateIndication ());
        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.registerMode (Modes.MODE_REC_ARM, new RecArmMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_TRACK_SELECT, new TrackMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_MUTE, new MuteMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_SOLO, new SoloMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_PAN, new PanMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_SENDS, new SendMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_STOP_CLIP, new StopClipMode (this.surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        viewManager.registerView (Views.VIEW_BROWSER, new BrowserView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_DEVICE, new DeviceView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM, new DrumView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM4, new DrumView4 (this.surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM8, new DrumView8 (this.surface, this.model));
        viewManager.registerView (Views.VIEW_PAN, new PanView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM64, new DrumView64 (this.surface, this.model));
        viewManager.registerView (Views.VIEW_PLAY, new PlayView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_RAINDROPS, new RaindropsView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_SENDS, new SendsView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_SEQUENCER, new SequencerView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_SESSION, new SessionView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_VOLUME, new VolumeView (this.surface, this.model));

        if (!this.isPro)
            viewManager.registerView (Views.VIEW_SHIFT, new ShiftView (this.surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        viewManager.registerTriggerCommand (Commands.COMMAND_SHIFT, new ShiftCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_METRONOME, new ClickCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_UNDO, new UndoCommand<> (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_DELETE, new DeleteCommand<> (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_QUANTIZE, new QuantizeCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_DUPLICATE, new DuplicateCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_DOUBLE, new DoubleCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_RECORD, new RecordCommand<> (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_PLAY, new PlayCommand<> (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_NEW, new NewCommand<> (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_SELECT_SESSION_VIEW, new SelectSessionViewCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_SELECT_PLAY_VIEW, new SelectNoteViewCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_DEVICE, new SelectDeviceViewCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_REC_ARM, new RecordArmCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_TRACK, new TrackSelectCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_MUTE, new MuteCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_SOLO, new SoloCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_VOLUME, new VolumeCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_PAN_SEND, new PanCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_SENDS, new SendsCommand (this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_STOP_CLIP, new StopClipCommand (this.model, this.surface));

        viewManager.registerTriggerCommand (Commands.COMMAND_ARROW_DOWN, new LaunchpadCursorCommand (Direction.DOWN, this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_ARROW_UP, new LaunchpadCursorCommand (Direction.UP, this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_ARROW_LEFT, new LaunchpadCursorCommand (Direction.LEFT, this.model, this.surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_ARROW_RIGHT, new LaunchpadCursorCommand (Direction.RIGHT, this.model, this.surface));

        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SHIFT, Commands.COMMAND_SHIFT);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_MKII_BUTTON_USER, Commands.COMMAND_SHIFT);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_CLICK, Commands.COMMAND_METRONOME);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_UNDO, Commands.COMMAND_UNDO);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DELETE, Commands.COMMAND_DELETE);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_QUANTIZE, Commands.COMMAND_QUANTIZE);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DUPLICATE, Commands.COMMAND_DUPLICATE);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DOUBLE, Commands.COMMAND_DOUBLE);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_RECORD, Commands.COMMAND_RECORD);
        this.surface.assignTriggerCommand (this.surface.getSessionButton (), Commands.COMMAND_SELECT_SESSION_VIEW);
        this.surface.assignTriggerCommand (this.surface.getNoteButton (), Commands.COMMAND_SELECT_PLAY_VIEW);
        this.surface.assignTriggerCommand (this.surface.getDeviceButton (), Commands.COMMAND_DEVICE);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_REC_ARM, Commands.COMMAND_REC_ARM);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_TRACK, Commands.COMMAND_TRACK);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_MUTE, Commands.COMMAND_MUTE);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SOLO, Commands.COMMAND_SOLO);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_VOLUME, Commands.COMMAND_VOLUME);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_PAN, Commands.COMMAND_PAN_SEND);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SENDS, Commands.COMMAND_SENDS);
        this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_STOP_CLIP, Commands.COMMAND_STOP_CLIP);

        this.surface.assignTriggerCommand (this.surface.getDownButtonId (), Commands.COMMAND_ARROW_DOWN);
        this.surface.assignTriggerCommand (this.surface.getUpButtonId (), Commands.COMMAND_ARROW_UP);
        this.surface.assignTriggerCommand (this.surface.getLeftButtonId (), Commands.COMMAND_ARROW_LEFT);
        this.surface.assignTriggerCommand (this.surface.getRightButtonId (), Commands.COMMAND_ARROW_RIGHT);

        if (this.isPro)
        {
            viewManager.registerTriggerCommand (Commands.COMMAND_SCENE1, new SceneCommand<> (7, this.model, this.surface));
            viewManager.registerTriggerCommand (Commands.COMMAND_SCENE2, new SceneCommand<> (6, this.model, this.surface));
            viewManager.registerTriggerCommand (Commands.COMMAND_SCENE3, new SceneCommand<> (5, this.model, this.surface));
            viewManager.registerTriggerCommand (Commands.COMMAND_SCENE4, new SceneCommand<> (4, this.model, this.surface));
            viewManager.registerTriggerCommand (Commands.COMMAND_SCENE5, new SceneCommand<> (3, this.model, this.surface));
            viewManager.registerTriggerCommand (Commands.COMMAND_SCENE6, new SceneCommand<> (2, this.model, this.surface));
            viewManager.registerTriggerCommand (Commands.COMMAND_SCENE7, new SceneCommand<> (1, this.model, this.surface));
            viewManager.registerTriggerCommand (Commands.COMMAND_SCENE8, new SceneCommand<> (0, this.model, this.surface));

            this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, Commands.COMMAND_SCENE1);
            this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, Commands.COMMAND_SCENE2);
            this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, Commands.COMMAND_SCENE3);
            this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, Commands.COMMAND_SCENE4);
            this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, Commands.COMMAND_SCENE5);
            this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, Commands.COMMAND_SCENE6);
            this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, Commands.COMMAND_SCENE7);
            this.surface.assignTriggerCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, Commands.COMMAND_SCENE8);
        }
        else
        {
            viewManager.registerNoteCommand (Commands.COMMAND_SCENE1, new SceneCommand<> (7, this.model, this.surface));
            viewManager.registerNoteCommand (Commands.COMMAND_SCENE2, new SceneCommand<> (6, this.model, this.surface));
            viewManager.registerNoteCommand (Commands.COMMAND_SCENE3, new SceneCommand<> (5, this.model, this.surface));
            viewManager.registerNoteCommand (Commands.COMMAND_SCENE4, new SceneCommand<> (4, this.model, this.surface));
            viewManager.registerNoteCommand (Commands.COMMAND_SCENE5, new SceneCommand<> (3, this.model, this.surface));
            viewManager.registerNoteCommand (Commands.COMMAND_SCENE6, new SceneCommand<> (2, this.model, this.surface));
            viewManager.registerNoteCommand (Commands.COMMAND_SCENE7, new SceneCommand<> (1, this.model, this.surface));
            viewManager.registerNoteCommand (Commands.COMMAND_SCENE8, new SceneCommand<> (0, this.model, this.surface));

            this.surface.assignNoteCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, Commands.COMMAND_SCENE1);
            this.surface.assignNoteCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, Commands.COMMAND_SCENE2);
            this.surface.assignNoteCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, Commands.COMMAND_SCENE3);
            this.surface.assignNoteCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, Commands.COMMAND_SCENE4);
            this.surface.assignNoteCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, Commands.COMMAND_SCENE5);
            this.surface.assignNoteCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, Commands.COMMAND_SCENE6);
            this.surface.assignNoteCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, Commands.COMMAND_SCENE7);
            this.surface.assignNoteCommand (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, Commands.COMMAND_SCENE8);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        viewManager.registerContinuousCommand (Commands.CONT_COMMAND_KNOB1, new FaderCommand (0, this.model, this.surface));
        viewManager.registerContinuousCommand (Commands.CONT_COMMAND_KNOB2, new FaderCommand (1, this.model, this.surface));
        viewManager.registerContinuousCommand (Commands.CONT_COMMAND_KNOB3, new FaderCommand (2, this.model, this.surface));
        viewManager.registerContinuousCommand (Commands.CONT_COMMAND_KNOB4, new FaderCommand (3, this.model, this.surface));
        viewManager.registerContinuousCommand (Commands.CONT_COMMAND_KNOB5, new FaderCommand (4, this.model, this.surface));
        viewManager.registerContinuousCommand (Commands.CONT_COMMAND_KNOB6, new FaderCommand (5, this.model, this.surface));
        viewManager.registerContinuousCommand (Commands.CONT_COMMAND_KNOB7, new FaderCommand (6, this.model, this.surface));
        viewManager.registerContinuousCommand (Commands.CONT_COMMAND_KNOB8, new FaderCommand (7, this.model, this.surface));

        this.surface.assignContinuousCommand (LaunchpadControlSurface.LAUNCHPAD_FADER_1, Commands.CONT_COMMAND_KNOB1);
        this.surface.assignContinuousCommand (LaunchpadControlSurface.LAUNCHPAD_FADER_2, Commands.CONT_COMMAND_KNOB2);
        this.surface.assignContinuousCommand (LaunchpadControlSurface.LAUNCHPAD_FADER_3, Commands.CONT_COMMAND_KNOB3);
        this.surface.assignContinuousCommand (LaunchpadControlSurface.LAUNCHPAD_FADER_4, Commands.CONT_COMMAND_KNOB4);
        this.surface.assignContinuousCommand (LaunchpadControlSurface.LAUNCHPAD_FADER_5, Commands.CONT_COMMAND_KNOB5);
        this.surface.assignContinuousCommand (LaunchpadControlSurface.LAUNCHPAD_FADER_6, Commands.CONT_COMMAND_KNOB6);
        this.surface.assignContinuousCommand (LaunchpadControlSurface.LAUNCHPAD_FADER_7, Commands.CONT_COMMAND_KNOB7);
        this.surface.assignContinuousCommand (LaunchpadControlSurface.LAUNCHPAD_FADER_8, Commands.CONT_COMMAND_KNOB8);

        final PlayView playView = (PlayView) viewManager.getView (Views.VIEW_PLAY);
        playView.registerAftertouchCommand (new AftertouchAbstractPlayViewCommand<> (playView, this.model, this.surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void startup ()
    {
        this.getHost ().scheduleTask ( () -> this.surface.getViewManager ().setActiveView (Views.VIEW_PLAY), 100);
    }


    private void updateButtons ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        final View activeView = viewManager.getActiveView ();
        if (activeView != null)
        {
            ((LaunchpadCursorCommand) activeView.getTriggerCommand (Commands.COMMAND_ARROW_DOWN)).updateArrows ();
            ((SceneView) activeView).updateSceneButtons ();
        }

        if (!this.isPro)
        {
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_MKII_BUTTON_USER, this.surface.isUserPressed () ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
            return;
        }

        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_PRO_BUTTON_USER, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);

        final boolean isShift = this.surface.isShiftPressed ();
        final TrackData selTrack = this.model.getCurrentTrackBank ().getSelectedTrack ();
        final int index = selTrack == null ? -1 : selTrack.getIndex ();

        final ModeManager modeManager = this.surface.getModeManager ();

        final TransportProxy transport = this.model.getTransport ();

        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SHIFT, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_CLICK, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING : transport.isMetronomeOn () ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_UNDO, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DELETE, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_QUANTIZE, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DUPLICATE, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_DOUBLE, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        final boolean flipRecord = this.surface.getConfiguration ().isFlipRecord ();
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_RECORD, isShift && !flipRecord || !isShift && flipRecord ? transport.isLauncherOverdub () ? LaunchpadColors.LAUNCHPAD_COLOR_ROSE : LaunchpadColors.LAUNCHPAD_COLOR_RED_AMBER : transport.isRecording () ? LaunchpadColors.LAUNCHPAD_COLOR_RED_HI : LaunchpadColors.LAUNCHPAD_COLOR_RED_LO);

        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_REC_ARM, modeManager.isActiveMode (Modes.MODE_REC_ARM) ? LaunchpadColors.LAUNCHPAD_COLOR_RED : index == 0 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_TRACK, modeManager.isActiveMode (Modes.MODE_TRACK_SELECT) ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN : index == 1 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_MUTE, modeManager.isActiveMode (Modes.MODE_MUTE) ? LaunchpadColors.LAUNCHPAD_COLOR_YELLOW : index == 2 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SOLO, modeManager.isActiveMode (Modes.MODE_SOLO) ? LaunchpadColors.LAUNCHPAD_COLOR_BLUE : index == 3 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_VOLUME, viewManager.isActiveView (Views.VIEW_VOLUME) ? LaunchpadColors.LAUNCHPAD_COLOR_CYAN : index == 4 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_PAN, viewManager.isActiveView (Views.VIEW_PAN) ? LaunchpadColors.LAUNCHPAD_COLOR_SKY : index == 5 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SENDS, viewManager.isActiveView (Views.VIEW_SENDS) ? LaunchpadColors.LAUNCHPAD_COLOR_ORCHID : index == 6 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_STOP_CLIP, modeManager.isActiveMode (Modes.MODE_STOP_CLIP) ? LaunchpadColors.LAUNCHPAD_COLOR_ROSE : index == 7 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);

        // Update the front LED with the color of the current track
        final TrackData track = index == -1 ? null : this.model.getCurrentTrackBank ().getTrack (index);
        final int color = track != null && track.doesExist () ? this.colorManager.getColor (BitwigColors.getColorIndex (track.getColor ())) : 0;
        this.surface.sendLaunchpadSysEx ("0A 63 " + MidiOutput.toHexStr (color));
    }


    private void updateIndication ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        final boolean isVolume = viewManager.isActiveView (Views.VIEW_VOLUME);
        final boolean isPan = viewManager.isActiveView (Views.VIEW_PAN);
        final boolean isSends = viewManager.isActiveView (Views.VIEW_SENDS);
        final boolean isDevice = viewManager.isActiveView (Views.VIEW_DEVICE);

        final TrackBankProxy tb = this.model.getTrackBank ();
        final EffectTrackBankProxy tbe = this.model.getEffectTrackBank ();
        final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
        final View view = viewManager.getActiveView ();
        final int selSend = view instanceof SendsView ? ((SendsView) view).getSelectedSend () : -1;
        final boolean isSession = view instanceof SessionView && !isVolume && !isPan && !isSends;

        final boolean isEffect = this.model.isEffectTrackBankActive ();

        tb.setIndication (!isEffect && isSession);
        tbe.setIndication (isEffect && isSession);

        for (int i = 0; i < 8; i++)
        {
            tb.setVolumeIndication (i, !isEffect && isVolume);
            tb.setPanIndication (i, !isEffect && isPan);
            for (int j = 0; j < 8; j++)
                tb.setSendIndication (i, j, !isEffect && isSends && selSend == j);

            tbe.setVolumeIndication (i, isEffect && isVolume);
            tbe.setPanIndication (i, isEffect && isPan);

            cursorDevice.getParameter (i).setIndication (isDevice);
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
        final ViewManager viewManager = this.surface.getViewManager ();
        if (!viewManager.isActiveView (Views.VIEW_SESSION))
        {
            final TrackData selectedTrack = this.model.getCurrentTrackBank ().getSelectedTrack ();
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
