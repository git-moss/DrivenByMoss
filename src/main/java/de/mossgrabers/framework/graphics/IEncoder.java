// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

import java.nio.ByteBuffer;


/**
 * An interface to an image encoder.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IEncoder
{
    /**
     * Encode the image data.
     *
     * @param imageBuffer The image data (red, green, blue, alpha, ...)
     * @param width The width of the image
     * @param height The height of the image
     */
    void encode (ByteBuffer imageBuffer, int width, int height);
}
