// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;


/**
 * The volume mode. The knobs control the volumes of the tracks on the current track page.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class TrackVolumeMode<S extends IControlSurface<C>, C extends Configuration> extends DefaultTrackMode<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     */
    public TrackVolumeMode (final S surface, final IModel model, final boolean isAbsolute)
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
    public TrackVolumeMode (final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls)
    {
        this (surface, model, isAbsolute, controls, surface::isShiftPressed);
    }


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     * @param isAlternativeFunction Callback function to execute the secondary function, e.g. a
     *            shift button
     */
    public TrackVolumeMode (final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls, final BooleanSupplier isAlternativeFunction)
    {
        super (Modes.NAME_VOLUME, surface, model, isAbsolute, controls, isAlternativeFunction);

        if (controls != null)
            this.setParameterProvider (new VolumeParameterProvider (model));
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final Optional<ITrack> track = this.getTrack (index);
        if (track.isEmpty ())
            return;
        final ITrack t = track.get ();
        if (this.isAbsolute)
            t.setVolume (value);
        else
            t.changeVolume (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final Optional<ITrack> track = this.getTrack (index);
        if (track.isEmpty ())
            return;

        final ITrack t = track.get ();
        if (isTouched && this.surface.isDeletePressed ())
            t.resetVolume ();
        t.touchVolume (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final Optional<ITrack> track = this.getTrack (index);
        return track.isEmpty () ? -1 : track.get ().getVolume ();
    }
}