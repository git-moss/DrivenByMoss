// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.hui;

import de.mossgrabers.controller.hui.command.trigger.AssignableCommand;
import de.mossgrabers.controller.hui.command.trigger.FaderTouchCommand;
import de.mossgrabers.controller.hui.command.trigger.ZoomAndKeysCursorCommand;
import de.mossgrabers.controller.hui.command.trigger.ZoomCommand;
import de.mossgrabers.controller.hui.controller.HUIControlSurface;
import de.mossgrabers.controller.hui.controller.HUIDisplay;
import de.mossgrabers.controller.hui.controller.HUIMainDisplay;
import de.mossgrabers.controller.hui.controller.HUISegmentDisplay;
import de.mossgrabers.controller.hui.mode.track.PanMode;
import de.mossgrabers.controller.hui.mode.track.SendMode;
import de.mossgrabers.controller.hui.mode.track.VolumeMode;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.AutomationCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand;
import de.mossgrabers.framework.command.trigger.application.SaveCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.track.MuteCommand;
import de.mossgrabers.framework.command.trigger.track.RecArmCommand;
import de.mossgrabers.framework.command.trigger.track.SelectCommand;
import de.mossgrabers.framework.command.trigger.track.SoloCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.Relative4ValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.ControlOnlyView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.Views;

import java.util.Arrays;


/**
 * Support for the Mackie HUI protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HUIControllerSetup extends AbstractControllerSetup<HUIControlSurface, HUIConfiguration>
{
    /** State for button LED on. */
    public static final int HUI_BUTTON_STATE_ON  = 127;
    /** State for button LED off. */
    public static final int HUI_BUTTON_STATE_OFF = 0;

    private final int []    vuValuesL            = new int [8];
    private final int []    vuValuesR            = new int [8];
    private final int []    faderValues          = new int [36];


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public HUIControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);

        Arrays.fill (this.vuValuesL, -1);
        Arrays.fill (this.vuValuesR, -1);
        Arrays.fill (this.faderValues, -1);

        this.colorManager = new ColorManager ();
        this.valueChanger = new Relative4ValueChanger (16384, 100, 10);
        this.configuration = new HUIConfiguration (host, this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushSurfaces ();

        this.updateButtons ();
        this.updateMode (this.getSurface ().getModeManager ().getActiveOrTempModeId ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFullFlatTrackList (true);
        ms.setNumTracks (8);
        ms.setNumSends (5);
        ms.setNumScenes (0);
        ms.setNumFilterColumnEntries (8);
        ms.setNumResults (8);
        ms.setNumParams (8);
        ms.setNumDeviceLayers (0);
        ms.setNumDrumPadLayers (0);
        ms.setNumMarkers (8);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);

        this.model.getTrackBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();

        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput (null);
        final HUIControlSurface surface = new HUIControlSurface (this.host, this.colorManager, this.configuration, output, input, this.model);
        this.surfaces.add (surface);
        surface.setDisplay (new HUIDisplay (this.host, output));
        surface.setMainDisplay (new HUIMainDisplay (this.host, output));
        surface.setSegmentDisplay (new HUISegmentDisplay (output));
        surface.getModeManager ().setDefaultMode (Modes.MODE_VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final HUIControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_PAN, new PanMode (surface, this.model));
        for (int i = 0; i < 5; i++)
            modeManager.registerMode (Modes.get (Modes.MODE_SEND1, i), new SendMode (i, surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        final HUIControlSurface surface = this.getSurface ();
        surface.getModeManager ().addModeListener ( (oldMode, newMode) -> {
            surface.getModeManager ().setActiveMode (newMode);
            this.updateMode (null);
            this.updateMode (newMode);
        });

        this.configuration.addSettingObserver (AbstractConfiguration.ENABLE_VU_METERS, () -> {
            final Mode activeMode = surface.getModeManager ().getActiveOrTempMode ();
            if (activeMode != null)
                activeMode.updateDisplay ();
            ((HUIDisplay) surface.getDisplay ()).forceFlush ();
        });
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final HUIControlSurface surface = this.getSurface ();
        surface.getViewManager ().registerView (Views.VIEW_CONTROL, new ControlOnlyView<> (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        // Assignments to the main device
        final HUIControlSurface surface = this.getSurface ();

        // Channel commands
        for (int i = 0; i < 8; i++)
        {
            this.addTriggerCommand (TriggerCommandID.get (TriggerCommandID.FADER_TOUCH_1, i), HUIControlSurface.HUI_FADER1 + i * 8, new FaderTouchCommand (i, this.model, surface));
            this.addTriggerCommand (TriggerCommandID.get (TriggerCommandID.ROW_SELECT_1, i), HUIControlSurface.HUI_SELECT1 + i * 8, new SelectCommand<> (i, this.model, surface));
            this.addTriggerCommand (TriggerCommandID.get (TriggerCommandID.ROW4_1, i), HUIControlSurface.HUI_MUTE1 + i * 8, new MuteCommand<> (i, this.model, surface));
            this.addTriggerCommand (TriggerCommandID.get (TriggerCommandID.ROW3_1, i), HUIControlSurface.HUI_SOLO1 + i * 8, new SoloCommand<> (i, this.model, surface));
            // HUI_AUTO1, not supported
            this.addTriggerCommand (TriggerCommandID.get (TriggerCommandID.ROW1_1, i), HUIControlSurface.HUI_VSELECT1 + i * 8, new ButtonRowModeCommand<> (0, i, this.model, surface));
            // HUI_INSERT1, not supported
            this.addTriggerCommand (TriggerCommandID.get (TriggerCommandID.ROW2_1, i), HUIControlSurface.HUI_ARM1 + i * 8, new RecArmCommand<> (i, this.model, surface));
        }

        // Key commands
        this.addTriggerCommand (TriggerCommandID.CONTROL, HUIControlSurface.HUI_KEY_CTRL_CLT, NopCommand.INSTANCE);
        this.addTriggerCommand (TriggerCommandID.SHIFT, HUIControlSurface.HUI_KEY_SHIFT_AD, NopCommand.INSTANCE);
        // HUI_KEY_EDITMODE, not supported
        this.addTriggerCommand (TriggerCommandID.UNDO, HUIControlSurface.HUI_KEY_UNDO, new UndoCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ALT, HUIControlSurface.HUI_KEY_ALT_FINE, NopCommand.INSTANCE);
        this.addTriggerCommand (TriggerCommandID.SELECT, HUIControlSurface.HUI_KEY_OPTION_A, NopCommand.INSTANCE);
        // HUI_KEY_EDITTOOL, not supported
        this.addTriggerCommand (TriggerCommandID.SAVE, HUIControlSurface.HUI_KEY_SAVE, new SaveCommand<> (this.model, surface));

        // Window commands
        this.addTriggerCommand (TriggerCommandID.MIXER, HUIControlSurface.HUI_WINDOW_MIX, new PaneCommand<> (PaneCommand.Panels.MIXER, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.NOTE_EDITOR, HUIControlSurface.HUI_WINDOW_EDIT, new PaneCommand<> (PaneCommand.Panels.NOTE, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_EDITOR, HUIControlSurface.HUI_WINDOW_TRANSPRT, new PaneCommand<> (PaneCommand.Panels.AUTOMATION, this.model, surface));
        // HUI_WINDOW_MEM_LOC, not supported
        this.addTriggerCommand (TriggerCommandID.TOGGLE_DEVICE, HUIControlSurface.HUI_WINDOW_STATUS, new PaneCommand<> (PaneCommand.Panels.DEVICE, this.model, surface));
        // HUI_WINDOW_ALT, not supported

        // Bank navigation
        this.addTriggerCommand (TriggerCommandID.MOVE_TRACK_LEFT, HUIControlSurface.HUI_CHANL_LEFT, new ModeCursorCommand<> (Direction.LEFT, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.MOVE_BANK_LEFT, HUIControlSurface.HUI_BANK_LEFT, new ModeCursorCommand<> (Direction.DOWN, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.MOVE_TRACK_RIGHT, HUIControlSurface.HUI_CHANL_RIGHT, new ModeCursorCommand<> (Direction.RIGHT, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.MOVE_BANK_RIGHT, HUIControlSurface.HUI_BANK_RIGHT, new ModeCursorCommand<> (Direction.UP, this.model, surface));

        // Assignment (mode selection)
        // HUI_ASSIGN1_OUTPUT, not supported
        // HUI_ASSIGN1_INPUT, not supported
        this.addTriggerCommand (TriggerCommandID.PAN_SEND, HUIControlSurface.HUI_ASSIGN1_PAN, new ModeSelectCommand<> (this.model, surface, Modes.MODE_PAN));
        this.addTriggerCommand (TriggerCommandID.SEND1, HUIControlSurface.HUI_ASSIGN1_SEND_A, new ModeSelectCommand<> (this.model, surface, Modes.MODE_SEND1));
        this.addTriggerCommand (TriggerCommandID.SEND2, HUIControlSurface.HUI_ASSIGN1_SEND_B, new ModeSelectCommand<> (this.model, surface, Modes.MODE_SEND2));
        this.addTriggerCommand (TriggerCommandID.SEND3, HUIControlSurface.HUI_ASSIGN1_SEND_C, new ModeSelectCommand<> (this.model, surface, Modes.MODE_SEND3));
        this.addTriggerCommand (TriggerCommandID.SEND4, HUIControlSurface.HUI_ASSIGN1_SEND_D, new ModeSelectCommand<> (this.model, surface, Modes.MODE_SEND4));
        this.addTriggerCommand (TriggerCommandID.SEND5, HUIControlSurface.HUI_ASSIGN1_SEND_E, new ModeSelectCommand<> (this.model, surface, Modes.MODE_SEND5));

        // Assignment 2
        // HUI_ASSIGN2_ASSIGN, not supported
        // HUI_ASSIGN2_DEFAULT, not supported
        // HUI_ASSIGN2_SUSPEND, not supported
        // HUI_ASSIGN2_SHIFT, not supported
        // HUI_ASSIGN2_MUTE, not supported
        // HUI_ASSIGN2_BYPASS, not supported
        // HUI_ASSIGN2_RECRDYAL, not supported

        // Cursor arrows
        this.addTriggerCommand (TriggerCommandID.ARROW_DOWN, HUIControlSurface.HUI_CURSOR_DOWN, new ZoomAndKeysCursorCommand (Direction.DOWN, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_LEFT, HUIControlSurface.HUI_CURSOR_LEFT, new ZoomAndKeysCursorCommand (Direction.LEFT, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ZOOM, HUIControlSurface.HUI_CURSOR_MODE, new ZoomCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_RIGHT, HUIControlSurface.HUI_CURSOR_RIGHT, new ZoomAndKeysCursorCommand (Direction.RIGHT, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_UP, HUIControlSurface.HUI_CURSOR_UP, new ZoomAndKeysCursorCommand (Direction.UP, this.model, surface));
        // HUI_WHEEL_SCRUB, not supported
        // HUI_WHEEL_SHUTTLE, not supported

        // Navigation
        // HUI_TRANSPORT_TALKBACK, not supported
        this.addTriggerCommand (TriggerCommandID.REWIND, HUIControlSurface.HUI_TRANSPORT_REWIND, new WindCommand<> (this.model, surface, false));
        this.addTriggerCommand (TriggerCommandID.FORWARD, HUIControlSurface.HUI_TRANSPORT_FAST_FWD, new WindCommand<> (this.model, surface, true));
        this.addTriggerCommand (TriggerCommandID.STOP, HUIControlSurface.HUI_TRANSPORT_STOP, new StopCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.PLAY, HUIControlSurface.HUI_TRANSPORT_PLAY, new PlayCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.RECORD, HUIControlSurface.HUI_TRANSPORT_RECORD, new RecordCommand<> (this.model, surface));
        // HUI_TRANSPORT_RETURN_TO_ZERO, not supported
        // HUI_TRANSPORT_TO_END, not supported
        // HUI_TRANSPORT_ON_LINE, not supported
        this.addTriggerCommand (TriggerCommandID.LOOP, HUIControlSurface.HUI_TRANSPORT_LOOP, new ToggleLoopCommand<> (this.model, surface));
        // HUI_TRANSPORT_QICK_PUNCH, not supported
        // HUI_TRANSPORT_AUDITION, not supported
        this.addTriggerCommand (TriggerCommandID.METRONOME, HUIControlSurface.HUI_TRANSPORT_PRE, new MetronomeCommand<> (this.model, surface));
        // HUI_TRANSPORT_IN, not supported
        // HUI_TRANSPORT_OUT, not supported
        this.addTriggerCommand (TriggerCommandID.TAP_TEMPO, HUIControlSurface.HUI_TRANSPORT_POST, new TapTempoCommand<> (this.model, surface));

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

        // Num-block
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
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_TRIM, HUIControlSurface.HUI_AUTO_MODE_TRIM, new AutomationCommand<> (2, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_LATCH, HUIControlSurface.HUI_AUTO_MODE_LATCH, new AutomationCommand<> (4, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_READ, HUIControlSurface.HUI_AUTO_MODE_READ, new AutomationCommand<> (0, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_READ, HUIControlSurface.HUI_AUTO_MODE_OFF, new AutomationCommand<> (0, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_WRITE, HUIControlSurface.HUI_AUTO_MODE_WRITE, new AutomationCommand<> (1, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_TOUCH, HUIControlSurface.HUI_AUTO_MODE_TOUCH, new AutomationCommand<> (3, this.model, surface));

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
        this.addTriggerCommand (TriggerCommandID.F1, HUIControlSurface.HUI_F1, new AssignableCommand (2, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.F2, HUIControlSurface.HUI_F2, new AssignableCommand (3, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.F3, HUIControlSurface.HUI_F3, new AssignableCommand (4, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.F4, HUIControlSurface.HUI_F4, new AssignableCommand (5, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.F5, HUIControlSurface.HUI_F5, new AssignableCommand (6, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.F6, HUIControlSurface.HUI_F6, new AssignableCommand (7, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.F7, HUIControlSurface.HUI_F7, new AssignableCommand (8, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.F8, HUIControlSurface.HUI_F8_ESC, new AssignableCommand (9, this.model, surface));

        // DSP Edit
        // HUI_DSP_EDIT_INS_PARA, not supported
        // HUI_DSP_EDIT_ASSIGN, not supported
        // HUI_DSP_EDIT_SELECT_1, not supported
        // HUI_DSP_EDIT_SELECT_2, not supported
        // HUI_DSP_EDIT_SELECT_3, not supported
        // HUI_DSP_EDIT_SELECT_4, not supported
        // HUI_DSP_EDIT_BYPASS, not supported
        // HUI_DSP_EDIT_COMPARE, not supported

        // Footswitches
        this.addTriggerCommand (TriggerCommandID.FOOTSWITCH1, HUIControlSurface.HUI_FS_RLAY1, new AssignableCommand (0, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.FOOTSWITCH2, HUIControlSurface.HUI_FS_RLAY2, new AssignableCommand (1, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final HUIControlSurface surface = this.getSurface ();

        surface.getViewManager ().setActiveView (Views.VIEW_CONTROL);
        surface.getModeManager ().setActiveMode (Modes.MODE_PAN);

        this.sendPing ();
    }


    private void sendPing ()
    {
        this.getSurface ().getOutput ().sendNote (0, 0);
        this.host.scheduleTask (this::sendPing, 1000);
    }


    @SuppressWarnings("unchecked")
    private void updateButtons ()
    {
        final HUIControlSurface surface = this.getSurface ();
        final Modes mode = surface.getModeManager ().getActiveOrTempModeId ();
        if (mode == null)
            return;

        this.updateVUandFaders ();
        this.updateSegmentDisplay ();

        // Set button states
        final ITransport t = this.model.getTransport ();
        surface.updateTrigger (HUIControlSurface.HUI_ASSIGN1_PAN, Modes.MODE_PAN.equals (mode) ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_ASSIGN1_SEND_A, Modes.MODE_SEND1.equals (mode) ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_ASSIGN1_SEND_B, Modes.MODE_SEND2.equals (mode) ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_ASSIGN1_SEND_C, Modes.MODE_SEND3.equals (mode) ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_ASSIGN1_SEND_D, Modes.MODE_SEND4.equals (mode) ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_ASSIGN1_SEND_E, Modes.MODE_SEND5.equals (mode) ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);

        final String automationWriteMode = t.getAutomationWriteMode ();
        final boolean writingArrangerAutomation = t.isWritingArrangerAutomation ();

        surface.updateTrigger (HUIControlSurface.HUI_AUTO_MODE_OFF, !writingArrangerAutomation ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_AUTO_MODE_READ, !writingArrangerAutomation ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_AUTO_MODE_WRITE, writingArrangerAutomation && TransportConstants.AUTOMATION_MODES_VALUES[2].equals (automationWriteMode) ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_AUTO_MODE_TRIM, t.isWritingClipLauncherAutomation () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_AUTO_MODE_TOUCH, writingArrangerAutomation && TransportConstants.AUTOMATION_MODES_VALUES[1].equals (automationWriteMode) ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_AUTO_MODE_LATCH, writingArrangerAutomation && TransportConstants.AUTOMATION_MODES_VALUES[0].equals (automationWriteMode) ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);

        final View view = surface.getViewManager ().getView (Views.VIEW_CONTROL);
        surface.updateTrigger (HUIControlSurface.HUI_TRANSPORT_REWIND, ((WindCommand<HUIControlSurface, HUIConfiguration>) view.getTriggerCommand (TriggerCommandID.REWIND)).isRewinding () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_TRANSPORT_FAST_FWD, ((WindCommand<HUIControlSurface, HUIConfiguration>) view.getTriggerCommand (TriggerCommandID.FORWARD)).isForwarding () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_TRANSPORT_LOOP, t.isLoop () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_TRANSPORT_STOP, !t.isPlaying () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_TRANSPORT_PLAY, t.isPlaying () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_TRANSPORT_RECORD, t.isRecording () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);

        surface.updateTrigger (HUIControlSurface.HUI_TRANSPORT_PRE, t.isMetronomeOn () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        surface.updateTrigger (HUIControlSurface.HUI_TRANSPORT_POST, HUI_BUTTON_STATE_OFF);

        surface.updateTrigger (HUIControlSurface.HUI_CURSOR_MODE, surface.getConfiguration ().isZoomState () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            final int offset = i * 8;
            surface.updateTrigger (HUIControlSurface.HUI_SELECT1 + offset, track.isSelected () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
            surface.updateTrigger (HUIControlSurface.HUI_ARM1 + offset, track.isRecArm () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
            surface.updateTrigger (HUIControlSurface.HUI_SOLO1 + offset, track.isSolo () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
            surface.updateTrigger (HUIControlSurface.HUI_MUTE1 + offset, track.isMute () ? HUI_BUTTON_STATE_ON : HUI_BUTTON_STATE_OFF);
        }
    }


    private void updateSegmentDisplay ()
    {
        if (!this.configuration.hasSegmentDisplay ())
            return;

        final ITransport t = this.model.getTransport ();
        String positionText = t.getPositionText ();
        positionText = positionText.substring (0, positionText.length () - 3);
        this.getSurface ().getSegmentDisplay ().setTransportPositionDisplay (positionText);
    }


    private void updateVUandFaders ()
    {
        final double upperBound = this.valueChanger.getUpperBound ();
        final boolean enableVUMeters = this.configuration.isEnableVUMeters ();
        final boolean hasMotorFaders = this.configuration.hasMotorFaders ();

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        IMidiOutput output;
        final HUIControlSurface surface = this.getSurface ();
        output = surface.getOutput ();
        for (int channel = 0; channel < 8; channel++)
        {
            final ITrack track = tb.getItem (channel);

            // Update VU LEDs of channel
            if (enableVUMeters)
            {
                final int vuLeft = track.getVuLeft ();
                if (vuLeft != this.vuValuesL[channel])
                {
                    this.vuValuesL[channel] = vuLeft;
                    final int scaledValue = (int) Math.floor (vuLeft * 12 / upperBound);
                    output.sendPolyphonicAftertouch (channel, scaledValue);
                }
                final int vuRight = track.getVuRight ();
                if (vuRight != this.vuValuesR[channel])
                {
                    this.vuValuesR[channel] = vuRight;
                    final int scaledValue = (int) Math.floor (vuRight * 12 / upperBound);
                    output.sendPolyphonicAftertouch (0x10 + channel, scaledValue);
                }
            }

            // Update motor fader of channel
            if (hasMotorFaders)
                this.updateFaders (output, channel, track);
        }
    }


    private void updateFaders (final IMidiOutput output, final int channel, final ITrack track)
    {
        final int value = track.getVolume ();
        if (value != this.faderValues[channel])
        {
            this.faderValues[channel] = value;
            output.sendCC (channel, value / 128);
            output.sendCC (0x20 + channel, value % 128);
        }
    }


    private void updateMode (final Modes mode)
    {
        if (mode != null)
            this.updateIndication (mode);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Modes mode)
    {
        if (this.currentMode != null && this.currentMode.equals (mode))
            return;
        this.currentMode = mode;

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final boolean isEffect = this.model.isEffectTrackBankActive ();
        final boolean isPan = Modes.MODE_PAN.equals (mode);
        final boolean isTrack = Modes.MODE_TRACK.equals (mode);
        final boolean isDevice = Modes.MODE_DEVICE_PARAMS.equals (mode);

        tb.setIndication (!isEffect);
        if (tbe != null)
            tbe.setIndication (isEffect);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final ITrack selectedTrack = tb.getSelectedItem ();
        for (int i = 0; i < tb.getPageSize (); i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i && isTrack;
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (!isEffect && (isTrack || hasTrackSel));
            track.setPanIndication (!isEffect && (isPan || hasTrackSel));

            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < sendBank.getPageSize (); j++)
                sendBank.getItem (j).setIndication (!isEffect && (mode.ordinal () - Modes.MODE_SEND1.ordinal () == j || hasTrackSel));

            if (tbe != null)
            {
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (isEffect);
                fxTrack.setPanIndication (isEffect && isPan);
            }
        }

        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        for (int i = 0; i < parameterBank.getPageSize (); i++)
            parameterBank.getItem (i).setIndication (isDevice);
    }
}
