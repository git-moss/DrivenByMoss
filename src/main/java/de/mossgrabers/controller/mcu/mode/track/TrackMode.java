// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.mode.track;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.StringUtils;


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
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack selectedTrack = this.model.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        final boolean effectTrackBankActive = this.model.isEffectTrackBankActive ();

        switch (index)
        {
            case 0:
                selectedTrack.changeVolume (value);
                return;
            case 1:
                selectedTrack.changePan (value);
                return;
            default:
                // Not used
                break;
        }

        final MCUConfiguration config = this.surface.getConfiguration ();

        if (index == 2)
        {
            if (config.isDisplayCrossfader ())
                selectedTrack.changeCrossfadeModeAsNumber (value);
            else if (!effectTrackBankActive)
                selectedTrack.getSendBank ().getItem (0).changeValue (value);
        }
        else if (!effectTrackBankActive)
            selectedTrack.getSendBank ().getItem (index - (config.isDisplayCrossfader () ? 3 : 2)).changeValue (value);
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

        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final ITrack selectedTrack = this.model.getSelectedTrack ();
        if (selectedTrack == null)
        {
            d.notify ("Please select a track...");
            return;
        }

        final MCUConfiguration config = this.surface.getConfiguration ();

        final boolean displayTrackNames = this.surface.getConfiguration ().isDisplayTrackNames ();
        if (!displayTrackNames)
        {
            d.setCell (0, 0, "Volume");
            d.setCell (0, 1, "Pan");
        }

        d.setCell (1, 0, selectedTrack.getVolumeStr (6));
        d.setCell (1, 1, selectedTrack.getPanStr (6));

        int sendStart = 2;
        int sendCount = 6;
        if (config.isDisplayCrossfader ())
        {
            sendStart = 3;
            sendCount = 5;
            final String crossfadeMode = selectedTrack.getCrossfadeMode ();
            if (!displayTrackNames)
                d.setCell (0, 2, "Crossfade");
            d.setCell (1, 2, "A".equals (crossfadeMode) ? "A" : "B".equals (crossfadeMode) ? "     B" : "  <>  ");
        }
        final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();
        final ISendBank sendBank = selectedTrack.getSendBank ();
        for (int i = 0; i < sendCount; i++)
        {
            final int pos = sendStart + i;
            if (!isEffectTrackBankActive && i < sendBank.getItemCount ())
            {
                final ISend send = sendBank.getItem (i);
                if (send.doesExist ())
                {
                    if (!displayTrackNames)
                        d.setCell (0, pos, StringUtils.fixASCII (send.getName ()));
                    d.setCell (1, pos, send.getDisplayedValue (6));
                }
            }
        }

        if (!displayTrackNames)
            d.done (0);
        d.done (1);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int upperBound = this.model.getValueChanger ().getUpperBound ();

        final ITrack t = this.model.getSelectedTrack ();
        if (t == null)
        {
            for (int i = 0; i < 8; i++)
                this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, 0, upperBound);
            return;
        }

        this.surface.setKnobLED (0, MCUControlSurface.KNOB_LED_MODE_WRAP, t.getVolume (), upperBound);
        this.surface.setKnobLED (1, MCUControlSurface.KNOB_LED_MODE_BOOST_CUT, t.getPan (), upperBound);

        final boolean displayCrossfader = this.surface.getConfiguration ().isDisplayCrossfader ();
        final int start = displayCrossfader ? 3 : 2;
        final int end = displayCrossfader ? 5 : 6;
        if (displayCrossfader)
        {
            final String crossfadeMode = t.getCrossfadeMode ();
            this.surface.setKnobLED (2, MCUControlSurface.KNOB_LED_MODE_SINGLE_DOT, "A".equals (crossfadeMode) ? 1 : "B".equals (crossfadeMode) ? 127 : 64, upperBound);
        }

        final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();
        for (int i = 0; i < end; i++)
            this.surface.setKnobLED (start + i, MCUControlSurface.KNOB_LED_MODE_WRAP, isEffectTrackBankActive ? 0 : t.getSendBank ().getItem (i).getValue (), upperBound);
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final ITrack selectedTrack = this.model.getSelectedTrack ();
        if (selectedTrack == null)
            return;
        switch (index)
        {
            case 0:
                selectedTrack.resetVolume ();
                break;
            case 1:
                selectedTrack.resetPan ();
                break;
            case 2:
                if (this.surface.getConfiguration ().isDisplayCrossfader ())
                    selectedTrack.setCrossfadeMode ("AB");
                else if (!this.model.isEffectTrackBankActive ())
                    selectedTrack.getSendBank ().getItem (0).resetValue ();
                break;
            default:
                if (!this.model.isEffectTrackBankActive ())
                    selectedTrack.getSendBank ().getItem (index - (this.surface.getConfiguration ().isDisplayCrossfader () ? 3 : 2)).resetValue ();
                break;
        }
    }
}