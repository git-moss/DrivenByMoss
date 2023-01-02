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
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.ResetParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SelectedTrackParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing a track parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIITrackMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SLMkIIITrackMode (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Track", surface, model);

        final IParameterProvider parameterProvider = new SelectedTrackParameterProvider (model);
        this.setParameterProvider (parameterProvider);
        this.setParameterProvider (ButtonID.DELETE, new ResetParameterProvider (parameterProvider));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();
        d.setCell (0, 8, "Track");

        final ITrack cursorTrack = this.model.getCursorTrack ();
        if (!cursorTrack.doesExist ())
        {
            d.setBlock (1, 1, " Please  select a").setBlock (1, 2, "track.");
            d.setCell (1, 8, "");
            d.hideAllElements ();
        }
        else
        {
            d.setCell (0, 0, "Volume").setCell (1, 0, cursorTrack.getVolumeStr (9));
            d.setPropertyColor (0, 0, SLMkIIIColorManager.SLMKIII_BLUE);
            d.setPropertyColor (0, 1, SLMkIIIColorManager.SLMKIII_BLUE);

            d.setCell (0, 1, "Pan").setCell (1, 1, cursorTrack.getPanStr (9));
            d.setPropertyColor (1, 0, SLMkIIIColorManager.SLMKIII_ORANGE);
            d.setPropertyColor (1, 1, SLMkIIIColorManager.SLMKIII_ORANGE);

            final ISendBank sendBank = cursorTrack.getSendBank ();
            for (int i = 0; i < 6; i++)
            {
                final int pos = 2 + i;

                int color = SLMkIIIColorManager.SLMKIII_BLACK;
                if (sendBank.getItemCount () > 0)
                {
                    final ISend send = sendBank.getItem (i);
                    if (send.doesExist ())
                    {
                        d.setCell (0, pos, send.getName (9)).setCell (1, pos, send.getDisplayedValue (9));
                        color = SLMkIIIColorManager.SLMKIII_YELLOW;
                    }
                }

                d.setPropertyColor (pos, 0, color);
                d.setPropertyColor (pos, 1, color);
            }

            d.setCell (1, 8, StringUtils.fixASCII (cursorTrack.getName (9)));
        }

        this.drawRow4 ();
        this.setButtonInfo (d);
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public int getModeColor ()
    {
        return SLMkIIIColorManager.SLMKIII_GREEN;
    }
}