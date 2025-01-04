// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.controller;

import java.util.List;
import java.util.UUID;

import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;


/**
 * Interface for describing several devices, which support a specific version of the NIHIA protocol.
 *
 * @author Jürgen Moßgraber
 */
public interface IKontrolProtocolDeviceDescriptor
{
    /**
     * Get the ID to use for the device extension.
     *
     * @return The ID
     */
    UUID getID ();


    /**
     * Get the display name for the device extension.
     *
     * @return The name
     */
    String getName ();


    /**
     * Get the MIDI port discovery pairs depending on the operating system.
     *
     * @param operatingSystem The operating system
     * @return The discovery pairs
     */
    List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem operatingSystem);
}
