// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.graphics;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IImage;
import de.mossgrabers.framework.utils.StringUtils;

import com.bitwig.extension.api.graphics.GradientPattern;
import com.bitwig.extension.api.graphics.GraphicsOutput;
import com.bitwig.extension.api.graphics.GraphicsOutput.AntialiasMode;
import com.bitwig.extension.api.graphics.Image;


/**
 * Implementation for the graphics context.
 *
 * @author Jürgen Moßgraber
 */
public class GraphicsContextImpl implements IGraphicsContext
{
    private final GraphicsOutput gc;


    /**
     * Constructor.
     *
     * @param antialiasMode The antialias mode to apply
     * @param gc The Bitwig graphics context
     */
    public GraphicsContextImpl (final AntialiasMode antialiasMode, final GraphicsOutput gc)
    {
        gc.setAntialias (antialiasMode);
        this.gc = gc;
    }


    /** {@inheritDoc} */
    @Override
    public void drawLine (final double x1, final double y1, final double x2, final double y2, final ColorEx color)
    {
        this.setColor (color);
        this.gc.moveTo (x1, y1);
        this.gc.lineTo (x2, y2);
        this.gc.stroke ();
    }


    /** {@inheritDoc} */
    @Override
    public void fillRectangle (final double x, final double y, final double width, final double height, final ColorEx color)
    {
        this.setColor (color);
        this.gc.rectangle (x, y, Math.max (0, width), Math.max (0, height));
        this.gc.fill ();
    }


    /** {@inheritDoc} */
    @Override
    public void strokeRectangle (final double left, final double top, final double width, final double height, final ColorEx color)
    {
        this.strokeRectangle (left, top, width, height, color, 1);
    }


    /** {@inheritDoc} */
    @Override
    public void strokeRectangle (final double left, final double top, final double width, final double height, final ColorEx color, final double lineWidth)
    {
        // Turn off antialias or otherwise we do not get a single line
        this.gc.setAntialias (AntialiasMode.OFF);
        this.setColor (color);
        this.gc.setLineWidth (lineWidth);
        this.gc.rectangle (left, top, width, height);
        this.gc.stroke ();
        this.gc.setAntialias (AntialiasMode.BEST);
    }


    /** {@inheritDoc} */
    @Override
    public void fillRoundedRectangle (final double left, final double top, final double width, final double height, final double radius, final ColorEx backgroundColor)
    {
        this.setColor (backgroundColor);
        this.drawRoundedRectInternal (left, top, width, height, radius);
    }


    /** {@inheritDoc} */
    @Override
    public void fillGradientRoundedRectangle (final double left, final double top, final double width, final double height, final double radius, final ColorEx color1, final ColorEx color2)
    {
        final GradientPattern linearGradient = this.gc.createLinearGradient (left, top, left, top + height);
        linearGradient.addColorStop (0, color1.getRed (), color1.getGreen (), color1.getBlue ());
        linearGradient.addColorStop (1, color2.getRed (), color2.getGreen (), color2.getBlue ());
        this.gc.setPattern (linearGradient);
        this.drawRoundedRectInternal (left, top, width, height, radius);
    }


    private void drawRoundedRectInternal (final double left, final double top, final double width, final double height, final double radius)
    {
        final double degrees = Math.PI / 180.0;
        this.gc.newSubPath ();
        this.gc.arc (left + width - radius, top + radius, radius, -90 * degrees, 0 * degrees);
        this.gc.arc (left + width - radius, top + height - radius, radius, 0 * degrees, 90 * degrees);
        this.gc.arc (left + radius, top + height - radius, radius, 90 * degrees, 180 * degrees);
        this.gc.arc (left + radius, top + radius, radius, 180 * degrees, 270 * degrees);
        this.gc.closePath ();
        this.gc.fill ();
    }


    /** {@inheritDoc} */
    @Override
    public void fillTriangle (final double x1, final double y1, final double x2, final double y2, final double x3, final double y3, final ColorEx fillColor)
    {
        this.setColor (fillColor);
        this.gc.moveTo (x1, y1);
        this.gc.lineTo (x2, y2);
        this.gc.lineTo (x3, y3);
        this.gc.lineTo (x1, y1);
        this.gc.fill ();
    }


    /** {@inheritDoc} */
    @Override
    public void strokeTriangle (final double x1, final double y1, final double x2, final double y2, final double x3, final double y3, final ColorEx lineColor)
    {
        this.setColor (lineColor);
        this.gc.moveTo (x1, y1);
        this.gc.lineTo (x2, y2);
        this.gc.lineTo (x3, y3);
        this.gc.lineTo (x1, y1);
        this.gc.stroke ();
    }


    /** {@inheritDoc} */
    @Override
    public void fillCircle (final double x, final double y, final double radius, final ColorEx fillColor)
    {
        this.setColor (fillColor);
        this.gc.circle (x, y, Math.max (0, radius));
        this.gc.fill ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawTextInBounds (final String text, final double x, final double y, final double width, final double height, final Align alignment, final ColorEx color, final double fontSize)
    {
        this.drawTextInBounds (text, x, y, width, height, alignment, color, null, fontSize);
    }


    /** {@inheritDoc} */
    @Override
    public void drawTextInBounds (final String text, final double x, final double y, final double width, final double height, final Align alignment, final ColorEx color, final ColorEx backgroundColor, final double fontSize)
    {
        if (text == null || text.length () == 0)
            return;

        final String txt = StringUtils.fixFontCharacters (text);

        this.gc.save ();
        this.gc.setFontSize (fontSize);

        // We need to calculate the text height from a character which has no ascent, since showText
        // always draws the text on the baseline of the font!
        final double h = this.gc.getTextExtents ("T").getHeight ();
        final double w = this.gc.getTextExtents (txt).getWidth ();
        final double posX = alignment == Align.CENTER ? x + (width - w) / 2.0 : x;
        final double posY = y + (height + h) / 2;

        this.gc.rectangle (x, y, width, height);
        this.gc.clip ();

        if (backgroundColor != null)
        {
            final double inset = 12.0;
            this.fillRoundedRectangle (posX - inset, posY - h - inset, w + 2 * inset, h + 2 * inset, inset, backgroundColor);
        }

        this.setColor (color);
        this.gc.moveTo (posX, posY);
        this.gc.showText (txt);
        this.gc.resetClip ();
        this.gc.restore ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawTextInHeight (final String text, final double x, final double y, final double height, final ColorEx color, final double fontSize)
    {
        this.drawTextInHeight (text, x, y, height, color, null, fontSize);
    }


    /** {@inheritDoc} */
    @Override
    public void drawTextInHeight (final String text, final double x, final double y, final double height, final ColorEx color, final ColorEx backgroundColor, final double fontSize)
    {
        if (text == null || text.length () == 0)
            return;

        final String txt = StringUtils.fixFontCharacters (text);

        this.gc.save ();
        this.gc.setFontSize (fontSize);

        // We need to calculate the text height from a character which has no ascent, since showText
        // always draws the text on the baseline of the font!
        final double h = this.gc.getTextExtents ("T").getHeight ();
        final double posY = y + (height + h) / 2;

        if (backgroundColor != null)
        {
            final double w = this.gc.getTextExtents (txt).getWidth ();
            final double inset = 12.0;
            this.fillRoundedRectangle (x - inset, posY - h - inset, w + 2 * inset, h + 2 * inset, inset, backgroundColor);
        }

        this.setColor (color);
        this.gc.moveTo (x, posY);
        this.gc.showText (txt);
        this.gc.restore ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawImage (final IImage icon, final double x, final double y)
    {
        this.gc.drawImage (((ImageImpl) icon).image (), x, y);
    }


    /** {@inheritDoc} */
    @Override
    public void maskImage (final IImage icon, final double x, final double y, final ColorEx maskColor)
    {
        final ImageImpl imageImpl = (ImageImpl) icon;
        try
        {
            this.setColor (maskColor);
            final Image image = imageImpl.image ();
            this.gc.mask (image, x, y);
            this.gc.fill ();
        }
        catch (final RuntimeException ex)
        {
            ex.printStackTrace ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public double calculateFontSize (final String text, final double maxHeight, final double maxWidth, final double minimumFontSize)
    {
        double size = minimumFontSize;
        double fittingSize = -1;
        while (size < maxHeight)
        {
            this.gc.setFontSize (size);
            final double width = this.gc.getTextExtents (text).getWidth ();
            if (width > maxWidth)
                break;
            fittingSize = size;
            size += 1.0;
        }
        return fittingSize;
    }


    protected void setColor (final ColorEx color)
    {
        this.gc.setColor (color.getRed (), color.getGreen (), color.getBlue ());
    }
}
