// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.mode;

import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * The track mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public TrackMode (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model);
    }
}
