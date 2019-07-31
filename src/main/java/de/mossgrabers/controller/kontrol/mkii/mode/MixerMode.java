// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mkii.mode;

import de.mossgrabers.controller.kontrol.mkii.KontrolMkIIConfiguration;
import de.mossgrabers.controller.kontrol.mkii.TrackType;
import de.mossgrabers.controller.kontrol.mkii.controller.KontrolMkIIControlSurface;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ISlotBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.track.VolumeMode;


/**
 * The mixer mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MixerMode extends VolumeMode<KontrolMkIIControlSurface, KontrolMkIIConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MixerMode (final KontrolMkIIControlSurface surface, final IModel model)
    {
        super (surface, model, false);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final ITrackBank bank = this.getBank ();

        final int [] vuData = new int [16];
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = bank.getItem (i);

            // Track Available
            this.surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_AVAILABLE, TrackType.toTrackType (track.getType ()), i);
            this.surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_SELECTED, track.isSelected () ? 1 : 0, i);
            this.surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_RECARM, track.isRecArm () ? 1 : 0, i);
            this.surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_VOLUME_TEXT, 0, i, track.getVolumeStr (8));
            this.surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_PAN_TEXT, 0, i, track.getPanStr (8));
            this.surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_NAME, 0, i, track.doesExist () ? "Track " + (track.getPosition () + 1) + "\n\n" + track.getName () : "");

            final int j = 2 * i;
            vuData[j] = valueChanger.toMidiValue (track.getVuLeft ());
            vuData[j + 1] = valueChanger.toMidiValue (track.getVuRight ());

            this.surface.updateContinuous (KontrolMkIIControlSurface.KONTROL_TRACK_VOLUME + i, valueChanger.toMidiValue (track.getVolume ()));
            this.surface.updateContinuous (KontrolMkIIControlSurface.KONTROL_TRACK_PAN + i, valueChanger.toMidiValue (track.getPan ()));
        }
        this.surface.sendKontrolTrackSysEx (KontrolMkIIControlSurface.KONTROL_TRACK_VU, 2, 0, vuData);

        final ITrack selectedTrack = bank.getSelectedItem ();
        final int scrollTracksState = (bank.canScrollBackwards () ? 1 : 0) + (bank.canScrollForwards () ? 2 : 0);
        int scrollClipsState = 0;
        if (selectedTrack != null)
        {
            final ISlotBank slotBank = selectedTrack.getSlotBank ();
            scrollClipsState = (slotBank.canScrollBackwards () ? 1 : 0) + (slotBank.canScrollForwards () ? 2 : 0);
        }
        final ISceneBank sceneBank = bank.getSceneBank ();
        final int scrollScenesState = (sceneBank.canScrollBackwards () ? 1 : 0) + (sceneBank.canScrollForwards () ? 2 : 0);

        final KontrolMkIIConfiguration configuration = this.surface.getConfiguration ();
        this.surface.updateContinuous (KontrolMkIIControlSurface.KONTROL_NAVIGATE_BANKS, (bank.canScrollPageBackwards () ? 1 : 0) + (bank.canScrollPageForwards () ? 2 : 0));
        this.surface.updateContinuous (KontrolMkIIControlSurface.KONTROL_NAVIGATE_TRACKS, configuration.isFlipTrackClipNavigation () ? configuration.isFlipClipSceneNavigation () ? scrollScenesState : scrollClipsState : scrollTracksState);
        this.surface.updateContinuous (KontrolMkIIControlSurface.KONTROL_NAVIGATE_CLIPS, configuration.isFlipTrackClipNavigation () ? scrollTracksState : configuration.isFlipClipSceneNavigation () ? scrollScenesState : scrollClipsState);
        this.surface.updateContinuous (KontrolMkIIControlSurface.KONTROL_NAVIGATE_SCENES, configuration.isFlipTrackClipNavigation () ? scrollTracksState : configuration.isFlipClipSceneNavigation () ? scrollClipsState : scrollScenesState);
    }
}
