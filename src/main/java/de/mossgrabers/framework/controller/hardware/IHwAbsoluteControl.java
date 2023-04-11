// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

/**
 * Interface for a proxy to an absolute controller (e.g. a fader or absolute knob) on a hardware
 * controller.
 *
 * @author Jürgen Moßgraber
 */
public interface IHwAbsoluteControl extends IHwContinuousControl
{
    /**
     * Determines if this hardware control should immediately take over the parameter it is bound to
     * rather than respecting the user's current take over mode.
     *
     * This is useful for motorized faders for example, where the fader is already at the value of
     * the bound parameter.
     */
    void disableTakeOver ();
}
