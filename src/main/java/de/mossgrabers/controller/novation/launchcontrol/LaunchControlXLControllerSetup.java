// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchcontrol;

import de.mossgrabers.controller.novation.launchcontrol.control.trigger.XLDeviceCommand;
import de.mossgrabers.controller.novation.launchcontrol.control.trigger.XLRecArmCommand;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.XLMixMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.XLSelectDeviceParamsPageMode;
import de.mossgrabers.controller.novation.launchcontrol.mode.XLTransportMode;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.DummyMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.MasterAndFXVolumeMode;
import de.mossgrabers.framework.mode.track.TrackMuteMode;
import de.mossgrabers.framework.mode.track.TrackRecArmMode;
import de.mossgrabers.framework.mode.track.TrackSoloMode;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Support for the Novation LauchControl XL controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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

    private final IHwAbsoluteKnob [] sendAKnobs = new IHwAbsoluteKnob [8];
    private final IHwAbsoluteKnob [] sendBKnobs = new IHwAbsoluteKnob [8];
    private final IHwAbsoluteKnob [] panKnobs   = new IHwAbsoluteKnob [8];


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

        this.configuration.addSettingObserver (LaunchControlXLConfiguration.ACTIVE_TEMPLATE, this::activateTemplate);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final LaunchControlXLControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.register (Modes.SEND, new XLMixMode (surface, this.model, KNOB_CONTROLS));
        modeManager.register (Modes.DUMMY, new DummyMode<> (surface, this.model, KNOB_CONTROLS));

        final ModeManager trackModeManager = surface.getTrackButtonModeManager ();
        trackModeManager.register (Modes.MUTE, new TrackMuteMode<> (surface, this.model));
        trackModeManager.register (Modes.SOLO, new TrackSoloMode<> (surface, this.model));
        trackModeManager.register (Modes.REC_ARM, new TrackRecArmMode<> (surface, this.model));
        trackModeManager.register (Modes.DEVICE_PARAMS, new XLSelectDeviceParamsPageMode (surface, this.model));
        trackModeManager.register (Modes.TRANSPORT, new XLTransportMode (surface, this.model));

        final ModeManager faderManager = surface.getFaderModeManager ();
        faderManager.register (Modes.VOLUME, new TrackVolumeMode<> (surface, this.model, true, FADER_IDS));
        faderManager.register (Modes.MASTER, new MasterAndFXVolumeMode<> (surface, this.model, true, FADER_IDS));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final LaunchControlXLControlSurface surface = this.getSurface ();
        final ModeManager trackModeManager = surface.getTrackButtonModeManager ();

        final int midiChannel = 8;

        final ModeCursorCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> upCommand = new ModeCursorCommand<> (Direction.LEFT, this.model, surface, true);
        this.addButton (ButtonID.MOVE_BANK_LEFT, "Send Previous", upCommand, midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_SEND_PREV, upCommand::canScroll);
        final ModeCursorCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> downCommand = new ModeCursorCommand<> (Direction.RIGHT, this.model, surface, true);
        this.addButton (ButtonID.MOVE_BANK_RIGHT, "Send Next", downCommand, midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_SEND_NEXT, downCommand::canScroll);

        final ModeCursorCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> leftCommand = new ModeCursorCommand<> (Direction.DOWN, this.model, surface, false);
        this.addButton (ButtonID.MOVE_TRACK_LEFT, "Previous", leftCommand, midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_TRACK_PREV, leftCommand::canScroll);
        final ModeCursorCommand<LaunchControlXLControlSurface, LaunchControlXLConfiguration> rightCommand = new ModeCursorCommand<> (Direction.UP, this.model, surface, false);
        this.addButton (ButtonID.MOVE_TRACK_RIGHT, "Next", rightCommand, midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_TRACK_NEXT, rightCommand::canScroll);

        final XLMixMode mixMode = (XLMixMode) surface.getModeManager ().get (Modes.SEND);
        this.addButton (ButtonID.DEVICE, "DEVICE", new XLDeviceCommand (surface, mixMode), midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_DEVICE, mixMode::isDeviceActive);
        this.addButton (ButtonID.MUTE, "MUTE", new ModeSelectCommand<> (trackModeManager, this.model, surface, Modes.MUTE), midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_MUTE, () -> trackModeManager.isActive (Modes.MUTE));
        this.addButton (ButtonID.SOLO, "SOLO", new ModeSelectCommand<> (trackModeManager, this.model, surface, Modes.SOLO), midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_SOLO, () -> trackModeManager.isActive (Modes.SOLO));
        this.addButton (ButtonID.REC_ARM, "REC ARM", new XLRecArmCommand (this.model, surface), midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_RECORD_ARM, () -> trackModeManager.isActive (Modes.REC_ARM));

        for (int i = 0; i < 4; i++)
        {
            final int j = 4 + i;

            final ButtonID row1ButtonID = ButtonID.get (ButtonID.ROW1_1, i);
            this.addButton (row1ButtonID, "Row 1: " + (i + 1), new ButtonRowModeCommand<> (0, i, this.model, surface), midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_TRACK_FOCUS_1 + i, () -> this.getModeColor (row1ButtonID));
            final ButtonID row14ButtonID = ButtonID.get (ButtonID.ROW1_1, j);
            this.addButton (row14ButtonID, "Row 1: " + (j + 1), new ButtonRowModeCommand<> (0, j, this.model, surface), midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_TRACK_FOCUS_5 + i, () -> this.getModeColor (row14ButtonID));

            final ButtonID row2ButtonID = ButtonID.get (ButtonID.ROW2_1, i);
            this.addButton (row2ButtonID, "Row 2: " + (i + 1), new ButtonRowModeCommand<> (trackModeManager, 0, i, this.model, surface), midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_TRACK_CONTROL_1 + i, () -> this.getTrackModeColor (row2ButtonID));
            final ButtonID row24ButtonID = ButtonID.get (ButtonID.ROW2_1, j);
            this.addButton (row24ButtonID, "Row 2: " + (j + 1), new ButtonRowModeCommand<> (trackModeManager, 0, j, this.model, surface), midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_TRACK_CONTROL_5 + i, () -> this.getTrackModeColor (row24ButtonID));
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

        for (int i = 0; i < 8; i++)
        {
            final int index = i;

            final int midiControl = LaunchControlXLControlSurface.LAUNCHCONTROL_KNOB_SEND_A_1 + i;
            this.sendAKnobs[i] = surface.createAbsoluteKnob (ContinuousID.get (ContinuousID.SEND1_KNOB1, i), "Send A Knob " + (i + 1));
            this.sendAKnobs[i].setIndexInGroup (i);
            this.sendAKnobs[i].addOutput ( () -> this.getKnobValue (0, index), value -> {
                final int green = value == 0 ? 0 : value / 42 + 1;
                final int midiChannel = this.configuration.getTemplate ();
                if (midiChannel >= 0)
                    surface.setKnobLED (midiChannel, 13 + (midiControl - 13) * 16, green, 0);
            });

            final int midiControl2 = LaunchControlXLControlSurface.LAUNCHCONTROL_KNOB_SEND_B_1 + i;
            this.sendBKnobs[i] = surface.createAbsoluteKnob (ContinuousID.get (ContinuousID.SEND2_KNOB1, i), "Send B Knob " + (i + 1));
            this.sendBKnobs[i].setIndexInGroup (i);
            this.sendBKnobs[i].addOutput ( () -> this.getKnobValue (8, index), value -> {
                final int red = value == 0 ? 0 : value / 42 + 1;
                final int midiChannel = this.configuration.getTemplate ();
                if (midiChannel >= 0)
                    surface.setKnobLED (midiChannel, 14 + (midiControl2 - 29) * 16, 0, red);
            });

            final int midiControl3 = LaunchControlXLControlSurface.LAUNCHCONTROL_KNOB_PAN_1 + i;
            this.panKnobs[i] = surface.createAbsoluteKnob (ContinuousID.get (ContinuousID.PAN_KNOB1, i), "Pan Knob " + (i + 1));
            this.panKnobs[i].setIndexInGroup (i);
            this.panKnobs[i].addOutput ( () -> this.getKnobValue (16, index), value -> {
                final int amber = value == 0 ? 0 : value / 42 + 1;
                final int midiChannel = this.configuration.getTemplate ();
                if (midiChannel >= 0)
                {
                    final XLMixMode mixMode = (XLMixMode) surface.getModeManager ().get (Modes.SEND);
                    final int red = mixMode.isDeviceActive () ? amber == 0 ? 0 : 1 : amber;
                    surface.setKnobLED (midiChannel, 15 + (midiControl3 - 49) * 16, amber, red);
                }
            });

            final int midiChannel = 8;
            final IHwFader fader = this.addFader (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), null, BindType.CC, midiChannel, LaunchControlXLControlSurface.LAUNCHCONTROL_FADER_1 + i);
            fader.setIndexInGroup (i);
        }
    }


    private void bindKnobsToTemplate ()
    {
        final LaunchControlXLControlSurface surface = this.getSurface ();

        final int midiChannel = this.configuration.getTemplate ();

        for (int i = 0; i < 8; i++)
        {
            final int midiControl = LaunchControlXLControlSurface.LAUNCHCONTROL_KNOB_SEND_A_1 + i;
            this.sendAKnobs[i].bind (surface.getMidiInput (), BindType.CC, midiChannel, midiControl);

            final int midiControl2 = LaunchControlXLControlSurface.LAUNCHCONTROL_KNOB_SEND_B_1 + i;
            this.sendBKnobs[i].bind (surface.getMidiInput (), BindType.CC, midiChannel, midiControl2);

            final int midiControl3 = LaunchControlXLControlSurface.LAUNCHCONTROL_KNOB_PAN_1 + i;
            this.panKnobs[i].bind (surface.getMidiInput (), BindType.CC, midiChannel, midiControl3);
        }
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
     * Get the color for a button, which is controlled by the active mode.
     *
     * @param buttonID The ID of the button
     * @return A color index
     */
    protected int getTrackModeColor (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.ROW2_1.ordinal ();
        if (index < 0 || index >= 8)
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        final Modes activeID = this.getSurface ().getTrackButtonModeManager ().getActiveID ();
        if (activeID == null)
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        if (activeID == Modes.DEVICE_PARAMS)
        {
            final IParameterPageBank parameterPageBank = this.model.getCursorDevice ().getParameterPageBank ();
            if (index >= parameterPageBank.getItemCount ())
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
            return index == parameterPageBank.getSelectedItemIndex () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW_LO;
        }
        else if (activeID == Modes.TRANSPORT)
        {
            final ITransport transport = this.model.getTransport ();
            switch (index)
            {
                case 0:
                    return transport.isPlaying () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN_LO;
                case 1:
                    return transport.isRecording () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED_LO;
                case 2, 3:
                    return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER_LO;
                case 4:
                    return transport.isLoop () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW_LO;
                case 5:
                    return transport.isMetronomeOn () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN_LO;
                case 6:
                    return transport.isWritingArrangerAutomation () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED_LO;
                case 7:
                    return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER_LO;
                default:
                    return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
            }
        }

        final ITrack track = this.model.getTrackBank ().getItem (index);
        if (!track.doesExist ())
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        switch (activeID)
        {
            case MUTE:
                return track.isMute () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER_LO;
            case SOLO:
                return track.isSolo () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_GREEN_LO;
            case REC_ARM:
                return track.isRecArm () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED_LO;
            default:
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
        }
    }


    private int getKnobValue (final int knobOffset, final int index)
    {
        final IMode mode = this.getSurface ().getModeManager ().getActive ();
        return mode == null ? 0 : Math.max (0, mode.getKnobValue (knobOffset + index));
    }


    private void activateTemplate ()
    {
        final int templateID = this.configuration.getTemplate ();
        this.host.println ("Switch to template: " + templateID);

        Modes mode = Modes.DUMMY;
        if (templateID == 8)
        {
            mode = Modes.SEND;
        }

        this.getSurface ().getModeManager ().setActive (mode);

        if (templateID >= 8)
            this.bindKnobsToTemplate ();
    }
}
