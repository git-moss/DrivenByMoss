// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.graphics.IGraphicsConfiguration;


/**
 * The configuration settings for Kontrol 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol2Configuration extends AbstractConfiguration implements IGraphicsConfiguration
{
    // TODO
    private static final Integer SCALE_IS_ACTIVE        = Integer.valueOf (40);
    /** Push 2 display debug window. */
    public static final Integer  DEBUG_WINDOW           = Integer.valueOf (42);

    private IEnumSetting         scaleIsActiveSetting;
    private boolean              scaleIsActive;

    private ColorEx              colorBackground        = DEFAULT_COLOR_BACKGROUND;
    private ColorEx              colorBorder            = DEFAULT_COLOR_BORDER;
    private ColorEx              colorText              = DEFAULT_COLOR_TEXT;
    private ColorEx              colorFader             = DEFAULT_COLOR_FADER;
    private ColorEx              colorVU                = DEFAULT_COLOR_VU;
    private ColorEx              colorEdit              = DEFAULT_COLOR_EDIT;
    private ColorEx              colorRecord            = DEFAULT_COLOR_RECORD;
    private ColorEx              colorSolo              = DEFAULT_COLOR_SOLO;
    private ColorEx              colorMute              = DEFAULT_COLOR_MUTE;
    private ColorEx              colorBackgroundDarker  = DEFAULT_COLOR_BACKGROUND_DARKER;
    private ColorEx              colorBackgroundLighter = DEFAULT_COLOR_BACKGROUND_LIGHTER;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     */
    public Kontrol2Configuration (final IValueChanger valueChanger)
    {
        super (valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        ///////////////////////////
        // Scale

        this.scaleIsActiveSetting = settingsUI.getEnumSetting ("Is active", CATEGORY_SCALES, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        this.scaleIsActiveSetting.addValueObserver (value -> {
            this.scaleIsActive = "On".equals (value);
            this.notifyObservers (SCALE_IS_ACTIVE);
        });

        this.activateScaleSetting (settingsUI);
        this.activateScaleBaseSetting (settingsUI);
        this.activateScaleInScaleSetting (settingsUI);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (settingsUI);
        this.activateFlipRecordSetting (settingsUI);

        ///////////////////////////
        // Workflow

        this.activateEnableVUMetersSetting (settingsUI);

        settingsUI.getSignalSetting (" ", CATEGORY_DEBUG, "Display window").addValueObserver (value -> this.notifyObservers (DEBUG_WINDOW));
    }


    /**
     * True if the scale is active.
     *
     * @return True if the scale is active.
     */
    public boolean isScaleIsActive ()
    {
        return this.scaleIsActive;
    }


    /**
     * Toggle if the scale is active.
     */
    public void toggleScaleIsActive ()
    {
        this.scaleIsActiveSetting.set (ON_OFF_OPTIONS[this.scaleIsActive ? 0 : 1]);
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorBackground ()
    {
        return this.colorBackground;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorBackgroundDarker ()
    {
        return this.colorBackgroundDarker;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorBackgroundLighter ()
    {
        return this.colorBackgroundLighter;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorBorder ()
    {
        return this.colorBorder;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorText ()
    {
        return this.colorText;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorEdit ()
    {
        return this.colorEdit;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorFader ()
    {
        return this.colorFader;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorVu ()
    {
        return this.colorVU;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorRecord ()
    {
        return this.colorRecord;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorSolo ()
    {
        return this.colorSolo;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColorMute ()
    {
        return this.colorMute;
    }
}
