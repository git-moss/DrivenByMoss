// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.osc;

/**
 * Interface for a callback to handle OSC messages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IOpenSoundControlCallback
{
    /**
     * Handle an OSC message.
     *
     * @param message The message to handle
     */
    void handle (IOpenSoundControlMessage message);
}
