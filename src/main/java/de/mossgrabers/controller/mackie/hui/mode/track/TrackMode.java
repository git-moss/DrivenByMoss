// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui.mode.track;

import java.util.Optional;

import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.mode.Modes;


/**
 * Mode for editing the parameters of a track.
 *
 * @author Jürgen Moßgraber
 */
public class TrackMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TrackMode (final HUIControlSurface surface, final IModel model)
    {
        super (Modes.NAME_TRACK, surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        if (extenderOffset > 0)
            return;

        final Optional<ITrack> trackOpt = this.model.getTrackBank ().getSelectedItem ();
        if (!trackOpt.isPresent ())
            return;

        final ITrack track = trackOpt.get ();
        switch (index)
        {
            case 0:
                track.changeVolume (value);
                break;
            case 1:
                track.changePan (value);
                break;
            default:
                track.getSendBank ().getItem (index - 2).changeValue (value);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (!this.surface.getConfiguration ().hasDisplay1 ())
            return;

        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final Optional<ITrack> trackOpt = this.model.getTrackBank ().getSelectedItem ();
        if (trackOpt.isPresent () && this.surface.getExtenderOffset () == 0)
        {
            d.setCell (0, 0, "Vol");
            d.setCell (0, 1, "Pan");
            d.setCell (0, 2, "S1");
            d.setCell (0, 3, "S2");
            d.setCell (0, 4, "S3");
            d.setCell (0, 5, "S4");
            d.setCell (0, 6, "S5");
            d.setCell (0, 7, "S6");
        }
        else
            d.setRow (0, "Please select a track.");

        d.done (0);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        if (selectedTrack.isPresent () && this.surface.getExtenderOffset () == 0)
        {
            final ITrack track = selectedTrack.get ();
            this.surface.setKnobLED (0, HUIControlSurface.KNOB_LED_MODE_WRAP, Math.max (track.getVolume (), 1), upperBound);
            this.surface.setKnobLED (1, HUIControlSurface.KNOB_LED_MODE_BOOST_CUT, Math.max (track.getPan (), 1), upperBound);

            final ISendBank sendBank = track.getSendBank ();
            for (int i = 0; i < 6; i++)
                this.surface.setKnobLED (2 + i, HUIControlSurface.KNOB_LED_MODE_WRAP, Math.max (sendBank.getItem (i).getValue (), 1), upperBound);
        }
        else
        {
            for (int i = 0; i < 8; i++)
                this.surface.setKnobLED (i, HUIControlSurface.KNOB_LED_MODE_OFF, 0, 0);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void resetParameter (final int index)
    {
        final Optional<ITrack> trackOpt = this.model.getTrackBank ().getSelectedItem ();
        if (!trackOpt.isPresent () || this.surface.getExtenderOffset () > 0)
            return;

        final ITrack track = trackOpt.get ();
        switch (index)
        {
            case 0:
                track.resetVolume ();
                break;
            case 1:
                track.resetPan ();
                break;
            default:
                track.getSendBank ().getItem (index - 2).resetValue ();
                break;
        }
    }
}