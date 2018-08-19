// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
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
    final String []    sendNames           = new String []
    {
        "",
        "",
        "",
        ""
    };
    final String []    sendTexts           = new String []
    {
        "",
        "",
        "",
        ""
    };
    final int []       sendValues          = new int []
    {
        0,
        0,
        0,
        0
    };
    final int []       modulatedSendValues = new int []
    {
        0,
        0,
        0,
        0
    };
    private boolean [] sendEdited          = new boolean []
    {
        false,
        false,
        false,
        false
    };
    private boolean    isExMode;


    /**
     * Constructor.
     *
     * @param sendNames The names of the send tracks
     * @param sendTexts The texts of the sends volumes
     * @param sendValues The values of the sends volumes
     * @param modulatedSendValues The modulated values of the sends volumes, -1 if not modulated
     * @param sendEdited The states of which send can be edited
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param type The type of the track
     * @param isExMode True if the sends grid element is an extension for a track grid element
     */
    public SendsGridElement (final String [] sendNames, final String [] sendTexts, final int [] sendValues, final int [] modulatedSendValues, final boolean [] sendEdited, final String menuName, final boolean isMenuSelected, final String name, final ColorEx color, final boolean isSelected, final ChannelType type, final boolean isExMode)
    {
        super (menuName, isMenuSelected, name, color, isSelected, type);
        for (int i = 0; i < 4; i++)
        {
            this.sendNames[i] = sendNames[i];
            this.sendTexts[i] = sendTexts[i];
            this.sendValues[i] = sendValues[i];
            this.modulatedSendValues[i] = modulatedSendValues[i];
            this.sendEdited[i] = sendEdited[i];
        }

        this.isExMode = isExMode;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final IGraphicsConfiguration configuration, IGraphicsDimensions dimensions, final double left, final double width, final double height)
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
        for (int i = 0; i < 4; i++)
        {
            if (this.sendNames[i].length () == 0)
                break;

            gc.drawTextInBounds (this.sendNames[i], faderLeft, topy + separatorSize, sliderWidth, sendRowHeight, Align.LEFT, textColor, sendRowHeight);
            topy += sendRowHeight;
            gc.fillRectangle (faderLeft, topy + separatorSize, sliderWidth, sliderHeight, borderColor);

            final double valueWidth = this.sendValues[i] * sliderWidth / getMaxValue ();
            final boolean isSendModulated = this.modulatedSendValues[i] != -1;
            final double modulatedValueWidth = isSendModulated ? (double) (this.modulatedSendValues[i] * sliderWidth / getMaxValue ()) : valueWidth;
            final double faderTop = topy + separatorSize + 1;
            gc.fillRectangle (faderLeft + 1, faderTop, modulatedValueWidth - 1, sliderHeight - 2, faderColor);

            if (this.sendEdited[i])
            {
                final boolean isTouched = this.sendTexts[i] != null && this.sendTexts[i].length () > 0;
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
        for (int i = 0; i < 4; i++)
        {
            topy += sendRowHeight;

            if (this.sendTexts[i].length () > 0)
            {
                final double volumeTextTop = topy + sliderHeight + 1 + (this.isExMode ? 0 : separatorSize);
                gc.fillRectangle (boxLeft, volumeTextTop, boxWidth, unit, backgroundDarker);
                gc.strokeRectangle (boxLeft, volumeTextTop, boxWidth - 1, unit, borderColor);
                gc.drawTextInBounds (this.sendTexts[i], boxLeft, volumeTextTop, boxWidth, unit, Align.CENTER, textColor, unit);
            }

            topy += sendRowHeight;
        }
    }
}
