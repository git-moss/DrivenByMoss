// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.ILayer;


/**
 * Default data for an empty layer.
 *
 * @author Jürgen Moßgraber
 */
public class EmptyLayer extends EmptyChannel implements ILayer
{
    /** The singleton. */
    public static final ILayer INSTANCE = new EmptyLayer ();


    /**
     * Constructor.
     */
    private EmptyLayer ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDevices ()
    {
        return false;
    }
}
