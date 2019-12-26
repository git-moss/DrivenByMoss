// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.utils.StringUtils;

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
    private ControllerHost host;
    private IValueChanger  valueChanger;
    private Transport      transport;

    private double         tempo;


    /**
     * Constructor
     *
     * @param host The host
     * @param valueChanger The value changer
     */
    public TransportImpl (final ControllerHost host, final IValueChanger valueChanger)
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
        this.transport.crossfade ().value ().markInterested ();

        final SettableRangedValue metronomeVolume = this.transport.metronomeVolume ();
        metronomeVolume.markInterested ();
        metronomeVolume.displayedValue ().markInterested ();

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
        Util.setIsSubscribed (this.transport.tempo ().value (), enable);
        Util.setIsSubscribed (this.transport.getPosition (), enable);
        Util.setIsSubscribed (this.transport.crossfade ().value (), enable);

        final SettableRangedValue metronomeVolume = this.transport.metronomeVolume ();
        Util.setIsSubscribed (metronomeVolume, enable);
        Util.setIsSubscribed (metronomeVolume.displayedValue (), enable);

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
        return this.valueChanger.fromNormalizedValue (this.transport.metronomeVolume ().get ());
    }


    /** {@inheritDoc} */
    @Override
    public void changeMetronomeVolume (final int control)
    {
        this.transport.metronomeVolume ().inc (Double.valueOf (this.valueChanger.calcKnobSpeed (control)), Integer.valueOf (this.valueChanger.getUpperBound ()));
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
            return StringUtils.formatMeasures (quartersPerMeasure, beatTime, 1);
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
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
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
    public double rescaleTempo (final double tempo, final int maxValue)
    {
        final double v = tempo - TransportConstants.MIN_TEMPO;
        return v * (maxValue - 1) / (TransportConstants.MAX_TEMPO - TransportConstants.MIN_TEMPO);
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
        return this.valueChanger.fromNormalizedValue (this.transport.crossfade ().get ());
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
    public int getPrerollAsBars ()
    {
        switch (this.getPreroll ())
        {
            case TransportConstants.PREROLL_NONE:
                return 0;
            case TransportConstants.PREROLL_1_BAR:
                return 1;
            case TransportConstants.PREROLL_2_BARS:
                return 2;
            case TransportConstants.PREROLL_4_BARS:
                return 4;
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setPreroll (final String preroll)
    {
        this.transport.preRoll ().set (preroll);
    }


    /** {@inheritDoc} */
    @Override
    public void setPrerollAsBars (final int preroll)
    {
        switch (preroll)
        {
            case 0:
                this.setPreroll (TransportConstants.PREROLL_NONE);
                break;
            case 1:
                this.setPreroll (TransportConstants.PREROLL_1_BAR);
                break;
            case 2:
                this.setPreroll (TransportConstants.PREROLL_2_BARS);
                break;
            case 4:
                this.setPreroll (TransportConstants.PREROLL_4_BARS);
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


    private void handleTempo (final double value)
    {
        this.tempo = Math.min (TransportConstants.MAX_TEMPO, Math.max (TransportConstants.MIN_TEMPO, value));
    }
}