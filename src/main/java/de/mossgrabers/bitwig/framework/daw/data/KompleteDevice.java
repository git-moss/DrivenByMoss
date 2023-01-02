// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;

import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SpecificPluginDevice;


/**
 * Komplete Kontrol device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KompleteDevice extends SpecificDeviceImpl
{
    /** The ID of the Komplete Kontrol VST2 plugin. */
    public static final int    VST2_KOMPLETE_ID = 1315523403;
    /** The ID of the Komplete Kontrol VST3 plugin. */
    public static final String VST3_KOMPLETE_ID = "5653544E694B4B6B6F6D706C65746520";

    private final Parameter    nikbVst2;
    private final Parameter    nikbVst3;


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param device The device to encapsulate
     */
    public KompleteDevice (final IHost host, final IValueChanger valueChanger, final Device device)
    {
        super (host, valueChanger, device, 0, 0, 0, 0, 0, 0);

        final SpecificPluginDevice specificVst2Device = device.createSpecificVst2Device (VST2_KOMPLETE_ID);
        this.nikbVst2 = specificVst2Device.createParameter (0);
        this.nikbVst2.exists ().markInterested ();
        this.nikbVst2.name ().markInterested ();

        final SpecificPluginDevice specificVst3Device = device.createSpecificVst3Device (VST3_KOMPLETE_ID);
        this.nikbVst3 = specificVst3Device.createParameter (0);
        this.nikbVst3.exists ().markInterested ();
        this.nikbVst3.name ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public String getID ()
    {
        if (this.nikbVst2.exists ().get ())
            return this.nikbVst2.name ().get ();
        return this.nikbVst3.name ().get ();
    }
}
