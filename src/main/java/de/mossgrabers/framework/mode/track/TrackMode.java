// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;


/**
 * The track mode. The knobs control the volume, the panorama and the sends of the selected track.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractTrackMode<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     */
    public TrackMode (final S surface, final IModel model, final boolean isAbsolute)
    {
        super ("Track", surface, model, isAbsolute);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track == null)
            return;
        switch (index)
        {
            case 0:
                if (this.isAbsolute)
                    track.setVolume (value);
                else
                    track.changeVolume (value);
                break;

            case 1:
                if (this.isAbsolute)
                    track.setPan (value);
                else
                    track.changePan (value);
                break;

            default:
                final ISend send = track.getSendBank ().getItem (index - 2);
                if (this.isAbsolute)
                    send.setValue (value);
                else
                    send.changeValue (value);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track == null)
            return;

        switch (index)
        {
            case 0:
                if (isTouched && this.surface.isDeletePressed ())
                    track.resetVolume ();
                track.touchVolume (isTouched);
                break;

            case 1:
                if (isTouched && this.surface.isDeletePressed ())
                    track.resetPan ();
                break;

            default:
                final ISend item = track.getSendBank ().getItem (index - 2);
                if (isTouched && this.surface.isDeletePressed ())
                    item.resetValue ();
                item.touchValue (isTouched);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        if (track == null)
            return -1;
        switch (index)
        {
            case 0:
                return track.getVolume ();

            case 1:
                return track.getPan ();

            default:
                return track.getSendBank ().getItem (index - 2).getValue ();
        }
    }
}