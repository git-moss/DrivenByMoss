// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.command.trigger;

import de.mossgrabers.controller.maschine.mikro.mk3.MaschineMikroMk3Configuration;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.command.trigger.track.AddTrackCommand;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the Add track. If the browser is open this command cancels and discards the
 * browsing.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ProjectButtonCommand extends AddTrackCommand<MaschineMikroMk3ControlSurface, MaschineMikroMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ProjectButtonCommand (final IModel model, final MaschineMikroMk3ControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (browser.isActive ())
            browser.stopBrowsing (false);
        else
            super.execute (event, velocity);
    }
}
