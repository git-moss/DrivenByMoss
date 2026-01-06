// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii;

import java.util.List;

import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocol;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The configuration settings for Komplete Kontrol Mk2/3.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolConfiguration extends AbstractConfiguration
{
    private static final Integer   FLIP_TRACK_CLIP_NAVIGATION = Integer.valueOf (NEXT_SETTING_ID);
    private static final Integer   FLIP_CLIP_SCENE_NAVIGATION = Integer.valueOf (NEXT_SETTING_ID + 1);
    /** ID for the DAW switch. */
    public static final Integer    DAW_SWITCH                 = Integer.valueOf (NEXT_SETTING_ID + 2);

    private static final String    CATEGORY_NAVIGATION        = "Navigation";

    private static final String [] DAW_NAMES                  = new String []
    {
        "Generic",
        "Bitwig",
        "Cubase",
        "Live",
        "Digital Performer",
        "Logic Pro"
    };

    private final int              version;
    private boolean                flipTrackClipNavigation    = false;
    private boolean                flipClipSceneNavigation    = false;
    private int                    dawNameIndex               = 0;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     * @param version The version number of the NIHIA protocol to support
     */
    public KontrolProtocolConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes, final int version)
    {
        super (host, valueChanger, arpeggiatorModes);

        this.version = version;
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        if (this.version >= KontrolProtocol.VERSION_3)
        {
            final IEnumSetting modeSwitchButtonSetting = globalSettings.getEnumSetting ("Device Background (requires restart)", CATEGORY_HARDWARE_SETUP, DAW_NAMES, DAW_NAMES[1]);
            modeSwitchButtonSetting.addValueObserver (value -> {
                this.dawNameIndex = lookupIndex (DAW_NAMES, value);
                this.notifyObservers (DAW_SWITCH);
            });
            this.isSettingActive.add (DAW_SWITCH);
        }

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
        this.activateKnobSpeedSetting (globalSettings, 30, 0);
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


    /**
     * Get the DAW name to use which triggers a different background image for the plug-in mode.
     *
     * @return The DAW name
     */
    public String getSelectedDaw ()
    {
        return DAW_NAMES[this.dawNameIndex];
    }
}
