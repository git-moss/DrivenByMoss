// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw.data;

import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ISend;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.SendBank;
import com.bitwig.extension.controller.api.SettableColorValue;


/**
 * The data of a channel.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ChannelImpl implements IChannel
{
    protected Channel  channel;
    private ISend []   sends;
    private int        index;
    private boolean    selected;
    private int        vu;
    private IParameter volumeParameter;
    private IParameter panParameter;


    /**
     * Constructor.
     *
     * @param channel The channel
     * @param maxParameterValue The maximum parameter value, remove when clipping bug is fixed
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     */
    public ChannelImpl (final Channel channel, final int maxParameterValue, final int index, final int numSends)
    {
        this.channel = channel;
        this.index = index;

        if (channel == null)
            return;

        channel.exists ().markInterested ();
        channel.name ().markInterested ();
        channel.isActivated ().markInterested ();
        channel.mute ().markInterested ();
        channel.solo ().markInterested ();
        channel.color ().markInterested ();

        this.volumeParameter = new ParameterImpl (channel.volume (), maxParameterValue);
        this.panParameter = new ParameterImpl (channel.pan (), maxParameterValue);

        channel.addVuMeterObserver (maxParameterValue, -1, true, value -> this.handleVUMeters (maxParameterValue, value));

        this.sends = new SendImpl [numSends];
        if (numSends == 0)
            return;
        final SendBank sendBank = channel.sendBank ();
        for (int i = 0; i < numSends; i++)
            this.sends[i] = new SendImpl (sendBank.getItemAt (i), maxParameterValue, i);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.channel.exists ().setIsSubscribed (enable);
        this.channel.name ().setIsSubscribed (enable);
        this.channel.isActivated ().setIsSubscribed (enable);
        this.channel.mute ().setIsSubscribed (enable);
        this.channel.solo ().setIsSubscribed (enable);
        this.channel.color ().setIsSubscribed (enable);

        this.volumeParameter.enableObservers (enable);
        this.panParameter.enableObservers (enable);

        for (final ISend send: this.sends)
            send.enableObservers (enable);

    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return this.index;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return this.selected;
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        this.selected = isSelected;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.channel.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActivated ()
    {
        return this.channel.isActivated ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.channel.name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr ()
    {
        return this.volumeParameter.getDisplayedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr (final int limit)
    {
        return this.volumeParameter.getDisplayedValue (limit);
    }


    /** {@inheritDoc} */
    @Override
    public int getVolume ()
    {
        return this.volumeParameter.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedVolume ()
    {
        return this.volumeParameter.getModulatedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr ()
    {
        return this.panParameter.getDisplayedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr (final int limit)
    {
        return this.panParameter.getDisplayedValue (limit);
    }


    /** {@inheritDoc} */
    @Override
    public int getPan ()
    {
        return this.panParameter.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedPan ()
    {
        return this.panParameter.getModulatedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        final SettableColorValue color = this.channel.color ();
        return new double []
        {
            color.red (),
            color.green (),
            color.blue ()
        };
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMute ()
    {
        return this.channel.mute ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSolo ()
    {
        return this.channel.solo ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getVu ()
    {
        return this.vu;
    }


    /** {@inheritDoc} */
    @Override
    public ISend [] getSends ()
    {
        return this.sends;
    }


    /**
     * Handle the value change of the VU meter.
     *
     * @param maxParameterValue For bug checking
     * @param value The value of the VU meter
     */
    private void handleVUMeters (final int maxParameterValue, final int value)
    {
        // Limit value to this.configuration.getMaxParameterValue () due to
        // https://github.com/teotigraphix/Framework4Bitwig/issues/98
        this.vu = value >= maxParameterValue ? maxParameterValue - 1 : value;
    }
}
