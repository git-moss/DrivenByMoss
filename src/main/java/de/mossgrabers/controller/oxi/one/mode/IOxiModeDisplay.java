// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.mode;

/**
 * Interface for toggling different display options for a mode.
 *
 * @author Jürgen Moßgraber
 */
public interface IOxiModeDisplay
{
    /**
     * Toggles between normal track display and mixer display.
     */
    void toggleDisplay ();
}
