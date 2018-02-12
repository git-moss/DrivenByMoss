// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * Interface to access MIDI input and outputs.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IMidiDevice
{
    /**
     * Create a midi input. Uses the first input.
     *
     * @param name The name to use for the input
     * @param filters The filters to regiser for
     * @return The created input
     */
    IMidiInput createInput (String name, String... filters);


    /**
     * Create a midi input.
     * 
     * @param index The index of the midi input
     * @param name The name to use for the input
     * @param filters The filters to regiser for
     * @return The created input
     */
    IMidiInput createInput (int index, String name, String... filters);
}
