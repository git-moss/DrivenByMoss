// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.PianoViewHelper;
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


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        if (ButtonID.isSceneButton (buttonID) && this.surface.isPressed (ButtonID.REPEAT))
            return NoteRepeatSceneHelper.getButtonColorID (this.surface, buttonID);
        return super.getButtonColorID (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (this.surface.isPressed (ButtonID.REPEAT))
            NoteRepeatSceneHelper.handleNoteRepeatSelection (this.surface, 7 - index);
    }
}