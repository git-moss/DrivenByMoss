// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

import de.mossgrabers.framework.daw.midi.IMidiInput;


/**
 * Interface for a proxy to a piano keyboard on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IHwPianoKeyboard extends IHwControl
{
    /**
     * Bind the piano keyboard to an MIDI input port.
     *
     * @param input
     */
    void bind (IMidiInput input);
}
