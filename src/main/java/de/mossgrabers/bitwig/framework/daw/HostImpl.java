// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.osc.OpenSoundControlMessageImpl;
import de.mossgrabers.bitwig.framework.osc.OpenSoundControlServerImpl;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.osc.IOpenSoundControlCallback;
import de.mossgrabers.framework.osc.IOpenSoundControlMessage;
import de.mossgrabers.framework.osc.IOpenSoundControlServer;

import com.bitwig.extension.api.opensoundcontrol.OscAddressSpace;
import com.bitwig.extension.api.opensoundcontrol.OscModule;
import com.bitwig.extension.controller.api.ControllerHost;

import java.util.List;


/**
 * Encapsulates the ControllerHost instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HostImpl implements IHost
{
    private ControllerHost host;


    /**
     * Constructor.
     *
     * @param host The host
     */
    public HostImpl (final ControllerHost host)
    {
        this.host = host;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Bitwig";
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasClips ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPinning ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasCrossfader ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDrumDevice ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasRepeat ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void scheduleTask (final Runnable task, final long delay)
    {
        this.host.scheduleTask (task, delay);
    }


    /** {@inheritDoc} */
    @Override
    public void error (final String text)
    {
        this.host.errorln (text);
    }


    /** {@inheritDoc} */
    @Override
    public void error (final String text, final Exception ex)
    {
        this.host.errorln (text);
        this.host.errorln (ex.getClass () + ":" + ex.getMessage ());
    }


    /** {@inheritDoc} */
    @Override
    public void println (final String text)
    {
        this.host.println (text);
    }


    /** {@inheritDoc} */
    @Override
    public void showNotification (final String message)
    {
        this.host.showPopupNotification (message);
    }


    /** {@inheritDoc} */
    @Override
    public IOpenSoundControlServer connectToOSCServer (final String serverAddress, final int serverPort)
    {
        // TODO Fix required: Can only be called in init but needs to listen to host and port
        // changes
        final OscModule oscModule = this.host.getOscModule ();
        return new OpenSoundControlServerImpl (oscModule.connectToUdpServer (serverAddress, serverPort, oscModule.createAddressSpace ()));
    }


    /** {@inheritDoc} */
    @Override
    public void createOSCServer (final IOpenSoundControlCallback callback, final int port)
    {
        final OscModule oscModule = this.host.getOscModule ();
        final OscAddressSpace addressSpace = oscModule.createAddressSpace ();
        addressSpace.registerDefaultMethod ( (source, message) -> callback.handle (new OpenSoundControlMessageImpl (message)));
        oscModule.createUdpServer (port, addressSpace);
    }


    /** {@inheritDoc} */
    @Override
    public IOpenSoundControlMessage createOSCMessage (final String address, final List<Object> values)
    {
        return new OpenSoundControlMessageImpl (address, values);
    }


    /** {@inheritDoc} */
    @Override
    public void releaseOSC ()
    {
        // This is automatically handled by the Bitwig framework
    }


    /** {@inheritDoc} */
    @Override
    public void sendDatagramPacket (final String string, final int port, final byte [] data)
    {
        this.host.sendDatagramPacket (string, port, data);
    }
}
