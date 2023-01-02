// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.trigger;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.mode.NoteMode;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The button of the SELECT knob.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FireSelectButtonCommand extends AbstractTriggerCommand<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public FireSelectButtonCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (velocity > 0)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();

        if (modeManager.isActive (Modes.NOTE))
        {
            ((NoteMode) modeManager.get (Modes.NOTE)).resetTranspose ();
            return;
        }

        if (modeManager.isActive (Modes.BROWSER))
        {
            ((FireBrowserCommand) this.surface.getButton (ButtonID.BROWSE).getCommand ()).discardBrowser (true);
            return;
        }

        final FireConfiguration configuration = this.surface.getConfiguration ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (modeManager.isActive (Modes.DEVICE_PARAMS) && configuration.isDeleteModeActive ())
        {
            cursorDevice.remove ();
            configuration.toggleDeleteModeActive ();
            return;
        }

        cursorDevice.toggleWindowOpen ();
    }
}
