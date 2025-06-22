// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.mode;

/**
 * Interface for all Exquis modes to be able to toggle the parameters between 1-4 and 5-8.
 *
 * @author Jürgen Moßgraber
 */
public interface IExquisMode
{
    /**
     * Toggle between using parameters 1-4 and 5-8.
     */
    void toggleParameters ();
}
