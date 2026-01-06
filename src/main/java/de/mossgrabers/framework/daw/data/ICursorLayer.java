// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import java.util.Optional;

import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;


/**
 * A cursor which monitors the selected layer of a device which supports layers.
 *
 * @author Jürgen Moßgraber
 */
public interface ICursorLayer
{
    /**
     * Get the device bank.
     *
     * @return The device bank
     */
    IDeviceBank getDeviceBank ();


    /**
     * Get the selected device.
     *
     * @return The device if there is a selection
     */
    Optional<ISpecificDevice> getSelectedDevice ();


    /**
     * Get the layer bank which is monitored by the cursor.
     *
     * @return The layer bank
     */
    ILayerBank getLayerBank ();
}
