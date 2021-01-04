// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The configuration settings for APC.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCminiConfiguration extends AbstractConfiguration
{
    /** Default function of the faders. */
    public static final Integer    FADER_CTRL         = Integer.valueOf (50);
    /** Default function of the track buttons. */
    public static final Integer    SOFT_KEYS          = Integer.valueOf (51);

    private static final String [] FADER_CTRL_OPTIONS =
    {
        "Volume",
        "Pan",
        "Send 1",
        "Send 2",
        "Send 3",
        "Send 4",
        "Send 5",
        "Send 6",
        "Send 7",
        "Send 8",
        "Device"
    };

    /** The names of the track button functions. */
    public static final String []  SOFT_KEYS_OPTIONS  =
    {
        "Clip Stop",
        "Solo",
        "Rec Arm",
        "Mute",
        "Select"
    };

    private String                 faderCtrl          = FADER_CTRL_OPTIONS[0];
    private String                 softKeys           = SOFT_KEYS_OPTIONS[0];

    private IEnumSetting           faderCtrlSetting;
    private IEnumSetting           softKeysSetting;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public APCminiConfiguration (final IHost host, final IValueChanger valueChanger, final ArpeggiatorMode [] arpeggiatorModes)
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
        this.activateScaleLayoutSetting (documentSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);

        ///////////////////////////
        // Button Control

        this.faderCtrlSetting = globalSettings.getEnumSetting ("Fader Ctrl", "Button Control", FADER_CTRL_OPTIONS, FADER_CTRL_OPTIONS[0]);
        this.faderCtrlSetting.addValueObserver (value -> {
            this.faderCtrl = value;
            this.notifyObservers (FADER_CTRL);
        });

        this.softKeysSetting = globalSettings.getEnumSetting ("Soft Keys", "Button Control", SOFT_KEYS_OPTIONS, SOFT_KEYS_OPTIONS[0]);
        this.softKeysSetting.addValueObserver (value -> {
            this.softKeys = value;
            this.notifyObservers (SOFT_KEYS);
        });
        this.isSettingActive.add (FADER_CTRL);
        this.isSettingActive.add (SOFT_KEYS);
    }


    /**
     * Set the fader control.
     *
     * @param faderCtrl The fader control
     */
    public void setFaderCtrl (final String faderCtrl)
    {
        this.faderCtrlSetting.set (faderCtrl);
    }


    /**
     * Set the track button function.
     *
     * @param softKeys The track button function
     */
    public void setSoftKeys (final String softKeys)
    {
        this.softKeysSetting.set (softKeys);
    }


    /**
     * Get the fader control.
     *
     * @return The fader control
     */
    public String getFaderCtrl ()
    {
        return this.faderCtrl;
    }


    /**
     * Get the track button function.
     *
     * @return The track button function
     */
    public String getSoftKeys ()
    {
        return this.softKeys;
    }
}
