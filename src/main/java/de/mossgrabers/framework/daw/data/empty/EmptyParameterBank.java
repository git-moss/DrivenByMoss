// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Default data for an empty parameter bank.
 *
 * @author Jürgen Moßgraber
 */
public class EmptyParameterBank extends EmptyBank<IParameter> implements IParameterBank
{
    /** The singleton. */
    public static final IParameterBank INSTANCE = new EmptyParameterBank ();


    /**
     * Constructor.
     */
    private EmptyParameterBank ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getItem (final int index)
    {
        return EmptyParameter.INSTANCE;
    }


    /** {@inheritDoc} */
    @Override
    public IParameterPageBank getPageBank ()
    {
        return EmptyParameterPageBank.INSTANCE;
    }
}