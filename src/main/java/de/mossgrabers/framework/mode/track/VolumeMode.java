// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;


/**
 * The volume mode. The knobs control the volumes of the tracks on the current track page.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumeMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractMode<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happending with a setter otherwise relative
     *            change method is used
     */
    public VolumeMode (final S surface, final IModel model, final boolean isAbsolute)
    {
        super ("Volume", surface, model, isAbsolute);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        if (track == null)
            return;
        if (this.isAbsolute)
            track.setVolume (value);
        else
            track.changeVolume (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (!isTouched)
            return;
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        if (track != null)
            track.resetVolume ();
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        return track == null ? -1 : track.getPan ();
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedItemName ()
    {
        final ITrack selectedItem = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedItem == null || !selectedItem.doesExist ())
            return null;
        return selectedItem.getPosition () + 1 + ": " + selectedItem.getName ();
    }
}