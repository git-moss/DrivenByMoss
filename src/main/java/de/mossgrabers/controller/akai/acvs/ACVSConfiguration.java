// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
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
 * The configuration settings for MPC.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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

    private IEnumSetting           launchClipsOrScenesSetting;
    private boolean                launchClips;


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
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);

        ///////////////////////////
        // Session

        this.launchClipsOrScenesSetting = globalSettings.getEnumSetting ("Launch", CATEGORY_SESSION, SCENE_CLIPS_OPTIONS, SCENE_CLIPS_OPTIONS[0]);
        this.launchClipsOrScenesSetting.addValueObserver (value -> {
            this.launchClips = SCENE_CLIPS_OPTIONS[0].equals (value);
            this.notifyObservers (LAUNCH_CLIPS_OR_SCENES);
        });

        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
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
}
