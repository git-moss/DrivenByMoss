// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw;

import de.mossgrabers.framework.StringUtils;
import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.daw.ITransport;

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
public class TransportImpl implements ITransport
{
    /** 1 beat. */
    private static final double INC_FRACTION_TIME      = 1.0;
    /** 1/20th of a beat. */
    private static final double INC_FRACTION_TIME_SLOW = 1.0 / 20;
    private static final int    TEMPO_MIN              = 20;
    private static final int    TEMPO_MAX              = 666;

    private ControllerHost      host;
    private ValueChanger        valueChanger;
    private Transport           transport;

    private int                 crossfade              = 0;
    private double              tempo;
    private int                 metronomeValue;


    /**
     * Constructor
     *
     * @param host The host
     * @param valueChanger The value changer
     */
    public TransportImpl (final ControllerHost host, final ValueChanger valueChanger)
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
        this.transport.crossfade ().value ().addValueObserver (valueChanger.getUpperBound (), this::handleCrossfade);

        final SettableRangedValue metronomeVolume = this.transport.metronomeVolume ();
        metronomeVolume.markInterested ();
        metronomeVolume.displayedValue ().markInterested ();
        metronomeVolume.addValueObserver (valueChanger.getUpperBound (), this::handleMetronomeValue);

        final TimeSignatureValue ts = this.transport.timeSignature ();
        ts.numerator ().markInterested ();
        ts.denominator ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.transport.isPlaying ().setIsSubscribed (enable);
        this.transport.isArrangerRecordEnabled ().setIsSubscribed (enable);
        this.transport.isArrangerOverdubEnabled ().setIsSubscribed (enable);
        this.transport.isClipLauncherAutomationWriteEnabled ().setIsSubscribed (enable);
        this.transport.isClipLauncherOverdubEnabled ().setIsSubscribed (enable);
        this.transport.isArrangerAutomationWriteEnabled ().setIsSubscribed (enable);
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
        this.transport.crossfade ().value ().setIsSubscribed (enable);

        final SettableRangedValue metronomeVolume = this.transport.metronomeVolume ();
        metronomeVolume.setIsSubscribed (enable);
        metronomeVolume.displayedValue ().setIsSubscribed (enable);

        final TimeSignatureValue ts = this.transport.timeSignature ();
        ts.numerator ().setIsSubscribed (enable);
        ts.denominator ().setIsSubscribed (enable);
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
    public void record ()
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
    public String getMetronomeVolumeStr ()
    {
        return this.transport.metronomeVolume ().displayedValue ().getLimited (6) + " dB";
    }


    /** {@inheritDoc} */
    @Override
    public int getMetronomeVolume ()
    {
        return this.metronomeValue;
    }


    /** {@inheritDoc} */
    @Override
    public void changeMetronomeVolume (final int control)
    {
        this.transport.metronomeVolume ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public void setMetronomeVolume (final double value)
    {
        this.transport.metronomeVolume ().set (Double.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
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
    public String getAutomationWriteMode ()
    {
        return this.transport.automationWriteMode ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setAutomationWriteMode (final String mode)
    {
        this.transport.automationWriteMode ().set (mode);
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
        return this.transport.getPosition ().getFormatted ();
    }


    /** {@inheritDoc} */
    @Override
    public String getBeatText ()
    {
        return this.transport.getPosition ().getFormatted ( (beatTime, isAbsolute, timeSignatureNumerator, timeSignatureDenominator, timeSignatureTicks) -> {
            final int quartersPerMeasure = 4 * timeSignatureNumerator / timeSignatureDenominator;
            return StringUtils.formatMeasures (quartersPerMeasure, beatTime, 0);
        });
    }


    /** {@inheritDoc} */
    @Override
    public void setPosition (final double beats)
    {
        this.transport.setPosition (beats);
    }


    /** {@inheritDoc} */
    @Override
    public void changePosition (final boolean increase)
    {
        this.changePosition (increase, this.valueChanger.isSlow ());
    }


    /** {@inheritDoc} */
    @Override
    public void changePosition (final boolean increase, final boolean slow)
    {
        final double frac = slow ? INC_FRACTION_TIME_SLOW : INC_FRACTION_TIME;
        this.transport.incPosition (increase ? frac : -frac, false);
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
    public void tapTempo ()
    {
        this.transport.tapTempo ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeTempo (final boolean increase)
    {
        final double offset = this.valueChanger.isSlow () ? 0.01 : 1;
        this.transport.tempo ().incRaw (increase ? offset : -offset);
    }


    /** {@inheritDoc} */
    @Override
    public void setTempo (final double tempo)
    {
        this.transport.tempo ().setRaw (tempo);
    }


    /** {@inheritDoc} */
    @Override
    public double getTempo ()
    {
        return this.tempo;
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
    public void setTempoIndication (final boolean isTouched)
    {
        this.transport.tempo ().setIndication (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void setCrossfade (final int value)
    {
        this.transport.crossfade ().set (Integer.valueOf (value), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public int getCrossfade ()
    {
        return this.crossfade;
    }


    /** {@inheritDoc} */
    @Override
    public void changeCrossfade (final int control)
    {
        this.transport.crossfade ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
    }


    /** {@inheritDoc} */
    @Override
    public String getPreroll ()
    {
        return this.transport.preRoll ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setPreroll (final String preroll)
    {
        this.transport.preRoll ().set (preroll);
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


    private void handleTempo (final double value)
    {
        this.tempo = Math.min (TransportImpl.TEMPO_MAX, Math.max (TransportImpl.TEMPO_MIN, value));
    }


    private void handleCrossfade (final int value)
    {
        this.crossfade = value;
    }


    private void handleMetronomeValue (final int value)
    {
        this.metronomeValue = value;
    }
}