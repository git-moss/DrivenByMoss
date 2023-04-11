// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.daw.resource.ResourceHandler;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;
import de.mossgrabers.framework.graphics.IGraphicsContext;
import de.mossgrabers.framework.graphics.IGraphicsDimensions;
import de.mossgrabers.framework.graphics.IGraphicsInfo;
import de.mossgrabers.framework.graphics.IImage;


/**
 * An element in the grid which contains the channel settings: Volume, VU, Pan, Mute, Solo and Arm.
 *
 * @author Jürgen Moßgraber
 */
public class ChannelComponent extends ChannelSelectComponent
{
    /** Edit volume. */
    public static final int   EDIT_TYPE_VOLUME     = 0;
    /** Edit panorama. */
    public static final int   EDIT_TYPE_PAN        = 1;
    /** Edit cross-fader setting. */
    public static final int   EDIT_TYPE_CROSSFADER = 2;
    /** Edit all settings. */
    public static final int   EDIT_TYPE_ALL        = 3;

    private final double      editType;
    private final double      volumeValue;
    private final double      modulatedVolumeValue;
    private final String      volumeText;
    private final double      panValue;
    private final double      modulatedPanValue;
    private final String      panText;
    private final double      vuValueLeft;
    private final double      vuValueRight;
    private final boolean     isMute;
    private final boolean     isSolo;
    private final boolean     isArm;
    private final double      crossfadeMode;

    private final boolean     isActive;
    private final ChannelType type;


    /**
     * Constructor.
     *
     * @param editType What to edit, 0 = Volume, 1 = Pan, 2 = Crossfade Mode
     * @param menuName The text for the menu
     * @param isMenuSelected True if the menu is selected
     * @param name The of the grid element (track name, parameter name, etc.)
     * @param color The color to use for the header, may be null
     * @param isSelected True if the grid element is selected
     * @param type The type of the track
     * @param volumeValue The value of the volume
     * @param modulatedVolumeValue The modulated value of the volume, -1 if not modulated
     * @param volumeText The textual form of the volumes value
     * @param panValue The value of the panorama
     * @param modulatedPanValue The modulated value of the panorama, -1 if not modulated
     * @param panText The textual form of the panorama
     * @param vuValueLeft The value of the VU of the left channel
     * @param vuValueRight The value of the VU of the right channel
     * @param isMute True if muted
     * @param isSolo True if soloed
     * @param isArm True if recording is armed
     * @param isActive True if channel is activated
     * @param crossfadeMode The cross-fader mode: 0 = A, 1 = AB, B = 2, -1 turns it off
     * @param isPinned True if the channel is pinned
     */
    public ChannelComponent (final double editType, final String menuName, final boolean isMenuSelected, final String name, final ColorEx color, final boolean isSelected, final ChannelType type, final double volumeValue, final double modulatedVolumeValue, final String volumeText, final double panValue, final double modulatedPanValue, final String panText, final double vuValueLeft, final double vuValueRight, final boolean isMute, final boolean isSolo, final boolean isArm, final boolean isActive, final double crossfadeMode, final boolean isPinned)
    {
        super (type, menuName, isMenuSelected, name, color, isSelected, isActive, isPinned);

        this.type = type;
        this.isActive = isActive;

        this.editType = editType;
        this.volumeValue = volumeValue;
        this.modulatedVolumeValue = modulatedVolumeValue;
        this.volumeText = volumeText;
        this.panValue = panValue;
        this.modulatedPanValue = modulatedPanValue;
        this.panText = panText;
        this.vuValueLeft = vuValueLeft;
        this.vuValueRight = vuValueRight;
        this.isMute = isMute;
        this.isSolo = isSolo;
        this.isArm = isArm;
        this.crossfadeMode = crossfadeMode;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsInfo info)
    {
        super.draw (info);

        final IGraphicsContext gc = info.getContext ();
        final IGraphicsDimensions dimensions = info.getDimensions ();
        final IGraphicsConfiguration configuration = info.getConfiguration ();
        final double left = info.getBounds ().left ();
        final double width = info.getBounds ().width ();
        final double height = info.getBounds ().height ();

        final double halfWidth = width / 2;

        final double separatorSize = dimensions.getSeparatorSize ();
        final double menuHeight = dimensions.getMenuHeight ();
        final double unit = dimensions.getUnit ();
        final double halfUnit = dimensions.getHalfUnit ();
        final double controlsTop = dimensions.getControlsTop ();
        final double inset = dimensions.getInset ();

        final int trackRowHeight = (int) (1.6 * unit);
        final double trackRowTop = height - trackRowHeight - unit - separatorSize;

        final double controlWidth = halfWidth - halfUnit - halfUnit / 2;
        final double controlStart = left + halfWidth + halfUnit - halfUnit / 2;

        final double panWidth = controlWidth - 2;
        final double panStart = controlStart + 1;
        final double panTop = controlsTop + 1.0;
        final double panHeight = unit - separatorSize;
        final double panTextTop = panTop + panHeight;

        final double faderOffset = controlWidth / 4;
        final double faderTop = panTop + panHeight + separatorSize + 1;
        final double vuX = controlStart + separatorSize;
        final double faderLeft = vuX + faderOffset;
        final double faderHeight = trackRowTop - faderTop - inset + 1;
        final double faderInnerHeight = faderHeight - 2 * separatorSize;

        final double volumeTextWidth = 1.4 * controlWidth;
        final double volumeTextLeft = faderLeft - volumeTextWidth - 2;

        final double buttonHeight = (faderHeight - 4 * separatorSize) / 3;

        //
        // Drawing
        //

        final ColorEx textColor = this.modifyIfOff (configuration.getColorText ());

        final String name = this.footer.getText ();
        // Element is off if the name is empty
        if (name == null || name.length () == 0)
            return;

        final ColorEx backgroundColor = this.modifyIfOff (configuration.getColorBackground ());

        // Draw the background
        gc.fillRectangle (left, menuHeight + 1, width, trackRowTop - (menuHeight + 1), this.footer.isSelected () ? this.modifyIfOff (configuration.getColorBackgroundLighter ()) : backgroundColor);

        // Background of pan and slider area
        final ColorEx borderColor = this.modifyIfOff (configuration.getColorBorder ());
        gc.fillRectangle (controlStart, controlsTop, halfWidth - unit + halfUnit / 2, unit, borderColor);
        gc.fillRectangle (controlStart, faderTop, controlWidth, faderHeight, borderColor);

        final ColorEx backgroundDarker = this.modifyIfOff (configuration.getColorBackgroundDarker ());
        final ColorEx editColor = this.modifyIfOff (configuration.getColorEdit ());

        // Crossfader A|B
        final double leftColumn = left + inset - 1;
        if (this.type != ChannelType.MASTER && this.type != ChannelType.LAYER && this.crossfadeMode != -1)
        {
            final ColorEx selColor = this.editType == EDIT_TYPE_CROSSFADER || this.editType == EDIT_TYPE_ALL ? editColor : ColorEx.ORANGE;
            final double crossOptWidth = controlWidth / 3.0;
            this.drawButton (gc, leftColumn, controlsTop, crossOptWidth, panHeight + 2, backgroundColor, this.modifyIfOff (selColor), textColor, this.crossfadeMode == 0, "track/crossfade_a.svg", configuration, 0);
            this.drawButton (gc, leftColumn + crossOptWidth, controlsTop, crossOptWidth, panHeight + 2, backgroundColor, this.modifyIfOff (selColor), textColor, this.crossfadeMode == 1, "track/crossfade_ab.svg", configuration, 0);
            this.drawButton (gc, leftColumn + 2 * crossOptWidth, controlsTop, crossOptWidth, panHeight + 2, backgroundColor, this.modifyIfOff (selColor), textColor, this.crossfadeMode == 2, "track/crossfade_b.svg", configuration, 0);
        }

        // Panorama
        gc.fillRectangle (panStart, panTop, panWidth, panHeight, backgroundDarker);

        final double panRange = panWidth / 2;
        final double panMiddle = panStart + panRange;

        gc.drawLine (panMiddle, panTop, panMiddle, panTop + panHeight, borderColor);

        final double maxValue = dimensions.getParameterUpperBound ();
        final double halfMax = maxValue / 2;
        final boolean isPanTouched = this.panText.length () > 0;

        // Panned to the left or right?
        final boolean isRight = this.panValue > halfMax;
        final boolean isModulatedRight = this.modulatedPanValue > halfMax;
        final double v = isRight ? (this.panValue - halfMax) * panRange / halfMax : panRange - this.panValue * panRange / halfMax;
        final boolean isPanModulated = this.modulatedPanValue != -1;
        final double vMod;
        if (isPanModulated)
        {
            if (isModulatedRight)
                vMod = (this.modulatedPanValue - halfMax) * panRange / halfMax;
            else
                vMod = panRange - this.modulatedPanValue * panRange / halfMax;
        }
        else
            vMod = v;

        final ColorEx faderColor = this.modifyIfOff (configuration.getColorFader ());
        final boolean rightMod = isPanModulated ? isModulatedRight : isRight;
        gc.fillRectangle (rightMod ? panMiddle + 1 : panMiddle - vMod, controlsTop + 1, vMod, panHeight, faderColor);

        if (this.editType == EDIT_TYPE_PAN || this.editType == EDIT_TYPE_ALL)
        {
            final double w = isPanTouched ? 3 : 1;
            final double start = isRight ? Math.min (panMiddle + panRange - w, panMiddle + v) : Math.max (panMiddle - panRange, panMiddle - v);
            gc.fillRectangle (start, controlsTop + 1, w, panHeight, editColor);
        }

        // Volume slider
        // Ensure that maximum value is reached even if rounding errors happen
        final double volumeWidth = controlWidth - 2 * separatorSize - faderOffset;
        final double volumeHeight = this.volumeValue >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.volumeValue / maxValue;
        final boolean isVolumeModulated = this.modulatedVolumeValue != -1;
        final double modulatedVolumeHeight;
        if (isVolumeModulated)
        {
            if (this.modulatedVolumeValue >= maxValue - 1)
                modulatedVolumeHeight = faderInnerHeight;
            else
                modulatedVolumeHeight = faderInnerHeight * this.modulatedVolumeValue / maxValue;
        }
        else
            modulatedVolumeHeight = volumeHeight;
        final double volumeTop = faderTop + separatorSize + faderInnerHeight - volumeHeight;
        final double modulatedVolumeTop = isVolumeModulated ? faderTop + separatorSize + faderInnerHeight - modulatedVolumeHeight : volumeTop;

        gc.fillRectangle (faderLeft, modulatedVolumeTop, volumeWidth, modulatedVolumeHeight, faderColor);

        final boolean isVolumeTouched = this.volumeText.length () > 0;
        if (this.editType == EDIT_TYPE_VOLUME || this.editType == EDIT_TYPE_ALL)
        {
            final double h = isVolumeTouched ? 3 : 1;
            gc.fillRectangle (faderLeft, Math.min (volumeTop + volumeHeight - h, volumeTop), volumeWidth, h, editColor);
        }

        // VU
        final double vuHeightLeft = this.vuValueLeft >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.vuValueLeft / maxValue;
        final double vuHeightRight = this.vuValueRight >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.vuValueRight / maxValue;
        final double vuOffsetLeft = faderInnerHeight - vuHeightLeft;
        final double vuOffsetRight = faderInnerHeight - vuHeightRight;
        final double vuWidth = faderOffset - separatorSize;
        gc.fillRectangle (vuX, faderTop + separatorSize, vuWidth + 1, faderInnerHeight, backgroundDarker);
        ColorEx colorVu = this.modifyIfOff (configuration.getColorVu ());
        if (this.isMute)
            colorVu = configuration.getColorMute ();
        gc.fillRectangle (vuX, faderTop + separatorSize + vuOffsetLeft, vuWidth / 2, vuHeightLeft, colorVu);
        gc.fillRectangle (vuX + vuWidth / 2, faderTop + separatorSize + vuOffsetRight, vuWidth / 2, vuHeightRight, colorVu);

        double buttonTop = faderTop;

        if (this.type != ChannelType.LAYER)
        {
            // Record Arm
            this.drawButton (gc, leftColumn, buttonTop, controlWidth, buttonHeight - 1, backgroundColor, this.modifyIfOff (configuration.getColorRecord ()), textColor, this.isArm, "channel/record_arm.svg", configuration);
        }

        // Solo
        buttonTop += buttonHeight + 2 * separatorSize;
        this.drawButton (gc, leftColumn, buttonTop, controlWidth, buttonHeight - 1, backgroundColor, this.modifyIfOff (configuration.getColorSolo ()), textColor, this.isSolo, "channel/solo.svg", configuration);

        // Mute
        buttonTop += buttonHeight + 2 * separatorSize;
        this.drawButton (gc, leftColumn, buttonTop, controlWidth, buttonHeight - 1, backgroundColor, this.modifyIfOff (configuration.getColorMute ()), textColor, this.isMute, "channel/mute.svg", configuration);

        // Draw panorama text on top if set
        if (isPanTouched)
        {
            gc.fillRectangle (controlStart, panTextTop, controlWidth, unit, backgroundDarker);
            gc.strokeRectangle (controlStart, panTextTop, controlWidth, unit, borderColor);
            gc.drawTextInBounds (this.panText, controlStart, panTextTop, controlWidth, unit, Align.CENTER, textColor, unit);
        }

        // Draw volume text on top if set
        if (isVolumeTouched)
        {
            final double volumeTextTop = this.volumeValue >= maxValue - 1 ? faderTop : Math.min (volumeTop - 1, faderTop + faderInnerHeight + separatorSize - unit + 1);
            gc.fillRectangle (volumeTextLeft, volumeTextTop, volumeTextWidth, unit, backgroundDarker);
            gc.strokeRectangle (volumeTextLeft, volumeTextTop, volumeTextWidth, unit, borderColor);
            gc.drawTextInBounds (this.volumeText, volumeTextLeft, volumeTextTop, volumeTextWidth, unit, Align.CENTER, textColor, unit);
        }
    }


    /**
     * Draws a button a gradient background.
     *
     * @param gc The graphics context
     * @param left The left bound of the drawing area
     * @param top The top bound of the drawing area
     * @param width The width of the drawing area
     * @param height The height of the drawing area
     * @param backgroundColor The background color
     * @param isOnColor The color if the button is on
     * @param textColor The color of the buttons text
     * @param isOn True if the button is on
     * @param iconName The name of the buttons icon
     * @param configuration The layout settings
     */
    private void drawButton (final IGraphicsContext gc, final double left, final double top, final double width, final double height, final ColorEx backgroundColor, final ColorEx isOnColor, final ColorEx textColor, final boolean isOn, final String iconName, final IGraphicsConfiguration configuration)
    {
        this.drawButton (gc, left, top, width, height, backgroundColor, isOnColor, textColor, isOn, iconName, configuration, 2.0);
    }


    /**
     * Draws a button a gradient background.
     *
     * @param gc The graphics context
     * @param left The left bound of the drawing area
     * @param top The top bound of the drawing area
     * @param width The width of the drawing area
     * @param height The height of the drawing area
     * @param backgroundColor The background color
     * @param isOnColor The color if the button is on
     * @param textColor The color of the buttons text
     * @param isOn True if the button is on
     * @param iconName The name of the buttons icon
     * @param configuration The layout settings
     * @param radius The radius of the surrounding border rectangle
     */
    private void drawButton (final IGraphicsContext gc, final double left, final double top, final double width, final double height, final ColorEx backgroundColor, final ColorEx isOnColor, final ColorEx textColor, final boolean isOn, final String iconName, final IGraphicsConfiguration configuration, final double radius)
    {
        final ColorEx borderColor = this.modifyIfOff (configuration.getColorBorder ());

        gc.fillRoundedRectangle (left, top, width, height, radius, borderColor);

        if (isOn)
            gc.fillRoundedRectangle (left + 1, top + 1, width - 2, height - 2, radius, isOnColor);
        else
            gc.fillGradientRoundedRectangle (left + 1, top + 1, width - 2, height - 2, radius, backgroundColor, ColorEx.brighter (backgroundColor));

        final IImage icon = ResourceHandler.getSVGImage (iconName);
        gc.maskImage (icon, left + (width - icon.getWidth ()) / 2, top + (height - icon.getHeight ()) / 2, isOn ? borderColor : textColor);
    }


    protected ColorEx modifyIfOff (final ColorEx color)
    {
        return this.isActive ? color : ColorEx.dimToGray (color);
    }


    /** {@inheritDoc} */
    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = super.hashCode ();
        long temp;
        temp = Double.doubleToLongBits (this.crossfadeMode);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits (this.editType);
        result = prime * result + (int) (temp ^ temp >>> 32);
        result = prime * result + (this.isActive ? 1231 : 1237);
        result = prime * result + (this.isArm ? 1231 : 1237);
        result = prime * result + (this.isMute ? 1231 : 1237);
        result = prime * result + (this.isSolo ? 1231 : 1237);
        temp = Double.doubleToLongBits (this.modulatedPanValue);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits (this.modulatedVolumeValue);
        result = prime * result + (int) (temp ^ temp >>> 32);
        result = prime * result + (this.panText == null ? 0 : this.panText.hashCode ());
        temp = Double.doubleToLongBits (this.panValue);
        result = prime * result + (int) (temp ^ temp >>> 32);
        result = prime * result + (this.type == null ? 0 : this.type.hashCode ());
        result = prime * result + (this.volumeText == null ? 0 : this.volumeText.hashCode ());
        temp = Double.doubleToLongBits (this.volumeValue);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits (this.vuValueLeft);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits (this.vuValueRight);
        return prime * result + (int) (temp ^ temp >>> 32);
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals (final Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals (obj) || this.getClass () != obj.getClass ())
            return false;
        final ChannelComponent other = (ChannelComponent) obj;
        if (Double.doubleToLongBits (this.crossfadeMode) != Double.doubleToLongBits (other.crossfadeMode) || Double.doubleToLongBits (this.editType) != Double.doubleToLongBits (other.editType) || this.isActive != other.isActive || this.isArm != other.isArm)
            return false;
        if (this.isMute != other.isMute || this.isSolo != other.isSolo || Double.doubleToLongBits (this.modulatedPanValue) != Double.doubleToLongBits (other.modulatedPanValue) || Double.doubleToLongBits (this.modulatedVolumeValue) != Double.doubleToLongBits (other.modulatedVolumeValue))
            return false;
        if (this.panText == null)
        {
            if (other.panText != null)
                return false;
        }
        else if (!this.panText.equals (other.panText))
            return false;
        if (Double.doubleToLongBits (this.panValue) != Double.doubleToLongBits (other.panValue) || this.type != other.type)
            return false;
        if (this.volumeText == null)
        {
            if (other.volumeText != null)
                return false;
        }
        else if (!this.volumeText.equals (other.volumeText))
            return false;
        if (Double.doubleToLongBits (this.volumeValue) != Double.doubleToLongBits (other.volumeValue) || Double.doubleToLongBits (this.vuValueLeft) != Double.doubleToLongBits (other.vuValueLeft))
            return false;
        return Double.doubleToLongBits (this.vuValueRight) == Double.doubleToLongBits (other.vuValueRight);
    }
}
