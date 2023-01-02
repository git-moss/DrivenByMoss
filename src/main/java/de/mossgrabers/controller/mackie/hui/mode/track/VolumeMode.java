// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui.mode.track;

import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;

import java.util.Optional;


/**
 * Mode for editing a volume parameter of all tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumeMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public VolumeMode (final HUIControlSurface surface, final IModel model)
    {
        super ("Volume", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final Optional<ITrack> track = this.getTrack (index);
        if (track.isPresent ())
            track.get ().changeVolume (value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (this.surface.getConfiguration ().hasDisplay1 ())
            this.drawTrackHeader ().setCell (0, 8, "Volu").done (0);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        for (int i = 0; i < 8; i++)
        {
            final Optional<ITrack> track = this.getTrack (i);
            this.surface.setKnobLED (i, HUIControlSurface.KNOB_LED_MODE_WRAP, track.isPresent () ? track.get ().getVolume () : 0, upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final Optional<ITrack> track = this.getTrack (index);
        if (track.isPresent ())
            track.get ().resetVolume ();
    }
}