// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.controller.display.model.grid;

import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.daw.resource.ResourceHandler;
import de.mossgrabers.push.PushConfiguration;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.api.GradientPattern;
import com.bitwig.extension.api.GraphicsOutput;
import com.bitwig.extension.api.Image;
import com.bitwig.extension.api.Pattern;


/**
 * An element in the grid which contains the channel settings: Volume, VU, Pan, Mute, Solo and Arm.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ChannelGridElement extends ChannelSelectionGridElement
{
    /** Edit volume. */
    public static final int EDIT_TYPE_VOLUME     = 0;
    /** Edit panorma. */
    public static final int EDIT_TYPE_PAN        = 1;
    /** Edit crossfader setting. */
    public static final int EDIT_TYPE_CROSSFADER = 2;
    /** Edit all settings. */
    public static final int EDIT_TYPE_ALL        = 3;

    private final double    editType;
    private final double    volumeValue;
    private final double    modulatedVolumeValue;
    private final String    volumeText;
    private final double    panValue;
    private final double    modulatedPanValue;
    private final String    panText;
    private final double    vuValue;
    private final boolean   isMute;
    private final boolean   isSolo;
    private final boolean   isArm;
    private final double    crossfadeMode;


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
     * @param vuValue The value of the VU
     * @param isMute True if muted
     * @param isSolo True if soloed
     * @param isArm True if recording is armed
     * @param crossfadeMode The crossfader mode: 0 = A, 1 = AB, B = 2, -1 turns it off
     */
    public ChannelGridElement (final double editType, final String menuName, final boolean isMenuSelected, final String name, final Color color, final boolean isSelected, final ChannelType type, final double volumeValue, final double modulatedVolumeValue, final String volumeText, final double panValue, final double modulatedPanValue, final String panText, final double vuValue, final boolean isMute, final boolean isSolo, final boolean isArm, final double crossfadeMode)
    {
        super (menuName, isMenuSelected, name, color, isSelected, type);

        this.editType = editType;
        this.volumeValue = volumeValue;
        this.modulatedVolumeValue = modulatedVolumeValue;
        this.volumeText = volumeText;
        this.panValue = panValue;
        this.modulatedPanValue = modulatedPanValue;
        this.panText = panText;
        this.vuValue = vuValue;
        this.isMute = isMute;
        this.isSolo = isSolo;
        this.isArm = isArm;
        this.crossfadeMode = crossfadeMode;
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final GraphicsOutput gc, final double left, final double width, final double height, final PushConfiguration configuration)
    {
        final double halfWidth = width / 2;

        final double trackRowTop = height - TRACK_ROW_HEIGHT - UNIT - SEPARATOR_SIZE;

        final double controlWidth = halfWidth - HALF_UNIT - HALF_UNIT / 2;
        final double controlStart = left + halfWidth + HALF_UNIT - HALF_UNIT / 2;

        final double panWidth = controlWidth - 2;
        final double panStart = controlStart + 1;
        final double panTop = CONTROLS_TOP + 1.0;
        final double panHeight = UNIT - SEPARATOR_SIZE;
        final double panTextTop = panTop + panHeight;

        final double faderOffset = controlWidth / 4;
        final double faderTop = panTop + panHeight + SEPARATOR_SIZE + 1;
        final double faderLeft = controlStart + SEPARATOR_SIZE + faderOffset;
        final double faderHeight = trackRowTop - faderTop - INSET + 1;
        final double faderInnerHeight = faderHeight - 2 * SEPARATOR_SIZE;

        final double volumeTextWidth = 1.4 * controlWidth;
        final double volumeTextLeft = faderLeft - volumeTextWidth - 2;

        final double buttonHeight = (faderHeight - 4 * SEPARATOR_SIZE) / 3;

        //
        // Drawing
        //

        final Color textColor = configuration.getColorText ();
        this.drawMenu (gc, left, width, configuration);

        final String name = this.getName ();
        // Element is off if the name is empty
        if (name == null || name.length () == 0)
            return;

        final Color backgroundColor = configuration.getColorBackground ();
        this.drawTrackInfo (gc, left, width, height, trackRowTop, name, configuration);

        // Draw the background
        gc.setColor (this.isSelected () ? configuration.getColorBackgroundLighter () : backgroundColor);
        gc.rectangle (left, MENU_HEIGHT + 1, width, trackRowTop - (MENU_HEIGHT + 1));
        gc.fill ();

        // Background of pan and slider area
        final Color borderColor = configuration.getColorBorder ();
        gc.setColor (borderColor);
        gc.rectangle (controlStart, CONTROLS_TOP, halfWidth - UNIT + HALF_UNIT / 2, UNIT);
        gc.rectangle (controlStart, faderTop, controlWidth, faderHeight);
        gc.fill ();

        final Color backgroundDarker = configuration.getColorBackgroundDarker ();
        final Color editColor = configuration.getColorEdit ();

        final ChannelType type = this.getType ();
        if (type != ChannelType.MASTER && type != ChannelType.LAYER && this.crossfadeMode != -1)
        {
            // Crossfader A|B
            final double crossWidth = controlWidth / 3;
            final Color selColor = this.editType == EDIT_TYPE_CROSSFADER || this.editType == EDIT_TYPE_ALL ? editColor : textColor;
            gc.setColor (this.crossfadeMode == 0 ? selColor : backgroundDarker);
            final Image crossfaderAIcon = ResourceHandler.getSVGImage ("track/crossfade_a.svg");
            gc.mask (crossfaderAIcon, left + INSET + (crossWidth - crossfaderAIcon.getWidth ()) / 2, CONTROLS_TOP + (panHeight - crossfaderAIcon.getHeight ()) / 2);
            gc.fill ();
            gc.setColor (this.crossfadeMode == 1 ? selColor : backgroundDarker);
            final Image crossfaderABIcon = ResourceHandler.getSVGImage ("track/crossfade_ab.svg");
            gc.mask (crossfaderABIcon, crossWidth + left + INSET + (crossWidth - crossfaderAIcon.getWidth ()) / 2, CONTROLS_TOP + (panHeight - crossfaderAIcon.getHeight ()) / 2);
            gc.fill ();
            gc.setColor (this.crossfadeMode == 2 ? selColor : backgroundDarker);
            final Image crossfaderBIcon = ResourceHandler.getSVGImage ("track/crossfade_b.svg");
            gc.mask (crossfaderBIcon, 2 * crossWidth + left + INSET + (crossWidth - crossfaderAIcon.getWidth ()) / 2, CONTROLS_TOP + (panHeight - crossfaderAIcon.getHeight ()) / 2);
            gc.fill ();
        }

        // Panorama
        gc.setColor (backgroundDarker);
        gc.rectangle (panStart, panTop, panWidth, panHeight);
        gc.fill ();
        gc.setColor (borderColor);
        final double panRange = panWidth / 2;
        final double panMiddle = panStart + panRange;
        gc.moveTo (panMiddle, panTop);
        gc.lineTo (panMiddle, panTop + panHeight);
        gc.stroke ();
        final double maxValue = getMaxValue ();
        final double halfMax = maxValue / 2;
        final Color faderColor = configuration.getColorFader ();
        gc.setColor (faderColor);
        final boolean isPanTouched = this.panText.length () > 0;

        // Panned to the left or right?
        final boolean isRight = this.panValue > halfMax;
        final boolean isModulatedRight = this.modulatedPanValue > halfMax;
        final double v = isRight ? (this.panValue - halfMax) * panRange / halfMax : panRange - this.panValue * panRange / halfMax;
        final boolean isPanModulated = this.modulatedPanValue != 16383; // == -1
        final double vMod = isPanModulated ? isModulatedRight ? (this.modulatedPanValue - halfMax) * panRange / halfMax : panRange - this.modulatedPanValue * panRange / halfMax : v;
        gc.rectangle ((isPanModulated ? isModulatedRight : isRight) ? panMiddle + 1 : panMiddle - vMod, CONTROLS_TOP + 1, vMod, panHeight);
        gc.fill ();
        if (this.editType == EDIT_TYPE_PAN || this.editType == EDIT_TYPE_ALL)
        {
            gc.setColor (editColor);
            final double w = isPanTouched ? 3 : 1;
            final double start = isRight ? Math.min (panMiddle + panRange - w, panMiddle + v) : Math.max (panMiddle - panRange, panMiddle - v);
            gc.rectangle (start, CONTROLS_TOP + 1, w, panHeight);
            gc.fill ();
        }

        // Volume slider
        // Ensure that maximum value is reached even if rounding errors happen
        final double volumeWidth = controlWidth - 2 * SEPARATOR_SIZE - faderOffset;
        final double volumeHeight = this.volumeValue >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.volumeValue / maxValue;
        final boolean isVolumeModulated = this.modulatedVolumeValue != 16383; // == -1
        final double modulatedVolumeHeight = isVolumeModulated ? (double) (this.modulatedVolumeValue >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.modulatedVolumeValue / maxValue) : volumeHeight;
        final double volumeTop = faderTop + SEPARATOR_SIZE + faderInnerHeight - volumeHeight;
        final double modulatedVolumeTop = isVolumeModulated ? faderTop + SEPARATOR_SIZE + faderInnerHeight - modulatedVolumeHeight : volumeTop;
        gc.setColor (faderColor);
        gc.rectangle (faderLeft, modulatedVolumeTop, volumeWidth, modulatedVolumeHeight);
        gc.fill ();
        final boolean isVolumeTouched = this.volumeText.length () > 0;
        if (this.editType == EDIT_TYPE_VOLUME || this.editType == EDIT_TYPE_ALL)
        {
            gc.setColor (editColor);
            final double h = isVolumeTouched ? 3 : 1;
            gc.rectangle (faderLeft, Math.min (volumeTop + volumeHeight - h, volumeTop), volumeWidth, h);
            gc.fill ();
        }

        // VU
        gc.setColor (backgroundDarker);
        final double vuHeight = this.vuValue >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.vuValue / maxValue;
        final double vuOffset = faderInnerHeight - vuHeight;
        gc.rectangle (controlStart + SEPARATOR_SIZE, faderTop + SEPARATOR_SIZE, faderOffset - SEPARATOR_SIZE, faderInnerHeight);
        gc.fill ();
        gc.setColor (configuration.getColorVu ());
        gc.rectangle (controlStart + SEPARATOR_SIZE, faderTop + SEPARATOR_SIZE + vuOffset, faderOffset - SEPARATOR_SIZE, vuHeight);
        gc.fill ();

        double buttonTop = faderTop;

        if (type != ChannelType.LAYER)
        {
            // Rec Arm
            drawButton (gc, left + INSET - 1, buttonTop, controlWidth - 1, buttonHeight - 1, backgroundColor, configuration.getColorRecord (), textColor, this.isArm, "channel/record_arm.svg", configuration);
        }

        // Solo
        buttonTop += buttonHeight + 2 * SEPARATOR_SIZE;
        drawButton (gc, left + INSET - 1, buttonTop, controlWidth - 1, buttonHeight - 1, backgroundColor, configuration.getColorSolo (), textColor, this.isSolo, "channel/solo.svg", configuration);

        // Mute
        buttonTop += buttonHeight + 2 * SEPARATOR_SIZE;
        drawButton (gc, left + INSET - 1, buttonTop, controlWidth - 1, buttonHeight - 1, backgroundColor, configuration.getColorMute (), textColor, this.isMute, "channel/mute.svg", configuration);

        // Draw panorama text on top if set
        if (isPanTouched)
        {
            gc.setColor (backgroundDarker);
            gc.rectangle (controlStart, panTextTop, controlWidth, UNIT);
            gc.fill ();
            gc.setColor (borderColor);
            gc.rectangle (controlStart, panTextTop, controlWidth - 1, UNIT);
            gc.stroke ();
            gc.setFontSize (UNIT);
            drawTextInBounds (gc, this.panText, controlStart, panTextTop, controlWidth, UNIT, Align.CENTER, textColor);
        }

        // Draw volume text on top if set
        if (isVolumeTouched)
        {
            final double volumeTextTop = this.volumeValue >= maxValue - 1 ? faderTop : Math.min (volumeTop - 1, faderTop + faderInnerHeight + SEPARATOR_SIZE - UNIT + 1);
            gc.setColor (backgroundDarker);
            gc.rectangle (volumeTextLeft, volumeTextTop, volumeTextWidth, UNIT);
            gc.fill ();
            gc.setColor (borderColor);
            gc.rectangle (volumeTextLeft, volumeTextTop, volumeTextWidth - 1, UNIT);
            gc.stroke ();
            gc.setFontSize (UNIT);
            drawTextInBounds (gc, this.volumeText, volumeTextLeft, volumeTextTop, volumeTextWidth, UNIT, Align.CENTER, textColor);
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
    private static void drawButton (final GraphicsOutput gc, final double left, final double top, final double width, final double height, final Color backgroundColor, final Color isOnColor, final Color textColor, final boolean isOn, final String iconName, final PushConfiguration configuration)
    {
        final Color borderColor = configuration.getColorBorder ();

        drawRoundedRect (gc, left, top, width, height, 5.0, borderColor);

        if (isOn)
            drawFilledRoundedRect (gc, left + 1, top + 1, width - 2, height - 2, 5.0, isOnColor);
        else
        {
            final Color brighter = ColorEx.brighter (backgroundColor);
            final GradientPattern linearGradient = gc.createLinearGradient (left, top + 1, left, top + height);
            linearGradient.addColorStop (0, backgroundColor.getRed (), backgroundColor.getGreen (), backgroundColor.getBlue ());
            linearGradient.addColorStop (1, brighter.getRed (), brighter.getGreen (), brighter.getBlue ());
            drawPatternFilledRoundedRect (gc, left + 1, top + 1, width - 2, height - 2, 5, linearGradient);
        }

        gc.setColor (isOn ? borderColor : textColor);
        final Image icon = ResourceHandler.getSVGImage (iconName);
        gc.mask (icon, left + (width - icon.getWidth ()) / 2, top + (height - icon.getHeight ()) / 2);
        gc.fill ();
    }


    private static void drawRoundedRect (final GraphicsOutput gc, final double left, final double top, final double width, final double height, final double radius, final Color backgroundColor)
    {
        gc.setColor (backgroundColor);
        drawRoundedRectInternal (gc, left, top, width, height, radius);
        gc.fill ();
    }


    private static void drawFilledRoundedRect (final GraphicsOutput gc, final double left, final double top, final double width, final double height, final double radius, final Color backgroundColor)
    {
        gc.setColor (backgroundColor);
        drawRoundedRectInternal (gc, left, top, width, height, radius);
        gc.fill ();
    }


    private static void drawPatternFilledRoundedRect (final GraphicsOutput gc, final double left, final double top, final double width, final double height, final double radius, final Pattern pattern)
    {
        gc.setPattern (pattern);
        drawRoundedRectInternal (gc, left, top, width, height, radius);
        gc.fill ();
    }


    private static void drawRoundedRectInternal (final GraphicsOutput gc, final double left, final double top, final double width, final double height, final double radius)
    {
        final double degrees = Math.PI / 180.0;
        gc.newSubPath ();
        gc.arc (left + width - radius, top + radius, radius, -90 * degrees, 0 * degrees);
        gc.arc (left + width - radius, top + height - radius, radius, 0 * degrees, 90 * degrees);
        gc.arc (left + radius, top + height - radius, radius, 90 * degrees, 180 * degrees);
        gc.arc (left + radius, top + radius, radius, 180 * degrees, 270 * degrees);
        gc.closePath ();
    }
}
