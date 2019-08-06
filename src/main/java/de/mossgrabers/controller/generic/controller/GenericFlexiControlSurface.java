// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.controller;

import de.mossgrabers.controller.generic.CommandSlot;
import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.command.trigger.ToggleKnobSpeedCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.track.ToggleTrackBanksCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.Relative2ValueChanger;
import de.mossgrabers.framework.controller.Relative3ValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * The Generic Flexi.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GenericFlexiControlSurface extends AbstractControlSurface<GenericFlexiConfiguration>
{
    private static final int                                                                    KNOB_MODE_ABSOLUTE        = 0;
    private static final int                                                                    KNOB_MODE_RELATIVE1       = 1;
    private static final int                                                                    KNOB_MODE_RELATIVE2       = 2;
    private static final int                                                                    KNOB_MODE_RELATIVE3       = 3;
    private static final int                                                                    KNOB_MODE_ABSOLUTE_TOGGLE = 4;

    protected static final int                                                                  SCROLL_RATE               = 6;
    private static final List<Modes>                                                            MODE_IDS                  = new ArrayList<> ();

    private int                                                                                 movementCounter           = 0;
    private boolean                                                                             isShiftButtonPressed      = false;

    private final IModel                                                                        model;
    private final IValueChanger                                                                 relative2ValueChanger     = new Relative2ValueChanger (128, 6, 1);
    private final IValueChanger                                                                 relative3ValueChanger     = new Relative3ValueChanger (128, 6, 1);
    private final int []                                                                        valueCache                = new int [GenericFlexiConfiguration.NUM_SLOTS];
    private boolean                                                                             isUpdatingValue           = false;
    private final TriggerCommand                                                                toggleTrackBankCommand;

    private final WindCommand<GenericFlexiControlSurface, GenericFlexiConfiguration>            rwdCommand;
    private final WindCommand<GenericFlexiControlSurface, GenericFlexiConfiguration>            ffwdCommand;
    private final PlayCommand<GenericFlexiControlSurface, GenericFlexiConfiguration>            playCommand;
    private final ToggleKnobSpeedCommand<GenericFlexiControlSurface, GenericFlexiConfiguration> knobSpeedCommand;

    static
    {
        MODE_IDS.add (Modes.TRACK);
        MODE_IDS.add (Modes.VOLUME);
        MODE_IDS.add (Modes.PAN);
        MODE_IDS.add (Modes.SEND1);
        MODE_IDS.add (Modes.SEND2);
        MODE_IDS.add (Modes.SEND3);
        MODE_IDS.add (Modes.SEND4);
        MODE_IDS.add (Modes.SEND5);
        MODE_IDS.add (Modes.SEND6);
        MODE_IDS.add (Modes.SEND7);
        MODE_IDS.add (Modes.SEND8);
        MODE_IDS.add (Modes.DEVICE_PARAMS);
    }


    /**
     * Constructor.
     *
     * @param host The host
     * @param model
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     */
    public GenericFlexiControlSurface (final IHost host, final IModel model, final ColorManager colorManager, final GenericFlexiConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, null);

        Arrays.fill (this.valueCache, -1);
        this.model = model;

        this.toggleTrackBankCommand = new ToggleTrackBanksCommand<> (model, this);

        this.configuration.addSettingObserver (GenericFlexiConfiguration.BUTTON_EXPORT, this::importFile);
        this.configuration.addSettingObserver (GenericFlexiConfiguration.BUTTON_IMPORT, this::exportFile);

        this.rwdCommand = new WindCommand<> (this.model, this, false);
        this.ffwdCommand = new WindCommand<> (this.model, this, true);
        this.playCommand = new PlayCommand<> (this.model, this);
        this.knobSpeedCommand = new ToggleKnobSpeedCommand<> (this.model, this);

        this.input.setSysexCallback (this::handleSysEx);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShiftPressed ()
    {
        return this.isShiftButtonPressed;
    }


    private void handleSysEx (final String dataStr)
    {
        final int [] data = StringUtils.fromHexStr (dataStr);
        if (data.length != 6 || data[0] != 0xF0 || data[1] != 0x7F || data[3] != 0x06 || data[5] != 0xF7)
            return;

        // This is not (fully) correct but at least supports 16 device IDs (and 7F for ignore)
        final int channel = data[2] % 16;
        final int number = data[4];

        this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_MMC + 1], number, channel);
        final int slotIndex = this.configuration.getSlotCommand (CommandSlot.TYPE_MMC, number, channel);
        if (slotIndex == -1)
            return;
        this.handleCommand (slotIndex, 127);
        this.handleCommand (slotIndex, 0);
    }


    /**
     * Export all settings to a file.
     */
    private void exportFile ()
    {
        final File file = this.getFile ();
        if (file == null)
            return;
        if (!file.exists ())
        {
            this.host.showNotification ("The entered file does not exist.");
            return;
        }
        try
        {
            this.configuration.importFrom (file);
            this.host.showNotification ("Imported from: " + file);
        }
        catch (final IOException ex)
        {
            this.host.showNotification ("Error reading file: " + ex.getMessage ());
        }
    }


    /**
     * Import all settings from a file.
     */
    private void importFile ()
    {
        final File file = this.getFile ();
        if (file == null)
            return;
        try
        {
            this.configuration.exportTo (file);
            this.host.showNotification ("Exported to: " + file);
        }
        catch (final IOException ex)
        {
            this.host.showNotification ("Error writing file: " + ex.getMessage ());
        }
    }


    /**
     * Get a the im-/export file.
     *
     * @return The file or null
     */
    private File getFile ()
    {
        final String filename = this.configuration.getFilename ();
        if (filename == null || filename.trim ().isEmpty ())
        {
            this.host.showNotification ("Please enter a filename first.");
            return null;
        }
        return new File (filename);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        if (this.isUpdatingValue)
            return;

        final CommandSlot [] slots = this.configuration.getCommandSlots ();
        for (int i = 0; i < slots.length; i++)
        {
            final FlexiCommand command = slots[i].getCommand ();
            if (command == FlexiCommand.OFF || !slots[i].isSendValue ())
                continue;
            final int value = this.getCommandValue (command);
            if (this.valueCache[i] == value)
                continue;
            this.valueCache[i] = value;
            this.reflectValue (slots[i], value);
        }
    }


    /**
     * Get the current value of a command.
     *
     * @param command The command
     * @return The value or -1
     */
    public int getCommandValue (final FlexiCommand command)
    {
        final Mode mode = this.modeManager.getActiveOrTempMode ();
        switch (command)
        {
            case GLOBAL_TOGGLE_AUDIO_ENGINE:
                return this.model.getApplication ().isEngineActive () ? 127 : 0;

            case GLOBAL_SHIFT_BUTTON:
                return this.isShiftPressed () ? 127 : 0;

            case TRANSPORT_PLAY:
                return this.model.getTransport ().isPlaying () ? 127 : 0;

            case TRANSPORT_STOP:
                return this.model.getTransport ().isPlaying () ? 0 : 127;

            case TRANSPORT_TOGGLE_REPEAT:
                return this.model.getTransport ().isLoop () ? 127 : 0;

            case TRANSPORT_TOGGLE_METRONOME:
                return this.model.getTransport ().isMetronomeOn () ? 127 : 0;

            case TRANSPORT_SET_METRONOME_VOLUME:
                return this.model.getTransport ().getMetronomeVolume ();

            case TRANSPORT_TOGGLE_METRONOME_IN_PREROLL:
                return this.model.getTransport ().isPrerollMetronomeEnabled () ? 127 : 0;

            case TRANSPORT_TOGGLE_PUNCH_IN:
                return this.model.getTransport ().isPunchInEnabled () ? 127 : 0;

            case TRANSPORT_TOGGLE_PUNCH_OUT:
                return this.model.getTransport ().isPunchOutEnabled () ? 127 : 0;

            case TRANSPORT_TOGGLE_RECORD:
                return this.model.getTransport ().isRecording () ? 127 : 0;

            case TRANSPORT_TOGGLE_ARRANGER_OVERDUB:
                return this.model.getTransport ().isArrangerOverdub () ? 127 : 0;

            case TRANSPORT_TOGGLE_CLIP_OVERDUB:
                return this.model.getTransport ().isLauncherOverdub () ? 127 : 0;

            case TRANSPORT_TOGGLE_ARRANGER_AUTOMATION_WRITE:
                return this.model.getTransport ().isWritingArrangerAutomation () ? 127 : 0;

            case TRANSPORT_TOGGLE_CLIP_AUTOMATION_WRITE:
                return this.model.getTransport ().isWritingClipLauncherAutomation () ? 127 : 0;

            case TRACK_1_SELECT:
            case TRACK_2_SELECT:
            case TRACK_3_SELECT:
            case TRACK_4_SELECT:
            case TRACK_5_SELECT:
            case TRACK_6_SELECT:
            case TRACK_7_SELECT:
            case TRACK_8_SELECT:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SELECT.ordinal ()).isSelected () ? 127 : 0;

            case TRACK_1_TOGGLE_ACTIVE:
            case TRACK_2_TOGGLE_ACTIVE:
            case TRACK_3_TOGGLE_ACTIVE:
            case TRACK_4_TOGGLE_ACTIVE:
            case TRACK_5_TOGGLE_ACTIVE:
            case TRACK_6_TOGGLE_ACTIVE:
            case TRACK_7_TOGGLE_ACTIVE:
            case TRACK_8_TOGGLE_ACTIVE:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_ACTIVE.ordinal ()).isActivated () ? 127 : 0;
            case TRACK_1_SET_ACTIVE:
            case TRACK_2_SET_ACTIVE:
            case TRACK_3_SET_ACTIVE:
            case TRACK_4_SET_ACTIVE:
            case TRACK_5_SET_ACTIVE:
            case TRACK_6_SET_ACTIVE:
            case TRACK_7_SET_ACTIVE:
            case TRACK_8_SET_ACTIVE:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_ACTIVE.ordinal ()).isActivated () ? 127 : 0;

            case TRACK_SELECTED_TOGGLE_ACTIVE:
            case TRACK_SELECTED_SET_ACTIVE:
                final ITrack selectedTrack = this.model.getSelectedTrack ();
                return selectedTrack != null && selectedTrack.isActivated () ? 127 : 0;

            case TRACK_1_SET_VOLUME:
            case TRACK_2_SET_VOLUME:
            case TRACK_3_SET_VOLUME:
            case TRACK_4_SET_VOLUME:
            case TRACK_5_SET_VOLUME:
            case TRACK_6_SET_VOLUME:
            case TRACK_7_SET_VOLUME:
            case TRACK_8_SET_VOLUME:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_VOLUME.ordinal ()).getVolume ();

            case TRACK_SELECTED_SET_VOLUME_TRACK:
                final ITrack sel = this.model.getSelectedTrack ();
                return sel == null ? 0 : sel.getVolume ();

            case TRACK_1_SET_PANORAMA:
            case TRACK_2_SET_PANORAMA:
            case TRACK_3_SET_PANORAMA:
            case TRACK_4_SET_PANORAMA:
            case TRACK_5_SET_PANORAMA:
            case TRACK_6_SET_PANORAMA:
            case TRACK_7_SET_PANORAMA:
            case TRACK_8_SET_PANORAMA:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_PANORAMA.ordinal ()).getPan ();

            case TRACK_SELECTED_SET_PANORAMA:
                final ITrack selTrack = this.model.getSelectedTrack ();
                return selTrack == null ? 0 : selTrack.getPan ();

            case TRACK_1_TOGGLE_MUTE:
            case TRACK_2_TOGGLE_MUTE:
            case TRACK_3_TOGGLE_MUTE:
            case TRACK_4_TOGGLE_MUTE:
            case TRACK_5_TOGGLE_MUTE:
            case TRACK_6_TOGGLE_MUTE:
            case TRACK_7_TOGGLE_MUTE:
            case TRACK_8_TOGGLE_MUTE:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_MUTE.ordinal ()).isMute () ? 127 : 0;
            case TRACK_1_SET_MUTE:
            case TRACK_2_SET_MUTE:
            case TRACK_3_SET_MUTE:
            case TRACK_4_SET_MUTE:
            case TRACK_5_SET_MUTE:
            case TRACK_6_SET_MUTE:
            case TRACK_7_SET_MUTE:
            case TRACK_8_SET_MUTE:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_MUTE.ordinal ()).isMute () ? 127 : 0;

            case TRACK_SELECTED_TOGGLE_MUTE:
            case TRACK_SELECTED_SET_MUTE:
                final ITrack track = this.model.getSelectedTrack ();
                return track != null && track.isMute () ? 127 : 0;

            case TRACK_1_TOGGLE_SOLO:
            case TRACK_2_TOGGLE_SOLO:
            case TRACK_3_TOGGLE_SOLO:
            case TRACK_4_TOGGLE_SOLO:
            case TRACK_5_TOGGLE_SOLO:
            case TRACK_6_TOGGLE_SOLO:
            case TRACK_7_TOGGLE_SOLO:
            case TRACK_8_TOGGLE_SOLO:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_SOLO.ordinal ()).isSolo () ? 127 : 0;
            case TRACK_1_SET_SOLO:
            case TRACK_2_SET_SOLO:
            case TRACK_3_SET_SOLO:
            case TRACK_4_SET_SOLO:
            case TRACK_5_SET_SOLO:
            case TRACK_6_SET_SOLO:
            case TRACK_7_SET_SOLO:
            case TRACK_8_SET_SOLO:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_SOLO.ordinal ()).isSolo () ? 127 : 0;

            case TRACK_SELECTED_TOGGLE_SOLO:
            case TRACK_SELECTED_SET_SOLO:
                final ITrack track2 = this.model.getSelectedTrack ();
                return track2 != null && track2.isSolo () ? 127 : 0;

            case TRACK_1_TOGGLE_ARM:
            case TRACK_2_TOGGLE_ARM:
            case TRACK_3_TOGGLE_ARM:
            case TRACK_4_TOGGLE_ARM:
            case TRACK_5_TOGGLE_ARM:
            case TRACK_6_TOGGLE_ARM:
            case TRACK_7_TOGGLE_ARM:
            case TRACK_8_TOGGLE_ARM:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_ARM.ordinal ()).isRecArm () ? 127 : 0;
            case TRACK_1_SET_ARM:
            case TRACK_2_SET_ARM:
            case TRACK_3_SET_ARM:
            case TRACK_4_SET_ARM:
            case TRACK_5_SET_ARM:
            case TRACK_6_SET_ARM:
            case TRACK_7_SET_ARM:
            case TRACK_8_SET_ARM:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_ARM.ordinal ()).isRecArm () ? 127 : 0;

            case TRACK_SELECTED_TOGGLE_ARM:
            case TRACK_SELECTED_SET_ARM:
                final ITrack track3 = this.model.getSelectedTrack ();
                return track3 != null && track3.isRecArm () ? 127 : 0;

            case TRACK_1_TOGGLE_MONITOR:
            case TRACK_2_TOGGLE_MONITOR:
            case TRACK_3_TOGGLE_MONITOR:
            case TRACK_4_TOGGLE_MONITOR:
            case TRACK_5_TOGGLE_MONITOR:
            case TRACK_6_TOGGLE_MONITOR:
            case TRACK_7_TOGGLE_MONITOR:
            case TRACK_8_TOGGLE_MONITOR:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_MONITOR.ordinal ()).isMonitor () ? 127 : 0;
            case TRACK_1_SET_MONITOR:
            case TRACK_2_SET_MONITOR:
            case TRACK_3_SET_MONITOR:
            case TRACK_4_SET_MONITOR:
            case TRACK_5_SET_MONITOR:
            case TRACK_6_SET_MONITOR:
            case TRACK_7_SET_MONITOR:
            case TRACK_8_SET_MONITOR:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_MONITOR.ordinal ()).isMonitor () ? 127 : 0;

            case TRACK_SELECTED_TOGGLE_MONITOR:
            case TRACK_SELECTED_SET_MONITOR:
                final ITrack track4 = this.model.getSelectedTrack ();
                return track4 != null && track4.isMonitor () ? 127 : 0;

            case TRACK_1_TOGGLE_AUTO_MONITOR:
            case TRACK_2_TOGGLE_AUTO_MONITOR:
            case TRACK_3_TOGGLE_AUTO_MONITOR:
            case TRACK_4_TOGGLE_AUTO_MONITOR:
            case TRACK_5_TOGGLE_AUTO_MONITOR:
            case TRACK_6_TOGGLE_AUTO_MONITOR:
            case TRACK_7_TOGGLE_AUTO_MONITOR:
            case TRACK_8_TOGGLE_AUTO_MONITOR:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_AUTO_MONITOR.ordinal ()).isAutoMonitor () ? 127 : 0;
            case TRACK_1_SET_AUTO_MONITOR:
            case TRACK_2_SET_AUTO_MONITOR:
            case TRACK_3_SET_AUTO_MONITOR:
            case TRACK_4_SET_AUTO_MONITOR:
            case TRACK_5_SET_AUTO_MONITOR:
            case TRACK_6_SET_AUTO_MONITOR:
            case TRACK_7_SET_AUTO_MONITOR:
            case TRACK_8_SET_AUTO_MONITOR:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_AUTO_MONITOR.ordinal ()).isAutoMonitor () ? 127 : 0;

            case TRACK_SELECTED_TOGGLE_AUTO_MONITOR:
            case TRACK_SELECTED_SET_AUTO_MONITOR:
                final ITrack track5 = this.model.getSelectedTrack ();
                return track5 != null && track5.isAutoMonitor () ? 127 : 0;

            case TRACK_1_SET_SEND_1:
            case TRACK_2_SET_SEND_1:
            case TRACK_3_SET_SEND_1:
            case TRACK_4_SET_SEND_1:
            case TRACK_5_SET_SEND_1:
            case TRACK_6_SET_SEND_1:
            case TRACK_7_SET_SEND_1:
            case TRACK_8_SET_SEND_1:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_1.ordinal (), 0);

            case TRACK_1_SET_SEND_2:
            case TRACK_2_SET_SEND_2:
            case TRACK_3_SET_SEND_2:
            case TRACK_4_SET_SEND_2:
            case TRACK_5_SET_SEND_2:
            case TRACK_6_SET_SEND_2:
            case TRACK_7_SET_SEND_2:
            case TRACK_8_SET_SEND_2:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_2.ordinal (), 1);

            case TRACK_1_SET_SEND_3:
            case TRACK_2_SET_SEND_3:
            case TRACK_3_SET_SEND_3:
            case TRACK_4_SET_SEND_3:
            case TRACK_5_SET_SEND_3:
            case TRACK_6_SET_SEND_3:
            case TRACK_7_SET_SEND_3:
            case TRACK_8_SET_SEND_3:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_3.ordinal (), 2);

            case TRACK_1_SET_SEND_4:
            case TRACK_2_SET_SEND_4:
            case TRACK_3_SET_SEND_4:
            case TRACK_4_SET_SEND_4:
            case TRACK_5_SET_SEND_4:
            case TRACK_6_SET_SEND_4:
            case TRACK_7_SET_SEND_4:
            case TRACK_8_SET_SEND_4:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_4.ordinal (), 3);

            case TRACK_1_SET_SEND_5:
            case TRACK_2_SET_SEND_5:
            case TRACK_3_SET_SEND_5:
            case TRACK_4_SET_SEND_5:
            case TRACK_5_SET_SEND_5:
            case TRACK_6_SET_SEND_5:
            case TRACK_7_SET_SEND_5:
            case TRACK_8_SET_SEND_5:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_5.ordinal (), 4);

            case TRACK_1_SET_SEND_6:
            case TRACK_2_SET_SEND_6:
            case TRACK_3_SET_SEND_6:
            case TRACK_4_SET_SEND_6:
            case TRACK_5_SET_SEND_6:
            case TRACK_6_SET_SEND_6:
            case TRACK_7_SET_SEND_6:
            case TRACK_8_SET_SEND_6:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_6.ordinal (), 5);

            case TRACK_1_SET_SEND_7:
            case TRACK_2_SET_SEND_7:
            case TRACK_3_SET_SEND_7:
            case TRACK_4_SET_SEND_7:
            case TRACK_5_SET_SEND_7:
            case TRACK_6_SET_SEND_7:
            case TRACK_7_SET_SEND_7:
            case TRACK_8_SET_SEND_7:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_7.ordinal (), 6);

            case TRACK_1_SET_SEND_8:
            case TRACK_2_SET_SEND_8:
            case TRACK_3_SET_SEND_8:
            case TRACK_4_SET_SEND_8:
            case TRACK_5_SET_SEND_8:
            case TRACK_6_SET_SEND_8:
            case TRACK_7_SET_SEND_8:
            case TRACK_8_SET_SEND_8:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_8.ordinal (), 7);

            case TRACK_SELECTED_SET_SEND_1:
            case TRACK_SELECTED_SET_SEND_2:
            case TRACK_SELECTED_SET_SEND_3:
            case TRACK_SELECTED_SET_SEND_4:
            case TRACK_SELECTED_SET_SEND_5:
            case TRACK_SELECTED_SET_SEND_6:
            case TRACK_SELECTED_SET_SEND_7:
            case TRACK_SELECTED_SET_SEND_8:
                return this.getSendValue (-1, command.ordinal () - FlexiCommand.TRACK_SELECTED_SET_SEND_1.ordinal ());

            case MASTER_SET_VOLUME:
                return this.model.getMasterTrack ().getVolume ();

            case MASTER_SET_PANORAMA:
                return this.model.getMasterTrack ().getPan ();

            case MASTER_TOGGLE_MUTE:
            case MASTER_SET_MUTE:
                return this.model.getMasterTrack ().isMute () ? 127 : 0;

            case MASTER_TOGGLE_SOLO:
            case MASTER_SET_SOLO:
                return this.model.getMasterTrack ().isSolo () ? 127 : 0;

            case MASTER_TOGGLE_ARM:
            case MASTER_SET_ARM:
                return this.model.getMasterTrack ().isRecArm () ? 127 : 0;

            case MASTER_CROSSFADER:
                return this.model.getTransport ().getCrossfade ();

            case DEVICE_TOGGLE_WINDOW:
                return this.model.getCursorDevice ().isWindowOpen () ? 127 : 0;

            case DEVICE_BYPASS:
                return this.model.getCursorDevice ().isEnabled () ? 0 : 127;

            case DEVICE_EXPAND:
                return this.model.getCursorDevice ().isExpanded () ? 127 : 0;

            case DEVICE_TOGGLE_PARAMETERS:
                return this.model.getCursorDevice ().isParameterPageSectionVisible () ? 127 : 0;

            case DEVICE_SET_PARAMETER_1:
            case DEVICE_SET_PARAMETER_2:
            case DEVICE_SET_PARAMETER_3:
            case DEVICE_SET_PARAMETER_4:
            case DEVICE_SET_PARAMETER_5:
            case DEVICE_SET_PARAMETER_6:
            case DEVICE_SET_PARAMETER_7:
            case DEVICE_SET_PARAMETER_8:
                return this.model.getCursorDevice ().getParameterBank ().getItem (command.ordinal () - FlexiCommand.DEVICE_SET_PARAMETER_1.ordinal ()).getValue ();

            case CLIP_PLAY:
                final ISlot selectedSlot = this.model.getSelectedSlot ();
                return selectedSlot != null && selectedSlot.isPlaying () ? 127 : 0;

            case CLIP_STOP:
                final ISlot selectedSlot2 = this.model.getSelectedSlot ();
                return selectedSlot2 != null && selectedSlot2.isPlaying () ? 0 : 127;

            case CLIP_RECORD:
                final ISlot selectedSlot3 = this.model.getSelectedSlot ();
                return selectedSlot3 != null && selectedSlot3.isRecording () ? 127 : 0;

            case MODES_KNOB1:
                return mode == null ? 0 : mode.getKnobValue (0);
            case MODES_KNOB2:
                return mode == null ? 0 : mode.getKnobValue (1);
            case MODES_KNOB3:
                return mode == null ? 0 : mode.getKnobValue (2);
            case MODES_KNOB4:
                return mode == null ? 0 : mode.getKnobValue (3);
            case MODES_KNOB5:
                return mode == null ? 0 : mode.getKnobValue (4);
            case MODES_KNOB6:
                return mode == null ? 0 : mode.getKnobValue (5);
            case MODES_KNOB7:
                return mode == null ? 0 : mode.getKnobValue (6);
            case MODES_KNOB8:
                return mode == null ? 0 : mode.getKnobValue (7);

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void handleMidi (final int status, final int data1, final int data2)
    {
        final int code = status & 0xF0;
        final int channel = status & 0xF;

        int slotIndex = -1;
        int value = data2;

        switch (code)
        {
            // Note on/off
            case 0x90:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_NOTE + 1], data1, channel);
                slotIndex = this.configuration.getSlotCommand (CommandSlot.TYPE_NOTE, data1, channel);
                break;

            // Program Change
            case 0xC0:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_PROGRAM_CHANGE + 1], data1, channel);
                slotIndex = this.configuration.getSlotCommand (CommandSlot.TYPE_PROGRAM_CHANGE, data1, channel);
                value = 127;
                break;

            // CC
            case 0xB0:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_CC + 1], data1, channel);
                slotIndex = this.configuration.getSlotCommand (CommandSlot.TYPE_CC, data1, channel);
                break;

            // Pitchbend
            case 0xE0:
                this.configuration.setLearnValues (GenericFlexiConfiguration.OPTIONS_TYPE[CommandSlot.TYPE_PITCH_BEND + 1], data1, channel);
                slotIndex = this.configuration.getSlotCommand (CommandSlot.TYPE_PITCH_BEND, data1, channel);
                break;

            default:
                // Not used
                break;
        }

        if (slotIndex != -1)
            this.handleCommand (slotIndex, value);
    }


    /**
     * Update the key translation table.
     */
    public void updateKeyTranslation ()
    {
        this.setKeyTranslationTable (this.configuration.getNoteMap ());
    }


    private void handleCommand (final int slotIndex, final int value)
    {
        this.isUpdatingValue = true;

        final CommandSlot commandSlot = this.configuration.getCommandSlots ()[slotIndex];
        final FlexiCommand command = commandSlot.getCommand ();
        final Mode mode = this.modeManager.getActiveOrTempMode ();
        if (mode == null)
            return;

        final int knobMode = commandSlot.getKnobMode ();
        final boolean isButtonPressed = knobMode == KNOB_MODE_ABSOLUTE_TOGGLE || knobMode == KNOB_MODE_ABSOLUTE && value > 0;

        switch (command)
        {
            case OFF:
                // No function
                break;

            // Global: Undo
            case GLOBAL_UNDO:
                if (isButtonPressed)
                    this.model.getApplication ().undo ();
                break;
            // Global: Redo
            case GLOBAL_REDO:
                if (isButtonPressed)
                    this.model.getApplication ().redo ();
                break;
            // Global: Previous Project
            case GLOBAL_PREVIOUS_PROJECT:
                if (isButtonPressed)
                    this.model.getProject ().previous ();
                break;
            // Global: Next Project
            case GLOBAL_NEXT_PROJECT:
                if (isButtonPressed)
                    this.model.getProject ().next ();
                break;
            // Global: Toggle Audio Engine
            case GLOBAL_TOGGLE_AUDIO_ENGINE:
                if (isButtonPressed)
                    this.model.getApplication ().toggleEngineActive ();
                break;
            // Global: Shift Button
            case GLOBAL_SHIFT_BUTTON:
                this.isShiftButtonPressed = isButtonPressed;
                this.knobSpeedCommand.execute (isButtonPressed ? ButtonEvent.DOWN : ButtonEvent.UP);
                break;

            // Transport: Play
            case TRANSPORT_PLAY:
                this.playCommand.execute (isButtonPressed ? ButtonEvent.DOWN : ButtonEvent.UP);
                break;
            // Transport: Stop
            case TRANSPORT_STOP:
                if (isButtonPressed)
                    this.model.getTransport ().stop ();
                break;
            // Transport: Restart
            case TRANSPORT_RESTART:
                if (isButtonPressed)
                    this.model.getTransport ().restart ();
                break;
            case TRANSPORT_REWIND:
                this.rwdCommand.execute (isButtonPressed ? ButtonEvent.DOWN : ButtonEvent.UP);
                break;
            case TRANSPORT_FAST_FORWARD:
                this.ffwdCommand.execute (isButtonPressed ? ButtonEvent.DOWN : ButtonEvent.UP);
                break;
            // Transport: Toggle Repeat
            case TRANSPORT_TOGGLE_REPEAT:
                if (isButtonPressed)
                    this.model.getTransport ().toggleLoop ();
                break;
            // Transport: Toggle Metronome
            case TRANSPORT_TOGGLE_METRONOME:
                if (isButtonPressed)
                    this.model.getTransport ().toggleMetronome ();
                break;
            // Transport: Set Metronome Volume
            case TRANSPORT_SET_METRONOME_VOLUME:
                this.handleMetronomeVolume (knobMode, value);
                break;
            // Transport: Toggle Metronome in Pre-roll
            case TRANSPORT_TOGGLE_METRONOME_IN_PREROLL:
                if (isButtonPressed)
                    this.model.getTransport ().togglePrerollMetronome ();
                break;
            // Transport: Toggle Punch In
            case TRANSPORT_TOGGLE_PUNCH_IN:
                if (isButtonPressed)
                    this.model.getTransport ().togglePunchIn ();
                break;
            // Transport: Toggle Punch Out
            case TRANSPORT_TOGGLE_PUNCH_OUT:
                if (isButtonPressed)
                    this.model.getTransport ().togglePunchOut ();
                break;
            // Transport: Toggle Record
            case TRANSPORT_TOGGLE_RECORD:
                if (isButtonPressed)
                    this.model.getTransport ().record ();
                break;
            // Transport: Toggle Arranger Overdub
            case TRANSPORT_TOGGLE_ARRANGER_OVERDUB:
                if (isButtonPressed)
                    this.model.getTransport ().toggleOverdub ();
                break;
            // Transport: Toggle Clip Overdub
            case TRANSPORT_TOGGLE_CLIP_OVERDUB:
                if (isButtonPressed)
                    this.model.getTransport ().toggleLauncherOverdub ();
                break;
            // Transport: Toggle Arranger Automation Write
            case TRANSPORT_TOGGLE_ARRANGER_AUTOMATION_WRITE:
                if (isButtonPressed)
                    this.model.getTransport ().toggleWriteArrangerAutomation ();
                break;
            // Transport: Toggle Clip Automation Write
            case TRANSPORT_TOGGLE_CLIP_AUTOMATION_WRITE:
                if (isButtonPressed)
                    this.model.getTransport ().toggleWriteClipLauncherAutomation ();
                break;
            // Transport: Set Write Mode: Latch
            case TRANSPORT_SET_WRITE_MODE_LATCH:
                if (isButtonPressed)
                    this.model.getTransport ().setAutomationWriteMode (TransportConstants.AUTOMATION_MODES_VALUES[0]);
                break;
            // Transport: Set Write Mode: Touch
            case TRANSPORT_SET_WRITE_MODE_TOUCH:
                if (isButtonPressed)
                    this.model.getTransport ().setAutomationWriteMode (TransportConstants.AUTOMATION_MODES_VALUES[1]);
                break;
            // Transport: Set Write Mode: Write
            case TRANSPORT_SET_WRITE_MODE_WRITE:
                if (isButtonPressed)
                    this.model.getTransport ().setAutomationWriteMode (TransportConstants.AUTOMATION_MODES_VALUES[2]);
                break;
            // Transport: Set Tempo
            case TRANSPORT_SET_TEMPO:
                this.handleTempo (knobMode, value);
                break;
            // Transport: Tap Tempo
            case TRANSPORT_TAP_TEMPO:
                if (isButtonPressed)
                    this.model.getTransport ().tapTempo ();
                break;
            // Transport: Move Play Cursor
            case TRANSPORT_MOVE_PLAY_CURSOR:
                this.handlePlayCursor (knobMode, value);
                break;

            // Layout: Set Arrange Layout
            case LAYOUT_SET_ARRANGE_LAYOUT:
                if (isButtonPressed)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_ARRANGE);
                break;
            // Layout: Set Mix Layout
            case LAYOUT_SET_MIX_LAYOUT:
                if (isButtonPressed)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_MIX);
                break;
            // Layout: Set Edit Layout
            case LAYOUT_SET_EDIT_LAYOUT:
                if (isButtonPressed)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_EDIT);
                break;
            // Layout: Toggle Note Editor
            case LAYOUT_TOGGLE_NOTE_EDITOR:
                if (isButtonPressed)
                    this.model.getApplication ().toggleNoteEditor ();
                break;
            // Layout: Toggle Automation Editor
            case LAYOUT_TOGGLE_AUTOMATION_EDITOR:
                if (isButtonPressed)
                    this.model.getApplication ().toggleAutomationEditor ();
                break;
            // Layout: Toggle Devices Panel
            case LAYOUT_TOGGLE_DEVICES_PANEL:
                if (isButtonPressed)
                    this.model.getApplication ().toggleDevices ();
                break;
            // Layout: Toggle Mixer Panel
            case LAYOUT_TOGGLE_MIXER_PANEL:
                if (isButtonPressed)
                    this.model.getApplication ().toggleMixer ();
                break;
            // Layout: Toggle Fullscreen
            case LAYOUT_TOGGLE_FULLSCREEN:
                if (isButtonPressed)
                    this.model.getApplication ().toggleFullScreen ();
                break;
            // Layout: Toggle Arranger Cue Markers
            case LAYOUT_TOGGLE_ARRANGER_CUE_MARKERS:
                if (isButtonPressed)
                    this.model.getArranger ().toggleCueMarkerVisibility ();
                break;
            // Layout: Toggle Arranger Playback Follow
            case LAYOUT_TOGGLE_ARRANGER_PLAYBACK_FOLLOW:
                if (isButtonPressed)
                    this.model.getArranger ().togglePlaybackFollow ();
                break;
            // Layout: Toggle Arranger Track Row Height
            case LAYOUT_TOGGLE_ARRANGER_TRACK_ROW_HEIGHT:
                if (isButtonPressed)
                    this.model.getArranger ().toggleTrackRowHeight ();
                break;
            // Layout: Toggle Arranger Clip Launcher Section
            case LAYOUT_TOGGLE_ARRANGER_CLIP_LAUNCHER_SECTION:
                if (isButtonPressed)
                    this.model.getArranger ().toggleClipLauncher ();
                break;
            // Layout: Toggle Arranger Time Line
            case LAYOUT_TOGGLE_ARRANGER_TIME_LINE:
                if (isButtonPressed)
                    this.model.getArranger ().toggleTimeLine ();
                break;
            // Layout: Toggle Arranger IO Section
            case LAYOUT_TOGGLE_ARRANGER_IO_SECTION:
                if (isButtonPressed)
                    this.model.getArranger ().toggleIoSection ();
                break;
            // Layout: Toggle Arranger Effect Tracks
            case LAYOUT_TOGGLE_ARRANGER_EFFECT_TRACKS:
                if (isButtonPressed)
                    this.model.getArranger ().toggleEffectTracks ();
                break;
            // Layout: Toggle Mixer Clip Launcher Section
            case LAYOUT_TOGGLE_MIXER_CLIP_LAUNCHER_SECTION:
                if (isButtonPressed)
                    this.model.getMixer ().toggleClipLauncherSectionVisibility ();
                break;
            // Layout: Toggle Mixer Cross Fade Section
            case LAYOUT_TOGGLE_MIXER_CROSS_FADE_SECTION:
                if (isButtonPressed)
                    this.model.getMixer ().toggleCrossFadeSectionVisibility ();
                break;
            // Layout: Toggle Mixer Device Section
            case LAYOUT_TOGGLE_MIXER_DEVICE_SECTION:
                if (isButtonPressed)
                    this.model.getMixer ().toggleDeviceSectionVisibility ();
                break;
            // Layout: Toggle Mixer sendsSection
            case LAYOUT_TOGGLE_MIXER_SENDSSECTION:
                if (isButtonPressed)
                    this.model.getMixer ().toggleSendsSectionVisibility ();
                break;
            // Layout: Toggle Mixer IO Section
            case LAYOUT_TOGGLE_MIXER_IO_SECTION:
                if (isButtonPressed)
                    this.model.getMixer ().toggleIoSectionVisibility ();
                break;
            // Layout: Toggle Mixer Meter Section
            case LAYOUT_TOGGLE_MIXER_METER_SECTION:
                if (isButtonPressed)
                    this.model.getMixer ().toggleMeterSectionVisibility ();
                break;

            case TRACK_TOGGLE_TRACK_BANK:
                if (isButtonPressed)
                    this.toggleTrackBankCommand.execute (ButtonEvent.DOWN);
                break;
            // Track: Add Audio Track
            case TRACK_ADD_AUDIO_TRACK:
                if (isButtonPressed)
                    this.model.getApplication ().addAudioTrack ();
                break;
            // Track: Add Effect Track
            case TRACK_ADD_EFFECT_TRACK:
                if (isButtonPressed)
                    this.model.getApplication ().addEffectTrack ();
                break;
            // Track: Add Instrument Track
            case TRACK_ADD_INSTRUMENT_TRACK:
                if (isButtonPressed)
                    this.model.getApplication ().addInstrumentTrack ();
                break;
            // Track: Select Previous Bank Page
            case TRACK_SELECT_PREVIOUS_BANK_PAGE:
                if (isButtonPressed)
                    this.scrollTrackLeft (true);
                break;
            // Track: Select Next Bank Page
            case TRACK_SELECT_NEXT_BANK_PAGE:
                if (isButtonPressed)
                    this.scrollTrackRight (true);
                break;
            // Track: Select Previous Track
            case TRACK_SELECT_PREVIOUS_TRACK:
                if (isButtonPressed)
                    this.scrollTrackLeft (false);
                break;
            // Track: Select Next Track
            case TRACK_SELECT_NEXT_TRACK:
                if (isButtonPressed)
                    this.scrollTrackRight (false);
                break;

            case TRACK_SCROLL_TRACKS:
                this.scrollTrack (knobMode, value);
                break;

            // Track 1-8: Select
            case TRACK_1_SELECT:
            case TRACK_2_SELECT:
            case TRACK_3_SELECT:
            case TRACK_4_SELECT:
            case TRACK_5_SELECT:
            case TRACK_6_SELECT:
            case TRACK_7_SELECT:
            case TRACK_8_SELECT:
                if (isButtonPressed)
                {
                    final ITrack track = this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SELECT.ordinal ());
                    track.select ();
                    this.getDisplay ().notify (track.getName ());
                }
                break;

            // Track 1-8: Toggle Active
            case TRACK_1_TOGGLE_ACTIVE:
            case TRACK_2_TOGGLE_ACTIVE:
            case TRACK_3_TOGGLE_ACTIVE:
            case TRACK_4_TOGGLE_ACTIVE:
            case TRACK_5_TOGGLE_ACTIVE:
            case TRACK_6_TOGGLE_ACTIVE:
            case TRACK_7_TOGGLE_ACTIVE:
            case TRACK_8_TOGGLE_ACTIVE:
                if (isButtonPressed)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_ACTIVE.ordinal ()).toggleIsActivated ();
                break;
            // Track 1-8: Set Active
            case TRACK_1_SET_ACTIVE:
            case TRACK_2_SET_ACTIVE:
            case TRACK_3_SET_ACTIVE:
            case TRACK_4_SET_ACTIVE:
            case TRACK_5_SET_ACTIVE:
            case TRACK_6_SET_ACTIVE:
            case TRACK_7_SET_ACTIVE:
            case TRACK_8_SET_ACTIVE:
                if (isButtonPressed)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_ACTIVE.ordinal ()).setIsActivated (value > 0);
                break;
            case TRACK_SELECTED_TOGGLE_ACTIVE:
                if (isButtonPressed)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleIsActivated ();
                }
                break;
            case TRACK_SELECTED_SET_ACTIVE:
                if (isButtonPressed)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.setIsActivated (value > 0);
                }
                break;

            // Track 1-8: Set Volume
            case TRACK_1_SET_VOLUME:
            case TRACK_2_SET_VOLUME:
            case TRACK_3_SET_VOLUME:
            case TRACK_4_SET_VOLUME:
            case TRACK_5_SET_VOLUME:
            case TRACK_6_SET_VOLUME:
            case TRACK_7_SET_VOLUME:
            case TRACK_8_SET_VOLUME:
                this.changeTrackVolume (knobMode, command.ordinal () - FlexiCommand.TRACK_1_SET_VOLUME.ordinal (), value);
                break;
            // Track Selected: Set Volume Track
            case TRACK_SELECTED_SET_VOLUME_TRACK:
                this.changeTrackVolume (knobMode, -1, value);
                break;

            // Track 1-8: Set Panorama
            case TRACK_1_SET_PANORAMA:
            case TRACK_2_SET_PANORAMA:
            case TRACK_3_SET_PANORAMA:
            case TRACK_4_SET_PANORAMA:
            case TRACK_5_SET_PANORAMA:
            case TRACK_6_SET_PANORAMA:
            case TRACK_7_SET_PANORAMA:
            case TRACK_8_SET_PANORAMA:
                this.changeTrackPanorama (knobMode, command.ordinal () - FlexiCommand.TRACK_1_SET_PANORAMA.ordinal (), value);
                break;
            // Track Selected: Set Panorama
            case TRACK_SELECTED_SET_PANORAMA:
                this.changeTrackPanorama (knobMode, -1, value);
                break;

            // Track 1-8: Toggle Mute
            case TRACK_1_TOGGLE_MUTE:
            case TRACK_2_TOGGLE_MUTE:
            case TRACK_3_TOGGLE_MUTE:
            case TRACK_4_TOGGLE_MUTE:
            case TRACK_5_TOGGLE_MUTE:
            case TRACK_6_TOGGLE_MUTE:
            case TRACK_7_TOGGLE_MUTE:
            case TRACK_8_TOGGLE_MUTE:
                if (isButtonPressed)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_MUTE.ordinal ()).toggleMute ();
                break;
            // Track 1-8: Set Mute
            case TRACK_1_SET_MUTE:
            case TRACK_2_SET_MUTE:
            case TRACK_3_SET_MUTE:
            case TRACK_4_SET_MUTE:
            case TRACK_5_SET_MUTE:
            case TRACK_6_SET_MUTE:
            case TRACK_7_SET_MUTE:
            case TRACK_8_SET_MUTE:
                if (isButtonPressed)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_MUTE.ordinal ()).setMute (value > 0);
                break;
            // Track Selected: Toggle Mute
            case TRACK_SELECTED_TOGGLE_MUTE:
                if (isButtonPressed)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleMute ();
                }
                break;
            // Track Selected: Set Mute
            case TRACK_SELECTED_SET_MUTE:
                if (isButtonPressed)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.setMute (value > 0);
                }
                break;

            // Track 1-8: Toggle Solo
            case TRACK_1_TOGGLE_SOLO:
            case TRACK_2_TOGGLE_SOLO:
            case TRACK_3_TOGGLE_SOLO:
            case TRACK_4_TOGGLE_SOLO:
            case TRACK_5_TOGGLE_SOLO:
            case TRACK_6_TOGGLE_SOLO:
            case TRACK_7_TOGGLE_SOLO:
            case TRACK_8_TOGGLE_SOLO:
                if (isButtonPressed)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_SOLO.ordinal ()).toggleSolo ();
                break;
            // Track 1-8: Set Solo
            case TRACK_1_SET_SOLO:
            case TRACK_2_SET_SOLO:
            case TRACK_3_SET_SOLO:
            case TRACK_4_SET_SOLO:
            case TRACK_5_SET_SOLO:
            case TRACK_6_SET_SOLO:
            case TRACK_7_SET_SOLO:
            case TRACK_8_SET_SOLO:
                if (isButtonPressed)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_SOLO.ordinal ()).setSolo (value > 0);
                break;
            // Track Selected: Toggle Solo
            case TRACK_SELECTED_TOGGLE_SOLO:
                if (isButtonPressed)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleSolo ();
                }
                break;
            // Track Selected: Set Solo
            case TRACK_SELECTED_SET_SOLO:
                if (isButtonPressed)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.setSolo (value > 0);
                }
                break;

            // Track 1-8: Toggle Arm
            case TRACK_1_TOGGLE_ARM:
            case TRACK_2_TOGGLE_ARM:
            case TRACK_3_TOGGLE_ARM:
            case TRACK_4_TOGGLE_ARM:
            case TRACK_5_TOGGLE_ARM:
            case TRACK_6_TOGGLE_ARM:
            case TRACK_7_TOGGLE_ARM:
            case TRACK_8_TOGGLE_ARM:
                if (isButtonPressed)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_ARM.ordinal ()).toggleRecArm ();
                break;
            // Track 1-8: Set Arm
            case TRACK_1_SET_ARM:
            case TRACK_2_SET_ARM:
            case TRACK_3_SET_ARM:
            case TRACK_4_SET_ARM:
            case TRACK_5_SET_ARM:
            case TRACK_6_SET_ARM:
            case TRACK_7_SET_ARM:
            case TRACK_8_SET_ARM:
                if (isButtonPressed)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_ARM.ordinal ()).setRecArm (value > 0);
                break;
            // Track Selected: Toggle Arm
            case TRACK_SELECTED_TOGGLE_ARM:
                if (isButtonPressed)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleRecArm ();
                }
                break;
            // Track Selected: Set Arm
            case TRACK_SELECTED_SET_ARM:
                if (isButtonPressed)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.setRecArm (value > 0);
                }
                break;

            // Track 1-8: Toggle Monitor
            case TRACK_1_TOGGLE_MONITOR:
            case TRACK_2_TOGGLE_MONITOR:
            case TRACK_3_TOGGLE_MONITOR:
            case TRACK_4_TOGGLE_MONITOR:
            case TRACK_5_TOGGLE_MONITOR:
            case TRACK_6_TOGGLE_MONITOR:
            case TRACK_7_TOGGLE_MONITOR:
            case TRACK_8_TOGGLE_MONITOR:
                if (isButtonPressed)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_MONITOR.ordinal ()).toggleMonitor ();
                break;
            // Track 1-8: Set Monitor
            case TRACK_1_SET_MONITOR:
            case TRACK_2_SET_MONITOR:
            case TRACK_3_SET_MONITOR:
            case TRACK_4_SET_MONITOR:
            case TRACK_5_SET_MONITOR:
            case TRACK_6_SET_MONITOR:
            case TRACK_7_SET_MONITOR:
            case TRACK_8_SET_MONITOR:
                if (isButtonPressed)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_MONITOR.ordinal ()).setMonitor (value > 0);
                break;
            // Track Selected: Toggle Monitor
            case TRACK_SELECTED_TOGGLE_MONITOR:
                if (isButtonPressed)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleMonitor ();
                }
                break;
            // Track Selected: Set Monitor
            case TRACK_SELECTED_SET_MONITOR:
                if (isButtonPressed)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.setMonitor (value > 0);
                }
                break;

            // Track 1: Toggle Auto Monitor
            case TRACK_1_TOGGLE_AUTO_MONITOR:
            case TRACK_2_TOGGLE_AUTO_MONITOR:
            case TRACK_3_TOGGLE_AUTO_MONITOR:
            case TRACK_4_TOGGLE_AUTO_MONITOR:
            case TRACK_5_TOGGLE_AUTO_MONITOR:
            case TRACK_6_TOGGLE_AUTO_MONITOR:
            case TRACK_7_TOGGLE_AUTO_MONITOR:
            case TRACK_8_TOGGLE_AUTO_MONITOR:
                if (isButtonPressed)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_AUTO_MONITOR.ordinal ()).toggleAutoMonitor ();
                break;
            // Track 1: Set Auto Monitor
            case TRACK_1_SET_AUTO_MONITOR:
            case TRACK_2_SET_AUTO_MONITOR:
            case TRACK_3_SET_AUTO_MONITOR:
            case TRACK_4_SET_AUTO_MONITOR:
            case TRACK_5_SET_AUTO_MONITOR:
            case TRACK_6_SET_AUTO_MONITOR:
            case TRACK_7_SET_AUTO_MONITOR:
            case TRACK_8_SET_AUTO_MONITOR:
                if (isButtonPressed)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_AUTO_MONITOR.ordinal ()).setAutoMonitor (value > 0);
                break;
            // Track Selected: Toggle Auto Monitor
            case TRACK_SELECTED_TOGGLE_AUTO_MONITOR:
                if (isButtonPressed)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleAutoMonitor ();
                }
                break;
            // Track Selected: Set Auto Monitor
            case TRACK_SELECTED_SET_AUTO_MONITOR:
                if (isButtonPressed)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.setAutoMonitor (value > 0);
                }
                break;

            // Track 1-8: Set Send 1
            case TRACK_1_SET_SEND_1:
            case TRACK_2_SET_SEND_1:
            case TRACK_3_SET_SEND_1:
            case TRACK_4_SET_SEND_1:
            case TRACK_5_SET_SEND_1:
            case TRACK_6_SET_SEND_1:
            case TRACK_7_SET_SEND_1:
            case TRACK_8_SET_SEND_1:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_1.ordinal (), 0, knobMode, value);
                break;

            // Track 1-8: Set Send 2
            case TRACK_1_SET_SEND_2:
            case TRACK_2_SET_SEND_2:
            case TRACK_3_SET_SEND_2:
            case TRACK_4_SET_SEND_2:
            case TRACK_5_SET_SEND_2:
            case TRACK_6_SET_SEND_2:
            case TRACK_7_SET_SEND_2:
            case TRACK_8_SET_SEND_2:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_2.ordinal (), 1, knobMode, value);
                break;

            // Track 1-8: Set Send 3
            case TRACK_1_SET_SEND_3:
            case TRACK_2_SET_SEND_3:
            case TRACK_3_SET_SEND_3:
            case TRACK_4_SET_SEND_3:
            case TRACK_5_SET_SEND_3:
            case TRACK_6_SET_SEND_3:
            case TRACK_7_SET_SEND_3:
            case TRACK_8_SET_SEND_3:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_3.ordinal (), 2, knobMode, value);
                break;

            // Track 1-8: Set Send 4
            case TRACK_1_SET_SEND_4:
            case TRACK_2_SET_SEND_4:
            case TRACK_3_SET_SEND_4:
            case TRACK_4_SET_SEND_4:
            case TRACK_5_SET_SEND_4:
            case TRACK_6_SET_SEND_4:
            case TRACK_7_SET_SEND_4:
            case TRACK_8_SET_SEND_4:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_4.ordinal (), 3, knobMode, value);
                break;

            // Track 1: Set Send 5
            case TRACK_1_SET_SEND_5:
            case TRACK_2_SET_SEND_5:
            case TRACK_3_SET_SEND_5:
            case TRACK_4_SET_SEND_5:
            case TRACK_5_SET_SEND_5:
            case TRACK_6_SET_SEND_5:
            case TRACK_7_SET_SEND_5:
            case TRACK_8_SET_SEND_5:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_5.ordinal (), 4, knobMode, value);
                break;

            // Track 1: Set Send 6
            case TRACK_1_SET_SEND_6:
            case TRACK_2_SET_SEND_6:
            case TRACK_3_SET_SEND_6:
            case TRACK_4_SET_SEND_6:
            case TRACK_5_SET_SEND_6:
            case TRACK_6_SET_SEND_6:
            case TRACK_7_SET_SEND_6:
            case TRACK_8_SET_SEND_6:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_6.ordinal (), 5, knobMode, value);
                break;

            // Track 1-8: Set Send 7
            case TRACK_1_SET_SEND_7:
            case TRACK_2_SET_SEND_7:
            case TRACK_3_SET_SEND_7:
            case TRACK_4_SET_SEND_7:
            case TRACK_5_SET_SEND_7:
            case TRACK_6_SET_SEND_7:
            case TRACK_7_SET_SEND_7:
            case TRACK_8_SET_SEND_7:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_7.ordinal (), 6, knobMode, value);
                break;

            // Track 1-8: Set Send 8
            case TRACK_1_SET_SEND_8:
            case TRACK_2_SET_SEND_8:
            case TRACK_3_SET_SEND_8:
            case TRACK_4_SET_SEND_8:
            case TRACK_5_SET_SEND_8:
            case TRACK_6_SET_SEND_8:
            case TRACK_7_SET_SEND_8:
            case TRACK_8_SET_SEND_8:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_8.ordinal (), 7, knobMode, value);
                break;

            // Track Selected: Set Send 1-8
            case TRACK_SELECTED_SET_SEND_1:
            case TRACK_SELECTED_SET_SEND_2:
            case TRACK_SELECTED_SET_SEND_3:
            case TRACK_SELECTED_SET_SEND_4:
            case TRACK_SELECTED_SET_SEND_5:
            case TRACK_SELECTED_SET_SEND_6:
            case TRACK_SELECTED_SET_SEND_7:
            case TRACK_SELECTED_SET_SEND_8:
                this.changeSendVolume (-1, command.ordinal () - FlexiCommand.TRACK_SELECTED_SET_SEND_1.ordinal (), knobMode, value);
                break;

            // Master: Set Volume
            case MASTER_SET_VOLUME:
                this.changeMasterVolume (knobMode, value);
                break;
            // Master: Set Panorama
            case MASTER_SET_PANORAMA:
                this.changeMasterPanorama (knobMode, value);
                break;
            // Master: Toggle Mute
            case MASTER_TOGGLE_MUTE:
                if (isButtonPressed)
                    this.model.getMasterTrack ().toggleMute ();
                break;
            // Master: Set Mute
            case MASTER_SET_MUTE:
                if (isButtonPressed)
                    this.model.getMasterTrack ().setMute (value > 0);
                break;
            // Master: Toggle Solo
            case MASTER_TOGGLE_SOLO:
                if (isButtonPressed)
                    this.model.getMasterTrack ().toggleSolo ();
                break;
            // Master: Set Solo
            case MASTER_SET_SOLO:
                if (isButtonPressed)
                    this.model.getMasterTrack ().setSolo (value > 0);
                break;
            // Master: Toggle Arm
            case MASTER_TOGGLE_ARM:
                if (isButtonPressed)
                    this.model.getMasterTrack ().toggleRecArm ();
                break;
            // Master: Set Arm
            case MASTER_SET_ARM:
                if (isButtonPressed)
                    this.model.getMasterTrack ().setRecArm (value > 0);
                break;
            // Master: Crossfader
            case MASTER_CROSSFADER:
                this.changeMasterCrossfader (knobMode, value);
                break;

            // Device: Toggle Window
            case DEVICE_TOGGLE_WINDOW:
                if (isButtonPressed)
                    this.model.getCursorDevice ().toggleWindowOpen ();
                break;
            // Device: Bypass
            case DEVICE_BYPASS:
                if (isButtonPressed)
                    this.model.getCursorDevice ().toggleEnabledState ();
                break;
            // Device: Expand
            case DEVICE_EXPAND:
                if (isButtonPressed)
                    this.model.getCursorDevice ().toggleExpanded ();
                break;
            // Device: Parameters
            case DEVICE_TOGGLE_PARAMETERS:
                if (isButtonPressed)
                    this.model.getCursorDevice ().toggleParameterPageSectionVisible ();
                break;
            // Device: Select Previous
            case DEVICE_SELECT_PREVIOUS:
                if (isButtonPressed)
                    this.model.getCursorDevice ().selectPrevious ();
                break;
            // Device: Select Next
            case DEVICE_SELECT_NEXT:
                if (isButtonPressed)
                    this.model.getCursorDevice ().selectNext ();
                break;

            case DEVICE_SCROLL_DEVICES:
                this.scrollDevice (knobMode, value);
                break;

            case DEVICE_SELECT_PREVIOUS_PARAMETER_PAGE:
                if (isButtonPressed)
                    this.model.getCursorDevice ().getParameterBank ().scrollBackwards ();
                break;
            case DEVICE_SELECT_NEXT_PARAMETER_PAGE:
                if (isButtonPressed)
                    this.model.getCursorDevice ().getParameterBank ().scrollForwards ();
                break;
            case DEVICE_SCROLL_PARAMETER_PAGES:
                this.scrollParameterPage (knobMode, value);
                break;

            // Device: Select Previous Parameter Bank
            case DEVICE_SELECT_PREVIOUS_PARAMETER_BANK:
                if (isButtonPressed)
                    this.model.getCursorDevice ().getParameterBank ().selectPreviousPage ();
                break;
            // Device: Select Next Parameter Bank
            case DEVICE_SELECT_NEXT_PARAMETER_BANK:
                if (isButtonPressed)
                    this.model.getCursorDevice ().getParameterBank ().selectNextPage ();
                break;

            case DEVICE_SCROLL_PARAMETER_BANKS:
                this.scrollParameterBank (knobMode, value);
                break;

            // Device: Set Parameter 1-8
            case DEVICE_SET_PARAMETER_1:
            case DEVICE_SET_PARAMETER_2:
            case DEVICE_SET_PARAMETER_3:
            case DEVICE_SET_PARAMETER_4:
            case DEVICE_SET_PARAMETER_5:
            case DEVICE_SET_PARAMETER_6:
            case DEVICE_SET_PARAMETER_7:
            case DEVICE_SET_PARAMETER_8:
                this.handleParameter (knobMode, command.ordinal () - FlexiCommand.DEVICE_SET_PARAMETER_1.ordinal (), value);
                break;

            // Browser: Browse Presets
            case BROWSER_BROWSE_PRESETS:
                if (isButtonPressed)
                    this.model.getBrowser ().browseForPresets ();
                break;
            // Browser: Insert Device before current
            case BROWSER_INSERT_DEVICE_BEFORE_CURRENT:
                if (isButtonPressed)
                    this.model.getCursorDevice ().browseToInsertBeforeDevice ();
                break;
            // Browser: Insert Device after current
            case BROWSER_INSERT_DEVICE_AFTER_CURRENT:
                if (isButtonPressed)
                    this.model.getCursorDevice ().browseToInsertAfterDevice ();
                break;
            // Browser: Commit Selection
            case BROWSER_COMMIT_SELECTION:
                if (isButtonPressed)
                    this.model.getBrowser ().stopBrowsing (true);
                break;
            // Browser: Cancel Selection
            case BROWSER_CANCEL_SELECTION:
                if (isButtonPressed)
                    this.model.getBrowser ().stopBrowsing (false);
                break;

            // Browser: Select Previous Filter in Column 1-6
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_1:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_2:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_3:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_4:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_5:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_6:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_7:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_8:
                if (isButtonPressed)
                    this.model.getBrowser ().selectPreviousFilterItem (command.ordinal () - FlexiCommand.BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_1.ordinal ());
                break;

            // Browser: Select Next Filter in Column 1-6
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_1:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_2:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_3:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_4:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_5:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_6:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_7:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_8:
                if (isButtonPressed)
                    this.model.getBrowser ().selectNextFilterItem (command.ordinal () - FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_1.ordinal ());
                break;

            case BROWSER_SCROLL_FILTER_IN_COLUMN_1:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_2:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_3:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_4:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_5:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_6:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_7:
            case BROWSER_SCROLL_FILTER_IN_COLUMN_8:
                this.scrollFilterColumn (knobMode, command.ordinal () - FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_1.ordinal (), value);
                break;

            // Browser: Reset Filter Column 1-6
            case BROWSER_RESET_FILTER_COLUMN_1:
            case BROWSER_RESET_FILTER_COLUMN_2:
            case BROWSER_RESET_FILTER_COLUMN_3:
            case BROWSER_RESET_FILTER_COLUMN_4:
            case BROWSER_RESET_FILTER_COLUMN_5:
            case BROWSER_RESET_FILTER_COLUMN_6:
            case BROWSER_RESET_FILTER_COLUMN_7:
            case BROWSER_RESET_FILTER_COLUMN_8:
                if (isButtonPressed)
                    this.model.getBrowser ().resetFilterColumn (command.ordinal () - FlexiCommand.BROWSER_RESET_FILTER_COLUMN_1.ordinal ());
                break;

            // Browser: Select the previous preset
            case BROWSER_SELECT_THE_PREVIOUS_PRESET:
                if (isButtonPressed)
                    this.model.getBrowser ().selectPreviousResult ();
                break;
            // Browser: Select the next preset
            case BROWSER_SELECT_THE_NEXT_PRESET:
                if (isButtonPressed)
                    this.model.getBrowser ().selectNextResult ();
                break;
            case BROWSER_SCROLL_PRESETS:
                this.scrollPresetColumn (knobMode, value);
                break;
            // Browser: Select the previous tab
            case BROWSER_SELECT_THE_PREVIOUS_TAB:
                if (isButtonPressed)
                    this.model.getBrowser ().previousContentType ();
                break;
            // Browser: Select the next tab"
            case BROWSER_SELECT_THE_NEXT_TAB:
                if (isButtonPressed)
                    this.model.getBrowser ().nextContentType ();
                break;
            case BROWSER_SCROLL_TABS:
                this.scrollBrowserTabs (knobMode, value);
                break;

            // Scene 1-8: Launch Scene
            case SCENE_1_LAUNCH_SCENE:
            case SCENE_2_LAUNCH_SCENE:
            case SCENE_3_LAUNCH_SCENE:
            case SCENE_4_LAUNCH_SCENE:
            case SCENE_5_LAUNCH_SCENE:
            case SCENE_6_LAUNCH_SCENE:
            case SCENE_7_LAUNCH_SCENE:
            case SCENE_8_LAUNCH_SCENE:
                if (isButtonPressed)
                    this.model.getSceneBank ().getItem (command.ordinal () - FlexiCommand.SCENE_1_LAUNCH_SCENE.ordinal ()).launch ();
                break;

            // Scene: Select Previous Bank
            case SCENE_SELECT_PREVIOUS_BANK:
                if (isButtonPressed)
                    this.model.getSceneBank ().selectPreviousPage ();
                break;
            // Scene: Select Next Bank
            case SCENE_SELECT_NEXT_BANK:
                if (isButtonPressed)
                    this.model.getSceneBank ().selectNextPage ();
                break;
            // Scene: Create Scene from playing Clips
            case SCENE_CREATE_SCENE_FROM_PLAYING_CLIPS:
                if (isButtonPressed)
                    this.model.getProject ().createSceneFromPlayingLauncherClips ();
                break;

            case CLIP_PREVIOUS:
                if (isButtonPressed)
                    this.scrollClipLeft (false);
                break;

            case CLIP_NEXT:
                if (isButtonPressed)
                    this.scrollClipRight (false);
                break;

            case CLIP_SCROLL:
                this.scrollClips (knobMode, value);
                break;

            case CLIP_PLAY:
                if (isButtonPressed)
                {
                    final ISlot selectedSlot = this.model.getSelectedSlot ();
                    if (selectedSlot != null)
                        selectedSlot.launch ();
                }
                break;

            case CLIP_STOP:
                if (isButtonPressed)
                {
                    final ITrack track = this.model.getSelectedTrack ();
                    if (track != null)
                        track.stop ();
                }
                break;

            case CLIP_RECORD:
                if (isButtonPressed)
                {
                    final ISlot selectedSlot = this.model.getSelectedSlot ();
                    if (selectedSlot != null)
                        selectedSlot.record ();
                }
                break;

            case CLIP_NEW:
                if (isButtonPressed)
                    new NewCommand<> (this.model, this).executeNormal (ButtonEvent.DOWN);
                break;

            case MARKER_1_LAUNCH_MARKER:
            case MARKER_2_LAUNCH_MARKER:
            case MARKER_3_LAUNCH_MARKER:
            case MARKER_4_LAUNCH_MARKER:
            case MARKER_5_LAUNCH_MARKER:
            case MARKER_6_LAUNCH_MARKER:
            case MARKER_7_LAUNCH_MARKER:
            case MARKER_8_LAUNCH_MARKER:
                final int index = command.ordinal () - FlexiCommand.MARKER_1_LAUNCH_MARKER.ordinal ();
                this.model.getMarkerBank ().getItem (index).launch (true);
                break;

            case MARKER_SELECT_PREVIOUS_BANK:
                this.model.getMarkerBank ().selectPreviousPage ();
                break;

            case MARKER_SELECT_NEXT_BANK:
                this.model.getMarkerBank ().selectNextPage ();
                break;

            case MODES_KNOB1:
            case MODES_KNOB2:
            case MODES_KNOB3:
            case MODES_KNOB4:
            case MODES_KNOB5:
            case MODES_KNOB6:
            case MODES_KNOB7:
            case MODES_KNOB8:
                this.changeModeValue (knobMode, command.ordinal () - FlexiCommand.MODES_KNOB1.ordinal (), value);
                break;

            case MODES_BUTTON1:
            case MODES_BUTTON2:
            case MODES_BUTTON3:
            case MODES_BUTTON4:
            case MODES_BUTTON5:
            case MODES_BUTTON6:
            case MODES_BUTTON7:
            case MODES_BUTTON8:
                if (isButtonPressed)
                {
                    mode.selectItem (command.ordinal () - FlexiCommand.MODES_BUTTON1.ordinal ());
                    this.notifyName (mode);
                }
                break;

            case MODES_NEXT_ITEM:
                if (isButtonPressed)
                {
                    mode.selectNextItem ();
                    this.notifyName (mode);
                }
                break;
            case MODES_PREV_ITEM:
                if (isButtonPressed)
                {
                    mode.selectPreviousItem ();
                    this.notifyName (mode);
                }
                break;
            case MODES_NEXT_PAGE:
                if (isButtonPressed)
                {
                    mode.selectNextItemPage ();
                    this.notifyName (mode);
                }
                break;
            case MODES_PREV_PAGE:
                if (isButtonPressed)
                {
                    mode.selectPreviousItemPage ();
                    this.notifyName (mode);
                }
                break;
            case MODES_SELECT_MODE_TRACK:
                if (isButtonPressed)
                    this.activateMode (Modes.TRACK);
                break;
            case MODES_SELECT_MODE_VOLUME:
                if (isButtonPressed)
                    this.activateMode (Modes.VOLUME);
                break;
            case MODES_SELECT_MODE_PAN:
                if (isButtonPressed)
                    this.activateMode (Modes.PAN);
                break;
            case MODES_SELECT_MODE_SEND1:
                if (isButtonPressed)
                    this.activateMode (Modes.SEND1);
                break;
            case MODES_SELECT_MODE_SEND2:
                if (isButtonPressed)
                    this.activateMode (Modes.SEND2);
                break;
            case MODES_SELECT_MODE_SEND3:
                if (isButtonPressed)
                    this.activateMode (Modes.SEND3);
                break;
            case MODES_SELECT_MODE_SEND4:
                if (isButtonPressed)
                    this.activateMode (Modes.SEND4);
                break;
            case MODES_SELECT_MODE_SEND5:
                if (isButtonPressed)
                    this.activateMode (Modes.SEND5);
                break;
            case MODES_SELECT_MODE_SEND6:
                if (isButtonPressed)
                    this.activateMode (Modes.SEND6);
                break;
            case MODES_SELECT_MODE_SEND7:
                if (isButtonPressed)
                    this.activateMode (Modes.SEND7);
                break;
            case MODES_SELECT_MODE_SEND8:
                if (isButtonPressed)
                    this.activateMode (Modes.SEND8);
                break;
            case MODES_SELECT_MODE_DEVICE:
                if (isButtonPressed)
                    this.activateMode (Modes.DEVICE_PARAMS);
                break;
            case MODES_SELECT_MODE_NEXT:
                if (isButtonPressed)
                    this.selectNextMode ();
                break;
            case MODES_SELECT_MODE_PREV:
                if (isButtonPressed)
                    this.selectPreviousMode ();
                break;
            case MODES_BROWSE_PRESETS:
                if (isButtonPressed)
                {
                    this.model.getBrowser ().browseForPresets ();
                    this.host.scheduleTask ( () -> this.activateMode (Modes.BROWSER), 500);
                }
                break;

            case MODES_MIDI_CC_0:
            case MODES_MIDI_CC_1:
            case MODES_MIDI_CC_2:
            case MODES_MIDI_CC_3:
            case MODES_MIDI_CC_4:
            case MODES_MIDI_CC_5:
            case MODES_MIDI_CC_6:
            case MODES_MIDI_CC_7:
            case MODES_MIDI_CC_8:
            case MODES_MIDI_CC_9:
            case MODES_MIDI_CC_10:
            case MODES_MIDI_CC_11:
            case MODES_MIDI_CC_12:
            case MODES_MIDI_CC_13:
            case MODES_MIDI_CC_14:
            case MODES_MIDI_CC_15:
            case MODES_MIDI_CC_16:
            case MODES_MIDI_CC_17:
            case MODES_MIDI_CC_18:
            case MODES_MIDI_CC_19:
            case MODES_MIDI_CC_20:
            case MODES_MIDI_CC_21:
            case MODES_MIDI_CC_22:
            case MODES_MIDI_CC_23:
            case MODES_MIDI_CC_24:
            case MODES_MIDI_CC_25:
            case MODES_MIDI_CC_26:
            case MODES_MIDI_CC_27:
            case MODES_MIDI_CC_28:
            case MODES_MIDI_CC_29:
            case MODES_MIDI_CC_30:
            case MODES_MIDI_CC_31:
            case MODES_MIDI_CC_32:
            case MODES_MIDI_CC_33:
            case MODES_MIDI_CC_34:
            case MODES_MIDI_CC_35:
            case MODES_MIDI_CC_36:
            case MODES_MIDI_CC_37:
            case MODES_MIDI_CC_38:
            case MODES_MIDI_CC_39:
            case MODES_MIDI_CC_40:
            case MODES_MIDI_CC_41:
            case MODES_MIDI_CC_42:
            case MODES_MIDI_CC_43:
            case MODES_MIDI_CC_44:
            case MODES_MIDI_CC_45:
            case MODES_MIDI_CC_46:
            case MODES_MIDI_CC_47:
            case MODES_MIDI_CC_48:
            case MODES_MIDI_CC_49:
            case MODES_MIDI_CC_50:
            case MODES_MIDI_CC_51:
            case MODES_MIDI_CC_52:
            case MODES_MIDI_CC_53:
            case MODES_MIDI_CC_54:
            case MODES_MIDI_CC_55:
            case MODES_MIDI_CC_56:
            case MODES_MIDI_CC_57:
            case MODES_MIDI_CC_58:
            case MODES_MIDI_CC_59:
            case MODES_MIDI_CC_60:
            case MODES_MIDI_CC_61:
            case MODES_MIDI_CC_62:
            case MODES_MIDI_CC_63:
            case MODES_MIDI_CC_64:
            case MODES_MIDI_CC_65:
            case MODES_MIDI_CC_66:
            case MODES_MIDI_CC_67:
            case MODES_MIDI_CC_68:
            case MODES_MIDI_CC_69:
            case MODES_MIDI_CC_70:
            case MODES_MIDI_CC_71:
            case MODES_MIDI_CC_72:
            case MODES_MIDI_CC_73:
            case MODES_MIDI_CC_74:
            case MODES_MIDI_CC_75:
            case MODES_MIDI_CC_76:
            case MODES_MIDI_CC_77:
            case MODES_MIDI_CC_78:
            case MODES_MIDI_CC_79:
            case MODES_MIDI_CC_80:
            case MODES_MIDI_CC_81:
            case MODES_MIDI_CC_82:
            case MODES_MIDI_CC_83:
            case MODES_MIDI_CC_84:
            case MODES_MIDI_CC_85:
            case MODES_MIDI_CC_86:
            case MODES_MIDI_CC_87:
            case MODES_MIDI_CC_88:
            case MODES_MIDI_CC_89:
            case MODES_MIDI_CC_90:
            case MODES_MIDI_CC_91:
            case MODES_MIDI_CC_92:
            case MODES_MIDI_CC_93:
            case MODES_MIDI_CC_94:
            case MODES_MIDI_CC_95:
            case MODES_MIDI_CC_96:
            case MODES_MIDI_CC_97:
            case MODES_MIDI_CC_98:
            case MODES_MIDI_CC_99:
            case MODES_MIDI_CC_100:
            case MODES_MIDI_CC_101:
            case MODES_MIDI_CC_102:
            case MODES_MIDI_CC_103:
            case MODES_MIDI_CC_104:
            case MODES_MIDI_CC_105:
            case MODES_MIDI_CC_106:
            case MODES_MIDI_CC_107:
            case MODES_MIDI_CC_108:
            case MODES_MIDI_CC_109:
            case MODES_MIDI_CC_110:
            case MODES_MIDI_CC_111:
            case MODES_MIDI_CC_112:
            case MODES_MIDI_CC_113:
            case MODES_MIDI_CC_114:
            case MODES_MIDI_CC_115:
            case MODES_MIDI_CC_116:
            case MODES_MIDI_CC_117:
            case MODES_MIDI_CC_118:
            case MODES_MIDI_CC_119:
            case MODES_MIDI_CC_120:
            case MODES_MIDI_CC_121:
            case MODES_MIDI_CC_122:
            case MODES_MIDI_CC_123:
            case MODES_MIDI_CC_124:
            case MODES_MIDI_CC_125:
            case MODES_MIDI_CC_126:
            case MODES_MIDI_CC_127:
                this.input.sendRawMidiEvent (0xB0, command.ordinal () - FlexiCommand.MODES_MIDI_CC_0.ordinal (), value);
                break;
        }

        this.host.scheduleTask ( () -> {
            this.valueCache[slotIndex] = this.getCommandValue (command);
            this.isUpdatingValue = false;
        }, 400);

    }


    private void notifyName (final Mode mode)
    {
        this.host.scheduleTask ( () -> this.getDisplay ().notify (mode.getSelectedItemName ()), 100);
    }


    /**
     * Activate a new mode.
     *
     * @param modeID The ID of the new mode
     */
    public void activateMode (final Modes modeID)
    {
        final String modeName = this.modeManager.getMode (modeID).getName ();

        if (!this.modeManager.isActiveMode (modeID))
        {
            this.modeManager.setActiveMode (modeID);
            this.host.showNotification (modeName);
        }
        if (!modeName.equals (this.configuration.getSelectedModeName ()))
            this.configuration.setSelectedMode (modeName);
    }


    private void handleParameter (final int knobMode, final int index, final int value)
    {
        final IParameter fxParam = this.model.getCursorDevice ().getParameterBank ().getItem (index);
        if (isAbsolute (knobMode))
            fxParam.setValue (value);
        else
            fxParam.setValue (this.limit (fxParam.getValue () + this.getRelativeSpeed (knobMode, value)));
    }


    private int getSendValue (final int trackIndex, final int sendIndex)
    {
        final ITrack track = this.getTrack (trackIndex);
        if (track == null)
            return 0;

        final ISendBank sendBank = track.getSendBank ();
        if (sendIndex >= sendBank.getPageSize ())
            return 0;

        final ISend send = sendBank.getItem (sendIndex);
        if (send == null)
            return 0;

        return send.getValue ();
    }


    private void changeSendVolume (final int trackIndex, final int sendIndex, final int knobMode, final int value)
    {
        final ITrack track = this.getTrack (trackIndex);
        if (track == null)
            return;

        final ISendBank sendBank = track.getSendBank ();
        if (sendIndex >= sendBank.getPageSize ())
            return;

        final ISend send = sendBank.getItem (sendIndex);
        if (send == null)
            return;

        if (isAbsolute (knobMode))
            send.setValue (value);
        else
            send.setValue (this.limit (send.getValue () + this.getRelativeSpeed (knobMode, value)));
    }


    private void handlePlayCursor (final int knobMode, final int value)
    {
        final ITransport transport = this.model.getTransport ();
        // Only relative modes are supported
        if (knobMode != KNOB_MODE_ABSOLUTE)
            transport.changePosition (this.getRelativeSpeed (knobMode, value) > 0);
    }


    private void handleTempo (final int knobMode, final int value)
    {
        final ITransport transport = this.model.getTransport ();
        if (isAbsolute (knobMode))
            transport.setTempo (value);
        else
            transport.changeTempo (this.getRelativeSpeed (knobMode, value) > 0);
    }


    private void handleMetronomeVolume (final int knobMode, final int value)
    {
        final ITransport transport = this.model.getTransport ();
        if (isAbsolute (knobMode))
            transport.setMetronomeVolume (value);
        else
            transport.setMetronomeVolume (this.limit (transport.getMetronomeVolume () + this.getRelativeSpeed (knobMode, value)));
    }


    private void changeTrackVolume (final int knobMode, final int trackIndex, final int value)
    {
        final ITrack track = this.getTrack (trackIndex);
        if (isAbsolute (knobMode))
            track.setVolume (value);
        else
            track.setVolume (this.limit (track.getVolume () + this.getRelativeSpeed (knobMode, value)));
    }


    private void scrollTrack (final int knobMode, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        if (!this.increaseKnobMovement ())
            return;

        if (this.getRelativeSpeed (knobMode, value) > 0)
            this.scrollTrackRight (false);
        else
            this.scrollTrackLeft (false);
    }


    private void changeMasterVolume (final int knobMode, final int value)
    {
        final ITrack track = this.model.getMasterTrack ();
        if (isAbsolute (knobMode))
            track.setVolume (value);
        else
            track.setVolume (this.limit (track.getVolume () + this.getRelativeSpeed (knobMode, value)));
    }


    private void changeTrackPanorama (final int knobMode, final int trackIndex, final int value)
    {
        final ITrack track = this.getTrack (trackIndex);
        if (isAbsolute (knobMode))
            track.setPan (value);
        else
            track.setPan (this.limit (track.getPan () + this.getRelativeSpeed (knobMode, value)));
    }


    private void changeMasterPanorama (final int knobMode, final int value)
    {
        final ITrack track = this.model.getMasterTrack ();
        if (isAbsolute (knobMode))
            track.setPan (value);
        else
            track.setPan (this.limit (track.getPan () + this.getRelativeSpeed (knobMode, value)));
    }


    private void changeMasterCrossfader (final int knobMode, final int value)
    {
        final ITransport transport = this.model.getTransport ();
        if (isAbsolute (knobMode))
            transport.setCrossfade (value);
        else
            transport.setCrossfade (this.limit (transport.getCrossfade () + this.getRelativeSpeed (knobMode, value)));
    }


    private void changeModeValue (final int knobMode, final int knobIndex, final int value)
    {
        final Mode mode = this.modeManager.getActiveOrTempMode ();
        if (isAbsolute (knobMode))
            mode.onKnobValue (knobIndex, value);
        else
        {
            final int knobValue = mode.getKnobValue (knobIndex);
            final int relativeSpeed = (int) Math.round (this.getRelativeSpeed (knobMode, value));
            mode.onKnobValue (knobIndex, knobValue == -1 ? relativeSpeed : (int) this.limit ((double) knobValue + relativeSpeed));
        }
    }


    private void scrollTrackLeft (final boolean switchBank)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack sel = tb.getSelectedItem ();
        final int index = sel == null ? 0 : sel.getIndex () - 1;
        if (index == -1 || switchBank)
        {
            tb.selectPreviousPage ();
            return;
        }
        tb.getItem (index).select ();
    }


    private void scrollTrackRight (final boolean switchBank)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack sel = tb.getSelectedItem ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        if (index == 8 || switchBank)
        {
            tb.selectNextPage ();
            return;
        }
        tb.getItem (index).select ();
    }


    private void scrollDevice (final int knobMode, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        if (!this.increaseKnobMovement ())
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (this.getRelativeSpeed (knobMode, value) > 0)
            cursorDevice.selectNext ();
        else
            cursorDevice.selectPrevious ();
        this.getDisplay ().notify (cursorDevice.getName ());
    }


    private void scrollParameterPage (final int knobMode, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        if (!this.increaseKnobMovement ())
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        if (this.getRelativeSpeed (knobMode, value) > 0)
            parameterBank.scrollForwards ();
        else
            parameterBank.scrollBackwards ();
        this.getDisplay ().notify (cursorDevice.getParameterPageBank ().getSelectedItem ());
    }


    private void scrollParameterBank (final int knobMode, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        if (!this.increaseKnobMovement ())
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        if (this.getRelativeSpeed (knobMode, value) > 0)
            parameterBank.selectNextPage ();
        else
            parameterBank.selectPreviousPage ();
        this.getDisplay ().notify (cursorDevice.getParameterPageBank ().getSelectedItem ());
    }


    private void scrollFilterColumn (final int knobMode, final int filterColumn, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        if (this.getRelativeSpeed (knobMode, value) > 0)
            this.model.getBrowser ().selectNextFilterItem (filterColumn);
        else
            this.model.getBrowser ().selectPreviousFilterItem (filterColumn);
    }


    private void scrollPresetColumn (final int knobMode, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        if (this.getRelativeSpeed (knobMode, value) > 0)
            this.model.getBrowser ().selectNextResult ();
        else
            this.model.getBrowser ().selectPreviousResult ();
    }


    private void scrollBrowserTabs (final int knobMode, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        if (!this.increaseKnobMovement ())
            return;

        if (this.getRelativeSpeed (knobMode, value) > 0)
            this.model.getBrowser ().nextContentType ();
        else
            this.model.getBrowser ().previousContentType ();
    }


    private void scrollClips (final int knobMode, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        if (!this.increaseKnobMovement ())
            return;

        if (this.getRelativeSpeed (knobMode, value) > 0)
            this.scrollClipRight (false);
        else
            this.scrollClipLeft (false);
    }


    private void scrollClipLeft (final boolean switchBank)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getSelectedItem ();
        if (track == null)
            return;
        final ISlotBank slotBank = track.getSlotBank ();
        final ISlot sel = slotBank.getSelectedItem ();
        final int index = sel == null ? 0 : sel.getIndex () - 1;
        if (index == -1 || switchBank)
        {
            tb.getSceneBank ().selectPreviousPage ();
            return;
        }
        slotBank.getItem (index).select ();
    }


    private void scrollClipRight (final boolean switchBank)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getSelectedItem ();
        if (track == null)
            return;
        final ISlotBank slotBank = track.getSlotBank ();
        final ISlot sel = slotBank.getSelectedItem ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        final ISceneBank sceneBank = tb.getSceneBank ();
        if (index == sceneBank.getPageSize () || switchBank)
        {
            sceneBank.selectNextPage ();
            return;
        }
        slotBank.getItem (index).select ();
    }


    private ITrack getTrack (final int trackIndex)
    {
        final ITrackBank tb = this.model.getTrackBank ();
        return trackIndex < 0 ? tb.getSelectedItem () : tb.getItem (trackIndex);
    }


    private double getRelativeSpeed (final int knobMode, final int value)
    {
        switch (knobMode)
        {
            case KNOB_MODE_RELATIVE1:
                return this.model.getValueChanger ().calcKnobSpeed (value);
            case KNOB_MODE_RELATIVE2:
                return this.relative2ValueChanger.calcKnobSpeed (value);
            case KNOB_MODE_RELATIVE3:
                return this.relative3ValueChanger.calcKnobSpeed (value);
            default:
                return 0;
        }
    }


    /**
     * Slows down knob movement. Increases the counter till the scroll rate.
     *
     * @return True if the knob movement should be executed otherwise false
     */
    protected boolean increaseKnobMovement ()
    {
        this.movementCounter++;
        if (this.movementCounter < SCROLL_RATE)
            return false;
        this.movementCounter = 0;
        return true;
    }


    private void selectPreviousMode ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        final Modes activeModeId = this.modeManager.getActiveModeId ();
        int index = MODE_IDS.indexOf (activeModeId);
        Modes newMode;
        int newModeID;
        // If a send mode is selected check if the according send exists
        do
        {
            index--;
            if (index < 0 || index >= MODE_IDS.size ())
                index = MODE_IDS.size () - 1;
            newMode = MODE_IDS.get (index);
            newModeID = newMode.ordinal ();
        } while (newModeID >= Modes.SEND1.ordinal () && newModeID <= Modes.SEND8.ordinal () && !trackBank.canEditSend (newModeID - Modes.SEND1.ordinal ()));

        this.activateMode (newMode);
    }


    private void selectNextMode ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        final Modes activeModeId = this.modeManager.getActiveModeId ();
        int index = MODE_IDS.indexOf (activeModeId);
        Modes newMode;
        int newModeID;
        // If a send mode is selected check if the according send exists
        do
        {
            index++;
            if (index < 0 || index >= MODE_IDS.size ())
                index = 0;
            newMode = MODE_IDS.get (index);
            newModeID = newMode.ordinal ();
        } while (newModeID >= Modes.SEND1.ordinal () && newModeID <= Modes.SEND8.ordinal () && !trackBank.canEditSend (newModeID - Modes.SEND1.ordinal ()));

        this.activateMode (newMode);
    }


    private void reflectValue (final CommandSlot slot, final int value)
    {
        switch (slot.getType ())
        {
            case CommandSlot.TYPE_CC:
                if (value >= 0 && value <= 127)
                    this.getOutput ().sendCCEx (slot.getMidiChannel (), slot.getNumber (), value);
                break;

            case CommandSlot.TYPE_PITCH_BEND:
                if (value >= 0 && value <= 127)
                    this.getOutput ().sendPitchbend (slot.getMidiChannel (), 0, value);
                break;

            default:
                // Other types not supported
                break;
        }
    }


    private int limit (final double value)
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();
        return (int) Math.max (0, Math.min (value, valueChanger.getUpperBound () - 1.0));
    }


    /**
     * Return if the given knob mode is one of the absoulte ones.
     *
     * @param knobMode The knob mode to test
     * @return True if it is an absolute mode
     */
    public static boolean isAbsolute (final int knobMode)
    {
        return knobMode == KNOB_MODE_ABSOLUTE || knobMode == KNOB_MODE_ABSOLUTE_TOGGLE;
    }


    /**
     * Update all knob speeds from the configuration settings.
     */
    public void updateKnobSpeeds ()
    {
        final double fraction = 128 * this.configuration.getKnobSpeedNormal () / 100.0;
        this.model.getValueChanger ().setFractionValue (fraction);
        this.relative2ValueChanger.setFractionValue (fraction);
        this.relative3ValueChanger.setFractionValue (fraction);

        final double slowFraction = 128 * this.configuration.getKnobSpeedSlow () / 100.0;
        this.model.getValueChanger ().setSlowFractionValue (slowFraction);
        this.relative2ValueChanger.setSlowFractionValue (slowFraction);
        this.relative3ValueChanger.setSlowFractionValue (slowFraction);
    }


    /**
     * Set the knob speed on all value changers.
     *
     * @param isSlow True to set to slow otherwise fast
     */
    public void setKnobSpeed (final boolean isSlow)
    {
        this.model.getValueChanger ().setSpeed (isSlow);
        this.relative2ValueChanger.setSpeed (isSlow);
        this.relative3ValueChanger.setSpeed (isSlow);
    }
}