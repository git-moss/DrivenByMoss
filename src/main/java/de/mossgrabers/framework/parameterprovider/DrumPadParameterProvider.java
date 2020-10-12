// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;


/**
 * Get a number of parameters. This implementation provides the selected layers volume, panorama and
 * send parameters of the first drum device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumPadParameterProvider extends ChannelParameterProvider
{
    /**
     * Constructor.
     *
     * @param model Uses the current channel bank from this model to get the parameters
     */
    public DrumPadParameterProvider (final IModel model)
    {
        super (model);
    }


    /** {@inheritDoc} */
    @Override
    protected IChannelBank<? extends IChannel> getBank ()
    {
        return this.model.getDrumDevice ().getDrumPadBank ();
    }
}
