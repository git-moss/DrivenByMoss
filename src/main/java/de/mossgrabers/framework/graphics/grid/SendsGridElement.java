// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.grid;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;


/**
 * An element in the grid which contains a menu and a channels' sends 1-4 or 5-8.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendsGridElement extends SelectionGridElement
{
    private final SendData [] sendData;
    private final boolean     isExMode;


    /**
     * Constructor.
     *
     * @param sendData The send data
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param type The type of the track
     * @param isExMode True if the sends grid element is an extension for a track grid element
     */
    public SendsGridElement (final SendData [] sendData, final String menuName, final boolean isMenuSelected, final String name, final ColorEx color, final boolean isSelected, final ChannelType type, final boolean isExMode)
    {
        super (menuName, isMenuSelected, name, color, isSelected, type);

        this.sendData = sendData;
        this.isExMode = isExMode;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final IGraphicsConfiguration configuration, final IGraphicsDimensions dimensions, final double left, final double width, final double height)
    {
        super.draw (gc, configuration, dimensions, left, width, height);

        final String name = this.getName ();
        // Element is off if the name is empty
        if ((name == null || name.length () == 0) && !this.isExMode)
            return;

        final double separatorSize = dimensions.getSeparatorSize ();
        final double menuHeight = dimensions.getMenuHeight ();
        final double unit = dimensions.getUnit ();
        final double inset = dimensions.getInset ();

        final int trackRowHeight = (int) (1.6 * unit);
        final double trackRowTop = height - trackRowHeight - unit - separatorSize;
        final double sliderWidth = width - 2 * inset - 1;
        final double t = menuHeight + 1;
        final double h = trackRowTop - t;
        final double sliderAreaHeight = h;
        // 4 rows of Texts and 4 rows of faders
        final double sendRowHeight = sliderAreaHeight / 8;
        final double sliderHeight = sendRowHeight - 2 * separatorSize;

        // Background of slider area
        final ColorEx backgroundColor = configuration.getColorBackground ();
        gc.fillRectangle (this.isExMode ? left - separatorSize : left, t, this.isExMode ? width + separatorSize : width, this.isExMode ? h - 2 : h, this.isSelected () || this.isExMode ? configuration.getColorBackgroundLighter () : backgroundColor);

        double topy = menuHeight + (this.isExMode ? 0 : separatorSize);

        final ColorEx textColor = configuration.getColorText ();
        final ColorEx borderColor = configuration.getColorBorder ();
        final ColorEx faderColor = configuration.getColorFader ();
        final ColorEx editColor = configuration.getColorEdit ();
        final double faderLeft = left + inset;
        for (int i = 0; i < this.sendData.length; i++)
        {
            final String n = this.sendData[i].getName ();
            if (n.length () == 0)
                break;

            gc.drawTextInBounds (n, faderLeft, topy + separatorSize, sliderWidth, sendRowHeight, Align.LEFT, textColor, sendRowHeight);
            topy += sendRowHeight;
            gc.fillRectangle (faderLeft, topy + separatorSize, sliderWidth, sliderHeight, borderColor);

            final double valueWidth = this.sendData[i].getValue () * sliderWidth / getMaxValue ();
            final int modulatedValue = this.sendData[i].getModulatedValue ();
            final boolean isSendModulated = modulatedValue != -1;
            final double modulatedValueWidth = isSendModulated ? (double) (modulatedValue * sliderWidth / getMaxValue ()) : valueWidth;
            final double faderTop = topy + separatorSize + 1;
            gc.fillRectangle (faderLeft + 1, faderTop, modulatedValueWidth - 1, sliderHeight - 2, faderColor);

            final String text = this.sendData[i].getText ();
            if (this.sendData[i].isEdited ())
            {
                final boolean isTouched = text != null && text.length () > 0;
                final double w = isTouched ? 3 : 1;
                gc.fillRectangle (Math.min (faderLeft + sliderWidth - w - 1, faderLeft + valueWidth + 1), faderTop, w, sliderHeight - 2, editColor);
            }

            topy += sendRowHeight;
        }

        // Draw volume text on top if set
        final double boxWidth = sliderWidth / 2;
        final double boxLeft = faderLeft + sliderWidth - boxWidth;
        topy = menuHeight;
        final ColorEx backgroundDarker = configuration.getColorBackgroundDarker ();
        for (int i = 0; i < this.sendData.length; i++)
        {
            topy += sendRowHeight;

            final String text = this.sendData[i].getText ();
            if (text.length () > 0)
            {
                final double volumeTextTop = topy + sliderHeight + 1 + (this.isExMode ? 0 : separatorSize);
                gc.fillRectangle (boxLeft, volumeTextTop, boxWidth, unit, backgroundDarker);
                gc.strokeRectangle (boxLeft, volumeTextTop, boxWidth - 1, unit, borderColor);
                gc.drawTextInBounds (text, boxLeft, volumeTextTop, boxWidth, unit, Align.CENTER, textColor, unit);
            }

            topy += sendRowHeight;
        }
    }
}
