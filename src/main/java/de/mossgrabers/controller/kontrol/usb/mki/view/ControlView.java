// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mki.view;

import de.mossgrabers.controller.kontrol.usb.mki.Kontrol1Configuration;
import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1Colors;
import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;


/**
 * The view for controlling the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ControlView extends AbstractPlayView<Kontrol1ControlSurface, Kontrol1Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ControlView (final Kontrol1ControlSurface surface, final IModel model)
    {
        super (surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        final ITransport transport = this.model.getTransport ();

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_SHIFT, this.surface.isShiftPressed () ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_SCALE, this.surface.getConfiguration ().isScaleIsActive () ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_ARP, this.surface.isShiftPressed () && transport.isMetronomeTicksOn () || !this.surface.isShiftPressed () && transport.isMetronomeOn () ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_LOOP, transport.isLoop () ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_RWD, this.surface.isPressed (Kontrol1ControlSurface.BUTTON_RWD) ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_FWD, this.surface.isPressed (Kontrol1ControlSurface.BUTTON_FWD) ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_PLAY, transport.isPlaying () ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_REC, transport.isRecording () ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_STOP, this.surface.isPressed (Kontrol1ControlSurface.BUTTON_STOP) ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_PAGE_LEFT, this.surface.isPressed (Kontrol1ControlSurface.BUTTON_PAGE_LEFT) ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_PAGE_RIGHT, this.surface.isPressed (Kontrol1ControlSurface.BUTTON_PAGE_RIGHT) ? Kontrol1Colors.BUTTON_STATE_HI : Kontrol1Colors.BUTTON_STATE_ON);

        // Update all mode relevant buttons
        this.surface.getModeManager ().getActiveOrTempMode ().updateFirstRow ();

        this.surface.updateButtonLEDs ();
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        final boolean isActive = this.surface.getConfiguration ().isScaleIsActive ();
        final int [] matrix = isActive && this.model.canSelectedTrackHoldNotes () ? Scales.getIdentityMatrix () : EMPTY_TABLE;
        // Only set the key manager not the translation matrix, since the keyboard always plays the
        // same notes!
        this.surface.scheduleTask ( () -> this.keyManager.setNoteMatrix (matrix), 6);
    }
}