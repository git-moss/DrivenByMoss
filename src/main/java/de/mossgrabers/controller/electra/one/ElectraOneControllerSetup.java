// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one;

import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.controller.electra.one.mode.MixerMode;
import de.mossgrabers.controller.electra.one.mode.SendsMode;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.DummyView;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Electra.One controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ElectraOneControllerSetup extends AbstractControllerSetup<ElectraOneControlSurface, ElectraOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public ElectraOneControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new ElectraOneColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new ElectraOneConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        // TODO
        // this.surfaces.forEach (surface -> {
        // final ModeManager modeManager = surface.getModeManager ();
        // final Modes mode = modeManager.getActiveID ();
        // this.updateMode (mode);
        //
        // if (mode == null)
        // return;
        //
        // this.updateVUMeters ();
        // this.updateFaders (surface.isShiftPressed ());
        // this.updateSegmentDisplay ();
        //
        // final IMode activeOrTempMode = modeManager.getActive ();
        // if (activeOrTempMode instanceof final BaseMode<?> baseMode)
        // baseMode.updateKnobLEDs ();
        // });
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();

        ms.enableDrumDevice (false);
        // TODO ms.enableDevice (DeviceID.EQ);

        ms.setNumTracks (5);

        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (true);
        ms.setNumSends (6);

        // TODO
        ms.setNumScenes (8);
        ms.setNumFilterColumnEntries (8);
        ms.setNumResults (8);
        ms.setNumParamPages (8);
        ms.setNumParams (8);
        ms.setNumDeviceLayers (0);
        ms.setNumDrumPadLayers (0);
        ms.setNumMarkers (8);

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

        final IMidiOutput output = midiAccess.createOutput (0);
        final IMidiInput input = midiAccess.createInput (0, null);
        final IMidiOutput ctrlOutput = midiAccess.createOutput (1);
        final IMidiInput ctrlInput = midiAccess.createInput (1, null);
        final ElectraOneControlSurface surface = new ElectraOneControlSurface (this.host, this.colorManager, this.configuration, output, input, ctrlInput, ctrlOutput);
        this.surfaces.add (surface);
        // TODO surface.addTextDisplay (new ElectraOneSegmentDisplay (this.host, output));

        surface.getModeManager ().setDefaultID (Modes.VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final ElectraOneControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.register (Modes.VOLUME, new MixerMode (surface, this.model));
        modeManager.register (Modes.SEND, new SendsMode (surface, this.model));

        // TODO
        // modeManager.register (Modes.DEVICE_PARAMS, new DeviceParamsMode (surface, this.model));
        // modeManager.register (Modes.EQ_DEVICE_PARAMS, new DeviceParamsMode ("Equalizer",
        // this.model.getSpecificDevice (DeviceID.EQ), surface, this.model));
        // modeManager.register (Modes.MARKERS, new MarkerMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final ElectraOneControlSurface surface = this.getSurface ();
        // We need this for triggering updateDispay in the modes
        surface.getViewManager ().register (Views.CONTROL, new DummyView<> ("Control", surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.configuration.registerDeactivatedItemsHandler (this.model);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final ITransport t = this.model.getTransport ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();

        final ElectraOneControlSurface surface = this.getSurface ();

        // Navigation

        // this.addButton (surface, ButtonID.LOOP, "Loop", new ToggleLoopCommand<> (this.model,
        // surface), 0, ElectraOneControlSurface.ELECTRA_ONE_REPEAT, t::isLoop);
        // this.addButton (surface, ButtonID.STOP, "Stop", new StopCommand<> (this.model, surface),
        // 0, ElectraOneControlSurface.ELECTRA_ONE_STOP, () -> !t.isPlaying ());
        // this.addButton (surface, ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface),
        // 0, ElectraOneControlSurface.ELECTRA_ONE_PLAY, t::isPlaying);
        // this.addButton (surface, ButtonID.RECORD, "Record", new ElectraOneRecordCommand
        // (this.model, surface), 0, ElectraOneControlSurface.ELECTRA_ONE_RECORD, () -> {
        // final boolean isOn = this.isRecordShifted (surface) ? t.isLauncherOverdub () :
        // t.isRecording ();
        // return isOn ? ElectraOne_BUTTON_STATE_ON : ElectraOne_BUTTON_STATE_OFF;
        // });
        //
        // this.addButton (surface, ButtonID.MOVE_TRACK_LEFT, "Left", new
        // ElectraOneMoveTrackBankCommand (this.model, surface, true, true), 0,
        // ElectraOneControlSurface.ELECTRA_ONE_TRACK_LEFT);
        // this.addButton (surface, ButtonID.MOVE_TRACK_RIGHT, TAG_RIGHT, new
        // ElectraOneMoveTrackBankCommand (this.model, surface, true, false), 0,
        // ElectraOneControlSurface.ELECTRA_ONE_TRACK_RIGHT);

        for (int i = 0; i < 6; i++)
        {
            final ButtonID row1ButtonID = ButtonID.get (ButtonID.ROW1_1, i);
            final ButtonID row2ButtonID = ButtonID.get (ButtonID.ROW2_1, i);
            final ButtonID row3ButtonID = ButtonID.get (ButtonID.ROW3_1, i);
            final ButtonID row4ButtonID = ButtonID.get (ButtonID.ROW4_1, i);

            final int labelIndex = i + 1;

            this.addButton (surface, row1ButtonID, "Rec Arm " + labelIndex, new ButtonRowModeCommand<> (0, i, this.model, surface), ElectraOneControlSurface.ELECTRA_ONE_ARM1 + i, () -> this.getButtonColor (surface, row1ButtonID));
            this.addButton (surface, row3ButtonID, "Mute " + labelIndex, new ButtonRowModeCommand<> (1, i, this.model, surface), ElectraOneControlSurface.ELECTRA_ONE_MUTE1 + i, () -> this.getButtonColor (surface, row2ButtonID));
            this.addButton (surface, row2ButtonID, "Solo " + labelIndex, new ButtonRowModeCommand<> (2, i, this.model, surface), ElectraOneControlSurface.ELECTRA_ONE_SOLO1 + i, () -> this.getButtonColor (surface, row3ButtonID));
            this.addButton (surface, row4ButtonID, "Select " + labelIndex, new ButtonRowModeCommand<> (3, i, this.model, surface), ElectraOneControlSurface.ELECTRA_ONE_SELECT1 + i, () -> this.getButtonColor (surface, row4ButtonID));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        for (int i = 0; i < 6; i++)
        {
            // Mixer Mode
            final IHwAbsoluteKnob volumeKnob = this.addAbsoluteKnob (ContinuousID.get (ContinuousID.VOLUME_KNOB1, i), "Volume " + i, null, ElectraOneControlSurface.ELECTRA_ONE_VOLUME1 + i);
            volumeKnob.setIndexInGroup (i);
            volumeKnob.disableTakeOver ();
            final IHwAbsoluteKnob panKnob = this.addAbsoluteKnob (ContinuousID.get (ContinuousID.PAN_KNOB1, i), "Pan " + i, null, ElectraOneControlSurface.ELECTRA_ONE_PAN1 + i);
            panKnob.setIndexInGroup (i);
            panKnob.disableTakeOver ();
        }

        for (int i = 0; i < 6; i++)
        {
            // Send Mode
            final IHwAbsoluteKnob send1Knob = this.addAbsoluteKnob (ContinuousID.get (ContinuousID.SEND1_KNOB1, i), "Send 1 " + i, null, BindType.CC, 1, ElectraOneControlSurface.ELECTRA_ONE_SEND1 + i);
            send1Knob.setIndexInGroup (i);
            send1Knob.disableTakeOver ();
            final IHwAbsoluteKnob send2Knob = this.addAbsoluteKnob (ContinuousID.get (ContinuousID.SEND2_KNOB1, i), "Send 2 " + i, null, BindType.CC, 1, ElectraOneControlSurface.ELECTRA_ONE_SEND2 + i);
            send2Knob.setIndexInGroup (i);
            send2Knob.disableTakeOver ();
            // final IHwAbsoluteKnob send3Knob = this.addAbsoluteKnob (ContinuousID.get
            // (ContinuousID.SEND3_KNOB1, i), "Send 3 " + i, null, BindType.CC, 1,
            // ElectraOneControlSurface.ELECTRA_ONE_SEND3 + i);
            // send3Knob.setIndexInGroup (i);
            // send3Knob.disableTakeOver ();
            // final IHwAbsoluteKnob send4Knob = this.addAbsoluteKnob (ContinuousID.get
            // (ContinuousID.SEND4_KNOB1, i), "Send 4 " + i, null, BindType.CC, 1,
            // ElectraOneControlSurface.ELECTRA_ONE_SEND4 + i);
            // send4Knob.setIndexInGroup (i);
            // send4Knob.disableTakeOver ();
            // final IHwAbsoluteKnob send5Knob = this.addAbsoluteKnob (ContinuousID.get
            // (ContinuousID.SEND5_KNOB1, i), "Send 5 " + i, null, BindType.CC, 1,
            // ElectraOneControlSurface.ELECTRA_ONE_SEND5 + i);
            // send5Knob.setIndexInGroup (i);
            // send5Knob.disableTakeOver ();
            // final IHwAbsoluteKnob send6Knob = this.addAbsoluteKnob (ContinuousID.get
            // (ContinuousID.SEND6_KNOB1, i), "Send 6 " + i, null, BindType.CC, 1,
            // ElectraOneControlSurface.ELECTRA_ONE_SEND6 + i);
            // send6Knob.setIndexInGroup (i);
            // send6Knob.disableTakeOver ();
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        // TODO
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final ElectraOneControlSurface surface = this.getSurface ();

        surface.getViewManager ().setActive (Views.CONTROL);
        surface.getModeManager ().setActive (Modes.VOLUME);
    }
}
