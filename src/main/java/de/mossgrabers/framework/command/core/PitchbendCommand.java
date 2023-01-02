// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.core;

/**
 * An pitchbend command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface PitchbendCommand
{
    /**
     * Execute the pitchbend command.
     *
     * @param data1 The first pitchbend byte (low byte)
     * @param data2 The second pitchbend byte (high byte)
     */
    void onPitchbend (final int data1, int data2);


    /**
     * Callback to update the value visually on the controller.
     */
    void updateValue ();
}
