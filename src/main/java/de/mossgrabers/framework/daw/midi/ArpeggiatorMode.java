// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

import java.util.HashMap;
import java.util.Map;


/**
 * An arpeggiator mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum ArpeggiatorMode
{
    /** All mode. */
    ALL("All"),
    /** Up mode. */
    UP("Up"),
    /** Up/Down mode. */
    UP_DOWN("Up/Down"),
    /** Up then down mode. */
    UP_THEN_DOWN("Up then Down"),
    /** Down mode. */
    DOWN("Down"),
    /** Down/Up mode. */
    DOWN_UP("Down/Up"),
    /** Down then up mode. */
    DOWN_THEN_UP("Down then Up"),
    /** Flow mode. */
    FLOW("Flow"),
    /** Random mode. */
    RANDOM("Random"),
    /** Converge up mode. */
    CONVERGE_UP("Converge Up"),
    /** Converge down mode. */
    CONVERGE_DOWN("Converge Down"),
    /** Diverge up mode. */
    DIVERGE_UP("Diverge Up"),
    /** Diverge down mode. */
    DIVERGE_DOWN("Diverge Down"),
    /** Thumb up mode. */
    THUMB_UP("Thumb Up"),
    /** Thumb down mode. */
    THUMB_DOWN("Thumb Down"),
    /** Pinky up mode. */
    PINKY_UP("Pinky UP"),
    /** Pinky down mode. */
    PINKY_DOWN("Pinky Down");


    private static final String []                    NAMES         = new String [ArpeggiatorMode.values ().length];
    private static final Map<String, ArpeggiatorMode> NAME_MODE_MAP = new HashMap<> (ArpeggiatorMode.values ().length);
    static
    {
        final ArpeggiatorMode [] values = ArpeggiatorMode.values ();
        for (int i = 0; i < values.length; i++)
        {
            NAMES[i] = values[i].getName ();
            NAME_MODE_MAP.put (NAMES[i], values[i]);
        }
    }

    private final String name;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     */
    private ArpeggiatorMode (final String name)
    {
        this.name = name;
    }


    /**
     * Get the name.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Get the names of all commands.
     *
     * @return The names
     */
    public static String [] getNames ()
    {
        return NAMES;
    }


    /**
     * Lookup an arpeggiator mode.
     *
     * @param value The value of the mode
     * @return The mode
     */
    public static ArpeggiatorMode lookup (final String value)
    {
        return ArpeggiatorMode.valueOf (value);
    }


    /**
     * Lookup an arpeggiator mode.
     *
     * @param name The name of the mode
     * @return The mode
     */
    public static ArpeggiatorMode lookupByName (final String name)
    {
        return NAME_MODE_MAP.getOrDefault (name, ArpeggiatorMode.ALL);
    }
}
