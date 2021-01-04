// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.mode;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for navigating mode pages and items.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModeCursorCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /** The direction of the cursor. */
    public enum Direction
    {
        /** Move left. */
        LEFT,
        /** Move right. */
        RIGHT,
        /** Move up. */
        UP,
        /** Move down. */
        DOWN
    }


    protected Direction     direction;
    protected boolean       canScrollLeft;
    protected boolean       canScrollRight;
    protected boolean       canScrollUp;
    protected boolean       canScrollDown;
    protected final boolean notifySelection;


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public ModeCursorCommand (final Direction direction, final IModel model, final S surface)
    {
        this (direction, model, surface, true);
    }


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     * @param notifySelection Set to true to show a notification message if an item is selected
     */
    public ModeCursorCommand (final Direction direction, final IModel model, final S surface, final boolean notifySelection)
    {
        super (model, surface);

        this.direction = direction;
        this.notifySelection = notifySelection;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (this.direction)
        {
            case LEFT:
                this.scrollLeft ();
                break;
            case RIGHT:
                this.scrollRight ();
                break;
            case UP:
                this.scrollUp ();
                break;
            case DOWN:
                this.scrollDown ();
                break;
        }

        if (this.notifySelection)
        {
            final IMode activeMode = this.surface.getModeManager ().getActive ();
            if (activeMode != null)
                this.mvHelper.notifySelectedItem (activeMode);
        }
    }


    /**
     * Test if the cursor can be scrolled in the set direction.
     *
     * @return True if it can be scrolled
     */
    public boolean canScroll ()
    {
        this.updateArrowStates ();

        switch (this.direction)
        {
            case LEFT:
                return this.canScrollLeft;
            case RIGHT:
                return this.canScrollRight;
            case UP:
                return this.canScrollUp;
            case DOWN:
                return this.canScrollDown;
            default:
                return false;
        }
    }


    /**
     * Update the states of the arrow buttons. Override to update arrow states.
     */
    protected void updateArrowStates ()
    {
        final IMode mode = this.surface.getModeManager ().getActive ();
        this.canScrollLeft = mode != null && mode.hasPreviousItem ();
        this.canScrollRight = mode != null && mode.hasNextItem ();
        this.canScrollUp = mode != null && mode.hasNextItemPage ();
        this.canScrollDown = mode != null && mode.hasPreviousItemPage ();
    }


    /**
     * Scroll left. Tracks, devices or parameter bank items.
     */
    protected void scrollLeft ()
    {
        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode != null)
            activeMode.selectPreviousItem ();
    }


    /**
     * Scroll right. Tracks, devices or parameter bank items.
     */
    protected void scrollRight ()
    {
        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode != null)
            activeMode.selectNextItem ();
    }


    /**
     * Scroll up. Tracks, devices or parameter bank pages.
     */
    protected void scrollUp ()
    {
        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode != null)
            activeMode.selectNextItemPage ();
    }


    /**
     * Scroll down. Tracks, devices or parameter bank pages.
     */
    protected void scrollDown ()
    {
        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode != null)
            activeMode.selectPreviousItemPage ();
    }
}
