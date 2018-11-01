// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.mode;

import java.util.HashSet;
import java.util.Set;


/**
 * Static mode IDs and some helper functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Modes
{
    /** Edit volume of all tracks. */
    public static final Integer       MODE_VOLUME   = Integer.valueOf (0);
    /** Edit panorama of all tracks. */
    public static final Integer       MODE_PAN      = Integer.valueOf (1);
    /** Edit Send 1 of all tracks. */
    public static final Integer       MODE_SEND1    = Integer.valueOf (2);
    /** Edit Send 2 of all tracks. */
    public static final Integer       MODE_SEND2    = Integer.valueOf (3);
    /** Edit Send 3 of all tracks. */
    public static final Integer       MODE_SEND3    = Integer.valueOf (4);
    /** Edit Send 4 of all tracks. */
    public static final Integer       MODE_SEND4    = Integer.valueOf (5);
    /** Edit Send 5 of all tracks. */
    public static final Integer       MODE_SEND5    = Integer.valueOf (6);
    /** Edit Send 6 of all tracks. */
    public static final Integer       MODE_SEND6    = Integer.valueOf (7);
    /** Edit Send 7 of all tracks. */
    public static final Integer       MODE_SEND7    = Integer.valueOf (8);
    /** Edit Send 8 of all tracks. */
    public static final Integer       MODE_SEND8    = Integer.valueOf (9);
    /** Device mode. */
    public static final Integer       MODE_DEVICE   = Integer.valueOf (10);
    /** Play cursor position mode. */
    public static final Integer       MODE_POSITION = Integer.valueOf (11);
    /** Tempo mode. */
    public static final Integer       MODE_TEMPO    = Integer.valueOf (12);
    /** Browser mode. */
    public static final Integer       MODE_BROWSE   = Integer.valueOf (13);

    private static final Set<Integer> SEND_MODES    = new HashSet<> ();

    static
    {
        SEND_MODES.add (MODE_SEND1);
        SEND_MODES.add (MODE_SEND2);
        SEND_MODES.add (MODE_SEND3);
        SEND_MODES.add (MODE_SEND4);
        SEND_MODES.add (MODE_SEND5);
        SEND_MODES.add (MODE_SEND6);
        SEND_MODES.add (MODE_SEND7);
        SEND_MODES.add (MODE_SEND8);
    }


    /**
     * Private due to utility class.
     */
    private Modes ()
    {
        // Intentionally empty
    }


    /**
     * Returns true if the given mode ID is one of the send modes.
     *
     * @param modeId The mode ID to test
     * @return True if it is a send mode
     */
    public static boolean isSendMode (final Integer modeId)
    {
        return SEND_MODES.contains (modeId);
    }
}
