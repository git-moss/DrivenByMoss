// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.mode;

import java.util.Optional;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.FaderConfig;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.track.TrackSendMode;


/**
 * The volume mode.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamSendMode extends TrackSendMode<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineJamMode
{
    private static final FaderConfig FADER_OFF  = new FaderConfig (FaderConfig.TYPE_DOT, 0, 0);

    private final FaderSlowChange          slowChange = new FaderSlowChange ();


    /**
     * Constructor.
     *
     * @param sendIndex The send index, if negative the sends of the selected track are edited
     * @param surface The control surface
     * @param model The model
     */
    public MaschineJamSendMode (final int sendIndex, final MaschineJamControlSurface surface, final IModel model)
    {
        super (sendIndex, surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final Optional<ITrack> track = this.getTrack (index);
        if (track.isEmpty ())
            return;
        final ISend item = track.get ().getSendBank ().getItem (this.sendIndex);
        if (item.doesExist ())
            this.slowChange.changeValue (this.surface, item, value);
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

        final ISend send = track.getSendBank ().getItem (this.sendIndex);
        if (!send.doesExist ())
            return FADER_OFF;

        final int value = this.model.getValueChanger ().toMidiValue (send.getValue ());
        return new FaderConfig (FaderConfig.TYPE_SINGLE, MaschineColorManager.COLOR_DARK_GREY, value);
    }
}