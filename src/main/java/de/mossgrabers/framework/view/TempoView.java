// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;


/**
 * Displays the current tempo of the DAW as a 3 digit number on the grid.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class TempoView<S extends IControlSurface<C>, C extends Configuration> extends AbstractNumberDisplayView<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param textColor1 The color of the 1st and 3rd digit
     * @param textColor2 The color of 2nd digit
     * @param backgroundColor The background color
     */
    public TempoView (final S surface, final IModel model, final int textColor1, final int textColor2, final int backgroundColor)
    {
        super ("Tempo", surface, model, textColor1, textColor2, backgroundColor);
    }


    /** {@inheritDoc}} */
    @Override
    protected int getNumber ()
    {
        return (int) Math.round (this.model.getTransport ().getTempo ());
    }
}
