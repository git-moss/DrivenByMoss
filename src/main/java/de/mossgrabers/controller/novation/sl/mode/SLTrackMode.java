// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.mode;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.mode.track.TrackMode;


/**
 * Edit track parameters mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLTrackMode extends TrackMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SLTrackMode (final SLControlSurface surface, final IModel model)
    {
        super (surface, model, true, ContinuousID.createSequentialList (ContinuousID.KNOB1, 8));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITrack cursorTrack = this.model.getCursorTrack ();
        final ITextDisplay d = this.surface.getTextDisplay ().clearRow (0).clearRow (1);

        if (!cursorTrack.doesExist ())
        {
            d.setRow (0, "                        Please select a track...                       ").done (0).done (1);
            return;
        }

        d.setCell (0, 0, "Volume").setCell (1, 0, cursorTrack.getVolumeStr (8)).setCell (0, 1, "Pan").setCell (1, 1, cursorTrack.getPanStr (8));

        final int sendStart = 2;
        final int sendCount = 6;
        int pos;

        final ISendBank sendBank = cursorTrack.getSendBank ();
        if (sendBank.getPageSize () > 0)
        {
            for (int i = 0; i < sendCount; i++)
            {
                pos = sendStart + i;
                final ISend send = sendBank.getItem (i);
                if (send.doesExist ())
                    d.setCell (0, pos, send.getName (8)).setCell (1, pos, send.getDisplayedValue (8));
            }
        }
        d.done (0).done (1);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        // Only bind once to knobs!
        if (!this.isActive)
            super.onActivate ();
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        // Never deactivate
    }
}
