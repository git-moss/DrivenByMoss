// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component.simple;

import de.mossgrabers.framework.graphics.canvas.component.IComponent;


/**
 * Base class for graphics components with 3 rows.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractBaseComponent implements IComponent
{
    protected static final int DEFAULT_WIDTH      = 128;
    protected static final int DEFAULT_HEIGHT     = 64;
    protected static final int DEFAULT_ROW_HEIGHT = 20;
    protected static final int CENTER             = DEFAULT_WIDTH / 2;
    protected static final int TOP                = 44;

    protected static final int RESOLUTION         = 1024;

    protected final String     label;


    /**
     * Constructor.
     *
     * @param label The first row text
     */
    protected AbstractBaseComponent (final String label)
    {
        this.label = label;
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.label == null ? 0 : this.label.hashCode ());
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null || this.getClass () != obj.getClass ())
            return false;
        final AbstractBaseComponent other = (AbstractBaseComponent) obj;
        if (this.label == null)
        {
            if (other.label != null)
                return false;
        }
        else if (!this.label.equals (other.label))
            return false;
        return true;
    }
}
