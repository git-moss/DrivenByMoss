// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.grid;

/**
 * Interface to a grid of pads.
 *
 * @author Jürgen Moßgraber
 */
public interface IPadGrid extends ILightGuide
{
    /** The ID for the pad state off. Can the value can be retrieved from the color manager. */
    String GRID_OFF = "GRID_COLOR_OFF";


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
     * @param x The x position of the pad in the grid
     * @param y The y position of the pad in the grid
     * @param colorID A registered color ID of the color / brightness
     */
    void lightEx (int x, int y, String colorID);


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
     * Plug for grids not sending notes in the range of 36-100.
     *
     * @param note The incoming note
     * @return The note scaled to the range of 36-100
     */
    int translateToGrid (int note);


    /**
     * Get the number the number of rows of the grid.
     *
     * @return The number of rows of the grid
     */
    int getRows ();
}