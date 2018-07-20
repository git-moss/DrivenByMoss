// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.IParameter;


/**
 * Interface to the Cursor device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ICursorDevice extends ObserverManagement
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
     * Returns true if the cursor device exists.
     *
     * @return True if the cursor device exists
     */
    boolean doesExist ();


    /**
     * Returns true if the cursor device is enabled.
     *
     * @return True if the cursor device is enabled
     */
    boolean isEnabled ();


    /**
     * Get the name of the cursor device.
     *
     * @return The name
     */
    String getName ();


    /**
     * Get the name of the cursor device.
     *
     * @param limit Limit the text to this length
     * @return The name
     */
    String getName (final int limit);


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
     * Change a parameter.
     *
     * @param index The index of the parameter
     * @param control The control value
     */
    void changeParameter (int index, int control);


    /**
     * Set a parameter.
     *
     * @param index The index of the parameter
     * @param value The parameter
     */
    void setParameter (int index, int value);


    /**
     * Reset a parameter.
     *
     * @param index The index of the parameter
     */
    void resetParameter (int index);


    /**
     * Set parameter indication.
     *
     * @param index The index of the parameter
     * @param indicate True to enable indication
     */
    void indicateParameter (int index, boolean indicate);


    /**
     * Touch parameter indication.
     *
     * @param index The index of the parameter
     * @param indicate True to enable touch indication
     */
    void touchParameter (int index, boolean indicate);


    /**
     * Select the previous parameter page, cycles to the last page from the first.
     */
    void previousParameterPage ();


    /**
     * Select the next parameter page, cycles to the first page from the last.
     */
    void nextParameterPage ();


    /**
     * Returns true if there is a previous parameter page.
     *
     * @return True if there is a previous parameter page
     */
    boolean hasPreviousParameterPage ();


    /**
     * Returns true if there is a next parameter page.
     *
     * @return True if there is a next parameter page
     */
    boolean hasNextParameterPage ();


    /**
     * Get the names of the parameter pages.
     *
     * @return The names of the parameter pages
     */
    String [] getParameterPageNames ();


    /**
     * Get the name of the selected parameter page.
     *
     * @return The name of the selected parameter page
     */
    String getSelectedParameterPageName ();


    /**
     * Get the index of the selected parameter page.
     *
     * @return The index of the selected parameter page
     */
    int getSelectedParameterPage ();


    /**
     * Set the index of the selected parameter page.
     *
     * @param index The index of the selected parameter page
     */
    void setSelectedParameterPage (int index);


    /**
     * Set the index of the selected parameter page relative to the current bank.
     *
     * @param index The index of the selected parameter page
     */
    void setSelectedParameterPageInBank (int index);


    /**
     * Select the previous parameter page bank.
     */
    void previousParameterPageBank ();


    /**
     * Select the next parameter page bank.
     */
    void nextParameterPageBank ();


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
     * Get the parameter data.
     *
     * @param index The index of the parameter
     * @return The parameter data
     */
    IParameter getFXParam (int index);


    /**
     * Toggle the expanded state of the device.
     */
    void toggleExpanded ();


    /**
     * Toggle the parameter section visibility state of the device.
     */
    void toggleParameterPageSectionVisible ();


    /**
     * Get the layer or drum pad depending on the device.
     *
     * @param index The index
     * @return The layer or drum pad
     */
    IChannel getLayerOrDrumPad (int index);


    /**
     * Get the selected layer or drum pad depending on the device.
     *
     * @return The selected layer or drum pad
     */
    IChannel getSelectedLayerOrDrumPad ();


    /**
     * Select a layer or drum pad depending on the device.
     *
     * @param index The index
     */
    void selectLayerOrDrumPad (int index);


    /**
     * Select the previous layer or drum pad depending on the device, if any.
     */
    void previousLayerOrDrumPad ();


    /**
     * Select the next layer or drum pad depending on the device, if any.
     */
    void nextLayerOrDrumPad ();


    /**
     * Select the previous layer or drum pad bank page depending on the device, if any.
     */
    void previousLayerOrDrumPadBank ();


    /**
     * Select the next layer or drum pad bank page depending on the device, if any.
     */
    void nextLayerOrDrumPadBank ();


    /**
     * Enter a layer or drum pad depending on the device.
     *
     * @param index The index
     */
    void enterLayerOrDrumPad (int index);


    /**
     * Select the first device in a layer or drum pad depending on the device.
     *
     * @param index The index
     */
    void selectFirstDeviceInLayerOrDrumPad (int index);


    /**
     * Can the layers or drum pads scrolled up depending on the device.
     *
     * @return True if scrolling is possible
     */
    boolean canScrollLayersOrDrumPadsUp ();


    /**
     * Can the layers or drum pads scrolled down depending on the device.
     *
     * @return True if scrolling is possible
     */
    boolean canScrollLayersOrDrumPadsDown ();


    /**
     * Scroll the layers or drum pads up by one page depending on the device.
     */
    void scrollLayersOrDrumPadsPageUp ();


    /**
     * Scroll the layers or drum pads down by one page depending on the device.
     */
    void scrollLayersOrDrumPadsPageDown ();


    /**
     * Set the layer or drum pad color depending on the device.
     *
     * @param index The index
     * @param red The red
     * @param green The green
     * @param blue The blue
     */
    void setLayerOrDrumPadColor (int index, double red, double green, double blue);


    /**
     * Get the layer or drum pad color ID depending on the device.
     *
     * @param index The index
     * @return The ID
     */
    String getLayerOrDrumPadColorEntry (int index);


    /**
     * Change the layer or drum pad volume depending on the device.
     *
     * @param index The index
     * @param control The control value
     */
    void changeLayerOrDrumPadVolume (int index, int control);


    /**
     * Set the layer or drum pad volume depending on the device.
     *
     * @param index The index
     * @param value The value
     */
    void setLayerOrDrumPadVolume (int index, int value);


    /**
     * Reset the layer or drum pad volume to its default value depending on the device.
     *
     * @param index The index
     */
    void resetLayerOrDrumPadVolume (int index);


    /**
     * Signal touch to the layer or drum pad volume depending on the device.
     *
     * @param index The index
     * @param isBeingTouched True if touched
     */
    void touchLayerOrDrumPadVolume (int index, boolean isBeingTouched);


    /**
     * Change the layer or drum pad panorama depending on the device.
     *
     * @param index The index
     * @param control The control value
     */
    void changeLayerOrDrumPadPan (int index, int control);


    /**
     * Set the layer or drum pad panorama depending on the device.
     *
     * @param index The index
     * @param value The value
     */
    void setLayerOrDrumPadPan (int index, int value);


    /**
     * Reset the layer or drum pad panorama to its default value depending on the device.
     *
     * @param index The index
     */
    void resetLayerOrDrumPadPan (int index);


    /**
     * Signal touch to the layer or drum pad panorama depending on the device.
     *
     * @param index The index
     * @param isBeingTouched True if touched
     */
    void touchLayerOrDrumPadPan (int index, boolean isBeingTouched);


    /**
     * Change the layer or drum pad send volume depending on the device.
     *
     * @param index The index
     * @param send The index of the send
     * @param control The control value
     */
    void changeLayerOrDrumPadSend (int index, int send, int control);


    /**
     * Set the layer or drum pad send volume depending on the device.
     *
     * @param index The index
     * @param send The index of the send
     * @param value The value
     */
    void setLayerOrDrumPadSend (int index, int send, int value);


    /**
     * Reset the layer or drum pad send volume to its default value depending on the device.
     *
     * @param index The index
     * @param send The index of the send
     */
    void resetLayerOrDrumPadSend (int index, int send);


    /**
     * Signal touch to the layer or drum pad send volume depending on the device.
     *
     * @param index The index
     * @param send The index of the send
     * @param isBeingTouched True if touched
     */
    void touchLayerOrDrumPadSend (int index, int send, boolean isBeingTouched);


    /**
     * Toggle if the the layer or drum pad is active depending on the device.
     *
     * @param index The index
     */
    void toggleLayerOrDrumPadIsActivated (int index);


    /**
     * Toggle the the layer or drum pad mute depending on the device.
     *
     * @param index The index
     */
    void toggleLayerOrDrumPadMute (int index);


    /**
     * Set the layer or drum pad mute depending on the device.
     *
     * @param index The index
     * @param value The value
     */
    void setLayerOrDrumPadMute (int index, boolean value);


    /**
     * Toggle the layer or drum pad solo depending on the device.
     *
     * @param index The index
     */
    void toggleLayerOrDrumPadSolo (int index);


    /**
     * Set the layer or drum pad solo depending on the device.
     *
     * @param index The index
     * @param value The value
     */
    void setLayerOrDrumPadSolo (int index, boolean value);


    /**
     * Check if there is at least 1 existing layer.
     *
     * @return True if there are no layers
     */
    boolean hasZeroLayers ();


    /**
     * Get the layer.
     *
     * @param index The index
     * @return The layer
     */
    IChannel getLayer (int index);


    /**
     * Get the selected layer.
     *
     * @return The selected layer
     */
    IChannel getSelectedLayer ();


    /**
     * Select a layer.
     *
     * @param index The index
     */
    void selectLayer (int index);


    /**
     * Select the previous layer, if any.
     */
    void previousLayer ();


    /**
     * Select the next layer, if any.
     */
    void nextLayer ();


    /**
     * Select the previous layer bank page, if any.
     */
    void previousLayerBank ();


    /**
     * Select the next layer bank page, if any.
     */
    void nextLayerBank ();


    /**
     * Enter a layer.
     *
     * @param index The index
     */
    void enterLayer (int index);


    /**
     * Select the first device in a layer.
     *
     * @param index The index
     */
    void selectFirstDeviceInLayer (int index);


    /**
     * Can the layers be scrolled up?
     *
     * @return True if the layers can be scrolled up
     */
    boolean canScrollLayersUp ();


    /**
     * Can the layers be scrolled down?
     *
     * @return True if the layers can be scrolled down
     */
    boolean canScrollLayersDown ();


    /**
     * Scroll the layers up by one page.
     */
    void scrollLayersPageUp ();


    /**
     * Scroll the layers down by one page.
     */
    void scrollLayersPageDown ();


    /**
     * Set the layer color.
     *
     * @param index The index
     * @param red The red
     * @param green The green
     * @param blue The blue
     */
    void setLayerColor (int index, double red, double green, double blue);


    /**
     * Change the layer volume.
     *
     * @param index The index
     * @param control The control value
     */
    void changeLayerVolume (int index, int control);


    /**
     * Set the drum pad volume.
     *
     * @param index The index
     * @param value The value
     */
    void setLayerVolume (int index, int value);


    /**
     * Reset the layer volume to its default value.
     *
     * @param index The index
     */
    void resetLayerVolume (int index);


    /**
     * Signal touch to the layer volume.
     *
     * @param index The index
     * @param isBeingTouched True if touched
     */
    void touchLayerVolume (int index, boolean isBeingTouched);


    /**
     * Change the layer panorama.
     *
     * @param index The index
     * @param control The control value
     */
    void changeLayerPan (int index, int control);


    /**
     * Set the drum pad panorama.
     *
     * @param index The index
     * @param value The value
     */
    void setLayerPan (int index, int value);


    /**
     * Reset the layer panorama to its default value.
     *
     * @param index The index
     */
    void resetLayerPan (int index);


    /**
     * Signal touch to the layer panorama.
     *
     * @param index The index
     * @param isBeingTouched True if touched
     */
    void touchLayerPan (int index, boolean isBeingTouched);


    /**
     * Change the layer send volume.
     *
     * @param index The index
     * @param sendIndex The index of the send
     * @param control The control value
     */
    void changeLayerSend (int index, int sendIndex, int control);


    /**
     * Set the drum pad send volume.
     *
     * @param index The index
     * @param sendIndex The index of the send
     * @param value The value
     */
    void setLayerSend (int index, int sendIndex, int value);


    /**
     * Reset the layer send volume to its default value.
     *
     * @param index The index
     * @param sendIndex The index of the send
     */
    void resetLayerSend (int index, int sendIndex);


    /**
     * Signal touch to the layer send volume.
     *
     * @param index The index
     * @param sendIndex The index of the send
     * @param isBeingTouched True if touched
     */
    void touchLayerSend (int index, int sendIndex, boolean isBeingTouched);


    /**
     * Toggle if the the layer is active.
     *
     * @param index The index
     */
    void toggleLayerIsActivated (int index);


    /**
     * Toggle the the layer mute.
     *
     * @param index The index
     */
    void toggleLayerMute (int index);


    /**
     * Set the layer mute.
     *
     * @param index The index
     * @param value The value
     */
    void setLayerMute (int index, boolean value);


    /**
     * Toggle the layer solo.
     *
     * @param index The index
     */
    void toggleLayerSolo (int index);


    /**
     * Set the layer solo.
     *
     * @param index The index
     * @param value The value
     */
    void setLayerSolo (int index, boolean value);


    /**
     * Set indication for drum pads.
     *
     * @param enable True to enable
     */
    void setDrumPadIndication (boolean enable);


    /**
     * Get the drum pad.
     *
     * @param index The index
     * @return The drum pad
     */
    IDrumPad getDrumPad (int index);


    /**
     * Get the selected drum pad.
     *
     * @return The selected drum pad
     */
    IChannel getSelectedDrumPad ();


    /**
     * Select a drum pad.
     *
     * @param index The index
     */
    void selectDrumPad (int index);


    /**
     * Select the previous drum pad, if any.
     */
    void previousDrumPad ();


    /**
     * Select the next drum pad, if any.
     */
    void nextDrumPad ();


    /**
     * Select the previous drum pad bank page, if any.
     */
    void previousDrumPadBank ();


    /**
     * Select the next drum pad bank page, if any.
     */
    void nextDrumPadBank ();


    /**
     * Enter a drum pad.
     *
     * @param index The index
     */
    void enterDrumPad (int index);


    /**
     * Select the first device in a drum pad.
     *
     * @param index The index
     */
    void selectFirstDeviceInDrumPad (int index);


    /**
     * Can the drum pads scrolled up.
     *
     * @return True if scrolling is possible
     */
    boolean canScrollDrumPadsUp ();


    /**
     * Can the drum pads scrolled down.
     *
     * @return True if scrolling is possible
     */
    boolean canScrollDrumPadsDown ();


    /**
     * Scroll the drum pads up by one page.
     */
    void scrollDrumPadsPageUp ();


    /**
     * Scroll the drum pads down by one page.
     */
    void scrollDrumPadsPageDown ();


    /**
     * Scroll the drum pads up by one.
     */
    void scrollDrumPadsUp ();


    /**
     * Scroll the drum pads down by one.
     */
    void scrollDrumPadsDown ();


    /**
     * Set the drum pad color.
     *
     * @param index The index
     * @param red The red
     * @param green The green
     * @param blue The blue
     */
    void setDrumPadColor (int index, double red, double green, double blue);


    /**
     * Change the drum pad volume.
     *
     * @param index The index
     * @param control The control value
     */
    void changeDrumPadVolume (int index, int control);


    /**
     * Set the drum pad volume.
     *
     * @param index The index
     * @param value The value
     */
    void setDrumPadVolume (int index, int value);


    /**
     * Reset the drum pad volume to its default value.
     *
     * @param index The index
     */
    void resetDrumPadVolume (int index);


    /**
     * Signal touch to the drum pad volume.
     *
     * @param index The index
     * @param isBeingTouched True if touched
     */
    void touchDrumPadVolume (int index, boolean isBeingTouched);


    /**
     * Change the drum pad panorama.
     *
     * @param index The index
     * @param control The control value
     */
    void changeDrumPadPan (int index, int control);


    /**
     * Set the drum pad panorama.
     *
     * @param index The index
     * @param value The value
     */
    void setDrumPadPan (int index, int value);


    /**
     * Reset the drum pad panorama to its default value.
     *
     * @param index The index
     */
    void resetDrumPadPan (int index);


    /**
     * Signal touch to the drum pad panorama.
     *
     * @param index The index
     * @param isBeingTouched True if touched
     */
    void touchDrumPadPan (int index, boolean isBeingTouched);


    /**
     * Change the drum pad send volume.
     *
     * @param index The index
     * @param sendIndex The index of the send
     * @param control The control value
     */
    void changeDrumPadSend (int index, int sendIndex, int control);


    /**
     * Set the drum pad send volume.
     *
     * @param index The index
     * @param sendIndex The index of the send
     * @param value The value
     */
    void setDrumPadSend (int index, int sendIndex, int value);


    /**
     * Reset the drum pad send volume to its default value.
     *
     * @param index The index
     * @param sendIndex The index of the send
     */
    void resetDrumPadSend (int index, int sendIndex);


    /**
     * Signal touch to the drum pad send volume.
     *
     * @param index The index
     * @param sendIndex The index of the send
     * @param isBeingTouched True if touched
     */
    void touchDrumPadSend (int index, int sendIndex, boolean isBeingTouched);


    /**
     * Toggle if the the drum pad is active.
     *
     * @param index The index
     */
    void toggleDrumPadIsActivated (int index);


    /**
     * Toggle the the drum pad mute.
     *
     * @param index The index
     */
    void toggleDrumPadMute (int index);


    /**
     * Set the drum pad mute.
     *
     * @param index The index
     * @param value The value
     */
    void setDrumPadMute (int index, boolean value);


    /**
     * Toggle the drum pad solo.
     *
     * @param index The index
     */
    void toggleDrumPadSolo (int index);


    /**
     * Set the drum pad solo.
     *
     * @param index The index
     * @param value The value
     */
    void setDrumPadSolo (int index, boolean value);


    /**
     * Get the number of layers of a bank page.
     *
     * @return The number
     */
    int getNumLayers ();


    /**
     * Get the number of drum pads of a bank page.
     *
     * @return The number
     */
    int getNumDrumPads ();


    /**
     * Get the number of a page in the parameters bank.
     *
     * @return The number
     */
    int getNumParameters ();


    /**
     * Get the device sibling bank.
     *
     * @return The bank
     */
    IDeviceBank getDeviceBank ();
}