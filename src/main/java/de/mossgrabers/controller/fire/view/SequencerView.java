// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.view;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.controller.fire.mode.NoteMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractNoteSequencerView;
import de.mossgrabers.framework.view.Views;


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
        super (Views.VIEW_NAME_SEQUENCER, surface, model, 16, 4, true);

        this.numDisplayRows = 4;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int index, final int x, final int y, final int velocity)
    {
        final INoteClip clip = this.getClip ();
        final int channel = this.configuration.getMidiEditChannel ();
        final int mappedY = this.keyManager.map (y);

        // Handle note editor mode
        final ModeManager modeManager = this.surface.getModeManager ();
        if (velocity > 0)
        {
            // Turn on Note mode if an existing note is pressed
            final int state = clip.getStep (channel, x, mappedY).getState ();
            if (state == IStepInfo.NOTE_START)
            {
                final NoteMode noteMode = (NoteMode) modeManager.get (Modes.NOTE);
                noteMode.setValues (clip, channel, x, mappedY);
                modeManager.setActive (Modes.NOTE);
            }
        }
        else
        {
            // Turn off Note mode
            if (modeManager.isActive (Modes.NOTE))
                modeManager.restore ();

            if (this.isNoteEdited)
            {
                this.isNoteEdited = false;
                return;
            }
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

        final ITrack cursorTrack = this.model.getCursorTrack ();
        final INoteClip clip = this.getClip ();

        switch (buttonID)
        {
            case ARROW_LEFT:
                if (this.surface.isPressed (ButtonID.ALT))
                    this.setResolutionIndex (this.selectedResolutionIndex - 1);
                else
                {
                    clip.scrollStepsPageBackwards ();
                    this.surface.getDisplay ().notify ("Page: " + (clip.getEditPage () + 1));
                }
                return;

            case ARROW_RIGHT:
                if (this.surface.isPressed (ButtonID.ALT))
                    this.setResolutionIndex (this.selectedResolutionIndex + 1);
                else
                {
                    clip.scrollStepsPageForward ();
                    this.surface.getDisplay ().notify ("Page: " + (clip.getEditPage () + 1));
                }
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
}