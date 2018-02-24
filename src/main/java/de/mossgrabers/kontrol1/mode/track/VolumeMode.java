// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1.mode.track;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.StringUtils;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.kontrol1.Kontrol1Configuration;
import de.mossgrabers.kontrol1.controller.Kontrol1ControlSurface;
import de.mossgrabers.kontrol1.controller.Kontrol1Display;


/**
 * Volume mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumeMode extends AbstractMode<Kontrol1ControlSurface, Kontrol1Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public VolumeMode (final Kontrol1ControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Kontrol1Display d = (Kontrol1Display) this.surface.getDisplay ();

        d.clear ();

        final IChannelBank tb = this.model.getCurrentTrackBank ();

        final StringBuilder sb = new StringBuilder ();
        final int positionFirst = tb.getTrackPositionFirst ();
        if (positionFirst >= 0)
        {
            sb.append (Integer.toString (positionFirst + 1));
            final int positionLast = tb.getTrackPositionLast ();
            if (positionLast >= 0)
                sb.append (" - ").append (Integer.toString (positionLast + 1));
        }

        d.setCell (0, 0, this.model.isEffectTrackBankActive () ? "VOL-FX" : "VOLUME").setCell (1, 0, sb.toString ());

        final ITrack selTrack = tb.getSelectedTrack ();

        final int selIndex = selTrack == null ? -1 : selTrack.getIndex ();
        for (int i = 0; i < 8; i++)
        {
            final boolean isSel = i == selIndex;
            final ITrack t = tb.getTrack (i);
            final String n = this.optimizeName (StringUtils.fixASCII (t.getName ()), isSel ? 7 : 8).toUpperCase ();
            d.setCell (0, 1 + i, isSel ? ">" + n : n).setCell (1, 1 + i, t.isMute () ? "-MUTED-" : t.isSolo () ? "-SOLO-" : t.getVolumeStr (8));

            d.setBar (1 + i, this.surface.isPressed (Kontrol1ControlSurface.TOUCH_ENCODER_1 + i) && t.doesExist (), t.getVolume ());
        }
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCurrentTrackBank ().getTrack (index).changeVolume (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}