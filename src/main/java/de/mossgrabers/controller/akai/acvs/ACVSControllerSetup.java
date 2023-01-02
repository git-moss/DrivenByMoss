// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs;

import de.mossgrabers.controller.akai.acvs.command.trigger.ACVSMasterCommand;
import de.mossgrabers.controller.akai.acvs.controller.ACVSColorManager;
import de.mossgrabers.controller.akai.acvs.controller.ACVSControlSurface;
import de.mossgrabers.controller.akai.acvs.controller.ACVSDisplay;
import de.mossgrabers.controller.akai.acvs.mode.ControlMode;
import de.mossgrabers.controller.akai.acvs.view.ControlView;
import de.mossgrabers.framework.command.continuous.LoopLengthCommand;
import de.mossgrabers.framework.command.continuous.LoopPositionCommand;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.ShiftCommand;
import de.mossgrabers.framework.command.trigger.application.DuplicateCommand;
import de.mossgrabers.framework.command.trigger.application.LoadCommand;
import de.mossgrabers.framework.command.trigger.application.OverdubCommand;
import de.mossgrabers.framework.command.trigger.application.PanelLayoutCommand;
import de.mossgrabers.framework.command.trigger.application.SaveCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.transport.AutomationCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.RestartCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.LaunchQuantization;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.TrackCrossfadeAMode;
import de.mossgrabers.framework.mode.track.TrackCrossfadeBMode;
import de.mossgrabers.framework.mode.track.TrackMuteMode;
import de.mossgrabers.framework.mode.track.TrackRecArmMode;
import de.mossgrabers.framework.mode.track.TrackSoloMode;
import de.mossgrabers.framework.mode.track.TrackStopClipMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;

import java.util.Optional;


/**
 * Support for Akai devices which support the ACVS protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ACVSControllerSetup extends AbstractControllerSetup<ACVSControlSurface, ACVSConfiguration>
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public ACVSControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new ACVSColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new ACVSConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFullFlatTrackList (true);
        ms.setNumSends (4);

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
        this.model.getTrackBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 52, 4, 4);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput (null);
        midiAccess.createInput (1, "Pads (Remote MIDI Port)", "8?????" /* Note off */,
                "9?????" /* Note on */, "A?????" /* Polyphonic Aftertouch */);
        final ACVSControlSurface surface = new ACVSControlSurface (this.host, this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);

        surface.setITextMessageHandler ( (itemID, text) -> {

            if (itemID == ACVSDisplay.ITEM_ID_TEMPO)
            {
                try
                {
                    this.model.getTransport ().setTempo (Double.parseDouble (text));
                }
                catch (final NumberFormatException ex)
                {
                    this.host.error ("Illegal tempo format: " + text);
                }
            }
            else
                surface.errorln ("Unknown text message: " + itemID);

        });
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final ACVSControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.register (Modes.USER, new ControlMode (surface, this.model));

        final ModeManager trackModeManager = surface.getTrackModeManager ();
        trackModeManager.register (Modes.MUTE, new TrackMuteMode<> (surface, this.model));
        trackModeManager.register (Modes.SOLO, new TrackSoloMode<> (surface, this.model));
        trackModeManager.register (Modes.REC_ARM, new TrackRecArmMode<> (surface, this.model));
        trackModeManager.register (Modes.STOP_CLIP, new TrackStopClipMode<> (surface, this.model));
        trackModeManager.register (Modes.CROSSFADE_MODE_A, new TrackCrossfadeAMode<> (surface, this.model));
        trackModeManager.register (Modes.CROSSFADE_MODE_B, new TrackCrossfadeBMode<> (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final ACVSControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.register (Views.CONTROL, new ControlView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final ITrackBank tb = this.model.getTrackBank ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final ACVSControlSurface surface = this.getSurface ();
        final boolean isForce = this.configuration.isActiveACVSDevice (ACVSDevice.FORCE);

        for (int i = 0; i < 8; i++)
        {
            final ButtonID selectID = ButtonID.get (ButtonID.ROW_SELECT_1, i);
            String label = "Track Select " + (i + 1);
            final int index = i;
            this.addReceiveButton (selectID, label, () -> tb.getItem (index).selectOrExpandGroup (), 0, ACVSControlSurface.NOTE_TRACK1_SELECT + i);

            final ButtonID stopID = ButtonID.get (ButtonID.ROW1_1, i);
            label = "Track Stop " + (i + 1);
            this.addReceiveButton (stopID, label, () -> tb.getItem (index).stop (), 0, ACVSControlSurface.NOTE_TRACK1_STOP + i);

            for (int j = 0; j < 8; j++)
            {
                final int pos = i + j * 8;
                final ButtonID padID = ButtonID.get (ButtonID.PAD20, pos);
                label = "Pad " + (pos + 1);
                final int slotIndex = j;
                final int midiControl = ACVSControlSurface.NOTE_CLIP1_LAUNCH + pos;
                this.addReceiveButton (padID, label, () -> {
                    final ISlot slot = tb.getItem (index).getSlotBank ().getItem (slotIndex);
                    if (slot.doesExist ())
                        this.handleClip (surface, slot);
                }, 0, midiControl);
            }

            // Scene launch
            final ButtonID sceneID = ButtonID.get (ButtonID.SCENE1, i);
            label = "Scene " + (i + 1);
            final ISceneBank sceneBank = this.model.getSceneBank ();
            this.addReceiveButton (sceneID, label, () -> this.handleScene (surface, sceneBank.getItem (index)), 0, ACVSControlSurface.NOTE_SCENE1 + i);

            // Track solo
            final ButtonID soloID = ButtonID.get (ButtonID.ROW2_1, i);
            label = "Solo " + (i + 1);
            this.addReceiveButton (soloID, label, () -> tb.getItem (index).toggleSolo (), i + 1, ACVSControlSurface.NOTE_SOLO);

            // Track mute
            final ButtonID muteID = ButtonID.get (ButtonID.ROW3_1, i);
            label = "Mute " + (i + 1);
            this.addReceiveButton (muteID, label, () -> tb.getItem (index).toggleMute (), i + 1, ACVSControlSurface.NOTE_MUTE);

            // NOTE_CUE - not supported

            // Track crossfade mode
            final ButtonID crossfaderID = ButtonID.get (ButtonID.ROW4_1, i);
            label = "Crossfader " + (i + 1);
            this.addReceiveButton (crossfaderID, label, (event, velocity) -> {
                if (event == ButtonEvent.LONG)
                    return;
                int v = 63;
                if (event == ButtonEvent.DOWN)
                {
                    if (velocity == 1)
                        v = 0;
                    else if (velocity == 2)
                        v = 127;
                }
                tb.getItem (index).getCrossfadeParameter ().setValue (v);

            }, i + 1, ACVSControlSurface.NOTE_CROSSFADER);

            // Track Record Arm
            final ButtonID recArmID = ButtonID.get (ButtonID.ROW5_1, i);
            label = "Rec Arm " + (i + 1);
            this.addReceiveButton (recArmID, label, () -> tb.getItem (index).toggleRecArm (), i + 1, ACVSControlSurface.NOTE_REC_ARM);
        }

        this.addReceiveButton (ButtonID.DEVICE_ON_OFF, "Device on/off", cursorDevice::toggleEnabledState, 0x09, ACVSControlSurface.NOTE_TOGGLE_DEVICE, true);
        this.addReceiveButton (ButtonID.DEVICE_LEFT, "Device prev", cursorDevice::selectPrevious, 0x09, ACVSControlSurface.NOTE_PREV_DEVICE);
        this.addReceiveButton (ButtonID.DEVICE_RIGHT, "Device next", cursorDevice::selectNext, 0x09, ACVSControlSurface.NOTE_NEXT_DEVICE);
        this.addReceiveButton (ButtonID.BANK_LEFT, "Bank prev", () -> cursorDevice.getParameterBank ().selectPreviousItem (), 0x09, ACVSControlSurface.NOTE_PREV_BANK);
        this.addReceiveButton (ButtonID.BANK_RIGHT, "Bank next", () -> cursorDevice.getParameterBank ().selectNextItem (), 0x09, ACVSControlSurface.NOTE_NEXT_BANK);

        this.registerMPCTouchDisplayTriggerCommands (surface, cursorDevice, tb);

        if (isForce)
            this.registerForceTriggerCommands (surface, tb);
        else
            this.registerMPCTriggerCommands (surface, cursorDevice, tb);
    }


    /**
     * Select, start, delete or duplicate a clip.
     *
     * @param surface The surface
     * @param slot The slot containing the clip
     */
    protected void handleClip (final ACVSControlSurface surface, final ISlot slot)
    {
        slot.select ();
        if (surface.isShiftPressed () || surface.isSelectPressed ())
            return;
        if (surface.isPressed (ButtonID.F2))
            slot.remove ();
        else if (surface.isPressed (ButtonID.DUPLICATE))
        {
            surface.setTriggerConsumed (ButtonID.DUPLICATE);
            slot.duplicate ();
        }
        else
            slot.launch ();
    }


    /**
     * Select, start, delete or duplicate a scene.
     *
     * @param surface The surface
     * @param scene The scene
     */
    protected void handleScene (final ACVSControlSurface surface, final IScene scene)
    {
        scene.select ();
        if (surface.isShiftPressed () || surface.isSelectPressed ())
            return;
        if (surface.isPressed (ButtonID.F2))
            scene.remove ();
        else if (surface.isPressed (ButtonID.DUPLICATE))
        {
            surface.setTriggerConsumed (ButtonID.DUPLICATE);
            scene.duplicate ();
        }
        else
            scene.launch ();
    }


    /**
     * Register MPC specific trigger commands issued from the touch display
     *
     * @param surface The control surface
     * @param cursorDevice The cursor device
     * @param tb The track bank
     */
    private void registerMPCTouchDisplayTriggerCommands (final ACVSControlSurface surface, final ICursorDevice cursorDevice, final ITrackBank tb)
    {
        final ITransport transport = this.model.getTransport ();
        this.addReceiveButton (ButtonID.METRONOME, "Metronome", transport::toggleMetronome, 0x0A, ACVSControlSurface.NOTE_METRONOME, true);

        // NOTE_MPC_CAPTURE_MIDI - not supported
        // NOTE_MPC_ABLETON_LINK - not supported

        this.addReceiveButton (ButtonID.F4, "Overdub", () -> {
            if (surface.isShiftPressed ())
                transport.toggleLauncherOverdub ();
            else
                transport.toggleOverdub ();
        }, 0x0A, ACVSControlSurface.NOTE_ARRANGE_OVERDUB, true);

        this.addReceiveButton (ButtonID.AUTOMATION_WRITE, "Automation", () -> {
            if (surface.isShiftPressed ())
                transport.toggleWriteClipLauncherAutomation ();
            else
                transport.toggleWriteArrangerAutomation ();
        }, 0x0A, ACVSControlSurface.NOTE_ARRANGER_AUTOMATION_ARM, true);
        this.addReceiveButton (ButtonID.LOOP, "Loop", transport::toggleLoop, 0x0A, ACVSControlSurface.NOTE_LOOP_SWITCH, true);

        this.addReceiveButton (ButtonID.LAUNCH_QUANTIZATION, "Launch Quantize", (event, velocity) -> {
            if (event == ButtonEvent.DOWN || event == ButtonEvent.UP)
                transport.setDefaultLaunchQuantization (convertLaunchQuantization (velocity));
        }, 0x0A, ACVSControlSurface.NOTE_LAUNCH_QUANTIZE);

        this.addReceiveButton (ButtonID.LAYOUT_ARRANGE, "Arrange / Session", (event, velocity) -> {
            if (event == ButtonEvent.DOWN)
                this.model.getApplication ().setPanelLayout (velocity == 0 ? IApplication.PANEL_LAYOUT_ARRANGE : IApplication.PANEL_LAYOUT_MIX);
        }, 0x0A, ACVSControlSurface.NOTE_TOGGLE_ARRANGE_SESSION);
        this.addReceiveButton (ButtonID.FOLLOW, "Follow", this.model.getArranger ()::togglePlaybackFollow, 0x0A, ACVSControlSurface.NOTE_FOLLOW, true);

        // NOTE_MPC_CLIP_DEV_VIEW - from where is this triggered?

        this.addReceiveButton (ButtonID.PIN_DEVICE, "Pin Device", cursorDevice::togglePinned, 0x0A, ACVSControlSurface.NOTE_DEVICE_LOCK, true);

        // NOTE_MPC_DETAILED_VIEW - from where is this triggered?

        this.addReceiveButton (ButtonID.NUDGE_MINUS, "Nudge Down", () -> transport.setTempo (transport.getTempo () - 1), 0x0A, ACVSControlSurface.NOTE_NUDGE_DOWN, false);
        this.addReceiveButton (ButtonID.NUDGE_PLUS, "Nudge Up", () -> transport.setTempo (transport.getTempo () + 1), 0x0A, ACVSControlSurface.NOTE_NUDGE_UP, false);

        this.addReceiveButton (ButtonID.DELETE, "Delete", () -> {
            final Optional<ITrack> selectedTrack = tb.getSelectedItem ();
            if (selectedTrack.isEmpty ())
                return;
            final Optional<ISlot> selectedSlot = selectedTrack.get ().getSlotBank ().getSelectedItem ();
            if (selectedSlot.isPresent ())
                selectedSlot.get ().remove ();
        }, 0x0A, ACVSControlSurface.NOTE_DELETE, true);

        this.addButton (surface, ButtonID.F3, "QUANTIZE INTERVAL", (event, velocity) -> {

            if (event != ButtonEvent.LONG)
            {
                final int quant = (10 - velocity) * 10;
                this.configuration.setQuantizeAmount (quant);
            }

        }, 0x0A, ACVSControlSurface.NOTE_QUANTIZE_INTERVAL, -1, false, null);

        this.addReceiveButton (ButtonID.QUANTIZE, "Quantize", () -> this.model.getCursorClip ().quantize (this.configuration.getQuantizeAmount () / 100.0), 0x0A, ACVSControlSurface.NOTE_QUANTIZE, true);

        // NOTE_MPC_DOUBLE - from where is this triggered?
        // NOTE_MPC_NEW - from where is this triggered?
        // NOTE_MPC_BACK_TO_ARRANGEMENT - from where is this triggered?

        this.addReceiveButton (ButtonID.STOP_ALL_CLIPS, "Stop all clips", tb::stop, 0x0A, ACVSControlSurface.NOTE_STOP_ALL_CLIPS);
        this.addReceiveButton (ButtonID.INSERT_SCENE, "Insert scene", this.model.getProject ()::createScene, 0x0A, ACVSControlSurface.NOTE_INSERT_SCENE);
        final NewCommand<ACVSControlSurface, ACVSConfiguration> newCommand = new NewCommand<> (this.model, surface);
        this.addReceiveButton (ButtonID.NEW, "REC (New)", newCommand::execute, 0x0A, ACVSControlSurface.NOTE_ARRANGE_RECORD, false);

        // NOTE_MPC_TOGGLE_CLIP_SCENE_LAUNCH - from where is this triggered?
    }


    /**
     * Register MPC specific trigger commands issued from buttons
     *
     * @param surface The control surface
     * @param cursorDevice The cursor device
     * @param tb The track bank
     */
    private void registerMPCTriggerCommands (final ACVSControlSurface surface, final ICursorDevice cursorDevice, final ITrackBank tb)
    {
        final ISceneBank sceneBank = this.model.getSceneBank ();
        final ITransport transport = this.model.getTransport ();

        this.addButton (ButtonID.ARROW_DOWN, "Bank A", (event, velocity) -> {
            if (event == ButtonEvent.DOWN)
                sceneBank.selectNextPage ();
        }, 0x0C, ACVSControlSurface.NOTE_MPC_CURSOR_DOWN);
        this.addButton (ButtonID.ARROW_UP, "Bank B", (event, velocity) -> {
            if (event == ButtonEvent.DOWN)
                sceneBank.selectPreviousPage ();
        }, 0x0C, ACVSControlSurface.NOTE_MPC_CURSOR_UP);

        this.addButton (ButtonID.ARROW_LEFT, "Bank C", (event, velocity) -> {
            if (event == ButtonEvent.DOWN)
                tb.selectPreviousPage ();
        }, 0x0C, ACVSControlSurface.NOTE_MPC_CURSOR_LEFT);
        this.addButton (ButtonID.ARROW_RIGHT, "Bank D", (event, velocity) -> {
            if (event == ButtonEvent.DOWN)
                tb.selectNextPage ();
        }, 0x0C, ACVSControlSurface.NOTE_MPC_CURSOR_RIGHT);

        this.addButton (ButtonID.REPEAT, "NOTE REPEAT", new PanelLayoutCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_NOTE_REPEAT, cursorDevice::isWindowOpen);
        this.addButton (ButtonID.AUTOMATION, "FULL LEVEL", new AutomationCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_FULL_LEVEL, () -> {

            if (surface.isShiftPressed ())
                return transport.isWritingClipLauncherAutomation () ? 1 : 0;
            return transport.isWritingArrangerAutomation () ? 2 : 0;

        }, ColorManager.BUTTON_STATE_OFF, ACVSColorManager.BUTTON_UNDO_STATE_ON, ACVSColorManager.BUTTON_UNDO_STATE_HI);

        this.addButton (ButtonID.CLIP, "16 LEVEL", (event, velocity) -> {

            if (event == ButtonEvent.DOWN)
            {
                final boolean launchClips = !this.configuration.isLaunchClips ();
                this.configuration.setLaunchClipsOrScenes (launchClips);
                this.host.showNotification ("Launch " + (launchClips ? "Clips" : "Scenes"));
            }

        }, 0x0C, ACVSControlSurface.NOTE_MPC_16_LEVEL, () -> this.configuration.isLaunchClips () ? 0 : 1, ColorManager.BUTTON_STATE_ON, ACVSColorManager.BUTTON_UNDO_STATE_ON);

        this.addButton (ButtonID.F2, "ERASE", NopCommand.INSTANCE, 0x0C, ACVSControlSurface.NOTE_MPC_ERASE);
        this.addButton (ButtonID.SHIFT, "SHIFT", new ShiftCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_SHIFT);

        // NOTE_MPC_MAIN - blocked by hardware, not sent

        this.addButton (ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_UNDO, () -> {

            if (surface.isShiftPressed ())
            {
                if (!this.model.getApplication ().canRedo ())
                    return 0;
            }
            else
            {
                if (!this.model.getApplication ().canUndo ())
                    return 0;
            }
            return surface.getButton (ButtonID.UNDO).isPressed () ? 2 : 1;

        }, ColorManager.BUTTON_STATE_OFF, ACVSColorManager.BUTTON_UNDO_STATE_ON, ACVSColorManager.BUTTON_UNDO_STATE_HI);

        this.addButton (ButtonID.DUPLICATE, "COPY", new DuplicateCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_COPY);
        this.addButton (ButtonID.TAP_TEMPO, "Tap Tempo", new TapTempoCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_TAP, () -> surface.isPressed (ButtonID.TAP_TEMPO) ? 1 : 0, ACVSColorManager.BUTTON_UNDO_STATE_ON, ACVSColorManager.BUTTON_UNDO_STATE_HI);
        this.addButton (ButtonID.RECORD, "REC", new RecordCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_REC, transport::isRecording);
        this.addButton (ButtonID.OVERDUB, "OVERDUB", new OverdubCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_OVERDUB, () -> surface.isShiftPressed () ? transport.isLauncherOverdub () : transport.isArrangerOverdub ());
        this.addButton (ButtonID.STOP, "STOP", new StopCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_STOP, () -> !transport.isPlaying ());
        this.addButton (ButtonID.PLAY, "PLAY", new PlayCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_PLAY, transport::isPlaying);
        this.addButton (ButtonID.RETURN_TO_ZERO, "PLAY START", new RestartCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_PLAY_START);

        for (int i = 0; i < 64; i++)
        {
            final int trackIndex = i % 8;
            final int clipIndex = i / 8;

            this.addButton (surface, ButtonID.get (ButtonID.MORE_PADS1, i), "PAD " + (i + 1), (event, velocity) -> {

                if (event != ButtonEvent.UP)
                    return;

                if (this.configuration.isLaunchClips ())
                {
                    final ISlot slot = tb.getItem (trackIndex).getSlotBank ().getItem (clipIndex);
                    this.handleClip (surface, slot);
                }
                else
                {
                    if (clipIndex < 2)
                    {
                        final IScene scene = this.model.getSceneBank ().getItem (clipIndex * 4 + trackIndex);
                        this.handleScene (surface, scene);
                    }
                }

            }, 0x0C, ACVSControlSurface.NOTE_MPC_LAUNCH_CLIP_OR_SCENE1 + i, -1, false, null);
        }
    }


    /**
     * Register MPC specific trigger commands issued from buttons
     *
     * @param surface The control surface
     * @param tb The track bank
     */
    private void registerForceTriggerCommands (final ACVSControlSurface surface, final ITrackBank tb)
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        final ITransport transport = this.model.getTransport ();

        final ModeManager trackModeManager = surface.getTrackModeManager ();

        for (int i = 0; i < 8; i++)
        {
            final int trackIndex = i;

            // Select one of the current eight tracks. The selected track will be highlighted white.
            // Sets the Global Launch Quantization to the value shown beneath the corresponding
            // button (None, 8, 4, ...). The current value will be lit white while Shift is held.

            this.addButton (surface, ButtonID.get (ButtonID.TRACK_SELECT_1, i), "TRACK SELECT " + (i + 1), (event, velocity) -> {

                if (event != ButtonEvent.DOWN)
                    return;

                if (surface.isShiftPressed ())
                {
                    final LaunchQuantization launchQuantization = LaunchQuantization.values ()[trackIndex];
                    transport.setDefaultLaunchQuantization (launchQuantization);
                    this.host.showNotification ("Launch Quantization: " + launchQuantization.getName ());
                    return;
                }

                final ITrack track = trackBank.getItem (trackIndex);
                if (surface.isPressed (ButtonID.F2))
                    track.remove ();
                else if (surface.isPressed (ButtonID.DUPLICATE))
                {
                    surface.setTriggerConsumed (ButtonID.DUPLICATE);
                    track.duplicate ();
                }
                else
                    track.selectOrExpandGroup ();

            }, 0x0C, ACVSControlSurface.NOTE_FORCE_TRACK_SELECT1 + i, -1, false, null);

            // Performs the selected action (Mute, Solo, Record Arm or Clip Stop) for the current
            // eight tracks.
            // Quantize: Quantizes the currently selected clip to the grid value set by Force. To
            // set this value, tap the Setting gear icon in the top-right of the display, then use
            // the Quantize To field to set the value.
            // Metronome: Enable or disable Ableton Live's metronome.

            final ButtonRowModeCommand<ACVSControlSurface, ACVSConfiguration> rowModeCommand = new ButtonRowModeCommand<> (trackModeManager, 0, trackIndex, this.model, surface);
            this.addButton (surface, ButtonID.get (ButtonID.TRACK_ASSIGN_1, i), "TRACK ASSIGN " + (i + 1), (event, velocity) -> {

                if (surface.isShiftPressed ())
                {
                    if (event != ButtonEvent.DOWN)
                        return;
                    switch (trackIndex)
                    {
                        case 0:
                            this.model.getCursorClip ().quantize (this.configuration.getQuantizeAmount () / 100.0);
                            break;
                        case 1:
                            new NewCommand<> (this.model, surface).execute ();
                            break;
                        case 2:
                            this.model.getCursorClip ().duplicateContent ();
                            break;
                        case 4:
                            transport.toggleMetronome ();
                            break;
                        default:
                            // Not used
                            break;
                    }
                    return;
                }
                rowModeCommand.execute (event, velocity);

            }, 0x0C, ACVSControlSurface.NOTE_FORCE_TRACK_ASSIGN1 + i, -1, false, null);
        }

        for (int i = 0; i < 64; i++)
        {
            final int trackIndex = i % 8;
            final int clipIndex = i / 8;

            this.addButton (surface, ButtonID.get (ButtonID.MORE_PADS1, i), "PAD " + (i + 1), (event, velocity) -> {

                if (event != ButtonEvent.UP)
                    return;

                if (this.configuration.isLaunchClips ())
                {
                    final ISlot slot = tb.getItem (trackIndex).getSlotBank ().getItem (clipIndex);
                    this.handleClip (surface, slot);
                }
                else
                {
                    if (clipIndex < 2)
                    {
                        final IScene scene = this.model.getSceneBank ().getItem (clipIndex * 4 + trackIndex);
                        this.handleScene (surface, scene);
                    }
                }

            }, 0x0C, ACVSControlSurface.NOTE_FORCE_LAUNCH_CLIP_OR_SCENE1 + i, -1, false, null);
        }

        final ISceneBank sceneBank = this.model.getSceneBank ();
        for (int i = 0; i < 8; i++)
        {
            final int index = i;
            this.addButton (surface, ButtonID.get (ButtonID.ROW6_1, i), "SCENE " + (i + 1), (event, velocity) -> {
                if (event == ButtonEvent.DOWN)
                    this.handleScene (surface, sceneBank.getItem (index));
            }, 0x0C, ACVSControlSurface.NOTE_FORCE_LAUNCH_SCENE1 + i, -1, false, null);
        }

        this.addButton (ButtonID.MASTERTRACK, "MASTER", new ACVSMasterCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_FORCE_MASTER, () -> this.model.getMasterTrack ().isSelected ());

        this.addButton (ButtonID.F5, "STOP ALL CLIPS", (event, velocity) -> {
            if (event == ButtonEvent.DOWN)
                this.model.getTrackBank ().stop ();
        }, 0x0C, ACVSControlSurface.NOTE_FORCE_STOP_ALL_CLIPS);

        // The following 3 buttons do currently nothing but could be used...
        this.addButton (ButtonID.SESSION, "LAUNCH", NopCommand.INSTANCE, 0x0C, ACVSControlSurface.NOTE_FORCE_LAUNCH);
        this.addButton (ButtonID.NOTE, "NOTE", NopCommand.INSTANCE, 0x0C, ACVSControlSurface.NOTE_FORCE_NOTE);
        this.addButton (ButtonID.SEQUENCER, "SEQUENCER", NopCommand.INSTANCE, 0x0C, ACVSControlSurface.NOTE_FORCE_STEP_SEQ);

        this.addButton (ButtonID.SELECT, "SELECT", NopCommand.INSTANCE, 0x0C, ACVSControlSurface.NOTE_FORCE_CLIP_SELECT);

        // Does currently nothing but could be used...
        this.addButton (ButtonID.NOTE_EDITOR, "EDIT", NopCommand.INSTANCE, 0x0C, ACVSControlSurface.NOTE_FORCE_EDIT);

        this.addButton (ButtonID.DUPLICATE, "COPY", new DuplicateCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_FORCE_COPY);
        this.addButton (ButtonID.F2, "ERASE", NopCommand.INSTANCE, 0x0C, ACVSControlSurface.NOTE_FORCE_DELETE);

        // Does currently nothing but could be used...
        this.addButton (ButtonID.REPEAT, "ARP", NopCommand.INSTANCE, 0x0C, ACVSControlSurface.NOTE_FORCE_ARP);

        this.addButton (ButtonID.TAP_TEMPO, "Tap Tempo", new TapTempoCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_FORCE_TAP_TEMPO, () -> surface.isPressed (ButtonID.TAP_TEMPO) ? 1 : 0, ACVSColorManager.BUTTON_UNDO_STATE_ON, ACVSColorManager.BUTTON_UNDO_STATE_HI);

        this.addButton (ButtonID.MUTE, "MUTE", new ModeSelectCommand<> (trackModeManager, this.model, surface, Modes.MUTE), 0x0C, ACVSControlSurface.NOTE_FORCE_MUTE, () -> trackModeManager.isActive (Modes.MUTE));
        this.addButton (ButtonID.SOLO, "SOLO", new ModeSelectCommand<> (trackModeManager, this.model, surface, Modes.SOLO), 0x0C, ACVSControlSurface.NOTE_FORCE_SOLO, () -> trackModeManager.isActive (Modes.SOLO));
        this.addButton (ButtonID.REC_ARM, "REC ARM", new ModeSelectCommand<> (trackModeManager, this.model, surface, Modes.REC_ARM), 0x0C, ACVSControlSurface.NOTE_FORCE_REC_ARM, () -> trackModeManager.isActive (Modes.REC_ARM));
        this.addButton (ButtonID.STOP_CLIP, "STOP CLIP", new ModeSelectCommand<> (trackModeManager, this.model, surface, Modes.STOP_CLIP), 0x0C, ACVSControlSurface.NOTE_FORCE_CLIP_STOP, () -> trackModeManager.isActive (Modes.STOP_CLIP));

        this.addButton (ButtonID.PLAY, "PLAY", new PlayCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_FORCE_PLAY, transport::isPlaying);
        this.addButton (ButtonID.STOP, "STOP", new StopCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_FORCE_STOP, () -> !transport.isPlaying ());
        this.addButton (ButtonID.RECORD, "REC", new RecordCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_FORCE_REC, transport::isRecording);
        this.addButton (ButtonID.UNDO, "UNDO", new UndoCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_FORCE_UNDO, () -> {

            if (surface.isShiftPressed ())
            {
                if (!this.model.getApplication ().canRedo ())
                    return 0;
            }
            else
            {
                if (!this.model.getApplication ().canUndo ())
                    return 0;
            }
            return surface.getButton (ButtonID.UNDO).isPressed () ? 2 : 1;

        }, ColorManager.BUTTON_STATE_OFF, ACVSColorManager.BUTTON_UNDO_STATE_ON, ACVSColorManager.BUTTON_UNDO_STATE_HI);
        this.addButton (ButtonID.LOAD, "LOAD", new LoadCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_FORCE_LOAD);
        this.addButton (ButtonID.SAVE, "SAVE", new SaveCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_FORCE_SAVE);

        // NOTE_FORCE_MATRIX, NOTE_FORCE_CLIP, NOTE_FORCE_MIXER - not sent controlled by hardware

        this.addButton (ButtonID.BROWSE, "NAVIGATE", NopCommand.INSTANCE, 0x0C, ACVSControlSurface.NOTE_FORCE_NAVIGATE);

        this.addButton (ButtonID.SHIFT, "SHIFT", new ShiftCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_FORCE_SHIFT);

        final CursorCommand<ACVSControlSurface, ACVSConfiguration> leftCommand = new CursorCommand<> (Direction.LEFT, this.model, surface, false);
        final CursorCommand<ACVSControlSurface, ACVSConfiguration> rightCommand = new CursorCommand<> (Direction.RIGHT, this.model, surface, false);
        final CursorCommand<ACVSControlSurface, ACVSConfiguration> upCommand = new CursorCommand<> (Direction.UP, this.model, surface, false);
        final CursorCommand<ACVSControlSurface, ACVSConfiguration> downCommand = new CursorCommand<> (Direction.DOWN, this.model, surface, false);

        this.addButton (ButtonID.ARROW_UP, "Up", upCommand, 0x0C, ACVSControlSurface.NOTE_FORCE_CURSOR_UP, () -> upCommand.canScroll () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ACVSColorManager.BUTTON_UNDO_STATE_ON);
        this.addButton (ButtonID.ARROW_DOWN, "Down", downCommand, 0x0C, ACVSControlSurface.NOTE_FORCE_CURSOR_DOWN, () -> downCommand.canScroll () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ACVSColorManager.BUTTON_UNDO_STATE_ON);
        this.addButton (ButtonID.ARROW_LEFT, "Left", leftCommand, 0x0C, ACVSControlSurface.NOTE_FORCE_CURSOR_LEFT, () -> leftCommand.canScroll () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ACVSColorManager.BUTTON_UNDO_STATE_ON);
        this.addButton (ButtonID.ARROW_RIGHT, "Right", rightCommand, 0x0C, ACVSControlSurface.NOTE_FORCE_CURSOR_RIGHT, () -> rightCommand.canScroll () ? 1 : 0, ColorManager.BUTTON_STATE_OFF, ACVSColorManager.BUTTON_UNDO_STATE_ON);

        this.addButton (ButtonID.CROSSFADE_A, "Crossfade A", (event, velocity) -> {

            if (event == ButtonEvent.DOWN)
                trackModeManager.setActive (Modes.CROSSFADE_MODE_A);
            else if (event == ButtonEvent.UP && trackModeManager.isActive (Modes.CROSSFADE_MODE_A))
                trackModeManager.restore ();

        }, 0x0C, ACVSControlSurface.NOTE_FORCE_ASSIGN_A);

        this.addButton (ButtonID.CROSSFADE_B, "Crossfade B", (event, velocity) -> {

            if (event == ButtonEvent.DOWN)
                trackModeManager.setActive (Modes.CROSSFADE_MODE_B);
            else if (event == ButtonEvent.UP && trackModeManager.isActive (Modes.CROSSFADE_MODE_B))
                trackModeManager.restore ();

        }, 0x0C, ACVSControlSurface.NOTE_FORCE_ASSIGN_B);
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        return BindType.NOTE;
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final ITrackBank tb = this.model.getTrackBank ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        final ACVSControlSurface surface = this.getSurface ();

        for (int i = 0; i < 8; i++)
        {
            final int index = i;

            ContinuousID contID = ContinuousID.get (ContinuousID.FADER1, i);
            String label = "Volume " + (i + 1);
            final ITrack track = tb.getItem (index);
            final ISendBank sendBank = track.getSendBank ();
            final IHwFader fader = this.addFader (contID, label, track::setVolume, BindType.CC, i + 1, ACVSControlSurface.CC_VOLUME);
            fader.setIndexInGroup (i);
            track.setVolumeIndication (true);

            contID = ContinuousID.get (ContinuousID.KNOB1, i);
            label = "Pan " + (i + 1);
            IHwAbsoluteKnob knob = this.addAbsoluteKnob (contID, label, track::setPan, BindType.CC, i + 1, ACVSControlSurface.CC_PAN);
            knob.setIndexInGroup (i);
            track.setPanIndication (true);

            contID = ContinuousID.get (ContinuousID.SEND1_KNOB1, i);
            label = "Send 1 - " + (i + 1);
            knob = this.addAbsoluteKnob (contID, label, value -> sendBank.getItem (0).setValue (value), BindType.CC, i + 1, ACVSControlSurface.CC_SEND1_LEVEL);
            knob.setIndexInGroup (i);
            sendBank.getItem (0).setIndication (true);

            contID = ContinuousID.get (ContinuousID.SEND2_KNOB1, i);
            label = "Send 2 - " + (i + 1);
            knob = this.addAbsoluteKnob (contID, label, value -> sendBank.getItem (1).setValue (value), BindType.CC, i + 1, ACVSControlSurface.CC_SEND2_LEVEL);
            knob.setIndexInGroup (i);
            sendBank.getItem (1).setIndication (true);

            contID = ContinuousID.get (ContinuousID.SEND3_KNOB1, i);
            label = "Send 3 - " + (i + 1);
            knob = this.addAbsoluteKnob (contID, label, value -> sendBank.getItem (2).setValue (value), BindType.CC, i + 1, ACVSControlSurface.CC_SEND3_LEVEL);
            knob.setIndexInGroup (i);
            sendBank.getItem (2).setIndication (true);

            contID = ContinuousID.get (ContinuousID.SEND4_KNOB1, i);
            label = "Send 4 - " + (i + 1);
            knob = this.addAbsoluteKnob (contID, label, value -> sendBank.getItem (3).setValue (value), BindType.CC, i + 1, ACVSControlSurface.CC_SEND4_LEVEL);
            knob.setIndexInGroup (i);
            sendBank.getItem (3).setIndication (true);

            contID = ContinuousID.get (ContinuousID.DEVICE_KNOB1, i);
            label = "Device Knob " + (i + 1);
            knob = this.addAbsoluteKnob (contID, label, value -> parameterBank.getItem (index).setValue (value), BindType.CC, 0x09, ACVSControlSurface.CC_PARAM1_VALUE + i);
            knob.setIndexInGroup (i);
            parameterBank.getItem (index).setIndication (true);
        }

        this.addRelativeKnob (ContinuousID.PLAY_POSITION, "Position", new PlayPositionCommand<> (this.model, surface), BindType.CC, 0x0A, ACVSControlSurface.CC_PLAY_POSITION);
        this.addRelativeKnob (ContinuousID.MOVE_LOOP, "Loop Start", new LoopPositionCommand<> (this.model, surface), BindType.CC, 0x0A, ACVSControlSurface.CC_MOVE_LOOP);
        this.addRelativeKnob (ContinuousID.LOOP_LENGTH, "Loop Length", new LoopLengthCommand<> (this.model, surface), BindType.CC, 0x0A, ACVSControlSurface.CC_LOOP_LENGTH);

        final IMidiInput midiInput = surface.getMidiInput ();
        for (int i = 0; i < 8; i++)
        {
            final int index = i;

            final ContinuousID volumeKnobID = ContinuousID.get (ContinuousID.VOLUME_KNOB1, i);
            final IHwRelativeKnob volumeKnob = this.addRelativeKnob (volumeKnobID, "Volume " + (i + 1), value -> tb.getItem (index).changeVolume (value), BindType.CC, 0x0D, i);
            volumeKnob.bindTouch ( (event, velocity) -> {

                if (event == ButtonEvent.LONG)
                    return;
                final IParameter volumeParameter = tb.getItem (index).getVolumeParameter ();
                final boolean isBeingTouched = event == ButtonEvent.DOWN;
                volumeParameter.touchValue (isBeingTouched);
                if (isBeingTouched && surface.isPressed (ButtonID.F2))
                    volumeParameter.resetValue ();

            }, midiInput, BindType.NOTE, 0x0D, i);
            volumeKnob.setIndexInGroup (i);

            final ContinuousID paramKnobID = ContinuousID.get (ContinuousID.PARAM_KNOB1, i);
            final IHwRelativeKnob paramKnob = this.addRelativeKnob (paramKnobID, "Param " + (i + 1), value -> cursorDevice.getParameterBank ().getItem (index).changeValue (value), BindType.CC, 0x0D, 8 + i);
            paramKnob.bindTouch ( (event, velocity) -> {

                if (event == ButtonEvent.LONG)
                    return;
                final IParameter parameter = cursorDevice.getParameterBank ().getItem (index);
                final boolean isBeingTouched = event == ButtonEvent.DOWN;
                parameter.touchValue (isBeingTouched);
                if (isBeingTouched && surface.isPressed (ButtonID.F2))
                    parameter.resetValue ();

            }, midiInput, BindType.NOTE, 0x0D, 8 + i);
            paramKnob.setIndexInGroup (i);
        }

        this.addFader (ContinuousID.CROSSFADER, "Crossfader", null, BindType.CC, 0x0D, 16, false).bind (this.model.getTransport ().getCrossfadeParameter ());
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
    public void startup ()
    {
        final ACVSControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActive (Views.CONTROL);
        surface.getModeManager ().setActive (Modes.USER);
        surface.getTrackModeManager ().setActive (Modes.STOP_CLIP);

        this.host.scheduleTask ( () -> {
            this.host.println ("Disconnected.");
            surface.sendPing ();
        }, 2000);
    }


    /**
     * Convert the index of a ACVS launch quantization to the enumeration constant.
     *
     * @param launchQuantization The index
     * @return The enumeration constant
     */
    private static LaunchQuantization convertLaunchQuantization (final int launchQuantization)
    {
        switch (launchQuantization)
        {
            case 13, 12, 11:
                return LaunchQuantization.RES_1_16;
            case 10, 9:
                return LaunchQuantization.RES_1_8;
            case 8, 7:
                return LaunchQuantization.RES_1_4;
            case 6, 5:
                return LaunchQuantization.RES_1_2;
            case 4:
                return LaunchQuantization.RES_1;
            case 3:
                return LaunchQuantization.RES_2;
            case 2:
                return LaunchQuantization.RES_4;
            case 1:
                return LaunchQuantization.RES_8;
            default:
            case 0:
                return LaunchQuantization.RES_NONE;
        }
    }


    /** Helper for short command code. */
    @FunctionalInterface
    private interface ButtonExec
    {
        public void exec ();
    }


    /**
     * Create a button without LED feedback.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param exec The simplified command to bind
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     */
    private void addReceiveButton (final ButtonID buttonID, final String label, final ButtonExec exec, final int midiChannel, final int midiControl)
    {
        this.addReceiveButton (buttonID, label, exec, midiChannel, midiControl, false);
    }


    /**
     * Create a button without LED feedback.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param exec The simplified command to bind
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     * @param isUpDown Trigger on button up or down
     */
    private void addReceiveButton (final ButtonID buttonID, final String label, final ButtonExec exec, final int midiChannel, final int midiControl, final boolean isUpDown)
    {
        this.addReceiveButton (buttonID, label, (event, velocity) -> {
            if (event == ButtonEvent.DOWN || isUpDown && event == ButtonEvent.UP)
                exec.exec ();
        }, midiChannel, midiControl);
    }


    /**
     * Create a button without LED feedback.
     *
     * @param buttonID The ID of the button (for later access)
     * @param label The label of the button
     * @param command The command to bind
     * @param midiChannel The MIDI channel
     * @param midiControl The MIDI CC or note
     */
    private void addReceiveButton (final ButtonID buttonID, final String label, final TriggerCommand command, final int midiChannel, final int midiControl)
    {
        final ACVSControlSurface surface = this.getSurface ();
        this.addButton (surface, buttonID, label, command, midiChannel, midiControl, -1, false, null);
    }
}
