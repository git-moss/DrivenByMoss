// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Novation SL controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1ControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID []   EXTENSION_ID   =
    {
        UUID.fromString ("457ef1d3-d197-4a94-a1d0-b4322ecbdd7d"),
        UUID.fromString ("90817073-0c11-41cf-8c56-f3334ec91fc4"),
        UUID.fromString ("99ff3646-3a65-47e5-a0e2-58c1c1799e93"),
        UUID.fromString ("18d5c565-f496-406d-8c3f-5af1004f61ff")
    };

    private static final String [] HARDWARE_MODEL =
    {
        "Komplete Kontrol S25",
        "Komplete Kontrol S49",
        "Komplete Kontrol S61",
        "Komplete Kontrol S88"
    };


    /**
     * Constructor.
     * 
     * @param modelIndex The index of the specific model (S25,
     */
    public Kontrol1ControllerDefinition (final int modelIndex)
    {
        super ("Kontrol14Bitwig", "Jürgen Moßgraber", "1.00", EXTENSION_ID[modelIndex], HARDWARE_MODEL[modelIndex], "Native Instruments", 1, 0);
    }
}
