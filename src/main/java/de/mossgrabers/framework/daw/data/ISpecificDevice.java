// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.observer.IValueObserver;


/**
 * Interface to a specific device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ISpecificDevice extends IDevice
{
    /**
     * Get the identifier of the device if any.
     *
     * @return The identifier, returns an empty string if none, never null
     */
    String getID ();


    /**
     * Returns true if the device is a non-DAW plugin.
     *
     * @return True if the device is a non-DAW plugin
     */
    boolean isPlugin ();


    /**
     * Is the device expanded?
     *
     * @return True if the device is expanded
     */
    boolean isExpanded ();


    /**
     * Toggle the expanded state of the device.
     */
    void toggleExpanded ();


    /**
     * Is the remote control section of the device expanded?
     *
     * @return True if the remote control section of the device is expanded
     */
    boolean isParameterPageSectionVisible ();


    /**
     * Toggle the parameter section visibility state of the device.
     */
    void toggleParameterPageSectionVisible ();


    /**
     * Is the device window open?
     *
     * @return True if the device window is open
     */
    boolean isWindowOpen ();


    /**
     * Toggle the window of an external device on/off.
     */
    void toggleWindowOpen ();


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
     * Add an observer for changes of drum pads support.
     *
     * @param observer True if it the selected device provides support
     */
    void addHasDrumPadsObserver (IValueObserver<Boolean> observer);


    /**
     * Remove an observer for changes of drum pads support.
     *
     * @param observer The observer to remove
     */
    void removeHasDrumPadsObserver (IValueObserver<Boolean> observer);


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
}