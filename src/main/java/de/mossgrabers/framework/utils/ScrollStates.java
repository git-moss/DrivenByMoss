// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

import de.mossgrabers.framework.command.trigger.Direction;


/**
 * Helper class to keep track of scroll states for arrow keys.
 *
 * @author Jürgen Moßgraber
 */
public class ScrollStates
{
    private boolean canScrollLeft;
    private boolean canScrollRight;
    private boolean canScrollUp;
    private boolean canScrollDown;


    /**
     * Can it go left?
     *
     * @return True if left is active
     */
    public boolean canScrollLeft ()
    {
        return this.canScrollLeft;
    }


    /**
     * Set left state.
     *
     * @param canScrollLeft True to enable going left
     */
    public void setCanScrollLeft (final boolean canScrollLeft)
    {
        this.canScrollLeft = canScrollLeft;
    }


    /**
     * Can it go right?
     *
     * @return True if right is active
     */
    public boolean canScrollRight ()
    {
        return this.canScrollRight;
    }


    /**
     * Set right state.
     *
     * @param canScrollRight True to enable going right
     */
    public void setCanScrollRight (final boolean canScrollRight)
    {
        this.canScrollRight = canScrollRight;
    }


    /**
     * Can it go up?
     *
     * @return True if up is active
     */
    public boolean canScrollUp ()
    {
        return this.canScrollUp;
    }


    /**
     * Set up state.
     *
     * @param canScrollUp True to enable going up
     */
    public void setCanScrollUp (final boolean canScrollUp)
    {
        this.canScrollUp = canScrollUp;
    }


    /**
     * Can it go down?
     *
     * @return True if down is active
     */
    public boolean canScrollDown ()
    {
        return this.canScrollDown;
    }


    /**
     * Set down state.
     *
     * @param canScrollDown True to enable going down
     */
    public void setCanScrollDown (final boolean canScrollDown)
    {
        this.canScrollDown = canScrollDown;
    }


    /**
     * Check the state for a direction.
     *
     * @param direction The direction
     * @return True if active
     */
    public boolean canScroll (final Direction direction)
    {
        switch (direction)
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
     * Set all states to the given state.
     *
     * @param enable True to activate all states
     */
    public void setAll (final boolean enable)
    {
        this.canScrollLeft = enable;
        this.canScrollRight = enable;
        this.canScrollUp = enable;
        this.canScrollDown = enable;
    }
}
