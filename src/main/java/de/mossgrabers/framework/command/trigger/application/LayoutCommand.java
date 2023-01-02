// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.application;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to switch layouts.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayoutCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final String layout;


    /**
     * Constructor. Flips through all layouts when triggered.
     *
     * @param model The model
     * @param surface The surface
     */
    public LayoutCommand (final IModel model, final S surface)
    {
        this (null, model, surface);
    }


    /**
     * Constructor.
     *
     * @param layout The layout to switch to when triggered
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
        if (event != ButtonEvent.DOWN)
            return;

        final IApplication application = this.model.getApplication ();
        if (this.layout == null)
            application.nextPanelLayout ();
        else
            application.setPanelLayout (this.layout);
    }
}
