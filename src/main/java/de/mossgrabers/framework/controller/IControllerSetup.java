// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

/**
 * Interface to setting up a controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IControllerSetup
{
    /**
     * Initialise all required functionality for the controller.
     */
    void init ();


    /**
     * Execute necessary shutdown functions.
     */
    void exit ();


    /**
     * Update cycle. Use e.g. for display updates.
     */
    void flush ();
}
