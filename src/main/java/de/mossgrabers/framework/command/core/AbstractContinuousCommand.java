// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.core;

import de.mossgrabers.framework.MVHelper;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;


/**
 * Abstract base class for continuous commands.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractContinuousCommand<S extends IControlSurface<C>, C extends Configuration> implements ContinuousCommand
{
    protected final IModel         model;
    protected final S              surface;
    protected final MVHelper<S, C> mvHelper;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    protected AbstractContinuousCommand (final IModel model, final S surface)
    {
        this.model = model;
        this.surface = surface;
        this.mvHelper = new MVHelper<> (model, surface);
    }
}
