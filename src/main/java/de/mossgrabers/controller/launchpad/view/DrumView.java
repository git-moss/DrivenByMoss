// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.daw.IModel;


/**
 * The drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends DrumViewBase
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 4, 4);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.surface.setLaunchpadToPrgMode ();

        super.onActivate ();
    }
}