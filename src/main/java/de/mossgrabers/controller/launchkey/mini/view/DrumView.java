// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey.mini.view;

import de.mossgrabers.controller.launchkey.mini.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.launchkey.mini.controller.LaunchkeyMiniMk3ColorManager;
import de.mossgrabers.controller.launchkey.mini.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.Views;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends AbstractDrumView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    private static final int NUM_DISPLAY_COLS = 16;

    private boolean          isPlayMode       = true;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DrumView (final LaunchkeyMiniMk3ControlSurface surface, final IModel model)
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
            final IStepInfo stepInfo = clip.getStep (editMidiChannel, col, offsetY + selPad);
            final int isSet = stepInfo.getState ();
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
            return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;

        // If we cannot get the color from the drum pads use a default color
        if (!primary.getName ().equals ("Drum Machine"))
            return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE;

        // Exists and active?
        final IChannel drumPad = primary.getDrumPadBank ().getItem (selPad);
        if (!drumPad.doesExist () || !drumPad.isActivated ())
            return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;

        return this.model.getColorManager ().getColorIndex (DAWColor.getColorIndex (drumPad.getColor ()));
    }


    private static int getSequencerPadColor (final int isSet, final boolean hilite, final int stepColor)
    {
        if (isSet > 0)
            return hilite ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN : stepColor;
        return hilite ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.SCENE1)
            return this.isPlayMode ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE;
        return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO;
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
        else if (this.isActive ())
            this.surface.getViewManager ().setActive (Views.SHIFT);
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