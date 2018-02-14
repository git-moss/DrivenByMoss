// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.grid;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.midi.IMidiOutput;

import java.util.Arrays;


/**
 * Implementation of a grid of pads.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PadGridImpl implements PadGrid
{
    protected IMidiOutput   output;
    protected ColorManager colorManager;

    protected int []       currentButtonColors;
    protected int []       buttonColors;
    protected int []       currentBlinkColors;
    protected int []       blinkColors;
    protected boolean []   currentBlinkFast;
    protected boolean []   blinkFast;


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The midi output which can address the pad states
     */
    public PadGridImpl (final ColorManager colorManager, final IMidiOutput output)
    {
        this.colorManager = colorManager;
        this.output = output;

        // Note: The grid contains only 64 pads but is more efficient to use
        // the 128 note values the pads understand

        this.currentButtonColors = new int [128];
        this.buttonColors = new int [128];
        this.currentBlinkColors = new int [128];
        this.blinkColors = new int [128];
        this.currentBlinkFast = new boolean [128];
        this.blinkFast = new boolean [128];

        final int color = colorManager.getColor (GRID_OFF);
        Arrays.fill (this.currentButtonColors, color);
        Arrays.fill (this.buttonColors, color);
        Arrays.fill (this.currentBlinkColors, color);
        Arrays.fill (this.blinkColors, color);
        Arrays.fill (this.currentBlinkFast, false);
        Arrays.fill (this.blinkFast, false);
    }


    /** {@inheritDoc} */
    @Override
    public void light (final int note, final int color)
    {
        this.setLight (note, color, -1, false);
    }


    /** {@inheritDoc} */
    @Override
    public void light (final int note, final int color, final int blinkColor, final boolean fast)
    {
        this.setLight (note, color, blinkColor, fast);
    }


    /** {@inheritDoc} */
    @Override
    public void lightEx (final int x, final int y, final int color)
    {
        this.lightEx (x, y, color, -1, false);
    }


    /** {@inheritDoc} */
    @Override
    public void lightEx (final int x, final int y, final int color, final int blinkColor, final boolean fast)
    {
        this.setLight (92 + x - 8 * y, color, blinkColor, fast);
    }


    /** {@inheritDoc} */
    @Override
    public void light (final int note, final String colorID)
    {
        this.light (note, colorID, null, false);
    }


    /** {@inheritDoc} */
    @Override
    public void lightEx (final int x, final int y, final String colorID)
    {
        this.lightEx (x, y, colorID, null, false);
    }


    /** {@inheritDoc} */
    @Override
    public void light (final int note, final String colorID, final String blinkColorID, final boolean fast)
    {
        this.light (note, this.colorManager.getColor (colorID), blinkColorID == null ? -1 : this.colorManager.getColor (blinkColorID), fast);
    }


    /** {@inheritDoc} */
    @Override
    public void lightEx (final int x, final int y, final String colorID, final String blinkColorID, final boolean fast)
    {
        this.lightEx (x, y, this.colorManager.getColor (colorID), blinkColorID == null ? -1 : this.colorManager.getColor (blinkColorID), fast);
    }


    /**
     * Set the lighting state of a pad.
     *
     * @param index The index in the array (0-127)
     * @param color The color or brightness to set
     * @param blinkColor The state to make a pad blink
     * @param fast Blinking is fast if true
     */
    protected void setLight (final int index, final int color, final int blinkColor, final boolean fast)
    {
        if (blinkColor >= 0)
        {
            this.buttonColors[index] = color;
            this.blinkColors[index] = blinkColor;
        }
        else
        {
            this.buttonColors[index] = color;
            this.blinkColors[index] = this.colorManager.getColor (GRID_OFF);
        }
        this.blinkFast[index] = fast;
    }


    /** {@inheritDoc} */
    @Override
    public void forceFlush (final int note)
    {
        this.currentButtonColors[note] = -1;
        this.currentBlinkColors[note] = -1;
        this.flush ();
    }


    /** {@inheritDoc} */
    @Override
    public void forceFlush ()
    {
        for (int i = 36; i < 100; i++)
        {
            this.currentButtonColors[i] = -1;
            this.currentBlinkColors[i] = -1;
        }
        this.flush ();
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        for (int i = 36; i < 100; i++)
        {
            final int note = this.translateToController (i);

            boolean baseChanged = false;
            if (this.currentButtonColors[i] != this.buttonColors[i])
            {
                this.currentButtonColors[i] = this.buttonColors[i];
                this.sendNoteState (note, this.buttonColors[i]);
                baseChanged = true;
            }
            // No "else" here: Blinking color needs a base color
            if (baseChanged || this.currentBlinkColors[i] != this.blinkColors[i] || this.currentBlinkFast[i] != this.blinkFast[i])
            {
                this.currentBlinkColors[i] = this.blinkColors[i];
                this.currentBlinkFast[i] = this.blinkFast[i];

                this.sendNoteState (note, this.currentButtonColors[i]);
                if (this.blinkColors[i] != this.colorManager.getColor (GRID_OFF))
                    this.sendBlinkState (note, this.blinkColors[i], this.blinkFast[i]);
            }
        }
    }


    /**
     * Send the note/pad update to the controller.
     *
     * @param note The note
     * @param color The color
     */
    protected void sendNoteState (final int note, final int color)
    {
        this.output.sendNote (note, color);
    }


    /**
     * Set the given pad/note to blink.
     *
     * @param note The note
     * @param blinkColor The color to use for blinking
     * @param fast Blink fast or slow
     */
    protected void sendBlinkState (final int note, final int blinkColor, final boolean fast)
    {
        this.output.sendNoteEx (fast ? 14 : 10, note, blinkColor);
    }


    /** {@inheritDoc} */
    @Override
    public void turnOff ()
    {
        final int color = this.colorManager.getColor (GRID_OFF);
        for (int i = 36; i < 100; i++)
            this.light (i, color, -1, false);
        this.flush ();
    }


    /** {@inheritDoc} */
    @Override
    public int translateToGrid (final int note)
    {
        return note;
    }


    /** {@inheritDoc} */
    @Override
    public int translateToController (final int note)
    {
        return note;
    }
}