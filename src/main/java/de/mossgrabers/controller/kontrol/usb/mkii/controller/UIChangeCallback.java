// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.controller;

/**
 * Callback interface for button and encoder changes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface UIChangeCallback
{
    /**
     * A button was pressed or released.
     *
     * @param buttonID The ID of the button
     * @param isPressed True if pressed
     */
    void buttonChange (int buttonID, boolean isPressed);


    /**
     * The main encoder was turned.
     *
     * @param valueIncreased The change value
     */
    void mainEncoderChanged (boolean valueIncreased);


    /**
     * An encoder was turned.
     *
     * @param encIndex The index of the encoder (0-7)
     * @param valueIncreased The change value
     */
    void encoderChanged (int encIndex, boolean valueIncreased);
}
