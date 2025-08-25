// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import java.util.Arrays;

import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.daw.constants.LaunchQuantization;
import de.mossgrabers.framework.daw.constants.PostRecordingAction;
import de.mossgrabers.framework.observer.IObserverManagement;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Interface to the Transport instance.
 *
 * @author Jürgen Moßgraber
 */
public interface ITransport extends IObserverManagement
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
    void startRecording ();


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
    void setLauncherOverdub (boolean on);


    /**
     * Toggle if the clip launcher overdub is enabled.
     */
    void toggleLauncherOverdub ();


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
     * Disable/enables the metronome.
     *
     * @param on Turn on if true
     */
    void setMetronome (boolean on);


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
     * Dis-/enables the metronome ticks.
     *
     * @param on Turn on if true
     */
    void setMetronomeTicks (boolean on);


    /**
     * Get the metronome volume parameter.
     *
     * @return The metronome volume parameter
     */
    IParameter getMetronomeVolumeParameter ();


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
    void changeMetronomeVolume (int control);


    /**
     * Set the metronome volume.
     *
     * @param value The value
     */
    void setMetronomeVolume (int value);


    /**
     * Get the metronome volume.
     *
     * @return The value
     */
    int getMetronomeVolume ();


    /**
     * Dis-/enable the arranger loop.
     *
     * @param on True if on
     */
    void setLoop (boolean on);


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
     * Get the automation mode parameter.
     *
     * @return The automation mode parameter
     */
    IParameter getAutomationModeParameter ();


    /**
     * Get the supported automation modes.
     *
     * @return The supported automation modes
     */
    AutomationMode [] getAutomationWriteModes ();


    /**
     * Get the automation write mode.
     *
     * @return The automation write mode
     */
    AutomationMode getAutomationWriteMode ();


    /**
     * Set the automation write mode.
     *
     * @param mode The automation write mode
     */
    void setAutomationWriteMode (AutomationMode mode);


    /**
     * Select the previous automation write mode.
     */
    default void previousAutomationWriteMode ()
    {
        final AutomationMode [] automationWriteModes = this.getAutomationWriteModes ();
        final AutomationMode automationWriteMode = this.getAutomationWriteMode ();

        int pos = Arrays.asList (automationWriteModes).indexOf (automationWriteMode) - 1;
        if (pos < 0)
            pos = 0;

        this.setAutomationWriteMode (automationWriteModes[pos]);
    }


    /**
     * Select the next automation write mode.
     */
    default void nextAutomationWriteMode ()
    {
        this.nextAutomationWriteMode (true);
    }


    /**
     * Select the next automation write mode.
     *
     * @param wrap If true wrap to first when at the end
     */
    default void nextAutomationWriteMode (final boolean wrap)
    {
        final AutomationMode [] automationWriteModes = this.getAutomationWriteModes ();
        final AutomationMode automationWriteMode = this.getAutomationWriteMode ();

        int pos = Arrays.asList (automationWriteModes).indexOf (automationWriteMode) + 1;
        if (pos >= automationWriteModes.length)
            pos = wrap ? 0 : automationWriteModes.length - 1;

        this.setAutomationWriteMode (automationWriteModes[pos]);
    }


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
     * Set the transport playback position to the given beat time value.
     *
     * @param beats The new playback position in beats
     */
    void setPosition (double beats);


    /**
     * Get the transport playback position.
     *
     * @return The playback position in beats
     */
    double getPosition ();


    /**
     * Set the transport playback position to the end of the project.
     */
    void setPositionToEnd ();


    /**
     * Changes the play position.
     *
     * @param increase If true move to the right otherwise left
     * @param slow Change slowly
     */
    void changePosition (boolean increase, boolean slow);


    /**
     * Get the loop start position formatted as measures and beats.
     *
     * @return The formatted text
     */
    String getLoopStartBeatText ();


    /**
     * Changes the loop start position.
     *
     * @param increase If true move to the right otherwise left
     * @param slow Change slowly
     */
    void changeLoopStart (boolean increase, boolean slow);


    /**
     * Set the play position to the start of the arranger loop, if a loop is set.
     */
    void selectLoopStart ();


    /**
     * Get the loop start position.
     *
     * @return The loop start position in beats
     */
    double getLoopStart ();


    /**
     * Get the loop end position.
     *
     * @return The loop end position in beats
     */
    double getLoopEnd ();


    /**
     * Set the play position to the end of the arranger loop, if a loop is set.
     */
    void selectLoopEnd ();


    /**
     * Get the loop length formatted as measures and beats.
     *
     * @return The formatted text
     */
    String getLoopLengthBeatText ();


    /**
     * Changes the loop length position.
     *
     * @param increase If true move to the right otherwise left
     * @param slow Change slowly
     */
    void changeLoopLength (boolean increase, boolean slow);


    /**
     * Set punch-in dis-/enabled in the transport.
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
     * @param slow Change slowly
     */
    void changeTempo (boolean increase, boolean slow);


    /**
     * Set the tempo.
     *
     * @param tempo The tempo in BPM
     */
    void setTempo (double tempo);


    /**
     * Get the tempo.
     *
     * @return The tempo in BPM
     */
    double getTempo ();


    /**
     * Get the minimum tempo supported by the DAW.
     *
     * @return The minimum tempo in BPM
     */
    double getMinimumTempo ();


    /**
     * Get the maximum tempo supported by the DAW.
     *
     * @return The maximum tempo in BPM
     */
    double getMaximumTempo ();


    /**
     * Format the tempo with 2 fractions.
     *
     * @param tempo The tempo to format
     * @return The formatted tempo
     */
    String formatTempo (double tempo);


    /**
     * Format the tempo with 2 fractions.
     *
     * @param tempo The tempo to format
     * @return The formatted tempo
     */
    String formatTempoNoFraction (double tempo);


    /**
     * Scale the tempo (in the range from MIN_TEMPO and MAX_TEMPO to the range of 0 to maxValue.
     *
     * @param tempo The tempo to scale
     * @param maxValue The upper bound
     * @return The rescaled tempo
     */
    double scaleTempo (double tempo, int maxValue);


    /**
     * Specifies if this value should be indicated as mapped.
     *
     * @param isTouched True if touched
     */
    void setTempoIndication (boolean isTouched);


    /**
     * Get the crossfade parameter.
     *
     * @return The tempo parameter
     */
    IParameter getCrossfadeParameter ();


    /**
     * Set the position of the crossfader.
     *
     * @param value The value
     */
    void setCrossfade (int value);


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
    void changeCrossfade (int control);


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
     * Get the value that reports the current pre-roll setting in bars: 0, 1, 2, 4.
     *
     * @return The number of preroll bars.
     */
    int getPrerollMeasures ();


    /**
     * Set the value that reports the current pre-roll setting in bars.
     *
     * @param preroll Possible values are 0, 1, 2, 4.
     */
    void setPrerollMeasures (int preroll);


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


    /**
     * Get the clip launcher post recording action.
     *
     * @return The clip launcher post recording action
     */
    PostRecordingAction getClipLauncherPostRecordingAction ();


    /**
     * Set the clip launcher post recording action.
     *
     * @param action The action
     */
    void setClipLauncherPostRecordingAction (final PostRecordingAction action);


    /**
     * Get the clip launcher post recording time offset.
     *
     * @return The number of beats
     */
    double getClipLauncherPostRecordingTimeOffset ();


    /**
     * Set the clip launcher post recording time offset.
     *
     * @param beats The number of beats
     */
    void setClipLauncherPostRecordingTimeOffset (final double beats);


    /**
     * Get the default launch quantization.
     *
     * @return The default launch quantization
     */
    LaunchQuantization getDefaultLaunchQuantization ();


    /**
     * Set the default launch quantization.
     *
     * @param launchQuantization The default launch quantization
     */
    void setDefaultLaunchQuantization (final LaunchQuantization launchQuantization);


    /**
     * Select the next automation write mode.
     */
    default void nextLaunchQuantization ()
    {
        final LaunchQuantization [] launchQuantizations = LaunchQuantization.values ();
        final LaunchQuantization launchQuantization = this.getDefaultLaunchQuantization ();

        int pos = Arrays.asList (launchQuantizations).indexOf (launchQuantization) + 1;
        if (pos >= launchQuantizations.length)
            pos = 0;

        this.setDefaultLaunchQuantization (launchQuantizations[pos]);
    }


    /**
     * Is the fill mode active?
     *
     * @return True if active
     */
    boolean isFillModeActive ();


    /**
     * Set the fill mode active.
     *
     * @param isActive True to activate
     */
    void setFillModeActive (boolean isActive);


    /**
     * Toggle the fill mode.
     */
    void toggleFillModeActive ();
}