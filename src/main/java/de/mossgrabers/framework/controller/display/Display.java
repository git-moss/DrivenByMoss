// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.display;

/**
 * Interface to a display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface Display
{
    /**
     * Clear a cell.
     *
     * @param row The row of the cell
     * @param column The column of the cell
     * @return The instance for concatenated calls
     */
    Display clearCell (int row, int column);


    /**
     * Set a cell.
     *
     * @param row The row of the cell
     * @param column The column of the cell
     * @param value The value to write into the cell
     * @param format How to format the value
     * @return The instance for concatenated calls
     */
    Display setCell (int row, int column, int value, Format format);


    /**
     * Set a cell.
     *
     * @param row The row of the cell
     * @param column The column of the cell
     * @param value The text to write into the cell
     * @return The instance for concatenated calls
     */
    Display setCell (int row, int column, String value);


    /**
     * Set a block.
     *
     * @param row The row of the block
     * @param block The number of the block
     * @param value The text to write into the block
     * @return The instance for concatenated calls
     */
    Display setBlock (int row, int block, String value);


    /**
     * Set a whole row.
     *
     * @param row The row to set
     * @param str The text to set. It must match the exact number of characters of a row!
     * @return The instance for concatenated calls
     */
    Display setRow (final int row, final String str);


    /**
     * Clear all rows.
     *
     * @return The instance for concatenated calls
     */
    Display clear ();


    /**
     * Clear a rows.
     *
     * @param row The row to clear
     * @return The instance for concatenated calls
     */
    Display clearRow (final int row);


    /**
     * Clear a block.
     *
     * @param row The row of the block
     * @param block The block to clear
     * @return The instance for concatenated calls
     */
    public Display clearBlock (final int row, final int block);


    /**
     * Clear a column.
     *
     * @param column The column to clear
     * @return The instance for concatenated calls
     */
    Display clearColumn (final int column);


    /**
     * Signals that editing of a row is finished and the full text for the row can be created.
     *
     * @param row The row
     * @return The instance for concatenated calls
     */
    Display done (final int row);


    /**
     * Marks editing as finished and create the text for all rows.
     *
     * @return The instance for concatenated calls
     */
    Display allDone ();


    /**
     * Send a row to the display.
     *
     * @param row The row to which to send the text
     * @param text The text to send
     */
    void writeLine (int row, String text);


    /**
     * Displays a notification message on the screen (in the DAW).
     *
     * @param message The message to display
     */
    void notify (final String message);


    /**
     * Displays a notification message on the display for a configured time.
     *
     * @param message The message to display
     * @param onDisplay If true displays the message on the screen (in the DAW)
     * @param onScreen If true displays the message on the display
     */
    void notify (final String message, final boolean onDisplay, final boolean onScreen);


    /**
     * Flushes (only) the changed texts of all rows.
     */
    void flush ();


    /**
     * If there is any cleanup necessary.
     */
    void shutdown ();
}
