// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.osc.mkii;

import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.osc.AbstractOpenSoundControlConfiguration;


/**
 * The configuration settings for OSC.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolOSCConfiguration extends AbstractOpenSoundControlConfiguration
{
    private static final int DEFAULT_SEND_PORT    = KontrolOSCControllerSetup.IS_16 ? 7577 : 7575;
    private static final int DEFAULT_RECEIVE_PORT = KontrolOSCControllerSetup.IS_16 ? 7578 : 7576;

    private int              receivePort          = DEFAULT_RECEIVE_PORT;
    private String           sendHost             = DEFAULT_SERVER;
    private int              sendPort             = DEFAULT_SEND_PORT;


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
        ///////////////////////////
        // Debug

        this.activateOSCLogging (settingsUI);
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
