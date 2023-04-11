// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.mode;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.TrackType;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocol;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;

import java.util.List;
import java.util.Optional;


/**
 * The mixer mode.
 *
 * @author Jürgen Moßgraber
 */
public class MixerMode extends TrackVolumeMode<KontrolProtocolControlSurface, KontrolProtocolConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public MixerMode (final KontrolProtocolControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super (surface, model, false);

        this.setControls (controls);
        this.setParameterProvider (new CombinedParameterProvider (new VolumeParameterProvider (model), new PanParameterProvider (model)));
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        // Note: Since we need multiple value (more than 8), index is the MIDI CC of the knob

        final IValueChanger valueChanger = this.model.getValueChanger ();

        if (index >= KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME && index < KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME + 8)
        {
            final ITrack track = this.bank.getItem (index - KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME);
            return valueChanger.toMidiValue (track.getVolume ());
        }

        if (index >= KontrolProtocolControlSurface.KONTROL_TRACK_PAN && index < KontrolProtocolControlSurface.KONTROL_TRACK_PAN + 8)
        {
            final ITrack track = this.bank.getItem (index - KontrolProtocolControlSurface.KONTROL_TRACK_PAN);
            return valueChanger.toMidiValue (track.getPan ());
        }

        final Optional<ITrack> selectedTrack = this.bank.getSelectedItem ();
        final int scrollTracksState = (this.bank.canScrollBackwards () ? 1 : 0) + (this.bank.canScrollForwards () ? 2 : 0);
        int scrollClipsState = 0;
        if (selectedTrack.isPresent ())
        {
            final ISlotBank slotBank = selectedTrack.get ().getSlotBank ();
            scrollClipsState = (slotBank.canScrollBackwards () ? 1 : 0) + (slotBank.canScrollForwards () ? 2 : 0);
        }

        final ISceneBank sceneBank = this.model.getSceneBank ();
        final int scrollScenesState = (sceneBank.canScrollBackwards () ? 1 : 0) + (sceneBank.canScrollForwards () ? 2 : 0);

        final KontrolProtocolConfiguration configuration = this.surface.getConfiguration ();

        switch (index)
        {
            case KontrolProtocolControlSurface.KONTROL_NAVIGATE_BANKS:
                return (this.bank.canScrollPageBackwards () ? 1 : 0) + (this.bank.canScrollPageForwards () ? 2 : 0);
            case KontrolProtocolControlSurface.KONTROL_NAVIGATE_TRACKS:
                if (configuration.isFlipTrackClipNavigation ())
                    return configuration.isFlipClipSceneNavigation () ? scrollScenesState : scrollClipsState;
                return scrollTracksState;
            case KontrolProtocolControlSurface.KONTROL_NAVIGATE_CLIPS:
                if (configuration.isFlipTrackClipNavigation ())
                    return scrollTracksState;
                return configuration.isFlipClipSceneNavigation () ? scrollScenesState : scrollClipsState;
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();

        final int [] vuData = new int [16];
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = this.bank.getItem (i);

            // Track Available
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_AVAILABLE, TrackType.toTrackType (track.getType ()), i);
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_SELECTED, track.isSelected () ? 1 : 0, i);
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_RECARM, track.isRecArm () ? 1 : 0, i);
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME_TEXT, 0, i, track.getVolumeStr (8));
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_PAN_TEXT, 0, i, track.getPanStr (8));
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_NAME, 0, i, this.formatTrackName (track));

            final int j = 2 * i;
            vuData[j] = valueChanger.toMidiValue (track.getVuLeft ());
            vuData[j + 1] = valueChanger.toMidiValue (track.getVuRight ());
        }
        this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_VU, 2, 0, vuData);
    }


    private String formatTrackName (final ITrack track)
    {
        if (!track.doesExist ())
            return "";
        final String name = track.getName ();
        if (this.surface.getProtocolVersion () == KontrolProtocol.VERSION_1)
            return name;
        return "Track " + (track.getPosition () + 1) + "\n\n" + name;
    }
}
