// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.controller.esi.xjam;

import de.mossgrabers.bitwig.framework.BitwigSetupFactory;
import de.mossgrabers.bitwig.framework.configuration.SettingsUIImpl;
import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.extension.AbstractControllerExtensionDefinition;
import de.mossgrabers.controller.esi.xjam.XjamConfiguration;
import de.mossgrabers.controller.esi.xjam.XjamControllerDefinition;
import de.mossgrabers.controller.esi.xjam.XjamControllerSetup;
import de.mossgrabers.controller.esi.xjam.controller.XjamControlSurface;
import de.mossgrabers.framework.controller.IControllerSetup;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Definition class for ESI Xjam controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XjamControllerExtensionDefinition extends AbstractControllerExtensionDefinition<XjamControlSurface, XjamConfiguration>
{
    /**
     * Constructor.
     */
    public XjamControllerExtensionDefinition ()
    {
        super (new XjamControllerDefinition ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean isUsingBetaAPI ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    protected IControllerSetup<XjamControlSurface, XjamConfiguration> getControllerSetup (final ControllerHost host)
    {
        return new XjamControllerSetup (new HostImpl (host), new BitwigSetupFactory (host), new SettingsUIImpl (host, host.getPreferences ()), new SettingsUIImpl (host, host.getDocumentState ()));
    }
}
