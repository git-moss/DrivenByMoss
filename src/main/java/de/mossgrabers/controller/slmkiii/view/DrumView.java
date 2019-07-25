// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.view;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends AbstractDrumView<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    private static final int NUM_DISPLAY_COLS = 16;

    private boolean          isPlayMode       = true;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DrumView (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 2, DrumView.NUM_DISPLAY_COLS);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        final int index = note - 36;

        final int offsetY = this.scales.getDrumOffset ();
        if (this.isPlayMode)
        {
            this.selectedPad = index; // 0-16

            // Mark selected note
            this.keyManager.setKeyPressed (offsetY + this.selectedPad, velocity);
        }
        else
        {
            if (velocity != 0)
                this.getClip ().toggleStep (index < 8 ? index + 8 : index - 8, offsetY + this.selectedPad, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
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
        final PadGrid padGrid = this.surface.getPadGrid ();
        if (!this.model.canSelectedTrackHoldNotes ())
        {
            padGrid.turnOff ();
            return;
        }

        final ICursorDevice primary = this.model.getInstrumentDevice ();
        if (this.isPlayMode)
        {
            final boolean hasDrumPads = primary.hasDrumPads ();
            boolean isSoloed = false;
            if (hasDrumPads)
            {
                for (int i = 0; i < 16; i++)
                {
                    if (primary.getDrumPadBank ().getItem (i).isSolo ())
                    {
                        isSoloed = true;
                        break;
                    }
                }
            }
            for (int y = 0; y < 2; y++)
            {
                for (int x = 0; x < 8; x++)
                {
                    final int index = 8 * y + x;
                    padGrid.lightEx (x, 1 - y, this.getPadColor (index, primary, isSoloed, false));
                }
            }
            return;
        }

        // Paint the sequencer steps
        final INoteClip clip = this.getClip ();
        final int step = clip.getCurrentStep ();
        final int stepColor = getStepColor (primary);
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        final int offsetY = this.scales.getDrumOffset ();
        for (int col = 0; col < DrumView.NUM_DISPLAY_COLS; col++)
        {
            final int isSet = clip.getStep (col, offsetY + this.selectedPad);
            final boolean hilite = col == hiStep;
            final int x = col % GRID_COLUMNS;
            final int y = col / GRID_COLUMNS;
            padGrid.lightEx (x, y, getSequencerPadColor (isSet, hilite, stepColor));
        }
    }


    private int getStepColor (final ICursorDevice primary)
    {
        if (this.selectedPad < 0)
            return SLMkIIIColors.SLMKIII_BLACK;

        // If we cannot get the color from the drum pads use a default color
        if (!primary.getName ().equals ("Drum Machine"))
            return SLMkIIIColors.SLMKIII_BLUE;

        // Exists and active?
        final IChannel drumPad = primary.getDrumPadBank ().getItem (this.selectedPad);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return SLMkIIIColors.SLMKIII_BLACK;

        return this.model.getColorManager ().getColor (DAWColors.getColorIndex (drumPad.getColor ()));
    }


    private static int getSequencerPadColor (final int isSet, final boolean hilite, final int stepColor)
    {
        if (isSet > 0)
            return hilite ? SLMkIIIColors.SLMKIII_GREEN : stepColor;
        return hilite ? SLMkIIIColors.SLMKIII_GREEN : SLMkIIIColors.SLMKIII_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_SCENE_1, this.isPlayMode ? SLMkIIIColors.SLMKIII_GREEN : SLMkIIIColors.SLMKIII_BLUE);
        this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_SCENE_2, this.surface.getModeManager ().isActiveOrTempMode (Modes.MODE_GROOVE) ? SLMkIIIColors.SLMKIII_PINK : SLMkIIIColors.SLMKIII_DARK_GREY);
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (index == 0)
        {
            this.isPlayMode = !this.isPlayMode;
            this.updateNoteMapping ();
            this.surface.getDisplay ().notify (this.isPlayMode ? "Play / Select" : "Steps");
        }
        else
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            if (modeManager.isActiveOrTempMode (Modes.MODE_GROOVE))
                modeManager.restoreMode ();
            else
                modeManager.setActiveMode (Modes.MODE_GROOVE);
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