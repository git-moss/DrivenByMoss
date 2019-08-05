// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.midi;

import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.INoteRepeat;


/**
 * Implementation for a note repeat.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteRepeatImpl implements INoteRepeat
{
    /**
     * Constructor.
     */
    public NoteRepeatImpl ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActive (final ITrack track)
    {
        // TODO API extension required - https://github.com/teotigraphix/Framework4Bitwig/issues/20
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleActive (final ITrack track)
    {
        // TODO API extension required - https://github.com/teotigraphix/Framework4Bitwig/issues/20
    }


    /** {@inheritDoc} */
    @Override
    public void setPeriod (final ITrack track, final double length)
    {
        // TODO API extension required - https://github.com/teotigraphix/Framework4Bitwig/issues/20
    }


    /** {@inheritDoc} */
    @Override
    public double getPeriod (final ITrack track)
    {
        // TODO API extension required - https://github.com/teotigraphix/Framework4Bitwig/issues/20
        return 1.0;
    }
}
