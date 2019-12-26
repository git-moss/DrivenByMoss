// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mki.mode.track;

import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1Display;
import de.mossgrabers.controller.kontrol.mki.mode.AbstractKontrol1Mode;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Volume mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumeMode extends AbstractKontrol1Mode
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public VolumeMode (final Kontrol1ControlSurface surface, final IModel model)
    {
        super ("Volume", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Kontrol1Display d = (Kontrol1Display) this.surface.getDisplay ();

        d.clear ();

        final ITrackBank tb = this.model.getCurrentTrackBank ();

        final StringBuilder sb = new StringBuilder ();
        final int positionFirst = tb.getScrollPosition ();
        if (positionFirst >= 0)
        {
            sb.append (Integer.toString (positionFirst + 1));
            final int positionLast = tb.getPositionOfLastItem ();
            if (positionLast >= 0)
                sb.append (" - ").append (Integer.toString (positionLast + 1));
        }

        d.setCell (0, 0, this.model.isEffectTrackBankActive () ? "VOL-FX" : "VOLUME").setCell (1, 0, sb.toString ());

        final ITrack selTrack = tb.getSelectedItem ();

        final int selIndex = selTrack == null ? -1 : selTrack.getIndex ();
        for (int i = 0; i < 8; i++)
        {
            final boolean isSel = i == selIndex;
            final ITrack t = tb.getItem (i);
            final String n = StringUtils.shortenAndFixASCII (t.getName (), isSel ? 7 : 8).toUpperCase ();
            d.setCell (0, 1 + i, isSel ? ">" + n : n).setCell (1, 1 + i, getSecondLineText (t));
            d.setBar (1 + i, this.surface.getContinuous (ContinuousID.get (ContinuousID.KNOB1, i)).isTouched () && t.doesExist (), t.getVolume ());
        }
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        this.model.getCurrentTrackBank ().getItem (index).changeVolume (value);
    }


    /** {@inheritDoc} */
    @Override
    protected ITrackBank getBank ()
    {
        return this.model.getCurrentTrackBank ();
    }
}