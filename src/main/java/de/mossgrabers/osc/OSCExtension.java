// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.osc;

import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.osc.protocol.OSCModel;
import de.mossgrabers.osc.protocol.OSCParser;
import de.mossgrabers.osc.protocol.OSCWriter;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.ControllerHost;
import com.illposed.osc.utility.OSCByteArrayToJavaConverter;
import com.illposed.osc.utility.OSCPacketDispatcher;


/**
 * Bitwig Studio extension to support the OSC protocol.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCExtension extends ControllerExtension
{
    private OSCWriter                         writer;
    private OSCParser                         parser;
    private OSCConfiguration                  configuration;
    private DefaultValueChanger               valueChanger;

    private final OSCByteArrayToJavaConverter converter  = new OSCByteArrayToJavaConverter ();
    private final OSCPacketDispatcher         dispatcher = new OSCPacketDispatcher ();


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
        final OSCModel model = new OSCModel (host, new ColorManager (), this.valueChanger, scales);
        this.writer = new OSCWriter (model, this.configuration);
        this.parser = new OSCParser (host, this.writer, this.configuration, model);

        this.dispatcher.addListener ( (messageAddress) -> {
            return true;
        }, this.parser);

        final String receiveHost = this.configuration.getReceiveHost ();
        final int receivePort = this.configuration.getReceivePort ();
        host.addDatagramPacketObserver (receiveHost, receivePort, this::handleOSCMessage);

        host.scheduleTask ( () -> this.writer.flush (true), 1000);
        host.println ("Initialized.");
    }


    private void handleOSCMessage (final byte [] data)
    {
        try
        {
            this.dispatcher.dispatchPacket (this.converter.convert (data, data.length));
        }
        catch (final IllegalArgumentException ex)
        {
            this.getHost ().errorln (ex.getLocalizedMessage ());
        }
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
