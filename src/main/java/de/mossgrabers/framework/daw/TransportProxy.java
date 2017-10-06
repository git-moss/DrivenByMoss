// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.ValueChanger;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableRangedValue;
import com.bitwig.extension.controller.api.TimeSignatureValue;
import com.bitwig.extension.controller.api.Transport;

import java.text.DecimalFormat;


/**
 * Encapsulates the Transport instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportProxy
{
    /** The names for automation modes. */
    public static final String [] AUTOMATION_MODES        =
    {
        "Latch",
        "Touch",
        "Write"
    };

    /** The names for automation modes values. */
    public static final String [] AUTOMATION_MODES_VALUES =
    {
        "latch",
        "touch",
        "write"
    };

    /** No preroll. */
    public static final String    PREROLL_NONE            = "none";
    /** 1 bar preroll. */
    public static final String    PREROLL_1_BAR           = "one_bar";
    /** 2 bar preroll. */
    public static final String    PREROLL_2_BARS          = "two_bars";
    /** 4 bar preroll. */
    public static final String    PREROLL_4_BARS          = "four_bars";

    /** 1 beat. */
    private static final double   INC_FRACTION_TIME       = 1.0;
    /** 1/20th of a beat. */
    private static final double   INC_FRACTION_TIME_SLOW  = 1.0 / 20;
    private static final int      TEMPO_MIN               = 20;
    private static final int      TEMPO_MAX               = 666;

    private ControllerHost        host;
    private ValueChanger          valueChanger;
    private Transport             transport;

    private int                   crossfade               = 0;
    private double                tempo;


    /**
     * Constructor
     *
     * @param host The host
     * @param valueChanger The value changer
     */
    public TransportProxy (final ControllerHost host, final ValueChanger valueChanger)
    {
        this.host = host;
        this.valueChanger = valueChanger;
        this.transport = host.createTransport ();

        this.transport.isPlaying ().markInterested ();
        this.transport.isArrangerRecordEnabled ().markInterested ();
        this.transport.isArrangerOverdubEnabled ().markInterested ();
        this.transport.isClipLauncherAutomationWriteEnabled ().markInterested ();
        this.transport.isClipLauncherOverdubEnabled ().markInterested ();
        this.transport.isArrangerAutomationWriteEnabled ().markInterested ();
        this.transport.isAutomationOverrideActive ().markInterested ();
        this.transport.automationWriteMode ().markInterested ();
        this.transport.isArrangerLoopEnabled ().markInterested ();
        this.transport.isPunchInEnabled ().markInterested ();
        this.transport.isPunchOutEnabled ().markInterested ();
        this.transport.isMetronomeEnabled ().markInterested ();
        this.transport.isMetronomeTickPlaybackEnabled ().markInterested ();
        this.transport.isMetronomeAudibleDuringPreRoll ().markInterested ();
        this.transport.preRoll ().markInterested ();
        this.transport.tempo ().value ().addRawValueObserver (this::handleTempo);
        this.transport.getPosition ().markInterested ();
        this.transport.getCrossfade ().value ().addValueObserver (valueChanger.getUpperBound (), this::handleCrossfade);

        final SettableRangedValue metronomeVolume = this.transport.metronomeVolume ();
        metronomeVolume.markInterested ();
        metronomeVolume.displayedValue ().markInterested ();

        final TimeSignatureValue ts = this.transport.getTimeSignature ();
        ts.getNumerator ().markInterested ();
        ts.getDenominator ().markInterested ();
    }


    /**
     * Dis-/Enable all attributes. They are enabled by default. Use this function if values are
     * currently not needed to improve performance.
     *
     * @param enable True to enable
     */
    public void enableObservers (final boolean enable)
    {
        this.transport.isPlaying ().setIsSubscribed (enable);
        this.transport.isArrangerRecordEnabled ().setIsSubscribed (enable);
        this.transport.isArrangerOverdubEnabled ().setIsSubscribed (enable);
        this.transport.isClipLauncherAutomationWriteEnabled ().setIsSubscribed (enable);
        this.transport.isClipLauncherOverdubEnabled ().setIsSubscribed (enable);
        this.transport.isArrangerAutomationWriteEnabled ().setIsSubscribed (enable);
        this.transport.isAutomationOverrideActive ().setIsSubscribed (enable);
        this.transport.automationWriteMode ().setIsSubscribed (enable);
        this.transport.isArrangerLoopEnabled ().setIsSubscribed (enable);
        this.transport.isPunchInEnabled ().setIsSubscribed (enable);
        this.transport.isPunchOutEnabled ().setIsSubscribed (enable);
        this.transport.isMetronomeEnabled ().setIsSubscribed (enable);
        this.transport.isMetronomeTickPlaybackEnabled ().setIsSubscribed (enable);
        this.transport.isMetronomeAudibleDuringPreRoll ().setIsSubscribed (enable);
        this.transport.preRoll ().setIsSubscribed (enable);
        this.transport.tempo ().value ().setIsSubscribed (enable);
        this.transport.getPosition ().setIsSubscribed (enable);
        this.transport.getCrossfade ().value ().setIsSubscribed (enable);

        final SettableRangedValue metronomeVolume = this.transport.metronomeVolume ();
        metronomeVolume.setIsSubscribed (enable);
        metronomeVolume.displayedValue ().setIsSubscribed (enable);

        final TimeSignatureValue ts = this.transport.getTimeSignature ();
        ts.getNumerator ().setIsSubscribed (enable);
        ts.getDenominator ().setIsSubscribed (enable);
    }


    /**
     * Start the playback.
     */
    public void play ()
    {
        this.transport.play ();
    }


    /**
     * Toggle the playback.
     */
    public void togglePlay ()
    {
        this.transport.togglePlay ();
    }


    /**
     * Returns true if playing.
     *
     * @return True if playing
     */
    public boolean isPlaying ()
    {
        return this.transport.isPlaying ().get ();
    }


    /**
     * Restart the playback. When the transport is stopped, calling this function starts transport
     * playback, otherwise the transport is first stopped and the playback is restarted from the
     * last play-start position.
     */
    public void restart ()
    {
        this.transport.restart ();
    }


    /**
     * Stop the playback.
     */
    public void stop ()
    {
        this.transport.stop ();
    }


    /**
     * Stops the playback and moves the play position to the start of the arrangement.
     */
    public void stopAndRewind ()
    {
        this.transport.stop ();
        // Delay the position movement to make sure that the playback is really stopped
        this.host.scheduleTask ( () -> this.transport.setPosition (0), 100);
    }


    /**
     * Start arranger recording.
     */
    public void record ()
    {
        this.transport.record ();
    }


    /**
     * Returns true if arranger recording is enabled.
     *
     * @return True if arranger recording is enabled
     */
    public boolean isRecording ()
    {
        return this.transport.isArrangerRecordEnabled ().get ();
    }


    /**
     * Forwards the play position.
     */
    public void fastForward ()
    {
        this.transport.fastForward ();
    }


    /**
     * Rewinds the play position.
     */
    public void rewind ()
    {
        this.transport.rewind ();
    }


    /**
     * Returns true if arranger overdub is enabled.
     *
     * @return True if arranger overdub is enabled
     */
    public boolean isArrangerOverdub ()
    {
        return this.transport.isArrangerOverdubEnabled ().get ();
    }


    /**
     * Toggle if arranger overdub is enabled.
     */
    public void toggleOverdub ()
    {
        this.transport.isArrangerOverdubEnabled ().toggle ();
    }


    /**
     * Returns true if clip launcher overdub is enabled.
     *
     * @return True if clip launcher overdub is enabled
     */
    public boolean isLauncherOverdub ()
    {
        return this.transport.isClipLauncherOverdubEnabled ().get ();
    }


    /**
     * Set if the clip launcher overdub is enabled.
     *
     * @param on True if clip launcher overdub is enabled
     */
    public void setLauncherOverdub (final boolean on)
    {
        this.transport.isClipLauncherOverdubEnabled ().set (on);
    }


    /**
     * Toggle if the clip launcher overdub is enabled.
     */
    public void toggleLauncherOverdub ()
    {
        this.transport.isClipLauncherOverdubEnabled ().toggle ();
    }


    /**
     * Dis-/enables the metronome.
     *
     * @param on Turn on if true
     */
    public void setMetronome (final boolean on)
    {
        this.transport.isMetronomeEnabled ().set (on);
    }


    /**
     * Returns true if the metronome is on.
     *
     * @return True if the metronome is on
     */
    public boolean isMetronomeOn ()
    {
        return this.transport.isMetronomeEnabled ().get ();
    }


    /**
     * Toggle the metronome on/off.
     */
    public void toggleMetronome ()
    {
        this.transport.isMetronomeEnabled ().toggle ();
    }


    /**
     * Returns true if the metronome ticks option is on.
     *
     * @return True if the metronome ticks option is on
     */
    public boolean isMetronomeTicksOn ()
    {
        return this.transport.isMetronomeTickPlaybackEnabled ().get ();
    }


    /**
     * Toggles if the metronome has tick playback enabled.
     */
    public void toggleMetronomeTicks ()
    {
        this.transport.isMetronomeTickPlaybackEnabled ().toggle ();
    }


    /**
     * Get the metronome volume as a formatted string.
     *
     * @return The formatted volume
     */
    public String getMetronomeVolumeStr ()
    {
        return this.transport.metronomeVolume ().displayedValue ().getLimited (6) + " dB";
    }


    /**
     * Change the metronome volume.
     *
     * @param control The control value
     */
    public void changeMetronomeVolume (final int control)
    {
        this.transport.metronomeVolume ().inc (this.valueChanger.calcKnobSpeed(control), this.valueChanger.getUpperBound());
    }


    /**
     * Set the metronome volume.
     *
     * @param value The new value
     */
    public void setMetronomeVolume (final int value)
    {
        this.transport.metronomeVolume ().set (value, this.valueChanger.getUpperBound());
    }


    /**
     * Should the metronome be audible during pre-roll?
     *
     * @return True if the metronome should be audible during pre-roll
     */
    public boolean isPrerollMetronomeEnabled ()
    {
        return this.transport.isMetronomeAudibleDuringPreRoll ().get ();
    }


    /**
     * Toggle if the metronome should be audible during pre-roll.
     */
    public void togglePrerollMetronome ()
    {
        this.transport.isMetronomeAudibleDuringPreRoll ().toggle ();
    }


    /**
     * Dis-/enable the arranger loop.
     *
     * @param on True if on
     */
    public void setLoop (final boolean on)
    {
        this.transport.isArrangerLoopEnabled ().set (on);
    }


    /**
     * Toggle the arranger loop.
     */
    public void toggleLoop ()
    {
        this.transport.isArrangerLoopEnabled ().toggle ();
    }


    /**
     * Returns true if the arranger loop is on.
     *
     * @return True if on
     */
    public boolean isLoop ()
    {
        return this.transport.isArrangerLoopEnabled ().get ();
    }


    /**
     * Returns true if writing clip launcher automation is on.
     *
     * @return True if writing clip launcher automation is on
     */
    public boolean isWritingClipLauncherAutomation ()
    {
        return this.transport.isClipLauncherAutomationWriteEnabled ().get ();
    }


    /**
     * Returns true if writing arranger automation is on.
     *
     * @return True if writing arranger automation is on
     */
    public boolean isWritingArrangerAutomation ()
    {
        return this.transport.isArrangerAutomationWriteEnabled ().get ();
    }


    /**
     * Returns true if automation override is on.
     *
     * @return True if automation override is on
     */
    public boolean isAutomationOverride ()
    {
        return this.transport.isAutomationOverrideActive ().get ();
    }


    /**
     * Get the automation write mode.
     *
     * @return The automation write mode (latch , touch, write)
     */
    public String getAutomationWriteMode ()
    {
        return this.transport.automationWriteMode ().get ();
    }


    /**
     * Set the automation write mode.
     *
     * @param mode The automation write mode (latch , touch, write)
     */
    public void setAutomationWriteMode (final String mode)
    {
        this.transport.automationWriteMode ().set (mode);
    }


    /**
     * Toggles the arranger automation write enabled state of the transport.
     */
    public void toggleWriteArrangerAutomation ()
    {
        this.transport.toggleWriteArrangerAutomation ();
    }


    /**
     * Toggles the clip launcher automation write enabled state of the transport.
     */
    public void toggleWriteClipLauncherAutomation ()
    {
        this.transport.toggleWriteClipLauncherAutomation ();
    }


    /**
     * Resets any automation overrides.
     */
    public void resetAutomationOverrides ()
    {
        this.transport.resetAutomationOverrides ();
    }


    /**
     * Switches playback to the arrangement sequencer on all tracks.
     */
    public void returnToArrangement ()
    {
        this.transport.returnToArrangement ();
    }


    /**
     * Get the default formatted play position.
     *
     * @return The formatted text
     */
    public String getPositionText ()
    {
        return this.transport.getPosition ().getFormatted ();
    }


    /**
     * Sets the transport playback position to the given beat time value.
     *
     * @param beats The new playback position in beats
     */
    public void setPosition (final double beats)
    {
        this.transport.setPosition (beats);
    }


    /**
     * Changes the play position.
     *
     * @param increase If true move to the right otherwise left
     */
    public void changePosition (final boolean increase)
    {
        this.changePosition (increase, this.valueChanger.isSlow ());
    }


    /**
     * Changes the play position.
     *
     * @param increase If true move to the right otherwise left
     * @param slow Change slowly
     */
    public void changePosition (final boolean increase, final boolean slow)
    {
        final double frac = slow ? INC_FRACTION_TIME_SLOW : INC_FRACTION_TIME;
        this.transport.incPosition (increase ? frac : -frac, false);
    }


    /**
     * Toggle punch-in enabled in the transport.
     */
    public void togglePunchIn ()
    {
        this.transport.isPunchInEnabled ().toggle ();
    }


    /**
     * Is punch-in enabled in the transport?
     *
     * @return True if punch-in is enabled in the transport
     */
    public boolean isPunchInEnabled ()
    {
        return this.transport.isPunchInEnabled ().get ();
    }


    /**
     * Is punch-out enabled in the transport?
     */
    public void togglePunchOut ()
    {
        this.transport.isPunchOutEnabled ().toggle ();
    }


    /**
     * Is punch-out enabled in the transport?
     *
     * @return True if punch-out is enabled in the transport
     */
    public boolean isPunchOutEnabled ()
    {
        return this.transport.isPunchOutEnabled ().get ();
    }


    /**
     * Tap the tempo.
     */
    public void tapTempo ()
    {
        this.transport.tapTempo ();
    }


    /**
     * Changes the tempo.
     *
     * @param increase True to increase otherwise decrease
     */
    public void changeTempo (final boolean increase)
    {
        final double offset = this.valueChanger.isSlow () ? 0.01 : 1;
        this.transport.tempo ().incRaw (increase ? offset : -offset);
    }


    /**
     * Set the tempo.
     *
     * @param tempo The tempo in BPM
     */
    public void setTempo (final double tempo)
    {
        this.transport.tempo ().setRaw (tempo);
    }


    /**
     * Get the tempo.
     *
     * @return The tempo in BPM
     */
    public double getTempo ()
    {
        return this.tempo;
    }


    /**
     * Format the tempo with 2 fractions.
     *
     * @param tempo The tempo to format
     * @return The formatted tempo
     */
    public String formatTempo (final double tempo)
    {
        return new DecimalFormat ("#.00").format (tempo);
    }


    /**
     * Format the tempo with 2 fractions.
     *
     * @param tempo The tempo to format
     * @return The formatted tempo
     */
    public String formatTempoNoFraction (final double tempo)
    {
        return new DecimalFormat ("###").format (tempo);
    }


    /**
     * Specifies if this value should be indicated as mapped.
     *
     * @param isTouched True if touched
     */
    public void setTempoIndication (final boolean isTouched)
    {
        this.transport.tempo ().setIndication (isTouched);
    }


    /**
     * Set the position of the crossfader.
     *
     * @param value The value
     */
    public void setCrossfade (final int value)
    {
        this.transport.getCrossfade ().set (value, this.valueChanger.getUpperBound());
    }


    /**
     * Get the position of the crossfader.
     *
     * @return The position
     */
    public int getCrossfade ()
    {
        return this.crossfade;
    }


    /**
     * Change the crossfade.
     *
     * @param control The control value
     */
    public void changeCrossfade (final int control)
    {
        this.transport.getCrossfade ().inc (this.valueChanger.calcKnobSpeed(control), this.valueChanger.getUpperBound());
    }


    /**
     * Get the value that reports the current pre-roll setting. Possible values are `"none"`,
     * `"one_bar"`, `"two_bars"`, or `"four_bars"`.
     *
     * @return The value
     */
    public String getPreroll ()
    {
        return this.transport.preRoll ().get ();
    }


    /**
     * Set the value that reports the current pre-roll setting.
     *
     * @param preroll Possible values are `"none"`, `"one_bar"`, `"two_bars"`, or `"four_bars"`.
     */
    public void setPreroll (final String preroll)
    {
        this.transport.preRoll ().set (preroll);
    }


    /**
     * Get the numerator of the time signature.
     *
     * @return The numerator
     */
    public int getNumerator ()
    {
        return this.transport.getTimeSignature ().getNumerator ().get ();
    }


    /**
     * Get the denominator of the time signature.
     *
     * @return The denominator
     */
    public int getDenominator ()
    {

        return this.transport.getTimeSignature ().getDenominator ().get ();
    }


    private void handleTempo (final double value)
    {
        this.tempo = Math.min (TransportProxy.TEMPO_MAX, Math.max (TransportProxy.TEMPO_MIN, value));
    }


    private void handleCrossfade (final int value)
    {
        this.crossfade = value;
    }
}