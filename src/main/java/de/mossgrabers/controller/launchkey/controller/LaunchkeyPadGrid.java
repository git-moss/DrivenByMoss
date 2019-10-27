// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.view.Views;


/**
 * Implementation of the Launchkey grid of pads.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchkeyPadGrid extends PadGridImpl
{
    private Views activeView;


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The midi output which can address the pad states
     */
    public LaunchkeyPadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output, 2, 8, 36);
    }


    /** {@inheritDoc} */
    @Override
    public int translateToGrid (final int note)
    {
        if (this.activeView == null)
            return note;

        switch (this.activeView)
        {
            case SESSION:
                // Row 2 (upper) sends notes: 0x60 - 0x67; returns 44-51
                if (note < 0x70)
                    return note - 52;
                // Row 1 sends notes: 0x70 - 0x77; returns 36-43
                return note - 76;

            case DRUM:
                // 40 41 42 43 48 49 50 51
                // 36 37 38 39 44 45 46 47
                if (note >= 44 && note < 48)
                    return note - 4;
                if (note >= 40 && note < 44)
                    return note + 4;
                return note;

            default:
                // Unsupported view
                return note;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int translateToController (final int note)
    {
        if (this.activeView == null)
            return note;

        switch (this.activeView)
        {
            case SESSION:
                if (note > 43)
                    return note + 52;
                return note + 76;

            case DRUM:
                // 40 41 42 43 48 49 50 51
                // 36 37 38 39 44 45 46 47
                if (note >= 44 && note < 48)
                    return note - 4;
                if (note >= 40 && note < 44)
                    return note + 4;
                return note;

            default:
                // Unsupported view
                return note;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int note, final int color)
    {
        if (this.activeView == null)
            return;

        switch (this.activeView)
        {
            case DRUM:
                this.output.sendNoteEx (0x09, note, color);
                break;

            case SESSION:
            default:
                this.output.sendNote (note, color);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void sendBlinkState (final int note, final int blinkColor, final boolean fast)
    {
        if (this.activeView == null)
            return;

        switch (this.activeView)
        {
            case DRUM:
                this.output.sendNoteEx (0x09 + (fast ? 1 : 2), note, blinkColor);
                break;

            case SESSION:
            default:
                this.output.sendNoteEx (fast ? 1 : 2, note, blinkColor);
                break;
        }
    }


    /**
     * Set the active view since the Launchkey has different key mappings in the different views.
     *
     * @param view The active view
     */
    public void setView (final Views view)
    {
        this.activeView = view;
    }
}