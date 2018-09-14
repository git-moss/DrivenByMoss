// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mki.view;

import de.mossgrabers.controller.kontrol.usb.mki.Kontrol1Configuration;
import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.view.AbstractView;


/**
 * The view for controlling the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ControlView extends AbstractView<Kontrol1ControlSurface, Kontrol1Configuration>
{
    private static final double [] COLOR_OFF = new double []
    {
        0,
        0,
        0
    };


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ControlView (final Kontrol1ControlSurface surface, final IModel model)
    {
        super ("Control", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        final ITransport transport = this.model.getTransport ();

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_SHIFT, this.surface.isShiftPressed () ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_SCALE, this.surface.getConfiguration ().isScaleIsActive () ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_ARP, this.surface.isShiftPressed () && transport.isMetronomeTicksOn () || !this.surface.isShiftPressed () && transport.isMetronomeOn () ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_LOOP, transport.isLoop () ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_RWD, this.surface.isPressed (Kontrol1ControlSurface.BUTTON_RWD) ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_FWD, this.surface.isPressed (Kontrol1ControlSurface.BUTTON_FWD) ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_PLAY, transport.isPlaying () ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_REC, transport.isRecording () ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_STOP, this.surface.isPressed (Kontrol1ControlSurface.BUTTON_STOP) ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);

        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_PAGE_LEFT, this.surface.isPressed (Kontrol1ControlSurface.BUTTON_PAGE_LEFT) ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol1ControlSurface.BUTTON_PAGE_RIGHT, this.surface.isPressed (Kontrol1ControlSurface.BUTTON_PAGE_RIGHT) ? Kontrol1ControlSurface.BUTTON_STATE_HI : Kontrol1ControlSurface.BUTTON_STATE_ON);

        // Update all mode relevant buttons
        this.surface.getModeManager ().getActiveOrTempMode ().updateFirstRow ();

        this.surface.updateButtonLEDs ();

        final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
        final ITrack t = currentTrackBank.getSelectedItem ();
        this.updateKeyLEDs (t == null ? COLOR_OFF : t.getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Intentionally empty
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
        // Intentionally empty
    }


    private void updateKeyLEDs (final double [] color)
    {
        int red = 0;
        int green = 0;
        int blue = 0;

        final boolean isActive = this.surface.getConfiguration ().isScaleIsActive ();
        if (isActive)
        {
            red = (int) Math.round (color[0] * 127);
            green = (int) Math.round (color[1] * 127);
            blue = (int) Math.round (color[2] * 127);
        }

        if (this.scales.isChromatic () || !isActive)
        {
            for (int i = 0; i < 88; i++)
                this.surface.setKeyLED (i, red, green, blue);
        }
        else
        {
            final Scale scale = this.scales.getScale ();
            final int scaleOffset = this.scales.getScaleOffset ();
            for (int i = 0; i < 88; i++)
            {
                final int key = i % 12;
                final boolean inScale = scale.isInScale (key);
                final int brighter = scaleOffset == key ? 10 : 0;
                this.surface.setKeyLED (i, inScale ? Math.min (red + brighter, 127) : 0, inScale ? Math.min (green + brighter, 127) : 0, inScale ? Math.min (blue + brighter, 127) : 0);
            }
        }

        this.surface.updateKeyLEDs ();
    }
}