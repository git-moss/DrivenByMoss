// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.view.sequencer.AbstractDrum4View;


/**
 * The 4 lane drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Drum4View extends AbstractDrum4View<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private NotePosition noteEditPosition;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public Drum4View (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - DRUM_START_KEY;
        final int x = index % this.numColumns;
        final int y = index / this.numColumns;
        final int sound = y % this.lanes + this.scales.getDrumOffset ();
        final int laneOffset = (this.allRows - 1 - y) / this.lanes * this.numColumns;

        // Remember the long pressed note to use it either for editing or for changing the length of
        // the note on pad release
        this.noteEditPosition = new NotePosition (this.configuration.getMidiEditChannel (), laneOffset + x, sound);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int velocity, final int downVelocity, final NotePosition notePosition)
    {
        // Toggle the note on up, so we can intercept the long presses
        if (velocity != 0)
        {
            this.noteEditPosition = null;
            return;
        }

        // Note: If the length of the note was changed this method will not be called since button
        // up was consumed! Therefore, always call edit note
        if (this.noteEditPosition != null)
            this.editNote (this.getClip (), this.noteEditPosition, false);
        else
            super.handleSequencerArea (velocity, downVelocity, notePosition);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int row, final int velocity, final int accentVelocity)
    {
        final boolean isUpPressed = this.surface.isPressed (ButtonID.UP);
        if (isUpPressed || this.surface.isPressed (ButtonID.DOWN))
        {
            this.surface.setTriggerConsumed (isUpPressed ? ButtonID.UP : ButtonID.DOWN);
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, isUpPressed);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, row, velocity, accentVelocity);
    }
}