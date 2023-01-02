// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.extension;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.IControllerDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.framework.usb.UsbMatcher;
import de.mossgrabers.framework.usb.UsbMatcher.EndpointMatcher;
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

import java.util.List;
import java.util.Locale;
import java.util.UUID;


/**
 * Some reoccurring functions for the extension definition.
 *
 * @param <C> The type of the configuration
 * @param <S> The type of the control surface
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractControllerExtensionDefinition<S extends IControlSurface<C>, C extends Configuration> extends ControllerExtensionDefinition
{
    private final IControllerDefinition definition;


    /**
     * Constructor.
     *
     * @param definition The definition
     */
    protected AbstractControllerExtensionDefinition (final IControllerDefinition definition)
    {
        this.definition = definition;
    }


    /** {@inheritDoc} */
    @Override
    public UUID getId ()
    {
        return this.definition.getId ();
    }


    /** {@inheritDoc} */
    @Override
    public String getVersion ()
    {
        final ClassLoader l = this.getClass ().getClassLoader ();
        return this.definition.getVersion (l.getDefinedPackage ("de.mossgrabers.framework.daw"));
    }


    /** {@inheritDoc} */
    @Override
    public String getHelpFilePath ()
    {
        return "DrivenByMoss-Manual.pdf";
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
        return 17;
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
        final OperatingSystem os = OperatingSystem.valueOf (platformType.name ().toUpperCase (Locale.US));
        for (final Pair<String [], String []> midiDiscoveryPair: this.definition.getMidiDiscoveryPairs (os))
            list.add (midiDiscoveryPair.getKey (), midiDiscoveryPair.getValue ());
    }


    /** {@inheritDoc} */
    @Override
    public void listHardwareDevices (final HardwareDeviceMatcherList matchers)
    {
        // Are there any USB devices configured?
        final UsbMatcher matcher = this.definition.claimUSBDevice ();
        if (matcher == null)
            return;

        final List<EndpointMatcher> endpoints = matcher.getEndpoints ();
        final int size = endpoints.size ();
        final UsbInterfaceMatcher [] interfaceMatchers = new UsbInterfaceMatcher [size];
        for (int i = 0; i < size; i++)
        {
            final EndpointMatcher endpoint = endpoints.get (i);
            final byte [] addresses = endpoint.getEndpointAddresses ();
            final boolean [] isBulk = endpoint.getEndpointIsBulk ();
            final UsbEndpointMatcher [] endpointMatchers = new UsbEndpointMatcher [addresses.length];
            for (int j = 0; j < addresses.length; j++)
                endpointMatchers[j] = createEndpointMatcher (addresses[j], isBulk[j]);

            interfaceMatchers[i] = createInterfaceMatcher (endpoint.getInterfaceNumber (), endpointMatchers);
        }
        final String name = this.getHardwareVendor () + " " + this.getHardwareModel ();
        matchers.add (createDeviceMatcher (name, matcher.getVendor (), matcher.getProductID (), interfaceMatchers));
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
    protected abstract IControllerSetup<S, C> getControllerSetup (final ControllerHost host);


    private static UsbDeviceMatcher createDeviceMatcher (final String name, final short vendor, final short productID, final UsbInterfaceMatcher... interfaceMatchers)
    {
        final String expression = "idVendor == 0x" + StringUtils.toHexStr (vendor) + " && idProduct == 0x" + StringUtils.toHexStr (productID);
        return new UsbDeviceMatcher (name, expression, interfaceMatchers);
    }


    private static UsbInterfaceMatcher createInterfaceMatcher (final byte interfaceNumber, final UsbEndpointMatcher... endpointMatcher)
    {
        final String interfaceExpression = "bInterfaceNumber == 0x" + StringUtils.toHexStr (Byte.toUnsignedInt (interfaceNumber));
        return new UsbInterfaceMatcher (interfaceExpression, endpointMatcher);
    }


    private static UsbEndpointMatcher createEndpointMatcher (final byte endpoint, final boolean isBulk)
    {
        return new UsbEndpointMatcher (isBulk ? UsbTransferType.BULK : UsbTransferType.INTERRUPT, endpoint);
    }
}
