// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.graphics.canvas.utils.SendData;
import de.mossgrabers.framework.mode.Modes;
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
        super ("Send", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        this.model.getCurrentTrackBank ().getItem (index).getSendBank ().getItem (this.getCurrentSendIndex ()).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final int sendIndex = this.getCurrentSendIndex ();

        this.isKnobTouched[index] = isTouched;

        final ITrack t = this.model.getCurrentTrackBank ().getItem (index);
        final ISend send = t.getSendBank ().getItem (sendIndex);
        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            send.resetValue ();
        }

        send.touchValue (isTouched);
        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final int sendIndex = this.getCurrentSendIndex ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            if (t.doesExist ())
            {
                final ISend send = t.getSendBank ().getItem (sendIndex);
                display.setCell (0, i, send.getName ());
                display.setCell (1, i, send.getDisplayedValue (8));
                display.setCell (2, i, send.getValue (), Format.FORMAT_VALUE);
            }
        }
        this.drawRow4 (display);
    }


    /** {@inheritDoc} */
    @SuppressWarnings("null")
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final int sendIndex = this.getCurrentSendIndex ();
        this.updateTrackMenu (5 + sendIndex % 4);

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final IValueChanger valueChanger = this.model.getValueChanger ();

        final int sendOffset = this.surface.getConfiguration ().isSendsAreToggled () ? 4 : 0;
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            final SendData [] sendData = new SendData [4];
            for (int j = 0; j < 4; j++)
            {
                final int sendPos = sendOffset + j;
                final ISend send = t.getSendBank ().getItem (sendPos);
                final boolean exists = send != null && send.doesExist ();
                sendData[j] = new SendData (exists ? send.getName () : " ", exists && sendIndex == sendPos && this.isKnobTouched[i] ? send.getDisplayedValue (8) : "", valueChanger.toDisplayValue (exists ? send.getValue () : -1), valueChanger.toDisplayValue (exists ? send.getModulatedValue () : -1), sendIndex == sendPos);
            }
            final Pair<String, Boolean> pair = this.menu.get (i);
            display.addSendsElement (pair.getKey (), pair.getValue ().booleanValue (), t.doesExist () ? t.getName () : "", t.getType (), t.getColor (), t.isSelected (), sendData, false, t.isActivated (), t.isActivated ());
        }
    }


    private int getCurrentSendIndex ()
    {
        return this.surface.getModeManager ().getActiveOrTempModeId ().ordinal () - Modes.SEND1.ordinal ();
    }
}