// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.controller;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;


/**
 * Different colors to use for the Electra.One.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneColorManager extends ColorManager
{
    /** State for button LED on. */
    public static final int     COLOR_BUTTON_STATE_ON  = 127;
    /** State for button LED off. */
    public static final int     COLOR_BUTTON_STATE_OFF = 0;

    /** Color when playback is stopped. */
    public static final ColorEx PLAY_OFF               = ColorEx.DARK_GREEN;
    /** Color when playback is active. */
    public static final ColorEx PLAY_ON                = ColorEx.GREEN;
    /** Color when recording is stopped. */
    public static final ColorEx RECORD_OFF             = ColorEx.DARK_RED;
    /** Color when recording is active. */
    public static final ColorEx RECORD_ON              = ColorEx.RED;

    /** Color when track is not armed for recording. */
    public static final ColorEx REC_ARM_OFF            = ColorEx.DARK_GRAY;
    /** Color when track is armed for recording. */
    public static final ColorEx REC_ARM_ON             = ColorEx.DARK_RED;
    /** Color when track is not muted. */
    public static final ColorEx MUTE_OFF               = ColorEx.DARK_GRAY;
    /** Color when track is muted. */
    public static final ColorEx MUTE_ON                = ColorEx.ORANGE;
    /** Color when track is not soloed. */
    public static final ColorEx SOLO_OFF               = ColorEx.DARK_GRAY;
    /** Color when track is soloed. */
    public static final ColorEx SOLO_ON                = ColorEx.YELLOW;
    /** Color when track is not selected. */
    public static final ColorEx SELECT_OFF             = ColorEx.DARK_GRAY;
    /** Color when track is selected. */
    public static final ColorEx SELECT_ON              = ColorEx.CYAN;

    /** Color when device is not pinned. */
    public static final ColorEx PINNED_OFF             = ColorEx.DARK_GRAY;
    /** Color when device is pinned. */
    public static final ColorEx PINNED_ON              = ColorEx.CYAN;
    /** Color when device is not expanded. */
    public static final ColorEx EXPANDED_OFF           = ColorEx.DARK_GRAY;
    /** Color when device is expanded. */
    public static final ColorEx EXPANDED_ON            = ColorEx.MINT;

    /** Color for parameter page. */
    public static final ColorEx PARAM_PAGE             = ColorEx.DARK_BLUE;
    /** Color for selected parameter page. */
    public static final ColorEx PARAM_PAGE_SELECTED    = ColorEx.SKY_BLUE;
    /** Color for device. */
    public static final ColorEx DEVICE                 = ColorEx.DARK_PURPLE;
    /** Color for selected device. */
    public static final ColorEx DEVICE_SELECTED        = ColorEx.PURPLE;
    /** Color for device window. */
    public static final ColorEx WINDOW                 = ColorEx.DARK_GRAY;
    /** Color for device opened window. */
    public static final ColorEx WINDOW_OPEN            = ColorEx.DARK_BROWN;
    /** Color for disabled device. */
    public static final ColorEx DEVICE_OFF             = ColorEx.DARK_GRAY;
    /** Color for enabled device window. */
    public static final ColorEx DEVICE_ON              = ColorEx.DARK_GREEN;

    /** Color for a disabled equalizer band. */
    public static final ColorEx BAND_OFF               = ColorEx.DARK_GRAY;
    /** Color for a enabled equalizer band. */
    public static final ColorEx BAND_ON                = ColorEx.GREEN;

    /** Color for a disabled metronome. */
    public static final ColorEx METRONOME_OFF          = ColorEx.DARK_GRAY;
    /** Color for a enabled metronome. */
    public static final ColorEx METRONOME_ON           = ColorEx.GREEN;
    /** Color for a disabled automation. */
    public static final ColorEx AUTO_OFF               = ColorEx.DARK_GRAY;
    /** Color for a enabled automation. */
    public static final ColorEx AUTO_ON                = ColorEx.RED;
    /** Color for a disabled automation mode. */
    public static final ColorEx AUTO_MODE_OFF          = ColorEx.DARK_GRAY;
    /** Color for a enabled automation mode. */
    public static final ColorEx AUTO_MODE_ON           = ColorEx.ORANGE;
    /** Color for a disabled marker launch. */
    public static final ColorEx MARKER_LAUNCH_OFF      = ColorEx.DARK_GRAY;
    /** Color for a enabled marker launch. */
    public static final ColorEx MARKER_LAUNCH_ON       = ColorEx.GREEN;


    /**
     * Constructor.
     */
    public ElectraOneColorManager ()
    {
        this.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, 0);

        this.registerColorIndex (ColorManager.BUTTON_STATE_OFF, COLOR_BUTTON_STATE_OFF);
        this.registerColorIndex (ColorManager.BUTTON_STATE_ON, COLOR_BUTTON_STATE_ON);
        this.registerColorIndex (ColorManager.BUTTON_STATE_HI, COLOR_BUTTON_STATE_ON);

        for (int i = 0; i < 127; i++)
            this.registerColor (i, ColorEx.BLACK);
        this.registerColor (127, ColorEx.WHITE);
    }
}