// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.command.trigger;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.launchpad.view.Views;


/**
 * Track panorama command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PanCommand extends AbstractTrackCommand
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PanCommand (final IModel model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        this.onFaderModeButton (event, Views.VIEW_PAN, "Pan");
    }
}
