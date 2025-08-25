// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * Implementation of the Exquis grid of pads.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisPadGrid extends PadGridImpl
{
    private ExquisControlSurface surface;


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     */
    public ExquisPadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output, 1, 61, 36);
    }


    /**
     * Set the surface.
     *
     * @param surface The surface
     */
    public void setSurface (final ExquisControlSurface surface)
    {
        this.surface = surface;
    }


    /** {@inheritDoc} */
    @Override
    public int translateToGrid (final int note)
    {
        return note + 36;
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateToController (final int note)
    {
        return new int []
        {
            15,
            note - 36
        };
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int channel, final int note, final int color)
    {
        if (this.surface != null)
            this.surface.setLED (note, this.colorManager.getColor (color, null));
    }


    /** {@inheritDoc} */
    @Override
    protected void sendBlinkState (final int channel, final int note, final int blinkColor, final boolean fast)
    {
        if (this.surface != null)
            this.surface.setLED (note, this.colorManager.getColor (blinkColor, null), 0x7F);
    }
}