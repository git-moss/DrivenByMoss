// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.controller.PushColorManager;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.Views;


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

        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        this.getClip ().toggleStep (editMidiChannel, col, this.scales.getDrumOffset () + this.selectedPad + sound, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
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
        final int blueHi = isPush2 ? PushColorManager.PUSH2_COLOR2_BLUE_HI : PushColorManager.PUSH1_COLOR2_BLUE_HI;
        final int greenLo = isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN_LO : PushColorManager.PUSH1_COLOR2_GREEN_LO;
        final int greenHi = isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN_HI : PushColorManager.PUSH1_COLOR2_GREEN_HI;
        final int off = isPush2 ? PushColorManager.PUSH2_COLOR2_BLACK : PushColorManager.PUSH1_COLOR2_BLACK;
        final int hiStep = this.isInXRange (step) ? step % DrumView8.NUM_DISPLAY_COLS : -1;
        final int offsetY = this.scales.getDrumOffset ();
        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        for (int sound = 0; sound < 8; sound++)
        {
            for (int col = 0; col < DrumView8.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = this.getClip ().getStep (editMidiChannel, col, offsetY + this.selectedPad + sound + this.soundOffset).getState ();
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
    protected String updateLowerSceneButtons (final int scene)
    {
        if (scene > 1)
            return AbstractSequencerView.COLOR_RESOLUTION_OFF;

        final int [] offsets =
        {
            0,
            8
        };

        return this.soundOffset == offsets[scene] ? AbstractSequencerView.COLOR_TRANSPOSE_SELECTED : AbstractSequencerView.COLOR_TRANSPOSE;
    }


    /** {@inheritDoc} */
    @Override
    protected void onLowerScene (final int index)
    {
        // 0, 8
        if (index > 1)
            return;
        this.soundOffset = index == 0 ? 0 : 8;
        this.surface.getDisplay ().notify ("Offset: " + this.soundOffset);
    }
}