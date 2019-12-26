// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mki.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.LightGuideImpl;


/**
 * Control the key colors (light guide) of the Kontrol 1.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1LightGuide extends LightGuideImpl
{
    private final Kontrol1UsbDevice usbDevice;


    /**
     * Constructor.
     *
     * @param colorManager The color manager
     * @param usbDevice The usb device
     */
    public Kontrol1LightGuide (final ColorManager colorManager, final Kontrol1UsbDevice usbDevice)
    {
        super (colorManager, null);

        this.usbDevice = usbDevice;
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int channel, final int note, final int color)
    {
        final int n = note - this.usbDevice.getFirstNote ();
        final ColorEx colorEx = this.colorManager.getColor (color, null);
        final int [] rgb = colorEx.toIntRGB127 ();
        this.usbDevice.setKeyLED (n, rgb[0], rgb[1], rgb[2]);
    }
}
