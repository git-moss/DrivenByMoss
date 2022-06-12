// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.track;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SelectedTrackParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Arrays;
import java.util.Optional;


/**
 * Mode for editing a track parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TrackMode (final MCUControlSurface surface, final IModel model)
    {
        super ("Track", surface, model);

        if (surface.getConfiguration ().shouldPinFXTracksToLastController () && surface.isLastDevice ())
            this.setParameterProvider (new RangeFilterParameterProvider (new SelectedTrackParameterProvider (model.getEffectTrackBank ()), 0, 8));
        else
        {
            final int surfaceID = surface.getSurfaceID ();
            if (surfaceID == 0)
                this.setParameterProvider (new RangeFilterParameterProvider (new SelectedTrackParameterProvider (model), 0, 8));
            else if (surfaceID == 1)
                this.setParameterProvider (new RangeFilterParameterProvider (new SendParameterProvider (model, -1, 6), 0, 8));
            else
                this.setParameterProvider (new EmptyParameterProvider (8));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final Optional<ITrack> selectedTrack = this.getSelectedTrack ();
        if (selectedTrack.isEmpty ())
            return;
        final ITrack track = selectedTrack.get ();

        this.setTouchedKnob (index, isTouched);

        final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();

        final int extenderOffset = this.getExtenderOffset ();
        if (extenderOffset == 0)
        {
            switch (index)
            {
                case 0:
                    track.touchVolume (isTouched);
                    break;
                case 1:
                    track.touchPan (isTouched);
                    break;
                default:
                    if (!isEffectTrackBankActive)
                        track.getSendBank ().getItem (index - 2).touchValue (isTouched);
                    break;
            }
        }
        else if (extenderOffset == 8 && !isEffectTrackBankActive)
        {
            track.getSendBank ().getItem (index + 6).touchValue (isTouched);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (!this.surface.getConfiguration ().hasDisplay1 ())
            return;

        this.drawDisplay2 ();
        this.drawTrackHeader ();

        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final Optional<ITrack> selectedTrack = this.getSelectedTrack ();
        if (selectedTrack.isEmpty ())
        {
            d.notify ("Please select a track...");
            return;
        }

        final ITrack t = selectedTrack.get ();
        final int extenderOffset = this.getExtenderOffset ();
        final int textLength = this.getTextLength ();
        final boolean displayTrackNames = this.surface.getConfiguration ().isDisplayTrackNames ();

        final ColorEx [] colors = new ColorEx [8];
        Arrays.fill (colors, ColorEx.BLACK);
        final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();
        final ISendBank sendBank = t.getSendBank ();

        if (extenderOffset == 0)
        {
            d.setCell (0, 0, displayTrackNames ? StringUtils.shortenAndFixASCII (t.getName (), textLength) : "Volume");
            d.setCell (0, 1, "Pan");

            d.setCell (1, 0, t.getVolumeStr (textLength));
            d.setCell (1, 1, t.getPanStr (textLength));

            colors[0] = preventBlack (t.doesExist (), t.getColor ());
            colors[1] = colors[0];

            final int sendStart = 2;
            for (int i = 0; i < 6; i++)
            {
                final int pos = sendStart + i;
                if (!isEffectTrackBankActive && i < sendBank.getItemCount ())
                {
                    final ISend send = sendBank.getItem (i);
                    final boolean doesExist = send.doesExist ();
                    if (doesExist)
                    {
                        d.setCell (0, pos, StringUtils.fixASCII (send.getName (textLength)));
                        d.setCell (1, pos, send.getDisplayedValue (textLength));
                    }
                    colors[sendStart + i] = preventBlack (doesExist, send.getColor ());
                }
            }
        }
        else if (extenderOffset == 8 && !isEffectTrackBankActive)
        {
            for (int i = 0; i < 8; i++)
            {
                if (i < sendBank.getItemCount ())
                {
                    final ISend send = sendBank.getItem (6 + i);
                    final boolean doesExist = send.doesExist ();
                    if (doesExist)
                    {
                        d.setCell (0, i, StringUtils.fixASCII (send.getName (textLength)));
                        d.setCell (1, i, send.getDisplayedValue (textLength));
                    }
                    colors[i] = preventBlack (doesExist, send.getColor ());
                }
            }
        }

        d.done (0);
        d.done (1);

        this.surface.sendDisplayColor (colors);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();

        final Optional<ITrack> t = this.getSelectedTrack ();
        if (t.isEmpty ())
        {
            for (int i = 0; i < 8; i++)
                this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, 0, upperBound);
            return;
        }

        final int extenderOffset = this.getExtenderOffset ();
        final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();
        final ITrack track = t.get ();
        final ISendBank sendBank = track.getSendBank ();

        if (extenderOffset == 0)
        {
            this.surface.setKnobLED (0, MCUControlSurface.KNOB_LED_MODE_WRAP, track.getVolume (), upperBound);
            this.surface.setKnobLED (1, MCUControlSurface.KNOB_LED_MODE_BOOST_CUT, track.getPan (), upperBound);

            final int start = 2;
            final int end = 6;

            for (int i = 0; i < end; i++)
            {
                final int value;
                if (!isEffectTrackBankActive && i < sendBank.getItemCount ())
                    value = sendBank.getItem (i).getValue ();
                else
                    value = 0;
                this.surface.setKnobLED (start + i, MCUControlSurface.KNOB_LED_MODE_WRAP, value, upperBound);
            }
        }
        else if (extenderOffset == 8)
        {
            for (int i = 0; i < 8; i++)
            {
                final int value;
                if (!isEffectTrackBankActive && i < sendBank.getItemCount ())
                    value = sendBank.getItem (6 + i).getValue ();
                else
                    value = 0;
                this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, value, upperBound);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final Optional<ITrack> selectedTrack = this.getSelectedTrack ();
        if (selectedTrack.isEmpty ())
            return;

        final ITrack track = selectedTrack.get ();
        final int extenderOffset = this.getExtenderOffset ();
        final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();

        if (extenderOffset == 0)
        {
            switch (index)
            {
                case 0:
                    this.resetParameter (track.getVolumeParameter ());
                    break;
                case 1:
                    this.resetParameter (track.getPanParameter ());
                    break;
                default:
                    if (!this.model.isEffectTrackBankActive ())
                        this.resetParameter (track.getSendBank ().getItem (index - 2));
                    break;
            }
        }
        else if (extenderOffset == 8 && !isEffectTrackBankActive)
        {
            this.resetParameter (track.getSendBank ().getItem (6 + index));
        }
    }


    private Optional<ITrack> getSelectedTrack ()
    {
        if (this.surface.getConfiguration ().shouldPinFXTracksToLastController () && this.surface.isLastDevice ())
            return this.model.getEffectTrackBank ().getSelectedItem ();
        return this.model.getCurrentTrackBank ().getSelectedItem ();
    }
}