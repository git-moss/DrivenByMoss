// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.framework.daw.clip.DefaultStepInfo;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NoteOccurrenceType;
import de.mossgrabers.framework.daw.clip.StepState;

import com.bitwig.extension.controller.api.NoteOccurrence;
import com.bitwig.extension.controller.api.NoteStep;


/**
 * Implementation for the data about a note in a sequencer step.
 *
 * @author Jürgen Moßgraber
 */
public class StepInfoImpl extends DefaultStepInfo
{
    /**
     * Constructor.
     */
    public StepInfoImpl ()
    {
        // Intentionally empty
    }


    /**
     * Copy constructor.
     *
     * @param sourceInfo The source step info
     */
    protected StepInfoImpl (final StepInfoImpl sourceInfo)
    {
        super (sourceInfo);
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
                this.state = StepState.START;
                break;
            case NoteSustain:
                this.state = StepState.CONTINUE;
                break;
            case Empty:
                this.state = StepState.OFF;
                break;
        }

        this.isMuted = stepInfo.isMuted ();
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


    /** {@inheritDoc} */
    @Override
    public IStepInfo createCopy ()
    {
        return new StepInfoImpl (this);
    }
}
