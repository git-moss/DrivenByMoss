// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.track;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * A command to select the previous or next track.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class SelectPrevNextTrackCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    protected boolean isLeft;


    /**
     * Constructor. Select the track at the given index in the page of the current track bank.
     *
     * @param model The model
     * @param surface The surface
     * @param isLeft True to select previous, otherwise next
     */
    public SelectPrevNextTrackCommand (final IModel model, final S surface, final boolean isLeft)
    {
        super (model, surface);

        this.isLeft = isLeft;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (this.isLeft)
            tb.selectPreviousItem ();
        else
            tb.selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (this.isLeft)
            tb.selectPreviousPage ();
        else
            tb.selectNextPage ();
    }
}
