// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

/**
 * Runtime exception to block use of wrong execution methods.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LatestTaskException extends RuntimeException
{
    private static final long serialVersionUID = 8072517378022859167L;


    /**
     * Constructor.
     */
    public LatestTaskException ()
    {
        super ("Use execute instead.");
    }
}
