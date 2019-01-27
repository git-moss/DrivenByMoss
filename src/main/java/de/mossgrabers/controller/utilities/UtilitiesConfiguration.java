// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.utilities;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.controller.IValueChanger;


/**
 * The configuration settings for the Utilities implementation.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UtilitiesConfiguration extends AbstractConfiguration
{
    private final AutoColor autoColor;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param autoColor The auto color access
     */
    public UtilitiesConfiguration (final IValueChanger valueChanger, final AutoColor autoColor)
    {
        super (valueChanger);

        this.autoColor = autoColor;
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        ///////////////////////////
        // Auto Color

        final NamedColor [] colors = NamedColor.values ();
        for (int i = 0; i < colors.length; i++)
        {
            final IStringSetting setting = settingsUI.getStringSetting (colors[i].getName (), "Auto Color", 256, "");
            final int index = i;
            setting.addValueObserver (name -> this.autoColor.handleRegExChange (index, colors[index], name));
        }
    }
}
