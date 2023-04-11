// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.BlinkingPadGrid;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Implementation of the Fire grid of pads.
 *
 * @author Jürgen Moßgraber
 */
public class FirePadGrid extends BlinkingPadGrid
{
    // @formatter:off
    static final int [] TRANSLATE_16x4_MATRIX =
    {
        102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117,
         86,  87,  88,  89,  90,  91,  92,  93,  94,  95,  96,  97,  98,  99, 100, 101,
         70,  71,  72,  73,  74,  75,  76,  77,  78,  79,  80,  81,  82,  83,  84,  85,
         54,  55,  56,  57,  58,  59,  60,  61,  62,  63,  64,  65,  66,  67,  68,  69
    };
    // @formatter:on

    private static final Map<Integer, Integer> INVERSE_TRANSLATE_16x4_MATRIX = new HashMap<> (64);
    static
    {
        for (int i = 0; i < TRANSLATE_16x4_MATRIX.length; i++)
            INVERSE_TRANSLATE_16x4_MATRIX.put (Integer.valueOf (TRANSLATE_16x4_MATRIX[i]), Integer.valueOf (36 + i));
    }

    private double padBrightness = 1.0;
    private double padSaturation = 1.0;


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     */
    public FirePadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output, 4, 16, 36);
    }


    /** {@inheritDoc} */
    @Override
    public int translateToGrid (final int note)
    {
        final Integer value = INVERSE_TRANSLATE_16x4_MATRIX.get (Integer.valueOf (note));
        return value == null ? -1 : value.intValue ();
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateToController (final int note)
    {
        return new int []
        {
            0,
            TRANSLATE_16x4_MATRIX[note - 36]
        };
    }


    /** {@inheritDoc} */
    @Override
    protected void updateController ()
    {
        final StringBuilder sb = new StringBuilder ();

        for (final Entry<Integer, LightInfo> e: this.padInfos.entrySet ())
        {
            final int note = e.getKey ().intValue ();
            final LightInfo info = e.getValue ();

            final int index = note - 54;
            // Note: The exact PADx is not needed for getting the color
            ColorEx color = this.colorManager.getColor (info.getColor (), ButtonID.PAD1);
            // Do not scale black!
            if (!color.equals (ColorEx.BLACK))
                color = color.scale (this.padBrightness, this.padSaturation);
            final int [] c = color.toIntRGB127 ();
            sb.append (StringUtils.toHexStr (index)).append (' ');
            sb.append (StringUtils.toHexStr (c[0])).append (' ');
            sb.append (StringUtils.toHexStr (c[1])).append (' ');
            sb.append (StringUtils.toHexStr (c[2])).append (' ');

            // Hardware does not support blinking, therefore needs to be implemented the hard
            // way
            final Integer key = Integer.valueOf (index);
            if (info.getBlinkColor () > 0)
                this.blinkingLights.put (key, info);
            else
                this.blinkingLights.remove (key);
        }

        int length = this.padInfos.size ();

        // Toggle blink colors every 600ms
        if (this.checkBlinking ())
        {
            length += this.blinkingLights.size ();

            for (final Entry<Integer, LightInfo> value: this.blinkingLights.entrySet ())
            {
                final LightInfo info = value.getValue ();

                final int colorIndex = this.isBlink ? info.getBlinkColor () : info.getColor ();
                final int [] c = this.colorManager.getColor (colorIndex, ButtonID.PAD1).scale (this.padBrightness, this.padSaturation).toIntRGB127 ();
                sb.append (StringUtils.toHexStr (value.getKey ().intValue ())).append (' ');
                sb.append (StringUtils.toHexStr (c[0])).append (' ');
                sb.append (StringUtils.toHexStr (c[1])).append (' ');
                sb.append (StringUtils.toHexStr (c[2])).append (' ');
            }
        }

        // No update necessary
        if (sb.length () == 0)
            return;

        length *= 4;
        final StringBuilder msg = new StringBuilder ("F0 47 7F 43 65 ");
        msg.append (StringUtils.toHexStr (length / 128)).append (' ');
        msg.append (StringUtils.toHexStr (length % 128)).append (' ');

        this.output.sendSysex (msg.append (sb).append ("F7").toString ());
    }


    /**
     * Update the LED brightness and saturation.
     *
     * @param padBrightness The brightness in the range of [0.25 .. 1]
     * @param padSaturation The color saturation in the range of [0 .. 1]
     */
    void configureLEDs (final double padBrightness, final double padSaturation)
    {
        this.padBrightness = padBrightness;
        this.padSaturation = padSaturation;
    }
}