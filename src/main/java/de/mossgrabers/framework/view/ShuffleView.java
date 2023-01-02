// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Displays the current shuffle value of the DAW as a 3 digit number on the grid.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShuffleView<S extends IControlSurface<C>, C extends Configuration> extends AbstractNumberDisplayView<S, C>
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
    public ShuffleView (final S surface, final IModel model, final int textColor1, final int textColor2, final int backgroundColor)
    {
        super ("Shuffle", surface, model, textColor1, textColor2, backgroundColor);
    }


    /** {@inheritDoc}} */
    @Override
    protected int getNumber ()
    {
        final int max = this.model.getValueChanger ().getUpperBound () - 1;
        final IParameter parameter = this.model.getGroove ().getParameter (GrooveParameterID.SHUFFLE_AMOUNT);
        return parameter == null ? 0 : (int) (parameter.getValue () * 100.0 / max);
    }
}
