// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3;

import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.controller.maschine.mikro.mk3.mode.Modes;
import de.mossgrabers.controller.maschine.mikro.mk3.mode.PanMode;
import de.mossgrabers.controller.maschine.mikro.mk3.mode.VolumeMode;
import de.mossgrabers.controller.maschine.mikro.mk3.view.PlayView;
import de.mossgrabers.controller.maschine.mikro.mk3.view.Views;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.KnobRowTouchModeCommand;
import de.mossgrabers.framework.command.trigger.ModeMultiSelectCommand;
import de.mossgrabers.framework.command.trigger.RepeatCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.transport.WriteArrangerAutomationCommand;
import de.mossgrabers.framework.command.trigger.transport.WriteClipLauncherAutomationCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.ViewManager;


/**
 * Support for the NI Maschine Mikro Mk3 controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineMikroMk3ControllerSetup extends AbstractControllerSetup<MaschineMikroMk3ControlSurface, MaschineMikroMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public MaschineMikroMk3ControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);
        this.colorManager = new ColorManager ();
        // TODO
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new MaschineMikroMk3Configuration (this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushSurfaces ();
        this.updateButtons ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 51, 4, 4);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setNumTracks (16);
        ms.setNumDevicesInBank (16);
        ms.setNumScenes (16);
        ms.setNumSends (8);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        // TODO do we need this?
        trackBank.addSelectionObserver (this::handleTrackChange);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Maschine Mikro Mk3");
        final MaschineMikroMk3ControlSurface surface = new MaschineMikroMk3ControlSurface (this.model.getHost (), this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);
        surface.setDisplay (new DummyDisplay (this.host));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();

        // TODO
        surface.getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> this.updateMode (null));
        surface.getModeManager ().addModeListener ( (previousModeId, activeModeId) -> this.updateMode (activeModeId));
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        // TODO
        modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_PAN, new PanMode (surface, this.model));

        // for (int i = 0; i < 8; i++)
        // modeManager.registerMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + i), new
        // SendMode (i, surface, this.model));
        // modeManager.registerMode (Modes.MODE_DEVICE, new DeviceMode (surface, this.model));

        modeManager.setDefaultMode (Modes.MODE_VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        // TODO

        viewManager.registerView (Views.VIEW_PLAY, new PlayView (surface, this.model));
        // viewManager.registerView (Views.VIEW_SHIFT, new ShiftView (surface, this.model));
        // viewManager.registerView (Views.VIEW_BROWSER, new BrowserView (surface, this.model));
        //
        // if (this.host.hasClips ())
        // {
        // viewManager.registerView (Views.VIEW_SESSION, new SessionView (surface, this.model));
        // viewManager.registerView (Views.VIEW_SEQUENCER, new SequencerView (surface, this.model));
        // viewManager.registerView (Views.VIEW_DRUM, new DrumView (surface, this.model));
        // viewManager.registerView (Views.VIEW_RAINDROPS, new RaindropsView (surface, this.model));
        // }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();

        // Transport
        this.addTriggerCommand (Commands.COMMAND_PLAY, MaschineMikroMk3ControlSurface.MIKRO_3_PLAY, new PlayCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_RECORD, MaschineMikroMk3ControlSurface.MIKRO_3_REC, new RecordCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_STOP, MaschineMikroMk3ControlSurface.MIKRO_3_STOP, new StopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_LOOP, MaschineMikroMk3ControlSurface.MIKRO_3_RESTART, new ToggleLoopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_UNDO, MaschineMikroMk3ControlSurface.MIKRO_3_ERASE, new UndoCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_METRONOME, MaschineMikroMk3ControlSurface.MIKRO_3_TAP_METRO, new MetronomeCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_QUANTIZE, MaschineMikroMk3ControlSurface.MIKRO_3_FOLLOW, new QuantizeCommand<> (this.model, surface));

        // Automation
        this.addTriggerCommand (Commands.COMMAND_NEW, MaschineMikroMk3ControlSurface.MIKRO_3_GROUP, new NewCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_AUTOMATION, MaschineMikroMk3ControlSurface.MIKRO_3_AUTO, new WriteClipLauncherAutomationCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_AUTOMATION_WRITE, MaschineMikroMk3ControlSurface.MIKRO_3_LOCK, new WriteArrangerAutomationCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_REPEAT, MaschineMikroMk3ControlSurface.MIKRO_3_NOTE_REPEAT, new RepeatCommand<> (this.model, surface));

        // Encoder Modes
        this.addTriggerCommand (Commands.CONT_COMMAND_KNOB1_TOUCH, MaschineMikroMk3ControlSurface.MIKRO_3_ENCODER_PUSH, new KnobRowTouchModeCommand<> (0, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_VOLUME, MaschineMikroMk3ControlSurface.MIKRO_3_VOLUME, new ModeMultiSelectCommand<> (this.model, surface, Modes.MODE_VOLUME, Modes.MODE_PAN));

        // TODO

    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();
        this.addContinuousCommand (Commands.CONT_COMMAND_KNOB1, MaschineMikroMk3ControlSurface.MIKRO_3_ENCODER, new KnobRowModeCommand<> (0, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final MaschineMikroMk3ControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActiveMode (Modes.MODE_VOLUME);
        surface.getViewManager ().setActiveView (Views.VIEW_PLAY);
    }


    private void updateButtons ()
    {
        // TODO

        final MaschineMikroMk3ControlSurface surface = this.getSurface ();

        final ITransport t = this.model.getTransport ();

        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_PLAY, t.isPlaying () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_REC, t.isRecording () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_STOP, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_RESTART, t.isLoop () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_ERASE, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_TAP_METRO, t.isMetronomeOn () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_FOLLOW, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);

        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_GROUP, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_AUTO, t.isWritingClipLauncherAutomation () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_LOCK, t.isWritingArrangerAutomation () ? MaschineMikroMk3ControlSurface.MIKRO_3_STATE_ON : MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);
        surface.updateButton (MaschineMikroMk3ControlSurface.MIKRO_3_NOTE_REPEAT, MaschineMikroMk3ControlSurface.MIKRO_3_STATE_OFF);

    }


    private void updateMode (final Integer mode)
    {
        this.updateIndication (mode == null ? this.getSurface ().getModeManager ().getActiveOrTempModeId () : mode);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Integer mode)
    {
        if (mode == this.currentMode)
            return;
        this.currentMode = mode;

        // TODO
        // final ITrackBank tb = this.model.getTrackBank ();
        // final ITrackBank tbe = this.model.getEffectTrackBank ();
        // final MaschineMikroMk3ControlSurface surface = this.getSurface ();
        // final ViewManager viewManager = surface.getViewManager ();
        // final boolean isShiftView = viewManager.isActiveView (Views.VIEW_SHIFT);
        // final boolean isSession = viewManager.isActiveView (Views.VIEW_SESSION) || isShiftView;
        // final boolean isEffect = this.model.isEffectTrackBankActive ();
        // final boolean isPan = Modes.MODE_PAN.equals (mode);
        // final boolean isDevice = Modes.MODE_DEVICE.equals (mode);
        //
        // tb.setIndication (!isEffect && isSession);
        // if (tbe != null)
        // tbe.setIndication (isEffect && isSession);
        //
        // final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        // for (int i = 0; i < 8; i++)
        // {
        // final ITrack track = tb.getItem (i);
        // track.setVolumeIndication (!isEffect);
        // track.setPanIndication (!isEffect && isPan);
        // final ISendBank sendBank = track.getSendBank ();
        // for (int j = 0; j < 8; j++)
        // sendBank.getItem (j).setIndication (!isEffect && (Modes.MODE_SEND1.equals (mode) && j ==
        // 0 || Modes.MODE_SEND2.equals (mode) && j == 1 || Modes.MODE_SEND3.equals (mode) && j == 2
        // || Modes.MODE_SEND4.equals (mode) && j == 3 || Modes.MODE_SEND5.equals (mode) && j == 4
        // || Modes.MODE_SEND6.equals (mode) && j == 5 || Modes.MODE_SEND7.equals (mode) && j == 6
        // || Modes.MODE_SEND8.equals (mode) && j == 7));
        //
        // if (tbe != null)
        // {
        // final ITrack fxTrack = tbe.getItem (i);
        // fxTrack.setVolumeIndication (isEffect);
        // fxTrack.setPanIndication (isEffect && isPan);
        // }
        //
        // cursorDevice.getParameterBank ().getItem (i).setIndication (isDevice || isShiftView);
        // }
    }


    /**
     * Handle a track selection change.
     *
     * @param index The index of the track
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final int index, final boolean isSelected)
    {
        if (!isSelected)
            return;

        // TODO

        // final MaschineMikroMk3ControlSurface surface = this.getSurface ();
        // final ViewManager viewManager = surface.getViewManager ();
        // if (viewManager.isActiveView (Views.VIEW_PLAY))
        // viewManager.getActiveView ().updateNoteMapping ();
        //
        // if (viewManager.isActiveView (Views.VIEW_PLAY))
        // viewManager.getActiveView ().updateNoteMapping ();
        //
        // // Reset drum octave because the drum pad bank is also reset
        // this.scales.setDrumOctave (0);
        // if (viewManager.isActiveView (Views.VIEW_DRUM))
        // viewManager.getView (Views.VIEW_DRUM).updateNoteMapping ();
    }
}
