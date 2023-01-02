// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.constants;

import java.util.HashMap;
import java.util.Map;


/**
 * Record quantization options.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum RecordQuantization
{
    /** Off */
    RES_OFF("Off", "OFF"),
    /** 1/32 */
    RES_1_32("1/32", "1/32"),
    /** 1/16 */
    RES_1_16("1/16", "1/16"),
    /** 1/8 */
    RES_1_8("1/8", "1/8"),
    /** 1/4 */
    RES_1_4("1/4", "1/4");


    private static final Map<String, RecordQuantization> QUANTIZATION_VALUES = new HashMap<> ();
    static
    {
        for (final RecordQuantization value: values ())
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
    RecordQuantization (final String name, final String value)
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
     * Get the record quantization by its value.
     *
     * @param value The value
     * @return The record quantization
     */
    public static RecordQuantization lookup (final String value)
    {
        return QUANTIZATION_VALUES.getOrDefault (value, RecordQuantization.RES_OFF);
    }
}
