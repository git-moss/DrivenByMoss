// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.view;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractPolySequencerView;


/**
 * The Poly Sequencer view.
 *
 * @author Jürgen Moßgraber
 */
public class PolySequencerView extends AbstractPolySequencerView<FireControlSurface, FireConfiguration> implements IFireView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public PolySequencerView (final FireControlSurface surface, final IModel model, final boolean useTrackColor)
    {
        super (surface, model, useTrackColor, 16, 4, 2);
    }


    /** {@inheritDoc} */
    @Override
    public int getSoloButtonColor (final int index)
    {
        final ITrack cursorTrack = this.model.getCursorTrack ();
        switch (index)
        {
            case 0:
                return 0;

            case 1:
                return cursorTrack.doesExist () && cursorTrack.isMute () ? 3 : 0;

            case 2:
                return cursorTrack.doesExist () && cursorTrack.isSolo () ? 4 : 0;

            case 3:
                return cursorTrack.doesExist () && cursorTrack.isRecArm () ? 1 : 0;

            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        switch (buttonID)
        {
            case SCENE1:
            case SCENE2:
            case SCENE3:
            case SCENE4:
                return 0;

            default:
                return super.getButtonColor (buttonID);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN || !this.isActive ())
            return;

        final ITrack cursorTrack = this.model.getCursorTrack ();
        final INoteClip clip = this.getClip ();

        switch (buttonID)
        {
            case ARROW_LEFT:
                if (this.surface.isPressed (ButtonID.ALT))
                    this.setResolutionIndex (this.getResolutionIndex () - 1);
                else
                {
                    clip.scrollStepsPageBackwards ();
                    this.mvHelper.notifyEditPage (clip);
                }
                break;

            case ARROW_RIGHT:
                if (this.surface.isPressed (ButtonID.ALT))
                    this.setResolutionIndex (this.getResolutionIndex () + 1);
                else
                {
                    clip.scrollStepsPageForward ();
                    this.mvHelper.notifyEditPage (clip);
                }
                break;

            case SCENE1:
                cursorTrack.stop (false);
                break;

            case SCENE2:
                cursorTrack.toggleMute ();
                break;

            case SCENE3:
                cursorTrack.toggleSolo ();
                break;

            case SCENE4:
                cursorTrack.toggleRecArm ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectKnobValue (final int value)
    {
        if (this.model.getValueChanger ().isIncrease (value))
            this.onOctaveUp (ButtonEvent.DOWN);
        else
            this.onOctaveDown (ButtonEvent.DOWN);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final int channel, final int step)
    {
        if (this.surface.getModeManager ().isActive (Modes.NOTE))
        {
            final NotePosition notePosition = new NotePosition (channel, step, 0);
            for (int row = 0; row < 128; row++)
            {
                notePosition.setNote (row);
                if (clip.getStep (notePosition).getState () == StepState.START)
                    this.editNote (clip, notePosition, true);
            }
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, channel, step);
    }
}