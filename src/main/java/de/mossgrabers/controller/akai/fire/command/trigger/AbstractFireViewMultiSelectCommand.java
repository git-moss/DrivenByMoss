// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.trigger;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Selects the next view from a list. Added some Fire specifics.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractFireViewMultiSelectCommand extends ViewMultiSelectCommand<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param viewIds The list with IDs of the views to select
     */
    protected AbstractFireViewMultiSelectCommand (final IModel model, final FireControlSurface surface, final Views... viewIds)
    {
        super (model, surface, true, ButtonEvent.UP, viewIds);
    }
}
