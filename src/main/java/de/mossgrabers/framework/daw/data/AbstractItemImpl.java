// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * An item.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractItemImpl implements IItem
{
    protected final int index;
    private boolean     selected;


    /**
     * Constructor.
     *
     * @param index The index of the item in a page
     */
    public AbstractItemImpl (final int index)
    {
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.index;
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return this.selected;
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        this.selected = isSelected;
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Not supported
    }
}
