// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.graphics;

import de.mossgrabers.framework.graphics.IImage;

import com.bitwig.extension.api.graphics.Image;


/**
 * An implementation for an image.
 *
 * @author Jürgen Moßgraber
 *
 * @param image The Bitwig image
 */
public record ImageImpl (Image image) implements IImage
{
    /** {@inheritDoc} */
    @Override
    public double getWidth ()
    {
        return this.image.getWidth ();
    }


    /** {@inheritDoc} */
    @Override
    public int getHeight ()
    {
        return this.image.getHeight ();
    }
}
