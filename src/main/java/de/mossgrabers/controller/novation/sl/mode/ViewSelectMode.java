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
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Mode for selecting the view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ViewSelectMode extends AbstractParameterMode<SLControlSurface, SLConfiguration, IItem>
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
        final ITextDisplay d = this.surface.getTextDisplay ().clearRow (0).clearRow (1);
        final ViewManager viewManager = this.surface.getViewManager ();
        d.setBlock (0, 0, "Select mode:");
        d.setCell (1, 0, (viewManager.isActive (Views.CONTROL) ? SLDisplay.RIGHT_ARROW : " ") + "Control");
        d.setCell (1, 1, " " + (viewManager.isActive (Views.PLAY) ? SLDisplay.RIGHT_ARROW : " ") + "Play");
        d.done (0).done (1);
    }
}
