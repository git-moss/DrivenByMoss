// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.mode;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Mode;
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


    protected Direction direction;
    protected boolean   canScrollLeft;
    protected boolean   canScrollRight;
    protected boolean   canScrollUp;
    protected boolean   canScrollDown;


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public ModeCursorCommand (final Direction direction, final IModel model, final S surface)
    {
        super (model, surface);
        this.direction = direction;
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

        this.model.getHost ().scheduleTask ( () -> {
            final Mode activeMode = this.surface.getModeManager ().getActiveOrTempMode ();
            if (activeMode != null)
                this.surface.getDisplay ().notify (activeMode.getSelectedItemName ());
        }, 200);
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
        final Mode mode = this.surface.getModeManager ().getActiveOrTempMode ();
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
        final Mode activeMode = this.surface.getModeManager ().getActiveOrTempMode ();
        if (activeMode != null)
            activeMode.selectPreviousItem ();
    }


    /**
     * Scroll right. Tracks, devices or parameter bank items.
     */
    protected void scrollRight ()
    {
        final Mode activeMode = this.surface.getModeManager ().getActiveOrTempMode ();
        if (activeMode != null)
            activeMode.selectNextItem ();
    }


    /**
     * Scroll up. Tracks, devices or parameter bank pages.
     */
    protected void scrollUp ()
    {
        final Mode activeMode = this.surface.getModeManager ().getActiveOrTempMode ();
        if (activeMode != null)
            activeMode.selectNextItemPage ();
    }


    /**
     * Scroll down. Tracks, devices or parameter bank pages.
     */
    protected void scrollDown ()
    {
        final Mode activeMode = this.surface.getModeManager ().getActiveOrTempMode ();
        if (activeMode != null)
            activeMode.selectPreviousItemPage ();
    }
}
