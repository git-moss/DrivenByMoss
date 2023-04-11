// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Interface for the description of a hardware control surface.
 *
 * @author Jürgen Moßgraber
 */
public interface IControllerDefinition
{
    /**
     * Get the ID of the definition.
     *
     * @return The ID
     */
    UUID getUUID ();


    /**
     * The name of this controller implementation.
     *
     * @return The name
     */
    String getName ();


    /**
     * The author of this controller implementation.
     *
     * @return The author
     */
    String getAuthor ();


    /**
     * The version of this controller implementation.
     *
     * @param pckg The package which contains the controller definition
     * @return The version
     */
    String getVersion (Package pckg);


    /**
     * Get a unique identifier for this controller implementation.
     *
     * @return The UUID
     */
    UUID getId ();


    /**
     * The vendor of the controller that this controller implementation supports.
     *
     * @return The name
     */
    String getHardwareVendor ();


    /**
     * The model name of the controller that this controller implementation supports.
     *
     * @return The name
     */
    String getHardwareModel ();


    /**
     * The number of MIDI in ports that this controller extension requires.
     *
     * @return The positive number, might be 0
     */
    int getNumMidiInPorts ();


    /**
     * The number of MIDI out ports that this controller extension requires.
     *
     * @return The positive number, might be 0
     */
    int getNumMidiOutPorts ();


    /**
     * Get all MIDI input/output port discovery names.
     *
     * @param os The operating system. Use for different names on different platforms.
     * @return The names
     */
    List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os);


    /**
     * Returns an USB matcher to claim one or more end-points of an USB device.
     *
     * @return The matcher description
     */
    UsbMatcher claimUSBDevice ();
}
