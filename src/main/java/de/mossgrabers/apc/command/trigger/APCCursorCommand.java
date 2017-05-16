// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.command.trigger;

import de.mossgrabers.apc.APCConfiguration;
import de.mossgrabers.apc.controller.APCControlSurface;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.trigger.CursorCommand;


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
    public APCCursorCommand (final Direction direction, final Model model, final APCControlSurface surface)
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
        this.scrollTracksLeft ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollRight ()
    {
        this.scrollTracksRight ();
    }
}
