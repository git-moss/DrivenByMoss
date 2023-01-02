// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.mode;

import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.AbstractWrapperProvider;

import java.util.Optional;


/**
 * Provide access to 8 parameters. If the ALT button is pressed the parameters 5-8 are provided
 * otherwise 1-4.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Fire4KnobProvider extends AbstractWrapperProvider
{
    private final FireControlSurface surface;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param parameterProvider The provider to wrap
     */
    public Fire4KnobProvider (final FireControlSurface surface, final IParameterProvider parameterProvider)
    {
        super (parameterProvider);

        this.surface = surface;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return 4;
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        return this.parameterProvider.get (this.surface.isPressed (ButtonID.ALT) ? 4 + index : index);
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ColorEx> getColor (final int index)
    {
        return this.parameterProvider.getColor (this.surface.isPressed (ButtonID.ALT) ? 4 + index : index);
    }
}
