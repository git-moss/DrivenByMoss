// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NoteOccurrenceType;
import de.mossgrabers.framework.daw.clip.StepState;


/**
 * Default data for an empty step info.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptyStepInfo implements IStepInfo
{
    /** The singleton. */
    public static final IStepInfo INSTANCE = new EmptyStepInfo ();


    /**
     * Constructor.
     */
    private EmptyStepInfo ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public StepState getState ()
    {
        return StepState.OFF;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMuted ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public double getDuration ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public double getVelocity ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public double getVelocitySpread ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public double getReleaseVelocity ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public double getPressure ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public double getTimbre ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public double getPan ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public double getTranspose ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public double getGain ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isChanceEnabled ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public double getChance ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOccurrenceEnabled ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public NoteOccurrenceType getOccurrence ()
    {
        return NoteOccurrenceType.ALWAYS;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecurrenceEnabled ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public int getRecurrenceLength ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getRecurrenceMask ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRepeatEnabled ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public int getRepeatCount ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public String getFormattedRepeatCount ()
    {
        return "-";
    }


    /** {@inheritDoc} */
    @Override
    public double getRepeatCurve ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public double getRepeatVelocityCurve ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public double getRepeatVelocityEnd ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public IStepInfo createCopy ()
    {
        return INSTANCE;
    }
}
