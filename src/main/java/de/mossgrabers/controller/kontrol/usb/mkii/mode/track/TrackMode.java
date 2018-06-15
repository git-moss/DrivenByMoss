// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.mode.track;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2ControlSurface;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Edit track parameters mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode extends AbstractMode<Kontrol2ControlSurface, Kontrol2Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public TrackMode (final Kontrol2ControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        // TODO Implement track display
        // final IChannelBank currentTrackBank = this.model.getCurrentTrackBank ();
        // final ITrack t = currentTrackBank.getSelectedTrack ();
        // final Kontrol2Display d = (Kontrol2Display) this.surface.getDisplay ();
        //
        // d.clear ();
        //
        // if (t == null)
        // {
        // d.setCell (0, 3, " PLEASE").setCell (0, 4, "SELECT A").setCell (0, 5, "TRACK").allDone
        // ();
        // return;
        // }
        //
        // final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();
        //
        // d.setCell (0, 0, (isEffectTrackBankActive ? "TR-FX " : "TRACK ") + (t.getPosition () +
        // 1)).setCell (1, 0, StringUtils.shortenAndFixASCII (t.getName (), 8).toUpperCase ());
        //
        // d.setCell (0, 1, "VOLUME").setCell (1, 1, t.isMute () ? "-MUTED-" : t.isSolo () ?
        // "-SOLO-" : t.getVolumeStr (8)).setCell (0, 2, "PAN").setCell (1, 2, t.getPanStr (8));
        // d.setBar (1, this.surface.isPressed (Kontrol2ControlSurface.TOUCH_ENCODER_1), t.getVolume
        // ());
        // d.setPanBar (2, this.surface.isPressed (Kontrol2ControlSurface.TOUCH_ENCODER_2), t.getPan
        // ());
        //
        // if (!isEffectTrackBankActive)
        // {
        // for (int i = 0; i < 6; i++)
        // {
        // final int pos = 3 + i;
        // final ISend sendData = t.getSend (i);
        // d.setCell (0, pos, StringUtils.shortenAndFixASCII (sendData.getName (8), 8).toUpperCase
        // ()).setCell (1, pos, sendData.getDisplayedValue (8));
        // d.setBar (pos, this.surface.isPressed (Kontrol2ControlSurface.TOUCH_ENCODER_1 + 2 + i) &&
        // sendData.doesExist (), sendData.getValue ());
        // }
        // }
        // d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedTrack ();
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
                selectedTrack.getSend (index - 2).changeValue (value);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}
