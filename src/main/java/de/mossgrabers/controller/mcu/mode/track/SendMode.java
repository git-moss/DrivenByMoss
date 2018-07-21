// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.mode.track;

import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mcu.mode.Modes;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
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
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        this.model.getCurrentTrackBank ().getItem (extenderOffset + index).getSendBank ().getItem (this.getCurrentSendIndex ()).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (!this.surface.getConfiguration ().hasDisplay1 ())
            return;

        if (this.model.isEffectTrackBankActive ())
        {
            this.surface.getModeManager ().setActiveMode (Modes.MODE_TRACK);
            return;
        }

        this.drawDisplay2 ();
        if (!this.drawTrackHeader ())
            return;

        final Display d = this.surface.getDisplay ();
        final int sendIndex = this.getCurrentSendIndex ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (!tb.canEditSend (sendIndex))
        {
            d.notify ("Send channel " + (sendIndex + 1) + " does not exist.", true, false);
            return;
        }
        final int extenderOffset = this.surface.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            d.setCell (1, i, t.getSendBank ().getItem (sendIndex).getDisplayedValue (6));
        }
        d.done (1);

    }


    /** {@inheritDoc} */
    @Override
    protected boolean drawTrackHeader ()
    {
        if (this.model.isEffectTrackBankActive ())
        {
            this.surface.getModeManager ().setActiveMode (Modes.MODE_TRACK);
            return true;
        }

        if (!super.drawTrackHeader ())
            return false;

        if (this.surface.getConfiguration ().isDisplayTrackNames ())
            return true;

        final Display d = this.surface.getDisplay ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final int sendIndex = this.getCurrentSendIndex ();
        final int extenderOffset = this.surface.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            if (t.doesExist ())
                d.setCell (0, i, StringUtils.shortenAndFixASCII (t.getSendBank ().getItem (sendIndex).getName (6), 6));
            else
                d.clearCell (0, i);
        }
        d.done (0);
        return true;
    }


    /** {@inheritDoc} */
    @Override
    protected void updateKnobLEDs ()
    {
        if (this.model.isEffectTrackBankActive ())
        {
            this.surface.getModeManager ().setActiveMode (Modes.MODE_TRACK);
            return;
        }

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        final int sendIndex = this.getCurrentSendIndex ();
        final int extenderOffset = this.surface.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, t.getSendBank ().getItem (sendIndex).getValue (), upperBound);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        this.model.getCurrentTrackBank ().getItem (extenderOffset + index).getSendBank ().getItem (this.getCurrentSendIndex ()).resetValue ();
    }


    private int getCurrentSendIndex ()
    {
        return this.surface.getModeManager ().getActiveOrTempModeId ().intValue () - Modes.MODE_SEND1.intValue ();
    }
}