// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.core.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.BlinkingPadGrid;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * Implementation of the Maschine grid of pads.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschinePadGrid extends BlinkingPadGrid
{
    /**
     * Constructor. A 4x4 grid.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     */
    public MaschinePadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output, 4, 4, 36);
    }


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     * @param rows The number of rows of the grid
     * @param cols The number of columns of the grid
     */
    public MaschinePadGrid (final ColorManager colorManager, final IMidiOutput output, final int rows, final int cols)
    {
        super (colorManager, output, rows, cols, 36);
    }
}