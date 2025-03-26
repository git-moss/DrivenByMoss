// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol.mode;

import de.mossgrabers.controller.melbourne.rotocontrol.RotoControlConfiguration;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;


/**
 * The volume mode. The knobs control the volumes of the tracks on the current track page. Updates
 * all relevant parameters for the mode on the ROTO CONTROl.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlTrackVolumeMode extends TrackVolumeMode<RotoControlControlSurface, RotoControlConfiguration> implements RotoControlMode
{
    private final RotoControlDisplay rotoDisplay;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public RotoControlTrackVolumeMode (final RotoControlControlSurface surface, final IModel model)
    {
        super (surface, model, true);

        this.rotoDisplay = new RotoControlDisplay (surface, model);

        this.flushDisplay ();
    }


    /** {@inheritDoc} */
    @Override
    public void flushDisplay ()
    {
        this.rotoDisplay.flushTrackDisplay ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.rotoDisplay.updateTrackDisplay ();
    }
}
