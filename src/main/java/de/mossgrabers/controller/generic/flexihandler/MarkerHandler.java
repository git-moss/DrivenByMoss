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
import de.mossgrabers.framework.daw.data.bank.IMarkerBank;


/**
 * The handler for marker commands.
 *
 * @author Jürgen Moßgraber
 */
public class MarkerHandler extends AbstractHandler
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
    public MarkerHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.MARKER_1_LAUNCH_MARKER,
            FlexiCommand.MARKER_2_LAUNCH_MARKER,
            FlexiCommand.MARKER_3_LAUNCH_MARKER,
            FlexiCommand.MARKER_4_LAUNCH_MARKER,
            FlexiCommand.MARKER_5_LAUNCH_MARKER,
            FlexiCommand.MARKER_6_LAUNCH_MARKER,
            FlexiCommand.MARKER_7_LAUNCH_MARKER,
            FlexiCommand.MARKER_8_LAUNCH_MARKER,
            FlexiCommand.MARKER_SELECT_PREVIOUS_BANK,
            FlexiCommand.MARKER_SELECT_NEXT_BANK
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        if (!this.isButtonPressed (knobMode, value))
            return;

        final IMarkerBank markerBank = this.model.getMarkerBank ();

        switch (command)
        {
            case MARKER_1_LAUNCH_MARKER:
            case MARKER_2_LAUNCH_MARKER:
            case MARKER_3_LAUNCH_MARKER:
            case MARKER_4_LAUNCH_MARKER:
            case MARKER_5_LAUNCH_MARKER:
            case MARKER_6_LAUNCH_MARKER:
            case MARKER_7_LAUNCH_MARKER:
            case MARKER_8_LAUNCH_MARKER:
                final int index = command.ordinal () - FlexiCommand.MARKER_1_LAUNCH_MARKER.ordinal ();
                markerBank.getItem (index).launch (true);
                break;

            case MARKER_SELECT_PREVIOUS_BANK:
                markerBank.selectPreviousPage ();
                break;

            case MARKER_SELECT_NEXT_BANK:
                markerBank.selectNextPage ();
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }
}
