// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import java.util.UUID;


/**
 * Default implementation for a controller definition, which gets all of its settings from the
 * constructor parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DefaultControllerDefinition implements IControllerDefinition
{
    private final String name;
    private final String author;
    private final String version;
    private final UUID   uuid;
    private final String hardwareModel;
    private final String hardwareVendor;
    private final int    numMidiInPorts;
    private final int    numMidiOutPorts;


    /**
     * Constructor.
     *
     * @param name The name of the controller implementation
     * @param author The author of the controller implementation
     * @param version The version of the controller implementation
     * @param uuid The UUID of the controller implementation
     * @param hardwareModel The hardware model which this controller implementation supports
     * @param hardwareVendor The hardware vendor of the controller
     * @param numMidiInPorts The number of required midi in ports
     * @param numMidiOutPorts The number of required midi out ports
     */
    public DefaultControllerDefinition (final String name, final String author, final String version, final UUID uuid, final String hardwareModel, final String hardwareVendor, final int numMidiInPorts, final int numMidiOutPorts)
    {
        this.name = name;
        this.author = author;
        this.version = version;
        this.uuid = uuid;
        this.hardwareModel = hardwareModel;
        this.hardwareVendor = hardwareVendor;
        this.numMidiInPorts = numMidiInPorts;
        this.numMidiOutPorts = numMidiOutPorts;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.name;
    }


    /** {@inheritDoc} */
    @Override
    public String getAuthor ()
    {
        return this.author;
    }


    /** {@inheritDoc} */
    @Override
    public String getVersion ()
    {
        return this.version;
    }


    /** {@inheritDoc} */
    @Override
    public UUID getId ()
    {
        return this.uuid;
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareVendor ()
    {
        return this.hardwareVendor;
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return this.hardwareModel;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumMidiInPorts ()
    {
        return this.numMidiInPorts;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumMidiOutPorts ()
    {
        return this.numMidiOutPorts;
    }
}
