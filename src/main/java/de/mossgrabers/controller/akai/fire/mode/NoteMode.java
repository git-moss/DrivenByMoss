// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.mode;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.graphics.canvas.component.TitleValueComponent;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.mode.AbstractNoteParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.parameter.NoteParameter;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;

import java.util.List;


/**
 * Note edit knob mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteMode extends AbstractNoteParameterMode<FireControlSurface, FireConfiguration, IItem>
{
    protected static final List<ContinuousID> KNOB_IDS = ContinuousID.createSequentialList (ContinuousID.KNOB1, 4);
    static
    {
        KNOB_IDS.add (ContinuousID.VIEW_SELECTION);
    }

    private final FixedParameterProvider provider;
    private final FixedParameterProvider altProvider;
    private final NoteParameter          transposeParameter;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteMode (final FireControlSurface surface, final IModel model)
    {
        super ("Note Edit", surface, model, false, null, KNOB_IDS);

        final IValueChanger valueChanger = model.getValueChanger ();
        final IDisplay display = surface.getDisplay ();

        this.transposeParameter = new NoteParameter (NoteAttribute.TRANSPOSE, display, model, this, valueChanger);

        this.provider = new FixedParameterProvider (
                // Gain
                new NoteParameter (NoteAttribute.GAIN, null, model, this, valueChanger),
                // Panorama
                new NoteParameter (NoteAttribute.PANORAMA, null, model, this, valueChanger),
                // Duration
                new NoteParameter (NoteAttribute.DURATION, null, model, this, valueChanger),
                // Velocity
                new NoteParameter (NoteAttribute.VELOCITY, null, model, this, valueChanger),
                // Transpose
                this.transposeParameter);

        this.altProvider = new FixedParameterProvider (
                // Pressure
                new NoteParameter (NoteAttribute.PRESSURE, null, model, this, valueChanger),
                // Timbre
                new NoteParameter (NoteAttribute.TIMBRE, null, model, this, valueChanger),
                // Chance
                new NoteParameter (NoteAttribute.CHANCE, null, model, this, valueChanger),
                // Velocity Spread
                new NoteParameter (NoteAttribute.VELOCITY_SPREAD, null, model, this, valueChanger),
                // Repeat Count
                new NoteParameter (NoteAttribute.REPEAT, display, model, this, valueChanger));

        this.setParameterProvider (this.provider);
        this.setParameterProvider (ButtonID.ALT, this.altProvider);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final INoteClip clip = this.getClip ();

        if (clip == null || this.isKnobTouched (index) == isTouched)
            return;

        this.setTouchedKnob (index, isTouched);
        if (isTouched)
        {
            clip.startEdit (this.getNotes ());
            this.preventNoteDeletion ();
        }
        else
            clip.stopEdit ();
    }


    /**
     * Note was modified, prevent deletion of note on button up.
     */
    private void preventNoteDeletion ()
    {
        final IView activeView = this.surface.getViewManager ().getActive ();
        if (activeView instanceof final AbstractSequencerView<?, ?> sequencerView)
            sequencerView.setNoteEdited ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final String desc;
        final List<NotePosition> notes = this.getNotes ();
        if (notes.isEmpty ())
            desc = "Select a note";
        else if (notes.size () > 1)
            desc = "Step: * - " + notes.size ();
        else
        {
            final NotePosition notePosition = notes.get (0);
            desc = "Step: " + (notePosition.getStep () + 1) + " - " + Scales.formatNoteAndOctave (notePosition.getNote (), -3);
        }

        String paramLine = "";
        int value = -1;
        final int touchedKnob = this.getTouchedKnob ();
        if (touchedKnob != -1)
        {
            final IParameter parameter = (this.surface.isPressed (ButtonID.ALT) ? this.altProvider : this.provider).get (touchedKnob);
            if (parameter != null)
            {
                value = parameter.getValue ();
                paramLine = parameter.getName (5) + ": " + parameter.getDisplayedValue ();
            }
        }

        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();
        display.addElement (new TitleValueComponent (desc, paramLine, value, false));
        display.send ();
    }


    /**
     * Reset the transpose setting.
     */
    public void resetTranspose ()
    {
        this.transposeParameter.resetValue ();
    }
}