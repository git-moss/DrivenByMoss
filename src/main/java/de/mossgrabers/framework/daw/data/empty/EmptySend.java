// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.ISend;


/**
 * Default data for an empty send.
 *
 * @author Jürgen Moßgraber
 */
public class EmptySend extends EmptyParameter implements ISend
{
    /** The singleton. */
    @SuppressWarnings("hiding")
    public static final ISend INSTANCE = new EmptySend ();


    /**
     * Constructor.
     */
    private EmptySend ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
    {
        return ColorEx.BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEnabled ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleEnabled ()
    {
        // Intentionally empty
    }
}
