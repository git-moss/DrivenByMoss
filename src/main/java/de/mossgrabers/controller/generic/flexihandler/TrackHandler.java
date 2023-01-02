// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.controller.generic.flexihandler.utils.FlexiHandlerException;
import de.mossgrabers.controller.generic.flexihandler.utils.KnobMode;
import de.mossgrabers.controller.generic.flexihandler.utils.MidiValue;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.track.ToggleTrackBanksCommand;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * The handler for track commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackHandler extends AbstractHandler
{
    private final TriggerCommand toggleTrackBankCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param absoluteLowResValueChanger The default absolute value changer in low res mode
     * @param signedBitRelativeValueChanger The signed bit relative value changer
     * @param offsetBinaryRelativeValueChanger The offset binary relative value changer
     */
    public TrackHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);

        this.toggleTrackBankCommand = new ToggleTrackBanksCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.TRACK_TOGGLE_TRACK_BANK,
            FlexiCommand.TRACK_ADD_AUDIO_TRACK,
            FlexiCommand.TRACK_ADD_EFFECT_TRACK,
            FlexiCommand.TRACK_ADD_INSTRUMENT_TRACK,
            FlexiCommand.TRACK_SELECT_PREVIOUS_BANK_PAGE,
            FlexiCommand.TRACK_SELECT_NEXT_BANK_PAGE,
            FlexiCommand.TRACK_SELECT_PREVIOUS_TRACK,
            FlexiCommand.TRACK_SELECT_NEXT_TRACK,
            FlexiCommand.TRACK_SCROLL_TRACKS,
            FlexiCommand.TRACK_1_SELECT,
            FlexiCommand.TRACK_2_SELECT,
            FlexiCommand.TRACK_3_SELECT,
            FlexiCommand.TRACK_4_SELECT,
            FlexiCommand.TRACK_5_SELECT,
            FlexiCommand.TRACK_6_SELECT,
            FlexiCommand.TRACK_7_SELECT,
            FlexiCommand.TRACK_8_SELECT,
            FlexiCommand.TRACK_1_TOGGLE_ACTIVE,
            FlexiCommand.TRACK_2_TOGGLE_ACTIVE,
            FlexiCommand.TRACK_3_TOGGLE_ACTIVE,
            FlexiCommand.TRACK_4_TOGGLE_ACTIVE,
            FlexiCommand.TRACK_5_TOGGLE_ACTIVE,
            FlexiCommand.TRACK_6_TOGGLE_ACTIVE,
            FlexiCommand.TRACK_7_TOGGLE_ACTIVE,
            FlexiCommand.TRACK_8_TOGGLE_ACTIVE,
            FlexiCommand.TRACK_1_SET_ACTIVE,
            FlexiCommand.TRACK_2_SET_ACTIVE,
            FlexiCommand.TRACK_3_SET_ACTIVE,
            FlexiCommand.TRACK_4_SET_ACTIVE,
            FlexiCommand.TRACK_5_SET_ACTIVE,
            FlexiCommand.TRACK_6_SET_ACTIVE,
            FlexiCommand.TRACK_7_SET_ACTIVE,
            FlexiCommand.TRACK_8_SET_ACTIVE,
            FlexiCommand.TRACK_SELECTED_TOGGLE_ACTIVE,
            FlexiCommand.TRACK_SELECTED_SET_ACTIVE,
            FlexiCommand.TRACK_1_SET_VOLUME,
            FlexiCommand.TRACK_2_SET_VOLUME,
            FlexiCommand.TRACK_3_SET_VOLUME,
            FlexiCommand.TRACK_4_SET_VOLUME,
            FlexiCommand.TRACK_5_SET_VOLUME,
            FlexiCommand.TRACK_6_SET_VOLUME,
            FlexiCommand.TRACK_7_SET_VOLUME,
            FlexiCommand.TRACK_8_SET_VOLUME,
            FlexiCommand.TRACK_SELECTED_SET_VOLUME_TRACK,
            FlexiCommand.TRACK_1_SET_PANORAMA,
            FlexiCommand.TRACK_2_SET_PANORAMA,
            FlexiCommand.TRACK_3_SET_PANORAMA,
            FlexiCommand.TRACK_4_SET_PANORAMA,
            FlexiCommand.TRACK_5_SET_PANORAMA,
            FlexiCommand.TRACK_6_SET_PANORAMA,
            FlexiCommand.TRACK_7_SET_PANORAMA,
            FlexiCommand.TRACK_8_SET_PANORAMA,
            FlexiCommand.TRACK_SELECTED_SET_PANORAMA,
            FlexiCommand.TRACK_1_TOGGLE_MUTE,
            FlexiCommand.TRACK_2_TOGGLE_MUTE,
            FlexiCommand.TRACK_3_TOGGLE_MUTE,
            FlexiCommand.TRACK_4_TOGGLE_MUTE,
            FlexiCommand.TRACK_5_TOGGLE_MUTE,
            FlexiCommand.TRACK_6_TOGGLE_MUTE,
            FlexiCommand.TRACK_7_TOGGLE_MUTE,
            FlexiCommand.TRACK_8_TOGGLE_MUTE,
            FlexiCommand.TRACK_1_SET_MUTE,
            FlexiCommand.TRACK_2_SET_MUTE,
            FlexiCommand.TRACK_3_SET_MUTE,
            FlexiCommand.TRACK_4_SET_MUTE,
            FlexiCommand.TRACK_5_SET_MUTE,
            FlexiCommand.TRACK_6_SET_MUTE,
            FlexiCommand.TRACK_7_SET_MUTE,
            FlexiCommand.TRACK_8_SET_MUTE,
            FlexiCommand.TRACK_SELECTED_TOGGLE_MUTE,
            FlexiCommand.TRACK_SELECTED_SET_MUTE,
            FlexiCommand.TRACK_1_TOGGLE_SOLO,
            FlexiCommand.TRACK_2_TOGGLE_SOLO,
            FlexiCommand.TRACK_3_TOGGLE_SOLO,
            FlexiCommand.TRACK_4_TOGGLE_SOLO,
            FlexiCommand.TRACK_5_TOGGLE_SOLO,
            FlexiCommand.TRACK_6_TOGGLE_SOLO,
            FlexiCommand.TRACK_7_TOGGLE_SOLO,
            FlexiCommand.TRACK_8_TOGGLE_SOLO,
            FlexiCommand.TRACK_1_SET_SOLO,
            FlexiCommand.TRACK_2_SET_SOLO,
            FlexiCommand.TRACK_3_SET_SOLO,
            FlexiCommand.TRACK_4_SET_SOLO,
            FlexiCommand.TRACK_5_SET_SOLO,
            FlexiCommand.TRACK_6_SET_SOLO,
            FlexiCommand.TRACK_7_SET_SOLO,
            FlexiCommand.TRACK_8_SET_SOLO,
            FlexiCommand.TRACK_SELECTED_TOGGLE_SOLO,
            FlexiCommand.TRACK_SELECTED_SET_SOLO,
            FlexiCommand.TRACK_1_TOGGLE_ARM,
            FlexiCommand.TRACK_2_TOGGLE_ARM,
            FlexiCommand.TRACK_3_TOGGLE_ARM,
            FlexiCommand.TRACK_4_TOGGLE_ARM,
            FlexiCommand.TRACK_5_TOGGLE_ARM,
            FlexiCommand.TRACK_6_TOGGLE_ARM,
            FlexiCommand.TRACK_7_TOGGLE_ARM,
            FlexiCommand.TRACK_8_TOGGLE_ARM,
            FlexiCommand.TRACK_1_SET_ARM,
            FlexiCommand.TRACK_2_SET_ARM,
            FlexiCommand.TRACK_3_SET_ARM,
            FlexiCommand.TRACK_4_SET_ARM,
            FlexiCommand.TRACK_5_SET_ARM,
            FlexiCommand.TRACK_6_SET_ARM,
            FlexiCommand.TRACK_7_SET_ARM,
            FlexiCommand.TRACK_8_SET_ARM,
            FlexiCommand.TRACK_SELECTED_TOGGLE_ARM,
            FlexiCommand.TRACK_SELECTED_SET_ARM,
            FlexiCommand.TRACK_1_TOGGLE_MONITOR,
            FlexiCommand.TRACK_2_TOGGLE_MONITOR,
            FlexiCommand.TRACK_3_TOGGLE_MONITOR,
            FlexiCommand.TRACK_4_TOGGLE_MONITOR,
            FlexiCommand.TRACK_5_TOGGLE_MONITOR,
            FlexiCommand.TRACK_6_TOGGLE_MONITOR,
            FlexiCommand.TRACK_7_TOGGLE_MONITOR,
            FlexiCommand.TRACK_8_TOGGLE_MONITOR,
            FlexiCommand.TRACK_1_SET_MONITOR,
            FlexiCommand.TRACK_2_SET_MONITOR,
            FlexiCommand.TRACK_3_SET_MONITOR,
            FlexiCommand.TRACK_4_SET_MONITOR,
            FlexiCommand.TRACK_5_SET_MONITOR,
            FlexiCommand.TRACK_6_SET_MONITOR,
            FlexiCommand.TRACK_7_SET_MONITOR,
            FlexiCommand.TRACK_8_SET_MONITOR,
            FlexiCommand.TRACK_SELECTED_TOGGLE_MONITOR,
            FlexiCommand.TRACK_SELECTED_SET_MONITOR,
            FlexiCommand.TRACK_1_TOGGLE_AUTO_MONITOR,
            FlexiCommand.TRACK_2_TOGGLE_AUTO_MONITOR,
            FlexiCommand.TRACK_3_TOGGLE_AUTO_MONITOR,
            FlexiCommand.TRACK_4_TOGGLE_AUTO_MONITOR,
            FlexiCommand.TRACK_5_TOGGLE_AUTO_MONITOR,
            FlexiCommand.TRACK_6_TOGGLE_AUTO_MONITOR,
            FlexiCommand.TRACK_7_TOGGLE_AUTO_MONITOR,
            FlexiCommand.TRACK_8_TOGGLE_AUTO_MONITOR,
            FlexiCommand.TRACK_1_SET_AUTO_MONITOR,
            FlexiCommand.TRACK_2_SET_AUTO_MONITOR,
            FlexiCommand.TRACK_3_SET_AUTO_MONITOR,
            FlexiCommand.TRACK_4_SET_AUTO_MONITOR,
            FlexiCommand.TRACK_5_SET_AUTO_MONITOR,
            FlexiCommand.TRACK_6_SET_AUTO_MONITOR,
            FlexiCommand.TRACK_7_SET_AUTO_MONITOR,
            FlexiCommand.TRACK_8_SET_AUTO_MONITOR,
            FlexiCommand.TRACK_SELECTED_TOGGLE_AUTO_MONITOR,
            FlexiCommand.TRACK_SELECTED_SET_AUTO_MONITOR,
            FlexiCommand.TRACK_SELECTED_TOGGLE_PIN,
            FlexiCommand.TRACK_SELECTED_SET_PIN,
            FlexiCommand.TRACK_1_SET_SEND_1,
            FlexiCommand.TRACK_2_SET_SEND_1,
            FlexiCommand.TRACK_3_SET_SEND_1,
            FlexiCommand.TRACK_4_SET_SEND_1,
            FlexiCommand.TRACK_5_SET_SEND_1,
            FlexiCommand.TRACK_6_SET_SEND_1,
            FlexiCommand.TRACK_7_SET_SEND_1,
            FlexiCommand.TRACK_8_SET_SEND_1,
            FlexiCommand.TRACK_1_SET_SEND_2,
            FlexiCommand.TRACK_2_SET_SEND_2,
            FlexiCommand.TRACK_3_SET_SEND_2,
            FlexiCommand.TRACK_4_SET_SEND_2,
            FlexiCommand.TRACK_5_SET_SEND_2,
            FlexiCommand.TRACK_6_SET_SEND_2,
            FlexiCommand.TRACK_7_SET_SEND_2,
            FlexiCommand.TRACK_8_SET_SEND_2,
            FlexiCommand.TRACK_1_SET_SEND_3,
            FlexiCommand.TRACK_2_SET_SEND_3,
            FlexiCommand.TRACK_3_SET_SEND_3,
            FlexiCommand.TRACK_4_SET_SEND_3,
            FlexiCommand.TRACK_5_SET_SEND_3,
            FlexiCommand.TRACK_6_SET_SEND_3,
            FlexiCommand.TRACK_7_SET_SEND_3,
            FlexiCommand.TRACK_8_SET_SEND_3,
            FlexiCommand.TRACK_1_SET_SEND_4,
            FlexiCommand.TRACK_2_SET_SEND_4,
            FlexiCommand.TRACK_3_SET_SEND_4,
            FlexiCommand.TRACK_4_SET_SEND_4,
            FlexiCommand.TRACK_5_SET_SEND_4,
            FlexiCommand.TRACK_6_SET_SEND_4,
            FlexiCommand.TRACK_7_SET_SEND_4,
            FlexiCommand.TRACK_8_SET_SEND_4,
            FlexiCommand.TRACK_1_SET_SEND_5,
            FlexiCommand.TRACK_2_SET_SEND_5,
            FlexiCommand.TRACK_3_SET_SEND_5,
            FlexiCommand.TRACK_4_SET_SEND_5,
            FlexiCommand.TRACK_5_SET_SEND_5,
            FlexiCommand.TRACK_6_SET_SEND_5,
            FlexiCommand.TRACK_7_SET_SEND_5,
            FlexiCommand.TRACK_8_SET_SEND_5,
            FlexiCommand.TRACK_1_SET_SEND_6,
            FlexiCommand.TRACK_2_SET_SEND_6,
            FlexiCommand.TRACK_3_SET_SEND_6,
            FlexiCommand.TRACK_4_SET_SEND_6,
            FlexiCommand.TRACK_5_SET_SEND_6,
            FlexiCommand.TRACK_6_SET_SEND_6,
            FlexiCommand.TRACK_7_SET_SEND_6,
            FlexiCommand.TRACK_8_SET_SEND_6,
            FlexiCommand.TRACK_1_SET_SEND_7,
            FlexiCommand.TRACK_2_SET_SEND_7,
            FlexiCommand.TRACK_3_SET_SEND_7,
            FlexiCommand.TRACK_4_SET_SEND_7,
            FlexiCommand.TRACK_5_SET_SEND_7,
            FlexiCommand.TRACK_6_SET_SEND_7,
            FlexiCommand.TRACK_7_SET_SEND_7,
            FlexiCommand.TRACK_8_SET_SEND_7,
            FlexiCommand.TRACK_1_SET_SEND_8,
            FlexiCommand.TRACK_2_SET_SEND_8,
            FlexiCommand.TRACK_3_SET_SEND_8,
            FlexiCommand.TRACK_4_SET_SEND_8,
            FlexiCommand.TRACK_5_SET_SEND_8,
            FlexiCommand.TRACK_6_SET_SEND_8,
            FlexiCommand.TRACK_7_SET_SEND_8,
            FlexiCommand.TRACK_8_SET_SEND_8,
            FlexiCommand.TRACK_SELECTED_SET_SEND_1,
            FlexiCommand.TRACK_SELECTED_SET_SEND_2,
            FlexiCommand.TRACK_SELECTED_SET_SEND_3,
            FlexiCommand.TRACK_SELECTED_SET_SEND_4,
            FlexiCommand.TRACK_SELECTED_SET_SEND_5,
            FlexiCommand.TRACK_SELECTED_SET_SEND_6,
            FlexiCommand.TRACK_SELECTED_SET_SEND_7,
            FlexiCommand.TRACK_SELECTED_SET_SEND_8
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final ITrackBank trackBank = this.model.getCurrentTrackBank ();
        if (trackBank == null)
            return -1;

        final ICursorTrack cursorTrack = this.model.getCursorTrack ();

        switch (command)
        {
            case TRACK_1_SELECT, TRACK_2_SELECT, TRACK_3_SELECT, TRACK_4_SELECT, TRACK_5_SELECT, TRACK_6_SELECT, TRACK_7_SELECT, TRACK_8_SELECT:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SELECT.ordinal ()).isSelected () ? 127 : 0;

            case TRACK_1_TOGGLE_ACTIVE, TRACK_2_TOGGLE_ACTIVE, TRACK_3_TOGGLE_ACTIVE, TRACK_4_TOGGLE_ACTIVE, TRACK_5_TOGGLE_ACTIVE, TRACK_6_TOGGLE_ACTIVE, TRACK_7_TOGGLE_ACTIVE, TRACK_8_TOGGLE_ACTIVE:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_ACTIVE.ordinal ()).isActivated () ? 127 : 0;

            case TRACK_1_SET_ACTIVE, TRACK_2_SET_ACTIVE, TRACK_3_SET_ACTIVE, TRACK_4_SET_ACTIVE, TRACK_5_SET_ACTIVE, TRACK_6_SET_ACTIVE, TRACK_7_SET_ACTIVE, TRACK_8_SET_ACTIVE:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_ACTIVE.ordinal ()).isActivated () ? 127 : 0;

            case TRACK_SELECTED_TOGGLE_ACTIVE, TRACK_SELECTED_SET_ACTIVE:
                return cursorTrack.doesExist () && cursorTrack.isActivated () ? 127 : 0;

            case TRACK_1_SET_VOLUME, TRACK_2_SET_VOLUME, TRACK_3_SET_VOLUME, TRACK_4_SET_VOLUME, TRACK_5_SET_VOLUME, TRACK_6_SET_VOLUME, TRACK_7_SET_VOLUME, TRACK_8_SET_VOLUME:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_VOLUME.ordinal ()).getVolume ();

            case TRACK_SELECTED_SET_VOLUME_TRACK:
                return cursorTrack.doesExist () ? cursorTrack.getVolume () : 0;

            case TRACK_1_SET_PANORAMA, TRACK_2_SET_PANORAMA, TRACK_3_SET_PANORAMA, TRACK_4_SET_PANORAMA, TRACK_5_SET_PANORAMA, TRACK_6_SET_PANORAMA, TRACK_7_SET_PANORAMA, TRACK_8_SET_PANORAMA:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_PANORAMA.ordinal ()).getPan ();

            case TRACK_SELECTED_SET_PANORAMA:
                return cursorTrack.doesExist () ? cursorTrack.getPan () : 0;

            case TRACK_1_TOGGLE_MUTE, TRACK_2_TOGGLE_MUTE, TRACK_3_TOGGLE_MUTE, TRACK_4_TOGGLE_MUTE, TRACK_5_TOGGLE_MUTE, TRACK_6_TOGGLE_MUTE, TRACK_7_TOGGLE_MUTE, TRACK_8_TOGGLE_MUTE:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_MUTE.ordinal ()).isMute () ? 127 : 0;
            case TRACK_1_SET_MUTE, TRACK_2_SET_MUTE, TRACK_3_SET_MUTE, TRACK_4_SET_MUTE, TRACK_5_SET_MUTE, TRACK_6_SET_MUTE, TRACK_7_SET_MUTE, TRACK_8_SET_MUTE:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_MUTE.ordinal ()).isMute () ? 127 : 0;
            case TRACK_SELECTED_TOGGLE_MUTE, TRACK_SELECTED_SET_MUTE:
                return cursorTrack.doesExist () && cursorTrack.isMute () ? 127 : 0;

            case TRACK_1_TOGGLE_SOLO, TRACK_2_TOGGLE_SOLO, TRACK_3_TOGGLE_SOLO, TRACK_4_TOGGLE_SOLO, TRACK_5_TOGGLE_SOLO, TRACK_6_TOGGLE_SOLO, TRACK_7_TOGGLE_SOLO, TRACK_8_TOGGLE_SOLO:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_SOLO.ordinal ()).isSolo () ? 127 : 0;
            case TRACK_1_SET_SOLO, TRACK_2_SET_SOLO, TRACK_3_SET_SOLO, TRACK_4_SET_SOLO, TRACK_5_SET_SOLO, TRACK_6_SET_SOLO, TRACK_7_SET_SOLO, TRACK_8_SET_SOLO:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_SOLO.ordinal ()).isSolo () ? 127 : 0;
            case TRACK_SELECTED_TOGGLE_SOLO, TRACK_SELECTED_SET_SOLO:
                return cursorTrack.doesExist () && cursorTrack.isSolo () ? 127 : 0;

            case TRACK_1_TOGGLE_ARM, TRACK_2_TOGGLE_ARM, TRACK_3_TOGGLE_ARM, TRACK_4_TOGGLE_ARM, TRACK_5_TOGGLE_ARM, TRACK_6_TOGGLE_ARM, TRACK_7_TOGGLE_ARM, TRACK_8_TOGGLE_ARM:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_ARM.ordinal ()).isRecArm () ? 127 : 0;
            case TRACK_1_SET_ARM, TRACK_2_SET_ARM, TRACK_3_SET_ARM, TRACK_4_SET_ARM, TRACK_5_SET_ARM, TRACK_6_SET_ARM, TRACK_7_SET_ARM, TRACK_8_SET_ARM:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_ARM.ordinal ()).isRecArm () ? 127 : 0;
            case TRACK_SELECTED_TOGGLE_ARM, TRACK_SELECTED_SET_ARM:
                return cursorTrack.doesExist () && cursorTrack.isRecArm () ? 127 : 0;

            case TRACK_1_TOGGLE_MONITOR, TRACK_2_TOGGLE_MONITOR, TRACK_3_TOGGLE_MONITOR, TRACK_4_TOGGLE_MONITOR, TRACK_5_TOGGLE_MONITOR, TRACK_6_TOGGLE_MONITOR, TRACK_7_TOGGLE_MONITOR, TRACK_8_TOGGLE_MONITOR:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_MONITOR.ordinal ()).isMonitor () ? 127 : 0;
            case TRACK_1_SET_MONITOR, TRACK_2_SET_MONITOR, TRACK_3_SET_MONITOR, TRACK_4_SET_MONITOR, TRACK_5_SET_MONITOR, TRACK_6_SET_MONITOR, TRACK_7_SET_MONITOR, TRACK_8_SET_MONITOR:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_MONITOR.ordinal ()).isMonitor () ? 127 : 0;
            case TRACK_SELECTED_TOGGLE_MONITOR, TRACK_SELECTED_SET_MONITOR:
                return cursorTrack.doesExist () && cursorTrack.isMonitor () ? 127 : 0;

            case TRACK_1_TOGGLE_AUTO_MONITOR, TRACK_2_TOGGLE_AUTO_MONITOR, TRACK_3_TOGGLE_AUTO_MONITOR, TRACK_4_TOGGLE_AUTO_MONITOR, TRACK_5_TOGGLE_AUTO_MONITOR, TRACK_6_TOGGLE_AUTO_MONITOR, TRACK_7_TOGGLE_AUTO_MONITOR, TRACK_8_TOGGLE_AUTO_MONITOR:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_AUTO_MONITOR.ordinal ()).isAutoMonitor () ? 127 : 0;
            case TRACK_1_SET_AUTO_MONITOR, TRACK_2_SET_AUTO_MONITOR, TRACK_3_SET_AUTO_MONITOR, TRACK_4_SET_AUTO_MONITOR, TRACK_5_SET_AUTO_MONITOR, TRACK_6_SET_AUTO_MONITOR, TRACK_7_SET_AUTO_MONITOR, TRACK_8_SET_AUTO_MONITOR:
                return trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_AUTO_MONITOR.ordinal ()).isAutoMonitor () ? 127 : 0;
            case TRACK_SELECTED_TOGGLE_AUTO_MONITOR, TRACK_SELECTED_SET_AUTO_MONITOR:
                return cursorTrack.doesExist () && cursorTrack.isAutoMonitor () ? 127 : 0;

            case TRACK_SELECTED_TOGGLE_PIN, TRACK_SELECTED_SET_PIN:
                return cursorTrack.doesExist () && cursorTrack.isPinned () ? 127 : 0;

            case TRACK_1_SET_SEND_1, TRACK_2_SET_SEND_1, TRACK_3_SET_SEND_1, TRACK_4_SET_SEND_1, TRACK_5_SET_SEND_1, TRACK_6_SET_SEND_1, TRACK_7_SET_SEND_1, TRACK_8_SET_SEND_1:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_1.ordinal (), 0);
            case TRACK_1_SET_SEND_2, TRACK_2_SET_SEND_2, TRACK_3_SET_SEND_2, TRACK_4_SET_SEND_2, TRACK_5_SET_SEND_2, TRACK_6_SET_SEND_2, TRACK_7_SET_SEND_2, TRACK_8_SET_SEND_2:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_2.ordinal (), 1);
            case TRACK_1_SET_SEND_3, TRACK_2_SET_SEND_3, TRACK_3_SET_SEND_3, TRACK_4_SET_SEND_3, TRACK_5_SET_SEND_3, TRACK_6_SET_SEND_3, TRACK_7_SET_SEND_3, TRACK_8_SET_SEND_3:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_3.ordinal (), 2);
            case TRACK_1_SET_SEND_4, TRACK_2_SET_SEND_4, TRACK_3_SET_SEND_4, TRACK_4_SET_SEND_4, TRACK_5_SET_SEND_4, TRACK_6_SET_SEND_4, TRACK_7_SET_SEND_4, TRACK_8_SET_SEND_4:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_4.ordinal (), 3);
            case TRACK_1_SET_SEND_5, TRACK_2_SET_SEND_5, TRACK_3_SET_SEND_5, TRACK_4_SET_SEND_5, TRACK_5_SET_SEND_5, TRACK_6_SET_SEND_5, TRACK_7_SET_SEND_5, TRACK_8_SET_SEND_5:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_5.ordinal (), 4);
            case TRACK_1_SET_SEND_6, TRACK_2_SET_SEND_6, TRACK_3_SET_SEND_6, TRACK_4_SET_SEND_6, TRACK_5_SET_SEND_6, TRACK_6_SET_SEND_6, TRACK_7_SET_SEND_6, TRACK_8_SET_SEND_6:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_6.ordinal (), 5);
            case TRACK_1_SET_SEND_7, TRACK_2_SET_SEND_7, TRACK_3_SET_SEND_7, TRACK_4_SET_SEND_7, TRACK_5_SET_SEND_7, TRACK_6_SET_SEND_7, TRACK_7_SET_SEND_7, TRACK_8_SET_SEND_7:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_7.ordinal (), 6);
            case TRACK_1_SET_SEND_8, TRACK_2_SET_SEND_8, TRACK_3_SET_SEND_8, TRACK_4_SET_SEND_8, TRACK_5_SET_SEND_8, TRACK_6_SET_SEND_8, TRACK_7_SET_SEND_8, TRACK_8_SET_SEND_8:
                return this.getSendValue (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_8.ordinal (), 7);
            case TRACK_SELECTED_SET_SEND_1, TRACK_SELECTED_SET_SEND_2, TRACK_SELECTED_SET_SEND_3, TRACK_SELECTED_SET_SEND_4, TRACK_SELECTED_SET_SEND_5, TRACK_SELECTED_SET_SEND_6, TRACK_SELECTED_SET_SEND_7, TRACK_SELECTED_SET_SEND_8:
                return this.getSendValue (-1, command.ordinal () - FlexiCommand.TRACK_SELECTED_SET_SEND_1.ordinal ());

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final ITrackBank trackBank = this.model.getCurrentTrackBank ();
        if (trackBank == null)
            return;

        final ICursorTrack cursorTrack = this.model.getCursorTrack ();

        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);

        switch (command)
        {
            case TRACK_TOGGLE_TRACK_BANK:
                if (isButtonPressed)
                    this.toggleTrackBankCommand.execute (ButtonEvent.DOWN, 127);
                break;
            // Track: Add Audio Track
            case TRACK_ADD_AUDIO_TRACK:
                if (isButtonPressed)
                    this.model.getTrackBank ().addChannel (ChannelType.AUDIO);
                break;
            // Track: Add Effect Track
            case TRACK_ADD_EFFECT_TRACK:
                if (isButtonPressed)
                    this.model.getApplication ().addEffectTrack ();
                break;
            // Track: Add Instrument Track
            case TRACK_ADD_INSTRUMENT_TRACK:
                if (isButtonPressed)
                    this.model.getTrackBank ().addChannel (ChannelType.INSTRUMENT);
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
            case TRACK_1_SELECT, TRACK_2_SELECT, TRACK_3_SELECT, TRACK_4_SELECT, TRACK_5_SELECT, TRACK_6_SELECT, TRACK_7_SELECT, TRACK_8_SELECT:
                if (isButtonPressed)
                {
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SELECT.ordinal ()).selectOrExpandGroup ();
                    this.mvHelper.notifySelectedTrack ();
                }
                break;

            // Track 1-8: Toggle Active
            case TRACK_1_TOGGLE_ACTIVE, TRACK_2_TOGGLE_ACTIVE, TRACK_3_TOGGLE_ACTIVE, TRACK_4_TOGGLE_ACTIVE, TRACK_5_TOGGLE_ACTIVE, TRACK_6_TOGGLE_ACTIVE, TRACK_7_TOGGLE_ACTIVE, TRACK_8_TOGGLE_ACTIVE:
                if (isButtonPressed)
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_ACTIVE.ordinal ()).toggleIsActivated ();
                break;
            // Track 1-8: Set Active
            case TRACK_1_SET_ACTIVE, TRACK_2_SET_ACTIVE, TRACK_3_SET_ACTIVE, TRACK_4_SET_ACTIVE, TRACK_5_SET_ACTIVE, TRACK_6_SET_ACTIVE, TRACK_7_SET_ACTIVE, TRACK_8_SET_ACTIVE:
                if (isButtonPressed)
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_ACTIVE.ordinal ()).setIsActivated (value.isPositive ());
                break;
            case TRACK_SELECTED_TOGGLE_ACTIVE:
                if (isButtonPressed)
                    cursorTrack.toggleIsActivated ();
                break;
            case TRACK_SELECTED_SET_ACTIVE:
                if (isButtonPressed)
                    cursorTrack.setIsActivated (value.isPositive ());
                break;

            // Track 1-8: Set Volume
            case TRACK_1_SET_VOLUME, TRACK_2_SET_VOLUME, TRACK_3_SET_VOLUME, TRACK_4_SET_VOLUME, TRACK_5_SET_VOLUME, TRACK_6_SET_VOLUME, TRACK_7_SET_VOLUME, TRACK_8_SET_VOLUME:
                this.changeTrackVolume (knobMode, command.ordinal () - FlexiCommand.TRACK_1_SET_VOLUME.ordinal (), value);
                break;
            // Track Selected: Set Volume Track
            case TRACK_SELECTED_SET_VOLUME_TRACK:
                this.changeTrackVolume (knobMode, -1, value);
                break;

            // Track 1-8: Set Panorama
            case TRACK_1_SET_PANORAMA, TRACK_2_SET_PANORAMA, TRACK_3_SET_PANORAMA, TRACK_4_SET_PANORAMA, TRACK_5_SET_PANORAMA, TRACK_6_SET_PANORAMA, TRACK_7_SET_PANORAMA, TRACK_8_SET_PANORAMA:
                this.changeTrackPanorama (knobMode, command.ordinal () - FlexiCommand.TRACK_1_SET_PANORAMA.ordinal (), value);
                break;
            // Track Selected: Set Panorama
            case TRACK_SELECTED_SET_PANORAMA:
                this.changeTrackPanorama (knobMode, -1, value);
                break;

            // Track 1-8: Toggle Mute
            case TRACK_1_TOGGLE_MUTE, TRACK_2_TOGGLE_MUTE, TRACK_3_TOGGLE_MUTE, TRACK_4_TOGGLE_MUTE, TRACK_5_TOGGLE_MUTE, TRACK_6_TOGGLE_MUTE, TRACK_7_TOGGLE_MUTE, TRACK_8_TOGGLE_MUTE:
                if (isButtonPressed)
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_MUTE.ordinal ()).toggleMute ();
                break;
            // Track 1-8: Set Mute
            case TRACK_1_SET_MUTE, TRACK_2_SET_MUTE, TRACK_3_SET_MUTE, TRACK_4_SET_MUTE, TRACK_5_SET_MUTE, TRACK_6_SET_MUTE, TRACK_7_SET_MUTE, TRACK_8_SET_MUTE:
                if (isButtonPressed)
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_MUTE.ordinal ()).setMute (value.isPositive ());
                break;
            // Track Selected: Toggle Mute
            case TRACK_SELECTED_TOGGLE_MUTE:
                if (isButtonPressed)
                    cursorTrack.toggleMute ();
                break;
            // Track Selected: Set Mute
            case TRACK_SELECTED_SET_MUTE:
                if (isButtonPressed)
                    cursorTrack.setMute (value.isPositive ());
                break;

            // Track 1-8: Toggle Solo
            case TRACK_1_TOGGLE_SOLO, TRACK_2_TOGGLE_SOLO, TRACK_3_TOGGLE_SOLO, TRACK_4_TOGGLE_SOLO, TRACK_5_TOGGLE_SOLO, TRACK_6_TOGGLE_SOLO, TRACK_7_TOGGLE_SOLO, TRACK_8_TOGGLE_SOLO:
                if (isButtonPressed)
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_SOLO.ordinal ()).toggleSolo ();
                break;
            // Track 1-8: Set Solo
            case TRACK_1_SET_SOLO, TRACK_2_SET_SOLO, TRACK_3_SET_SOLO, TRACK_4_SET_SOLO, TRACK_5_SET_SOLO, TRACK_6_SET_SOLO, TRACK_7_SET_SOLO, TRACK_8_SET_SOLO:
                if (isButtonPressed)
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_SOLO.ordinal ()).setSolo (value.isPositive ());
                break;
            // Track Selected: Toggle Solo
            case TRACK_SELECTED_TOGGLE_SOLO:
                if (isButtonPressed)
                    cursorTrack.toggleSolo ();
                break;
            // Track Selected: Set Solo
            case TRACK_SELECTED_SET_SOLO:
                if (isButtonPressed)
                    cursorTrack.setSolo (value.isPositive ());
                break;

            // Track 1-8: Toggle Arm
            case TRACK_1_TOGGLE_ARM, TRACK_2_TOGGLE_ARM, TRACK_3_TOGGLE_ARM, TRACK_4_TOGGLE_ARM, TRACK_5_TOGGLE_ARM, TRACK_6_TOGGLE_ARM, TRACK_7_TOGGLE_ARM, TRACK_8_TOGGLE_ARM:
                if (isButtonPressed)
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_ARM.ordinal ()).toggleRecArm ();
                break;
            // Track 1-8: Set Arm
            case TRACK_1_SET_ARM, TRACK_2_SET_ARM, TRACK_3_SET_ARM, TRACK_4_SET_ARM, TRACK_5_SET_ARM, TRACK_6_SET_ARM, TRACK_7_SET_ARM, TRACK_8_SET_ARM:
                if (isButtonPressed)
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_ARM.ordinal ()).setRecArm (value.isPositive ());
                break;
            // Track Selected: Toggle Arm
            case TRACK_SELECTED_TOGGLE_ARM:
                if (isButtonPressed)
                    cursorTrack.toggleRecArm ();
                break;
            // Track Selected: Set Arm
            case TRACK_SELECTED_SET_ARM:
                if (isButtonPressed)
                    cursorTrack.setRecArm (value.isPositive ());
                break;

            // Track 1-8: Toggle Monitor
            case TRACK_1_TOGGLE_MONITOR, TRACK_2_TOGGLE_MONITOR, TRACK_3_TOGGLE_MONITOR, TRACK_4_TOGGLE_MONITOR, TRACK_5_TOGGLE_MONITOR, TRACK_6_TOGGLE_MONITOR, TRACK_7_TOGGLE_MONITOR, TRACK_8_TOGGLE_MONITOR:
                if (isButtonPressed)
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_MONITOR.ordinal ()).toggleMonitor ();
                break;
            // Track 1-8: Set Monitor
            case TRACK_1_SET_MONITOR, TRACK_2_SET_MONITOR, TRACK_3_SET_MONITOR, TRACK_4_SET_MONITOR, TRACK_5_SET_MONITOR, TRACK_6_SET_MONITOR, TRACK_7_SET_MONITOR, TRACK_8_SET_MONITOR:
                if (isButtonPressed)
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_MONITOR.ordinal ()).setMonitor (value.isPositive ());
                break;
            // Track Selected: Toggle Monitor
            case TRACK_SELECTED_TOGGLE_MONITOR:
                if (isButtonPressed)
                    cursorTrack.toggleMonitor ();
                break;
            // Track Selected: Set Monitor
            case TRACK_SELECTED_SET_MONITOR:
                if (isButtonPressed)
                    cursorTrack.setMonitor (value.isPositive ());
                break;

            // Track 1: Toggle Auto Monitor
            case TRACK_1_TOGGLE_AUTO_MONITOR, TRACK_2_TOGGLE_AUTO_MONITOR, TRACK_3_TOGGLE_AUTO_MONITOR, TRACK_4_TOGGLE_AUTO_MONITOR, TRACK_5_TOGGLE_AUTO_MONITOR, TRACK_6_TOGGLE_AUTO_MONITOR, TRACK_7_TOGGLE_AUTO_MONITOR, TRACK_8_TOGGLE_AUTO_MONITOR:
                if (isButtonPressed)
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_TOGGLE_AUTO_MONITOR.ordinal ()).toggleAutoMonitor ();
                break;
            // Track 1: Set Auto Monitor
            case TRACK_1_SET_AUTO_MONITOR, TRACK_2_SET_AUTO_MONITOR, TRACK_3_SET_AUTO_MONITOR, TRACK_4_SET_AUTO_MONITOR, TRACK_5_SET_AUTO_MONITOR, TRACK_6_SET_AUTO_MONITOR, TRACK_7_SET_AUTO_MONITOR, TRACK_8_SET_AUTO_MONITOR:
                if (isButtonPressed)
                    trackBank.getItem (command.ordinal () - FlexiCommand.TRACK_1_SET_AUTO_MONITOR.ordinal ()).setAutoMonitor (value.isPositive ());
                break;
            // Track Selected: Toggle Auto Monitor
            case TRACK_SELECTED_TOGGLE_AUTO_MONITOR:
                if (isButtonPressed)
                    cursorTrack.toggleAutoMonitor ();
                break;
            // Track Selected: Set Auto Monitor
            case TRACK_SELECTED_SET_AUTO_MONITOR:
                if (isButtonPressed)
                    cursorTrack.setAutoMonitor (value.isPositive ());
                break;

            // Track Selected: Toggle Pinned
            case TRACK_SELECTED_TOGGLE_PIN:
                if (isButtonPressed)
                    cursorTrack.togglePinned ();
                break;
            // Track Selected: Set Pinned
            case TRACK_SELECTED_SET_PIN:
                if (isButtonPressed)
                    cursorTrack.setPinned (value.isPositive ());
                break;

            // Track 1-8: Set Send 1
            case TRACK_1_SET_SEND_1, TRACK_2_SET_SEND_1, TRACK_3_SET_SEND_1, TRACK_4_SET_SEND_1, TRACK_5_SET_SEND_1, TRACK_6_SET_SEND_1, TRACK_7_SET_SEND_1, TRACK_8_SET_SEND_1:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_1.ordinal (), 0, knobMode, value);
                break;
            // Track 1-8: Set Send 2
            case TRACK_1_SET_SEND_2, TRACK_2_SET_SEND_2, TRACK_3_SET_SEND_2, TRACK_4_SET_SEND_2, TRACK_5_SET_SEND_2, TRACK_6_SET_SEND_2, TRACK_7_SET_SEND_2, TRACK_8_SET_SEND_2:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_2.ordinal (), 1, knobMode, value);
                break;
            // Track 1-8: Set Send 3
            case TRACK_1_SET_SEND_3, TRACK_2_SET_SEND_3, TRACK_3_SET_SEND_3, TRACK_4_SET_SEND_3, TRACK_5_SET_SEND_3, TRACK_6_SET_SEND_3, TRACK_7_SET_SEND_3, TRACK_8_SET_SEND_3:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_3.ordinal (), 2, knobMode, value);
                break;
            // Track 1-8: Set Send 4
            case TRACK_1_SET_SEND_4, TRACK_2_SET_SEND_4, TRACK_3_SET_SEND_4, TRACK_4_SET_SEND_4, TRACK_5_SET_SEND_4, TRACK_6_SET_SEND_4, TRACK_7_SET_SEND_4, TRACK_8_SET_SEND_4:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_4.ordinal (), 3, knobMode, value);
                break;
            // Track 1: Set Send 5
            case TRACK_1_SET_SEND_5, TRACK_2_SET_SEND_5, TRACK_3_SET_SEND_5, TRACK_4_SET_SEND_5, TRACK_5_SET_SEND_5, TRACK_6_SET_SEND_5, TRACK_7_SET_SEND_5, TRACK_8_SET_SEND_5:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_5.ordinal (), 4, knobMode, value);
                break;
            // Track 1: Set Send 6
            case TRACK_1_SET_SEND_6, TRACK_2_SET_SEND_6, TRACK_3_SET_SEND_6, TRACK_4_SET_SEND_6, TRACK_5_SET_SEND_6, TRACK_6_SET_SEND_6, TRACK_7_SET_SEND_6, TRACK_8_SET_SEND_6:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_6.ordinal (), 5, knobMode, value);
                break;
            // Track 1-8: Set Send 7
            case TRACK_1_SET_SEND_7, TRACK_2_SET_SEND_7, TRACK_3_SET_SEND_7, TRACK_4_SET_SEND_7, TRACK_5_SET_SEND_7, TRACK_6_SET_SEND_7, TRACK_7_SET_SEND_7, TRACK_8_SET_SEND_7:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_7.ordinal (), 6, knobMode, value);
                break;
            // Track 1-8: Set Send 8
            case TRACK_1_SET_SEND_8, TRACK_2_SET_SEND_8, TRACK_3_SET_SEND_8, TRACK_4_SET_SEND_8, TRACK_5_SET_SEND_8, TRACK_6_SET_SEND_8, TRACK_7_SET_SEND_8, TRACK_8_SET_SEND_8:
                this.changeSendVolume (command.ordinal () - FlexiCommand.TRACK_1_SET_SEND_8.ordinal (), 7, knobMode, value);
                break;
            // Track Selected: Set Send 1-8
            case TRACK_SELECTED_SET_SEND_1, TRACK_SELECTED_SET_SEND_2, TRACK_SELECTED_SET_SEND_3, TRACK_SELECTED_SET_SEND_4, TRACK_SELECTED_SET_SEND_5, TRACK_SELECTED_SET_SEND_6, TRACK_SELECTED_SET_SEND_7, TRACK_SELECTED_SET_SEND_8:
                this.changeSendVolume (-1, command.ordinal () - FlexiCommand.TRACK_SELECTED_SET_SEND_1.ordinal (), knobMode, value);
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private int getSendValue (final int trackIndex, final int sendIndex)
    {
        final Optional<ITrack> track = this.getTrack (trackIndex);
        if (track.isEmpty ())
            return 0;

        final ISendBank sendBank = track.get ().getSendBank ();
        if (sendIndex >= sendBank.getPageSize ())
            return 0;

        final ISend send = sendBank.getItem (sendIndex);
        if (send == null)
            return 0;

        return send.getValue ();
    }


    private Optional<ITrack> getTrack (final int trackIndex)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (tb == null)
            return Optional.empty ();
        if (trackIndex < 0)
            return tb.getSelectedItem ();
        final ITrack item = tb.getItem (trackIndex);
        return item.doesExist () ? Optional.of (item) : Optional.empty ();
    }


    private void changeTrackVolume (final KnobMode knobMode, final int trackIndex, final MidiValue value)
    {
        final Optional<ITrack> track = this.getTrack (trackIndex);
        if (track.isEmpty ())
            return;
        final int val = value.getValue ();
        final IParameter volumeParameter = track.get ().getVolumeParameter ();
        if (isAbsolute (knobMode))
            volumeParameter.setValue (this.getAbsoluteValueChanger (value), val);
        else
            volumeParameter.changeValue (this.getRelativeValueChanger (knobMode), val);
    }


    private void changeTrackPanorama (final KnobMode knobMode, final int trackIndex, final MidiValue value)
    {
        final Optional<ITrack> track = this.getTrack (trackIndex);
        if (track.isEmpty ())
            return;
        final int val = value.getValue ();
        final IParameter panParameter = track.get ().getPanParameter ();
        if (isAbsolute (knobMode))
            panParameter.setValue (this.getAbsoluteValueChanger (value), val);
        else
            panParameter.changeValue (this.getRelativeValueChanger (knobMode), val);
    }


    private void changeSendVolume (final int trackIndex, final int sendIndex, final KnobMode knobMode, final MidiValue value)
    {
        final Optional<ITrack> track = this.getTrack (trackIndex);
        if (track.isEmpty ())
            return;

        final ISendBank sendBank = track.get ().getSendBank ();
        if (sendIndex >= sendBank.getPageSize ())
            return;

        final ISend send = sendBank.getItem (sendIndex);
        if (send == null)
            return;

        final int val = value.getValue ();
        if (isAbsolute (knobMode))
            send.setValue (this.getAbsoluteValueChanger (value), val);
        else
            send.changeValue (this.getRelativeValueChanger (knobMode), val);
    }


    private void scrollTrack (final KnobMode knobMode, final MidiValue value)
    {
        if (isAbsolute (knobMode) || !this.increaseKnobMovement ())
            return;

        if (this.isIncrease (knobMode, value))
            this.scrollTrackRight (false);
        else
            this.scrollTrackLeft (false);
    }


    private void scrollTrackLeft (final boolean switchBank)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final Optional<ITrack> sel = tb.getSelectedItem ();
        final int index = sel.isEmpty () ? 0 : sel.get ().getIndex () - 1;
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
        final Optional<ITrack> sel = tb.getSelectedItem ();
        final int index = sel.isEmpty () ? 0 : sel.get ().getIndex () + 1;
        if (index == 8 || switchBank)
        {
            tb.selectNextPage ();
            return;
        }
        tb.getItem (index).select ();
    }
}
