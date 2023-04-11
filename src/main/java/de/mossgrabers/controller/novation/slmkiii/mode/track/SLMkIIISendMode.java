// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.mode.track;

import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.parameterprovider.special.ResetParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing a send volume parameter of all tracks.
 *
 * @author Jürgen Moßgraber
 */
public class SLMkIIISendMode extends AbstractTrackMode
{
    private final int sendIndex;


    /**
     * Constructor.
     *
     * @param sendIndex The index of the send
     * @param surface The control surface
     * @param model The model
     */
    public SLMkIIISendMode (final int sendIndex, final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Send", surface, model);

        this.sendIndex = sendIndex;

        final SendParameterProvider parameterProvider = new SendParameterProvider (model, sendIndex, 0);
        this.setParameterProvider (parameterProvider);
        this.setParameterProvider (ButtonID.DELETE, new ResetParameterProvider (parameterProvider));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();
        d.setCell (0, 8, "Send " + (this.sendIndex + 1));

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            int color = SLMkIIIColorManager.SLMKIII_BLACK;
            final ITrack t = tb.getItem (i);
            if (t.doesExist ())
            {
                final ISend send = t.getSendBank ().getItem (this.sendIndex);
                if (send.doesExist ())
                {
                    d.setCell (0, i, send.getName (9)).setCell (1, i, send.getDisplayedValue (9));
                    color = SLMkIIIColorManager.SLMKIII_YELLOW;
                }
            }

            this.setColumnColors (d, i, t, color);
        }

        final ITrack cursorTrack = this.model.getCursorTrack ();
        d.setCell (1, 8, cursorTrack == null ? "" : StringUtils.fixASCII (cursorTrack.getName (9)));

        this.drawRow4 ();
        this.setButtonInfo (d);
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public int getModeColor ()
    {
        return SLMkIIIColorManager.SLMKIII_YELLOW;
    }
}