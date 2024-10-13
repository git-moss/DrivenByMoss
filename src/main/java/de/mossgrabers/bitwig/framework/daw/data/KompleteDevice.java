// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

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
    public static final int    VST2_KOMPLETE_ID  = 1315523403;
    /** The ID of the Komplete Kontrol VST3 plugin. */
    public static final String VST3_KOMPLETE_ID  = "5653544E694B4B6B6F6D706C65746520";
    /** The ID of the Konktakt 7 VST3 plugin. */
    public static final String VST3_KONTAKT_7_ID = "5653544E694B376B6F6E74616B742037";
    /** The ID of the Konktakt 8 VST3 plugin. */
    public static final String VST3_KONTAKT_8_ID = "5653544E694B386B6F6E74616B742038";

    private final Parameter    nikbVst2;
    private final Parameter    nikbVst3;
    private final Parameter    nikbVst3Kontakt7;
    private final Parameter    nikbVst3Kontakt8;


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

        SpecificPluginDevice specificDevice = device.createSpecificVst2Device (VST2_KOMPLETE_ID);
        this.nikbVst2 = specificDevice.createParameter (0);
        this.nikbVst2.exists ().markInterested ();
        this.nikbVst2.name ().markInterested ();

        specificDevice = device.createSpecificVst3Device (VST3_KOMPLETE_ID);
        this.nikbVst3 = specificDevice.createParameter (0);
        this.nikbVst3.exists ().markInterested ();
        this.nikbVst3.name ().markInterested ();

        specificDevice = device.createSpecificVst3Device (VST3_KONTAKT_7_ID);
        this.nikbVst3Kontakt7 = specificDevice.createParameter (2048);
        this.nikbVst3Kontakt7.exists ().markInterested ();
        this.nikbVst3Kontakt7.name ().markInterested ();

        specificDevice = device.createSpecificVst3Device (VST3_KONTAKT_8_ID);
        this.nikbVst3Kontakt8 = specificDevice.createParameter (2048);
        this.nikbVst3Kontakt8.exists ().markInterested ();
        this.nikbVst3Kontakt8.name ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public String getID ()
    {
        if (this.nikbVst2.exists ().get ())
            return this.nikbVst2.name ().get ();
        if (this.nikbVst3.exists ().get ())
            return this.nikbVst3.name ().get ();
        if (this.nikbVst3Kontakt7.exists ().get ())
            return this.nikbVst3Kontakt7.name ().get ();
        if (this.nikbVst3Kontakt8.exists ().get ())
            return this.nikbVst3Kontakt8.name ().get ();
        return "";
    }
}
