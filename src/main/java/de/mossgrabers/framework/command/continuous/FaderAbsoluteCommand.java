// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.continuous;

import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;


/**
 * Command to change the volumes of the current track bank.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FaderAbsoluteCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractContinuousCommand<S, C>
{
    private final int index;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public FaderAbsoluteCommand (final int index, final IModel model, final S surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
        final ITrack track = currentTrackBank.getItem (this.index);
        if (!track.doesExist ())
            return;

        if (this.surface.isShiftPressed ())
        {
            final int volume = track.getVolume ();
            this.surface.getDisplay ().notify (getNotificationText (value, volume));
        }
        else
            track.setVolume (value);
    }


    private static String getNotificationText (final int value, final int volume)
    {
        if (volume < value)
            return "Move down";
        return volume > value ? "Move up" : "Perfect!";
    }
}
