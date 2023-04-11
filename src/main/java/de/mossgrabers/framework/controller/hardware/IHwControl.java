// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

/**
 * A control on a controller surface.
 *
 * @author Jürgen Moßgraber
 */
public interface IHwControl
{
    /**
     * Get the label.
     *
     * @return The label
     */
    String getLabel ();


    /**
     * Update the state of the control (e.g. light, fader position).
     */
    void update ();


    /**
     * The physical bounds of this hardware element on the controller. The unit is scaled into the
     * GUI window.
     *
     * @param x The X position of the control
     * @param y The Y position of the control
     * @param width The width of the control
     * @param height The height of the control
     */
    void setBounds (double x, double y, double width, double height);
}
