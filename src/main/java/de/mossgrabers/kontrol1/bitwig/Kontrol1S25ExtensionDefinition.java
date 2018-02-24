// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1.bitwig;

/**
 * Definition class for Native Instruments Komplete Kontrol 1 S25 controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1S25ExtensionDefinition extends AbstractKontrol1ExtensionDefinition
{
    private static final short PRODUCT_ID_S25 = 0x1340;


    /**
     * Constructor.
     */
    public Kontrol1S25ExtensionDefinition ()
    {
        super (PRODUCT_ID_S25);
    }


    /** {@inheritDoc} */
    @Override
    public String getHardwareModel ()
    {
        return "Komplete Kontrol S25";
    }
}
