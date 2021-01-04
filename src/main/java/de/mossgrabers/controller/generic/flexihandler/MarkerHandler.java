// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IMarkerBank;


/**
 * The handler for marker commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MarkerHandler extends AbstractHandler
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param relative2ValueChanger The relative value changer variant 2
     * @param relative3ValueChanger The relative value changer variant 3
     */
    public MarkerHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger relative2ValueChanger, final IValueChanger relative3ValueChanger)
    {
        super (model, surface, configuration, relative2ValueChanger, relative3ValueChanger);
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
    public void handle (final FlexiCommand command, final int knobMode, final int value)
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
