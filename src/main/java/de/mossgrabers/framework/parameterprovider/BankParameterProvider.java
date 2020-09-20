// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.utils.FrameworkException;


/**
 * Get a number of parameters. This implementation provides all parameters from the current page of
 * the bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BankParameterProvider implements IParameterProvider
{
    private IBank<? extends IItem> bank;


    /**
     * Constructor.
     *
     * @param bank The bank from which to get the parameters
     */
    public BankParameterProvider (final IBank<? extends IItem> bank)
    {
        this.bank = bank;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.bank.getPageSize ();
    }


    /**
     * Throws a FrameworkException if the bank does not contain parameters.
     *
     * {@inheritDoc}
     */
    @Override
    public IParameter get (final int index)
    {
        final IItem item = this.bank.getItem (index);
        if (!(item instanceof IParameter))
            throw new FrameworkException ("Items in bank are not implementing IParameter!");
        return (IParameter) item;
    }
}
