// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;


/**
 * Implementation of the APCmini grid of pads.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCminiPadGrid extends PadGridImpl
{
    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param surface The APCmini surface
     */
    public APCminiPadGrid (final ColorManager colorManager, final APCminiControlSurface surface)
    {
        super (colorManager, surface.getOutput ());
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int note, final int color)
    {
        this.output.sendNote (note, color);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendBlinkState (final int note, final int blinkColor, final boolean fast)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int translateToGrid (final int note)
    {
        return note + 36;
    }


    /** {@inheritDoc} */
    @Override
    public int translateToController (final int note)
    {
        return note - 36;
    }
}