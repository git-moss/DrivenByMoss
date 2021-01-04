// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.constants.Resolution;

import com.bitwig.extension.controller.api.NoteStep;


/**
 * Implementation for the data about a note in a sequencer step.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StepInfoImpl implements IStepInfo
{
    private int    state;
    private double duration = Resolution.RES_1_16.getValue ();
    private double velocity;
    private double releaseVelocity;
    private double pressure;
    private double timbre;
    private double pan;
    private double transpose;
    private double gain;


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
    }


    void setDuration (final double duration)
    {
        this.duration = duration;
    }


    void setVelocity (final double velocity)
    {
        this.velocity = velocity;
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
}
