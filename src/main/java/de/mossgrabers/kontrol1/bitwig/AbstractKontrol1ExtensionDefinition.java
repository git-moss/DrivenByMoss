// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1.bitwig;

import de.mossgrabers.framework.bitwig.BitwigSetupFactory;
import de.mossgrabers.framework.bitwig.configuration.SettingsUI;
import de.mossgrabers.framework.bitwig.daw.HostImpl;
import de.mossgrabers.framework.bitwig.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.framework.controller.IControllerSetup;
import de.mossgrabers.kontrol1.Kontrol1ControllerDefinition;
import de.mossgrabers.kontrol1.Kontrol1ControllerSetup;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.UsbDeviceInfo;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.List;


/**
 * Definition class for Native Instruments Komplete Kontrol 1 Sxx controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractKontrol1ExtensionDefinition extends AbstractControllerExtensionDefinition
{
    /** Kontrol 1 USB Vendor ID. */
    private static final short    VENDOR_ID  = 0x17cc;

    private static final short [] PRODUCT_ID =
    {
        0x1340,
        0x1350,
        0x1360,
        0x1410
    };

    private short                 productID;


    /**
     * Constructor.
     *
     * @param modelIndex The index of the specific model
     */
    public AbstractKontrol1ExtensionDefinition (final int modelIndex)
    {
        super (new Kontrol1ControllerDefinition (modelIndex));
        this.productID = PRODUCT_ID[modelIndex];
    }


    /** {@inheritDoc} */
    @Override
    public void listUsbEndpoints (final List<UsbDeviceInfo> endpoints)
    {
        endpoints.add (new UsbDeviceInfo (VENDOR_ID, this.productID));
    }


    /** {@inheritDoc} */
    @Override
    public void listAutoDetectionMidiPortNames (final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
    {
        list.add (new String []
        {
            "Komplete Kontrol - 1"
        }, new String [0]);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isUsingBetaAPI ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup getControllerSetup (final ControllerHost host)
    {
        return new Kontrol1ControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUI (host.getPreferences ()));
    }
}
