// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.controller;

import de.mossgrabers.framework.controller.display.AbstractGraphicDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.graphics.ChromaticGraphicsConfiguration;
import de.mossgrabers.framework.graphics.DefaultGraphicsDimensions;
import de.mossgrabers.framework.graphics.IBitmap;

import java.util.Arrays;


/**
 * The display of the Akai Fire.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FireDisplay extends AbstractGraphicDisplay
{
    // @formatter:off
    private static int [][] bitMutate =
    {
        { 13,  19,  25,  31,  37,  43,  49 },
        {  0,  20,  26,  32,  38,  44,  50 },
        {  1,   7,  27,  33,  39,  45,  51 },
        {  2,   8,  14,  34,  40,  46,  52 },
        {  3,   9,  15,  21,  41,  47,  53 },
        {  4,  10,  16,  22,  28,  48,  54 },
        {  5,  11,  17,  23,  29,  35,  55 },
        {  6,  12,  18,  24,  30,  36,  42 }
      };
    // @formatter:on

    private final IMidiOutput output;
    private final int []      oledBitmap    = new int [1175];
    private final byte []     data          = new byte [8 + this.oledBitmap.length];
    private final int []      oldOledBitmap = new int [this.oledBitmap.length];

    private long              lastSend      = System.currentTimeMillis ();


    /**
     * Constructor. 4 rows (0-3) with 4 blocks (0-3). Each block consists of 17 characters or 2
     * cells (0-7).
     *
     * @param host The host
     * @param output The midi output which addresses the display
     * @param maxParameterValue The maximum parameter value (upper bound)
     */
    public FireDisplay (final IHost host, final IMidiOutput output, final int maxParameterValue)
    {
        super (host, new ChromaticGraphicsConfiguration (), new DefaultGraphicsDimensions (128, 64, maxParameterValue), "Fire Display");

        this.output = output;

        this.oledBitmap[0] = 0x00;
        this.oledBitmap[1] = 0x07;
        this.oledBitmap[2] = 0x00;
        this.oledBitmap[3] = 0x7f;

        this.data[0] = (byte) 0xF0;
        this.data[1] = 0x47; // AKAI
        this.data[2] = 0x7F; // All-Call
        this.data[3] = 0x43; // Fire
        this.data[4] = 0x0E; // WRITE OLED
        this.data[5] = (byte) (this.oledBitmap.length / 128); // Payload length high
        this.data[6] = (byte) (this.oledBitmap.length % 128); // Payload length low
        this.data[this.data.length - 1] = (byte) 0xF7;
    }


    /** {@inheritDoc} */
    @Override
    public void notify (final String message)
    {
        if (message == null)
            return;
        this.host.showNotification (message);
        this.setNotificationMessage (message);
    }


    /** {@inheritDoc} */
    @Override
    protected void send (final IBitmap image)
    {
        synchronized (this.data)
        {
            image.encode ( (imageBuffer, width, height) -> {

                // Unwind 128x64 arrangement into a 1024x8 arrangement of pixels
                for (int y = 0; y < height; y++)
                {
                    for (int x = 0; x < width; x++)
                    {
                        final int blue = imageBuffer.get ();
                        final int green = imageBuffer.get ();
                        final int red = imageBuffer.get ();
                        imageBuffer.get (); // Drop unused Alpha

                        final int xpos = x + 128 * (y / 8);
                        final int ypos = y % 8;

                        // Remap by tiling 7x8 block of translated pixels
                        final int remapBit = bitMutate[ypos][xpos % 7];
                        if (blue + green + red < 0)
                            this.oledBitmap[4 + xpos / 7 * 8 + remapBit / 7] |= 1 << remapBit % 7;
                        else
                            this.oledBitmap[4 + xpos / 7 * 8 + remapBit / 7] &= ~(1 << remapBit % 7);
                    }
                }

                // Convert to sysex and send to device
                final int length = this.oledBitmap.length;
                for (int i = 0; i < length; i++)
                    this.data[7 + i] = (byte) this.oledBitmap[i];
            });

            // Slow down display updates to not flood the device controller
            // Send if content has change or every 3 seconds if there was no change to keep the
            // display from going into sleep mode
            final long now = System.currentTimeMillis ();
            if (Arrays.compare (this.oledBitmap, this.oldOledBitmap) == 0 && now - this.lastSend < 3000)
                return;

            this.output.sendSysex (this.data);
            System.arraycopy (this.oledBitmap, 0, this.oldOledBitmap, 0, this.oledBitmap.length);
            this.lastSend = now;
        }
    }
}