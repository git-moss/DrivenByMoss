// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.AbstractItemImpl;


/**
 * Default data for an empty item.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptyItem extends AbstractItemImpl
{
    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        // Intentionally empty
    }
}
