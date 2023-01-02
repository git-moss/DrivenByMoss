// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.constants;

import java.util.HashMap;
import java.util.Map;


/**
 * Launch quantization options.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum LaunchQuantization
{
    /** Off */
    RES_NONE("None", "none"),
    /** 8 */
    RES_8("8", "8"),
    /** 4 */
    RES_4("4", "4"),
    /** 2 */
    RES_2("2", "2"),
    /** 1 */
    RES_1("1", "1"),
    /** 1/2 */
    RES_1_2("1/2", "1/2"),
    /** 1/4 */
    RES_1_4("1/4", "1/4"),
    /** 1/8 */
    RES_1_8("1/8", "1/8"),
    /** 1/16 */
    RES_1_16("1/16", "1/16");


    private static final Map<String, LaunchQuantization> QUANTIZATION_VALUES = new HashMap<> ();
    static
    {
        for (final LaunchQuantization value: values ())
            QUANTIZATION_VALUES.put (value.getValue (), value);
    }

    private final String name;
    private final String value;


    /**
     * Constructor.
     *
     * @param name The name of the quantization
     * @param value The value
     */
    LaunchQuantization (final String name, final String value)
    {
        this.name = name;
        this.value = value;
    }


    /**
     * Get the name of the quantization.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Get the value of the quantization.
     *
     * @return The value
     */
    public String getValue ()
    {
        return this.value;
    }


    /**
     * Get the launch quantization by its value.
     *
     * @param value The value
     * @return The launch quantization
     */
    public static LaunchQuantization lookup (final String value)
    {
        return QUANTIZATION_VALUES.getOrDefault (value, LaunchQuantization.RES_NONE);
    }
}
