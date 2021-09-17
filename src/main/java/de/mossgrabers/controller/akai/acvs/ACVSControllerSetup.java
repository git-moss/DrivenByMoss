// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs;

import de.mossgrabers.controller.akai.acvs.controller.ACVSColorManager;
import de.mossgrabers.controller.akai.acvs.controller.ACVSControlSurface;
import de.mossgrabers.controller.akai.acvs.controller.ACVSDisplay;
import de.mossgrabers.controller.akai.acvs.mode.ControlMode;
import de.mossgrabers.controller.akai.acvs.view.ControlView;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.ShiftCommand;
import de.mossgrabers.framework.command.trigger.application.OverdubCommand;
import de.mossgrabers.framework.command.trigger.application.PanelLayoutCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
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
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;

import java.util.Optional;


/**
 * Support for the Akai MPC controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ACVSControllerSetup extends AbstractControllerSetup<ACVSControlSurface, ACVSConfiguration>
{
    private final ACVSDevice acvsDevice;


    /**
     * Constructor.
     *
     * @param acvsDevice The specific ACVS device
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public ACVSControllerSetup (final ACVSDevice acvsDevice, final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.acvsDevice = acvsDevice;
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

        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
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
        // TODO Midi Input
        final IMidiInput input = midiAccess.createInput ("Pads", "80????" /* Note off */,
                "90????" /* Note on */);
        final ACVSControlSurface surface = new ACVSControlSurface (this.acvsDevice, this.host, this.colorManager, this.configuration, output, input);
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
        final boolean isMPC = surface.getAcvsDevice () != ACVSDevice.FORCE;

        for (int i = 0; i < 8; i++)
        {
            final ButtonID selectID = ButtonID.get (ButtonID.ROW_SELECT_1, i);
            String label = "Track Select " + (i + 1);
            final int index = i;
            this.addReceiveButton (selectID, label, tb.getItem (index)::select, 0, ACVSControlSurface.NOTE_TRACK1_SELECT + i);

            final ButtonID stopID = ButtonID.get (ButtonID.ROW1_1, i);
            label = "Track Stop " + (i + 1);
            this.addReceiveButton (stopID, label, tb.getItem (index)::stop, 0, ACVSControlSurface.NOTE_TRACK1_STOP + i);

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
            this.addReceiveButton (soloID, label, tb.getItem (index)::toggleSolo, i + 1, ACVSControlSurface.NOTE_SOLO);

            // Track mute
            final ButtonID muteID = ButtonID.get (ButtonID.ROW3_1, i);
            label = "Mute " + (i + 1);
            this.addReceiveButton (muteID, label, tb.getItem (index)::toggleMute, i + 1, ACVSControlSurface.NOTE_MUTE);

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
            this.addReceiveButton (recArmID, label, tb.getItem (index)::toggleRecArm, i + 1, ACVSControlSurface.NOTE_REC_ARM);
        }

        this.addReceiveButton (ButtonID.DEVICE_ON_OFF, "Device on/off", cursorDevice::toggleEnabledState, 0x09, ACVSControlSurface.NOTE_TOGGLE_DEVICE, true);
        this.addReceiveButton (ButtonID.DEVICE_LEFT, "Device prev", cursorDevice::selectPrevious, 0x09, ACVSControlSurface.NOTE_PREV_DEVICE);
        this.addReceiveButton (ButtonID.DEVICE_RIGHT, "Device next", cursorDevice::selectNext, 0x09, ACVSControlSurface.NOTE_NEXT_DEVICE);
        this.addReceiveButton (ButtonID.BANK_LEFT, "Bank prev", cursorDevice.getParameterBank ()::selectPreviousItem, 0x09, ACVSControlSurface.NOTE_PREV_BANK);
        this.addReceiveButton (ButtonID.BANK_RIGHT, "Bank next", cursorDevice.getParameterBank ()::selectNextItem, 0x09, ACVSControlSurface.NOTE_NEXT_BANK);

        if (isMPC)
            this.registerMPCTriggerCommands (surface, cursorDevice, tb);
        else
        {
            // TODO Force
        }
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
        if (surface.isShiftPressed ())
            return;
        if (surface.isPressed (ButtonID.F2))
            slot.remove ();
        else if (surface.isPressed (ButtonID.DUPLICATE))
            slot.duplicate ();
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
        if (surface.isShiftPressed ())
            return;
        if (surface.isPressed (ButtonID.F2))
            scene.remove ();
        else if (surface.isPressed (ButtonID.DUPLICATE))
            scene.duplicate ();
        else
            scene.launch ();
    }


    /**
     * Register MPC specific trigger commands.
     *
     * @param surface The control surface
     * @param cursorDevice The cursor device
     * @param tb The track bank
     */
    private void registerMPCTriggerCommands (final ACVSControlSurface surface, final ICursorDevice cursorDevice, final ITrackBank tb)
    {
        ////////////////////////////////////////////////////////////////
        // Commands issued from the touch display
        ////////////////////////////////////////////////////////////////

        final ITransport t = this.model.getTransport ();
        this.addReceiveButton (ButtonID.METRONOME, "Metronome", t::toggleMetronome, 0x0A, ACVSControlSurface.NOTE_MPC_METRONOME, true);

        // NOTE_MPC_CAPTURE_MIDI - not supported
        // NOTE_MPC_ABLETON_LINK - not supported

        this.addReceiveButton (ButtonID.REC_ARM, "Overdub", t::toggleOverdub, 0x0A, ACVSControlSurface.NOTE_MPC_ARRANGE_OVERDUB, true);
        this.addReceiveButton (ButtonID.AUTOMATION_WRITE, "Automation", t::toggleWriteArrangerAutomation, 0x0A, ACVSControlSurface.NOTE_MPC_ARRANGER_AUTOMATION_ARM, true);
        this.addReceiveButton (ButtonID.LOOP, "Loop", t::toggleLoop, 0x0A, ACVSControlSurface.NOTE_MPC_LOOP_SWITCH, true);

        this.addReceiveButton (ButtonID.LAUNCH_QUANTIZATION, "Launch Quantize", (event, velocity) -> {
            if (event == ButtonEvent.DOWN)
                t.setDefaultLaunchQuantization (convertLaunchQuantization (velocity));
        }, 0x0A, ACVSControlSurface.NOTE_MPC_LAUNCH_QUANTIZE);

        this.addReceiveButton (ButtonID.LAYOUT_ARRANGE, "Arrange / Session", (event, velocity) -> {
            if (event == ButtonEvent.DOWN)
                this.model.getApplication ().setPanelLayout (velocity == 0 ? IApplication.PANEL_LAYOUT_ARRANGE : IApplication.PANEL_LAYOUT_MIX);
        }, 0x0A, ACVSControlSurface.NOTE_MPC_TOGGLE_ARRANGE_SESSION);
        this.addReceiveButton (ButtonID.FOLLOW, "Follow", this.model.getArranger ()::togglePlaybackFollow, 0x0A, ACVSControlSurface.NOTE_MPC_FOLLOW, true);

        // NOTE_MPC_CLIP_DEV_VIEW - from where is this triggered?

        this.addReceiveButton (ButtonID.PIN_DEVICE, "Pin Device", cursorDevice::togglePinned, 0x0A, ACVSControlSurface.NOTE_MPC_DEVICE_LOCK, true);

        // NOTE_MPC_DETAILED_VIEW - from where is this triggered?

        this.addReceiveButton (ButtonID.NUDGE_MINUS, "Nudge Down", () -> t.setTempo (t.getTempo () - 1), 0x0A, ACVSControlSurface.NOTE_MPC_NUDGE_DOWN, false);
        this.addReceiveButton (ButtonID.NUDGE_PLUS, "Nudge Up", () -> t.setTempo (t.getTempo () + 1), 0x0A, ACVSControlSurface.NOTE_MPC_NUDGE_UP, false);

        this.addReceiveButton (ButtonID.DELETE, "Delete", () -> {
            final Optional<ITrack> selectedTrack = tb.getSelectedItem ();
            if (selectedTrack.isEmpty ())
                return;
            final Optional<ISlot> selectedSlot = selectedTrack.get ().getSlotBank ().getSelectedItem ();
            if (selectedSlot.isPresent ())
                selectedSlot.get ().remove ();
        }, 0x0A, ACVSControlSurface.NOTE_MPC_DELETE, true);

        this.addButton (surface, ButtonID.F3, "QUANTIZE INTERVAL", (event, velocity) -> {

            if (event != ButtonEvent.LONG)
            {
                final int quant = (10 - velocity) * 10;
                this.configuration.setQuantizeAmount (quant);
            }

        }, 0x0A, ACVSControlSurface.NOTE_MPC_QUANTIZE_INTERVAL, -1, false, null);

        this.addReceiveButton (ButtonID.QUANTIZE, "Quantize", () -> this.model.getCursorClip ().quantize (this.configuration.getQuantizeAmount () / 100.0), 0x0A, ACVSControlSurface.NOTE_MPC_QUANTIZE, true);

        // NOTE_MPC_DOUBLE - from where is this triggered?
        // NOTE_MPC_NEW - from where is this triggered?
        // NOTE_MPC_BACK_TO_ARRANGEMENT - from where is this triggered?

        this.addReceiveButton (ButtonID.STOP_ALL_CLIPS, "Stop all clips", tb::stop, 0x0A, ACVSControlSurface.NOTE_MPC_STOP_ALL_CLIPS);
        this.addReceiveButton (ButtonID.INSERT_SCENE, "Insert scene", this.model.getProject ()::createScene, 0x0A, ACVSControlSurface.NOTE_MPC_INSERT_SCENE);
        this.addReceiveButton (ButtonID.F1, "Record", t::startRecording, 0x0A, ACVSControlSurface.NOTE_MPC_ARRANGE_RECORD);

        // NOTE_MPC_TOGGLE_CLIP_SCENE_LAUNCH - from where is this triggered?

        ////////////////////////////////////////////////////////////////
        // Commands issued from buttons
        ////////////////////////////////////////////////////////////////

        final ISceneBank sceneBank = this.model.getSceneBank ();
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
        this.addButton (ButtonID.AUTOMATION, "Automate", new AutomationCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_FULL_LEVEL, () -> {

            if (surface.isShiftPressed ())
                return t.isWritingClipLauncherAutomation () ? 1 : 0;
            return t.isWritingArrangerAutomation () ? 2 : 0;

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

        this.addButton (ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_UNDO, () -> surface.isPressed (ButtonID.UNDO) ? 1 : 0, ACVSColorManager.BUTTON_UNDO_STATE_ON, ACVSColorManager.BUTTON_UNDO_STATE_HI);
        this.addButton (ButtonID.DUPLICATE, "COPY", NopCommand.INSTANCE, 0x0C, ACVSControlSurface.NOTE_MPC_COPY);
        this.addButton (ButtonID.TAP_TEMPO, "Tap Tempo", new TapTempoCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_TAP, () -> surface.isPressed (ButtonID.TAP_TEMPO) ? 1 : 0, ACVSColorManager.BUTTON_UNDO_STATE_ON, ACVSColorManager.BUTTON_UNDO_STATE_HI);
        this.addButton (ButtonID.RECORD, "REC", new RecordCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_REC, t::isRecording);
        this.addButton (ButtonID.OVERDUB, "OVERDUB", new OverdubCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_OVERDUB, () -> surface.isShiftPressed () ? t.isLauncherOverdub () : t.isArrangerOverdub ());
        this.addButton (ButtonID.STOP, "STOP", new StopCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_STOP, () -> !t.isPlaying ());
        this.addButton (ButtonID.PLAY, "PLAY", new PlayCommand<> (this.model, surface), 0x0C, ACVSControlSurface.NOTE_MPC_PLAY, t::isPlaying);
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

            }, 0x0C, ACVSControlSurface.NOTE_LAUNCH_CLIP_OR_SCENE1 + i, -1, false, null);
        }
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
        final ACVSControlSurface surface = this.getSurface ();

        for (int i = 0; i < 8; i++)
        {
            final int index = i;

            ContinuousID contID = ContinuousID.get (ContinuousID.FADER1, i);
            String label = "Volume " + (i + 1);
            final IHwFader fader = this.addFader (contID, label, value -> tb.getItem (index).setVolume (value), BindType.CC, i + 1, ACVSControlSurface.CC_VOLUME);
            fader.setIndexInGroup (i);

            contID = ContinuousID.get (ContinuousID.KNOB1, i);
            label = "Pan " + (i + 1);
            IHwAbsoluteKnob knob = this.addAbsoluteKnob (contID, label, value -> tb.getItem (index).setPan (value), BindType.CC, i + 1, ACVSControlSurface.CC_PAN);
            knob.setIndexInGroup (i);

            contID = ContinuousID.get (ContinuousID.SEND1_KNOB1, i);
            label = "Send 1 - " + (i + 1);
            knob = this.addAbsoluteKnob (contID, label, value -> tb.getItem (index).getSendBank ().getItem (0).setValue (value), BindType.CC, i + 1, ACVSControlSurface.CC_SEND1_LEVEL);
            knob.setIndexInGroup (i);

            contID = ContinuousID.get (ContinuousID.SEND2_KNOB1, i);
            label = "Send 2 - " + (i + 1);
            knob = this.addAbsoluteKnob (contID, label, value -> tb.getItem (index).getSendBank ().getItem (1).setValue (value), BindType.CC, i + 1, ACVSControlSurface.CC_SEND2_LEVEL);
            knob.setIndexInGroup (i);

            contID = ContinuousID.get (ContinuousID.SEND3_KNOB1, i);
            label = "Send 3 - " + (i + 1);
            knob = this.addAbsoluteKnob (contID, label, value -> tb.getItem (index).getSendBank ().getItem (2).setValue (value), BindType.CC, i + 1, ACVSControlSurface.CC_SEND3_LEVEL);
            knob.setIndexInGroup (i);

            contID = ContinuousID.get (ContinuousID.SEND4_KNOB1, i);
            label = "Send 4 - " + (i + 1);
            knob = this.addAbsoluteKnob (contID, label, value -> tb.getItem (index).getSendBank ().getItem (3).setValue (value), BindType.CC, i + 1, ACVSControlSurface.CC_SEND4_LEVEL);
            knob.setIndexInGroup (i);

            contID = ContinuousID.get (ContinuousID.DEVICE_KNOB1, i);
            label = "Device Knob " + (i + 1);
            knob = this.addAbsoluteKnob (contID, label, value -> cursorDevice.getParameterBank ().getItem (index).setValue (value), BindType.CC, 0x09, ACVSControlSurface.CC_PARAM1_VALUE + i);
            knob.setIndexInGroup (i);
        }

        this.addRelativeKnob (ContinuousID.PLAY_POSITION, "Position", new PlayPositionCommand<> (this.model, surface), BindType.CC, 0x0A, 0);
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

        surface.sendPing ();
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
