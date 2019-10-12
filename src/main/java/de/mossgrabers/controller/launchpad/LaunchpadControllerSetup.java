// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad;

import de.mossgrabers.controller.launchpad.command.continuous.FaderCommand;
import de.mossgrabers.controller.launchpad.command.trigger.ClickCommand;
import de.mossgrabers.controller.launchpad.command.trigger.LaunchpadCursorCommand;
import de.mossgrabers.controller.launchpad.command.trigger.LaunchpadDuplicateCommand;
import de.mossgrabers.controller.launchpad.command.trigger.MuteCommand;
import de.mossgrabers.controller.launchpad.command.trigger.PanCommand;
import de.mossgrabers.controller.launchpad.command.trigger.PlayAndNewCommand;
import de.mossgrabers.controller.launchpad.command.trigger.RecordArmCommand;
import de.mossgrabers.controller.launchpad.command.trigger.SelectDeviceViewCommand;
import de.mossgrabers.controller.launchpad.command.trigger.SelectNoteViewCommand;
import de.mossgrabers.controller.launchpad.command.trigger.SelectSessionViewCommand;
import de.mossgrabers.controller.launchpad.command.trigger.SendsCommand;
import de.mossgrabers.controller.launchpad.command.trigger.ShiftCommand;
import de.mossgrabers.controller.launchpad.command.trigger.SoloCommand;
import de.mossgrabers.controller.launchpad.command.trigger.StopClipCommand;
import de.mossgrabers.controller.launchpad.command.trigger.TrackSelectCommand;
import de.mossgrabers.controller.launchpad.command.trigger.VolumeCommand;
import de.mossgrabers.controller.launchpad.controller.LaunchpadColors;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.launchpad.controller.LaunchpadScales;
import de.mossgrabers.controller.launchpad.definition.ILaunchpadControllerDefinition;
import de.mossgrabers.controller.launchpad.definition.LaunchpadProControllerDefinition;
import de.mossgrabers.controller.launchpad.mode.RecArmMode;
import de.mossgrabers.controller.launchpad.mode.SendMode;
import de.mossgrabers.controller.launchpad.mode.StopClipMode;
import de.mossgrabers.controller.launchpad.mode.TrackMode;
import de.mossgrabers.controller.launchpad.view.BrowserView;
import de.mossgrabers.controller.launchpad.view.DeviceView;
import de.mossgrabers.controller.launchpad.view.DrumView;
import de.mossgrabers.controller.launchpad.view.DrumView4;
import de.mossgrabers.controller.launchpad.view.DrumView64;
import de.mossgrabers.controller.launchpad.view.DrumView8;
import de.mossgrabers.controller.launchpad.view.PanView;
import de.mossgrabers.controller.launchpad.view.PianoView;
import de.mossgrabers.controller.launchpad.view.PlayView;
import de.mossgrabers.controller.launchpad.view.RaindropsView;
import de.mossgrabers.controller.launchpad.view.SendsView;
import de.mossgrabers.controller.launchpad.view.SequencerView;
import de.mossgrabers.controller.launchpad.view.SessionView;
import de.mossgrabers.controller.launchpad.view.ShiftView;
import de.mossgrabers.controller.launchpad.view.UserView;
import de.mossgrabers.controller.launchpad.view.VolumeView;
import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.SceneCommand;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.aftertouch.AftertouchAbstractViewCommand;
import de.mossgrabers.framework.command.trigger.application.DeleteCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.DAWColors;
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
import de.mossgrabers.framework.mode.track.MuteMode;
import de.mossgrabers.framework.mode.track.PanMode;
import de.mossgrabers.framework.mode.track.SoloMode;
import de.mossgrabers.framework.mode.track.VolumeMode;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Support for several Novation Launchpad controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadControllerSetup extends AbstractControllerSetup<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private final ILaunchpadControllerDefinition definition;
    private int                                  frontColor = -1;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     * @param definition The Launchpad definition
     */
    public LaunchpadControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings, final ILaunchpadControllerDefinition definition)
    {
        super (factory, host, globalSettings, documentSettings);

        this.definition = definition;
        this.colorManager = new ColorManager ();
        LaunchpadColors.addColors (this.colorManager);
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new LaunchpadConfiguration (host, this.valueChanger, definition);
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
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFullFlatTrackList (true);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
        final ITrackBank trackBank = this.model.getTrackBank ();
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
        final LaunchpadControlSurface surface = new LaunchpadControlSurface (this.host, this.colorManager, this.configuration, output, input, this.definition);
        this.surfaces.add (surface);
        surface.setLaunchpadToStandalone ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        this.getSurface ().getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> this.updateIndication (null));
        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.registerMode (Modes.REC_ARM, new RecArmMode (surface, this.model));
        modeManager.registerMode (Modes.TRACK_SELECT, new TrackMode (surface, this.model));
        modeManager.registerMode (Modes.MUTE, new MuteMode<> (surface, this.model));
        modeManager.registerMode (Modes.SOLO, new SoloMode<> (surface, this.model));
        modeManager.registerMode (Modes.VOLUME, new VolumeMode<> (surface, this.model, true));
        modeManager.registerMode (Modes.PAN, new PanMode<> (surface, this.model, true));
        modeManager.registerMode (Modes.SEND, new SendMode (surface, this.model));
        modeManager.registerMode (Modes.STOP_CLIP, new StopClipMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.BROWSER, new BrowserView (surface, this.model));
        viewManager.registerView (Views.DEVICE, new DeviceView (surface, this.model));
        viewManager.registerView (Views.DRUM, new DrumView (surface, this.model));
        viewManager.registerView (Views.DRUM4, new DrumView4 (surface, this.model));
        viewManager.registerView (Views.DRUM8, new DrumView8 (surface, this.model));
        viewManager.registerView (Views.TRACK_PAN, new PanView (surface, this.model));
        viewManager.registerView (Views.DRUM64, new DrumView64 (surface, this.model));
        viewManager.registerView (Views.PLAY, new PlayView (surface, this.model));
        viewManager.registerView (Views.PIANO, new PianoView (surface, this.model));
        viewManager.registerView (Views.RAINDROPS, new RaindropsView (surface, this.model));
        viewManager.registerView (Views.TRACK_SENDS, new SendsView (surface, this.model));
        viewManager.registerView (Views.SEQUENCER, new SequencerView (surface, this.model));
        viewManager.registerView (Views.SESSION, new SessionView (surface, this.model));
        viewManager.registerView (Views.TRACK_VOLUME, new VolumeView (surface, this.model));
        viewManager.registerView (Views.SHIFT, new ShiftView (surface, this.model));
        if (this.definition.isPro () && this.host.hasUserParameters ())
            viewManager.registerView (Views.CONTROL, new UserView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();

        this.addTriggerCommand (TriggerCommandID.SHIFT, surface.getTriggerId (ButtonID.SHIFT), new ShiftCommand (this.model, surface));

        if (this.definition.isPro () && this.host.hasUserParameters ())
            this.addTriggerCommand (TriggerCommandID.USER, surface.getTriggerId (ButtonID.USER), new ViewMultiSelectCommand<> (this.model, surface, true, Views.CONTROL));

        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerTriggerCommand (TriggerCommandID.PLAY, new PlayCommand<> (this.model, surface));
        viewManager.registerTriggerCommand (TriggerCommandID.NEW, new NewCommand<> (this.model, surface));

        this.addTriggerCommand (TriggerCommandID.ARROW_DOWN, surface.getTriggerId (ButtonID.DOWN), new LaunchpadCursorCommand (Direction.DOWN, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_UP, surface.getTriggerId (ButtonID.UP), new LaunchpadCursorCommand (Direction.UP, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_LEFT, surface.getTriggerId (ButtonID.LEFT), new LaunchpadCursorCommand (Direction.LEFT, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_RIGHT, surface.getTriggerId (ButtonID.RIGHT), new LaunchpadCursorCommand (Direction.RIGHT, this.model, surface));

        this.addTriggerCommand (TriggerCommandID.SELECT_SESSION_VIEW, surface.getTriggerId (ButtonID.SESSION), new SelectSessionViewCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SELECT_PLAY_VIEW, surface.getTriggerId (ButtonID.NOTE), new SelectNoteViewCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.DEVICE, surface.getTriggerId (ButtonID.DEVICE), new SelectDeviceViewCommand (this.model, surface));

        // The following buttons are only available on the Pro but the commands are used by all
        // Launchpad models!
        this.addTriggerCommand (TriggerCommandID.METRONOME, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_CLICK, new ClickCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.UNDO, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_UNDO, new UndoCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.DELETE, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_DELETE, new DeleteCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.QUANTIZE, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_QUANTIZE, new QuantizeCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.DUPLICATE, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_DUPLICATE, new LaunchpadDuplicateCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.DOUBLE, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_DOUBLE, new PlayAndNewCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.RECORD, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_RECORD, new RecordCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.REC_ARM, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_REC_ARM, new RecordArmCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.TRACK, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_TRACK, new TrackSelectCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.MUTE, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_MUTE, new MuteCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SOLO, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_SOLO, new SoloCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.VOLUME, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_VOLUME, new VolumeCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.PAN_SEND, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_PAN, new PanCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SENDS, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_SENDS, new SendsCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.STOP_CLIP, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_STOP_CLIP, new StopClipCommand (this.model, surface));

        if (this.definition.sceneButtonsUseCC ())
        {
            this.addTriggerCommand (TriggerCommandID.SCENE1, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, new SceneCommand<> (0, this.model, surface));
            this.addTriggerCommand (TriggerCommandID.SCENE2, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, new SceneCommand<> (1, this.model, surface));
            this.addTriggerCommand (TriggerCommandID.SCENE3, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, new SceneCommand<> (2, this.model, surface));
            this.addTriggerCommand (TriggerCommandID.SCENE4, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, new SceneCommand<> (3, this.model, surface));
            this.addTriggerCommand (TriggerCommandID.SCENE5, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, new SceneCommand<> (4, this.model, surface));
            this.addTriggerCommand (TriggerCommandID.SCENE6, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, new SceneCommand<> (5, this.model, surface));
            this.addTriggerCommand (TriggerCommandID.SCENE7, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, new SceneCommand<> (6, this.model, surface));
            this.addTriggerCommand (TriggerCommandID.SCENE8, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, new SceneCommand<> (7, this.model, surface));
        }
        else
        {
            this.addNoteCommand (TriggerCommandID.SCENE1, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, new SceneCommand<> (0, this.model, surface));
            this.addNoteCommand (TriggerCommandID.SCENE2, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, new SceneCommand<> (1, this.model, surface));
            this.addNoteCommand (TriggerCommandID.SCENE3, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, new SceneCommand<> (2, this.model, surface));
            this.addNoteCommand (TriggerCommandID.SCENE4, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, new SceneCommand<> (3, this.model, surface));
            this.addNoteCommand (TriggerCommandID.SCENE5, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, new SceneCommand<> (4, this.model, surface));
            this.addNoteCommand (TriggerCommandID.SCENE6, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, new SceneCommand<> (5, this.model, surface));
            this.addNoteCommand (TriggerCommandID.SCENE7, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, new SceneCommand<> (6, this.model, surface));
            this.addNoteCommand (TriggerCommandID.SCENE8, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, new SceneCommand<> (7, this.model, surface));
        }
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
        final LaunchpadControlSurface surface = this.getSurface ();
        for (int i = 0; i < 8; i++)
            this.addContinuousCommand (ContinuousCommandID.get (ContinuousCommandID.KNOB1, i), LaunchpadControlSurface.LAUNCHPAD_FADER_1 + i, new FaderCommand (i, this.model, surface));
        final ViewManager viewManager = surface.getViewManager ();

        Views [] views =
        {
            Views.PLAY,
            Views.PIANO,
            Views.DRUM,
            Views.DRUM64
        };
        for (final Views viewID: views)
        {
            final AbstractView view = (AbstractView) viewManager.getView (viewID);
            view.registerAftertouchCommand (new AftertouchAbstractViewCommand (view, this.model, surface));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        this.getSurface ().getViewManager ().setActiveView (Views.PLAY);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateButtons ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final View activeView = viewManager.getActiveView ();
        if (activeView != null)
        {
            ((LaunchpadCursorCommand) activeView.getTriggerCommand (TriggerCommandID.ARROW_DOWN)).updateArrows ();
            ((SceneView) activeView).updateSceneButtons ();
        }

        final boolean isShift = surface.isShiftPressed ();
        surface.setTrigger (surface.getTriggerId (ButtonID.SHIFT), isShift ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);

        // Update the front or logo LED with the color of the current track
        final ITrack selTrack = this.model.getSelectedTrack ();
        final int index = selTrack == null ? -1 : selTrack.getIndex ();
        final ITrack track = index == -1 ? null : this.model.getCurrentTrackBank ().getItem (index);
        final int color = track != null && track.doesExist () ? this.colorManager.getColor (DAWColors.getColorIndex (track.getColor ())) : 0;
        if (this.definition.isPro ())
        {
            if (color != this.frontColor)
            {
                surface.sendLaunchpadSysEx ("0A 63 " + StringUtils.toHexStr (color));
                this.frontColor = color;
            }
        }
        else
            surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_LOGO, color);

        if (!this.definition.isPro ())
            return;

        final ModeManager modeManager = surface.getModeManager ();
        final ITransport transport = this.model.getTransport ();

        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_CLICK, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING : transport.isMetronomeOn () ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_UNDO, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_DELETE, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_QUANTIZE, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_BLACK : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_DUPLICATE, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_DOUBLE, isShift ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN_SPRING : LaunchpadColors.LAUNCHPAD_COLOR_GREEN_LO);
        final boolean flipRecord = surface.getConfiguration ().isFlipRecord ();
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_RECORD, isShift && !flipRecord || !isShift && flipRecord ? transport.isLauncherOverdub () ? LaunchpadColors.LAUNCHPAD_COLOR_ROSE : LaunchpadColors.LAUNCHPAD_COLOR_RED_AMBER : transport.isRecording () ? LaunchpadColors.LAUNCHPAD_COLOR_RED_HI : LaunchpadColors.LAUNCHPAD_COLOR_RED_LO);

        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_REC_ARM, modeManager.isActiveOrTempMode (Modes.REC_ARM) ? LaunchpadColors.LAUNCHPAD_COLOR_RED : index == 0 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_TRACK, modeManager.isActiveOrTempMode (Modes.TRACK_SELECT) ? LaunchpadColors.LAUNCHPAD_COLOR_GREEN : index == 1 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_MUTE, modeManager.isActiveOrTempMode (Modes.MUTE) ? LaunchpadColors.LAUNCHPAD_COLOR_YELLOW : index == 2 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_SOLO, modeManager.isActiveOrTempMode (Modes.SOLO) ? LaunchpadColors.LAUNCHPAD_COLOR_BLUE : index == 3 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_VOLUME, viewManager.isActiveView (Views.TRACK_VOLUME) ? LaunchpadColors.LAUNCHPAD_COLOR_CYAN : index == 4 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_PAN, viewManager.isActiveView (Views.TRACK_PAN) ? LaunchpadColors.LAUNCHPAD_COLOR_SKY : index == 5 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_SENDS, viewManager.isActiveView (Views.TRACK_SENDS) ? LaunchpadColors.LAUNCHPAD_COLOR_ORCHID : index == 6 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        surface.setTrigger (LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_STOP_CLIP, modeManager.isActiveOrTempMode (Modes.STOP_CLIP) ? LaunchpadColors.LAUNCHPAD_COLOR_ROSE : index == 7 ? LaunchpadColors.LAUNCHPAD_COLOR_WHITE : LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Modes mode)
    {
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        final boolean isVolume = viewManager.isActiveView (Views.TRACK_VOLUME);
        final boolean isPan = viewManager.isActiveView (Views.TRACK_PAN);
        final boolean isSends = viewManager.isActiveView (Views.TRACK_SENDS);
        final boolean isDevice = viewManager.isActiveView (Views.DEVICE);

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final View view = viewManager.getActiveView ();
        final int selSend = view instanceof SendsView ? ((SendsView) view).getSelectedSend () : -1;
        final boolean isSession = view instanceof SessionView && !isVolume && !isPan && !isSends;

        final boolean isEffect = this.model.isEffectTrackBankActive ();

        tb.setIndication (!isEffect && isSession);
        if (tbe != null)
            tbe.setIndication (isEffect && isSession);

        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (!isEffect && isVolume);
            track.setPanIndication (!isEffect && isPan);
            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < 8; j++)
                sendBank.getItem (j).setIndication (!isEffect && isSends && selSend == j);

            if (tbe != null)
            {
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (isEffect && isVolume);
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

        // Recall last used view (if we are not in session mode)
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        if (!viewManager.isActiveView (Views.SESSION))
        {
            final ITrack selectedTrack = this.model.getSelectedTrack ();
            if (selectedTrack != null)
            {
                final Views preferredView = viewManager.getPreferredView (selectedTrack.getPosition ());
                viewManager.setActiveView (preferredView == null ? Views.PLAY : preferredView);
            }
        }

        if (viewManager.isActiveView (Views.PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.resetDrumOctave ();
        if (viewManager.isActiveView (Views.DRUM))
            viewManager.getView (Views.DRUM).updateNoteMapping ();
    }
}
