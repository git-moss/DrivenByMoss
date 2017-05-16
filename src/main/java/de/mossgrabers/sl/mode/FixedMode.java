// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.sl.SLConfiguration;
import de.mossgrabers.sl.controller.SLControlSurface;
import de.mossgrabers.sl.controller.SLDisplay;


/**
 * Mode for selecting the length of new clips.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FixedMode extends AbstractMode<SLControlSurface, SLConfiguration>
{
    private static final String [] CLIP_LENGTHS =
    {
            "1 Beat",
            "2 Beats",
            "1 Bar",
            "2 Bars",
            "4 Bars",
            "8 Bars",
            "16 Bars",
            "32 Bars"
    };


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public FixedMode (final SLControlSurface surface, final Model model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Display d = this.surface.getDisplay ().clearRow (0).setBlock (0, 0, "New Clip Length:").done (0);
        for (int i = 0; i < 8; i++)
            d.setCell (2, i, (this.surface.getConfiguration ().getNewClipLength () == i ? SLDisplay.RIGHT_ARROW : " ") + FixedMode.CLIP_LENGTHS[i]);
        d.done (2);
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}
