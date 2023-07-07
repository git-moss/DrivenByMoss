// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.core.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.core.command.trigger.GroupButtonCommand;
import de.mossgrabers.controller.ni.maschine.core.command.trigger.MaschineMonitorEncoderCommand;
import de.mossgrabers.controller.ni.maschine.core.controller.EncoderModeManager;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamAuxCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamControlCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamLevelCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamMacroCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamMuteCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamPageLeftCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamPageRightCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamSessionViewCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamSoloCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamStartSceneCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamSwingCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamTapTempoCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamViewButtonCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamViewCommand;
import de.mossgrabers.controller.ni.maschine.jam.controller.FaderConfig;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.controller.ni.maschine.jam.mode.IMaschineJamMode;
import de.mossgrabers.controller.ni.maschine.jam.mode.MaschineJamPanMode;
import de.mossgrabers.controller.ni.maschine.jam.mode.MaschineJamParameterMode;
import de.mossgrabers.controller.ni.maschine.jam.mode.MaschineJamSendMode;
import de.mossgrabers.controller.ni.maschine.jam.mode.MaschineJamTrackMode;
import de.mossgrabers.controller.ni.maschine.jam.mode.MaschineJamUserMode;
import de.mossgrabers.controller.ni.maschine.jam.mode.MaschineJamVolumeMode;
import de.mossgrabers.controller.ni.maschine.jam.view.AccentView;
import de.mossgrabers.controller.ni.maschine.jam.view.ChordsView;
import de.mossgrabers.controller.ni.maschine.jam.view.Drum4View;
import de.mossgrabers.controller.ni.maschine.jam.view.Drum64View;
import de.mossgrabers.controller.ni.maschine.jam.view.Drum8View;
import de.mossgrabers.controller.ni.maschine.jam.view.DrumView;
import de.mossgrabers.controller.ni.maschine.jam.view.NoteRepeatView;
import de.mossgrabers.controller.ni.maschine.jam.view.PianoView;
import de.mossgrabers.controller.ni.maschine.jam.view.PlayView;
import de.mossgrabers.controller.ni.maschine.jam.view.PolySequencerView;
import de.mossgrabers.controller.ni.maschine.jam.view.RaindropsView;
import de.mossgrabers.controller.ni.maschine.jam.view.SequencerView;
import de.mossgrabers.controller.ni.maschine.jam.view.SessionView;
import de.mossgrabers.controller.ni.maschine.jam.view.ShiftView;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.command.trigger.FootswitchCommand;
import de.mossgrabers.framework.command.trigger.clip.DoubleCommand;
import de.mossgrabers.framework.command.trigger.clip.FillModeNoteRepeatCommand;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.command.trigger.transport.AutomationCommand;
import de.mossgrabers.framework.command.trigger.transport.ConfiguredRecordCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.view.ToggleShiftViewCommand;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IBrowser;
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
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.view.BrowserView;
import de.mossgrabers.framework.view.ShuffleView;
import de.mossgrabers.framework.view.TempoView;
import de.mossgrabers.framework.view.Views;

import java.util.Optional;
import java.util.function.BooleanSupplier;


/**
 * Support for the NI Maschine controller series.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamControllerSetup extends AbstractControllerSetup<MaschineJamControlSurface, MaschineJamConfiguration>
{
    private static final Views []                                                   SEQUENCER_VIEWS =
    {
        Views.SEQUENCER,
        Views.POLY_SEQUENCER,
        Views.RAINDROPS,
        Views.DRUM,
        Views.DRUM4,
        Views.DRUM8
    };

    private static final Views []                                                   PLAY_VIEWS      =
    {
        Views.PLAY,
        Views.CHORDS,
        Views.PIANO,
        Views.DRUM64
    };

    private EncoderModeManager<MaschineJamControlSurface, MaschineJamConfiguration> encoderManager;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public MaschineJamControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new MaschineColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new MaschineJamConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        if (OperatingSystem.get () == OperatingSystem.LINUX)
            throw new FrameworkException ("Maschine Jam is not supported on Linux since there is no Native Instruments DAW Integration Host.");

        super.init ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.enableDrum64Device ();
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Pads", "80????", "90????");
        final MaschineJamControlSurface surface = new MaschineJamControlSurface (this.host, this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final MaschineJamControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.register (Modes.TRACK, new MaschineJamTrackMode (surface, this.model));
        modeManager.register (Modes.VOLUME, new MaschineJamVolumeMode (surface, this.model));
        modeManager.register (Modes.PAN, new MaschineJamPanMode (surface, this.model));
        for (int i = 0; i < 8; i++)
            modeManager.register (Modes.get (Modes.SEND1, i), new MaschineJamSendMode (i, surface, this.model));

        modeManager.register (Modes.DEVICE_PARAMS, new MaschineJamParameterMode (surface, this.model));
        modeManager.register (Modes.USER, new MaschineJamUserMode (surface, this.model));

        modeManager.setDefaultID (Modes.VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final MaschineJamControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        viewManager.register (Views.SHIFT, new ShiftView (surface, this.model));
        viewManager.register (Views.REPEAT_NOTE, new NoteRepeatView (surface, this.model));
        viewManager.register (Views.SESSION, new SessionView (surface, this.model));
        viewManager.register (Views.PLAY, new PlayView (surface, this.model));
        viewManager.register (Views.CHORDS, new ChordsView (surface, this.model));
        viewManager.register (Views.PIANO, new PianoView (surface, this.model));
        viewManager.register (Views.DRUM64, new Drum64View (surface, this.model));
        viewManager.register (Views.DRUM, new DrumView (surface, this.model));
        viewManager.register (Views.DRUM4, new Drum4View (surface, this.model));
        viewManager.register (Views.DRUM8, new Drum8View (surface, this.model));
        viewManager.register (Views.RAINDROPS, new RaindropsView (surface, this.model));
        viewManager.register (Views.SEQUENCER, new SequencerView (surface, this.model));
        viewManager.register (Views.POLY_SEQUENCER, new PolySequencerView (surface, this.model));
        viewManager.register (Views.TEMPO, new TempoView<> (surface, this.model, MaschineColorManager.COLOR_BLUE, MaschineColorManager.COLOR_WHITE, MaschineColorManager.COLOR_BLACK));
        viewManager.register (Views.SHUFFLE, new ShuffleView<> (surface, this.model, MaschineColorManager.COLOR_PINK, MaschineColorManager.COLOR_WHITE, MaschineColorManager.COLOR_BLACK));
        viewManager.register (Views.BROWSER, new BrowserView<> (surface, this.model));
        viewManager.register (Views.CONTROL, new AccentView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        final MaschineJamControlSurface surface = this.getSurface ();

        this.configuration.registerDeactivatedItemsHandler (this.model);
        this.createScaleObservers (this.configuration);
        this.createNoteRepeatObservers (this.configuration, surface);

        this.model.getBrowser ().addActiveObserver (isActive -> {

            final ViewManager viewManager = this.getSurface ().getViewManager ();
            if (isActive.booleanValue ())
            {
                viewManager.setTemporary (Views.BROWSER);
                this.encoderManager.enableTemporaryEncodeMode (EncoderMode.TEMPORARY_BROWSER);
            }
            else
            {
                viewManager.restore ();
                this.encoderManager.disableTemporaryEncodeMode ();
            }

        });
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final MaschineJamControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        final ViewManager viewManager = surface.getViewManager ();

        final ITransport t = this.model.getTransport ();

        this.addButton (ButtonID.SHIFT, "SHIFT", new ToggleShiftViewCommand<> (this.model, surface), -1);
        this.addButton (ButtonID.SELECT, "SELECT", NopCommand.INSTANCE, MaschineJamControlSurface.SELECT);

        this.addButton (ButtonID.DELETE, "CLEAR", NopCommand.INSTANCE, MaschineJamControlSurface.CLEAR);
        this.addButton (ButtonID.DUPLICATE, "DUPLICATE", new DoubleCommand<> (this.model, surface, true), MaschineJamControlSurface.DUPLICATE);

        this.addButton (ButtonID.PLAY, "PLAY", new PlayCommand<> (this.model, surface)
        {
            @Override
            protected void executeShifted ()
            {
                this.transport.restart ();
            }
        }, MaschineJamControlSurface.PLAY, t::isPlaying, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);

        final ConfiguredRecordCommand<MaschineJamControlSurface, MaschineJamConfiguration> recordCommand = new ConfiguredRecordCommand<> (this.model, surface);
        this.addButton (ButtonID.RECORD, "REC", recordCommand, MaschineJamControlSurface.RECORD, (BooleanSupplier) recordCommand::isLit);

        this.addButton (ButtonID.PAGE_LEFT, "PAGE LEFT", new MaschineJamPageLeftCommand (this.model, surface), MaschineJamControlSurface.LEFT);
        this.addButton (ButtonID.PAGE_RIGHT, "PAGE RIGHT", new MaschineJamPageRightCommand (this.model, surface), MaschineJamControlSurface.RIGHT);

        this.addButton (ButtonID.SOLO, "SOLO", new MaschineJamSoloCommand (this.model, surface), MaschineJamControlSurface.SOLO);
        this.addButton (ButtonID.MUTE, "MUTE", new MaschineJamMuteCommand (this.model, surface), MaschineJamControlSurface.MUTE);

        this.addButton (ButtonID.REPEAT, "NOTE REPEAT", new FillModeNoteRepeatCommand<> (this.model, surface, false), MaschineJamControlSurface.NOTE_REPEAT, this.configuration::isNoteRepeatActive);

        this.addButton (ButtonID.TRACK, "MACRO", new MaschineJamMacroCommand (this.model, surface), MaschineJamControlSurface.MACRO, () -> modeManager.isActive (Modes.TRACK));
        this.addButton (ButtonID.VOLUME, "LEVEL", new MaschineJamLevelCommand (this.model, surface), MaschineJamControlSurface.LEVEL, () -> modeManager.isActive (Modes.VOLUME, Modes.PAN));
        this.addButton (ButtonID.SENDS, "AUX", new MaschineJamAuxCommand (this.model, surface), MaschineJamControlSurface.AUX, () -> Modes.isSendMode (modeManager.getActiveID ()));
        this.addButton (ButtonID.DEVICE, "CONTROL", new MaschineJamControlCommand (this.model, surface), MaschineJamControlSurface.CONTROL, () -> modeManager.isActive (Modes.DEVICE_PARAMS, Modes.USER));

        final AutomationCommand<MaschineJamControlSurface, MaschineJamConfiguration> automationCommand = new AutomationCommand<> (this.model, surface);
        this.addButton (ButtonID.AUTOMATION, "AUTO", automationCommand, MaschineJamControlSurface.AUTO, automationCommand::isLit);

        for (int i = 0; i < 8; i++)
        {
            final GroupButtonCommand<MaschineJamControlSurface, MaschineJamConfiguration> command = new GroupButtonCommand<> (this.model, surface, i);
            this.addButton (ButtonID.get (ButtonID.ROW_SELECT_1, i), Character.toString ('A' + i), command, MaschineJamControlSurface.GROUP_A + i, command::getButtonColor);
        }

        this.addButton (ButtonID.SESSION, "SONG", new MaschineJamSessionViewCommand (this.model, surface), MaschineJamControlSurface.SONG, () -> viewManager.isActive (Views.SESSION));
        this.addButton (ButtonID.SEQUENCER, "STEP", new ViewMultiSelectCommand<> (this.model, surface, true, ButtonEvent.UP, SEQUENCER_VIEWS), MaschineJamControlSurface.STEP, () -> viewManager.isActive (SEQUENCER_VIEWS));
        this.addButton (ButtonID.NOTE, "PAD MODE", new ViewMultiSelectCommand<> (this.model, surface, true, ButtonEvent.UP, PLAY_VIEWS), MaschineJamControlSurface.PAD_MODE, () -> viewManager.isActive (PLAY_VIEWS));

        for (int i = 0; i < 8; i++)
        {
            final MaschineJamStartSceneCommand sceneCommand = new MaschineJamStartSceneCommand (this.model, surface, i);
            this.addButton (ButtonID.get (ButtonID.SCENE1, i), "SCENE " + i, sceneCommand, MaschineJamControlSurface.SCENE1 + i, sceneCommand::getButtonColor);
        }

        final MaschineJamViewButtonCommand leftCommand = new MaschineJamViewButtonCommand (ButtonID.ARROW_LEFT, this.model, surface);
        final MaschineJamViewButtonCommand rightCommand = new MaschineJamViewButtonCommand (ButtonID.ARROW_RIGHT, this.model, surface);
        final MaschineJamViewButtonCommand upCommand = new MaschineJamViewButtonCommand (ButtonID.ARROW_UP, this.model, surface);
        final MaschineJamViewButtonCommand downCommand = new MaschineJamViewButtonCommand (ButtonID.ARROW_DOWN, this.model, surface);

        this.addButton (ButtonID.ARROW_LEFT, "LEFT", leftCommand, MaschineJamControlSurface.NAV_LEFT, leftCommand::canScroll);
        this.addButton (ButtonID.ARROW_RIGHT, "RIGHT", rightCommand, MaschineJamControlSurface.NAV_RIGHT, rightCommand::canScroll);
        this.addButton (ButtonID.ARROW_UP, "UP", upCommand, MaschineJamControlSurface.NAV_UP, upCommand::canScroll);
        this.addButton (ButtonID.ARROW_DOWN, "DOWN", downCommand, MaschineJamControlSurface.NAV_DOWN, downCommand::canScroll);

        this.addButton (ButtonID.BROWSE, "Browser", new BrowserCommand<> (this.model, surface)
        {
            /** {@inheritDoc} */
            @Override
            protected boolean getCommit ()
            {
                // Discard browser, confirmation is via encoder
                return false;
            }
        }, MaschineJamControlSurface.BROWSE, this.model.getBrowser ()::isActive);

        this.addButton (ButtonID.FOOTSWITCH1, "Foot Controller (Tip)", new FootswitchCommand<> (this.model, surface, 0), MaschineJamControlSurface.FOOTSWITCH_TIP);
        this.addButton (ButtonID.FOOTSWITCH2, "Foot Controller (Ring)", new FootswitchCommand<> (this.model, surface, 1), MaschineJamControlSurface.FOOTSWITCH_RING);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final MaschineJamControlSurface surface = this.getSurface ();
        final IMidiInput input = surface.getMidiInput ();

        for (int i = 0; i < 8; i++)
        {
            final IHwFader fader = this.addFader (surface, ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), new KnobRowModeCommand<> (i, this.model, surface), BindType.CC, 0, MaschineJamControlSurface.FADER1 + i);
            // Prevent catch up jitter with 'motor faders'
            fader.disableTakeOver ();
            // Clear+touch does not work since the 'touched value' is set as well
            fader.bindTouch (NopCommand.INSTANCE, input, BindType.CC, 0, MaschineJamControlSurface.FADER_TOUCH1 + i);
            fader.setIndexInGroup (i);
        }

        final IHwRelativeKnob knob = this.addRelativeKnob (ContinuousID.MASTER_KNOB, "Encoder", null, MaschineJamControlSurface.KNOB_TURN);
        this.encoderManager = new EncoderModeManager<> (knob, this.model, surface);
        knob.bind (this.encoderManager);

        final MaschineMonitorEncoderCommand<MaschineJamControlSurface, MaschineJamConfiguration> encoderCommandMaster = new MaschineMonitorEncoderCommand<> (this.encoderManager, EncoderMode.MASTER_VOLUME, this.model, surface);
        this.addButton (ButtonID.MASTERTRACK, "MST", encoderCommandMaster, MaschineJamControlSurface.MASTER, encoderCommandMaster::isLit);
        final MaschineMonitorEncoderCommand<MaschineJamControlSurface, MaschineJamConfiguration> encoderCommandPlayPosition = new MaschineMonitorEncoderCommand<> (this.encoderManager, EncoderMode.PLAY_POSITION, this.model, surface);
        this.addButton (ButtonID.ALT, "GRP", encoderCommandPlayPosition, MaschineJamControlSurface.GROUP, encoderCommandPlayPosition::isLit);
        final MaschineMonitorEncoderCommand<MaschineJamControlSurface, MaschineJamConfiguration> encoderCommandMetronome = new MaschineMonitorEncoderCommand<> (this.encoderManager, EncoderMode.METRONOME_VOLUME, this.model, surface);
        this.addButton (ButtonID.METRONOME, "IN 1", encoderCommandMetronome, MaschineJamControlSurface.IN, encoderCommandMetronome::isLit);
        final MaschineMonitorEncoderCommand<MaschineJamControlSurface, MaschineJamConfiguration> encoderCommandCue = new MaschineMonitorEncoderCommand<> (this.encoderManager, EncoderMode.CUE_VOLUME, this.model, surface);
        this.addButton (ButtonID.MIXER, "HEADPHONE", encoderCommandCue, MaschineJamControlSurface.HEADPHONE, encoderCommandCue::isLit);

        // Activate the default mode
        this.encoderManager.setActiveEncoderMode (EncoderMode.MASTER_VOLUME);

        this.addButton (ButtonID.ENTER, "ENC_PRESS", (event, velocity) -> {
            if (event != ButtonEvent.DOWN)
                return;
            final IBrowser browser = this.model.getBrowser ();
            if (browser.isActive ())
                browser.stopBrowsing (true);
            else
                this.encoderManager.toggleFunction ();
        }, MaschineJamControlSurface.KNOB_PUSH);
        this.addButton (ButtonID.MASTERTRACK_TOUCH, "ENC_TOUCH", NopCommand.INSTANCE, MaschineJamControlSurface.KNOB_TOUCH);

        this.addButton (ButtonID.TAP_TEMPO, "TEMPO", new MaschineJamTapTempoCommand (this.encoderManager, this.model, surface), MaschineJamControlSurface.TEMPO);
        this.addButton (ButtonID.ACCENT, "SWING", new MaschineJamSwingCommand (this.encoderManager, this.model, surface), MaschineJamControlSurface.SWING, () -> this.model.getGroove ().getParameter (GrooveParameterID.ENABLED).getValue () > 0);
        this.addButton (ButtonID.GROOVE, "GRID", new QuantizeCommand<> (this.model, surface), MaschineJamControlSurface.GRID);

        this.addButton (ButtonID.ROW1_1, "PERFORM", new MaschineJamViewCommand (this.encoderManager, EncoderMode.TEMPORARY_PERFORM, this.model, surface), MaschineJamControlSurface.PERFORM);
        this.addButton (ButtonID.ROW1_2, "NOTES", new MaschineJamViewCommand (this.encoderManager, EncoderMode.TEMPORARY_NOTES, this.model, surface), MaschineJamControlSurface.NOTES);
        this.addButton (ButtonID.ROW1_3, "LOCK", new MaschineJamViewCommand (this.encoderManager, EncoderMode.TEMPORARY_LOCK, this.model, surface), MaschineJamControlSurface.LOCK, () -> this.configuration.isAccentActive ());
        this.addButton (ButtonID.ROW1_4, "TUNE", new MaschineJamViewCommand (this.encoderManager, EncoderMode.TEMPORARY_TUNE, this.model, surface), MaschineJamControlSurface.TUNE);
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final MaschineJamControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (101.5, 530.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD2).setBounds (176.75, 530.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD3).setBounds (252.0, 528.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD4).setBounds (327.25, 529.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD5).setBounds (402.5, 529.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD6).setBounds (478.0, 528.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD7).setBounds (553.25, 527.25, 43.75, 40.0);
        surface.getButton (ButtonID.PAD8).setBounds (631.0, 526.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD9).setBounds (101.5, 465.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD10).setBounds (176.75, 465.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD11).setBounds (252.0, 464.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD12).setBounds (327.25, 465.25, 43.75, 40.0);
        surface.getButton (ButtonID.PAD13).setBounds (402.5, 465.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD14).setBounds (478.0, 464.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD15).setBounds (553.25, 463.25, 43.75, 40.0);
        surface.getButton (ButtonID.PAD16).setBounds (631.0, 462.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD17).setBounds (101.5, 401.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD18).setBounds (176.75, 401.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD19).setBounds (252.0, 400.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD20).setBounds (327.25, 401.25, 43.75, 40.0);
        surface.getButton (ButtonID.PAD21).setBounds (402.5, 400.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD22).setBounds (478.0, 400.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD23).setBounds (553.25, 399.25, 43.75, 40.0);
        surface.getButton (ButtonID.PAD24).setBounds (631.0, 398.25, 43.75, 40.0);
        surface.getButton (ButtonID.PAD25).setBounds (101.5, 337.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD26).setBounds (176.75, 337.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD27).setBounds (252.0, 336.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD28).setBounds (327.25, 337.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD29).setBounds (402.5, 336.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD30).setBounds (478.0, 336.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD31).setBounds (553.25, 335.25, 43.75, 40.0);
        surface.getButton (ButtonID.PAD32).setBounds (631.0, 334.25, 43.75, 40.0);
        surface.getButton (ButtonID.PAD33).setBounds (101.5, 273.25, 43.75, 40.0);
        surface.getButton (ButtonID.PAD34).setBounds (176.75, 273.25, 43.75, 40.0);
        surface.getButton (ButtonID.PAD35).setBounds (252.0, 272.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD36).setBounds (327.25, 273.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD37).setBounds (402.5, 272.25, 43.75, 40.0);
        surface.getButton (ButtonID.PAD38).setBounds (478.0, 271.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD39).setBounds (553.25, 271.25, 43.75, 40.0);
        surface.getButton (ButtonID.PAD40).setBounds (631.0, 270.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD41).setBounds (101.5, 209.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD42).setBounds (176.75, 209.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD43).setBounds (252.0, 208.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD44).setBounds (327.25, 208.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD45).setBounds (402.5, 208.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD46).setBounds (478.0, 207.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD47).setBounds (553.25, 207.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD48).setBounds (631.0, 205.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD49).setBounds (101.5, 144.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD50).setBounds (176.75, 144.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD51).setBounds (252.0, 144.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD52).setBounds (327.25, 144.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD53).setBounds (402.5, 143.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD54).setBounds (478.0, 143.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD55).setBounds (553.25, 143.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD56).setBounds (631.0, 141.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD57).setBounds (101.5, 80.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD58).setBounds (176.75, 80.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD59).setBounds (252.0, 80.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD60).setBounds (327.25, 80.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD61).setBounds (402.5, 79.5, 43.75, 40.0);
        surface.getButton (ButtonID.PAD62).setBounds (478.0, 79.75, 43.75, 40.0);
        surface.getButton (ButtonID.PAD63).setBounds (553.25, 79.0, 43.75, 40.0);
        surface.getButton (ButtonID.PAD64).setBounds (631.0, 77.5, 43.75, 40.0);
        surface.getButton (ButtonID.SCENE1).setBounds (103.25, 24.5, 43.75, 28.0);
        surface.getButton (ButtonID.SCENE2).setBounds (179.0, 24.5, 43.75, 28.0);
        surface.getButton (ButtonID.SCENE3).setBounds (255.0, 24.5, 43.75, 28.0);
        surface.getButton (ButtonID.SCENE4).setBounds (330.75, 24.5, 43.75, 28.0);
        surface.getButton (ButtonID.SCENE5).setBounds (406.5, 24.5, 43.75, 28.0);
        surface.getButton (ButtonID.SCENE6).setBounds (482.25, 24.5, 43.75, 28.0);
        surface.getButton (ButtonID.SCENE7).setBounds (558.25, 24.5, 43.75, 28.0);
        surface.getButton (ButtonID.SCENE8).setBounds (634.0, 24.5, 43.75, 28.0);
        surface.getButton (ButtonID.SHIFT).setBounds (21.0, 758.0, 61.0, 25.25);
        surface.getButton (ButtonID.SELECT).setBounds (706.5, 761.0, 69.5, 25.25);
        surface.getButton (ButtonID.DELETE).setBounds (21.0, 202.25, 62.75, 25.25);
        surface.getButton (ButtonID.DUPLICATE).setBounds (21.0, 242.25, 62.75, 25.25);
        surface.getButton (ButtonID.PLAY).setBounds (103.25, 756.75, 61.0, 25.25);
        surface.getButton (ButtonID.RECORD).setBounds (176.5, 756.75, 61.0, 25.25);
        surface.getButton (ButtonID.PAGE_LEFT).setBounds (249.75, 756.75, 61.0, 25.25);
        surface.getButton (ButtonID.PAGE_RIGHT).setBounds (323.0, 756.75, 61.0, 25.25);
        surface.getButton (ButtonID.SOLO).setBounds (542.75, 756.75, 61.0, 25.25);
        surface.getButton (ButtonID.MUTE).setBounds (616.0, 756.75, 61.0, 25.25);
        surface.getButton (ButtonID.REPEAT).setBounds (21.0, 534.75, 63.25, 25.25);
        surface.getButton (ButtonID.TRACK).setBounds (21.0, 595.25, 63.25, 25.25);
        surface.getButton (ButtonID.VOLUME).setBounds (21.0, 626.25, 63.25, 25.25);
        surface.getButton (ButtonID.SENDS).setBounds (21.0, 655.0, 63.25, 25.25);
        surface.getButton (ButtonID.DEVICE).setBounds (21.0, 691.0, 63.25, 25.25);
        surface.getButton (ButtonID.AUTOMATION).setBounds (21.0, 723.75, 61.0, 25.25);
        surface.getButton (ButtonID.ROW_SELECT_1).setBounds (102.5, 595.75, 43.75, 40.0);
        surface.getButton (ButtonID.ROW_SELECT_2).setBounds (178.0, 595.75, 43.75, 40.0);
        surface.getButton (ButtonID.ROW_SELECT_3).setBounds (253.25, 595.75, 43.75, 40.0);
        surface.getButton (ButtonID.ROW_SELECT_4).setBounds (328.75, 595.75, 43.75, 40.0);
        surface.getButton (ButtonID.ROW_SELECT_5).setBounds (404.0, 595.75, 43.75, 40.0);
        surface.getButton (ButtonID.ROW_SELECT_6).setBounds (479.5, 595.75, 43.75, 40.0);
        surface.getButton (ButtonID.ROW_SELECT_7).setBounds (555.0, 595.75, 43.75, 40.0);
        surface.getButton (ButtonID.ROW_SELECT_8).setBounds (630.25, 595.75, 43.75, 40.0);
        surface.getButton (ButtonID.SESSION).setBounds (21.0, 27.0, 62.75, 25.25);
        surface.getButton (ButtonID.NOTE).setBounds (21.0, 157.75, 62.75, 25.25);
        surface.getButton (ButtonID.ARROW_LEFT).setBounds (9.0, 407.0, 30.5, 25.25);
        surface.getButton (ButtonID.ARROW_RIGHT).setBounds (56.25, 407.0, 30.5, 25.25);
        surface.getButton (ButtonID.ARROW_UP).setBounds (31.5, 375.5, 36.5, 25.25);
        surface.getButton (ButtonID.ARROW_DOWN).setBounds (31.5, 441.75, 36.5, 25.25);
        surface.getButton (ButtonID.BROWSE).setBounds (703.75, 463.25, 69.5, 25.25);
        surface.getButton (ButtonID.MASTERTRACK).setBounds (701.5, 220.75, 33.75, 42.75);
        surface.getButton (ButtonID.ALT).setBounds (745.75, 220.75, 33.75, 42.75);
        surface.getButton (ButtonID.METRONOME).setBounds (701.5, 282.0, 33.75, 42.75);
        surface.getButton (ButtonID.MIXER).setBounds (745.75, 282.0, 33.75, 42.75);
        surface.getButton (ButtonID.ENTER).setBounds (720.5, 353.5, 45.25, 25.75);
        surface.getButton (ButtonID.TAP_TEMPO).setBounds (396.25, 756.75, 61.0, 25.25);
        surface.getButton (ButtonID.ACCENT).setBounds (706.5, 722.0, 69.5, 25.25);
        surface.getButton (ButtonID.GROOVE).setBounds (469.5, 756.75, 61.0, 25.25);
        surface.getButton (ButtonID.ROW1_1).setBounds (706.5, 578.0, 69.5, 25.25);
        surface.getButton (ButtonID.ROW1_2).setBounds (706.5, 609.75, 69.5, 25.25);
        surface.getButton (ButtonID.ROW1_3).setBounds (706.5, 646.0, 69.5, 25.25);
        surface.getButton (ButtonID.ROW1_4).setBounds (706.5, 681.0, 69.5, 25.25);
        surface.getButton (ButtonID.FOOTSWITCH1).setBounds (738.5, 10.75, 52.5, 28.5);

        surface.getContinuous (ContinuousID.FADER1).setBounds (105.5, 654.75, 41.25, 90.75);
        surface.getContinuous (ContinuousID.FADER2).setBounds (180.5, 654.75, 41.25, 90.75);
        surface.getContinuous (ContinuousID.FADER3).setBounds (255.5, 654.75, 41.25, 90.75);
        surface.getContinuous (ContinuousID.FADER4).setBounds (330.75, 654.75, 41.25, 90.75);
        surface.getContinuous (ContinuousID.FADER5).setBounds (405.75, 654.75, 41.25, 90.75);
        surface.getContinuous (ContinuousID.FADER6).setBounds (480.75, 654.75, 41.25, 90.75);
        surface.getContinuous (ContinuousID.FADER7).setBounds (555.75, 654.75, 41.25, 90.75);
        surface.getContinuous (ContinuousID.FADER8).setBounds (630.75, 654.75, 41.25, 90.75);
        surface.getContinuous (ContinuousID.MASTER_KNOB).setBounds (721.75, 385.5, 43.5, 42.75);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final MaschineJamControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActive (Modes.VOLUME);
        surface.getViewManager ().setActive (this.configuration.shouldStartWithSessionView () ? Views.SESSION : this.configuration.getPreferredNoteView ());
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        this.updateFaders ();

        // Update main VU
        final MaschineJamControlSurface surface = this.getSurface ();
        final IMidiOutput midiOutput = surface.getMidiOutput ();
        final ITrack track;
        if (this.encoderManager.isActiveEncoderMode (EncoderMode.SELECTED_TRACK_VOLUME))
        {
            final ITrackBank trackBank = this.model.getTrackBank ();
            final Optional<ITrack> trackOptional = trackBank.getSelectedItem ();
            if (trackOptional.isPresent ())
                track = trackOptional.get ();
            else
                track = EmptyTrack.getInstance (trackBank.getItem (0).getSendBank ().getPageSize ());
        }
        else
            track = this.model.getMasterTrack ();

        final int vuLeft = this.valueChanger.toMidiValue (track.getVuLeft ());
        final int vuRight = this.valueChanger.toMidiValue (track.getVuRight ());
        midiOutput.sendCC (MaschineJamControlSurface.STRIP_LEFT, vuLeft);
        midiOutput.sendCC (MaschineJamControlSurface.STRIP_RIGHT, vuRight);
    }


    /**
     * Update the faders (color, position, etc.).
     */
    private void updateFaders ()
    {
        final MaschineJamControlSurface surface = this.getSurface ();
        final IMode mode = surface.getModeManager ().getActive ();
        if (mode instanceof final IMaschineJamMode jamMode)
        {
            final FaderConfig [] configs = new FaderConfig [8];
            for (int i = 0; i < 8; i++)
                configs[i] = jamMode.setupFader (i);
            surface.setupFaders (configs);
        }
    }
}
