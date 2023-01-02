// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.IDrumPad;


/**
 * Default data for an empty drum pad.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptyDrumPad extends EmptyChannel implements IDrumPad
{
    /** The singleton. */
    public static final IDrumPad INSTANCE = new EmptyDrumPad ();


    /**
     * Constructor.
     */
    private EmptyDrumPad ()
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
