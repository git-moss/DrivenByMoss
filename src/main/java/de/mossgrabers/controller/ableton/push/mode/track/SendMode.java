// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.track;

import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.graphics.canvas.utils.SendData;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;
import de.mossgrabers.framework.utils.Pair;


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
    public SendMode (final PushControlSurface surface, final IModel model, final int sendIndex)
    {
        super ("Send", surface, model);

        this.sendIndex = sendIndex;

        this.setParameterProvider (new SendParameterProvider (model, this.sendIndex, 0));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            if (t.doesExist ())
            {
                final ISend send = t.getSendBank ().getItem (this.sendIndex);
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
        this.updateTrackMenu (5 + this.sendIndex % 4);

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final IValueChanger valueChanger = this.model.getValueChanger ();

        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            final ISendBank sendBank = t.getSendBank ();
            final SendData [] sendData = new SendData [4];
            for (int j = 0; j < 4; j++)
            {
                final ISend send = sendBank.getItem (j);
                final boolean exists = send != null && send.doesExist ();
                sendData[j] = new SendData (exists ? send.getName () : "", exists && this.sendIndex == j && this.isKnobTouched (i) ? send.getDisplayedValue (8) : "", valueChanger.toDisplayValue (exists ? send.getValue () : -1), valueChanger.toDisplayValue (exists ? send.getModulatedValue () : -1), this.sendIndex == j);
            }
            final Pair<String, Boolean> pair = this.menu.get (i);
            display.addSendsElement (pair.getKey (), pair.getValue ().booleanValue (), t.doesExist () ? t.getName () : "", this.updateType (t), t.getColor (), t.isSelected (), sendData, false, t.isActivated (), t.isActivated ());
        }
    }
}