// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider.device;

import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.Optional;


/**
 * Get a number of parameters. This implementation provides send parameters of the layers of the
 * current device.
 *
 * @author Jürgen Moßgraber
 */
public class SendLayerOrDrumPadParameterProvider extends AbstractLayerOrDrumPadParameterProvider
{
    private final int sendIndex;


    /**
     * Constructor.
     *
     * @param device Uses the layer bank from the given device to get their volume parameters
     * @param sendIndex The index of the send to provide
     */
    public SendLayerOrDrumPadParameterProvider (final ISpecificDevice device, final int sendIndex)
    {
        super (device);

        this.sendIndex = sendIndex;
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        if (this.sendIndex == -1)
        {
            final Optional<? extends IChannel> channel = this.bank.getSelectedItem ();
            if (channel.isEmpty ())
                return EmptyParameter.INSTANCE;
            final ISendBank sendBank = channel.get ().getSendBank ();
            return sendBank.getItemCount () > 0 ? sendBank.getItem (index) : EmptyParameter.INSTANCE;
        }

        final ISendBank sendBank = this.bank.getItem (index).getSendBank ();
        return sendBank.getItemCount () == 0 ? EmptyParameter.INSTANCE : sendBank.getItem (this.sendIndex);
    }
}
