// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.ISlot;


/**
 * Default data for an empty slot.
 *
 * @author Jürgen Moßgraber
 */
public class EmptySlot extends EmptyItem implements ISlot
{
    /** The singleton. */
    public static final ISlot INSTANCE = new EmptySlot ();


    /**
     * Constructor.
     */
    private EmptySlot ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasContent ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecording ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaying ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlayingQueued ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecordingQueued ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isStopQueued ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
    {
        return ColorEx.BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final ColorEx color)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void launch (final boolean isPressed, final boolean isAlternative)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void startRecording ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void remove ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void paste (final ISlot slot)
    {
        // Intentionally empty
    }
}
