// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;


/**
 * The Drum 8 view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView8 extends DrumViewBase
{
    private static final int NUM_DISPLAY_COLS = 8;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView8 (final PushControlSurface surface, final IModel model)
    {
        super (Views.VIEW_NAME_DRUM8, surface, model, 1, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes () || velocity == 0)
            return;

        final int index = note - 36;
        final int x = index % 8;
        final int y = index / 8;

        final int sound = y + this.soundOffset;
        final int col = x;

        this.getClip ().toggleStep (col, this.offsetY + this.selectedPad + sound, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        if (!this.model.canSelectedTrackHoldNotes ())
        {
            this.surface.getPadGrid ().turnOff ();
            return;
        }

        // Clip length/loop area
        final int step = this.getClip ().getCurrentStep ();

        // Paint the sequencer steps
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int blueHi = isPush2 ? PushColors.PUSH2_COLOR2_BLUE_HI : PushColors.PUSH1_COLOR2_BLUE_HI;
        final int greenLo = isPush2 ? PushColors.PUSH2_COLOR2_GREEN_LO : PushColors.PUSH1_COLOR2_GREEN_LO;
        final int greenHi = isPush2 ? PushColors.PUSH2_COLOR2_GREEN_HI : PushColors.PUSH1_COLOR2_GREEN_HI;
        final int off = isPush2 ? PushColors.PUSH2_COLOR2_BLACK : PushColors.PUSH1_COLOR2_BLACK;
        final int hiStep = this.isInXRange (step) ? step % DrumView8.NUM_DISPLAY_COLS : -1;
        for (int sound = 0; sound < 8; sound++)
        {
            for (int col = 0; col < DrumView8.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = this.getClip ().getStep (col, this.offsetY + this.selectedPad + sound + this.soundOffset);
                final boolean hilite = col == hiStep;
                final int x = col % 8;
                int y = col / 8;
                y += sound;
                this.surface.getPadGrid ().lightEx (x, 7 - y, isSet > 0 ? hilite ? greenLo : blueHi : hilite ? greenHi : off, -1, false);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.surface.setKeyTranslationTable (this.scales.translateMatrixToGrid (EMPTY_TABLE));
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, ColorManager.BUTTON_STATE_ON);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, ColorManager.BUTTON_STATE_ON);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateLowerSceneButtons ()
    {
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int yellow = isPush2 ? PushColors.PUSH2_COLOR_SCENE_YELLOW : PushColors.PUSH1_COLOR_SCENE_YELLOW;
        final int green = isPush2 ? PushColors.PUSH2_COLOR_SCENE_GREEN : PushColors.PUSH1_COLOR_SCENE_GREEN;
        final int off = isPush2 ? PushColors.PUSH2_COLOR2_BLACK : PushColors.PUSH1_COLOR2_BLACK;
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE1, this.soundOffset == 0 ? yellow : green);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE2, this.soundOffset == 8 ? yellow : green);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE3, off);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE4, off);
    }


    /** {@inheritDoc} */
    @Override
    protected void onLowerScene (final int index)
    {
        // 7, 6, 5, 4
        if (index < 6)
            return;
        this.soundOffset = index == 7 ? 0 : 8;
        this.surface.getDisplay ().notify ("Offset: " + this.soundOffset);
    }
}