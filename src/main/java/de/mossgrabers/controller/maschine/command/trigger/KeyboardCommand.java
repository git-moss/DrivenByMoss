// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.command.trigger;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.controller.maschine.view.PlayView;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Command for the keybaord button which activates the play view or toggles chromatic mode if
 * already active.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KeyboardCommand extends AbstractTriggerCommand<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public KeyboardCommand (final IModel model, final MaschineControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.isActiveView (Views.PLAY))
        {
            if (!this.surface.getMaschine ().hasMCUDisplay ())
                ((PlayView) viewManager.getView (Views.PLAY)).toggleShifted ();

            final ModeManager modeManager = this.surface.getModeManager ();
            if (modeManager.isActiveOrTempMode (Modes.SCALES))
                modeManager.restoreMode ();
            else
                modeManager.setActiveMode (Modes.SCALES);
        }
        else
            viewManager.setActiveView (Views.PLAY);
    }
}
