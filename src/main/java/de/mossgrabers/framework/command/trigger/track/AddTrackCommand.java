// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.track;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.resource.ChannelType;
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
    private final ButtonID combi1;
    private final ButtonID combi2;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public AddTrackCommand (final IModel model, final S surface)
    {
        this (model, surface, ButtonID.SHIFT, ButtonID.SELECT);
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param combi1 The button to be pressed for the first button combination to trigger inserting
     *            an effect track
     * @param combi2 The button to be pressed for the second button combination to trigger inserting
     *            an audio track
     */
    public AddTrackCommand (final IModel model, final S surface, final ButtonID combi1, final ButtonID combi2)
    {
        super (model, surface);

        this.combi1 = combi1;
        this.combi2 = combi2;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        ITrackBank tb = this.model.getTrackBank ();
        final IApplication application = this.model.getApplication ();
        if (this.combi1 != null && this.surface.isPressed (this.combi1))
        {
            application.addEffectTrack ();
            tb = this.model.getEffectTrackBank ();
            if (tb == null)
                return;
        }
        else if (this.combi2 != null && this.surface.isPressed (this.combi2))
            this.model.getTrackBank ().addChannel (ChannelType.AUDIO);
        else
            this.model.getTrackBank ().addChannel (ChannelType.INSTRUMENT);

        final ITrackBank bank = tb;
        this.surface.scheduleTask ( () -> {
            final int pos = bank.getItemCount () - 1;
            if (pos < 0)
            {
                this.surface.errorln ("Warning: No track created.");
                return;
            }
            bank.scrollTo (pos);
            bank.getItem (pos % bank.getPageSize ()).select ();
        }, 200);
    }
}
