// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.graphics;

import de.mossgrabers.framework.graphics.IImage;

import com.bitwig.extension.api.graphics.Image;


/**
 * An implementation for an image.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ImageImpl implements IImage
{
    private Image image;


    /**
     * Constructor.
     *
     * @param image The Bitwig image
     */
    public ImageImpl (final Image image)
    {
        this.image = image;
    }


    /**
     * Get the encapsulated Bitwig image.
     *
     * @return The image
     */
    public Image getImage ()
    {
        return this.image;
    }


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
