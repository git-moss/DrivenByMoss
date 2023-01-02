// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.IGraphicsInfo;
import de.mossgrabers.framework.graphics.canvas.utils.SendData;

import java.util.Arrays;


/**
 * An element in the grid which contains a menu and a channels' sends 1-4 or 5-8.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendsComponent extends ChannelSelectComponent
{
    private final SendData [] sendData;
    private final boolean     isExMode;
    private final boolean     isSendActive;


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
     * @param isSendActive True if the upper send part is activated
     * @param isChannelLabelActive True if channel is activated
     */
    public SendsComponent (final SendData [] sendData, final String menuName, final boolean isMenuSelected, final String name, final ColorEx color, final boolean isSelected, final ChannelType type, final boolean isExMode, final boolean isSendActive, final boolean isChannelLabelActive)
    {
        super (type, menuName, isMenuSelected, name, color, isSelected, isChannelLabelActive);

        this.sendData = sendData;
        this.isExMode = isExMode;
        this.isSendActive = isSendActive;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        super.draw (info);

        final String name = this.footer.getText ();
        // Element is off if the name is empty
        if ((name == null || name.length () == 0) && !this.isExMode)
            return;

        final IGraphicsContext gc = info.getContext ();
        final IGraphicsDimensions dimensions = info.getDimensions ();
        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final double left = info.getBounds ().left ();
        final double width = info.getBounds ().width ();
        final double height = info.getBounds ().height ();

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
        final ColorEx backgroundColor = this.modifyIfOff (configuration.getColorBackground ());
        gc.fillRectangle (this.isExMode ? left - separatorSize : left, t, this.isExMode ? width + separatorSize : width, this.isExMode ? h - 2 : h, this.footer.isSelected () || this.isExMode ? this.modifyIfOff (configuration.getColorBackgroundLighter ()) : backgroundColor);

        double topy = menuHeight + (this.isExMode ? 0 : separatorSize);

        final ColorEx textColor = this.modifyIfOff (configuration.getColorText ());
        final ColorEx borderColor = this.modifyIfOff (configuration.getColorBorder ());
        final ColorEx faderColor = this.modifyIfOff (configuration.getColorFader ());
        final ColorEx editColor = this.modifyIfOff (configuration.getColorEdit ());
        final double faderLeft = left + inset;
        for (final SendData send: this.sendData)
        {
            final String sendName = send.name ();
            if (sendName.length () == 0)
                break;

            gc.drawTextInBounds (sendName, faderLeft, topy + separatorSize, sliderWidth, sendRowHeight, Align.LEFT, textColor, sendRowHeight);
            topy += sendRowHeight;
            gc.fillRectangle (faderLeft, topy + separatorSize, sliderWidth, sliderHeight, borderColor);

            final double valueWidth = send.value () * sliderWidth / dimensions.getParameterUpperBound ();
            final int modulatedValue = send.modulatedValue ();
            final boolean isSendModulated = modulatedValue != -1;
            final double modulatedValueWidth = isSendModulated ? (double) (modulatedValue * sliderWidth / dimensions.getParameterUpperBound ()) : valueWidth;
            final double faderTop = topy + separatorSize + 1;
            gc.fillRectangle (faderLeft + 1, faderTop, modulatedValueWidth - 1, sliderHeight - 2, faderColor);

            final String text = send.text ();
            if (send.edited ())
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
        final ColorEx backgroundDarker = this.modifyIfOff (configuration.getColorBackgroundDarker ());
        for (final SendData element: this.sendData)
        {
            topy += sendRowHeight;

            final String text = element.text ();
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


    protected ColorEx modifyIfOff (final ColorEx color)
    {
        return this.isSendActive ? color : ColorEx.dimToGray (color);
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = super.hashCode ();
        result = prime * result + (this.isExMode ? 1231 : 1237);
        result = prime * result + (this.isSendActive ? 1231 : 1237);
        result = prime * result + Arrays.hashCode (this.sendData);
        return result;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals (obj) || this.getClass () != obj.getClass ())
            return false;
        final SendsComponent other = (SendsComponent) obj;
        if (this.isExMode != other.isExMode || this.isSendActive != other.isSendActive)
            return false;
        return Arrays.equals (this.sendData, other.sendData);
    }
}
