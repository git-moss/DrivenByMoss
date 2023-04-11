// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.clip;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Stop the playing clip on the given track. Return to arrangement if shifted.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class StopClipCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    protected int index;


    /**
     * Constructor. Stops the playing clip of the currently selected track, if any.
     *
     * @param model The model
     * @param surface The surface
     */
    public StopClipCommand (final IModel model, final S surface)
    {
        this (-1, model, surface);
    }


    /**
     * Constructor.
     *
     * @param index The channel index
     * @param model The model
     * @param surface The surface
     */
    public StopClipCommand (final int index, final IModel model, final S surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
        final Optional<ITrack> track = this.index == -1 ? currentTrackBank.getSelectedItem () : Optional.of (currentTrackBank.getItem (this.index));
        if (track.isPresent ())
            track.get ().stop ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
        final Optional<ITrack> track = this.index == -1 ? currentTrackBank.getSelectedItem () : Optional.of (currentTrackBank.getItem (this.index));
        if (track.isPresent ())
            track.get ().returnToArrangement ();
    }
}
