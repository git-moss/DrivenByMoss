// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

/**
 * Interface for a proxy to a light / LED on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IHwLight extends IHwControl
{
    /**
     * Switch off the light.
     */
    void turnOff ();


    /**
     * Clear the button cache state.
     */
    void forceFlush ();
}
