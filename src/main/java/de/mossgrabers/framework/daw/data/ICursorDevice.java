// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.data.bank.IDeviceBank;


/**
 * Interface to the Cursor device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ICursorDevice extends ISpecificDevice, IPinnable
{
    /**
     * Select the parent of the device.
     */
    void selectParent ();


    /**
     * Select the channel which hosts the device.
     */
    void selectChannel ();


    /**
     * Is there a previous device?
     *
     * @return True if there is a previous device
     */
    boolean canSelectPreviousFX ();


    /**
     * Is there a next device?
     *
     * @return True if there is a next device
     */
    boolean canSelectNextFX ();


    /**
     * Select the previous device (if any).
     */
    void selectPrevious ();


    /**
     * Select the next device (if any).
     */
    void selectNext ();


    /**
     * Get the device sibling bank.
     *
     * @return The bank
     */
    IDeviceBank getDeviceBank ();


    /**
     * Get the names of slot chains of a device.
     *
     * @return The names or an empty array
     */
    String [] getSlotChains ();


    /**
     * Select the first device of a slot chain.
     *
     * @param slotChainName One of the slot chain names retrieved with {@link #getSlotChains()}.
     */
    void selectSlotChain (String slotChainName);
}