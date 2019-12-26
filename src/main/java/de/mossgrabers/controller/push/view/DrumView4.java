// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.controller.PushColorManager;
import de.mossgrabers.controller.push.controller.PushControlSurface;
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
        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        this.getClip ().toggleStep (editMidiChannel, col, offsetY + this.selectedPad + sound, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
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
        final int blueHi = isPush2 ? PushColorManager.PUSH2_COLOR2_BLUE_HI : PushColorManager.PUSH1_COLOR2_BLUE_HI;
        final int greenLo = isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN_LO : PushColorManager.PUSH1_COLOR2_GREEN_LO;
        final int greenHi = isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN_HI : PushColorManager.PUSH1_COLOR2_GREEN_HI;
        final int off = isPush2 ? PushColorManager.PUSH2_COLOR2_BLACK : PushColorManager.PUSH1_COLOR2_BLACK;
        final int hiStep = this.isInXRange (step) ? step % DrumView4.NUM_DISPLAY_COLS : -1;
        final int offsetY = this.scales.getDrumOffset ();
        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        for (int sound = 0; sound < 4; sound++)
        {
            for (int col = 0; col < DrumView4.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = clip.getStep (editMidiChannel, col, offsetY + this.selectedPad + sound + this.soundOffset).getState ();
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
    protected void onLowerScene (final int index)
    {
        if (index > 3)
            return;

        // 7, 6, 5, 4
        this.soundOffset = 4 * index;
        this.surface.getDisplay ().notify ("Offset: " + this.soundOffset);
    }


    /** {@inheritDoc} */
    @Override
    protected String updateLowerSceneButtons (final int scene)
    {
        final int [] offsets =
        {
            0,
            4,
            8,
            12
        };

        return this.soundOffset == offsets[scene] ? AbstractSequencerView.COLOR_TRANSPOSE_SELECTED : AbstractSequencerView.COLOR_TRANSPOSE;
    }
}