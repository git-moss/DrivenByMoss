// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini.controller;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.scale.Scales;


/**
 * Changes matrices to different grid note mapping of the APCmini.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCminiScales extends Scales
{
    /**
     * Constructor.
     *
     * @param valueChanger A value changer
     * @param startNote The first midi note of the pad grid
     * @param endNote The last midi note of the pad grid
     * @param numColumns The number of columns of the pad grid
     * @param numRows The number of rows of the pad grid
     */
    public APCminiScales (final ValueChanger valueChanger, final int startNote, final int endNote, final int numColumns, final int numRows)
    {
        super (valueChanger, startNote, endNote, numColumns, numRows);
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateMatrixToGrid (final int [] matrix)
    {
        final int [] gridMatrix = Scales.getEmptyMatrix ();
        System.arraycopy(matrix, 36, gridMatrix, 0, 64);
        return gridMatrix;
    }
}