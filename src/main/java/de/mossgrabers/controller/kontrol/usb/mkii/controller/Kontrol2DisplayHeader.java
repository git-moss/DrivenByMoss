// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.controller;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMemoryBlock;

import java.nio.ByteBuffer;


/**
 * The header of a display message of the Kontrol 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol2DisplayHeader
{
    private static final byte [] DISPLAY_HEADER =
    {
        (byte) 0x84,
        (byte) 0x00,
        // 00 for first display, 01 for second one.
        (byte) 0x00,
        (byte) 0x60,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        (byte) 0x00,
        // Offset X-Axis (16-bit)
        (byte) 0x00,
        (byte) 0x00,
        // Offset Y-Axis (16-bit)
        (byte) 0x00,
        (byte) 0x00,
        // width (16 bit) - max 480 Pixel (= "01 E0")
        (byte) 0x01,
        (byte) 0xE0,
        // height (16 bit) - max value unknown yet
        (byte) 0x00,
        (byte) 0x80
    };

    private final IMemoryBlock   headerBlock;


    /**
     * Constructor.
     *
     * @param host The host
     * @param isFirst True for the first display otherwise the second
     */
    public Kontrol2DisplayHeader (final IHost host, final boolean isFirst)
    {
        this.headerBlock = host.createMemoryBlock (DISPLAY_HEADER.length);
        final ByteBuffer buffer = this.headerBlock.createByteBuffer ();
        buffer.put (DISPLAY_HEADER);
        buffer.put (2, (byte) (isFirst ? 0x00 : 0x01));
    }


    /**
     * Get the memory block with the configured header.
     *
     * @return The block
     */
    public IMemoryBlock getMemoryBlock ()
    {
        return this.headerBlock;
    }
}
