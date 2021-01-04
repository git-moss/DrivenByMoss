// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

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
    /** Pikny up mode. */
    PINKY_UP("Pinky UP"),
    /** Pinky down mode. */
    PINKY_DOWN("Pinky Down");


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
        final ArpeggiatorMode [] values = ArpeggiatorMode.values ();
        for (final ArpeggiatorMode mode: values)
        {
            if (mode.getName ().equals (name))
                return mode;
        }
        return values[0];
    }
}
