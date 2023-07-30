// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui;

import de.mossgrabers.controller.mackie.hui.command.trigger.AssignableCommand;
import de.mossgrabers.controller.mackie.hui.command.trigger.FaderTouchCommand;
import de.mossgrabers.controller.mackie.hui.command.trigger.WorkaroundFader;
import de.mossgrabers.controller.mackie.hui.command.trigger.WorkaroundMasterFader;
import de.mossgrabers.controller.mackie.hui.command.trigger.ZoomAndKeysCursorCommand;
import de.mossgrabers.controller.mackie.hui.command.trigger.ZoomCommand;
import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.controller.mackie.hui.controller.HUIDisplay;
import de.mossgrabers.controller.mackie.hui.controller.HUISegmentDisplay;
import de.mossgrabers.controller.mackie.hui.mode.track.AbstractTrackMode;
import de.mossgrabers.controller.mackie.hui.mode.track.PanMode;
import de.mossgrabers.controller.mackie.hui.mode.track.SendMode;
import de.mossgrabers.controller.mackie.hui.mode.track.VolumeMode;
import de.mossgrabers.framework.command.continuous.JogWheelCommand;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.ShiftCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand;
import de.mossgrabers.framework.command.trigger.application.PanelLayoutCommand;
import de.mossgrabers.framework.command.trigger.application.SaveCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.track.MuteCommand;
import de.mossgrabers.framework.command.trigger.track.RecArmAllCommand;
import de.mossgrabers.framework.command.trigger.track.RecArmCommand;
import de.mossgrabers.framework.command.trigger.track.SelectCommand;
import de.mossgrabers.framework.command.trigger.track.SoloCommand;
import de.mossgrabers.framework.command.trigger.transport.AutomationModeCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.PunchInCommand;
import de.mossgrabers.framework.command.trigger.transport.PunchOutCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ControlOnlyView;
import de.mossgrabers.framework.view.Views;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;


/**
 * Support for the Mackie HUI protocol.
 *
 * @author Jürgen Moßgraber
 */
public class HUIControllerSetup extends AbstractControllerSetup<HUIControlSurface, HUIConfiguration>
{
    /** State for button LED on. */
    public static final int HUI_BUTTON_STATE_ON  = 127;
    /** State for button LED off. */
    public static final int HUI_BUTTON_STATE_OFF = 0;

    private final int       numHUIDevices;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     * @param numHUIDevices The number of HUI devices (main device + extenders) to support
     */
    public HUIControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings, final int numHUIDevices)
    {
        super (factory, host, globalSettings, documentSettings);

        this.numHUIDevices = numHUIDevices;

        this.colorManager = new ColorManager ();
        this.colorManager.registerColor (0, ColorEx.BLACK);
        this.colorManager.registerColor (127, ColorEx.RED);
        this.colorManager.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, 0);
        this.colorManager.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON, 127);

        this.valueChanger = new TwosComplementValueChanger (16384, 100);
        this.configuration = new HUIConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        this.surfaces.forEach (surface -> {
            this.updateMode (surface, surface.getModeManager ().getActiveID ());
            this.updateSegmentDisplay (surface);
        });

        this.updateVUandFaders ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.enableMainDrumDevice (false);
        ms.setHasFullFlatTrackList (true);
        ms.setNumTracks (8 * this.numHUIDevices);
        ms.setNumSends (5);
        ms.setNumParamPages (0);
        ms.setNumParams (0);
        ms.setNumScenes (0);
        ms.setNumFilterColumnEntries (8);
        ms.setNumResults (8);
        ms.setNumDeviceLayers (0);
        ms.setNumDrumPadLayers (0);
        // This is required to make the new clip function work!
        ms.setNumScenes (8);
        ms.setNumMarkers (8 * this.numHUIDevices);
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);

        this.model.getTrackBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();

        for (int i = 0; i < this.numHUIDevices; i++)
        {
            final IMidiOutput output = midiAccess.createOutput (i);
            final IMidiInput input = midiAccess.createInput (i, null);
            final HUIControlSurface surface = new HUIControlSurface (this.surfaces, this.host, this.colorManager, this.configuration, output, input, this.model, 8 * i);
            this.surfaces.add (surface);
            surface.addTextDisplay (new HUIDisplay (this.host, output));
            surface.addTextDisplay (new HUISegmentDisplay (this.host, output));
            surface.getModeManager ().setDefaultID (Modes.VOLUME);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        for (int index = 0; index < this.numHUIDevices; index++)
        {
            final HUIControlSurface surface = this.getSurface (index);
            final ModeManager modeManager = surface.getModeManager ();
            modeManager.register (Modes.VOLUME, new VolumeMode (surface, this.model));
            modeManager.register (Modes.PAN, new PanMode (surface, this.model));
            for (int i = 0; i < 5; i++)
                modeManager.register (Modes.get (Modes.SEND1, i), new SendMode (i, surface, this.model));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        for (int index = 0; index < this.numHUIDevices; index++)
        {
            final HUIControlSurface surface = this.getSurface (index);
            surface.getViewManager ().register (Views.CONTROL, new ControlOnlyView<> (surface, this.model));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        for (int i = 0; i < this.numHUIDevices; i++)
        {
            // Connect all modes
            final HUIControlSurface surface = this.getSurface (i);
            final ModeManager mm = surface.getModeManager ();
            for (int j = 0; j < this.numHUIDevices; j++)
            {
                if (i != j)
                    this.getSurface (j).getModeManager ().addConnectedManagerListener (mm);
            }

            mm.addChangeListener ( (oldMode, newMode) -> this.updateMode (surface, newMode));

            this.configuration.addSettingObserver (AbstractConfiguration.ENABLE_VU_METERS, () -> {
                final IMode activeMode = surface.getModeManager ().getActive ();
                if (activeMode != null)
                    activeMode.updateDisplay ();
                ((HUIDisplay) surface.getDisplay ()).forceFlush ();
            });
        }

        this.configuration.registerDeactivatedItemsHandler (this.model);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        for (int index = 0; index < this.numHUIDevices; index++)
        {
            final HUIControlSurface surface = this.getSurface (index);

            final ModeManager modeManager = surface.getModeManager ();
            final ITransport t = this.model.getTransport ();
            final IApplication application = this.model.getApplication ();

            // Channel commands
            for (int channel = 0; channel < 8; channel++)
            {
                final int channelIdx = index * 8 + channel;

                this.addButtonHUI (surface, ButtonID.get (ButtonID.FADER_TOUCH_1, channel), "Fader " + (channel + 1), new FaderTouchCommand (channelIdx, this.model, surface), HUIControlSurface.HUI_FADER1 + channel * 8);
                this.addButtonHUI (surface, ButtonID.get (ButtonID.ROW1_1, channel), "VSelect " + (channel + 1), new ButtonRowModeCommand<> (0, channel, this.model, surface), HUIControlSurface.HUI_VSELECT1 + channel * 8);

                final ButtonID selectButtonID = ButtonID.get (ButtonID.ROW_SELECT_1, channel);
                this.addButtonHUI (surface, selectButtonID, "Select " + (channel + 1), new SelectCommand<> (channelIdx, this.model, surface), HUIControlSurface.HUI_SELECT1 + channel * 8, () -> this.getButtonColor (surface, selectButtonID));

                final ButtonID muteButtonID = ButtonID.get (ButtonID.ROW4_1, channel);
                this.addButtonHUI (surface, muteButtonID, "Mute " + (channel + 1), new MuteCommand<> (channelIdx, this.model, surface), HUIControlSurface.HUI_MUTE1 + channel * 8, () -> this.getButtonColor (surface, muteButtonID));

                final ButtonID soloButtonID = ButtonID.get (ButtonID.ROW3_1, channel);
                this.addButtonHUI (surface, soloButtonID, "Solo " + (channel + 1), new SoloCommand<> (channelIdx, this.model, surface), HUIControlSurface.HUI_SOLO1 + channel * 8, () -> this.getButtonColor (surface, soloButtonID));

                // HUI_INSERT1 is used on icon for selection
                final ButtonID insertButtonID = ButtonID.get (ButtonID.ROW5_1, channel);
                this.addButtonHUI (surface, insertButtonID, "Insert " + (channel + 1), new SelectCommand<> (channelIdx, this.model, surface), HUIControlSurface.HUI_INSERT1 + channel * 8, () -> this.getButtonColor (surface, insertButtonID));

                final ButtonID recArmButtonID = ButtonID.get (ButtonID.ROW2_1, channel);
                this.addButtonHUI (surface, recArmButtonID, "Arm " + (channel + 1), new RecArmCommand<> (channelIdx, this.model, surface), HUIControlSurface.HUI_ARM1 + channel * 8, () -> this.getButtonColor (surface, recArmButtonID));

                // HUI_AUTO1, not supported
            }

            // Key commands
            this.addButtonHUI (surface, ButtonID.CONTROL, "Control", NopCommand.INSTANCE, HUIControlSurface.HUI_KEY_CTRL_CLT);
            this.addButtonHUI (surface, ButtonID.SHIFT, "Shift", new ShiftCommand<> (this.model, surface), HUIControlSurface.HUI_KEY_SHIFT_AD);
            // HUI_KEY_EDITMODE, not supported
            this.addButtonHUI (surface, ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), HUIControlSurface.HUI_KEY_UNDO);
            this.addButtonHUI (surface, ButtonID.ALT, "Alt", NopCommand.INSTANCE, HUIControlSurface.HUI_KEY_ALT_FINE);
            this.addButtonHUI (surface, ButtonID.SELECT, "Select", NopCommand.INSTANCE, HUIControlSurface.HUI_KEY_OPTION_A);
            // HUI_KEY_EDITTOOL, not supported
            this.addButtonHUI (surface, ButtonID.SAVE, "Save", new SaveCommand<> (this.model, surface), HUIControlSurface.HUI_KEY_SAVE);

            // Window commands
            this.addButtonHUI (surface, ButtonID.MIXER, "Mixer", new PaneCommand<> (PaneCommand.Panels.MIXER, this.model, surface), HUIControlSurface.HUI_WINDOW_MIX);
            this.addButtonHUI (surface, ButtonID.NOTE_EDITOR, "Note", new PaneCommand<> (PaneCommand.Panels.NOTE, this.model, surface), HUIControlSurface.HUI_WINDOW_EDIT);
            this.addButtonHUI (surface, ButtonID.AUTOMATION_EDITOR, "Automation", new PaneCommand<> (PaneCommand.Panels.AUTOMATION, this.model, surface), HUIControlSurface.HUI_WINDOW_TRANSPRT);
            // HUI_WINDOW_MEM_LOC, not supported
            this.addButtonHUI (surface, ButtonID.TOGGLE_DEVICE, "Device", new PanelLayoutCommand<> (this.model, surface), HUIControlSurface.HUI_WINDOW_STATUS);
            // HUI_WINDOW_ALT, not supported

            // Bank navigation
            this.addButtonHUI (surface, ButtonID.MOVE_TRACK_LEFT, "Channel Left", new ModeCursorCommand<> (Direction.LEFT, this.model, surface), HUIControlSurface.HUI_CHANL_LEFT);
            this.addButtonHUI (surface, ButtonID.MOVE_BANK_LEFT, "Bank Left", new ModeCursorCommand<> (Direction.DOWN, this.model, surface), HUIControlSurface.HUI_BANK_LEFT);
            this.addButtonHUI (surface, ButtonID.MOVE_TRACK_RIGHT, "Channel Right", new ModeCursorCommand<> (Direction.RIGHT, this.model, surface), HUIControlSurface.HUI_CHANL_RIGHT);
            this.addButtonHUI (surface, ButtonID.MOVE_BANK_RIGHT, "Bank Right", new ModeCursorCommand<> (Direction.UP, this.model, surface), HUIControlSurface.HUI_BANK_RIGHT);

            // Assignment (mode selection)
            // HUI_ASSIGN1_OUTPUT, not supported
            // HUI_ASSIGN1_INPUT, not supported
            this.addButtonHUI (surface, ButtonID.PAN_SEND, "Panorama", new ModeSelectCommand<> (this.model, surface, Modes.PAN), HUIControlSurface.HUI_ASSIGN1_PAN, () -> modeManager.isActive (Modes.PAN));
            this.addButtonHUI (surface, ButtonID.SEND1, "Send 1", new ModeSelectCommand<> (this.model, surface, Modes.SEND1), HUIControlSurface.HUI_ASSIGN1_SEND_A, () -> modeManager.isActive (Modes.SEND1));
            this.addButtonHUI (surface, ButtonID.SEND2, "Send 2", new ModeSelectCommand<> (this.model, surface, Modes.SEND2), HUIControlSurface.HUI_ASSIGN1_SEND_B, () -> modeManager.isActive (Modes.SEND2));
            this.addButtonHUI (surface, ButtonID.SEND3, "Send 3", new ModeSelectCommand<> (this.model, surface, Modes.SEND3), HUIControlSurface.HUI_ASSIGN1_SEND_C, () -> modeManager.isActive (Modes.SEND3));
            this.addButtonHUI (surface, ButtonID.SEND4, "Send 4", new ModeSelectCommand<> (this.model, surface, Modes.SEND4), HUIControlSurface.HUI_ASSIGN1_SEND_D, () -> modeManager.isActive (Modes.SEND4));
            this.addButtonHUI (surface, ButtonID.SEND5, "Send 5", new ModeSelectCommand<> (this.model, surface, Modes.SEND5), HUIControlSurface.HUI_ASSIGN1_SEND_E, () -> modeManager.isActive (Modes.SEND5));

            // Assignment 2
            // HUI_ASSIGN2_ASSIGN, not supported
            // HUI_ASSIGN2_DEFAULT, not supported
            // HUI_ASSIGN2_SUSPEND, not supported
            // HUI_ASSIGN2_SHIFT, not supported
            // HUI_ASSIGN2_MUTE, not supported
            // HUI_ASSIGN2_BYPASS, not supported

            // HUI_ASSIGN2_RECRDYAL
            this.addButtonHUI (surface, ButtonID.REC_ARM_ALL, "RecRdyAll", new RecArmAllCommand<> (this.model, surface), HUIControlSurface.HUI_ASSIGN2_RECRDYAL);

            // Cursor arrows
            this.addButtonHUI (surface, ButtonID.ARROW_DOWN, "Down", new ZoomAndKeysCursorCommand (Direction.DOWN, this.model, surface), HUIControlSurface.HUI_CURSOR_DOWN);
            this.addButtonHUI (surface, ButtonID.ARROW_LEFT, "Left", new ZoomAndKeysCursorCommand (Direction.LEFT, this.model, surface), HUIControlSurface.HUI_CURSOR_LEFT);
            this.addButtonHUI (surface, ButtonID.ZOOM, "Toggle", new ZoomCommand (this.model, surface), HUIControlSurface.HUI_CURSOR_MODE, surface.getConfiguration ()::isZoomState);
            this.addButtonHUI (surface, ButtonID.ARROW_RIGHT, "Right", new ZoomAndKeysCursorCommand (Direction.RIGHT, this.model, surface), HUIControlSurface.HUI_CURSOR_RIGHT);
            this.addButtonHUI (surface, ButtonID.ARROW_UP, "Up", new ZoomAndKeysCursorCommand (Direction.UP, this.model, surface), HUIControlSurface.HUI_CURSOR_UP);
            // HUI_WHEEL_SCRUB, not supported
            // HUI_WHEEL_SHUTTLE, not supported

            // Navigation
            // HUI_TRANSPORT_TALKBACK, not supported
            final WindCommand<HUIControlSurface, HUIConfiguration> rewindCommand = new WindCommand<> (this.model, surface, false);
            this.addButtonHUI (surface, ButtonID.REWIND, "<<", rewindCommand, HUIControlSurface.HUI_TRANSPORT_REWIND, rewindCommand::isRewinding);
            final WindCommand<HUIControlSurface, HUIConfiguration> forwardCommand = new WindCommand<> (this.model, surface, true);
            this.addButtonHUI (surface, ButtonID.FORWARD, ">>", forwardCommand, HUIControlSurface.HUI_TRANSPORT_FAST_FWD, forwardCommand::isForwarding);
            this.addButtonHUI (surface, ButtonID.STOP, "Stop", new StopCommand<> (this.model, surface), HUIControlSurface.HUI_TRANSPORT_STOP, () -> !t.isPlaying ());
            this.addButtonHUI (surface, ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface), HUIControlSurface.HUI_TRANSPORT_PLAY, t::isPlaying);
            this.addButtonHUI (surface, ButtonID.RECORD, "Record", new RecordCommand<> (this.model, surface), HUIControlSurface.HUI_TRANSPORT_RECORD, t::isRecording);
            this.addButtonHUI (surface, ButtonID.RETURN_TO_ZERO, "Return to Zero", (event, velocity) -> {
                if (event == ButtonEvent.DOWN)
                    t.setPosition (0);
            }, HUIControlSurface.HUI_TRANSPORT_RETURN_TO_ZERO, () -> surface.getButton (ButtonID.RETURN_TO_ZERO).isPressed ());
            // HUI_TRANSPORT_TO_END, not supported
            this.addButtonHUI (surface, ButtonID.AUDIO_ENGINE, "Audio Engine", (event, velocity) -> {
                if (event == ButtonEvent.DOWN)
                    application.toggleEngineActive ();
            }, HUIControlSurface.HUI_TRANSPORT_ON_LINE, application::isEngineActive);

            this.addButtonHUI (surface, ButtonID.LOOP, "Loop", new ToggleLoopCommand<> (this.model, surface), HUIControlSurface.HUI_TRANSPORT_LOOP, t::isLoop);
            this.addButtonHUI (surface, ButtonID.OVERDUB, "Quick Punch", new PunchInCommand<> (this.model, surface), HUIControlSurface.HUI_TRANSPORT_QICK_PUNCH, t::isPunchInEnabled);
            // HUI_TRANSPORT_AUDITION, not supported
            this.addButtonHUI (surface, ButtonID.METRONOME, "Metronome", new MetronomeCommand<> (this.model, surface, false), HUIControlSurface.HUI_TRANSPORT_PRE, t::isMetronomeOn);
            // Note: The following two punch commands should actually set the punch point, which is
            // currently not possible with the Bitwig API
            this.addButtonHUI (surface, ButtonID.PUNCH_IN, "In", new PunchInCommand<> (this.model, surface), HUIControlSurface.HUI_TRANSPORT_IN, t::isPunchInEnabled);
            this.addButtonHUI (surface, ButtonID.PUNCH_OUT, "Out", new PunchOutCommand<> (this.model, surface), HUIControlSurface.HUI_TRANSPORT_OUT, t::isPunchOutEnabled);
            this.addButtonHUI (surface, ButtonID.TAP_TEMPO, "Tap Tempo", new TapTempoCommand<> (this.model, surface), HUIControlSurface.HUI_TRANSPORT_POST, () -> false);

            // Control room
            // HUI_CONTROL_ROOM_INPUT_3, not supported
            // HUI_CONTROL_ROOM_INPUT_2, not supported
            // HUI_CONTROL_ROOM_INPUT_1, not supported
            // HUI_CONTROL_ROOM_MUTE, not supported
            // HUI_CONTROL_ROOM_DISCRETE, not supported
            // HUI_CONTROL_ROOM_OUTPUT_3, not supported
            // HUI_CONTROL_ROOM_OUTPUT_2, not supported
            // HUI_CONTROL_ROOM_OUTPUT_1, not supported
            // HUI_CONTROL_ROOM_DIM, not supported
            // HUI_CONTROL_ROOM_MONO, not supported

            // NUM-block
            // HUI_NUM_0, not supported
            // HUI_NUM_1, not supported
            // HUI_NUM_4, not supported
            // HUI_NUM_2, not supported
            // HUI_NUM_5, not supported
            // HUI_NUM_DOT, not supported
            // HUI_NUM_3, not supported
            // HUI_NUM_6, not supported
            // HUI_NUM_ENTER, not supported
            // HUI_NUM_PLUS, not supported
            // HUI_NUM_7, not supported
            // HUI_NUM_8, not supported
            // HUI_NUM_9, not supported
            // HUI_NUM_MINUS, not supported
            // HUI_NUM_CLR, not supported
            // HUI_NUM_SET, not supported
            // HUI_NUM_DIV, not supported
            // HUI_NUM_MULT , not supported

            // Auto enable
            // HUI_AUTO_ENABLE_PLUG_IN, not supported
            // HUI_AUTO_ENABLE_PAN, not supported
            // HUI_AUTO_ENABLE_FADER, not supported
            // HUI_AUTO_ENABLE_SENDMUTE, not supported
            // HUI_AUTO_ENABLE_SEND, not supported
            // HUI_AUTO_ENABLE_MUTE, not supported

            // Automation modes
            this.addButtonHUI (surface, ButtonID.AUTOMATION_OFF, "Off", new AutomationModeCommand<> (AutomationMode.TRIM_READ, this.model, surface), HUIControlSurface.HUI_AUTO_MODE_OFF, () -> false);
            this.addButtonHUI (surface, ButtonID.AUTOMATION_TRIM, "Trim", new AutomationModeCommand<> (AutomationMode.TRIM_READ, this.model, surface), HUIControlSurface.HUI_AUTO_MODE_TRIM, () -> t.getAutomationWriteMode () == AutomationMode.TRIM_READ);
            this.addButtonHUI (surface, ButtonID.AUTOMATION_READ, "Read", new AutomationModeCommand<> (AutomationMode.READ, this.model, surface), HUIControlSurface.HUI_AUTO_MODE_READ, () -> t.getAutomationWriteMode () == AutomationMode.READ);
            this.addButtonHUI (surface, ButtonID.AUTOMATION_LATCH, "Latch", new AutomationModeCommand<> (AutomationMode.LATCH, this.model, surface), HUIControlSurface.HUI_AUTO_MODE_LATCH, () -> t.getAutomationWriteMode () == AutomationMode.LATCH);
            this.addButtonHUI (surface, ButtonID.AUTOMATION_WRITE, "Write", new AutomationModeCommand<> (AutomationMode.WRITE, this.model, surface), HUIControlSurface.HUI_AUTO_MODE_WRITE, () -> t.getAutomationWriteMode () == AutomationMode.WRITE);
            this.addButtonHUI (surface, ButtonID.AUTOMATION_TOUCH, "Touch", new AutomationModeCommand<> (AutomationMode.TOUCH, this.model, surface), HUIControlSurface.HUI_AUTO_MODE_TOUCH, () -> t.getAutomationWriteMode () == AutomationMode.TOUCH);

            // Status
            // HUI_STATUS_PHASE, not supported
            // HUI_STATUS_MONITOR, not supported
            // HUI_STATUS_AUTO, not supported
            // HUI_STATUS_SUSPEND, not supported
            // HUI_STATUS_CREATE, not supported
            // HUI_STATUS_GROUP, not supported

            // Edit
            // HUI_EDIT_PASTE, not supported
            // HUI_EDIT_CUT, not supported
            // HUI_EDIT_CAPTURE, not supported
            // HUI_EDIT_DELETE, not supported
            // HUI_EDIT_COPY, not supported
            // HUI_EDIT_SEPARATE, not supported

            // Function keys
            this.addButtonHUI (surface, ButtonID.F1, "F1", new AssignableCommand (2, this.model, surface), HUIControlSurface.HUI_F1);
            this.addButtonHUI (surface, ButtonID.F2, "F2", new AssignableCommand (3, this.model, surface), HUIControlSurface.HUI_F2);
            this.addButtonHUI (surface, ButtonID.F3, "F3", new AssignableCommand (4, this.model, surface), HUIControlSurface.HUI_F3);
            this.addButtonHUI (surface, ButtonID.F4, "F4", new AssignableCommand (5, this.model, surface), HUIControlSurface.HUI_F4);
            this.addButtonHUI (surface, ButtonID.F5, "F5", new AssignableCommand (6, this.model, surface), HUIControlSurface.HUI_F5);
            this.addButtonHUI (surface, ButtonID.F6, "F6", new AssignableCommand (7, this.model, surface), HUIControlSurface.HUI_F6);
            this.addButtonHUI (surface, ButtonID.F7, "F7", new AssignableCommand (8, this.model, surface), HUIControlSurface.HUI_F7);
            this.addButtonHUI (surface, ButtonID.F8, "F8", new AssignableCommand (9, this.model, surface), HUIControlSurface.HUI_F8_ESC);

            // DSP Edit
            // HUI_DSP_EDIT_INS_PARA, not supported
            // HUI_DSP_EDIT_ASSIGN, not supported
            // HUI_DSP_EDIT_SELECT_1, not supported
            // HUI_DSP_EDIT_SELECT_2, not supported
            // HUI_DSP_EDIT_SELECT_3, not supported
            // HUI_DSP_EDIT_SELECT_4, not supported
            // HUI_DSP_EDIT_BYPASS, not supported
            // HUI_DSP_EDIT_COMPARE, not supported

            // Foot switches
            this.addButtonHUI (surface, ButtonID.FOOTSWITCH1, "Footswitch 1", new AssignableCommand (0, this.model, surface), HUIControlSurface.HUI_FS_RLAY1);
            this.addButtonHUI (surface, ButtonID.FOOTSWITCH2, "Footswitch 2", new AssignableCommand (1, this.model, surface), HUIControlSurface.HUI_FS_RLAY2);

            // Additional command for foot controllers
            this.addButton (surface, ButtonID.NEW, "New", new NewCommand<> (this.model, surface), -1);
        }
    }


    private void addButtonHUI (final HUIControlSurface surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int huiControl)
    {
        this.addButtonHUI (surface, buttonID, label, command, huiControl, (IntSupplier) null);
    }


    private void addButtonHUI (final HUIControlSurface surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int huiControl, final BooleanSupplier supplier)
    {
        this.addButtonHUI (surface, buttonID, label, command, huiControl, () -> supplier.getAsBoolean () ? 127 : 0);
    }


    private void addButtonHUI (final HUIControlSurface surface, final ButtonID buttonID, final String label, final TriggerCommand command, final int huiControl, final IntSupplier supplier)
    {
        final IHwButton button = surface.createButton (buttonID, label);
        button.bind (command);

        surface.addHuiButton (Integer.valueOf (huiControl), button);

        final IntSupplier intSupplier = () -> button.isPressed () ? 127 : 0;
        final IntSupplier supp = supplier == null ? intSupplier : supplier;
        surface.createLight (null, supp, color -> surface.setTrigger (0, huiControl, color), state -> this.colorManager.getColor (state, buttonID), button);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        for (int index = 0; index < this.numHUIDevices; index++)
        {
            final HUIControlSurface surface = this.getSurface (index);

            for (int i = 0; i < 8; i++)
            {
                final IHwFader fader = surface.createFader (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), true);
                fader.bind (new WorkaroundFader (index * 8 + i, this.model, surface));

                final IHwRelativeKnob knob = surface.createRelativeKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1));
                knob.bind (new KnobRowModeCommand<> (i, this.model, surface));
            }
            final IHwFader fader = surface.createFader (ContinuousID.FADER_MASTER, "Master", true);
            if (this.configuration.hasMotorFaders ())
            {
                // Prevent catch up jitter with motor faders
                fader.disableTakeOver ();
            }
            fader.bind (new WorkaroundMasterFader (this.model, surface));

            surface.createRelativeKnob (ContinuousID.PLAY_POSITION, "Jog").bind (new JogWheelCommand<> (this.model, surface));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        for (int index = 0; index < this.numHUIDevices; index++)
        {
            final HUIControlSurface surface = this.getSurface (index);

            surface.getButton (ButtonID.FADER_TOUCH_1).setBounds (12.75, 460.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_1).setBounds (12.75, 389.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_1).setBounds (12.75, 342.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_1).setBounds (12.75, 295.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW1_1).setBounds (12.75, 172.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW5_1).setBounds (12.75, 207.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW2_1).setBounds (12.75, 248.25, 65.0, 39.75);
            surface.getButton (ButtonID.FADER_TOUCH_2).setBounds (87.25, 460.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_2).setBounds (87.25, 389.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_2).setBounds (87.25, 342.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_2).setBounds (87.25, 295.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW1_2).setBounds (87.25, 172.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW5_2).setBounds (87.25, 207.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW2_2).setBounds (87.25, 248.25, 65.0, 39.75);
            surface.getButton (ButtonID.FADER_TOUCH_3).setBounds (163.75, 460.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_3).setBounds (163.75, 389.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_3).setBounds (163.75, 342.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_3).setBounds (163.75, 295.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW1_3).setBounds (163.75, 172.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW5_3).setBounds (163.75, 207.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW2_3).setBounds (163.75, 248.25, 65.0, 39.75);
            surface.getButton (ButtonID.FADER_TOUCH_4).setBounds (237.0, 460.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_4).setBounds (237.0, 389.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_4).setBounds (237.0, 342.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_4).setBounds (237.0, 295.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW1_4).setBounds (237.0, 172.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW5_4).setBounds (237.0, 207.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW2_4).setBounds (237.0, 248.25, 65.0, 39.75);
            surface.getButton (ButtonID.FADER_TOUCH_5).setBounds (311.25, 460.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_5).setBounds (311.25, 389.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_5).setBounds (311.25, 342.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_5).setBounds (311.25, 295.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW1_5).setBounds (311.25, 172.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW5_5).setBounds (311.25, 207.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW2_5).setBounds (311.25, 248.25, 65.0, 39.75);
            surface.getButton (ButtonID.FADER_TOUCH_6).setBounds (386.5, 460.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_6).setBounds (386.5, 389.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_6).setBounds (386.5, 342.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_6).setBounds (386.5, 295.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW1_6).setBounds (386.5, 172.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW5_6).setBounds (386.5, 207.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW2_6).setBounds (386.5, 248.25, 65.0, 39.75);
            surface.getButton (ButtonID.FADER_TOUCH_7).setBounds (459.0, 460.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_7).setBounds (459.0, 389.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_7).setBounds (459.0, 342.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_7).setBounds (459.0, 295.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW1_7).setBounds (459.0, 172.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW5_7).setBounds (459.0, 207.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW2_7).setBounds (459.0, 248.25, 65.0, 39.75);
            surface.getButton (ButtonID.FADER_TOUCH_8).setBounds (532.25, 460.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_8).setBounds (532.25, 389.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_8).setBounds (532.25, 342.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_8).setBounds (532.25, 295.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW1_8).setBounds (532.25, 172.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW5_8).setBounds (532.25, 207.5, 65.0, 25.0);
            surface.getButton (ButtonID.ROW2_8).setBounds (532.25, 248.25, 65.0, 39.75);
            surface.getButton (ButtonID.CONTROL).setBounds (921.0, 637.5, 65.0, 39.75);
            surface.getButton (ButtonID.SHIFT).setBounds (776.5, 637.5, 65.0, 39.75);
            surface.getButton (ButtonID.UNDO).setBounds (849.25, 320.25, 65.0, 39.75);
            surface.getButton (ButtonID.ALT).setBounds (847.75, 637.5, 65.0, 39.75);
            surface.getButton (ButtonID.SELECT).setBounds (703.75, 637.5, 65.0, 39.75);
            surface.getButton (ButtonID.SAVE).setBounds (921.5, 320.25, 65.0, 39.75);
            surface.getButton (ButtonID.MIXER).setBounds (921.5, 366.75, 65.0, 39.75);
            surface.getButton (ButtonID.NOTE_EDITOR).setBounds (705.0, 366.75, 65.0, 39.75);
            surface.getButton (ButtonID.AUTOMATION_EDITOR).setBounds (777.75, 366.75, 65.0, 39.75);
            surface.getButton (ButtonID.TOGGLE_DEVICE).setBounds (849.25, 366.75, 65.0, 39.75);
            surface.getButton (ButtonID.MOVE_TRACK_LEFT).setBounds (847.75, 542.0, 65.0, 39.75);
            surface.getButton (ButtonID.MOVE_BANK_LEFT).setBounds (703.75, 542.0, 65.0, 39.75);
            surface.getButton (ButtonID.MOVE_TRACK_RIGHT).setBounds (921.0, 542.0, 65.0, 39.75);
            surface.getButton (ButtonID.MOVE_BANK_RIGHT).setBounds (776.5, 542.0, 65.0, 39.75);
            surface.getButton (ButtonID.PAN_SEND).setBounds (632.5, 366.75, 65.0, 39.75);
            surface.getButton (ButtonID.SEND1).setBounds (632.5, 414.0, 65.0, 39.75);
            surface.getButton (ButtonID.SEND2).setBounds (705.0, 414.0, 65.0, 39.75);
            surface.getButton (ButtonID.SEND3).setBounds (777.75, 414.0, 65.0, 39.75);
            surface.getButton (ButtonID.SEND4).setBounds (849.25, 414.0, 65.0, 39.75);
            surface.getButton (ButtonID.SEND5).setBounds (921.5, 414.0, 65.0, 39.75);
            surface.getButton (ButtonID.ARROW_DOWN).setBounds (777.75, 797.0, 65.0, 39.75);
            surface.getButton (ButtonID.ARROW_LEFT).setBounds (705.0, 750.25, 65.0, 39.75);
            surface.getButton (ButtonID.ZOOM).setBounds (777.75, 750.25, 65.0, 39.75);
            surface.getButton (ButtonID.ARROW_RIGHT).setBounds (849.0, 750.25, 65.0, 39.75);
            surface.getButton (ButtonID.ARROW_UP).setBounds (777.75, 702.25, 65.0, 39.75);
            surface.getButton (ButtonID.REWIND).setBounds (556.0, 942.0, 65.0, 39.75);
            surface.getButton (ButtonID.FORWARD).setBounds (630.0, 942.0, 65.0, 39.75);
            surface.getButton (ButtonID.STOP).setBounds (779.0, 942.0, 65.0, 39.75);
            surface.getButton (ButtonID.PLAY).setBounds (850.25, 942.0, 65.0, 39.75);
            surface.getButton (ButtonID.RECORD).setBounds (923.5, 942.0, 65.0, 39.75);
            surface.getButton (ButtonID.RETURN_TO_ZERO).setBounds (399.75, 942.0, 65.0, 39.75);
            surface.getButton (ButtonID.AUDIO_ENGINE).setBounds (705.0, 320.25, 65.0, 39.75);
            surface.getButton (ButtonID.LOOP).setBounds (706.25, 942.0, 65.0, 39.75);
            surface.getButton (ButtonID.OVERDUB).setBounds (703.75, 590.25, 65.0, 39.75);
            surface.getButton (ButtonID.METRONOME).setBounds (921.0, 590.25, 65.0, 39.75);
            surface.getButton (ButtonID.PUNCH_IN).setBounds (776.5, 590.25, 65.0, 39.75);
            surface.getButton (ButtonID.PUNCH_OUT).setBounds (847.75, 590.25, 65.0, 39.75);
            surface.getButton (ButtonID.TAP_TEMPO).setBounds (481.25, 942.0, 65.0, 39.75);
            surface.getButton (ButtonID.AUTOMATION_OFF).setBounds (632.5, 272.25, 30.25, 39.75);
            surface.getButton (ButtonID.AUTOMATION_TRIM).setBounds (777.75, 272.25, 65.0, 39.75);
            surface.getButton (ButtonID.AUTOMATION_READ).setBounds (667.25, 272.25, 30.25, 39.75);
            surface.getButton (ButtonID.AUTOMATION_LATCH).setBounds (921.5, 272.25, 65.0, 39.75);
            surface.getButton (ButtonID.AUTOMATION_WRITE).setBounds (705.0, 272.25, 65.0, 39.75);
            surface.getButton (ButtonID.AUTOMATION_TOUCH).setBounds (849.25, 272.25, 65.0, 39.75);
            surface.getButton (ButtonID.REC_ARM_ALL).setBounds (632.5, 225.5, 65.0, 39.75);
            surface.getButton (ButtonID.F1).setBounds (632.5, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.F2).setBounds (705.0, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.F3).setBounds (777.75, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.F4).setBounds (849.25, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.F5).setBounds (921.5, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.F6).setBounds (777.75, 225.5, 65.0, 39.75);
            surface.getButton (ButtonID.F7).setBounds (849.25, 225.5, 65.0, 39.75);
            surface.getButton (ButtonID.F8).setBounds (921.5, 225.5, 65.0, 39.75);
            surface.getButton (ButtonID.FOOTSWITCH1).setBounds (12.5, 942.0, 77.75, 39.75);
            surface.getButton (ButtonID.FOOTSWITCH2).setBounds (102.5, 942.0, 77.75, 39.75);

            surface.getContinuous (ContinuousID.FADER1).setBounds (12.75, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.KNOB1).setBounds (12.25, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.FADER2).setBounds (87.25, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.KNOB2).setBounds (86.25, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.FADER3).setBounds (163.75, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.KNOB3).setBounds (160.0, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.FADER4).setBounds (237.0, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.KNOB4).setBounds (234.0, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.FADER5).setBounds (311.25, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.KNOB5).setBounds (308.0, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.FADER6).setBounds (386.5, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.KNOB6).setBounds (381.75, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.FADER7).setBounds (459.0, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.KNOB7).setBounds (455.75, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.FADER8).setBounds (532.25, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.KNOB8).setBounds (529.5, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.FADER_MASTER).setBounds (613.5, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.PLAY_POSITION).setBounds (859.5, 806.5, 115.25, 115.75);

            surface.getTextDisplay (0).getHardwareDisplay ().setBounds (11.75, 11.75, 601.0, 73.25);
            surface.getTextDisplay (1).getHardwareDisplay ().setBounds (699.0, 27.0, 229.0, 57.75);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final boolean shouldSendPing = this.configuration.shouldSendPing ();

        for (int index = 0; index < this.numHUIDevices; index++)
        {
            final HUIControlSurface surface = this.getSurface (index);
            surface.getViewManager ().setActive (Views.CONTROL);
            surface.getModeManager ().setActive (Modes.PAN);

            if (shouldSendPing)
                this.sendPing (surface);
        }
    }


    private void sendPing (final HUIControlSurface surface)
    {
        surface.getMidiOutput ().sendNote (0, 0);
        this.host.scheduleTask ( () -> this.sendPing (surface), 1000);
    }


    private void updateSegmentDisplay (final HUIControlSurface surface)
    {
        if (!this.configuration.hasSegmentDisplay ())
            return;

        final ITransport t = this.model.getTransport ();
        String positionText = t.getPositionText ();
        positionText = positionText.replace ('.', ':');
        surface.getSegmentDisplay ().setRow (0, positionText).allDone ();
    }


    private void updateVUandFaders ()
    {
        final double upperBound = this.valueChanger.getUpperBound ();
        final boolean enableVUMeters = this.configuration.isEnableVUMeters ();
        final boolean hasMotorFaders = this.configuration.hasMotorFaders ();

        final ITrackBank tb = this.model.getCurrentTrackBank ();

        for (int index = 0; index < this.surfaces.size (); index++)
        {
            final HUIControlSurface surface = this.surfaces.get (index);

            for (int channel = 0; channel < 8; channel++)
            {
                final ITrack track = tb.getItem (index * 8 + channel);

                // Update VU LEDs of channel
                if (enableVUMeters)
                    surface.updateVuMeters (channel, track.getVuLeft (), track.getVuRight (), upperBound);

                // Update motor fader of channel
                if (hasMotorFaders)
                    surface.updateFaders (channel, track.getVolume ());
            }
        }
    }


    private void updateMode (final HUIControlSurface surface, final Modes modeID)
    {
        if (modeID == null)
            return;

        final IMode mode = surface.getModeManager ().get (modeID);
        if (mode instanceof final AbstractTrackMode abstractMode)
            abstractMode.updateKnobLEDs ();
        this.updateIndication (modeID);
    }


    protected void updateIndication (final Modes mode)
    {
        if (this.currentMode != null && this.currentMode.equals (mode))
            return;
        this.currentMode = mode;

        final ITrackBank tb = this.model.getTrackBank ();
        final boolean isVolume = Modes.VOLUME == mode;
        final boolean isPan = Modes.PAN == mode;

        for (int i = 0; i < tb.getPageSize (); i++)
        {
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (isVolume);
            track.setPanIndication (isPan);

            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < sendBank.getPageSize (); j++)
                sendBank.getItem (j).setIndication (mode.ordinal () - Modes.SEND1.ordinal () == j);
        }
    }
}
