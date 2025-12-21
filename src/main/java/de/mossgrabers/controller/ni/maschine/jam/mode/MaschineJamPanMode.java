// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.mode;

import java.util.Optional;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.FaderConfig;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.track.TrackPanMode;


/**
 * The volume mode.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamPanMode extends TrackPanMode<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineJamMode
{
    private static final FaderConfig FADER_OFF  = new FaderConfig (FaderConfig.TYPE_DOT, 0, 0);

    private final FaderSlowChange          slowChange = new FaderSlowChange ();


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MaschineJamPanMode (final MaschineJamControlSurface surface, final IModel model)
    {
        super (surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final Optional<ITrack> track = this.getTrack (index);
        if (!track.isEmpty ())
            this.slowChange.changeValue (this.surface, track.get ().getPanParameter (), value);
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
        final int value = this.model.getValueChanger ().toMidiValue (track.getPan ());
        return new FaderConfig (FaderConfig.TYPE_PAN, color, value);
    }
}