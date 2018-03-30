// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.osc;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.controller.IValueChanger;


/**
 * The configuration settings for OSC4Bitwig.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCConfiguration extends AbstractConfiguration
{
    /** ID for receive port setting. */
    public static final Integer RECEIVE_PORT   = Integer.valueOf (31);
    /** ID for send host setting. */
    public static final Integer SEND_HOST      = Integer.valueOf (32);
    /** ID for send port setting. */
    public static final Integer SEND_PORT      = Integer.valueOf (33);
    /** ID for debug option. */
    public static final Integer DEBUG_COMMANDS = Integer.valueOf (34);

    private static final String DEFAULT_SERVER = "127.0.0.1";

    private int                 receivePort    = 8000;
    private String              sendHost       = DEFAULT_SERVER;
    private int                 sendPort       = 9000;
    private boolean             debugCommands  = false;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     */
    public OSCConfiguration (final IValueChanger valueChanger)
    {
        super (valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        ///////////////////////////
        // Network

        final IIntegerSetting receivePortSetting = settingsUI.getRangeSetting ("Port", "Receive from (Script restart required)", 0, 65535, 1, "", 8000);
        receivePortSetting.addValueObserver (value -> {
            this.receivePort = value.intValue ();
            this.notifyObservers (OSCConfiguration.RECEIVE_PORT);
        });

        final IStringSetting sendHostSetting = settingsUI.getStringSetting ("Host", "Send to", 15, DEFAULT_SERVER);
        sendHostSetting.addValueObserver (value -> {
            this.sendHost = value;
            this.notifyObservers (OSCConfiguration.SEND_HOST);
        });

        final IIntegerSetting sendPortSetting = settingsUI.getRangeSetting ("Port", "Send to", 0, 65535, 1, "", 9000);
        sendPortSetting.addValueObserver (value -> {
            this.sendPort = value.intValue ();
            this.notifyObservers (SEND_PORT);
        });

        ///////////////////////////
        // Accent

        this.activateAccentActiveSetting (settingsUI);
        this.activateAccentValueSetting (settingsUI);

        ///////////////////////////
        // Workflow

        this.activateEnableVUMetersSetting (settingsUI);

        ///////////////////////////
        // Debug

        final IEnumSetting debugCommandsSetting = settingsUI.getEnumSetting ("Debug commands", "Debug", ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        debugCommandsSetting.addValueObserver (value -> {
            this.debugCommands = "On".equals (value);
            this.notifyObservers (DEBUG_COMMANDS);
        });
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


    /**
     * Get if debugging should be enabled.
     *
     * @return True if debugging should be enabled
     */
    public boolean getDebugCommands ()
    {
        return this.debugCommands;
    }
}
