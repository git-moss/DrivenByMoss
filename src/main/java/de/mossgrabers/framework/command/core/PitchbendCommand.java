// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.core;

/**
 * An pitch-bend command.
 *
 * @author Jürgen Moßgraber
 */
@FunctionalInterface
public interface PitchbendCommand
{
    /**
     * Execute the pitch-bend command.
     *
     * @param data1 The first pitch-bend byte (low byte)
     * @param data2 The second pitch-bend byte (high byte)
     */
    void onPitchbend (final int data1, int data2);
}
