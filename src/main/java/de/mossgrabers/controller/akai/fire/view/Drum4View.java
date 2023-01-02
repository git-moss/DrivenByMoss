// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
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
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractDrum4View;

import java.util.Optional;


/**
 * The 4 lane drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Drum4View extends AbstractDrum4View<FireControlSurface, FireConfiguration> implements IFireView
{
    private boolean blockSelectKnob = false;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public Drum4View (final FireControlSurface surface, final IModel model)
    {
        super (surface, model, 4, 4, 16, 16, true, false);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.isActive ())
            return;

        final int index = note - DRUM_START_KEY;
        final int x = index % this.numColumns;
        final int y = index / this.numColumns;

        final int sound = y % this.lanes + this.scales.getDrumOffset ();
        final int step = this.numColumns * (y / this.lanes) + x;

        final int channel = this.configuration.getMidiEditChannel ();
        final int vel = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).getPressedVelocity ();
        final INoteClip clip = this.getClip ();
        final NotePosition notePosition = new NotePosition (channel, step, sound);

        if (this.handleSequencerAreaButtonCombinations (clip, notePosition, y, velocity, vel))
            return;

        // Handle note editor mode
        final ModeManager modeManager = this.surface.getModeManager ();
        if (velocity > 0)
        {
            if (modeManager.isActive (Modes.NOTE))
            {
                // Store existing note for editing
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

        if (velocity == 0)
            clip.toggleStep (notePosition, vel);
    }


    /** {@inheritDoc} */
    @Override
    public int getSoloButtonColor (final int index)
    {
        return this.isActive () && this.primary.hasDrumPads () && this.primary.getDrumPadBank ().getItem (3 - index).isSelected () ? this.lanes : 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        switch (buttonID)
        {
            case SCENE1, SCENE2, SCENE3, SCENE4:
                final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
                if (this.primary.hasDrumPads ())
                {
                    final IDrumPad item = this.primary.getDrumPadBank ().getItem (3 - scene);
                    if (item.doesExist ())
                    {
                        if (item.isSolo ())
                            return 2;
                        return item.isMute () ? 0 : 1;
                    }
                }
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

        final INoteClip clip = this.getClip ();
        if (buttonID == ButtonID.ARROW_LEFT)
        {
            if (this.surface.isPressed (ButtonID.ALT))
                this.setResolutionIndex (this.getResolutionIndex () - 1);
            else
            {
                clip.scrollStepsPageBackwards ();
                this.mvHelper.notifyEditPage (clip);
            }
            return;
        }

        if (buttonID == ButtonID.ARROW_RIGHT)
        {
            if (this.surface.isPressed (ButtonID.ALT))
                this.setResolutionIndex (this.getResolutionIndex () + 1);
            else
            {
                clip.scrollStepsPageForward ();
                this.mvHelper.notifyEditPage (clip);
            }
            return;
        }

        if (!ButtonID.isSceneButton (buttonID))
            return;

        final int index = 3 - (buttonID.ordinal () - ButtonID.SCENE1.ordinal ());
        if (!this.primary.hasDrumPads ())
            return;

        final IDrumPad item = this.primary.getDrumPadBank ().getItem (index);
        if (!item.doesExist ())
            return;

        if (this.surface.isPressed (ButtonID.SHIFT))
        {
            item.toggleSolo ();
            return;
        }

        if (this.surface.isPressed (ButtonID.ALT))
        {
            this.surface.getDisplay ().notify (item.getName ());
            return;
        }

        item.toggleMute ();
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectKnobValue (final int value)
    {
        if (!this.primary.hasDrumPads () || this.blockSelectKnob)
            return;

        final boolean isUp = this.model.getValueChanger ().isIncrease (value);
        final IDrumPadBank drumPadBank = this.primary.getDrumPadBank ();

        final Optional<ILayer> sel = drumPadBank.getSelectedItem ();
        final int pageSize = drumPadBank.getPageSize ();

        if (isUp)
        {
            final int index = sel.isEmpty () ? pageSize : sel.get ().getIndex () + 1;
            if (index == pageSize)
            {
                this.adjustPage (isUp, 0);
                return;
            }
            this.selectDrumPad (index);
            return;
        }

        final int index = sel.isEmpty () ? -1 : sel.get ().getIndex () - 1;
        if (index == -1)
        {
            this.adjustPage (isUp, pageSize - 1);
            return;
        }
        this.selectDrumPad (index);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int row, final int velocity, final int accentVelocity)
    {
        if (this.isButtonCombination (ButtonID.BROWSE))
        {
            if (velocity == 0)
            {
                this.surface.setTriggerConsumed (ButtonID.BROWSE);

                if (!this.primary.hasDrumPads ())
                    return true;

                final IDrumPadBank drumPadBank = this.primary.getDrumPadBank ();
                this.scrollPosition = drumPadBank.getScrollPosition ();
                this.model.getBrowser ().replace (drumPadBank.getItem (row));
            }
            return true;
        }

        final boolean isUpPressed = this.surface.isPressed (ButtonID.ARROW_UP);
        if (isUpPressed || this.surface.isPressed (ButtonID.ARROW_DOWN))
        {
            this.surface.setTriggerConsumed (isUpPressed ? ButtonID.ARROW_UP : ButtonID.ARROW_DOWN);
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, isUpPressed);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, row, velocity, accentVelocity);
    }


    private void adjustPage (final boolean isUp, final int selection)
    {
        this.blockSelectKnob = true;
        this.changeOctave (ButtonEvent.DOWN, isUp, 4, true, true);
        this.surface.scheduleTask ( () -> {
            this.selectDrumPad (selection);
            this.blockSelectKnob = false;
        }, 100);
    }


    private void selectDrumPad (final int index)
    {
        this.primary.getDrumPadBank ().getItem (index).select ();
        final IMode activeMode = this.surface.getModeManager ().getActive ();
        if (activeMode instanceof final FireLayerMode fireLayerMode)
            fireLayerMode.parametersAdjusted ();
    }
}