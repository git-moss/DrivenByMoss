// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.scale;

/**
 * A scale applied to a grid.
 *
 * @author Jürgen Moßgraber
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
     * @param semitoneShift The number of semitones the notes in the next rows are shifted in
     *            chromatic mode
     */
    public ScaleGrid (final Scale scale, final ScaleLayout layout, final Orientation orientation, final int rows, final int cols, final int shift, final int semitoneShift)
    {
        final int size = cols * rows;
        this.matrix = new int [size];
        this.chromatic = new int [size];
        this.scale = scale;

        final int [] intervals = this.scale.getIntervals ();
        final int len = intervals.length;

        final boolean isUp = orientation == Orientation.ORIENT_UP;
        final int centerOffset = layout == ScaleLayout.EIGHT_UP_CENTER || layout == ScaleLayout.EIGHT_RIGHT_CENTER ? -3 : 0;

        // Axis deltas, in scale steps.
        int dx = 1;
        int dy = shift;

        // Fix 8th layout for scales which do not have 7 steps
        if (shift == 7)
            dy = len;

        if (layout == ScaleLayout.STAGGERED_UP || layout == ScaleLayout.STAGGERED_RIGHT)
        {
            dx = 2;
            // Staggered layout steps right by + 2, so on the row above a nice symmetry is produced
            // by octave - 2. Divide until shift is odd to evenly distribute the full scale and
            // ensure all steps are included.
            dy = len - 2;
            while (dy > 1 && (dy & 1) == 0)
                dy /= 2;
        }

        for (int row = 0; row < rows; row++)
        {
            for (int column = 0; column < cols; column++)
            {
                final int y = isUp ? row : column;
                final int x = isUp ? column : row;
                final int index = row * cols + column;

                int offset = y * dy + x * dx + centerOffset;
                int oct = offset / len;

                // Fix negative values introduced by centerOffset
                if (offset < 0)
                {
                    offset = len + offset;
                    oct = offset / len - 1;
                }

                this.matrix[index] = oct * 12 + intervals[offset % len];
                this.chromatic[index] = y * semitoneShift + x;
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
