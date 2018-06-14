// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.usb;

import java.util.ArrayList;
import java.util.List;


/**
 * Contains the description for a USB device to claim and the interfaces/endpoints, which are
 * intended to be used.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UsbMatcher
{
    private final short                 vendor;
    private final short                 productID;
    private final List<EndpointMatcher> endpoints = new ArrayList<> ();
    private final boolean               enableHID;


    /**
     * Constructor for an USB device matcher.
     *
     * @param vendor The vendor ID
     * @param productID The product ID
     * @param enableHID Set to true to use as a HID device
     */
    public UsbMatcher (final short vendor, final short productID, final boolean enableHID)
    {
        this.vendor = vendor;
        this.productID = productID;
        // TODO Remove if HID works on Linux
        this.enableHID = enableHID;
    }


    /**
     * Constructor for an USB device matcher with 1 interface and 1 endpoint.
     *
     * @param vendor The vendor ID
     * @param productID The product ID
     * @param interfaceNumber The interface
     * @param endpointAddress The endpoint
     * @param isBulk True to use bulk otherwise interrupted
     * @param enableHID Set to true to use as a HID device
     */
    public UsbMatcher (final short vendor, final short productID, final byte interfaceNumber, final byte endpointAddress, final boolean isBulk, final boolean enableHID)
    {
        this (vendor, productID, interfaceNumber, new byte []
        {
            endpointAddress
        }, new boolean []
        {
            isBulk
        }, enableHID);
    }


    /**
     * Constructor for an USB device matcher with 1 interface and multiple endpoints.
     *
     * @param vendor The vendor ID
     * @param productID The product ID
     * @param interfaceNumber The interface
     * @param endpointAddresses The endpoints
     * @param isBulk True to use bulk otherwise interrupted
     * @param enableHID Set to true to use as a HID device
     */
    public UsbMatcher (final short vendor, final short productID, final byte interfaceNumber, final byte [] endpointAddresses, final boolean [] isBulk, final boolean enableHID)
    {
        this (vendor, productID, enableHID);
        this.addEndpoints (interfaceNumber, endpointAddresses, isBulk);
    }


    /**
     * Get the vendor ID of the USB device
     *
     * @return The vendor ID
     */
    public short getVendor ()
    {
        return this.vendor;
    }


    /**
     * Get the product ID of the USB device
     *
     * @return The product ID
     */
    public short getProductID ()
    {
        return this.productID;
    }


    /**
     * Returns true to use as a HID device.
     *
     * @return True to use as a HID device
     */
    public boolean isEnableHID ()
    {
        return this.enableHID;
    }


    /**
     * Add multiple endpoints on an interface.
     *
     * @param interfaceNumber The interface
     * @param endpointAddresses The endpoints on the interface
     * @param isBulk True to use bulk otherwise interrupted
     */
    public final void addEndpoints (final byte interfaceNumber, final byte [] endpointAddresses, final boolean [] isBulk)
    {
        this.endpoints.add (new EndpointMatcher (interfaceNumber, endpointAddresses, isBulk));
    }


    /**
     * Add an endpoint.
     *
     * @param interfaceNumber The interface
     * @param endpointAddress The endpoint on the interface
     * @param isBulk True to use bulk otherwise interrupted
     */
    public final void addEndpoint (final byte interfaceNumber, final byte endpointAddress, final boolean isBulk)
    {
        this.endpoints.add (new EndpointMatcher (interfaceNumber, new byte []
        {
            endpointAddress
        }, new boolean []
        {
            isBulk
        }));
    }


    /**
     * Get the configured endpoints.
     *
     * @return The endpoints
     */
    public List<EndpointMatcher> getEndpoints ()
    {
        return new ArrayList<> (this.endpoints);
    }

    /**
     * One or more endpoints on the USB device.
     */
    public class EndpointMatcher
    {
        private final byte       interfaceNumber;
        private final byte []    endpointAddresses;
        private final boolean [] isBulk;


        /**
         * Constructor.
         *
         * @param interfaceNumber The interface number
         * @param endpointAddresses The addresses of the endpoints
         * @param isBulk True to use bulk transfer otherwise interrupted
         */
        public EndpointMatcher (final byte interfaceNumber, final byte [] endpointAddresses, final boolean [] isBulk)
        {
            this.interfaceNumber = interfaceNumber;
            this.endpointAddresses = endpointAddresses;
            this.isBulk = isBulk;
        }


        /**
         * Get the interface number.
         *
         * @return The interface number
         */
        public byte getInterfaceNumber ()
        {
            return this.interfaceNumber;
        }


        /**
         * Get the endpoint addresses of the interface.
         *
         * @return The endpoint addresses
         */
        public byte [] getEndpointAddresses ()
        {
            return this.endpointAddresses;
        }


        /**
         * Get the transport mode.
         *
         * @return True if bulk otherwise interrupted
         */
        public boolean [] getEndpointIsBulk ()
        {
            return this.isBulk;
        }
    }
}
