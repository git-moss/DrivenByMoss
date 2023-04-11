// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Default implementation for a controller definition, which gets all of its settings from the
 * constructor parameters.
 *
 * @author Jürgen Moßgraber
 */
public abstract class DefaultControllerDefinition implements IControllerDefinition
{
    private final String name;
    private final String author;
    private final UUID   uuid;
    private final String hardwareModel;
    private final String hardwareVendor;
    private final int    numMidiInPorts;
    private final int    numMidiOutPorts;


    /**
     * Constructor.
     *
     * @param uuid The UUID of the controller implementation
     * @param hardwareModel The hardware model which this controller implementation supports
     * @param hardwareVendor The hardware vendor of the controller
     * @param numMidiInPorts The number of required MIDI in ports
     * @param numMidiOutPorts The number of required MIDI out ports
     */
    protected DefaultControllerDefinition (final UUID uuid, final String hardwareModel, final String hardwareVendor, final int numMidiInPorts, final int numMidiOutPorts)
    {
        this.name = "";
        this.author = "Jürgen Moßgraber";
        this.uuid = uuid;
        this.hardwareModel = hardwareModel;
        this.hardwareVendor = hardwareVendor;
        this.numMidiInPorts = numMidiInPorts;
        this.numMidiOutPorts = numMidiOutPorts;
    }


    /** {@inheritDoc} */
    @Override
    public UUID getUUID ()
    {
        return this.uuid;
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
    public String getVersion (final Package pckg)
    {
        return pckg == null ? "1.0" : pckg.getImplementationVersion ();
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


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        return new ArrayList<> ();
    }


    /** {@inheritDoc} */
    @Override
    public UsbMatcher claimUSBDevice ()
    {
        return null;
    }


    /**
     * Creates 60 variations from the given device name for auto lookup.
     *
     * @param deviceName The base name to use
     * @return The created pairs
     */
    protected List<Pair<String [], String []>> createDeviceDiscoveryPairs (final String deviceName)
    {
        final String namePattern = "%s" + deviceName;
        final List<Pair<String [], String []>> results = this.createWindowsDeviceDiscoveryPairs (namePattern, namePattern);
        for (int i = 1; i < 20; i++)
        {
            results.add (this.addDeviceDiscoveryPair (deviceName + " MIDI " + i));
            results.add (this.addDeviceDiscoveryPair (deviceName + " " + i + " MIDI 1"));
        }
        return results;
    }


    /**
     * Creates 20 Windows variations from the given device name for auto lookup.
     *
     * @param inputNamePattern The base name to use, must contain a %s
     * @param outputNamePattern The base name to use, must contain a %s
     * @return The created pairs
     */
    protected List<Pair<String [], String []>> createWindowsDeviceDiscoveryPairs (final String inputNamePattern, final String outputNamePattern)
    {
        final List<Pair<String [], String []>> results = new ArrayList<> ();
        results.add (this.addDeviceDiscoveryPair (String.format (inputNamePattern, ""), String.format (outputNamePattern, "")));
        for (int i = 1; i < 20; i++)
            results.add (this.addDeviceDiscoveryPair (String.format (inputNamePattern, i + "- "), String.format (outputNamePattern, i + "- ")));
        return results;
    }


    /**
     * Creates 20 Linux variations from the given device name for auto lookup. It appends '
     * [hw:x,0,p]' to the given input and output names and port.
     *
     * @param inputName The base name to use
     * @param outputName The base name to use
     * @return The created pairs
     */
    protected List<Pair<String [], String []>> createLinuxDeviceDiscoveryPairs (final String inputName, final String outputName)
    {
        return this.createLinuxDeviceDiscoveryPairs (inputName, outputName, 0);
    }


    /**
     * Creates 20 Linux variations from the given device name for auto lookup. It appends '
     * [hw:x,0,0]' to the given input and output names.
     *
     * @param inputName The base name to use
     * @param outputName The base name to use
     * @param port The port
     * @return The created pairs
     */
    protected List<Pair<String [], String []>> createLinuxDeviceDiscoveryPairs (final String inputName, final String outputName, final int port)
    {
        final List<Pair<String [], String []>> results = new ArrayList<> ();
        results.add (this.addDeviceDiscoveryPair (inputName, outputName));
        for (int i = 1; i < 20; i++)
            results.add (this.addDeviceDiscoveryPair (String.format ("%s [hw:%d,0,%d]", inputName, Integer.valueOf (i), Integer.valueOf (port)), String.format ("%s [hw:%d,0,%d]", outputName, Integer.valueOf (i), Integer.valueOf (port))));
        return results;
    }


    /**
     * Adds a MIDI discovery pair to the auto detection with the same name for input and output
     * port.
     *
     * @param name The name to look for
     * @return The created pair
     */
    protected Pair<String [], String []> addDeviceDiscoveryPair (final String name)
    {
        return this.addDeviceDiscoveryPair (name, name);
    }


    /**
     * Adds a discovery pair to the auto detection for MIDI inputs and outputs.
     *
     * @param nameIn The name to use for the input port, may be null
     * @param nameOut The name to use for the output port, may be null
     * @return The created pair
     */
    protected Pair<String [], String []> addDeviceDiscoveryPair (final String nameIn, final String nameOut)
    {
        return this.addDeviceDiscoveryPair (nameIn == null ? new String [0] : new String []
        {
            nameIn
        }, nameOut == null ? new String [0] : new String []
        {
            nameOut
        });
    }


    /**
     * Adds a MIDI discovery pair to the auto detection with the same name for input and output
     * port.
     *
     * @param ins The input names to look for
     * @param outs The output names to look for
     * @return The created pair
     */
    protected Pair<String [], String []> addDeviceDiscoveryPair (final String [] ins, final String [] outs)
    {
        return new Pair<> (ins, outs);
    }


    /** {@inheritDoc} */
    @Override
    public String toString ()
    {
        return new StringBuilder (this.getHardwareVendor ()).append (' ').append (this.getHardwareModel ()).toString ();
    }
}
