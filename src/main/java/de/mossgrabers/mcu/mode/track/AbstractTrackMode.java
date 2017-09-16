// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.mode.track;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.StringUtils;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.mcu.controller.MCUControlSurface;
import de.mossgrabers.mcu.mode.BaseMode;


/**
 * Abstract base mode for all track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public AbstractTrackMode (final MCUControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    protected boolean drawTrackHeader ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();

        final Display d = this.surface.getDisplay ().clear ();
        final AbstractTrackBankProxy currentTrackBank = this.model.getCurrentTrackBank ();
        final TrackData selectedTrack = currentTrackBank.getSelectedTrack ();
        if (selectedTrack == null)
        {
            d.notify ("Please select a track...", true, false);
            return false;
        }

        // Format track names
        for (int i = 0; i < 8; i++)
        {
            final TrackData t = tb.getTrack (i);
            final String name = t.getName ();
            d.setCell (0, i, this.optimizeName (StringUtils.fixASCII (name), 6));
        }
        d.done (0);

        return true;
    }
}