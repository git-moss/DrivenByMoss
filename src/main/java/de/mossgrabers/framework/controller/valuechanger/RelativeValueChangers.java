// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.valuechanger;

import java.util.EnumMap;
import java.util.Map;


/**
 * Collection of Reaper internal relative value changers.
 *
 * @author Jürgen Moßgraber
 */
public class RelativeValueChangers
{
    private static final Map<RelativeEncoding, IValueChanger> VALUE_CHANGERS = new EnumMap<> (RelativeEncoding.class);

    static
    {
        VALUE_CHANGERS.put (RelativeEncoding.TWOS_COMPLEMENT, new TwosComplementValueChanger (127, 1));
        VALUE_CHANGERS.put (RelativeEncoding.OFFSET_BINARY, new OffsetBinaryRelativeValueChanger (127, 1));
        VALUE_CHANGERS.put (RelativeEncoding.SIGNED_BIT, new SignedBitRelativeValueChanger (127, 1));
        VALUE_CHANGERS.put (RelativeEncoding.SIGNED_BIT2, new SignedBit2RelativeValueChanger (127, 1));
    }


    /**
     * Get the value changer for the encoding.
     *
     * @param encoding The encoding
     * @return The value changer
     */
    public static IValueChanger get (final RelativeEncoding encoding)
    {
        return VALUE_CHANGERS.get (encoding);
    }


    /**
     * Update the sensitivity on all value changers.
     *
     * @param sensitivity The sensitivity in the range [-100..100], 0 is the default, negative
     *            values are slower, positive faster
     */
    public static void setSensitivity (final double sensitivity)
    {
        VALUE_CHANGERS.forEach ( (enc, valueChanger) -> valueChanger.setSensitivity (sensitivity));
    }


    /**
     * Constructor. Private since this is a helper class.
     */
    private RelativeValueChangers ()
    {
        // Intentionally empty
    }
}
