// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.mode;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.ScrollStates;

import java.util.function.BooleanSupplier;


/**
 * Command for navigating mode pages and items.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class ModeCursorCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    protected Direction             direction;
    protected final ScrollStates    scrollStates = new ScrollStates ();
    protected final boolean         notifySelection;
    protected final BooleanSupplier alternateMode;
    protected ButtonEvent           triggerEvent = ButtonEvent.DOWN;


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
        this (direction, model, surface, notifySelection, null);
    }


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     * @param notifySelection Set to true to show a notification message if an item is selected
     * @param alternateMode Default is checking the Shift key but this allows to trigger with
     *            something else
     */
    public ModeCursorCommand (final Direction direction, final IModel model, final S surface, final boolean notifySelection, final BooleanSupplier alternateMode)
    {
        super (model, surface);

        this.direction = direction;
        this.notifySelection = notifySelection;
        this.alternateMode = alternateMode == null ? () -> false : alternateMode;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != this.getTriggerEvent ())
            return;

        switch (this.direction)
        {
            case LEFT:
                if (this.alternateMode.getAsBoolean ())
                    this.scrollDown ();
                else
                    this.scrollLeft ();
                break;
            case RIGHT:
                if (this.alternateMode.getAsBoolean ())
                    this.scrollUp ();
                else
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
        return this.scrollStates.canScroll (this.direction);
    }


    /**
     * Update the states of the arrow buttons. Override to update arrow states.
     */
    protected void updateArrowStates ()
    {
        final IMode mode = this.surface.getModeManager ().getActive ();
        this.scrollStates.setCanScrollLeft (mode != null && mode.hasPreviousItem ());
        this.scrollStates.setCanScrollRight (mode != null && mode.hasNextItem ());
        this.scrollStates.setCanScrollUp (mode != null && mode.hasNextItemPage ());
        this.scrollStates.setCanScrollDown (mode != null && mode.hasPreviousItemPage ());
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


    /**
     * Use a method to make it over-writable.
     *
     * @return The button event to trigger execute
     */
    protected ButtonEvent getTriggerEvent ()
    {
        return this.triggerEvent;
    }
}
