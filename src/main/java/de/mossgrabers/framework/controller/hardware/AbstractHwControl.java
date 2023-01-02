// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

import de.mossgrabers.framework.daw.IHost;


/**
 * A control on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractHwControl implements IHwControl
{
    protected final IHost  host;
    protected final String label;


    /**
     * Constructor.
     *
     * @param host The host
     * @param label The label of the control
     */
    protected AbstractHwControl (final IHost host, final String label)
    {
        this.host = host;
        this.label = label;
    }


    /** {@index} */
    @Override
    public String getLabel ()
    {
        return this.label;
    }


    /** {@index} */
    @Override
    public void update ()
    {
        // Intentionally empty, overwrite for update functionality
    }
}
