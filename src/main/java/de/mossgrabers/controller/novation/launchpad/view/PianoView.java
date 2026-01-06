// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import java.util.Arrays;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.ScrollStates;
import de.mossgrabers.framework.view.PianoViewHelper;
import de.mossgrabers.framework.view.Views;


/**
 * The Piano view.
 *
 * @author Jürgen Moßgraber
 */
public class PianoView extends PlayView
{
    private final PlayControls playControls;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PianoView (final LaunchpadControlSurface surface, final IModel model)
    {
        super (Views.NAME_PIANO, surface, model);

        this.playControls = new PlayControls (surface, this.scales);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final boolean isActive = this.playControls.isActive ();
        PianoViewHelper.drawGrid (this.surface.getPadGrid (), this.model, this.keyManager, isActive ? 36 + 8 : 36, isActive ? 7 : 8, 8);
        this.playControls.draw ();
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
    protected int [] getMapping ()
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return EMPTY_TABLE;

        final int [] noteMatrix = this.scales.getPianoMatrix (8, 8);

        if (this.blockNotes > 0)
        {
            final int startNote = this.scales.getStartNote ();
            final int endNote = this.scales.getEndNote ();
            final int length = endNote - startNote - this.blockNotes;
            System.arraycopy (noteMatrix, startNote, noteMatrix, startNote + this.blockNotes, length);
            Arrays.fill (noteMatrix, startNote, startNote + 8, -1);
        }

        return noteMatrix;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN || !this.model.canSelectedTrackHoldNotes ())
            return;

        if (buttonID == ButtonID.SCENE4)
        {
            this.playControls.toggle ();
            this.setBlockedNotes (this.playControls.isActive () ? 8 : 0);
            this.updateNoteMapping ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return buttonID == ButtonID.SCENE4 ? this.playControls.getToggleButtonColor () : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int key, final int velocity)
    {
        if (!this.playControls.handleGridNotes (key, velocity))
            super.onGridNote (key, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void updateScrollStates (final ScrollStates scrollStates)
    {
        final int pianoOctave = this.scales.getPianoOctave ();
        scrollStates.setCanScrollLeft (false);
        scrollStates.setCanScrollRight (false);
        scrollStates.setCanScrollUp (pianoOctave < 3);
        scrollStates.setCanScrollDown (pianoOctave > -3);
    }
}