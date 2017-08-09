// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.MasterTrackProxy;
import de.mossgrabers.framework.daw.data.TrackData;
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
    public VolumeMode (final SLControlSurface surface, final Model model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Display d = this.surface.getDisplay ();

        final MasterTrackProxy masterTrack = this.model.getMasterTrack ();
        if (masterTrack.isSelected ())
        {
            d.clear ();
            final String n = this.optimizeName (fixASCII (masterTrack.getName ()), 7);
            d.setCell (1, 0, SLDisplay.RIGHT_ARROW + n).setCell (3, 0, masterTrack.getVolumeStr (8)).done (1).done (3);
            return;
        }

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData selTrack = tb.getSelectedTrack ();

        final int selIndex = selTrack == null ? -1 : selTrack.getIndex ();
        for (int i = 0; i < 8; i++)
        {
            final boolean isSel = i == selIndex;
            final TrackData t = tb.getTrack (i);
            final String n = this.optimizeName (fixASCII (t.getName ()), isSel ? 7 : 8);
            d.setCell (1, i, isSel ? SLDisplay.RIGHT_ARROW + n : n).setCell (3, i, t.getVolumeStr (8));
        }
        d.done (1).done (3);
    }


    private static String fixASCII (final String name)
    {
        final StringBuilder str = new StringBuilder ();
        for (int i = 0; i < name.length (); i++)
        {
            final char c = name.charAt (i);
            if (c > 127)
            {
                switch (c)
                {
                    case 'Ä':
                        str.append ("Ae");
                        break;
                    case 'ä':
                        str.append ("ae");
                        break;
                    case 'Ö':
                        str.append ("Oe");
                        break;
                    case 'ö':
                        str.append ("oe");
                        break;
                    case 'Ü':
                        str.append ("Ue");
                        break;
                    case 'ü':
                        str.append ("ue");
                        break;
                    case 'ß':
                        str.append ("ss");
                        break;
                    default:
                        str.append ("?");
                        break;
                }
            }
            else
                str.append (c);
        }
        return str.toString ();
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}