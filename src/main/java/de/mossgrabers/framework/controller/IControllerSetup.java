// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.daw.IModel;


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
     * Startup the controller.
     */
    void startup ();


    /**
     * Execute necessary shutdown functions.
     */
    void exit ();


    /**
     * Update cycle. Use e.g. for display updates.
     */
    void flush ();


    /**
     * Get the DAW model.
     *
     * @return The model
     */
    IModel getModel ();


    /**
     * Get the configuratgion.
     *
     * @return The configuration
     */
    Configuration getConfiguration ();
}
