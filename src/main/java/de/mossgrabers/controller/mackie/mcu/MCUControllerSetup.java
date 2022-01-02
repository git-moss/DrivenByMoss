// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu;

import de.mossgrabers.controller.mackie.mcu.command.continuous.PlayPositionTempoCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.AssignableCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.DevicesCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.FaderTouchCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.GrooveCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.KeyCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.KeyCommand.Key;
import de.mossgrabers.controller.mackie.mcu.command.trigger.MCUCursorCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.MCUFlipCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.MCUMoveTrackBankCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.MCURecordCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.ScrubCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.SelectCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.SendSelectCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.TempoTicksCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.ToggleDisplayCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.TracksCommand;
import de.mossgrabers.controller.mackie.mcu.command.trigger.ZoomCommand;
import de.mossgrabers.controller.mackie.mcu.controller.MCUAssignmentDisplay;
import de.mossgrabers.controller.mackie.mcu.controller.MCUColorManager;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.controller.MCUDeviceType;
import de.mossgrabers.controller.mackie.mcu.controller.MCUDisplay;
import de.mossgrabers.controller.mackie.mcu.controller.MCUSegmentDisplay;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.controller.mackie.mcu.mode.MarkerMode;
import de.mossgrabers.controller.mackie.mcu.mode.device.DeviceBrowserMode;
import de.mossgrabers.controller.mackie.mcu.mode.device.DeviceParamsMode;
import de.mossgrabers.controller.mackie.mcu.mode.device.UserMode;
import de.mossgrabers.controller.mackie.mcu.mode.track.MasterMode;
import de.mossgrabers.controller.mackie.mcu.mode.track.PanMode;
import de.mossgrabers.controller.mackie.mcu.mode.track.SendMode;
import de.mossgrabers.controller.mackie.mcu.mode.track.TrackMode;
import de.mossgrabers.controller.mackie.mcu.mode.track.VolumeMode;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.MarkerCommand;
import de.mossgrabers.framework.command.trigger.ShiftCommand;
import de.mossgrabers.framework.command.trigger.application.DuplicateCommand;
import de.mossgrabers.framework.command.trigger.application.LayoutCommand;
import de.mossgrabers.framework.command.trigger.application.OverdubCommand;
import de.mossgrabers.framework.command.trigger.application.PaneCommand;
import de.mossgrabers.framework.command.trigger.application.PanelLayoutCommand;
import de.mossgrabers.framework.command.trigger.application.SaveCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.device.DeviceOnOffCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.track.ToggleVUCommand;
import de.mossgrabers.framework.command.trigger.transport.AutomationModeCommand;
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
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.OutputID;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.MasterVolumeMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.ControlOnlyView;
import de.mossgrabers.framework.view.Views;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;


/**
 * Support for the Mackie MCU protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUControllerSetup extends AbstractControllerSetup<MCUControlSurface, MCUConfiguration>
{
    private static final String             TAG_RIGHT            = "Right";

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

    private static final Set<Modes> VALUE_MODES      = EnumSet.of (Modes.VOLUME, Modes.PAN, Modes.TRACK, Modes.SEND1, Modes.SEND2, Modes.SEND3, Modes.SEND4, Modes.SEND5, Modes.SEND6, Modes.SEND7, Modes.SEND8, Modes.DEVICE_PARAMS);

    private final int []            masterVuValues   = new int [2];
    private int                     masterFaderValue = -1;
    private final int []            vuValues         = new int [36];
    private final int []            faderValues      = new int [36];
    private final int               numMCUDevices;


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

        this.colorManager = new MCUColorManager ();
        this.valueChanger = new TwosComplementValueChanger (16241 + 1, 10);
        this.configuration = new MCUConfiguration (host, this.valueChanger, numMCUDevices, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        this.surfaces.forEach (surface -> {
            final ModeManager modeManager = surface.getModeManager ();
            final Modes mode = modeManager.getActiveID ();
            this.updateMode (mode);

            if (mode == null)
                return;

            this.updateVUMeters ();
            this.updateFaders (surface.isShiftPressed ());
            this.updateSegmentDisplay ();

            final IMode activeOrTempMode = modeManager.getActive ();
            if (activeOrTempMode instanceof final BaseMode<?> baseMode)
                baseMode.updateKnobLEDs ();
        });
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();

        ms.enableDrumDevice (false);

        if (this.configuration.shouldPinFXTracksToLastController ())
        {
            ms.setNumTracks (8 * (this.numMCUDevices - 1));
            ms.setNumFxTracks (8);
        }
        else
        {
            ms.setNumTracks (8 * this.numMCUDevices);
        }

        ms.setHasFlatTrackList (this.configuration.isTrackNavigationFlat ());
        ms.setHasFullFlatTrackList (this.configuration.shouldIncludeFXTracksInTrackBank ());
        // This is required to make the new clip function work!
        ms.setNumScenes (8);
        ms.setNumFilterColumnEntries (8);
        ms.setNumResults (8);
        ms.setNumParamPages (8 * this.numMCUDevices);
        ms.setNumParams (8 * this.numMCUDevices);
        ms.setNumDeviceLayers (0);
        ms.setNumDrumPadLayers (0);
        ms.setNumMarkers (8 * this.numMCUDevices);
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();

        for (int i = 0; i < this.numMCUDevices; i++)
        {
            final MCUDeviceType deviceType = this.configuration.getDeviceType (i);
            final boolean isMainDevice = deviceType == MCUDeviceType.MAIN;

            final IMidiOutput output = midiAccess.createOutput (i);
            final IMidiInput input = midiAccess.createInput (i, null);
            final MCUControlSurface surface = new MCUControlSurface (this.surfaces, this.host, this.colorManager, this.configuration, output, input, 8 * i, isMainDevice);
            this.surfaces.add (surface);
            surface.addTextDisplay (new MCUDisplay (this.host, output, true, deviceType == MCUDeviceType.MACKIE_EXTENDER, false));
            surface.addTextDisplay (new MCUDisplay (this.host, output, false, false, isMainDevice));
            surface.addTextDisplay (new MCUSegmentDisplay (this.host, output));
            surface.addTextDisplay (new MCUAssignmentDisplay (this.host, output));
            surface.getModeManager ().setDefaultID (Modes.VOLUME);
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

            modeManager.register (Modes.TRACK, new TrackMode (surface, this.model));
            modeManager.register (Modes.VOLUME, new VolumeMode (surface, this.model));
            modeManager.register (Modes.PAN, new PanMode (surface, this.model));
            for (int i = 0; i < 8; i++)
                modeManager.register (Modes.get (Modes.SEND1, i), new SendMode (surface, this.model, i));
            modeManager.register (Modes.MASTER, new MasterMode (surface, this.model));

            modeManager.register (Modes.DEVICE_PARAMS, new DeviceParamsMode (surface, this.model));
            modeManager.register (Modes.USER, new UserMode (surface, this.model));
            modeManager.register (Modes.BROWSER, new DeviceBrowserMode (surface, this.model));
            modeManager.register (Modes.MARKERS, new MarkerMode (surface, this.model));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        for (int index = 0; index < this.numMCUDevices; index++)
        {
            final MCUControlSurface surface = this.getSurface (index);
            surface.getViewManager ().register (Views.CONTROL, new ControlOnlyView<> (surface, this.model));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        // Connect all modes
        for (int i = 0; i < this.numMCUDevices; i++)
        {
            final ModeManager mm = this.getSurface (i).getModeManager ();
            for (int j = 0; j < this.numMCUDevices; j++)
            {
                if (i != j)
                    this.getSurface (j).getModeManager ().addConnectedManagerListener (mm);
            }
        }

        this.model.getMasterTrack ().addSelectionObserver ( (index, isSelected) -> {
            if (isSelected)
                this.getSurface ().getModeManager ().setActive (Modes.MASTER);
        });

        this.configuration.addSettingObserver (AbstractConfiguration.ENABLE_VU_METERS, () -> {
            for (int index = 0; index < this.numMCUDevices; index++)
            {
                final MCUControlSurface surface = this.getSurface (index);
                surface.switchVuMode (this.configuration.isEnableVUMeters () ? MCUControlSurface.VUMODE_LED_AND_LCD : MCUControlSurface.VUMODE_OFF);
                final IMode activeMode = surface.getModeManager ().getActive ();
                if (activeMode != null)
                    activeMode.updateDisplay ();
                ((MCUDisplay) surface.getDisplay ()).forceFlush ();
            }
        });

        this.configuration.addSettingObserver (MCUConfiguration.USE_FADERS_AS_KNOBS, () -> {
            for (int index = 0; index < this.numMCUDevices; index++)
            {
                final MCUControlSurface surface = this.getSurface (index);
                final AbstractMode<?, ?, ?> mode = (AbstractMode<?, ?, ?>) surface.getModeManager ().getActive ();
                if (mode != null)
                    mode.parametersAdjusted ();
            }
        });

        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.activateBrowserObserver (Modes.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final ITransport t = this.model.getTransport ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();

        for (int index = 0; index < this.numMCUDevices; index++)
        {
            final MCUControlSurface surface = this.getSurface (index);

            if (this.configuration.getDeviceType (index) == MCUDeviceType.MAIN)
            {
                // Foot-switches
                this.addButton (surface, ButtonID.FOOTSWITCH1, "Footswitch 1", new AssignableCommand (0, this.model, surface), MCUControlSurface.MCU_USER_A);
                this.addButton (surface, ButtonID.FOOTSWITCH2, "Footswitch 2", new AssignableCommand (1, this.model, surface), MCUControlSurface.MCU_USER_B);

                // Navigation

                final WindCommand<MCUControlSurface, MCUConfiguration> rewindCommand = new WindCommand<> (this.model, surface, false);
                final WindCommand<MCUControlSurface, MCUConfiguration> forwardCommand = new WindCommand<> (this.model, surface, true);
                this.addButton (surface, ButtonID.REWIND, "<<", rewindCommand, 0, MCUControlSurface.MCU_REWIND, rewindCommand::isRewinding);
                this.addButton (surface, ButtonID.FORWARD, ">>", forwardCommand, 0, MCUControlSurface.MCU_FORWARD, forwardCommand::isForwarding);
                this.addButton (surface, ButtonID.LOOP, "Loop", new ToggleLoopCommand<> (this.model, surface), 0, MCUControlSurface.MCU_REPEAT, t::isLoop);
                this.addButton (surface, ButtonID.STOP, "Stop", new StopCommand<> (this.model, surface), 0, MCUControlSurface.MCU_STOP, () -> !t.isPlaying ());
                this.addButton (surface, ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface), 0, MCUControlSurface.MCU_PLAY, t::isPlaying);
                this.addButton (surface, ButtonID.RECORD, "Record", new MCURecordCommand (this.model, surface), 0, MCUControlSurface.MCU_RECORD, () -> {
                    final boolean isOn = this.isRecordShifted (surface) ? t.isLauncherOverdub () : t.isRecording ();
                    return isOn ? MCU_BUTTON_STATE_ON : MCU_BUTTON_STATE_OFF;
                });

                this.addButton (surface, ButtonID.SCRUB, "Scrub", new ScrubCommand (this.model, surface), 0, MCUControlSurface.MCU_SCRUB, () -> surface.getModeManager ().isActive (Modes.DEVICE_PARAMS));
                this.addButton (surface, ButtonID.ARROW_LEFT, "Left", new MCUCursorCommand (Direction.LEFT, this.model, surface), 0, MCUControlSurface.MCU_ARROW_LEFT);
                this.addButton (surface, ButtonID.ARROW_RIGHT, TAG_RIGHT, new MCUCursorCommand (Direction.RIGHT, this.model, surface), 0, MCUControlSurface.MCU_ARROW_RIGHT);
                this.addButton (surface, ButtonID.ARROW_UP, "Up", new MCUCursorCommand (Direction.UP, this.model, surface), 0, MCUControlSurface.MCU_ARROW_UP);
                this.addButton (surface, ButtonID.ARROW_DOWN, "Down", new MCUCursorCommand (Direction.DOWN, this.model, surface), 0, MCUControlSurface.MCU_ARROW_DOWN);
                this.addButton (surface, ButtonID.ZOOM, "Zoom", new ZoomCommand (this.model, surface), 0, MCUControlSurface.MCU_ZOOM, surface.getConfiguration ()::isZoomState);

                // Display Mode
                this.addButton (surface, ButtonID.TOGGLE_DISPLAY, "Toggle Display", new ToggleDisplayCommand (this.model, surface), 0, MCUControlSurface.MCU_NAME_VALUE, surface.getConfiguration ()::isDisplayTrackNames);
                this.addButton (surface, ButtonID.TEMPO_TICKS, "Tempo Ticks", new TempoTicksCommand (this.model, surface), 0, MCUControlSurface.MCU_SMPTE_BEATS, this.configuration::isDisplayTicks);

                // Functions
                this.addButton (surface, ButtonID.SHIFT, "Shift", new ShiftCommand<> (this.model, surface), 0, MCUControlSurface.MCU_SHIFT);
                this.addButton (surface, ButtonID.SELECT, "Option", NopCommand.INSTANCE, 0, MCUControlSurface.MCU_OPTION);
                this.addButton (surface, ButtonID.PUNCH_IN, "Punch In", new PunchInCommand<> (this.model, surface), 0, MCUControlSurface.MCU_F6, t::isPunchInEnabled);
                this.addButton (surface, ButtonID.PUNCH_OUT, "Punch Out", new PunchOutCommand<> (this.model, surface), 0, MCUControlSurface.MCU_F7, t::isPunchOutEnabled);

                for (int i = 0; i < 5; i++)
                {
                    final AssignableCommand command = new AssignableCommand (2 + i, this.model, surface);
                    this.addButton (surface, ButtonID.get (ButtonID.F1, i), "F" + (i + 1), command, 0, MCUControlSurface.MCU_F1 + i, command::isActive);
                }

                // Assignment - mode selection

                final ModeManager modeManager = surface.getModeManager ();

                this.addButton (surface, ButtonID.TRACK, "Track", new TracksCommand (this.model, surface), 0, MCUControlSurface.MCU_MODE_IO, () -> surface.getButton (ButtonID.SELECT).isPressed () ? this.model.getCursorTrack ().isPinned () : modeManager.isActive (Modes.TRACK, Modes.VOLUME));
                this.addButton (surface, ButtonID.PAN_SEND, "Pan", new ModeSelectCommand<> (this.model, surface, Modes.PAN), 0, MCUControlSurface.MCU_MODE_PAN, () -> modeManager.isActive (Modes.PAN));
                this.addButton (surface, ButtonID.SENDS, "Sends", new SendSelectCommand (this.model, surface), 0, MCUControlSurface.MCU_MODE_SENDS, () -> Modes.isSendMode (modeManager.getActiveID ()));
                this.addButton (surface, ButtonID.DEVICE, "Device", new DevicesCommand (this.model, surface), 0, MCUControlSurface.MCU_MODE_PLUGIN, () -> surface.getButton (ButtonID.SELECT).isPressed () ? cursorDevice.isPinned () : modeManager.isActive (Modes.DEVICE_PARAMS, Modes.USER));

                final MCUMoveTrackBankCommand leftTrackCommand = new MCUMoveTrackBankCommand (this.model, surface, true, true);
                final MCUMoveTrackBankCommand rightTrackCommand = new MCUMoveTrackBankCommand (this.model, surface, true, false);
                this.addButton (surface, ButtonID.PAGE_LEFT, "Left", leftTrackCommand, 0, MCUControlSurface.MCU_MODE_EQ);
                this.addButton (surface, ButtonID.PAGE_RIGHT, TAG_RIGHT, rightTrackCommand, 0, MCUControlSurface.MCU_MODE_DYN);
                this.addButton (surface, ButtonID.MOVE_TRACK_LEFT, "Left", leftTrackCommand, 0, MCUControlSurface.MCU_TRACK_LEFT);
                this.addButton (surface, ButtonID.MOVE_TRACK_RIGHT, TAG_RIGHT, rightTrackCommand, 0, MCUControlSurface.MCU_TRACK_RIGHT);

                // Automation
                this.addButton (surface, ButtonID.AUTOMATION_TRIM, "Trim", new AutomationModeCommand<> (AutomationMode.TRIM_READ, this.model, surface), 0, MCUControlSurface.MCU_TRIM, () -> !t.isWritingArrangerAutomation () && t.getAutomationWriteMode () == AutomationMode.TRIM_READ);
                this.addButton (surface, ButtonID.AUTOMATION_READ, "Read", new AutomationModeCommand<> (AutomationMode.READ, this.model, surface), 0, MCUControlSurface.MCU_READ, () -> !t.isWritingArrangerAutomation () && t.getAutomationWriteMode () != AutomationMode.TRIM_READ);
                this.addButton (surface, ButtonID.AUTOMATION_WRITE, "Write", new AutomationModeCommand<> (AutomationMode.WRITE, this.model, surface), 0, MCUControlSurface.MCU_WRITE, () -> t.isWritingArrangerAutomation () && t.getAutomationWriteMode () == AutomationMode.WRITE);
                this.addButton (surface, ButtonID.AUTOMATION_GROUP, "Group/Write", new AutomationModeCommand<> (AutomationMode.LATCH_PREVIEW, this.model, surface), 0, MCUControlSurface.MCU_GROUP, () -> t.isWritingArrangerAutomation () && t.getAutomationWriteMode () == AutomationMode.LATCH_PREVIEW);
                this.addButton (surface, ButtonID.AUTOMATION_TOUCH, "Touch", new AutomationModeCommand<> (AutomationMode.TOUCH, this.model, surface), 0, MCUControlSurface.MCU_TOUCH, () -> t.isWritingArrangerAutomation () && t.getAutomationWriteMode () == AutomationMode.TOUCH);
                this.addButton (surface, ButtonID.AUTOMATION_LATCH, "Latch", new AutomationModeCommand<> (AutomationMode.LATCH, this.model, surface), 0, MCUControlSurface.MCU_LATCH, () -> t.isWritingArrangerAutomation () && t.getAutomationWriteMode () == AutomationMode.LATCH);
                this.addButton (surface, ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), MCUControlSurface.MCU_UNDO);

                // Panes
                this.addButton (surface, ButtonID.NOTE_EDITOR, "Note Editor", new PaneCommand<> (PaneCommand.Panels.NOTE, this.model, surface), MCUControlSurface.MCU_MIDI_TRACKS);
                this.addButton (surface, ButtonID.AUTOMATION_EDITOR, "Automation Editor", new PaneCommand<> (PaneCommand.Panels.AUTOMATION, this.model, surface), MCUControlSurface.MCU_INPUTS);
                this.addButton (surface, ButtonID.TOGGLE_DEVICE, "Toggle Device", new PanelLayoutCommand<> (this.model, surface), 0, MCUControlSurface.MCU_AUDIO_TRACKS, () -> !surface.isShiftPressed () && cursorDevice.isWindowOpen ());
                this.addButton (surface, ButtonID.MIXER, "Mixer", new PaneCommand<> (PaneCommand.Panels.MIXER, this.model, surface), MCUControlSurface.MCU_AUDIO_INSTR);

                // Layouts
                this.addButton (surface, ButtonID.LAYOUT_ARRANGE, "Arrange", new LayoutCommand<> (IApplication.PANEL_LAYOUT_ARRANGE, this.model, surface), MCUControlSurface.MCU_AUX);
                this.addButton (surface, ButtonID.LAYOUT_MIX, "Mix", new LayoutCommand<> (IApplication.PANEL_LAYOUT_MIX, this.model, surface), MCUControlSurface.MCU_BUSSES);
                this.addButton (surface, ButtonID.LAYOUT_EDIT, "Edit", new LayoutCommand<> (IApplication.PANEL_LAYOUT_EDIT, this.model, surface), MCUControlSurface.MCU_OUTPUTS);

                // Utilities
                this.addButton (surface, ButtonID.BROWSE, "Browse", new BrowserCommand<> (this.model, surface), 0, MCUControlSurface.MCU_USER, () -> modeManager.isActive (Modes.BROWSER));
                this.addButton (surface, ButtonID.METRONOME, "Metronome", new MetronomeCommand<> (this.model, surface, false), 0, MCUControlSurface.MCU_CLICK, () -> surface.getButton (ButtonID.SHIFT).isPressed () ? t.isMetronomeTicksOn () : t.isMetronomeOn ());
                this.addButton (surface, ButtonID.GROOVE, "Groove", new GrooveCommand (this.model, surface), 0, MCUControlSurface.MCU_SOLO, () -> {
                    final IParameter parameter = this.model.getGroove ().getParameter (GrooveParameterID.ENABLED);
                    return parameter != null && parameter.getValue () > 0;
                });
                this.addButton (surface, ButtonID.OVERDUB, "Overdub", new OverdubCommand<> (this.model, surface), 0, MCUControlSurface.MCU_REPLACE, () -> (surface.getButton (ButtonID.SHIFT).isPressed () ? t.isLauncherOverdub () : t.isArrangerOverdub ()));
                this.addButton (surface, ButtonID.TAP_TEMPO, "Tap Tempo", new TapTempoCommand<> (this.model, surface), 0, MCUControlSurface.MCU_NUDGE);
                this.addButton (surface, ButtonID.DUPLICATE, "Duplicate", new DuplicateCommand<> (this.model, surface), 0, MCUControlSurface.MCU_DROP);

                this.addButton (surface, ButtonID.DEVICE_ON_OFF, "Device On/Off", new DeviceOnOffCommand<> (this.model, surface), MCUControlSurface.MCU_F8);

                // Currently not used but prevent error in console
                this.addButton (surface, ButtonID.CONTROL, "Control", NopCommand.INSTANCE, MCUControlSurface.MCU_CONTROL);
                this.addButton (surface, ButtonID.ALT, "Alt", NopCommand.INSTANCE, MCUControlSurface.MCU_ALT);

                // Fader Controls
                this.addButton (surface, ButtonID.FLIP, "Flip", new MCUFlipCommand (this.model, surface), 0, MCUControlSurface.MCU_FLIP, this.model::isEffectTrackBankActive);
                this.addButton (surface, ButtonID.CANCEL, "Cancel", new KeyCommand (Key.ESCAPE, this.model, surface), MCUControlSurface.MCU_CANCEL);
                this.addButton (surface, ButtonID.ENTER, "Enter", new KeyCommand (Key.ENTER, this.model, surface), MCUControlSurface.MCU_ENTER);

                this.addButton (surface, ButtonID.MOVE_BANK_LEFT, "Bank Left", new MCUMoveTrackBankCommand (this.model, surface, false, true), MCUControlSurface.MCU_BANK_LEFT);
                this.addButton (surface, ButtonID.MOVE_BANK_RIGHT, "Bank Right", new MCUMoveTrackBankCommand (this.model, surface, false, false), MCUControlSurface.MCU_BANK_RIGHT);

                // Additional command for foot controllers
                this.addButton (surface, ButtonID.NEW, "New", new NewCommand<> (this.model, surface), -1);

                // Only MCU
                this.addButton (surface, ButtonID.SAVE, "Save", new SaveCommand<> (this.model, surface), MCUControlSurface.MCU_SAVE);
                this.addButton (surface, ButtonID.MARKER, "Marker", new MarkerCommand<> (this.model, surface), 0, MCUControlSurface.MCU_MARKER, () -> surface.getButton (ButtonID.SHIFT).isPressed () ? this.model.getArranger ().areCueMarkersVisible () : modeManager.isActive (Modes.MARKERS));
                this.addButton (surface, ButtonID.TOGGLE_VU, "Toggle VU", new ToggleVUCommand<> (this.model, surface), 0, MCUControlSurface.MCU_EDIT, () -> this.configuration.isEnableVUMeters ());

                this.addLight (surface, OutputID.LED1, 0, MCUControlSurface.MCU_SMPTE_LED, () -> this.configuration.isDisplayTicks () ? 2 : 0);
                this.addLight (surface, OutputID.LED2, 0, MCUControlSurface.MCU_BEATS_LED, () -> !this.configuration.isDisplayTicks () ? 2 : 0);
            }

            for (int i = 0; i < 8; i++)
            {
                final ButtonID row1ButtonID = ButtonID.get (ButtonID.ROW2_1, i);
                final ButtonID row2ButtonID = ButtonID.get (ButtonID.ROW3_1, i);
                final ButtonID row3ButtonID = ButtonID.get (ButtonID.ROW4_1, i);
                final ButtonID row4ButtonID = ButtonID.get (ButtonID.ROW_SELECT_1, i);

                final int labelIndex = 8 * (this.numMCUDevices - index - 1) + i + 1;

                this.addButton (surface, row1ButtonID, "Rec Arm " + labelIndex, new ButtonRowModeCommand<> (1, i, this.model, surface), MCUControlSurface.MCU_ARM1 + i, () -> getButtonColor (surface, row1ButtonID));
                this.addButton (surface, row2ButtonID, "Solo " + labelIndex, new ButtonRowModeCommand<> (2, i, this.model, surface), MCUControlSurface.MCU_SOLO1 + i, () -> getButtonColor (surface, row2ButtonID));
                this.addButton (surface, row3ButtonID, "Mute " + labelIndex, new ButtonRowModeCommand<> (3, i, this.model, surface), MCUControlSurface.MCU_MUTE1 + i, () -> getButtonColor (surface, row3ButtonID));
                this.addButton (surface, row4ButtonID, "Select " + labelIndex, new SelectCommand (i, this.model, surface), MCUControlSurface.MCU_SELECT1 + i, () -> getButtonColor (surface, row4ButtonID));
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        for (int index = 0; index < this.numMCUDevices; index++)
        {
            final MCUControlSurface surface = this.getSurface (index);
            final IMidiInput input = surface.getMidiInput ();

            if (this.configuration.getDeviceType (index) == MCUDeviceType.MAIN)
            {
                this.addRelativeKnob (surface, ContinuousID.PLAY_POSITION, "Jog Wheel", new PlayPositionTempoCommand (this.model, surface), MCUControlSurface.MCU_CC_JOG, RelativeEncoding.SIGNED_BIT);

                final IHwFader master = this.addFader (surface, ContinuousID.FADER_MASTER, "Master", null, 8);
                master.bindTouch (new FaderTouchCommand (8, this.model, surface), input, BindType.NOTE, 0, MCUControlSurface.MCU_FADER_MASTER);
                if (this.configuration.hasMotorFaders ())
                {
                    // Prevent catch up jitter with motor faders
                    master.disableTakeOver ();
                }
                new MasterVolumeMode<> (surface, this.model, ContinuousID.FADER_MASTER).onActivate ();
            }

            for (int i = 0; i < 8; i++)
            {
                final IHwRelativeKnob knob = this.addRelativeKnob (surface, ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + i, new KnobRowModeCommand<> (i, this.model, surface), MCUControlSurface.MCU_CC_VPOT1 + i, RelativeEncoding.SIGNED_BIT);
                // Note: this is pressing the knobs' button not touching it!
                knob.bindTouch (new ButtonRowModeCommand<> (0, i, this.model, surface), input, BindType.NOTE, 0, MCUControlSurface.MCU_VSELECT1 + i);
                knob.setIndexInGroup (index * 8 + i);

                final IHwFader fader = this.addFader (surface, ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), null, i);
                if (this.configuration.hasMotorFaders ())
                {
                    // Prevent catch up jitter with motor faders
                    fader.disableTakeOver ();
                }

                fader.bindTouch (new FaderTouchCommand (i, this.model, surface), input, BindType.NOTE, 0, MCUControlSurface.MCU_FADER_TOUCH1 + i);
                fader.setIndexInGroup (index * 8 + i);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        for (int index = 0; index < this.numMCUDevices; index++)
        {
            final MCUControlSurface surface = this.getSurface (index);

            if (this.configuration.getDeviceType (index) == MCUDeviceType.MAIN)
            {
                surface.getButton (ButtonID.FOOTSWITCH1).setBounds (12.5, 942.0, 77.75, 39.75);
                surface.getButton (ButtonID.FOOTSWITCH2).setBounds (102.5, 942.0, 77.75, 39.75);
                surface.getButton (ButtonID.REWIND).setBounds (556.0, 942.0, 65.0, 39.75);
                surface.getButton (ButtonID.FORWARD).setBounds (630.0, 942.0, 65.0, 39.75);
                surface.getButton (ButtonID.LOOP).setBounds (706.25, 942.0, 65.0, 39.75);
                surface.getButton (ButtonID.STOP).setBounds (779.0, 942.0, 65.0, 39.75);
                surface.getButton (ButtonID.PLAY).setBounds (850.25, 942.0, 65.0, 39.75);
                surface.getButton (ButtonID.RECORD).setBounds (923.5, 942.0, 65.0, 39.75);
                surface.getButton (ButtonID.SCRUB).setBounds (922.25, 697.25, 65.0, 39.75);
                surface.getButton (ButtonID.ARROW_LEFT).setBounds (705.0, 750.25, 65.0, 39.75);
                surface.getButton (ButtonID.ARROW_RIGHT).setBounds (849.0, 750.25, 65.0, 39.75);
                surface.getButton (ButtonID.ARROW_UP).setBounds (777.75, 702.25, 65.0, 39.75);
                surface.getButton (ButtonID.ARROW_DOWN).setBounds (777.75, 797.0, 65.0, 39.75);
                surface.getButton (ButtonID.ZOOM).setBounds (777.75, 750.25, 65.0, 39.75);
                surface.getButton (ButtonID.TOGGLE_DISPLAY).setBounds (701.25, 92.5, 77.75, 39.75);
                surface.getButton (ButtonID.TEMPO_TICKS).setBounds (879.0, 92.5, 77.75, 39.75);
                surface.getButton (ButtonID.SHIFT).setBounds (776.5, 637.5, 65.0, 39.75);
                surface.getButton (ButtonID.SELECT).setBounds (703.75, 637.5, 65.0, 39.75);
                surface.getButton (ButtonID.PUNCH_IN).setBounds (705.0, 320.25, 65.0, 39.75);
                surface.getButton (ButtonID.PUNCH_OUT).setBounds (777.75, 320.25, 65.0, 39.75);
                surface.getButton (ButtonID.F1).setBounds (632.5, 178.25, 65.0, 39.75);
                surface.getButton (ButtonID.F2).setBounds (705.0, 178.25, 65.0, 39.75);
                surface.getButton (ButtonID.F3).setBounds (777.75, 178.25, 65.0, 39.75);
                surface.getButton (ButtonID.F4).setBounds (849.25, 178.25, 65.0, 39.75);
                surface.getButton (ButtonID.F5).setBounds (921.5, 178.25, 65.0, 39.75);
                surface.getButton (ButtonID.TRACK).setBounds (705.0, 225.25, 65.0, 39.75);
                surface.getButton (ButtonID.PAN_SEND).setBounds (777.75, 225.25, 65.0, 39.75);
                surface.getButton (ButtonID.SENDS).setBounds (849.25, 225.25, 65.0, 39.75);
                surface.getButton (ButtonID.DEVICE).setBounds (921.5, 225.25, 65.0, 39.75);
                surface.getButton (ButtonID.MOVE_TRACK_LEFT).setBounds (847.75, 542.0, 65.0, 39.75);
                surface.getButton (ButtonID.MOVE_TRACK_RIGHT).setBounds (921.0, 542.0, 65.0, 39.75);
                surface.getButton (ButtonID.AUTOMATION_READ).setBounds (632.5, 272.25, 65.0, 39.75);
                surface.getButton (ButtonID.AUTOMATION_WRITE).setBounds (705.0, 272.25, 30.0, 39.75);
                surface.getButton (ButtonID.AUTOMATION_GROUP).setBounds (740.0, 272.25, 30.0, 39.75);
                surface.getButton (ButtonID.AUTOMATION_TRIM).setBounds (777.75, 272.25, 65.0, 39.75);
                surface.getButton (ButtonID.AUTOMATION_TOUCH).setBounds (849.25, 272.25, 65.0, 39.75);
                surface.getButton (ButtonID.AUTOMATION_LATCH).setBounds (921.5, 272.25, 65.0, 39.75);
                surface.getButton (ButtonID.UNDO).setBounds (849.25, 320.25, 65.0, 39.75);
                surface.getButton (ButtonID.NOTE_EDITOR).setBounds (705.0, 366.75, 65.0, 39.75);
                surface.getButton (ButtonID.AUTOMATION_EDITOR).setBounds (777.75, 366.75, 65.0, 39.75);
                surface.getButton (ButtonID.TOGGLE_DEVICE).setBounds (849.25, 366.75, 65.0, 39.75);
                surface.getButton (ButtonID.MIXER).setBounds (921.5, 366.75, 65.0, 39.75);
                surface.getButton (ButtonID.LAYOUT_ARRANGE).setBounds (703.75, 591.0, 65.0, 39.75);
                surface.getButton (ButtonID.LAYOUT_MIX).setBounds (776.5, 591.0, 65.0, 39.75);
                surface.getButton (ButtonID.LAYOUT_EDIT).setBounds (847.75, 591.0, 65.0, 39.75);
                surface.getButton (ButtonID.BROWSE).setBounds (705.0, 416.75, 65.0, 39.75);
                surface.getButton (ButtonID.METRONOME).setBounds (777.75, 416.75, 65.0, 39.75);
                surface.getButton (ButtonID.GROOVE).setBounds (849.25, 416.75, 65.0, 39.75);
                surface.getButton (ButtonID.OVERDUB).setBounds (921.5, 416.75, 65.0, 39.75);
                surface.getButton (ButtonID.TAP_TEMPO).setBounds (481.25, 942.0, 65.0, 39.75);
                surface.getButton (ButtonID.DUPLICATE).setBounds (776.5, 492.75, 65.0, 39.75);
                surface.getButton (ButtonID.DEVICE_ON_OFF).setBounds (921.0, 590.0, 65.0, 39.75);
                surface.getButton (ButtonID.CONTROL).setBounds (921.0, 637.5, 65.0, 39.75);
                surface.getButton (ButtonID.ALT).setBounds (847.75, 637.5, 65.0, 39.75);
                surface.getButton (ButtonID.FLIP).setBounds (703.75, 492.75, 65.0, 39.75);
                surface.getButton (ButtonID.CANCEL).setBounds (847.75, 492.75, 65.0, 39.75);
                surface.getButton (ButtonID.ENTER).setBounds (921.0, 492.75, 65.0, 39.75);
                surface.getButton (ButtonID.MOVE_BANK_LEFT).setBounds (703.75, 542.0, 65.0, 39.75);
                surface.getButton (ButtonID.MOVE_BANK_RIGHT).setBounds (776.5, 542.0, 65.0, 39.75);
                surface.getButton (ButtonID.NEW).setBounds (392.0, 942.0, 77.75, 39.75);
                surface.getButton (ButtonID.SAVE).setBounds (921.5, 320.25, 65.0, 39.75);
                surface.getButton (ButtonID.MARKER).setBounds (632.5, 225.25, 65.0, 39.75);
                surface.getButton (ButtonID.TOGGLE_VU).setBounds (790.25, 92.5, 77.75, 39.75);

                surface.getContinuous (ContinuousID.PLAY_POSITION).setBounds (859.5, 806.5, 115.25, 115.75);
                surface.getContinuous (ContinuousID.FADER_MASTER).setBounds (613.5, 501.5, 65.0, 419.0);

                surface.getTextDisplay (2).getHardwareDisplay ().setBounds (699.0, 27.0, 263.25, 44.25);
                surface.getTextDisplay (3).getHardwareDisplay ().setBounds (633.5, 92.5, 49.5, 39.75);

                surface.getLight (OutputID.LED1).setBounds (968.5, 92.5, 21.25, 12.75);
                surface.getLight (OutputID.LED2).setBounds (968.5, 119.5, 21.25, 12.75);
            }

            surface.getButton (ButtonID.ROW2_1).setBounds (12.75, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_1).setBounds (12.75, 225.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_1).setBounds (12.75, 272.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_1).setBounds (12.75, 366.75, 65.0, 39.75);
            surface.getButton (ButtonID.ROW2_2).setBounds (87.25, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_2).setBounds (87.25, 225.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_2).setBounds (87.25, 272.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_2).setBounds (87.25, 366.75, 65.0, 39.75);
            surface.getButton (ButtonID.ROW2_3).setBounds (163.75, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_3).setBounds (163.75, 225.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_3).setBounds (163.75, 272.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_3).setBounds (163.75, 366.75, 65.0, 39.75);
            surface.getButton (ButtonID.ROW2_4).setBounds (237.0, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_4).setBounds (237.0, 225.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_4).setBounds (237.0, 272.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_4).setBounds (237.0, 366.75, 65.0, 39.75);
            surface.getButton (ButtonID.ROW2_5).setBounds (311.25, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_5).setBounds (311.25, 225.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_5).setBounds (311.25, 272.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_5).setBounds (311.25, 366.75, 65.0, 39.75);
            surface.getButton (ButtonID.ROW2_6).setBounds (386.5, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_6).setBounds (386.5, 225.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_6).setBounds (386.5, 272.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_6).setBounds (386.5, 366.75, 65.0, 39.75);
            surface.getButton (ButtonID.ROW2_7).setBounds (459.0, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_7).setBounds (459.0, 225.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_7).setBounds (459.0, 272.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_7).setBounds (459.0, 366.75, 65.0, 39.75);
            surface.getButton (ButtonID.ROW2_8).setBounds (532.25, 178.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW3_8).setBounds (532.25, 225.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW4_8).setBounds (532.25, 272.25, 65.0, 39.75);
            surface.getButton (ButtonID.ROW_SELECT_8).setBounds (532.25, 366.75, 65.0, 39.75);

            surface.getContinuous (ContinuousID.KNOB1).setBounds (12.25, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.KNOB2).setBounds (86.25, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.KNOB3).setBounds (160.0, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.KNOB4).setBounds (234.0, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.KNOB5).setBounds (308.0, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.KNOB6).setBounds (381.75, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.KNOB7).setBounds (455.75, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.KNOB8).setBounds (529.5, 93.5, 72.0, 74.75);
            surface.getContinuous (ContinuousID.FADER1).setBounds (12.5, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.FADER2).setBounds (87.25, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.FADER3).setBounds (163.75, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.FADER4).setBounds (237.0, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.FADER5).setBounds (311.25, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.FADER6).setBounds (386.5, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.FADER7).setBounds (459.0, 501.5, 65.0, 419.0);
            surface.getContinuous (ContinuousID.FADER8).setBounds (532.25, 501.5, 65.0, 419.0);

            surface.getTextDisplay (0).getHardwareDisplay ().setBounds (11.75, 11.75, 601.0, 73.25);
            surface.getTextDisplay (1).getHardwareDisplay ().setBounds (11.5, 419.5, 668.25, 73.25);
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

            surface.getViewManager ().setActive (Views.CONTROL);
            surface.getModeManager ().setActive (Modes.PAN);
        }
    }


    private void updateSegmentDisplay ()
    {
        if (!this.configuration.hasSegmentDisplay ())
            return;

        final ITransport t = this.model.getTransport ();

        String positionText = this.configuration.isDisplayTime () ? t.getPositionText () : t.getBeatText ();
        positionText = positionText.replace ('.', ':');

        if (this.configuration.isDisplayTicks ())
        {
            if (!this.configuration.isDisplayTime ())
                positionText += " ";
        }
        else
        {
            String tempoStr = t.formatTempoNoFraction (t.getTempo ());
            final int pos = positionText.lastIndexOf (':');
            if (tempoStr.length () < 3)
                tempoStr = "0" + tempoStr;
            positionText = positionText.substring (0, pos + 1) + tempoStr;
        }

        for (int index = 0; index < this.numMCUDevices; index++)
        {
            if (this.configuration.getDeviceType (index) == MCUDeviceType.MAIN)
                this.getSurface (index).getTextDisplay (2).setRow (0, positionText).allDone ();
        }
    }


    private void updateVUMeters ()
    {
        if (!this.configuration.isEnableVUMeters ())
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final boolean shouldPinFXTracksToLastController = this.configuration.shouldPinFXTracksToLastController ();

        for (int index = 0; index < this.numMCUDevices; index++)
        {
            final MCUControlSurface surface = this.getSurface (index);
            final IMidiOutput output = surface.getMidiOutput ();
            final boolean pinLastDevice = shouldPinFXTracksToLastController && index == this.numMCUDevices - 1;
            final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
            final ITrackBank trackBank = pinLastDevice && effectTrackBank != null ? effectTrackBank : tb;
            final int extenderOffset = pinLastDevice ? 0 : surface.getExtenderOffset ();
            for (int i = 0; i < 8; i++)
            {
                final int channel = extenderOffset + i;
                final ITrack track = trackBank.getItem (channel);

                final int vu = track.getVu ();
                if (vu != this.vuValues[channel])
                {
                    this.vuValues[channel] = vu;
                    this.sendVUValue (output, i, vu, false);
                }
            }

            // Stereo VU of master channel
            if (this.configuration.getDeviceType (index) == MCUDeviceType.MAIN && this.configuration.hasMasterVU ())
            {
                final IMasterTrack masterTrack = this.model.getMasterTrack ();

                int vu = masterTrack.getVuLeft ();
                if (vu != this.masterVuValues[0])
                {
                    this.masterVuValues[0] = vu;
                    this.sendVUValue (output, 0, vu, true);
                }

                vu = masterTrack.getVuRight ();
                if (vu != this.masterVuValues[1])
                {
                    this.masterVuValues[1] = vu;
                    this.sendVUValue (output, 1, vu, true);
                }
            }
        }
    }


    private void sendVUValue (final IMidiOutput output, final int track, final int vu, final boolean isMaster)
    {
        final int scaledValue = (int) Math.round (this.valueChanger.toNormalizedValue (vu) * 13);
        output.sendChannelAftertouch (isMaster ? 1 : 0, 0x10 * track + scaledValue, 0);
    }


    private void updateFaders (final boolean isShiftPressed)
    {
        if (!this.configuration.hasMotorFaders ())
            return;

        final Modes activeMode = this.getSurface ().getModeManager ().getActiveID ();
        final Modes modeId = this.configuration.useFadersAsKnobs () && VALUE_MODES.contains (activeMode) ? activeMode : Modes.VOLUME;

        for (int index = 0; index < this.numMCUDevices; index++)
        {
            final MCUControlSurface surface = this.getSurface (index);
            final IMode mode = surface.getModeManager ().get (modeId);
            final IMidiOutput output = surface.getMidiOutput ();
            for (int channel = 0; channel < 8; channel++)
            {
                // Don't update fader if the user touches and therefore 'stops' it
                if (mode.isKnobTouched (channel))
                    continue;

                final int value = Math.max (0, mode.getKnobValue (channel));
                final int position = surface.getExtenderOffset () + channel;
                if (value != this.faderValues[position])
                {
                    this.faderValues[position] = value;
                    output.sendPitchbend (channel, value % 127, value / 127);
                }
            }

            // Update motor fader of master channel
            if (this.configuration.getDeviceType (index) == MCUDeviceType.MAIN)
            {
                final int volume = isShiftPressed ? this.model.getTransport ().getMetronomeVolume () : this.model.getMasterTrack ().getVolume ();
                if (volume != this.masterFaderValue)
                {
                    this.masterFaderValue = volume;
                    output.sendPitchbend (8, volume % 127, volume / 127);
                }
            }
        }
    }


    private void updateMode (final Modes mode)
    {
        if (mode == null || !this.configuration.hasAssignmentDisplay ())
            return;

        for (int index = 0; index < this.numMCUDevices; index++)
        {
            if (this.configuration.getDeviceType (index) == MCUDeviceType.MAIN)
                this.getSurface (index).getTextDisplay (3).setRow (0, MODE_ACRONYMS.get (mode)).allDone ();
        }
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        return BindType.NOTE;
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
        if (modeManager.isActive (Modes.MASTER))
            modeManager.setActive (Modes.TRACK);
    }


    private static int getButtonColor (final MCUControlSurface surface, final ButtonID buttonID)
    {
        final IMode mode = surface.getModeManager ().getActive ();
        return mode == null ? 0 : mode.getButtonColor (buttonID);
    }
}
