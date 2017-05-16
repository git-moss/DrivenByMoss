package de.mossgrabers.launchpad.mode;

import de.mossgrabers.framework.Model;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * The track mute mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MuteMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public MuteMode (final LaunchpadControlSurface surface, final Model model)
    {
        super (surface, model);
    }
}
