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
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * The handler for user commands.
 *
 * @author Jürgen Moßgraber
 */
public class UserHandler extends AbstractHandler
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
    public UserHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);
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
            FlexiCommand.USER_SELECT_NEXT_PAGE,
            FlexiCommand.USER_TOGGLE_PARAMETER_1,
            FlexiCommand.USER_TOGGLE_PARAMETER_2,
            FlexiCommand.USER_TOGGLE_PARAMETER_3,
            FlexiCommand.USER_TOGGLE_PARAMETER_4,
            FlexiCommand.USER_TOGGLE_PARAMETER_5,
            FlexiCommand.USER_TOGGLE_PARAMETER_6,
            FlexiCommand.USER_TOGGLE_PARAMETER_7,
            FlexiCommand.USER_TOGGLE_PARAMETER_8,
            FlexiCommand.USER_RESET_PARAMETER_1,
            FlexiCommand.USER_RESET_PARAMETER_2,
            FlexiCommand.USER_RESET_PARAMETER_3,
            FlexiCommand.USER_RESET_PARAMETER_4,
            FlexiCommand.USER_RESET_PARAMETER_5,
            FlexiCommand.USER_RESET_PARAMETER_6,
            FlexiCommand.USER_RESET_PARAMETER_7,
            FlexiCommand.USER_RESET_PARAMETER_8
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final IParameterBank userParameterBank = this.model.getProject ().getParameterBank ();
        if (userParameterBank == null)
            return -1;

        switch (command)
        {
            case USER_SET_PARAMETER_1, USER_SET_PARAMETER_2, USER_SET_PARAMETER_3, USER_SET_PARAMETER_4, USER_SET_PARAMETER_5, USER_SET_PARAMETER_6, USER_SET_PARAMETER_7, USER_SET_PARAMETER_8:
                return userParameterBank.getItem (command.ordinal () - FlexiCommand.USER_SET_PARAMETER_1.ordinal ()).getValue ();

            case USER_TOGGLE_PARAMETER_1, USER_TOGGLE_PARAMETER_2, USER_TOGGLE_PARAMETER_3, USER_TOGGLE_PARAMETER_4, USER_TOGGLE_PARAMETER_5, USER_TOGGLE_PARAMETER_6, USER_TOGGLE_PARAMETER_7, USER_TOGGLE_PARAMETER_8:
                final int value = userParameterBank.getItem (command.ordinal () - FlexiCommand.USER_TOGGLE_PARAMETER_1.ordinal ()).getValue ();
                return toMidiValue (value > 0);

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final IParameterBank userParameterBank = this.model.getProject ().getParameterBank ();
        if (userParameterBank == null)
            return;

        switch (command)
        {
            case USER_SET_PARAMETER_1, USER_SET_PARAMETER_2, USER_SET_PARAMETER_3, USER_SET_PARAMETER_4, USER_SET_PARAMETER_5, USER_SET_PARAMETER_6, USER_SET_PARAMETER_7, USER_SET_PARAMETER_8:
                final IParameter userParam = userParameterBank.getItem (command.ordinal () - FlexiCommand.USER_SET_PARAMETER_1.ordinal ());
                final int val = value.getValue ();
                if (isAbsolute (knobMode))
                    userParam.setValue (this.getAbsoluteValueChanger (value), val);
                else
                    userParam.changeValue (this.getRelativeValueChanger (knobMode), val);
                break;

            case USER_RESET_PARAMETER_1, USER_RESET_PARAMETER_2, USER_RESET_PARAMETER_3, USER_RESET_PARAMETER_4, USER_RESET_PARAMETER_5, USER_RESET_PARAMETER_6, USER_RESET_PARAMETER_7, USER_RESET_PARAMETER_8:
                if (this.isButtonPressed (knobMode, value))
                    userParameterBank.getItem (command.ordinal () - FlexiCommand.USER_RESET_PARAMETER_1.ordinal ()).resetValue ();
                break;

            case USER_TOGGLE_PARAMETER_1, USER_TOGGLE_PARAMETER_2, USER_TOGGLE_PARAMETER_3, USER_TOGGLE_PARAMETER_4, USER_TOGGLE_PARAMETER_5, USER_TOGGLE_PARAMETER_6, USER_TOGGLE_PARAMETER_7, USER_TOGGLE_PARAMETER_8:
                final IParameter userToggleParam = userParameterBank.getItem (command.ordinal () - FlexiCommand.USER_TOGGLE_PARAMETER_1.ordinal ());
                if (this.isButtonPressed (knobMode, value))
                {
                    final int v = userToggleParam.getValue ();
                    userToggleParam.setValue (v > 0 ? 0 : this.model.getValueChanger ().getUpperBound () - 1);
                }
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
