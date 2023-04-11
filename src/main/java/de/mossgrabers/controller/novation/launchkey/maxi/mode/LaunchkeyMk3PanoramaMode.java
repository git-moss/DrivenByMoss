// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi.mode;

import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3Display;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.track.TrackPanMode;

import java.util.List;


/**
 * The panorama mode. The knobs control the panorama of the tracks on the current track page.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchkeyMk3PanoramaMode extends TrackPanMode<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public LaunchkeyMk3PanoramaMode (final LaunchkeyMk3ControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super (surface, model, true, controls);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ();

        d.setCell (LaunchkeyMk3Display.SCREEN_ROW_BASE, 0, "Pan   " + this.formatPageRange (" %d - %d"));

        final ICursorTrack cursorTrack = this.model.getCursorTrack ();
        final String trackText = cursorTrack.doesExist () ? String.format ("%d: %s", Integer.valueOf (cursorTrack.getPosition () + 1), cursorTrack.getName ()) : "No sel. track";
        d.setCell (LaunchkeyMk3Display.SCREEN_ROW_BASE + 1, 0, trackText);

        // Format track names
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            final boolean exists = t.doesExist ();
            final int offset = i * 2;
            d.setCell (LaunchkeyMk3Display.SCREEN_ROW_POTS + offset, 0, exists ? String.format ("%d: %s", Integer.valueOf (t.getPosition () + 1), t.getName ()) : "No track");
            d.setCell (LaunchkeyMk3Display.SCREEN_ROW_POTS + offset + 1, 0, exists ? "Pan: " + t.getPanStr () : "");
        }

        d.allDone ();
    }
}