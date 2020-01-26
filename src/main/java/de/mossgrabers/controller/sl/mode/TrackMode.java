// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.mode;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.track.AbstractTrackMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Edit track parameters mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode extends AbstractTrackMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public TrackMode (final SLControlSurface surface, final IModel model)
    {
        super ("Track", surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITrack t = this.model.getSelectedTrack ();
        final ITextDisplay d = this.surface.getTextDisplay ().clearRow (0).clearRow (2);

        if (t == null)
        {
            d.setRow (0, "                        Please select a track...                       ").done (0).done (2);
            return;
        }

        d.setCell (0, 0, "Volume").setCell (2, 0, t.getVolumeStr (8)).setCell (0, 1, "Pan").setCell (2, 1, t.getPanStr (8));

        int sendStart = 2;
        int sendCount = 6;
        if (this.surface.getConfiguration ().isDisplayCrossfader ())
        {
            sendStart = 3;
            sendCount = 5;
            final String crossfadeMode = t.getCrossfadeMode ();
            d.setCell (0, 2, "Crossfdr").setCell (2, 2, "A".equals (crossfadeMode) ? "A" : "B".equals (crossfadeMode) ? "       B" : "   <> ");
        }

        int pos;
        final ISendBank sendBank = t.getSendBank ();
        if (!this.model.isEffectTrackBankActive () && sendBank.getPageSize () > 0)
        {
            for (int i = 0; i < sendCount; i++)
            {
                pos = sendStart + i;
                final ISend send = sendBank.getItem (i);
                d.setCell (0, pos, send.getName (8)).setCell (2, pos, send.getDisplayedValue (8));
            }
        }
        d.done (0).done (2);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}
