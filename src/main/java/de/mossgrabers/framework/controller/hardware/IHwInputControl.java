// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.hardware;

import de.mossgrabers.framework.daw.midi.IMidiInput;


/**
 * A input control on a controller surface.
 *
 * @author Jürgen Moßgraber
 */
public interface IHwInputControl extends IHwControl
{
    /**
     * Bind a MIDI command coming from a MIDI input to the input control.
     *
     * @param input The MIDI input
     * @param type How to bind
     * @param control The MIDI CC or note to bind
     */
    void bind (IMidiInput input, BindType type, int control);


    /**
     * Bind a MIDI command coming from a MIDI input to the input control.
     *
     * @param input The MIDI input
     * @param channel The MIDI channel
     * @param type How to bind
     * @param control The MIDI CC or note to bind
     */
    void bind (IMidiInput input, BindType type, int channel, int control);


    /**
     * Unbind the input control from the previously bound MIDI command.
     */
    void unbind ();


    /**
     * Bind the input control again to the previously unbound MIDI command.
     */
    void rebind ();


    /**
     * Test if the control is bound to a MIDI input.
     *
     * @return True if bound
     */
    boolean isBound ();
}
