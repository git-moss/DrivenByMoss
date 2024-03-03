// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii;

import java.util.List;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The configuration settings for Komplete Kontrol MkII.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolConfiguration extends AbstractConfiguration
{
    private static final Integer   FLIP_TRACK_CLIP_NAVIGATION = Integer.valueOf (52);
    private static final Integer   FLIP_CLIP_SCENE_NAVIGATION = Integer.valueOf (53);
    private static final Integer   MODE_SWITCH_BUTTON         = Integer.valueOf (54);

    private static final String    CATEGORY_NAVIGATION        = "Navigation";
    private static final String [] MODE_SWITCH_BUTTONS        = new String []
    {
        "Off",
        "Auto",
        "Loop",
        "Quantize",
        "Redo",
        "Restart",
        "Stop",
        "Tempo"
    };


    /** The button to switch modes. */
    public enum SwitchButton
    {
        /** None. */
        OFF,
        /** The Auto button. */
        AUTO,
        /** The Loop button. */
        LOOP,
        /** The Quantize button. */
        QUANTIZE,
        /** The Redo button. */
        REDO,
        /** The Restart button. */
        RESTART,
        /** The Stop button. */
        STOP,
        /** The Tempo button. */
        TEMPO
    }


    private final int    version;
    private boolean      flipTrackClipNavigation = false;
    private boolean      flipClipSceneNavigation = false;
    private SwitchButton modeSwitchButton        = SwitchButton.OFF;


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
        final IEnumSetting modeSwitchButtonSetting = globalSettings.getEnumSetting ("Switch modes with", CATEGORY_HARDWARE_SETUP, MODE_SWITCH_BUTTONS, MODE_SWITCH_BUTTONS[this.version >= 3 ? 6 : 0]);
        modeSwitchButtonSetting.addValueObserver (value -> {
            this.modeSwitchButton = SwitchButton.values ()[lookupIndex (MODE_SWITCH_BUTTONS, value)];
            this.notifyObservers (MODE_SWITCH_BUTTON);
        });
        this.isSettingActive.add (MODE_SWITCH_BUTTON);

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
        this.activateKnobSpeedSetting (globalSettings, 0, 20);
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
     * Get the button to use as the mode switcher.
     * 
     * @return The mode switch button
     */
    public SwitchButton getModeSwitchButton ()
    {
        return this.modeSwitchButton;
    }
}
