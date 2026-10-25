// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.BlinkingPadGrid;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.FrameworkException;


/**
 * Implementation of the OXI One grid of pads.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOnePadGrid extends BlinkingPadGrid
{
    private static final int []         COLOR_INDICES =
    {
        1,
        9,
        15,
        255
    };

    private final byte []               header        = new byte [7];
    private final ByteArrayOutputStream byteBuffer    = new ByteArrayOutputStream ();

    private final OxiOneConfiguration   configuration;


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     * @param configuration The configuration
     * @param isMk2 True if it is Mk2 otherwise Mk1
     */
    public OxiOnePadGrid (final ColorManager colorManager, final IMidiOutput output, final OxiOneConfiguration configuration, final boolean isMk2)
    {
        super (colorManager, output, 8, 16, 0);

        this.configuration = configuration;

        this.header[0] = (byte) 0xF0;
        this.header[1] = 0x00;
        this.header[2] = 0x21;
        this.header[3] = 0x5B;
        this.header[4] = (byte) (isMk2 ? 0x01 : 0x00);
        this.header[5] = 0x01;
        this.header[6] = 0x01;
    }


    /** {@inheritDoc} */
    @Override
    protected void updateController ()
    {
        try
        {
            synchronized (this.byteBuffer)
            {
                this.byteBuffer.reset ();
                this.byteBuffer.write (this.header);
                super.updateController ();
                this.byteBuffer.write ((byte) 0xF7);
                this.output.sendSysex (this.byteBuffer.toByteArray ());
            }
        }
        catch (final IOException ex)
        {
            throw new FrameworkException ("Could not create sysex message.");
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void sendPadUpdate (final int note, final int colorIndex)
    {
        synchronized (this.byteBuffer)
        {
            this.byteBuffer.write ((byte) (note / 16)); // y
            this.byteBuffer.write ((byte) (note % 16)); // x

            final double brightness = this.configuration.getPadBrightness ();
            final ColorEx color = this.colorManager.getColor (colorIndex, ButtonID.PAD1);
            final ColorEx scaledColor = color.scale (brightness, 1.0);

            final int red = crushBits (scaledColor.getRed ());
            this.byteBuffer.write ((byte) ((red & 0xF0) >> 4));
            this.byteBuffer.write ((byte) (red & 0xF));

            final int green = crushBits (scaledColor.getGreen ());
            this.byteBuffer.write ((byte) ((green & 0xF0) >> 4));
            this.byteBuffer.write ((byte) (green & 0xF));

            final int blue = crushBits (scaledColor.getBlue ());
            this.byteBuffer.write ((byte) ((blue & 0xF0) >> 4));
            this.byteBuffer.write ((byte) (blue & 0xF));
        }
    }


    /**
     * The color range of each R, G, B component is more like 4 distinguishable values than 255.
     *
     * @param colorPart The color component (R, G, or B) in the range of [0..1]
     * @return One of the 4 indices which is closest to the given value
     */
    private static int crushBits (final double colorPart)
    {
        if (colorPart == 0)
            return 0;
        return COLOR_INDICES[(int) Math.round (colorPart * 3)];
    }
}