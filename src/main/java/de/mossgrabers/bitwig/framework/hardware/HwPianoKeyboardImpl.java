// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.bitwig.framework.midi.MidiInputImpl;
import de.mossgrabers.framework.controller.hardware.IHwPianoKeyboard;
import de.mossgrabers.framework.daw.midi.IMidiInput;

import com.bitwig.extension.controller.api.PianoKeyboard;


/**
 * Implementation of a proxy to a fader on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwPianoKeyboardImpl implements IHwPianoKeyboard
{
    private final PianoKeyboard hardwarePianoKeyboard;


    /**
     * Constructor.
     *
     * @param hardwarePianoKeyboard The Bitwig hardware piano keyboard
     */
    public HwPianoKeyboardImpl (final PianoKeyboard hardwarePianoKeyboard)
    {
        this.hardwarePianoKeyboard = hardwarePianoKeyboard;
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.hardwarePianoKeyboard.setBounds (x, y, width, height);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input)
    {
        this.hardwarePianoKeyboard.setMidiIn (((MidiInputImpl) input).getPort ());
    }


    /** {@inheritDoc} */
    @Override
    public void update ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public String getLabel ()
    {
        return "Keyboard";
    }
}
