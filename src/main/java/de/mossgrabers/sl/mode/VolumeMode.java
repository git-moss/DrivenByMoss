// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.StringUtils;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.sl.SLConfiguration;
import de.mossgrabers.sl.controller.SLControlSurface;
import de.mossgrabers.sl.controller.SLDisplay;


/**
 * Volume mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumeMode extends AbstractMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public VolumeMode (final SLControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Display d = this.surface.getDisplay ();

        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        if (masterTrack.isSelected ())
        {
            d.clear ();
            final String n = StringUtils.shortenAndFixASCII (masterTrack.getName (), 7);
            d.setCell (1, 0, SLDisplay.RIGHT_ARROW + n).setCell (3, 0, masterTrack.getVolumeStr (8)).done (1).done (3);
            return;
        }

        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack selTrack = tb.getSelectedTrack ();

        final int selIndex = selTrack == null ? -1 : selTrack.getIndex ();
        for (int i = 0; i < 8; i++)
        {
            final boolean isSel = i == selIndex;
            final ITrack t = tb.getTrack (i);
            final String n = StringUtils.shortenAndFixASCII (t.getName (), isSel ? 7 : 8);
            d.setCell (1, i, isSel ? SLDisplay.RIGHT_ARROW + n : n).setCell (3, i, t.getVolumeStr (8));
        }
        d.done (1).done (3);
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}