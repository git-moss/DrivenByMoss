// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mkii;

import de.mossgrabers.controller.kontrol.mkii.command.trigger.KontrolRecordCommand;
import de.mossgrabers.controller.kontrol.mkii.command.trigger.StartClipOrSceneCommand;
import de.mossgrabers.controller.kontrol.mkii.controller.KontrolProtocolColorManager;
import de.mossgrabers.controller.kontrol.mkii.controller.KontrolProtocolControlSurface;
import de.mossgrabers.controller.kontrol.mkii.mode.MixerMode;
import de.mossgrabers.controller.kontrol.mkii.mode.ParamsMode;
import de.mossgrabers.controller.kontrol.mkii.mode.SendMode;
import de.mossgrabers.controller.kontrol.mkii.view.ControlView;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.application.RedoCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.command.trigger.clip.StartSceneCommand;
import de.mossgrabers.framework.command.trigger.clip.StopClipCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.command.trigger.track.MuteCommand;
import de.mossgrabers.framework.command.trigger.track.SoloCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.transport.WriteArrangerAutomationCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.DefaultValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;

import java.util.List;
import java.util.function.IntSupplier;


/**
 * Setup for the Komplete Kontrol NIHIA protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolProtocolControllerSetup extends AbstractControllerSetup<KontrolProtocolControlSurface, KontrolProtocolConfiguration>
{
    private final int version;
    private String    kompleteInstance = "";


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     * @param version The version number of the NIHIA protocol to support
     */
    public KontrolProtocolControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings, final int version)
    {
        super (factory, host, globalSettings, documentSettings);

        this.version = version;
        this.colorManager = new KontrolProtocolColorManager ();
        this.valueChanger = new DefaultValueChanger (1024, 4);
        this.configuration = new KontrolProtocolConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        if (OperatingSystem.get () == OperatingSystem.LINUX)
            throw new FrameworkException ("Komplete Kontrol MkII is not supported on Linux since there is no Native Instruments DAW Integration Host.");

        super.init ();
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        // Do not flush until handshake has finished
        if (!this.getSurface ().isConnectedToNIHIA ())
            return;

        final KontrolProtocolControlSurface surface = this.getSurface ();
        final String kompleteInstanceNew = this.getKompleteInstance ();
        if (!this.kompleteInstance.equals (kompleteInstanceNew))
        {
            this.kompleteInstance = kompleteInstanceNew;
            surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_INSTANCE, 0, 0, kompleteInstanceNew);
        }

        final ITrackBank bank = this.model.getCurrentTrackBank ();

        final boolean hasSolo = this.model.getProject ().hasSolo ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = bank.getItem (i);
            surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_MUTE, track.isMute () ? 1 : 0, i);
            surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_SOLO, track.isSolo () ? 1 : 0, i);
            surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_MUTED_BY_SOLO, !track.isSolo () && hasSolo ? 1 : 0, i);
        }

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();
        surface.sendCommand (KontrolProtocolControlSurface.KONTROL_SELECTED_TRACK_AVAILABLE, selectedTrack != null ? TrackType.toTrackType (selectedTrack.getType ()) : 0);
        surface.sendCommand (KontrolProtocolControlSurface.KONTROL_SELECTED_TRACK_MUTED_BY_SOLO, selectedTrack != null && !selectedTrack.isSolo () && hasSolo ? 1 : 0);

        super.flush ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 0, 128, 128, 1);
        this.scales.setChromatic (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.enableDrumDevice (false);
        ms.enableDevice (DeviceID.NI_KOMPLETE);
        ms.setHasFullFlatTrackList (true);
        ms.setNumFilterColumnEntries (0);
        ms.setNumResults (0);
        ms.setNumDeviceLayers (0);
        ms.setNumDrumPadLayers (0);
        ms.setNumMarkers (0);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput pianoInput = midiAccess.createInput (1, "Keyboard", "80????" /* Note off */,
                "90????" /* Note on */, "B0????" /* Sustainpedal + Modulation + Strip */,
                "D0????" /* Channel Aftertouch */, "E0????" /* Pitchbend */);
        final KontrolProtocolControlSurface surface = new KontrolProtocolControlSurface (this.host, this.colorManager, this.configuration, output, midiAccess.createInput (null), this.version);
        this.surfaces.add (surface);

        surface.addPianoKeyboard (49, pianoInput, true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final KontrolProtocolControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.CONTROL, new ControlView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final KontrolProtocolControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        final List<ContinuousID> controls = ContinuousID.createSequentialList (ContinuousID.KNOB1, 8);
        controls.addAll (ContinuousID.createSequentialList (ContinuousID.FADER1, 8));

        modeManager.registerMode (Modes.VOLUME, new MixerMode (surface, this.model, controls));
        modeManager.registerMode (Modes.SEND, new SendMode (surface, this.model, controls));
        modeManager.registerMode (Modes.DEVICE_PARAMS, new ParamsMode (surface, this.model, controls));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.getSurface ().getModeManager ().addModeListener ( (oldMode, newMode) -> this.updateIndication (newMode));

        this.configuration.registerDeactivatedItemsHandler (this.model);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final KontrolProtocolControlSurface surface = this.getSurface ();
        final ITransport t = this.model.getTransport ();

        this.addButton (ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_PLAY, t::isPlaying);
        this.addButton (ButtonID.NEW, "Shift+\nPlay", new NewCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_RESTART);
        this.addButton (ButtonID.RECORD, "Record", new KontrolRecordCommand (true, this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_RECORD, this.model::hasRecordingState);
        this.addButton (ButtonID.REC_ARM, "Shift+\nRecord", new KontrolRecordCommand (false, this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_COUNT_IN, this.model::hasRecordingState);
        this.addButton (ButtonID.STOP, "Stop", new StopCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_STOP, () -> !t.isPlaying ());

        this.addButton (ButtonID.LOOP, "Loop", new ToggleLoopCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_LOOP, t::isLoop);
        this.addButton (ButtonID.METRONOME, "Metronome", new MetronomeCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_METRO, t::isMetronomeOn);
        this.addButton (ButtonID.TAP_TEMPO, "Tempo", new TapTempoCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_TAP_TEMPO);

        // Note: Since there is no pressed-state with this device, in the sim-GUI the following
        // buttons are always on
        this.addButton (ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_UNDO, () -> true);
        this.addButton (ButtonID.REDO, "Redo", new RedoCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_REDO, () -> true);
        this.addButton (ButtonID.QUANTIZE, "Quantize", new QuantizeCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_QUANTIZE, () -> true);
        this.addButton (ButtonID.AUTOMATION, "Automation", new WriteArrangerAutomationCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_AUTOMATION, t::isWritingArrangerAutomation);

        this.addButton (ButtonID.DELETE, "Modes", new ModeMultiSelectCommand<> (this.model, surface, Modes.VOLUME, Modes.SEND, Modes.DEVICE_PARAMS), 15, KontrolProtocolControlSurface.KONTROL_CLEAR, () -> true);

        this.addButton (ButtonID.CLIP, "Start Clip", new StartClipOrSceneCommand (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_PLAY_SELECTED_CLIP);
        this.addButton (ButtonID.STOP_CLIP, "Stop Clip", new StopClipCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_STOP_CLIP);
        // Not implemented in NIHIA
        this.addButton (ButtonID.SCENE1, "Play Scene", new StartSceneCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_PLAY_SCENE);

        // KONTROL_RECORD_SESSION - Not implemented in NIHIA

        this.addButton (ButtonID.MUTE, "Mute", new MuteCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_SELECTED_TRACK_MUTE, () -> {
            final ITrackBank tb = this.model.getCurrentTrackBank ();
            final ITrack selectedTrack = tb.getSelectedItem ();
            return selectedTrack != null && selectedTrack.isMute () ? 1 : 0;
        });
        this.addButton (ButtonID.SOLO, "Solo", new SoloCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.KONTROL_SELECTED_TRACK_SOLO, () -> {
            final ITrackBank tb = this.model.getCurrentTrackBank ();
            final ITrack selectedTrack = tb.getSelectedItem ();
            return selectedTrack != null && selectedTrack.isSolo () ? 1 : 0;
        });

        this.addButtons (surface, 0, 8, ButtonID.ROW_SELECT_1, "Select", (event, index) -> {
            if (event == ButtonEvent.DOWN)
                this.model.getCurrentTrackBank ().getItem (index).select ();
        }, 15, KontrolProtocolControlSurface.KONTROL_TRACK_SELECTED, index -> this.model.getTrackBank ().getItem (index).isSelected () ? 1 : 0);

        this.addButtons (surface, 0, 8, ButtonID.ROW1_1, "Mute", (event, index) -> {
            if (event == ButtonEvent.DOWN)
                this.model.getTrackBank ().getItem (index).toggleMute ();
        }, 15, KontrolProtocolControlSurface.KONTROL_TRACK_MUTE, index -> this.model.getTrackBank ().getItem (index).isMute () ? 1 : 0);

        this.addButtons (surface, 0, 8, ButtonID.ROW2_1, "Solo", (event, index) -> {
            if (event == ButtonEvent.DOWN)
                this.model.getTrackBank ().getItem (index).toggleSolo ();
        }, 15, KontrolProtocolControlSurface.KONTROL_TRACK_SOLO, index -> this.model.getTrackBank ().getItem (index).isSolo () ? 1 : 0);

        this.addButtons (surface, 0, 8, ButtonID.ROW3_1, "Arm", (event, index) -> {
            if (event == ButtonEvent.DOWN)
                this.model.getTrackBank ().getItem (index).toggleRecArm ();
        }, 15, KontrolProtocolControlSurface.KONTROL_TRACK_RECARM, index -> this.model.getTrackBank ().getItem (index).isRecArm () ? 1 : 0);

        this.addButton (ButtonID.F1, "", NopCommand.INSTANCE, 15, KontrolProtocolControlSurface.KONTROL_SELECTED_TRACK_AVAILABLE);
        this.addButton (ButtonID.F2, "", NopCommand.INSTANCE, 15, KontrolProtocolControlSurface.KONTROL_SELECTED_TRACK_MUTED_BY_SOLO);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final KontrolProtocolControlSurface surface = this.getSurface ();

        this.addFader (ContinuousID.HELLO, "Hello", surface::handshakeSuccess, BindType.CC, 15, KontrolProtocolControlSurface.CMD_HELLO);

        this.addButton (surface, ButtonID.BANK_LEFT, "Left", (event, velocity) -> this.moveTrackBank (event, true), 15, KontrolProtocolControlSurface.KONTROL_NAVIGATE_BANKS, 127, () -> this.getKnobValue (KontrolProtocolControlSurface.KONTROL_NAVIGATE_BANKS));
        this.addButton (surface, ButtonID.BANK_RIGHT, "Right", (event, velocity) -> this.moveTrackBank (event, false), 15, KontrolProtocolControlSurface.KONTROL_NAVIGATE_BANKS, 1, () -> this.getKnobValue (KontrolProtocolControlSurface.KONTROL_NAVIGATE_BANKS));
        this.addButton (surface, ButtonID.MOVE_TRACK_LEFT, "Enc Left", (event, velocity) -> this.moveTrack (event, true), 15, KontrolProtocolControlSurface.KONTROL_NAVIGATE_TRACKS, 127, () -> this.getKnobValue (KontrolProtocolControlSurface.KONTROL_NAVIGATE_TRACKS));
        this.addButton (surface, ButtonID.MOVE_TRACK_RIGHT, "Enc Right", (event, velocity) -> this.moveTrack (event, false), 15, KontrolProtocolControlSurface.KONTROL_NAVIGATE_TRACKS, 1, () -> this.getKnobValue (KontrolProtocolControlSurface.KONTROL_NAVIGATE_TRACKS));
        this.addButton (surface, ButtonID.ARROW_UP, "Enc Up", (event, velocity) -> this.moveClips (event, true), 15, KontrolProtocolControlSurface.KONTROL_NAVIGATE_CLIPS, 127, () -> this.getKnobValue (KontrolProtocolControlSurface.KONTROL_NAVIGATE_CLIPS));
        this.addButton (surface, ButtonID.ARROW_DOWN, "Enc Down", (event, velocity) -> this.moveClips (event, false), 15, KontrolProtocolControlSurface.KONTROL_NAVIGATE_CLIPS, 1, () -> this.getKnobValue (KontrolProtocolControlSurface.KONTROL_NAVIGATE_CLIPS));

        this.addRelativeKnob (ContinuousID.MOVE_TRANSPORT, "Move Transport", value -> this.changeTransportPosition (value, 0), BindType.CC, 15, KontrolProtocolControlSurface.KONTROL_NAVIGATE_MOVE_TRANSPORT);
        this.addRelativeKnob (ContinuousID.MOVE_LOOP, "Move Loop", this::changeLoopPosition, BindType.CC, 15, KontrolProtocolControlSurface.KONTROL_NAVIGATE_MOVE_LOOP);

        // Only on S models
        this.addRelativeKnob (ContinuousID.NAVIGATE_VOLUME, "Navigate Volume", value -> this.changeTransportPosition (value, 1), BindType.CC, 15, KontrolProtocolControlSurface.KONTROL_CHANGE_SELECTED_TRACK_VOLUME);
        this.addRelativeKnob (ContinuousID.NAVIGATE_PAN, "Navigate Pan", value -> this.changeTransportPosition (value, 2), BindType.CC, 15, KontrolProtocolControlSurface.KONTROL_CHANGE_SELECTED_TRACK_PAN);

        for (int i = 0; i < 8; i++)
        {
            final int knobMidi1 = KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME + i;
            final IHwRelativeKnob knob1 = this.addRelativeKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1), null, BindType.CC, 15, knobMidi1);
            knob1.addOutput ( () -> this.getKnobValue (knobMidi1), value -> surface.setTrigger (15, knobMidi1, value));

            final int knobMidi2 = KontrolProtocolControlSurface.KONTROL_TRACK_PAN + i;
            final IHwRelativeKnob knob2 = this.addRelativeKnob (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), null, BindType.CC, 15, KontrolProtocolControlSurface.KONTROL_TRACK_PAN + i);
            knob2.addOutput ( () -> this.getKnobValue (knobMidi2), value -> surface.setTrigger (15, knobMidi2, value));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void addButton (final KontrolProtocolControlSurface surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final IntSupplier supplier, final String... colorIds)
    {
        super.addButton (surface, buttonID, label, (event, velocity) -> {

            // Since there is only a down event from the device, long has no meaning
            if (event == ButtonEvent.LONG)
                return;

            // Add missing UP event
            command.execute (ButtonEvent.DOWN, velocity);
            command.execute (ButtonEvent.UP, velocity);

        }, midiChannel, midiControl, supplier, colorIds);
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final KontrolProtocolControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PLAY).setBounds (20.25, 149.5, 31.75, 22.75);
        surface.getButton (ButtonID.NEW).setBounds (20.25, 179.5, 31.75, 22.75);
        surface.getButton (ButtonID.RECORD).setBounds (63.0, 149.25, 31.75, 22.75);
        surface.getButton (ButtonID.REC_ARM).setBounds (63.0, 179.25, 31.75, 22.75);
        surface.getButton (ButtonID.STOP).setBounds (105.75, 149.5, 31.75, 22.75);
        surface.getButton (ButtonID.LOOP).setBounds (20.25, 120.5, 31.75, 22.75);
        surface.getButton (ButtonID.METRONOME).setBounds (63.0, 120.5, 31.75, 22.75);
        surface.getButton (ButtonID.TAP_TEMPO).setBounds (105.75, 120.5, 31.75, 22.75);
        surface.getButton (ButtonID.UNDO).setBounds (21.0, 43.0, 31.75, 22.75);
        surface.getButton (ButtonID.REDO).setBounds (21.0, 75.5, 31.75, 22.75);
        surface.getButton (ButtonID.QUANTIZE).setBounds (63.75, 43.0, 31.75, 22.75);
        surface.getButton (ButtonID.AUTOMATION).setBounds (106.5, 43.0, 31.75, 22.75);
        surface.getButton (ButtonID.DELETE).setBounds (225.75, 120.5, 31.75, 22.75);
        surface.getButton (ButtonID.MUTE).setBounds (194.0, 43.0, 24.25, 22.75);
        surface.getButton (ButtonID.SOLO).setBounds (226.25, 43.0, 24.25, 22.75);

        surface.getButton (ButtonID.ROW_SELECT_1).setBounds (276.0, 43.0, 39.75, 16.0);
        surface.getButton (ButtonID.ROW_SELECT_2).setBounds (330.5, 43.0, 39.75, 16.0);
        surface.getButton (ButtonID.ROW_SELECT_3).setBounds (385.0, 43.0, 39.75, 16.0);
        surface.getButton (ButtonID.ROW_SELECT_4).setBounds (439.5, 43.0, 39.75, 16.0);
        surface.getButton (ButtonID.ROW_SELECT_5).setBounds (494.0, 43.0, 39.75, 16.0);
        surface.getButton (ButtonID.ROW_SELECT_6).setBounds (548.5, 43.0, 39.75, 16.0);
        surface.getButton (ButtonID.ROW_SELECT_7).setBounds (602.75, 43.0, 39.75, 16.0);
        surface.getButton (ButtonID.ROW_SELECT_8).setBounds (657.25, 43.0, 39.75, 16.0);
        surface.getButton (ButtonID.ROW1_1).setBounds (276.0, 67.5, 39.75, 16.0);
        surface.getButton (ButtonID.ROW1_2).setBounds (330.5, 67.5, 39.75, 16.0);
        surface.getButton (ButtonID.ROW1_3).setBounds (385.0, 67.5, 39.75, 16.0);
        surface.getButton (ButtonID.ROW1_4).setBounds (439.5, 67.5, 39.75, 16.0);
        surface.getButton (ButtonID.ROW1_5).setBounds (494.0, 67.5, 39.75, 16.0);
        surface.getButton (ButtonID.ROW1_6).setBounds (548.5, 67.5, 39.75, 16.0);
        surface.getButton (ButtonID.ROW1_7).setBounds (602.75, 67.5, 39.75, 16.0);
        surface.getButton (ButtonID.ROW1_8).setBounds (657.25, 67.5, 39.75, 16.0);
        surface.getButton (ButtonID.ROW2_1).setBounds (276.0, 92.25, 39.75, 16.0);
        surface.getButton (ButtonID.ROW2_2).setBounds (330.5, 92.25, 39.75, 16.0);
        surface.getButton (ButtonID.ROW2_3).setBounds (385.0, 92.25, 39.75, 16.0);
        surface.getButton (ButtonID.ROW2_4).setBounds (439.5, 92.25, 39.75, 16.0);
        surface.getButton (ButtonID.ROW2_5).setBounds (494.0, 92.25, 39.75, 16.0);
        surface.getButton (ButtonID.ROW2_6).setBounds (548.5, 92.25, 39.75, 16.0);
        surface.getButton (ButtonID.ROW2_7).setBounds (602.75, 92.25, 39.75, 16.0);
        surface.getButton (ButtonID.ROW2_8).setBounds (657.25, 92.25, 39.75, 16.0);
        surface.getButton (ButtonID.ROW3_1).setBounds (276.0, 116.75, 39.75, 16.0);
        surface.getButton (ButtonID.ROW3_2).setBounds (330.5, 116.75, 39.75, 16.0);
        surface.getButton (ButtonID.ROW3_3).setBounds (385.0, 116.75, 39.75, 16.0);
        surface.getButton (ButtonID.ROW3_4).setBounds (439.5, 116.75, 39.75, 16.0);
        surface.getButton (ButtonID.ROW3_5).setBounds (494.0, 116.75, 39.75, 16.0);
        surface.getButton (ButtonID.ROW3_6).setBounds (548.5, 116.75, 39.75, 16.0);
        surface.getButton (ButtonID.ROW3_7).setBounds (602.75, 116.75, 39.75, 16.0);
        surface.getButton (ButtonID.ROW3_8).setBounds (657.25, 116.75, 39.75, 16.0);

        surface.getButton (ButtonID.BANK_LEFT).setBounds (188.5, 78.5, 29.75, 20.5);
        surface.getButton (ButtonID.BANK_RIGHT).setBounds (225.75, 78.5, 29.75, 20.5);
        surface.getButton (ButtonID.MOVE_TRACK_LEFT).setBounds (705.5, 188.5, 29.75, 20.5);
        surface.getButton (ButtonID.MOVE_TRACK_RIGHT).setBounds (751.0, 188.5, 29.75, 20.5);
        surface.getButton (ButtonID.ARROW_UP).setBounds (727.25, 163.25, 29.75, 20.5);
        surface.getButton (ButtonID.ARROW_DOWN).setBounds (727.25, 211.5, 29.75, 20.5);

        surface.getButton (ButtonID.CLIP).setBounds (512.75, 0.75, 31.75, 22.75);
        surface.getButton (ButtonID.STOP_CLIP).setBounds (550.25, 0.75, 31.75, 22.75);
        surface.getButton (ButtonID.SCENE1).setBounds (588.0, 0.75, 31.75, 22.75);

        surface.getButton (ButtonID.F1).setBounds (637.5, 0.75, 31.75, 22.75);
        surface.getButton (ButtonID.F2).setBounds (675.25, 0.75, 31.75, 22.75);

        surface.getContinuous (ContinuousID.MOVE_TRANSPORT).setBounds (713.5, 40.75, 27.75, 29.75);
        surface.getContinuous (ContinuousID.MOVE_LOOP).setBounds (752.25, 40.75, 27.75, 29.75);
        surface.getContinuous (ContinuousID.NAVIGATE_VOLUME).setBounds (713.5, 80.75, 27.75, 29.75);
        surface.getContinuous (ContinuousID.NAVIGATE_PAN).setBounds (752.25, 80.75, 27.75, 29.75);

        surface.getContinuous (ContinuousID.KNOB1).setBounds (284.0, 143.25, 28.0, 29.25);
        surface.getContinuous (ContinuousID.FADER1).setBounds (284.0, 178.5, 28.0, 29.25);
        surface.getContinuous (ContinuousID.KNOB2).setBounds (338.25, 143.25, 28.0, 29.25);
        surface.getContinuous (ContinuousID.FADER2).setBounds (338.25, 178.5, 28.0, 29.25);
        surface.getContinuous (ContinuousID.KNOB3).setBounds (392.5, 143.25, 28.0, 29.25);
        surface.getContinuous (ContinuousID.FADER3).setBounds (392.75, 178.5, 28.0, 29.25);
        surface.getContinuous (ContinuousID.KNOB4).setBounds (446.75, 143.25, 28.0, 29.25);
        surface.getContinuous (ContinuousID.FADER4).setBounds (447.0, 178.5, 28.0, 29.25);
        surface.getContinuous (ContinuousID.KNOB5).setBounds (501.25, 143.25, 28.0, 29.25);
        surface.getContinuous (ContinuousID.FADER5).setBounds (501.25, 178.5, 28.0, 29.25);
        surface.getContinuous (ContinuousID.KNOB6).setBounds (555.5, 143.25, 28.0, 29.25);
        surface.getContinuous (ContinuousID.FADER6).setBounds (555.75, 178.5, 28.0, 29.25);
        surface.getContinuous (ContinuousID.KNOB7).setBounds (609.75, 143.25, 28.0, 29.25);
        surface.getContinuous (ContinuousID.FADER7).setBounds (610.0, 178.5, 28.0, 29.25);
        surface.getContinuous (ContinuousID.KNOB8).setBounds (664.25, 143.25, 28.0, 29.25);
        surface.getContinuous (ContinuousID.FADER8).setBounds (664.25, 178.5, 28.0, 29.25);

        surface.getContinuous (ContinuousID.MODULATION_WHEEL).setBounds (100.0, 222.75, 22.75, 67.5);
        surface.getContinuous (ContinuousID.PITCHBEND_WHEEL).setBounds (65.5, 222.75, 22.75, 67.5);

        surface.getPianoKeyboard ().setBounds (162.75, 218.5, 531.5, 79.75);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final KontrolProtocolControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActiveView (Views.CONTROL);
        surface.getModeManager ().setActiveMode (Modes.VOLUME);
        surface.initHandshake ();
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Modes mode)
    {
        if (this.currentMode != null && this.currentMode == mode)
            return;

        if (mode != null)
            this.currentMode = mode;

        final ITrackBank tb = this.model.getTrackBank ();
        final boolean isVolume = Modes.VOLUME == this.currentMode;
        final boolean isSend = Modes.SEND == this.currentMode;
        final boolean isDevice = Modes.isDeviceMode (this.currentMode) || Modes.isLayerMode (this.currentMode);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final ITrack selectedTrack = tb.getSelectedItem ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        for (int i = 0; i < tb.getPageSize (); i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i;

            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (isVolume);
            track.setPanIndication (isVolume);

            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < sendBank.getPageSize (); j++)
                sendBank.getItem (j).setIndication (isSend && hasTrackSel);

            parameterBank.getItem (i).setIndication (isDevice);
        }
    }


    /**
     * Get the name of an Komplete Kontrol instance on the current track.
     *
     * @return The instance name, which is the actual label of the first parameter (e.g. NIKB01). An
     *         empty string if none is present
     */
    private String getKompleteInstance ()
    {
        final ISpecificDevice kkDevice = this.model.getSpecificDevice (DeviceID.NI_KOMPLETE);
        return kkDevice.doesExist () ? kkDevice.getID () : "";
    }


    /**
     * Navigate to the previous or next scene (if any).
     *
     * @param isLeft Select the previous scene if true
     */
    private void navigateScenes (final boolean isLeft)
    {
        final ISceneBank sceneBank = this.model.getSceneBank ();
        if (sceneBank == null)
            return;
        if (isLeft)
            sceneBank.selectPreviousItem ();
        else
            sceneBank.selectNextItem ();
    }


    /**
     * Navigate to the previous or next clip of the selected track (if any).
     *
     * @param isLeft Select the previous clip if true
     */
    private void navigateClips (final boolean isLeft)
    {
        final ITrack selectedTrack = this.model.getSelectedTrack ();
        if (selectedTrack == null)
            return;
        if (isLeft)
            selectedTrack.getSlotBank ().selectPreviousItem ();
        else
            selectedTrack.getSlotBank ().selectNextItem ();
    }


    /**
     * Navigate to the previous or next track (if any).
     *
     * @param isLeft Select the previous track if true
     */
    private void navigateTracks (final boolean isLeft)
    {
        if (isLeft)
            this.model.getTrackBank ().selectPreviousItem ();
        else
            this.model.getTrackBank ().selectNextItem ();
    }


    private void changeTransportPosition (final int value, final int mode)
    {
        final boolean increase = mode == 0 ? value == 1 : value <= 63;
        this.model.getTransport ().changePosition (increase, false);
    }


    private void changeLoopPosition (final int value)
    {
        // Changing of loop position is not possible. Therefore, change position fine grained
        this.model.getTransport ().changePosition (value <= 63, true);
    }


    // These are the left/right buttons
    private void moveTrackBank (final ButtonEvent event, final boolean isLeft)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final Mode activeMode = this.getSurface ().getModeManager ().getActiveOrTempMode ();
        if (activeMode == null)
            return;
        if (isLeft)
            activeMode.selectPreviousItemPage ();
        else
            activeMode.selectNextItemPage ();
    }


    // This is encoder left/right
    private void moveTrack (final ButtonEvent event, final boolean isLeft)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.getSurface ().getModeManager ().isActiveMode (Modes.VOLUME))
        {
            if (this.configuration.isFlipTrackClipNavigation ())
            {
                if (this.configuration.isFlipClipSceneNavigation ())
                    this.navigateScenes (isLeft);
                else
                    this.navigateClips (isLeft);
            }
            else
                this.navigateTracks (isLeft);
            return;
        }

        final Mode activeMode = this.getSurface ().getModeManager ().getActiveOrTempMode ();
        if (activeMode == null)
            return;
        if (isLeft)
            activeMode.selectPreviousItem ();
        else
            activeMode.selectNextItem ();
    }


    // This is encoder up/down
    private void moveClips (final ButtonEvent event, final boolean isLeft)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.getSurface ().getModeManager ().isActiveMode (Modes.VOLUME))
        {
            if (this.configuration.isFlipTrackClipNavigation ())
            {
                this.navigateTracks (isLeft);
                return;
            }

            if (this.configuration.isFlipClipSceneNavigation ())
                this.navigateScenes (isLeft);
            else
                this.navigateClips (isLeft);
            return;
        }

        this.moveTrackBank (event, isLeft);
    }


    private int getKnobValue (final int continuousMidiControl)
    {
        final Mode mode = this.getSurface ().getModeManager ().getActiveOrTempMode ();
        return mode == null ? 0 : mode.getKnobValue (continuousMidiControl);
    }
}
