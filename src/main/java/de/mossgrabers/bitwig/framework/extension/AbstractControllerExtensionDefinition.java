// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.extension;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.UsbDeviceInfo;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.List;
import java.util.UUID;


/**
 * Some reoccurring functions for the extension definition.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractControllerExtensionDefinition extends ControllerExtensionDefinition
{
    private final IControllerDefinition definition;


    /**
     * Constructor.
     *
     * @param definition The definition
     */
    public AbstractControllerExtensionDefinition (final IControllerDefinition definition)
    {
        this.definition = definition;
    }


    /** {@inheritDoc} */
    @Override
    public String getAuthor ()
    {
        return this.definition.getAuthor ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.definition.getName ();
    }


    /** {@inheritDoc} */
    @Override
    public String getVersion ()
    {
        return this.definition.getVersion ();
    }


    /** {@inheritDoc} */
    @Override
    public UUID getId ()
    {
        return this.definition.getId ();
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareVendor ()
    {
        return this.definition.getHardwareVendor ();
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return this.definition.getHardwareModel ();
    }


    /** {@inheritDoc} */
    @Override
    public int getRequiredAPIVersion ()
    {
        return 6;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumMidiInPorts ()
    {
        return this.definition.getNumMidiInPorts ();
    }


    /** {@inheritDoc} */
    @Override
    public int getNumMidiOutPorts ()
    {
        return this.definition.getNumMidiOutPorts ();
    }


    /** {@inheritDoc} */
    @Override
    public void listAutoDetectionMidiPortNames (final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
    {
        final OperatingSystem os = OperatingSystem.valueOf (platformType.name ().toUpperCase ());
        for (final Pair<String [], String []> midiDiscoveryPair: this.definition.getMidiDiscoveryPairs (os))
            list.add (midiDiscoveryPair.getKey (), midiDiscoveryPair.getValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void listUsbEndpoints (final List<UsbDeviceInfo> endpoints)
    {
        final Pair<Short, Short> claimUSBDevice = this.definition.claimUSBDevice ();
        if (claimUSBDevice != null)
            endpoints.add (new UsbDeviceInfo (claimUSBDevice.getKey ().shortValue (), claimUSBDevice.getValue ().shortValue ()));
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
}
