// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
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
    public static final int VST2_KOMPLETE_ID = 1315523403;

    private final Parameter nikb;


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

        this.nikb = specificVst2Device.createParameter (0);
        this.nikb.name ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public String getID ()
    {
        return this.nikb.name ().get ();
    }
}
