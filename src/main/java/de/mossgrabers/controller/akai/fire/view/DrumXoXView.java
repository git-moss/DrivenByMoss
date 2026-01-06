// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.view;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.mode.FireLayerMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.INoteEditorMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractDrumXoXView;


/**
 * The Drum XoX view.
 *
 * @author Jürgen Moßgraber
 */
public class DrumXoXView extends AbstractDrumXoXView<FireControlSurface, FireConfiguration> implements IFireView
{
    protected boolean isCopy        = true;
    protected boolean isSolo        = true;
    protected boolean editLoopRange = false;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumXoXView (final FireControlSurface surface, final IModel model)
    {
        super (Views.NAME_DRUM, surface, model, 16);

        this.buttonSelect = ButtonID.ALT;
        this.deleteButton = ButtonID.SCENE1;
        this.editLoopTriggerButton = ButtonID.SCENE3;
    }


    /** {@inheritDoc} */
    @Override
    protected IDrumDevice getDrumDevice ()
    {
        return this.model.getDrumDevice (16);
    }


    /** {@inheritDoc} */
    @Override
    protected void selectDrumPad (final int index)
    {
        super.selectDrumPad (index);

        if (this.surface.getModeManager ().getActive () instanceof final FireLayerMode fireLayerMode)
            fireLayerMode.parametersAdjusted ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP || !this.isActive () || this.handleGridNavigation (buttonID) || !ButtonID.isSceneButton (buttonID))
            return;

        final IDrumPadBank drumPadBank2 = this.getDrumDevice ().getDrumPadBank ();
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        switch (index)
        {
            case 0:
                this.isCopy = !this.isCopy;
                break;

            case 1:
                if (this.isButtonCombination (ButtonID.ALT))
                {
                    for (int i = 0; i < drumPadBank2.getPageSize (); i++)
                    {
                        final IDrumPad item = drumPadBank2.getItem (i);
                        if (this.isSolo)
                            item.setSolo (false);
                        else
                            item.setMute (false);
                    }
                    return;
                }
                this.isSolo = !this.isSolo;
                break;

            case 2:
                this.editLoopRange = !this.editLoopRange;
                break;

            case 3:
            default:
                this.configuration.toggleNoteRepeatActive ();
                this.mvHelper.delayDisplay ( () -> "Note Repeat: " + (this.configuration.isNoteRepeatActive () ? "On" : "Off"));
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectKnobValue (final int value)
    {
        final IDrumDevice drumDevice = this.getDrumDevice ();
        if (!drumDevice.hasDrumPads () || this.blockSelectKnob)
            return;

        final boolean isUp = this.model.getValueChanger ().isIncrease (value);

        // Change note repeat if active and a pad is held
        if (this.configuration.isNoteRepeatActive ())
        {
            boolean isDrumPadPressed = false;
            for (int i = 0; i < 16; i++)
            {
                if (this.surface.isPressed (ButtonID.get (ButtonID.PAD33, i)))
                    isDrumPadPressed = true;
            }
            if (isDrumPadPressed)
            {
                final Resolution activePeriod = this.configuration.getNoteRepeatPeriod ();
                final Resolution sel;
                if (isUp)
                    sel = NEXT_RESOLUTION.get (activePeriod);
                else
                    sel = PREV_RESOLUTION.get (activePeriod);
                this.configuration.setNoteRepeatPeriod (sel);
                this.mvHelper.delayDisplay ( () -> "Period: " + sel.getName ());
                return;
            }
        }

        this.adjustPage (isUp, 0);
    }


    /** {@inheritDoc} */
    @Override
    public int getSoloButtonColor (final int index)
    {
        if (!this.isActive ())
            return 0;

        switch (index)
        {
            case 0:
                return this.isCopy ? 2 : 1;

            case 1:
                return this.isSolo ? 2 : 1;

            case 2:
                return this.isEditLoopRange () ? 2 : 0;

            default:
            case 3:
                return this.configuration.isNoteRepeatActive () ? 2 : 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void handleClipLaunch (final ISlot slot, final boolean isPressed)
    {
        if (this.isAlternateFunction ())
            slot.select ();
        else
            slot.launch (isPressed, this.surface.isPressed (this.buttonSelect));
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isAlternateFunction ()
    {
        return this.surface.isPressed (ButtonID.ALT) && this.surface.isPressed (ButtonID.SHIFT);
    }


    /**
     * Handle the grid left/right buttons.
     *
     * @param buttonID The button ID
     * @return True if handled
     */
    protected boolean handleGridNavigation (final ButtonID buttonID)
    {
        final INoteClip clip = this.getClip ();
        if (buttonID == ButtonID.ARROW_LEFT)
        {
            if (this.surface.isPressed (ButtonID.SHIFT))
                this.rotateStepsLeft (clip);
            else if (this.surface.isPressed (ButtonID.ALT))
                this.setResolutionIndex (this.getResolutionIndex () - 1);
            else
            {
                clip.scrollStepsPageBackwards ();
                this.mvHelper.notifyEditPage (clip);
            }
            return true;
        }

        if (buttonID == ButtonID.ARROW_RIGHT)
        {
            if (this.surface.isPressed (ButtonID.SHIFT))
                this.rotateStepsRight (clip);
            else if (this.surface.isPressed (ButtonID.ALT))
                this.setResolutionIndex (this.getResolutionIndex () + 1);
            else
            {
                clip.scrollStepsPageForward ();
                this.mvHelper.notifyEditPage (clip);
            }
            return true;
        }

        return false;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int velocity)
    {
        final boolean isUpPressed = this.surface.isPressed (ButtonID.ARROW_UP);
        if (isUpPressed || this.surface.isPressed (ButtonID.ARROW_DOWN))
        {
            this.surface.setTriggerConsumed (isUpPressed ? ButtonID.ARROW_UP : ButtonID.ARROW_DOWN);
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, isUpPressed);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleNoteEditorMode (final int x, final int y, final int offsetY, final int velocity)
    {
        // Handle note editor mode
        final ModeManager modeManager = this.surface.getModeManager ();
        if (velocity > 0)
        {
            final IMode activeMode = modeManager.getActive ();
            if (activeMode instanceof final INoteEditorMode noteMode)
            {
                // Store existing note for editing
                final int sound = offsetY + this.selectedPad;
                final int step = this.numColumns * (this.allRows - 1 - y) + x;
                final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), step, sound);
                final INoteClip clip = this.getClip ();
                final StepState state = clip.getStep (notePosition).getState ();
                if (state == StepState.START)
                {
                    this.editNote (clip, notePosition, true);
                    if (noteMode.getNoteEditor ().getNotes ().isEmpty ())
                    {
                        this.surface.getDisplay ().notify ("Edit Notes: Off");
                        this.isNoteEdited = false;
                    }
                }
                return true;
            }
            return false;
        }

        if (this.isNoteEdited)
            this.isNoteEdited = false;
        return modeManager.isActive (Modes.NOTE);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isSoloTrigger ()
    {
        return this.isSolo && this.isButtonCombination (ButtonID.SCENE2);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isMuteTrigger ()
    {
        return !this.isSolo && this.isButtonCombination (ButtonID.SCENE2);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isDeleteTrigger ()
    {
        return !this.isCopy && this.isButtonCombination (this.deleteButton);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isDuplicateTrigger ()
    {
        return this.isCopy && this.isButtonCombination (this.deleteButton);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean isEditLoopRange ()
    {
        return this.surface.isPressed (ButtonID.SCENE3) || this.editLoopRange;
    }
}