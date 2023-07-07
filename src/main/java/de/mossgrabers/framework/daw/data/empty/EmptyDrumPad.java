// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.IDrumPad;

import java.util.HashMap;
import java.util.Map;


/**
 * Default data for an empty drum pad.
 *
 * @author Jürgen Moßgraber
 */
public class EmptyDrumPad extends EmptyChannel implements IDrumPad
{
    private static final Map<Integer, EmptyDrumPad> INSTANCES = new HashMap<> ();


    /**
     * Get an instance of an EmptyDrumPad for the given page size. Instances are cached.
     *
     * @param pageSize The page size for which to get an empty bank
     * @return The bank
     */
    public static EmptyDrumPad getInstance (final int pageSize)
    {
        return INSTANCES.computeIfAbsent (Integer.valueOf (pageSize), EmptyDrumPad::new);
    }


    /**
     * Constructor.
     *
     * @param sendPageSize The size of the sends pages
     */
    public EmptyDrumPad (final int sendPageSize)
    {
        super (sendPageSize);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDevices ()
    {
        return false;
    }
}
