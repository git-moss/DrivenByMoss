// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.view;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.Views;


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
        super ("Drum", surface, model, 2, DrumView.NUM_DISPLAY_COLS, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.isActive ())
            return;

        final int index = note - 36;

        final int offsetY = this.scales.getDrumOffset ();
        if (this.isPlayMode)
        {
            this.setSelectedPad (index, velocity); // 0-16

            // Mark selected note
            this.keyManager.setKeyPressed (offsetY + this.getSelectedPad (), velocity);
        }
        else
        {
            if (velocity != 0)
                this.getClip ().toggleStep (this.configuration.getMidiEditChannel (), index < 8 ? index + 8 : index - 8, offsetY + this.getSelectedPad (), this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
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
        ((SessionView) this.surface.getViewManager ().get (Views.SESSION)).drawLightGuide (this.surface.getLightGuide ());

        final IPadGrid padGrid = this.surface.getPadGrid ();
        final IDrumDevice primary = this.model.getDrumDevice ();
        if (this.isPlayMode)
        {
            for (int y = 0; y < 2; y++)
            {
                for (int x = 0; x < 8; x++)
                {
                    final int index = 8 * y + x;
                    padGrid.lightEx (x, 1 - y, this.getDrumPadColor (index, primary, false));
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
        final int stepColor = this.getStepColor (primary);
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        final int offsetY = this.scales.getDrumOffset ();
        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        final int selPad = this.getSelectedPad ();
        for (int col = 0; col < DrumView.NUM_DISPLAY_COLS; col++)
        {
            final int isSet = clip.getStep (editMidiChannel, col, offsetY + selPad).getState ();
            final boolean hilite = col == hiStep;
            final int x = col % GRID_COLUMNS;
            final int y = col / GRID_COLUMNS;
            padGrid.lightEx (x, y, getSequencerPadColor (isSet, hilite, stepColor));
        }
    }


    private int getStepColor (final IDrumDevice primary)
    {
        final int selPad = this.getSelectedPad ();
        if (selPad < 0)
            return SLMkIIIColorManager.SLMKIII_BLACK;

        // If we cannot get the color from the drum pads use a default color
        if (!primary.getName ().equals ("Drum Machine"))
            return SLMkIIIColorManager.SLMKIII_BLUE;

        // Exists and active?
        final IChannel drumPad = primary.getDrumPadBank ().getItem (selPad);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return SLMkIIIColorManager.SLMKIII_BLACK;

        return this.model.getColorManager ().getColorIndex (DAWColor.getColorIndex (drumPad.getColor ()));
    }


    private static int getSequencerPadColor (final int isSet, final boolean hilite, final int stepColor)
    {
        if (isSet > 0)
            return hilite ? SLMkIIIColorManager.SLMKIII_GREEN : stepColor;
        return hilite ? SLMkIIIColorManager.SLMKIII_GREEN : SLMkIIIColorManager.SLMKIII_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        if (buttonID == ButtonID.SCENE1)
        {
            this.isPlayMode = !this.isPlayMode;
            this.updateNoteMapping ();
            this.surface.getDisplay ().notify (this.isPlayMode ? "Play / Select" : "Steps");
        }
        else
        {
            if (!this.isActive ())
                return;
            final ModeManager modeManager = this.surface.getModeManager ();
            if (modeManager.isActive (Modes.GROOVE))
                modeManager.restore ();
            else
                modeManager.setActive (Modes.GROOVE);
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


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.SCENE1)
            return this.isPlayMode ? SLMkIIIColorManager.SLMKIII_GREEN : SLMkIIIColorManager.SLMKIII_BLUE;

        if (buttonID == ButtonID.SCENE2)
        {
            if (!this.isActive ())
                return SLMkIIIColorManager.SLMKIII_BLACK;
            return this.surface.getModeManager ().isActive (Modes.GROOVE) ? SLMkIIIColorManager.SLMKIII_PINK : SLMkIIIColorManager.SLMKIII_DARK_GREY;
        }

        return super.getButtonColor (buttonID);
    }
}