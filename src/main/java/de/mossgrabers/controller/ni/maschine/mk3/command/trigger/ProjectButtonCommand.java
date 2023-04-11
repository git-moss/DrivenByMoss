// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.trigger;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.trigger.track.AddTrackCommand;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the Add track. If the browser is open this command cancels and discards the
 * browsing.
 *
 * @author Jürgen Moßgraber
 */
public class ProjectButtonCommand extends AddTrackCommand<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ProjectButtonCommand (final IModel model, final MaschineControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.surface.getMaschine ().hasMCUDisplay () || this.surface.isShiftPressed ())
        {
            this.model.getProject ().save ();
            return;
        }

        final IBrowser browser = this.model.getBrowser ();
        if (browser.isActive ())
            browser.stopBrowsing (false);
        else
            super.execute (event, velocity);
    }
}
