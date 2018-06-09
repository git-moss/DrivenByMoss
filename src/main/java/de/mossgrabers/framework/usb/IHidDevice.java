// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.usb;

/**
 * Interface to a HID device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IHidDevice
{
    /**
     * Set the callback function to reveive input.
     *
     * @param callback The callback
     */
    void setCallback (final IHidCallback callback);
}
