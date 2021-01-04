// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;


/**
 * Get a number of parameters. This implementation provides the selected layers volume, panorama and
 * send parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayerParameterProvider extends ChannelParameterProvider
{
    /**
     * Constructor.
     *
     * @param model Uses the current channel bank from this model to get the parameters
     */
    public LayerParameterProvider (final IModel model)
    {
        super (model);
    }


    /** {@inheritDoc} */
    @Override
    protected IChannelBank<? extends IChannel> getBank ()
    {
        return this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT).getLayerOrDrumPadBank ();
    }


    /** {@inheritDoc} */
    @Override
    public void addParametersObserver (final IParametersAdjustObserver observer)
    {
        this.observers.add (observer);

        final IChannelBank<? extends IChannel> b = this.getBank ();
        b.addPageObserver (this);
        b.addSelectionObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    public void removeParametersObserver (final IParametersAdjustObserver observer)
    {
        this.observers.remove (observer);

        final IChannelBank<? extends IChannel> b = this.getBank ();
        b.removePageObserver (this);
        b.removeSelectionObserver (this);
    }
}
