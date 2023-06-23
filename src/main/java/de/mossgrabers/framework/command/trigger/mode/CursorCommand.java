// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.mode;

import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.IMode;


/**
 * Command for cursor arrow keys.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class CursorCommand<S extends IControlSurface<C>, C extends Configuration> extends ModeCursorCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public CursorCommand (final Direction direction, final IModel model, final S surface)
    {
        super (direction, model, surface);
    }


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     * @param notifySelection Set to true to show a notification message if an item is selected
     */
    public CursorCommand (final Direction direction, final IModel model, final S surface, final boolean notifySelection)
    {
        super (direction, model, surface, notifySelection);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateArrowStates ()
    {
        final ISceneBank sceneBank = this.getSceneBank ();
        final IMode mode = this.surface.getModeManager ().getActive ();
        final boolean shiftPressed = this.surface.isShiftPressed ();
        this.scrollStates.setCanScrollUp (sceneBank.canScrollBackwards ());
        this.scrollStates.setCanScrollDown (sceneBank.canScrollForwards ());
        this.scrollStates.setCanScrollLeft (mode != null && (shiftPressed ? mode.hasPreviousItemPage () : mode.hasPreviousItem ()));
        this.scrollStates.setCanScrollRight (mode != null && (shiftPressed ? mode.hasNextItemPage () : mode.hasNextItem ()));
    }


    /**
     * Scroll scenes up.
     */
    @Override
    protected void scrollUp ()
    {
        final ISceneBank sceneBank = this.getSceneBank ();
        if (this.surface.isShiftPressed ())
            sceneBank.selectPreviousPage ();
        else
            sceneBank.scrollBackwards ();
    }


    /**
     * Scroll scenes down.
     */
    @Override
    protected void scrollDown ()
    {
        final ISceneBank sceneBank = this.getSceneBank ();
        if (this.surface.isShiftPressed ())
            sceneBank.selectNextPage ();
        else
            sceneBank.scrollForwards ();
    }


    /**
     * Get the scene bank to use for up/down.
     *
     * @return The scene bank
     */
    protected ISceneBank getSceneBank ()
    {
        return this.model.getCurrentTrackBank ().getSceneBank ();
    }
}
