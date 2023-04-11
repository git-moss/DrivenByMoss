// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.application;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Command to duplicate the selected clip and start playback, if any. Creates a new scene if used
 * with Select.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class DuplicateCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public DuplicateCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isSelectPressed ())
        {
            if (event == ButtonEvent.UP)
                this.model.getProject ().createScene ();
            return;
        }

        super.execute (event, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        // Is there a selected track?
        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
            return;

        // Is there a selected slot?
        final ISlotBank slotBank = cursorTrack.getSlotBank ();
        final Optional<ISlot> slot = slotBank.getSelectedItem ();
        if (slot.isEmpty ())
            return;

        final boolean isPlaying = slot.get ().isPlaying ();

        // Duplicate the clip in the selected slot
        slot.get ().duplicate ();

        if (!isPlaying)
            return;

        // Need to wait a bit with starting the duplicated clip until it is selected
        this.model.getHost ().scheduleTask ( () -> {
            final Optional<ISlot> slotNew = slotBank.getSelectedItem ();
            if (slotNew.isPresent ())
            {
                slotNew.get ().launch (true, false);
                return;
            }

            // Try to find the clip in the next page...
            slotBank.selectNextPage ();
            this.model.getHost ().scheduleTask ( () -> {
                final Optional<ISlot> slotNew2 = slotBank.getSelectedItem ();
                if (slotNew2.isPresent ())
                    slotNew2.get ().launch (true, false);
            }, 200);
        }, 200);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.model.getProject ().createSceneFromPlayingLauncherClips ();
    }
}
