// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.midi;

import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;

import java.util.HashMap;
import java.util.Map;


/**
 * List of Bitwig note repeat arpeggiator modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum NoteRepeatModes
{
    /** All mode. */
    ALL,
    /** Up mode. */
    UP,
    /** Up/Down mode. */
    UP_DOWN,
    /** Up then down mode. */
    UP_THEN_DOWN,
    /** Down mode. */
    DOWN,
    /** Down/Up mode. */
    DOWN_UP,
    /** Down then up mode. */
    DOWN_THEN_UP,
    /** Flow mode. */
    FLOW,
    /** Random mode. */
    RANDOM,
    /** Converge up mode. */
    CONVERGE_UP,
    /** Converge down mode. */
    CONVERGE_DOWN,
    /** Diverge up mode. */
    DIVERGE_UP,
    /** Diverge down mode. */
    DIVERGE_DOWN,
    /** Thumb up mode. */
    THUMB_UP,
    /** Thumb down mode. */
    THUMB_DOWN,
    /** Pikny up mode. */
    PINKY_UP,
    /** Pinky down mode. */
    PINKY_DOWN;


    private static final ArpeggiatorMode []           ARP_MODES;
    private static final Map<String, ArpeggiatorMode> ARP_MODE_LOOKUP = new HashMap<> ();
    static
    {
        final NoteRepeatModes [] values = NoteRepeatModes.values ();
        ARP_MODES = new ArpeggiatorMode [values.length];
        for (int i = 0; i < values.length; i++)
        {
            final String name = values[i].name ();
            ARP_MODES[i] = new ArpeggiatorMode (i, name.substring (0, 1) + name.substring (1).toLowerCase ().replace ('_', ' '), name.toLowerCase ().replace ('_', '-'));
            ARP_MODE_LOOKUP.put (ARP_MODES[i].getValue (), ARP_MODES[i]);
        }
    }


    /**
     * Get all Bitwig modes.
     *
     * @return The formatted names
     */
    public static ArpeggiatorMode [] getArpeggiatorModes ()
    {
        return ARP_MODES;
    }


    /**
     * Lookup an arpeggiator mode.
     *
     * @param value The Bitwig value of the mode
     * @return The mode
     */
    public static ArpeggiatorMode lookup (final String value)
    {
        return ARP_MODE_LOOKUP.get (value);
    }
}
