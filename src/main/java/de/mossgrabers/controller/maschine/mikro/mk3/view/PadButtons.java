// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.view;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Interface for addinh button presses to a view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface PadButtons
{
    /**
     * Implement to execute a function on the press of one of the four buttons above the grid.
     *
     * @param index The index of the button (0-3)
     * @param event The button event
     */
    void onButton (final int index, final ButtonEvent event);
}
