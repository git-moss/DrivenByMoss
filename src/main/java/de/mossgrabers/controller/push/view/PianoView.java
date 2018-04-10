// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.controller.push.controller.PushControlSurface;


/**
 * The Piano view.
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
    public PianoView (final PushControlSurface surface, final IModel model)
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

        final ColorManager colorManager = this.model.getColorManager ();
        final boolean isRecording = this.model.hasRecordingState ();
        final ITrack track = this.model.getCurrentTrackBank ().getSelectedTrack ();
        final int playKeyColor = colorManager.getColor (isRecording ? AbstractPlayView.COLOR_RECORD : AbstractPlayView.COLOR_PLAY);
        final int whiteKeyColor = colorManager.getColor (Scales.SCALE_COLOR_NOTE);
        final int blackKeyColor = colorManager.getColor (replaceOctaveColorWithTrackColor (track, Scales.SCALE_COLOR_OCTAVE));
        final int offKeyColor = colorManager.getColor (Scales.SCALE_COLOR_OFF);

        for (int i = 0; i < 8; i++)
        {
            if (i % 2 == 0)
            {
                for (int j = 0; j < 8; j++)
                {
                    final int n = 36 + 8 * i + j;
                    gridPad.light (n, this.pressedKeys[n] > 0 ? playKeyColor : whiteKeyColor, -1, false);
                }
            }
            else
            {
                for (int j = 0; j < 8; j++)
                {
                    final int n = 36 + 8 * i + j;
                    if (j == 0 || j == 3 || j == 7)
                        gridPad.light (n, offKeyColor, -1, false);
                    else
                        gridPad.light (n, this.pressedKeys[n] > 0 ? playKeyColor : blackKeyColor, -1, false);
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