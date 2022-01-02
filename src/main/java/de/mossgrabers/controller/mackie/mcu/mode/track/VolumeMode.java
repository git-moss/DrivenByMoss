// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.track;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;


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
    public VolumeMode (final MCUControlSurface surface, final IModel model)
    {
        super (Modes.NAME_VOLUME, surface, model);

        final IParameterProvider parameterProvider;
        if (surface.getConfiguration ().shouldPinFXTracksToLastController () && surface.isLastDevice ())
            parameterProvider = new VolumeParameterProvider (model.getEffectTrackBank ());
        else
        {
            final int surfaceID = surface.getSurfaceID ();
            parameterProvider = new RangeFilterParameterProvider (new VolumeParameterProvider (model), surfaceID * 8, 8);
        }
        this.setParameterProvider (parameterProvider);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        final int channel = this.getExtenderOffset () + index;
        final ITrack t = this.getTrackBank ().getItem (channel);
        if (t.doesExist ())
            t.touchVolume (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (!this.surface.getConfiguration ().hasDisplay1 ())
            return;

        this.drawDisplay2 ();
        if (!this.drawTrackHeader ())
            return;

        final ITextDisplay d = this.surface.getTextDisplay ();
        final ITrackBank tb = this.getTrackBank ();
        final int extenderOffset = this.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            d.setCell (1, i, t.getVolumeStr (6));
        }
        d.done (1);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean drawTrackHeader ()
    {
        if (!super.drawTrackHeader ())
            return false;

        if (this.surface.getConfiguration ().isDisplayTrackNames ())
            return true;

        final ITrackBank tb = this.getTrackBank ();
        final ITextDisplay d = this.surface.getTextDisplay ();
        final int extenderOffset = this.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            if (tb.getItem (extenderOffset + i).doesExist ())
                d.setCell (0, i, "Volume");
            else
                d.clearCell (0, i);
        }
        d.done (0);

        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final ITrackBank tb = this.getTrackBank ();
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        final int extenderOffset = this.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, t.getVolume (), upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final int extenderOffset = this.getExtenderOffset ();
        this.getTrackBank ().getItem (extenderOffset + index).resetVolume ();
    }
}