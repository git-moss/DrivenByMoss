// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn;

import de.mossgrabers.controller.yaeltex.turn.command.trigger.YaeltexTurnRecordCommand;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnColorManager;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.controller.yaeltex.turn.mode.IYaeltexKnobMode;
import de.mossgrabers.controller.yaeltex.turn.mode.YaeltexTurnTrackMixMode;
import de.mossgrabers.controller.yaeltex.turn.view.SessionView;
import de.mossgrabers.framework.command.TempoCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.ShiftCommand;
import de.mossgrabers.framework.command.trigger.application.OverdubCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ScenePlayView;
import de.mossgrabers.framework.view.Views;

import java.util.ArrayList;
import java.util.List;


/**
 * Support for the Yaeltex Turn controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class YaeltexTurnControllerSetup extends AbstractControllerSetup<YaeltexTurnControlSurface, YaeltexTurnConfiguration>
{
    private static final List<ContinuousID> CONTINUOUS_CONTROLS = new ArrayList<> ();
    static
    {
        CONTINUOUS_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.PAN_KNOB1, 8));
        CONTINUOUS_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.SEND1_KNOB1, 8));
        CONTINUOUS_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.SEND2_KNOB1, 8));
        CONTINUOUS_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.DEVICE_KNOB1, 8));

        CONTINUOUS_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.EQ_TYPE_KNOB1, 8));
        CONTINUOUS_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.EQ_Q_KNOB1, 8));
        CONTINUOUS_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.EQ_FREQUENCY_KNOB1, 8));
        CONTINUOUS_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.EQ_GAIN_KNOB1, 8));

        CONTINUOUS_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.FADER1, 8));
    }


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public YaeltexTurnControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new YaeltexTurnColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new YaeltexTurnConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 68, 8, 4);
        this.scales.setDrumDefaultOffset (12);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (this.configuration.areMasterTracksIncluded ());
        ms.setNumScenes (4);
        ms.setNumSends (3);
        ms.setNumDrumPadLayers (8);
        ms.enableDevice (DeviceID.EQ);
        ms.enableDevice (DeviceID.FIRST_INSTRUMENT);

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
        final IMidiInput input = midiAccess.createInput ("Pads", "80????" /* Note off */,
                "90????" /* Note on */, "B040??" /* Sustain pedal */);
        final YaeltexTurnControlSurface surface = new YaeltexTurnControlSurface (this.host, this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.configuration.registerDeactivatedItemsHandler (this.model);
        this.createScaleObservers (this.configuration);

        this.getSurface ().getModeManager ().addChangeListener ( (previousMode, activeMode) -> onModeChange (activeMode));
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final YaeltexTurnControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.register (Modes.TRACK, new YaeltexTurnTrackMixMode (surface, this.model, CONTINUOUS_CONTROLS));

        // TODO
        // Modes.DRUM_SEQUENCER
        // Modes.NOTE_SEQUENCER
        // Modes.DEVICE_LAYER
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final YaeltexTurnControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.register (Views.SESSION, new SessionView (surface, this.model));
        viewManager.register (Views.SCENE_PLAY, new ScenePlayView<> (surface, this.model));

        // TODO
        // viewManager.register (Views.SEQUENCER, new SequencerView (surface, this.model));
        // viewManager.register (Views.DRUM, new DrumView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final YaeltexTurnControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        final ViewManager viewManager = surface.getViewManager ();
        final ITransport t = this.model.getTransport ();

        final int channel = YaeltexTurnControlSurface.MIDI_CHANNEL_MAIN;

        // Modifier buttons

        this.addButton (ButtonID.SHIFT, "shift", new ShiftCommand<> (this.model, surface), channel, YaeltexTurnControlSurface.BUTTON_SHIFT);
        this.addButton (ButtonID.SELECT, "select", NopCommand.INSTANCE, channel, YaeltexTurnControlSurface.BUTTON_SELECT);

        // Mode selection

        this.addButton (ButtonID.CLIP, "clips", new ModeSelectCommand<> (this.model, surface, Modes.DRUM_SEQUENCER), channel, YaeltexTurnControlSurface.BUTTON_CLIPS, () -> modeManager.isActive (Modes.DRUM_SEQUENCER));
        this.addButton (ButtonID.SESSION, "session", new ViewMultiSelectCommand<> (this.model, surface, Views.SESSION, Views.SCENE_PLAY), channel, YaeltexTurnControlSurface.BUTTON_SESSION, () -> viewManager.isActive (Views.SCENE_PLAY));
        this.addButton (ButtonID.USER, "usr", new ModeSelectCommand<> (this.model, surface, Modes.DEVICE_LAYER), channel, YaeltexTurnControlSurface.BUTTON_USR, () -> modeManager.isActive (Modes.DEVICE_LAYER));
        this.addButton (ButtonID.TRACK, "trk", new ModeSelectCommand<> (this.model, surface, Modes.TRACK), channel, YaeltexTurnControlSurface.BUTTON_TRK, () -> modeManager.isActive (Modes.TRACK));

        // Transport

        final OverdubCommand<YaeltexTurnControlSurface, YaeltexTurnConfiguration> overdubCommand = new OverdubCommand<> (this.model, surface);
        final RecordCommand<YaeltexTurnControlSurface, YaeltexTurnConfiguration> recordCommand = new YaeltexTurnRecordCommand (this.model, surface);

        this.addButton (ButtonID.PLAY, "play", new PlayCommand<> (this.model, surface), channel, YaeltexTurnControlSurface.BUTTON_PLAY, () -> t.isPlaying () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_HI);
        this.addButton (ButtonID.STOP, "stop", new StopCommand<> (this.model, surface), channel, YaeltexTurnControlSurface.BUTTON_STOP, () -> !t.isPlaying () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_HI);
        this.addButton (ButtonID.RECORD, "rec", recordCommand, channel, YaeltexTurnControlSurface.BUTTON_REC, () -> recordCommand.isActive () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_HI);
        this.addButton (ButtonID.OVERDUB, "overdub", overdubCommand, channel, YaeltexTurnControlSurface.BUTTON_OVERDUB, () -> overdubCommand.isActive () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_HI);
        this.addButton (ButtonID.LOOP, "loop", new ToggleLoopCommand<> (this.model, surface), channel, YaeltexTurnControlSurface.BUTTON_LOOP, () -> t.isLoop () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ColorManager.BUTTON_STATE_HI);
        this.addButton (ButtonID.TAP_TEMPO, "tap tempo", new TapTempoCommand<> (this.model, surface), channel, YaeltexTurnControlSurface.BUTTON_TAP_TEMPO);

        this.addButton (ButtonID.ARROW_LEFT, "Left", (event, velocity) -> {

            if (event == ButtonEvent.DOWN)
                this.model.getTrackBank ().selectPreviousPage ();

        }, channel, YaeltexTurnControlSurface.BUTTON_LEFT, () -> this.model.getTrackBank ().canScrollPageBackwards ());

        this.addButton (ButtonID.ARROW_RIGHT, "Right", (event, velocity) -> {

            if (event == ButtonEvent.DOWN)
                this.model.getTrackBank ().selectNextPage ();

        }, channel, YaeltexTurnControlSurface.BUTTON_RIGHT, () -> this.model.getTrackBank ().canScrollPageForwards ());

        final ModeCursorCommand<YaeltexTurnControlSurface, YaeltexTurnConfiguration> downCommand = new ModeCursorCommand<> (Direction.LEFT, this.model, surface, false);
        final ModeCursorCommand<YaeltexTurnControlSurface, YaeltexTurnConfiguration> upCommand = new ModeCursorCommand<> (Direction.RIGHT, this.model, surface, false);
        this.addButton (ButtonID.ARROW_UP, "Up", upCommand, channel, YaeltexTurnControlSurface.BUTTON_DOWN, upCommand::canScroll);
        this.addButton (ButtonID.ARROW_DOWN, "Down", downCommand, channel, YaeltexTurnControlSurface.BUTTON_UP, downCommand::canScroll);

        final int buttonChannel = YaeltexTurnControlSurface.MIDI_CHANNEL_TRACK_BUTTONS;

        for (int i = 0; i < 8; i++)
        {
            final int number = i + 1;

            int midiID = YaeltexTurnControlSurface.BUTTON_ROW1_1 + i * 2;
            final ButtonID crossfadeButtonID = ButtonID.get (ButtonID.ROW1_1, i);
            this.addButton (crossfadeButtonID, "A|B " + number, new ButtonRowModeCommand<> (0, i, this.model, surface), buttonChannel, midiID, () -> this.getModeColor (crossfadeButtonID), ColorManager.BUTTON_STATE_OFF, YaeltexTurnColorManager.BUTTON_STATE_CROSS_A, YaeltexTurnColorManager.BUTTON_STATE_CROSS_B);

            final ButtonID stopButtonID = ButtonID.get (ButtonID.ROW2_1, i);
            this.addButton (stopButtonID, "Stop " + number, new ButtonRowModeCommand<> (1, i, this.model, surface), buttonChannel, midiID + 1, () -> this.getModeColor (stopButtonID), ColorManager.BUTTON_STATE_OFF, YaeltexTurnColorManager.BUTTON_STATE_STOP_ON);

            midiID = YaeltexTurnControlSurface.BUTTON_ROW2_1 + i * 2;
            final ButtonID armButtonID = ButtonID.get (ButtonID.ROW3_1, i);
            this.addButton (armButtonID, "Arm " + number, new ButtonRowModeCommand<> (2, i, this.model, surface), buttonChannel, midiID, () -> this.getModeColor (armButtonID), ColorManager.BUTTON_STATE_OFF, YaeltexTurnColorManager.BUTTON_STATE_REC_ARM_ON);

            final ButtonID soloButtonID = ButtonID.get (ButtonID.ROW4_1, i);
            this.addButton (soloButtonID, "Solo " + number, new ButtonRowModeCommand<> (3, i, this.model, surface), buttonChannel, midiID + 1, () -> this.getModeColor (soloButtonID), ColorManager.BUTTON_STATE_OFF, YaeltexTurnColorManager.BUTTON_STATE_SOLO_ON);

            final ButtonID muteButtonID = ButtonID.get (ButtonID.ROW5_1, i);
            this.addButton (muteButtonID, "Mute " + number, new ButtonRowModeCommand<> (4, i, this.model, surface), buttonChannel, YaeltexTurnControlSurface.BUTTON_ROW3_1 + i, () -> this.getModeColor (muteButtonID), ColorManager.BUTTON_STATE_OFF, YaeltexTurnColorManager.BUTTON_STATE_MUTE_ON);

            final ButtonID selectButtonID = ButtonID.get (ButtonID.ROW6_1, i);
            this.addButton (selectButtonID, "Select " + number, new ButtonRowModeCommand<> (5, i, this.model, surface), 14, YaeltexTurnControlSurface.BUTTON_ROW4_1 + i, () -> this.getModeColor (selectButtonID), ColorManager.BUTTON_STATE_OFF, YaeltexTurnColorManager.BUTTON_STATE_SELECT_ON, YaeltexTurnColorManager.BUTTON_STATE_SELECT_HI, YaeltexTurnColorManager.BUTTON_STATE_NEW_CLIP_LENGTH_ON);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final YaeltexTurnControlSurface surface = this.getSurface ();

        final ITransport transport = this.model.getTransport ();

        this.addFader (ContinuousID.TEMPO, "Tempo", new TempoCommand<> (this.model, surface, 60, 188), BindType.CC, 15, YaeltexTurnControlSurface.KNOB_ANALOG_TEMPO);
        this.addFader (ContinuousID.CROSSFADER, "A|B", null, BindType.CC, 15, YaeltexTurnControlSurface.KNOB_ANALOG_A_B, false).bind (transport.getCrossfadeParameter ());
        this.addFader (ContinuousID.CUE, "Cue", null, BindType.CC, 15, YaeltexTurnControlSurface.KNOB_ANALOG_CUE).bind (this.model.getProject ().getCueVolumeParameter ());
        this.addFader (ContinuousID.FADER_MASTER, "Master", null, BindType.CC, 15, YaeltexTurnControlSurface.KNOB_ANALOG_MASTER).bind (this.model.getMasterTrack ().getVolumeParameter ());

        ContinuousID continuousID;
        for (int i = 0; i < 8; i++)
        {
            final int number = i + 1;

            continuousID = ContinuousID.get (ContinuousID.PAN_KNOB1, i);
            this.addRelativeKnob (continuousID, "Pan Knob " + number, null, BindType.CC, 15, YaeltexTurnControlSurface.KNOB_DIGITAL_ROW1 + i).setIndexInGroup (i);
            continuousID = ContinuousID.get (ContinuousID.SEND1_KNOB1, i);
            this.addRelativeKnob (continuousID, "Send 1 Knob " + number, null, BindType.CC, 15, YaeltexTurnControlSurface.KNOB_DIGITAL_ROW1 + 8 + i).setIndexInGroup (i);
            continuousID = ContinuousID.get (ContinuousID.SEND2_KNOB1, i);
            this.addRelativeKnob (continuousID, "Send 2 Knob " + number, null, BindType.CC, 15, YaeltexTurnControlSurface.KNOB_DIGITAL_ROW1 + 16 + i).setIndexInGroup (i);
            continuousID = ContinuousID.get (ContinuousID.DEVICE_KNOB1, i);
            this.addRelativeKnob (continuousID, "Device Knob " + number, null, BindType.CC, 15, YaeltexTurnControlSurface.KNOB_DIGITAL_ROW1 + 24 + i).setIndexInGroup (i);

            continuousID = ContinuousID.get (ContinuousID.EQ_TYPE_KNOB1, i);
            this.addAbsoluteKnob (continuousID, "EQ Type Knob " + number, null, BindType.CC, 15, YaeltexTurnControlSurface.KNOB_ANALOG_ROW1 + i).setIndexInGroup (i);
            continuousID = ContinuousID.get (ContinuousID.EQ_Q_KNOB1, i);
            this.addAbsoluteKnob (continuousID, "EQ Q Knob " + number, null, BindType.CC, 15, YaeltexTurnControlSurface.KNOB_ANALOG_ROW1 + 8 + i).setIndexInGroup (i);
            continuousID = ContinuousID.get (ContinuousID.EQ_FREQUENCY_KNOB1, i);
            this.addAbsoluteKnob (continuousID, "EQ Frequency Knob " + number, null, BindType.CC, 15, YaeltexTurnControlSurface.KNOB_ANALOG_ROW1 + 16 + i).setIndexInGroup (i);
            continuousID = ContinuousID.get (ContinuousID.EQ_GAIN_KNOB1, i);
            this.addAbsoluteKnob (continuousID, "EQ Gain Knob " + number, null, BindType.CC, 15, YaeltexTurnControlSurface.KNOB_ANALOG_ROW1 + 24 + i).setIndexInGroup (i);

            continuousID = ContinuousID.get (ContinuousID.FADER1, i);
            this.addFader (continuousID, "Fader " + number, null, BindType.CC, 15, YaeltexTurnControlSurface.FADER1 + i).setIndexInGroup (i);
        }

        new TrackVolumeMode<> (surface, this.model, true, ContinuousID.createSequentialList (ContinuousID.FADER1, 8)).onActivate ();
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final YaeltexTurnControlSurface surface = this.getSurface ();

        // TODO
        // surface.getButton (ButtonID.NUDGE_MINUS).setBounds (624.0, 132.0, 33.25, 15.25);
        // surface.getButton (ButtonID.NUDGE_PLUS).setBounds (686.0, 132.0, 33.25, 15.25);
        //
        // surface.getButton (ButtonID.METRONOME).setBounds (624.0, 96.0, 33.25, 15.25);
        // surface.getButton (ButtonID.CLIP).setBounds (747.75, 59.25, 32.5, 20.0);
        // surface.getButton (ButtonID.LAYOUT).setBounds (747.75, 319.75, 33.25, 15.25);
        // surface.getButton (ButtonID.TOGGLE_DEVICES_PANE).setBounds (686.0, 319.75, 33.25, 15.25);
        //
        // surface.getButton (ButtonID.DEVICE_ON_OFF).setBounds (562.25, 319.75, 33.25, 15.25);
        //
        // surface.getButton (ButtonID.PAD1).setBounds (13.0, 194.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD2).setBounds (74.25, 194.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD3).setBounds (135.25, 194.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD4).setBounds (196.5, 194.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD5).setBounds (257.75, 194.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD6).setBounds (318.75, 194.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD7).setBounds (380.0, 194.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD8).setBounds (441.0, 194.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD9).setBounds (12.0, 165.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD10).setBounds (73.25, 165.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD11).setBounds (134.5, 165.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD12).setBounds (196.0, 165.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD13).setBounds (257.25, 165.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD14).setBounds (318.5, 165.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD15).setBounds (379.75, 165.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD16).setBounds (441.0, 165.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD17).setBounds (11.75, 135.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD18).setBounds (73.25, 135.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD19).setBounds (135.0, 135.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD20).setBounds (196.5, 135.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD21).setBounds (258.0, 135.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD22).setBounds (319.5, 135.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD23).setBounds (381.25, 135.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD24).setBounds (441.0, 137.5, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD25).setBounds (12.5, 108.75, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD26).setBounds (73.75, 108.75, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD27).setBounds (135.25, 108.75, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD28).setBounds (196.5, 108.75, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD29).setBounds (257.75, 108.75, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD30).setBounds (319.25, 108.75, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD31).setBounds (380.5, 108.75, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD32).setBounds (441.0, 108.75, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD33).setBounds (12.75, 78.0, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD34).setBounds (74.25, 78.0, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD35).setBounds (135.5, 78.0, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD36).setBounds (196.75, 78.0, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD37).setBounds (256.5, 78.0, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD38).setBounds (318.0, 78.0, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD39).setBounds (379.5, 78.0, 52.25, 18.5);
        // surface.getButton (ButtonID.PAD40).setBounds (441.0, 78.0, 52.25, 18.5);
        //
        // surface.getButton (ButtonID.SHIFT).setBounds (686.0, 360.5, 33.25, 15.25);
        // surface.getButton (ButtonID.PLAY).setBounds (624.0, 59.25, 32.5, 20.0);
        // surface.getButton (ButtonID.RECORD).setBounds (686.0, 59.25, 32.5, 20.0);
        // surface.getButton (ButtonID.TAP_TEMPO).setBounds (686.0, 96.0, 33.25, 15.25);
        //
        // surface.getButton (ButtonID.QUANTIZE).setBounds (624.0, 319.75, 33.25, 15.25);
        // surface.getButton (ButtonID.PAN_SEND).setBounds (562.25, 64.0, 33.25, 15.25);
        // surface.getButton (ButtonID.SEND1).setBounds (562.25, 96.0, 33.25, 15.25);
        // surface.getButton (ButtonID.SEND2).setBounds (562.25, 132.0, 33.25, 15.25);
        //
        // surface.getButton (ButtonID.ARROW_UP).setBounds (581.25, 361.75, 33.25, 25.0);
        // surface.getButton (ButtonID.ARROW_DOWN).setBounds (581.5, 386.25, 33.25, 25.0);
        // surface.getButton (ButtonID.ARROW_LEFT).setBounds (562.5, 361.75, 17.0, 49.25);
        // surface.getButton (ButtonID.ARROW_RIGHT).setBounds (616.0, 361.75, 17.0, 49.25);
        //
        // surface.getButton (ButtonID.SCENE1).setBounds (500.75, 78.0, 36.0, 18.5);
        // surface.getButton (ButtonID.SCENE2).setBounds (500.75, 108.75, 36.0, 18.5);
        // surface.getButton (ButtonID.SCENE3).setBounds (500.75, 137.5, 36.0, 18.5);
        // surface.getButton (ButtonID.SCENE4).setBounds (500.75, 165.5, 36.0, 18.5);
        // surface.getButton (ButtonID.SCENE5).setBounds (500.75, 194.5, 36.0, 18.5);
        // surface.getButton (ButtonID.STOP_ALL_CLIPS).setBounds (502.25, 227.25, 33.25, 18.75);
        // surface.getButton (ButtonID.MASTERTRACK).setBounds (500.75, 263.0, 36.0, 15.75);
        //
        // surface.getButton (ButtonID.ROW1_1).setBounds (12.5, 262.5, 50.5, 15.75);
        // surface.getButton (ButtonID.ROW1_2).setBounds (74.0, 262.5, 50.5, 15.75);
        // surface.getButton (ButtonID.ROW1_3).setBounds (135.25, 262.5, 50.5, 15.75);
        // surface.getButton (ButtonID.ROW1_4).setBounds (196.75, 262.5, 50.5, 15.75);
        // surface.getButton (ButtonID.ROW1_5).setBounds (258.25, 262.5, 50.5, 15.75);
        // surface.getButton (ButtonID.ROW1_6).setBounds (319.5, 262.5, 50.5, 15.75);
        // surface.getButton (ButtonID.ROW1_7).setBounds (381.0, 262.5, 50.5, 15.75);
        // surface.getButton (ButtonID.ROW1_8).setBounds (442.5, 262.5, 50.5, 15.75);
        // surface.getButton (ButtonID.ROW2_1).setBounds (14.0, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW2_2).setBounds (75.25, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW2_3).setBounds (136.5, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW2_4).setBounds (197.5, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW2_5).setBounds (258.75, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW2_6).setBounds (320.0, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW2_7).setBounds (381.25, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW2_8).setBounds (442.5, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW3_1).setBounds (14.25, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW3_2).setBounds (75.5, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW3_3).setBounds (136.5, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW3_4).setBounds (197.75, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW3_5).setBounds (259.0, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW3_6).setBounds (320.0, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW3_7).setBounds (381.25, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW3_8).setBounds (442.5, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW4_1).setBounds (44.5, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW4_2).setBounds (105.75, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW4_3).setBounds (167.0, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW4_4).setBounds (228.25, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW4_5).setBounds (289.5, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW4_6).setBounds (350.75, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW4_7).setBounds (411.75, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW4_8).setBounds (473.0, 317.75, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW5_1).setBounds (44.75, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW5_2).setBounds (106.0, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW5_3).setBounds (167.25, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW5_4).setBounds (228.25, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW5_5).setBounds (289.5, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW5_6).setBounds (350.75, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW5_7).setBounds (411.75, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW5_8).setBounds (473.0, 291.5, 19.5, 18.0);
        // surface.getButton (ButtonID.ROW6_1).setBounds (21.25, 227.25, 31.5, 18.75);
        // surface.getButton (ButtonID.ROW6_2).setBounds (82.75, 227.25, 31.5, 18.75);
        // surface.getButton (ButtonID.ROW6_3).setBounds (144.25, 227.25, 31.5, 18.75);
        // surface.getButton (ButtonID.ROW6_4).setBounds (205.75, 227.25, 31.5, 18.75);
        // surface.getButton (ButtonID.ROW6_5).setBounds (267.0, 227.25, 31.5, 18.75);
        // surface.getButton (ButtonID.ROW6_6).setBounds (328.5, 227.25, 31.5, 18.75);
        // surface.getButton (ButtonID.ROW6_7).setBounds (390.0, 227.25, 31.5, 18.75);
        // surface.getButton (ButtonID.ROW6_8).setBounds (451.5, 227.25, 31.5, 18.75);
        //
        // surface.getButton (ButtonID.BANK_LEFT).setBounds (686.0, 289.75, 33.25, 15.25);
        // surface.getButton (ButtonID.BANK_RIGHT).setBounds (747.75, 289.75, 33.25, 15.25);
        //
        // surface.getButton (ButtonID.DEVICE_LEFT).setBounds (562.25, 289.75, 33.25, 15.25);
        // surface.getButton (ButtonID.DEVICE_RIGHT).setBounds (624.0, 289.75, 33.25, 15.25);
        // surface.getButton (ButtonID.BROWSE).setBounds (747.75, 359.75, 33.25, 15.25);
        //
        // surface.getContinuous (ContinuousID.FADER_MASTER).setBounds (500.25, 348.5, 40.5, 115.0);
        // surface.getContinuous (ContinuousID.PLAY_POSITION).setBounds (497.75, 293.75, 40.25,
        // 37.75);
        // surface.getContinuous (ContinuousID.CROSSFADER).setBounds (651.25, 419.5, 104.0, 50.0);
        // surface.getContinuous (ContinuousID.FADER1).setBounds (19.75, 348.5, 40.5, 115.0);
        // surface.getContinuous (ContinuousID.KNOB1).setBounds (16.75, 19.0, 40.25, 37.75);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB1).setBounds (560.25, 173.75, 40.25,
        // 37.75);
        // surface.getContinuous (ContinuousID.FADER2).setBounds (80.75, 348.5, 40.5, 115.0);
        // surface.getContinuous (ContinuousID.KNOB2).setBounds (78.5, 19.0, 40.25, 37.75);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB2).setBounds (620.75, 173.75, 40.25,
        // 37.75);
        // surface.getContinuous (ContinuousID.FADER3).setBounds (141.5, 348.5, 40.5, 115.0);
        // surface.getContinuous (ContinuousID.KNOB3).setBounds (140.25, 19.0, 40.25, 37.75);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB3).setBounds (682.5, 173.75, 40.25,
        // 37.75);
        // surface.getContinuous (ContinuousID.FADER4).setBounds (202.5, 348.5, 40.5, 115.0);
        // surface.getContinuous (ContinuousID.KNOB4).setBounds (202.0, 19.0, 40.25, 37.75);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB4).setBounds (744.25, 173.75, 40.25,
        // 37.75);
        // surface.getContinuous (ContinuousID.FADER5).setBounds (263.5, 348.5, 40.5, 115.0);
        // surface.getContinuous (ContinuousID.KNOB5).setBounds (263.5, 19.0, 40.25, 37.75);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB5).setBounds (560.25, 235.75, 40.25,
        // 37.75);
        // surface.getContinuous (ContinuousID.FADER6).setBounds (324.25, 348.5, 40.5, 115.0);
        // surface.getContinuous (ContinuousID.KNOB6).setBounds (325.25, 19.0, 40.25, 37.75);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB6).setBounds (620.75, 235.75, 40.25,
        // 37.75);
        // surface.getContinuous (ContinuousID.FADER7).setBounds (385.25, 348.5, 40.5, 115.0);
        // surface.getContinuous (ContinuousID.KNOB7).setBounds (387.0, 19.0, 40.25, 37.75);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB7).setBounds (682.5, 235.75, 40.25,
        // 37.75);
        // surface.getContinuous (ContinuousID.FADER8).setBounds (446.25, 348.5, 40.5, 115.0);
        // surface.getContinuous (ContinuousID.KNOB8).setBounds (448.75, 19.0, 40.25, 37.75);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB8).setBounds (744.25, 235.75, 40.25,
        // 37.75);
        //
        // surface.getContinuous (ContinuousID.TEMPO).setBounds (743.25, 106.75, 40.25, 37.75);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final YaeltexTurnControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActive (Views.SESSION);
        surface.getModeManager ().setActive (Modes.TRACK);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        final YaeltexTurnControlSurface surface = this.getSurface ();
        final IMode mode = surface.getModeManager ().getActive ();
        if (mode instanceof IYaeltexKnobMode yMode)
        {
            for (int i = 0; i < 32; i++)
                surface.setLED (YaeltexTurnControlSurface.KNOB_DIGITAL_ROW1 + i, mode.getKnobValue (i), yMode.getKnobColor (i));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        return BindType.NOTE;
    }


    /**
     * Keep the matching view in sync to the selected mode
     *
     * @param activeMode The newly activated mode
     */
    private void onModeChange (final Modes activeMode)
    {
        // TODO get the matching view from a map

        // Modes.DRUM_SEQUENCER:
        // Modes.NOTE_SEQUENCER
        // Modes.DEVICE_LAYER
        // Modes.TRACK
    }
}
