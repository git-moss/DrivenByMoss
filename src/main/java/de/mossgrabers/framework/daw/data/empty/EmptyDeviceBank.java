// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import java.util.HashMap;
import java.util.Map;

import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;


/**
 * Default data for an empty device bank.
 *
 * @author Jürgen Moßgraber
 */
public class EmptyDeviceBank extends EmptyBank<IDevice> implements IDeviceBank
{
    private static final Map<Integer, EmptyDeviceBank> INSTANCES = new HashMap<> ();


    /**
     * Get an instance of an EmptyDeviceBank for the given page size. Instances are cached.
     *
     * @param pageSize The page size for which to get an empty bank
     * @return The bank
     */
    public static EmptyDeviceBank getInstance (final int pageSize)
    {
        return INSTANCES.computeIfAbsent (Integer.valueOf (pageSize), EmptyDeviceBank::new);
    }


    /**
     * Constructor.
     *
     * @param pageSize The number of elements in a page of the bank
     */
    private EmptyDeviceBank (final int pageSize)
    {
        super (pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public IDevice getItem (final int index)
    {
        return EmptyDevice.INSTANCE;
    }
}