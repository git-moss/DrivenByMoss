package de.mossgrabers.controller.osc;

import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;


/**
 * A dummy surface implementation for OSC.
 *
 * @author Jürgen Moßgraber
 */
public class OSCControlSurface extends AbstractControlSurface<OSCConfiguration>
{
    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param input The MIDI input
     */
    public OSCControlSurface (final IHost host, final OSCConfiguration configuration, final ColorManager colorManager, final IMidiInput input)
    {
        super (host, configuration, colorManager, null, input, null, 100, 100);
    }
}
