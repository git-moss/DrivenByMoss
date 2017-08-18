package de.mossgrabers.push.controller.display.model.grid;

import de.mossgrabers.push.controller.display.model.ChannelType;
import de.mossgrabers.push.controller.display.model.LayoutSettings;

import com.bitwig.extension.api.GraphicsOutput;

import java.awt.Color;
import java.awt.Label;
import java.io.IOException;


/**
 * An element in the grid which contains a menu and a channels' sends 1-4 or 5-8.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendsGridElement extends ChannelSelectionGridElement
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
    public SendsGridElement (final String [] sendNames, final String [] sendTexts, final int [] sendValues, final int [] modulatedSendValues, final boolean [] sendEdited, final String menuName, final boolean isMenuSelected, final String name, final Color color, final boolean isSelected, final ChannelType type, final boolean isExMode)
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
    public void draw (final GraphicsOutput gc, final double left, final double width, final double height, final LayoutSettings layoutSettings) throws IOException
    {
        super.draw (gc, left, width, height, layoutSettings);

        final String name = this.getName ();
        // Element is off if the name is empty
        if ((name == null || name.length () == 0) && !this.isExMode)
            return;

        final double trackRowTop = height - TRACK_ROW_HEIGHT - UNIT - SEPARATOR_SIZE;
        final double sliderWidth = width - 2 * INSET - 1;
        final double t = MENU_HEIGHT + 1;
        final double h = trackRowTop - t;
        final double sliderAreaHeight = h;
        // 4 rows of Texts and 4 rows of faders
        final double sendRowHeight = sliderAreaHeight / 8;
        final double sliderHeight = sendRowHeight - 2 * SEPARATOR_SIZE;

        // Background of slider area
        final Color backgroundColor = layoutSettings.getBackgroundColor ();
        setColor (gc, this.isSelected () || this.isExMode ? backgroundColor.brighter () : backgroundColor);
        gc.rectangle (this.isExMode ? left - SEPARATOR_SIZE : left, t, this.isExMode ? width + SEPARATOR_SIZE : width, this.isExMode ? h - 2 : h);
        gc.fill ();

        double topy = MENU_HEIGHT + (this.isExMode ? 0 : SEPARATOR_SIZE);

        // TODO gc.setFont (layoutSettings.getTextFont (sendRowHeight));
        final Color textColor = layoutSettings.getTextColor ();
        final Color borderColor = layoutSettings.getBorderColor ();
        final Color faderColor = layoutSettings.getFaderColor ();
        final Color editColor = layoutSettings.getEditColor ();
        final double faderLeft = left + INSET;
        for (int i = 0; i < 4; i++)
        {
            if (this.sendNames[i].length () == 0)
                break;

            setColor (gc, textColor);
            drawTextInBounds (gc, this.sendNames[i], faderLeft, topy + SEPARATOR_SIZE, width, sendRowHeight, Label.LEFT);
            topy += sendRowHeight;
            setColor (gc, borderColor);
            gc.rectangle (faderLeft, topy + SEPARATOR_SIZE, sliderWidth, sliderHeight);
            gc.fill ();
            final double valueWidth = (double) (this.sendValues[i] * sliderWidth / getMaxValue ());
            final boolean isSendModulated = this.modulatedSendValues[i] != 16383; // == -1
            final double modulatedValueWidth = isSendModulated ? (double) (this.modulatedSendValues[i] * sliderWidth / getMaxValue ()) : valueWidth;
            setColor (gc, faderColor);
            final double faderTop = topy + SEPARATOR_SIZE + 1;
            gc.rectangle (faderLeft + 1, faderTop, modulatedValueWidth - 1, sliderHeight - 2);
            gc.fill ();

            if (this.sendEdited[i])
            {
                setColor (gc, editColor);
                final boolean isTouched = this.sendTexts[i] != null && this.sendTexts[i].length () > 0;
                final double w = isTouched ? 3 : 1;
                gc.rectangle (Math.min (faderLeft + sliderWidth - w - 1, faderLeft + valueWidth + 1), faderTop, w, sliderHeight - 2);
                gc.fill ();
            }

            topy += sendRowHeight;
        }

        // Draw volume text on top if set
        final double boxWidth = sliderWidth / 2;
        final double boxLeft = faderLeft + sliderWidth - boxWidth;
        topy = MENU_HEIGHT;
        final Color backgroundDarker = backgroundColor.darker ();
        // TODO final Font textFont = layoutSettings.getTextFont (UNIT);
        for (int i = 0; i < 4; i++)
        {
            topy += sendRowHeight;

            if (this.sendTexts[i].length () > 0)
            {
                final double volumeTextTop = topy + sliderHeight + 1 + (this.isExMode ? 0 : SEPARATOR_SIZE);
                setColor (gc, backgroundDarker);
                gc.rectangle (boxLeft, volumeTextTop, boxWidth, UNIT);
                gc.fill ();
                setColor (gc, borderColor);
                gc.rectangle (boxLeft, volumeTextTop, boxWidth - 1, UNIT);
                gc.stroke ();
                // TODO gc.setFont (textFont);
                setColor (gc, textColor);
                drawTextInBounds (gc, this.sendTexts[i], boxLeft, volumeTextTop, boxWidth, UNIT, Label.CENTER);
            }

            topy += sendRowHeight;
        }
    }
}
