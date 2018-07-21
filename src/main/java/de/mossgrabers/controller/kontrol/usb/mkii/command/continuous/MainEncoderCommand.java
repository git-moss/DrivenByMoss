// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.command.continuous;

import de.mossgrabers.controller.kontrol.usb.mki.mode.IKontrol1Mode;
import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2ControlSurface;
import de.mossgrabers.framework.command.continuous.MasterVolumeCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Mode;


/**
 * Command to change the Main encoder.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MainEncoderCommand extends MasterVolumeCommand<Kontrol2ControlSurface, Kontrol2Configuration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MainEncoderCommand (final IModel model, final Kontrol2ControlSurface surface)
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
