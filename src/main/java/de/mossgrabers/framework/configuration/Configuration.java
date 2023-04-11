// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.configuration;

import de.mossgrabers.framework.configuration.AbstractConfiguration.RecordFunction;
import de.mossgrabers.framework.configuration.AbstractConfiguration.TransportBehavior;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.observer.ISettingObserver;
import de.mossgrabers.framework.view.Views;

import java.util.List;


/**
 * Interface to several configuration settings.
 *
 * @author Jürgen Moßgraber
 */
public interface Configuration
{
    /**
     * Registers an observer which gets called when the given setting has changed.
     *
     * @param settingID The ID of the setting to observe
     * @param observer The observer to call when the settings has changed
     */
    void addSettingObserver (Integer settingID, ISettingObserver observer);


    /**
     * Remove an observer which was previously registered.
     *
     * @param settingID The ID of the observed setting
     * @param observer The observer to remove
     */
    void removeSettingObserver (Integer settingID, ISettingObserver observer);


    /**
     * Notify all registered observers no matter for which they are registered.
     */
    void notifyAllObservers ();


    /**
     * Removes all setting observers.
     */
    void clearSettingObservers ();


    /**
     * Check if the setting with the given ID is active and can be observed.
     *
     * @param settingID The ID of a setting
     * @return True if it can be observed
     */
    boolean canSettingBeObserved (Integer settingID);


    /**
     * Get the scale by name.
     *
     * @return Get the name of the scale
     */
    String getScale ();


    /**
     * Set the scale by name.
     *
     * @param scale The name of a scale
     */
    void setScale (String scale);


    /**
     * Get the scale base note by name.
     *
     * @return The name of a scale base note
     */
    String getScaleBase ();


    /**
     * Set the scale base note by name.
     *
     * @param scaleBase The name of a scale base note
     */
    void setScaleBase (final String scaleBase);


    /**
     * Get the in-key setting.
     *
     * @return True if scale otherwise chromatic
     */
    boolean isScaleInKey ();


    /**
     * Set the in-scale setting.
     *
     * @param inScale True if scale otherwise chromatic
     */
    void setScaleInKey (boolean inScale);


    /**
     * Get the scale layout.
     *
     * @return The scale layout
     */
    String getScaleLayout ();


    /**
     * Set the scale layout.
     *
     * @param scaleLayout The scale layout
     */
    void setScaleLayout (String scaleLayout);


    /**
     * Is the VU meters state enabled?
     *
     * @return True if enabled
     */
    boolean isEnableVUMeters ();


    /**
     * Get the behavior when stop is pressed.
     *
     * @return The behavior
     */
    TransportBehavior getBehaviourOnStop ();


    /**
     * Get the behavior when pause is pressed.
     *
     * @return The behavior
     */
    TransportBehavior getBehaviourOnPause ();


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
     * Get the default length for new clips as beats.
     *
     * @param quartersPerMeasure The number of quarters per measure
     * @return The default length for new clips in beats
     */
    int getNewClipLenghthInBeats (int quartersPerMeasure);


    /**
     * Set the index (0-7) of the default length for new clips.
     *
     * @param value The index of the length which is: 0: "1 Beat", 1: "2 Beat", 2: "1 Bar", 3: "2
     *            Bars", 4: "4 Bars", 5: "8 Bars", 6: "16 Bars", 7: "32 Bars"
     */
    void setNewClipLength (int value);


    /**
     * Select the next new clip length. Wraps around to the first.
     */
    void nextNewClipLength ();


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
     * Should the drum pads sound with or without using a combination button?
     *
     * @return True if use combination button
     */
    boolean isCombinationButtonToSoundDrumPads ();


    /**
     * Returns true if draw record stripe is enabled.
     *
     * @return True if draw record stripe is enabled.
     */
    boolean isDrawRecordStripe ();


    /**
     * Get the action for record armed pads.
     *
     * @return The action for record armed pads (0-2).
     */
    int getActionForRecArmedPad ();


    /**
     * Overwrite this function to add the settings which are supported by your extension.
     *
     * @param globalSettings The global user interface settings
     * @param documentSettings The document (project) specific user interface settings
     */
    void init (ISettingsUI globalSettings, ISettingsUI documentSettings);


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
    void setFixedAccentValue (int value);


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
     * Get the functionality of a footswitch.
     *
     * @param index The index of the footswitch
     * @return The functionality of the footswitch.
     */
    int getFootswitch (int index);


    /**
     * Get the change value for normal knob speed.
     *
     * @return The value in the range of [-100, 100], default is 0, negative values are slower,
     *         positive faster
     */
    int getKnobSensitivityDefault ();


    /**
     * Get the change value for slow knob speed.
     *
     * @return The value in the range of [-100, 100], default is 0, negative values are slower,
     *         positive faster
     */
    int getKnobSensitivitySlow ();


    /**
     * Get all supported Arpeggiator modes.
     *
     * @return The modes
     */
    List<ArpeggiatorMode> getArpeggiatorModes ();


    /**
     * Lookup the index of the given arpeggiator mode among the available ones.
     *
     * @param arpMode The arpeggiator mode to look up
     * @return The index
     */
    int lookupArpeggiatorModeIndex (ArpeggiatorMode arpMode);


    /**
     * Is note repeat active?
     *
     * @return True if active
     */
    boolean isNoteRepeatActive ();


    /**
     * Set note repeat de-/active.
     *
     * @param active True to set active
     */
    void setNoteRepeatActive (boolean active);


    /**
     * Toggle note repeat de-/active.
     */
    void toggleNoteRepeatActive ();


    /**
     * Get the note repeat period.
     *
     * @return The note repeat period
     */
    Resolution getNoteRepeatPeriod ();


    /**
     * Set the note repeat period.
     *
     * @param noteRepeatPeriod The note repeat period
     */
    void setNoteRepeatPeriod (Resolution noteRepeatPeriod);


    /**
     * Get the note repeat length.
     *
     * @return The note repeat length
     */
    Resolution getNoteRepeatLength ();


    /**
     * Get the note repeat mode.
     *
     * @return The note repeat mode
     */
    ArpeggiatorMode getNoteRepeatMode ();


    /**
     * Set the note repeat length.
     *
     * @param noteRepeatLength The note repeat length
     */
    void setNoteRepeatLength (Resolution noteRepeatLength);


    /**
     * Get the note repeat octave.
     *
     * @return The note repeat octave
     */
    int getNoteRepeatOctave ();


    /**
     * Set the note repeat octave.
     *
     * @param octave The note repeat octave
     */
    void setNoteRepeatOctave (int octave);


    /**
     * Set the note repeat mode.
     *
     * @param arpMode The note repeat mode
     */
    void setNoteRepeatMode (ArpeggiatorMode arpMode);


    /**
     * Select the next or previous note repeat mode.
     *
     * @param increase True to select the next otherwise the previous
     */
    default void setPrevNextNoteRepeatMode (final boolean increase)
    {
        final ArpeggiatorMode arpMode = this.getNoteRepeatMode ();
        final int modeIndex = this.lookupArpeggiatorModeIndex (arpMode);
        final List<ArpeggiatorMode> modes = this.getArpeggiatorModes ();
        final int newIndex = Math.max (0, Math.min (modes.size () - 1, modeIndex + (increase ? 1 : -1)));
        this.setNoteRepeatMode (modes.get (newIndex));
    }


    /**
     * Get the MIDI channel for editing.
     *
     * @return The MIDI channel for editing notes
     */
    int getMidiEditChannel ();


    /**
     * Set the MIDI channel for editing.
     *
     * @param midiChannel The MIDI channel, 0-15
     */
    void setMidiEditChannel (int midiChannel);


    /**
     * Get the selected function for the record button.
     *
     * @return The function index
     */
    RecordFunction getRecordButtonFunction ();


    /**
     * Get the selected function for the shifted record button.
     *
     * @return The function index
     */
    RecordFunction getShiftedRecordButtonFunction ();


    /**
     * Get the preferred note view.
     *
     * @return The preferred note view
     */
    Views getPreferredNoteView ();


    /**
     * Get the preferred audio view.
     *
     * @return The preferred note view
     */
    Views getPreferredAudioView ();


    /**
     * Should the session view be activated on startup (instead of a play view)?
     *
     * @return True if session view should be active
     */
    boolean shouldStartWithSessionView ();
}
