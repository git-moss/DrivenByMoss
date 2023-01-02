// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.command.trigger;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * APC command for cursor arrow keys.
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
        super (direction, model, surface, false);

        this.triggerEvent = ButtonEvent.UP;
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollUp ()
    {
        if (this.surface.getModeManager ().isActive (Modes.BROWSER))
        {
            this.model.getBrowser ().selectPreviousResult ();
            return;
        }
        super.scrollUp ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollDown ()
    {
        if (this.surface.getModeManager ().isActive (Modes.BROWSER))
        {
            this.model.getBrowser ().selectNextResult ();
            return;
        }
        super.scrollDown ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollLeft ()
    {
        if (this.surface.isShiftPressed ())
        {
            this.model.getMarkerBank ().selectPreviousItem ();
            return;
        }

        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode != null)
            activeMode.selectPreviousItemPage ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollRight ()
    {
        if (this.surface.isShiftPressed ())
        {
            this.model.getMarkerBank ().selectNextItem ();
            return;
        }

        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode != null)
            activeMode.selectNextItemPage ();
    }
}
