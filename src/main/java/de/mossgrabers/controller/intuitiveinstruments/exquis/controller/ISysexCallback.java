// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.controller;

/**
 * Callback for commands received via system exclusive messages.
 *
 * @author Jürgen Moßgraber
 */
public interface ISysexCallback
{
    /**
     * Update the tempo.
     *
     * @param tempo The tempo
     */
    void updateTempo (final int tempo);
}
