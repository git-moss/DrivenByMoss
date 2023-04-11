// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * Implementation of the SL MkIII grid of pads.
 *
 * @author Jürgen Moßgraber
 */
public class SLMkIIIPadGrid extends PadGridImpl
{
    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     */
    public SLMkIIIPadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output, 2, 8, 36);
    }


    /** {@inheritDoc} */
    @Override
    public int translateToGrid (final int note)
    {
        // Row 2 sends notes: 0x60 - 0x67; returns 44-51
        if (note < 0x70)
            return note - 52;

        // Row 1 sends notes: 0x70 - 0x77; returns 36-43
        return note - 76;
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateToController (final int note)
    {
        if (note > 43)
            return new int []
            {
                15,
                note + 52
            };
        return new int []
        {
            15,
            note + 76
        };
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int channel, final int note, final int color)
    {
        this.output.sendNoteEx (15, note, color);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendBlinkState (final int channel, final int note, final int blinkColor, final boolean fast)
    {
        this.output.sendNoteEx (fast ? 1 : 2, note, blinkColor);
    }
}
