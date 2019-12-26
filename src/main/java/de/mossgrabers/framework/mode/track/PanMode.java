// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;


/**
 * The pan mode. The knobs control the panorama of the tracks on the current track page.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PanMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractTrackMode<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     */
    public PanMode (final S surface, final IModel model, final boolean isAbsolute)
    {
        super ("Panorama", surface, model, isAbsolute);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        if (track == null)
            return;
        if (this.isAbsolute)
            track.setPan (value);
        else
            track.changePan (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        if (!track.doesExist ())
            return;

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            track.resetPan ();
        }
        track.touchPan (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        return track == null ? -1 : track.getPan ();
    }
}