// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mki.command.trigger;

import de.mossgrabers.controller.ni.kontrol.mki.Kontrol1Configuration;
import de.mossgrabers.controller.ni.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for pressing the Scale button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScaleButtonCommand extends AbstractTriggerCommand<Kontrol1ControlSurface, Kontrol1Configuration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public ScaleButtonCommand (final IModel model, final Kontrol1ControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.surface.getConfiguration ().toggleScaleIsActive ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.SCALES))
            modeManager.restore ();
        else
            modeManager.setTemporary (Modes.SCALES);
    }
}
