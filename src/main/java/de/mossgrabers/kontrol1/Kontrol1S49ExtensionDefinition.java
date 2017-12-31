// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for Native Instruments Komplete Kontrol 1 S49 controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1S49ExtensionDefinition extends AbstractKontrol1ExtensionDefinition
{
    private static final short PRODUCT_ID_S49 = 0x1350;


    /**
     * Constructor.
     */
    public Kontrol1S49ExtensionDefinition ()
    {
        super (PRODUCT_ID_S49);
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return "Komplete Kontrol S49";
    }


    /** {@inheritDoc} */
    @Override
    public Kontrol1Extension createInstance (final ControllerHost host)
    {
        return new Kontrol1Extension (this, host);
    }
}
