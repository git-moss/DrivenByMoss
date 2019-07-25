// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

/**
 * Interface for notifications about mode changes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@FunctionalInterface
public interface ModeChangeListener
{
    /**
     * Called when a mode changes.
     *
     * @param previousModeId The ID of the previous mode
     * @param activeModeId The ID of the newly activated mode
     */
    void call (Modes previousModeId, Modes activeModeId);
}
