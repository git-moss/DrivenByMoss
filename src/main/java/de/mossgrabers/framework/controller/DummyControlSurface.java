// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;


/**
 * Dummy control surface for implementations, which only implement a protocol.
 *
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class DummyControlSurface<C extends Configuration> extends AbstractControlSurface<C>
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     */
    public DummyControlSurface (final IHost host, final ColorManager colorManager, final C configuration)
    {
        super (host, configuration, colorManager, null, null, null, 10, 10);
    }
}