// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.mode;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
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
    public void execute (final ButtonEvent event)
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
    }


    /**
     * Update the arrow buttons.
     */
    public void updateArrows ()
    {
        this.updateArrowStates ();
        this.surface.scheduleTask (this::delayedUpdateArrows, 150);
    }


    protected void delayedUpdateArrows ()
    {
        final int buttonOnColor = this.getButtonOnColor ();
        final int buttonOffColor = this.getButtonOffColor ();

        final int leftButtonId = this.surface.getLeftTriggerId ();
        if (leftButtonId >= 0)
            this.surface.updateTrigger (leftButtonId, this.canScrollLeft ? buttonOnColor : buttonOffColor);

        final int rightButtonId = this.surface.getRightTriggerId ();
        if (rightButtonId >= 0)
            this.surface.updateTrigger (rightButtonId, this.canScrollRight ? buttonOnColor : buttonOffColor);

        final int upButtonId = this.surface.getUpTriggerId ();
        if (upButtonId >= 0)
            this.surface.updateTrigger (upButtonId, this.canScrollUp ? buttonOnColor : buttonOffColor);

        final int downButtonId = this.surface.getDownTriggerId ();
        if (downButtonId >= 0)
            this.surface.updateTrigger (downButtonId, this.canScrollDown ? buttonOnColor : buttonOffColor);
    }


    /**
     * Get the color of when the button should be off.
     *
     * @return The color ID
     */
    protected int getButtonOffColor ()
    {
        return this.model.getColorManager ().getColor (ColorManager.BUTTON_STATE_OFF);
    }


    /**
     * Get the color of when the button should be on.
     *
     * @return The color ID
     */
    protected int getButtonOnColor ()
    {
        return this.model.getColorManager ().getColor (ColorManager.BUTTON_STATE_ON);
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
