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
    void updateTempo (int tempo);


    /**
     * Store the settings of the current track.
     *
     * @param trackPosition The position of the track for which the data was received, -1 if not was
     *            initiated from the DAW side
     * @param settings The settings to store
     */
    void storeTrackSettings (int trackPosition, byte [] settings);
}
