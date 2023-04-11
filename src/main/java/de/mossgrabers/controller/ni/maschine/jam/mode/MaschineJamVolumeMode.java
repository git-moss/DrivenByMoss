// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.mode;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.FaderConfig;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;

import java.util.Optional;


/**
 * The volume mode.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamVolumeMode extends TrackVolumeMode<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineJamMode
{
    private static final FaderConfig FADER_OFF = new FaderConfig (FaderConfig.TYPE_DOT, 0, 0);


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MaschineJamVolumeMode (final MaschineJamControlSurface surface, final IModel model)
    {
        super (surface, model, true, ContinuousID.createSequentialList (ContinuousID.FADER1, 8));
    }


    /** {@inheritDoc} */
    @Override
    public FaderConfig setupFader (final int index)
    {
        final Optional<ITrack> optionalTrack = this.getTrack (index);
        if (optionalTrack.isEmpty ())
            return FADER_OFF;

        final ITrack track = optionalTrack.get ();
        if (!track.doesExist ())
            return FADER_OFF;

        final String c = DAWColor.getColorID (track.getColor ());
        final int color = this.colorManager.getColorIndex (c);
        final int value = this.model.getValueChanger ().toMidiValue (track.getVolume ());

        if (!this.model.getTransport ().isPlaying ())
            return new FaderConfig (FaderConfig.TYPE_SINGLE, color, value);

        final int vu = this.model.getValueChanger ().toMidiValue (track.getVu ());
        return new FaderConfig (FaderConfig.TYPE_DUAL, color, vu, value);
    }
}