// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * Dummy view for controllers without any keys or pads.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ControlOnlyView<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ControlOnlyView (final S surface, final IModel model)
    {
        super ("Control", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Intentionally empty
    }
}