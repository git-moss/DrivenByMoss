// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mki.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.LightGuideImpl;


/**
 * Control the key colors (light guide) of the Kontrol 1.
 *
 * @author Jürgen Moßgraber
 */
public class Kontrol1LightGuide extends LightGuideImpl
{
    private final Kontrol1UsbDevice usbDevice;


    /**
     * Constructor.
     *
     * @param colorManager The color manager
     * @param usbDevice The USB device
     */
    public Kontrol1LightGuide (final ColorManager colorManager, final Kontrol1UsbDevice usbDevice)
    {
        super (0, 128, colorManager, null);

        this.usbDevice = usbDevice;
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateToController (final int note)
    {
        final int [] translated = super.translateToController (note);

        final int firstNote = this.usbDevice.getFirstNote ();
        if (note < firstNote || note >= firstNote + this.usbDevice.getNumKeys ())
            translated[1] = -1;
        else
            translated[1] = note - firstNote;
        return translated;
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int channel, final int note, final int color)
    {
        if (note < 0 || note >= 88)
            return;
        final ColorEx colorEx = this.colorManager.getColor (color, null);
        final int [] rgb = colorEx.toIntRGB127 ();
        // After previous translation to controller, note is only the index into the 0..N LEDs
        this.usbDevice.setKeyLED (note, rgb[0], rgb[1], rgb[2]);
    }
}
