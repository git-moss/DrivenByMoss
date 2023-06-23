package de.mossgrabers.framework.featuregroup;

import de.mossgrabers.framework.utils.ScrollStates;


/**
 * A view with cursor key states.
 *
 * @author Jürgen Moßgraber
 */
public interface IScrollableView extends IView
{
    /**
     * Update the scroll states.
     *
     * @param scrollStates The scroll states to update
     */
    void updateScrollStates (ScrollStates scrollStates);
}
