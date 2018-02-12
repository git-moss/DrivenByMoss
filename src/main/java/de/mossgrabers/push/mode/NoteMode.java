// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;


/**
 * Editing of note parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteMode extends BaseMode
{
    ICursorClip clip         = null;
    double      noteLength   = 1.0;
    int         noteVelocity = 127;
    int         step         = 0;
    int         note         = 60;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
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
    public void setValues (final ICursorClip clip, final int step, final int note, final double noteLength, final int noteVelocity)
    {
        this.clip = clip;
        this.step = step;
        this.note = note;
        this.noteLength = noteLength;
        this.noteVelocity = noteVelocity;
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final ValueChanger valueChanger = this.model.getValueChanger ();
        switch (index)
        {
            case 0:
                this.noteLength = valueChanger.changeIntValue (value, (int) Math.floor (this.noteLength), 1, 1024);
                this.clip.setStep (this.step, this.note, this.noteVelocity, this.noteLength);
                break;
            case 1:
                this.noteLength = valueChanger.changeValue (value, (int) this.noteLength, 0, 1024);
                this.clip.setStep (this.step, this.note, this.noteVelocity, this.noteLength);
                break;
            case 2:
                this.noteVelocity = valueChanger.changeIntValue (value, this.noteVelocity, 1, 128);
                this.clip.setStep (this.step, this.note, this.noteVelocity, this.noteLength);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final int quarters = (int) Math.floor (this.noteLength);
        final int fine = (int) Math.floor (this.noteLength * 100) % 100;
        final Display d = this.surface.getDisplay ();
        d.clear ().setCell (0, 0, "Quarters").setCell (1, 0, Integer.toString (quarters));
        d.setCell (0, 1, "Fine").setCell (1, 1, Integer.toString (fine));
        d.setCell (0, 2, "Velocity").setCell (1, 2, Integer.toString (this.noteVelocity)).allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final int quarters = (int) Math.floor (this.noteLength);
        final int fine = (int) Math.floor (this.noteLength * 100) % 100;

        final PushDisplay display = (PushDisplay) this.surface.getDisplay ();
        final DisplayMessage message = display.createMessage ();
        message.addParameterElement ("Quarters", quarters, Integer.toString (quarters), this.isKnobTouched[0], -1);
        message.addParameterElement ("Fine", fine, Integer.toString (fine), this.isKnobTouched[1], -1);
        message.addParameterElement ("Velocity", this.noteVelocity * 1023 / 127, Integer.toString (this.noteVelocity), this.isKnobTouched[2], -1);
        for (int i = 3; i < 8; i++)
            message.addOptionElement ("", "", false, "", "", false, false);
        display.send (message);
    }
}