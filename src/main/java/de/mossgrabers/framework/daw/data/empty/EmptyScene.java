// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.IScene;


/**
 * Default data for an empty scene.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptyScene extends EmptyItem implements IScene
{
    /** The singleton. */
    public static final IScene INSTANCE = new EmptyScene ();


    /**
     * Constructor.
     */
    private EmptyScene ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        return COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final double red, final double green, final double blue)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void launch ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        // Intentionally empty
    }
}
