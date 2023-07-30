// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.ParameterImpl;
import de.mossgrabers.bitwig.framework.daw.data.RangedValueImpl;
import de.mossgrabers.bitwig.framework.daw.data.RawParameterImpl;
import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.daw.constants.LaunchQuantization;
import de.mossgrabers.framework.daw.constants.PostRecordingAction;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.utils.StringUtils;

import com.bitwig.extension.controller.api.BeatTimeFormatter;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.TimeSignatureValue;
import com.bitwig.extension.controller.api.Transport;

import java.text.DecimalFormat;


/**
 * Encapsulates the Transport instance.
 *
 * @author Jürgen Moßgraber
 */
public class TransportImpl implements ITransport
{
    /** No preroll. */
    private static final String            PREROLL_NONE            = "none";
    /** 1 bar preroll. */
    private static final String            PREROLL_1_BAR           = "one_bar";
    /** 2 bar preroll. */
    private static final String            PREROLL_2_BARS          = "two_bars";
    /** 4 bar preroll. */
    private static final String            PREROLL_4_BARS          = "four_bars";

    private static final String            ACTION_JUMP_TO_END      = "jump_to_end_of_arrangement";

    private static final AutomationMode [] AUTOMATION_MODES        = new AutomationMode []
    {
        AutomationMode.READ,
        AutomationMode.LATCH,
        AutomationMode.TOUCH,
        AutomationMode.WRITE
    };

    private static final BeatTimeFormatter BEAT_POSITION_FORMATTER = formatAsBeats (1);
    private static final BeatTimeFormatter BEAT_LENGTH_FORMATTER   = formatAsBeats (0);

    private final ControllerHost           host;
    private final IApplication             application;
    private final IValueChanger            valueChanger;
    private final Transport                transport;

    private final RawParameterImpl         tempoParameter;
    private final IParameter               crossfadeParameter;
    private final IParameter               metronomeVolumeParameter;


    /**
     * Constructor
     *
     * @param host The host
     * @param application The application
     * @param valueChanger The value changer
     */
    public TransportImpl (final ControllerHost host, final IApplication application, final IValueChanger valueChanger)
    {
        this.host = host;
        this.application = application;
        this.valueChanger = valueChanger;
        this.transport = host.createTransport ();

        this.transport.isPlaying ().markInterested ();
        this.transport.isArrangerRecordEnabled ().markInterested ();
        this.transport.isArrangerOverdubEnabled ().markInterested ();
        this.transport.isClipLauncherAutomationWriteEnabled ().markInterested ();
        this.transport.isClipLauncherOverdubEnabled ().markInterested ();
        this.transport.isArrangerAutomationWriteEnabled ().markInterested ();
        this.transport.automationWriteMode ().markInterested ();
        this.transport.isArrangerLoopEnabled ().markInterested ();
        this.transport.isPunchInEnabled ().markInterested ();
        this.transport.isPunchOutEnabled ().markInterested ();
        this.transport.isMetronomeEnabled ().markInterested ();
        this.transport.isMetronomeTickPlaybackEnabled ().markInterested ();
        this.transport.isMetronomeAudibleDuringPreRoll ().markInterested ();
        this.transport.preRoll ().markInterested ();
        this.transport.getPosition ().markInterested ();
        this.transport.playStartPosition ().markInterested ();
        this.transport.arrangerLoopStart ().markInterested ();
        this.transport.arrangerLoopDuration ().markInterested ();
        this.transport.clipLauncherPostRecordingAction ().markInterested ();
        this.transport.getClipLauncherPostRecordingTimeOffset ().markInterested ();
        this.transport.defaultLaunchQuantization ().markInterested ();
        this.transport.isFillModeActive ().markInterested ();

        this.crossfadeParameter = new ParameterImpl (valueChanger, this.transport.crossfade ());
        this.metronomeVolumeParameter = new RangedValueImpl ("Metronome Volume", valueChanger, this.transport.metronomeVolume ());
        this.tempoParameter = new RawParameterImpl (valueChanger, this.transport.tempo (), TransportConstants.MIN_TEMPO, TransportConstants.MAX_TEMPO);

        final TimeSignatureValue ts = this.transport.timeSignature ();
        ts.numerator ().markInterested ();
        ts.denominator ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.transport.isPlaying (), enable);
        Util.setIsSubscribed (this.transport.isArrangerRecordEnabled (), enable);
        Util.setIsSubscribed (this.transport.isArrangerOverdubEnabled (), enable);
        Util.setIsSubscribed (this.transport.isClipLauncherAutomationWriteEnabled (), enable);
        Util.setIsSubscribed (this.transport.isClipLauncherOverdubEnabled (), enable);
        Util.setIsSubscribed (this.transport.isArrangerAutomationWriteEnabled (), enable);
        Util.setIsSubscribed (this.transport.automationWriteMode (), enable);
        Util.setIsSubscribed (this.transport.isArrangerLoopEnabled (), enable);
        Util.setIsSubscribed (this.transport.isPunchInEnabled (), enable);
        Util.setIsSubscribed (this.transport.isPunchOutEnabled (), enable);
        Util.setIsSubscribed (this.transport.isMetronomeEnabled (), enable);
        Util.setIsSubscribed (this.transport.isMetronomeTickPlaybackEnabled (), enable);
        Util.setIsSubscribed (this.transport.isMetronomeAudibleDuringPreRoll (), enable);
        Util.setIsSubscribed (this.transport.preRoll (), enable);
        Util.setIsSubscribed (this.transport.getPosition (), enable);
        Util.setIsSubscribed (this.transport.playStartPosition (), enable);
        Util.setIsSubscribed (this.transport.arrangerLoopStart (), enable);
        Util.setIsSubscribed (this.transport.arrangerLoopDuration (), enable);
        Util.setIsSubscribed (this.transport.clipLauncherPostRecordingAction (), enable);
        Util.setIsSubscribed (this.transport.getClipLauncherPostRecordingTimeOffset (), enable);
        Util.setIsSubscribed (this.transport.defaultLaunchQuantization (), enable);
        Util.setIsSubscribed (this.transport.isFillModeActive (), enable);

        this.crossfadeParameter.enableObservers (enable);
        this.metronomeVolumeParameter.enableObservers (enable);
        this.tempoParameter.enableObservers (enable);

        final TimeSignatureValue ts = this.transport.timeSignature ();
        Util.setIsSubscribed (ts.numerator (), enable);
        Util.setIsSubscribed (ts.denominator (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public void play ()
    {
        this.transport.play ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaying ()
    {
        return this.transport.isPlaying ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void restart ()
    {
        this.transport.restart ();
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        this.transport.stop ();
    }


    /** {@inheritDoc} */
    @Override
    public void stopAndRewind ()
    {
        this.transport.stop ();
        // Delay the position movement to make sure that the playback is really stopped
        this.host.scheduleTask ( () -> this.transport.setPosition (0), 100);
    }


    /** {@inheritDoc} */
    @Override
    public void startRecording ()
    {
        this.transport.record ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecording ()
    {
        return this.transport.isArrangerRecordEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isArrangerOverdub ()
    {
        return this.transport.isArrangerOverdubEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleOverdub ()
    {
        this.transport.isArrangerOverdubEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLauncherOverdub ()
    {
        return this.transport.isClipLauncherOverdubEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLauncherOverdub (final boolean on)
    {
        this.transport.isClipLauncherOverdubEnabled ().set (on);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLauncherOverdub ()
    {
        this.transport.isClipLauncherOverdubEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMetronome (final boolean on)
    {
        this.transport.isMetronomeEnabled ().set (on);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMetronomeOn ()
    {
        return this.transport.isMetronomeEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMetronome ()
    {
        this.transport.isMetronomeEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMetronomeTicksOn ()
    {
        return this.transport.isMetronomeTickPlaybackEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMetronomeTicks ()
    {
        this.transport.isMetronomeTickPlaybackEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMetronomeTicks (final boolean on)
    {
        this.transport.isMetronomeTickPlaybackEnabled ().set (on);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getMetronomeVolumeParameter ()
    {
        return this.metronomeVolumeParameter;
    }


    /** {@inheritDoc} */
    @Override
    public String getMetronomeVolumeStr ()
    {
        return this.transport.metronomeVolume ().displayedValue ().getLimited (6) + " dB";
    }


    /** {@inheritDoc} */
    @Override
    public int getMetronomeVolume ()
    {
        return this.valueChanger.fromNormalizedValue (this.transport.metronomeVolume ().get ());
    }


    /** {@inheritDoc} */
    @Override
    public void changeMetronomeVolume (final int control)
    {
        this.transport.metronomeVolume ().inc (Double.valueOf (this.valueChanger.calcKnobChange (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setMetronomeVolume (final int value)
    {
        this.transport.metronomeVolume ().set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPrerollMetronomeEnabled ()
    {
        return this.transport.isMetronomeAudibleDuringPreRoll ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void togglePrerollMetronome ()
    {
        this.transport.isMetronomeAudibleDuringPreRoll ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLoop (final boolean on)
    {
        this.transport.isArrangerLoopEnabled ().set (on);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleLoop ()
    {
        this.transport.isArrangerLoopEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLoop ()
    {
        return this.transport.isArrangerLoopEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isWritingClipLauncherAutomation ()
    {
        return this.transport.isClipLauncherAutomationWriteEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isWritingArrangerAutomation ()
    {
        return this.transport.isArrangerAutomationWriteEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public AutomationMode [] getAutomationWriteModes ()
    {
        return AUTOMATION_MODES;
    }


    /** {@inheritDoc} */
    @Override
    public AutomationMode getAutomationWriteMode ()
    {
        if (this.isWritingArrangerAutomation () || this.isWritingClipLauncherAutomation ())
            return AutomationMode.lookup (this.transport.automationWriteMode ().get ());
        return AutomationMode.READ;
    }


    /** {@inheritDoc} */
    @Override
    public void setAutomationWriteMode (final AutomationMode mode)
    {
        switch (mode)
        {
            case TRIM_READ, READ:
                this.transport.isArrangerAutomationWriteEnabled ().set (false);
                this.transport.isClipLauncherAutomationWriteEnabled ().set (false);
                break;

            case WRITE, TOUCH, LATCH, LATCH_PREVIEW:
                this.transport.isArrangerAutomationWriteEnabled ().set (true);
                this.transport.isClipLauncherAutomationWriteEnabled ().set (true);
                final String identifier = mode == AutomationMode.LATCH_PREVIEW ? AutomationMode.LATCH.getIdentifier () : mode.getIdentifier ();
                this.transport.automationWriteMode ().set (identifier);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void toggleWriteArrangerAutomation ()
    {
        this.transport.toggleWriteArrangerAutomation ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleWriteClipLauncherAutomation ()
    {
        this.transport.toggleWriteClipLauncherAutomation ();
    }


    /** {@inheritDoc} */
    @Override
    public void resetAutomationOverrides ()
    {
        this.transport.resetAutomationOverrides ();
    }


    /** {@inheritDoc} */
    @Override
    public void returnToArrangement ()
    {
        this.transport.returnToArrangement ();
    }


    /** {@inheritDoc} */
    @Override
    public String getPositionText ()
    {
        return this.transport.getPosition ().getFormatted ( (beatTime, isAbsolute, timeSignatureNumerator, timeSignatureDenominator, timeSignatureTicks) -> StringUtils.formatTimeLong (this.getTempo (), beatTime, true));
    }


    /** {@inheritDoc} */
    @Override
    public String getBeatText ()
    {
        return this.transport.getPosition ().getFormatted (BEAT_POSITION_FORMATTER);
    }


    /** {@inheritDoc} */
    @Override
    public void setPositionToEnd ()
    {
        this.application.invokeAction (ACTION_JUMP_TO_END);

        // Force moving the end of the arranger into view
        this.changePosition (false, true);
        this.changePosition (true, true);
    }


    /** {@inheritDoc} */
    @Override
    public void setPosition (final double beats)
    {
        this.transport.playStartPosition ().set (beats);
        if (this.transport.isPlaying ().get ())
            this.transport.jumpToPlayStartPosition ();
    }


    /** {@inheritDoc} */
    @Override
    public void changePosition (final boolean increase, final boolean slow)
    {
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        final double position = this.transport.playStartPosition ().get ();
        double newPos = Math.max (0, position + (increase ? frac : -frac));

        // Adjust to resolution
        final double intPosition = Math.floor (newPos / frac);
        newPos = intPosition * frac;

        this.setPosition (newPos);
    }


    /** {@inheritDoc} */
    @Override
    public String getLoopStartBeatText ()
    {
        return this.transport.arrangerLoopStart ().getFormatted (BEAT_POSITION_FORMATTER);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLoopStart (final boolean increase, final boolean slow)
    {
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.transport.arrangerLoopStart ().inc (increase ? frac : -frac);
    }


    /** {@inheritDoc} */
    @Override
    public void selectLoopStart ()
    {
        final double beats = this.transport.arrangerLoopStart ().get ();
        if (beats >= 0)
            this.transport.setPosition (beats);
    }


    /** {@inheritDoc} */
    @Override
    public void selectLoopEnd ()
    {
        final double pos = this.transport.arrangerLoopStart ().get ();
        if (pos >= 0)
            this.transport.setPosition (pos + this.transport.arrangerLoopDuration ().get ());
    }


    /** {@inheritDoc} */
    @Override
    public String getLoopLengthBeatText ()
    {
        return this.transport.arrangerLoopDuration ().getFormatted (BEAT_LENGTH_FORMATTER);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLoopLength (final boolean increase, final boolean slow)
    {
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.transport.arrangerLoopDuration ().inc (increase ? frac : -frac);
    }


    /** {@inheritDoc} */
    @Override
    public void setPunchIn (final boolean enable)
    {
        this.transport.isPunchInEnabled ().set (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void togglePunchIn ()
    {
        this.transport.isPunchInEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPunchInEnabled ()
    {
        return this.transport.isPunchInEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setPunchOut (final boolean enable)
    {
        this.transport.isPunchOutEnabled ().set (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void togglePunchOut ()
    {
        this.transport.isPunchOutEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPunchOutEnabled ()
    {
        return this.transport.isPunchOutEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getTempoParameter ()
    {
        return this.tempoParameter;
    }


    /** {@inheritDoc} */
    @Override
    public void tapTempo ()
    {
        this.transport.tapTempo ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeTempo (final boolean increase, final boolean slow)
    {
        final double offset = slow ? 0.01 : 1;
        this.tempoParameter.incRawValue (increase ? offset : -offset);
    }


    /** {@inheritDoc} */
    @Override
    public void setTempo (final double tempo)
    {
        this.tempoParameter.setRawValue (tempo);
    }


    /** {@inheritDoc} */
    @Override
    public double getTempo ()
    {
        return this.tempoParameter.getRawValue ();
    }


    /** {@inheritDoc} */
    @Override
    public String formatTempo (final double tempo)
    {
        return new DecimalFormat ("#.00").format (tempo);
    }


    /** {@inheritDoc} */
    @Override
    public String formatTempoNoFraction (final double tempo)
    {
        return new DecimalFormat ("###").format (tempo);
    }


    /** {@inheritDoc} */
    @Override
    public double scaleTempo (final double tempo, final int maxValue)
    {
        final double v = tempo - TransportConstants.MIN_TEMPO;
        return v * (maxValue - 1) / (TransportConstants.MAX_TEMPO - TransportConstants.MIN_TEMPO);
    }


    /** {@inheritDoc} */
    @Override
    public void setTempoIndication (final boolean isTouched)
    {
        this.tempoParameter.setIndication (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setCrossfade (final int value)
    {
        this.crossfadeParameter.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getCrossfadeParameter ()
    {
        return this.crossfadeParameter;
    }


    /** {@inheritDoc} */
    @Override
    public int getCrossfade ()
    {
        return this.crossfadeParameter.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeCrossfade (final int control)
    {
        this.crossfadeParameter.inc (this.valueChanger.calcKnobChange (control));
    }


    /** {@inheritDoc} */
    @Override
    public int getPrerollMeasures ()
    {
        final String preroll = this.transport.preRoll ().get ();

        switch (preroll)
        {
            case PREROLL_NONE:
                return 0;
            case PREROLL_1_BAR:
                return 1;
            case PREROLL_2_BARS:
                return 2;
            case PREROLL_4_BARS:
                return 4;
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setPrerollMeasures (final int preroll)
    {
        final SettableEnumValue preRollValue = this.transport.preRoll ();
        switch (preroll)
        {
            case 0:
                preRollValue.set (PREROLL_NONE);
                break;
            case 1:
                preRollValue.set (PREROLL_1_BAR);
                break;
            case 2:
                preRollValue.set (PREROLL_2_BARS);
                break;
            case 4:
                preRollValue.set (PREROLL_4_BARS);
                break;
            default:
                this.host.errorln ("Unknown Preroll length: " + preroll);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getNumerator ()
    {
        return this.transport.timeSignature ().numerator ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getDenominator ()
    {
        return this.transport.timeSignature ().denominator ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getQuartersPerMeasure ()
    {
        return 4 * this.getNumerator () / this.getDenominator ();
    }


    /** {@inheritDoc} */
    @Override
    public PostRecordingAction getClipLauncherPostRecordingAction ()
    {
        return PostRecordingAction.lookup (this.transport.clipLauncherPostRecordingAction ().get ());
    }


    /** {@inheritDoc} */
    @Override
    public void setClipLauncherPostRecordingAction (final PostRecordingAction action)
    {
        this.transport.clipLauncherPostRecordingAction ().set (action.getIdentifier ());
    }


    /** {@inheritDoc} */
    @Override
    public double getClipLauncherPostRecordingTimeOffset ()
    {
        return this.transport.getClipLauncherPostRecordingTimeOffset ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setClipLauncherPostRecordingTimeOffset (final double beats)
    {
        this.transport.getClipLauncherPostRecordingTimeOffset ().set (beats);
    }


    /** {@inheritDoc} */
    @Override
    public LaunchQuantization getDefaultLaunchQuantization ()
    {
        return LaunchQuantization.lookup (this.transport.defaultLaunchQuantization ().get ());
    }


    /** {@inheritDoc} */
    @Override
    public void setDefaultLaunchQuantization (final LaunchQuantization launchQuantization)
    {
        this.transport.defaultLaunchQuantization ().set (launchQuantization.getValue ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean isFillModeActive ()
    {
        return this.transport.isFillModeActive ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setFillModeActive (final boolean isActive)
    {
        this.transport.isFillModeActive ().set (isActive);
    }


    /** {@inheritDoc} */
    @Override
    public void toggleFillModeActive ()
    {
        this.transport.isFillModeActive ().toggle ();
    }


    /**
     * Get the Bitwig transport object.
     *
     * @return The transport object
     */
    public Transport getTransport ()
    {
        return this.transport;
    }


    private static BeatTimeFormatter formatAsBeats (final int offset)
    {
        return (beatTime, isAbsolute, timeSignatureNumerator, timeSignatureDenominator, timeSignatureTicks) -> {
            final int quartersPerMeasure = 4 * timeSignatureNumerator / timeSignatureDenominator;
            return StringUtils.formatMeasuresLong (quartersPerMeasure, beatTime, offset, true);
        };
    }
}