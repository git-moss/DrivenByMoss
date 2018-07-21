// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.display.DisplayModel;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.Pair;


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
        this.model.getCurrentTrackBank ().getItem (index).getSendBank ().getItem (this.getCurrentSendIndex ()).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        final int sendIndex = this.getCurrentSendIndex ();

        this.isKnobTouched[index] = isTouched;

        final ITrack t = this.model.getCurrentTrackBank ().getItem (index);
        final ISend send = t.getSendBank ().getItem (sendIndex);
        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
                send.resetValue ();
                return;
            }

            final ITrackBank fxTrackBank = this.model.getEffectTrackBank ();
            if (t.doesExist ())
                this.surface.getDisplay ().notify ("Send " + (fxTrackBank == null ? send.getName () : fxTrackBank.getItem (sendIndex).getName ()) + ": " + send.getValue ());
        }

        send.touchValue (isTouched);
        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final int sendIndex = this.getCurrentSendIndex ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            if (t.doesExist ())
            {
                final ISend send = t.getSendBank ().getItem (sendIndex);
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
    @SuppressWarnings("null")
    @Override
    public void updateDisplay2 ()
    {
        final int sendIndex = this.getCurrentSendIndex ();
        this.updateTrackMenu (5 + sendIndex % 4);

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        final IValueChanger valueChanger = this.model.getValueChanger ();

        final int sendOffset = this.surface.getConfiguration ().isSendsAreToggled () ? 4 : 0;
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            final String [] sendName = new String [4];
            final String [] valueStr = new String [4];
            final int [] value = new int [4];
            final int [] modulatedValue = new int [4];
            final boolean [] selected = new boolean [4];
            for (int j = 0; j < 4; j++)
            {
                final int sendPos = sendOffset + j;
                final ISend send = t.getSendBank ().getItem (sendPos);
                final boolean exists = send != null && send.doesExist ();
                sendName[j] = exists ? send.getName () : " ";
                valueStr[j] = exists && sendIndex == sendPos && this.isKnobTouched[i] ? send.getDisplayedValue (8) : "";
                value[j] = valueChanger.toDisplayValue (exists ? send.getValue () : -1);
                modulatedValue[j] = valueChanger.toDisplayValue (exists ? send.getModulatedValue () : -1);
                selected[j] = sendIndex == sendPos;
            }
            final Pair<String, Boolean> pair = this.menu.get (i);
            message.addSendsElement (pair.getKey (), pair.getValue ().booleanValue (), t.doesExist () ? t.getName () : "", t.getType (), t.getColor (), t.isSelected (), sendName, valueStr, value, modulatedValue, selected, false);
        }
        message.send ();
    }


    private int getCurrentSendIndex ()
    {
        return this.surface.getModeManager ().getActiveOrTempModeId ().intValue () - Modes.MODE_SEND1.intValue ();
    }
}