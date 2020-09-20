// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameterprovider;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;


/**
 * Get a number of parameters. This abstract implementation provides a parameter of the channels of
 * the active page of the given channel bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractChannelParameterProvider implements IParameterProvider
{
    protected final IModel                         model;
    private final IChannelBank<? extends IChannel> bank;


    /**
     * Constructor.
     *
     * @param bank The bank from which to get the parameters
     */
    public AbstractChannelParameterProvider (final IChannelBank<? extends IChannel> bank)
    {
        this.bank = bank;
        this.model = null;
    }


    /**
     * Constructor.
     *
     * @param model Uses the current track bank from this model to get the parameters
     */
    public AbstractChannelParameterProvider (final IModel model)
    {
        this.bank = null;
        this.model = model;
    }


    /** {@inheritDoc} */
    @Override
    public int size ()
    {
        return this.getBank ().getPageSize ();
    }


    protected IChannel getChannel (final int index)
    {
        return this.getBank ().getItem (index);
    }


    protected IChannelBank<? extends IChannel> getBank ()
    {
        return this.bank == null ? this.model.getCurrentTrackBank () : this.bank;
    }
}
