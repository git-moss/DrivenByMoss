// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;


/**
 * A control on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractHwInputControl extends AbstractHwControl implements IHwInputControl
{
    protected IMidiInput input;
    protected BindType   type;
    protected int        channel;


    /**
     * Constructor.
     *
     * @param host The host
     * @param label The label of the control
     */
    protected AbstractHwInputControl (final IHost host, final String label)
    {
        super (host, label);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int value)
    {
        this.bind (input, type, 0, value);
    }
}
