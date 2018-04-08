// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrolosc;

import de.mossgrabers.framework.bitwig.configuration.SettingsUI;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.kontrolosc.protocol.OSCModel;
import de.mossgrabers.kontrolosc.protocol.OSCParser;
import de.mossgrabers.kontrolosc.protocol.OSCWriter;

import com.bitwig.extension.api.opensoundcontrol.OscAddressSpace;
import com.bitwig.extension.api.opensoundcontrol.OscModule;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Bitwig Studio extension to support the OSC protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolOSCExtension extends ControllerExtension
{
    /** Global protocol version switch. */
    public static final boolean     IS_16          = false;

    /** Globally enable logging. */
    public static final boolean     ENABLE_LOGGING = false;

    private OSCWriter               writer;
    private KontrolOSCConfiguration configuration;
    private DefaultValueChanger     valueChanger;


    /**
     * Constructor.
     *
     * @param extensionDefinition The extension definition
     * @param host The Bitwig host
     */
    protected KontrolOSCExtension (final KontrolOSCExtensionDefinition extensionDefinition, final ControllerHost host)
    {
        super (extensionDefinition, host);

        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new KontrolOSCConfiguration (this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        this.configuration.init (new SettingsUI (this.getHost ().getPreferences ()));

        final Scales scales = new Scales (this.valueChanger, 0, 128, 128, 1);
        scales.setChromatic (true);

        final ControllerHost host = this.getHost ();
        final OSCModel model = new OSCModel (host, new ColorManager (), this.valueChanger, scales);
        final OscModule oscModule = host.getOscModule ();

        // Send OSC messages
        this.writer = new OSCWriter (IS_16, ENABLE_LOGGING, model, this.configuration, oscModule);

        // Receive OSC messages
        final OscAddressSpace addressSpace = oscModule.createAddressSpace ();
        addressSpace.registerDefaultMethod (new OSCParser (IS_16, ENABLE_LOGGING, host, this.writer, this.configuration, model));
        oscModule.createUdpServer (this.configuration.getReceivePort (), addressSpace);

        host.getMidiInPort (0).createNoteInput ("Kontrol OSC Midi");

        host.println ("Initialized.");
    }


    /** {@inheritDoc} */
    @Override
    public void exit ()
    {
        this.writer.shutdown ();

        this.getHost ().println ("Exited.");
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.writer.sendFrequentProperties (false);
    }
}
