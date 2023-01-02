// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler.utils;

import java.util.HashMap;
import java.util.Map;


/**
 * A MIDI value in low (7-bit) or high resolution (14-bit).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiValue
{
    private static final Map<Integer, MidiValue> HI_RES_VALUES = new HashMap<> (16384);
    private static final Map<Integer, MidiValue> LO_RES_VALUES = new HashMap<> (128);

    private final boolean                        isHighRes;
    private final int                            value;
    private final int                            upscaled;


    /**
     * Constructor.
     *
     * @param isHighRes True if high resolution
     * @param value The value in 7-bit or 14-bit
     */
    private MidiValue (final int value, final boolean isHighRes)
    {
        this.isHighRes = isHighRes;
        this.value = value;
        this.upscaled = this.isHighRes ? value : (int) Math.round (value * 16383.0 / 127.0);
    }


    /**
     * Get an instance of a MidiValue. All instances are cached.
     *
     * @param value The value
     * @param isHighRes Is it a high resolution value (14-bit)?
     * @return The instance
     */
    public static MidiValue get (final int value, final boolean isHighRes)
    {
        if (isHighRes)
            return HI_RES_VALUES.computeIfAbsent (Integer.valueOf (value), val -> new MidiValue (val.intValue (), true));
        return LO_RES_VALUES.computeIfAbsent (Integer.valueOf (value), val -> new MidiValue (val.intValue (), false));
    }


    /**
     * Is the value positive (> 0)?
     *
     * @return True if positive
     */
    public boolean isPositive ()
    {
        return this.value > 0;
    }


    /**
     * Is the value high or low resolution?
     *
     * @return True if high resolution.
     */
    public boolean isHighRes ()
    {
        return this.isHighRes;
    }


    /**
     * Get the value.
     *
     * @return The value
     */
    public int getValue ()
    {
        return this.value;
    }


    /**
     * Get the value. If this is a high-res value it is unmodified. If it is low-res it is
     * up-scaled.
     *
     * @return The up-scaled value
     */
    public int getUpscaled ()
    {
        return this.upscaled;
    }
}
