// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.controller;

import de.mossgrabers.controller.generic.CommandSlot;
import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.mode.Modes;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
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
import de.mossgrabers.framework.utils.ButtonEvent;

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
    private static final int           BUTTON_REPEAT_INTERVAL = 75;

    private static final int           KNOB_MODE_ABSOLUTE     = 0;
    private static final int           KNOB_MODE_RELATIVE1    = 1;
    private static final int           KNOB_MODE_RELATIVE2    = 2;
    private static final int           KNOB_MODE_RELATIVE3    = 3;

    protected static final int         SCROLL_RATE            = 6;
    private static final List<Integer> MODE_IDS               = new ArrayList<> ();

    private int                        movementCounter        = 0;

    private final IModel               model;
    private final IValueChanger        relative2ValueChanger  = new Relative2ValueChanger (128, 1, 0.5);
    private final IValueChanger        relative3ValueChanger  = new Relative3ValueChanger (128, 1, 0.5);
    private final int []               valueCache             = new int [GenericFlexiConfiguration.NUM_SLOTS];
    private boolean                    isUpdatingValue        = false;

    static
    {
        MODE_IDS.add (Modes.MODE_TRACK);
        MODE_IDS.add (Modes.MODE_VOLUME);
        MODE_IDS.add (Modes.MODE_PAN);
        MODE_IDS.add (Modes.MODE_SEND1);
        MODE_IDS.add (Modes.MODE_SEND2);
        MODE_IDS.add (Modes.MODE_SEND3);
        MODE_IDS.add (Modes.MODE_SEND4);
        MODE_IDS.add (Modes.MODE_SEND5);
        MODE_IDS.add (Modes.MODE_SEND6);
        MODE_IDS.add (Modes.MODE_SEND7);
        MODE_IDS.add (Modes.MODE_SEND8);
        MODE_IDS.add (Modes.MODE_DEVICE);
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
        super (host, configuration, colorManager, output, input, null, new int [0]);

        Arrays.fill (this.valueCache, -1);
        this.model = model;

        this.configuration.addSettingObserver (GenericFlexiConfiguration.BUTTON_EXPORT, this::importFile);
        this.configuration.addSettingObserver (GenericFlexiConfiguration.BUTTON_IMPORT, this::exportFile);
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

            switch (slots[i].getType ())
            {
                case CommandSlot.TYPE_CC:
                    if (value >= 0 && value <= 127)
                        this.getOutput ().sendCCEx (slots[i].getMidiChannel (), slots[i].getNumber (), value);
                    break;

                case CommandSlot.TYPE_PITCH_BEND:
                    if (value >= 0 && value <= 127)
                        this.getOutput ().sendPitchbend (slots[i].getMidiChannel (), 0, value);
                    break;

                default:
                    // Other types not supported
                    break;
            }
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
        switch (command)
        {
            case GLOBAL_TOGGLE_AUDIO_ENGINE:
                return this.model.getApplication ().isEngineActive () ? 127 : 0;

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

            case TRANSPORT_SET_CROSSFADER:
                return this.model.getTransport ().getCrossfade ();

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

            case TRACK_SELECTED_TOGGLE_ACTIVE:
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

            case TRACK_SELECTED_TOGGLE_MUTE:
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

            case TRACK_SELECTED_TOGGLE_SOLO:
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

            case TRACK_SELECTED_TOGGLE_ARM:
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

            case TRACK_SELECTED_TOGGLE_MONITOR:
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

            case TRACK_SELECTED_TOGGLE_AUTO_MONITOR:
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
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_1.ordinal ()).getSendBank ().getItem (0).getValue ();

            case TRACK_1_SET_SEND_2:
            case TRACK_2_SET_SEND_2:
            case TRACK_3_SET_SEND_2:
            case TRACK_4_SET_SEND_2:
            case TRACK_5_SET_SEND_2:
            case TRACK_6_SET_SEND_2:
            case TRACK_7_SET_SEND_2:
            case TRACK_8_SET_SEND_2:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_2.ordinal ()).getSendBank ().getItem (1).getValue ();

            case TRACK_1_SET_SEND_3:
            case TRACK_2_SET_SEND_3:
            case TRACK_3_SET_SEND_3:
            case TRACK_4_SET_SEND_3:
            case TRACK_5_SET_SEND_3:
            case TRACK_6_SET_SEND_3:
            case TRACK_7_SET_SEND_3:
            case TRACK_8_SET_SEND_3:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_3.ordinal ()).getSendBank ().getItem (2).getValue ();

            case TRACK_1_SET_SEND_4:
            case TRACK_2_SET_SEND_4:
            case TRACK_3_SET_SEND_4:
            case TRACK_4_SET_SEND_4:
            case TRACK_5_SET_SEND_4:
            case TRACK_6_SET_SEND_4:
            case TRACK_7_SET_SEND_4:
            case TRACK_8_SET_SEND_4:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_4.ordinal ()).getSendBank ().getItem (3).getValue ();

            case TRACK_1_SET_SEND_5:
            case TRACK_2_SET_SEND_5:
            case TRACK_3_SET_SEND_5:
            case TRACK_4_SET_SEND_5:
            case TRACK_5_SET_SEND_5:
            case TRACK_6_SET_SEND_5:
            case TRACK_7_SET_SEND_5:
            case TRACK_8_SET_SEND_5:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_5.ordinal ()).getSendBank ().getItem (4).getValue ();

            case TRACK_1_SET_SEND_6:
            case TRACK_2_SET_SEND_6:
            case TRACK_3_SET_SEND_6:
            case TRACK_4_SET_SEND_6:
            case TRACK_5_SET_SEND_6:
            case TRACK_6_SET_SEND_6:
            case TRACK_7_SET_SEND_6:
            case TRACK_8_SET_SEND_6:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_6.ordinal ()).getSendBank ().getItem (5).getValue ();

            case TRACK_1_SET_SEND_7:
            case TRACK_2_SET_SEND_7:
            case TRACK_3_SET_SEND_7:
            case TRACK_4_SET_SEND_7:
            case TRACK_5_SET_SEND_7:
            case TRACK_6_SET_SEND_7:
            case TRACK_7_SET_SEND_7:
            case TRACK_8_SET_SEND_7:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_7.ordinal ()).getSendBank ().getItem (6).getValue ();

            case TRACK_1_SET_SEND_8:
            case TRACK_2_SET_SEND_8:
            case TRACK_3_SET_SEND_8:
            case TRACK_4_SET_SEND_8:
            case TRACK_5_SET_SEND_8:
            case TRACK_6_SET_SEND_8:
            case TRACK_7_SET_SEND_8:
            case TRACK_8_SET_SEND_8:
                return this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_8.ordinal ()).getSendBank ().getItem (7).getValue ();

            case TRACK_SELECTED_SET_SEND_1:
            case TRACK_SELECTED_SET_SEND_2:
            case TRACK_SELECTED_SET_SEND_3:
            case TRACK_SELECTED_SET_SEND_4:
            case TRACK_SELECTED_SET_SEND_5:
            case TRACK_SELECTED_SET_SEND_6:
            case TRACK_SELECTED_SET_SEND_7:
            case TRACK_SELECTED_SET_SEND_8:
                final ITrack track6 = this.model.getSelectedTrack ();
                return track6 == null ? 0 : track6.getSendBank ().getItem (command.ordinal () - FlexiCommand.TRACK_SELECTED_SET_SEND_1.ordinal ()).getValue ();

            case MASTER_SET_VOLUME:
                return this.model.getMasterTrack ().getVolume ();

            case MASTER_SET_PANORAMA:
                return this.model.getMasterTrack ().getPan ();

            case MASTER_TOGGLE_MUTE:
                return this.model.getMasterTrack ().isMute () ? 127 : 0;

            case MASTER_TOGGLE_SOLO:
                return this.model.getMasterTrack ().isSolo () ? 127 : 0;

            case MASTER_TOGGLE_ARM:
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
                return this.modeManager.getActiveOrTempMode ().getKnobValue (0);
            case MODES_KNOB2:
                return this.modeManager.getActiveOrTempMode ().getKnobValue (1);
            case MODES_KNOB3:
                return this.modeManager.getActiveOrTempMode ().getKnobValue (2);
            case MODES_KNOB4:
                return this.modeManager.getActiveOrTempMode ().getKnobValue (3);
            case MODES_KNOB5:
                return this.modeManager.getActiveOrTempMode ().getKnobValue (4);
            case MODES_KNOB6:
                return this.modeManager.getActiveOrTempMode ().getKnobValue (5);
            case MODES_KNOB7:
                return this.modeManager.getActiveOrTempMode ().getKnobValue (6);
            case MODES_KNOB8:
                return this.modeManager.getActiveOrTempMode ().getKnobValue (7);

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
        switch (command)
        {
            case OFF:
                // No function
                break;

            // Global: Undo
            case GLOBAL_UNDO:
                if (value > 0)
                    this.model.getApplication ().undo ();
                break;
            // Global: Redo
            case GLOBAL_REDO:
                if (value > 0)
                    this.model.getApplication ().redo ();
                break;
            // Global: Previous Project
            case GLOBAL_PREVIOUS_PROJECT:
                if (value > 0)
                    this.model.getProject ().previous ();
                break;
            // Global: Next Project
            case GLOBAL_NEXT_PROJECT:
                if (value > 0)
                    this.model.getProject ().next ();
                break;
            // Global: Toggle Audio Engine
            case GLOBAL_TOGGLE_AUDIO_ENGINE:
                if (value > 0)
                    this.model.getApplication ().toggleEngineActive ();
                break;

            // Transport: Play
            case TRANSPORT_PLAY:
                if (value > 0)
                    this.model.getTransport ().play ();
                break;
            // Transport: Stop
            case TRANSPORT_STOP:
                if (value > 0)
                    this.model.getTransport ().stop ();
                break;
            // Transport: Restart
            case TRANSPORT_RESTART:
                if (value > 0)
                    this.model.getTransport ().restart ();
                break;
            // Transport: Toggle Repeat
            case TRANSPORT_TOGGLE_REPEAT:
                if (value > 0)
                    this.model.getTransport ().toggleLoop ();
                break;
            // Transport: Toggle Metronome
            case TRANSPORT_TOGGLE_METRONOME:
                if (value > 0)
                    this.model.getTransport ().toggleMetronome ();
                break;
            // Transport: Set Metronome Volume
            case TRANSPORT_SET_METRONOME_VOLUME:
                this.handleMetronomeVolume (commandSlot.getKnobMode (), value);
                break;
            // Transport: Toggle Metronome in Pre-roll
            case TRANSPORT_TOGGLE_METRONOME_IN_PREROLL:
                if (value > 0)
                    this.model.getTransport ().togglePrerollMetronome ();
                break;
            // Transport: Toggle Punch In
            case TRANSPORT_TOGGLE_PUNCH_IN:
                if (value > 0)
                    this.model.getTransport ().togglePunchIn ();
                break;
            // Transport: Toggle Punch Out
            case TRANSPORT_TOGGLE_PUNCH_OUT:
                if (value > 0)
                    this.model.getTransport ().togglePunchOut ();
                break;
            // Transport: Toggle Record
            case TRANSPORT_TOGGLE_RECORD:
                if (value > 0)
                    this.model.getTransport ().record ();
                break;
            // Transport: Toggle Arranger Overdub
            case TRANSPORT_TOGGLE_ARRANGER_OVERDUB:
                if (value > 0)
                    this.model.getTransport ().toggleOverdub ();
                break;
            // Transport: Toggle Clip Overdub
            case TRANSPORT_TOGGLE_CLIP_OVERDUB:
                if (value > 0)
                    this.model.getTransport ().toggleLauncherOverdub ();
                break;
            // Transport: Set Crossfader
            case TRANSPORT_SET_CROSSFADER:
                this.handleCrossfade (commandSlot.getKnobMode (), value);
                break;
            // Transport: Toggle Arranger Automation Write
            case TRANSPORT_TOGGLE_ARRANGER_AUTOMATION_WRITE:
                if (value > 0)
                    this.model.getTransport ().toggleWriteArrangerAutomation ();
                break;
            // Transport: Toggle Clip Automation Write
            case TRANSPORT_TOGGLE_CLIP_AUTOMATION_WRITE:
                if (value > 0)
                    this.model.getTransport ().toggleWriteClipLauncherAutomation ();
                break;
            // Transport: Set Write Mode: Latch
            case TRANSPORT_SET_WRITE_MODE_LATCH:
                if (value > 0)
                    this.model.getTransport ().setAutomationWriteMode (TransportConstants.AUTOMATION_MODES_VALUES[0]);
                break;
            // Transport: Set Write Mode: Touch
            case TRANSPORT_SET_WRITE_MODE_TOUCH:
                if (value > 0)
                    this.model.getTransport ().setAutomationWriteMode (TransportConstants.AUTOMATION_MODES_VALUES[1]);
                break;
            // Transport: Set Write Mode: Write
            case TRANSPORT_SET_WRITE_MODE_WRITE:
                if (value > 0)
                    this.model.getTransport ().setAutomationWriteMode (TransportConstants.AUTOMATION_MODES_VALUES[2]);
                break;
            // Transport: Set Tempo
            case TRANSPORT_SET_TEMPO:
                this.handleTempo (commandSlot.getKnobMode (), value);
                break;
            // Transport: Tap Tempo
            case TRANSPORT_TAP_TEMPO:
                if (value > 0)
                    this.model.getTransport ().tapTempo ();
                break;
            // Transport: Move Play Cursor
            case TRANSPORT_MOVE_PLAY_CURSOR:
                this.handlePlayCursor (commandSlot.getKnobMode (), value);
                break;

            // Layout: Set Arrange Layout
            case LAYOUT_SET_ARRANGE_LAYOUT:
                if (value > 0)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_ARRANGE);
                break;
            // Layout: Set Mix Layout
            case LAYOUT_SET_MIX_LAYOUT:
                if (value > 0)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_MIX);
                break;
            // Layout: Set Edit Layout
            case LAYOUT_SET_EDIT_LAYOUT:
                if (value > 0)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_EDIT);
                break;
            // Layout: Toggle Note Editor
            case LAYOUT_TOGGLE_NOTE_EDITOR:
                if (value > 0)
                    this.model.getApplication ().toggleNoteEditor ();
                break;
            // Layout: Toggle Automation Editor
            case LAYOUT_TOGGLE_AUTOMATION_EDITOR:
                if (value > 0)
                    this.model.getApplication ().toggleAutomationEditor ();
                break;
            // Layout: Toggle Devices Panel
            case LAYOUT_TOGGLE_DEVICES_PANEL:
                if (value > 0)
                    this.model.getApplication ().toggleDevices ();
                break;
            // Layout: Toggle Mixer Panel
            case LAYOUT_TOGGLE_MIXER_PANEL:
                if (value > 0)
                    this.model.getApplication ().toggleMixer ();
                break;
            // Layout: Toggle Fullscreen
            case LAYOUT_TOGGLE_FULLSCREEN:
                if (value > 0)
                    this.model.getApplication ().toggleFullScreen ();
                break;
            // Layout: Toggle Arranger Cue Markers
            case LAYOUT_TOGGLE_ARRANGER_CUE_MARKERS:
                if (value > 0)
                    this.model.getArranger ().toggleCueMarkerVisibility ();
                break;
            // Layout: Toggle Arranger Playback Follow
            case LAYOUT_TOGGLE_ARRANGER_PLAYBACK_FOLLOW:
                if (value > 0)
                    this.model.getArranger ().togglePlaybackFollow ();
                break;
            // Layout: Toggle Arranger Track Row Height
            case LAYOUT_TOGGLE_ARRANGER_TRACK_ROW_HEIGHT:
                if (value > 0)
                    this.model.getArranger ().toggleTrackRowHeight ();
                break;
            // Layout: Toggle Arranger Clip Launcher Section
            case LAYOUT_TOGGLE_ARRANGER_CLIP_LAUNCHER_SECTION:
                if (value > 0)
                    this.model.getArranger ().toggleClipLauncher ();
                break;
            // Layout: Toggle Arranger Time Line
            case LAYOUT_TOGGLE_ARRANGER_TIME_LINE:
                if (value > 0)
                    this.model.getArranger ().toggleTimeLine ();
                break;
            // Layout: Toggle Arranger IO Section
            case LAYOUT_TOGGLE_ARRANGER_IO_SECTION:
                if (value > 0)
                    this.model.getArranger ().toggleIoSection ();
                break;
            // Layout: Toggle Arranger Effect Tracks
            case LAYOUT_TOGGLE_ARRANGER_EFFECT_TRACKS:
                if (value > 0)
                    this.model.getArranger ().toggleEffectTracks ();
                break;
            // Layout: Toggle Mixer Clip Launcher Section
            case LAYOUT_TOGGLE_MIXER_CLIP_LAUNCHER_SECTION:
                if (value > 0)
                    this.model.getMixer ().toggleClipLauncherSectionVisibility ();
                break;
            // Layout: Toggle Mixer Cross Fade Section
            case LAYOUT_TOGGLE_MIXER_CROSS_FADE_SECTION:
                if (value > 0)
                    this.model.getMixer ().toggleCrossFadeSectionVisibility ();
                break;
            // Layout: Toggle Mixer Device Section
            case LAYOUT_TOGGLE_MIXER_DEVICE_SECTION:
                if (value > 0)
                    this.model.getMixer ().toggleDeviceSectionVisibility ();
                break;
            // Layout: Toggle Mixer sendsSection
            case LAYOUT_TOGGLE_MIXER_SENDSSECTION:
                if (value > 0)
                    this.model.getMixer ().toggleSendsSectionVisibility ();
                break;
            // Layout: Toggle Mixer IO Section
            case LAYOUT_TOGGLE_MIXER_IO_SECTION:
                if (value > 0)
                    this.model.getMixer ().toggleIoSectionVisibility ();
                break;
            // Layout: Toggle Mixer Meter Section
            case LAYOUT_TOGGLE_MIXER_METER_SECTION:
                if (value > 0)
                    this.model.getMixer ().toggleMeterSectionVisibility ();
                break;

            // Track: Add Audio Track
            case TRACK_ADD_AUDIO_TRACK:
                if (value > 0)
                    this.model.getApplication ().addAudioTrack ();
                break;
            // Track: Add Effect Track
            case TRACK_ADD_EFFECT_TRACK:
                if (value > 0)
                    this.model.getApplication ().addEffectTrack ();
                break;
            // Track: Add Instrument Track
            case TRACK_ADD_INSTRUMENT_TRACK:
                if (value > 0)
                    this.model.getApplication ().addInstrumentTrack ();
                break;
            // Track: Select Previous Bank Page
            case TRACK_SELECT_PREVIOUS_BANK_PAGE:
                if (value > 0)
                    this.scrollTrackLeft (true);
                break;
            // Track: Select Next Bank Page
            case TRACK_SELECT_NEXT_BANK_PAGE:
                if (value > 0)
                    this.scrollTrackRight (true);
                break;
            // Track: Select Previous Track
            case TRACK_SELECT_PREVIOUS_TRACK:
                if (value > 0)
                    this.scrollTrackLeft (false);
                break;
            // Track: Select Next Track
            case TRACK_SELECT_NEXT_TRACK:
                if (value > 0)
                    this.scrollTrackRight (false);
                break;

            case TRACK_SCROLL_TRACKS:
                this.scrollTrack (commandSlot.getKnobMode (), value);
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
                if (value > 0)
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
                if (value > 0)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_ACTIVE.ordinal ()).toggleIsActivated ();
                break;
            case TRACK_SELECTED_TOGGLE_ACTIVE:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleIsActivated ();
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
                this.changeTrackVolume (commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.TRACK_1_SET_VOLUME.ordinal (), value);
                break;
            // Track Selected: Set Volume Track
            case TRACK_SELECTED_SET_VOLUME_TRACK:
                this.changeTrackVolume (commandSlot.getKnobMode (), -1, value);
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
                this.changeTrackPanorama (commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.TRACK_1_SET_PANORAMA.ordinal (), value);
                break;
            // Track Selected: Set Panorama
            case TRACK_SELECTED_SET_PANORAMA:
                this.changeTrackPanorama (commandSlot.getKnobMode (), -1, value);
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
                if (value > 0)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_MUTE.ordinal ()).toggleMute ();
                break;
            // Track Selected: Toggle Mute
            case TRACK_SELECTED_TOGGLE_MUTE:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleMute ();
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
                if (value > 0)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_SOLO.ordinal ()).toggleSolo ();
                break;
            // Track Selected: Toggle Solo
            case TRACK_SELECTED_TOGGLE_SOLO:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleSolo ();
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
                if (value > 0)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_ARM.ordinal ()).toggleRecArm ();
                break;
            // Track Selected: Toggle Arm
            case TRACK_SELECTED_TOGGLE_ARM:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleRecArm ();
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
                if (value > 0)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_MONITOR.ordinal ()).toggleMonitor ();
                break;
            // Track Selected: Toggle Monitor
            case TRACK_SELECTED_TOGGLE_MONITOR:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleMonitor ();
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
                if (value > 0)
                    this.model.getTrackBank ().getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_AUTO_MONITOR.ordinal ()).toggleAutoMonitor ();
                break;
            // Track Selected: Toggle Auto Monitor
            case TRACK_SELECTED_TOGGLE_AUTO_MONITOR:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleAutoMonitor ();
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
                this.changeSendVolume (0, commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_1.ordinal (), value);
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
                this.changeSendVolume (1, commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_2.ordinal (), value);
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
                this.changeSendVolume (2, commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_3.ordinal (), value);
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
                this.changeSendVolume (3, commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_4.ordinal (), value);
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
                this.changeSendVolume (4, commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_5.ordinal (), value);
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
                this.changeSendVolume (5, commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_6.ordinal (), value);
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
                this.changeSendVolume (6, commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_7.ordinal (), value);
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
                this.changeSendVolume (7, commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_8.ordinal (), value);
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
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_SELECTED_SET_SEND_1.ordinal (), commandSlot.getKnobMode (), -1, value);
                break;

            // Master: Set Volume
            case MASTER_SET_VOLUME:
                this.changeMasterVolume (commandSlot.getKnobMode (), value);
                break;
            // Master: Set Panorama
            case MASTER_SET_PANORAMA:
                this.changeMasterPanorama (commandSlot.getKnobMode (), value);
                break;
            // Master: Toggle Mute
            case MASTER_TOGGLE_MUTE:
                if (value > 0)
                    this.model.getMasterTrack ().toggleMute ();
                break;
            // Master: Toggle Solo
            case MASTER_TOGGLE_SOLO:
                if (value > 0)
                    this.model.getMasterTrack ().toggleSolo ();
                break;
            // Master: Toggle Arm
            case MASTER_TOGGLE_ARM:
                if (value > 0)
                    this.model.getMasterTrack ().toggleRecArm ();
                break;
            // Master: Crossfader
            case MASTER_CROSSFADER:
                this.changeMasterCrossfader (commandSlot.getKnobMode (), value);
                break;

            // Device: Toggle Window
            case DEVICE_TOGGLE_WINDOW:
                if (value > 0)
                    this.model.getCursorDevice ().toggleWindowOpen ();
                break;
            // Device: Bypass
            case DEVICE_BYPASS:
                if (value > 0)
                    this.model.getCursorDevice ().toggleEnabledState ();
                break;
            // Device: Expand
            case DEVICE_EXPAND:
                if (value > 0)
                    this.model.getCursorDevice ().toggleExpanded ();
                break;
            // Device: Parameters
            case DEVICE_TOGGLE_PARAMETERS:
                if (value > 0)
                    this.model.getCursorDevice ().toggleParameterPageSectionVisible ();
                break;
            // Device: Select Previous
            case DEVICE_SELECT_PREVIOUS:
                if (value > 0)
                    this.model.getCursorDevice ().selectPrevious ();
                break;
            // Device: Select Next
            case DEVICE_SELECT_NEXT:
                if (value > 0)
                    this.model.getCursorDevice ().selectNext ();
                break;

            case DEVICE_SCROLL_DEVICES:
                this.scrollDevice (commandSlot.getKnobMode (), value);
                break;

            case DEVICE_SELECT_PREVIOUS_PARAMETER_PAGE:
                if (value > 0)
                    this.model.getCursorDevice ().getParameterBank ().scrollBackwards ();
                break;
            case DEVICE_SELECT_NEXT_PARAMETER_PAGE:
                if (value > 0)
                    this.model.getCursorDevice ().getParameterBank ().scrollForwards ();
                break;
            case DEVICE_SCROLL_PARAMETER_PAGES:
                this.scrollParameterPage (commandSlot.getKnobMode (), value);
                break;

            // Device: Select Previous Parameter Bank
            case DEVICE_SELECT_PREVIOUS_PARAMETER_BANK:
                if (value > 0)
                    this.model.getCursorDevice ().getParameterBank ().scrollPageBackwards ();
                break;
            // Device: Select Next Parameter Bank
            case DEVICE_SELECT_NEXT_PARAMETER_BANK:
                if (value > 0)
                    this.model.getCursorDevice ().getParameterBank ().scrollPageForwards ();
                break;

            case DEVICE_SCROLL_PARAMETER_BANKS:
                this.scrollParameterBank (commandSlot.getKnobMode (), value);
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
                this.handleParameter (commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.DEVICE_SET_PARAMETER_1.ordinal (), value);
                break;

            // Browser: Browse Presets
            case BROWSER_BROWSE_PRESETS:
                if (value > 0)
                    this.model.getBrowser ().browseForPresets ();
                break;
            // Browser: Insert Device before current
            case BROWSER_INSERT_DEVICE_BEFORE_CURRENT:
                if (value > 0)
                    this.model.getCursorDevice ().browseToInsertBeforeDevice ();
                break;
            // Browser: Insert Device after current
            case BROWSER_INSERT_DEVICE_AFTER_CURRENT:
                if (value > 0)
                    this.model.getCursorDevice ().browseToInsertAfterDevice ();
                break;
            // Browser: Commit Selection
            case BROWSER_COMMIT_SELECTION:
                if (value > 0)
                    this.model.getBrowser ().stopBrowsing (true);
                break;
            // Browser: Cancel Selection
            case BROWSER_CANCEL_SELECTION:
                if (value > 0)
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
                if (value > 0)
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
                if (value > 0)
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
                this.scrollFilterColumn (commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_1.ordinal (), value);
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
                if (value > 0)
                    this.model.getBrowser ().resetFilterColumn (command.ordinal () - FlexiCommand.BROWSER_RESET_FILTER_COLUMN_1.ordinal ());
                break;

            // Browser: Select the previous preset
            case BROWSER_SELECT_THE_PREVIOUS_PRESET:
                if (value > 0)
                    this.model.getBrowser ().selectPreviousResult ();
                break;
            // Browser: Select the next preset
            case BROWSER_SELECT_THE_NEXT_PRESET:
                if (value > 0)
                    this.model.getBrowser ().selectNextResult ();
                break;
            case BROWSER_SCROLL_PRESETS:
                this.scrollPresetColumn (commandSlot.getKnobMode (), value);
                break;
            // Browser: Select the previous tab
            case BROWSER_SELECT_THE_PREVIOUS_TAB:
                if (value > 0)
                    this.model.getBrowser ().previousContentType ();
                break;
            // Browser: Select the next tab"
            case BROWSER_SELECT_THE_NEXT_TAB:
                if (value > 0)
                    this.model.getBrowser ().nextContentType ();
                break;
            case BROWSER_SCROLL_TABS:
                this.scrollBrowserTabs (commandSlot.getKnobMode (), value);
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
                if (value > 0)
                    this.model.getSceneBank ().getItem (command.ordinal () - FlexiCommand.SCENE_1_LAUNCH_SCENE.ordinal ()).launch ();
                break;

            // Scene: Select Previous Bank
            case SCENE_SELECT_PREVIOUS_BANK:
                if (value > 0)
                    this.model.getSceneBank ().scrollPageBackwards ();
                break;
            // Scene: Select Next Bank
            case SCENE_SELECT_NEXT_BANK:
                if (value > 0)
                    this.model.getSceneBank ().scrollPageForwards ();
                break;
            // Scene: Create Scene from playing Clips
            case SCENE_CREATE_SCENE_FROM_PLAYING_CLIPS:
                if (value > 0)
                    this.model.getProject ().createSceneFromPlayingLauncherClips ();
                break;

            case CLIP_PREVIOUS:
                if (value > 0)
                    this.scrollClipLeft (false);
                break;

            case CLIP_NEXT:
                if (value > 0)
                    this.scrollClipRight (false);
                break;

            case CLIP_SCROLL:
                this.scrollClips (commandSlot.getKnobMode (), value);
                break;

            case CLIP_PLAY:
                if (value > 0)
                {
                    final ISlot selectedSlot = this.model.getSelectedSlot ();
                    if (selectedSlot != null)
                        selectedSlot.launch ();
                }
                break;

            case CLIP_STOP:
                if (value > 0)
                {
                    final ITrack track = this.model.getSelectedTrack ();
                    if (track != null)
                        track.stop ();
                }
                break;

            case CLIP_RECORD:
                if (value > 0)
                {
                    final ISlot selectedSlot = this.model.getSelectedSlot ();
                    if (selectedSlot != null)
                        selectedSlot.record ();
                }
                break;

            case CLIP_NEW:
                if (value > 0)
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
                this.changeModeValue (commandSlot.getKnobMode (), command.ordinal () - FlexiCommand.MODES_KNOB1.ordinal (), value);
                break;

            case MODES_BUTTON1:
            case MODES_BUTTON2:
            case MODES_BUTTON3:
            case MODES_BUTTON4:
            case MODES_BUTTON5:
            case MODES_BUTTON6:
            case MODES_BUTTON7:
            case MODES_BUTTON8:
                if (value > 0)
                {
                    mode.selectItem (command.ordinal () - FlexiCommand.MODES_BUTTON1.ordinal ());
                    this.notifyName (mode);
                }
                break;

            case MODES_NEXT_ITEM:
                if (value > 0)
                {
                    mode.selectNextItem ();
                    this.notifyName (mode);
                }
                break;
            case MODES_PREV_ITEM:
                if (value > 0)
                {
                    mode.selectPreviousItem ();
                    this.notifyName (mode);
                }
                break;
            case MODES_NEXT_PAGE:
                if (value > 0)
                {
                    mode.selectNextItemPage ();
                    this.notifyName (mode);
                }
                break;
            case MODES_PREV_PAGE:
                if (value > 0)
                {
                    mode.selectPreviousItemPage ();
                    this.notifyName (mode);
                }
                break;
            case MODES_SELECT_MODE_TRACK:
                if (value > 0)
                    this.activateMode (Modes.MODE_TRACK);
                break;
            case MODES_SELECT_MODE_VOLUME:
                if (value > 0)
                    this.activateMode (Modes.MODE_VOLUME);
                break;
            case MODES_SELECT_MODE_PAN:
                if (value > 0)
                    this.activateMode (Modes.MODE_PAN);
                break;
            case MODES_SELECT_MODE_SEND1:
                if (value > 0)
                    this.activateMode (Modes.MODE_SEND1);
                break;
            case MODES_SELECT_MODE_SEND2:
                if (value > 0)
                    this.activateMode (Modes.MODE_SEND2);
                break;
            case MODES_SELECT_MODE_SEND3:
                if (value > 0)
                    this.activateMode (Modes.MODE_SEND3);
                break;
            case MODES_SELECT_MODE_SEND4:
                if (value > 0)
                    this.activateMode (Modes.MODE_SEND4);
                break;
            case MODES_SELECT_MODE_SEND5:
                if (value > 0)
                    this.activateMode (Modes.MODE_SEND5);
                break;
            case MODES_SELECT_MODE_SEND6:
                if (value > 0)
                    this.activateMode (Modes.MODE_SEND6);
                break;
            case MODES_SELECT_MODE_SEND7:
                if (value > 0)
                    this.activateMode (Modes.MODE_SEND7);
                break;
            case MODES_SELECT_MODE_SEND8:
                if (value > 0)
                    this.activateMode (Modes.MODE_SEND8);
                break;
            case MODES_SELECT_MODE_DEVICE:
                if (value > 0)
                    this.activateMode (Modes.MODE_DEVICE);
                break;
            case MODES_SELECT_MODE_NEXT:
                if (value > 0)
                    this.selectNextMode ();
                break;
            case MODES_SELECT_MODE_PREV:
                if (value > 0)
                    this.selectPreviousMode ();
                break;
            case MODES_BROWSE_PRESETS:
                if (value > 0)
                {
                    this.model.getBrowser ().browseForPresets ();
                    this.host.scheduleTask ( () -> this.activateMode (Modes.MODE_BROWSE), 500);
                }
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


    private void activateMode (final Integer modeID)
    {
        this.modeManager.setActiveMode (modeID);
        this.host.showNotification (this.modeManager.getMode (modeID).getName ());
    }


    private void handleParameter (final int knobMode, final int index, final int value)
    {
        final IParameter fxParam = this.model.getCursorDevice ().getParameterBank ().getItem (index);
        if (knobMode == KNOB_MODE_ABSOLUTE)
            fxParam.setValue (value);
        else
            fxParam.setValue (this.limit (fxParam.getValue () + this.getRelativeSpeed (knobMode, value)));
    }


    private void changeSendVolume (final int sendIndex, final int knobMode, final int trackIndex, final int value)
    {
        final ITrack track = this.getTrack (trackIndex);
        if (track == null)
            return;
        final ISend send = track.getSendBank ().getItem (sendIndex);
        if (send == null)
            return;
        if (knobMode == KNOB_MODE_ABSOLUTE)
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
        if (knobMode == KNOB_MODE_ABSOLUTE)
            transport.setTempo (value);
        else
            transport.changeTempo (this.getRelativeSpeed (knobMode, value) > 0);
    }


    private void handleCrossfade (final int knobMode, final int value)
    {
        final ITransport transport = this.model.getTransport ();
        if (knobMode == KNOB_MODE_ABSOLUTE)
            transport.setCrossfade (value);
        else
            transport.setCrossfade (this.limit (transport.getCrossfade () + this.getRelativeSpeed (knobMode, value)));
    }


    private void handleMetronomeVolume (final int knobMode, final int value)
    {
        final ITransport transport = this.model.getTransport ();
        if (knobMode == KNOB_MODE_ABSOLUTE)
            transport.setMetronomeVolume (value);
        else
            transport.setMetronomeVolume (this.limit (transport.getMetronomeVolume () + this.getRelativeSpeed (knobMode, value)));
    }


    private void changeTrackVolume (final int knobMode, final int trackIndex, final int value)
    {
        final ITrack track = this.getTrack (trackIndex);
        if (knobMode == KNOB_MODE_ABSOLUTE)
            track.setVolume (value);
        else
            track.setVolume (this.limit (track.getVolume () + this.getRelativeSpeed (knobMode, value)));
    }


    private void scrollTrack (final int knobMode, final int value)
    {
        if (knobMode == KNOB_MODE_ABSOLUTE)
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
        if (knobMode == KNOB_MODE_ABSOLUTE)
            track.setVolume (value);
        else
            track.setVolume (this.limit (track.getVolume () + this.getRelativeSpeed (knobMode, value)));
    }


    private void changeTrackPanorama (final int knobMode, final int trackIndex, final int value)
    {
        final ITrack track = this.getTrack (trackIndex);
        if (knobMode == KNOB_MODE_ABSOLUTE)
            track.setPan (value);
        else
            track.setPan (this.limit (track.getPan () + this.getRelativeSpeed (knobMode, value)));
    }


    private void changeMasterPanorama (final int knobMode, final int value)
    {
        final ITrack track = this.model.getMasterTrack ();
        if (knobMode == KNOB_MODE_ABSOLUTE)
            track.setPan (value);
        else
            track.setPan (this.limit (track.getPan () + this.getRelativeSpeed (knobMode, value)));
    }


    private void changeMasterCrossfader (final int knobMode, final int value)
    {
        final ITransport transport = this.model.getTransport ();
        if (knobMode == KNOB_MODE_ABSOLUTE)
            transport.setCrossfade (value);
        else
            transport.setCrossfade (this.limit (transport.getCrossfade () + this.getRelativeSpeed (knobMode, value)));
    }


    private void changeModeValue (final int knobMode, final int knobIndex, final int value)
    {
        final Mode mode = this.modeManager.getActiveOrTempMode ();
        if (knobMode == KNOB_MODE_ABSOLUTE)
            mode.onKnobValue (knobIndex, value);
        else
        {
            final int knobValue = mode.getKnobValue (knobIndex);
            final int relativeSpeed = (int) Math.round (this.getRelativeSpeed (knobMode, value));
            mode.onKnobValue (knobIndex, knobValue == -1 ? relativeSpeed : this.limit (knobValue + relativeSpeed));
        }
    }


    private void scrollTrackLeft (final boolean switchBank)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack sel = tb.getSelectedItem ();
        final int index = sel == null ? 0 : sel.getIndex () - 1;
        if (index == -1 || switchBank)
        {
            this.scrollTrackBankLeft (sel, index);
            return;
        }
        tb.getItem (index).select ();
    }


    private void scrollTrackBankLeft (final ITrack sel, final int index)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (!tb.canScrollBackwards ())
            return;
        tb.scrollPageBackwards ();
        final int newSel = index == -1 || sel == null ? 7 : sel.getIndex ();
        this.scheduleTask ( () -> tb.getItem (newSel).select (), BUTTON_REPEAT_INTERVAL);
    }


    private void scrollTrackRight (final boolean switchBank)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack sel = tb.getSelectedItem ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        if (index == 8 || switchBank)
        {
            this.scrollTrackBankRight (sel, index);
            return;
        }
        tb.getItem (index).select ();
    }


    private void scrollTrackBankRight (final ITrack sel, final int index)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (!tb.canScrollForwards ())
            return;
        tb.scrollPageForwards ();
        final int newSel = index == 8 || sel == null ? 0 : sel.getIndex ();
        this.scheduleTask ( () -> tb.getItem (newSel).select (), BUTTON_REPEAT_INTERVAL);
    }


    private void scrollDevice (final int knobMode, final int value)
    {
        if (knobMode == KNOB_MODE_ABSOLUTE)
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
        if (knobMode == KNOB_MODE_ABSOLUTE)
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
        if (knobMode == KNOB_MODE_ABSOLUTE)
            return;

        if (!this.increaseKnobMovement ())
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        if (this.getRelativeSpeed (knobMode, value) > 0)
            parameterBank.scrollPageForwards ();
        else
            parameterBank.scrollPageBackwards ();
        this.getDisplay ().notify (cursorDevice.getParameterPageBank ().getSelectedItem ());
    }


    private void scrollFilterColumn (final int knobMode, final int filterColumn, final int value)
    {
        if (knobMode == KNOB_MODE_ABSOLUTE)
            return;

        if (this.getRelativeSpeed (knobMode, value) > 0)
            this.model.getBrowser ().selectNextFilterItem (filterColumn);
        else
            this.model.getBrowser ().selectPreviousFilterItem (filterColumn);
    }


    private void scrollPresetColumn (final int knobMode, final int value)
    {
        if (knobMode == KNOB_MODE_ABSOLUTE)
            return;

        if (this.getRelativeSpeed (knobMode, value) > 0)
            this.model.getBrowser ().selectNextResult ();
        else
            this.model.getBrowser ().selectPreviousResult ();
    }


    private void scrollBrowserTabs (final int knobMode, final int value)
    {
        if (knobMode == KNOB_MODE_ABSOLUTE)
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
        if (knobMode == KNOB_MODE_ABSOLUTE)
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
            tb.getSceneBank ().scrollPageBackwards ();
            final int newSel = index == -1 || sel == null ? 7 : sel.getIndex ();
            this.scheduleTask ( () -> slotBank.getItem (newSel).select (), BUTTON_REPEAT_INTERVAL);
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
            sceneBank.scrollPageForwards ();
            final int newSel = index == 8 || sel == null ? 0 : sel.getIndex ();
            this.scheduleTask ( () -> slotBank.getItem (newSel).select (), BUTTON_REPEAT_INTERVAL);
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
        final Integer activeModeId = this.modeManager.getActiveModeId ();
        int index = MODE_IDS.indexOf (activeModeId);
        Integer newMode;
        int newModeID;
        // If a send mode is selected check if the according send exists
        do
        {
            index--;
            if (index < 0 || index >= MODE_IDS.size ())
                index = MODE_IDS.size () - 1;
            newMode = MODE_IDS.get (index);
            newModeID = newMode.intValue ();
        } while (newModeID >= Modes.MODE_SEND1.intValue () && newModeID <= Modes.MODE_SEND8.intValue () && !trackBank.canEditSend (newModeID - Modes.MODE_SEND1.intValue ()));

        this.activateMode (newMode);
    }


    private void selectNextMode ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        final Integer activeModeId = this.modeManager.getActiveModeId ();
        int index = MODE_IDS.indexOf (activeModeId);
        Integer newMode;
        int newModeID;
        // If a send mode is selected check if the according send exists
        do
        {
            index++;
            if (index < 0 || index >= MODE_IDS.size ())
                index = 0;
            newMode = MODE_IDS.get (index);
            newModeID = newMode.intValue ();
        } while (newModeID >= Modes.MODE_SEND1.intValue () && newModeID <= Modes.MODE_SEND8.intValue () && !trackBank.canEditSend (newModeID - Modes.MODE_SEND1.intValue ()));

        this.activateMode (newMode);
    }


    private int limit (final double value)
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();
        return (int) Math.max (0, Math.min (value, valueChanger.getUpperBound () - 1));
    }
}