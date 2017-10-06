// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.sl.SLConfiguration;
import de.mossgrabers.sl.controller.SLControlSurface;


/**
 * Different track parameters which can be toggled and device navigation mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackTogglesMode extends AbstractMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public TrackTogglesMode (final SLControlSurface surface, final Model model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final TrackData t = this.model.getCurrentTrackBank ().getSelectedTrack ();
        final Display d = this.surface.getDisplay ();

        if (t == null)
        {
            d.setRow (0, "                        Please select a track...                       ").clearRow (2).done (2);
        }
        else
        {
            final CursorDeviceProxy device = this.model.getCursorDevice ();
            d.setCell (0, 0, "  Mute");
            d.setCell (2, 0, t.isMute () ? "   On" : "   Off");
            d.setCell (0, 1, "  Solo");
            d.setCell (2, 1, t.isSolo () ? "   On" : "   Off");
            d.setCell (0, 2, "Rec Arm");
            d.setCell (2, 2, t.isRecArm() ? "   On" : "   Off");
            d.setCell (0, 3, " Write");
            d.setCell (2, 3, this.model.getTransport ().isWritingArrangerAutomation () ? "   On" : "   Off");
            d.setCell (0, 4, " Browse");
            d.setCell (2, 4, "");
            d.setCell (0, 5, device.getName ().length () > 0 ? device.getName ().length () > 8 ? device.getName ().substring (0, 8) : device.getName () : "None");
            d.setCell (2, 5, device.isEnabled () ? "Enabled" : "Disabled");
            d.setCell (0, 6, "<<Device").setCell (2, 6, "");
            d.setCell (0, 7, "Device>>").setCell (2, 7, "").done (0).done (2);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}
