// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.display;

import de.mossgrabers.framework.controller.hardware.IHwTextDisplay;


/**
 * Interface to a text only display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ITextDisplay extends IDisplay
{
    /**
     * Get the number of lines of the display.
     *
     * @return The number of lines (rows)
     */
    int getNoOfLines ();


    /**
     * Clears the display.
     *
     * @return The instance for concatenated calls
     */
    ITextDisplay clear ();


    /**
     * Clear a cell.
     *
     * @param row The row of the cell
     * @param column The column of the cell
     * @return The instance for concatenated calls
     */
    ITextDisplay clearCell (int row, int column);


    /**
     * Set a cell.
     *
     * @param row The row of the cell
     * @param column The column of the cell
     * @param value The value to write into the cell
     * @param format How to format the value
     * @return The instance for concatenated calls
     */
    ITextDisplay setCell (int row, int column, int value, Format format);


    /**
     * Set a cell.
     *
     * @param row The row of the cell
     * @param column The column of the cell
     * @param value The text to write into the cell
     * @return The instance for concatenated calls
     */
    ITextDisplay setCell (int row, int column, String value);


    /**
     * Set a block.
     *
     * @param row The row of the block
     * @param block The number of the block
     * @param value The text to write into the block
     * @return The instance for concatenated calls
     */
    ITextDisplay setBlock (int row, int block, String value);


    /**
     * Set a whole row.
     *
     * @param row The row to set
     * @param str The text to set. It must match the exact number of characters of a row!
     * @return The instance for concatenated calls
     */
    ITextDisplay setRow (final int row, final String str);


    /**
     * Clear a rows.
     *
     * @param row The row to clear
     * @return The instance for concatenated calls
     */
    ITextDisplay clearRow (final int row);


    /**
     * Clear a block.
     *
     * @param row The row of the block
     * @param block The block to clear
     * @return The instance for concatenated calls
     */
    public ITextDisplay clearBlock (final int row, final int block);


    /**
     * Clear a column.
     *
     * @param column The column to clear
     * @return The instance for concatenated calls
     */
    ITextDisplay clearColumn (final int column);


    /**
     * Signals that editing of a row is finished and the full text for the row can be created.
     *
     * @param row The row
     * @return The instance for concatenated calls
     */
    ITextDisplay done (final int row);


    /**
     * Marks editing as finished and create the text for all rows.
     *
     * @return The instance for concatenated calls
     */
    ITextDisplay allDone ();


    /**
     * Send a row to the display.
     *
     * @param row The row to which to send the text
     * @param text The text to send
     */
    void writeLine (int row, String text);


    /**
     * Flushes (only) the changed texts of all rows.
     */
    void flush ();


    /**
     * Forces the recreation of all row texts. The next call to flush will then send all rows.
     */
    void forceFlush ();


    /**
     * Assign a proxy to the hardware display, which gets filled by this text display.
     *
     * @param display The hardware display
     */
    void setHardwareDisplay (IHwTextDisplay display);


    /**
     * Get the hardware display.
     *
     * @return The hardware display
     */
    IHwTextDisplay getHardwareDisplay ();
}
