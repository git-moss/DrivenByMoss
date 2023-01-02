// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.osc;

/**
 * The configuration interface for OSC settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IOpenSoundControlConfiguration
{
    /**
     * Get if logging of input commands should be enabled.
     *
     * @return True if logging should be enabled
     */
    boolean shouldLogInputCommands ();


    /**
     * Get if logging of output commands should be enabled.
     *
     * @return True if logging should be enabled
     */
    boolean shouldLogOutputCommands ();


    /**
     * Return true to filter heartbeat messages (e.g. ping) from message logging.
     *
     * @return True to enable filtering
     */
    boolean filterHeartbeatMessages ();
}
