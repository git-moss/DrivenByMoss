// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.controller;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.scale.Scales;


/**
 * Changes matrices to different grid note mapping of the OXI One.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneScales extends Scales
{
    /**
     * Constructor.
     *
     * @param valueChanger A value changer
     */
    public OxiOneScales (final IValueChanger valueChanger)
    {
        super (valueChanger, 0, 128, 16, 8);
    }

    // /** {@inheritDoc} */
    // @Override
    // public int [] translateMatrixToGrid (final int [] matrix)
    // {
    // final int [] gridMatrix = Scales.getEmptyMatrix ();
    // for (int i = 36; i < 100; i++)
    // gridMatrix[OxiOnePadGrid.TRANSLATE_16x4_MATRIX[i - 36]] = matrix[i];
    // return gridMatrix;
    // }
}