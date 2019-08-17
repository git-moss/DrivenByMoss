// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.scale.Scales;


/**
 * Editing of note parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteMode extends BaseMode
{
    INoteClip            clip         = null;
    double               noteLength   = 1.0;
    int                  noteVelocity = 127;
    int                  step         = 0;
    int                  note         = 60;

    private final Object updateLock   = new Object ();
    private boolean      isDirty      = false;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteMode (final PushControlSurface surface, final IModel model)
    {
        super ("Note", surface, model);
    }


    /**
     * Set the values.
     *
     * @param clip The clip to edit
     * @param step The step to edit
     * @param note The note to edit
     * @param noteLength The note length to edit
     * @param noteVelocity The note velocity to edit
     */
    public void setValues (final INoteClip clip, final int step, final int note, final double noteLength, final int noteVelocity)
    {
        this.clip = clip;
        this.step = step;
        this.note = note;
        this.noteLength = noteLength;
        this.noteVelocity = noteVelocity;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();
        switch (index)
        {
            // Note length
            case 0:
                if (!this.increaseKnobMovement ())
                    return;
                final double speed = valueChanger.calcKnobSpeed (value, 1);
                this.noteLength = Math.max (1.0, this.noteLength + speed);
                break;
            // Note length fine
            case 1:
                if (!this.increaseKnobMovement ())
                    return;
                final double speed2 = valueChanger.calcKnobSpeed (value, 0.1);
                this.noteLength = Math.max (0.1, this.noteLength + speed2);
                break;
            // Note velocity
            case 2:
                this.noteVelocity = valueChanger.changeValue (value, this.noteVelocity, 1, 128);
                break;
            default:
                return;
        }

        synchronized (this.updateLock)
        {
            if (this.isDirty)
                return;
            this.isDirty = true;
            this.model.getHost ().scheduleTask (this::updateNote, 200);
        }
    }


    private void updateNote ()
    {
        synchronized (this.updateLock)
        {
            this.clip.clearStep (this.step, this.note);
            // TODO Bugfix required: setStep makes Bitwig hang
            // https://github.com/teotigraphix/Framework4Bitwig/issues/124
            this.clip.setStep (this.step, this.note, this.noteVelocity, this.noteLength);
            this.isDirty = false;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final int quarters = (int) Math.floor (this.noteLength);
        final int fine = (int) Math.floor (this.noteLength * 100) % 100;
        final ITextDisplay d = this.surface.getDisplay ();
        d.clear ().setCell (0, 0, "Quarters").setCell (1, 0, Integer.toString (quarters));
        d.setCell (0, 1, "Fine").setCell (1, 1, Integer.toString (fine));
        d.setCell (0, 2, "Velocity").setCell (1, 2, Integer.toString (this.noteVelocity * 100 / 127) + "%");
        d.setBlock (3, 0, "Step: " + (this.step + 1));
        d.setBlock (3, 1, "Selec. Note: " + Scales.formatNoteAndOctave (this.note, -3));
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final int quarters = (int) Math.floor (this.noteLength);
        final int fine = (int) Math.floor (this.noteLength * 100) % 100;

        final DisplayModel message = this.surface.getGraphicsDisplay ().getModel ();
        message.addParameterElement ("Quarters", quarters, Integer.toString (quarters), this.isKnobTouched[0], -1);
        message.addParameterElement ("Fine", fine, Integer.toString (fine), this.isKnobTouched[1], -1);
        final int parameterValue = this.noteVelocity * 1023 / 127;
        message.addParameterElement ("Velocity", parameterValue, Integer.toString (this.noteVelocity * 100 / 127) + "%", this.isKnobTouched[2], parameterValue);
        message.addOptionElement ("    Step: " + (this.step + 1), "", false, "    Selected note: " + Scales.formatNoteAndOctave (this.note, -3), "", false, false);
        for (int i = 4; i < 8; i++)
            message.addOptionElement ("", "", false, "", "", false, false);
        message.send ();
    }
}