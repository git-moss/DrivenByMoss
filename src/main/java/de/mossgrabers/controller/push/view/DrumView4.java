// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.Views;


/**
 * The Drum 4 view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView4 extends DrumViewBase
{
    private static final int NUM_DISPLAY_COLS = 16;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView4 (final PushControlSurface surface, final IModel model)
    {
        super (Views.VIEW_NAME_DRUM4, surface, model, 2, 0);

        this.soundOffset = 0;
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

        final int sound = y % 4 + this.soundOffset;
        final int col = 8 * (1 - y / 4) + x;

        final int offsetY = this.scales.getDrumOffset ();
        this.getClip ().toggleStep (col, offsetY + this.selectedPad + sound, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
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
        final INoteClip clip = this.getClip ();
        final int step = clip.getCurrentStep ();

        // Paint the sequencer steps
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int blueHi = isPush2 ? PushColors.PUSH2_COLOR2_BLUE_HI : PushColors.PUSH1_COLOR2_BLUE_HI;
        final int greenLo = isPush2 ? PushColors.PUSH2_COLOR2_GREEN_LO : PushColors.PUSH1_COLOR2_GREEN_LO;
        final int greenHi = isPush2 ? PushColors.PUSH2_COLOR2_GREEN_HI : PushColors.PUSH1_COLOR2_GREEN_HI;
        final int off = isPush2 ? PushColors.PUSH2_COLOR2_BLACK : PushColors.PUSH1_COLOR2_BLACK;
        final int hiStep = this.isInXRange (step) ? step % DrumView4.NUM_DISPLAY_COLS : -1;
        final int offsetY = this.scales.getDrumOffset ();
        for (int sound = 0; sound < 4; sound++)
        {
            for (int col = 0; col < DrumView4.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = clip.getStep (col, offsetY + this.selectedPad + sound + this.soundOffset);
                final boolean hilite = col == hiStep;
                final int x = col % 8;
                int y = col / 8;
                if (col < 8)
                    y += 5;
                y += sound;
                this.surface.getPadGrid ().lightEx (x, 8 - y, isSet > 0 ? hilite ? greenLo : blueHi : hilite ? greenHi : off, -1, false);
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
        this.surface.updateTrigger (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, ColorManager.BUTTON_STATE_ON);
        this.surface.updateTrigger (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, ColorManager.BUTTON_STATE_ON);
    }


    /** {@inheritDoc} */
    @Override
    protected void onLowerScene (final int index)
    {
        // 7, 6, 5, 4
        this.soundOffset = 4 * (7 - index);
        this.surface.getDisplay ().notify ("Offset: " + this.soundOffset);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateLowerSceneButtons ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final int colorTranspose = colorManager.getColor (AbstractSequencerView.COLOR_TRANSPOSE);
        final int colorSelectedTranspose = colorManager.getColor (AbstractSequencerView.COLOR_TRANSPOSE_SELECTED);
        this.surface.updateTrigger (PushControlSurface.PUSH_BUTTON_SCENE1, this.soundOffset == 0 ? colorSelectedTranspose : colorTranspose);
        this.surface.updateTrigger (PushControlSurface.PUSH_BUTTON_SCENE2, this.soundOffset == 4 ? colorSelectedTranspose : colorTranspose);
        this.surface.updateTrigger (PushControlSurface.PUSH_BUTTON_SCENE3, this.soundOffset == 8 ? colorSelectedTranspose : colorTranspose);
        this.surface.updateTrigger (PushControlSurface.PUSH_BUTTON_SCENE4, this.soundOffset == 12 ? colorSelectedTranspose : colorTranspose);
    }
}