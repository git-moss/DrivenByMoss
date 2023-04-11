// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.controller;

import de.mossgrabers.controller.novation.launchpad.definition.ILaunchpadControllerDefinition;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.midi.IMidiOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * Implementation of the Launchpad grid of pads.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadPadGrid extends PadGridImpl
{
    // @formatter:off
    static final int [] TRANSLATE_MATRIX =
    {
        11, 12, 13, 14, 15, 16, 17, 18,
        21, 22, 23, 24, 25, 26, 27, 28,
        31, 32, 33, 34, 35, 36, 37, 38,
        41, 42, 43, 44, 45, 46, 47, 48,
        51, 52, 53, 54, 55, 56, 57, 58,
        61, 62, 63, 64, 65, 66, 67, 68,
        71, 72, 73, 74, 75, 76, 77, 78,
        81, 82, 83, 84, 85, 86, 87, 88
    };
    // @formatter:on

    private static final Map<Integer, Integer> INVERSE_TRANSLATE_MATRIX = new HashMap<> (64);
    static
    {
        for (int i = 0; i < TRANSLATE_MATRIX.length; i++)
            INVERSE_TRANSLATE_MATRIX.put (Integer.valueOf (TRANSLATE_MATRIX[i]), Integer.valueOf (36 + i));
    }

    private final ILaunchpadControllerDefinition definition;
    private final Map<Integer, LightInfo>        padInfos = new TreeMap<> ();


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     * @param definition The Launchpad definition
     */
    public LaunchpadPadGrid (final ColorManager colorManager, final IMidiOutput output, final ILaunchpadControllerDefinition definition)
    {
        super (colorManager, output);

        this.definition = definition;
    }


    /** {@inheritDoc} */
    @Override
    public int translateToGrid (final int note)
    {
        final Integer value = INVERSE_TRANSLATE_MATRIX.get (Integer.valueOf (note));
        return value == null ? -1 : value.intValue ();
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateToController (final int note)
    {
        // Translates note range 36-100 to launchpad grid (11-18, 21-28, ...)
        return new int []
        {
            0,
            TRANSLATE_MATRIX[note - 36]
        };
    }


    /**
     * Flush the changed pad LEDs using system exclusive.
     */
    public void flush ()
    {
        synchronized (this.padInfos)
        {
            if (this.padInfos.isEmpty ())
                return;
            for (final String update: this.definition.buildLEDUpdate (this.padInfos))
                this.output.sendSysex (update);
            this.padInfos.clear ();
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int channel, final int note, final int color)
    {
        synchronized (this.padInfos)
        {
            this.padInfos.computeIfAbsent (Integer.valueOf (note), key -> new LightInfo ()).setColor (color);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void sendBlinkState (final int channel, final int note, final int blinkColor, final boolean fast)
    {
        synchronized (this.padInfos)
        {
            final LightInfo info = this.padInfos.computeIfAbsent (Integer.valueOf (note), key -> new LightInfo ());
            info.setBlinkColor (blinkColor);
            info.setFast (fast);
        }
    }
}