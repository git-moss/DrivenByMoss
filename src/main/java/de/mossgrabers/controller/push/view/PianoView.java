// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.Views;


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
        final IPadGrid gridPad = this.surface.getPadGrid ();
        if (!this.model.canSelectedTrackHoldNotes ())
        {
            gridPad.turnOff ();
            return;
        }

        final ColorManager colorManager = this.model.getColorManager ();
        final boolean isRecording = this.model.hasRecordingState ();
        final ITrack track = this.model.getSelectedTrack ();
        final int playKeyColor = colorManager.getColorIndex (isRecording ? AbstractPlayView.COLOR_RECORD : AbstractPlayView.COLOR_PLAY);
        final int whiteKeyColor = colorManager.getColorIndex (Scales.SCALE_COLOR_NOTE);
        final int blackKeyColor = colorManager.getColorIndex (replaceOctaveColorWithTrackColor (track, Scales.SCALE_COLOR_OCTAVE));
        final int offKeyColor = colorManager.getColorIndex (Scales.SCALE_COLOR_OFF);

        for (int i = 0; i < 8; i++)
        {
            if (i % 2 == 0)
            {
                for (int j = 0; j < 8; j++)
                {
                    final int n = 36 + 8 * i + j;
                    gridPad.light (n, this.keyManager.isKeyPressed (n) ? playKeyColor : whiteKeyColor, -1, false);
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
                        gridPad.light (n, this.keyManager.isKeyPressed (n) ? playKeyColor : blackKeyColor, -1, false);
                }
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.keyManager.clearPressedKeys ();
        this.scales.decPianoOctave ();
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getPianoRangeText ());
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.keyManager.clearPressedKeys ();
        this.scales.incPianoOctave ();
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getPianoRangeText ());
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.delayedUpdateNoteMapping (this.model.canSelectedTrackHoldNotes () ? this.scales.getPianoMatrix () : EMPTY_TABLE);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveUpButtonOn ()
    {
        final int octave = this.scales.getPianoOctave ();
        return octave < Scales.PIANO_OCTAVE_RANGE;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveDownButtonOn ()
    {
        final int octave = this.scales.getPianoOctave ();
        return octave > -Scales.PIANO_OCTAVE_RANGE;
    }
}