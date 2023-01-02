// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.midi;

/**
 * Abstract note input implementation.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractNoteInput implements INoteInput
{
    protected INoteRepeat noteRepeat;
    protected boolean     isMPEEnabled            = false;
    protected int         mpePitchBendSensitivity = 48;


    /**
     * Constructor.
     */
    protected AbstractNoteInput ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public INoteRepeat getNoteRepeat ()
    {
        return this.noteRepeat;
    }
}
