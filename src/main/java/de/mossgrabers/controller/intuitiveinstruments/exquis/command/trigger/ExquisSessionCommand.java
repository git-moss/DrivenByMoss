// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.command.trigger;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command for selecting (temporarily) the session mode.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisSessionCommand extends AbstractTriggerCommand<ExquisControlSurface, ExquisConfiguration>
{
    private boolean isTemporary;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ExquisSessionCommand (final IModel model, final ExquisControlSurface surface)
    {
        super (model, surface);
    }


    /**
     * Activate temporary display of session view.
     */
    public void setTemporary ()
    {
        this.isTemporary = true;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        if (event == ButtonEvent.DOWN)
        {
            this.isTemporary = false;

            if (viewManager.isActive (Views.SESSION))
            {
                viewManager.setActive (Views.PLAY);
                return;
            }

            viewManager.setActive (Views.SESSION);
            return;
        }

        if (event == ButtonEvent.UP && this.isTemporary)
            viewManager.restore ();
    }
}
