// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;

import java.util.List;


/**
 * The configuration settings for Komplete Kontrol MkII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolProtocolConfiguration extends AbstractConfiguration
{
    private static final Integer FLIP_TRACK_CLIP_NAVIGATION = Integer.valueOf (52);
    private static final Integer FLIP_CLIP_SCENE_NAVIGATION = Integer.valueOf (53);

    private static final String  CATEGORY_NAVIGATION        = "Navigation";

    private boolean              flipTrackClipNavigation    = false;
    private boolean              flipClipSceneNavigation    = false;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public KontrolProtocolConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateBehaviourOnPauseSetting (globalSettings);
        this.activateRecordButtonSetting (globalSettings);
        this.activateShiftedRecordButtonSetting (globalSettings);

        ///////////////////////////
        // Navigation

        final IEnumSetting flipTrackClipNavigationSetting = globalSettings.getEnumSetting ("Flip track/clip navigation", CATEGORY_NAVIGATION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        flipTrackClipNavigationSetting.addValueObserver (value -> {
            this.flipTrackClipNavigation = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (FLIP_TRACK_CLIP_NAVIGATION);
        });
        this.isSettingActive.add (FLIP_TRACK_CLIP_NAVIGATION);

        final IEnumSetting flipClipSceneNavigationSetting = globalSettings.getEnumSetting ("Flip clip/scene navigation", CATEGORY_NAVIGATION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        flipClipSceneNavigationSetting.addValueObserver (value -> {
            this.flipClipSceneNavigation = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (FLIP_CLIP_SCENE_NAVIGATION);
        });
        this.isSettingActive.add (FLIP_CLIP_SCENE_NAVIGATION);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);
    }


    /**
     * Returns true if track and clip navigation should be flipped.
     *
     * @return True if track and clip navigation should be flipped
     */
    public boolean isFlipTrackClipNavigation ()
    {
        return this.flipTrackClipNavigation;
    }


    /**
     * Returns true if clip and scene navigation should be flipped.
     *
     * @return True if clip and scene navigation should be flipped
     */
    public boolean isFlipClipSceneNavigation ()
    {
        return this.flipClipSceneNavigation;
    }
}
