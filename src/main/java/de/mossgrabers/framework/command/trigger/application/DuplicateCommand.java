// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
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


/**
 * Command to duplicate an object (clip, track, ...) depending on the context.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
        final ISlot slot = slotBank.getSelectedItem ();
        if (slot == null)
            return;

        final boolean isPlaying = slot.isPlaying ();

        // Duplicate the clip in the selected slot
        slot.duplicate ();

        if (!isPlaying)
            return;

        // Need to wait a bit with starting the duplicated clip until it is selected
        this.model.getHost ().scheduleTask ( () -> {
            final ISlot slotNew = slotBank.getSelectedItem ();
            if (slotNew != null)
            {
                slotNew.launch ();
                return;
            }

            // Try to find the clip in the next page...
            slotBank.selectNextPage ();
            this.model.getHost ().scheduleTask ( () -> {
                final ISlot slotNew2 = slotBank.getSelectedItem ();
                if (slotNew2 != null)
                    slotNew2.launch ();
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
