// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.constants.RecordQuantization;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * Default data for an empty track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptyTrack extends EmptyChannel implements ITrack
{
    /** The singleton. */
    public static final ITrack INSTANCE = new EmptyTrack ();


    /**
     * Constructor.
     */
    private EmptyTrack ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getCrossfadeParameter ()
    {
        return EmptyParameter.INSTANCE;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGroup ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGroupExpanded ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void setGroupExpanded (final boolean isExpanded)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleGroupExpanded ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasParent ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecArm ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMonitor ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAutoMonitor ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canHoldNotes ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canHoldAudioData ()
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
    public void setRecArm (final boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRecArm ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setMonitor (final boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMonitor ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setAutoMonitor (final boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleAutoMonitor ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void stop ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void returnToArrangement ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public ISlotBank getSlotBank ()
    {
        return EmptySlotBank.INSTANCE;
    }


    /** {@inheritDoc} */
    @Override
    public void createClip (final int slotIndex, final int lengthInBeats)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecordQuantizationNoteLength ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRecordQuantizationNoteLength ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public RecordQuantization getRecordQuantizationGrid ()
    {
        return RecordQuantization.RES_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void setRecordQuantizationGrid (final RecordQuantization recordQuantization)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasDrumDevice ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void launchLastClipImmediately ()
    {
        // Intentionally empty
    }
}
