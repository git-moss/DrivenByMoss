// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3;

import de.mossgrabers.controller.maschine.mikro.mk3.command.continuous.PitchbendCommand;
import de.mossgrabers.controller.maschine.mikro.mk3.command.trigger.AddDeviceCommand;
import de.mossgrabers.controller.maschine.mikro.mk3.command.trigger.GridButtonCommand;
import de.mossgrabers.controller.maschine.mikro.mk3.command.trigger.ProjectButtonCommand;
import de.mossgrabers.controller.maschine.mikro.mk3.command.trigger.RibbonCommand;
import de.mossgrabers.controller.maschine.mikro.mk3.command.trigger.ToggleDuplicateButtonCommand;
import de.mossgrabers.controller.maschine.mikro.mk3.command.trigger.ToggleFixedVelCommand;
import de.mossgrabers.controller.maschine.mikro.mk3.command.trigger.VolumePanSendCommand;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.controller.maschine.mikro.mk3.mode.BrowseMode;
import de.mossgrabers.controller.maschine.mikro.mk3.mode.PositionMode;
import de.mossgrabers.controller.maschine.mikro.mk3.mode.TempoMode;
import de.mossgrabers.controller.maschine.mikro.mk3.view.ClipView;
import de.mossgrabers.controller.maschine.mikro.mk3.view.DrumView;
import de.mossgrabers.controller.maschine.mikro.mk3.view.MuteView;
import de.mossgrabers.controller.maschine.mikro.mk3.view.ParameterView;
import de.mossgrabers.controller.maschine.mikro.mk3.view.PlayView;
import de.mossgrabers.controller.maschine.mikro.mk3.view.SceneView;
import de.mossgrabers.controller.maschine.mikro.mk3.view.SelectView;
import de.mossgrabers.controller.maschine.mikro.mk3.view.SoloView;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.NoteRepeatCommand;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.command.trigger.mode.KnobRowTouchModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.transport.WriteArrangerAutomationCommand;
import de.mossgrabers.framework.command.trigger.transport.WriteClipLauncherAutomationCommand;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
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
import de.mossgrabers.framework.mode.device.SelectedDeviceMode;
import de.mossgrabers.framework.mode.track.SelectedPanMode;
import de.mossgrabers.framework.mode.track.SelectedSendMode;
import de.mossgrabers.framework.mode.track.SelectedVolumeMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the NI Maschine Mikro Mk3 controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineMikroMk3ControllerSetup extends AbstractControllerSetup<MaschineMikroMk3ControlSurface, MaschineMikroMk3Configuration>
{
    // @formatter:off
    /** The drum grid matrix. */
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

    private static final Integer COMMAND_MOD       = Integer.valueOf (200);
    private static final Integer COMMAND_PERFORM   = Integer.valueOf (201);
    private static final Integer COMMAND_NOTES     = Integer.valueOf (202);
    private static final Integer COMMAND_PITCH     = Integer.valueOf (203);
    private static final Integer COMMAND_PARAMETER = Integer.valueOf (204);


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public MaschineMikroMk3ControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);
        this.colorManager = new ColorManager ();
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new MaschineMikroMk3Configuration (this.valueChanger);
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
        this.scales = new Scales (this.valueChanger, 36, 52, 4, 4);
        this.scales.setDrumMatrix (DRUM_MATRIX);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setNumTracks (16);
        ms.setNumDevicesInBank (16);
        ms.setNumScenes (16);
        ms.setNumSends (8);
        ms.setNumParams (16);
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
        final IMidiInput input = midiAccess.createInput ("Maschine Mikro Mk3", "80????", "90????");
        this.colorManager.registerColor (PadGrid.GRID_OFF, 0);
        final MaschineMikroMk3ControlSurface surface = new MaschineMikroMk3ControlSurface (this.model.getHost (), this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);
        surface.setDisplay (new DummyDisplay (this.host));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();

        surface.getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> this.updateMode (null));
        surface.getModeManager ().addModeListener ( (previousModeId, activeModeId) -> this.updateMode (activeModeId));

        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.registerMode (Modes.MODE_BROWSER, new BrowseMode (surface, this.model));

        modeManager.registerMode (Modes.MODE_VOLUME, new SelectedVolumeMode<> (surface, this.model));
        modeManager.registerMode (Modes.MODE_PAN, new SelectedPanMode<> (surface, this.model));
        for (int i = 0; i < 8; i++)
            modeManager.registerMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + i), new SelectedSendMode<> (i, surface, this.model));

        modeManager.registerMode (Modes.MODE_POSITION, new PositionMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_TEMPO, new TempoMode (surface, this.model));

        modeManager.registerMode (Modes.MODE_DEVICE_PARAMS, new SelectedDeviceMode<> (surface, this.model));

        modeManager.setDefaultMode (Modes.MODE_VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        viewManager.registerView (Views.VIEW_SCENE_PLAY, new SceneView (surface, this.model));
        viewManager.registerView (Views.VIEW_CLIP, new ClipView (surface, this.model));

        viewManager.registerView (Views.VIEW_PLAY, new PlayView (surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM, new DrumView (surface, this.model));

        viewManager.registerView (Views.VIEW_DEVICE, new ParameterView (surface, this.model));

        viewManager.registerView (Views.VIEW_TRACK_SELECT, new SelectView (surface, this.model));
        viewManager.registerView (Views.VIEW_TRACK_SOLO, new SoloView (surface, this.model));
        viewManager.registerView (Views.VIEW_TRACK_MUTE, new MuteView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();

        // Transport
        this.addTriggerCommand (Commands.COMMAND_PLAY, MaschineMikroMk3ControlSurface.MIKRO_3_PLAY, new PlayCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_RECORD, MaschineMikroMk3ControlSurface.MIKRO_3_REC, new RecordCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_STOP, MaschineMikroMk3ControlSurface.MIKRO_3_STOP, new StopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_LOOP, MaschineMikroMk3ControlSurface.MIKRO_3_RESTART, new ToggleLoopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_UNDO, MaschineMikroMk3ControlSurface.MIKRO_3_ERASE, new UndoCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_METRONOME, MaschineMikroMk3ControlSurface.MIKRO_3_TAP_METRO, new MetronomeCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_QUANTIZE, MaschineMikroMk3ControlSurface.MIKRO_3_FOLLOW, new QuantizeCommand<> (this.model, surface));

        // Automation
        this.addTriggerCommand (Commands.COMMAND_NEW, MaschineMikroMk3ControlSurface.MIKRO_3_GROUP, new NewCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_AUTOMATION, MaschineMikroMk3ControlSurface.MIKRO_3_AUTO, new WriteClipLauncherAutomationCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_AUTOMATION_WRITE, MaschineMikroMk3ControlSurface.MIKRO_3_LOCK, new WriteArrangerAutomationCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_REPEAT, MaschineMikroMk3ControlSurface.MIKRO_3_NOTE_REPEAT, new NoteRepeatCommand<> (this.model, surface));

        // Ribbon
        this.addTriggerCommand (COMMAND_PITCH, MaschineMikroMk3ControlSurface.MIKRO_3_PITCH, new RibbonCommand (this.model, surface, MaschineMikroMk3Configuration.RIBBON_MODE_PITCH_DOWN, MaschineMikroMk3Configuration.RIBBON_MODE_PITCH_UP, MaschineMikroMk3Configuration.RIBBON_MODE_PITCH_DOWN_UP));
        this.addTriggerCommand (COMMAND_MOD, MaschineMikroMk3ControlSurface.MIKRO_3_MOD, new RibbonCommand (this.model, surface, MaschineMikroMk3Configuration.RIBBON_MODE_CC_1));
        this.addTriggerCommand (COMMAND_PERFORM, MaschineMikroMk3ControlSurface.MIKRO_3_PERFORM, new RibbonCommand (this.model, surface, MaschineMikroMk3Configuration.RIBBON_MODE_CC_11));
        this.addTriggerCommand (COMMAND_NOTES, MaschineMikroMk3ControlSurface.MIKRO_3_NOTES, new RibbonCommand (this.model, surface, MaschineMikroMk3Configuration.RIBBON_MODE_MASTER_VOLUME));

        // Encoder Modes
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_1, MaschineMikroMk3ControlSurface.MIKRO_3_ENCODER_PUSH, new KnobRowTouchModeCommand<> (0, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_VOLUME, MaschineMikroMk3ControlSurface.MIKRO_3_VOLUME, new VolumePanSendCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_TAP_TEMPO, MaschineMikroMk3ControlSurface.MIKRO_3_SWING, new ModeSelectCommand<> (Modes.MODE_POSITION, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_USER, MaschineMikroMk3ControlSurface.MIKRO_3_TEMPO, new ModeSelectCommand<> (Modes.MODE_TEMPO, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DEVICE, MaschineMikroMk3ControlSurface.MIKRO_3_PLUGIN, new ModeSelectCommand<> (Modes.MODE_DEVICE_PARAMS, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DEVICE_ON_OFF, MaschineMikroMk3ControlSurface.MIKRO_3_SAMPLING, new PaneCommand<> (PaneCommand.Panels.DEVICE, this.model, surface));

        // Browser
        this.addTriggerCommand (Commands.COMMAND_ADD_TRACK, MaschineMikroMk3ControlSurface.MIKRO_3_PROJECT, new ProjectButtonCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ADD_EFFECT, MaschineMikroMk3ControlSurface.MIKRO_3_FAVORITES, new AddDeviceCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_BROWSE, MaschineMikroMk3ControlSurface.MIKRO_3_BROWSER, new BrowserCommand<> (Modes.MODE_BROWSER, this.model, surface));

        // Pad modes
        this.addTriggerCommand (Commands.COMMAND_ACCENT, MaschineMikroMk3ControlSurface.MIKRO_3_FIXED_VEL, new ToggleFixedVelCommand (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_SCENE1, MaschineMikroMk3ControlSurface.MIKRO_3_SCENE, new ViewMultiSelectCommand<> (this.model, surface, true, Views.VIEW_SCENE_PLAY));
        this.addTriggerCommand (Commands.COMMAND_CLIP, MaschineMikroMk3ControlSurface.MIKRO_3_PATTERN, new ViewMultiSelectCommand<> (this.model, surface, true, Views.VIEW_CLIP));
        this.addTriggerCommand (Commands.COMMAND_SELECT_PLAY_VIEW, MaschineMikroMk3ControlSurface.MIKRO_3_EVENTS, new ViewMultiSelectCommand<> (this.model, surface, true, Views.VIEW_PLAY, Views.VIEW_DRUM));
        this.addTriggerCommand (COMMAND_PARAMETER, MaschineMikroMk3ControlSurface.MIKRO_3_VARIATION, new ViewMultiSelectCommand<> (this.model, surface, true, Views.VIEW_DEVICE));
        this.addTriggerCommand (Commands.COMMAND_DUPLICATE, MaschineMikroMk3ControlSurface.MIKRO_3_DUPLICATE, new ToggleDuplicateButtonCommand (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_TRACK, MaschineMikroMk3ControlSurface.MIKRO_3_SELECT, new ViewMultiSelectCommand<> (this.model, surface, true, Views.VIEW_TRACK_SELECT));
        this.addTriggerCommand (Commands.COMMAND_SOLO, MaschineMikroMk3ControlSurface.MIKRO_3_SOLO, new ViewMultiSelectCommand<> (this.model, surface, true, Views.VIEW_TRACK_SOLO));
        this.addTriggerCommand (Commands.COMMAND_MUTE, MaschineMikroMk3ControlSurface.MIKRO_3_MUTE, new ViewMultiSelectCommand<> (this.model, surface, true, Views.VIEW_TRACK_MUTE));

        this.addTriggerCommand (Commands.COMMAND_ROW1_1, MaschineMikroMk3ControlSurface.MIKRO_3_PAD_MODE, new GridButtonCommand (0, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ROW1_2, MaschineMikroMk3ControlSurface.MIKRO_3_KEYBOARD, new GridButtonCommand (1, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ROW1_3, MaschineMikroMk3ControlSurface.MIKRO_3_CHORDS, new GridButtonCommand (2, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ROW1_4, MaschineMikroMk3ControlSurface.MIKRO_3_STEP, new GridButtonCommand (3, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();
        this.addContinuousCommand (Commands.CONT_COMMAND_KNOB1, MaschineMikroMk3ControlSurface.MIKRO_3_ENCODER, new KnobRowModeCommand<> (0, this.model, surface));
        this.addContinuousCommand (Commands.CONT_COMMAND_CROSSFADER, MaschineMikroMk3ControlSurface.MIKRO_3_TOUCHSTRIP, new PitchbendCommand (this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActiveMode (Modes.MODE_VOLUME);
        surface.getViewManager ().setActiveView (Views.VIEW_PLAY);
    }


    private void updateButtons ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();

        final ITransport t = this.model.getTransport ();
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_PLAY, t.isPlaying () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_REC, t.isRecording () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_STOP, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_RESTART, t.isLoop () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_ERASE, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_TAP_METRO, t.isMetronomeOn () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_FOLLOW, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);

        final int ribbonMode = surface.getConfiguration ().getRibbonMode ();
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_PITCH, ribbonMode <= MaschineMikroMk3Configuration.RIBBON_MODE_PITCH_DOWN_UP ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_MOD, ribbonMode == MaschineMikroMk3Configuration.RIBBON_MODE_CC_1 ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_PERFORM, ribbonMode == MaschineMikroMk3Configuration.RIBBON_MODE_CC_11 ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_NOTES, ribbonMode == MaschineMikroMk3Configuration.RIBBON_MODE_MASTER_VOLUME ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);

        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_GROUP, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_AUTO, t.isWritingClipLauncherAutomation () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_LOCK, t.isWritingArrangerAutomation () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);

        int repeatState = MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF;
        if (this.host.hasRepeat ())
        {
            final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
            if (selectedTrack != null)
                repeatState = selectedTrack.isNoteRepeat () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF;
        }
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_NOTE_REPEAT, repeatState);

        final Integer modeID = this.getSurface ().getModeManager ().getActiveOrTempModeId ();
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_VOLUME, modeID != null && modeID.intValue () <= Modes.MODE_SEND8.intValue () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_SWING, modeID != null && modeID.intValue () == Modes.MODE_POSITION.intValue () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_TEMPO, modeID != null && modeID.intValue () == Modes.MODE_TEMPO.intValue () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_PLUGIN, modeID != null && modeID.intValue () == Modes.MODE_DEVICE_PARAMS.intValue () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_SAMPLING, this.model.getCursorDevice ().isWindowOpen () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);

        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_FIXED_VEL, this.configuration.isAccentActive () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);

        final ViewManager viewManager = this.getSurface ().getViewManager ();
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_SCENE, viewManager.isActiveView (Views.VIEW_SCENE_PLAY) ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_PATTERN, viewManager.isActiveView (Views.VIEW_CLIP) ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_EVENTS, viewManager.isActiveView (Views.VIEW_PLAY) || viewManager.isActiveView (Views.VIEW_DRUM) ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_VARIATION, viewManager.isActiveView (Views.VIEW_DEVICE) ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);

        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_DUPLICATE, this.configuration.isDuplicateEnabled () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_SELECT, viewManager.isActiveView (Views.VIEW_TRACK_SELECT) ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_SOLO, viewManager.isActiveView (Views.VIEW_TRACK_SOLO) ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_MUTE, viewManager.isActiveView (Views.VIEW_TRACK_MUTE) ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);

        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_PAD_MODE, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_KEYBOARD, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_CHORDS, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_STEP, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
    }


    private void updateMode (final Integer mode)
    {
        final Integer m = mode == null ? this.getSurface ().getModeManager ().getActiveOrTempModeId () : mode;
        if (m == this.currentMode)
            return;
        this.currentMode = m;
        this.updateIndication (m);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Integer mode)
    {
        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        final boolean isSession = viewManager.isActiveView (Views.VIEW_SCENE_PLAY) || viewManager.isActiveView (Views.VIEW_CLIP);
        final boolean isEffect = this.model.isEffectTrackBankActive ();

        final boolean isVolume = Modes.MODE_VOLUME.equals (mode);
        final boolean isPan = Modes.MODE_PAN.equals (mode);

        tb.setIndication (!isEffect && isSession);
        if (tbe != null)
            tbe.setIndication (isEffect && isSession);

        final ITrack selectedTrack = tb.getSelectedItem ();
        final int selIndex = selectedTrack == null ? -1 : selectedTrack.getIndex ();

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        for (int i = 0; i < 16; i++)
        {
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (selIndex == i && !isEffect && isVolume);
            track.setPanIndication (selIndex == i && !isEffect && isPan);
            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < 8; j++)
                sendBank.getItem (j).setIndication (selIndex == i && !isEffect && (Modes.MODE_SEND1.equals (mode) && j == 0 || Modes.MODE_SEND2.equals (mode) && j == 1 || Modes.MODE_SEND3.equals (mode) && j == 2 || Modes.MODE_SEND4.equals (mode) && j == 3 || Modes.MODE_SEND5.equals (mode) && j == 4 || Modes.MODE_SEND6.equals (mode) && j == 5 || Modes.MODE_SEND7.equals (mode) && j == 6 || Modes.MODE_SEND8.equals (mode) && j == 7));

            if (tbe != null)
            {
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (selIndex == i && isEffect && isVolume);
                fxTrack.setPanIndication (selIndex == i && isEffect && isPan);
            }

            cursorDevice.getParameterBank ().getItem (i).setIndication (true);
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

        final MaschineMikroMk3ControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        if (viewManager.isActiveView (Views.VIEW_PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.resetDrumOctave ();
        if (viewManager.isActiveView (Views.VIEW_DRUM))
            viewManager.getView (Views.VIEW_DRUM).updateNoteMapping ();

        this.updateIndication (this.currentMode);
    }
}
