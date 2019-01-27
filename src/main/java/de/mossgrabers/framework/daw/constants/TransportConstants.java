// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Constants for the transport.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportConstants
{
    /** The names for automation modes. */
    public static final List<String> AUTOMATION_MODES        = Collections.unmodifiableList (Arrays.asList ("Latch", "Touch", "Write"));

    /** The names for automation modes values. */
    public static final String []    AUTOMATION_MODES_VALUES =
    {
        "latch",
        "touch",
        "write"
    };

    /** No preroll. */
    public static final String       PREROLL_NONE            = "none";
    /** 1 bar preroll. */
    public static final String       PREROLL_1_BAR           = "one_bar";
    /** 2 bar preroll. */
    public static final String       PREROLL_2_BARS          = "two_bars";
    /** 4 bar preroll. */
    public static final String       PREROLL_4_BARS          = "four_bars";


    /**
     * Helper class constructor.
     */
    private TransportConstants ()
    {
        // Intentionally empty
    }
}
