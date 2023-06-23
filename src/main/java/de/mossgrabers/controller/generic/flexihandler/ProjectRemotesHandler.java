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
 * The handler for project remote parameter commands.
 *
 * @author Jürgen Moßgraber
 */
public class ProjectRemotesHandler extends AbstractHandler
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
    public ProjectRemotesHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.PROJECT_SET_PARAMETER_1,
            FlexiCommand.PROJECT_SET_PARAMETER_2,
            FlexiCommand.PROJECT_SET_PARAMETER_3,
            FlexiCommand.PROJECT_SET_PARAMETER_4,
            FlexiCommand.PROJECT_SET_PARAMETER_5,
            FlexiCommand.PROJECT_SET_PARAMETER_6,
            FlexiCommand.PROJECT_SET_PARAMETER_7,
            FlexiCommand.PROJECT_SET_PARAMETER_8,
            FlexiCommand.PROJECT_SELECT_PREVIOUS_PAGE,
            FlexiCommand.PROJECT_SELECT_NEXT_PAGE,
            FlexiCommand.PROJECT_TOGGLE_PARAMETER_1,
            FlexiCommand.PROJECT_TOGGLE_PARAMETER_2,
            FlexiCommand.PROJECT_TOGGLE_PARAMETER_3,
            FlexiCommand.PROJECT_TOGGLE_PARAMETER_4,
            FlexiCommand.PROJECT_TOGGLE_PARAMETER_5,
            FlexiCommand.PROJECT_TOGGLE_PARAMETER_6,
            FlexiCommand.PROJECT_TOGGLE_PARAMETER_7,
            FlexiCommand.PROJECT_TOGGLE_PARAMETER_8,
            FlexiCommand.PROJECT_RESET_PARAMETER_1,
            FlexiCommand.PROJECT_RESET_PARAMETER_2,
            FlexiCommand.PROJECT_RESET_PARAMETER_3,
            FlexiCommand.PROJECT_RESET_PARAMETER_4,
            FlexiCommand.PROJECT_RESET_PARAMETER_5,
            FlexiCommand.PROJECT_RESET_PARAMETER_6,
            FlexiCommand.PROJECT_RESET_PARAMETER_7,
            FlexiCommand.PROJECT_RESET_PARAMETER_8
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final IParameterBank projectParameterBank = this.model.getProject ().getParameterBank ();
        if (projectParameterBank == null)
            return -1;

        switch (command)
        {
            case PROJECT_SET_PARAMETER_1, PROJECT_SET_PARAMETER_2, PROJECT_SET_PARAMETER_3, PROJECT_SET_PARAMETER_4, PROJECT_SET_PARAMETER_5, PROJECT_SET_PARAMETER_6, PROJECT_SET_PARAMETER_7, PROJECT_SET_PARAMETER_8:
                return projectParameterBank.getItem (command.ordinal () - FlexiCommand.PROJECT_SET_PARAMETER_1.ordinal ()).getValue ();

            case PROJECT_TOGGLE_PARAMETER_1, PROJECT_TOGGLE_PARAMETER_2, PROJECT_TOGGLE_PARAMETER_3, PROJECT_TOGGLE_PARAMETER_4, PROJECT_TOGGLE_PARAMETER_5, PROJECT_TOGGLE_PARAMETER_6, PROJECT_TOGGLE_PARAMETER_7, PROJECT_TOGGLE_PARAMETER_8:
                final int value = projectParameterBank.getItem (command.ordinal () - FlexiCommand.PROJECT_TOGGLE_PARAMETER_1.ordinal ()).getValue ();
                return toMidiValue (value > 0);

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final IParameterBank projectParameterBank = this.model.getProject ().getParameterBank ();
        if (projectParameterBank == null)
            return;

        switch (command)
        {
            case PROJECT_SET_PARAMETER_1, PROJECT_SET_PARAMETER_2, PROJECT_SET_PARAMETER_3, PROJECT_SET_PARAMETER_4, PROJECT_SET_PARAMETER_5, PROJECT_SET_PARAMETER_6, PROJECT_SET_PARAMETER_7, PROJECT_SET_PARAMETER_8:
                final IParameter projectParam = projectParameterBank.getItem (command.ordinal () - FlexiCommand.PROJECT_SET_PARAMETER_1.ordinal ());
                final int val = value.getValue ();
                if (isAbsolute (knobMode))
                    projectParam.setValue (this.getAbsoluteValueChanger (value), val);
                else
                    projectParam.changeValue (this.getRelativeValueChanger (knobMode), val);
                break;

            case PROJECT_RESET_PARAMETER_1, PROJECT_RESET_PARAMETER_2, PROJECT_RESET_PARAMETER_3, PROJECT_RESET_PARAMETER_4, PROJECT_RESET_PARAMETER_5, PROJECT_RESET_PARAMETER_6, PROJECT_RESET_PARAMETER_7, PROJECT_RESET_PARAMETER_8:
                if (this.isButtonPressed (knobMode, value))
                    projectParameterBank.getItem (command.ordinal () - FlexiCommand.PROJECT_RESET_PARAMETER_1.ordinal ()).resetValue ();
                break;

            case PROJECT_TOGGLE_PARAMETER_1, PROJECT_TOGGLE_PARAMETER_2, PROJECT_TOGGLE_PARAMETER_3, PROJECT_TOGGLE_PARAMETER_4, PROJECT_TOGGLE_PARAMETER_5, PROJECT_TOGGLE_PARAMETER_6, PROJECT_TOGGLE_PARAMETER_7, PROJECT_TOGGLE_PARAMETER_8:
                final IParameter projectToggleParam = projectParameterBank.getItem (command.ordinal () - FlexiCommand.PROJECT_TOGGLE_PARAMETER_1.ordinal ());
                if (this.isButtonPressed (knobMode, value))
                {
                    final int v = projectToggleParam.getValue ();
                    projectToggleParam.setValue (v > 0 ? 0 : this.model.getValueChanger ().getUpperBound () - 1);
                }
                break;

            case PROJECT_SELECT_PREVIOUS_PAGE:
                projectParameterBank.scrollBackwards ();
                this.mvHelper.notifySelectedProjectParameterPage ();
                break;

            case PROJECT_SELECT_NEXT_PAGE:
                projectParameterBank.scrollForwards ();
                this.mvHelper.notifySelectedProjectParameterPage ();
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }
}
