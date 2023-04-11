// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3;

import de.mossgrabers.controller.ni.maschine.Maschine;
import de.mossgrabers.controller.ni.maschine.core.RibbonMode;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.scale.ScaleLayout;

import java.util.List;


/**
 * The configuration settings for Maschine.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineConfiguration extends AbstractConfiguration
{
    /** Setting for the ribbon mode. */
    public static final Integer RIBBON_MODE = Integer.valueOf (50);

    private final Maschine      maschine;

    /** What does the ribbon send? **/
    private RibbonMode          ribbonMode  = RibbonMode.PITCH_DOWN;

    private IEnumSetting        ribbonModeSetting;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     * @param maschine The type of Maschine
     */
    public MaschineConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes, final Maschine maschine)
    {
        super (host, valueChanger, arpeggiatorModes);

        this.maschine = maschine;
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

        this.activateRecordButtonSetting (globalSettings);
        this.activateShiftedRecordButtonSetting (globalSettings);
        this.activateBehaviourOnPauseSetting (globalSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateAccentActiveSetting (globalSettings);
        this.activateAccentValueSetting (globalSettings);
        this.activateQuantizeAmountSetting (globalSettings);

        final String [] ribbonModeNames = RibbonMode.getNames ();
        this.ribbonModeSetting = globalSettings.getEnumSetting ("Ribbon Mode", CATEGORY_PLAY_AND_SEQUENCE, ribbonModeNames, ribbonModeNames[0]);
        this.ribbonModeSetting.addValueObserver (value -> {
            this.ribbonMode = RibbonMode.lookupByName (value);
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

        final int footswitches = this.maschine.getFootswitches ();
        if (footswitches >= 2)
        {
            this.activateFootswitchSetting (globalSettings, 0, "Footswitch (Tip)");
            this.activateFootswitchSetting (globalSettings, 1, "Footswitch (Ring)");

            if (footswitches == 4)
            {
                this.activateFootswitchSetting (globalSettings, 2, "Footswitch 2 (Tip)");
                this.activateFootswitchSetting (globalSettings, 3, "Footswitch 2 (Ring)");
            }
        }

        ///////////////////////////
        // Pads

        this.activateConvertAftertouchSetting (globalSettings);
    }


    /**
     * Set the ribbon mode.
     *
     * @param mode The functionality for the ribbon
     */
    public void setRibbonMode (final RibbonMode mode)
    {
        this.ribbonModeSetting.set (mode.getName ());
    }


    /**
     * Get the ribbon mode.
     *
     * @return The functionality for the ribbon
     */
    public RibbonMode getRibbonMode ()
    {
        return this.ribbonMode;
    }
}
