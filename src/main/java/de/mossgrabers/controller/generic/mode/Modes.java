// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.mode;

/**
 * Static mode IDs and some helper functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Modes
{
    /** Edit volume, pan and sends of the selected track. */
    public static final Integer MODE_TRACK  = Integer.valueOf (0);
    /** Edit volume of all tracks. */
    public static final Integer MODE_VOLUME = Integer.valueOf (1);
    /** Edit panorama of all tracks. */
    public static final Integer MODE_PAN    = Integer.valueOf (2);
    /** Edit Send 1 of all tracks. */
    public static final Integer MODE_SEND1  = Integer.valueOf (3);
    /** Edit Send 2 of all tracks. */
    public static final Integer MODE_SEND2  = Integer.valueOf (4);
    /** Edit Send 3 of all tracks. */
    public static final Integer MODE_SEND3  = Integer.valueOf (5);
    /** Edit Send 4 of all tracks. */
    public static final Integer MODE_SEND4  = Integer.valueOf (6);
    /** Edit Send 5 of all tracks. */
    public static final Integer MODE_SEND5  = Integer.valueOf (7);
    /** Edit Send 6 of all tracks. */
    public static final Integer MODE_SEND6  = Integer.valueOf (8);
    /** Edit Send 7 of all tracks. */
    public static final Integer MODE_SEND7  = Integer.valueOf (9);
    /** Edit Send 8 of all tracks. */
    public static final Integer MODE_SEND8  = Integer.valueOf (10);
    /** Device mode. */
    public static final Integer MODE_DEVICE = Integer.valueOf (11);
    /** Browser mode. */
    public static final Integer MODE_BROWSE = Integer.valueOf (12);


    /**
     * Private due to utility class.
     */
    private Modes ()
    {
        // Intentionally empty
    }
}
