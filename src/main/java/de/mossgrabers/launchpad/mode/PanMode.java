package de.mossgrabers.launchpad.mode;

import de.mossgrabers.framework.Model;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * The track panorama mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PanMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PanMode (final LaunchpadControlSurface surface, final Model model)
    {
        super (surface, model);
    }
}
