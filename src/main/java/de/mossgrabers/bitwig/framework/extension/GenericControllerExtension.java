// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.extension;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

import de.mossgrabers.framework.controller.IControllerSetup;


/**
 * A generic controller extension implementation, which delegates to a setup instance.
 *
 * @author Jürgen Moßgraber
 */
public class GenericControllerExtension extends ControllerExtension
{
    private final IControllerSetup<?, ?> setup;


    /**
     * Constructor.
     *
     * @param setup A setup
     * @param definition A definition
     * @param host The DAW host
     */
    public GenericControllerExtension (final IControllerSetup<?, ?> setup, final ControllerExtensionDefinition definition, final ControllerHost host)
    {
        super (definition, host);
        this.setup = setup;
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        this.setup.init ();
        final ControllerHost host = this.getHost ();
        host.scheduleTask (() -> {
            try
            {
                this.setup.startup ();
                host.println ("Running.");
            }
            catch (final RuntimeException ex)
            {
                host.println (ex.getMessage ());
            }
        }, 1000);
    }


    /** {@inheritDoc} */
    @Override
    public void exit ()
    {
        this.setup.exit ();
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.setup.flush ();
    }
}
