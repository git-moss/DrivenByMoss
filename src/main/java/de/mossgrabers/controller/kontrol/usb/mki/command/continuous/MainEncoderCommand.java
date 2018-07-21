// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mki.command.continuous;

import de.mossgrabers.controller.kontrol.usb.mki.Kontrol1Configuration;
import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.controller.kontrol.usb.mki.mode.IKontrol1Mode;
import de.mossgrabers.framework.command.continuous.MasterVolumeCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Mode;


/**
 * Command to change the Main encoder.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MainEncoderCommand extends MasterVolumeCommand<Kontrol1ControlSurface, Kontrol1Configuration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MainEncoderCommand (final IModel model, final Kontrol1ControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        if (this.surface.isShiftPressed ())
        {
            super.execute (value);
            return;
        }

        final Mode activeMode = this.surface.getModeManager ().getActiveOrTempMode ();
        ((IKontrol1Mode) activeMode).onMainKnob (value);
    }
}
