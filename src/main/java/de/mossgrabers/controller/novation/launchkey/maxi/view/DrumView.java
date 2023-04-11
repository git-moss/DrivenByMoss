// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi.view;

import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;

import java.util.List;
import java.util.Optional;


/**
 * The Drum view.
 *
 * @author Jürgen Moßgraber
 */
public class DrumView extends AbstractDrumView<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    private static final int NUM_DISPLAY_COLS = 16;

    private boolean          isPlayMode       = true;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DrumView (final LaunchkeyMk3ControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 2, DrumView.NUM_DISPLAY_COLS, true);

        this.allRows = 2;
        this.firstPad = ButtonID.PAD17;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        final int index = note - DRUM_START_KEY;
        final int offsetY = this.scales.getDrumOffset ();

        if (this.isPlayMode)
        {
            this.setSelectedPad (index, velocity); // 0-16

            // Mark selected note
            this.keyManager.setKeyPressed (offsetY + this.getSelectedPad (), velocity);
        }
        else
        {
            if (this.isActive ())
            {
                final int x = index % this.numColumns;
                final int y = index / this.numColumns;
                this.handleSequencerArea (index, x, y, offsetY, velocity);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (this.model.canSelectedTrackHoldNotes () && this.isPlayMode ? this.scales.getDrumMatrix () : EMPTY_TABLE);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final IDrumDevice primary = this.model.getDrumDevice ();
        final IDrumPadBank drumPadBank = primary.getDrumPadBank ();

        if (this.isPlayMode)
        {
            for (int y = 0; y < 2; y++)
            {
                for (int x = 0; x < 8; x++)
                {
                    final int index = 8 * y + x;
                    padGrid.lightEx (x, 1 - y, this.getDrumPadColor (index, drumPadBank, false));
                }
            }
            return;
        }

        if (!this.isActive ())
        {
            padGrid.turnOff ();
            return;
        }

        // Paint the sequencer steps
        final INoteClip clip = this.getClip ();
        final int step = clip.getCurrentStep ();
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        final int offsetY = this.scales.getDrumOffset ();
        final int channel = this.configuration.getMidiEditChannel ();
        final int selPad = this.getSelectedPad ();
        final List<NotePosition> editNotes = this.getEditNotes ();
        final NotePosition notePosition = new NotePosition (channel, 0, 0);
        for (int col = 0; col < DrumView.NUM_DISPLAY_COLS; col++)
        {
            final int noteRow = offsetY + selPad;
            notePosition.setStep (col);
            notePosition.setNote (noteRow);
            final IStepInfo stepInfo = clip.getStep (notePosition);
            final boolean hilite = col == hiStep;
            final int x = col % GRID_COLUMNS;
            final int y = col / GRID_COLUMNS;

            final Optional<ColorEx> rowColor = this.getPadColor (primary, this.selectedPad);
            padGrid.lightEx (x, y, this.getStepColor (stepInfo, hilite, rowColor, channel, col, noteRow, editNotes));
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final DrumView view = (DrumView) this.surface.getViewManager ().get (Views.DRUM);

        switch (buttonID)
        {
            case SCENE1:
                return this.isPlayMode ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLUE;

            case SCENE2:
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;

            case ARROW_UP:
                if (this.surface.isShiftPressed ())
                    return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
                return view.isOctaveUpButtonOn () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;

            case ARROW_DOWN:
                if (this.surface.isShiftPressed ())
                    return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
                return view.isOctaveDownButtonOn () ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;

            default:
                return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final DrumView view = (DrumView) this.surface.getViewManager ().get (Views.DRUM);

        switch (buttonID)
        {
            case SCENE1:
                this.isPlayMode = !this.isPlayMode;
                this.updateNoteMapping ();
                this.surface.getDisplay ().notify (this.isPlayMode ? "Play / Select" : "Steps");
                break;

            case SCENE2:
                if (this.isActive ())
                    this.surface.getViewManager ().setActive (Views.SHIFT);
                break;

            case ARROW_UP:
                view.onOctaveUp (ButtonEvent.DOWN);
                break;

            case ARROW_DOWN:
                view.onOctaveDown (ButtonEvent.DOWN);
                break;

            default:
                // Not used
                break;
        }
    }


    /**
     * Check if play mode is active.
     *
     * @return True if play mode is active otherwise the sequencer steps of a note a shown.
     */
    public boolean isPlayMode ()
    {
        return this.isPlayMode;
    }
}