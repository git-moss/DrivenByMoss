// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.featuregroup.IScrollableView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ScrollStates;
import de.mossgrabers.framework.view.sequencer.AbstractNoteSequencerView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * The sequencer view.
 *
 * @author Jürgen Moßgraber
 */
public class SequencerView extends AbstractNoteSequencerView<LaunchpadControlSurface, LaunchpadConfiguration> implements IScrollableView
{
    private NotePosition noteEditPosition;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SequencerView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Sequencer", surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (!this.isActive ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;

        final int ordinal = buttonID.ordinal ();
        if (ordinal < ButtonID.SCENE1.ordinal () || ordinal > ButtonID.SCENE8.ordinal ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;

        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        return scene == 7 - this.getResolutionIndex () ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int row, final int velocity)
    {
        final boolean isUpPressed = this.surface.isPressed (ButtonID.UP);
        if (isUpPressed || this.surface.isPressed (ButtonID.DOWN))
        {
            this.surface.setTriggerConsumed (isUpPressed ? ButtonID.UP : ButtonID.DOWN);
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, isUpPressed);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, row, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int index, final int x, final int y, final int velocity)
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
            super.handleSequencerArea (index, x, y, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - 36;
        final int y = index / 8;
        if (y >= this.numSequencerRows)
            return;

        // Remember the long pressed note to use it either for editing or for changing the length of
        // the note on pad release
        this.noteEditPosition = new NotePosition (this.configuration.getMidiEditChannel (), index % 8, this.keyManager.map (y));
    }


    /** {@inheritDoc} */
    @Override
    public void updateScrollStates (final ScrollStates scrollStates)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        final INoteClip clip = AbstractSequencerView.class.cast (viewManager.getActive ()).getClip ();
        final int seqOctave = this.scales.getOctave ();
        scrollStates.setCanScrollLeft (clip.canScrollStepsBackwards ());
        scrollStates.setCanScrollRight (clip.canScrollStepsForwards ());
        scrollStates.setCanScrollUp (seqOctave < Scales.OCTAVE_RANGE);
        scrollStates.setCanScrollDown (seqOctave > -Scales.OCTAVE_RANGE);
    }
}
