// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.mode;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.FaderConfig;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.track.TrackSendMode;

import java.util.Optional;


/**
 * The volume mode.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamSendMode extends TrackSendMode<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineJamMode
{
    private static final FaderConfig FADER_OFF = new FaderConfig (FaderConfig.TYPE_DOT, 0, 0);


    /**
     * Constructor.
     *
     * @param sendIndex The send index, if negative the sends of the selected track are edited
     * @param surface The control surface
     * @param model The model
     */
    public MaschineJamSendMode (final int sendIndex, final MaschineJamControlSurface surface, final IModel model)
    {
        super (sendIndex, surface, model, true, ContinuousID.createSequentialList (ContinuousID.FADER1, 8));
    }


    /** {@inheritDoc} */
    @Override
    public FaderConfig setupFader (final int index)
    {
        if (this.model.isEffectTrackBankActive ())
            return FADER_OFF;

        final Optional<ITrack> optionalTrack = this.getTrack (index);
        if (optionalTrack.isEmpty ())
            return FADER_OFF;

        final ITrack track = optionalTrack.get ();
        if (!track.doesExist ())
            return FADER_OFF;

        final ISend send = track.getSendBank ().getItem (this.sendIndex);
        if (!send.doesExist ())
            return FADER_OFF;

        final int value = this.model.getValueChanger ().toMidiValue (send.getValue ());
        return new FaderConfig (FaderConfig.TYPE_SINGLE, MaschineColorManager.COLOR_DARK_GREY, value);
    }
}