// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui.mode.track;

import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Modes;

import java.util.Optional;


/**
 * Mode for editing the panorama of all tracks.
 *
 * @author Jürgen Moßgraber
 */
public class PanMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public PanMode (final HUIControlSurface surface, final IModel model)
    {
        super (Modes.NAME_PANORAMA, surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final Optional<ITrack> track = this.getTrack (index);
        if (track.isPresent ())
            track.get ().changePan (value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (this.surface.getConfiguration ().hasDisplay1 ())
            this.drawTrackHeader ().setCell (0, 8, "Pans").done (0);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        for (int i = 0; i < 8; i++)
        {
            final Optional<ITrack> t = this.getTrack (i);
            if (t.isPresent ())
                this.surface.setKnobLED (i, HUIControlSurface.KNOB_LED_MODE_BOOST_CUT, Math.max (t.get ().getPan (), 1), upperBound);
            else
                this.surface.setKnobLED (i, HUIControlSurface.KNOB_LED_MODE_OFF, 0, 0);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final Optional<ITrack> track = this.getTrack (index);
        if (track.isPresent ())
            track.get ().resetPan ();
    }
}