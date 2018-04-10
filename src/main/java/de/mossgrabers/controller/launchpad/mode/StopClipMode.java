// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.mode;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;


/**
 * The track stop clip mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StopClipMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public StopClipMode (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model);
    }
}
