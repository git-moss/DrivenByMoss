// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The pad grid of the Beatstep.
 *
 * @author Jürgen Moßgraber
 */
public class BeatstepPadGrid extends PadGridImpl
{
    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     */
    public BeatstepPadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output, 2, 8, 36);
    }


    /** {@inheritDoc} */
    @Override
    public void lightEx (final int x, final int y, final int color, final int blinkColor, final boolean fast)
    {
        this.setLight (36 + x + 8 * y, color, blinkColor, fast);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int channel, final int note, final int color)
    {
        this.lightPad (note, color);
    }


    /**
     * Light a pad.
     *
     * @param note The note
     * @param color The color: 0, 1, 16, 17
     */
    public void lightPad (final int note, final int color)
    {
        final int n = note - 36;
        final int pad = n < this.cols ? BeatstepControlSurface.BEATSTEP_PAD_9 + n : BeatstepControlSurface.BEATSTEP_PAD_1 + n - this.cols;
        final String data = BeatstepControlSurface.SYSEX_HEADER + StringUtils.toHexStr (new int []
        {
            pad,
            color
        }) + BeatstepControlSurface.SYSEX_END;
        this.output.sendSysex (data);
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateToController (final int note)
    {
        final int [] translate = super.translateToController (note);
        translate[0] = 2;
        return translate;
    }
}