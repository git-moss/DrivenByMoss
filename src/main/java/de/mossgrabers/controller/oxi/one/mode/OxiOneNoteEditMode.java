// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.mode;

import java.util.List;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.graphics.canvas.component.simple.TitleValueMenuComponent;
import de.mossgrabers.framework.mode.AbstractNoteParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.parameter.NoteParameter;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FourKnobProvider;
import de.mossgrabers.framework.scale.Scales;


/**
 * The mode for editing notes.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneNoteEditMode extends AbstractNoteParameterMode<OxiOneControlSurface, OxiOneConfiguration, IItem>
{
    protected static final List<ContinuousID> KNOB_IDS      = ContinuousID.createSequentialList (ContinuousID.KNOB1, 4);

    private static final String []            MENU          =
    {
        "Gain",
        "Pan",
        "Dura",
        "Vel"
    };

    private static final String []            SHIFTED_MENU  =
    {
        "Press",
        "Timb",
        "Chnc",
        "VelS"
    };

    private int                               selectedIndex = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public OxiOneNoteEditMode (final OxiOneControlSurface surface, final IModel model)
    {
        super ("Note Edit", surface, model, false, null, KNOB_IDS);

        final IValueChanger valueChanger = model.getValueChanger ();

        this.setParameterProvider (new FourKnobProvider<> (surface, new FixedParameterProvider (
                // Gain
                new NoteParameter (NoteAttribute.GAIN, null, model, this.noteEditor, valueChanger),
                // Panorama
                new NoteParameter (NoteAttribute.PANORAMA, null, model, this.noteEditor, valueChanger),
                // Duration
                new NoteParameter (NoteAttribute.DURATION, null, model, this.noteEditor, valueChanger),
                // Velocity
                new NoteParameter (NoteAttribute.VELOCITY, null, model, this.noteEditor, valueChanger),
                // Pressure
                new NoteParameter (NoteAttribute.PRESSURE, null, model, this.noteEditor, valueChanger),
                // Timbre
                new NoteParameter (NoteAttribute.TIMBRE, null, model, this.noteEditor, valueChanger),
                // Chance
                new NoteParameter (NoteAttribute.CHANCE, null, model, this.noteEditor, valueChanger),
                // Velocity Spread
                new NoteParameter (NoteAttribute.VELOCITY_SPREAD, null, model, this.noteEditor, valueChanger)), ButtonID.SHIFT));

    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.updateMode ();

        final String desc;
        final List<NotePosition> notes = this.noteEditor.getNotes ();
        if (notes.isEmpty ())
            desc = "Select a note";
        else if (notes.size () > 1)
            desc = notes.size () + " notes selected";
        else
        {
            final NotePosition notePosition = notes.get (0);
            desc = "Step: " + (notePosition.getStep () + 1) + " - " + Scales.formatNoteAndOctave (notePosition.getNote (), -3);
        }

        String paramLine = "";
        int value = -1;

        final IParameter parameter = this.getParameterProvider ().get (this.selectedIndex % 4);
        if (parameter != null)
        {
            value = parameter.getValue ();
            paramLine = parameter.getName () + ": " + parameter.getDisplayedValue ();
        }

        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();
        display.addElement (new TitleValueMenuComponent (desc, paramLine, this.surface.isShiftPressed () ? SHIFTED_MENU : MENU, value, 0, 0, false));
        display.send ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.selectedIndex = this.surface.isPressed (ButtonID.SHIFT) ? index + 4 : index;
    }


    /**
     * Ensure that the correct mode is still active in case the modifier key was toggled.
     */
    private void updateMode ()
    {
        if (this.surface.isShiftPressed ())
        {
            if (this.selectedIndex < 4)
                this.selectedIndex += 4;
        }
        else if (this.selectedIndex >= 4)
            this.selectedIndex -= 4;
    }
}
