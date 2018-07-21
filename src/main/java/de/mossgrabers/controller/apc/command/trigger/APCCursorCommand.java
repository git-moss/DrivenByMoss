// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.command.trigger;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.trigger.CursorCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Mode;


/**
 * Command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCCursorCommand extends CursorCommand<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public APCCursorCommand (final Direction direction, final IModel model, final APCControlSurface surface)
    {
        super (direction, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateArrowStates ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollLeft ()
    {
        final Mode activeMode = this.surface.getModeManager ().getActiveOrTempMode ();
        if (activeMode != null)
            activeMode.selectPreviousTrack ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollRight ()
    {
        final Mode activeMode = this.surface.getModeManager ().getActiveOrTempMode ();
        if (activeMode != null)
            activeMode.selectNextTrack ();

    }
}
