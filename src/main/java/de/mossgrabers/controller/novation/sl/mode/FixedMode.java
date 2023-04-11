// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.mode;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.controller.novation.sl.controller.SLDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;


/**
 * Mode for selecting the length of new clips.
 *
 * @author Jürgen Moßgraber
 */
public class FixedMode extends AbstractParameterMode<SLControlSurface, SLConfiguration, IItem>
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
        final ITextDisplay d = this.surface.getTextDisplay ().clearRow (0).clearRow (1);
        d.setBlock (0, 0, "New Clip Length:");
        final int newClipLength = this.surface.getConfiguration ().getNewClipLength ();
        for (int i = 0; i < 8; i++)
            d.setCell (1, i, (newClipLength == i ? SLDisplay.RIGHT_ARROW : " ") + FixedMode.CLIP_LENGTHS[i]);
        d.done (0).done (1);
    }
}
