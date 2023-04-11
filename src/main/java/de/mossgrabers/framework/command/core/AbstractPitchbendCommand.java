// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.core;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;


/**
 * Abstract base class for aftertouch commands.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractPitchbendCommand<S extends IControlSurface<C>, C extends Configuration> implements PitchbendCommand
{
    protected final IModel model;
    protected final S      surface;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    protected AbstractPitchbendCommand (final IModel model, final S surface)
    {
        this.model = model;
        this.surface = surface;
    }


    /** {@inheritDoc} */
    @Override
    public void updateValue ()
    {
        // Intentionally empty
    }
}
