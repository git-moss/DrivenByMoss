// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.controller;

import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * Implementation of the Fire grid of pads.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FirePadGrid extends PadGridImpl
{
    // @formatter:off
    static final int [] TRANSLATE_MATRIX =
    {
        102, 103, 104, 105, 106, 107, 108, 109,  
         86,  87,  88,  89,  90,  91,  92,  93,  
         70,  71,  72,  73,  74,  75,  76,  77,
         54,  55,  56,  57,  58,  59,  60,  61,  
        110, 111, 112, 113, 114, 115, 116, 117,  
         94,  95,  96,  97,  98,  99, 100, 101,  
         78,  79,  80,  81,  82,  83,  84,  85, 
         62,  63,  64,  65,  66,  67,  68,  69  
    };
    // @formatter:on

    private static final Map<Integer, Integer> INVERSE_TRANSLATE_MATRIX = new HashMap<> (64);
    static
    {
        for (int i = 0; i < TRANSLATE_MATRIX.length; i++)
            INVERSE_TRANSLATE_MATRIX.put (Integer.valueOf (TRANSLATE_MATRIX[i]), Integer.valueOf (36 + i));
    }

    private final Map<Integer, LightInfo> padInfos = new TreeMap<> ();


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The midi output which can address the pad states
     */
    public FirePadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output);
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
        return new int []
        {
            0,
            TRANSLATE_MATRIX[note - 36]
        };
    }


    /**
     * Flush the changed pad LEDs using sysex.
     */
    public void flush ()
    {
        synchronized (this.padInfos)
        {
            if (this.padInfos.isEmpty ())
                return;

            final String update = this.buildLEDUpdate (this.padInfos);
            this.output.sendSysex (update);

            this.padInfos.clear ();
        }
    }


    private String buildLEDUpdate (final Map<Integer, LightInfo> padInfos)
    {
        final StringBuilder sb = new StringBuilder ("F0 47 7F 43 65 ");

        final int length = this.padInfos.size () * 4;
        sb.append (StringUtils.toHexStr (length / 128)).append (' ');
        sb.append (StringUtils.toHexStr (length % 128)).append (' ');

        for (final Entry<Integer, LightInfo> e: padInfos.entrySet ())
        {
            final int note = e.getKey ().intValue ();
            final LightInfo info = e.getValue ();

            final int index = note - 54;
            final int translatedIndex = (3 - index / 16) * 16 + index % 16;

            int colorIndex = info.getColor ();

            final ColorEx color = this.colorManager.getColor (colorIndex, ButtonID.get (ButtonID.PAD1, translatedIndex));
            final int [] c = color.toIntRGB127 ();
            sb.append (StringUtils.toHexStr (index)).append (' ');
            sb.append (StringUtils.toHexStr (c[0])).append (' ');
            sb.append (StringUtils.toHexStr (c[1])).append (' ');
            sb.append (StringUtils.toHexStr (c[2])).append (' ');

            // TODO Does the hardware support blinking?
            // if (info.getBlinkColor () <= 0)
            // {
            // // 00h: Static colour from palette, Lighting data is 1 byte specifying palette
            // // entry.
            // sb.append ("00 ").append (StringUtils.toHexStr (note)).append (' ').append
            // (StringUtils.toHexStr (info.getColor ())).append (' ');
            // }
            // else
            // {
            // if (info.isFast ())
            // {
            // // 01h: Flashing colour, Lighting data is 2 bytes specifying Colour B and
            // // Colour A.
            // sb.append ("01 ").append (StringUtils.toHexStr (note)).append (' ').append
            // (StringUtils.toHexStr (info.getBlinkColor ())).append (' ').append
            // (StringUtils.toHexStr (info.getColor ())).append (' ');
            // }
            // else
            // {
            // // 02h: Pulsing colour, Lighting data is 1 byte specifying palette entry.
            // sb.append ("02 ").append (StringUtils.toHexStr (note)).append (' ').append
            // (StringUtils.toHexStr (info.getColor ())).append (' ');
            // }
            // }
        }
        return sb.append ("F7").toString ();
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