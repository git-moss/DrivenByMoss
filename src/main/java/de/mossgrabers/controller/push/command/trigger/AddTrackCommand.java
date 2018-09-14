// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the Add track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AddTrackCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public AddTrackCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        ITrackBank tb = this.model.getTrackBank ();
        final IApplication application = this.model.getApplication ();
        if (this.surface.isShiftPressed ())
        {
            application.addEffectTrack ();
            tb = this.model.getEffectTrackBank ();
        }
        else if (this.surface.isSelectPressed ())
            application.addAudioTrack ();
        else
            application.addInstrumentTrack ();

        final ITrackBank bank = tb;
        this.surface.scheduleTask ( () -> {
            final int pos = bank.getItemCount () - 1;
            bank.scrollTo (pos);
            bank.getItem (pos % bank.getPageSize ()).select ();
        }, 200);
    }
}
