// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.protocol;

import de.mossgrabers.controller.osc.OSCConfiguration;
import de.mossgrabers.controller.osc.module.IModule;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.osc.AbstractOpenSoundControlWriter;
import de.mossgrabers.framework.osc.IOpenSoundControlClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Writes the changed DAW status as OSC messages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCWriter extends AbstractOpenSoundControlWriter
{
    private static final Set<String> HEARTBEAT_MESSAGES = new HashSet<> (3);

    static
    {
        HEARTBEAT_MESSAGES.add ("/update");
        HEARTBEAT_MESSAGES.add ("/time/str");
        HEARTBEAT_MESSAGES.add ("/beat/str");
    }

    private final List<IModule> modules = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param oscClient The OSC client to write to
     * @param configuration The configuration
     */
    public OSCWriter (final IHost host, final IModel model, final IOpenSoundControlClient oscClient, final OSCConfiguration configuration)
    {
        super (host, model, oscClient, configuration);
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        if (!this.isConnected ())
            return;
        this.modules.forEach (module -> module.flush (dump));
        this.flush ("/update");
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isHeartbeatMessage (final String address)
    {
        return HEARTBEAT_MESSAGES.contains (address);
    }


    /**
     * Register a command module.
     *
     * @param module The module to register
     */
    public void registerModule (final IModule module)
    {
        this.modules.add (module);
    }
}
