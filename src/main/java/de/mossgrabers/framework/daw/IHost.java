// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

/**
 * Interface to the Host.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IHost
{
    /**
     * Get the hosts name.
     *
     * @return The name
     */
    String getName ();


    /**
     * Returns true if the DAW supports a clip based view.
     *
     * @return True if the DAW supports a clip based view
     */
    boolean hasClips ();


    /**
     * Schedules the given task for execution after the given delay.
     *
     * @param task The task to execute
     * @param delay The duration after which the callback function will be called in milliseconds
     */
    void scheduleTask (Runnable task, long delay);


    /**
     * Print the error to the console.
     *
     * @param text The description text
     */
    void error (String text);


    /**
     * Print the exception to the console.
     *
     * @param text The description text
     * @param ex The exception
     */
    void error (String text, Exception ex);


    /**
     * Print a text to the console.
     *
     * @param text The text to print
     */
    void println (String text);


    /**
     * Display a notification in the DAW.
     *
     * @param message The message to display
     */
    void showNotification (String message);


    /**
     * Send a datagram package to the given server. TODO: Remove when USB API is available
     *
     * @param hostAddress The IP address of the server
     * @param port The port
     * @param data The data to send
     */
    void sendDatagramPacket (String hostAddress, int port, byte [] data);
}