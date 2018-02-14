// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.mode.track;

import de.mossgrabers.framework.StringUtils;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;


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
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        final boolean effectTrackBankActive = this.model.isEffectTrackBankActive ();

        switch (index)
        {
            case 0:
                tb.changeVolume (selectedTrack.getIndex (), value);
                return;
            case 1:
                tb.changePan (selectedTrack.getIndex (), value);
                return;
        }

        final MCUConfiguration config = this.surface.getConfiguration ();

        if (index == 2)
        {
            if (config.isDisplayCrossfader ())
                tb.changeCrossfadeModeAsNumber (selectedTrack.getIndex (), value);
            else if (!effectTrackBankActive)
                ((ITrackBank) tb).changeSend (selectedTrack.getIndex (), 0, value);
        }
        else if (!effectTrackBankActive)
            ((ITrackBank) tb).changeSend (selectedTrack.getIndex (), index - (config.isDisplayCrossfader () ? 3 : 2), value);
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

        final Display d = this.surface.getDisplay ().clear ();

        final IChannelBank currentTrackBank = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = currentTrackBank.getSelectedTrack ();
        if (selectedTrack == null)
        {
            d.notify ("Please select a track...", true, false);
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
        for (int i = 0; i < sendCount; i++)
        {
            final int pos = sendStart + i;
            if (!isEffectTrackBankActive)
            {
                final ISend send = selectedTrack.getSends ()[i];
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
    protected void updateKnobLEDs ()
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final int upperBound = this.model.getValueChanger ().getUpperBound ();

        final ITrack t = tb.getSelectedTrack ();
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
            this.surface.setKnobLED (start + i, MCUControlSurface.KNOB_LED_MODE_WRAP, isEffectTrackBankActive ? 0 : t.getSends ()[i].getValue (), upperBound);
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedTrack ();
        if (selectedTrack == null)
            return;
        final int trackIndex = selectedTrack.getIndex ();
        switch (index)
        {
            case 0:
                tb.resetVolume (trackIndex);
                break;
            case 1:
                tb.resetPan (trackIndex);
                break;
            case 2:
                if (this.surface.getConfiguration ().isDisplayCrossfader ())
                    tb.setCrossfadeMode (trackIndex, "AB");
                else if (!this.model.isEffectTrackBankActive ())
                    ((ITrackBank) tb).resetSend (trackIndex, 0);
                break;
            default:
                if (!this.model.isEffectTrackBankActive ())
                    ((ITrackBank) tb).resetSend (trackIndex, index - (this.surface.getConfiguration ().isDisplayCrossfader () ? 3 : 2));
                break;
        }
    }
}