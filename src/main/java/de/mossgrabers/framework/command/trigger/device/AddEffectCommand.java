// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.device;

import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
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
     * @param browserMode The ID of the mode to activate for browsing
     * @param model The model
     * @param surface The surface
     */
    public AddEffectCommand (final Modes browserMode, final IModel model, final S surface)
    {
        super (browserMode, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.startBrowser (true, false);
    }
}
