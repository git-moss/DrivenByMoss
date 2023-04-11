// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.utilities.autocolor;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IHost;

import java.util.EnumMap;
import java.util.Map;


/**
 * The configuration settings for the Auto Color implementation.
 *
 * @author Jürgen Moßgraber
 */
public class AutoColorConfiguration extends AbstractConfiguration
{
    private static final String         CATEGORY_AUTO_COLOR = "Auto Color";

    /** ID for dis-/enabling the auto color setting. */
    public static final Integer         ENABLE_AUTO_COLOR   = Integer.valueOf (50);
    /** First ID for all auto color settings. NOTE: All colors increase from that value! */
    public static final Integer         COLOR_REGEX         = Integer.valueOf (100);

    private boolean                     enableAutoColor;
    private final Map<DAWColor, String> colorRegEx          = new EnumMap<> (DAWColor.class);


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     */
    public AutoColorConfiguration (final IHost host, final IValueChanger valueChanger)
    {
        super (host, valueChanger, null);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Auto Color

        final IEnumSetting enableAutoColorSetting = globalSettings.getEnumSetting (CATEGORY_AUTO_COLOR, CATEGORY_AUTO_COLOR, ON_OFF_OPTIONS, ON_OFF_OPTIONS[1]);
        enableAutoColorSetting.addValueObserver (value -> {
            this.enableAutoColor = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (ENABLE_AUTO_COLOR);
        });
        this.isSettingActive.add (ENABLE_AUTO_COLOR);

        final DAWColor [] colors = DAWColor.values ();
        for (int i = 0; i < colors.length; i++)
        {
            final DAWColor color = colors[i];
            final IStringSetting setting = globalSettings.getStringSetting (color.getName (), CATEGORY_AUTO_COLOR, 256, "");
            final int index = i;
            final Integer colorRegexIndex = Integer.valueOf (COLOR_REGEX.intValue () + index);
            setting.addValueObserver (value -> {
                this.colorRegEx.put (color, value);
                this.notifyObservers (colorRegexIndex);
            });
            this.isSettingActive.add (colorRegexIndex);
        }
    }


    /**
     * Returns true if auto coloring is enabled.
     *
     * @return True if auto coloring is enabled
     */
    public boolean isEnableAutoColor ()
    {
        return this.enableAutoColor;
    }


    /**
     * Get the regular expression value for the given color.
     *
     * @param color The color
     * @return The regular expression
     */
    public String getColorRegExValue (final DAWColor color)
    {
        return this.colorRegEx.get (color);
    }
}
