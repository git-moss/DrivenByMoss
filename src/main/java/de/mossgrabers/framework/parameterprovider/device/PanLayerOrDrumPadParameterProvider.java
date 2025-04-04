// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.device;

import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Get a number of parameters. This implementation provides all panning parameters of the layers of
 * the current device.
 *
 * @author Jürgen Moßgraber
 */
public class PanLayerOrDrumPadParameterProvider extends AbstractLayerOrDrumPadParameterProvider
{
    /**
     * Constructor.
     *
     * @param device Uses the layer bank from the given device to get their volume parameters
     */
    public PanLayerOrDrumPadParameterProvider (final ISpecificDevice device)
    {
        super (device);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        return this.bank.getItem (index).getPanParameter ();
    }
}
