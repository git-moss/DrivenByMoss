// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.mode.track;

import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing a Send volumes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SendMode (final MCUControlSurface surface, final IModel model)
    {
        super ("Send", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final int channel = this.getExtenderOffset () + index;
        final ISendBank sendBank = this.getTrackBank ().getItem (channel).getSendBank ();
        final int sendIndex = this.getCurrentSendIndex ();
        if (sendIndex < sendBank.getPageSize ())
            sendBank.getItem (sendIndex).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final int sendIndex = this.getCurrentSendIndex ();

        this.isKnobTouched[index] = isTouched;

        final int channel = this.getExtenderOffset () + index;
        final ITrack t = this.getTrackBank ().getItem (channel);
        if (!t.doesExist ())
            return;
        final ISendBank sendBank = t.getSendBank ();
        if (sendIndex < sendBank.getPageSize ())
            sendBank.getItem (sendIndex).touchValue (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final int channel = this.getExtenderOffset () + index;
        final ITrack track = this.getTrackBank ().getItem (channel);
        if (!track.doesExist ())
            return 0;
        final ISendBank sendBank = track.getSendBank ();
        final int sendIndex = this.getCurrentSendIndex ();
        if (sendIndex < sendBank.getPageSize ())
            return sendBank.getItem (sendIndex).getValue ();
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (!this.surface.getConfiguration ().hasDisplay1 ())
            return;

        if (this.model.isEffectTrackBankActive ())
        {
            this.surface.getModeManager ().setActiveMode (Modes.TRACK);
            return;
        }

        this.drawDisplay2 ();
        if (!this.drawTrackHeader ())
            return;

        final ITextDisplay d = this.surface.getTextDisplay ();
        final int sendIndex = this.getCurrentSendIndex ();
        final ITrackBank tb = this.getTrackBank ();
        if (!tb.canEditSend (sendIndex))
        {
            d.notify ("Send channel " + (sendIndex + 1) + " does not exist.");
            return;
        }
        final int extenderOffset = this.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            final ISendBank sendBank = t.getSendBank ();
            d.setCell (1, i, sendIndex < sendBank.getPageSize () ? sendBank.getItem (sendIndex).getDisplayedValue (6) : "");
        }
        d.done (1);

    }


    /** {@inheritDoc} */
    @Override
    protected boolean drawTrackHeader ()
    {
        if (this.model.isEffectTrackBankActive ())
        {
            this.surface.getModeManager ().setActiveMode (Modes.TRACK);
            return true;
        }

        if (!super.drawTrackHeader ())
            return false;

        if (this.surface.getConfiguration ().isDisplayTrackNames ())
            return true;

        final ITextDisplay d = this.surface.getTextDisplay ();
        final ITrackBank tb = this.getTrackBank ();
        final int sendIndex = this.getCurrentSendIndex ();
        final int extenderOffset = this.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            if (t.doesExist ())
            {
                final ISendBank sendBank = t.getSendBank ();
                d.setCell (0, i, StringUtils.shortenAndFixASCII (sendIndex < sendBank.getPageSize () ? sendBank.getItem (sendIndex).getName (6) : "", 6));
            }
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

        if (this.model.isEffectTrackBankActive ())
        {
            this.surface.getModeManager ().setActiveMode (Modes.TRACK);
            return;
        }

        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        final int sendIndex = this.getCurrentSendIndex ();
        final int extenderOffset = this.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            final ISendBank sendBank = t.getSendBank ();
            this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, sendIndex < sendBank.getPageSize () ? sendBank.getItem (sendIndex).getValue () : 0, upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final int extenderOffset = this.getExtenderOffset ();
        final ISendBank sendBank = this.getTrackBank ().getItem (extenderOffset + index).getSendBank ();
        final int sendIndex = this.getCurrentSendIndex ();
        if (sendIndex < sendBank.getPageSize ())
            sendBank.getItem (sendIndex).resetValue ();
    }


    private int getCurrentSendIndex ()
    {
        return this.surface.getModeManager ().getActiveOrTempModeId ().ordinal () - Modes.SEND1.ordinal ();
    }
}