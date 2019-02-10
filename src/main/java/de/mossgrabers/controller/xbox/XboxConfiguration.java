// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.xbox;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;


/**
 * The configuration settings for Xbox.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XboxConfiguration extends AbstractConfiguration
{
    private final IHost host;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     */
    public XboxConfiguration (final IHost host, final IValueChanger valueChanger)
    {
        super (valueChanger);
        this.host = host;
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        // TODO
    }
}
