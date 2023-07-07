// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.HashMap;
import java.util.Map;


/**
 * Default data for an empty parameter bank.
 *
 * @author Jürgen Moßgraber
 */
public class EmptyParameterBank extends EmptyBank<IParameter> implements IParameterBank
{
    private static final Map<Integer, EmptyParameterBank> INSTANCES = new HashMap<> ();


    /**
     * Get an instance of an EmptyParameterBank for the given page size. Instances are cached.
     *
     * @param pageSize The page size for which to get an empty bank
     * @return The bank
     */
    public static EmptyParameterBank getInstance (final int pageSize)
    {
        return INSTANCES.computeIfAbsent (Integer.valueOf (pageSize), EmptyParameterBank::new);
    }


    /**
     * Constructor.
     * 
     * @param pageSize The number of elements in a page of the bank
     */
    private EmptyParameterBank (final int pageSize)
    {
        super (pageSize);
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