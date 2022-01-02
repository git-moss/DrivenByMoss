// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.track;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing a Send volumes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendMode extends AbstractTrackMode
{
    private final int sendIndex;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param sendIndex The index of the send
     */
    public SendMode (final MCUControlSurface surface, final IModel model, final int sendIndex)
    {
        super ("Send", surface, model);

        this.sendIndex = sendIndex;

        final IParameterProvider parameterProvider;
        if (surface.getConfiguration ().shouldPinFXTracksToLastController () && surface.isLastDevice ())
            parameterProvider = new EmptyParameterProvider (8);
        else
        {
            final int surfaceID = surface.getSurfaceID ();
            parameterProvider = new RangeFilterParameterProvider (new SendParameterProvider (model, sendIndex), surfaceID * 8, 8);
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
        if (!t.doesExist ())
            return;
        final ISendBank sendBank = t.getSendBank ();
        if (this.sendIndex < sendBank.getPageSize ())
            sendBank.getItem (this.sendIndex).touchValue (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (!this.surface.getConfiguration ().hasDisplay1 ())
            return;

        if (this.model.isEffectTrackBankActive ())
        {
            this.surface.getModeManager ().setActive (Modes.TRACK);
            return;
        }

        this.drawDisplay2 ();
        if (!this.drawTrackHeader ())
            return;

        final ITextDisplay d = this.surface.getTextDisplay ();
        final ITrackBank tb = this.getTrackBank ();
        if (!tb.canEditSend (this.sendIndex))
        {
            d.notify ("Send channel " + (this.sendIndex + 1) + " does not exist.");
            return;
        }
        final int extenderOffset = this.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            final ISendBank sendBank = t.getSendBank ();
            d.setCell (1, i, this.sendIndex < sendBank.getPageSize () ? sendBank.getItem (this.sendIndex).getDisplayedValue (6) : "");
        }
        d.done (1);

    }


    /** {@inheritDoc} */
    @Override
    protected boolean drawTrackHeader ()
    {
        if (this.model.isEffectTrackBankActive ())
        {
            this.surface.getModeManager ().setActive (Modes.TRACK);
            return true;
        }

        if (!super.drawTrackHeader ())
            return false;

        if (this.surface.getConfiguration ().isDisplayTrackNames ())
            return true;

        final ITextDisplay d = this.surface.getTextDisplay ();
        final ITrackBank tb = this.getTrackBank ();
        final int extenderOffset = this.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            if (t.doesExist ())
            {
                final ISendBank sendBank = t.getSendBank ();
                d.setCell (0, i, StringUtils.shortenAndFixASCII (this.sendIndex < sendBank.getPageSize () ? sendBank.getItem (this.sendIndex).getName (6) : "", 6));
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
            this.surface.getModeManager ().setActive (Modes.TRACK);
            return;
        }

        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        final int extenderOffset = this.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            final ISendBank sendBank = t.getSendBank ();
            this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, this.sendIndex < sendBank.getPageSize () ? sendBank.getItem (this.sendIndex).getValue () : 0, upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final int extenderOffset = this.getExtenderOffset ();
        final ISendBank sendBank = this.getTrackBank ().getItem (extenderOffset + index).getSendBank ();
        if (this.sendIndex < sendBank.getPageSize ())
            sendBank.getItem (this.sendIndex).resetValue ();
    }
}