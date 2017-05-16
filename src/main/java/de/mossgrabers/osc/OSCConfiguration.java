// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.osc;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ValueChanger;

import com.bitwig.extension.controller.api.Preferences;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.SettableStringValue;


/**
 * The configuration settings for OSC4Bitwig.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCConfiguration extends AbstractConfiguration
{
    /** ID for receive host setting. */
    public static final Integer RECEIVE_HOST   = Integer.valueOf (20);
    /** ID for receive port setting. */
    public static final Integer RECEIVE_PORT   = Integer.valueOf (21);
    /** ID for send host setting. */
    public static final Integer SEND_HOST      = Integer.valueOf (22);
    /** ID for send port setting. */
    public static final Integer SEND_PORT      = Integer.valueOf (23);

    private static final String DEFAULT_SERVER = "127.0.0.1";

    private String              receiveHost    = DEFAULT_SERVER;
    private int                 receivePort    = 8000;
    private String              sendHost       = DEFAULT_SERVER;
    private int                 sendPort       = 9000;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     */
    public OSCConfiguration (final ValueChanger valueChanger)
    {
        super (valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final Preferences prefs)
    {
        ///////////////////////////
        // Network

        final SettableStringValue receiveHostSetting = prefs.getStringSetting ("Host", "Receive from (Script restart required)", 15, DEFAULT_SERVER);
        receiveHostSetting.addValueObserver (value -> {
            this.receiveHost = value;
            this.notifyObservers (OSCConfiguration.RECEIVE_HOST);
        });

        final SettableRangedValue receivePortSetting = prefs.getNumberSetting ("Port", "Receive from (Script restart required)", 0, 65535, 1, "", 8000);
        receivePortSetting.addValueObserver (65535, value -> {
            this.receivePort = value;
            this.notifyObservers (OSCConfiguration.RECEIVE_PORT);
        });

        final SettableStringValue sendHostSetting = prefs.getStringSetting ("Host", "Send to", 15, DEFAULT_SERVER);
        sendHostSetting.addValueObserver (value -> {
            this.sendHost = value;
            this.notifyObservers (OSCConfiguration.SEND_HOST);
        });

        final SettableRangedValue sendPortSetting = prefs.getNumberSetting ("Port", "Send to", 0, 65535, 1, "", 9000);
        sendPortSetting.addValueObserver (65535, value -> {
            this.sendPort = value;
            this.notifyObservers (SEND_PORT);
        });

        ///////////////////////////
        // Accent

        this.activateAccentActiveSetting (prefs);
        this.activateAccentValueSetting (prefs);

        ///////////////////////////
        // Workflow

        this.activateEnableVUMetersSetting (prefs);
    }


    /**
     * Get the host on which the extension receives OSC messages.
     *
     * @return The receive host
     */
    public String getReceiveHost ()
    {
        return this.receiveHost;
    }


    /**
     * Get the port of the host on which the extension receives OSC messages.
     *
     * @return The port
     */
    public int getReceivePort ()
    {
        return this.receivePort;
    }


    /**
     * Get the host on which the extension sends OSC messages.
     *
     * @return The receive host
     */
    public String getSendHost ()
    {
        return this.sendHost;
    }


    /**
     * Get the port of the host on which the extension sends OSC messages.
     *
     * @return The port
     */
    public int getSendPort ()
    {
        return this.sendPort;
    }
}
