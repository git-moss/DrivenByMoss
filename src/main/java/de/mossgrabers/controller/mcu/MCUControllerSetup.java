// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu;

import de.mossgrabers.controller.mcu.command.continuous.PlayPositionTempoCommand;
import de.mossgrabers.controller.mcu.command.pitchbend.PitchbendVolumeCommand;
import de.mossgrabers.controller.mcu.command.trigger.AssignableCommand;
import de.mossgrabers.controller.mcu.command.trigger.DevicesCommand;
import de.mossgrabers.controller.mcu.command.trigger.FaderTouchCommand;
import de.mossgrabers.controller.mcu.command.trigger.GrooveCommand;
import de.mossgrabers.controller.mcu.command.trigger.KeyCommand;
import de.mossgrabers.controller.mcu.command.trigger.KeyCommand.Key;
import de.mossgrabers.controller.mcu.command.trigger.MCUCursorCommand;
import de.mossgrabers.controller.mcu.command.trigger.MCURecordCommand;
import de.mossgrabers.controller.mcu.command.trigger.OverdubCommand;
import de.mossgrabers.controller.mcu.command.trigger.ScrubCommand;
import de.mossgrabers.controller.mcu.command.trigger.SelectCommand;
import de.mossgrabers.controller.mcu.command.trigger.SendSelectCommand;
import de.mossgrabers.controller.mcu.command.trigger.TempoTicksCommand;
import de.mossgrabers.controller.mcu.command.trigger.ToggleDisplayCommand;
import de.mossgrabers.controller.mcu.command.trigger.TracksCommand;
import de.mossgrabers.controller.mcu.command.trigger.ZoomCommand;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mcu.controller.MCUDisplay;
import de.mossgrabers.controller.mcu.controller.MCUSegmentDisplay;
import de.mossgrabers.controller.mcu.mode.MarkerMode;
import de.mossgrabers.controller.mcu.mode.device.DeviceBrowserMode;
import de.mossgrabers.controller.mcu.mode.device.DeviceParamsMode;
import de.mossgrabers.controller.mcu.mode.track.MasterMode;
import de.mossgrabers.controller.mcu.mode.track.PanMode;
import de.mossgrabers.controller.mcu.mode.track.SendMode;
import de.mossgrabers.controller.mcu.mode.track.TrackMode;
import de.mossgrabers.controller.mcu.mode.track.VolumeMode;
import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.AutomationCommand;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.command.trigger.MarkerCommand;
import de.mossgrabers.framework.command.trigger.ShiftCommand;
import de.mossgrabers.framework.command.trigger.application.DuplicateCommand;
import de.mossgrabers.framework.command.trigger.application.LayoutCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand;
import de.mossgrabers.framework.command.trigger.application.SaveCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.device.DeviceOnOffCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.track.MoveTrackBankCommand;
import de.mossgrabers.framework.command.trigger.track.ToggleTrackBanksCommand;
import de.mossgrabers.framework.command.trigger.track.ToggleVUCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.PunchInCommand;
import de.mossgrabers.framework.command.trigger.transport.PunchOutCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.Relative2ValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.ControlOnlyView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;


/**
 * Support for the Mackie MCU protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUControllerSetup extends AbstractControllerSetup<MCUControlSurface, MCUConfiguration>
{
    /** State for button LED on. */
    public static final int                 MCU_BUTTON_STATE_ON  = 127;
    /** State for button LED off. */
    public static final int                 MCU_BUTTON_STATE_OFF = 0;

    private static final Map<Modes, String> MODE_ACRONYMS        = new EnumMap<> (Modes.class);

    static
    {
        MODE_ACRONYMS.put (Modes.TRACK, "TR");
        MODE_ACRONYMS.put (Modes.VOLUME, "VL");
        MODE_ACRONYMS.put (Modes.PAN, "PN");
        MODE_ACRONYMS.put (Modes.SEND1, "S1");
        MODE_ACRONYMS.put (Modes.SEND2, "S2");
        MODE_ACRONYMS.put (Modes.SEND3, "S3");
        MODE_ACRONYMS.put (Modes.SEND4, "S4");
        MODE_ACRONYMS.put (Modes.SEND5, "S5");
        MODE_ACRONYMS.put (Modes.SEND6, "S6");
        MODE_ACRONYMS.put (Modes.SEND7, "S7");
        MODE_ACRONYMS.put (Modes.SEND8, "S8");
        MODE_ACRONYMS.put (Modes.MASTER, "MT");
        MODE_ACRONYMS.put (Modes.DEVICE_PARAMS, "DC");
        MODE_ACRONYMS.put (Modes.BROWSER, "BR");
        MODE_ACRONYMS.put (Modes.MARKERS, "MK");

    }

    private final int [] masterVuValues   = new int [2];
    private int          masterFaderValue = -1;
    private final int [] vuValues         = new int [36];
    private final int [] faderValues      = new int [36];
    private final int    numMCUDevices;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     * @param numMCUDevices The number of MCU devices (main device + extenders) to support
     */
    public MCUControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings, final int numMCUDevices)
    {
        super (factory, host, globalSettings, documentSettings);

        this.numMCUDevices = numMCUDevices;

        Arrays.fill (this.vuValues, -1);
        Arrays.fill (this.faderValues, -1);
        Arrays.fill (this.masterVuValues, -1);

        this.colorManager = new ColorManager ();
        this.valueChanger = new Relative2ValueChanger (16241 + 1, 100, 10);
        this.configuration = new MCUConfiguration (host, this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        this.updateMode (this.getSurface ().getModeManager ().getActiveOrTempModeId ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final int adjustedNum = 8 * this.numMCUDevices;

        final ModelSetup ms = new ModelSetup ();
        ms.setNumTracks (adjustedNum);
        ms.setNumScenes (0);
        ms.setNumFilterColumnEntries (8);
        ms.setNumResults (8);
        ms.setNumParams (adjustedNum);
        ms.setNumDeviceLayers (0);
        ms.setNumDrumPadLayers (0);
        ms.setNumMarkers (adjustedNum);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));

        this.model.getMasterTrack ().addSelectionObserver ( (index, isSelected) -> {
            final ModeManager modeManager = this.getSurface ().getModeManager ();
            if (isSelected)
                modeManager.setActiveMode (Modes.MASTER);
            else
                modeManager.restoreMode ();
        });
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();

        for (int i = 0; i < this.numMCUDevices; i++)
        {
            final IMidiOutput output = midiAccess.createOutput (i);
            final IMidiInput input = midiAccess.createInput (i, null);
            final MCUControlSurface surface = new MCUControlSurface (this.surfaces, this.host, this.colorManager, this.configuration, output, input, 8 * (this.numMCUDevices - i - 1), i == 0);
            this.surfaces.add (surface);
            surface.setDisplay (new MCUDisplay (this.host, output, true, false));
            surface.setSecondDisplay (new MCUDisplay (this.host, output, false, i == 0));
            surface.setSegmentDisplay (new MCUSegmentDisplay (output));
            surface.getModeManager ().setDefaultMode (Modes.VOLUME);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        for (int index = 0; index < this.numMCUDevices; index++)
        {
            final MCUControlSurface surface = this.getSurface (index);
            final ModeManager modeManager = surface.getModeManager ();

            modeManager.registerMode (Modes.TRACK, new TrackMode (surface, this.model));
            modeManager.registerMode (Modes.VOLUME, new VolumeMode (surface, this.model));
            modeManager.registerMode (Modes.PAN, new PanMode (surface, this.model));
            final SendMode modeSend = new SendMode (surface, this.model);
            for (int i = 0; i < 8; i++)
                modeManager.registerMode (Modes.get (Modes.SEND1, i), modeSend);
            modeManager.registerMode (Modes.MASTER, new MasterMode (surface, this.model, false));

            modeManager.registerMode (Modes.DEVICE_PARAMS, new DeviceParamsMode (surface, this.model));
            modeManager.registerMode (Modes.BROWSER, new DeviceBrowserMode (surface, this.model));
            modeManager.registerMode (Modes.MARKERS, new MarkerMode (surface, this.model));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        for (int index = 0; index < this.numMCUDevices; index++)
        {
            final MCUControlSurface surface = this.getSurface (index);
            surface.getModeManager ().addModeListener ( (oldMode, newMode) -> {

                for (int d = 0; d < this.numMCUDevices; d++)
                {
                    final MCUControlSurface s = this.getSurface (d);
                    if (!s.equals (surface))
                        s.getModeManager ().setActiveMode (newMode);
                }

                this.updateMode (null);
                this.updateMode (newMode);
            });
        }

        this.configuration.addSettingObserver (AbstractConfiguration.ENABLE_VU_METERS, () -> {
            for (int index = 0; index < this.numMCUDevices; index++)
            {
                final MCUControlSurface surface = this.getSurface (index);
                surface.switchVuMode (this.configuration.isEnableVUMeters () ? MCUControlSurface.VUMODE_LED_AND_LCD : MCUControlSurface.VUMODE_OFF);
                final Mode activeMode = surface.getModeManager ().getActiveOrTempMode ();
                if (activeMode != null)
                    activeMode.updateDisplay ();
                ((MCUDisplay) surface.getDisplay ()).forceFlush ();
            }
        });
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        for (int index = 0; index < this.numMCUDevices; index++)
        {
            final MCUControlSurface surface = this.getSurface (index);
            surface.getViewManager ().registerView (Views.CONTROL, new ControlOnlyView<> (surface, this.model));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        // Assignments to the main device
        final MCUControlSurface surface = this.getSurface ();

        // Footswitches
        this.addTriggerCommand (TriggerCommandID.FOOTSWITCH1, MCUControlSurface.MCU_USER_A, new AssignableCommand (0, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.FOOTSWITCH2, MCUControlSurface.MCU_USER_B, new AssignableCommand (1, this.model, surface));

        // Navigation
        this.addTriggerCommand (TriggerCommandID.REWIND, MCUControlSurface.MCU_REWIND, new WindCommand<> (this.model, surface, false));
        this.addTriggerCommand (TriggerCommandID.FORWARD, MCUControlSurface.MCU_FORWARD, new WindCommand<> (this.model, surface, true));
        this.addTriggerCommand (TriggerCommandID.LOOP, MCUControlSurface.MCU_REPEAT, new ToggleLoopCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.STOP, MCUControlSurface.MCU_STOP, new StopCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.PLAY, MCUControlSurface.MCU_PLAY, new PlayCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.RECORD, MCUControlSurface.MCU_RECORD, new MCURecordCommand (this.model, surface));

        this.addTriggerCommand (TriggerCommandID.SCRUB, MCUControlSurface.MCU_SCRUB, new ScrubCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_LEFT, MCUControlSurface.MCU_ARROW_LEFT, new MCUCursorCommand (Direction.LEFT, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_RIGHT, MCUControlSurface.MCU_ARROW_RIGHT, new MCUCursorCommand (Direction.RIGHT, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_UP, MCUControlSurface.MCU_ARROW_UP, new MCUCursorCommand (Direction.UP, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_DOWN, MCUControlSurface.MCU_ARROW_DOWN, new MCUCursorCommand (Direction.DOWN, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ZOOM, MCUControlSurface.MCU_ZOOM, new ZoomCommand (this.model, surface));

        // Display Mode
        this.addTriggerCommand (TriggerCommandID.TOGGLE_DISPLAY, MCUControlSurface.MCU_NAME_VALUE, new ToggleDisplayCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.TEMPO_TICKS, MCUControlSurface.MCU_SMPTE_BEATS, new TempoTicksCommand (this.model, surface));

        // Functions
        this.addTriggerCommand (TriggerCommandID.SHIFT, MCUControlSurface.MCU_SHIFT, new ShiftCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SELECT, MCUControlSurface.MCU_OPTION, NopCommand.INSTANCE);
        this.addTriggerCommand (TriggerCommandID.PUNCH_IN, MCUControlSurface.MCU_F6, new PunchInCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.PUNCH_OUT, MCUControlSurface.MCU_F7, new PunchOutCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.F1, MCUControlSurface.MCU_F1, new AssignableCommand (2, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.F2, MCUControlSurface.MCU_F2, new AssignableCommand (3, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.F3, MCUControlSurface.MCU_F3, new AssignableCommand (4, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.F4, MCUControlSurface.MCU_F4, new AssignableCommand (5, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.F5, MCUControlSurface.MCU_F5, new AssignableCommand (6, this.model, surface));

        // Assignment
        this.addTriggerCommand (TriggerCommandID.TRACK, MCUControlSurface.MCU_MODE_IO, new TracksCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.PAN_SEND, MCUControlSurface.MCU_MODE_PAN, new ModeSelectCommand<> (this.model, surface, Modes.PAN));
        this.addTriggerCommand (TriggerCommandID.SENDS, MCUControlSurface.MCU_MODE_SENDS, new SendSelectCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.DEVICE, MCUControlSurface.MCU_MODE_PLUGIN, new DevicesCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.MOVE_TRACK_LEFT, MCUControlSurface.MCU_MODE_EQ, new MoveTrackBankCommand<> (this.model, surface, Modes.DEVICE_PARAMS, true, true));
        this.addTriggerCommand (TriggerCommandID.MOVE_TRACK_RIGHT, MCUControlSurface.MCU_MODE_DYN, new MoveTrackBankCommand<> (this.model, surface, Modes.DEVICE_PARAMS, true, false));

        // Automation
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_READ, MCUControlSurface.MCU_READ, new AutomationCommand<> (0, this.model, surface));
        final AutomationCommand<MCUControlSurface, MCUConfiguration> writeCommand = new AutomationCommand<> (1, this.model, surface);
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_WRITE, MCUControlSurface.MCU_WRITE, writeCommand);
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_WRITE, MCUControlSurface.MCU_GROUP, writeCommand);
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_TRIM, MCUControlSurface.MCU_TRIM, new AutomationCommand<> (2, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_TOUCH, MCUControlSurface.MCU_TOUCH, new AutomationCommand<> (3, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_LATCH, MCUControlSurface.MCU_LATCH, new AutomationCommand<> (4, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.UNDO, MCUControlSurface.MCU_UNDO, new UndoCommand<> (this.model, surface));

        // Panes
        this.addTriggerCommand (TriggerCommandID.NOTE_EDITOR, MCUControlSurface.MCU_MIDI_TRACKS, new PaneCommand<> (PaneCommand.Panels.NOTE, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.AUTOMATION_EDITOR, MCUControlSurface.MCU_INPUTS, new PaneCommand<> (PaneCommand.Panels.AUTOMATION, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.TOGGLE_DEVICE, MCUControlSurface.MCU_AUDIO_TRACKS, new PaneCommand<> (PaneCommand.Panels.DEVICE, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.MIXER, MCUControlSurface.MCU_AUDIO_INSTR, new PaneCommand<> (PaneCommand.Panels.MIXER, this.model, surface));

        // Layouts
        this.addTriggerCommand (TriggerCommandID.LAYOUT_ARRANGE, MCUControlSurface.MCU_AUX, new LayoutCommand<> (IApplication.PANEL_LAYOUT_ARRANGE, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.LAYOUT_MIX, MCUControlSurface.MCU_BUSSES, new LayoutCommand<> (IApplication.PANEL_LAYOUT_MIX, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.LAYOUT_EDIT, MCUControlSurface.MCU_OUTPUTS, new LayoutCommand<> (IApplication.PANEL_LAYOUT_EDIT, this.model, surface));

        // Utilities
        this.addTriggerCommand (TriggerCommandID.BROWSE, MCUControlSurface.MCU_USER, new BrowserCommand<> (Modes.BROWSER, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.METRONOME, MCUControlSurface.MCU_CLICK, new MetronomeCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.GROOVE, MCUControlSurface.MCU_SOLO, new GrooveCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.OVERDUB, MCUControlSurface.MCU_REPLACE, new OverdubCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.TAP_TEMPO, MCUControlSurface.MCU_NUDGE, new TapTempoCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.DUPLICATE, MCUControlSurface.MCU_DROP, new DuplicateCommand<> (this.model, surface));

        this.addTriggerCommand (TriggerCommandID.DEVICE_ON_OFF, MCUControlSurface.MCU_F8, new DeviceOnOffCommand<> (this.model, surface));

        // Currently not used but prevent error in console
        this.addTriggerCommand (TriggerCommandID.CONTROL, MCUControlSurface.MCU_CONTROL, NopCommand.INSTANCE);
        this.addTriggerCommand (TriggerCommandID.ALT, MCUControlSurface.MCU_ALT, NopCommand.INSTANCE);

        // Fader Controls
        this.addTriggerCommand (TriggerCommandID.FLIP, MCUControlSurface.MCU_FLIP, new ToggleTrackBanksCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.CANCEL, MCUControlSurface.MCU_CANCEL, new KeyCommand (Key.ESCAPE, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ENTER, MCUControlSurface.MCU_ENTER, new KeyCommand (Key.ENTER, this.model, surface));

        this.addTriggerCommand (TriggerCommandID.MOVE_BANK_LEFT, MCUControlSurface.MCU_BANK_LEFT, new MoveTrackBankCommand<> (this.model, surface, Modes.DEVICE_PARAMS, false, true));
        this.addTriggerCommand (TriggerCommandID.MOVE_BANK_RIGHT, MCUControlSurface.MCU_BANK_RIGHT, new MoveTrackBankCommand<> (this.model, surface, Modes.DEVICE_PARAMS, false, false));
        this.addTriggerCommand (TriggerCommandID.MOVE_TRACK_LEFT, MCUControlSurface.MCU_TRACK_LEFT, new MoveTrackBankCommand<> (this.model, surface, Modes.DEVICE_PARAMS, true, true));
        this.addTriggerCommand (TriggerCommandID.MOVE_TRACK_RIGHT, MCUControlSurface.MCU_TRACK_RIGHT, new MoveTrackBankCommand<> (this.model, surface, Modes.DEVICE_PARAMS, true, false));

        // Additional commands for footcontrollers
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerTriggerCommand (TriggerCommandID.NEW, new NewCommand<> (this.model, surface));
        viewManager.registerTriggerCommand (TriggerCommandID.TAP_TEMPO, new TapTempoCommand<> (this.model, surface));

        // Only MCU
        this.addTriggerCommand (TriggerCommandID.SAVE, MCUControlSurface.MCU_SAVE, new SaveCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.MARKER, MCUControlSurface.MCU_MARKER, new MarkerCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.TOGGLE_VU, MCUControlSurface.MCU_EDIT, new ToggleVUCommand<> (this.model, surface));

        this.addTriggerCommand (TriggerCommandID.MASTERTRACK, MCUControlSurface.MCU_FADER_MASTER, new SelectCommand (8, this.model, surface));

        this.addTriggerCommand (TriggerCommandID.LED_1, MCUControlSurface.MCU_SMPTE_LED, NopCommand.INSTANCE);
        this.addTriggerCommand (TriggerCommandID.LED_2, MCUControlSurface.MCU_BEATS_LED, NopCommand.INSTANCE);

        this.registerTriggerCommandsToAllDevices ();
    }


    /**
     * Common track editing - Assignment to all devices
     */
    protected void registerTriggerCommandsToAllDevices ()
    {
        for (int index = 0; index < this.numMCUDevices; index++)
        {
            final MCUControlSurface surface = this.getSurface (index);
            final ViewManager viewManager = surface.getViewManager ();
            for (int i = 0; i < 8; i++)
            {
                TriggerCommandID commandID = TriggerCommandID.get (TriggerCommandID.ROW_SELECT_1, i);
                viewManager.registerTriggerCommand (commandID, new SelectCommand (i, this.model, surface));
                surface.assignTriggerCommand (MCUControlSurface.MCU_SELECT1 + i, commandID);

                commandID = TriggerCommandID.get (TriggerCommandID.FADER_TOUCH_1, i);
                viewManager.registerTriggerCommand (commandID, new FaderTouchCommand (i, this.model, surface));
                surface.assignTriggerCommand (MCUControlSurface.MCU_FADER_TOUCH1 + i, commandID);

                this.addTriggerCommand (TriggerCommandID.get (TriggerCommandID.ROW1_1, i), MCUControlSurface.MCU_VSELECT1 + i, new ButtonRowModeCommand<> (0, i, this.model, surface), index);
                this.addTriggerCommand (TriggerCommandID.get (TriggerCommandID.ROW2_1, i), MCUControlSurface.MCU_ARM1 + i, new ButtonRowModeCommand<> (1, i, this.model, surface), index);
                this.addTriggerCommand (TriggerCommandID.get (TriggerCommandID.ROW3_1, i), MCUControlSurface.MCU_SOLO1 + i, new ButtonRowModeCommand<> (2, i, this.model, surface), index);
                this.addTriggerCommand (TriggerCommandID.get (TriggerCommandID.ROW4_1, i), MCUControlSurface.MCU_MUTE1 + i, new ButtonRowModeCommand<> (3, i, this.model, surface), index);
            }

            viewManager.registerPitchbendCommand (new PitchbendVolumeCommand (this.model, surface));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        MCUControlSurface surface = this.getSurface ();
        ViewManager viewManager = surface.getViewManager ();
        viewManager.registerContinuousCommand (ContinuousCommandID.PLAY_POSITION, new PlayPositionTempoCommand (this.model, surface));
        surface.assignContinuousCommand (1, MCUControlSurface.MCU_CC_JOG, ContinuousCommandID.PLAY_POSITION);

        for (int index = 0; index < this.numMCUDevices; index++)
        {
            surface = this.getSurface (index);
            viewManager = surface.getViewManager ();
            for (int i = 0; i < 8; i++)
            {
                final ContinuousCommandID commandID = ContinuousCommandID.get (ContinuousCommandID.KNOB1, i);
                viewManager.registerContinuousCommand (commandID, new KnobRowModeCommand<> (i, this.model, surface));
                surface.assignContinuousCommand (1, MCUControlSurface.MCU_CC_VPOT1 + i, commandID);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        for (int index = 0; index < this.numMCUDevices; index++)
        {
            final MCUControlSurface surface = this.getSurface (index);
            surface.switchVuMode (MCUControlSurface.VUMODE_LED);

            surface.getViewManager ().setActiveView (Views.CONTROL);
            surface.getModeManager ().setActiveMode (Modes.PAN);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void updateButtons ()
    {
        final MCUControlSurface surface = this.getSurface ();
        final Modes mode = surface.getModeManager ().getActiveOrTempModeId ();
        if (mode == null)
            return;

        final boolean isShift = surface.isShiftPressed ();

        this.updateVUandFaders (isShift);
        this.updateSegmentDisplay ();

        // Set button states
        final ITransport t = this.model.getTransport ();
        final boolean isFlipRecord = this.configuration.isFlipRecord ();
        final boolean isRecordShifted = isShift && !isFlipRecord || !isShift && isFlipRecord;

        final boolean isTrackOn = Modes.TRACK.equals (mode) || Modes.VOLUME.equals (mode);
        final boolean isPanOn = Modes.PAN.equals (mode);
        final boolean isSendOn = mode.ordinal () >= Modes.SEND1.ordinal () && mode.ordinal () <= Modes.SEND8.ordinal ();
        final boolean isDeviceOn = Modes.DEVICE_PARAMS.equals (mode);

        final boolean isLEDOn = surface.isPressed (MCUControlSurface.MCU_OPTION) ? this.model.isCursorTrackPinned () : isTrackOn;
        surface.updateTrigger (MCUControlSurface.MCU_MODE_IO, isLEDOn ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_MODE_PAN, isPanOn ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_MODE_SENDS, isSendOn ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final boolean isOn = surface.isPressed (MCUControlSurface.MCU_OPTION) ? cursorDevice.isPinned () : isDeviceOn;

        surface.updateTrigger (MCUControlSurface.MCU_MODE_PLUGIN, isOn ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_USER, Modes.BROWSER.equals (mode) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        final String automationWriteMode = t.getAutomationWriteMode ();
        final boolean writingArrangerAutomation = t.isWritingArrangerAutomation ();

        surface.updateTrigger (MCUControlSurface.MCU_F6, t.isPunchInEnabled () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_F7, t.isPunchOutEnabled () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        surface.updateTrigger (MCUControlSurface.MCU_READ, !writingArrangerAutomation ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        final int writeState = writingArrangerAutomation && TransportConstants.AUTOMATION_MODES_VALUES[2].equals (automationWriteMode) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF;
        surface.updateTrigger (MCUControlSurface.MCU_WRITE, writeState);
        surface.updateTrigger (MCUControlSurface.MCU_GROUP, writeState);
        surface.updateTrigger (MCUControlSurface.MCU_TRIM, t.isWritingClipLauncherAutomation () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_TOUCH, writingArrangerAutomation && TransportConstants.AUTOMATION_MODES_VALUES[1].equals (automationWriteMode) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_LATCH, writingArrangerAutomation && TransportConstants.AUTOMATION_MODES_VALUES[0].equals (automationWriteMode) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        final View view = surface.getViewManager ().getView (Views.CONTROL);
        surface.updateTrigger (MCUControlSurface.MCU_REWIND, ((WindCommand<?, ?>) view.getTriggerCommand (TriggerCommandID.REWIND)).isRewinding () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_FORWARD, ((WindCommand<?, ?>) view.getTriggerCommand (TriggerCommandID.FORWARD)).isForwarding () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_REPEAT, t.isLoop () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_STOP, !t.isPlaying () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_PLAY, t.isPlaying () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_RECORD, isRecordShifted ? t.isLauncherOverdub () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF : t.isRecording () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        surface.updateTrigger (MCUControlSurface.MCU_NAME_VALUE, surface.getConfiguration ().isDisplayTrackNames () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_ZOOM, surface.getConfiguration ().isZoomState () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_SCRUB, surface.getModeManager ().isActiveOrTempMode (Modes.DEVICE_PARAMS) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        surface.updateTrigger (MCUControlSurface.MCU_MIDI_TRACKS, MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_INPUTS, MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_AUDIO_TRACKS, surface.isShiftPressed () && cursorDevice.isWindowOpen () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_AUDIO_INSTR, MCU_BUTTON_STATE_OFF);

        surface.updateTrigger (MCUControlSurface.MCU_CLICK, (isShift ? t.isMetronomeTicksOn () : t.isMetronomeOn ()) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_SOLO, this.model.getGroove ().getParameters ()[0].getValue () > 0 ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_REPLACE, (isShift ? t.isLauncherOverdub () : t.isArrangerOverdub ()) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_FLIP, this.model.isEffectTrackBankActive () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        final boolean displayTicks = this.configuration.isDisplayTicks ();
        surface.updateTrigger (MCUControlSurface.MCU_SMPTE_BEATS, displayTicks ? MCU_BUTTON_STATE_OFF : MCU_BUTTON_STATE_ON);
        surface.updateTrigger (MCUControlSurface.MCU_SMPTE_LED, displayTicks ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateTrigger (MCUControlSurface.MCU_BEATS_LED, displayTicks ? MCU_BUTTON_STATE_OFF : MCU_BUTTON_STATE_ON);

        surface.updateTrigger (MCUControlSurface.MCU_MARKER, this.model.getArranger ().areCueMarkersVisible () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
    }


    private void updateSegmentDisplay ()
    {
        if (!this.configuration.hasSegmentDisplay ())
            return;

        final ITransport t = this.model.getTransport ();
        String positionText = t.getPositionText ();
        if (this.configuration.isDisplayTicks ())
            positionText += " ";
        else
        {
            String tempoStr = t.formatTempoNoFraction (t.getTempo ());
            final int pos = positionText.lastIndexOf (':');
            if (tempoStr.length () < 3)
                tempoStr = "0" + tempoStr;
            positionText = positionText.substring (0, pos + 1) + tempoStr;
        }

        this.getSurface ().getSegmentDisplay ().setTransportPositionDisplay (positionText);
    }


    private void updateVUandFaders (final boolean isShiftPressed)
    {
        final double upperBound = this.valueChanger.getUpperBound ();
        final boolean enableVUMeters = this.configuration.isEnableVUMeters ();
        final boolean hasMotorFaders = this.configuration.hasMotorFaders ();

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        IMidiOutput output;
        for (int index = 0; index < this.numMCUDevices; index++)
        {
            final MCUControlSurface surface = this.getSurface (index);
            output = surface.getOutput ();
            final int extenderOffset = surface.getExtenderOffset ();
            for (int i = 0; i < 8; i++)
            {
                final int channel = extenderOffset + i;
                final ITrack track = tb.getItem (channel);

                // Update VU LEDs of channel
                if (enableVUMeters)
                {
                    final int vu = track.getVu ();
                    if (vu != this.vuValues[channel])
                    {
                        this.vuValues[channel] = vu;
                        final int scaledValue = (int) Math.round (vu * 12 / upperBound);
                        output.sendChannelAftertouch (0x10 * i + scaledValue, 0);
                    }
                }

                // Update motor fader of channel
                if (hasMotorFaders)
                    this.updateFaders (output, i, channel, track);
            }
        }

        final IMasterTrack masterTrack = this.model.getMasterTrack ();

        final MCUControlSurface surface = this.getSurface ();
        output = surface.getOutput ();

        // Stereo VU of master channel
        if (enableVUMeters)
        {
            int vu = masterTrack.getVuLeft ();
            if (vu != this.masterVuValues[0])
            {
                this.masterVuValues[0] = vu;
                final int scaledValue = (int) Math.round (vu * 12 / upperBound);
                output.sendChannelAftertouch (1, scaledValue, 0);
            }

            vu = masterTrack.getVuRight ();
            if (vu != this.masterVuValues[1])
            {
                this.masterVuValues[1] = vu;
                final int scaledValue = (int) Math.round (vu * 12 / upperBound);
                output.sendChannelAftertouch (1, 0x10 + scaledValue, 0);
            }
        }

        // Update motor fader of master channel
        if (hasMotorFaders)
        {
            final int volume = isShiftPressed ? this.model.getTransport ().getMetronomeVolume () : masterTrack.getVolume ();
            if (volume != this.masterFaderValue)
            {
                this.masterFaderValue = volume;
                output.sendPitchbend (8, volume % 127, volume / 127);
            }
        }
    }


    private void updateFaders (final IMidiOutput output, final int index, final int channel, final ITrack track)
    {
        int value = track.getVolume ();

        if (this.configuration.useFadersAsKnobs ())
        {
            final ModeManager modeManager = this.getSurface ().getModeManager ();
            if (modeManager.isActiveOrTempMode (Modes.VOLUME))
                value = track.getVolume ();
            else if (modeManager.isActiveOrTempMode (Modes.PAN))
                value = track.getPan ();
            else if (modeManager.isActiveOrTempMode (Modes.TRACK))
            {
                final ITrack selectedTrack = this.model.getSelectedTrack ();
                if (selectedTrack == null)
                    value = 0;
                else
                {
                    switch (index)
                    {
                        case 0:
                            value = selectedTrack.getVolume ();
                            break;
                        case 1:
                            value = selectedTrack.getPan ();
                            break;
                        default:
                            final boolean effectTrackBankActive = this.model.isEffectTrackBankActive ();
                            if (index == 2)
                            {
                                if (this.configuration.isDisplayCrossfader ())
                                {
                                    final int crossfadeMode = selectedTrack.getCrossfadeModeAsNumber ();
                                    value = crossfadeMode == 2 ? this.valueChanger.getUpperBound () : crossfadeMode == 1 ? this.valueChanger.getUpperBound () / 2 : 0;
                                }
                                else if (!effectTrackBankActive)
                                    value = selectedTrack.getSendBank ().getItem (0).getValue ();
                            }
                            else if (!effectTrackBankActive)
                                value = selectedTrack.getSendBank ().getItem (index - (this.configuration.isDisplayCrossfader () ? 3 : 2)).getValue ();
                            break;
                    }
                }
            }
            else if (modeManager.isActiveOrTempMode (Modes.SEND1))
                value = track.getSendBank ().getItem (0).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.SEND2))
                value = track.getSendBank ().getItem (1).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.SEND3))
                value = track.getSendBank ().getItem (2).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.SEND4))
                value = track.getSendBank ().getItem (3).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.SEND5))
                value = track.getSendBank ().getItem (4).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.SEND6))
                value = track.getSendBank ().getItem (5).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.SEND7))
                value = track.getSendBank ().getItem (6).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.SEND8))
                value = track.getSendBank ().getItem (7).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.DEVICE_PARAMS))
                value = this.model.getCursorDevice ().getParameterBank ().getItem (channel).getValue ();
        }

        if (value != this.faderValues[channel])
        {
            this.faderValues[channel] = value;
            output.sendPitchbend (index, value % 127, value / 127);
        }
    }


    private void updateMode (final Modes mode)
    {
        if (mode == null)
            return;

        this.updateIndication (mode);
        if (this.configuration.hasAssignmentDisplay ())
            this.getSurface ().getSegmentDisplay ().setAssignmentDisplay (MODE_ACRONYMS.get (mode));
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
        final boolean isPan = Modes.PAN.equals (mode);
        final boolean isTrack = Modes.TRACK.equals (mode);
        final boolean isVolume = Modes.VOLUME.equals (mode);
        final boolean isDevice = Modes.DEVICE_PARAMS.equals (mode);

        tb.setIndication (!isEffect);
        if (tbe != null)
            tbe.setIndication (isEffect);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final ITrack selectedTrack = tb.getSelectedItem ();
        for (int i = 0; i < tb.getPageSize (); i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i && isTrack;
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (!isEffect && (isVolume || hasTrackSel));
            track.setPanIndication (!isEffect && (isPan || hasTrackSel));

            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < sendBank.getPageSize (); j++)
                sendBank.getItem (j).setIndication (!isEffect && (mode.ordinal () - Modes.SEND1.ordinal () == j || hasTrackSel));

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
        if (modeManager.isActiveOrTempMode (Modes.MASTER))
            modeManager.setActiveMode (Modes.TRACK);
    }
}
