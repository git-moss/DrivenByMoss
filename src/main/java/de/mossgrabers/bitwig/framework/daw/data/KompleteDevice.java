// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import java.util.ArrayList;
import java.util.List;

import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SpecificPluginDevice;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;


/**
 * Komplete Kontrol device.
 *
 * @author Jürgen Moßgraber
 */
public class KompleteDevice extends SpecificDeviceImpl
{
    /** The ID of the Komplete Kontrol VST2 plugin. */
    public static final int       VST2_KOMPLETE_ID   = 1315523403;
    /** The ID of the Komplete Kontrol VST3 plugin. */
    public static final String    VST3_KOMPLETE_ID   = "5653544E694B4B6B6F6D706C65746520";
    /** The ID of the Konktakt 7 VST3 plugin. */
    public static final String    VST3_KONTAKT_7_ID  = "5653544E694B376B6F6E74616B742037";
    /** The ID of the Konktakt 8 VST3 plugin. */
    public static final String    VST3_KONTAKT_8_ID  = "5653544E694B386B6F6E74616B742038";
    /** The ID of the Maschine 3 VST3 plugin. */
    public static final String    VST3_MASCHINE_3_ID = "5653544E694D336D61736368696E6520";

    private final List<Parameter> parameters         = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param device The device to encapsulate
     */
    public KompleteDevice (final IHost host, final IValueChanger valueChanger, final Device device)
    {
        super (host, valueChanger, device, 0, 0, 0, 0, 0, 0, 0);

        this.registerSpecificDevice (device.createSpecificVst2Device (VST2_KOMPLETE_ID), 0);
        this.registerSpecificDevice (device.createSpecificVst3Device (VST3_KOMPLETE_ID), 0);
        this.registerSpecificDevice (device.createSpecificVst3Device (VST3_KONTAKT_7_ID), 2048);
        this.registerSpecificDevice (device.createSpecificVst3Device (VST3_KONTAKT_8_ID), 2048);
        this.registerSpecificDevice (device.createSpecificVst3Device (VST3_MASCHINE_3_ID), 128);
    }


    private void registerSpecificDevice (final SpecificPluginDevice specificDevice, final int parameterIndex)
    {
        final Parameter parameter = specificDevice.createParameter (parameterIndex);
        parameter.exists ().markInterested ();
        parameter.name ().markInterested ();
        this.parameters.add (parameter);
    }


    /** {@inheritDoc} */
    @Override
    public String getID ()
    {
        for (final Parameter parameter: this.parameters)
        {
            if (parameter.exists ().get ())
                return parameter.name ().get ();
        }
        return "";
    }
}
