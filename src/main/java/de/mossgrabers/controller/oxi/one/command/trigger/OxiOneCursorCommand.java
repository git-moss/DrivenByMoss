// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.command.trigger;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command for the left, right,up, down buttons.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneCursorCommand extends CursorCommand<OxiOneControlSurface, OxiOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public OxiOneCursorCommand (final Direction direction, final IModel model, final OxiOneControlSurface surface)
    {
        super (direction, model, surface, false, true);
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollUp ()
    {
        super.scrollUp ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollDown ()
    {
        super.scrollDown ();
    }


    /** {@inheritDoc} */
    @Override
    protected void updateArrowStates ()
    {
        super.updateArrowStates ();

        final boolean isShiftPressed = this.isShifted ();
        this.scrollStates.setCanScrollUp (this.canSelectSceneItemOrPage (isShiftPressed, false));
        this.scrollStates.setCanScrollDown (this.canSelectSceneItemOrPage (isShiftPressed, true));
    }
}
