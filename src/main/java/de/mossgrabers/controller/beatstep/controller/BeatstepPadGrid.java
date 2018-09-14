// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.controller;

import de.mossgrabers.framework.StringUtils;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The pad grid of the Beatstep.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepPadGrid extends PadGridImpl
{
    private int columns;


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The midi output which can address the pad states
     */
    public BeatstepPadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output);

        this.columns = 8;
    }


    /** {@inheritDoc} */
    @Override
    public void lightEx (final int x, final int y, final int color, final int blinkColor, final boolean fast)
    {
        this.setLight (36 + x + 8 * y, color, blinkColor, fast);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int note, final int color)
    {
        final int n = note - 36;
        final int pad = n < this.columns ? BeatstepControlSurface.BEATSTEP_PAD_9 + n : BeatstepControlSurface.BEATSTEP_PAD_1 + n - this.columns;
        final String data = BeatstepControlSurface.SYSEX_HEADER + StringUtils.toHexStr (new int []
        {
            pad,
            color
        }) + BeatstepControlSurface.SYSEX_END;
        this.output.sendSysex (data);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        for (int i = 36; i < 52; i++)
        {
            final int note = this.translateToController (i);
            if (this.currentButtonColors[i] != this.buttonColors[i])
            {
                this.currentButtonColors[i] = this.buttonColors[i];
                this.sendNoteState (note, this.buttonColors[i]);
            }
        }
    }
}