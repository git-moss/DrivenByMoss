// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.device;

import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the Add effect.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AddEffectCommand<S extends IControlSurface<C>, C extends Configuration> extends BrowserCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public AddEffectCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param firstTrigger If this button is pressed when the command is executed a new device is
     *            inserted before the current one
     * @param secondTrigger If this button is pressed when the command is executed a new device is
     *            inserted after the current one
     */
    public AddEffectCommand (final IModel model, final S surface, final ButtonID firstTrigger, final ButtonID secondTrigger)
    {
        super (model, surface, firstTrigger, secondTrigger);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.startBrowser (true, false);
    }
}
