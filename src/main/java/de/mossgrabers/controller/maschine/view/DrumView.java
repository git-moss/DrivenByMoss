// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.view;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineColorManager;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.controller.maschine.mode.EditNoteMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends AbstractDrumView<MaschineControlSurface, MaschineConfiguration>
{
    private boolean isShifted          = false;
    private boolean isSequencerVisible = false;
    private boolean isGridEditor       = false;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DrumView (final MaschineControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 0, 4, 4, 128, 16, true, true);

        this.sequencerSteps = 16;
    }


    /**
     * Toggle between the play area and the sequencer steps.
     */
    public void toggleSequencerVisible ()
    {
        this.isSequencerVisible = !this.isSequencerVisible;
        this.updateNoteMapping ();
    }


    /**
     * Test if the play area or the sequencer steps are visible.
     *
     * @return True if the sequencer steps area is visible
     */
    public boolean isSequencerVisible ()
    {
        return this.isSequencerVisible;
    }


    /**
     * Toggle the grid editor off/on.
     */
    public void toggleGridEditor ()
    {
        this.isGridEditor = !this.isGridEditor;
        this.updateNoteMapping ();
    }


    /**
     * Test if the grid editor is active.
     *
     * @return True if active
     */
    public boolean isGridEditor ()
    {
        return this.isGridEditor;
    }


    /**
     * Toggle to 'shifted' view for editing the configuration settings.
     */
    public void toggleShifted ()
    {
        this.isShifted = !this.isShifted;
        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        if (this.isShifted || this.isSequencerVisible || this.isGridEditor)
            this.delayedUpdateNoteMapping (EMPTY_TABLE);
        else
            super.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        if (this.isGridEditor)
        {
            final IPadGrid padGrid = this.surface.getPadGrid ();

            for (int i = 0; i < 8; i++)
                padGrid.light (36 + i, this.getButtonColorID (ButtonID.get (ButtonID.SCENE1, 7 - i)));
            for (int i = 8; i < 12; i++)
                padGrid.light (36 + i, MaschineColorManager.COLOR_BLACK);
            for (int i = 12; i < 14; i++)
                padGrid.light (36 + i, MaschineColorManager.COLOR_BLUE);
            for (int i = 14; i < 16; i++)
                padGrid.light (36 + i, MaschineColorManager.COLOR_BLACK);
            return;
        }

        if (this.isSequencerVisible)
        {
            final INoteClip clip = this.getClip ();
            final boolean isActive = this.isActive ();
            final IDrumDevice primary = this.model.getDrumDevice ();
            this.drawSequencerSteps (clip, isActive, this.scales.getDrumOffset () + this.selectedPad, this.getDrumPadColor (primary, this.selectedPad), y -> 3 - y);
            return;
        }

        if (this.isShifted)
            this.drawShiftedGrid ();
        else
            super.drawGrid ();
    }


    private void drawShiftedGrid ()
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        final IPadGrid padGrid = this.surface.getPadGrid ();

        for (int i = 36; i < 50; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 50; i < 52; i++)
            padGrid.light (i, isKeyboardEnabled ? MaschineColorManager.COLOR_SKIN : MaschineColorManager.COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int key, final int velocity)
    {
        final int index = key - DRUM_START_KEY;

        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        if (this.isGridEditor)
        {
            if (velocity == 0)
                return;
            if (index < 8)
                this.setResolutionIndex (index);
            else
            {
                final INoteClip clip = this.getClip ();
                if (index == 12)
                    clip.scrollStepsPageBackwards ();
                else if (index == 13)
                    clip.scrollStepsPageForward ();
                this.surface.getDisplay ().notify ("Page " + (clip.getEditPage () + 1));
            }
            return;
        }

        if (this.isSequencerVisible)
        {
            final int x = index % this.numColumns;
            final int y = index / this.numColumns;
            final int offsetY = this.scales.getDrumOffset ();

            if (this.isActive ())
                this.handleSequencerArea (index, x, y, offsetY, velocity);
            return;
        }

        if (!this.isShifted)
        {
            super.onGridNote (key, velocity);
            return;
        }

        if (velocity == 0)
            return;

        switch (index)
        {
            case 14:
                this.onOctaveDown (ButtonEvent.DOWN);
                break;

            case 15:
                this.onOctaveUp (ButtonEvent.DOWN);
                break;

            default:
                // Not used
                break;
        }

        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int note, final int velocity)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.NOTE))
        {
            final int isSet = clip.getStep (channel, step, note).getState ();
            this.model.getHost ().showNotification ("Note " + Scales.formatNoteAndOctave (note, -3) + " - Step " + Integer.toString (step + 1));
            ((EditNoteMode) modeManager.get (Modes.NOTE)).setValues (isSet == IStepInfo.NOTE_START ? clip : null, channel, step, note);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, channel, step, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int index, final int x, final int y, final int offsetY, final int velocity)
    {
        final int yMod = 3 - y;
        super.handleSequencerArea (index, x, yMod, offsetY, velocity);
    }
}