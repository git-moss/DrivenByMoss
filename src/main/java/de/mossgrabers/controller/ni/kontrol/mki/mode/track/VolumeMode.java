// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mki.mode.track;

import de.mossgrabers.controller.ni.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.controller.ni.kontrol.mki.controller.Kontrol1Display;
import de.mossgrabers.controller.ni.kontrol.mki.mode.AbstractKontrol1Mode;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Locale;
import java.util.Optional;


/**
 * Volume mode.
 *
 * @author Jürgen Moßgraber
 */
public class VolumeMode extends AbstractKontrol1Mode<ITrack>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public VolumeMode (final Kontrol1ControlSurface surface, final IModel model)
    {
        super ("Volume", surface, model, model.getCurrentTrackBank ());

        model.addTrackBankObserver (this::switchBanks);

        this.setParameterProvider (new VolumeParameterProvider (model));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Kontrol1Display d = (Kontrol1Display) this.surface.getDisplay ();

        d.clear ();
        d.setCell (0, 0, this.model.isEffectTrackBankActive () ? "VOL-FX" : "VOLUME").setCell (1, 0, this.formatPageRange ("%d - %d"));

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final Optional<ITrack> selTrack = tb.getSelectedItem ();

        final int selIndex = selTrack.isEmpty () ? -1 : selTrack.get ().getIndex ();
        for (int i = 0; i < 8; i++)
        {
            final boolean isSel = i == selIndex;
            final ITrack t = tb.getItem (i);
            final String n = StringUtils.shortenAndFixASCII (t.getName (), isSel ? 7 : 8).toUpperCase (Locale.US);
            d.setCell (0, 1 + i, isSel ? ">" + n : n).setCell (1, 1 + i, getSecondLineText (t));
            d.setBar (1 + i, this.surface.getContinuous (ContinuousID.get (ContinuousID.KNOB1, i)).isTouched () && t.doesExist (), t.getVolume ());
        }
        d.allDone ();
    }
}