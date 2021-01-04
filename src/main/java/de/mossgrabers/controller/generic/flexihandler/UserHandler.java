// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;


/**
 * The handler for user commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserHandler extends AbstractHandler
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
    public UserHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger relative2ValueChanger, final IValueChanger relative3ValueChanger)
    {
        super (model, surface, configuration, relative2ValueChanger, relative3ValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.USER_SET_PARAMETER_1,
            FlexiCommand.USER_SET_PARAMETER_2,
            FlexiCommand.USER_SET_PARAMETER_3,
            FlexiCommand.USER_SET_PARAMETER_4,
            FlexiCommand.USER_SET_PARAMETER_5,
            FlexiCommand.USER_SET_PARAMETER_6,
            FlexiCommand.USER_SET_PARAMETER_7,
            FlexiCommand.USER_SET_PARAMETER_8,
            FlexiCommand.USER_SELECT_PREVIOUS_PAGE,
            FlexiCommand.USER_SELECT_NEXT_PAGE
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final IParameterBank userParameterBank = this.model.getUserParameterBank ();
        if (userParameterBank == null)
            return -1;

        switch (command)
        {
            case USER_SET_PARAMETER_1:
            case USER_SET_PARAMETER_2:
            case USER_SET_PARAMETER_3:
            case USER_SET_PARAMETER_4:
            case USER_SET_PARAMETER_5:
            case USER_SET_PARAMETER_6:
            case USER_SET_PARAMETER_7:
            case USER_SET_PARAMETER_8:
                return userParameterBank.getItem (command.ordinal () - FlexiCommand.USER_SET_PARAMETER_1.ordinal ()).getValue ();

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final int knobMode, final int value)
    {
        final IParameterBank userParameterBank = this.model.getUserParameterBank ();
        if (userParameterBank == null)
            return;

        switch (command)
        {
            case USER_SET_PARAMETER_1:
            case USER_SET_PARAMETER_2:
            case USER_SET_PARAMETER_3:
            case USER_SET_PARAMETER_4:
            case USER_SET_PARAMETER_5:
            case USER_SET_PARAMETER_6:
            case USER_SET_PARAMETER_7:
            case USER_SET_PARAMETER_8:
                final int index = command.ordinal () - FlexiCommand.USER_SET_PARAMETER_1.ordinal ();
                final IParameter userParam = userParameterBank.getItem (index);
                if (isAbsolute (knobMode))
                    userParam.setValue (value);
                else
                    userParam.setValue (this.limit (userParam.getValue () + this.getRelativeSpeed (knobMode, value)));
                break;

            case USER_SELECT_PREVIOUS_PAGE:
                userParameterBank.scrollBackwards ();
                this.displaySelectedPage (userParameterBank);
                break;

            case USER_SELECT_NEXT_PAGE:
                userParameterBank.scrollForwards ();
                this.displaySelectedPage (userParameterBank);
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private void displaySelectedPage (final IParameterBank userParameterBank)
    {
        final int selectedPage = userParameterBank.getScrollPosition () / userParameterBank.getPageSize () + 1;
        this.surface.getDisplay ().notify ("User Page " + selectedPage);
    }
}
