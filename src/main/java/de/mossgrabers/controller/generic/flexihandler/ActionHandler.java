// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;


/**
 * The handler for action commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ActionHandler extends AbstractHandler
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
    public ActionHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger relative2ValueChanger, final IValueChanger relative3ValueChanger)
    {
        super (model, surface, configuration, relative2ValueChanger, relative3ValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.ACTION_1,
            FlexiCommand.ACTION_2,
            FlexiCommand.ACTION_3,
            FlexiCommand.ACTION_4,
            FlexiCommand.ACTION_5,
            FlexiCommand.ACTION_6,
            FlexiCommand.ACTION_7,
            FlexiCommand.ACTION_8
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

        final String assignableActionID = this.configuration.getAssignableAction (command.ordinal () - FlexiCommand.ACTION_1.ordinal ());
        if (assignableActionID != null)
            this.model.getApplication ().invokeAction (assignableActionID);
    }
}
