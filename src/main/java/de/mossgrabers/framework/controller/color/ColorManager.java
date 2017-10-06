// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.color;

import java.util.HashMap;
import java.util.Map;


/**
 * Manages color indices by IDs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ColorManager
{
    /** ID for color when button is turned off. */
    public static final String         BUTTON_STATE_OFF = "BUTTON_STATE_OFF";
    /** ID for color when button is turned on. */
    public static final String         BUTTON_STATE_ON  = "BUTTON_STATE_ON";
    /** ID for color when button is highlighted. */
    public static final String         BUTTON_STATE_HI  = "BUTTON_STATE_HI";

    private final Map<String, Integer> colors           = new HashMap<> ();


    /**
     * Registers a a color index. An exception is thrown if the color index is already registered.
     *
     * @param key The key under which to register the color index
     * @param colorIndex The color index
     */
    public void registerColor (final String key, final int colorIndex)
    {
        if (this.colors.containsKey (key))
            throw new ColorIndexException ("Color for key " + key + " is already registered!");
        this.colors.put (key, colorIndex);
    }


    /**
     * Get the color index which is registered with the given key.
     *
     * @param key The key
     * @return The color index
     */
    public int getColor (final String key)
    {
        final Integer colorIndex = this.colors.get (key);
        if (colorIndex == null)
            throw new ColorIndexException ("Color for key " + key + " is not registered!");
        return colorIndex;
    }
}
