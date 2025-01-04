// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.mode;

import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;


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
     * @param notifySelection Set to true to show a notification message if an item is selected
     */
    public CursorCommand (final Direction direction, final IModel model, final S surface, final boolean notifySelection)
    {
        this (direction, model, surface, notifySelection, false);
    }


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     * @param notifySelection Set to true to show a notification message if an item is selected
     * @param isFlipped If true selecting items or pages is flipped
     */
    public CursorCommand (final Direction direction, final IModel model, final S surface, final boolean notifySelection, final boolean isFlipped)
    {
        super (direction, model, surface, notifySelection, isFlipped);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateArrowStates ()
    {
        final boolean isShiftPressed = this.isShifted ();
        this.scrollStates.setCanScrollUp (this.canSelectSceneItemOrPage (isShiftPressed, false));
        this.scrollStates.setCanScrollDown (this.canSelectSceneItemOrPage (isShiftPressed, true));
        this.scrollStates.setCanScrollLeft (this.canSelectItemOrPage (isShiftPressed, false));
        this.scrollStates.setCanScrollRight (this.canSelectItemOrPage (isShiftPressed, true));
    }


    /**
     * Scroll scenes up.
     */
    @Override
    protected void scrollUp ()
    {
        this.selectSceneItemOrPage (this.isShifted (), false);
    }


    /**
     * Scroll scenes down.
     */
    @Override
    protected void scrollDown ()
    {
        this.selectSceneItemOrPage (this.isShifted (), true);
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollLeft ()
    {
        this.selectItemOrPage (this.isShifted (), false);
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollRight ()
    {
        this.selectItemOrPage (this.isShifted (), true);
    }


    /**
     * Select the previous/next item or item page.
     *
     * @param isPage If true the previous/next page is selected otherwise the item
     * @param isNext If true the next page/item is selected otherwise the previous
     */
    protected void selectSceneItemOrPage (final boolean isPage, final boolean isNext)
    {
        final ISceneBank sceneBank = this.getSceneBank ();
        if (sceneBank == null)
            return;

        if (isPage ^ this.isFlipped)
        {
            if (isNext)
                sceneBank.selectNextPage ();
            else
                sceneBank.selectPreviousPage ();
            return;
        }

        if (isNext)
            sceneBank.scrollForwards ();
        else
            sceneBank.scrollBackwards ();
    }


    /**
     * Get the scroll state for the previous/next scene or scene page.
     *
     * @param isPage If true the state of the previous/next page is returned otherwise the scene
     * @param isNext If true the state of the next page/scene is returned otherwise the previous
     * @return The state
     */
    protected boolean canSelectSceneItemOrPage (final boolean isPage, final boolean isNext)
    {
        final ISceneBank sceneBank = this.getSceneBank ();
        if (sceneBank == null)
            return false;

        if (isPage ^ this.isFlipped)
            return isNext ? sceneBank.canScrollPageForwards () : sceneBank.canScrollPageBackwards ();

        return isNext ? sceneBank.canScrollForwards () : sceneBank.canScrollBackwards ();
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


    /**
     * Check if the shifted commands should be used.
     *
     * @return True if shifted
     */
    protected boolean isShifted ()
    {
        return this.surface.isShiftPressed ();
    }
}
