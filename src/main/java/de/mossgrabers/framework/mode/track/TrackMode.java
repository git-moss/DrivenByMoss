// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.parameterprovider.track.SelectedTrackParameterProvider;

import java.util.List;
import java.util.Optional;


/**
 * The track mode. The knobs control the volume, the panorama and the sends of the selected track.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode<S extends IControlSurface<C>, C extends Configuration> extends DefaultTrackMode<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     */
    public TrackMode (final S surface, final IModel model, final boolean isAbsolute)
    {
        this (surface, model, isAbsolute, null);
    }


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public TrackMode (final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls)
    {
        this ("Track", surface, model, isAbsolute, controls);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public TrackMode (final String name, final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls)
    {
        super (name, surface, model, isAbsolute, controls);

        if (controls != null)
            this.setParameterProvider (new SelectedTrackParameterProvider (model));
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final Optional<ITrack> track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track.isEmpty ())
            return;
        final ITrack t = track.get ();
        switch (index)
        {
            case 0:
                if (this.isAbsolute)
                    t.setVolume (value);
                else
                    t.changeVolume (value);
                break;

            case 1:
                if (this.isAbsolute)
                    t.setPan (value);
                else
                    t.changePan (value);
                break;

            default:
                final ISendBank sendBank = t.getSendBank ();
                if (!sendBank.hasExistingItems ())
                    return;
                final ISend send = sendBank.getItem (index - 2);
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
        final Optional<ITrack> track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track.isEmpty ())
            return;

        final ITrack t = track.get ();
        switch (index)
        {
            case 0:
                if (isTouched && this.surface.isDeletePressed ())
                    t.resetVolume ();
                t.touchVolume (isTouched);
                break;

            case 1:
                if (isTouched && this.surface.isDeletePressed ())
                    t.resetPan ();
                t.touchPan (isTouched);
                break;

            default:
                final ISendBank sendBank = t.getSendBank ();
                if (!sendBank.hasExistingItems ())
                    return;

                final ISend item = sendBank.getItem (index - 2);
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
        final Optional<ITrack> track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track.isEmpty ())
            return -1;

        final ITrack t = track.get ();
        switch (index)
        {
            case 0:
                return t.getVolume ();

            case 1:
                return t.getPan ();

            default:
                final ISendBank sendBank = t.getSendBank ();
                if (!sendBank.hasExistingItems ())
                    return 0;
                return sendBank.getItem (index - 2).getValue ();
        }
    }
}