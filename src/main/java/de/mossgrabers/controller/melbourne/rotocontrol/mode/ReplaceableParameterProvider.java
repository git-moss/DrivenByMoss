// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol.mode;

import java.util.Arrays;
import java.util.Optional;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.AbstractParameterProvider;


/**
 * Provide access to a fixed number of parameters which can be replaced.
 *
 * @author Jürgen Moßgraber
 */
public class ReplaceableParameterProvider extends AbstractParameterProvider
{
    private final int           numberOfParameters;
    private final IParameter [] parameters;


    /**
     * Constructor.
     *
     * @param numberOfParameters The number of parameters
     */
    public ReplaceableParameterProvider (final int numberOfParameters)
    {
        this.numberOfParameters = numberOfParameters;

        this.parameters = new IParameter [numberOfParameters];
        Arrays.fill (this.parameters, EmptyParameter.INSTANCE);
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.numberOfParameters;
    }


    /**
     * Set a parameter.
     *
     * @param index The index of the parameter
     * @param parameter The parameter
     */
    public void set (final int index, final IParameter parameter)
    {
        this.parameters[index] = parameter;
        this.notifyParametersObservers ();
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        return this.parameters[index];
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ColorEx> getColor (final int index)
    {
        return Optional.empty ();
    }
}
