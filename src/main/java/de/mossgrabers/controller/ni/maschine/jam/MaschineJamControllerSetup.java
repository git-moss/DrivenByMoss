// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.core.command.trigger.GroupButtonCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamEncoderCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamMuteCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamPageLeftCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamPageRightCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamSoloCommand;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.MaschineJamTapTempoCommand;
import de.mossgrabers.controller.ni.maschine.jam.controller.FaderConfig;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.controller.ni.maschine.jam.mode.IMaschineJamMode;
import de.mossgrabers.controller.ni.maschine.jam.mode.MaschineJamPanMode;
import de.mossgrabers.controller.ni.maschine.jam.mode.MaschineJamParameterMode;
import de.mossgrabers.controller.ni.maschine.jam.mode.MaschineJamSendMode;
import de.mossgrabers.controller.ni.maschine.jam.mode.MaschineJamTrackMode;
import de.mossgrabers.controller.ni.maschine.jam.mode.MaschineJamVolumeMode;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.ShiftCommand;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.transport.AutomationCommand;
import de.mossgrabers.framework.command.trigger.transport.ConfiguredRecordCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.DefaultValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the NI Maschine controller series.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineJamControllerSetup extends AbstractControllerSetup<MaschineJamControlSurface, MaschineJamConfiguration>
{
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
        this.valueChanger = new DefaultValueChanger (128, 1);
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

        modeManager.setDefaultID (Modes.VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final MaschineJamControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        // viewManager.register (Views.SESSION, new SessionView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        final MaschineJamControlSurface surface = this.getSurface ();

        // TODO
        // surface.getViewManager ().addChangeListener ( (previousViewId, activeViewId) ->
        // this.updateMode (null));
        // surface.getModeManager ().addChangeListener ( (previousModeId, activeModeId) ->
        // this.updateMode (activeModeId));
        //
        // this.configuration.registerDeactivatedItemsHandler (this.model);
        // this.createScaleObservers (this.configuration);
        // this.createNoteRepeatObservers (this.configuration, surface);
        //
        // this.activateBrowserObserver (Modes.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final MaschineJamControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        final ITransport t = this.model.getTransport ();

        this.addButton (ButtonID.SHIFT, "SHIFT", new ShiftCommand<> (this.model, surface), -1);
        this.addButton (ButtonID.SELECT, "SELECT", NopCommand.INSTANCE, MaschineJamControlSurface.SELECT);

        this.addButton (ButtonID.DELETE, "CLEAR", NopCommand.INSTANCE, MaschineJamControlSurface.CLEAR);
        this.addButton (ButtonID.DUPLICATE, "DUPLICATE", NopCommand.INSTANCE, MaschineJamControlSurface.DUPLICATE);

        this.addButton (ButtonID.PLAY, "PLAY", new PlayCommand<> (this.model, surface)
        {
            @Override
            protected void executeShifted ()
            {
                this.transport.restart ();
            }
        }, MaschineJamControlSurface.PLAY, t::isPlaying, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);

        final ConfiguredRecordCommand<MaschineJamControlSurface, MaschineJamConfiguration> recordCommand = new ConfiguredRecordCommand<> (this.model, surface);
        this.addButton (ButtonID.RECORD, "REC", recordCommand, MaschineJamControlSurface.RECORD, recordCommand::isLit);

        this.addButton (ButtonID.PAGE_LEFT, "PAGE LEFT", new MaschineJamPageLeftCommand (this.model, surface), MaschineJamControlSurface.LEFT);
        this.addButton (ButtonID.PAGE_RIGHT, "PAGE RIGHT", new MaschineJamPageRightCommand (this.model, surface), MaschineJamControlSurface.RIGHT);

        this.addButton (ButtonID.TAP_TEMPO, "TEMPO", new MaschineJamTapTempoCommand (this.model, surface), MaschineJamControlSurface.TEMPO);

        this.addButton (ButtonID.GROOVE, "GRID", new QuantizeCommand<> (this.model, surface), MaschineJamControlSurface.GRID);
        this.addButton (ButtonID.SOLO, "SOLO", new MaschineJamSoloCommand (this.model, surface), MaschineJamControlSurface.SOLO);
        this.addButton (ButtonID.MUTE, "MUTE", new MaschineJamMuteCommand (this.model, surface), MaschineJamControlSurface.MUTE);

        this.addButton (ButtonID.TRACK, "MACRO", new ModeSelectCommand<> (this.model, surface, Modes.TRACK), MaschineJamControlSurface.MACRO, () -> modeManager.isActive (Modes.TRACK));
        this.addButton (ButtonID.VOLUME, "LEVEL", new ModeMultiSelectCommand<> (this.model, surface, Modes.VOLUME, Modes.PAN), MaschineJamControlSurface.LEVEL, () -> modeManager.isActive (Modes.VOLUME, Modes.PAN));
        this.addButton (ButtonID.SENDS, "AUX", new ModeMultiSelectCommand<> (this.model, surface, Modes.SEND1, Modes.SEND2, Modes.SEND3, Modes.SEND4, Modes.SEND5, Modes.SEND6, Modes.SEND7, Modes.SEND8), MaschineJamControlSurface.AUX, () -> Modes.isSendMode (modeManager.getActiveID ()));
        this.addButton (ButtonID.DEVICE, "CONTROL", new ModeMultiSelectCommand<> (this.model, surface, Modes.DEVICE_PARAMS, Modes.USER), MaschineJamControlSurface.CONTROL, () -> modeManager.isActive (Modes.DEVICE_PARAMS, Modes.USER));

        final AutomationCommand<MaschineJamControlSurface, MaschineJamConfiguration> automationCommand = new AutomationCommand<> (this.model, surface);
        this.addButton (ButtonID.AUTOMATION, "AUTO", automationCommand, MaschineJamControlSurface.AUTO, automationCommand::isLit);

        for (int i = 0; i < 8; i++)
        {
            final GroupButtonCommand<MaschineJamControlSurface, MaschineJamConfiguration> command = new GroupButtonCommand<> (this.model, surface, i);
            this.addButton (ButtonID.get (ButtonID.ROW_SELECT_1, i), Character.toString ('A' + i), command, MaschineJamControlSurface.GROUP_A + i, command::getButtonColor);
        }
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

        final MaschineJamEncoderCommand encoderCommandMaster = new MaschineJamEncoderCommand (knob, EncoderMode.MASTER_VOLUME, this.model, surface);
        this.addButton (ButtonID.MASTERTRACK, "MST", encoderCommandMaster, MaschineJamControlSurface.MASTER, encoderCommandMaster::isLit);
        final MaschineJamEncoderCommand encoderCommandSelectedTrack = new MaschineJamEncoderCommand (knob, EncoderMode.SELECTED_TRACK_VOLUME, this.model, surface);
        this.addButton (ButtonID.ALT, "GRP", encoderCommandSelectedTrack, MaschineJamControlSurface.GROUP, encoderCommandSelectedTrack::isLit);
        final MaschineJamEncoderCommand encoderCommandMetronome = new MaschineJamEncoderCommand (knob, EncoderMode.METRONOME_VOLUME, this.model, surface);
        this.addButton (ButtonID.METRONOME, "IN 1", encoderCommandMetronome, MaschineJamControlSurface.IN, encoderCommandMetronome::isLit);
        final MaschineJamEncoderCommand encoderCommandCue = new MaschineJamEncoderCommand (knob, EncoderMode.CUE_VOLUME, this.model, surface);
        this.addButton (ButtonID.MIXER, "HEADPHONE", encoderCommandCue, MaschineJamControlSurface.HEADPHONE, encoderCommandCue::isLit);
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final MaschineJamControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (33.25, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD2).setBounds (48.75, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD3).setBounds (64.5, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD4).setBounds (80.0, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD5).setBounds (95.5, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD6).setBounds (110.75, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD7).setBounds (126.75, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD8).setBounds (142.75, 141.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD9).setBounds (33.25, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD10).setBounds (48.75, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD11).setBounds (64.5, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD12).setBounds (80.0, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD13).setBounds (95.5, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD14).setBounds (110.75, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD15).setBounds (126.75, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD16).setBounds (142.75, 130.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD17).setBounds (33.25, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD18).setBounds (48.75, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD19).setBounds (64.5, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD20).setBounds (80.0, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD21).setBounds (95.5, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD22).setBounds (110.75, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD23).setBounds (126.75, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD24).setBounds (142.75, 118.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD25).setBounds (33.25, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD26).setBounds (48.75, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD27).setBounds (64.5, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD28).setBounds (80.0, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD29).setBounds (95.5, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD30).setBounds (110.75, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD31).setBounds (126.75, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD32).setBounds (142.75, 105.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD33).setBounds (33.25, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD34).setBounds (48.75, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD35).setBounds (64.5, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD36).setBounds (80.0, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD37).setBounds (95.5, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD38).setBounds (110.75, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD39).setBounds (126.75, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD40).setBounds (142.75, 93.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD41).setBounds (33.25, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD42).setBounds (48.75, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD43).setBounds (64.5, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD44).setBounds (80.0, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD45).setBounds (95.5, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD46).setBounds (110.75, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD47).setBounds (126.75, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD48).setBounds (142.75, 82.0, 12.75, 10.0);
        surface.getButton (ButtonID.PAD49).setBounds (33.25, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD50).setBounds (48.75, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD51).setBounds (64.5, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD52).setBounds (80.0, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD53).setBounds (95.5, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD54).setBounds (110.75, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD55).setBounds (126.75, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD56).setBounds (142.75, 70.75, 12.75, 10.0);
        surface.getButton (ButtonID.PAD57).setBounds (33.25, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD58).setBounds (48.75, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD59).setBounds (64.5, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD60).setBounds (80.0, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD61).setBounds (95.5, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD62).setBounds (110.75, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD63).setBounds (126.75, 59.25, 12.75, 10.0);
        surface.getButton (ButtonID.PAD64).setBounds (142.75, 59.25, 12.75, 10.0);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final MaschineJamControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActive (Modes.VOLUME);
        // TODO
        // surface.getViewManager ().setActive (Views.PLAY);
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
        final ITrack master = this.model.getMasterTrack ();

        final int vuLeft = this.valueChanger.toMidiValue (master.getVuLeft ());
        final int vuRight = this.valueChanger.toMidiValue (master.getVuRight ());
        midiOutput.sendCC (MaschineJamControlSurface.STRIP_LEFT, vuLeft);
        midiOutput.sendCC (MaschineJamControlSurface.STRIP_RIGHT, vuRight);
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

        final MaschineJamControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        if (viewManager.isActive (Views.PLAY))
            viewManager.getActive ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.resetDrumOctave ();
        if (viewManager.isActive (Views.DRUM))
            viewManager.get (Views.DRUM).updateNoteMapping ();
    }


    /**
     * Update the faders (color, position, etc.).
     */
    private void updateFaders ()
    {
        final MaschineJamControlSurface surface = this.getSurface ();

        final IMode mode = surface.getModeManager ().getActive ();
        if (!(mode instanceof IMaschineJamMode))
            return;
        final IMaschineJamMode maschineMode = (IMaschineJamMode) mode;

        final FaderConfig [] configs = new FaderConfig [8];
        for (int i = 0; i < 8; i++)
            configs[i] = maschineMode.setupFader (i);
        surface.setupFaders (configs);
    }
}
