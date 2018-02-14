// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode.track;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
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
    public SendMode (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final int sendIndex = this.getCurrentSendIndex ();
        final IChannelBank currentTrackBank = this.model.getCurrentTrackBank ();
        if (currentTrackBank instanceof ITrackBank)
            ((ITrackBank) currentTrackBank).changeSend (index, sendIndex, value);
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
                final IChannelBank currentTrackBank = this.model.getCurrentTrackBank ();
                if (currentTrackBank instanceof ITrackBank)
                    ((ITrackBank) currentTrackBank).resetSend (index, sendIndex);
                return;
            }

            final IChannelBank fxTrackBank = this.model.getEffectTrackBank ();
            final ITrack t = this.model.getCurrentTrackBank ().getTrack (index);
            if (t.doesExist ())
                this.surface.getDisplay ().notify ("Send " + (fxTrackBank == null ? t.getSends ()[sendIndex].getName () : fxTrackBank.getTrack (sendIndex).getName ()) + ": " + t.getSends ()[sendIndex].getValue ());
        }

        final IChannelBank currentTrackBank = this.model.getCurrentTrackBank ();
        if (currentTrackBank instanceof ITrackBank)
            ((ITrackBank) currentTrackBank).touchSend (index, sendIndex, isTouched);
        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final int sendIndex = this.getCurrentSendIndex ();
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getTrack (i);
            if (t.doesExist ())
            {
                final ISend send = t.getSends ()[sendIndex];
                d.setCell (0, i, send.getName ());
                d.setCell (1, i, send.getDisplayedValue (8));
                d.setCell (2, i, send.getValue (), Format.FORMAT_VALUE);
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
        this.updateTrackMenu ();

        final int sendIndex = this.getCurrentSendIndex ();
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final IChannelBank fxTrackBank = this.model.getEffectTrackBank ();
        final PushConfiguration config = this.surface.getConfiguration ();
        final PushDisplay display = (PushDisplay) this.surface.getDisplay ();
        final DisplayMessage message = display.createMessage ();

        final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getTrack (i);

            // The menu item
            String topMenu;
            boolean topMenuSelected;
            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP))
            {
                topMenu = t.doesExist () ? "Stop Clip" : "";
                topMenuSelected = t.isPlaying ();
            }
            else if (config.isMuteLongPressed () || config.isMuteSoloLocked () && config.isMuteState ())
            {
                topMenu = t.doesExist () ? "Mute" : "";
                topMenuSelected = t.isMute ();
            }
            else if (config.isSoloLongPressed () || config.isMuteSoloLocked () && config.isSoloState ())
            {
                topMenu = t.doesExist () ? "Solo" : "";
                topMenuSelected = t.isSolo ();
            }
            else
            {
                topMenu = this.menu[i];
                topMenuSelected = i == 7 || i > 3 && i - 4 + sendOffset == sendIndex;
            }

            final ValueChanger valueChanger = this.model.getValueChanger ();
            final String [] sendName = new String [4];
            final String [] valueStr = new String [4];
            final int [] value = new int [4];
            final int [] modulatedValue = new int [4];
            final boolean [] selected = new boolean [4];
            for (int j = 0; j < 4; j++)
            {
                final int sendPos = sendOffset + j;
                final ISend send = t.getSends ()[sendPos];
                sendName[j] = fxTrackBank == null ? send.getName () : fxTrackBank.getTrack (sendPos).getName ();
                valueStr[j] = send != null && sendIndex == sendPos && this.isKnobTouched[i] ? send.getDisplayedValue (8) : "";
                value[j] = valueChanger.toDisplayValue (send != null ? send.getValue () : -1);
                modulatedValue[j] = valueChanger.toDisplayValue (send != null ? send.getModulatedValue () : -1);
                selected[j] = sendIndex == sendPos;
            }

            message.addSendsElement (topMenu, topMenuSelected, t.doesExist () ? t.getName () : "", t.getType (), tb.getTrackColorEntry (i), t.isSelected (), sendName, valueStr, value, modulatedValue, selected, false);
        }

        display.send (message);
    }


    private int getCurrentSendIndex ()
    {
        return this.surface.getModeManager ().getActiveModeId ().intValue () - Modes.MODE_SEND1.intValue ();
    }
}