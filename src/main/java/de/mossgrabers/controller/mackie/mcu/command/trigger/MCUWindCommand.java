// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IMarkerBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command handle the rewind and fast forward buttons.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUWindCommand extends WindCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param isFastForward If true the command executes fast forwarding otherwise rewinding
     */
    public MCUWindCommand (final IModel model, final MCUControlSurface surface, final boolean isFastForward)
    {
        super (model, surface, isFastForward);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (this.surface.isSelectPressed ())
        {
            if (event != ButtonEvent.DOWN)
                return;
            final IMarkerBank markerBank = this.model.getMarkerBank ();
            if (this.isFastForwarding)
                markerBank.selectNextItem ();
            else
                markerBank.selectPreviousItem ();
            return;
        }

        super.executeNormal (event);
    }
}
