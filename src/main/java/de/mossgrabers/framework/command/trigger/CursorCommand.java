// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for cursor arrow keys.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class CursorCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
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
    public CursorCommand (final Direction direction, final IModel model, final S surface)
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
        this.surface.updateButton (this.surface.getLeftButtonId (), this.canScrollLeft ? buttonOnColor : buttonOffColor);
        this.surface.updateButton (this.surface.getRightButtonId (), this.canScrollRight ? buttonOnColor : buttonOffColor);
        this.surface.updateButton (this.surface.getUpButtonId (), this.canScrollUp ? buttonOnColor : buttonOffColor);
        this.surface.updateButton (this.surface.getDownButtonId (), this.canScrollDown ? buttonOnColor : buttonOffColor);
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
        // Intentionally empty
    }


    /**
     * Scroll left. Tracks, devices or parameter banks.
     */
    protected abstract void scrollLeft ();


    /**
     * Scroll right. Tracks, devices or parameter banks.
     */
    protected abstract void scrollRight ();


    /**
     * Scroll scenes up.
     */
    protected void scrollUp ()
    {
        final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();
        if (this.surface.isShiftPressed ())
            sceneBank.scrollPageBackwards ();
        else
            sceneBank.scrollBackwards ();
    }


    /**
     * Scroll scenes down.
     */
    protected void scrollDown ()
    {
        final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();
        if (this.surface.isShiftPressed ())
            sceneBank.scrollPageForwards ();
        else
            sceneBank.scrollForwards ();
    }
}
