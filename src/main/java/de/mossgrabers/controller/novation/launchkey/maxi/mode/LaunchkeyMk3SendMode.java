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
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.track.TrackSendMode;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.List;


/**
 * Mode for editing Send volumes. The knobs control the volumes of the given send of the tracks on
 * the selected track page.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchkeyMk3SendMode extends TrackSendMode<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    private final boolean areKnobs;


    /**
     * Constructor.
     *
     * @param sendIndex The send index
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public LaunchkeyMk3SendMode (final int sendIndex, final LaunchkeyMk3ControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super (sendIndex, surface, model, true, controls);

        this.areKnobs = controls.get (0) == ContinuousID.KNOB1;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ();

        final ICursorTrack cursorTrack = this.model.getCursorTrack ();
        final String trackText = cursorTrack.doesExist () ? String.format ("%d: %s", Integer.valueOf (cursorTrack.getPosition () + 1), cursorTrack.getName ()) : "No sel. track";

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (this.areKnobs)
        {
            String sendName = tb.getEditSendName (this.sendIndex);
            sendName = StringUtils.optimizeName (sendName.isEmpty () ? "Send " + (this.sendIndex + 1) : sendName, 6);

            d.setCell (LaunchkeyMk3Display.SCREEN_ROW_BASE, 0, sendName + this.formatPageRange (" %d - %d"));
            d.setCell (LaunchkeyMk3Display.SCREEN_ROW_BASE + 1, 0, trackText);
        }

        final int row = this.areKnobs ? LaunchkeyMk3Display.SCREEN_ROW_POTS : LaunchkeyMk3Display.SCREEN_ROW_FADERS;

        // Format track names
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            final ISend s = t.getSendBank ().getItem (this.sendIndex);
            final boolean exists = s.doesExist ();

            final int offset = i * 2;
            d.setCell (row + offset, 0, exists ? String.format ("%d: %s", Integer.valueOf (s.getPosition () + 1), s.getName ()) : "No send");
            d.setCell (row + offset + 1, 0, exists ? "S-Vol: " + s.getDisplayedValue () : "");
        }

        // Add master fader
        if (!this.areKnobs)
        {
            final IMasterTrack masterTrack = this.model.getMasterTrack ();
            d.setCell (row + 16, 0, "Master");
            d.setCell (row + 16 + 1, 0, "Vol: " + masterTrack.getVolumeStr ());
        }

        d.allDone ();
    }
}