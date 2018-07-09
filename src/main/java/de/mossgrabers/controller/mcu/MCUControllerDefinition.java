// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Mackie MCU protocol extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID []   EXTENSION_ID   =
    {
        UUID.fromString ("5F10A0CD-F866-41C0-B16A-AEA16282B657"),
        UUID.fromString ("7FF808DD-45DB-4026-AA6E-844ED8C05B55"),
        UUID.fromString ("8E0EDA26-ACB9-4F5E-94FB-B886C9468C7A"),
        UUID.fromString ("4923C47A-B1DC-48C8-AE89-A332AA26BA87")
    };

    private static final String [] HARDWARE_MODEL =
    {
        "Control Universal",
        "Control Universal + 1 Extender",
        "Control Universal + 2 Extenders",
        "Control Universal + 3 Extenders"
    };


    /**
     * Constructor.
     *
     * @param numMCUExtenders The number of supported Extenders
     */
    public MCUControllerDefinition (final int numMCUExtenders)
    {
        super ("", "Jürgen Moßgraber", "2.62", EXTENSION_ID[numMCUExtenders], HARDWARE_MODEL[numMCUExtenders], "Mackie", numMCUExtenders + 1, numMCUExtenders + 1);
    }
}
