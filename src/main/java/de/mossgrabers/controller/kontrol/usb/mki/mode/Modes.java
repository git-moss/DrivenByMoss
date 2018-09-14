// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mki.mode;

/**
 * Static mode IDs and some helper functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Modes
{
    /** Single Track editing mode. */
    public static final Integer MODE_TRACK   = Integer.valueOf (1);
    /** Edit volume of all tracks. */
    public static final Integer MODE_VOLUME  = Integer.valueOf (2);
    /** Edit device parameters. */
    public static final Integer MODE_PARAMS  = Integer.valueOf (3);
    /** The preset browser. */
    public static final Integer MODE_BROWSER = Integer.valueOf (4);
    /** Mode to configure the scale. */
    public static final Integer MODE_SCALE   = Integer.valueOf (5);


    /**
     * Private due to utility class.
     */
    private Modes ()
    {
        // Intentionally empty
    }
}
