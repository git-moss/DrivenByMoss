// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode.track;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.EffectTrackBankProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.data.SendData;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;
import de.mossgrabers.push.mode.Modes;


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
    public SendMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final int sendIndex = this.getCurrentSendIndex ();
        final AbstractTrackBankProxy currentTrackBank = this.model.getCurrentTrackBank ();
        if (currentTrackBank instanceof TrackBankProxy)
            ((TrackBankProxy) currentTrackBank).changeSend (index, sendIndex, value);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        final int sendIndex = this.getCurrentSendIndex ();

        this.isKnobTouched[index] = isTouched;

        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                final AbstractTrackBankProxy currentTrackBank = this.model.getCurrentTrackBank ();
                if (currentTrackBank instanceof TrackBankProxy)
                    ((TrackBankProxy) currentTrackBank).resetSend (index, sendIndex);
                return;
            }

            final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
            final TrackData t = this.model.getCurrentTrackBank ().getTrack (index);
            if (t.doesExist ())
                this.surface.getDisplay ().notify ("Send " + (fxTrackBank == null ? t.getSends ()[sendIndex].getName () : fxTrackBank.getTrack (sendIndex).getName ()) + ": " + t.getSends ()[sendIndex].getValue ());
        }

        final AbstractTrackBankProxy currentTrackBank = this.model.getCurrentTrackBank ();
        if (currentTrackBank instanceof TrackBankProxy)
            ((TrackBankProxy) currentTrackBank).touchSend (index, sendIndex, isTouched);
        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final int sendIndex = this.getCurrentSendIndex ();
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final TrackData t = tb.getTrack (i);
            if (t.doesExist ())
            {
                final SendData sendData = t.getSends ()[sendIndex];
                d.setCell (0, i, sendData.getName ());
                d.setCell (1, i, sendData.getDisplayedValue (8));
                d.setCell (2, i, sendData.getValue (), Format.FORMAT_VALUE);
            }
            else
                d.clearColumn (i);
        }
        d.done (0).done (1).done (2);

        this.drawRow4 ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final int sendIndex = this.getCurrentSendIndex ();
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
        final PushConfiguration config = this.surface.getConfiguration ();
        TrackData t;
        if (this.isPush2)
        {
            this.updateTrackMenu ();

            final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();

            final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
            for (int i = 0; i < 8; i++)
            {
                t = tb.getTrack (i);

                message.addByte (DisplayMessage.GRID_ELEMENT_CHANNEL_SENDS);

                // The menu item
                if (config.isMuteLongPressed () || config.isMuteSoloLocked () && config.isMuteState ())
                {
                    message.addString (t.doesExist () ? "Mute" : "");
                    message.addBoolean (t.isMute ());
                }
                else if (config.isSoloLongPressed () || config.isMuteSoloLocked () && config.isSoloState ())
                {
                    message.addString (t.doesExist () ? "Solo" : "");
                    message.addBoolean (t.isSolo ());
                }
                else
                {
                    message.addString (this.menu[i]);
                    message.addBoolean (i > 3 && i - 4 + sendOffset == sendIndex);
                }

                // Channel info
                message.addString (t.doesExist () ? t.getName () : "");
                message.addString (t.getType ());
                message.addColor (tb.getTrackColorEntry (i));
                message.addByte (t.isSelected () ? 1 : 0);

                final ValueChanger valueChanger = this.model.getValueChanger ();
                for (int j = 0; j < 4; j++)
                {
                    final int sendPos = sendOffset + j;
                    final SendData send = t.getSends ()[sendPos];
                    message.addString (fxTrackBank == null ? send.getName () : fxTrackBank.getTrack (sendPos).getName ());
                    message.addString (send != null && sendIndex == sendPos && this.isKnobTouched[i] ? send.getDisplayedValue (8) : "");
                    message.addInteger (valueChanger.toDisplayValue (send != null ? send.getValue () : -1));
                    message.addInteger (valueChanger.toDisplayValue (send != null ? send.getModulatedValue () : -1));
                    message.addByte (sendIndex == sendPos ? 1 : 0);
                }

                // Signal Track mode off
                message.addBoolean (false);
            }

            message.send ();
        }
    }


    private int getCurrentSendIndex ()
    {
        return this.surface.getModeManager ().getActiveModeId ().intValue () - Modes.MODE_SEND1.intValue ();
    }
}