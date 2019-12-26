// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.track;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to trigger the Add track.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AddTrackCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public AddTrackCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
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
