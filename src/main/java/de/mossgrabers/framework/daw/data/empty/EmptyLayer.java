// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.ILayer;

import java.util.HashMap;
import java.util.Map;


/**
 * Default data for an empty layer.
 *
 * @author Jürgen Moßgraber
 */
public class EmptyLayer extends EmptyChannel implements ILayer
{
    private static final Map<Integer, EmptyLayer> INSTANCES = new HashMap<> ();


    /**
     * Get an instance of an EmptyLayer for the given page size. Instances are cached.
     *
     * @param pageSize The page size for which to get an empty bank
     * @return The bank
     */
    public static EmptyLayer getInstance (final int pageSize)
    {
        return INSTANCES.computeIfAbsent (Integer.valueOf (pageSize), EmptyLayer::new);
    }


    /**
     * Constructor.
     *
     * @param sendPageSize The size of the sends pages
     */
    public EmptyLayer (final int sendPageSize)
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
