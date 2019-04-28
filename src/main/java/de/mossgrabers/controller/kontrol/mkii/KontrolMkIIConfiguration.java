// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mkii;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IHost;


/**
 * The configuration settings for Komplete Kontrol MkII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolMkIIConfiguration extends AbstractConfiguration
{
    private static final Integer   RECORD_BUTTON_FUNCTION         = Integer.valueOf (50);
    private static final Integer   SHIFTED_RECORD_BUTTON_FUNCTION = Integer.valueOf (51);
    private static final Integer   FLIP_TRACK_CLIP_NAVIGATION     = Integer.valueOf (52);
    private static final Integer   FLIP_CLIP_SCENE_NAVIGATION     = Integer.valueOf (53);

    /** Record in arranger. */
    public static final int        RECORD_ARRANGER                = 0;
    /** Record in clip. */
    public static final int        RECORD_CLIP                    = 1;
    /** Create a new clip, enable overdub and start playback. */
    public static final int        NEW_CLIP                       = 2;
    /** Toggle arranger overdub. */
    public static final int        TOGGLE_ARRANGER_OVERDUB        = 3;
    /** Toggle clip overdub. */
    public static final int        TOGGLE_CLIP_OVERDUB            = 4;

    private static final String    CATEGORY_NAVIGATION            = "Navigation";

    private static final String [] RECORD_OPTIONS                 = new String []
    {
        "Record arranger",
        "Record clip",
        "New clip",
        "Toggle arranger overdub",
        "Toggle clip overdub",
    };

    private int                    recordButtonFunction           = 0;
    private int                    shiftedRecordButtonFunction    = 1;
    private boolean                flipTrackClipNavigation        = false;
    private boolean                flipClipSceneNavigation        = false;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     */
    public KontrolMkIIConfiguration (final IHost host, final IValueChanger valueChanger)
    {
        super (host, valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (settingsUI);

        final IEnumSetting recordButtonSetting = settingsUI.getEnumSetting ("Record button", CATEGORY_TRANSPORT, RECORD_OPTIONS, RECORD_OPTIONS[1]);
        recordButtonSetting.addValueObserver (value -> {
            for (int i = 0; i < RECORD_OPTIONS.length; i++)
            {
                if (RECORD_OPTIONS[i].equals (value))
                    this.recordButtonFunction = i;
            }
            this.notifyObservers (RECORD_BUTTON_FUNCTION);
        });

        final IEnumSetting shiftedRecordButtonSetting = settingsUI.getEnumSetting ("Shift + Record button", CATEGORY_TRANSPORT, RECORD_OPTIONS, RECORD_OPTIONS[0]);
        shiftedRecordButtonSetting.addValueObserver (value -> {
            for (int i = 0; i < RECORD_OPTIONS.length; i++)
            {
                if (RECORD_OPTIONS[i].equals (value))
                    this.shiftedRecordButtonFunction = i;
            }
            this.notifyObservers (SHIFTED_RECORD_BUTTON_FUNCTION);
        });

        ///////////////////////////
        // Navigation

        final IEnumSetting flipTrackClipNavigationSetting = settingsUI.getEnumSetting ("Flip track/clip navigation", CATEGORY_NAVIGATION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        flipTrackClipNavigationSetting.addValueObserver (value -> {
            this.flipTrackClipNavigation = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (FLIP_TRACK_CLIP_NAVIGATION);
        });

        final IEnumSetting flipClipSceneNavigationSetting = settingsUI.getEnumSetting ("Flip clip/scene navigation", CATEGORY_NAVIGATION, ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        flipClipSceneNavigationSetting.addValueObserver (value -> {
            this.flipClipSceneNavigation = ON_OFF_OPTIONS[1].equals (value);
            this.notifyObservers (FLIP_CLIP_SCENE_NAVIGATION);
        });

        ///////////////////////////
        // Workflow

        this.activateNewClipLengthSetting (settingsUI);
    }


    /**
     * Get the selected function for the record button.
     *
     * @return The function index
     */
    public int getRecordButtonFunction ()
    {
        return this.recordButtonFunction;
    }


    /**
     * Get the selected function for the shifted record button.
     *
     * @return The function index
     */
    public int getShiftedRecordButtonFunction ()
    {
        return this.shiftedRecordButtonFunction;
    }


    /**
     * Returns true if track and clip navigation should be flipped
     *
     * @return True if track and clip navigation should be flipped
     */
    public boolean isFlipTrackClipNavigation ()
    {
        return this.flipTrackClipNavigation;
    }


    /**
     * Returns true if clip and scene navigation should be flipped
     *
     * @return True if clip and scene navigation should be flipped
     */
    public boolean isFlipClipSceneNavigation ()
    {
        return this.flipClipSceneNavigation;
    }
}
