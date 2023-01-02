// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn;

import de.mossgrabers.controller.yaeltex.turn.command.trigger.YaeltexTurnModeCursorCommand;
import de.mossgrabers.controller.yaeltex.turn.command.trigger.YaeltexTurnRecordCommand;
import de.mossgrabers.controller.yaeltex.turn.command.trigger.YaeltexTurnTapTempoCommand;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnColorManager;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.controller.yaeltex.turn.mode.YaeltexTurnDrumMixMode;
import de.mossgrabers.controller.yaeltex.turn.mode.YaeltexTurnDrumSeqMode;
import de.mossgrabers.controller.yaeltex.turn.mode.YaeltexTurnNoteSeqMode;
import de.mossgrabers.controller.yaeltex.turn.mode.YaeltexTurnTrackMixMode;
import de.mossgrabers.controller.yaeltex.turn.view.DrumView;
import de.mossgrabers.controller.yaeltex.turn.view.MonophonicSequencerView;
import de.mossgrabers.controller.yaeltex.turn.view.SessionView;
import de.mossgrabers.framework.command.TempoCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.ShiftCommand;
import de.mossgrabers.framework.command.trigger.application.OverdubCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
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
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;


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

    private Views sessionView = Views.SESSION;


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

        this.colorManager = new YaeltexTurnColorManager (host);
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new YaeltexTurnConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 68, 8, 4);
        this.scales.setDrumDefaultOffset (8);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (this.configuration.areMasterTracksIncluded ());
        ms.setNumScenes (4);
        ms.setNumSends (2);
        ms.setNumDrumPadLayers (8);
        ms.setNumDevicesInBank (16);
        ms.setNumParamPages (16);
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
        this.createNoteRepeatObservers (this.configuration, this.getSurface ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final YaeltexTurnControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.register (Modes.TRACK, new YaeltexTurnTrackMixMode (surface, this.model, CONTINUOUS_CONTROLS));
        modeManager.register (Modes.NOTE_SEQUENCER, new YaeltexTurnNoteSeqMode (surface, this.model, CONTINUOUS_CONTROLS));
        modeManager.register (Modes.DEVICE_LAYER, new YaeltexTurnDrumMixMode (surface, this.model, CONTINUOUS_CONTROLS));
        modeManager.register (Modes.DRUM_SEQUENCER, new YaeltexTurnDrumSeqMode (surface, this.model, CONTINUOUS_CONTROLS));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final YaeltexTurnControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.register (Views.SESSION, new SessionView (surface, this.model));
        viewManager.register (Views.SCENE_PLAY, new ScenePlayView<> (surface, this.model));
        viewManager.register (Views.SEQUENCER, new MonophonicSequencerView (surface, this.model));
        viewManager.register (Views.DRUM, new DrumView (surface, this.model));
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

        final ColorIntensity shiftIntensity = new ColorIntensity ( () -> surface.isPressed (ButtonID.SHIFT), YaeltexTurnColorManager.BUTTON_STATE_SHIFT);
        final ColorIntensity selectIntensity = new ColorIntensity ( () -> surface.isPressed (ButtonID.SELECT), YaeltexTurnColorManager.BUTTON_STATE_SELECT);

        this.addButton (ButtonID.SHIFT, "shift", new ShiftCommand<> (this.model, surface), channel, YaeltexTurnControlSurface.BUTTON_SHIFT, shiftIntensity);
        this.addButton (ButtonID.SELECT, "select", NopCommand.INSTANCE, channel, YaeltexTurnControlSurface.BUTTON_SELECT, selectIntensity);

        // Mode selection

        final ColorIntensity clipIntensity = new ColorIntensity ( () -> viewManager.isActive (Views.SEQUENCER, Views.DRUM), YaeltexTurnColorManager.BUTTON_STATE_SESSION);
        final ColorIntensity sessionIntensity = new ColorIntensity ( () -> viewManager.isActive (Views.SCENE_PLAY), YaeltexTurnColorManager.BUTTON_STATE_SESSION);

        this.addButton (ButtonID.CLIP, "clips", (event, velocity) -> this.toggleSequencer (event), channel, YaeltexTurnControlSurface.BUTTON_CLIPS, clipIntensity);
        this.addButton (ButtonID.SESSION, "session", (event, velocity) -> this.toggleSession (event), channel, YaeltexTurnControlSurface.BUTTON_SESSION, sessionIntensity);
        // TODO
        this.addButton (ButtonID.USER, "usr", new ModeSelectCommand<> (this.model, surface, Modes.DEVICE_LAYER), channel, YaeltexTurnControlSurface.BUTTON_USR);
        this.addButton (ButtonID.TRACK, "trk", (event, velocity) -> this.toggleTrackAndLayer (event), channel, YaeltexTurnControlSurface.BUTTON_TRK, () -> modeManager.isActive (Modes.TRACK, Modes.NOTE_SEQUENCER) ? 1 : 0, YaeltexTurnColorManager.BUTTON_STATE_TRACK, YaeltexTurnColorManager.BUTTON_STATE_LAYER);

        // Transport

        final OverdubCommand<YaeltexTurnControlSurface, YaeltexTurnConfiguration> overdubCommand = new OverdubCommand<> (this.model, surface);
        final RecordCommand<YaeltexTurnControlSurface, YaeltexTurnConfiguration> recordCommand = new YaeltexTurnRecordCommand (this.model, surface);

        final ColorIntensity playIntensity = new ColorIntensity (t::isPlaying, YaeltexTurnColorManager.BUTTON_STATE_PLAY);
        final ColorIntensity stopIntensity = new ColorIntensity ( () -> !t.isPlaying (), YaeltexTurnColorManager.BUTTON_STATE_STOP);
        final ColorIntensity recIntensity = new ColorIntensity (recordCommand::isActive, YaeltexTurnColorManager.BUTTON_STATE_REC);
        final ColorIntensity overdubIntensity = new ColorIntensity (overdubCommand::isActive, YaeltexTurnColorManager.BUTTON_STATE_OVERDUB);
        final ColorIntensity loopIntensity = new ColorIntensity (t::isLoop, YaeltexTurnColorManager.BUTTON_STATE_LOOP);
        final ColorIntensity tapTempoIntensity = new ColorIntensity ( () -> surface.isPressed (ButtonID.TAP_TEMPO), YaeltexTurnColorManager.BUTTON_STATE_TAP_TEMPO);

        this.addButton (ButtonID.PLAY, "play", new PlayCommand<> (this.model, surface), channel, YaeltexTurnControlSurface.BUTTON_PLAY, playIntensity);
        this.addButton (ButtonID.STOP, "stop", new StopCommand<> (this.model, surface), channel, YaeltexTurnControlSurface.BUTTON_STOP, stopIntensity);
        this.addButton (ButtonID.RECORD, "rec", recordCommand, channel, YaeltexTurnControlSurface.BUTTON_REC, recIntensity);
        this.addButton (ButtonID.OVERDUB, "overdub", overdubCommand, channel, YaeltexTurnControlSurface.BUTTON_OVERDUB, overdubIntensity);
        this.addButton (ButtonID.LOOP, "loop", new ToggleLoopCommand<> (this.model, surface), channel, YaeltexTurnControlSurface.BUTTON_LOOP, loopIntensity);
        this.addButton (ButtonID.TAP_TEMPO, "tap tempo", new YaeltexTurnTapTempoCommand (this.model, surface), channel, YaeltexTurnControlSurface.BUTTON_TAP_TEMPO, tapTempoIntensity);

        final YaeltexTurnModeCursorCommand leftCommand = new YaeltexTurnModeCursorCommand (Direction.LEFT, this.model, surface);
        final YaeltexTurnModeCursorCommand rightCommand = new YaeltexTurnModeCursorCommand (Direction.RIGHT, this.model, surface);
        final YaeltexTurnModeCursorCommand upCommand = new YaeltexTurnModeCursorCommand (Direction.UP, this.model, surface);
        final YaeltexTurnModeCursorCommand downCommand = new YaeltexTurnModeCursorCommand (Direction.DOWN, this.model, surface);

        final ColorIntensity leftIntensity = new ColorIntensity (leftCommand::canScroll, YaeltexTurnColorManager.BUTTON_STATE_ARROW);
        final ColorIntensity rightIntensity = new ColorIntensity (rightCommand::canScroll, YaeltexTurnColorManager.BUTTON_STATE_ARROW);
        final ColorIntensity upIntensity = new ColorIntensity (upCommand::canScroll, YaeltexTurnColorManager.BUTTON_STATE_ARROW);
        final ColorIntensity downIntensity = new ColorIntensity (downCommand::canScroll, YaeltexTurnColorManager.BUTTON_STATE_ARROW);

        this.addButton (ButtonID.ARROW_LEFT, "Left", leftCommand, channel, YaeltexTurnControlSurface.BUTTON_LEFT, leftIntensity);
        this.addButton (ButtonID.ARROW_RIGHT, "Right", rightCommand, channel, YaeltexTurnControlSurface.BUTTON_RIGHT, rightIntensity);
        this.addButton (ButtonID.ARROW_UP, "Up", upCommand, channel, YaeltexTurnControlSurface.BUTTON_UP, upIntensity);
        this.addButton (ButtonID.ARROW_DOWN, "Down", downCommand, channel, YaeltexTurnControlSurface.BUTTON_DOWN, downIntensity);

        for (int i = 0; i < 8; i++)
        {
            final int number = i + 1;

            int midiID = YaeltexTurnControlSurface.BUTTON_ROW1_1 + i * 2;
            final ButtonID crossfadeButtonID = ButtonID.get (ButtonID.ROW1_1, i);
            this.addButton (crossfadeButtonID, "A|B " + number, new ButtonRowModeCommand<> (0, i, this.model, surface), channel, midiID, () -> this.getModeColor (crossfadeButtonID));

            final ButtonID stopButtonID = ButtonID.get (ButtonID.ROW2_1, i);
            this.addButton (stopButtonID, "Stop " + number, new ButtonRowModeCommand<> (1, i, this.model, surface), channel, midiID + 1, () -> this.getModeColor (stopButtonID));

            midiID = YaeltexTurnControlSurface.BUTTON_ROW2_1 + i * 2;
            final ButtonID armButtonID = ButtonID.get (ButtonID.ROW3_1, i);
            this.addButton (armButtonID, "Arm " + number, new ButtonRowModeCommand<> (2, i, this.model, surface), channel, midiID, () -> this.getModeColor (armButtonID));

            final ButtonID soloButtonID = ButtonID.get (ButtonID.ROW4_1, i);
            this.addButton (soloButtonID, "Solo " + number, new ButtonRowModeCommand<> (3, i, this.model, surface), channel, midiID + 1, () -> this.getModeColor (soloButtonID));

            final ButtonID muteButtonID = ButtonID.get (ButtonID.ROW5_1, i);
            this.addButton (muteButtonID, "Mute " + number, new ButtonRowModeCommand<> (4, i, this.model, surface), channel, YaeltexTurnControlSurface.BUTTON_ROW3_1 + i, () -> this.getModeColor (muteButtonID));

            final ButtonID selectButtonID = ButtonID.get (ButtonID.ROW6_1, i);
            this.addButton (selectButtonID, "Select " + number, new ButtonRowModeCommand<> (5, i, this.model, surface), channel, YaeltexTurnControlSurface.BUTTON_ROW4_1 + i, () -> this.getModeColor (selectButtonID));
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

        surface.getButton (ButtonID.PAD1).setBounds (24.75, 288.0, 51.25, 22.5);
        surface.getButton (ButtonID.PAD2).setBounds (99.0, 287.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD3).setBounds (173.5, 287.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD4).setBounds (247.75, 287.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD5).setBounds (322.0, 287.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD6).setBounds (396.5, 287.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD7).setBounds (470.75, 287.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD8).setBounds (545.0, 287.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD9).setBounds (24.75, 215.0, 51.25, 22.5);
        surface.getButton (ButtonID.PAD10).setBounds (99.0, 214.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD11).setBounds (173.0, 214.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD12).setBounds (247.25, 214.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD13).setBounds (321.25, 214.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD14).setBounds (395.5, 214.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD15).setBounds (469.5, 214.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD16).setBounds (543.75, 214.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD17).setBounds (24.75, 138.5, 51.25, 22.5);
        surface.getButton (ButtonID.PAD18).setBounds (98.75, 138.0, 51.25, 22.5);
        surface.getButton (ButtonID.PAD19).setBounds (172.5, 138.0, 51.25, 22.5);
        surface.getButton (ButtonID.PAD20).setBounds (246.5, 138.0, 51.25, 22.5);
        surface.getButton (ButtonID.PAD21).setBounds (320.5, 138.0, 51.25, 22.5);
        surface.getButton (ButtonID.PAD22).setBounds (394.25, 138.0, 51.25, 22.5);
        surface.getButton (ButtonID.PAD23).setBounds (468.25, 138.0, 51.25, 22.5);
        surface.getButton (ButtonID.PAD24).setBounds (542.25, 138.0, 51.25, 22.5);
        surface.getButton (ButtonID.PAD25).setBounds (23.25, 64.25, 51.25, 22.5);
        surface.getButton (ButtonID.PAD26).setBounds (97.5, 63.75, 51.25, 22.5);
        surface.getButton (ButtonID.PAD27).setBounds (172.0, 63.75, 51.25, 22.5);
        surface.getButton (ButtonID.PAD28).setBounds (246.25, 63.75, 51.25, 22.5);
        surface.getButton (ButtonID.PAD29).setBounds (320.5, 63.75, 51.25, 22.5);
        surface.getButton (ButtonID.PAD30).setBounds (395.0, 63.75, 51.25, 22.5);
        surface.getButton (ButtonID.PAD31).setBounds (469.25, 63.75, 51.25, 22.5);
        surface.getButton (ButtonID.PAD32).setBounds (543.5, 63.75, 51.25, 22.5);
        surface.getButton (ButtonID.SHIFT).setBounds (633.5, 812.5, 20.25, 22.75);
        surface.getButton (ButtonID.SELECT).setBounds (633.5, 783.75, 20.25, 22.75);
        surface.getButton (ButtonID.CLIP).setBounds (633.5, 608.75, 20.25, 22.75);
        surface.getButton (ButtonID.SESSION).setBounds (663.0, 608.75, 20.25, 22.75);
        surface.getButton (ButtonID.USER).setBounds (633.5, 637.75, 20.25, 22.75);
        surface.getButton (ButtonID.TRACK).setBounds (663.0, 637.75, 20.25, 22.75);
        surface.getButton (ButtonID.PLAY).setBounds (633.5, 665.25, 20.25, 22.75);
        surface.getButton (ButtonID.STOP).setBounds (663.0, 665.25, 20.25, 22.75);
        surface.getButton (ButtonID.RECORD).setBounds (633.5, 695.75, 20.25, 22.75);
        surface.getButton (ButtonID.OVERDUB).setBounds (663.0, 695.75, 20.25, 22.75);
        surface.getButton (ButtonID.LOOP).setBounds (633.5, 726.0, 20.25, 22.75);
        surface.getButton (ButtonID.TAP_TEMPO).setBounds (663.0, 726.0, 20.25, 22.75);
        surface.getButton (ButtonID.ARROW_LEFT).setBounds (633.5, 754.5, 20.25, 22.75);
        surface.getButton (ButtonID.ARROW_RIGHT).setBounds (663.0, 754.5, 20.25, 22.75);
        surface.getButton (ButtonID.ARROW_UP).setBounds (663.0, 783.75, 20.25, 22.75);
        surface.getButton (ButtonID.ARROW_DOWN).setBounds (663.0, 812.5, 20.25, 22.75);
        surface.getButton (ButtonID.ROW1_1).setBounds (12.0, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW2_1).setBounds (49.25, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW3_1).setBounds (12.5, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW4_1).setBounds (49.75, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW5_1).setBounds (13.5, 688.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW6_1).setBounds (15.25, 728.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW1_2).setBounds (86.75, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW6_8).setBounds (537.5, 728.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW2_2).setBounds (124.0, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW3_2).setBounds (87.25, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW4_2).setBounds (124.5, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW5_2).setBounds (88.25, 688.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW6_2).setBounds (89.75, 728.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW1_3).setBounds (161.5, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW2_3).setBounds (198.75, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW3_3).setBounds (161.75, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW4_3).setBounds (199.0, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW5_3).setBounds (162.75, 688.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW6_3).setBounds (164.5, 728.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW1_4).setBounds (236.25, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW2_4).setBounds (273.5, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW3_4).setBounds (236.5, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW4_4).setBounds (273.75, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW5_4).setBounds (237.5, 688.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW6_4).setBounds (239.0, 728.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW1_5).setBounds (311.0, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW2_5).setBounds (348.25, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW5_8).setBounds (536.0, 688.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW3_5).setBounds (311.0, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW4_5).setBounds (348.25, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW5_5).setBounds (312.0, 688.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW6_5).setBounds (313.75, 728.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW1_6).setBounds (385.75, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW2_6).setBounds (423.0, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW3_6).setBounds (385.75, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW4_6).setBounds (423.0, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW5_6).setBounds (386.75, 688.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW6_6).setBounds (388.25, 728.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW1_7).setBounds (460.5, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW2_7).setBounds (496.75, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW3_7).setBounds (460.25, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW4_7).setBounds (497.5, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW5_7).setBounds (461.25, 688.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW6_7).setBounds (463.0, 728.25, 67.25, 27.75);
        surface.getButton (ButtonID.ROW1_8).setBounds (534.25, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW2_8).setBounds (571.5, 603.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW3_8).setBounds (535.0, 641.75, 31.25, 27.75);
        surface.getButton (ButtonID.ROW4_8).setBounds (572.25, 641.75, 31.25, 27.75);

        surface.getContinuous (ContinuousID.EQ_Q_KNOB3).setBounds (167.75, 411.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_FREQUENCY_KNOB3).setBounds (171.25, 464.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_GAIN_KNOB3).setBounds (175.0, 514.5, 38.25, 33.5);
        surface.getContinuous (ContinuousID.FADER3).setBounds (164.25, 765.5, 67.25, 75.5);
        surface.getContinuous (ContinuousID.PAN_KNOB4).setBounds (247.25, 18.5, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND1_KNOB4).setBounds (247.25, 97.25, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND2_KNOB4).setBounds (247.75, 174.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB4).setBounds (248.5, 247.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_TYPE_KNOB4).setBounds (243.0, 348.75, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_Q_KNOB4).setBounds (243.0, 411.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_FREQUENCY_KNOB4).setBounds (245.75, 464.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_GAIN_KNOB4).setBounds (249.75, 514.5, 38.25, 33.5);
        surface.getContinuous (ContinuousID.FADER4).setBounds (238.75, 765.5, 67.25, 75.5);
        surface.getContinuous (ContinuousID.PAN_KNOB5).setBounds (321.5, 18.5, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND1_KNOB5).setBounds (321.5, 97.25, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND2_KNOB5).setBounds (322.0, 174.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB5).setBounds (323.0, 247.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_TYPE_KNOB5).setBounds (317.5, 348.75, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_Q_KNOB5).setBounds (318.0, 411.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_FREQUENCY_KNOB5).setBounds (320.0, 464.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_GAIN_KNOB5).setBounds (324.5, 514.5, 38.25, 33.5);
        surface.getContinuous (ContinuousID.FADER5).setBounds (313.25, 765.5, 67.25, 75.5);
        surface.getContinuous (ContinuousID.PAN_KNOB6).setBounds (395.75, 18.5, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND1_KNOB6).setBounds (395.75, 97.25, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND2_KNOB6).setBounds (396.5, 174.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB6).setBounds (397.5, 247.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_TYPE_KNOB6).setBounds (391.75, 348.75, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_Q_KNOB6).setBounds (393.0, 411.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_FREQUENCY_KNOB6).setBounds (394.5, 464.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_GAIN_KNOB6).setBounds (399.25, 514.5, 38.25, 33.5);
        surface.getContinuous (ContinuousID.FADER6).setBounds (387.5, 765.5, 67.25, 75.5);
        surface.getContinuous (ContinuousID.PAN_KNOB7).setBounds (470.0, 18.5, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND1_KNOB7).setBounds (470.0, 97.25, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND2_KNOB7).setBounds (470.75, 174.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB7).setBounds (472.0, 247.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_TYPE_KNOB7).setBounds (466.25, 348.75, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_Q_KNOB7).setBounds (468.0, 411.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_FREQUENCY_KNOB7).setBounds (468.75, 464.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_GAIN_KNOB7).setBounds (473.75, 514.5, 38.25, 33.5);
        surface.getContinuous (ContinuousID.TEMPO).setBounds (636.5, 350.75, 39.0, 50.0);
        surface.getContinuous (ContinuousID.CROSSFADER).setBounds (636.5, 518.0, 39.5, 34.25);
        surface.getContinuous (ContinuousID.CUE).setBounds (636.5, 406.5, 39.0, 50.0);
        surface.getContinuous (ContinuousID.FADER_MASTER).setBounds (636.5, 462.25, 39.0, 50.0);
        surface.getContinuous (ContinuousID.PAN_KNOB1).setBounds (24.75, 19.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND1_KNOB1).setBounds (24.75, 97.75, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND2_KNOB1).setBounds (24.75, 174.5, 46.25, 33.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB1).setBounds (24.75, 247.5, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_TYPE_KNOB1).setBounds (24.75, 349.25, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_Q_KNOB1).setBounds (24.75, 411.5, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_FREQUENCY_KNOB1).setBounds (24.75, 464.5, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_GAIN_KNOB1).setBounds (24.75, 515.0, 38.25, 33.5);
        surface.getContinuous (ContinuousID.FADER1).setBounds (15.5, 765.5, 67.25, 76.75);
        surface.getContinuous (ContinuousID.PAN_KNOB2).setBounds (99.0, 18.5, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND1_KNOB2).setBounds (99.0, 97.25, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND2_KNOB2).setBounds (99.0, 174.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB2).setBounds (99.25, 247.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_TYPE_KNOB2).setBounds (94.25, 348.75, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_Q_KNOB2).setBounds (92.75, 411.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_FREQUENCY_KNOB2).setBounds (97.0, 464.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_GAIN_KNOB2).setBounds (100.5, 514.5, 38.25, 33.5);
        surface.getContinuous (ContinuousID.FADER2).setBounds (90.0, 765.5, 67.25, 75.5);
        surface.getContinuous (ContinuousID.PAN_KNOB3).setBounds (173.25, 18.5, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND1_KNOB3).setBounds (173.25, 97.25, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND2_KNOB3).setBounds (173.5, 174.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB3).setBounds (173.75, 247.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_TYPE_KNOB3).setBounds (168.5, 348.75, 46.25, 33.5);
        surface.getContinuous (ContinuousID.FADER7).setBounds (462.0, 765.5, 67.25, 75.5);
        surface.getContinuous (ContinuousID.PAN_KNOB8).setBounds (544.25, 18.5, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND1_KNOB8).setBounds (544.25, 97.25, 46.25, 33.5);
        surface.getContinuous (ContinuousID.SEND2_KNOB8).setBounds (545.0, 174.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB8).setBounds (546.5, 247.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_TYPE_KNOB8).setBounds (540.75, 348.75, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_Q_KNOB8).setBounds (543.25, 411.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_FREQUENCY_KNOB8).setBounds (543.25, 464.0, 46.25, 33.5);
        surface.getContinuous (ContinuousID.EQ_GAIN_KNOB8).setBounds (548.5, 514.5, 38.25, 33.5);
        surface.getContinuous (ContinuousID.FADER8).setBounds (536.5, 765.5, 67.25, 75.5);
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
        if (mode == null)
            return;
        for (int i = 0; i < 32; i++)
            surface.setLED (YaeltexTurnControlSurface.KNOB_DIGITAL_ROW1 + i, mode.getKnobValue (i), mode.getKnobColor (i));
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        return BindType.NOTE;
    }


    /**
     * Toggle the sequencer mode on/off.
     *
     * @param event The button event
     */
    private void toggleSequencer (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final YaeltexTurnControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        final Modes mode;
        if (modeManager.isActive (Modes.TRACK, Modes.NOTE_SEQUENCER))
            mode = modeManager.isActive (Modes.TRACK) ? Modes.NOTE_SEQUENCER : Modes.TRACK;
        else
            mode = modeManager.isActive (Modes.DEVICE_LAYER) ? Modes.DRUM_SEQUENCER : Modes.DEVICE_LAYER;
        modeManager.setActive (mode);
        this.host.showNotification (modeManager.getActive ().getName ());

        // Sync view
        final Views view;
        switch (mode)
        {
            case NOTE_SEQUENCER:
                view = Views.SEQUENCER;
                break;

            case DRUM_SEQUENCER:
                view = Views.DRUM;
                break;

            default:
                view = this.sessionView;
                break;
        }
        surface.getViewManager ().setActive (view);
    }


    /**
     * Toggle the session view between clips and scenes.
     *
     * @param event The button event
     */
    private void toggleSession (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.getSurface ().getViewManager ();
        if (viewManager.isActive (Views.SESSION, Views.SCENE_PLAY))
            this.sessionView = viewManager.isActive (Views.SESSION) ? Views.SCENE_PLAY : Views.SESSION;
        viewManager.setActive (this.sessionView);
        this.host.showNotification (viewManager.getActive ().getName ());

        // Sync mode
        final ModeManager modeManager = this.getSurface ().getModeManager ();
        if (modeManager.isActive (Modes.NOTE_SEQUENCER))
            modeManager.setActive (Modes.TRACK);
        else if (modeManager.isActive (Modes.DRUM_SEQUENCER))
            modeManager.setActive (Modes.DEVICE_LAYER);
    }


    /**
     * Toggle between track and layer mixer.
     *
     * @param event The button event
     */
    private void toggleTrackAndLayer (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final YaeltexTurnControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        final Modes mode;
        if (modeManager.isActive (Modes.TRACK, Modes.NOTE_SEQUENCER))
            mode = modeManager.isActive (Modes.TRACK) ? Modes.DEVICE_LAYER : Modes.DRUM_SEQUENCER;
        else
            mode = modeManager.isActive (Modes.DEVICE_LAYER) ? Modes.TRACK : Modes.NOTE_SEQUENCER;
        modeManager.setActive (mode);
        this.host.showNotification (modeManager.getActive ().getName ());

        // Sync view
        final Views view;
        switch (mode)
        {
            case NOTE_SEQUENCER:
                view = Views.SEQUENCER;
                break;

            case DRUM_SEQUENCER:
                view = Views.DRUM;
                break;

            default:
                view = this.sessionView;
                break;
        }
        surface.getViewManager ().setActive (view);
    }


    /** Sends additional color intensity when color state changes. */
    private class ColorIntensity implements IntSupplier
    {
        private final BooleanSupplier supplier;
        private final int             colorIndex;


        ColorIntensity (final BooleanSupplier supplier, final String colorID)
        {
            this.supplier = supplier;
            this.colorIndex = YaeltexTurnControllerSetup.this.getModel ().getColorManager ().getColorIndex (colorID);
        }


        /** {@inheritDoc} */
        @Override
        public int getAsInt ()
        {
            // Little trick to identify values which need an intensity
            return this.supplier.getAsBoolean () ? this.colorIndex : 128 + this.colorIndex;
        }
    }
}
