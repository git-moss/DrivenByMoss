// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.grid;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * Implementation of a light guide.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LightGuideImpl extends PadGridImpl
{
    /**
     * Constructor.
     *
     * @param startNote The first note
     * @param keys The number of keys
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     */
    public LightGuideImpl (final int startNote, final int keys, final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output, 1, keys, startNote);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendBlinkState (final int channel, final int note, final int blinkColor, final boolean fast)
    {
        // No blinky blink
    }
}