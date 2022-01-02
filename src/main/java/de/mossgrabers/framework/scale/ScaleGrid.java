// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.scale;

/**
 * A scale applied to a grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
class ScaleGrid
{
    /** Orientation of the layout. */
    public enum Orientation
    {
        /** Lower notes are on the bottom, higher notes on the top. */
        ORIENT_UP,
        /** Lower notes are on the left, higher notes on the right. */
        ORIENT_RIGHT
    }


    private final Scale  scale;
    private final int [] matrix;
    private final int [] chromatic;


    /**
     * Creates two grid matrices from the scale. One, which contains only the notes of the scale and
     * a chromatic one.
     *
     * @param scale The sale from which to create a grid
     * @param layout The layout to use
     * @param orientation The orientation of the scale on the grid
     * @param rows The number of rows of the grid
     * @param cols The number of columns of the grid
     * @param shift The number of scale steps that the notes in the next rows are shifted (e.g. 4)
     */
    public ScaleGrid (final Scale scale, final ScaleLayout layout, final Orientation orientation, final int rows, final int cols, final int shift)
    {
        final int size = cols * rows;
        this.matrix = new int [size];
        this.chromatic = new int [size];
        this.scale = scale;

        final int [] intervals = this.scale.getIntervals ();
        final int len = intervals.length;

        final boolean isUp = orientation == Orientation.ORIENT_UP;
        final int shiftedNote;
        if (shift == rows)
            shiftedNote = rows;
        else
            shiftedNote = shift == 7 ? 12 : cols - shift;
        final int centerOffset = layout == ScaleLayout.EIGHT_UP_CENTER || layout == ScaleLayout.EIGHT_RIGHT_CENTER ? -3 : 0;

        for (int row = 0; row < rows; row++)
        {
            for (int column = 0; column < cols; column++)
            {
                final int y = isUp ? row : column;
                final int x = isUp ? column : row;

                int s = shift;
                // Fix 8th layout for scales which do not have 7 steps
                if (shift == 7)
                    s = len;
                int offset = y * s + x + centerOffset;

                int oct = offset / len;

                // Fix negative values introduced by centerOffset
                if (offset < 0)
                {
                    offset = len + offset;
                    oct = offset / len - 1;
                }

                final int index = row * cols + column;
                this.matrix[index] = oct * 12 + intervals[offset % len];
                this.chromatic[index] = y * shiftedNote + x;
            }
        }
    }


    /**
     * Get the name of the scale.
     *
     * @return The name of the scale
     */
    public String getName ()
    {
        return this.scale.getName ();
    }


    /**
     * Get the matrix.
     *
     * @return The matrix
     */
    public int [] getMatrix ()
    {
        return this.matrix;
    }


    /**
     * Get the chromatic matrix.
     *
     * @return The chromatic matrix.
     */
    public int [] getChromatic ()
    {
        return this.chromatic;
    }
}
