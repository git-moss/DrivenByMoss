// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.observer.ObserverManagement;


/**
 * Interface to the Transport instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ITransport extends ObserverManagement
{
    /**
     * Start the playback.
     */
    void play ();


    /**
     * Returns true if playing.
     *
     * @return True if playing
     */
    boolean isPlaying ();


    /**
     * Restart the playback. When the transport is stopped, calling this function starts transport
     * playback, otherwise the transport is first stopped and the playback is restarted from the
     * last play-start position.
     */
    void restart ();


    /**
     * Stop the playback.
     */
    void stop ();


    /**
     * Stops the playback and moves the play position to the start of the arrangement.
     */
    void stopAndRewind ();


    /**
     * Start arranger recording.
     */
    void record ();


    /**
     * Returns true if arranger recording is enabled.
     *
     * @return True if arranger recording is enabled
     */
    boolean isRecording ();


    /**
     * Returns true if arranger overdub is enabled.
     *
     * @return True if arranger overdub is enabled
     */
    boolean isArrangerOverdub ();


    /**
     * Toggle if arranger overdub is enabled.
     */
    void toggleOverdub ();


    /**
     * Returns true if clip launcher overdub is enabled.
     *
     * @return True if clip launcher overdub is enabled
     */
    boolean isLauncherOverdub ();


    /**
     * Set if the clip launcher overdub is enabled.
     *
     * @param on True if clip launcher overdub is enabled
     */
    void setLauncherOverdub (final boolean on);


    /**
     * Toggle if the clip launcher overdub is enabled.
     */
    void toggleLauncherOverdub ();


    /**
     * Dis-/enables the metronome.
     *
     * @param on Turn on if true
     */
    void setMetronome (final boolean on);


    /**
     * Returns true if the metronome is on.
     *
     * @return True if the metronome is on
     */
    boolean isMetronomeOn ();


    /**
     * Toggle the metronome on/off.
     */
    void toggleMetronome ();


    /**
     * Returns true if the metronome ticks option is on.
     *
     * @return True if the metronome ticks option is on
     */
    boolean isMetronomeTicksOn ();


    /**
     * Toggles if the metronome has tick playback enabled.
     */
    void toggleMetronomeTicks ();


    /**
     * Get the metronome volume as a formatted string.
     *
     * @return The formatted volume
     */
    String getMetronomeVolumeStr ();


    /**
     * Change the metronome volume.
     *
     * @param control The control value
     */
    void changeMetronomeVolume (final int control);


    /**
     * Set the metronome volume.
     *
     * @param value The value
     */
    void setMetronomeVolume (final double value);


    /**
     * Get the metronome volume.
     *
     * @return The value
     */
    int getMetronomeVolume ();


    /**
     * Should the metronome be audible during pre-roll?
     *
     * @return True if the metronome should be audible during pre-roll
     */
    boolean isPrerollMetronomeEnabled ();


    /**
     * Toggle if the metronome should be audible during pre-roll.
     */
    void togglePrerollMetronome ();


    /**
     * Dis-/enable the arranger loop.
     *
     * @param on True if on
     */
    void setLoop (final boolean on);


    /**
     * Toggle the arranger loop.
     */
    void toggleLoop ();


    /**
     * Returns true if the arranger loop is on.
     *
     * @return True if on
     */
    boolean isLoop ();


    /**
     * Returns true if writing clip launcher automation is on.
     *
     * @return True if writing clip launcher automation is on
     */
    boolean isWritingClipLauncherAutomation ();


    /**
     * Returns true if writing arranger automation is on.
     *
     * @return True if writing arranger automation is on
     */
    boolean isWritingArrangerAutomation ();


    /**
     * Get the automation write mode.
     *
     * @return The automation write mode (latch , touch, write)
     */
    String getAutomationWriteMode ();


    /**
     * Set the automation write mode.
     *
     * @param mode The automation write mode (latch , touch, write)
     */
    void setAutomationWriteMode (final String mode);


    /**
     * Toggles the arranger automation write enabled state of the transport.
     */
    void toggleWriteArrangerAutomation ();


    /**
     * Toggles the clip launcher automation write enabled state of the transport.
     */
    void toggleWriteClipLauncherAutomation ();


    /**
     * Resets any automation overrides.
     */
    void resetAutomationOverrides ();


    /**
     * Switches playback to the arrangement sequencer on all tracks.
     */
    void returnToArrangement ();


    /**
     * Get the default formatted play position (Minutes, Seconds, ...).
     *
     * @return The formatted text
     */
    String getPositionText ();


    /**
     * Get the play position formatted as measures and beats.
     *
     * @return The formatted text
     */
    String getBeatText ();


    /**
     * Sets the transport playback position to the given beat time value.
     *
     * @param beats The new playback position in beats
     */
    void setPosition (final double beats);


    /**
     * Changes the play position.
     *
     * @param increase If true move to the right otherwise left
     */
    void changePosition (final boolean increase);


    /**
     * Changes the play position.
     *
     * @param increase If true move to the right otherwise left
     * @param slow Change slowly
     */
    void changePosition (final boolean increase, final boolean slow);


    /**
     * Set punch-in dis/enabled in the transport.
     *
     * @param enable True to enable
     */
    void setPunchIn (boolean enable);


    /**
     * Toggle punch-in enabled in the transport.
     */
    void togglePunchIn ();


    /**
     * Is punch-in enabled in the transport?
     *
     * @return True if punch-in is enabled in the transport
     */
    boolean isPunchInEnabled ();


    /**
     * Set punch-out dis/enabled in the transport.
     *
     * @param enable True to enable
     */
    void setPunchOut (boolean enable);


    /**
     * Is punch-out enabled in the transport?
     */
    void togglePunchOut ();


    /**
     * Is punch-out enabled in the transport?
     *
     * @return True if punch-out is enabled in the transport
     */
    boolean isPunchOutEnabled ();


    /**
     * Tap the tempo.
     */
    void tapTempo ();


    /**
     * Changes the tempo.
     *
     * @param increase True to increase otherwise decrease
     */
    void changeTempo (final boolean increase);


    /**
     * Set the tempo.
     *
     * @param tempo The tempo in BPM
     */
    void setTempo (final double tempo);


    /**
     * Get the tempo.
     *
     * @return The tempo in BPM
     */
    double getTempo ();


    /**
     * Format the tempo with 2 fractions.
     *
     * @param tempo The tempo to format
     * @return The formatted tempo
     */
    String formatTempo (final double tempo);


    /**
     * Format the tempo with 2 fractions.
     *
     * @param tempo The tempo to format
     * @return The formatted tempo
     */
    String formatTempoNoFraction (final double tempo);


    /**
     * Specifies if this value should be indicated as mapped.
     *
     * @param isTouched True if touched
     */
    void setTempoIndication (final boolean isTouched);


    /**
     * Set the position of the crossfader.
     *
     * @param value The value
     */
    void setCrossfade (final double value);


    /**
     * Get the position of the crossfader.
     *
     * @return The position
     */
    int getCrossfade ();


    /**
     * Change the crossfade.
     *
     * @param control The control value
     */
    void changeCrossfade (final int control);


    /**
     * Get the value that reports the current pre-roll setting. Possible values are `"none"`,
     * `"one_bar"`, `"two_bars"`, or `"four_bars"`.
     *
     * @return The value
     */
    String getPreroll ();


    /**
     * Get the value that reports the current pre-roll setting in bars: 0, 1, 2, 4.
     *
     * @return The number of preroll bars.
     */
    int getPrerollAsBars ();


    /**
     * Set the value that reports the current pre-roll setting.
     *
     * @param preroll Possible values are `"none"`, `"one_bar"`, `"two_bars"`, or `"four_bars"`.
     */
    void setPreroll (final String preroll);


    /**
     * Set the value that reports the current pre-roll setting in bars.
     *
     * @param preroll Possible values are 0, 1, 2, 4.
     */
    void setPrerollAsBars (final int preroll);


    /**
     * Get the numerator of the time signature.
     *
     * @return The numerator
     */
    int getNumerator ();


    /**
     * Get the denominator of the time signature.
     *
     * @return The denominator
     */
    int getDenominator ();


    /**
     * Get the quarters per measure calculated from the numerator and denominator.
     *
     * @return The quarters per measure.
     */
    int getQuartersPerMeasure ();
}