// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.mode.track;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2ControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Volume mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumeMode extends AbstractMode<Kontrol2ControlSurface, Kontrol2Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public VolumeMode (final Kontrol2ControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        // TODO Implement volume display
        // final Kontrol2Display d = (Kontrol2Display) this.surface.getDisplay ();
        //
        // d.clear ();
        //
        // final ITrackBank tb = this.model.getCurrentTrackBank ();
        //
        // final StringBuilder sb = new StringBuilder ();
        // final int positionFirst = tb.getTrackPositionFirst ();
        // if (positionFirst >= 0)
        // {
        // sb.append (Integer.toString (positionFirst + 1));
        // final int positionLast = tb.getTrackPositionLast ();
        // if (positionLast >= 0)
        // sb.append (" - ").append (Integer.toString (positionLast + 1));
        // }
        //
        // d.setCell (0, 0, this.model.isEffectTrackBankActive () ? "VOL-FX" : "VOLUME").setCell (1,
        // 0, sb.toString ());
        //
        // final ITrack selTrack = tb.getSelectedTrack ();
        //
        // final int selIndex = selTrack == null ? -1 : selTrack.getIndex ();
        // for (int i = 0; i < 8; i++)
        // {
        // final boolean isSel = i == selIndex;
        // final ITrack t = tb.getItem (i);
        // final String n = StringUtils.shortenAndFixASCII (t.getName (), isSel ? 7 : 8).toUpperCase
        // ();
        // d.setCell (0, 1 + i, isSel ? ">" + n : n).setCell (1, 1 + i, t.isMute () ? "-MUTED-" :
        // t.isSolo () ? "-SOLO-" : t.getVolumeStr (8));
        //
        // d.setBar (1 + i, this.surface.isPressed (Kontrol2ControlSurface.TOUCH_ENCODER_1 + i) &&
        // t.doesExist (), t.getVolume ());
        // }
        // d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCurrentTrackBank ().getItem (index).changeVolume (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}