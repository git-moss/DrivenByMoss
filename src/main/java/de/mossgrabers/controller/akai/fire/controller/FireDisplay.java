// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.controller;

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
    private static final int [][] BIT_MUTATE =
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

    private static final int       STRIPE_SIZE   = 147;
    private static final int       PACKET_SIZE   = 4 + STRIPE_SIZE;

    private final IMidiOutput      output;
    private final int [] []        oledBitmap    = new int [8] [STRIPE_SIZE];
    private final int [] []        oldOledBitmap = new int [8] [STRIPE_SIZE];
    private final byte []          data          = new byte [12 + STRIPE_SIZE];

    private long                   lastSend      = System.currentTimeMillis ();


    /**
     * Constructor. The display is divided into eight bands of 8×128 pixels. Each band in this
     * arrangement is written in a block of 8×7 pixels
     *
     * @param host The host
     * @param output The MIDI output which addresses the display
     * @param maxParameterValue The maximum parameter value (upper bound)
     */
    public FireDisplay (final IHost host, final IMidiOutput output, final int maxParameterValue)
    {
        super (host, new ChromaticGraphicsConfiguration (), new DefaultGraphicsDimensions (128, 64, maxParameterValue), "Fire Display");

        this.output = output;

        this.data[0] = (byte) 0xF0;
        this.data[1] = 0x47; // AKAI
        this.data[2] = 0x7F; // All-Call
        this.data[3] = 0x43; // Fire
        this.data[4] = 0x0E; // WRITE OLED

        // Pay-load length high
        this.data[5] = (byte) (PACKET_SIZE / 128);
        // Pay-load length low
        this.data[6] = (byte) (PACKET_SIZE % 128);

        // Start column of update
        this.data[9] = 0x00;
        // End column of update
        this.data[10] = 0x7f;

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
                for (int stripe = 0; stripe < 8; stripe++)
                {
                    for (int y = 0; y < height / 8; y++)
                    {
                        for (int x = 0; x < width; x++)
                        {
                            final int blue = imageBuffer.get ();
                            final int green = imageBuffer.get ();
                            final int red = imageBuffer.get ();
                            imageBuffer.get (); // Drop unused Alpha

                            final int xpos = x + 128 * (y / 8);
                            final int ypos = y % 8;

                            // Re-map by tiling 7x8 block of translated pixels
                            final int remapBit = BIT_MUTATE[ypos][xpos % 7];
                            final int idx = xpos / 7 * 8 + remapBit / 7;
                            if (blue + green + red < 0)
                                this.oledBitmap[stripe][idx] |= 1 << remapBit % 7;
                            else
                                this.oledBitmap[stripe][idx] &= ~(1 << remapBit % 7);
                        }
                    }
                }
            });

            // Convert to system exclusive and send to device
            for (int stripe = 0; stripe < 8; stripe++)
            {
                // Start 8-pixel band of update
                this.data[7] = (byte) stripe;
                // End 8-pixel band of update (here, 8 bands of 8 pixels, i.e. the whole display)
                this.data[8] = (byte) stripe;

                for (int i = 0; i < STRIPE_SIZE; i++)
                    this.data[11 + i] = (byte) this.oledBitmap[stripe][i];

                // Slow down display updates to not flood the device controller
                // Send if content has change or every 3 seconds if there was no change to keep
                // the display from going into sleep mode
                final long now = System.currentTimeMillis ();
                if (Arrays.compare (this.oledBitmap[stripe], this.oldOledBitmap[stripe]) == 0 && now - this.lastSend < 3000)
                    continue;
                System.arraycopy (this.oledBitmap[stripe], 0, this.oldOledBitmap[stripe], 0, STRIPE_SIZE);
                this.lastSend = now;

                this.output.sendSysex (this.data);
            }
        }
    }
}