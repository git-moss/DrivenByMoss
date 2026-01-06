// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.controller;

import java.util.BitSet;

import de.mossgrabers.framework.controller.display.AbstractGraphicDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.graphics.ChromaticGraphicsConfiguration;
import de.mossgrabers.framework.graphics.DefaultGraphicsDimensions;
import de.mossgrabers.framework.graphics.IBitmap;


/**
 * The display of the OXI One.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneDisplay extends AbstractGraphicDisplay
{
    private static final int  NUM_COLS      = 128;
    private static final int  NUM_ROWS      = 64;
    private static final int  NUM_PIXELS    = NUM_COLS * NUM_ROWS;

    private final IMidiOutput output;
    private final BitSet      oledBitmap    = new BitSet (NUM_PIXELS);
    private final BitSet      oldOledBitmap = new BitSet (NUM_PIXELS);
    private final byte []     displayData   = new byte [8 + NUM_PIXELS / 4];

    private long              lastSend      = System.currentTimeMillis ();


    /**
     * Constructor. The display has 128x64 pixels.
     *
     * @param host The host
     * @param output The MIDI output which addresses the display
     * @param maxParameterValue The maximum parameter value (upper bound)
     */
    public OxiOneDisplay (final IHost host, final IMidiOutput output, final int maxParameterValue)
    {
        super (host, new ChromaticGraphicsConfiguration (), new DefaultGraphicsDimensions (NUM_COLS, NUM_ROWS, maxParameterValue), "OXI One Display");

        this.output = output;

        this.displayData[0] = (byte) 0xF0;
        this.displayData[1] = (byte) 0x00;
        this.displayData[2] = (byte) 0x21;
        this.displayData[3] = (byte) 0x5B;
        this.displayData[4] = (byte) 0x00;
        this.displayData[5] = (byte) 0x01;

        this.displayData[6] = 0x03; // Set display command

        this.displayData[this.displayData.length - 1] = (byte) 0xF7;
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
        synchronized (this.displayData)
        {
            image.encode ( (imageBuffer, width, height) -> {

                // Set all 128x64 pixels
                for (int y = 0; y < height; y++)
                {
                    for (int x = 0; x < width; x++)
                    {
                        final int blue = imageBuffer.get ();
                        final int green = imageBuffer.get ();
                        final int red = imageBuffer.get ();
                        imageBuffer.get (); // Drop unused Alpha

                        this.oledBitmap.set (y * NUM_COLS + x, blue + green + red < 0);
                    }
                }
            });

            // Slow down display updates to not flood the device controller
            // Send if content has changed or every 3 seconds if there was no change to keep
            // the display from going into sleep mode
            final long now = System.currentTimeMillis ();
            if (this.oledBitmap.equals (this.oldOledBitmap))
            {
                if (now - this.lastSend < 3000)
                    return;
            }
            else
            {
                this.oldOledBitmap.clear ();
                this.oldOledBitmap.or (this.oledBitmap);

                int pos = 7; // Offset to the 1st data byte

                // Format is a bit weird: 2 bytes contain 8 pixel in a y-column. These stripes start
                // from top left.
                for (int y = 0; y < NUM_ROWS; y += 8)
                {
                    for (int x = 0; x < NUM_COLS; x++)
                    {
                        byte nibble1 = 0;
                        byte nibble2 = 0;
                        for (int bit = 0; bit < 8; bit++)
                        {
                            if (this.oledBitmap.get ((y + bit) * NUM_COLS + x))
                            {
                                if (bit > 3)
                                    nibble1 |= 1 << bit - 4;
                                else
                                    nibble2 |= 1 << bit;
                            }
                        }
                        this.displayData[pos] = nibble1;
                        this.displayData[pos + 1] = nibble2;
                        pos += 2;
                    }
                }
            }
            this.lastSend = now;

            this.output.sendSysex (this.displayData);
        }
    }
}