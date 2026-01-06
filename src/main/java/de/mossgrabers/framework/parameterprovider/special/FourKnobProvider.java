// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.special;

import java.util.Optional;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;


/**
 * Provide access to 8 parameters. If the modifier button is pressed the parameters 5-8 are provided
 * otherwise 1-4.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class FourKnobProvider<S extends IControlSurface<C>, C extends Configuration> extends AbstractWrapperProvider
{
    private final S        surface;
    private final ButtonID modifier;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param parameterProvider The provider to wrap
     * @param modifier The modifier button to trigger the 2nd parameter layer
     */
    public FourKnobProvider (final S surface, final IParameterProvider parameterProvider, final ButtonID modifier)
    {
        super (parameterProvider);

        this.surface = surface;
        this.modifier = modifier;
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
        return this.parameterProvider.get (this.getIndex (index));
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ColorEx> getColor (final int index)
    {
        return this.parameterProvider.getColor (this.getIndex (index));
    }


    /**
     * Get the index depending on the state of the modifier button.
     *
     * @param index The index
     * @return The index or the offset index
     */
    protected int getIndex (final int index)
    {
        return this.surface.isPressed (this.modifier) ? this.size () + index : index;
    }
}
