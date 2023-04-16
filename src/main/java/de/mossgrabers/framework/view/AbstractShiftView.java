// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * Abstract implementation of a shift view.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractShiftView<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C>
{
    private boolean wasUsed = false;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     */
    protected AbstractShiftView (final String name, final S surface, final IModel model)
    {
        super (name, surface, model);
    }


    /**
     * Returns true if a function of the view was used.
     *
     * @return True if it was used
     */
    public boolean wasUsed ()
    {
        return this.wasUsed;
    }


    /**
     * Set that a function of the view was used.
     */
    public void setWasUsed ()
    {
        this.wasUsed = true;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.wasUsed = false;
    }
}
