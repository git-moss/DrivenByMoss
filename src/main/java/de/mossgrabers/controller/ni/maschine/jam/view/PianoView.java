// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.PianoViewHelper;
import de.mossgrabers.framework.view.Views;


/**
 * The Piano view.
 *
 * @author Jürgen Moßgraber
 */
public class PianoView extends PlayView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PianoView (final MaschineJamControlSurface surface, final IModel model)
    {
        super (Views.NAME_PIANO, surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        PianoViewHelper.drawGrid (this.surface.getPadGrid (), this.model, this.keyManager);
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
        this.delayedUpdateNoteMapping (this.model.canSelectedTrackHoldNotes () ? this.scales.getPianoMatrix (8, 8) : EMPTY_TABLE);
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