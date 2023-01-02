// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.command.trigger;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the P2 buttons.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class P2ButtonCommand extends AbstractTriggerCommand<SLControlSurface, SLConfiguration>
{
    private final boolean isUp;


    /**
     * Constructor.
     *
     * @param isUp True if is up button
     * @param model The model
     * @param surface The surface
     */
    public P2ButtonCommand (final boolean isUp, final IModel model, final SLControlSurface surface)
    {
        super (model, surface);

        this.isUp = isUp;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (this.isUp)
            tb.selectNextPage ();
        else
            tb.selectPreviousPage ();
    }
}
