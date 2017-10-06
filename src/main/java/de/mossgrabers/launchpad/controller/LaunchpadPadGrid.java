// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.midi.MidiOutput;

import java.util.HashMap;
import java.util.Map;


/**
 * Implementation of the Launchpad grid of pads.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadPadGrid extends PadGridImpl
{
    static final int []                        TRANSLATE_MATRIX         =
    {
        11,
        12,
        13,
        14,
        15,
        16,
        17,
        18,
        21,
        22,
        23,
        24,
        25,
        26,
        27,
        28,
        31,
        32,
        33,
        34,
        35,
        36,
        37,
        38,
        41,
        42,
        43,
        44,
        45,
        46,
        47,
        48,
        51,
        52,
        53,
        54,
        55,
        56,
        57,
        58,
        61,
        62,
        63,
        64,
        65,
        66,
        67,
        68,
        71,
        72,
        73,
        74,
        75,
        76,
        77,
        78,
        81,
        82,
        83,
        84,
        85,
        86,
        87,
        88
    };

    private static final Map<Integer, Integer> INVERSE_TRANSLATE_MATRIX = new HashMap<> (64);

    static
    {
        for (int i = 0; i < TRANSLATE_MATRIX.length; i++)
            INVERSE_TRANSLATE_MATRIX.put (TRANSLATE_MATRIX[i], 36 + i);
    }

    private final LaunchpadControlSurface surface;


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param surface The launchpad surface
     */
    public LaunchpadPadGrid (final ColorManager colorManager, final LaunchpadControlSurface surface)
    {
        super (colorManager, surface.getOutput ());
        this.surface = surface;
    }


    /** {@inheritDoc} */
    @Override
    protected void sendBlinkState (final int note, final int blinkColor, final boolean fast)
    {
        this.surface.sendLaunchpadSysEx ("23 " + MidiOutput.toHexStr (note) + " " + MidiOutput.toHexStr (blinkColor));
    }


    /** {@inheritDoc} */
    @Override
    public int translateToGrid (final int note)
    {
        final Integer value = INVERSE_TRANSLATE_MATRIX.get (note);
        return value == null ? -1 : value;
    }


    /**
     * Translates note range 36-100 to launchpad grid (11-18, 21-28, ...)
     *
     * @param note The note to translate
     * @return The translated note
     */
    @Override
    public int translateToController (final int note)
    {
        return TRANSLATE_MATRIX[note - 36];
    }
}