// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.application;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command to switch layouts.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayoutCommand<S extends ControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private String layout;


    /**
     * Constructor.
     *
     * @param layout The layout to switch to
     * @param model The model
     * @param surface The surface
     */
    public LayoutCommand (final String layout, final IModel model, final S surface)
    {
        super (model, surface);
        this.layout = layout;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.model.getApplication ().setPanelLayout (this.layout);
    }
}
