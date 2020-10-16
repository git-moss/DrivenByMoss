// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.mode;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode for modifying several play/sequence options.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayOptionsMode extends AbstractMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PlayOptionsMode (final SLControlSurface surface, final IModel model)
    {
        super ("Play options", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ();
        d.clearRow (2).setCell (2, 0, "Oct Down").setCell (2, 1, " Oct Up").setCell (2, 2, "Res Down").setCell (2, 3, " Res Up").setCell (2, 4, "  Left").setCell (2, 5, "  Right").setCell (2, 7, "Play/Seq").clearRow (0).done (0).done (2);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}