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
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractNoteSequencerView;


/**
 * The Sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SequencerView extends AbstractNoteSequencerView<FireControlSurface, FireConfiguration> implements IFireView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SequencerView (final FireControlSurface surface, final IModel model)
    {
        super (Views.NAME_SEQUENCER, surface, model, 16, 4, true);

        this.numDisplayRows = 4;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int index, final int x, final int y, final int velocity)
    {
        // Handle note editor mode
        final ModeManager modeManager = this.surface.getModeManager ();
        if (velocity > 0)
        {
            if (modeManager.isActive (Modes.NOTE))
            {
                // Store existing note for editing
                final INoteClip clip = this.getClip ();
                final int mappedY = this.keyManager.map (y);
                final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), x, mappedY);
                final StepState state = clip.getStep (notePosition).getState ();
                if (state == StepState.START)
                    this.editNote (clip, notePosition, true);
                return;
            }
        }
        else
        {
            if (this.isNoteEdited)
                this.isNoteEdited = false;
            if (modeManager.isActive (Modes.NOTE))
                return;
        }

        super.handleSequencerArea (index, x, y, velocity);
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
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN || !this.isActive ())
            return;

        final boolean isShiftPressed = this.surface.isShiftPressed ();
        final boolean isAltPressed = this.surface.isPressed (ButtonID.ALT);

        final ITrack cursorTrack = this.model.getCursorTrack ();

        switch (buttonID)
        {
            case ARROW_LEFT:
                this.handleArrowLeft (isShiftPressed, isAltPressed);
                return;

            case ARROW_RIGHT:
                this.handleArrowRight (isShiftPressed, isAltPressed);
                return;

            case SCENE1:
                cursorTrack.stop ();
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


    private void handleArrowLeft (final boolean isShiftPressed, final boolean isAltPressed)
    {
        if (isShiftPressed)
        {
            if (isAltPressed)
            {
                this.scales.prevScaleOffset ();
                this.mvHelper.delayDisplay ( () -> Scales.BASES.get (this.scales.getScaleOffsetIndex ()));
            }
            else
            {
                this.scales.prevScale ();
                this.mvHelper.delayDisplay ( () -> this.scales.getScale ().getName ());
            }
            this.updateScale ();
        }
        else if (isAltPressed)
        {
            this.setResolutionIndex (this.getResolutionIndex () - 1);
        }
        else
        {
            final INoteClip clip = this.getClip ();
            clip.scrollStepsPageBackwards ();
            this.mvHelper.notifyEditPage (clip);
        }
    }


    private void handleArrowRight (final boolean isShiftPressed, final boolean isAltPressed)
    {
        if (isShiftPressed)
        {
            if (isAltPressed)
            {
                this.scales.nextScaleOffset ();
                this.mvHelper.delayDisplay ( () -> Scales.BASES.get (this.scales.getScaleOffsetIndex ()));
            }
            else
            {
                this.scales.nextScale ();
                this.mvHelper.delayDisplay ( () -> this.scales.getScale ().getName ());
            }
            this.updateScale ();
        }
        else if (isAltPressed)
        {
            this.setResolutionIndex (this.getResolutionIndex () + 1);
        }
        else
        {
            final INoteClip clip = this.getClip ();
            clip.scrollStepsPageForward ();
            this.mvHelper.notifyEditPage (clip);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int row, final int velocity)
    {
        final boolean isUpPressed = this.surface.isPressed (ButtonID.ARROW_UP);
        if (isUpPressed || this.surface.isPressed (ButtonID.ARROW_DOWN))
        {
            this.surface.setTriggerConsumed (isUpPressed ? ButtonID.ARROW_UP : ButtonID.ARROW_DOWN);
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, isUpPressed);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, row, velocity);
    }
}