// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.constants;

/**
 * Constants for the transport.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public final class TransportConstants
{
    /** 1 beat. */
    public static final double INC_FRACTION_TIME      = 1.0;
    /** 1/20th of a beat. */
    public static final double INC_FRACTION_TIME_SLOW = 1.0 / 16;

    /** The minimum tempo in BPM. */
    public static final int    MIN_TEMPO              = 20;
    /** The maximum tempo in BPM. */
    public static final int    MAX_TEMPO              = 666;


    /**
     * Helper class constructor.
     */
    private TransportConstants ()
    {
        // Intentionally empty
    }
}
