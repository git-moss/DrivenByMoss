// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.track;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * A command to toggle mute on a track. Additionally, toggles monitor if shifted.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class MuteCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private int index;


    /**
     * Constructor. Toggles the mute of the currently selected track, if any.
     *
     * @param model The model
     * @param surface The surface
     */
    public MuteCommand (final IModel model, final S surface)
    {
        this (-1, model, surface);
    }


    /**
     * Constructor. Toggles the mute of the track at the given index in the page of the current
     * track bank.
     *
     * @param index The channel index
     * @param model The model
     * @param surface The surface
     */
    public MuteCommand (final int index, final IModel model, final S surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
        if (this.index == -1)
        {
            final Optional<ITrack> selectedItem = currentTrackBank.getSelectedItem ();
            if (selectedItem.isPresent ())
                selectedItem.get ().toggleMute ();
        }
        else
            currentTrackBank.getItem (this.index).toggleMute ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
        if (this.index == -1)
        {
            final Optional<ITrack> selectedItem = currentTrackBank.getSelectedItem ();
            if (selectedItem.isPresent ())
                selectedItem.get ().toggleMonitor ();
        }
        else
            currentTrackBank.getItem (this.index).toggleMonitor ();
    }
}
