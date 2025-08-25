// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii;

/**
 * Callback for values updates received via SysEx.
 *
 * @author Jürgen Moßgraber
 */
public interface NIHIASysExCallback
{
    /**
     * Callback for setting the tempo via SysEx.
     *
     * @param tempo THe tempo
     */
    void setTempo (double tempo);


    /**
     * Send the info about the DAW to the Kontrol.
     */
    void sendDAWInfo ();


    /**
     * Select a device on the current track.
     *
     * @param deviceIndex The index of the device to select
     */
    void selectDevice (int deviceIndex);
}
