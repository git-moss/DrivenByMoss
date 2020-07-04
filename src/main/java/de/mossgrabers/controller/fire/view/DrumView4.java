// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.view;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.controller.fire.mode.NoteMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;


/**
 * The 4 lane drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView4 extends AbstractDrumView<FireControlSurface, FireConfiguration> implements IFireView
{
    private static final int    NUM_DISPLAY_COLS = 16;

    private final int           columns;
    private final ICursorDevice primary;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView4 (final FireControlSurface surface, final IModel model)
    {
        super ("Drum 4", surface, model, 2, 0, false);

        this.columns = 16;
        this.primary = this.model.getInstrumentDevice ();
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.isActive ())
            return;

        final int index = note - DRUM_START_KEY;
        final int x = index % this.columns;
        final int y = index / this.columns;

        final int sound = y % 4 + this.scales.getDrumOffset ();
        final int step = this.columns * (y / 4) + x;

        final int channel = this.configuration.getMidiEditChannel ();
        final int vel = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity;
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


    /**
     * Handle button combinations on the note area of the sequencer.
     *
     * @param clip The sequenced midi clip
     * @param channel The MIDI channel of the note
     * @param row The row in the current page in the clip
     * @param note The note in the current page of the pad in the clip
     * @param step The step in the current page in the clip
     * @param velocity The velocity
     * @param vel The velocity or accent
     * @return True if handled
     */
    private boolean handleNoteAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int row, final int note, final int velocity, final int vel)
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
                this.browserModeActivator.activate ();
            }
            return true;
        }

        // Change length of a note or create a new one with a length
        final int lines = 4;
        final boolean isLower = row / lines == 0;
        final int offset = row * this.columns;
        for (int s = 0; s < step; s++)
        {
            final IHwButton button = this.surface.getButton (ButtonID.get (ButtonID.PAD1, offset + s));
            if (button.isLongPressed ())
            {
                int start = s;
                if (isLower)
                    start += this.columns;
                button.setConsumed ();
                final int length = step - start + 1;
                final double duration = length * Resolution.getValueAt (this.getResolutionIndex ());
                final int state = note < 0 ? 0 : clip.getStep (channel, start, note).getState ();
                if (state == IStepInfo.NOTE_START)
                    clip.updateStepDuration (channel, start, note, duration);
                else
                    clip.setStep (channel, start, note, vel, duration);
                return true;
            }
        }

        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        if (!this.isActive ())
        {
            padGrid.turnOff ();
            return;
        }

        // Clip length/loop area
        final INoteClip clip = this.getClip ();
        final int step = clip.getCurrentStep ();

        // Paint the sequencer steps
        final int hiStep = this.isInXRange (step) ? step % DrumView4.NUM_DISPLAY_COLS : -1;
        final int offsetY = this.scales.getDrumOffset ();
        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        for (int sound = 0; sound < 4; sound++)
        {
            final int noteRow = offsetY + sound;
            final ColorEx drumPadColor = this.getDrumPadColor (this.primary, sound);
            for (int col = 0; col < DrumView4.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = clip.getStep (editMidiChannel, col, noteRow).getState ();
                final boolean hilite = col == hiStep;
                final int x = col % this.columns;
                int y = 0;
                if (col >= this.columns)
                    y += 4;
                y += sound;
                padGrid.lightEx (x, 3 - y, this.getStepColor (isSet, hilite, drumPadColor));
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.surface.setKeyTranslationTable (this.scales.translateMatrixToGrid (EMPTY_TABLE));
    }


    /** {@inheritDoc} */
    @Override
    public int getSoloButtonColor (final int index)
    {
        if (!this.isActive ())
            return 0;

        final int pos = 3 - index;
        if (this.primary.hasDrumPads ())
            return this.primary.getDrumPadBank ().getItem (pos).isSelected () ? 4 : 0;
        return 0;
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

        if (buttonID == ButtonID.ARROW_LEFT)
        {
            if (this.surface.isPressed (ButtonID.ALT))
                this.setResolutionIndex (this.selectedResolutionIndex - 1);
            else
                this.getClip ().scrollStepsPageBackwards ();
            return;
        }

        if (buttonID == ButtonID.ARROW_RIGHT)
        {
            if (this.surface.isPressed (ButtonID.ALT))
                this.setResolutionIndex (this.selectedResolutionIndex + 1);
            else
                this.getClip ().scrollStepsPageForward ();
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

        final boolean isUp = this.model.getValueChanger ().calcKnobSpeed (value) > 0;
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