// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.controller;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
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
     */
    public APCminiScales (final IValueChanger valueChanger)
    {
        super (valueChanger, 36, 100, 8, 8);
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateMatrixToGrid (final int [] matrix)
    {
        final int [] gridMatrix = Scales.getEmptyMatrix ();
        for (int i = 36; i < 100; i++)
            gridMatrix[i - 36] = matrix[i];
        return gridMatrix;
    }
}