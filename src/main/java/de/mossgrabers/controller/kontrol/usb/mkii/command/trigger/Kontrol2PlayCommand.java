// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.command.trigger;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2ControlSurface;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the play button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol2PlayCommand extends PlayCommand<Kontrol2ControlSurface, Kontrol2Configuration>
{
    private final NewCommand<Kontrol2ControlSurface, Kontrol2Configuration> newCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public Kontrol2PlayCommand (final IModel model, final Kontrol2ControlSurface surface)
    {
        super (model, surface);
        this.newCommand = new NewCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.newCommand.executeNormal (event);
    }
}
