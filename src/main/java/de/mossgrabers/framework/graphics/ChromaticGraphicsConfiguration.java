// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

import de.mossgrabers.framework.controller.color.ColorEx;


/**
 * Graphics configuration for a black and white graphics.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ChromaticGraphicsConfiguration implements IGraphicsConfiguration
{
    /** {@inheritDoc}} */
    @Override
    public ColorEx getColorText ()
    {
        return ColorEx.WHITE;
    }


    /** {@inheritDoc}} */
    @Override
    public ColorEx getColorBackground ()
    {
        return ColorEx.BLACK;
    }


    /** {@inheritDoc}} */
    @Override
    public ColorEx getColorBackgroundDarker ()
    {
        return ColorEx.BLACK;
    }


    /** {@inheritDoc}} */
    @Override
    public ColorEx getColorBackgroundLighter ()
    {
        return ColorEx.BLACK;
    }


    /** {@inheritDoc}} */
    @Override
    public ColorEx getColorBorder ()
    {
        return ColorEx.BLACK;
    }


    /** {@inheritDoc}} */
    @Override
    public ColorEx getColorEdit ()
    {
        return ColorEx.WHITE;
    }


    /** {@inheritDoc}} */
    @Override
    public ColorEx getColorFader ()
    {
        return ColorEx.WHITE;
    }


    /** {@inheritDoc}} */
    @Override
    public ColorEx getColorVu ()
    {
        return ColorEx.WHITE;
    }


    /** {@inheritDoc}} */
    @Override
    public ColorEx getColorRecord ()
    {
        return ColorEx.WHITE;
    }


    /** {@inheritDoc}} */
    @Override
    public ColorEx getColorSolo ()
    {
        return ColorEx.WHITE;
    }


    /** {@inheritDoc}} */
    @Override
    public ColorEx getColorMute ()
    {
        return ColorEx.WHITE;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAntialiasEnabled ()
    {
        return false;
    }
}
