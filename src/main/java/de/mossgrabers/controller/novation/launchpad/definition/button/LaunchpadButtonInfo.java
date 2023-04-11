// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.definition.button;

/**
 * Info about a Launchpad button.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadButtonInfo
{
    /** A virtual button info. */
    public static final LaunchpadButtonInfo VIRTUAL = new LaunchpadButtonInfo (-1, true);

    private final int                       midiControl;
    private final boolean                   isShifted;


    /**
     * Constructor.
     *
     * @param midiControl The MIDI control number
     * @param isShifted True if the function requires to be combined with a pressed Shift key
     */
    public LaunchpadButtonInfo (final int midiControl, final boolean isShifted)
    {
        this.midiControl = midiControl;
        this.isShifted = isShifted;
    }


    /**
     * Get the MIDI control number.
     *
     * @return The MIDI control number
     */
    public int getControl ()
    {
        return this.midiControl;
    }


    /**
     * Get if it is not a physical button and needs to be emulated in the Shift mode.
     *
     * @return True if it is not a physical button and needs to be emulated in the Shift mode
     */
    public boolean isVirtual ()
    {
        return this.midiControl < 0;
    }


    /**
     * Get if it the function requires to be combined with a pressed Shift key.
     *
     * @return True if the function requires to be combined with a pressed Shift key
     */
    public boolean isShifted ()
    {
        return this.isShifted;
    }
}
