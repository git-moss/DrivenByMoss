// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

/**
 * Interface for a proxy to a light / LED on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public interface IHwLight extends IHwControl
{
    /**
     * Switch off the light.
     */
    void turnOff ();


    /**
     * Clear the light cache state.
     */
    void forceFlush ();
}
