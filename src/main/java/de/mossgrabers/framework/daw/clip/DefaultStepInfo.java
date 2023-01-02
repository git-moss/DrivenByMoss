// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.clip;

import de.mossgrabers.framework.daw.constants.Resolution;


/**
 * Default implementation for the data about a note in a sequencer step.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DefaultStepInfo implements IStepInfo
{
    protected StepState          state    = StepState.OFF;
    protected double             duration = Resolution.RES_1_16.getValue ();
    protected double             velocity;
    protected double             velocitySpread;
    protected double             releaseVelocity;
    protected double             pressure;
    protected double             timbre;
    protected double             pan;
    protected double             transpose;
    protected double             gain;
    protected boolean            isChanceEnabled;
    protected double             chance;
    protected boolean            isOccurrenceEnabled;
    protected NoteOccurrenceType occurrence;
    protected boolean            isRecurrenceEnabled;
    protected int                recurrenceLength;
    protected int                recurrenceMask;
    protected boolean            isRepeatEnabled;
    protected int                repeatCount;
    protected double             repeatCurve;
    protected double             repeatVelocityCurve;
    protected double             repeatVelocityEnd;
    protected boolean            isMuted;


    /**
     * Constructor.
     */
    public DefaultStepInfo ()
    {
        // Intentionally empty
    }


    /**
     * Copy constructor.
     *
     * @param sourceInfo The source step info
     */
    protected DefaultStepInfo (final DefaultStepInfo sourceInfo)
    {
        this.state = sourceInfo.state;
        this.duration = sourceInfo.duration;
        this.velocity = sourceInfo.velocity;
        this.velocitySpread = sourceInfo.velocitySpread;
        this.releaseVelocity = sourceInfo.releaseVelocity;
        this.pressure = sourceInfo.pressure;
        this.timbre = sourceInfo.timbre;
        this.pan = sourceInfo.pan;
        this.transpose = sourceInfo.transpose;
        this.gain = sourceInfo.gain;
        this.isChanceEnabled = sourceInfo.isChanceEnabled;
        this.chance = sourceInfo.chance;
        this.isOccurrenceEnabled = sourceInfo.isOccurrenceEnabled;
        this.occurrence = sourceInfo.occurrence;
        this.isRecurrenceEnabled = sourceInfo.isRecurrenceEnabled;
        this.recurrenceLength = sourceInfo.recurrenceLength;
        this.recurrenceMask = sourceInfo.recurrenceMask;
        this.isRepeatEnabled = sourceInfo.isRepeatEnabled;
        this.repeatCount = sourceInfo.repeatCount;
        this.repeatCurve = sourceInfo.repeatCurve;
        this.repeatVelocityCurve = sourceInfo.repeatVelocityCurve;
        this.repeatVelocityEnd = sourceInfo.repeatVelocityEnd;
        this.isMuted = sourceInfo.isMuted;
    }


    /** {@inheritDoc} */
    @Override
    public StepState getState ()
    {
        return this.state;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMuted ()
    {
        return this.isMuted;
    }


    /** {@inheritDoc} */
    @Override
    public double getDuration ()
    {
        return this.duration;
    }


    /** {@inheritDoc} */
    @Override
    public double getVelocity ()
    {
        return this.velocity;
    }


    /** {@inheritDoc} */
    @Override
    public double getVelocitySpread ()
    {
        return this.velocitySpread;
    }


    /** {@inheritDoc} */
    @Override
    public double getReleaseVelocity ()
    {
        return this.releaseVelocity;
    }


    /** {@inheritDoc} */
    @Override
    public double getPressure ()
    {
        return this.pressure;
    }


    /** {@inheritDoc} */
    @Override
    public double getTimbre ()
    {
        return this.timbre;
    }


    /** {@inheritDoc} */
    @Override
    public double getPan ()
    {
        return this.pan;
    }


    /** {@inheritDoc} */
    @Override
    public double getTranspose ()
    {
        return this.transpose;
    }


    /** {@inheritDoc} */
    @Override
    public double getGain ()
    {
        return this.gain;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isChanceEnabled ()
    {
        return this.isChanceEnabled;
    }


    /** {@inheritDoc} */
    @Override
    public double getChance ()
    {
        return this.chance;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOccurrenceEnabled ()
    {
        return this.isOccurrenceEnabled;
    }


    /** {@inheritDoc} */
    @Override
    public NoteOccurrenceType getOccurrence ()
    {
        return this.occurrence;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecurrenceEnabled ()
    {
        return this.isRecurrenceEnabled;
    }


    /** {@inheritDoc} */
    @Override
    public int getRecurrenceLength ()
    {
        return this.recurrenceLength;
    }


    /** {@inheritDoc} */
    @Override
    public int getRecurrenceMask ()
    {
        return this.recurrenceMask;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRepeatEnabled ()
    {
        return this.isRepeatEnabled;
    }


    /** {@inheritDoc} */
    @Override
    public int getRepeatCount ()
    {
        return this.repeatCount;
    }


    /** {@inheritDoc} */
    @Override
    public String getFormattedRepeatCount ()
    {
        final int count = this.getRepeatCount ();
        if (count == 0)
            return "Off";
        if (count < 0)
            return "1/" + Math.abs (count - 1);
        return Integer.toString (count + 1);
    }


    /** {@inheritDoc} */
    @Override
    public double getRepeatCurve ()
    {
        return this.repeatCurve;
    }


    /** {@inheritDoc} */
    @Override
    public double getRepeatVelocityCurve ()
    {
        return this.repeatVelocityCurve;
    }


    /** {@inheritDoc} */
    @Override
    public double getRepeatVelocityEnd ()
    {
        return this.repeatVelocityEnd;
    }


    /** {@inheritDoc} */
    @Override
    public IStepInfo createCopy ()
    {
        return new DefaultStepInfo (this);
    }


    /**
     * Set the state.
     *
     * @param state The state, 0: not set, 1: note continues playing, 2: start of note, see the
     *            defined constants
     */
    public void setState (final StepState state)
    {
        this.state = state;
    }


    /**
     * Set the muted state.
     *
     * @param isMuted True to set muted
     */
    public void setMuted (final boolean isMuted)
    {
        this.isMuted = isMuted;
    }


    /**
     * Set the duration.
     *
     * @param duration The duration
     */
    public void setDuration (final double duration)
    {
        this.duration = duration;
    }


    /**
     * Set the velocity.
     *
     * @param velocity The velocity
     */
    public void setVelocity (final double velocity)
    {
        this.velocity = velocity;
    }


    /**
     * Set the velocity spread.
     *
     * @param velocitySpread The velocity spread
     */
    public void setVelocitySpread (final double velocitySpread)
    {
        this.velocitySpread = velocitySpread;
    }


    /**
     * Set the release velocity.
     *
     * @param releaseVelocity The release velocity
     */
    public void setReleaseVelocity (final double releaseVelocity)
    {
        this.releaseVelocity = releaseVelocity;
    }


    /**
     * Set the pressure.
     *
     * @param pressure The pressure
     */
    public void setPressure (final double pressure)
    {
        this.pressure = pressure;
    }


    /**
     * Set the timbre.
     *
     * @param timbre The timbre
     */
    public void setTimbre (final double timbre)
    {
        this.timbre = timbre;
    }


    /**
     * Set the panorama.
     *
     * @param pan The panorama
     */
    public void setPan (final double pan)
    {
        this.pan = pan;
    }


    /**
     * Set the transpose.
     *
     * @param transpose The transpose
     */
    public void setTranspose (final double transpose)
    {
        this.transpose = transpose;
    }


    /**
     * Set the gain.
     *
     * @param gain The gain
     */
    public void setGain (final double gain)
    {
        this.gain = gain;
    }


    /**
     * Disable/enable the chance.
     *
     * @param isEnabled True to enable
     */
    public void setIsChanceEnabled (final boolean isEnabled)
    {
        this.isChanceEnabled = isEnabled;
    }


    /**
     * Set the chance.
     *
     * @param chance The chance
     */
    public void setChance (final double chance)
    {
        this.chance = chance;
    }


    /**
     * Disable/enable the occurrence.
     *
     * @param isEnabled True to enable
     */
    public void setIsOccurrenceEnabled (final boolean isEnabled)
    {
        this.isOccurrenceEnabled = isEnabled;
    }


    /**
     * Set the occurrence.
     *
     * @param occurrence The occurrence
     */
    public void setOccurrence (final NoteOccurrenceType occurrence)
    {
        this.occurrence = occurrence;
    }


    /**
     * Disable/enable the recurrence.
     *
     * @param isEnabled True to enable
     */
    public void setIsRecurrenceEnabled (final boolean isEnabled)
    {
        this.isRecurrenceEnabled = isEnabled;
    }


    /**
     * Set the recurrence length.
     *
     * @param recurrenceLength The recurrence length
     */
    public void setRecurrenceLength (final int recurrenceLength)
    {
        this.recurrenceLength = recurrenceLength;
    }


    /**
     * Set the recurrence mask.
     *
     * @param recurrenceMask The recurrence mask
     */
    public void setRecurrenceMask (final int recurrenceMask)
    {
        this.recurrenceMask = recurrenceMask;
    }


    /**
     * Disable/enable the repeat.
     *
     * @param isEnabled True to enable
     */
    public void setIsRepeatEnabled (final boolean isEnabled)
    {
        this.isRepeatEnabled = isEnabled;
    }


    /**
     * Set the repeat count.
     *
     * @param repeatCount The repeat count
     */
    public void setRepeatCount (final int repeatCount)
    {
        this.repeatCount = repeatCount;
    }


    /**
     * Set the repeat curve.
     *
     * @param repeatCurve The repeat curve
     */
    public void setRepeatCurve (final double repeatCurve)
    {
        this.repeatCurve = repeatCurve;
    }


    /**
     * Set the repeat velocity curve.
     *
     * @param repeatVelocityCurve The repeat velocity curve
     */
    public void setRepeatVelocityCurve (final double repeatVelocityCurve)
    {
        this.repeatVelocityCurve = repeatVelocityCurve;
    }


    /**
     * Set the repeat velocity end.
     *
     * @param repeatVelocityEnd The repeat velocity end
     */
    public void setRepeatVelocityEnd (final double repeatVelocityEnd)
    {
        this.repeatVelocityEnd = repeatVelocityEnd;
    }
}
