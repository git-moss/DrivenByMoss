// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.extension;

import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.framework.usb.USBMatcher;
import de.mossgrabers.framework.usb.USBMatcher.EndpointMatcher;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.HardwareDeviceMatcherList;
import com.bitwig.extension.controller.UsbDeviceMatcher;
import com.bitwig.extension.controller.UsbEndpointMatcher;
import com.bitwig.extension.controller.UsbInterfaceMatcher;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.UsbTransferType;

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
        // Return the identical name to prevent a very long text in the selection menu
        return this.getHardwareModel ();
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
        return 7;
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
    public void listHardwareDevices (final HardwareDeviceMatcherList matchers)
    {
        final USBMatcher matcher = this.definition.claimUSBDevice ();
        if (matcher == null)
            return;

        for (final EndpointMatcher endpoint: matcher.getEndpoints ())
        {
            final byte [] addresses = endpoint.getEndpointAddresses ();
            final boolean [] isBulk = endpoint.getEndpointIsBulk ();
            final UsbEndpointMatcher [] endpointMatchers = new UsbEndpointMatcher [addresses.length];
            for (int i = 0; i < addresses.length; i++)
                endpointMatchers[i] = createEndpointMatcher (addresses[i], isBulk[i]);

            final String name = this.getHardwareVendor () + " " + this.getHardwareModel ();
            matchers.add (createDeviceMatcher (name, matcher.getVendor (), matcher.getProductID (), endpoint.getInterfaceNumber (), endpointMatchers));
        }
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


    private static UsbEndpointMatcher createEndpointMatcher (final byte endpoint, final boolean isBulk)
    {
        return new UsbEndpointMatcher (isBulk ? UsbTransferType.BULK : UsbTransferType.INTERRUPT, endpoint);
    }


    private static UsbDeviceMatcher createDeviceMatcher (final String name, final short vendor, final short productID, final byte interfaceNumber, final UsbEndpointMatcher... endpointMatcher)
    {
        final String interfaceExpression = "bInterfaceNumber == 0x" + StringUtils.toHexStr (Byte.toUnsignedInt (interfaceNumber));
        final String expression = "idVendor == 0x" + StringUtils.toHexStr (vendor) + " && idProduct == 0x" + StringUtils.toHexStr (productID);
        return new UsbDeviceMatcher (name, expression, new UsbInterfaceMatcher (interfaceExpression, endpointMatcher));
    }
}
