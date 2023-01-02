// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3;

import de.mossgrabers.controller.mackie.mcu.controller.MCUDisplay;
import de.mossgrabers.controller.ni.maschine.Maschine;
import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.core.RibbonMode;
import de.mossgrabers.controller.ni.maschine.core.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.core.command.trigger.GroupButtonCommand;
import de.mossgrabers.controller.ni.maschine.core.command.trigger.MaschineMonitorEncoderCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.continuous.MainKnobRowModeCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.continuous.TouchstripCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.AddDeviceCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.KeyboardCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.MaschineCursorCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.MaschineSelectButtonCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.MaschineSendSelectCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.MaschineStopCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.PadModeCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.PageCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.ProjectButtonCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.RibbonCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.SwingCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.TempoCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.ToggleFixedVelCommand;
import de.mossgrabers.controller.ni.maschine.mk3.command.trigger.VolumePanSendCommand;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.controller.ni.maschine.mk3.controller.StudioEncoderModeManager;
import de.mossgrabers.controller.ni.maschine.mk3.mode.BrowseMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.DrumConfigurationMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.EditNoteMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.LoopLengthMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.LoopStartMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.MaschinePanMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.MaschineParametersMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.MaschineSendMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.MaschineUserMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.MaschineVolumeMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.NoteRepeatMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.PlayConfigurationMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.PositionMode;
import de.mossgrabers.controller.ni.maschine.mk3.mode.TempoMode;
import de.mossgrabers.controller.ni.maschine.mk3.view.ClipView;
import de.mossgrabers.controller.ni.maschine.mk3.view.DrumView;
import de.mossgrabers.controller.ni.maschine.mk3.view.MuteView;
import de.mossgrabers.controller.ni.maschine.mk3.view.NoteRepeatView;
import de.mossgrabers.controller.ni.maschine.mk3.view.ParameterView;
import de.mossgrabers.controller.ni.maschine.mk3.view.PlayView;
import de.mossgrabers.controller.ni.maschine.mk3.view.SceneView;
import de.mossgrabers.controller.ni.maschine.mk3.view.SelectView;
import de.mossgrabers.controller.ni.maschine.mk3.view.ShiftView;
import de.mossgrabers.controller.ni.maschine.mk3.view.SoloView;
import de.mossgrabers.framework.command.aftertouch.AftertouchViewCommand;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.FootswitchCommand;
import de.mossgrabers.framework.command.trigger.application.LayoutCommand;
import de.mossgrabers.framework.command.trigger.application.OverdubCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand.Panels;
import de.mossgrabers.framework.command.trigger.application.PanelLayoutCommand;
import de.mossgrabers.framework.command.trigger.clip.ConvertCommand;
import de.mossgrabers.framework.command.trigger.clip.FillModeNoteRepeatCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.track.AddTrackCommand;
import de.mossgrabers.framework.command.trigger.transport.AutomationCommand;
import de.mossgrabers.framework.command.trigger.transport.ConfiguredRecordCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.view.ToggleShiftViewCommand;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.data.empty.EmptyTrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.view.Views;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


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

    private static final List<RibbonMode> RIBBON_MODES_PITCH         = Arrays.asList (RibbonMode.PITCH_DOWN, RibbonMode.PITCH_DOWN_UP, RibbonMode.PITCH_UP);
    private static final List<RibbonMode> RIBBON_MODES_CC            = Arrays.asList (RibbonMode.CC_1, RibbonMode.CC_11);
    private static final List<RibbonMode> RIBBON_MODES_MASTER_VOLUME = Arrays.asList (RibbonMode.MASTER_VOLUME);
    private static final List<RibbonMode> RIBBON_MODES_NOTE_REPEAT   = Arrays.asList (RibbonMode.NOTE_REPEAT_PERIOD, RibbonMode.NOTE_REPEAT_LENGTH);

    private StudioEncoderModeManager      encoderManager;

    private final Maschine                maschine;
    private ShiftView                     shiftView;


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
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new MaschineConfiguration (host, this.valueChanger, factory.getArpeggiatorModes (), maschine);
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
        ms.setNumTracks (this.maschine.hasGroupButtons () ? 8 : 16);
        ms.setNumDevicesInBank (16);
        ms.setNumScenes (16);
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
        final IMidiInput input = midiAccess.createInput (this.maschine.getName (), "80????", "90????");
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

        modeManager.register (Modes.TEMPO, new TempoMode (surface, this.model));
        modeManager.register (Modes.POSITION, new PositionMode (surface, this.model));
        modeManager.register (Modes.LOOP_START, new LoopStartMode (surface, this.model));
        modeManager.register (Modes.LOOP_LENGTH, new LoopLengthMode (surface, this.model));

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

        if (!this.maschine.hasGroupButtons ())
        {
            viewManager.register (Views.TRACK_SELECT, new SelectView (surface, this.model));
            viewManager.register (Views.TRACK_SOLO, new SoloView (surface, this.model));
            viewManager.register (Views.TRACK_MUTE, new MuteView (surface, this.model));
        }

        viewManager.register (Views.REPEAT_NOTE, new NoteRepeatView (surface, this.model));

        this.shiftView = new ShiftView (surface, this.model);
        viewManager.register (Views.SHIFT, this.shiftView);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        final MaschineControlSurface surface = this.getSurface ();

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
        final ViewManager viewManager = surface.getViewManager ();
        final ITransport t = this.model.getTransport ();

        this.addButton (ButtonID.SHIFT, "SHIFT", new ToggleShiftViewCommand<> (this.model, surface), this.maschine == Maschine.STUDIO ? MaschineControlSurface.NAVIGATE_BACK : -1, () -> viewManager.isActive (Views.SHIFT) || surface.isShiftPressed ());

        // Transport
        this.addButton (ButtonID.PLAY, "PLAY", new PlayCommand<> (this.model, surface), MaschineControlSurface.PLAY, t::isPlaying);

        final ConfiguredRecordCommand<MaschineControlSurface, MaschineConfiguration> recordCommand = new ConfiguredRecordCommand<> (this.model, surface);
        this.addButton (ButtonID.RECORD, "RECORD", recordCommand, MaschineControlSurface.REC, recordCommand::isLit);
        this.addButton (ButtonID.STOP, "STOP", new MaschineStopCommand (this.model, surface), MaschineControlSurface.STOP, () -> !t.isPlaying ());
        this.addButton (ButtonID.LOOP, "LOOP", new ToggleLoopCommand<> (this.model, surface), MaschineControlSurface.RESTART, t::isLoop);
        this.addButton (ButtonID.DELETE, "ERASE", NopCommand.INSTANCE, MaschineControlSurface.ERASE);
        final MetronomeCommand<MaschineControlSurface, MaschineConfiguration> metroCommand = new MetronomeCommand<> (this.model, surface, false);
        final TapTempoCommand<MaschineControlSurface, MaschineConfiguration> tapTempoCommand = new TapTempoCommand<> (this.model, surface);
        this.addButton (ButtonID.TAP_TEMPO, "TAP/METRO", (event, velocity) -> {
            if (event != ButtonEvent.UP)
                return;
            if (surface.isShiftPressed ())
            {
                surface.setStopConsumed ();
                metroCommand.executeNormal (event);
            }
            else
                tapTempoCommand.execute (ButtonEvent.DOWN, velocity);
        }, MaschineControlSurface.TAP_METRO, t::isMetronomeOn);

        this.addButton (ButtonID.FLIP, this.maschine == Maschine.STUDIO || this.maschine == Maschine.MK2 ? "GRID" : "FOLLOW", (event, velocity) -> {
            if (event != ButtonEvent.UP)
                return;
            if (viewManager.isActive (Views.DRUM, Views.PLAY))
                ((DrumView) viewManager.get (Views.DRUM)).toggleGridEditor ();
        }, MaschineControlSurface.FOLLOW, () -> viewManager.isActive (Views.DRUM, Views.PLAY) && ((DrumView) viewManager.get (Views.DRUM)).isGridEditor ());

        this.addButton (ButtonID.NEW, this.maschine.hasCursorKeys () ? "MACRO" : "GROUP", new NewCommand<> (this.model, surface), MaschineControlSurface.GROUP);

        final AutomationCommand<MaschineControlSurface, MaschineConfiguration> automationCommand = new AutomationCommand<> (this.model, surface)
        {
            @Override
            protected void cleanupShift ()
            {
                this.surface.setStopConsumed ();
            }

        };
        this.addButton (ButtonID.AUTOMATION, "AUTO", automationCommand, MaschineControlSurface.AUTO, automationCommand::isLit);
        this.addButton (ButtonID.OVERDUB, this.maschine == Maschine.STUDIO || this.maschine == Maschine.MK2 ? "ENTER" : "LOCK", new OverdubCommand<> (this.model, surface)
        {
            @Override
            protected void shiftedFunction ()
            {
                this.surface.setStopConsumed ();
                super.shiftedFunction ();
            }
        }, MaschineControlSurface.LOCK, () -> surface.isShiftPressed () ? t.isLauncherOverdub () : t.isArrangerOverdub ());
        this.addButton (ButtonID.REPEAT, "NOTE REPEAT", new FillModeNoteRepeatCommand<> (this.model, surface, this.maschine.hasMCUDisplay ()), MaschineControlSurface.NOTE_REPEAT, this.configuration::isNoteRepeatActive);

        // Ribbon
        this.addButton (ButtonID.F1, "PITCH", new RibbonCommand (this.model, surface, RIBBON_MODES_PITCH), MaschineControlSurface.PITCH, () -> this.isRibbonMode (new HashSet<> (RIBBON_MODES_PITCH)));
        this.addButton (ButtonID.F2, "MOD", new RibbonCommand (this.model, surface, RIBBON_MODES_CC), MaschineControlSurface.MOD, () -> this.isRibbonMode (new HashSet<> (RIBBON_MODES_CC)));
        this.addButton (ButtonID.F3, "PERFORM", new RibbonCommand (this.model, surface, RIBBON_MODES_MASTER_VOLUME), MaschineControlSurface.PERFORM, () -> this.isRibbonMode (new HashSet<> (RIBBON_MODES_MASTER_VOLUME)));
        this.addButton (ButtonID.F4, "NOTES", new RibbonCommand (this.model, surface, RIBBON_MODES_NOTE_REPEAT), MaschineControlSurface.NOTES, () -> this.isRibbonMode (new HashSet<> (RIBBON_MODES_NOTE_REPEAT)));

        this.addButton (ButtonID.FADER_TOUCH_1, "ENCODER PRESS", (event, velocity) -> {

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
        this.addButton (ButtonID.VOLUME, "VOLUME", new VolumePanSendCommand (this.model, surface), MaschineControlSurface.VOLUME, () -> Modes.isTrackMode (modeManager.getActiveID ()));
        this.addButton (ButtonID.SWING, "SWING", new SwingCommand (this.model, surface), MaschineControlSurface.SWING, () -> modeManager.isActive (Modes.POSITION, Modes.LOOP_START, Modes.LOOP_LENGTH));
        this.addButton (ButtonID.TEMPO_TOUCH, "TEMPO", new TempoCommand (this.model, surface), MaschineControlSurface.TEMPO, () -> modeManager.isActive (Modes.TEMPO));
        this.addButton (ButtonID.DEVICE, "PLUG-IN", (event, velocity) -> {

            if (this.maschine.hasMCUDisplay () || surface.isShiftPressed ())
                new PanelLayoutCommand<> (this.model, surface).executeNormal (event);
            else
                new ModeSelectCommand<> (this.model, surface, Modes.DEVICE_PARAMS).execute (event, velocity);

        }, MaschineControlSurface.PLUGIN, () -> this.maschine.hasMCUDisplay () || surface.isShiftPressed () ? this.model.getCursorDevice ().isWindowOpen () : modeManager.isActive (Modes.DEVICE_PARAMS));

        this.addButton (ButtonID.DEVICE_ON_OFF, "SAMPLING", new ConvertCommand<> (this.model, surface)
        {
            @Override
            protected void sliceToSampler ()
            {
                this.surface.setStopConsumed ();
                super.sliceToSampler ();
            }
        }, MaschineControlSurface.SAMPLING);

        // Browser
        final String fileLabel;
        if (this.maschine.hasCursorKeys ())
        {
            if (this.maschine == Maschine.STUDIO || this.maschine == Maschine.MK2)
                fileLabel = "ALL/SAVE";
            else
                fileLabel = "FILE";
        }
        else
            fileLabel = "PROJECT";

        this.addButton (ButtonID.ADD_TRACK, fileLabel, new ProjectButtonCommand (this.model, surface), MaschineControlSurface.PROJECT);
        this.addButton (ButtonID.ADD_EFFECT, this.maschine.hasCursorKeys () ? "SETTINGS" : "FAVORITES", new AddDeviceCommand (this.model, surface), MaschineControlSurface.FAVORITES);
        this.addButton (ButtonID.BROWSE, this.maschine == Maschine.STUDIO || this.maschine == Maschine.MK2 ? "BROWSE" : "BROWSER", new BrowserCommand<> (this.model, surface, ButtonID.SHIFT, ButtonID.SELECT)
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
        this.addButton (ButtonID.ACCENT, "ACCENT", new ToggleFixedVelCommand (this.model, surface), MaschineControlSurface.FIXED_VEL, this.configuration::isAccentActive);

        this.addButton (ButtonID.SCENE1, "SCENE", new ViewMultiSelectCommand<> (this.model, surface, Views.SCENE_PLAY), MaschineControlSurface.SCENE, () -> viewManager.isActive (Views.SCENE_PLAY));
        this.addButton (ButtonID.CLIP, "PATTERN", new ViewMultiSelectCommand<> (this.model, surface, Views.CLIP), MaschineControlSurface.PATTERN, () -> viewManager.isActive (Views.CLIP));
        this.addButton (ButtonID.NOTE, "EVENTS", new ModeSelectCommand<> (this.model, surface, Modes.NOTE, true), MaschineControlSurface.EVENTS, () -> modeManager.isActive (Modes.NOTE));
        this.addButton (ButtonID.TOGGLE_DEVICE, this.maschine == Maschine.STUDIO || this.maschine == Maschine.MK2 ? "NAVIGATE" : "VARIATION", new ViewMultiSelectCommand<> (this.model, surface, Views.DEVICE), MaschineControlSurface.VARIATION, () -> viewManager.isActive (Views.DEVICE));
        this.addButton (ButtonID.DUPLICATE, "DUPLICATE", NopCommand.INSTANCE, MaschineControlSurface.DUPLICATE);

        if (this.maschine.hasGroupButtons ())
        {
            this.addButton (ButtonID.SELECT, "SELECT", new MaschineSelectButtonCommand (this.model, surface), MaschineControlSurface.SELECT);

            this.addButton (ButtonID.SOLO, "SOLO", (event, velocity) -> {
                if (event == ButtonEvent.UP && surface.isShiftPressed ())
                {
                    surface.setStopConsumed ();
                    this.model.getProject ().clearSolo ();
                }
            }, MaschineControlSurface.SOLO);

            this.addButton (ButtonID.MUTE, "MUTE", (event, velocity) -> {
                if (event == ButtonEvent.UP && surface.isShiftPressed ())
                {
                    surface.setStopConsumed ();
                    this.model.getProject ().clearMute ();
                }
            }, MaschineControlSurface.MUTE);
        }
        else
        {
            this.addButton (ButtonID.SELECT, "SELECT", new ViewMultiSelectCommand<> (this.model, surface, Views.TRACK_SELECT), MaschineControlSurface.SELECT, () -> viewManager.isActive (Views.TRACK_SELECT));
            this.addButton (ButtonID.SOLO, "SOLO", new ViewMultiSelectCommand<> (this.model, surface, Views.TRACK_SOLO), MaschineControlSurface.SOLO, () -> viewManager.isActive (Views.TRACK_SOLO));
            this.addButton (ButtonID.MUTE, "MUTE", new ViewMultiSelectCommand<> (this.model, surface, Views.TRACK_MUTE), MaschineControlSurface.MUTE, () -> viewManager.isActive (Views.TRACK_MUTE));
        }

        final KeyboardCommand keyboardCommand = new KeyboardCommand (this.model, surface);
        this.addButton (ButtonID.ROW1_1, "PAD MODE", new PadModeCommand (keyboardCommand, this.model, surface), MaschineControlSurface.PAD_MODE, () -> viewManager.isActive (Views.DRUM));
        this.addButton (ButtonID.ROW1_2, "KEYBOARD", keyboardCommand, MaschineControlSurface.KEYBOARD, () -> viewManager.isActive (Views.PLAY));
        this.addButton (ButtonID.ROW1_3, "CHORDS", (event, velocity) -> {
            if (velocity == 0)
                ((PlayView) viewManager.get (Views.PLAY)).toggleChordMode ();
        }, MaschineControlSurface.CHORDS, ((PlayView) viewManager.get (Views.PLAY))::isChordMode);

        final DrumView drumView = (DrumView) viewManager.get (Views.DRUM);
        this.addButton (ButtonID.ROW1_4, "STEP", (event, velocity) -> {

            if (event == ButtonEvent.UP)
                drumView.toggleSequencerVisible ();

        }, MaschineControlSurface.STEP, drumView::isSequencerVisible);

        this.registerCursorKeys (surface);
        this.registerDisplayButtons (surface, modeManager);
        this.registerGroupButtons (surface);

        if (this.maschine == Maschine.STUDIO)
            this.registerMaschineStudioButtons (surface);

        // Register foot switches
        final int footswitches = this.maschine.getFootswitches ();
        if (footswitches >= 2)
        {
            this.addButton (ButtonID.FOOTSWITCH1, "Foot Controller (Tip)", new FootswitchCommand<> (this.model, surface, 0), MaschineControlSurface.FOOTSWITCH1_TIP);
            this.addButton (ButtonID.FOOTSWITCH2, "Foot Controller (Ring)", new FootswitchCommand<> (this.model, surface, 1), MaschineControlSurface.FOOTSWITCH1_RING);

            if (footswitches == 4)
            {
                this.addButton (ButtonID.FOOTSWITCH3, "Foot Controller 2 (Tip)", new FootswitchCommand<> (this.model, surface, 2), MaschineControlSurface.FOOTSWITCH2_TIP);
                this.addButton (ButtonID.FOOTSWITCH4, "Foot Controller 2 (Ring)", new FootswitchCommand<> (this.model, surface, 3), MaschineControlSurface.FOOTSWITCH2_RING);
            }
        }
    }


    private void registerCursorKeys (final MaschineControlSurface surface)
    {
        if (!this.maschine.hasCursorKeys ())
            return;

        this.addButton (ButtonID.ARROW_LEFT, "LEFT", new MaschineCursorCommand (Direction.LEFT, this.model, surface), MaschineControlSurface.CURSOR_LEFT, () -> this.getEncoderColor (ButtonID.ARROW_LEFT));
        this.addButton (ButtonID.ARROW_RIGHT, "RIGHT", new MaschineCursorCommand (Direction.RIGHT, this.model, surface), MaschineControlSurface.CURSOR_RIGHT, () -> this.getEncoderColor (ButtonID.ARROW_RIGHT));
        this.addButton (ButtonID.ARROW_UP, "UP", new ModeCursorCommand<> (Direction.UP, this.model, surface, false), MaschineControlSurface.CURSOR_UP, () -> this.getEncoderColor (ButtonID.ARROW_UP));
        this.addButton (ButtonID.ARROW_DOWN, "DOWN", new ModeCursorCommand<> (Direction.DOWN, this.model, surface, false), MaschineControlSurface.CURSOR_DOWN, () -> this.getEncoderColor (ButtonID.ARROW_DOWN));

        this.addButton (ButtonID.PAGE_LEFT, "PAGE LEFT", new PageCommand (Direction.LEFT, this.model, surface), MaschineControlSurface.PAGE_LEFT);
        this.addButton (ButtonID.PAGE_RIGHT, "PAGE RIGHT", new PageCommand (Direction.RIGHT, this.model, surface), MaschineControlSurface.PAGE_RIGHT);

        this.addButton (ButtonID.LAYOUT_ARRANGE, this.maschine == Maschine.STUDIO ? "ARRANGE" : "ARRANGER", new LayoutCommand<> (this.model, surface), MaschineControlSurface.ARRANGER);
        this.addButton (ButtonID.MIXER, this.maschine == Maschine.STUDIO ? "MIX" : "MIXER", new PaneCommand<> (Panels.MIXER, this.model, surface), MaschineControlSurface.MIXER);
        this.addButton (ButtonID.DRUM, this.maschine == Maschine.MK2 ? "CONTROL" : "CHANNEL", new AddTrackCommand<> (this.model, surface, null, ButtonID.STOP), MaschineControlSurface.CHANNEL);
    }


    private void registerDisplayButtons (final MaschineControlSurface surface, final ModeManager modeManager)
    {
        if (!this.maschine.hasMCUDisplay ())
            return;

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


    private void registerGroupButtons (final MaschineControlSurface surface)
    {
        if (!this.maschine.hasGroupButtons ())
            return;

        for (int i = 0; i < 8; i++)
        {
            final GroupButtonCommand<MaschineControlSurface, MaschineConfiguration> command = new GroupButtonCommand<> (this.model, surface, i);
            this.addButton (ButtonID.get (ButtonID.ROW3_1, i), "Bank " + (i + 1), command, MaschineControlSurface.BANK_1 + i, command::getButtonColor);
        }
    }


    private void registerMaschineStudioButtons (final MaschineControlSurface surface)
    {
        this.addButton (ButtonID.METRONOME, "METRO", new MetronomeCommand<> (this.model, surface, false), MaschineControlSurface.METRO, () -> this.model.getTransport ().isMetronomeOn ());

        this.addButton (ButtonID.UNDO, "UNDO", this.createShiftViewFunction (surface, 0, 2), MaschineControlSurface.EDIT_UNDO);
        this.addButton (ButtonID.REDO, "REDO", this.createShiftViewFunction (surface, 1, 3), MaschineControlSurface.EDIT_REDO);
        this.addButton (ButtonID.QUANTIZE, "QUANTIZE", this.createShiftViewFunction (surface, 4, 5), MaschineControlSurface.EDIT_QUANTIZE);
        this.addButton (ButtonID.CONTROL, "CLEAR", this.createShiftViewFunction (surface, 8, 9), MaschineControlSurface.EDIT_CLEAR);

        this.addButton (ButtonID.COPY, "COPY", this.createShiftViewFunction (surface, 10, 10), MaschineControlSurface.EDIT_COPY);
        this.addButton (ButtonID.PASTE, "PASTE", this.createShiftViewFunction (surface, 11, 11), MaschineControlSurface.EDIT_PASTE);
        this.addButton (ButtonID.NUDGE_MINUS, "NOTE", this.createShiftViewFunction (surface, 13, 12), MaschineControlSurface.EDIT_NOTE);
        this.addButton (ButtonID.NUDGE_PLUS, "NUDGE", this.createShiftViewFunction (surface, 15, 14), MaschineControlSurface.EDIT_NUDGE);
    }


    private TriggerCommand createShiftViewFunction (final MaschineControlSurface surface, final int padIndex, final int shiftPadIndex)
    {
        return (event, velocity) -> {
            if (event == ButtonEvent.UP)
                this.shiftView.executeFunction (surface.isShiftPressed () ? shiftPadIndex : padIndex);
        };
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

            case SEND1, SEND2, SEND3, SEND4, SEND5, SEND6, SEND7, SEND8:
                return surface.isPressed (arrowButton) ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_YELLOW;

            case DEVICE_PARAMS, USER:
                return surface.isPressed (arrowButton) ? MaschineColorManager.COLOR_PINK : MaschineColorManager.COLOR_PINK_LO;

            case BROWSER:
                return surface.isPressed (arrowButton) ? MaschineColorManager.COLOR_AMBER : MaschineColorManager.COLOR_AMBER_LO;

            case NOTE:
                return surface.isPressed (arrowButton) ? MaschineColorManager.COLOR_GREEN : MaschineColorManager.COLOR_GREEN_LO;

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
        final ViewManager viewManager = surface.getViewManager ();

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

        // Enable aftertouch
        final Views [] views =
        {
            Views.PLAY,
            Views.DRUM,
        };
        for (final Views viewID: views)
        {
            final IView view = viewManager.get (viewID);
            view.registerAftertouchCommand (new AftertouchViewCommand<> (view, this.model, surface));
        }

        if (this.maschine == Maschine.STUDIO)
        {
            final IHwAbsoluteKnob masterKnob = this.addAbsoluteKnob (ContinuousID.MONITOR_KNOB, "Encoder", null, MaschineControlSurface.MONITOR_ENCODER);
            this.encoderManager = new StudioEncoderModeManager (masterKnob, this.model, surface);
            masterKnob.bind (this.encoderManager);

            final MaschineMonitorEncoderCommand<MaschineControlSurface, MaschineConfiguration> encoderCommandMaster = new MaschineMonitorEncoderCommand<> (this.encoderManager, EncoderMode.MASTER_VOLUME, this.model, surface);
            this.addButton (ButtonID.ROW4_1, "MST", encoderCommandMaster, MaschineControlSurface.MONITOR_MST, encoderCommandMaster::isLit);
            final MaschineMonitorEncoderCommand<MaschineControlSurface, MaschineConfiguration> encoderCommandSelectedTrack = new MaschineMonitorEncoderCommand<> (this.encoderManager, EncoderMode.SELECTED_TRACK_VOLUME, this.model, surface);
            this.addButton (ButtonID.ROW4_2, "GRP", encoderCommandSelectedTrack, MaschineControlSurface.MONITOR_GRP, encoderCommandSelectedTrack::isLit);
            final MaschineMonitorEncoderCommand<MaschineControlSurface, MaschineConfiguration> encoderCommandMetronome = new MaschineMonitorEncoderCommand<> (this.encoderManager, EncoderMode.METRONOME_VOLUME, this.model, surface);
            this.addButton (ButtonID.ROW4_3, "SND", encoderCommandMetronome, MaschineControlSurface.MONITOR_SND, encoderCommandMetronome::isLit);
            final MaschineMonitorEncoderCommand<MaschineControlSurface, MaschineConfiguration> encoderCommandCue = new MaschineMonitorEncoderCommand<> (this.encoderManager, EncoderMode.CUE_VOLUME, this.model, surface);
            this.addButton (ButtonID.ROW4_4, "CUE", encoderCommandCue, MaschineControlSurface.MONITOR_CUE, encoderCommandCue::isLit);

            final MaschineMonitorEncoderCommand<MaschineControlSurface, MaschineConfiguration> encoderCommandMasterPan = new MaschineMonitorEncoderCommand<> (this.encoderManager, EncoderMode.MASTER_PANORAMA, this.model, surface);
            this.addButton (ButtonID.ROW4_5, "IN1", encoderCommandMasterPan, MaschineControlSurface.MONITOR_IN1, encoderCommandMasterPan::isLit);
            final MaschineMonitorEncoderCommand<MaschineControlSurface, MaschineConfiguration> encoderCommandSelectedTrackPan = new MaschineMonitorEncoderCommand<> (this.encoderManager, EncoderMode.SELECTED_TRACK_PANORAMA, this.model, surface);
            this.addButton (ButtonID.ROW4_6, "IN2", encoderCommandSelectedTrackPan, MaschineControlSurface.MONITOR_IN2, encoderCommandSelectedTrackPan::isLit);
            final MaschineMonitorEncoderCommand<MaschineControlSurface, MaschineConfiguration> encoderCommandCueMix = new MaschineMonitorEncoderCommand<> (this.encoderManager, EncoderMode.CUE_MIX, this.model, surface);
            this.addButton (ButtonID.ROW4_8, "IN4", encoderCommandCueMix, MaschineControlSurface.MONITOR_IN4, encoderCommandCueMix::isLit);

            this.addButton (ButtonID.TOGGLE_VU, "IN3", (event, value) -> {
                if (event == ButtonEvent.UP)
                    this.encoderManager.toggleMode ();
            }, MaschineControlSurface.MONITOR_IN3, this.encoderManager::isParameterMode);

            // Activate the default mode
            this.encoderManager.setActiveEncoderMode (EncoderMode.MASTER_VOLUME);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        switch (this.maschine)
        {
            case MK2:
                this.layoutMk2 ();
                break;
            case MK3, PLUS:
                this.layoutMk3 ();
                break;
            case STUDIO:
                this.layoutStudio ();
                break;
            case MIKRO_MK3:
                this.layoutMikroMk3 ();
                break;
            default:
                // Not used
                break;
        }
    }


    private void layoutMk2 ()
    {
        final MaschineControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (425.5, 604.5, 76.25, 79.0);
        surface.getButton (ButtonID.PAD2).setBounds (516.25, 605.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD3).setBounds (606.75, 605.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD4).setBounds (694.75, 605.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD5).setBounds (425.5, 515.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD6).setBounds (516.25, 515.5, 76.25, 79.0);
        surface.getButton (ButtonID.PAD7).setBounds (606.75, 515.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD8).setBounds (694.75, 515.5, 76.25, 79.0);
        surface.getButton (ButtonID.PAD9).setBounds (425.5, 425.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD10).setBounds (516.25, 426.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD11).setBounds (606.75, 426.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD12).setBounds (694.75, 425.5, 76.25, 79.0);
        surface.getButton (ButtonID.PAD13).setBounds (425.5, 335.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD14).setBounds (516.25, 336.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD15).setBounds (606.75, 336.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD16).setBounds (694.75, 335.75, 76.25, 79.0);
        surface.getButton (ButtonID.SHIFT).setBounds (96.75, 619.0, 56.0, 18.0);
        surface.getButton (ButtonID.PLAY).setBounds (24.75, 650.0, 55.75, 32.0);
        surface.getButton (ButtonID.RECORD).setBounds (96.75, 650.0, 55.75, 32.0);
        surface.getButton (ButtonID.LOOP).setBounds (24.75, 619.0, 56.0, 18.0);
        surface.getButton (ButtonID.DELETE).setBounds (166.25, 650.0, 55.75, 32.0);
        surface.getButton (ButtonID.TAP_TEMPO).setBounds (166.25, 619.0, 56.0, 18.0);
        surface.getButton (ButtonID.FLIP).setBounds (236.5, 619.0, 56.0, 18.0);
        surface.getButton (ButtonID.AUTOMATION).setBounds (94.0, 236.25, 55.75, 32.0);
        surface.getButton (ButtonID.OVERDUB).setBounds (231.5, 406.75, 58.0, 19.0);
        surface.getButton (ButtonID.REPEAT).setBounds (231.5, 337.25, 58.0, 46.25);
        surface.getButton (ButtonID.FADER_TOUCH_1).setBounds (123.25, 303.25, 69.5, 22.75);
        surface.getButton (ButtonID.VOLUME).setBounds (27.75, 337.25, 58.0, 18.0);
        surface.getButton (ButtonID.SWING).setBounds (27.75, 373.0, 58.0, 18.0);
        surface.getButton (ButtonID.TEMPO_TOUCH).setBounds (27.75, 407.75, 58.0, 18.0);
        surface.getButton (ButtonID.DEVICE_ON_OFF).setBounds (95.75, 107.25, 55.75, 38.5);
        surface.getButton (ButtonID.ADD_TRACK).setBounds (27.25, 236.25, 55.75, 32.0);
        surface.getButton (ButtonID.BROWSE).setBounds (28.75, 107.25, 55.75, 38.5);
        surface.getButton (ButtonID.SCENE1).setBounds (346.5, 335.5, 58.0, 34.0);
        surface.getButton (ButtonID.CLIP).setBounds (346.5, 379.25, 58.0, 34.0);
        surface.getButton (ButtonID.NOTE).setBounds (346.5, 561.5, 58.0, 34.0);
        surface.getButton (ButtonID.TOGGLE_DEVICE).setBounds (346.5, 466.75, 58.0, 41.25);
        surface.getButton (ButtonID.DUPLICATE).setBounds (347.25, 515.0, 58.0, 34.0);
        surface.getButton (ButtonID.SOLO).setBounds (346.5, 605.25, 58.0, 34.0);
        surface.getButton (ButtonID.MUTE).setBounds (346.5, 649.0, 58.0, 34.0);
        surface.getButton (ButtonID.ROW1_1).setBounds (346.5, 423.0, 58.0, 34.0);
        surface.getButton (ButtonID.ROW1_4).setBounds (95.75, 21.25, 55.75, 23.0);
        surface.getButton (ButtonID.ARROW_LEFT).setBounds (121.5, 408.25, 32.75, 17.5);
        surface.getButton (ButtonID.ARROW_RIGHT).setBounds (157.0, 408.25, 32.75, 17.5);
        surface.getButton (ButtonID.PAGE_LEFT).setBounds (28.75, 157.75, 55.75, 23.0);
        surface.getButton (ButtonID.PAGE_RIGHT).setBounds (95.75, 157.75, 55.75, 23.0);
        surface.getButton (ButtonID.DRUM).setBounds (28.75, 21.25, 55.75, 23.0);
        surface.getButton (ButtonID.ROW2_1).setBounds (179.25, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_2).setBounds (254.25, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_3).setBounds (329.25, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_4).setBounds (404.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_5).setBounds (479.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_6).setBounds (554.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_7).setBounds (629.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_8).setBounds (704.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW3_1).setBounds (26.25, 480.0, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_2).setBounds (96.75, 480.0, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_3).setBounds (167.0, 480.0, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_4).setBounds (237.5, 480.0, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_5).setBounds (26.25, 526.0, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_6).setBounds (96.75, 526.0, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_7).setBounds (167.0, 526.0, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_8).setBounds (237.5, 526.0, 53.75, 35.0);

        surface.getContinuous (ContinuousID.MASTER_KNOB).setBounds (125.0, 337.25, 64.0, 63.0);
        surface.getContinuous (ContinuousID.KNOB1).setBounds (183.5, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB2).setBounds (259.0, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB3).setBounds (334.75, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB4).setBounds (410.25, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB5).setBounds (486.0, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB6).setBounds (561.5, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB7).setBounds (637.0, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB8).setBounds (712.75, 226.25, 53.75, 49.25);

        surface.getTextDisplay ().getHardwareDisplay ().setBounds (182.75, 111.75, 591.75, 64.5);
    }


    private void layoutStudio ()
    {
        final MaschineControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (425.5, 590.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD2).setBounds (516.25, 590.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD3).setBounds (606.75, 590.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD4).setBounds (694.75, 590.75, 75.75, 79.0);
        surface.getButton (ButtonID.PAD5).setBounds (425.5, 500.5, 76.25, 79.0);
        surface.getButton (ButtonID.PAD6).setBounds (516.25, 501.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD7).setBounds (606.75, 501.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD8).setBounds (694.75, 501.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD9).setBounds (425.5, 410.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD10).setBounds (516.25, 411.5, 76.25, 79.0);
        surface.getButton (ButtonID.PAD11).setBounds (606.75, 411.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD12).setBounds (694.75, 411.0, 76.25, 79.0);
        surface.getButton (ButtonID.PAD13).setBounds (425.5, 321.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD14).setBounds (516.25, 321.75, 76.25, 79.0);
        surface.getButton (ButtonID.PAD15).setBounds (606.75, 322.25, 76.25, 79.0);
        surface.getButton (ButtonID.PAD16).setBounds (694.75, 321.25, 76.25, 79.0);
        surface.getButton (ButtonID.SHIFT).setBounds (837.25, 637.75, 56.25, 32.0);
        surface.getButton (ButtonID.PLAY).setBounds (24.0, 636.25, 56.75, 32.0);
        surface.getButton (ButtonID.RECORD).setBounds (96.0, 636.25, 56.75, 32.0);
        surface.getButton (ButtonID.LOOP).setBounds (24.75, 604.5, 56.0, 18.0);
        surface.getButton (ButtonID.DELETE).setBounds (165.5, 636.25, 56.75, 32.0);
        surface.getButton (ButtonID.TAP_TEMPO).setBounds (27.75, 318.0, 56.25, 37.0);
        surface.getButton (ButtonID.FLIP).setBounds (236.5, 604.5, 56.0, 18.0);
        surface.getButton (ButtonID.NEW).setBounds (168.5, 318.0, 56.25, 37.5);
        surface.getButton (ButtonID.AUTOMATION).setBounds (94.75, 236.75, 55.75, 32.0);
        surface.getButton (ButtonID.OVERDUB).setBounds (1052.0, 637.75, 56.25, 32.0);
        surface.getButton (ButtonID.REPEAT).setBounds (239.0, 318.0, 56.25, 37.0);
        surface.getButton (ButtonID.FADER_TOUCH_1).setBounds (1037.0, 426.5, 69.5, 22.75);
        surface.getButton (ButtonID.DEVICE).setBounds (94.75, 21.25, 55.75, 23.0);
        surface.getButton (ButtonID.DEVICE_ON_OFF).setBounds (94.75, 107.25, 55.75, 38.5);
        surface.getButton (ButtonID.ADD_TRACK).setBounds (27.75, 236.75, 55.75, 32.0);
        surface.getButton (ButtonID.BROWSE).setBounds (27.75, 107.25, 55.75, 38.5);
        surface.getButton (ButtonID.SCENE1).setBounds (347.25, 321.0, 58.0, 34.0);
        surface.getButton (ButtonID.CLIP).setBounds (347.25, 365.75, 58.0, 34.0);
        surface.getButton (ButtonID.NOTE).setBounds (165.5, 604.5, 56.0, 18.0);
        surface.getButton (ButtonID.TOGGLE_DEVICE).setBounds (347.25, 455.0, 58.0, 34.0);
        surface.getButton (ButtonID.DUPLICATE).setBounds (347.25, 499.5, 58.0, 34.0);
        surface.getButton (ButtonID.SELECT).setBounds (347.25, 544.25, 58.0, 34.0);
        surface.getButton (ButtonID.SOLO).setBounds (347.25, 588.75, 58.0, 34.0);
        surface.getButton (ButtonID.MUTE).setBounds (347.25, 633.5, 58.0, 34.0);
        surface.getButton (ButtonID.ROW1_1).setBounds (347.25, 410.25, 58.0, 34.0);
        surface.getButton (ButtonID.ROW1_4).setBounds (98.25, 318.0, 56.25, 37.0);
        surface.getButton (ButtonID.ARROW_LEFT).setBounds (908.75, 637.75, 56.25, 32.0);
        surface.getButton (ButtonID.ARROW_RIGHT).setBounds (980.5, 637.75, 56.25, 32.0);
        surface.getButton (ButtonID.PAGE_LEFT).setBounds (27.75, 157.75, 55.75, 23.0);
        surface.getButton (ButtonID.PAGE_RIGHT).setBounds (94.75, 157.75, 55.75, 23.0);
        surface.getButton (ButtonID.LAYOUT_ARRANGE).setBounds (27.75, 56.5, 55.75, 38.5);
        surface.getButton (ButtonID.MIXER).setBounds (94.75, 56.5, 55.75, 38.5);
        surface.getButton (ButtonID.DRUM).setBounds (27.75, 21.25, 55.75, 23.0);
        surface.getButton (ButtonID.ROW2_1).setBounds (179.25, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_2).setBounds (254.25, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_3).setBounds (329.25, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_4).setBounds (404.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_5).setBounds (479.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_6).setBounds (554.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_7).setBounds (629.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW2_8).setBounds (704.0, 21.25, 66.25, 27.25);
        surface.getButton (ButtonID.ROW3_1).setBounds (28.5, 438.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_2).setBounds (98.25, 438.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_3).setBounds (167.75, 438.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_4).setBounds (239.75, 438.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_5).setBounds (28.5, 484.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_6).setBounds (98.25, 484.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_7).setBounds (167.75, 484.75, 53.75, 35.0);
        surface.getButton (ButtonID.ROW3_8).setBounds (239.75, 484.75, 53.75, 35.0);
        surface.getButton (ButtonID.METRONOME).setBounds (96.0, 604.5, 56.0, 18.0);
        surface.getButton (ButtonID.UNDO).setBounds (837.25, 366.25, 56.25, 32.0);
        surface.getButton (ButtonID.REDO).setBounds (908.75, 366.25, 56.25, 32.0);
        surface.getButton (ButtonID.QUANTIZE).setBounds (980.5, 366.25, 56.25, 32.0);
        surface.getButton (ButtonID.CONTROL).setBounds (1052.0, 366.25, 56.25, 32.0);
        surface.getButton (ButtonID.COPY).setBounds (837.25, 323.0, 56.25, 32.0);
        surface.getButton (ButtonID.PASTE).setBounds (908.75, 323.0, 56.25, 32.0);
        surface.getButton (ButtonID.NUDGE_MINUS).setBounds (980.5, 323.0, 56.25, 32.0);
        surface.getButton (ButtonID.NUDGE_PLUS).setBounds (1052.0, 323.0, 56.25, 32.0);
        surface.getButton (ButtonID.ROW4_1).setBounds (1033.75, 44.5, 27.5, 22.75);
        surface.getButton (ButtonID.ROW4_2).setBounds (1033.75, 89.5, 27.5, 22.75);
        surface.getButton (ButtonID.ROW4_3).setBounds (1033.75, 134.25, 27.5, 22.75);
        surface.getButton (ButtonID.ROW4_4).setBounds (1033.75, 179.25, 27.5, 22.75);
        surface.getButton (ButtonID.ROW4_5).setBounds (930.75, 44.5, 27.5, 22.75);
        surface.getButton (ButtonID.ROW4_6).setBounds (930.75, 89.5, 27.5, 22.75);
        surface.getButton (ButtonID.ROW4_8).setBounds (930.75, 179.25, 27.5, 22.75);
        surface.getButton (ButtonID.TOGGLE_VU).setBounds (930.75, 134.25, 27.5, 22.75);

        surface.getContinuous (ContinuousID.MASTER_KNOB).setBounds (905.0, 427.0, 143.5, 138.5);
        surface.getContinuous (ContinuousID.KNOB1).setBounds (183.5, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB2).setBounds (259.0, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB3).setBounds (334.75, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB4).setBounds (410.25, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB5).setBounds (486.0, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB6).setBounds (561.5, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB7).setBounds (637.0, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.KNOB8).setBounds (712.75, 226.25, 53.75, 49.25);
        surface.getContinuous (ContinuousID.MONITOR_KNOB).setBounds (962.0, 217.0, 70.75, 69.0);

        surface.getTextDisplay ().getHardwareDisplay ().setBounds (182.75, 111.75, 591.75, 64.5);
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
        surface.getButton (ButtonID.SHIFT).setBounds (236.5, 697.75, 56.0, 32.0);
        surface.getButton (ButtonID.LOOP).setBounds (24.75, 666.75, 56.0, 18.0);
        surface.getButton (ButtonID.DELETE).setBounds (96.75, 666.75, 56.0, 18.0);
        surface.getButton (ButtonID.TAP_TEMPO).setBounds (166.25, 666.75, 56.0, 18.0);
        surface.getButton (ButtonID.FLIP).setBounds (236.5, 666.75, 56.0, 18.0);
        surface.getButton (ButtonID.NEW).setBounds (95.75, 237.25, 55.75, 32.0);
        surface.getButton (ButtonID.AUTOMATION).setBounds (28.75, 237.25, 55.75, 32.0);
        surface.getButton (ButtonID.OVERDUB).setBounds (233.75, 402.0, 58.0, 19.0);
        surface.getButton (ButtonID.REPEAT).setBounds (233.75, 343.75, 58.0, 46.25);
        surface.getButton (ButtonID.F1).setBounds (24.0, 464.75, 58.0, 19.0);
        surface.getButton (ButtonID.F2).setBounds (96.0, 465.5, 57.25, 18.25);
        surface.getButton (ButtonID.F3).setBounds (165.5, 465.5, 57.25, 18.25);
        surface.getButton (ButtonID.F4).setBounds (233.75, 465.5, 57.25, 18.25);
        surface.getButton (ButtonID.FADER_TOUCH_1).setBounds (51.25, 315.0, 69.5, 22.75);
        surface.getButton (ButtonID.VOLUME).setBounds (164.5, 343.75, 58.0, 18.0);
        surface.getButton (ButtonID.SWING).setBounds (164.5, 373.25, 58.0, 18.0);
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
        surface.getButton (ButtonID.SELECT).setBounds (346.5, 609.25, 58.0, 34.0);
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
        surface.getButton (ButtonID.TAP_TEMPO).setBounds (167.75, 350.5, 56.0, 18.0);
        surface.getButton (ButtonID.FLIP).setBounds (238.0, 350.5, 56.0, 18.0);
        surface.getButton (ButtonID.NEW).setBounds (26.25, 280.5, 55.75, 32.0);
        surface.getButton (ButtonID.AUTOMATION).setBounds (98.25, 280.5, 55.75, 32.0);
        surface.getButton (ButtonID.OVERDUB).setBounds (167.75, 280.5, 55.75, 32.0);
        surface.getButton (ButtonID.REPEAT).setBounds (238.0, 280.5, 55.75, 32.0);
        surface.getButton (ButtonID.F1).setBounds (26.25, 170.0, 58.0, 19.0);
        surface.getButton (ButtonID.F2).setBounds (98.25, 170.75, 57.25, 18.25);
        surface.getButton (ButtonID.F3).setBounds (167.75, 170.75, 57.25, 18.25);
        surface.getButton (ButtonID.F4).setBounds (238.0, 170.75, 57.25, 18.25);
        surface.getButton (ButtonID.FADER_TOUCH_1).setBounds (74.0, 21.25, 69.5, 22.75);
        surface.getButton (ButtonID.VOLUME).setBounds (166.0, 25.75, 58.0, 19.0);
        surface.getButton (ButtonID.SWING).setBounds (166.0, 56.75, 58.0, 19.0);
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
        surface.getButton (ButtonID.SELECT).setBounds (348.0, 293.0, 58.0, 34.0);
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

        final MaschineControlSurface surface = this.getSurface ();
        final TouchstripCommand command = (TouchstripCommand) surface.getContinuous (ContinuousID.CROSSFADER).getCommand ();
        if (command != null)
            command.updateValue ();

        if (this.maschine == Maschine.STUDIO)
        {
            // Update main VU
            final IMidiOutput midiOutput = surface.getMidiOutput ();

            final int value;
            if (this.encoderManager.isParameterMode ())
            {
                final EncoderMode activeEncoderMode = this.encoderManager.getActiveEncoderMode ();
                switch (activeEncoderMode)
                {
                    case MASTER_VOLUME:
                        value = this.valueChanger.toMidiValue (this.model.getMasterTrack ().getVolume ());
                        break;
                    case MASTER_PANORAMA:
                        value = this.valueChanger.toMidiValue (this.model.getMasterTrack ().getPan ());
                        break;
                    case SELECTED_TRACK_VOLUME, SELECTED_TRACK_PANORAMA:
                        final ITrack track;
                        final Optional<ITrack> trackOptional = this.model.getTrackBank ().getSelectedItem ();
                        if (trackOptional.isPresent ())
                            track = trackOptional.get ();
                        else
                            track = EmptyTrack.INSTANCE;
                        value = this.valueChanger.toMidiValue (activeEncoderMode == EncoderMode.SELECTED_TRACK_VOLUME ? track.getVolume () : track.getPan ());
                        break;
                    case CUE_VOLUME:
                        value = this.valueChanger.toMidiValue (this.model.getProject ().getCueVolume ());
                        break;
                    case CUE_MIX:
                        value = this.valueChanger.toMidiValue (this.model.getProject ().getCueMix ());
                        break;
                    case METRONOME_VOLUME:
                        value = this.valueChanger.toMidiValue (this.model.getTransport ().getMetronomeVolume ());
                        break;
                    default:
                        value = 0;
                        break;
                }
            }
            else
            {
                final ITrack track;
                if (this.encoderManager.isActiveEncoderMode (EncoderMode.SELECTED_TRACK_VOLUME))
                {
                    final Optional<ITrack> trackOptional = this.model.getTrackBank ().getSelectedItem ();
                    if (trackOptional.isPresent ())
                        track = trackOptional.get ();
                    else
                        track = EmptyTrack.INSTANCE;
                }
                else
                    track = this.model.getMasterTrack ();
                value = this.valueChanger.toMidiValue (track.getVu ());
            }

            midiOutput.sendCC (MaschineControlSurface.MONITOR_ENCODER, value);
        }
    }


    private boolean isRibbonMode (final Set<RibbonMode> modes)
    {
        return modes.contains (this.configuration.getRibbonMode ());
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        return buttonID == ButtonID.ROW2_7 ? BindType.NOTE : BindType.CC;
    }
}
