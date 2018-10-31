// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu;

import de.mossgrabers.controller.mcu.command.continuous.PlayPositionTempoCommand;
import de.mossgrabers.controller.mcu.command.pitchbend.PitchbendVolumeCommand;
import de.mossgrabers.controller.mcu.command.trigger.AssignableCommand;
import de.mossgrabers.controller.mcu.command.trigger.AutomationCommand;
import de.mossgrabers.controller.mcu.command.trigger.CursorCommand;
import de.mossgrabers.controller.mcu.command.trigger.CursorCommand.Direction;
import de.mossgrabers.controller.mcu.command.trigger.DevicesCommand;
import de.mossgrabers.controller.mcu.command.trigger.FaderTouchCommand;
import de.mossgrabers.controller.mcu.command.trigger.GrooveCommand;
import de.mossgrabers.controller.mcu.command.trigger.KeyCommand;
import de.mossgrabers.controller.mcu.command.trigger.KeyCommand.Key;
import de.mossgrabers.controller.mcu.command.trigger.MCURecordCommand;
import de.mossgrabers.controller.mcu.command.trigger.OverdubCommand;
import de.mossgrabers.controller.mcu.command.trigger.ScrubCommand;
import de.mossgrabers.controller.mcu.command.trigger.SelectCommand;
import de.mossgrabers.controller.mcu.command.trigger.SendSelectCommand;
import de.mossgrabers.controller.mcu.command.trigger.ShiftCommand;
import de.mossgrabers.controller.mcu.command.trigger.TempoTicksCommand;
import de.mossgrabers.controller.mcu.command.trigger.ToggleDisplayCommand;
import de.mossgrabers.controller.mcu.command.trigger.TracksCommand;
import de.mossgrabers.controller.mcu.command.trigger.ZoomCommand;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mcu.controller.MCUDisplay;
import de.mossgrabers.controller.mcu.controller.MCUSegmentDisplay;
import de.mossgrabers.controller.mcu.mode.MarkerMode;
import de.mossgrabers.controller.mcu.mode.Modes;
import de.mossgrabers.controller.mcu.mode.device.DeviceBrowserMode;
import de.mossgrabers.controller.mcu.mode.device.DeviceParamsMode;
import de.mossgrabers.controller.mcu.mode.track.MasterMode;
import de.mossgrabers.controller.mcu.mode.track.PanMode;
import de.mossgrabers.controller.mcu.mode.track.SendMode;
import de.mossgrabers.controller.mcu.mode.track.TrackMode;
import de.mossgrabers.controller.mcu.mode.track.VolumeMode;
import de.mossgrabers.controller.mcu.view.ControlView;
import de.mossgrabers.controller.mcu.view.Views;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.command.trigger.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.DuplicateCommand;
import de.mossgrabers.framework.command.trigger.MarkerCommand;
import de.mossgrabers.framework.command.trigger.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.MoveTrackBankCommand;
import de.mossgrabers.framework.command.trigger.NopCommand;
import de.mossgrabers.framework.command.trigger.application.LayoutCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand;
import de.mossgrabers.framework.command.trigger.application.SaveCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.device.DeviceOnOffCommand;
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
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Support for the Mackie MCU protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUControllerSetup extends AbstractControllerSetup<MCUControlSurface, MCUConfiguration>
{
    /** State for button LED on. */
    public static final int                   MCU_BUTTON_STATE_ON       = 127;
    /** State for button LED off. */
    public static final int                   MCU_BUTTON_STATE_OFF      = 0;

    private static final Integer              COMMAND_NOTE_EDITOR       = Integer.valueOf (150);
    private static final Integer              COMMAND_AUTOMATION_EDITOR = Integer.valueOf (151);
    private static final Integer              COMMAND_TOGGLE_DEVICE     = Integer.valueOf (152);
    private static final Integer              COMMAND_MIXER             = Integer.valueOf (153);
    private static final Integer              COMMAND_TEMPO_TICKS       = Integer.valueOf (154);
    private static final Integer              COMMAND_ENTER             = Integer.valueOf (155);
    private static final Integer              COMMAND_CANCEL            = Integer.valueOf (156);
    private static final Integer              COMMAND_FLIP              = Integer.valueOf (157);
    private static final Integer              COMMAND_GROOVE            = Integer.valueOf (158);
    private static final Integer              COMMAND_OVERDUB           = Integer.valueOf (159);
    private static final Integer              COMMAND_SCRUB             = Integer.valueOf (160);
    private static final Integer              COMMAND_FOOTSWITCH1       = Integer.valueOf (161);
    private static final Integer              COMMAND_FOOTSWITCH2       = Integer.valueOf (162);
    private static final Integer              COMMAND_F1                = Integer.valueOf (163);
    private static final Integer              COMMAND_F2                = Integer.valueOf (164);
    private static final Integer              COMMAND_F3                = Integer.valueOf (165);
    private static final Integer              COMMAND_F4                = Integer.valueOf (166);
    private static final Integer              COMMAND_F5                = Integer.valueOf (167);
    private static final Integer              COMMAND_TOGGLE_DISPLAY    = Integer.valueOf (168);
    private static final Integer              COMMAND_LAYOUT_ARRANGE    = Integer.valueOf (169);
    private static final Integer              COMMAND_LAYOUT_MIX        = Integer.valueOf (170);
    private static final Integer              COMMAND_LAYOUT_EDIT       = Integer.valueOf (171);
    private static final Integer              COMMAND_CONTROL           = Integer.valueOf (172);
    private static final Integer              COMMAND_ALT               = Integer.valueOf (173);

    private static final Map<Integer, String> MODE_ACRONYMS             = new HashMap<> ();

    static
    {
        MODE_ACRONYMS.put (Modes.MODE_TRACK, "TR");
        MODE_ACRONYMS.put (Modes.MODE_VOLUME, "VL");
        MODE_ACRONYMS.put (Modes.MODE_PAN, "PN");
        MODE_ACRONYMS.put (Modes.MODE_SEND1, "S1");
        MODE_ACRONYMS.put (Modes.MODE_SEND2, "S2");
        MODE_ACRONYMS.put (Modes.MODE_SEND3, "S3");
        MODE_ACRONYMS.put (Modes.MODE_SEND4, "S4");
        MODE_ACRONYMS.put (Modes.MODE_SEND5, "S5");
        MODE_ACRONYMS.put (Modes.MODE_SEND6, "S6");
        MODE_ACRONYMS.put (Modes.MODE_SEND7, "S7");
        MODE_ACRONYMS.put (Modes.MODE_SEND8, "S8");
        MODE_ACRONYMS.put (Modes.MODE_MASTER, "MT");
        MODE_ACRONYMS.put (Modes.MODE_DEVICE_PARAMS, "DC");
        MODE_ACRONYMS.put (Modes.MODE_BROWSER, "BR");
        MODE_ACRONYMS.put (Modes.MODE_MARKER, "MK");

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
     * @param settings The settings
     * @param numMCUDevices The number of MCU devices (main device + extenders) to support
     */
    public MCUControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings, final int numMCUDevices)
    {
        super (factory, host, settings);

        this.numMCUDevices = numMCUDevices;

        Arrays.fill (this.vuValues, -1);
        Arrays.fill (this.faderValues, -1);
        Arrays.fill (this.masterVuValues, -1);

        this.colorManager = new ColorManager ();
        this.valueChanger = new Relative2ValueChanger (16241 + 1, 100, 10);
        this.configuration = new MCUConfiguration (this.valueChanger);
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
        final int adjustedNum = 8 * this.numMCUDevices;

        final ModelSetup ms = new ModelSetup ();
        ms.setNumTracks (adjustedNum);
        ms.setNumFilterColumnEntries (8);
        ms.setNumResults (8);
        ms.setNumParams (adjustedNum);
        ms.setNumDeviceLayers (0);
        ms.setNumDrumPadLayers (0);
        ms.setNumMarkers (adjustedNum);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addSelectionObserver (this::handleTrackChange);

        this.model.getMasterTrack ().addSelectionObserver ( (index, isSelected) -> {
            final ModeManager modeManager = this.getSurface ().getModeManager ();
            if (isSelected)
                modeManager.setActiveMode (Modes.MODE_MASTER);
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
            final MCUControlSurface surface = new MCUControlSurface (this.surfaces, this.model.getHost (), this.colorManager, this.configuration, output, input, 8 * (this.numMCUDevices - i - 1), i == 0);
            this.surfaces.add (surface);
            surface.setDisplay (new MCUDisplay (this.host, output, true, false));
            surface.setSecondDisplay (new MCUDisplay (this.host, output, false, i == 0));
            surface.setSegmentDisplay (new MCUSegmentDisplay (output));
            surface.getModeManager ().setDefaultMode (Modes.MODE_VOLUME);
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

            modeManager.registerMode (Modes.MODE_TRACK, new TrackMode (surface, this.model));
            modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode (surface, this.model));
            modeManager.registerMode (Modes.MODE_PAN, new PanMode (surface, this.model));
            final SendMode modeSend = new SendMode (surface, this.model);
            for (int i = 0; i < 8; i++)
                modeManager.registerMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + i), modeSend);
            modeManager.registerMode (Modes.MODE_MASTER, new MasterMode (surface, this.model, false));

            modeManager.registerMode (Modes.MODE_DEVICE_PARAMS, new DeviceParamsMode (surface, this.model));
            modeManager.registerMode (Modes.MODE_BROWSER, new DeviceBrowserMode (surface, this.model));
            modeManager.registerMode (Modes.MODE_MARKER, new MarkerMode (surface, this.model));
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
            final ViewManager viewManager = surface.getViewManager ();
            viewManager.registerView (Views.VIEW_CONTROL, new ControlView (surface, this.model));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        // Assignments to the main device
        final MCUControlSurface surface = this.getSurface ();

        // Footswitches
        this.addTriggerCommand (COMMAND_FOOTSWITCH1, MCUControlSurface.MCU_USER_A, new AssignableCommand (0, this.model, surface));
        this.addTriggerCommand (COMMAND_FOOTSWITCH2, MCUControlSurface.MCU_USER_B, new AssignableCommand (1, this.model, surface));

        // Navigation
        this.addTriggerCommand (Commands.COMMAND_REWIND, MCUControlSurface.MCU_REWIND, new WindCommand<> (this.model, surface, false));
        this.addTriggerCommand (Commands.COMMAND_FORWARD, MCUControlSurface.MCU_FORWARD, new WindCommand<> (this.model, surface, true));
        this.addTriggerCommand (Commands.COMMAND_LOOP, MCUControlSurface.MCU_REPEAT, new ToggleLoopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_STOP, MCUControlSurface.MCU_STOP, new StopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_PLAY, MCUControlSurface.MCU_PLAY, new PlayCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_RECORD, MCUControlSurface.MCU_RECORD, new MCURecordCommand (this.model, surface));

        this.addTriggerCommand (COMMAND_SCRUB, MCUControlSurface.MCU_SCRUB, new ScrubCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_LEFT, MCUControlSurface.MCU_ARROW_LEFT, new CursorCommand (Direction.LEFT, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_RIGHT, MCUControlSurface.MCU_ARROW_RIGHT, new CursorCommand (Direction.RIGHT, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_UP, MCUControlSurface.MCU_ARROW_UP, new CursorCommand (Direction.UP, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_DOWN, MCUControlSurface.MCU_ARROW_DOWN, new CursorCommand (Direction.DOWN, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ZOOM, MCUControlSurface.MCU_ZOOM, new ZoomCommand (this.model, surface));

        // Display Mode
        this.addTriggerCommand (COMMAND_TOGGLE_DISPLAY, MCUControlSurface.MCU_NAME_VALUE, new ToggleDisplayCommand (this.model, surface));
        this.addTriggerCommand (COMMAND_TEMPO_TICKS, MCUControlSurface.MCU_SMPTE_BEATS, new TempoTicksCommand (this.model, surface));

        // Functions
        this.addTriggerCommand (Commands.COMMAND_SHIFT, MCUControlSurface.MCU_SHIFT, new ShiftCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_SELECT, MCUControlSurface.MCU_OPTION, new NopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_PUNCH_IN, MCUControlSurface.MCU_F6, new PunchInCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_PUNCH_OUT, MCUControlSurface.MCU_F7, new PunchOutCommand<> (this.model, surface));
        this.addTriggerCommand (COMMAND_F1, MCUControlSurface.MCU_F1, new AssignableCommand (2, this.model, surface));
        this.addTriggerCommand (COMMAND_F2, MCUControlSurface.MCU_F2, new AssignableCommand (3, this.model, surface));
        this.addTriggerCommand (COMMAND_F3, MCUControlSurface.MCU_F3, new AssignableCommand (4, this.model, surface));
        this.addTriggerCommand (COMMAND_F4, MCUControlSurface.MCU_F4, new AssignableCommand (5, this.model, surface));
        this.addTriggerCommand (COMMAND_F5, MCUControlSurface.MCU_F5, new AssignableCommand (6, this.model, surface));

        // Assignment
        this.addTriggerCommand (Commands.COMMAND_TRACK, MCUControlSurface.MCU_MODE_IO, new TracksCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_PAN_SEND, MCUControlSurface.MCU_MODE_PAN, new ModeSelectCommand<> (Modes.MODE_PAN, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_SENDS, MCUControlSurface.MCU_MODE_SENDS, new SendSelectCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DEVICE, MCUControlSurface.MCU_MODE_PLUGIN, new DevicesCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_MOVE_TRACK_LEFT, MCUControlSurface.MCU_MODE_EQ, new MoveTrackBankCommand<> (this.model, surface, Modes.MODE_DEVICE_PARAMS, true, true));
        this.addTriggerCommand (Commands.COMMAND_MOVE_TRACK_RIGHT, MCUControlSurface.MCU_MODE_DYN, new MoveTrackBankCommand<> (this.model, surface, Modes.MODE_DEVICE_PARAMS, true, false));

        // Automation
        this.addTriggerCommand (Commands.COMMAND_AUTOMATION_READ, MCUControlSurface.MCU_READ, new AutomationCommand (0, this.model, surface));
        final AutomationCommand writeCommand = new AutomationCommand (1, this.model, surface);
        this.addTriggerCommand (Commands.COMMAND_AUTOMATION_WRITE, MCUControlSurface.MCU_WRITE, writeCommand);
        this.addTriggerCommand (Commands.COMMAND_AUTOMATION_WRITE, MCUControlSurface.MCU_GROUP, writeCommand);
        this.addTriggerCommand (Commands.COMMAND_AUTOMATION_TRIM, MCUControlSurface.MCU_TRIM, new AutomationCommand (2, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_AUTOMATION_TOUCH, MCUControlSurface.MCU_TOUCH, new AutomationCommand (3, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_AUTOMATION_LATCH, MCUControlSurface.MCU_LATCH, new AutomationCommand (4, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_UNDO, MCUControlSurface.MCU_UNDO, new UndoCommand<> (this.model, surface));

        // Panes
        this.addTriggerCommand (COMMAND_NOTE_EDITOR, MCUControlSurface.MCU_MIDI_TRACKS, new PaneCommand<> (PaneCommand.Panels.NOTE, this.model, surface));
        this.addTriggerCommand (COMMAND_AUTOMATION_EDITOR, MCUControlSurface.MCU_INPUTS, new PaneCommand<> (PaneCommand.Panels.AUTOMATION, this.model, surface));
        this.addTriggerCommand (COMMAND_TOGGLE_DEVICE, MCUControlSurface.MCU_AUDIO_TRACKS, new PaneCommand<> (PaneCommand.Panels.DEVICE, this.model, surface));
        this.addTriggerCommand (COMMAND_MIXER, MCUControlSurface.MCU_AUDIO_INSTR, new PaneCommand<> (PaneCommand.Panels.MIXER, this.model, surface));

        // Layouts
        this.addTriggerCommand (COMMAND_LAYOUT_ARRANGE, MCUControlSurface.MCU_AUX, new LayoutCommand<> (IApplication.PANEL_LAYOUT_ARRANGE, this.model, surface));
        this.addTriggerCommand (COMMAND_LAYOUT_MIX, MCUControlSurface.MCU_BUSSES, new LayoutCommand<> (IApplication.PANEL_LAYOUT_MIX, this.model, surface));
        this.addTriggerCommand (COMMAND_LAYOUT_EDIT, MCUControlSurface.MCU_OUTPUTS, new LayoutCommand<> (IApplication.PANEL_LAYOUT_EDIT, this.model, surface));

        // Utilities
        this.addTriggerCommand (Commands.COMMAND_BROWSE, MCUControlSurface.MCU_USER, new BrowserCommand<> (Modes.MODE_BROWSER, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_METRONOME, MCUControlSurface.MCU_CLICK, new MetronomeCommand<> (this.model, surface));
        this.addTriggerCommand (COMMAND_GROOVE, MCUControlSurface.MCU_SOLO, new GrooveCommand (this.model, surface));
        this.addTriggerCommand (COMMAND_OVERDUB, MCUControlSurface.MCU_REPLACE, new OverdubCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_TAP_TEMPO, MCUControlSurface.MCU_NUDGE, new TapTempoCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_DUPLICATE, MCUControlSurface.MCU_DROP, new DuplicateCommand<> (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_DEVICE_ON_OFF, MCUControlSurface.MCU_F8, new DeviceOnOffCommand<> (this.model, surface));

        // Currently not used but prevent error in console
        this.addTriggerCommand (COMMAND_CONTROL, MCUControlSurface.MCU_CONTROL, new NopCommand<> (this.model, surface));
        this.addTriggerCommand (COMMAND_ALT, MCUControlSurface.MCU_ALT, new NopCommand<> (this.model, surface));

        // Fader Controls
        this.addTriggerCommand (COMMAND_FLIP, MCUControlSurface.MCU_FLIP, new ToggleTrackBanksCommand<> (this.model, surface));
        this.addTriggerCommand (COMMAND_CANCEL, MCUControlSurface.MCU_CANCEL, new KeyCommand (Key.ESCAPE, this.model, surface));
        this.addTriggerCommand (COMMAND_ENTER, MCUControlSurface.MCU_ENTER, new KeyCommand (Key.ENTER, this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_MOVE_BANK_LEFT, MCUControlSurface.MCU_BANK_LEFT, new MoveTrackBankCommand<> (this.model, surface, Modes.MODE_DEVICE_PARAMS, false, true));
        this.addTriggerCommand (Commands.COMMAND_MOVE_BANK_RIGHT, MCUControlSurface.MCU_BANK_RIGHT, new MoveTrackBankCommand<> (this.model, surface, Modes.MODE_DEVICE_PARAMS, false, false));
        this.addTriggerCommand (Commands.COMMAND_MOVE_TRACK_LEFT, MCUControlSurface.MCU_TRACK_LEFT, new MoveTrackBankCommand<> (this.model, surface, Modes.MODE_DEVICE_PARAMS, true, true));
        this.addTriggerCommand (Commands.COMMAND_MOVE_TRACK_RIGHT, MCUControlSurface.MCU_TRACK_RIGHT, new MoveTrackBankCommand<> (this.model, surface, Modes.MODE_DEVICE_PARAMS, true, false));

        // Additional commands for footcontrollers
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerTriggerCommand (Commands.COMMAND_NEW, new NewCommand<> (this.model, surface));
        viewManager.registerTriggerCommand (Commands.COMMAND_TAP_TEMPO, new TapTempoCommand<> (this.model, surface));

        // Only MCU
        this.addTriggerCommand (Commands.COMMAND_SAVE, MCUControlSurface.MCU_SAVE, new SaveCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_MARKER, MCUControlSurface.MCU_MARKER, new MarkerCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_TOGGLE_VU, MCUControlSurface.MCU_EDIT, new ToggleVUCommand<> (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_MASTERTRACK, MCUControlSurface.MCU_FADER_MASTER, new SelectCommand (8, this.model, surface));

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
                Integer commandID = Integer.valueOf (Commands.COMMAND_ROW_SELECT_1.intValue () + i);
                viewManager.registerTriggerCommand (commandID, new SelectCommand (i, this.model, surface));
                surface.assignTriggerCommand (MCUControlSurface.MCU_SELECT1 + i, commandID);

                commandID = Integer.valueOf (Commands.COMMAND_FADER_TOUCH_1.intValue () + i);
                viewManager.registerTriggerCommand (commandID, new FaderTouchCommand (i, this.model, surface));
                surface.assignTriggerCommand (MCUControlSurface.MCU_FADER_TOUCH1 + i, commandID);

                this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW1_1.intValue () + i), MCUControlSurface.MCU_VSELECT1 + i, new ButtonRowModeCommand<> (0, i, this.model, surface), index);
                this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW2_1.intValue () + i), MCUControlSurface.MCU_ARM1 + i, new ButtonRowModeCommand<> (1, i, this.model, surface), index);
                this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW3_1.intValue () + i), MCUControlSurface.MCU_SOLO1 + i, new ButtonRowModeCommand<> (2, i, this.model, surface), index);
                this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW4_1.intValue () + i), MCUControlSurface.MCU_MUTE1 + i, new ButtonRowModeCommand<> (3, i, this.model, surface), index);
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
        viewManager.registerContinuousCommand (Commands.CONT_COMMAND_PLAY_POSITION, new PlayPositionTempoCommand (this.model, surface));
        surface.assignContinuousCommand (MCUControlSurface.MCU_CC_JOG, 1, Commands.CONT_COMMAND_PLAY_POSITION);

        for (int index = 0; index < this.numMCUDevices; index++)
        {
            surface = this.getSurface (index);
            viewManager = surface.getViewManager ();
            for (int i = 0; i < 8; i++)
            {
                final Integer commandID = Integer.valueOf (Commands.CONT_COMMAND_KNOB1.intValue () + i);
                viewManager.registerContinuousCommand (commandID, new KnobRowModeCommand<> (i, this.model, surface));
                surface.assignContinuousCommand (MCUControlSurface.MCU_CC_VPOT1 + i, 1, commandID);
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

            surface.getViewManager ().setActiveView (Views.VIEW_CONTROL);
            surface.getModeManager ().setActiveMode (Modes.MODE_PAN);
        }
    }


    @SuppressWarnings("unchecked")
    private void updateButtons ()
    {
        final MCUControlSurface surface = this.getSurface ();
        final Integer mode = surface.getModeManager ().getActiveOrTempModeId ();
        if (mode == null)
            return;

        this.updateVUandFaders ();
        this.updateSegmentDisplay ();

        // Set button states
        final ITransport t = this.model.getTransport ();
        final boolean isShift = surface.isShiftPressed ();
        final boolean isFlipRecord = this.configuration.isFlipRecord ();
        final boolean isRecordShifted = isShift && !isFlipRecord || !isShift && isFlipRecord;

        final boolean isTrackOn = Modes.MODE_TRACK.equals (mode) || Modes.MODE_VOLUME.equals (mode);
        final boolean isPanOn = Modes.MODE_PAN.equals (mode);
        final boolean isSendOn = mode.intValue () >= Modes.MODE_SEND1.intValue () && mode.intValue () <= Modes.MODE_SEND8.intValue ();
        final boolean isDeviceOn = Modes.MODE_DEVICE_PARAMS.equals (mode);

        final boolean isLEDOn = surface.isPressed (MCUControlSurface.MCU_OPTION) ? this.model.isCursorTrackPinned () : isTrackOn;
        surface.updateButton (MCUControlSurface.MCU_MODE_IO, isLEDOn ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_MODE_PAN, isPanOn ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_MODE_SENDS, isSendOn ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final boolean isOn = surface.isPressed (MCUControlSurface.MCU_OPTION) ? cursorDevice.isPinned () : isDeviceOn;

        surface.updateButton (MCUControlSurface.MCU_MODE_PLUGIN, isOn ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_USER, Modes.MODE_BROWSER.equals (mode) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        final ITransport transport = this.model.getTransport ();
        final String automationWriteMode = transport.getAutomationWriteMode ();
        final boolean writingArrangerAutomation = transport.isWritingArrangerAutomation ();

        surface.updateButton (MCUControlSurface.MCU_F6, transport.isPunchInEnabled () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_F7, transport.isPunchOutEnabled () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        surface.updateButton (MCUControlSurface.MCU_READ, !writingArrangerAutomation ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        final int writeState = writingArrangerAutomation && ITransport.AUTOMATION_MODES_VALUES[2].equals (automationWriteMode) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF;
        surface.updateButton (MCUControlSurface.MCU_WRITE, writeState);
        surface.updateButton (MCUControlSurface.MCU_GROUP, writeState);
        surface.updateButton (MCUControlSurface.MCU_TRIM, transport.isWritingClipLauncherAutomation () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_TOUCH, writingArrangerAutomation && ITransport.AUTOMATION_MODES_VALUES[1].equals (automationWriteMode) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_LATCH, writingArrangerAutomation && ITransport.AUTOMATION_MODES_VALUES[0].equals (automationWriteMode) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        final View view = surface.getViewManager ().getView (Views.VIEW_CONTROL);
        surface.updateButton (MCUControlSurface.MCU_REWIND, ((WindCommand<MCUControlSurface, MCUConfiguration>) view.getTriggerCommand (Commands.COMMAND_REWIND)).isRewinding () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_FORWARD, ((WindCommand<MCUControlSurface, MCUConfiguration>) view.getTriggerCommand (Commands.COMMAND_FORWARD)).isForwarding () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_REPEAT, t.isLoop () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_STOP, !t.isPlaying () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_PLAY, t.isPlaying () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_RECORD, isRecordShifted ? t.isLauncherOverdub () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF : t.isRecording () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        surface.updateButton (MCUControlSurface.MCU_NAME_VALUE, surface.getConfiguration ().isDisplayTrackNames () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_ZOOM, surface.getConfiguration ().isZoomState () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_SCRUB, surface.getModeManager ().isActiveOrTempMode (Modes.MODE_DEVICE_PARAMS) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        surface.updateButton (MCUControlSurface.MCU_MIDI_TRACKS, MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_INPUTS, MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_AUDIO_TRACKS, surface.isShiftPressed () && cursorDevice.isWindowOpen () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_AUDIO_INSTR, MCU_BUTTON_STATE_OFF);

        surface.updateButton (MCUControlSurface.MCU_CLICK, (isShift ? t.isMetronomeTicksOn () : t.isMetronomeOn ()) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_SOLO, this.model.getGroove ().getParameters ()[0].getValue () > 0 ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_REPLACE, (isShift ? t.isLauncherOverdub () : t.isArrangerOverdub ()) ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_FLIP, this.model.isEffectTrackBankActive () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);

        final boolean displayTicks = this.configuration.isDisplayTicks ();
        surface.updateButton (MCUControlSurface.MCU_SMPTE_BEATS, displayTicks ? MCU_BUTTON_STATE_OFF : MCU_BUTTON_STATE_ON);
        surface.updateButton (MCUControlSurface.MCU_SMPTE_LED, displayTicks ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
        surface.updateButton (MCUControlSurface.MCU_BEATS_LED, displayTicks ? MCU_BUTTON_STATE_OFF : MCU_BUTTON_STATE_ON);

        surface.updateButton (MCUControlSurface.MCU_MARKER, this.model.getArranger ().areCueMarkersVisible () ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF);
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


    private void updateVUandFaders ()
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
            final int volume = surface.isShiftPressed () ? this.model.getTransport ().getMetronomeVolume () : masterTrack.getVolume ();
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
            if (modeManager.isActiveOrTempMode (Modes.MODE_VOLUME))
                value = track.getVolume ();
            else if (modeManager.isActiveOrTempMode (Modes.MODE_PAN))
                value = track.getPan ();
            else if (modeManager.isActiveOrTempMode (Modes.MODE_TRACK))
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
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND1))
                value = track.getSendBank ().getItem (0).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND2))
                value = track.getSendBank ().getItem (1).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND3))
                value = track.getSendBank ().getItem (2).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND4))
                value = track.getSendBank ().getItem (3).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND5))
                value = track.getSendBank ().getItem (4).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND6))
                value = track.getSendBank ().getItem (5).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND7))
                value = track.getSendBank ().getItem (6).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.MODE_SEND8))
                value = track.getSendBank ().getItem (7).getValue ();
            else if (modeManager.isActiveOrTempMode (Modes.MODE_DEVICE_PARAMS))
                value = this.model.getCursorDevice ().getParameterBank ().getItem (channel).getValue ();
        }

        if (value != this.faderValues[channel])
        {
            this.faderValues[channel] = value;
            output.sendPitchbend (index, value % 127, value / 127);
        }
    }


    private void updateMode (final Integer mode)
    {
        if (mode == null)
            return;

        this.updateIndication (mode);
        if (this.configuration.hasAssignmentDisplay ())
            this.getSurface ().getSegmentDisplay ().setAssignmentDisplay (MODE_ACRONYMS.get (mode));
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Integer mode)
    {
        if (mode == this.currentMode)
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
                sendBank.getItem (j).setIndication (!isEffect && (mode.intValue () - Modes.MODE_SEND1.intValue () == j || hasTrackSel));

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
     * @param index The index of the track
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final int index, final boolean isSelected)
    {
        if (!isSelected)
            return;

        final ModeManager modeManager = this.getSurface ().getModeManager ();
        if (modeManager.isActiveOrTempMode (Modes.MODE_MASTER))
            modeManager.setActiveMode (Modes.MODE_TRACK);
    }
}
