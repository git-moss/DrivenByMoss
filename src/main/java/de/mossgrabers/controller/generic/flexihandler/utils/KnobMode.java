// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
package de.mossgrabers.controller.generic.flexihandler.utils;

import java.util.HashMap;
import java.util.Map;


/**
 * All knob mode value encodings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum KnobMode
{
    /** Absolute values: 0. */
    ABSOLUTE("Absolute (push button: Button down > 0, button up = 0)"),
    /** Relative values encoded as twos complement: 1. */
    RELATIVE_TWOS_COMPLEMENT("Relative: Twos Complement (1-64 increment, 127-65 decrement)"),
    /** Relative values encoded as signed bit: 2. */
    RELATIVE_SIGNED_BIT("Relative: Signed Bit (1-63 increment, 65-127 decrement)"),
    /** Relative values encoded as offset binary: 3. */
    RELATIVE_OFFSET_BINARY("Relative: Offset Binary (65-127 increment, 63-0 decrement)"),
    /** Absolute values for toggle buttons: 4. */
    ABSOLUTE_TOGGLE("Absolute (toggle button: 1st press > 0, 2nd press = 0)");


    private static final String []             LABELS;
    private static final Map<String, KnobMode> LABEL_KNOBMODE_MAP;
    static
    {
        final KnobMode [] values = KnobMode.values ();
        LABELS = new String [values.length];
        LABEL_KNOBMODE_MAP = new HashMap<> (values.length);
        for (int i = 0; i < values.length; i++)
        {
            LABELS[i] = values[i].getLabel ();
            LABEL_KNOBMODE_MAP.put (LABELS[i], values[i]);
        }
    }

    private final String label;


    /**
     * Constructor.
     *
     * @param label The name of the knob mode
     */
    private KnobMode (final String label)
    {
        this.label = label;
    }


    /**
     * Get the label of the knob mode.
     *
     * @return The label
     */
    public String getLabel ()
    {
        return this.label;
    }


    /**
     * Get the labels of all knob modes.
     *
     * @return The labels
     */
    public static String [] getLabels ()
    {
        return LABELS;
    }


    /**
     * Lookup a knob mode by its label.
     *
     * @param label The label
     * @return The knob mode or if not found the ABSOLUTE mode
     */
    public static KnobMode lookupByLabel (final String label)
    {
        return LABEL_KNOBMODE_MAP.getOrDefault (label, KnobMode.ABSOLUTE);
    }
}
