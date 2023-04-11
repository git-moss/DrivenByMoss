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
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.Optional;


/**
 * The handler for effect track commands.
 *
 * @author Jürgen Moßgraber
 */
public class FxTrackHandler extends AbstractHandler
{
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
    public FxTrackHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.FX_TRACK_SELECT_PREVIOUS_BANK_PAGE,
            FlexiCommand.FX_TRACK_SELECT_NEXT_BANK_PAGE,
            FlexiCommand.FX_TRACK_SELECT_PREVIOUS_TRACK,
            FlexiCommand.FX_TRACK_SELECT_NEXT_TRACK,
            FlexiCommand.FX_TRACK_SCROLL_TRACKS,
            FlexiCommand.FX_TRACK_1_SELECT,
            FlexiCommand.FX_TRACK_2_SELECT,
            FlexiCommand.FX_TRACK_3_SELECT,
            FlexiCommand.FX_TRACK_4_SELECT,
            FlexiCommand.FX_TRACK_5_SELECT,
            FlexiCommand.FX_TRACK_6_SELECT,
            FlexiCommand.FX_TRACK_7_SELECT,
            FlexiCommand.FX_TRACK_8_SELECT,
            FlexiCommand.FX_TRACK_1_TOGGLE_ACTIVE,
            FlexiCommand.FX_TRACK_2_TOGGLE_ACTIVE,
            FlexiCommand.FX_TRACK_3_TOGGLE_ACTIVE,
            FlexiCommand.FX_TRACK_4_TOGGLE_ACTIVE,
            FlexiCommand.FX_TRACK_5_TOGGLE_ACTIVE,
            FlexiCommand.FX_TRACK_6_TOGGLE_ACTIVE,
            FlexiCommand.FX_TRACK_7_TOGGLE_ACTIVE,
            FlexiCommand.FX_TRACK_8_TOGGLE_ACTIVE,
            FlexiCommand.FX_TRACK_1_SET_ACTIVE,
            FlexiCommand.FX_TRACK_2_SET_ACTIVE,
            FlexiCommand.FX_TRACK_3_SET_ACTIVE,
            FlexiCommand.FX_TRACK_4_SET_ACTIVE,
            FlexiCommand.FX_TRACK_5_SET_ACTIVE,
            FlexiCommand.FX_TRACK_6_SET_ACTIVE,
            FlexiCommand.FX_TRACK_7_SET_ACTIVE,
            FlexiCommand.FX_TRACK_8_SET_ACTIVE,
            FlexiCommand.FX_TRACK_1_SET_VOLUME,
            FlexiCommand.FX_TRACK_2_SET_VOLUME,
            FlexiCommand.FX_TRACK_3_SET_VOLUME,
            FlexiCommand.FX_TRACK_4_SET_VOLUME,
            FlexiCommand.FX_TRACK_5_SET_VOLUME,
            FlexiCommand.FX_TRACK_6_SET_VOLUME,
            FlexiCommand.FX_TRACK_7_SET_VOLUME,
            FlexiCommand.FX_TRACK_8_SET_VOLUME,
            FlexiCommand.FX_TRACK_1_SET_PANORAMA,
            FlexiCommand.FX_TRACK_2_SET_PANORAMA,
            FlexiCommand.FX_TRACK_3_SET_PANORAMA,
            FlexiCommand.FX_TRACK_4_SET_PANORAMA,
            FlexiCommand.FX_TRACK_5_SET_PANORAMA,
            FlexiCommand.FX_TRACK_6_SET_PANORAMA,
            FlexiCommand.FX_TRACK_7_SET_PANORAMA,
            FlexiCommand.FX_TRACK_8_SET_PANORAMA,
            FlexiCommand.FX_TRACK_1_TOGGLE_MUTE,
            FlexiCommand.FX_TRACK_2_TOGGLE_MUTE,
            FlexiCommand.FX_TRACK_3_TOGGLE_MUTE,
            FlexiCommand.FX_TRACK_4_TOGGLE_MUTE,
            FlexiCommand.FX_TRACK_5_TOGGLE_MUTE,
            FlexiCommand.FX_TRACK_6_TOGGLE_MUTE,
            FlexiCommand.FX_TRACK_7_TOGGLE_MUTE,
            FlexiCommand.FX_TRACK_8_TOGGLE_MUTE,
            FlexiCommand.FX_TRACK_1_SET_MUTE,
            FlexiCommand.FX_TRACK_2_SET_MUTE,
            FlexiCommand.FX_TRACK_3_SET_MUTE,
            FlexiCommand.FX_TRACK_4_SET_MUTE,
            FlexiCommand.FX_TRACK_5_SET_MUTE,
            FlexiCommand.FX_TRACK_6_SET_MUTE,
            FlexiCommand.FX_TRACK_7_SET_MUTE,
            FlexiCommand.FX_TRACK_8_SET_MUTE,
            FlexiCommand.FX_TRACK_1_TOGGLE_SOLO,
            FlexiCommand.FX_TRACK_2_TOGGLE_SOLO,
            FlexiCommand.FX_TRACK_3_TOGGLE_SOLO,
            FlexiCommand.FX_TRACK_4_TOGGLE_SOLO,
            FlexiCommand.FX_TRACK_5_TOGGLE_SOLO,
            FlexiCommand.FX_TRACK_6_TOGGLE_SOLO,
            FlexiCommand.FX_TRACK_7_TOGGLE_SOLO,
            FlexiCommand.FX_TRACK_8_TOGGLE_SOLO,
            FlexiCommand.FX_TRACK_1_SET_SOLO,
            FlexiCommand.FX_TRACK_2_SET_SOLO,
            FlexiCommand.FX_TRACK_3_SET_SOLO,
            FlexiCommand.FX_TRACK_4_SET_SOLO,
            FlexiCommand.FX_TRACK_5_SET_SOLO,
            FlexiCommand.FX_TRACK_6_SET_SOLO,
            FlexiCommand.FX_TRACK_7_SET_SOLO,
            FlexiCommand.FX_TRACK_8_SET_SOLO,
            FlexiCommand.FX_TRACK_1_TOGGLE_ARM,
            FlexiCommand.FX_TRACK_2_TOGGLE_ARM,
            FlexiCommand.FX_TRACK_3_TOGGLE_ARM,
            FlexiCommand.FX_TRACK_4_TOGGLE_ARM,
            FlexiCommand.FX_TRACK_5_TOGGLE_ARM,
            FlexiCommand.FX_TRACK_6_TOGGLE_ARM,
            FlexiCommand.FX_TRACK_7_TOGGLE_ARM,
            FlexiCommand.FX_TRACK_8_TOGGLE_ARM,
            FlexiCommand.FX_TRACK_1_SET_ARM,
            FlexiCommand.FX_TRACK_2_SET_ARM,
            FlexiCommand.FX_TRACK_3_SET_ARM,
            FlexiCommand.FX_TRACK_4_SET_ARM,
            FlexiCommand.FX_TRACK_5_SET_ARM,
            FlexiCommand.FX_TRACK_6_SET_ARM,
            FlexiCommand.FX_TRACK_7_SET_ARM,
            FlexiCommand.FX_TRACK_8_SET_ARM,
            FlexiCommand.FX_TRACK_1_TOGGLE_MONITOR,
            FlexiCommand.FX_TRACK_2_TOGGLE_MONITOR,
            FlexiCommand.FX_TRACK_3_TOGGLE_MONITOR,
            FlexiCommand.FX_TRACK_4_TOGGLE_MONITOR,
            FlexiCommand.FX_TRACK_5_TOGGLE_MONITOR,
            FlexiCommand.FX_TRACK_6_TOGGLE_MONITOR,
            FlexiCommand.FX_TRACK_7_TOGGLE_MONITOR,
            FlexiCommand.FX_TRACK_8_TOGGLE_MONITOR,
            FlexiCommand.FX_TRACK_1_SET_MONITOR,
            FlexiCommand.FX_TRACK_2_SET_MONITOR,
            FlexiCommand.FX_TRACK_3_SET_MONITOR,
            FlexiCommand.FX_TRACK_4_SET_MONITOR,
            FlexiCommand.FX_TRACK_5_SET_MONITOR,
            FlexiCommand.FX_TRACK_6_SET_MONITOR,
            FlexiCommand.FX_TRACK_7_SET_MONITOR,
            FlexiCommand.FX_TRACK_8_SET_MONITOR,
            FlexiCommand.FX_TRACK_1_TOGGLE_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_2_TOGGLE_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_3_TOGGLE_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_4_TOGGLE_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_5_TOGGLE_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_6_TOGGLE_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_7_TOGGLE_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_8_TOGGLE_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_1_SET_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_2_SET_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_3_SET_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_4_SET_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_5_SET_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_6_SET_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_7_SET_AUTO_MONITOR,
            FlexiCommand.FX_TRACK_8_SET_AUTO_MONITOR
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank == null)
            return -1;

        switch (command)
        {
            case FX_TRACK_1_SELECT:
            case FX_TRACK_2_SELECT:
            case FX_TRACK_3_SELECT:
            case FX_TRACK_4_SELECT:
            case FX_TRACK_5_SELECT:
            case FX_TRACK_6_SELECT:
            case FX_TRACK_7_SELECT:
            case FX_TRACK_8_SELECT:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SELECT.ordinal ()).isSelected () ? 127 : 0;

            case FX_TRACK_1_TOGGLE_ACTIVE:
            case FX_TRACK_2_TOGGLE_ACTIVE:
            case FX_TRACK_3_TOGGLE_ACTIVE:
            case FX_TRACK_4_TOGGLE_ACTIVE:
            case FX_TRACK_5_TOGGLE_ACTIVE:
            case FX_TRACK_6_TOGGLE_ACTIVE:
            case FX_TRACK_7_TOGGLE_ACTIVE:
            case FX_TRACK_8_TOGGLE_ACTIVE:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_TOGGLE_ACTIVE.ordinal ()).isActivated () ? 127 : 0;
            case FX_TRACK_1_SET_ACTIVE:
            case FX_TRACK_2_SET_ACTIVE:
            case FX_TRACK_3_SET_ACTIVE:
            case FX_TRACK_4_SET_ACTIVE:
            case FX_TRACK_5_SET_ACTIVE:
            case FX_TRACK_6_SET_ACTIVE:
            case FX_TRACK_7_SET_ACTIVE:
            case FX_TRACK_8_SET_ACTIVE:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_ACTIVE.ordinal ()).isActivated () ? 127 : 0;

            case FX_TRACK_1_SET_VOLUME:
            case FX_TRACK_2_SET_VOLUME:
            case FX_TRACK_3_SET_VOLUME:
            case FX_TRACK_4_SET_VOLUME:
            case FX_TRACK_5_SET_VOLUME:
            case FX_TRACK_6_SET_VOLUME:
            case FX_TRACK_7_SET_VOLUME:
            case FX_TRACK_8_SET_VOLUME:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_VOLUME.ordinal ()).getVolume ();

            case FX_TRACK_1_SET_PANORAMA:
            case FX_TRACK_2_SET_PANORAMA:
            case FX_TRACK_3_SET_PANORAMA:
            case FX_TRACK_4_SET_PANORAMA:
            case FX_TRACK_5_SET_PANORAMA:
            case FX_TRACK_6_SET_PANORAMA:
            case FX_TRACK_7_SET_PANORAMA:
            case FX_TRACK_8_SET_PANORAMA:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_PANORAMA.ordinal ()).getPan ();

            case FX_TRACK_1_TOGGLE_MUTE:
            case FX_TRACK_2_TOGGLE_MUTE:
            case FX_TRACK_3_TOGGLE_MUTE:
            case FX_TRACK_4_TOGGLE_MUTE:
            case FX_TRACK_5_TOGGLE_MUTE:
            case FX_TRACK_6_TOGGLE_MUTE:
            case FX_TRACK_7_TOGGLE_MUTE:
            case FX_TRACK_8_TOGGLE_MUTE:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_TOGGLE_MUTE.ordinal ()).isMute () ? 127 : 0;
            case FX_TRACK_1_SET_MUTE:
            case FX_TRACK_2_SET_MUTE:
            case FX_TRACK_3_SET_MUTE:
            case FX_TRACK_4_SET_MUTE:
            case FX_TRACK_5_SET_MUTE:
            case FX_TRACK_6_SET_MUTE:
            case FX_TRACK_7_SET_MUTE:
            case FX_TRACK_8_SET_MUTE:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_MUTE.ordinal ()).isMute () ? 127 : 0;

            case FX_TRACK_1_TOGGLE_SOLO:
            case FX_TRACK_2_TOGGLE_SOLO:
            case FX_TRACK_3_TOGGLE_SOLO:
            case FX_TRACK_4_TOGGLE_SOLO:
            case FX_TRACK_5_TOGGLE_SOLO:
            case FX_TRACK_6_TOGGLE_SOLO:
            case FX_TRACK_7_TOGGLE_SOLO:
            case FX_TRACK_8_TOGGLE_SOLO:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_TOGGLE_SOLO.ordinal ()).isSolo () ? 127 : 0;
            case FX_TRACK_1_SET_SOLO:
            case FX_TRACK_2_SET_SOLO:
            case FX_TRACK_3_SET_SOLO:
            case FX_TRACK_4_SET_SOLO:
            case FX_TRACK_5_SET_SOLO:
            case FX_TRACK_6_SET_SOLO:
            case FX_TRACK_7_SET_SOLO:
            case FX_TRACK_8_SET_SOLO:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_SOLO.ordinal ()).isSolo () ? 127 : 0;

            case FX_TRACK_1_TOGGLE_ARM:
            case FX_TRACK_2_TOGGLE_ARM:
            case FX_TRACK_3_TOGGLE_ARM:
            case FX_TRACK_4_TOGGLE_ARM:
            case FX_TRACK_5_TOGGLE_ARM:
            case FX_TRACK_6_TOGGLE_ARM:
            case FX_TRACK_7_TOGGLE_ARM:
            case FX_TRACK_8_TOGGLE_ARM:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_TOGGLE_ARM.ordinal ()).isRecArm () ? 127 : 0;
            case FX_TRACK_1_SET_ARM:
            case FX_TRACK_2_SET_ARM:
            case FX_TRACK_3_SET_ARM:
            case FX_TRACK_4_SET_ARM:
            case FX_TRACK_5_SET_ARM:
            case FX_TRACK_6_SET_ARM:
            case FX_TRACK_7_SET_ARM:
            case FX_TRACK_8_SET_ARM:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_ARM.ordinal ()).isRecArm () ? 127 : 0;

            case FX_TRACK_1_TOGGLE_MONITOR:
            case FX_TRACK_2_TOGGLE_MONITOR:
            case FX_TRACK_3_TOGGLE_MONITOR:
            case FX_TRACK_4_TOGGLE_MONITOR:
            case FX_TRACK_5_TOGGLE_MONITOR:
            case FX_TRACK_6_TOGGLE_MONITOR:
            case FX_TRACK_7_TOGGLE_MONITOR:
            case FX_TRACK_8_TOGGLE_MONITOR:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_TOGGLE_MONITOR.ordinal ()).isMonitor () ? 127 : 0;
            case FX_TRACK_1_SET_MONITOR:
            case FX_TRACK_2_SET_MONITOR:
            case FX_TRACK_3_SET_MONITOR:
            case FX_TRACK_4_SET_MONITOR:
            case FX_TRACK_5_SET_MONITOR:
            case FX_TRACK_6_SET_MONITOR:
            case FX_TRACK_7_SET_MONITOR:
            case FX_TRACK_8_SET_MONITOR:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_MONITOR.ordinal ()).isMonitor () ? 127 : 0;

            case FX_TRACK_1_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_2_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_3_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_4_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_5_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_6_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_7_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_8_TOGGLE_AUTO_MONITOR:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_TOGGLE_AUTO_MONITOR.ordinal ()).isAutoMonitor () ? 127 : 0;
            case FX_TRACK_1_SET_AUTO_MONITOR:
            case FX_TRACK_2_SET_AUTO_MONITOR:
            case FX_TRACK_3_SET_AUTO_MONITOR:
            case FX_TRACK_4_SET_AUTO_MONITOR:
            case FX_TRACK_5_SET_AUTO_MONITOR:
            case FX_TRACK_6_SET_AUTO_MONITOR:
            case FX_TRACK_7_SET_AUTO_MONITOR:
            case FX_TRACK_8_SET_AUTO_MONITOR:
                return effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_AUTO_MONITOR.ordinal ()).isAutoMonitor () ? 127 : 0;

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank == null)
            return;

        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);

        switch (command)
        {
            // Track: Select Previous Bank Page
            case FX_TRACK_SELECT_PREVIOUS_BANK_PAGE:
                if (isButtonPressed)
                    this.scrollTrackLeft (true);
                break;
            // Track: Select Next Bank Page
            case FX_TRACK_SELECT_NEXT_BANK_PAGE:
                if (isButtonPressed)
                    this.scrollTrackRight (true);
                break;
            // Track: Select Previous Track
            case FX_TRACK_SELECT_PREVIOUS_TRACK:
                if (isButtonPressed)
                    this.scrollTrackLeft (false);
                break;
            // Track: Select Next Track
            case FX_TRACK_SELECT_NEXT_TRACK:
                if (isButtonPressed)
                    this.scrollTrackRight (false);
                break;

            case FX_TRACK_SCROLL_TRACKS:
                this.scrollTrack (knobMode, value);
                break;

            // Track 1-8: Select
            case FX_TRACK_1_SELECT:
            case FX_TRACK_2_SELECT:
            case FX_TRACK_3_SELECT:
            case FX_TRACK_4_SELECT:
            case FX_TRACK_5_SELECT:
            case FX_TRACK_6_SELECT:
            case FX_TRACK_7_SELECT:
            case FX_TRACK_8_SELECT:
                if (isButtonPressed)
                {
                    final ITrack track = effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SELECT.ordinal ());
                    track.select ();
                    this.mvHelper.notifySelectedTrack ();
                }
                break;

            // Track 1-8: Toggle Active
            case FX_TRACK_1_TOGGLE_ACTIVE:
            case FX_TRACK_2_TOGGLE_ACTIVE:
            case FX_TRACK_3_TOGGLE_ACTIVE:
            case FX_TRACK_4_TOGGLE_ACTIVE:
            case FX_TRACK_5_TOGGLE_ACTIVE:
            case FX_TRACK_6_TOGGLE_ACTIVE:
            case FX_TRACK_7_TOGGLE_ACTIVE:
            case FX_TRACK_8_TOGGLE_ACTIVE:
                if (isButtonPressed)
                    effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_TOGGLE_ACTIVE.ordinal ()).toggleIsActivated ();
                break;
            // Track 1-8: Set Active
            case FX_TRACK_1_SET_ACTIVE:
            case FX_TRACK_2_SET_ACTIVE:
            case FX_TRACK_3_SET_ACTIVE:
            case FX_TRACK_4_SET_ACTIVE:
            case FX_TRACK_5_SET_ACTIVE:
            case FX_TRACK_6_SET_ACTIVE:
            case FX_TRACK_7_SET_ACTIVE:
            case FX_TRACK_8_SET_ACTIVE:
                if (isButtonPressed)
                    effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_ACTIVE.ordinal ()).setIsActivated (value.isPositive ());
                break;

            // Track 1-8: Set Volume
            case FX_TRACK_1_SET_VOLUME:
            case FX_TRACK_2_SET_VOLUME:
            case FX_TRACK_3_SET_VOLUME:
            case FX_TRACK_4_SET_VOLUME:
            case FX_TRACK_5_SET_VOLUME:
            case FX_TRACK_6_SET_VOLUME:
            case FX_TRACK_7_SET_VOLUME:
            case FX_TRACK_8_SET_VOLUME:
                this.changeTrackVolume (knobMode, command.ordinal () - FlexiCommand.FX_TRACK_1_SET_VOLUME.ordinal (), value);
                break;

            // Track 1-8: Set Panorama
            case FX_TRACK_1_SET_PANORAMA:
            case FX_TRACK_2_SET_PANORAMA:
            case FX_TRACK_3_SET_PANORAMA:
            case FX_TRACK_4_SET_PANORAMA:
            case FX_TRACK_5_SET_PANORAMA:
            case FX_TRACK_6_SET_PANORAMA:
            case FX_TRACK_7_SET_PANORAMA:
            case FX_TRACK_8_SET_PANORAMA:
                this.changeTrackPanorama (knobMode, command.ordinal () - FlexiCommand.FX_TRACK_1_SET_PANORAMA.ordinal (), value);
                break;

            // Track 1-8: Toggle Mute
            case FX_TRACK_1_TOGGLE_MUTE:
            case FX_TRACK_2_TOGGLE_MUTE:
            case FX_TRACK_3_TOGGLE_MUTE:
            case FX_TRACK_4_TOGGLE_MUTE:
            case FX_TRACK_5_TOGGLE_MUTE:
            case FX_TRACK_6_TOGGLE_MUTE:
            case FX_TRACK_7_TOGGLE_MUTE:
            case FX_TRACK_8_TOGGLE_MUTE:
                if (isButtonPressed)
                    effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_TOGGLE_MUTE.ordinal ()).toggleMute ();
                break;
            // Track 1-8: Set Mute
            case FX_TRACK_1_SET_MUTE:
            case FX_TRACK_2_SET_MUTE:
            case FX_TRACK_3_SET_MUTE:
            case FX_TRACK_4_SET_MUTE:
            case FX_TRACK_5_SET_MUTE:
            case FX_TRACK_6_SET_MUTE:
            case FX_TRACK_7_SET_MUTE:
            case FX_TRACK_8_SET_MUTE:
                if (isButtonPressed)
                    effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_MUTE.ordinal ()).setMute (value.isPositive ());
                break;

            // Track 1-8: Toggle Solo
            case FX_TRACK_1_TOGGLE_SOLO:
            case FX_TRACK_2_TOGGLE_SOLO:
            case FX_TRACK_3_TOGGLE_SOLO:
            case FX_TRACK_4_TOGGLE_SOLO:
            case FX_TRACK_5_TOGGLE_SOLO:
            case FX_TRACK_6_TOGGLE_SOLO:
            case FX_TRACK_7_TOGGLE_SOLO:
            case FX_TRACK_8_TOGGLE_SOLO:
                if (isButtonPressed)
                    effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_TOGGLE_SOLO.ordinal ()).toggleSolo ();
                break;
            // Track 1-8: Set Solo
            case FX_TRACK_1_SET_SOLO:
            case FX_TRACK_2_SET_SOLO:
            case FX_TRACK_3_SET_SOLO:
            case FX_TRACK_4_SET_SOLO:
            case FX_TRACK_5_SET_SOLO:
            case FX_TRACK_6_SET_SOLO:
            case FX_TRACK_7_SET_SOLO:
            case FX_TRACK_8_SET_SOLO:
                if (isButtonPressed)
                    effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_SOLO.ordinal ()).setSolo (value.isPositive ());
                break;

            // Track 1-8: Toggle Arm
            case FX_TRACK_1_TOGGLE_ARM:
            case FX_TRACK_2_TOGGLE_ARM:
            case FX_TRACK_3_TOGGLE_ARM:
            case FX_TRACK_4_TOGGLE_ARM:
            case FX_TRACK_5_TOGGLE_ARM:
            case FX_TRACK_6_TOGGLE_ARM:
            case FX_TRACK_7_TOGGLE_ARM:
            case FX_TRACK_8_TOGGLE_ARM:
                if (isButtonPressed)
                    effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_TOGGLE_ARM.ordinal ()).toggleRecArm ();
                break;
            // Track 1-8: Set Arm
            case FX_TRACK_1_SET_ARM:
            case FX_TRACK_2_SET_ARM:
            case FX_TRACK_3_SET_ARM:
            case FX_TRACK_4_SET_ARM:
            case FX_TRACK_5_SET_ARM:
            case FX_TRACK_6_SET_ARM:
            case FX_TRACK_7_SET_ARM:
            case FX_TRACK_8_SET_ARM:
                if (isButtonPressed)
                    effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_ARM.ordinal ()).setRecArm (value.isPositive ());
                break;

            // Track 1-8: Toggle Monitor
            case FX_TRACK_1_TOGGLE_MONITOR:
            case FX_TRACK_2_TOGGLE_MONITOR:
            case FX_TRACK_3_TOGGLE_MONITOR:
            case FX_TRACK_4_TOGGLE_MONITOR:
            case FX_TRACK_5_TOGGLE_MONITOR:
            case FX_TRACK_6_TOGGLE_MONITOR:
            case FX_TRACK_7_TOGGLE_MONITOR:
            case FX_TRACK_8_TOGGLE_MONITOR:
                if (isButtonPressed)
                    effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_TOGGLE_MONITOR.ordinal ()).toggleMonitor ();
                break;
            // Track 1-8: Set Monitor
            case FX_TRACK_1_SET_MONITOR:
            case FX_TRACK_2_SET_MONITOR:
            case FX_TRACK_3_SET_MONITOR:
            case FX_TRACK_4_SET_MONITOR:
            case FX_TRACK_5_SET_MONITOR:
            case FX_TRACK_6_SET_MONITOR:
            case FX_TRACK_7_SET_MONITOR:
            case FX_TRACK_8_SET_MONITOR:
                if (isButtonPressed)
                    effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_MONITOR.ordinal ()).setMonitor (value.isPositive ());
                break;

            // Track 1: Toggle Auto Monitor
            case FX_TRACK_1_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_2_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_3_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_4_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_5_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_6_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_7_TOGGLE_AUTO_MONITOR:
            case FX_TRACK_8_TOGGLE_AUTO_MONITOR:
                if (isButtonPressed)
                    effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_TOGGLE_AUTO_MONITOR.ordinal ()).toggleAutoMonitor ();
                break;
            // Track 1: Set Auto Monitor
            case FX_TRACK_1_SET_AUTO_MONITOR:
            case FX_TRACK_2_SET_AUTO_MONITOR:
            case FX_TRACK_3_SET_AUTO_MONITOR:
            case FX_TRACK_4_SET_AUTO_MONITOR:
            case FX_TRACK_5_SET_AUTO_MONITOR:
            case FX_TRACK_6_SET_AUTO_MONITOR:
            case FX_TRACK_7_SET_AUTO_MONITOR:
            case FX_TRACK_8_SET_AUTO_MONITOR:
                if (isButtonPressed)
                    effectTrackBank.getItem (command.ordinal () - FlexiCommand.FX_TRACK_1_SET_AUTO_MONITOR.ordinal ()).setAutoMonitor (value.isPositive ());
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private Optional<ITrack> getTrack (final int trackIndex)
    {
        final ITrackBank tb = this.model.getEffectTrackBank ();
        if (trackIndex < 0)
            return tb.getSelectedItem ();

        final ITrack track = tb.getItem (trackIndex);
        return track.doesExist () ? Optional.of (track) : Optional.empty ();
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
        final IParameter panParameter = track.get ().getPanParameter ();
        final int val = value.getValue ();
        if (isAbsolute (knobMode))
            panParameter.setValue (this.getAbsoluteValueChanger (value), val);
        else
            panParameter.changeValue (this.getRelativeValueChanger (knobMode), val);
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
