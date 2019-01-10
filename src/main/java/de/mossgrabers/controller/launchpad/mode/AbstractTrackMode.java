// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.mode;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.SimpleMode;


/**
 * The base class for track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AbstractTrackMode extends SimpleMode<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The surface
     * @param model The model
     */
    public AbstractTrackMode (final String name, final LaunchpadControlSurface surface, final IModel model)
    {
        super (name, surface, model, true);
    }
}
