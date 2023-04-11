// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.mode;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.controller.novation.sl.controller.SLDisplay;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Volume mode.
 *
 * @author Jürgen Moßgraber
 */
public class SLVolumeMode extends de.mossgrabers.framework.mode.track.TrackVolumeMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SLVolumeMode (final SLControlSurface surface, final IModel model)
    {
        super (surface, model, true, ContinuousID.createSequentialList (ContinuousID.FADER1, 8));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clearRow (2).clearRow (3);

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            final String n = t.getName ();
            d.setCell (2, i, StringUtils.shortenAndFixASCII (t.isSelected () ? SLDisplay.RIGHT_ARROW + n : n, 8));
            d.setCell (3, i, t.getVolumeStr (8));
        }
        d.done (2).done (3);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        // Only bind once to faders!
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