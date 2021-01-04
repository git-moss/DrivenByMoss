// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.IStepInfo;


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
    public int getState ()
    {
        return NOTE_OFF;
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
}
