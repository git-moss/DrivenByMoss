// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.SlotData;
import de.mossgrabers.framework.daw.data.TrackData;


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
    public DuplicateCommand (final Model model, final S surface)
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
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData trackData = tb.getSelectedTrack ();
        if (trackData == null || !trackData.doesExist ())
            return;

        // Is there a selected slot?
        final int trackIndex = trackData.getIndex ();
        final SlotData slotData = tb.getSelectedSlot (trackIndex);
        if (slotData == null)
            return;

        final boolean isPlaying = slotData.isPlaying ();

        // Duplicate the clip in the selected slot
        tb.duplicateClip (trackIndex, slotData.getIndex ());

        if (!isPlaying)
            return;

        // Need to wait a bit with starting the duplicated clip until it is selected
        this.model.getHost ().scheduleTask ( () -> {
            final SlotData slotDataNew = tb.getSelectedSlot (trackIndex);
            if (slotDataNew != null)
            {
                tb.launchClip (trackIndex, slotDataNew.getIndex ());
                return;
            }

            // Try to find the clip in the next page...
            tb.scrollClipPageForwards (trackIndex);
            this.model.getHost ().scheduleTask ( () -> {
                final SlotData slotDataNew2 = tb.getSelectedSlot (trackIndex);
                if (slotDataNew2 != null)
                    tb.launchClip (trackIndex, slotDataNew2.getIndex ());
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
