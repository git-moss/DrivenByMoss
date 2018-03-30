// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import java.util.UUID;


/**
 * Interface for the description of a hardware control surface.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IControllerDefinition
{
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
     * @return The version
     */
    String getVersion ();


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
}
