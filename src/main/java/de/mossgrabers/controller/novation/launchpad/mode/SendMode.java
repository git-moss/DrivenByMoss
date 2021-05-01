// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.mode;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.track.DefaultTrackMode;


/**
 * The track send mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendMode extends DefaultTrackMode<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SendMode (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Send", surface, model, true);
    }
}
