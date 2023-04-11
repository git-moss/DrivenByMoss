// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.scale;

/**
 * Creates a scale matrix which has the same notes of the scale in each row. This can be used as a
 * basis for chords.
 *
 * @author Jürgen Moßgraber
 */
class ChordGrid
{
    private final Scale  scale;
    private final int [] matrix;


    /**
     * Constructor.
     *
     * @param scale The sale from which to create a grid
     * @param rows The number of rows of the grid
     * @param cols The number of columns of the grid
     */
    public ChordGrid (final Scale scale, final int rows, final int cols)
    {
        final int size = cols * rows;
        this.matrix = new int [size];
        this.scale = scale;

        final int [] intervals = this.scale.getIntervals ();
        final int len = intervals.length;

        for (int column = 0; column < cols; column++)
        {
            final int oct = column / len;
            final int note = oct * 12 + intervals[column % len];

            for (int row = 0; row < rows; row++)
            {
                final int index = row * cols + column;
                this.matrix[index] = note;
            }
        }
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
}
