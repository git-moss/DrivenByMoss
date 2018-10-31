// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.grid;

/**
 * Interface to a grid of pads.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface PadGrid
{
    /** The ID for the pad state off. Can the value can be retrieved from the color manager. */
    String GRID_OFF = "GRID_COLOR_OFF";


    /**
     * Set the lighting state of a pad.
     *
     * @param note The midi note of the pad
     * @param color The color or brightness to set
     */
    void light (int note, int color);


    /**
     * Set the lighting state of a pad.
     *
     * @param note The midi note of the pad
     * @param color The color or brightness to set
     * @param blinkColor The state to make a pad blink
     * @param fast Blinking is fast if true
     */
    void light (int note, int color, int blinkColor, boolean fast);


    /**
     * Set the lighting state of a pad.
     *
     * @param x The x position of the pad in the grid
     * @param y The y position of the pad in the grid
     * @param color A registered color ID of the color / brightness
     */
    void lightEx (int x, int y, int color);


    /**
     * Set the lighting state of a pad.
     *
     * @param x The x position of the pad in the grid
     * @param y The y position of the pad in the grid
     * @param color A registered color ID of the color / brightness
     * @param blinkColor The state to make a pad blink
     * @param fast Blinking is fast if true
     */
    void lightEx (int x, int y, int color, int blinkColor, boolean fast);


    /**
     * Set the lighting state of a pad.
     *
     * @param note The midi note of the pad
     * @param colorID A registered color ID of the color / brightness
     */
    void light (int note, String colorID);


    /**
     * Set the lighting state of a pad.
     *
     * @param x The x position of the pad in the grid
     * @param y The y position of the pad in the grid
     * @param colorID A registered color ID of the color / brightness
     */
    void lightEx (int x, int y, String colorID);


    /**
     * Set the lighting state of a pad.
     *
     * @param note The midi note of the pad
     * @param colorID A registered color ID of the color / brightness
     * @param blinkColorID A registered color ID of the blinking color / brightness
     * @param fast Blinking is fast if true
     */
    void light (int note, String colorID, String blinkColorID, boolean fast);


    /**
     * Set the lighting state of a pad.
     *
     * @param x The x position of the pad in the grid
     * @param y The y position of the pad in the grid
     * @param colorID A registered color ID of the color / brightness
     * @param blinkColorID A registered color ID of the blinking color / brightness
     * @param fast Blinking is fast if true
     */
    void lightEx (int x, int y, String colorID, String blinkColorID, boolean fast);


    /**
     * Flush out all changes of the pad states.
     */
    void flush ();


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
     * Turn off the lights of all pads in the grid.
     */
    void turnOff ();


    /**
     * Plug for grids not sending notes in the range of 36-100.
     *
     * @param note The incoming note
     * @return The note scaled to the range of 36-100
     */
    int translateToGrid (int note);


    /**
     * Plug for grids not sending notes in the range of 36-100.
     *
     * @param note The outgoing note
     * @return The note scaled to the controller
     */
    int translateToController (int note);


    /**
     * Get the number the number of rows of the grid.
     *
     * @return The number of rows of the grid
     */
    int getRows ();


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
}
