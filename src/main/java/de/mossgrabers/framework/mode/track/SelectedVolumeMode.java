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
 * The selected volume mode. All knobs control the panorama of the selected track.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectedVolumeMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractMode<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SelectedVolumeMode (final S surface, final IModel model)
    {
        super ("Volume", surface, model, false);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack != null)
            selectedTrack.changeVolume (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        if (!isTouched)
            return;
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack != null)
            selectedTrack.resetVolume ();
    }
}