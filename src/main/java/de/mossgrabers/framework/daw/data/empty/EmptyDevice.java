// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.IDevice;


/**
 * Default data for an empty device.
 *
 * @author Jürgen Moßgraber
 */
public class EmptyDevice extends EmptyItem implements IDevice
{
    /** The instance. */
    public static final EmptyDevice INSTANCE = new EmptyDevice ();


    /**
     * Constructor.
     */
    private EmptyDevice ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
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


    /** {@inheritDoc} */
    @Override
    public boolean isEnabled ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleEnabledState ()
    {
        // Intentionally empty
    }
}
