// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.core.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.BlinkingPadGrid;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.midi.IMidiOutput;

import java.util.Map.Entry;


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


    /** {@inheritDoc} */
    @Override
    protected void updateController ()
    {
        final int channel = 0;

        for (final Entry<Integer, LightInfo> e: this.padInfos.entrySet ())
        {
            final Integer note = e.getKey ();
            final LightInfo info = e.getValue ();

            // Note: The exact PADx is not needed for getting the color

            this.output.sendNoteEx (channel, note.intValue (), info.getColor ());

            // Hardware does not support blinking, therefore needs to be implemented the hard
            // way
            if (info.getBlinkColor () > 0)
                this.blinkingLights.put (note, info);
            else
                this.blinkingLights.remove (note);
        }

        // Toggle blink colors every 600ms
        if (!this.checkBlinking ())
            return;
        for (final Entry<Integer, LightInfo> value: this.blinkingLights.entrySet ())
        {
            final LightInfo info = value.getValue ();
            final int colorIndex = this.isBlink ? info.getBlinkColor () : info.getColor ();
            final int note = value.getKey ().intValue ();
            this.output.sendNoteEx (channel, note, colorIndex);
        }
    }
}