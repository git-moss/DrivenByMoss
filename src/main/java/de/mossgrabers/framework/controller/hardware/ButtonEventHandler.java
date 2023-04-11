// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Callback interface for button events.
 *
 * @author Jürgen Moßgraber
 */
@FunctionalInterface
public interface ButtonEventHandler
{
    /**
     * Handle the event.
     *
     * @param event The button event
     */
    void handle (ButtonEvent event);
}
