// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.continuous;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Interface for modes which support the Push 3 encoder.
 *
 * @author Jürgen Moßgraber
 */
public interface IPush3Encoder
{
    /**
     * The encoder was turned.
     *
     * @param value The relative offset value
     */
    void encoderTurn (int value);


    /**
     * The encoder was moved to the left.
     *
     * @param event The button event
     */
    void encoderLeft (ButtonEvent event);


    /**
     * The encoder was moved to the right.
     *
     * @param event The button event
     */
    void encoderRight (ButtonEvent event);


    /**
     * The encoder was pressed.
     *
     * @param event The button event
     */
    void encoderPress (ButtonEvent event);


    /**
     * The button in the center of the 4 arrows was pressed.
     *
     * @param event The button event
     */
    void arrowCenter (ButtonEvent event);
}