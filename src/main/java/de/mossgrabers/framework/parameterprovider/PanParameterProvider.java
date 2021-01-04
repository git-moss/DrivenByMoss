// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;


/**
 * Get a number of parameters. This implementation provides all panorama parameters of the tracks of
 * the current channel bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PanParameterProvider extends AbstractChannelParameterProvider
{
    /**
     * Constructor.
     *
     * @param bank The bank from which to get the parameters
     */
    public PanParameterProvider (final IChannelBank<? extends IChannel> bank)
    {
        super (bank);
    }


    /**
     * Constructor.
     *
     * @param model Uses the current track bank from this model to get the parameters
     */
    public PanParameterProvider (final IModel model)
    {
        super (model);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter get (final int index)
    {
        return this.getChannel (index).getPanParameter ();
    }
}
