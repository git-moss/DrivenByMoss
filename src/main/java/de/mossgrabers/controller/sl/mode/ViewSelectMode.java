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
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Mode for selecting the view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ViewSelectMode extends AbstractMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ViewSelectMode (final SLControlSurface surface, final IModel model)
    {
        super ("View select", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ();
        final ViewManager viewManager = this.surface.getViewManager ();
        for (int i = 0; i < 2; i++)
        {
            d.clearRow (0 + i).setBlock (0 + i, 0, "Select mode:").done (0 + i);
            d.clearRow (2 + i);
            d.setCell (2 + i, 0, (viewManager.isActive (Views.CONTROL) ? SLDisplay.RIGHT_ARROW : " ") + "Control");
            d.setCell (2 + i, 1, " " + (viewManager.isActive (Views.PLAY) ? SLDisplay.RIGHT_ARROW : " ") + "Play");
            d.done (2 + i);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}
