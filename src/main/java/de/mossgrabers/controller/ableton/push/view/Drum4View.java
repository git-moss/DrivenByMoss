// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrum4View;


/**
 * The Drum 4 view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Drum4View extends AbstractDrum4View<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public Drum4View (final PushControlSurface surface, final IModel model)
    {
        super (surface, model, true);
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
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - DRUM_START_KEY;
        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();

        final int x = index % 8;
        final int y = index / 8;

        final int sound = y % 4;
        final int stepX = 8 * (1 - y / 4) + x;
        final int stepY = this.scales.getDrumOffset () + sound;

        final int channel = this.configuration.getMidiEditChannel ();
        final INoteClip clip = this.getClip ();
        this.editNote (clip, channel, stepX, stepY, false);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (this.surface.isPressed (ButtonID.REPEAT))
        {
            NoteRepeatSceneHelper.handleNoteRepeatSelection (this.surface, 7 - index);
            return;
        }

        super.onButton (buttonID, event, velocity);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleNoteAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int row, final int note, final int velocity, final int accentVelocity)
    {
        final boolean isSelectPressed = this.surface.isSelectPressed ();

        if (this.surface.isShiftPressed ())
        {
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, channel, step, note, velocity, !isSelectPressed);
            return true;
        }

        if (isSelectPressed)
        {
            if (velocity > 0)
                this.editNote (clip, channel, step, note, true);
            return true;
        }

        return super.handleNoteAreaButtonCombinations (clip, channel, step, row, note, velocity, accentVelocity);
    }
}