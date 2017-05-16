package de.mossgrabers.launchpad.mode;

import de.mossgrabers.framework.Model;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * The track volume mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumeMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public VolumeMode (final LaunchpadControlSurface surface, final Model model)
    {
        super (surface, model);
    }
}
