// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

/**
 * Interface for notifications about view changes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@FunctionalInterface
public interface ViewChangeListener
{
    /**
     * Called when a view changes.
     *
     * @param previousViewId The ID of the previous view
     * @param activeViewId The ID of the newly activated view
     */
    void call (Views previousViewId, Views activeViewId);
}
