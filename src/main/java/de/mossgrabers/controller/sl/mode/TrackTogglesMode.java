// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.mode;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.track.AbstractTrackMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Different track parameters which can be toggled and device navigation mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackTogglesMode extends AbstractTrackMode<SLControlSurface, SLConfiguration>
{
    private static final String OFF = "   Off";
    private static final String ON  = "   On";


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public TrackTogglesMode (final SLControlSurface surface, final IModel model)
    {
        super ("Track", surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITrack t = this.model.getSelectedTrack ();
        final ITextDisplay d = this.surface.getTextDisplay ();
        if (t == null)
        {
            d.setRow (0, "                        Please select a track...                       ").done (0).clearRow (2).done (2);
        }
        else
        {
            final ICursorDevice device = this.model.getCursorDevice ();
            d.setCell (0, 0, "  Mute");
            d.setCell (2, 0, t.isMute () ? ON : OFF);
            d.setCell (0, 1, "  Solo");
            d.setCell (2, 1, t.isSolo () ? ON : OFF);
            d.setCell (0, 2, "Rec Arm");
            d.setCell (2, 2, t.isRecArm () ? ON : OFF);
            d.setCell (0, 3, " Write");
            d.setCell (2, 3, this.model.getTransport ().isWritingArrangerAutomation () ? ON : OFF);
            d.setCell (0, 4, " Browse");
            d.setCell (2, 4, "");
            d.setCell (0, 5, device.doesExist () ? device.getName (8) : "None");
            d.setCell (2, 5, device.isEnabled () ? "Enabled" : "Disabled");
            d.setCell (0, 6, "<<Device").setCell (2, 6, "");
            d.setCell (0, 7, "Device>>").setCell (2, 7, "").done (0).done (2);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}
