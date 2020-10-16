// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.mode;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.controller.sl.controller.SLDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


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
    public FixedMode (final SLControlSurface surface, final IModel model)
    {
        super ("Fixed", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clearRow (0).setBlock (0, 0, "New Clip Length:").done (0);
        for (int i = 0; i < 8; i++)
            d.setCell (2, i, (this.surface.getConfiguration ().getNewClipLength () == i ? SLDisplay.RIGHT_ARROW : " ") + FixedMode.CLIP_LENGTHS[i]);
        d.done (2);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}
