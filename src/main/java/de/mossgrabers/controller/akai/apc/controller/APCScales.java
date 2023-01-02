// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.controller;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.scale.Scales;


/**
 * Changes matrices to different grid note mapping of the APC40.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCScales extends Scales
{
    /**
     * Constructor.
     *
     * @param valueChanger A value changer
     */
    public APCScales (final IValueChanger valueChanger)
    {
        super (valueChanger, 36, 76, 8, 5);

        this.setDrumNoteEnd (76);
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateMatrixToGrid (final int [] matrix)
    {
        final int [] gridMatrix = Scales.getEmptyMatrix ();
        for (int i = 36; i < 76; i++)
            gridMatrix[i - 36] = matrix[i];
        return gridMatrix;
    }
}