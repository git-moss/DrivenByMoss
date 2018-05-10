// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;


/**
 * Implementation of the APC grid of pads.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCPadGrid extends PadGridImpl
{
    private final APCControlSurface surface;


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param surface The APC surface
     */
    public APCPadGrid (final ColorManager colorManager, final APCControlSurface surface)
    {
        super (colorManager, surface.getOutput ());
        this.surface = surface;
    }


    /** {@inheritDoc} */
    @Override
    public void lightEx (final int x, final int y, final int color, final int blinkColor, final boolean fast)
    {
        this.setLight (68 + x - 8 * y, color, blinkColor, fast);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int note, final int color)
    {
        final int i = note - 36;
        if (this.surface.isMkII ())
        {
            this.output.sendNote (i, color);
        }
        else
        {
            final int x = i % 8;
            final int y = 4 - i / 8;
            this.output.sendNoteEx (x, APCControlSurface.APC_BUTTON_CLIP_LAUNCH_1 + y, color);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void sendBlinkState (final int note, final int blinkColor, final boolean fast)
    {
        final int i = note - 36;
        if (this.surface.isMkII ())
        {
            this.output.sendNoteEx (fast ? 12 : 10, i, blinkColor);
        }
        else
        {
            final int x = i % 8;
            final int y = 4 - i / 8;
            this.output.sendNoteEx (x, APCControlSurface.APC_BUTTON_CLIP_LAUNCH_1 + y, blinkColor);
        }
    }
}