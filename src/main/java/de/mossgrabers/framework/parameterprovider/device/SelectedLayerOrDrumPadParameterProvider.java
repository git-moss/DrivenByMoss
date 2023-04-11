// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.device;

import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.observer.IValueObserver;

import java.util.Set;


/**
 * Get a number of parameters. This implementation provides the selected layers volume, panorama and
 * send parameters.
 *
 * @author Jürgen Moßgraber
 */
public class SelectedLayerOrDrumPadParameterProvider extends AbstractSelectedChannelParameterProvider<IChannelBank<ILayer>, ILayer> implements IValueObserver<Boolean>
{
    protected final ISpecificDevice device;


    /**
     * Constructor.
     *
     * @param device Uses the layer bank from the given device to get the parameters
     */
    public SelectedLayerOrDrumPadParameterProvider (final ISpecificDevice device)
    {
        super (device.getLayerBank ());

        this.device = device;
        this.device.addHasDrumPadsObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void update (final Boolean hasDrumPads)
    {
        // Switch bank and re-register all observers
        final Set<IParametersAdjustObserver> previousObservers = this.removeParametersObservers ();
        this.bank = hasDrumPads.booleanValue () ? this.device.getDrumPadBank () : this.device.getLayerBank ();
        previousObservers.forEach (this::addParametersObserver);

        this.notifyParametersObservers ();
    }
}
