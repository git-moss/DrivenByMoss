// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine;

import de.mossgrabers.controller.maschine.command.continuous.MainKnobRowModeCommand;
import de.mossgrabers.controller.maschine.command.continuous.TouchstripCommand;
import de.mossgrabers.controller.maschine.command.trigger.AddDeviceCommand;
import de.mossgrabers.controller.maschine.command.trigger.KeyboardCommand;
import de.mossgrabers.controller.maschine.command.trigger.MaschineSendSelectCommand;
import de.mossgrabers.controller.maschine.command.trigger.MaschineStopCommand;
import de.mossgrabers.controller.maschine.command.trigger.PadModeCommand;
import de.mossgrabers.controller.maschine.command.trigger.PageCommand;
import de.mossgrabers.controller.maschine.command.trigger.ProjectButtonCommand;
import de.mossgrabers.controller.maschine.command.trigger.RibbonCommand;
import de.mossgrabers.controller.maschine.command.trigger.SwingCommand;
import de.mossgrabers.controller.maschine.command.trigger.TempoCommand;
import de.mossgrabers.controller.maschine.command.trigger.ToggleFixedVelCommand;
import de.mossgrabers.controller.maschine.command.trigger.VolumePanSendCommand;
import de.mossgrabers.controller.maschine.controller.MaschineColorManager;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.controller.maschine.mode.BrowseMode;
import de.mossgrabers.controller.maschine.mode.DrumConfigurationMode;
import de.mossgrabers.controller.maschine.mode.EditNoteMode;
import de.mossgrabers.controller.maschine.mode.MaschinePanMode;
import de.mossgrabers.controller.maschine.mode.MaschineParametersMode;
import de.mossgrabers.controller.maschine.mode.MaschineSendMode;
import de.mossgrabers.controller.maschine.mode.MaschineUserMode;
import de.mossgrabers.controller.maschine.mode.MaschineVolumeMode;
import de.mossgrabers.controller.maschine.mode.NoteRepeatMode;
import de.mossgrabers.controller.maschine.mode.PlayConfigurationMode;
import de.mossgrabers.controller.maschine.mode.PositionMode;
import de.mossgrabers.controller.maschine.mode.TempoMode;
import de.mossgrabers.controller.maschine.view.ClipView;
import de.mossgrabers.controller.maschine.view.DrumView;
import de.mossgrabers.controller.maschine.view.MuteView;
import de.mossgrabers.controller.maschine.view.NoteRepeatView;
import de.mossgrabers.controller.maschine.view.ParameterView;
import de.mossgrabers.controller.maschine.view.PlayView;
import de.mossgrabers.controller.maschine.view.SceneView;
import de.mossgrabers.controller.maschine.view.SelectView;
import de.mossgrabers.controller.maschine.view.ShiftView;
import de.mossgrabers.controller.maschine.view.SoloView;
import de.mossgrabers.controller.mcu.controller.MCUDisplay;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.command.trigger.application.LayoutCommand;
import de.mossgrabers.framework.command.trigger.application.OverdubCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand.Panels;
import de.mossgrabers.framework.command.trigger.application.PanelLayoutCommand;
import de.mossgrabers.framework.command.trigger.clip.ConvertCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.NoteRepeatCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.track.AddTrackCommand;
import de.mossgrabers.framework.command.trigger.track.MuteCommand;
import de.mossgrabers.framework.command.trigger.track.RecArmCommand;
import de.mossgrabers.framework.command.trigger.track.SoloCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.transport.WriteArrangerAutomationCommand;
import de.mossgrabers.framework.command.trigger.transport.WriteClipLauncherAutomationCommand;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.DefaultValueChanger;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the NI Maschine controller series.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineControllerSetup extends AbstractControllerSetup<MaschineControlSurface, MaschineConfiguration>
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

    private final Maschine      maschine;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     * @param maschine The specific maschine model
     */
    public MaschineControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings, final Maschine maschine)
    {
        super (factory, host, globalSettings, documentSettings);

        this.maschine = maschine;
        this.colorManager = new MaschineColorManager ();
        this.valueChanger = new DefaultValueChanger (128, 8);
        this.configuration = new MaschineConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        if (OperatingSystem.get () == OperatingSystem.LINUX)
            throw new FrameworkException ("Maschine is not supported on Linux since there is no Native Instruments DAW Integration Host.");

        super.init ();
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
        ms.setHasFullFlatTrackList (true);
        ms.setNumTracks (this.maschine.hasBankButtons () ? 8 : 16);
        ms.setNumDevicesInBank (16);
        ms.setNumScenes (16);
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
        final IMidiInput input = midiAccess.createInput (this.maschine.getName (), "80????", "90????");
        this.colorManager.registerColorIndex (IPadGrid.GRID_OFF, 0);
        final MaschineControlSurface surface = new MaschineControlSurface (this.host, this.colorManager, this.maschine, this.configuration, output, input);
        this.surfaces.add (surface);

        if (this.maschine.hasMCUDisplay ())
        {
            final MCUDisplay display = new MCUDisplay (this.host, output, true, false, false);
            display.setCenterNotification (false);
            surface.addTextDisplay (display);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final MaschineControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.register (Modes.BROWSER, new BrowseMode (surface, this.model));

        modeManager.register (Modes.VOLUME, new MaschineVolumeMode (surface, this.model));
        modeManager.register (Modes.PAN, new MaschinePanMode (surface, this.model));
        for (int i = 0; i < 8; i++)
            modeManager.register (Modes.get (Modes.SEND1, i), new MaschineSendMode (i, surface, this.model));

        modeManager.register (Modes.POSITION, new PositionMode (surface, this.model));
        modeManager.register (Modes.TEMPO, new TempoMode (surface, this.model));

        modeManager.register (Modes.REPEAT_NOTE, new NoteRepeatMode (surface, this.model));
        modeManager.register (Modes.SCALES, new PlayConfigurationMode (surface, this.model));
        modeManager.register (Modes.PLAY_OPTIONS, new DrumConfigurationMode (surface, this.model));
        modeManager.register (Modes.NOTE, new EditNoteMode (surface, this.model));

        modeManager.register (Modes.DEVICE_PARAMS, new MaschineParametersMode (surface, this.model));
        if (this.maschine.hasMCUDisplay ())
            modeManager.register (Modes.USER, new MaschineUserMode (surface, this.model));

        modeManager.setDefaultID (Modes.VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final MaschineControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        viewManager.register (Views.SCENE_PLAY, new SceneView (surface, this.model));
        viewManager.register (Views.CLIP, new ClipView (surface, this.model));

        final DrumView drumView = new DrumView (surface, this.model);
        viewManager.register (Views.DRUM, drumView);
        viewManager.register (Views.PLAY, new PlayView (surface, this.model, drumView));

        viewManager.register (Views.DEVICE, new ParameterView (surface, this.model));

        if (!this.maschine.hasBankButtons ())
        {
            viewManager.register (Views.TRACK_SELECT, new SelectView (surface, this.model));
            viewManager.register (Views.TRACK_SOLO, new SoloView (surface, this.model));
            viewManager.register (Views.TRACK_MUTE, new MuteView (surface, this.model));
        }

        viewManager.register (Views.REPEAT_NOTE, new NoteRepeatView (surface, this.model));
        viewManager.register (Views.SHIFT, new ShiftView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        final MaschineControlSurface surface = this.getSurface ();

        surface.getViewManager ().addChangeListener ( (previousViewId, activeViewId) -> this.updateMode (null));
        surface.getModeManager ().addChangeListener ( (previousModeId, activeModeId) -> this.updateMode (activeModeId));

        this.configuration.registerDeactivatedItemsHandler (this.model);
        this.createScaleObservers (this.configuration);
        this.createNoteRepeatObservers (this.configuration, surface);

        this.activateBrowserObserver (Modes.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final MaschineControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        final ITransport t = this.model.getTransport ();

        // Transport
        this.addButton (ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface), MaschineControlSurface.PLAY, t::isPlaying);

        this.addButton (ButtonID.RECORD, "Record", (event, velocity) -> {

            if (event != ButtonEvent.DOWN)
                return;
            boolean pressed = surface.isPressed (ButtonID.STOP);
            if (pressed)
                surface.setStopConsumed ();
            if (this.configuration.isFlipRecord ())
                pressed = !pressed;
            if (pressed)
            {
                final ISlot slot = this.model.getSelectedSlot ();
                if (slot == null || !slot.doesExist ())
                    return;
                if (!slot.isRecording ())
                    slot.record ();
                slot.launch ();
            }
            else
                this.model.getTransport ().record ();

        }, MaschineControlSurface.REC, t::isRecording);

        this.addButton (ButtonID.STOP, "Stop", new MaschineStopCommand (this.model, surface), MaschineControlSurface.STOP, () -> !t.isPlaying ());
        this.addButton (ButtonID.LOOP, "Loop", new ToggleLoopCommand<> (this.model, surface), MaschineControlSurface.RESTART, t::isLoop);
        this.addButton (ButtonID.DELETE, "Erase", NopCommand.INSTANCE, MaschineControlSurface.ERASE);
        final MetronomeCommand<MaschineControlSurface, MaschineConfiguration> metroCommand = new MetronomeCommand<> (this.model, surface, false);
        final TapTempoCommand<MaschineControlSurface, MaschineConfiguration> tapTempoCommand = new TapTempoCommand<> (this.model, surface);
        this.addButton (ButtonID.METRONOME, "Tap/Metro", (event, velocity) -> {
            if (event != ButtonEvent.UP)
                return;
            if (surface.isPressed (ButtonID.STOP))
            {
                surface.setStopConsumed ();
                metroCommand.executeNormal (event);
            }
            else
                tapTempoCommand.execute (ButtonEvent.DOWN, velocity);
        }, MaschineControlSurface.TAP_METRO, t::isMetronomeOn);

        this.addButton (ButtonID.FLIP, "Follow", (event, velocity) -> {

            if (event != ButtonEvent.UP)
                return;
            if (viewManager.isActive (Views.DRUM, Views.PLAY))
                ((DrumView) viewManager.get (Views.DRUM)).toggleGridEditor ();

        }, MaschineControlSurface.FOLLOW, () -> viewManager.isActive (Views.DRUM, Views.PLAY) && ((DrumView) viewManager.get (Views.DRUM)).isGridEditor ());

        this.addButton (ButtonID.NEW, this.maschine.hasCursorKeys () ? "Macro" : "Group", new NewCommand<> (this.model, surface), MaschineControlSurface.GROUP);

        // Automation
        final WriteClipLauncherAutomationCommand<MaschineControlSurface, MaschineConfiguration> writeClipLauncherAutomationCommand = new WriteClipLauncherAutomationCommand<> (this.model, surface);
        final WriteArrangerAutomationCommand<MaschineControlSurface, MaschineConfiguration> writeArrangerAutomationCommand = new WriteArrangerAutomationCommand<> (this.model, surface);
        this.addButton (ButtonID.AUTOMATION, "Auto", (event, velocity) -> {
            if (event != ButtonEvent.DOWN)
                return;
            boolean pressed = surface.isPressed (ButtonID.STOP);
            if (pressed)
                surface.setStopConsumed ();
            if (this.configuration.isFlipRecord ())
                pressed = !pressed;
            if (pressed)
                writeClipLauncherAutomationCommand.execute (event, velocity);
            else
                writeArrangerAutomationCommand.execute (event, velocity);
        }, MaschineControlSurface.AUTO, () -> surface.isPressed (ButtonID.STOP) ? t.isWritingClipLauncherAutomation () : t.isWritingArrangerAutomation ());

        // Overwrite
        final OverdubCommand<MaschineControlSurface, MaschineConfiguration> overdubCommand = new OverdubCommand<> (this.model, surface);
        this.addButton (ButtonID.AUTOMATION_WRITE, "Lock", (event, velocity) -> {
            if (event != ButtonEvent.DOWN)
                return;
            boolean pressed = surface.isPressed (ButtonID.STOP);
            if (pressed)
                surface.setStopConsumed ();
            if (this.configuration.isFlipRecord ())
                pressed = !pressed;
            if (pressed)
                overdubCommand.executeShifted (event);
            else
                overdubCommand.executeNormal (event);
        }, MaschineControlSurface.LOCK, () -> surface.isPressed (ButtonID.STOP) ? t.isLauncherOverdub () : t.isArrangerOverdub ());

        this.addButton (ButtonID.REPEAT, "Repeat", new NoteRepeatCommand<> (this.model, surface, this.maschine.hasMCUDisplay ()), MaschineControlSurface.NOTE_REPEAT, this.configuration::isNoteRepeatActive);

        // Ribbon
        this.addButton (ButtonID.F1, "Pitch", new RibbonCommand (this.model, surface, MaschineConfiguration.RIBBON_MODE_PITCH_DOWN, MaschineConfiguration.RIBBON_MODE_PITCH_UP, MaschineConfiguration.RIBBON_MODE_PITCH_DOWN_UP), MaschineControlSurface.PITCH, () -> this.isRibbonMode (MaschineConfiguration.RIBBON_MODE_PITCH_DOWN, MaschineConfiguration.RIBBON_MODE_PITCH_DOWN_UP, MaschineConfiguration.RIBBON_MODE_PITCH_UP));
        this.addButton (ButtonID.F2, "Mod", new RibbonCommand (this.model, surface, MaschineConfiguration.RIBBON_MODE_CC_1, MaschineConfiguration.RIBBON_MODE_CC_11), MaschineControlSurface.MOD, () -> this.isRibbonMode (MaschineConfiguration.RIBBON_MODE_CC_1, MaschineConfiguration.RIBBON_MODE_CC_11));
        this.addButton (ButtonID.F3, "Perform", new RibbonCommand (this.model, surface, MaschineConfiguration.RIBBON_MODE_MASTER_VOLUME), MaschineControlSurface.PERFORM, () -> this.isRibbonMode (MaschineConfiguration.RIBBON_MODE_MASTER_VOLUME));
        this.addButton (ButtonID.F4, "Notes", new RibbonCommand (this.model, surface, MaschineConfiguration.RIBBON_MODE_NOTE_REPEAT_PERIOD, MaschineConfiguration.RIBBON_MODE_NOTE_REPEAT_LENGTH), MaschineControlSurface.NOTES, () -> this.isRibbonMode (MaschineConfiguration.RIBBON_MODE_NOTE_REPEAT_PERIOD, MaschineConfiguration.RIBBON_MODE_NOTE_REPEAT_LENGTH));

        this.addButton (ButtonID.FADER_TOUCH_1, "Encoder Press", (event, velocity) -> {

            if (event != ButtonEvent.DOWN)
                return;

            if (modeManager.getActiveID () == Modes.BROWSER)
            {
                this.model.getBrowser ().stopBrowsing (true);
                modeManager.restore ();
            }
            else
            {
                final boolean isSlow = !surface.isKnobSensitivitySlow ();
                surface.setKnobSensitivityIsSlow (isSlow);
                surface.getDisplay ().notify ("Value change speed: " + (isSlow ? "Slow" : "Fast"));
            }

        }, MaschineControlSurface.ENCODER_PUSH);

        // Encoder Modes
        this.addButton (ButtonID.VOLUME, "Volume", new VolumePanSendCommand (this.model, surface), MaschineControlSurface.VOLUME, () -> Modes.isTrackMode (modeManager.getActiveID ()));
        this.addButton (ButtonID.TAP_TEMPO, "Swing", new SwingCommand (this.model, surface), MaschineControlSurface.SWING, () -> modeManager.isActive (Modes.POSITION));
        this.addButton (ButtonID.TEMPO_TOUCH, "Tempo", new TempoCommand (this.model, surface), MaschineControlSurface.TEMPO, () -> modeManager.isActive (Modes.TEMPO));
        this.addButton (ButtonID.DEVICE, "Plugin", (event, velocity) -> {

            if (this.maschine.hasMCUDisplay () || surface.isPressed (ButtonID.STOP))
            {
                surface.setTriggerConsumed (ButtonID.STOP);
                new PanelLayoutCommand<> (this.model, surface).executeNormal (event);
            }
            else
                new ModeSelectCommand<> (this.model, surface, Modes.DEVICE_PARAMS).execute (event, velocity);

        }, MaschineControlSurface.PLUGIN, () -> modeManager.isActive (Modes.DEVICE_PARAMS));
        final ConvertCommand<MaschineControlSurface, MaschineConfiguration> convertCommand = new ConvertCommand<> (this.model, surface);
        this.addButton (ButtonID.DEVICE_ON_OFF, "Sampling", (event, velocity) -> {
            if (surface.isPressed (ButtonID.STOP))
                convertCommand.executeShifted (event);
            else
                convertCommand.executeNormal (event);
        }, MaschineControlSurface.SAMPLING, this.model.getCursorDevice ()::isWindowOpen);

        // Browser
        this.addButton (ButtonID.ADD_TRACK, this.maschine.hasCursorKeys () ? "File" : "Project", new ProjectButtonCommand (this.model, surface), MaschineControlSurface.PROJECT);
        this.addButton (ButtonID.ADD_EFFECT, this.maschine.hasCursorKeys () ? "Settings" : "Favorites", new AddDeviceCommand (this.model, surface), MaschineControlSurface.FAVORITES);
        this.addButton (ButtonID.BROWSE, "Browser", new BrowserCommand<> (this.model, surface)
        {
            /** {@inheritDoc} */
            @Override
            protected boolean getCommit ()
            {
                // Discard browser, confirmation is via encoder
                return false;
            }
        }, MaschineControlSurface.BROWSER, this.model.getBrowser ()::isActive);

        // Pad modes
        this.addButton (ButtonID.ACCENT, "Accent", new ToggleFixedVelCommand (this.model, surface), MaschineControlSurface.FIXED_VEL, this.configuration::isAccentActive);

        this.addButton (ButtonID.SCENE1, "Scene", new ViewMultiSelectCommand<> (this.model, surface, true, Views.SCENE_PLAY), MaschineControlSurface.SCENE, () -> viewManager.isActive (Views.SCENE_PLAY));
        this.addButton (ButtonID.CLIP, "Pattern", new ViewMultiSelectCommand<> (this.model, surface, true, Views.CLIP), MaschineControlSurface.PATTERN, () -> viewManager.isActive (Views.CLIP));
        this.addButton (ButtonID.NOTE, "Events", new ModeSelectCommand<> (this.model, surface, Modes.NOTE, true), MaschineControlSurface.EVENTS, () -> modeManager.isActive (Modes.NOTE));
        this.addButton (ButtonID.TOGGLE_DEVICE, "Variation", new ViewMultiSelectCommand<> (this.model, surface, true, Views.DEVICE), MaschineControlSurface.VARIATION, () -> viewManager.isActive (Views.DEVICE));
        this.addButton (ButtonID.DUPLICATE, "Duplicate", NopCommand.INSTANCE, MaschineControlSurface.DUPLICATE);

        if (this.maschine.hasMCUDisplay ())
        {
            this.addButton (ButtonID.TRACK, "Select", new RecArmCommand<> (this.model, surface), MaschineControlSurface.SELECT, () -> {
                final ITrack selectedTrack = this.model.getCursorTrack ();
                return selectedTrack.doesExist () && selectedTrack.isRecArm ();
            });
            this.addButton (ButtonID.SOLO, "Solo", new SoloCommand<> (this.model, surface), MaschineControlSurface.SOLO, () -> {
                final ITrack selectedTrack = this.model.getCursorTrack ();
                return selectedTrack.doesExist () && selectedTrack.isSolo ();
            });
            this.addButton (ButtonID.MUTE, "Mute", new MuteCommand<> (this.model, surface), MaschineControlSurface.MUTE, () -> {
                final ITrack selectedTrack = this.model.getCursorTrack ();
                return selectedTrack.doesExist () && selectedTrack.isMute ();
            });
        }
        else
        {
            this.addButton (ButtonID.TRACK, "Select", new ViewMultiSelectCommand<> (this.model, surface, true, Views.TRACK_SELECT), MaschineControlSurface.SELECT, () -> viewManager.isActive (Views.TRACK_SELECT));
            this.addButton (ButtonID.SOLO, "Solo", new ViewMultiSelectCommand<> (this.model, surface, true, Views.TRACK_SOLO), MaschineControlSurface.SOLO, () -> viewManager.isActive (Views.TRACK_SOLO));
            this.addButton (ButtonID.MUTE, "Mute", new ViewMultiSelectCommand<> (this.model, surface, true, Views.TRACK_MUTE), MaschineControlSurface.MUTE, () -> viewManager.isActive (Views.TRACK_MUTE));
        }

        this.addButton (ButtonID.ROW1_1, "Pad Mode", new PadModeCommand (this.model, surface), MaschineControlSurface.PAD_MODE, () -> viewManager.isActive (Views.DRUM));
        this.addButton (ButtonID.ROW1_2, "Keyboard", new KeyboardCommand (this.model, surface), MaschineControlSurface.KEYBOARD, () -> viewManager.isActive (Views.PLAY));
        this.addButton (ButtonID.ROW1_3, "Chords", (event, velocity) -> {
            if (velocity == 0)
                ((PlayView) surface.getViewManager ().get (Views.PLAY)).toggleChordMode ();
        }, MaschineControlSurface.CHORDS, ((PlayView) surface.getViewManager ().get (Views.PLAY))::isChordMode);

        final DrumView drumView = (DrumView) viewManager.get (Views.DRUM);
        this.addButton (ButtonID.ROW1_4, "Step", (event, velocity) -> {

            if (event == ButtonEvent.UP)
                drumView.toggleSequencerVisible ();

        }, MaschineControlSurface.STEP, drumView::isSequencerVisible);

        if (this.maschine.hasCursorKeys ())
        {
            this.addButton (ButtonID.ARROW_LEFT, "Left", new ModeCursorCommand<> (Direction.LEFT, this.model, surface, false), MaschineControlSurface.CURSOR_LEFT, () -> this.getEncoderColor (ButtonID.ARROW_LEFT));
            this.addButton (ButtonID.ARROW_RIGHT, "Right", new ModeCursorCommand<> (Direction.RIGHT, this.model, surface, false), MaschineControlSurface.CURSOR_RIGHT, () -> this.getEncoderColor (ButtonID.ARROW_RIGHT));
            this.addButton (ButtonID.ARROW_UP, "Up", new ModeCursorCommand<> (Direction.UP, this.model, surface, false), MaschineControlSurface.CURSOR_UP, () -> this.getEncoderColor (ButtonID.ARROW_UP));
            this.addButton (ButtonID.ARROW_DOWN, "Down", new ModeCursorCommand<> (Direction.DOWN, this.model, surface, false), MaschineControlSurface.CURSOR_DOWN, () -> this.getEncoderColor (ButtonID.ARROW_DOWN));

            this.addButton (ButtonID.PAGE_LEFT, "Page Left", new PageCommand (Direction.LEFT, this.model, surface), MaschineControlSurface.PAGE_LEFT);
            this.addButton (ButtonID.PAGE_RIGHT, "Page Right", new PageCommand (Direction.RIGHT, this.model, surface), MaschineControlSurface.PAGE_RIGHT);

            this.addButton (ButtonID.LAYOUT_ARRANGE, "Arranger", new LayoutCommand<> (this.model, surface), MaschineControlSurface.ARRANGER);
            this.addButton (ButtonID.MIXER, "Mixer", new PaneCommand<> (Panels.MIXER, this.model, surface), MaschineControlSurface.MIXER);
            this.addButton (ButtonID.DRUM, "Channel", new AddTrackCommand<> (this.model, surface, null, ButtonID.STOP), MaschineControlSurface.CHANNEL);
        }

        if (this.maschine.hasMCUDisplay ())
        {
            this.addButton (ButtonID.ROW2_1, "Volume", new ModeSelectCommand<> (this.model, surface, Modes.VOLUME), MaschineControlSurface.MODE_BUTTON_1, () -> modeManager.isActive (Modes.VOLUME));
            this.addButton (ButtonID.ROW2_2, "Pan", new ModeSelectCommand<> (this.model, surface, Modes.PAN), MaschineControlSurface.MODE_BUTTON_2, () -> modeManager.isActive (Modes.PAN));

            final MaschineSendSelectCommand sendSelectCommand = new MaschineSendSelectCommand (this.model, surface);
            this.addButton (ButtonID.ROW2_3, "Send -", (event, velocity) -> sendSelectCommand.executeShifted (event), MaschineControlSurface.MODE_BUTTON_3, () -> Modes.isSendMode (modeManager.getActiveID ()));
            this.addButton (ButtonID.ROW2_4, "Send +", sendSelectCommand, MaschineControlSurface.MODE_BUTTON_4, () -> Modes.isSendMode (modeManager.getActiveID ()));

            this.addButton (ButtonID.ROW2_5, "Pin", (event, velocity) -> {
                if (event != ButtonEvent.DOWN)
                    return;
                if (modeManager.isActive (Modes.DEVICE_PARAMS))
                    this.model.getCursorDevice ().togglePinned ();
                else
                    this.model.getCursorTrack ().togglePinned ();
            }, MaschineControlSurface.MODE_BUTTON_5, () -> {
                if (modeManager.isActive (Modes.DEVICE_PARAMS))
                    return this.model.getCursorDevice ().isPinned ();
                return this.model.getCursorTrack ().isPinned ();
            });

            this.addButton (ButtonID.ROW2_6, "Active", (event, velocity) -> {
                if (event != ButtonEvent.DOWN)
                    return;
                if (modeManager.isActive (Modes.DEVICE_PARAMS))
                    this.model.getCursorDevice ().toggleEnabledState ();
                else
                    this.model.getCursorTrack ().toggleIsActivated ();
            }, MaschineControlSurface.MODE_BUTTON_6, () -> {
                if (modeManager.isActive (Modes.DEVICE_PARAMS))
                    return this.model.getCursorDevice ().isEnabled ();
                final ITrack selectedTrack = this.model.getCursorTrack ();
                return selectedTrack.doesExist () && selectedTrack.isActivated ();
            });

            // This button is mapped as Note not CC since it requires at least 1 MCU button to make
            // the MCU display activate!
            this.addButton (ButtonID.ROW2_7, "User Params", new ModeSelectCommand<> (this.model, surface, Modes.USER), MaschineControlSurface.MODE_BUTTON_7, () -> modeManager.isActive (Modes.USER));

            this.addButton (ButtonID.ROW2_8, "Parameters", new ModeSelectCommand<> (this.model, surface, Modes.DEVICE_PARAMS), MaschineControlSurface.MODE_BUTTON_8, () -> modeManager.isActive (Modes.DEVICE_PARAMS));
        }

        if (this.maschine.hasBankButtons ())
        {
            for (int i = 0; i < 8; i++)
            {
                final int index = i;
                this.addButton (ButtonID.get (ButtonID.ROW3_1, i), "Bank " + (i + 1), (event, velocity) -> {

                    if (event != ButtonEvent.UP)
                        return;

                    final ITrack item = this.model.getCurrentTrackBank ().getItem (index);
                    if (surface.isPressed (ButtonID.TRACK))
                    {
                        surface.setTriggerConsumed (ButtonID.TRACK);
                        item.toggleRecArm ();
                    }
                    else if (surface.isPressed (ButtonID.SOLO))
                    {
                        surface.setTriggerConsumed (ButtonID.SOLO);
                        item.toggleSolo ();
                    }
                    else if (surface.isPressed (ButtonID.MUTE))
                    {
                        surface.setTriggerConsumed (ButtonID.MUTE);
                        item.toggleMute ();
                    }
                    else if (surface.isPressed (ButtonID.DUPLICATE))
                    {
                        surface.setTriggerConsumed (ButtonID.DUPLICATE);
                        item.duplicate ();
                    }
                    else if (surface.isPressed (ButtonID.DELETE))
                    {
                        surface.setTriggerConsumed (ButtonID.DELETE);
                        item.remove ();
                    }
                    else
                        item.select ();

                }, MaschineControlSurface.BANK_1 + i, () -> {

                    final ITrack item = this.model.getCurrentTrackBank ().getItem (index);
                    return item.doesExist () ? this.colorManager.getColorIndex (DAWColor.getColorIndex (item.getColor ())) : 0;

                });
            }
        }
    }


    private int getEncoderColor (final ButtonID arrowButton)
    {
        final MaschineControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        final Modes modeID = modeManager.getActiveID ();
        if (modeID == null)
            return MaschineColorManager.COLOR_BLACK;

        final IMode mode = modeManager.getActive ();

        boolean isOn;
        switch (arrowButton)
        {
            case ARROW_LEFT:
                isOn = mode.hasPreviousItem ();
                break;
            case ARROW_RIGHT:
                isOn = mode.hasNextItem ();
                break;
            case ARROW_UP:
                isOn = mode.hasNextItemPage ();
                break;
            case ARROW_DOWN:
                isOn = mode.hasPreviousItemPage ();
                break;
            // Never reached
            default:
                return MaschineColorManager.COLOR_BLACK;
        }

        if (!isOn)
            return MaschineColorManager.COLOR_BLACK;

        switch (modeID)
        {
            case VOLUME:
                return surface.isPressed (arrowButton) ? MaschineColorManager.COLOR_BLUE : MaschineColorManager.COLOR_BLUE_LO;

            case PAN:
                return surface.isPressed (arrowButton) ? MaschineColorManager.COLOR_SKY : MaschineColorManager.COLOR_SKY_LO;

            case SEND1:
            case SEND2:
            case SEND3:
            case SEND4:
            case SEND5:
            case SEND6:
            case SEND7:
            case SEND8:
                return surface.isPressed (arrowButton) ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_YELLOW;

            case DEVICE_PARAMS:
            case USER:
                return surface.isPressed (arrowButton) ? MaschineColorManager.COLOR_PINK : MaschineColorManager.COLOR_PINK_LO;

            case BROWSER:
                return surface.isPressed (arrowButton) ? MaschineColorManager.COLOR_AMBER : MaschineColorManager.COLOR_AMBER_LO;

            default:
                return MaschineColorManager.COLOR_BLACK;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final MaschineControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        final IHwRelativeKnob knob = this.addRelativeKnob (ContinuousID.MASTER_KNOB, "Encoder", new MainKnobRowModeCommand (this.model, surface), MaschineControlSurface.ENCODER);
        knob.bindTouch ( (event, velocity) -> {
            final IMode mode = modeManager.getActive ();
            if (mode != null && event != ButtonEvent.LONG)
                mode.onKnobTouch (8, event == ButtonEvent.DOWN);
        }, surface.getMidiInput (), BindType.CC, 0, MaschineControlSurface.ENCODER_TOUCH);

        if (this.maschine.hasMCUDisplay ())
        {
            for (int i = 0; i < 8; i++)
            {
                final int index = i;
                final IHwRelativeKnob modeKnob = this.addRelativeKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1), new KnobRowModeCommand<> (i, this.model, surface), MaschineControlSurface.MODE_KNOB_1 + i);
                modeKnob.bindTouch ( (event, velocity) -> {
                    final IMode mode = modeManager.getActive ();
                    if (mode != null && event != ButtonEvent.LONG)
                        mode.onKnobTouch (index, event == ButtonEvent.DOWN);
                }, surface.getMidiInput (), BindType.CC, 0, MaschineControlSurface.MODE_KNOB_TOUCH_1 + i);
                modeKnob.setIndexInGroup (i);
            }
        }

        final TouchstripCommand touchstripCommand = new TouchstripCommand (this.model, surface);
        this.addFader (ContinuousID.CROSSFADER, "Touchstrip", touchstripCommand, BindType.CC, MaschineControlSurface.TOUCHSTRIP, false);
        surface.getContinuous (ContinuousID.CROSSFADER).bindTouch (touchstripCommand, surface.getMidiInput (), BindType.CC, 0, MaschineControlSurface.TOUCHSTRIP_TOUCH);
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        switch (this.maschine)
        {
            case MK3:
                this.layoutMk3 ();
                break;
            case MIKRO_MK3:
                this.layoutMikroMk3 ();
                break;
            default:
                // Not used
                break;
        }
    }


    private void layoutMk3 ()
    {
        final MaschineControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (425.5, 652.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD2).setBounds (516.25, 653.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD3).setBounds (606.75, 653.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD4).setBounds (694.75, 653.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD5).setBounds (425.5, 562.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD6).setBounds (516.25, 563.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD7).setBounds (606.75, 563.5, 76.25, 79.0);
        surface.getButton (ButtonID.PAD8).setBounds (694.75, 563.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD9).setBounds (425.5, 473.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD10).setBounds (516.25, 473.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD11).setBounds (606.75, 474.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD12).setBounds (694.75, 473.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD13).setBounds (425.5, 383.5, 76.25, 79.0);
        surface.getButton (ButtonID.PAD14).setBounds (516.25, 384.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD15).setBounds (606.75, 384.5, 76.25, 79.0);
        surface.getButton (ButtonID.PAD16).setBounds (694.75, 383.5, 76.25, 79.0);
        surface.getButton (ButtonID.PLAY).setBounds (24.75, 697.75, 55.75, 32.0);
        surface.getButton (ButtonID.RECORD).setBounds (96.75, 697.75, 55.75, 32.0);
        surface.getButton (ButtonID.STOP).setBounds (166.25, 697.75, 55.75, 32.0);
        surface.getButton (ButtonID.LOOP).setBounds (24.75, 666.75, 56.0, 18.0);
        surface.getButton (ButtonID.DELETE).setBounds (96.75, 666.75, 56.0, 18.0);
        surface.getButton (ButtonID.METRONOME).setBounds (166.25, 666.75, 56.0, 18.0);
        surface.getButton (ButtonID.FLIP).setBounds (236.5, 666.75, 56.0, 18.0);
        surface.getButton (ButtonID.NEW).setBounds (95.75, 237.25, 55.75, 32.0);
        surface.getButton (ButtonID.AUTOMATION).setBounds (28.75, 237.25, 55.75, 32.0);
        surface.getButton (ButtonID.AUTOMATION_WRITE).setBounds (233.75, 402.0, 58.0, 19.0);
        surface.getButton (ButtonID.REPEAT).setBounds (233.75, 343.75, 58.0, 46.25);
        surface.getButton (ButtonID.F1).setBounds (24.0, 464.75, 58.0, 19.0);
        surface.getButton (ButtonID.F2).setBounds (96.0, 465.5, 57.25, 18.25);
        surface.getButton (ButtonID.F3).setBounds (165.5, 465.5, 57.25, 18.25);
        surface.getButton (ButtonID.F4).setBounds (233.75, 465.5, 57.25, 18.25);
        surface.getButton (ButtonID.FADER_TOUCH_1).setBounds (51.25, 315.0, 69.5, 22.75);
        surface.getButton (ButtonID.VOLUME).setBounds (164.5, 343.75, 58.0, 18.0);
        surface.getButton (ButtonID.TAP_TEMPO).setBounds (164.5, 373.25, 58.0, 18.0);
        surface.getButton (ButtonID.TEMPO_TOUCH).setBounds (164.5, 402.75, 58.0, 18.0);
        surface.getButton (ButtonID.DEVICE).setBounds (95.75, 21.25, 55.75, 23.0);
        surface.getButton (ButtonID.DEVICE_ON_OFF).setBounds (95.75, 107.25, 55.75, 38.5);
        surface.getButton (ButtonID.ADD_TRACK).setBounds (28.75, 193.0, 55.75, 32.0);
        surface.getButton (ButtonID.ADD_EFFECT).setBounds (95.75, 193.0, 55.75, 32.0);
        surface.getButton (ButtonID.BROWSE).setBounds (28.75, 107.25, 55.75, 38.5);
        surface.getButton (ButtonID.ACCENT).setBounds (344.75, 343.75, 58.0, 20.75);
        surface.getButton (ButtonID.SCENE1).setBounds (346.5, 383.25, 58.0, 34.0);
        surface.getButton (ButtonID.CLIP).setBounds (346.5, 427.0, 58.0, 34.0);
        surface.getButton (ButtonID.NOTE).setBounds (346.5, 470.75, 58.0, 34.0);
        surface.getButton (ButtonID.TOGGLE_DEVICE).setBounds (346.5, 514.5, 58.0, 41.25);
        surface.getButton (ButtonID.DUPLICATE).setBounds (347.25, 562.75, 58.0, 34.0);
        surface.getButton (ButtonID.TRACK).setBounds (346.5, 609.25, 58.0, 34.0);
        surface.getButton (ButtonID.SOLO).setBounds (346.5, 653.0, 58.0, 34.0);
        surface.getButton (ButtonID.MUTE).setBounds (346.5, 696.75, 58.0, 34.0);
        surface.getButton (ButtonID.ROW1_1).setBounds (425.5, 343.75, 78.0, 20.75);
        surface.getButton (ButtonID.ROW1_2).setBounds (516.25, 343.75, 78.0, 20.75);
        surface.getButton (ButtonID.ROW1_3).setBounds (606.75, 343.75, 78.0, 20.75);
        surface.getButton (ButtonID.ROW1_4).setBounds (694.75, 343.75, 78.0, 20.75);
        surface.getButton (ButtonID.ARROW_LEFT).setBounds (25.75, 385.0, 22.25, 17.5);
        surface.getButton (ButtonID.ARROW_RIGHT).setBounds (120.25, 385.0, 22.25, 17.5);
        surface.getButton (ButtonID.ARROW_UP).setBounds (76.0, 342.5, 22.25, 17.5);
        surface.getButton (ButtonID.ARROW_DOWN).setBounds (76.0, 431.25, 22.25, 17.5);
        surface.getButton (ButtonID.PAGE_LEFT).setBounds (28.75, 157.75, 55.75, 23.0);
        surface.getButton (ButtonID.PAGE_RIGHT).setBounds (95.75, 157.75, 55.75, 23.0);
        surface.getButton (ButtonID.ROW2_1).setBounds (179.25, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_2).setBounds (254.25, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_3).setBounds (329.25, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_4).setBounds (404.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_5).setBounds (479.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_6).setBounds (554.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_7).setBounds (629.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_8).setBounds (704.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW3_1).setBounds (26.25, 564.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_2).setBounds (96.75, 564.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_3).setBounds (167.0, 564.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_4).setBounds (237.5, 564.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_5).setBounds (26.25, 610.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_6).setBounds (96.75, 610.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_7).setBounds (167.0, 610.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_8).setBounds (237.5, 610.75, 53.75, 35.0);
        surface.getButton (ButtonID.LAYOUT_ARRANGE).setBounds (28.75, 56.5, 55.75, 38.5);
        surface.getButton (ButtonID.MIXER).setBounds (95.75, 56.5, 55.75, 38.5);
        surface.getButton (ButtonID.DRUM).setBounds (28.75, 21.25, 55.75, 23.0);

        surface.getContinuous (ContinuousID.KNOB1).setBounds (183.5, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB2).setBounds (259.0, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB3).setBounds (334.75, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB4).setBounds (410.25, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB5).setBounds (486.0, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB6).setBounds (561.5, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB7).setBounds (637.0, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB8).setBounds (712.75, 226.25, 53.75, 49.25);

        surface.getContinuous (ContinuousID.MASTER_KNOB).setBounds (53.0, 362.25, 64.0, 63.0);
        surface.getContinuous (ContinuousID.CROSSFADER).setBounds (24.0, 498.75, 268.0, 50.0);

        surface.getTextDisplay ().getHardwareDisplay ().setBounds (182.75, 111.75, 591.75, 64.5);
    }


    private void layoutMikroMk3 ()
    {
        final MaschineControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (427.0, 336.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD2).setBounds (517.75, 336.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD3).setBounds (608.25, 336.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD4).setBounds (696.25, 336.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD5).setBounds (427.0, 246.5, 76.25, 79.0);
        surface.getButton (ButtonID.PAD6).setBounds (517.75, 247.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD7).setBounds (608.25, 247.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD8).setBounds (696.25, 247.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD9).setBounds (427.0, 156.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD10).setBounds (517.75, 157.5, 76.25, 79.0);
        surface.getButton (ButtonID.PAD11).setBounds (608.25, 157.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD12).setBounds (696.25, 157.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD13).setBounds (427.0, 67.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD14).setBounds (517.75, 67.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD15).setBounds (608.25, 68.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD16).setBounds (696.25, 67.25, 76.25, 79.0);
        surface.getButton (ButtonID.PLAY).setBounds (26.25, 381.5, 55.75, 32.0);
        surface.getButton (ButtonID.RECORD).setBounds (98.25, 381.5, 55.75, 32.0);
        surface.getButton (ButtonID.STOP).setBounds (167.75, 381.5, 55.75, 32.0);
        surface.getButton (ButtonID.LOOP).setBounds (26.25, 350.5, 56.0, 18.0);
        surface.getButton (ButtonID.DELETE).setBounds (98.25, 350.5, 56.0, 18.0);
        surface.getButton (ButtonID.METRONOME).setBounds (167.75, 350.5, 56.0, 18.0);
        surface.getButton (ButtonID.FLIP).setBounds (238.0, 350.5, 56.0, 18.0);
        surface.getButton (ButtonID.NEW).setBounds (26.25, 280.5, 55.75, 32.0);
        surface.getButton (ButtonID.AUTOMATION).setBounds (98.25, 280.5, 55.75, 32.0);
        surface.getButton (ButtonID.AUTOMATION_WRITE).setBounds (167.75, 280.5, 55.75, 32.0);
        surface.getButton (ButtonID.REPEAT).setBounds (238.0, 280.5, 55.75, 32.0);
        surface.getButton (ButtonID.F1).setBounds (26.25, 170.0, 58.0, 19.0);
        surface.getButton (ButtonID.F2).setBounds (98.25, 170.75, 57.25, 18.25);
        surface.getButton (ButtonID.F3).setBounds (167.75, 170.75, 57.25, 18.25);
        surface.getButton (ButtonID.F4).setBounds (238.0, 170.75, 57.25, 18.25);
        surface.getButton (ButtonID.FADER_TOUCH_1).setBounds (74.0, 21.25, 69.5, 22.75);
        surface.getButton (ButtonID.VOLUME).setBounds (166.0, 25.75, 58.0, 19.0);
        surface.getButton (ButtonID.TAP_TEMPO).setBounds (166.0, 56.75, 58.0, 19.0);
        surface.getButton (ButtonID.TEMPO_TOUCH).setBounds (166.0, 85.75, 58.0, 19.0);
        surface.getButton (ButtonID.DEVICE).setBounds (238.0, 25.75, 58.0, 19.0);
        surface.getButton (ButtonID.DEVICE_ON_OFF).setBounds (238.0, 56.75, 58.0, 19.0);
        surface.getButton (ButtonID.ADD_TRACK).setBounds (26.25, 22.75, 25.5, 25.0);
        surface.getButton (ButtonID.ADD_EFFECT).setBounds (26.25, 51.75, 25.5, 25.0);
        surface.getButton (ButtonID.BROWSE).setBounds (26.25, 80.75, 25.5, 25.0);
        surface.getButton (ButtonID.ACCENT).setBounds (346.25, 25.75, 58.0, 20.75);
        surface.getButton (ButtonID.SCENE1).setBounds (348.0, 67.0, 58.0, 34.0);
        surface.getButton (ButtonID.CLIP).setBounds (348.0, 110.75, 58.0, 34.0);
        surface.getButton (ButtonID.NOTE).setBounds (348.0, 154.5, 58.0, 34.0);
        surface.getButton (ButtonID.TOGGLE_DEVICE).setBounds (348.0, 198.25, 58.0, 41.25);
        surface.getButton (ButtonID.DUPLICATE).setBounds (348.75, 246.5, 58.0, 34.0);
        surface.getButton (ButtonID.TRACK).setBounds (348.0, 293.0, 58.0, 34.0);
        surface.getButton (ButtonID.SOLO).setBounds (348.0, 336.75, 58.0, 34.0);
        surface.getButton (ButtonID.MUTE).setBounds (348.0, 380.5, 58.0, 34.0);
        surface.getButton (ButtonID.ROW1_1).setBounds (427.0, 25.75, 78.0, 20.75);
        surface.getButton (ButtonID.ROW1_2).setBounds (517.75, 25.75, 78.0, 20.75);
        surface.getButton (ButtonID.ROW1_3).setBounds (608.25, 25.75, 78.0, 20.75);
        surface.getButton (ButtonID.ROW1_4).setBounds (696.25, 25.75, 78.0, 20.75);

        surface.getContinuous (ContinuousID.MASTER_KNOB).setBounds (75.0, 50.0, 64.0, 63.0);
        surface.getContinuous (ContinuousID.CROSSFADER).setBounds (26.25, 204.0, 268.0, 50.0);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final MaschineControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActive (Modes.VOLUME);
        surface.getViewManager ().setActive (Views.PLAY);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        final TouchstripCommand command = (TouchstripCommand) this.getSurface ().getContinuous (ContinuousID.CROSSFADER).getCommand ();
        if (command != null)
            command.updateValue ();
    }


    private void updateMode (final Modes mode)
    {
        this.currentMode = mode == null ? this.getSurface ().getModeManager ().getActiveID () : mode;
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

        final MaschineControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        if (viewManager.isActive (Views.PLAY))
            viewManager.getActive ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.resetDrumOctave ();
        if (viewManager.isActive (Views.DRUM))
            viewManager.get (Views.DRUM).updateNoteMapping ();
    }


    private boolean isRibbonMode (final int... modes)
    {
        final int ribbonMode = this.configuration.getRibbonMode ();
        for (final int mode: modes)
        {
            if (ribbonMode == mode)
                return true;
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        return buttonID == ButtonID.ROW2_7 ? BindType.NOTE : BindType.CC;
    }
}
