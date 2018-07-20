// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.mode;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Edit track parameters mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode extends AbstractMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public TrackMode (final SLControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITrack t = this.model.getSelectedTrack ();
        final Display d = this.surface.getDisplay ();

        if (t == null)
        {
            d.setRow (0, "                        Please select a track...                       ").clearRow (2).done (2);
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

        final ITrackBank fxTrackBank = this.model.getEffectTrackBank ();
        int pos;
        if (fxTrackBank != null)
        {
            final boolean isFX = this.model.isEffectTrackBankActive ();
            for (int i = 0; i < sendCount; i++)
            {
                final ITrack fxTrack = fxTrackBank.getItem (i);
                final boolean isEmpty = isFX || !fxTrack.doesExist ();
                pos = sendStart + i;
                d.setCell (0, pos, isEmpty ? "" : fxTrack.getName ()).setCell (2, pos, isEmpty ? "" : t.getSendBank ().getItem (i).getDisplayedValue (8));
            }

            if (isFX)
                d.setCell (0, 7, t.getName ());
        }
        else
        {
            for (int i = 0; i < sendCount; i++)
            {
                pos = sendStart + i;
                final ISend send = t.getSendBank ().getItem (i);
                d.setCell (0, pos, send.getName (8)).setCell (2, pos, send.getDisplayedValue (8));
            }
        }
        d.done (0).done (2);
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}
