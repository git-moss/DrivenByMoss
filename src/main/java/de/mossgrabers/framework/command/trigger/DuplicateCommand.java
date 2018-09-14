// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;


/**
 * Command to duplicate an object (clip, track, ...) depending on the context.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DuplicateCommand<S extends ControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
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
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getSelectedTrack ();
        if (track == null || !track.doesExist ())
            return;

        // Is there a selected slot?
        final ISlot slot = track.getSelectedSlot ();
        if (slot == null)
            return;

        final boolean isPlaying = slot.isPlaying ();

        // Duplicate the clip in the selected slot
        slot.duplicate ();

        if (!isPlaying)
            return;

        // Need to wait a bit with starting the duplicated clip until it is selected
        this.model.getHost ().scheduleTask ( () -> {
            final ISlot slotNew = track.getSelectedSlot ();
            if (slotNew != null)
            {
                slotNew.launch ();
                return;
            }

            // Try to find the clip in the next page...
            track.scrollClipPageForwards ();
            this.model.getHost ().scheduleTask ( () -> {
                final ISlot slotNew2 = track.getSelectedSlot ();
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
