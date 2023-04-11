// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchcontrol;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.IXLMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.XLDummyMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLDrumSequencerMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLLayerMuteMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLLayerSoloMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLLoopLengthMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLNoteSequencerMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLScenesMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLSelectDeviceParamsPageMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLSequencerResolutionMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLTrackMuteMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLTrackRecArmMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLTrackSoloMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLTransportMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.faders.XLEqGainMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.faders.XLLayerVolumeMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.main.XLDrumPadEditMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.main.XLEqMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.main.XLLayerMixMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.main.XLNoteEditMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.main.XLTrackMixMode;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.DummyMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.MasterAndFXVolumeMode;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;


/**
 * Support for the Novation LauchControl XL controller.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchControlXLControllerSetup extends AbstractControllerSetup<LaunchControlXLControlSurface, LaunchControlXLConfiguration>
{
    private static final List<ContinuousID> FADER_IDS     = ContinuousID.createSequentialList (ContinuousID.FADER1, 8);
    private static final List<ContinuousID> KNOB_CONTROLS = new ArrayList<> (24);
    static
    {
        KNOB_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.SEND1_KNOB1, 8));
        KNOB_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.SEND2_KNOB1, 8));
        KNOB_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.PAN_KNOB1, 8));
    }

    private final IHwAbsoluteKnob []     sendAKnobs     = new IHwAbsoluteKnob [8];
    private final IHwAbsoluteKnob []     sendBKnobs     = new IHwAbsoluteKnob [8];
    private final IHwAbsoluteKnob []     panKnobs       = new IHwAbsoluteKnob [8];
    private final IHwFader []            faders         = new IHwFader [8];
    private final Map<ButtonID, Integer> buttonControls = new EnumMap<> (ButtonID.class);


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public LaunchControlXLControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new LaunchControlXLColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new LaunchControlXLConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (this.configuration.areMasterTracksIncluded ());
        ms.setNumSends (2);
        ms.setNumDrumPadLayers (8);
        ms.enableDevice (DeviceID.EQ);
        ms.enableDevice (DeviceID.FIRST_INSTRUMENT);
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
        this.model.getTrackBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();

        final IMidiInput input = midiAccess.createInput ("User Templates",
                // Route all CC from user template on channel 1-8
                "B0????", "B1????", "B2????", "B3????", "B4????", "B5????", "B6????", "B7????",
                // Route all note on from user template on channel 1-8
                "90????", "91????", "92????", "93????", "94????", "95????", "96????", "97????",
                // Route all note off from user template on channel 1-8
                "80????", "81????", "82????", "83????", "84????", "85????", "86????", "87????");

        this.surfaces.add (new LaunchControlXLControlSurface (this.host, this.colorManager, this.configuration, output, input));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.configuration.registerDeactivatedItemsHandler (this.model);
        this.createScaleObservers (this.configuration);
        this.configuration.addSettingObserver (LaunchControlXLConfiguration.ACTIVE_TEMPLATE, this::activateTemplate);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final LaunchControlXLControlSurface surface = this.getSurface ();

        this.createButton (ButtonID.REC_ARM, "REC ARM", new ButtonRowModeCommand<> (2, 3, this.model, surface), LaunchControlXLControlSurface.LAUNCHCONTROL_RECORD_ARM, () -> this.getModeColor (ButtonID.REC_ARM));

        final ModeManager modeManager = surface.getModeManager ();
        modeManager.register (Modes.SEND, new XLTrackMixMode (surface, this.model, KNOB_CONTROLS));
        modeManager.register (Modes.EQ_DEVICE_PARAMS, new XLEqMode (surface, this.model, KNOB_CONTROLS));
        modeManager.register (Modes.DEVICE_LAYER, new XLLayerMixMode (surface, this.model, KNOB_CONTROLS));
        final XLDrumPadEditMode drumPadEditMode = new XLDrumPadEditMode (surface, this.model, 127, 8, KNOB_CONTROLS);
        modeManager.register (Modes.DRUM_SEQUENCER, drumPadEditMode);
        final XLNoteEditMode noteEditMode = new XLNoteEditMode (surface, this.model, 127, 8, KNOB_CONTROLS);
        modeManager.register (Modes.NOTE_SEQUENCER, noteEditMode);
        modeManager.register (Modes.DUMMY, new XLDummyMode (surface, this.model, KNOB_CONTROLS));

        final ModeManager trackModeManager = surface.getTrackButtonModeManager ();
        trackModeManager.register (Modes.MUTE, new XLTrackMuteMode (surface, this.model));
        trackModeManager.register (Modes.SOLO, new XLTrackSoloMode (surface, this.model));
        trackModeManager.register (Modes.REC_ARM, new XLTrackRecArmMode (surface, this.model));
        trackModeManager.register (Modes.DEVICE_PARAMS, new XLSelectDeviceParamsPageMode (surface, this.model, this.model.getCursorDevice ()));
        trackModeManager.register (Modes.INSTRUMENT_DEVICE_PARAMS, new XLSelectDeviceParamsPageMode (surface, this.model, this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT)));
        trackModeManager.register (Modes.TRANSPORT, new XLTransportMode (surface, this.model));
        trackModeManager.register (Modes.CLIP, new XLScenesMode (surface, this.model));
        trackModeManager.register (Modes.DEVICE_LAYER_MUTE, new XLLayerMuteMode (surface, this.model));
        trackModeManager.register (Modes.DEVICE_LAYER_SOLO, new XLLayerSoloMode (surface, this.model));
        trackModeManager.register (Modes.DRUM_SEQUENCER, new XLDrumSequencerMode (surface, this.model));
        trackModeManager.register (Modes.NOTE_SEQUENCER, new XLNoteSequencerMode (surface, this.model));
        trackModeManager.register (Modes.LOOP_LENGTH, new XLLoopLengthMode (surface, this.model));
        trackModeManager.register (Modes.CONFIGURATION, new XLSequencerResolutionMode (surface, this.model));

        final ModeManager faderManager = surface.getFaderModeManager ();
        faderManager.register (Modes.VOLUME, new TrackVolumeMode<> (surface, this.model, true, FADER_IDS));
        faderManager.register (Modes.MASTER, new MasterAndFXVolumeMode<> (surface, this.model, true, FADER_IDS));
        faderManager.register (Modes.EQ_DEVICE_PARAMS, new XLEqGainMode (surface, this.model, FADER_IDS));
        faderManager.register (Modes.DEVICE_LAYER, new XLLayerVolumeMode (surface, this.model, FADER_IDS));
        faderManager.register (Modes.DRUM_SEQUENCER, drumPadEditMode);
        faderManager.register (Modes.NOTE_SEQUENCER, noteEditMode);
        faderManager.register (Modes.DUMMY, new DummyMode<> (surface, this.model, FADER_IDS));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final LaunchControlXLControlSurface surface = this.getSurface ();
        final ModeManager trackModeManager = surface.getTrackButtonModeManager ();

        final ModeCursorCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> upCommand = new ModeCursorCommand<> (Direction.LEFT, this.model, surface, false);
        this.createButton (ButtonID.MOVE_BANK_LEFT, "Send Previous", upCommand, LaunchControlXLControlSurface.LAUNCHCONTROL_SEND_PREV, upCommand::canScroll);
        final ModeCursorCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> downCommand = new ModeCursorCommand<> (Direction.RIGHT, this.model, surface, false);
        this.createButton (ButtonID.MOVE_BANK_RIGHT, "Send Next", downCommand, LaunchControlXLControlSurface.LAUNCHCONTROL_SEND_NEXT, downCommand::canScroll);

        final ModeCursorCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> leftCommand = new ModeCursorCommand<> (Direction.DOWN, this.model, surface, false);
        this.createButton (ButtonID.MOVE_TRACK_LEFT, "Previous", leftCommand, LaunchControlXLControlSurface.LAUNCHCONTROL_TRACK_PREV, leftCommand::canScroll);
        final ModeCursorCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> rightCommand = new ModeCursorCommand<> (Direction.UP, this.model, surface, false);
        this.createButton (ButtonID.MOVE_TRACK_RIGHT, "Next", rightCommand, LaunchControlXLControlSurface.LAUNCHCONTROL_TRACK_NEXT, rightCommand::canScroll);

        this.createButton (ButtonID.DEVICE, "DEVICE", new ButtonRowModeCommand<> (2, 0, this.model, surface), LaunchControlXLControlSurface.LAUNCHCONTROL_DEVICE, () -> this.getModeColor (ButtonID.DEVICE));
        this.createButton (ButtonID.MUTE, "MUTE", new ButtonRowModeCommand<> (2, 1, this.model, surface), LaunchControlXLControlSurface.LAUNCHCONTROL_MUTE, () -> this.getModeColor (ButtonID.MUTE));
        this.createButton (ButtonID.SOLO, "SOLO", new ButtonRowModeCommand<> (2, 2, this.model, surface), LaunchControlXLControlSurface.LAUNCHCONTROL_SOLO, () -> this.getModeColor (ButtonID.SOLO));

        for (int i = 0; i < 4; i++)
        {
            final int j = 4 + i;

            final ButtonID row1ButtonID = ButtonID.get (ButtonID.ROW1_1, i);
            this.createButton (row1ButtonID, "Row 1: " + (i + 1), new ButtonRowModeCommand<> (0, i, this.model, surface), LaunchControlXLControlSurface.LAUNCHCONTROL_TRACK_FOCUS_1 + i, () -> this.getModeColor (row1ButtonID));
            final ButtonID row14ButtonID = ButtonID.get (ButtonID.ROW1_1, j);
            this.createButton (row14ButtonID, "Row 1: " + (j + 1), new ButtonRowModeCommand<> (0, j, this.model, surface), LaunchControlXLControlSurface.LAUNCHCONTROL_TRACK_FOCUS_5 + i, () -> this.getModeColor (row14ButtonID));

            final ButtonID row2ButtonID = ButtonID.get (ButtonID.ROW2_1, i);
            this.createButton (row2ButtonID, "Row 2: " + (i + 1), new ButtonRowModeCommand<> (trackModeManager, 0, i, this.model, surface), LaunchControlXLControlSurface.LAUNCHCONTROL_TRACK_CONTROL_1 + i, () -> this.getTrackModeColor (row2ButtonID));
            final ButtonID row24ButtonID = ButtonID.get (ButtonID.ROW2_1, j);
            this.createButton (row24ButtonID, "Row 2: " + (j + 1), new ButtonRowModeCommand<> (trackModeManager, 0, j, this.model, surface), LaunchControlXLControlSurface.LAUNCHCONTROL_TRACK_CONTROL_5 + i, () -> this.getTrackModeColor (row24ButtonID));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.MOVE_TRACK_LEFT || buttonID == ButtonID.MOVE_TRACK_RIGHT || buttonID == ButtonID.MOVE_BANK_LEFT || buttonID == ButtonID.MOVE_BANK_RIGHT)
            return BindType.CC;
        return BindType.NOTE;
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final LaunchControlXLControlSurface surface = this.getSurface ();

        // Command bindings are only for 'non-parameter' modes like note editing
        // MIDI binding happens separately since each template has its' own MIDI channel and needs
        // to be re-bound on each template selection
        for (int i = 0; i < 8; i++)
        {
            final int index = i;

            this.sendAKnobs[i] = surface.createAbsoluteKnob (ContinuousID.get (ContinuousID.SEND1_KNOB1, i), "Send A Knob " + (i + 1));
            this.sendAKnobs[i].bind (new KnobRowModeCommand<> (i, this.model, surface));
            this.sendAKnobs[i].setIndexInGroup (i);
            this.sendAKnobs[i].addOutput ( () -> this.getKnobValue (0, index), value -> this.setKnobRowColor (0, index, value));

            this.sendBKnobs[i] = surface.createAbsoluteKnob (ContinuousID.get (ContinuousID.SEND2_KNOB1, i), "Send B Knob " + (i + 1));
            this.sendBKnobs[i].bind (new KnobRowModeCommand<> (8 + i, this.model, surface));
            this.sendBKnobs[i].setIndexInGroup (i);
            this.sendBKnobs[i].addOutput ( () -> this.getKnobValue (8, index), value -> this.setKnobRowColor (1, index, value));

            this.panKnobs[i] = surface.createAbsoluteKnob (ContinuousID.get (ContinuousID.PAN_KNOB1, i), "Pan Knob " + (i + 1));
            this.panKnobs[i].bind (new KnobRowModeCommand<> (16 + i, this.model, surface));
            this.panKnobs[i].setIndexInGroup (i);
            this.panKnobs[i].addOutput ( () -> this.getKnobValue (16, index), value -> this.setKnobRowColor (2, index, value));

            this.faders[i] = surface.createFader (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), true);
            this.faders[i].bind (new KnobRowModeCommand<> (24 + i, this.model, surface));
            this.faders[i].setIndexInGroup (i);
        }
    }


    /**
     * Re-bind all controls to the MIDI channel of the selected LaunchControl XL factory template.
     * Each template has its' own MIDI channel (8-16).
     */
    private void bindToTemplate ()
    {
        final LaunchControlXLControlSurface surface = this.getSurface ();

        final int midiChannel = this.configuration.getTemplate ();
        if (midiChannel < 0)
            return;

        final IMidiInput midiInput = surface.getMidiInput ();

        for (int i = 0; i < 8; i++)
        {
            this.sendAKnobs[i].bind (midiInput, BindType.CC, midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_KNOB_SEND_A_1 + i);
            this.sendBKnobs[i].bind (midiInput, BindType.CC, midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_KNOB_SEND_B_1 + i);
            this.panKnobs[i].bind (midiInput, BindType.CC, midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_KNOB_PAN_1 + i);
            this.faders[i].bind (midiInput, BindType.CC, midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_FADER_1 + i);
        }

        for (final Map.Entry<ButtonID, IHwButton> buttonEntry: surface.getButtons ().entrySet ())
        {
            final ButtonID key = buttonEntry.getKey ();
            final BindType bindType = this.getTriggerBindType (key);
            final Integer midiControl = this.buttonControls.get (key);
            buttonEntry.getValue ().bind (midiInput, bindType, midiChannel, midiControl.intValue ());
        }

        surface.forceFlush ();
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final LaunchControlXLControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.MOVE_BANK_LEFT).setBounds (526.25, 21.75, 35.5, 34.75);
        surface.getButton (ButtonID.MOVE_BANK_RIGHT).setBounds (577.0, 21.0, 35.5, 34.75);
        surface.getButton (ButtonID.MOVE_TRACK_LEFT).setBounds (527.5, 71.0, 35.5, 34.75);
        surface.getButton (ButtonID.MOVE_TRACK_RIGHT).setBounds (578.25, 70.25, 35.5, 34.75);
        surface.getButton (ButtonID.MUTE).setBounds (547.25, 183.5, 46.75, 34.75);
        surface.getButton (ButtonID.SOLO).setBounds (547.25, 233.25, 46.75, 34.75);
        surface.getButton (ButtonID.REC_ARM).setBounds (547.25, 288.0, 46.75, 34.75);
        surface.getButton (ButtonID.ROW1_1).setBounds (18.5, 339.5, 37.75, 34.75);
        surface.getButton (ButtonID.ROW1_5).setBounds (271.5, 339.5, 37.75, 34.75);
        surface.getButton (ButtonID.ROW2_1).setBounds (18.5, 392.75, 37.75, 34.75);
        surface.getButton (ButtonID.ROW2_5).setBounds (271.25, 392.75, 37.75, 34.75);
        surface.getButton (ButtonID.ROW1_2).setBounds (81.75, 339.5, 37.75, 34.75);
        surface.getButton (ButtonID.ROW1_6).setBounds (334.75, 339.5, 37.75, 35.0);
        surface.getButton (ButtonID.ROW2_2).setBounds (81.75, 392.75, 37.75, 34.75);
        surface.getButton (ButtonID.ROW2_6).setBounds (334.25, 392.75, 37.75, 34.75);
        surface.getButton (ButtonID.ROW1_3).setBounds (145.0, 339.5, 37.75, 34.75);
        surface.getButton (ButtonID.ROW1_7).setBounds (398.0, 339.5, 37.75, 34.75);
        surface.getButton (ButtonID.ROW2_3).setBounds (144.75, 392.75, 37.75, 34.75);
        surface.getButton (ButtonID.ROW2_7).setBounds (397.5, 392.75, 37.75, 34.75);
        surface.getButton (ButtonID.ROW1_4).setBounds (208.25, 339.5, 37.75, 34.75);
        surface.getButton (ButtonID.ROW1_8).setBounds (461.25, 339.5, 37.75, 34.75);
        surface.getButton (ButtonID.ROW2_4).setBounds (208.0, 392.75, 37.75, 34.75);
        surface.getButton (ButtonID.ROW2_8).setBounds (460.5, 392.75, 37.75, 34.75);

        surface.getContinuous (ContinuousID.SEND1_KNOB1).setBounds (2.0, 22.0, 70.5, 34.0);
        surface.getContinuous (ContinuousID.SEND1_KNOB2).setBounds (65.5, 22.0, 70.5, 34.0);
        surface.getContinuous (ContinuousID.SEND1_KNOB3).setBounds (128.75, 22.0, 70.5, 34.0);
        surface.getContinuous (ContinuousID.SEND1_KNOB4).setBounds (192.25, 22.0, 70.5, 34.0);
        surface.getContinuous (ContinuousID.SEND1_KNOB5).setBounds (255.75, 22.0, 70.5, 34.0);
        surface.getContinuous (ContinuousID.SEND1_KNOB6).setBounds (319.25, 22.0, 70.5, 34.0);
        surface.getContinuous (ContinuousID.SEND1_KNOB7).setBounds (382.5, 22.0, 70.5, 34.0);
        surface.getContinuous (ContinuousID.SEND1_KNOB8).setBounds (446.0, 22.0, 70.5, 34.0);

        surface.getContinuous (ContinuousID.SEND2_KNOB1).setBounds (2.0, 75.0, 69.75, 34.0);
        surface.getContinuous (ContinuousID.SEND2_KNOB2).setBounds (65.25, 75.0, 69.75, 34.0);
        surface.getContinuous (ContinuousID.SEND2_KNOB3).setBounds (128.75, 75.0, 69.75, 34.0);
        surface.getContinuous (ContinuousID.SEND2_KNOB4).setBounds (192.0, 75.0, 69.75, 34.0);
        surface.getContinuous (ContinuousID.SEND2_KNOB5).setBounds (255.25, 75.0, 69.75, 34.0);
        surface.getContinuous (ContinuousID.SEND2_KNOB6).setBounds (318.75, 75.0, 69.75, 34.0);
        surface.getContinuous (ContinuousID.SEND2_KNOB7).setBounds (382.0, 75.0, 69.75, 34.0);
        surface.getContinuous (ContinuousID.SEND2_KNOB8).setBounds (445.25, 76.5, 69.75, 34.0);

        surface.getContinuous (ContinuousID.PAN_KNOB1).setBounds (2.0, 127.25, 69.75, 34.0);
        surface.getContinuous (ContinuousID.PAN_KNOB2).setBounds (65.25, 127.25, 69.75, 34.0);
        surface.getContinuous (ContinuousID.PAN_KNOB3).setBounds (128.5, 127.25, 69.75, 34.0);
        surface.getContinuous (ContinuousID.PAN_KNOB4).setBounds (191.75, 127.25, 69.75, 34.0);
        surface.getContinuous (ContinuousID.PAN_KNOB5).setBounds (255.0, 127.25, 69.75, 34.0);
        surface.getContinuous (ContinuousID.PAN_KNOB6).setBounds (318.25, 127.25, 69.75, 34.0);
        surface.getContinuous (ContinuousID.PAN_KNOB7).setBounds (381.5, 128.0, 69.75, 34.0);
        surface.getContinuous (ContinuousID.PAN_KNOB8).setBounds (444.5, 127.25, 69.75, 34.0);

        surface.getContinuous (ContinuousID.FADER1).setBounds (19.25, 190.75, 34.75, 130.25);
        surface.getContinuous (ContinuousID.FADER2).setBounds (82.5, 190.75, 34.75, 130.25);
        surface.getContinuous (ContinuousID.FADER3).setBounds (145.75, 190.75, 34.75, 130.25);
        surface.getContinuous (ContinuousID.FADER4).setBounds (209.0, 190.75, 34.75, 130.25);
        surface.getContinuous (ContinuousID.FADER5).setBounds (272.25, 190.75, 34.75, 130.25);
        surface.getContinuous (ContinuousID.FADER6).setBounds (335.5, 190.75, 34.75, 130.25);
        surface.getContinuous (ContinuousID.FADER7).setBounds (398.75, 190.75, 34.75, 130.25);
        surface.getContinuous (ContinuousID.FADER8).setBounds (462.0, 190.75, 34.75, 130.25);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final LaunchControlXLControlSurface surface = this.getSurface ();
        surface.getTrackButtonModeManager ().setActive (Modes.MUTE);
        surface.getFaderModeManager ().setActive (Modes.VOLUME);
        surface.selectTemplate (8);
    }


    /**
     * Get the color for a button, which is controlled by the active track button mode.
     *
     * @param buttonID The ID of the button
     * @return A color index
     */
    protected int getTrackModeColor (final ButtonID buttonID)
    {
        final IMode mode = this.getSurface ().getTrackButtonModeManager ().getActive ();
        return mode == null ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK : mode.getButtonColor (buttonID);
    }


    /**
     * Get the value of one of the knobs.
     *
     * @param knobOffset The row offset
     * @param index The index of the column
     * @return The value
     */
    private int getKnobValue (final int knobOffset, final int index)
    {
        final IMode mode = this.getSurface ().getModeManager ().getActive ();
        return mode == null ? 0 : Math.max (0, mode.getKnobValue (knobOffset + index));
    }


    /**
     * Set the color of a knobs' LED.
     *
     * @param row The row of the knob
     * @param column The column of the knob
     * @param value The value to set
     */
    private void setKnobRowColor (final int row, final int column, final int value)
    {
        final ModeManager modeManager = this.getSurface ().getModeManager ();
        if (modeManager.getActive () instanceof final IXLMode xlMode)
            xlMode.setKnobColor (row, column, value);
    }


    /**
     * A different factory template has been selected. Update all modes and bindings.
     */
    private void activateTemplate ()
    {
        final int templateID = this.configuration.getTemplate ();
        this.host.println ("Switch to template: " + templateID);

        final Modes mode;
        final Modes faderMode;
        final String message;
        switch (templateID)
        {
            // Factory mode 1 - Track Mixer
            case 8:
                mode = Modes.SEND;
                faderMode = Modes.VOLUME;
                message = "Mixer";
                break;
            // Factory mode 2 - Equalizer
            case 9:
                mode = Modes.EQ_DEVICE_PARAMS;
                faderMode = Modes.EQ_DEVICE_PARAMS;
                message = "Equalizer";
                break;
            // Factory mode 3 - Layer Mixer
            case 10:
                mode = Modes.DEVICE_LAYER;
                faderMode = Modes.DEVICE_LAYER;
                message = "Layer Mixer";
                break;

            // Factory mode 4 - Drum Sequencer
            case 11:
                mode = Modes.DRUM_SEQUENCER;
                faderMode = Modes.DRUM_SEQUENCER;
                message = "Drum Sequencer";
                break;

            // Factory mode 5 - Note Sequencer
            case 12:
                mode = Modes.NOTE_SEQUENCER;
                faderMode = Modes.NOTE_SEQUENCER;
                message = "Note Sequencer";
                break;

            default:
                mode = Modes.DUMMY;
                faderMode = Modes.DUMMY;
                message = "";
                break;
        }

        final LaunchControlXLControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActive (mode);
        surface.getFaderModeManager ().setActive (faderMode);

        if (templateID >= 8)
        {
            this.bindToTemplate ();
            if (message.length () > 0)
                this.host.showNotification ("Selected Mode: " + message);
        }
    }


    /**
     * Create a hardware button proxy, bind a trigger command to it.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param command The command to bind
     * @param midiControl The MIDI CC or note
     * @param supplier Callback for retrieving the state of the light
     */
    private void createButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiControl, final BooleanSupplier supplier)
    {
        this.createButton (buttonID, label, command, midiControl, () -> supplier.getAsBoolean () ? 127 : 0);
    }


    /**
     * Create a hardware button proxy, bind a trigger command to it.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param command The command to bind
     * @param midiControl The MIDI CC or note
     * @param supplier Callback for retrieving the state of the light
     */
    private void createButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiControl, final IntSupplier supplier)
    {
        this.buttonControls.put (buttonID, Integer.valueOf (midiControl));
        final LaunchControlXLControlSurface surface = this.getSurface ();
        final IHwButton button = surface.createButton (buttonID, label);
        button.bind (command);
        final IntSupplier intSupplier = () -> button.isPressed () ? 1 : 0;
        final IntSupplier supp = supplier == null ? intSupplier : supplier;
        final BindType bindType = this.getTriggerBindType (buttonID);
        surface.createLight (null, supp, color -> surface.setTrigger (bindType, this.configuration.getTemplate (), midiControl, color), state -> this.colorManager.getColor (state, buttonID), button);
    }
}
