// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.view.Views;

import java.util.List;


/**
 * The configuration settings for APC.
 *
 * @author Jürgen Moßgraber
 */
public class APCminiConfiguration extends AbstractConfiguration
{
    /** Default function of the faders. */
    public static final Integer       FADER_CTRL           = Integer.valueOf (50);
    /** Default function of the track buttons. */
    public static final Integer       SOFT_KEYS            = Integer.valueOf (51);

    private static final List<String> FADER_CTRL_OPTIONS   = List.of ("Volume", "Pan", "Send 1", "Send 2", "Send 3", "Send 4", "Send 5", "Send 6", "Send 7", "Send 8", "Device");

    /** The names of the track button functions. */
    public static final List<String>  SOFT_KEYS_OPTIONS    = List.of ("Clip Stop", "Solo", "Rec Arm", "Mute", "Select");

    private static final Views []     PREFERRED_NOTE_VIEWS =
    {
        Views.PLAY,
        Views.DRUM,
        Views.SEQUENCER,
        Views.RAINDROPS
    };

    private String                    faderCtrl            = FADER_CTRL_OPTIONS.get (0);
    private String                    softKeys             = SOFT_KEYS_OPTIONS.get (0);

    private IEnumSetting              faderCtrlSetting;
    private IEnumSetting              softKeysSetting;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public APCminiConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
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
        this.activatePreferredNoteViewSetting (globalSettings, PREFERRED_NOTE_VIEWS);
        this.activateStartWithSessionViewSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateBehaviourOnPauseSetting (globalSettings);
        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);

        ///////////////////////////
        // Button Control

        this.faderCtrlSetting = globalSettings.getEnumSetting ("Fader Ctrl", "Button Control", FADER_CTRL_OPTIONS, this.faderCtrl);
        this.faderCtrlSetting.addValueObserver (value -> {
            this.faderCtrl = value;
            this.notifyObservers (FADER_CTRL);
        });

        this.softKeysSetting = globalSettings.getEnumSetting ("Soft Keys", "Button Control", SOFT_KEYS_OPTIONS, this.softKeys);
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
