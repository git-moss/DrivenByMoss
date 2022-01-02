// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.view;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.controller.akai.apc.mode.NoteMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.StepState;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;


/**
 * The drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends AbstractDrumView<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView (final APCControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 2, 3, surface.isMkII ());
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int index, final int x, final int y, final int offsetY, final int velocity)
    {
        if (!this.isActive ())
            return;

        final ModeManager modeManager = this.surface.getModeManager ();

        if (velocity > 0)
        {
            // Turn on Note mode if an existing note is pressed
            final INoteClip cursorClip = this.getClip ();
            final int channel = this.configuration.getMidiEditChannel ();
            final int step = this.numColumns * (this.allRows - 1 - y) + x;
            final int note = offsetY + this.selectedPad;
            final StepState state = cursorClip.getStep (channel, step, note).getState ();
            if (state == StepState.START)
            {
                final NoteMode noteMode = (NoteMode) modeManager.get (Modes.NOTE);
                noteMode.setValues (cursorClip, channel, step, note);
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

        super.handleSequencerArea (index, x, y, offsetY, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected String getPadContentColor (final IChannel drumPad)
    {
        return this.surface.isMkII () ? DAWColor.getColorIndex (drumPad.getColor ()) : AbstractDrumView.COLOR_PAD_HAS_CONTENT;
    }


    /** {@inheritDoc} */
    @Override
    public void playNote (final int note, final int velocity)
    {
        this.surface.sendMidiEvent (0x90, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN || !this.isActive ())
            return;

        switch (buttonID)
        {
            case SCENE1:
                this.changeOctave (event, true, 4);
                break;
            case SCENE2:
                this.changeOctave (event, false, 4);
                break;
            case SCENE4:
                this.onOctaveUp (event);
                break;
            case SCENE5:
                this.onOctaveDown (event);
                break;
            default:
                // Intentionally empty
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.SCENE3)
            return ColorManager.BUTTON_STATE_OFF;
        return this.isActive () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int note, final int velocity)
    {
        final boolean isUpPressed = this.surface.isPressed (ButtonID.ARROW_UP);
        if (isUpPressed || this.surface.isPressed (ButtonID.ARROW_DOWN))
        {
            this.surface.setTriggerConsumed (isUpPressed ? ButtonID.ARROW_UP : ButtonID.ARROW_DOWN);
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, channel, step, note, velocity, isUpPressed);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, channel, step, note, velocity);
    }
}