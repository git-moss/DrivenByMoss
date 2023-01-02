// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.grid;

/**
 * Interface to a light guide (LEDs above keys).
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ILightGuide
{
    /**
     * Set the lighting state of an LED.
     *
     * @param note The MIDI note of the LED
     * @param color The color or brightness to set
     */
    void light (int note, int color);


    /**
     * Set the lighting state of a LED.
     *
     * @param note The MIDI note of the LED
     * @param color The color or brightness to set
     * @param blinkColor The state to make a LED blink
     * @param fast Blinking is fast if true
     */
    void light (int note, int color, int blinkColor, boolean fast);


    /**
     * Set the lighting state of a LED.
     *
     * @param note The MIDI note of the LED
     * @param colorID A registered color ID of the color / brightness
     */
    void light (int note, String colorID);


    /**
     * Set the lighting state of a LED.
     *
     * @param note The MIDI note of the LED
     * @param colorID A registered color ID of the color / brightness
     * @param blinkColorID A registered color ID of the blinking color / brightness
     * @param fast Blinking is fast if true
     */
    void light (int note, String colorID, String blinkColorID, boolean fast);


    /**
     * Resets the caching which forces a complete flush.
     */
    void forceFlush ();


    /**
     * Resets the caching for the given note which forces a flush.
     *
     * @param note The note to force flush
     */
    void forceFlush (int note);


    /**
     * Turn off the lights of all LEDs in the grid.
     */
    void turnOff ();


    /**
     * Plug for grids not sending notes in the range of 36-100.
     *
     * @param note The outgoing note
     * @return The MIDI channel (index 0) and note (index 1) scaled to the controller
     */
    int [] translateToController (int note);


    /**
     * Get the number the number of columns of the grid.
     *
     * @return The number of columns of the grid
     */
    int getCols ();


    /**
     * Get the start note of the grid.
     *
     * @return The start note of the grid
     */
    int getStartNote ();


    /**
     * Get the color and blink states of a LED.
     *
     * @param note The LED of the note (0-127)
     * @return The info
     */
    LightInfo getLightInfo (int note);


    /**
     * Set the state to the controller.
     *
     * @param note The note (0-127)
     */
    void sendState (int note);
}