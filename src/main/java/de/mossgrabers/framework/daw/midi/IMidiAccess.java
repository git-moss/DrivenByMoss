// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * Interface to access MIDI input and outputs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IMidiAccess
{
    /**
     * Create a MIDI output.
     *
     * @return The output
     */
    IMidiOutput createOutput ();


    /**
     * Create a MIDI output.
     *
     * @param index The index of the MIDI output
     * @return The output
     */
    IMidiOutput createOutput (int index);


    /**
     * Create a MIDI input. Uses the first input.
     *
     * @param name The name to use for the input
     * @param filters The filters to register for
     * @return The created input
     */
    IMidiInput createInput (String name, String... filters);


    /**
     * Create a MIDI input.
     *
     * @param index The index of the MIDI input
     * @param name The name to use for the input
     * @param filters The filters to register for
     * @return The created input
     */
    IMidiInput createInput (int index, String name, String... filters);
}
