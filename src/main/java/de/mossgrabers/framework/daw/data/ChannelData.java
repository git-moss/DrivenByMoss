// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.SendBank;
import com.bitwig.extension.controller.api.SettableColorValue;


/**
 * The data of a channel.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ChannelData
{
    protected Channel     channel;
    private SendData []   sends;
    private int           index;
    private boolean       selected;
    private int           vu;
    private ParameterData volumeParameter;
    private ParameterData panParameter;


    /**
     * Constructor.
     *
     * @param channel The channel
     * @param maxParameterValue The maximum parameter value, remove when clipping bug is fixed
     * @param index The index of the channel in the page
     * @param numSends The number of sends of a bank
     */
    public ChannelData (final Channel channel, final int maxParameterValue, final int index, final int numSends)
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

        channel.addIsSelectedInEditorObserver (this::handleChannelSelect);

        this.volumeParameter = new ParameterData (channel.volume (), maxParameterValue);
        this.panParameter = new ParameterData (channel.pan (), maxParameterValue);

        channel.addVuMeterObserver (maxParameterValue, -1, true, value -> this.handleVUMeters (maxParameterValue, value));

        this.sends = new SendData [numSends];
        if (numSends == 0)
            return;
        final SendBank sendBank = channel.sendBank ();
        for (int i = 0; i < numSends; i++)
            this.sends[i] = new SendData (sendBank.getItemAt (i), maxParameterValue, i);
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
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

        for (final SendData send: this.sends)
            send.enableObservers (enable);

    }


    /**
     * Get the index of the channel in the current bank page.
     *
     * @return The index of the channel in the current bank page
     */
    public int getIndex ()
    {
        return this.index;
    }


    /**
     * True if the channel is selected.
     *
     * @return True if the channel is selected.
     */
    public boolean isSelected ()
    {
        return this.selected;
    }


    /**
     * Set the selected state of the channel.
     *
     * @param isSelected True if the channel is selected
     */
    public void setSelected (final boolean isSelected)
    {
        this.selected = isSelected;
    }


    /**
     * Returns true if the channel exits.
     *
     * @return True if the channel exits.
     */
    public boolean doesExist ()
    {
        return this.channel.exists ().get ();
    }


    /**
     * Returns true if the channel is activated.
     *
     * @return True if the channel is activated
     */
    public boolean isActivated ()
    {
        return this.channel.isActivated ().get ();
    }


    /**
     * Get the name of the channel.
     *
     * @return The name of the channel
     */
    public String getName ()
    {
        return this.channel.name ().get ();
    }


    /**
     * Get the volume as a formatted text.
     *
     * @return The volume text
     */
    public String getVolumeStr ()
    {
        return this.volumeParameter.getDisplayedValue ();
    }


    /**
     * Get the volume as a formatted text.
     *
     * @param limit Limit the text to this length
     * @return The volume text
     */
    public String getVolumeStr (final int limit)
    {
        return this.volumeParameter.getDisplayedValue (limit);
    }


    /**
     * Get the volume.
     *
     * @return The volume
     */
    public int getVolume ()
    {
        return this.volumeParameter.getValue ();
    }


    /**
     * Get the modulated volume.
     *
     * @return The modulated volume
     */
    public int getModulatedVolume ()
    {
        return this.volumeParameter.getModulatedValue ();
    }


    /**
     * Get the panorama as a formatted text
     *
     * @return The panorama text
     */
    public String getPanStr ()
    {
        return this.panParameter.getDisplayedValue ();
    }


    /**
     * Get the panorama as a formatted text
     *
     * @param limit Limit the text to this length
     * @return The panorama text
     */
    public String getPanStr (final int limit)
    {
        return this.panParameter.getDisplayedValue (limit);
    }


    /**
     * Get the panorama.
     *
     * @return The panorama
     */
    public int getPan ()
    {
        return this.panParameter.getValue ();
    }


    /**
     * Get the modulated panorama.
     *
     * @return The modulated panorama
     */
    public int getModulatedPan ()
    {
        return this.panParameter.getModulatedValue ();
    }


    /**
     * Get the color of the channel.
     *
     * @return The color in RGB
     */
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


    /**
     * True if muted.
     *
     * @return True if muted.
     */
    public boolean isMute ()
    {
        return this.channel.mute ().get ();
    }


    /**
     * True if soloed.
     *
     * @return True if soloed.
     */
    public boolean isSolo ()
    {
        return this.channel.solo ().get ();
    }


    /**
     * Get the VU value.
     *
     * @return The VU value
     */
    public int getVu ()
    {
        return this.vu;
    }


    /**
     * Get the sends of the channel.
     *
     * @return The sends
     */
    public SendData [] getSends ()
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


    private void handleChannelSelect (final boolean isSelected)
    {
        this.selected = isSelected;
    }
}
