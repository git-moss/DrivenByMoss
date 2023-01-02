// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;

import java.util.Optional;


/**
 * Default data for an empty slot bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptySlotBank extends EmptyBank<ISlot> implements ISlotBank
{
    /** The singleton. */
    public static final ISlotBank INSTANCE = new EmptySlotBank ();


    /**
     * Constructor.
     */
    private EmptySlotBank ()
    {
        // Intentionally empty
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