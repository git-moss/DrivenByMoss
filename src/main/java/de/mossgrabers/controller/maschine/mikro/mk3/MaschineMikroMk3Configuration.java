// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;


/**
 * The configuration settings for Maschine Mikro Mk3.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineMikroMk3Configuration extends AbstractConfiguration
{
    /** Setting for the ribbon mode. */
    public static final Integer   RIBBON_MODE               = Integer.valueOf (30);

    /** Use ribbon for pitch bend down. */
    public static final int       RIBBON_MODE_PITCH_DOWN    = 0;
    /** Use ribbon for pitch bend up. */
    public static final int       RIBBON_MODE_PITCH_UP      = 1;
    /** Use ribbon for pitch bend down/up. */
    public static final int       RIBBON_MODE_PITCH_DOWN_UP = 2;
    /** Use ribbon for midi CC 1. */
    public static final int       RIBBON_MODE_CC_1          = 3;
    /** Use ribbon for midi CC 11. */
    public static final int       RIBBON_MODE_CC_11         = 4;
    /** Use ribbon for master volume. */
    public static final int       RIBBON_MODE_MASTER_VOLUME = 5;

    /** The ribbon mode names. */
    public static final String [] RIBBON_MODE_VALUES        =
    {
        "Pitch Down",
        "Pitch Up",
        "Pitch Down/Up",
        "Modulation (CC 1)",
        "Expression (CC 11)",
        "Master Volume"
    };

    /** What does the ribbon send? **/
    private int                   ribbonMode                = RIBBON_MODE_PITCH_DOWN;
    private boolean               duplicateEnabled;

    private IEnumSetting          ribbonModeSetting;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     */
    public MaschineMikroMk3Configuration (final IValueChanger valueChanger)
    {
        super (valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        ///////////////////////////
        // Play and Sequence

        this.activateAccentActiveSetting (settingsUI);
        this.activateAccentValueSetting (settingsUI);
        this.activateQuantizeAmountSetting (settingsUI);

        this.ribbonModeSetting = settingsUI.getEnumSetting ("Ribbon Mode", CATEGORY_PLAY_AND_SEQUENCE, RIBBON_MODE_VALUES, RIBBON_MODE_VALUES[0]);
        this.ribbonModeSetting.addValueObserver (value -> {
            this.ribbonMode = lookupIndex (RIBBON_MODE_VALUES, value);
            this.notifyObservers (RIBBON_MODE);
        });

        ///////////////////////////
        // Scale

        this.activateScaleSetting (settingsUI);
        this.activateScaleBaseSetting (settingsUI);
        this.activateScaleInScaleSetting (settingsUI);
        this.activateScaleLayoutSetting (settingsUI);

        ///////////////////////////
        // Workflow

        this.activateBehaviourOnStopSetting (settingsUI);
        this.activateSelectClipOnLaunchSetting (settingsUI);
        this.activateNewClipLengthSetting (settingsUI);
    }


    /**
     * Set the ribbon mode.
     *
     * @param mode The functionality for the ribbon
     */
    public void setRibbonMode (final int mode)
    {
        this.ribbonModeSetting.set (RIBBON_MODE_VALUES[mode]);
    }


    /**
     * Get the ribbon mode.
     *
     * @return The functionality for the ribbon
     */
    public int getRibbonMode ()
    {
        return this.ribbonMode;
    }


    /**
     * Returns true if duplicate is enabled.
     *
     * @return True if duplicate is enabled
     */
    public boolean isDuplicateEnabled ()
    {
        return this.duplicateEnabled;
    }


    /**
     * Set if duplicate is enabled.
     *
     * @param duplicateEnabled True to enable
     */
    public void setDuplicateEnabled (final boolean duplicateEnabled)
    {
        this.duplicateEnabled = duplicateEnabled;
    }
}
