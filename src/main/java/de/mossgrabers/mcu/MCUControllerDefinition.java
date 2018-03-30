// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Mackie MCU protocol extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUControllerDefinition extends DefaultControllerDefinition
{
    /**
     * Constructor.
     *
     * @param uuid The unique identifier
     * @param hardwareModel The hardware model
     * @param numMCUDevices The number of supported devices (Master + Extenders)
     */
    public MCUControllerDefinition (final UUID uuid, final String hardwareModel, final int numMCUDevices)
    {
        super ("MCU4Bitwig", "Jürgen Moßgraber", "2.61", uuid, hardwareModel, "Mackie", numMCUDevices, numMCUDevices);
    }
}
