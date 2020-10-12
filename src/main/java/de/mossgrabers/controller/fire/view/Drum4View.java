// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.view;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.controller.fire.mode.NoteMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrum4View;


/**
 * The 4 lane drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Drum4View extends AbstractDrum4View<FireControlSurface, FireConfiguration> implements IFireView
{
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

        if (this.handleNoteAreaButtonCombinations (clip, channel, step, y, sound, velocity, vel))
            return;

        // Handle note editor mode
        final ModeManager modeManager = this.surface.getModeManager ();
        if (velocity > 0)
        {
            // Turn on Note mode if an existing note is pressed
            final int state = clip.getStep (channel, step, sound).getState ();
            if (state == IStepInfo.NOTE_START)
            {
                final NoteMode noteMode = (NoteMode) modeManager.getMode (Modes.NOTE);
                noteMode.setValues (clip, channel, step, sound);
                modeManager.setActiveMode (Modes.NOTE);
            }
        }
        else
        {
            // Turn off Note mode
            if (modeManager.isActiveOrTempMode (Modes.NOTE))
                modeManager.restoreMode ();

            if (this.isNoteEdited)
            {
                this.isNoteEdited = false;
                return;
            }
        }

        if (velocity == 0)
            clip.toggleStep (channel, step, sound, vel);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleNoteAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int row, final int note, final int velocity, final int accentVelocity)
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

        return super.handleNoteAreaButtonCombinations (clip, channel, step, row, note, velocity, accentVelocity);
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
            case SCENE1:
            case SCENE2:
            case SCENE3:
            case SCENE4:
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
                this.setResolutionIndex (this.selectedResolutionIndex - 1);
            else
            {
                clip.scrollStepsPageBackwards ();
                this.surface.getDisplay ().notify ("Page: " + (clip.getEditPage () + 1));
            }
            return;
        }

        if (buttonID == ButtonID.ARROW_RIGHT)
        {
            if (this.surface.isPressed (ButtonID.ALT))
                this.setResolutionIndex (this.selectedResolutionIndex + 1);
            else
            {
                clip.scrollStepsPageForward ();
                this.surface.getDisplay ().notify ("Page: " + (clip.getEditPage () + 1));
            }
            return;
        }

        if (!ButtonID.isSceneButton (buttonID))
            return;
        final int index = 3 - (buttonID.ordinal () - ButtonID.SCENE1.ordinal ());
        if (this.primary.hasDrumPads ())
        {
            final IDrumPad item = this.primary.getDrumPadBank ().getItem (index);
            if (this.surface.isPressed (ButtonID.SHIFT))
                item.toggleSolo ();
            else
                item.toggleMute ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectKnobValue (final int value)
    {
        if (!this.primary.hasDrumPads ())
            return;

        final boolean isUp = this.model.getValueChanger ().isIncrease (value);
        final IDrumPadBank drumPadBank = this.primary.getDrumPadBank ();

        final IDrumPad sel = drumPadBank.getSelectedItem ();

        final int index;
        if (isUp)
        {
            index = sel == null ? drumPadBank.getPageSize () : sel.getIndex () + 1;
            if (index == drumPadBank.getPageSize ())
            {
                this.changeOctave (ButtonEvent.DOWN, isUp, 4, true, true);
                this.surface.scheduleTask ( () -> drumPadBank.getItem (0).select (), 100);
            }
            else
                drumPadBank.getItem (index).select ();
        }
        else
        {
            index = sel == null ? -1 : sel.getIndex () - 1;
            if (index == -1)
            {
                this.changeOctave (ButtonEvent.DOWN, isUp, 4, true, true);
                this.surface.scheduleTask ( () -> drumPadBank.getItem (drumPadBank.getPageSize () - 1).select (), 100);
            }
            else
                drumPadBank.getItem (index).select ();
        }
    }
}