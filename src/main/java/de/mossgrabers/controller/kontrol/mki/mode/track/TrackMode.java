// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mki.mode.track;

import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1Display;
import de.mossgrabers.controller.kontrol.mki.mode.AbstractKontrol1Mode;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Edit track parameters mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode extends AbstractKontrol1Mode
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public TrackMode (final Kontrol1ControlSurface surface, final IModel model)
    {
        super ("Track", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
        final ITrack t = currentTrackBank.getSelectedItem ();
        final Kontrol1Display d = (Kontrol1Display) this.surface.getDisplay ();

        d.clear ();

        if (t == null)
        {
            d.setCell (0, 3, "  PLEASE").setCell (0, 4, "SELECT A").setCell (0, 5, "TRACK").allDone ();
            return;
        }

        final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();

        d.setCell (0, 0, (isEffectTrackBankActive ? "TR-FX " : "TRACK ") + (t.getPosition () + 1)).setCell (1, 0, StringUtils.shortenAndFixASCII (t.getName (), 8).toUpperCase ());

        d.setCell (0, 1, "VOLUME").setCell (1, 1, getSecondLineText (t)).setCell (0, 2, "PAN").setCell (1, 2, t.getPanStr (8));
        d.setBar (1, this.surface.getContinuous (ContinuousID.KNOB1).isTouched (), t.getVolume ());
        d.setPanBar (2, this.surface.getContinuous (ContinuousID.KNOB2).isTouched (), t.getPan ());

        if (!isEffectTrackBankActive)
        {
            final ISendBank sendBank = t.getSendBank ();
            for (int i = 0; i < 6; i++)
            {
                final int pos = 3 + i;
                final ISend sendData = sendBank.getItem (i);
                d.setCell (0, pos, StringUtils.shortenAndFixASCII (sendData.getName (8), 8).toUpperCase ()).setCell (1, pos, sendData.getDisplayedValue (8));
                d.setBar (pos, this.surface.getContinuous (ContinuousID.get (ContinuousID.KNOB3, i)).isTouched () && sendData.doesExist (), sendData.getValue ());
            }
        }
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();
        if (selectedTrack == null)
            return;

        switch (index)
        {
            case 0:
                selectedTrack.changeVolume (value);
                return;
            case 1:
                selectedTrack.changePan (value);
                return;
            default:
                selectedTrack.getSendBank ().getItem (index - 2).changeValue (value);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected ITrackBank getBank ()
    {
        return this.model.getCurrentTrackBank ();
    }
}
