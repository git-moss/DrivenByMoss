// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;


/**
 * The play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PianoView extends PlayView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PianoView (final PushControlSurface surface, final Model model)
    {
        super (Views.VIEW_NAME_PIANO, surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final PadGrid gridPad = this.surface.getPadGrid ();
        if (!this.model.canSelectedTrackHoldNotes ())
        {
            gridPad.turnOff ();
            return;
        }

        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int red = isPush2 ? PushColors.PUSH2_COLOR2_RED_HI : PushColors.PUSH1_COLOR2_RED_HI;
        final int green = isPush2 ? PushColors.PUSH2_COLOR2_GREEN_HI : PushColors.PUSH1_COLOR2_GREEN_HI;
        final int white = isPush2 ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH1_COLOR2_WHITE;
        final int off = isPush2 ? PushColors.PUSH2_COLOR2_BLACK : PushColors.PUSH1_COLOR2_BLACK;

        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedTrack ();
        final double [] color = selectedTrack.getColor ();
        final int trackColor = this.model.getColorManager ().getColor (BitwigColors.getColorIndex (color[0], color[1], color[2]));

        final boolean isRecording = this.model.hasRecordingState ();
        for (int i = 0; i < 8; i++)
        {
            if (i % 2 == 0)
            {
                for (int j = 0; j < 8; j++)
                {
                    final int n = 36 + 8 * i + j;
                    gridPad.light (n, this.pressedKeys[n] > 0 ? isRecording ? red : green : white, -1, false);
                }
            }
            else
            {
                for (int j = 0; j < 8; j++)
                {
                    final int n = 36 + 8 * i + j;
                    if (j == 0 || j == 3 || j == 7)
                        gridPad.light (n, off, -1, false);
                    else
                        gridPad.light (n, this.pressedKeys[n] > 0 ? isRecording ? red : green : trackColor, -1, false);
                }
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes () || this.noteMap[note] == -1)
            return;

        // Mark selected notes
        for (int i = 0; i < 128; i++)
        {
            if (this.noteMap[note] == this.noteMap[i])
                this.pressedKeys[i] = velocity;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.clearPressedKeys ();
        this.scales.decPianoOctave ();
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getPianoRangeText (), true, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.clearPressedKeys ();
        this.scales.incPianoOctave ();
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getPianoRangeText (), true, true);
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        // Workaround: https://github.com/git-moss/Push4Bitwig/issues/7
        this.surface.scheduleTask (this::delayedUpdateNoteMapping, 100);
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        super.updateButtons ();
        final int octave = this.scales.getPianoOctave ();
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, octave < Scales.PIANO_OCTAVE_RANGE ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, octave > -Scales.PIANO_OCTAVE_RANGE ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
    }


    private void delayedUpdateNoteMapping ()
    {
        this.noteMap = this.model.canSelectedTrackHoldNotes () ? this.scales.getPianoMatrix () : Scales.getEmptyMatrix ();
        this.surface.setKeyTranslationTable (this.noteMap);
    }
}