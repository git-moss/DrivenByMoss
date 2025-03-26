// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol.mode;

import de.mossgrabers.controller.melbourne.rotocontrol.RotoControlConfiguration;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.track.TrackMode;


/**
 * The selected track mode. The knobs control the volume, the panning and 6 sends of the selected
 * track. Updates all relevant parameters for the mode on the ROTO CONTROl.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlSelectedTrackMode extends TrackMode<RotoControlControlSurface, RotoControlConfiguration> implements RotoControlMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public RotoControlSelectedTrackMode (final RotoControlControlSurface surface, final IModel model)
    {
        super (surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void flushDisplay ()
    {
        // This is fixed
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        // This is fixed
    }
}
