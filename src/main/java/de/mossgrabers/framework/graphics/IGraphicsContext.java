// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

import de.mossgrabers.framework.controller.color.ColorEx;


/**
 * Interface to drawing functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IGraphicsContext
{
    /**
     * Draw a filled rectangle.
     *
     * @param left The left position of the rectangle
     * @param top The top position of the rectangle
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The color of the rectangle
     */
    void fillRectangle (double left, double top, double width, double height, ColorEx color);


    /**
     * Draw a rectangle.
     *
     * @param left The left position of the rectangle
     * @param top The top position of the rectangle
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The color of the border of the rectangle
     */
    void strokeRectangle (double left, double top, double width, double height, ColorEx color);


    /**
     * Draw a rectangle.
     *
     * @param left The left position of the rectangle
     * @param top The top position of the rectangle
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param color The color of the border of the rectangle
     * @param lineWidth The width of the line
     */
    void strokeRectangle (double left, double top, double width, double height, ColorEx color, double lineWidth);


    /**
     * Draw a filled rounded rectangle.
     *
     * @param left The left position of the rectangle
     * @param top The top position of the rectangle
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param radius The radius of the rounded corners
     * @param fillColor The color of the rectangle
     */
    void fillRoundedRectangle (double left, double top, double width, double height, double radius, ColorEx fillColor);


    /**
     * Draw a rounded rectangle filled with a gradient color.
     *
     * @param left The left position of the rectangle
     * @param top The top position of the rectangle
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param radius The radius of the rounded corners
     * @param color1 The start color of the gradient
     * @param color2 The end color of the gradient
     */
    void fillGradientRoundedRectangle (double left, double top, double width, double height, double radius, ColorEx color1, ColorEx color2);


    /**
     * Draw a filled triangle.
     *
     * @param x1 The X position of the first corner of the triangle
     * @param y1 The Y position of the first corner of the triangle
     * @param x2 The X position of the second corner of the triangle
     * @param y2 The Y position of the second corner of the triangle
     * @param x3 The X position of the third corner of the triangle
     * @param y3 The Y position of the third corner of the triangle
     * @param fillColor The color of the triangle
     */
    void fillTriangle (double x1, double y1, double x2, double y2, double x3, double y3, ColorEx fillColor);


    /**
     * Draw a filled triangle.
     *
     * @param x1 The X position of the first corner of the triangle
     * @param y1 The Y position of the first corner of the triangle
     * @param x2 The X position of the second corner of the triangle
     * @param y2 The Y position of the second corner of the triangle
     * @param x3 The X position of the third corner of the triangle
     * @param y3 The Y position of the third corner of the triangle
     * @param color The border color of the triangle
     */
    void strokeTriangle (double x1, double y1, double x2, double y2, double x3, double y3, ColorEx color);


    /**
     * Draw a filled circle.
     *
     * @param x The X position of the circles center
     * @param y The Y position of the circles center
     * @param radius The radius of the circle
     * @param fillColor The color of the circle
     */
    void fillCircle (double x, double y, double radius, ColorEx fillColor);


    /**
     * Draws a text centered into a height (horizontally). The text is not clipped.
     *
     * @param text The text to draw
     * @param x The x position of the boundary
     * @param y The y position of the boundary
     * @param height The height position of the boundary
     * @param color The color of the text
     * @param fontSize The size of the font
     */
    void drawTextInHeight (String text, double x, double y, double height, ColorEx color, double fontSize);


    /**
     * Draws a text centered into a height (horizontally). The text is not clipped.
     *
     * @param text The text to draw
     * @param x The x position of the boundary
     * @param y The y position of the boundary
     * @param height The height position of the boundary
     * @param color The color of the text
     * @param backgroundColor Draws a background behind the text with this color
     * @param fontSize The size of the font
     */
    void drawTextInHeight (String text, double x, double y, double height, ColorEx color, ColorEx backgroundColor, double fontSize);


    /**
     * Draws a text into a boundary. The text is clipped on the right border of the bounds.
     * Calculates the text descent.
     *
     * @param text The text to draw
     * @param x The x position of the boundary
     * @param y The y position of the boundary
     * @param width The width position of the boundary
     * @param height The height position of the boundary
     * @param alignment The alignment of the text: Label.LEFT or Label.CENTER
     * @param color The color of the text
     * @param fontSize The size of the font
     */
    void drawTextInBounds (String text, double x, double y, double width, double height, Align alignment, ColorEx color, double fontSize);


    /**
     * Draws a text into a boundary. The text is clipped on the right border of the bounds.
     * Calculates the text descent.
     *
     * @param text The text to draw
     * @param x The x position of the boundary
     * @param y The y position of the boundary
     * @param width The width position of the boundary
     * @param height The height position of the boundary
     * @param alignment The alignment of the text: Label.LEFT or Label.CENTER
     * @param color The color of the text
     * @param backgroundColor Draws a background behind the text with this color
     * @param fontSize The size of the font
     */
    void drawTextInBounds (String text, double x, double y, double width, double height, Align alignment, ColorEx color, ColorEx backgroundColor, double fontSize);


    /**
     * Draw an image.
     *
     * @param image The image to draw
     * @param x The X position of where to draw the image
     * @param y The Y position of where to draw the image
     */
    void drawImage (IImage image, double x, double y);


    /**
     * Draw an image masked by a color.
     *
     * @param image The image to draw
     * @param x The X position of where to draw the image
     * @param y The Y position of where to draw the image
     * @param maskColor The color to use for masking
     */
    void maskImage (IImage image, double x, double y, final ColorEx maskColor);


    /**
     * Draw a line.
     *
     * @param x1 The start X position of the line
     * @param y1 The start Y position of the line
     * @param x2 The end Y position of the line
     * @param y2 The end Y position of the line
     * @param lineColor The color of the line
     */
    void drawLine (double x1, double y1, double x2, double y2, ColorEx lineColor);


    /**
     * Calculates the maximum height of a text which needs to fit into a width.
     *
     * @param text The text
     * @param maxHeight The maximum height of the text
     * @param maxWidth The maximum width
     * @param minimumFontSize The minimum font size to return (this might prevent that the text fits
     *            fully in the given dimensions)
     * @return The text height or -1 if the minimum height of 10 does not fit into the width
     */
    double calculateFontSize (String text, double maxHeight, double maxWidth, double minimumFontSize);
}
