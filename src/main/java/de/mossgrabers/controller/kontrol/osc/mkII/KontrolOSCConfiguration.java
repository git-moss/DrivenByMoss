// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.osc.mkII;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;


/**
 * The configuration settings for OSC4Bitwig.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolOSCConfiguration extends AbstractConfiguration
{
    private static final String DEFAULT_SERVER       = "127.0.0.1";
    private static final int    DEFAULT_SEND_PORT    = KontrolOSCExtension.IS_16 ? 7577 : 7575;
    private static final int    DEFAULT_RECEIVE_PORT = KontrolOSCExtension.IS_16 ? 7578 : 7576;

    private int                 receivePort          = DEFAULT_RECEIVE_PORT;
    private String              sendHost             = DEFAULT_SERVER;
    private int                 sendPort             = DEFAULT_SEND_PORT;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     */
    public KontrolOSCConfiguration (final IValueChanger valueChanger)
    {
        super (valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        // Intentionally empty
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
