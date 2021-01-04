// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.scale.ScaleLayout;


/**
 * The configuration settings for Maschine.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineConfiguration extends AbstractConfiguration
{
    /** Setting for the ribbon mode. */
    public static final Integer   RIBBON_MODE                    = Integer.valueOf (50);

    /** Use ribbon for pitch bend down. */
    public static final int       RIBBON_MODE_PITCH_DOWN         = 0;
    /** Use ribbon for pitch bend up. */
    public static final int       RIBBON_MODE_PITCH_UP           = 1;
    /** Use ribbon for pitch bend down/up. */
    public static final int       RIBBON_MODE_PITCH_DOWN_UP      = 2;
    /** Use ribbon for midi CC 1. */
    public static final int       RIBBON_MODE_CC_1               = 3;
    /** Use ribbon for midi CC 11. */
    public static final int       RIBBON_MODE_CC_11              = 4;
    /** Use ribbon for master volume. */
    public static final int       RIBBON_MODE_MASTER_VOLUME      = 5;
    /** Use ribbon for note repeat period. */
    public static final int       RIBBON_MODE_NOTE_REPEAT_PERIOD = 6;
    /** Use ribbon for note repeat length. */
    public static final int       RIBBON_MODE_NOTE_REPEAT_LENGTH = 7;

    /** The ribbon mode names. */
    public static final String [] RIBBON_MODE_VALUES             =
    {
        "Pitch Down",
        "Pitch Up",
        "Pitch Down/Up",
        "Modulation (CC 1)",
        "Expression (CC 11)",
        "Master Volume",
        "Note Repeat: Period",
        "Note Repeat: Length"
    };

    /** What does the ribbon send? **/
    private int                   ribbonMode                     = RIBBON_MODE_PITCH_DOWN;

    private IEnumSetting          ribbonModeSetting;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public MaschineConfiguration (final IHost host, final IValueChanger valueChanger, final ArpeggiatorMode [] arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Scale

        this.activateScaleSetting (documentSettings);
        this.activateScaleBaseSetting (documentSettings);
        this.activateScaleInScaleSetting (documentSettings);
        this.activateScaleLayoutSetting (documentSettings, ScaleLayout.SEQUENT_UP.getName ());

        ///////////////////////////
        // Note Repeat

        this.activateNoteRepeatSetting (documentSettings);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateFlipRecordSetting (globalSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateAccentActiveSetting (globalSettings);
        this.activateAccentValueSetting (globalSettings);
        this.activateQuantizeAmountSetting (globalSettings);

        this.ribbonModeSetting = globalSettings.getEnumSetting ("Ribbon Mode", CATEGORY_PLAY_AND_SEQUENCE, RIBBON_MODE_VALUES, RIBBON_MODE_VALUES[0]);
        this.ribbonModeSetting.addValueObserver (value -> {
            this.ribbonMode = lookupIndex (RIBBON_MODE_VALUES, value);
            this.notifyObservers (RIBBON_MODE);
        });
        this.isSettingActive.add (RIBBON_MODE);

        ///////////////////////////
        // Session

        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateActionForRecArmedPad (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);
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
}
