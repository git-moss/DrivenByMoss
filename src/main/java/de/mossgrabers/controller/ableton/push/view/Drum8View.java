// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractDrum8View;


/**
 * The Drum 8 view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Drum8View extends AbstractDrum8View<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public Drum8View (final PushControlSurface surface, final IModel model)
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
        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();

        final int stepX = index % this.numColumns;
        final int stepY = this.scales.getDrumOffset () + index / this.numColumns;

        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), stepX, stepY);
        this.editNote (this.getClip (), notePosition, false);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (this.surface.isPressed (ButtonID.REPEAT))
        {
            NoteRepeatSceneHelper.handleNoteRepeatSelection (this.surface, 7 - index);
            return;
        }

        super.onButton (buttonID, event, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int row, final int velocity, final int accentVelocity)
    {
        final boolean isSelectPressed = this.surface.isSelectPressed ();

        if (this.surface.isShiftPressed ())
        {
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, !isSelectPressed);
            return true;
        }

        if (isSelectPressed)
        {
            if (velocity > 0)
                this.editNote (clip, notePosition, true);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, row, velocity, accentVelocity);
    }
}