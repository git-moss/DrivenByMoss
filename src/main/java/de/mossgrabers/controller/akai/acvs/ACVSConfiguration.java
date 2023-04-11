// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;

import java.util.List;


/**
 * The configuration settings for AVCS devices.
 *
 * @author Jürgen Moßgraber
 */
public class ACVSConfiguration extends AbstractConfiguration
{
    /** Setting for launching clips or scenes. */
    public static final Integer    LAUNCH_CLIPS_OR_SCENES = Integer.valueOf (50);

    private static final String [] SCENE_CLIPS_OPTIONS    =
    {
        "Clips",
        "Scenes"
    };

    private static final String [] ACVS_DEVICE;
    static
    {
        final ACVSDevice [] values = ACVSDevice.values ();
        ACVS_DEVICE = new String [values.length];
        for (int i = 0; i < values.length; i++)
            ACVS_DEVICE[i] = values[i].getName ();
    }

    private IEnumSetting launchClipsOrScenesSetting;
    private boolean      launchClips;

    private IEnumSetting acvsDeviceSetting;
    private ACVSDevice   acvsDevice = ACVSDevice.MPC_LIVE_ONE;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public ACVSConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);

        this.selectClipOnLaunch = true;
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Hardware

        this.acvsDeviceSetting = globalSettings.getEnumSetting ("Device", CATEGORY_HARDWARE_SETUP, ACVS_DEVICE, ACVSDevice.MPC_LIVE_ONE.getName ());
        final String acvsDeviceName = this.acvsDeviceSetting.get ();
        final ACVSDevice [] values = ACVSDevice.values ();
        for (final ACVSDevice value: values)
        {
            if (value.getName ().equals (acvsDeviceName))
            {
                this.acvsDevice = value;
                break;
            }
        }
        this.acvsDeviceSetting.setEnabled (false);

        ///////////////////////////
        // Session

        this.launchClipsOrScenesSetting = globalSettings.getEnumSetting ("Launch", CATEGORY_SESSION, SCENE_CLIPS_OPTIONS, SCENE_CLIPS_OPTIONS[0]);
        this.launchClipsOrScenesSetting.addValueObserver (value -> {
            this.launchClips = SCENE_CLIPS_OPTIONS[0].equals (value);
            this.notifyObservers (LAUNCH_CLIPS_OR_SCENES);
        });

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateBehaviourOnPauseSetting (globalSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
    }


    /**
     * Set to launch clips or scenes?
     *
     * @param launchClips True if clips should be launched otherwise scenes
     */
    public void setLaunchClipsOrScenes (final boolean launchClips)
    {
        this.launchClipsOrScenesSetting.set (SCENE_CLIPS_OPTIONS[launchClips ? 0 : 1]);
    }


    /**
     * Launch clips or scenes?
     *
     * @return True if clips should be launched otherwise scenes
     */
    public boolean isLaunchClips ()
    {
        return this.launchClips;
    }


    /**
     * Set the active ACVS device.
     *
     * @param acvsDevice The ACVS device to active
     */
    public void setACVSActiveDevice (final ACVSDevice acvsDevice)
    {
        this.acvsDeviceSetting.set (acvsDevice.getName ());
    }


    /**
     * Get the active ACVS device.
     *
     * @return The active ACVS device
     */
    public ACVSDevice getACVSActiveDevice ()
    {
        return this.acvsDevice;
    }


    /**
     * Check if the given ACVS device is the one currently connected.
     *
     * @param acvsDevice The device to compare
     * @return True if the given ACVS device is the active one
     */
    public boolean isActiveACVSDevice (final ACVSDevice acvsDevice)
    {
        return this.acvsDevice == acvsDevice;
    }
}
