// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

import de.mossgrabers.framework.configuration.AbstractConfiguration.BehaviourOnStop;
import de.mossgrabers.framework.observer.SettingObserver;


/**
 * Interface to several configuration settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface Configuration
{
    /**
     * Registers an observer which gets called when the given setting has changed.
     *
     * @param settingID The ID of the setting to observe
     * @param observer The observer to call when the settings has changed
     */
    void addSettingObserver (Integer settingID, SettingObserver observer);


    /**
     * Removes all setting observers.
     */
    void clearSettingObservers ();


    /**
     * Get the scale by name.
     *
     * @return Get the name of the scale
     */
    String getScale ();


    /**
     * Get the scale base note by name.
     *
     * @return The name of a scale base note
     */
    String getScaleBase ();


    /**
     * Get the in-key setting.
     *
     * @return True if scale otherwise chromatic
     */
    boolean isScaleInKey ();


    /**
     * Get the scale layout.
     *
     * @return The scale layout
     */
    String getScaleLayout ();


    /**
     * Is the VU meters state enabled?
     *
     * @return True if enabled
     */
    boolean isEnableVUMeters ();


    /**
     * Get the behaviour when stop is pressed.
     *
     * @return THe behaviour
     */
    BehaviourOnStop getBehaviourOnStop ();


    /**
     * Flip the arrange record and clip record buttons?
     *
     * @return True if flipped
     */
    boolean isFlipRecord ();


    /**
     * Is accent active?
     *
     * @return True if active
     */
    boolean isAccentActive ();


    /**
     * Get the fixed accent value.
     *
     * @return The fixed accent value
     */
    int getFixedAccentValue ();


    /**
     * Get the quantize amount.
     *
     * @return The quantize amount
     */
    int getQuantizeAmount ();


    /**
     * The conversion type of aftertouch.
     *
     * @return The conversion type
     */
    int getConvertAftertouch ();


    /**
     * Is the session flipped?
     *
     * @return True if flipped
     */
    boolean isFlipSession ();


    /**
     * Set the flip session state.
     *
     * @param enabled True if flipped
     */
    void setFlipSession (boolean enabled);


    /**
     * Should the crossfader be displayed?
     *
     * @return True if displayed
     */
    boolean isDisplayCrossfader ();


    /**
     * Select the clip on launch?
     *
     * @return True if the clip should be selected
     */
    boolean isSelectClipOnLaunch ();


    /**
     * Get the default length for new clips.
     *
     * @return The default length for new clips
     */
    int getNewClipLength ();


    /**
     * Set the default length for new clips.
     *
     * @param value The default length for new clips
     */
    void setNewClipLength (int value);


    /**
     * Returns true if auto selecting drum channel is enabled.
     *
     * @return True if auto selecting drum channel is enabled
     */
    boolean isAutoSelectDrum ();


    /**
     * Turn off the LEDs of empty drum pads?
     *
     * @return True if should turned off
     */
    boolean isTurnOffEmptyDrumPads ();


    /**
     * Returns true if lock flip session is enabled.
     *
     * @return True if lock flip session is enabled.
     */
    boolean isLockFlipSession ();


    /**
     * Returns true if draw record stripe is enabled.
     *
     * @return True if draw record stripe is enabled.
     */
    boolean isDrawRecordStripe ();


    /**
     * Get the action for rec armed pads.
     *
     * @return The action for rec armed pads (0-2).
     */
    int getActionForRecArmedPad ();


    /**
     * Overwrite this function to the settings which are supported by your extension.
     *
     * @param settings The user interface settings
     */
    void init (ISettingsUI settings);


    /**
     * Set the VU meters enabled state.
     *
     * @param enabled True if enabled
     */
    void setVUMetersEnabled (boolean enabled);


    /**
     * Set the accent enabled state.
     *
     * @param enabled The enabled state
     */
    void setAccentEnabled (boolean enabled);


    /**
     * Set the accent value.
     *
     * @param value The accent value
     */
    void setAccentValue (int value);


    /**
     * Change the quantize amount.
     *
     * @param control The change value
     */
    void changeQuantizeAmount (int control);


    /**
     * Set the quantize amount (1-100).
     *
     * @param value The value
     */
    void setQuantizeAmount (int value);


    /**
     * Reset the quantize amount.
     */
    void resetQuantizeAmount ();


    /**
     * Get the functionality of the footswitch 2.
     *
     * @return The functionality of the footswitch 2.
     */
    int getFootswitch2 ();
}
