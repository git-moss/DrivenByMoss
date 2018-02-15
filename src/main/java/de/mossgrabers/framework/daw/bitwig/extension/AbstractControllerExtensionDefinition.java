// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig.extension;

import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Some reoccurring functions for the extension definition.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractControllerExtensionDefinition extends ControllerExtensionDefinition
{
    /** {@inheritDoc} */
    @Override
    public String getAuthor ()
    {
        return "Jürgen Moßgraber";
    }


    /** {@inheritDoc} */
    @Override
    public int getRequiredAPIVersion ()
    {
        return 5;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumMidiInPorts ()
    {
        return 1;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumMidiOutPorts ()
    {
        return 1;
    }


    /** {@inheritDoc} */
    @Override
    public void listAutoDetectionMidiPortNames (final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public ControllerExtension createInstance (final ControllerHost host)
    {
        return new GenericControllerExtension (this.getControllerSetup (host), this, host);
    }


    /**
     * Get the controller setup for this extension.
     *
     * @param host The host
     * @return The controller setup
     */
    protected abstract IControllerSetup getControllerSetup (final ControllerHost host);


    /**
     * Creates 60 variations from the given device name for auto lookup.
     *
     * @param deviceName The base name to use
     * @param list The list where to add the generated names
     */
    protected void createDeviceDiscoveryPairs (final String deviceName, final AutoDetectionMidiPortNamesList list)
    {
        this.addDeviceDiscoveryPair (deviceName, list);
        for (int i = 1; i < 20; i++)
        {
            this.addDeviceDiscoveryPair (i + "- " + deviceName, list);
            this.addDeviceDiscoveryPair (deviceName + " MIDI " + i, list);
            this.addDeviceDiscoveryPair (deviceName + " " + i + " MIDI 1", list);
        }
    }


    /**
     * Adds a discovery pair to the auto detection with the same name for input and output port.
     *
     * @param name The name to look for
     * @param list The auto detection list to add the name
     */
    protected void addDeviceDiscoveryPair (final String name, final AutoDetectionMidiPortNamesList list)
    {
        list.add (new String []
        {
            name
        }, new String []
        {
            name
        });
    }


    /**
     * Adds a discovery pair to the auto detection.
     *
     * @param nameIn The name to use for the input port
     * @param nameOut The name to use for the output port
     * @param list The auto detection list to add the name
     */
    protected void addDeviceDiscoveryPair (final String nameIn, final String nameOut, final AutoDetectionMidiPortNamesList list)
    {
        list.add (new String []
        {
            nameIn
        }, new String []
        {
            nameOut
        });
    }
}
