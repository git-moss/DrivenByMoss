// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.kontrol1.Kontrol1Configuration;
import de.mossgrabers.kontrol1.controller.Kontrol1ControlSurface;
import de.mossgrabers.kontrol1.mode.Modes;
import de.mossgrabers.kontrol1.view.Views;


/**
 * Command for pressing the Enter button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EnterButtonCommand extends AbstractTriggerCommand<Kontrol1ControlSurface, Kontrol1Configuration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public EnterButtonCommand (final IModel model, final Kontrol1ControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_PARAMS))
        {
            // No function
            return;
        }

        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
        {
            ((BrowserCommand) this.surface.getViewManager ().getView (Views.VIEW_CONTROL).getTriggerCommand (Commands.COMMAND_BROWSE)).startBrowser (false, false);
            return;
        }

        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedTrack ();
        if (selectedTrack != null)
            selectedTrack.toggleSolo ();
    }
}
