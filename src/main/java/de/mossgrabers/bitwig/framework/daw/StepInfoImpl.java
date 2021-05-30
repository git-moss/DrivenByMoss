// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.NoteOccurrenceType;
import de.mossgrabers.framework.daw.constants.Resolution;

import com.bitwig.extension.controller.api.NoteOccurrence;
import com.bitwig.extension.controller.api.NoteStep;


/**
 * Implementation for the data about a note in a sequencer step.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StepInfoImpl implements IStepInfo
{
    private int               state;
    private double            duration = Resolution.RES_1_16.getValue ();
    private double            velocity;
    private double            velocitySpread;
    private double            releaseVelocity;
    private double            pressure;
    private double            timbre;
    private double            pan;
    private double            transpose;
    private double            gain;
    private boolean           isChanceEnabled;
    private double            chance;
    private boolean           isOccurrenceEnabled;
    private NoteOccurrenceType occurrence;
    private boolean           isRecurrenceEnabled;
    private int               recurrenceLength;
    private int               recurrenceMask;
    private boolean           isRepeatEnabled;
    private int               repeatCount;
    private double            repeatCurve;
    private double            repeatVelocityCurve;
    private double            repeatVelocityEnd;


    /**
     * Constructor.
     */
    public StepInfoImpl ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getState ()
    {
        return this.state;
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


    /**
     * Set the state.
     *
     * @param state The state, 0: not set, 1: note continues playing, 2: start of note, see the
     *            defined constants
     */
    public void setState (final int state)
    {
        this.state = state;
    }


    /**
     * Set the given state and update all note data from the Bitwig StepInfo.
     *
     * @param stepInfo The step info
     */
    public void updateData (final NoteStep stepInfo)
    {
        switch (stepInfo.state ())
        {
            case NoteOn:
                this.state = IStepInfo.NOTE_START;
                break;
            case NoteSustain:
                this.state = IStepInfo.NOTE_CONTINUE;
                break;
            case Empty:
                this.state = IStepInfo.NOTE_OFF;
                break;
        }

        this.duration = stepInfo.duration ();
        this.velocity = stepInfo.velocity ();
        this.releaseVelocity = stepInfo.releaseVelocity ();
        this.pressure = stepInfo.pressure ();
        this.timbre = stepInfo.timbre ();
        this.pan = stepInfo.pan ();
        this.transpose = stepInfo.transpose ();
        this.gain = stepInfo.gain ();

        this.isChanceEnabled = stepInfo.isChanceEnabled ();
        this.chance = stepInfo.chance ();

        this.isOccurrenceEnabled = stepInfo.isOccurrenceEnabled ();
        final NoteOccurrence noteOccurrence = stepInfo.occurrence ();
        this.occurrence = NoteOccurrenceType.lookup (noteOccurrence.name ());

        this.isRecurrenceEnabled = stepInfo.isRecurrenceEnabled ();
        this.recurrenceLength = stepInfo.recurrenceLength ();
        this.recurrenceMask = stepInfo.recurrenceMask ();

        this.isRepeatEnabled = stepInfo.isRepeatEnabled ();
        this.repeatCount = stepInfo.repeatCount ();
        this.repeatCurve = stepInfo.repeatCurve ();
        this.repeatVelocityCurve = stepInfo.repeatVelocityCurve ();
        this.repeatVelocityEnd = stepInfo.repeatVelocityEnd ();
    }


    void setDuration (final double duration)
    {
        this.duration = duration;
    }


    void setVelocity (final double velocity)
    {
        this.velocity = velocity;
    }


    void setVelocitySpread (final double velocitySpread)
    {
        this.velocitySpread = velocitySpread;
    }


    void setReleaseVelocity (final double releaseVelocity)
    {
        this.releaseVelocity = releaseVelocity;
    }


    void setPressure (final double pressure)
    {
        this.pressure = pressure;
    }


    void setTimbre (final double timbre)
    {
        this.timbre = timbre;
    }


    void setPan (final double pan)
    {
        this.pan = pan;
    }


    void setTranspose (final double transpose)
    {
        this.transpose = transpose;
    }


    void setGain (final double gain)
    {
        this.gain = gain;
    }


    void setIsChanceEnabled (final boolean isEnabled)
    {
        this.isChanceEnabled = isEnabled;
    }


    void setChance (final double chance)
    {
        this.chance = chance;
    }


    void setIsOccurrenceEnabled (final boolean isEnabled)
    {
        this.isOccurrenceEnabled = isEnabled;
    }


    void setOccurrence (final NoteOccurrenceType occurrence)
    {
        this.occurrence = occurrence;
    }


    void setIsRecurrenceEnabled (final boolean isEnabled)
    {
        this.isRecurrenceEnabled = isEnabled;
    }


    void setRecurrenceLength (final int recurrenceLength)
    {
        this.recurrenceLength = recurrenceLength;
    }


    void setRecurrenceMask (final int recurrenceMask)
    {
        this.recurrenceMask = recurrenceMask;
    }


    void setIsRepeatEnabled (final boolean isEnabled)
    {
        this.isRepeatEnabled = isEnabled;
    }


    void setRepeatCount (final int repeatCount)
    {
        this.repeatCount = repeatCount;
    }


    void setRepeatCurve (final double repeatCurve)
    {
        this.repeatCurve = repeatCurve;
    }


    void setRepeatVelocityCurve (final double repeatVelocityCurve)
    {
        this.repeatVelocityCurve = repeatVelocityCurve;
    }


    void setRepeatVelocityEnd (final double repeatVelocityEnd)
    {
        this.repeatVelocityEnd = repeatVelocityEnd;
    }
}
