// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.mode.track;

import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing a send volume parameter of all tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendMode extends AbstractTrackMode
{
    private final int sendIndex;


    /**
     * Constructor.
     *
     * @param sendIndex The index of the send
     * @param surface The control surface
     * @param model The model
     */
    public SendMode (final int sendIndex, final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Send", surface, model);

        this.sendIndex = sendIndex;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ISend send = this.model.getCurrentTrackBank ().getItem (index).getSendBank ().getItem (this.sendIndex);
        if (this.surface.isDeletePressed ())
            send.resetValue ();
        else
            send.changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();
        d.setCell (0, 8, "Send " + (this.sendIndex + 1));

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            int color = SLMkIIIColors.SLMKIII_BLACK;
            final ITrack t = tb.getItem (i);
            if (t.doesExist ())
            {
                final ISend send = t.getSendBank ().getItem (this.sendIndex);
                if (send.doesExist ())
                {
                    d.setCell (0, i, send.getName (9)).setCell (1, i, send.getDisplayedValue (9));
                    this.surface.updateContinuous (SLMkIIIControlSurface.MKIII_KNOB_1 + i, valueChanger.toMidiValue (send.getValue ()));
                    color = SLMkIIIColors.SLMKIII_YELLOW;
                }
            }

            this.setColumnColors (d, i, t, color);
        }

        final ITrack t = this.model.getSelectedTrack ();
        d.setCell (1, 8, t == null ? "" : StringUtils.fixASCII (t.getName (9)));

        d.setPropertyColor (8, 0, SLMkIIIColors.SLMKIII_YELLOW);

        this.drawRow4 ();
        this.setButtonInfo (d);
        d.allDone ();
    }
}