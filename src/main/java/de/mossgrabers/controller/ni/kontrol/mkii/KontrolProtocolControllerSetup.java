// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

import de.mossgrabers.controller.ni.kontrol.mkii.command.trigger.KontrolProtocolMuteCommand;
import de.mossgrabers.controller.ni.kontrol.mkii.command.trigger.KontrolProtocolSoloCommand;
import de.mossgrabers.controller.ni.kontrol.mkii.command.trigger.StartClipOrSceneCommand;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocol;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolColorManager;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolControlSurface;
import de.mossgrabers.controller.ni.kontrol.mkii.mode.FakeParamsMode;
import de.mossgrabers.controller.ni.kontrol.mkii.mode.LayerMode;
import de.mossgrabers.controller.ni.kontrol.mkii.mode.MixerMode;
import de.mossgrabers.controller.ni.kontrol.mkii.mode.ParamsMode;
import de.mossgrabers.controller.ni.kontrol.mkii.mode.SendMode;
import de.mossgrabers.controller.ni.kontrol.mkii.view.ControlView;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.ShiftCommand;
import de.mossgrabers.framework.command.trigger.application.RedoCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.command.trigger.clip.StartSceneCommand;
import de.mossgrabers.framework.command.trigger.clip.StopClipCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.command.trigger.track.MuteCommand;
import de.mossgrabers.framework.command.trigger.track.SoloCommand;
import de.mossgrabers.framework.command.trigger.transport.ConfiguredRecordCommand;
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
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IClipLauncherNavigator;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.utils.IntConsumerSupplier;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.view.Views;


/**
 * Setup for the Komplete Kontrol NIHIA protocol.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolControllerSetup extends AbstractControllerSetup<KontrolProtocolControlSurface, KontrolProtocolConfiguration> implements NIHIASysExCallback
{
    private static final Modes []                                                               WORKAROUND_MODES      =
    {
        Modes.VOLUME,
        Modes.SEND,
        Modes.DEVICE_PARAMS
    };

    private static final Modes []                                                               MIX_MODES_WITH_LAYERS =
    {
        Modes.VOLUME,
        Modes.SEND,
        Modes.DEVICE_LAYER
    };

    private static final Modes []                                                               MIX_MODES             =
    {
        Modes.VOLUME,
        Modes.SEND
    };

    private ModeMultiSelectCommand<KontrolProtocolControlSurface, KontrolProtocolConfiguration> switcher;
    private final int                                                                           version;
    private long                                                                                lastTriggerTime       = -1;


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
        this.valueChanger = new TwosComplementValueChanger (1024, 4);
        this.configuration = new KontrolProtocolConfiguration (host, this.valueChanger, factory.getArpeggiatorModes (), version);
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        // Only Mk3 does work on Linux
        if (OperatingSystem.get () == OperatingSystem.LINUX && this.version < KontrolProtocol.VERSION_3)
            throw new FrameworkException ("Komplete Kontrol MkII is not supported on Linux since there is no Native Instruments DAW Integration Host.");

        super.init ();
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        // Do not flush until handshake has finished
        if (this.getSurface ().isConnectedToNIHIA ())
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
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput pianoInput = midiAccess.createInput (1, "Keyboard", "8?????" /* Note off */,
                "9?????" /* Note on */, "A?????" /* Poly-Aftertouch */,
                "B?????" /* Sustain-pedal + Modulation + Strip */, "D?????" /* Channel-Aftertouch */,
                "E?????" /* Pitch-bend */);
        final KontrolProtocolControlSurface surface = new KontrolProtocolControlSurface (this.host, this.colorManager, this.configuration, output, midiAccess.createInput (null), this, this.version);
        this.surfaces.add (surface);

        surface.addPianoKeyboard (49, pianoInput, true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.enableMainDrumDevice (false);
        ms.enableDevice (DeviceID.FIRST_INSTRUMENT);
        ms.enableDevice (DeviceID.NI_KOMPLETE);
        ms.setHasFullFlatTrackList (true);
        ms.setNumDevicesInBank (64);
        ms.setNumDeviceLayers (8);
        ms.setNumDrumPadLayers (8);
        ms.setNumMarkers (0);
        ms.setWantsClipLauncherNavigator (true);
        ms.setCursorLayer (true);
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);

        this.model.getTrackBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final KontrolProtocolControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.register (Views.CONTROL, new ControlView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final KontrolProtocolControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        final List<ContinuousID> mixerControls = ContinuousID.createSequentialList (ContinuousID.KNOB1, 8);
        mixerControls.addAll (ContinuousID.createSequentialList (ContinuousID.FADER1, 8));

        modeManager.register (Modes.VOLUME, new MixerMode (surface, this.model, mixerControls));
        modeManager.register (Modes.SEND, new SendMode (surface, this.model, mixerControls));

        if (this.version < KontrolProtocol.VERSION_3)
            modeManager.register (Modes.DEVICE_PARAMS, new FakeParamsMode (surface, this.model, mixerControls));
        else
        {
            final List<ContinuousID> paramControls = ContinuousID.createSequentialList (ContinuousID.PARAM_KNOB1, 8);
            modeManager.register (Modes.DEVICE_PARAMS, new ParamsMode (surface, this.model, paramControls));
            if (this.host.supports (Capability.HAS_DEVICE_LAYERS))
                modeManager.register (Modes.DEVICE_LAYER, new LayerMode (surface, this.model, mixerControls));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.configuration.addSettingObserver (KontrolProtocolConfiguration.DAW_SWITCH, () -> this.sendDAWInfo ());

        this.configuration.registerDeactivatedItemsHandler (this.model);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final KontrolProtocolControlSurface surface = this.getSurface ();
        final ITransport t = this.model.getTransport ();

        this.switcher = new ModeMultiSelectCommand<> (this.model, surface, this.host.supports (Capability.HAS_DEVICE_LAYERS) ? MIX_MODES_WITH_LAYERS : MIX_MODES);

        this.addButton (ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_PLAY, t::isPlaying);
        this.addButton (ButtonID.NEW, "Restart", new NewCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_RESTART);
        final ConfiguredRecordCommand<KontrolProtocolControlSurface, KontrolProtocolConfiguration> recordCommand = new ConfiguredRecordCommand<> (false, this.model, surface);
        this.addButton (ButtonID.RECORD, "Record", recordCommand, 15, KontrolProtocolControlSurface.CC_RECORD, (BooleanSupplier) recordCommand::isLit);
        final ConfiguredRecordCommand<KontrolProtocolControlSurface, KontrolProtocolConfiguration> shiftedRecordCommand = new ConfiguredRecordCommand<> (true, this.model, surface);
        this.addButton (ButtonID.REC_ARM, "Shift+\nRecord", shiftedRecordCommand, 15, KontrolProtocolControlSurface.CC_COUNT_IN, (BooleanSupplier) shiftedRecordCommand::isLit);
        this.addButton (ButtonID.STOP, "Stop", new StopCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_STOP, () -> !t.isPlaying ());

        this.addButton (ButtonID.LOOP, "Loop", new ToggleLoopCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_LOOP, t::isLoop);
        this.addButton (ButtonID.METRONOME, "Metronome", new MetronomeCommand<> (this.model, surface, false), 15, KontrolProtocolControlSurface.CC_METRO, t::isMetronomeOn);
        this.addButton (ButtonID.TAP_TEMPO, "Tempo", new TapTempoCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_TAP_TEMPO);

        // Note: Since there is no pressed-state with this device, in the simulator-GUI the
        // following buttons are always on
        this.addButton (ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_UNDO, () -> this.model.getApplication ().canUndo ());
        this.addButton (ButtonID.REDO, "Redo", new RedoCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_REDO, () -> this.model.getApplication ().canRedo ());
        this.addButton (ButtonID.QUANTIZE, "Quantize", new QuantizeCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_QUANTIZE, () -> true);
        this.addButton (ButtonID.AUTOMATION, "Automation", new WriteArrangerAutomationCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_AUTOMATION, t::isWritingArrangerAutomation);

        final ModeMultiSelectCommand<KontrolProtocolControlSurface, KontrolProtocolConfiguration> modeSwitchCommand = new ModeMultiSelectCommand<> (this.model, surface, WORKAROUND_MODES);
        this.addButton (ButtonID.DELETE, "Modes", modeSwitchCommand, 15, KontrolProtocolControlSurface.CC_CLEAR, () -> true);

        this.addButton (ButtonID.CLIP, "Start Clip", new StartClipOrSceneCommand (this.model, surface), 15, KontrolProtocolControlSurface.CC_PLAY_SELECTED_CLIP);
        this.addButton (ButtonID.STOP_CLIP, "Stop Clip", new StopClipCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_STOP_CLIP);
        // Not implemented in NIHIA
        this.addButton (ButtonID.SCENE1, "Play Scene", new StartSceneCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_PLAY_SCENE);

        // CC_RECORD_SESSION - Not implemented in NIHIA

        this.addButton (ButtonID.MUTE, "Mute", new MuteCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_SELECTED_TRACK_MUTE, () -> {
            final ITrackBank tb = this.model.getCurrentTrackBank ();
            final Optional<ITrack> selectedTrack = tb.getSelectedItem ();
            return selectedTrack.isPresent () && selectedTrack.get ().isMute () ? 1 : 0;
        });
        this.addButton (ButtonID.SOLO, "Solo", new SoloCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_SELECTED_TRACK_SOLO, () -> {
            final ITrackBank tb = this.model.getCurrentTrackBank ();
            final Optional<ITrack> selectedTrack = tb.getSelectedItem ();
            return selectedTrack.isPresent () && selectedTrack.get ().isSolo () ? 1 : 0;
        });

        this.addButtons (surface, 0, 8, ContinuousID.TRACK_SELECT, "Select", (event, index) -> {
            if (event == ButtonEvent.DOWN)
                this.model.getCurrentTrackBank ().getItem (index).selectOrExpandGroup ();
        }, 15, KontrolProtocolControlSurface.SYSEX_TRACK_SELECTED, index -> this.model.getTrackBank ().getItem (index).isSelected () ? 1 : 0);

        this.addButtons (surface, 0, 8, ContinuousID.TRACK_MUTE, "Mute", new KontrolProtocolMuteCommand (this.model, surface), 15, KontrolProtocolControlSurface.SYSEX_TRACK_MUTE, index -> this.model.getTrackBank ().getItem (index).isMute () ? 1 : 0);
        this.addButtons (surface, 0, 8, ContinuousID.TRACK_SOLO, "Solo", new KontrolProtocolSoloCommand (this.model, surface), 15, KontrolProtocolControlSurface.SYSEX_TRACK_SOLO, index -> this.model.getTrackBank ().getItem (index).isSolo () ? 1 : 0);

        this.addButtons (surface, 0, 8, ContinuousID.TRACK_ARM, "Arm", (event, index) -> {
            if (event == ButtonEvent.DOWN)
                this.model.getTrackBank ().getItem (index).toggleRecArm ();
        }, 15, KontrolProtocolControlSurface.SYSEX_TRACK_RECARM, index -> this.model.getTrackBank ().getItem (index).isRecArm () ? 1 : 0);

        this.addButton (ButtonID.F1, "", NopCommand.INSTANCE, 15, KontrolProtocolControlSurface.CC_SELECTED_TRACK_AVAILABLE);
        this.addButton (ButtonID.F2, "", NopCommand.INSTANCE, 15, KontrolProtocolControlSurface.CC_SELECTED_TRACK_MUTED_BY_SOLO);

        if (this.version >= KontrolProtocol.VERSION_4)
        {
            this.addButton (ButtonID.SHIFT, "Shift", new ShiftCommand<> (this.model, surface), 15, KontrolProtocolControlSurface.CC_SHIFT);
            this.addButton (ButtonID.BROWSE, "Browse", this::handleBrowserButtons, 15, KontrolProtocolControlSurface.CC_NAVIGATE_PRESETS, () -> 3);
        }
    }


    /**
     * Simulate multiple hardware buttons via an absolute control. Each button is matched by a
     * specific value. The first value is startValue, which gets increased by one for the other
     * buttons.
     *
     * @param surface The control surface
     * @param startValue The first matched value
     * @param numberOfValues The number of buttons
     * @param continuousID The continuous ID to assign
     * @param label The label of the button
     * @param supplier Callback for retrieving the state of the light
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param command The command to bind
     * @param colorIds The color IDs to map to the states
     */
    protected void addButtons (final KontrolProtocolControlSurface surface, final int startValue, final int numberOfValues, final ContinuousID continuousID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final IntConsumerSupplier supplier, final String... colorIds)
    {
        this.addAbsoluteKnob (continuousID, label, value -> {

            command.execute (ButtonEvent.DOWN, value);
            command.execute (ButtonEvent.UP, value);

        }, BindType.CC, midiChannel, midiControl);

        for (int i = 0; i < numberOfValues; i++)
        {
            final int index = i;

            final IntSupplier supp = () -> supplier.process (index);
            this.addLight (surface, null, midiChannel, midiControl, supp, colorIds);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final KontrolProtocolControlSurface surface = this.getSurface ();

        this.addFader (ContinuousID.HELLO, "Hello", surface::handshakeSuccess, BindType.CC, 15, KontrolProtocolControlSurface.CC_HELLO);

        this.addButton (surface, ButtonID.BANK_LEFT, "Left", (event, velocity) -> this.moveTrackBank (event, true), 15, KontrolProtocolControlSurface.CC_NAVIGATE_BANKS, 127, () -> this.getKnobValue (KontrolProtocolControlSurface.CC_NAVIGATE_BANKS));
        this.addButton (surface, ButtonID.BANK_RIGHT, "Right", (event, velocity) -> this.moveTrackBank (event, false), 15, KontrolProtocolControlSurface.CC_NAVIGATE_BANKS, 1, () -> this.getKnobValue (KontrolProtocolControlSurface.CC_NAVIGATE_BANKS));
        this.addButton (surface, ButtonID.MOVE_TRACK_LEFT, "Enc Left", (event, velocity) -> this.moveTrack (event, true), 15, KontrolProtocolControlSurface.CC_NAVIGATE_TRACKS, 127, () -> this.getKnobValue (KontrolProtocolControlSurface.CC_NAVIGATE_TRACKS));
        this.addButton (surface, ButtonID.MOVE_TRACK_RIGHT, "Enc Right", (event, velocity) -> this.moveTrack (event, false), 15, KontrolProtocolControlSurface.CC_NAVIGATE_TRACKS, 1, () -> this.getKnobValue (KontrolProtocolControlSurface.CC_NAVIGATE_TRACKS));
        this.addButton (surface, ButtonID.ARROW_UP, "Enc Up", (event, velocity) -> this.moveClips (event, true), 15, KontrolProtocolControlSurface.CC_NAVIGATE_CLIPS, 127, () -> this.getKnobValue (KontrolProtocolControlSurface.CC_NAVIGATE_CLIPS));
        this.addButton (surface, ButtonID.ARROW_DOWN, "Enc Down", (event, velocity) -> this.moveClips (event, false), 15, KontrolProtocolControlSurface.CC_NAVIGATE_CLIPS, 1, () -> this.getKnobValue (KontrolProtocolControlSurface.CC_NAVIGATE_CLIPS));

        this.addRelativeKnob (ContinuousID.MOVE_TRANSPORT, "Move Transport", value -> this.changeTransportPosition (value, 0), BindType.CC, 15, KontrolProtocolControlSurface.CC_NAVIGATE_MOVE_TRANSPORT);
        this.addRelativeKnob (ContinuousID.MOVE_LOOP, "Move Loop", this::changeLoopPosition, BindType.CC, 15, KontrolProtocolControlSurface.CC_NAVIGATE_MOVE_LOOP);

        // Only on S models
        this.addRelativeKnob (ContinuousID.NAVIGATE_VOLUME, "Navigate Volume", value -> this.changeTransportPosition (value, 1), BindType.CC, 15, KontrolProtocolControlSurface.CC_CHANGE_SELECTED_TRACK_VOLUME);
        this.addRelativeKnob (ContinuousID.NAVIGATE_PAN, "Navigate Pan", value -> this.changeTransportPosition (value, 2), BindType.CC, 15, KontrolProtocolControlSurface.CC_CHANGE_SELECTED_TRACK_PAN);

        if (this.version >= KontrolProtocol.VERSION_4)
        {
            this.addButton (surface, ButtonID.LOCK_MODE, "Mode Selection", (event, velocity) -> this.selectMainMode (event, velocity), 15, KontrolProtocolControlSurface.CC_MODE_SELECT);

            for (int i = 0; i < 8; i++)
            {
                final int knobMidi1 = KontrolProtocolControlSurface.CC_PARAM_VALUE_CHANGE + i;
                final IHwRelativeKnob knob1 = this.addRelativeKnob (ContinuousID.get (ContinuousID.PARAM_KNOB1, i), "Param Knob " + (i + 1), null, BindType.CC, 15, knobMidi1);
                knob1.addOutput ( () -> this.getKnobValue (knobMidi1), value -> surface.setTrigger (15, knobMidi1, value));
                knob1.setIndexInGroup (i);
            }
        }

        for (int i = 0; i < 8; i++)
        {
            final int knobMidi1 = KontrolProtocolControlSurface.CC_TRACK_VOLUME + i;
            final IHwRelativeKnob knob1 = this.addRelativeKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1), null, BindType.CC, 15, knobMidi1);
            knob1.addOutput ( () -> this.getKnobValue (knobMidi1), value -> surface.setTrigger (15, knobMidi1, value));
            knob1.setIndexInGroup (i);

            final int knobMidi2 = KontrolProtocolControlSurface.CC_TRACK_PAN + i;
            final IHwRelativeKnob knob2 = this.addRelativeKnob (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), null, BindType.CC, 15, knobMidi2);
            knob2.addOutput ( () -> this.getKnobValue (knobMidi2), value -> surface.setTrigger (15, knobMidi2, value));
            knob2.setIndexInGroup (i);
        }
    }


    private void selectMainMode (final ButtonEvent event, final int value)
    {
        // Only called from > v3

        if (event != ButtonEvent.UP)
            return;

        // Prevent double triggers which occur most of the time
        final long triggerTime = System.currentTimeMillis ();
        if (this.lastTriggerTime == -1)
        {
            // Prevent mode change on startup
            this.lastTriggerTime = triggerTime;
            return;
        }
        if (triggerTime - this.lastTriggerTime < 300)
            return;
        this.lastTriggerTime = triggerTime;

        // Prevent double trigger on button press/release which sends the exact same values
        final ISpecificDevice nksDevice = this.model.getSpecificDevice (DeviceID.NI_KOMPLETE);
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final boolean isNksDeviceSelected = nksDevice.doesExist () && cursorDevice.getPosition () == nksDevice.getPosition ();

        // Note: There is no notification when the special NKS device mode is entered. As a
        // workaround all switches in the same mode (between device and between volume modes)
        // are blocked!

        final ModeManager modeManager = this.getSurface ().getModeManager ();
        final ParamsMode paramsMode = (ParamsMode) modeManager.get (Modes.DEVICE_PARAMS);
        if (value == 0)
        {
            // Switch Volume modes
            if (!isNksDeviceSelected || paramsMode.getPreviouslySelectedDevice () != nksDevice.getPosition () || modeManager.isActive (Modes.DEVICE_PARAMS))
                this.switcher.executeNormal (event);
        }
        else
        {
            // Switch Device modes
            if (!isNksDeviceSelected || modeManager.isActive (Modes.VOLUME, Modes.SEND, Modes.DEVICE_LAYER))
            {
                if (modeManager.isActive (Modes.DEVICE_PARAMS))
                {
                    // Prevent accidental switching to Track controls mode when coming from NKS mode
                    if (paramsMode.isTrackOrProjectMode () || !nksDevice.doesExist () || paramsMode.getPreviouslySelectedDevice () != nksDevice.getPosition ())
                        paramsMode.selectNextMode ();
                }
                else
                {
                    final boolean isLayerModeActive = modeManager.isActive (Modes.DEVICE_LAYER);
                    if (!isLayerModeActive)
                        cursorDevice.selectParent ();

                    modeManager.setActive (Modes.DEVICE_PARAMS);
                    paramsMode.enableLayerSubMode (isLayerModeActive);
                }
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void addButton (final KontrolProtocolControlSurface surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl, final IntSupplier supplier, final String... colorIds)
    {
        if (buttonID == ButtonID.SHIFT)
        {
            super.addButton (surface, buttonID, label, command, midiChannel, midiControl, supplier, colorIds);
            return;
        }

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
        surface.getViewManager ().setActive (Views.CONTROL);
        surface.getModeManager ().setActive (Modes.VOLUME);
        surface.initHandshake ();
    }


    // This is encoder left/right
    private void moveTrack (final ButtonEvent event, final boolean isLeft)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final KontrolProtocolControlSurface surface = this.getSurface ();
        if (surface.getModeManager ().isActive (Modes.VOLUME))
        {
            final IClipLauncherNavigator clipLauncherNavigator = this.model.getClipLauncherNavigator ();
            if (this.configuration.isFlipTrackClipNavigation ())
            {
                if (this.configuration.isFlipClipSceneNavigation ())
                    clipLauncherNavigator.navigateScenes (isLeft);
                else
                    clipLauncherNavigator.navigateClips (isLeft);
            }
            else
                clipLauncherNavigator.navigateTracks (isLeft);
            return;
        }

        final IMode activeMode = surface.getModeManager ().getActive ();
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

        final KontrolProtocolControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        switch (modeManager.getActiveID ())
        {
            case VOLUME:
                final IClipLauncherNavigator clipLauncherNavigator = this.model.getClipLauncherNavigator ();
                if (this.configuration.isFlipTrackClipNavigation ())
                {
                    clipLauncherNavigator.navigateTracks (isLeft);
                    return;
                }

                if (this.configuration.isFlipClipSceneNavigation ())
                    clipLauncherNavigator.navigateScenes (isLeft);
                else
                    clipLauncherNavigator.navigateClips (isLeft);
                break;

            case DEVICE_PARAMS:
                if (modeManager.getActive () instanceof final FakeParamsMode paramMode)
                    paramMode.switchProvider (isLeft);
                else
                    this.model.getClipLauncherNavigator ().navigateTracks (isLeft);
                break;

            default:
                this.moveTrackBank (event, isLeft);
                break;
        }
    }


    private void changeTransportPosition (final int value, final int mode)
    {
        final IBrowser browser = this.model.getBrowser ();
        final boolean increase = mode == 0 ? value == 1 : value <= 63;
        if (this.getSurface ().isShiftPressed ())
        {
            if (browser.isActive ())
            {
                if (increase)
                    browser.selectNextFilterItem (2);
                else
                    browser.selectPreviousFilterItem (2);
            }
            else
            {
                if (increase)
                    this.model.getApplication ().zoomIn ();
                else
                    this.model.getApplication ().zoomOut ();
            }
        }
        else
        {
            if (browser.isActive ())
            {
                if (increase)
                    browser.selectNextResult ();
                else
                    browser.selectPreviousResult ();
            }
            else
                this.model.getTransport ().changePosition (increase, false);
        }
    }


    private void changeLoopPosition (final int value)
    {
        if (this.getSurface ().isShiftPressed ())
            this.model.getTransport ().changeLoopLength (value <= 63, false);
        else
            this.model.getTransport ().changeLoopStart (value <= 63, false);
    }


    // These are the left/right buttons
    private void moveTrackBank (final ButtonEvent event, final boolean isLeft)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final IMode activeMode = this.getSurface ().getModeManager ().getActive ();
        if (activeMode == null)
            return;
        if (isLeft)
            activeMode.selectPreviousItemPage ();
        else
            activeMode.selectNextItemPage ();
    }


    private int getKnobValue (final int continuousMidiControl)
    {
        final IMode mode = this.getSurface ().getModeManager ().getActive ();
        return mode == null ? 0 : Math.max (0, mode.getKnobValue (continuousMidiControl));
    }


    /** {@inheritDoc} */
    @Override
    public void setTempo (final double tempo)
    {
        final ITransport transport = this.model.getTransport ();
        final double minimumTempo = transport.getMinimumTempo ();
        final double maximumTempo = transport.getMaximumTempo ();
        if (tempo < minimumTempo)
            this.getSurface ().sendTempo (minimumTempo, true);
        else if (tempo > maximumTempo)
            this.getSurface ().sendTempo (maximumTempo, true);
        else
            transport.setTempo (tempo);
    }


    /** {@inheritDoc} */
    @Override
    public void sendDAWInfo ()
    {
        // The DAW name trigger the background graphics. "Bitwig" (not "Bitwig Studio") triggers the
        // Bitwig background. An unknown name triggers an unreadable rainbow background, therefore,
        // provide an option for the user to decide
        final int [] version = this.host.getVersion ();
        final KontrolProtocolControlSurface surface = this.getSurface ();
        surface.sendDAWInfo (version[0], version[1], surface.getConfiguration ().getSelectedDaw ());
    }


    /** {@inheritDoc} */
    @Override
    public void selectDevice (final int deviceIndex)
    {
        if (this.getSurface ().getModeManager ().get (Modes.DEVICE_PARAMS) instanceof final ParamsMode mode)
            mode.selectedDeviceHasChanged (deviceIndex);
    }


    private void handleBrowserButtons (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (browser.isActive ())
        {
            if (this.getSurface ().isShiftPressed ())
            {
                browser.stopBrowsing (false);
                return;
            }

            if (velocity == 127)
                browser.selectPreviousResult ();
            else
                browser.selectNextResult ();
        }
        else
        {
            if (this.getSurface ().isShiftPressed ())
            {
                if (velocity == 127)
                    browser.insertBeforeCursorDevice ();
                else
                    browser.insertAfterCursorDevice ();
            }
            else
                browser.replace (this.model.getCursorDevice ());
        }
    }
}
