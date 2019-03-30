// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mki.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;


/**
 * Pad grid to control the key colors of the Kontrol 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1PadGrid extends PadGridImpl
{
    private final Kontrol1UsbDevice usbDevice;


    /**
     * Constructor.
     *
     * @param colorManager The color manager
     * @param usbDevice The usb device
     */
    public Kontrol1PadGrid (final ColorManager colorManager, final Kontrol1UsbDevice usbDevice)
    {
        super (colorManager, null, 1, 88, 0);

        this.usbDevice = usbDevice;
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int note, final int color)
    {
        final int [] rgb = Kontrol1Colors.getColorFromIndex (color);
        final int n = note - this.usbDevice.getFirstNote ();
        this.usbDevice.setKeyLED (n, rgb[0], rgb[1], rgb[2]);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendBlinkState (final int note, final int blinkColor, final boolean fast)
    {
        // No blinky blink
    }
}
