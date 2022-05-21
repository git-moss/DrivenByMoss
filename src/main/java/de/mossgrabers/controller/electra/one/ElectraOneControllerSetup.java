// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one;

import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;


/**
 * Support for the Electra.One controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ElectraOneControllerSetup extends AbstractControllerSetup<ElectraOneControlSurface, ElectraOneConfiguration>
{
    // TODO
    /** State for button LED on. */
    public static final int ElectraOne_BUTTON_STATE_ON  = 127;
    /** State for button LED off. */
    public static final int ElectraOne_BUTTON_STATE_OFF = 0;

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
        this.valueChanger = new TwosComplementValueChanger (16241 + 1, 10);
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
        final ElectraOneControlSurface surface = new ElectraOneControlSurface (this.surfaces, this.host, this.colorManager, this.configuration, output, input);
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

        // TODO
        // modeManager.register (Modes.TRACK, new TrackMode (surface, this.model));
        // modeManager.register (Modes.VOLUME, new VolumeMode (surface, this.model));
        // modeManager.register (Modes.PAN, new PanMode (surface, this.model));
        // for (int i = 0; i < 8; i++)
        // modeManager.register (Modes.get (Modes.SEND1, i), new SendMode (surface, this.model, i));
        // modeManager.register (Modes.MASTER, new MasterMode (surface, this.model));
        //
        // modeManager.register (Modes.DEVICE_PARAMS, new DeviceParamsMode (surface, this.model));
        // modeManager.register (Modes.EQ_DEVICE_PARAMS, new DeviceParamsMode ("Equalizer",
        // this.model.getSpecificDevice (DeviceID.EQ), surface, this.model));
        // modeManager.register (Modes.INSTRUMENT_DEVICE_PARAMS, new DeviceParamsMode ("First
        // Instrument", this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT), surface,
        // this.model));
        // modeManager.register (Modes.USER, new UserMode (surface, this.model));
        // modeManager.register (Modes.BROWSER, new DeviceBrowserMode (surface, this.model));
        // modeManager.register (Modes.MARKERS, new MarkerMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        // TODO
        // final ElectraOneControlSurface surface = this.getSurface ();
        // surface.getViewManager ().register (Views.CONTROL, new ControlOnlyView<> (surface,
        // this.model));
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
        // surface), 0, ElectraOneControlSurface.ElectraOne_REPEAT, t::isLoop);
        // this.addButton (surface, ButtonID.STOP, "Stop", new StopCommand<> (this.model, surface),
        // 0, ElectraOneControlSurface.ElectraOne_STOP, () -> !t.isPlaying ());
        // this.addButton (surface, ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface),
        // 0, ElectraOneControlSurface.ElectraOne_PLAY, t::isPlaying);
        // this.addButton (surface, ButtonID.RECORD, "Record", new ElectraOneRecordCommand
        // (this.model, surface), 0, ElectraOneControlSurface.ElectraOne_RECORD, () -> {
        // final boolean isOn = this.isRecordShifted (surface) ? t.isLauncherOverdub () :
        // t.isRecording ();
        // return isOn ? ElectraOne_BUTTON_STATE_ON : ElectraOne_BUTTON_STATE_OFF;
        // });
        //
        // this.addButton (surface, ButtonID.MOVE_TRACK_LEFT, "Left", new
        // ElectraOneMoveTrackBankCommand (this.model, surface, true, true), 0,
        // ElectraOneControlSurface.ElectraOne_TRACK_LEFT);
        // this.addButton (surface, ButtonID.MOVE_TRACK_RIGHT, TAG_RIGHT, new
        // ElectraOneMoveTrackBankCommand (this.model, surface, true, false), 0,
        // ElectraOneControlSurface.ElectraOne_TRACK_RIGHT);
        //
        // for (int i = 0; i < 8; i++)
        // {
        // final ButtonID row1ButtonID = ButtonID.get (ButtonID.ROW2_1, i);
        // final ButtonID row2ButtonID = ButtonID.get (ButtonID.ROW3_1, i);
        // final ButtonID row3ButtonID = ButtonID.get (ButtonID.ROW4_1, i);
        // final ButtonID row4ButtonID = ButtonID.get (ButtonID.ROW_SELECT_1, i);
        //
        // final int labelIndex = 8 * (this.numElectraOneDevices - index - 1) + i + 1;
        //
        // this.addButton (surface, row1ButtonID, "Rec Arm " + labelIndex, new
        // ButtonRowModeCommand<> (1, i, this.model, surface),
        // ElectraOneControlSurface.ElectraOne_ARM1 + i, () -> getButtonColor (surface,
        // row1ButtonID));
        // this.addButton (surface, row2ButtonID, "Solo " + labelIndex, new ButtonRowModeCommand<>
        // (2, i, this.model, surface), ElectraOneControlSurface.ElectraOne_SOLO1 + i, () ->
        // getButtonColor (surface, row2ButtonID));
        // this.addButton (surface, row3ButtonID, "Mute " + labelIndex, new ButtonRowModeCommand<>
        // (3, i, this.model, surface), ElectraOneControlSurface.ElectraOne_MUTE1 + i, () ->
        // getButtonColor (surface, row3ButtonID));
        // this.addButton (surface, row4ButtonID, "Select " + labelIndex, new SelectCommand (i,
        // this.model, surface), ElectraOneControlSurface.ElectraOne_SELECT1 + i, () ->
        // getButtonColor (surface, row4ButtonID));
        // }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final ElectraOneControlSurface surface = this.getSurface ();
        final IMidiInput input = surface.getMidiInput ();

        // this.addRelativeKnob (surface, ContinuousID.PLAY_POSITION, "Jog Wheel", new
        // PlayPositionTempoCommand (this.model, surface),
        // ElectraOneControlSurface.ElectraOne_CC_JOG, RelativeEncoding.SIGNED_BIT);
        //
        // final IHwFader master = this.addFader (surface, ContinuousID.FADER_MASTER, "Master",
        // null, 8);
        // master.bindTouch (new FaderTouchCommand (8, this.model, surface), input, BindType.NOTE,
        // 0, ElectraOneControlSurface.ElectraOne_FADER_MASTER);
        // if (this.configuration.hasMotorFaders ())
        // {
        // // Prevent catch up jitter with motor faders
        // master.disableTakeOver ();
        // }
        // new MasterVolumeMode<> (surface, this.model, ContinuousID.FADER_MASTER).onActivate ();
        //
        // for (int i = 0; i < 8; i++)
        // {
        // final IHwRelativeKnob knob = this.addRelativeKnob (surface, ContinuousID.get
        // (ContinuousID.KNOB1, i), "Knob " + i, new KnobRowModeCommand<> (i, this.model, surface),
        // ElectraOneControlSurface.ElectraOne_CC_VPOT1 + i, RelativeEncoding.SIGNED_BIT);
        // // Note: this is pressing the knobs' button not touching it!
        // knob.bindTouch (new ButtonRowModeCommand<> (0, i, this.model, surface), input,
        // BindType.NOTE, 0, ElectraOneControlSurface.ElectraOne_VSELECT1 + i);
        // knob.setIndexInGroup (index * 8 + i);
        //
        // final IHwFader fader = this.addFader (surface, ContinuousID.get (ContinuousID.FADER1, i),
        // "Fader " + (i + 1), null, i);
        // if (this.configuration.hasMotorFaders ())
        // {
        // // Prevent catch up jitter with motor faders
        // fader.disableTakeOver ();
        // }
        //
        // fader.bindTouch (new FaderTouchCommand (i, this.model, surface), input, BindType.NOTE, 0,
        // ElectraOneControlSurface.ElectraOne_FADER_TOUCH1 + i);
        // fader.setIndexInGroup (index * 8 + i);
        // }
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

        // TODO
        // surface.getViewManager ().setActive (Views.CONTROL);
        // surface.getModeManager ().setActive (Modes.PAN);
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

        final ModeManager modeManager = this.getSurface ().getModeManager ();
        if (modeManager.isActive (Modes.MASTER) && !this.model.getMasterTrack ().isSelected ())
        {
            if (Modes.isTrackMode (modeManager.getPreviousID ()))
                modeManager.restore ();
            else
                modeManager.setActive (Modes.TRACK);
        }
    }


    private static int getButtonColor (final ElectraOneControlSurface surface, final ButtonID buttonID)
    {
        final IMode mode = surface.getModeManager ().getActive ();
        return mode == null ? 0 : mode.getButtonColor (buttonID);
    }
}
