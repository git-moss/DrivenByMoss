// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.osc;

import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.osc.protocol.OSCModel;
import de.mossgrabers.osc.protocol.OSCParser;
import de.mossgrabers.osc.protocol.OSCWriter;

import com.bitwig.extension.api.opensoundcontrol.OscAddressSpace;
import com.bitwig.extension.api.opensoundcontrol.OscConnection;
import com.bitwig.extension.api.opensoundcontrol.OscModule;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Bitwig Studio extension to support the OSC protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCExtension extends ControllerExtension
{
    private OSCWriter           writer;
    private OSCParser           parser;
    private OSCConfiguration    configuration;
    private DefaultValueChanger valueChanger;


    /**
     * Constructor.
     *
     * @param extensionDefinition The extension definition
     * @param host The Bitwig host
     */
    protected OSCExtension (final OSCExtensionDefinition extensionDefinition, final ControllerHost host)
    {
        super (extensionDefinition, host);

        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new OSCConfiguration (this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        this.configuration.init (this.getHost ().getPreferences ());

        final Scales scales = new Scales (this.valueChanger, 0, 128, 128, 1);
        scales.setChromatic (true);

        final ControllerHost host = this.getHost ();

        final OscModule oscModule = host.getOscModule ();
        final OscAddressSpace addressSpace = oscModule.createAddressSpace ();

        this.configuration.addSettingObserver (OSCConfiguration.DEBUG_COMMANDS, () -> {
            addressSpace.setShouldLogMessages (this.configuration.getDebugCommands ());
        });

        final OSCModel model = new OSCModel (host, new ColorManager (), this.valueChanger, scales);
        this.parser = new OSCParser (host, this.writer, this.configuration, model);

        addressSpace.registerDefaultMethod (this.parser);

        final int sendPort = this.configuration.getSendPort ();
        final OscConnection udpServer = oscModule.connectToUdpServer (this.configuration.getSendHost (), sendPort, oscModule.createAddressSpace ());

        this.writer = new OSCWriter (model, this.configuration, udpServer);

        final int receivePort = this.configuration.getReceivePort ();
        oscModule.createUdpServer (receivePort, addressSpace);

        host.scheduleTask ( () -> this.writer.flush (true), 1000);
        host.println ("Initialized.");
    }


    /** {@inheritDoc} */
    @Override
    public void exit ()
    {
        this.getHost ().println ("Exited.");
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.writer.flush (false);
    }
}
