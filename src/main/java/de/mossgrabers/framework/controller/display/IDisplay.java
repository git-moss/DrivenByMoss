// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.display;

/**
 * Interface to a display.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IDisplay
{
    /**
     * Displays a notification message on the display and in the DAW.
     *
     * @param message The message to display
     */
    void notify (final String message);


    /**
     * Cancels the display of a notification message.
     */
    void cancelNotification ();


    /**
     * Check if a notification is active.
     *
     * @return True if a notification is active
     */
    boolean isNotificationActive ();


    /**
     * If there is any cleanup necessary.
     */
    void shutdown ();
}
