// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw.data;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.resource.ChannelType;

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
    protected IValueChanger valueChanger;
    protected Channel       channel;

    private ISend []        sends;
    private int             index;
    private boolean         selected;
    private int             vu;
    private int             vuLeft;
    private int             vuRight;
    private IParameter      volumeParameter;
    private IParameter      panParameter;


    /**
     * Constructor.
     *
     * @param channel The channel
     * @param valueChanger The valueChanger
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     */
    public ChannelImpl (final Channel channel, final IValueChanger valueChanger, final int index, final int numSends)
    {
        this.channel = channel;
        this.valueChanger = valueChanger;
        this.index = index;

        if (channel == null)
            return;

        channel.exists ().markInterested ();
        channel.name ().markInterested ();
        channel.isActivated ().markInterested ();
        channel.mute ().markInterested ();
        channel.solo ().markInterested ();
        channel.color ().markInterested ();

        final int maxParameterValue = valueChanger.getUpperBound ();

        this.volumeParameter = new ParameterImpl (valueChanger, channel.volume (), maxParameterValue);
        this.panParameter = new ParameterImpl (valueChanger, channel.pan (), maxParameterValue);

        channel.addVuMeterObserver (maxParameterValue, -1, true, value -> this.handleVUMeters (maxParameterValue, value));
        channel.addVuMeterObserver (maxParameterValue, 0, true, value -> this.handleVULeftMeter (maxParameterValue, value));
        channel.addVuMeterObserver (maxParameterValue, 1, true, value -> this.handleVURightMeter (maxParameterValue, value));

        this.sends = new SendImpl [numSends];
        if (numSends == 0)
            return;
        final SendBank sendBank = channel.sendBank ();
        for (int i = 0; i < numSends; i++)
            this.sends[i] = new SendImpl (valueChanger, sendBank.getItemAt (i), maxParameterValue, i);
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
    public ChannelType getType ()
    {
        return ChannelType.UNKNOWN;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActivated ()
    {
        return this.channel.isActivated ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setIsActivated (final boolean value)
    {
        this.channel.isActivated ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsActivated ()
    {
        this.channel.isActivated ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.channel.name ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return this.channel.name ().getLimited (limit);
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
    public void changeVolume (final int control)
    {
        this.volumeParameter.changeValue (control);
    }


    /** {@inheritDoc} */
    @Override
    public void setVolume (final double value)
    {
        this.volumeParameter.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetVolume ()
    {
        this.volumeParameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchVolume (final boolean isBeingTouched)
    {
        this.volumeParameter.touchValue (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setVolumeIndication (final boolean indicate)
    {
        this.volumeParameter.setIndication (indicate);
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
    public void changePan (final int control)
    {
        this.panParameter.changeValue (control);
    }


    /** {@inheritDoc} */
    @Override
    public void setPan (final double value)
    {
        this.panParameter.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void resetPan ()
    {
        this.panParameter.resetValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void touchPan (final boolean isBeingTouched)
    {
        this.panParameter.touchValue (isBeingTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setPanIndication (final boolean indicate)
    {
        this.panParameter.setIndication (indicate);
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedPan ()
    {
        return this.panParameter.getModulatedValue ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMute ()
    {
        return this.channel.mute ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMute (final boolean value)
    {
        this.channel.mute ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMute ()
    {
        this.channel.mute ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSolo ()
    {
        return this.channel.solo ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setSolo (final boolean value)
    {
        this.channel.solo ().set (value);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleSolo ()
    {
        this.channel.solo ().toggle ();
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
    public void setColor (final double red, final double green, final double blue)
    {
        this.channel.color ().set ((float) red, (float) green, (float) blue);
    }


    /** {@inheritDoc} */
    @Override
    public int getVu ()
    {
        return this.vu;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuLeft ()
    {
        return this.vuLeft;
    }


    /** {@inheritDoc} */
    @Override
    public int getVuRight ()
    {
        return this.vuRight;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumSends ()
    {
        return this.sends.length;
    }


    /** {@inheritDoc} */
    @Override
    public ISend getSend (final int sendIndex)
    {
        return sendIndex < this.sends.length ? this.sends[sendIndex] : null;
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.channel.duplicate ();
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        this.channel.selectInEditor ();
        this.channel.selectInMixer ();
    }


    /** {@inheritDoc} */
    @Override
    public void makeVisible ()
    {
        this.channel.makeVisibleInArranger ();
        this.channel.makeVisibleInMixer ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectAndMakeVisible ()
    {
        this.select ();
        this.makeVisible ();
    }


    private void handleVUMeters (final int maxParameterValue, final int value)
    {
        // Limit value to this.configuration.getMaxParameterValue () due to
        // https://github.com/teotigraphix/Framework4Bitwig/issues/98
        this.vu = value >= maxParameterValue ? maxParameterValue - 1 : value;
    }


    private void handleVULeftMeter (final int maxParameterValue, final int value)
    {
        // Limit value to this.configuration.getMaxParameterValue () due to
        // https://github.com/teotigraphix/Framework4Bitwig/issues/98
        this.vuLeft = value >= maxParameterValue ? maxParameterValue - 1 : value;
    }


    private void handleVURightMeter (final int maxParameterValue, final int value)
    {
        // Limit value to this.configuration.getMaxParameterValue () due to
        // https://github.com/teotigraphix/Framework4Bitwig/issues/98
        this.vuRight = value >= maxParameterValue ? maxParameterValue - 1 : value;
    }
}
