// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.IDevice;


/**
 * Interface to the Cursor device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ICursorDevice extends IDevice
{
    /**
     * Start the browser to replace a device.
     */
    void browseToReplaceDevice ();


    /**
     * Start the browser to insert a new device before the current one.
     */
    void browseToInsertBeforeDevice ();


    /**
     * Start the browser to insert a new device after the current one.
     */
    void browseToInsertAfterDevice ();


    /**
     * Select the parent of the device.
     */
    void selectParent ();


    /**
     * Select the channel which hosts the device.
     */
    void selectChannel ();


    /**
     * Returns true if the cursor device is enabled.
     *
     * @return True if the cursor device is enabled
     */
    boolean isEnabled ();


    /**
     * Returns true if the cursor device is a non-DAW plugin.
     *
     * @return True if the cursor device is a non-DAW plugin
     */
    boolean isPlugin ();


    /**
     * Get the position of the device in the chain.
     *
     * @return The position of the device in the chain.
     */
    int getPositionInChain ();


    /**
     * Get the position of the device in the bank page.
     *
     * @return The position of the device in the bank page.
     */
    int getPositionInBank ();


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
     * Is the device expanded?
     *
     * @return True if the device is expanded
     */
    boolean isExpanded ();


    /**
     * Is the remote control section of the device expanded?
     *
     * @return True if the remote control section of the device is expanded
     */
    boolean isParameterPageSectionVisible ();


    /**
     * Is the device window open?
     *
     * @return True if the device window is open
     */
    boolean isWindowOpen ();


    /**
     * Is the device nested?
     *
     * @return True if the device is nested into another device
     */
    boolean isNested ();


    /**
     * Does the device have drum pads?
     *
     * @return True if the device has drum pads
     */
    boolean hasDrumPads ();


    /**
     * Does the device support layers? Might still have no layer.
     *
     * @return True if the device has layers
     */
    boolean hasLayers ();


    /**
     * Does the device have slots?
     *
     * @return True if the device has slots
     */
    boolean hasSlots ();


    /**
     * Get if the cursor device is pinned.
     *
     * @return True if pinned
     */
    boolean isPinned ();


    /**
     * Toggles if the cursor device is pinned.
     */
    void togglePinned ();


    /**
     * Toggle the device on/off.
     */
    void toggleEnabledState ();


    /**
     * Toggle the window of an external device on/off.
     */
    void toggleWindowOpen ();


    /**
     * Select the previous device (if any).
     */
    void selectPrevious ();


    /**
     * Select the next device (if any).
     */
    void selectNext ();


    /**
     * Is there a current device?
     *
     * @return True if there is a device
     */
    boolean hasSelectedDevice ();


    /**
     * Toggle the expanded state of the device.
     */
    void toggleExpanded ();


    /**
     * Toggle the parameter section visibility state of the device.
     */
    void toggleParameterPageSectionVisible ();


    /**
     * Get the device sibling bank.
     *
     * @return The bank
     */
    IDeviceBank getDeviceBank ();


    /**
     * Get the parameter page bank.
     *
     * @return The bank
     */
    IParameterPageBank getParameterPageBank ();


    /**
     * Get the parameter bank.
     *
     * @return The bank
     */
    IParameterBank getParameterBank ();


    /**
     * Get the layer bank.
     *
     * @return The bank
     */
    ILayerBank getLayerBank ();


    /**
     * Get the drum pad bank.
     *
     * @return The bank
     */
    IDrumPadBank getDrumPadBank ();


    /**
     * Get the layer or drum pad bank whatever is present for a device.
     *
     * @return The bank
     */
    IChannelBank<?> getLayerOrDrumPadBank ();
}