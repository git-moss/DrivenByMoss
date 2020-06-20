// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mode;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.track.VolumeMode;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing a volume parameter of all tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineVolumeMode extends VolumeMode<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MaschineVolumeMode (final MaschineControlSurface surface, final IModel model)
    {
        super (surface, model, false);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            String name = StringUtils.shortenAndFixASCII (t.getName (), 6);
            if (t.isSelected ())
                name = ">" + name;
            d.setCell (0, i, name);
            d.setCell (1, i, t.getVolumeStr (6));
        }
        d.allDone ();
    }
}
