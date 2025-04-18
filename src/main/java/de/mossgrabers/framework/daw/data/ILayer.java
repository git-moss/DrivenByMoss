// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

/**
 * The interface to a layer channel.
 *
 * @author Jürgen Moßgraber
 */
public interface ILayer extends IChannel
{
    /**
     * Check if the layer contains devices.
     *
     * @return True if there is at least one device
     */
    boolean hasDevices ();
}