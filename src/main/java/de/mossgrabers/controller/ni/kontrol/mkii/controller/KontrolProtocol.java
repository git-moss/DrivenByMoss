// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.controller;

/**
 * Information about the Komplete Kontrol MIDI protocol.
 *
 * @author Jürgen Moßgraber
 */
public final class KontrolProtocol
{
    /** Protocol version 1. */
    public static final int VERSION_1   = 1;
    /** Protocol version 2. */
    public static final int VERSION_2   = 2;
    /** Protocol version 3. */
    public static final int VERSION_3   = 3;

    /** The maximal NIHIA protocol version which is supported by this extension. */
    public static final int MAX_VERSION = VERSION_3;


    /**
     * Constructor.
     */
    private KontrolProtocol ()
    {
        // Intentionally empty
    }
}
