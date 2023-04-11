// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.bank.ISendBank;


/**
 * Default data for an empty send bank.
 *
 * @author Jürgen Moßgraber
 */
public class EmptySendBank extends EmptyBank<ISend> implements ISendBank
{
    /** The singleton. */
    public static final ISendBank INSTANCE = new EmptySendBank ();


    /**
     * Constructor.
     */
    private EmptySendBank ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public ISend getItem (final int index)
    {
        return EmptySend.INSTANCE;
    }
}