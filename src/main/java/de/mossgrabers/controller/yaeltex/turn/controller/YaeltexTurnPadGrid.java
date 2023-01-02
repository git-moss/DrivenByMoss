// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.BlinkingPadGrid;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * Implementation of the Yaeltex Turn grid of push buttons.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class YaeltexTurnPadGrid extends BlinkingPadGrid
{
    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     */
    public YaeltexTurnPadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output, 4, 8, 36);
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateToController (final int note)
    {
        return new int []
        {
            YaeltexTurnControlSurface.MIDI_CHANNEL_MAIN,
            note
        };
    }


    /** {@inheritDoc} */
    @Override
    protected void sendPadUpdate (final int note, final int colorIndex)
    {
        this.output.sendNoteEx (YaeltexTurnControlSurface.MIDI_CHANNEL_MAIN, note, colorIndex);
    }
}