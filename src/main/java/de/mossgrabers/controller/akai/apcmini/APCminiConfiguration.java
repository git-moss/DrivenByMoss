// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini;

import java.util.List;

import de.mossgrabers.controller.akai.apcmini.definition.IAPCminiControllerDefinition;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.view.Views;


/**
 * The configuration settings for APC.
 *
 * @author Jürgen Moßgraber
 */
public class APCminiConfiguration extends AbstractConfiguration
{
    /** Default function of the faders. */
    public static final Integer                FADER_CTRL           = Integer.valueOf (NEXT_SETTING_ID);
    /** Default function of the track buttons. */
    public static final Integer                SOFT_KEYS            = Integer.valueOf (NEXT_SETTING_ID + 1);
    /** The pad brightness. */
    public static final Integer                PAD_BRIGHTNESS       = Integer.valueOf (NEXT_SETTING_ID + 2);

    private static final List<String>          FADER_CTRL_OPTIONS   = List.of ("Volume", "Pan", "Send 1", "Send 2", "Send 3", "Send 4", "Send 5", "Send 6", "Send 7", "Send 8", "Device");

    private static final List<String>          BRIGHTNESS_OPTIONS   = List.of ("10%", "25%", "50%", "65%", "75%", "90%", "100%");

    /** The names of the track button functions. */
    public static final List<String>           SOFT_KEYS_OPTIONS    = List.of ("Clip Stop", "Solo", "Rec Arm", "Mute", "Select");

    private static final Views []              PREFERRED_NOTE_VIEWS =
    {
        Views.PLAY,
        Views.DRUM,
        Views.SEQUENCER,
        Views.RAINDROPS
    };

    private final IAPCminiControllerDefinition definition;

    private String                             faderCtrl            = FADER_CTRL_OPTIONS.get (0);
    private String                             softKeys             = SOFT_KEYS_OPTIONS.get (0);

    private IEnumSetting                       faderCtrlSetting;
    private IEnumSetting                       softKeysSetting;
    private int                                padBrightness        = 4;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     * @param definition The specific APCmini
     */
    public APCminiConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes, final IAPCminiControllerDefinition definition)
    {
        super (host, valueChanger, arpeggiatorModes);

        this.definition = definition;
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Hardware

        if (this.definition.hasBrightness ())
        {
            final IEnumSetting padBrightnessSetting = globalSettings.getEnumSetting ("Pad Brightness", CATEGORY_HARDWARE_SETUP, BRIGHTNESS_OPTIONS, BRIGHTNESS_OPTIONS.get (this.padBrightness));
            padBrightnessSetting.addValueObserver (value -> {
                this.padBrightness = lookupIndex (BRIGHTNESS_OPTIONS, value);
                this.notifyObservers (PAD_BRIGHTNESS);
            });
        }

        ///////////////////////////
        // Scale

        this.activateScaleSetting (documentSettings);
        this.activateScaleBaseSetting (documentSettings);
        this.activateScaleInScaleSetting (documentSettings);
        this.activateScaleLayoutSetting (documentSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (globalSettings);
        this.activateStartupViewSetting (globalSettings, PREFERRED_NOTE_VIEWS);
        this.activateStartWithSessionViewSetting (globalSettings);
        this.activateShowPlayedChordsSetting (globalSettings);

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


    /**
     * Get the pad brightness.
     *
     * @return The pad brightness in the range of [0..6]
     */
    public int getPadBrightness ()
    {
        return this.padBrightness;
    }
}
