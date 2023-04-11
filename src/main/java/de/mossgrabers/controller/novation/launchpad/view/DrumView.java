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
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractDrumExView;


/**
 * The drum sequencer.
 *
 * @author Jürgen Moßgraber
 */
public class DrumView extends AbstractDrumExView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private NotePosition noteEditPosition;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 4, 4, true);

        this.buttonSelect = ButtonID.PAD13;
        this.buttonMute = ButtonID.PAD14;
        this.buttonSolo = ButtonID.PAD15;
        this.buttonBrowse = ButtonID.PAD16;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int ordinal = buttonID.ordinal ();
        if (ordinal < ButtonID.SCENE1.ordinal () || ordinal > ButtonID.SCENE8.ordinal ())
            return 0;

        final int scene = ordinal - ButtonID.SCENE1.ordinal ();

        if (ButtonID.isSceneButton (buttonID))
        {
            final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();
            if (this.noteRepeatPeriodOn)
            {
                final int periodIndex = Resolution.getMatch (noteRepeat.getPeriod ());
                return buttonID == ButtonID.get (ButtonID.SCENE1, 7 - periodIndex) ? LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_LO;
            }

            if (this.noteRepeatLengthOn)
            {
                final int lengthIndex = Resolution.getMatch (noteRepeat.getNoteLength ());
                return buttonID == ButtonID.get (ButtonID.SCENE1, 7 - lengthIndex) ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_LO;
            }
        }

        if (!this.isActive ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
        return scene == 7 - this.getResolutionIndex () ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN || !this.isActive ())
            return;

        if (this.noteRepeatPeriodOn)
        {
            this.setPeriod (7 - (buttonID.ordinal () - ButtonID.SCENE1.ordinal ()));
            return;
        }

        if (this.noteRepeatLengthOn)
        {
            this.setNoteLength (7 - (buttonID.ordinal () - ButtonID.SCENE1.ordinal ()));
            return;
        }

        super.onButton (buttonID, event, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleLoopArea (final int pad, final int velocity)
    {
        if (pad == 15)
        {
            if (velocity > 0)
                this.toggleExtraButtons ();
            return;
        }

        super.handleLoopArea (pad, velocity);
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
        final int offsetY = this.scales.getDrumOffset ();

        // Sequencer steps
        if (y < this.playRows)
            return;

        // Remember the long pressed note to use it either for editing or for changing the length of
        // the note on pad release
        this.noteEditPosition = new NotePosition (this.configuration.getMidiEditChannel (), this.numColumns * (this.allRows - 1 - y) + x, offsetY + this.selectedPad);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int index, final int x, final int y, final int offsetY, final int velocity)
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
            super.handleSequencerArea (index, x, y, offsetY, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int velocity)
    {
        final boolean isUpPressed = this.surface.isPressed (ButtonID.UP);
        if (isUpPressed || this.surface.isPressed (ButtonID.DOWN))
        {
            this.surface.setTriggerConsumed (isUpPressed ? ButtonID.UP : ButtonID.DOWN);
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, isUpPressed);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, velocity);
    }


    private void setPeriod (final int index)
    {
        this.surface.getConfiguration ().setNoteRepeatPeriod (Resolution.values ()[index]);
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Period: " + Resolution.getNameAt (index)), 100);
    }


    private void setNoteLength (final int index)
    {
        this.surface.getConfiguration ().setNoteRepeatLength (Resolution.values ()[index]);
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Note Length: " + Resolution.getNameAt (index)), 100);
    }
}