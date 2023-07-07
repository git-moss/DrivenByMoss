// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Default data for an empty slot bank.
 *
 * @author Jürgen Moßgraber
 */
public class EmptySlotBank extends EmptyBank<ISlot> implements ISlotBank
{
    private static final Map<Integer, EmptySlotBank> INSTANCES = new HashMap<> ();


    /**
     * Get an instance of an EmptySlotBank for the given page size. Instances are cached.
     *
     * @param pageSize The page size for which to get an empty bank
     * @return The bank
     */
    public static EmptySlotBank getInstance (final int pageSize)
    {
        return INSTANCES.computeIfAbsent (Integer.valueOf (pageSize), EmptySlotBank::new);
    }


    /**
     * Constructor.
     *
     * @param pageSize The size of the pages
     */
    public EmptySlotBank (final int pageSize)
    {
        super (pageSize);
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ISlot> getEmptySlot (final int startFrom)
    {
        return Optional.empty ();
    }


    /** {@inheritDoc} */
    @Override
    public ISlot getItem (final int index)
    {
        return EmptySlot.INSTANCE;
    }
}