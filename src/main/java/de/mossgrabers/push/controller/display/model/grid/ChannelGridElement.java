package de.mossgrabers.push.controller.display.model.grid;

import de.mossgrabers.push.controller.display.model.ChannelType;
import de.mossgrabers.push.controller.display.model.LayoutSettings;

import com.bitwig.extension.api.GraphicsOutput;

import java.awt.Color;
import java.awt.Label;
import java.io.IOException;


/**
 * An element in the grid which contains the channel settings: Volume, VU, Pan, Mute, Solo and Arm.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
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
    public void draw (final GraphicsOutput gc, final double left, final double width, final double height, final LayoutSettings layoutSettings) throws IOException
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

        final Color textColor = layoutSettings.getTextColor ();
        this.drawMenu (gc, left, width, layoutSettings);

        final String name = this.getName ();
        // Element is off if the name is empty
        if (name == null || name.length () == 0)
            return;

        final Color backgroundColor = layoutSettings.getBackgroundColor ();
        this.drawTrackInfo (gc, left, width, height, trackRowTop, name, layoutSettings);

        // Draw the background
        setColor (gc, this.isSelected () ? backgroundColor.brighter () : backgroundColor);
        gc.rectangle (left, MENU_HEIGHT + 1, width, trackRowTop - (MENU_HEIGHT + 1));
        gc.fill ();

        // Background of pan and slider area
        final Color borderColor = layoutSettings.getBorderColor ();
        setColor (gc, borderColor);
        gc.rectangle (controlStart, CONTROLS_TOP, halfWidth - UNIT + HALF_UNIT / 2 + 1, UNIT);
        gc.rectangle (controlStart, faderTop, controlWidth, faderHeight);
        gc.fill ();

        final Color backgroundDarker = backgroundColor.darker ();
        final Color editColor = layoutSettings.getEditColor ();

        final ChannelType type = this.getType ();
        if (type != ChannelType.MASTER && type != ChannelType.LAYER && this.crossfadeMode != -1)
        {
            // Crossfader A|B
            // TODO
            // final double crossWidth = controlWidth / 3;
            // final Color selColor = this.editType == EDIT_TYPE_CROSSFADER || this.editType ==
            // EDIT_TYPE_ALL ? editColor : textColor;
            // final BufferedImage crossfaderAIcon = SVGImage.getSVGImage
            // ("/images/track/crossfade_a.svg", this.crossfadeMode == 0 ? selColor :
            // backgroundDarker);
            // gc.drawImage (crossfaderAIcon, left + INSET + (crossWidth - crossfaderAIcon.getWidth
            // ()) / 2, CONTROLS_TOP + (panHeight - crossfaderAIcon.getHeight ()) / 2, null);
            // final BufferedImage crossfaderABIcon = SVGImage.getSVGImage
            // ("/images/track/crossfade_ab.svg", this.crossfadeMode == 1 ? selColor :
            // backgroundDarker);
            // gc.drawImage (crossfaderABIcon, crossWidth + left + INSET + (crossWidth -
            // crossfaderAIcon.getWidth ()) / 2, CONTROLS_TOP + (panHeight -
            // crossfaderAIcon.getHeight ()) / 2, null);
            // final BufferedImage crossfaderBIcon = SVGImage.getSVGImage
            // ("/images/track/crossfade_b.svg", this.crossfadeMode == 2 ? selColor :
            // backgroundDarker);
            // gc.drawImage (crossfaderBIcon, 2 * crossWidth + left + INSET + (crossWidth -
            // crossfaderAIcon.getWidth ()) / 2, CONTROLS_TOP + (panHeight -
            // crossfaderAIcon.getHeight ()) / 2, null);
        }

        // Panorama
        setColor (gc, backgroundDarker);
        gc.rectangle (panStart, panTop, panWidth, panHeight);
        gc.fill ();
        setColor (gc, borderColor);
        final double panRange = panWidth / 2;
        final double panMiddle = panStart + panRange;
        gc.moveTo (panMiddle, panTop);
        gc.lineTo (panMiddle, panTop + panHeight);
        gc.stroke ();
        final double maxValue = getMaxValue ();
        final double halfMax = maxValue / 2;
        final Color faderColor = layoutSettings.getFaderColor ();
        setColor (gc, faderColor);
        final boolean isPanTouched = this.panText.length () > 0;

        // Panned to the left or right?
        final boolean isRight = this.panValue > halfMax;
        final boolean isModulatedRight = this.modulatedPanValue > halfMax;
        final double v = isRight ? (this.panValue - halfMax) * panRange / halfMax : panRange - this.panValue * panRange / halfMax;
        final boolean isPanModulated = this.modulatedPanValue != 16383; // == -1
        final double vMod = isPanModulated ? (isModulatedRight ? (this.modulatedPanValue - halfMax) * panRange / halfMax : panRange - this.modulatedPanValue * panRange / halfMax) : v;
        gc.rectangle ((isPanModulated ? isModulatedRight : isRight) ? panMiddle + 1 : panMiddle - vMod, CONTROLS_TOP + 1, vMod, panHeight);
        gc.fill ();
        if (this.editType == EDIT_TYPE_PAN || this.editType == EDIT_TYPE_ALL)
        {
            setColor (gc, editColor);
            final double w = isPanTouched ? 3 : 1;
            final double start = isRight ? Math.min (panMiddle + panRange - w, panMiddle + v) : Math.max (panMiddle - panRange, panMiddle - v);
            gc.rectangle (start, CONTROLS_TOP + 1, w, panHeight);
            gc.fill ();
        }

        // Volume slider
        // Ensure that maximum value is reached even if rounding errors happen
        final double volumeWidth = controlWidth - 2 * SEPARATOR_SIZE - faderOffset;
        final double volumeHeight = (double) (this.volumeValue >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.volumeValue / maxValue);
        final boolean isVolumeModulated = this.modulatedVolumeValue != 16383; // == -1
        final double modulatedVolumeHeight = isVolumeModulated ? (double) (this.modulatedVolumeValue >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.modulatedVolumeValue / maxValue) : volumeHeight;
        final double volumeTop = faderTop + SEPARATOR_SIZE + faderInnerHeight - volumeHeight;
        final double modulatedVolumeTop = isVolumeModulated ? faderTop + SEPARATOR_SIZE + faderInnerHeight - modulatedVolumeHeight : volumeTop;
        setColor (gc, faderColor);
        gc.rectangle (faderLeft, modulatedVolumeTop, volumeWidth, modulatedVolumeHeight);
        gc.fill ();
        final boolean isVolumeTouched = this.volumeText.length () > 0;
        if (this.editType == EDIT_TYPE_VOLUME || this.editType == EDIT_TYPE_ALL)
        {
            setColor (gc, editColor);
            final double h = isVolumeTouched ? 3 : 1;
            gc.rectangle (faderLeft, Math.min (volumeTop + volumeHeight - h, volumeTop), volumeWidth, h);
            gc.fill ();
        }

        // VU
        setColor (gc, backgroundDarker);
        final double vuHeight = (double) (this.vuValue >= maxValue - 1 ? faderInnerHeight : faderInnerHeight * this.vuValue / maxValue);
        final double vuOffset = faderInnerHeight - vuHeight;
        gc.rectangle (controlStart + SEPARATOR_SIZE, faderTop + SEPARATOR_SIZE, faderOffset - SEPARATOR_SIZE, faderInnerHeight);
        gc.fill ();
        setColor (gc, layoutSettings.getVuColor ());
        gc.rectangle (controlStart + SEPARATOR_SIZE, faderTop + SEPARATOR_SIZE + vuOffset, faderOffset - SEPARATOR_SIZE, vuHeight);
        gc.fill ();

        double buttonTop = faderTop;

        if (type != ChannelType.LAYER)
        {
            // Rec Arm
            drawButton (gc, left + INSET - 1, buttonTop, controlWidth - 1, buttonHeight - 1, backgroundColor, Color.RED, textColor, this.isArm, "/images/channel/record_arm.svg", layoutSettings);
        }

        // Solo
        buttonTop += buttonHeight + 2 * SEPARATOR_SIZE;
        drawButton (gc, left + INSET - 1, buttonTop, controlWidth - 1, buttonHeight - 1, backgroundColor, Color.YELLOW, textColor, this.isSolo, "/images/channel/solo.svg", layoutSettings);

        // Mute
        buttonTop += buttonHeight + 2 * SEPARATOR_SIZE;
        drawButton (gc, left + INSET - 1, buttonTop, controlWidth - 1, buttonHeight - 1, backgroundColor, new Color (245, 129, 17), textColor, this.isMute, "/images/channel/mute.svg", layoutSettings);

        // Draw panorama text on top if set
        if (isPanTouched)
        {
            setColor (gc, backgroundDarker);
            gc.rectangle (controlStart, panTextTop, controlWidth, UNIT);
            gc.fill ();
            setColor (gc, borderColor);
            gc.rectangle (controlStart, panTextTop, controlWidth - 1, UNIT);
            gc.stroke ();
            // TODO gc.setFont (layoutSettings.getTextFont (UNIT));
            setColor (gc, textColor);
            drawTextInBounds (gc, this.panText, controlStart, panTextTop, controlWidth, UNIT, Label.CENTER);
        }

        // Draw volume text on top if set
        if (isVolumeTouched)
        {
            final double volumeTextTop = this.volumeValue >= maxValue - 1 ? faderTop : Math.min (volumeTop - 1, faderTop + faderInnerHeight + SEPARATOR_SIZE - UNIT + 1);
            setColor (gc, backgroundDarker);
            gc.rectangle (volumeTextLeft, volumeTextTop, volumeTextWidth, UNIT);
            gc.fill ();
            setColor (gc, borderColor);
            gc.rectangle (volumeTextLeft, volumeTextTop, volumeTextWidth - 1, UNIT);
            gc.stroke ();
            // TODO gc.setFont (layoutSettings.getTextFont (UNIT));
            setColor (gc, textColor);
            drawTextInBounds (gc, this.volumeText, volumeTextLeft, volumeTextTop, volumeTextWidth, UNIT, Label.CENTER);
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
     * @param layoutSettings The layout settings
     * @throws IOException Could not load a SVG image
     */
    private static void drawButton (final GraphicsOutput gc, final double left, final double top, final double width, final double height, final Color backgroundColor, final Color isOnColor, final Color textColor, final boolean isOn, final String iconName, final LayoutSettings layoutSettings) throws IOException
    {
        final Color borderColor = layoutSettings.getBorderColor ();

        // setColor (gc, borderColor);
        // gc.drawRoundRect (left, top, width, height, 5, 5);
        //
        // if (isOn)
        // {
        // setColor (gc, isOnColor);
        // gc.fillRoundRect (left + 1, top + 1, width - 1, height - 1, 5, 5);
        // }
        // else
        // {
        // final Color brighter = backgroundColor.brighter ();
        // setColor (gc, brighter.brighter ());
        // gc.drawRoundRect (left + 1, top + 1, width - 2, height - 2, 5, 5);
        //
        // final Paint oldPaint = gc.getPaint ();
        // final GradientPaint gp = new GradientPaint (left, top + 1, backgroundColor, left, top +
        // height, brighter);
        // gc.setPaint (gp);
        // gc.fillRoundRect (left + 2, top + 2, width - 2, height - 2, 5, 5);
        // gc.setPaint (oldPaint);
        // }

        // TODO
        // final BufferedImage icon = SVGImage.getSVGImage (iconName, isOn ? borderColor :
        // textColor);
        // gc.drawImage (icon, left + (width - icon.getWidth ()) / 2, top + (height - icon.getHeight
        // ()) / 2, null);
    }
}
