// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.mode;

import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.mode.INoteMode;
import de.mossgrabers.framework.mode.NoteEditor;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.parameter.NoteParameter;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;

import java.util.List;


/**
 * Note edit knob mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteMode extends BaseMode<IItem> implements INoteMode
{
    private final NoteEditor             noteEditor;
    private final FixedParameterProvider fixedParameterProvider;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public NoteMode (final APCControlSurface surface, final IModel model)
    {
        super ("Note Edit", surface, model, APCControlSurface.LED_MODE_VOLUME, null);

        this.noteEditor = new NoteEditor ();

        final IValueChanger valueChanger = model.getValueChanger ();
        final IDisplay display = surface.getDisplay ();

        this.fixedParameterProvider = new FixedParameterProvider (
                // Duration
                new NoteParameter (NoteAttribute.DURATION, display, model, this, valueChanger),
                // Velocity
                new NoteParameter (NoteAttribute.VELOCITY, display, model, this, valueChanger),
                // Velocity Spread
                new NoteParameter (NoteAttribute.VELOCITY_SPREAD, display, model, this, valueChanger),
                // Gain
                new NoteParameter (NoteAttribute.GAIN, display, model, this, valueChanger),
                // Panorama
                new NoteParameter (NoteAttribute.PANORAMA, display, model, this, valueChanger),
                // Chance
                new NoteParameter (NoteAttribute.CHANCE, display, model, this, valueChanger),
                // Timbre
                new NoteParameter (NoteAttribute.TIMBRE, display, model, this, valueChanger),
                // Pressure
                new NoteParameter (NoteAttribute.PRESSURE, display, model, this, valueChanger));
    }


    /**
     * Set the values.
     *
     * @param clip The clip to edit
     * @param notePosition The position of the note to edit
     */
    public void setValues (final INoteClip clip, final NotePosition notePosition)
    {
        this.noteEditor.setNote (clip, notePosition);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int index, final int value)
    {
        // Workaround to still intercept changes
        this.fixedParameterProvider.get (index).setValue (value);

        // Note was modified, prevent deletion of note on button up
        final IView activeView = this.surface.getViewManager ().getActive ();
        if (activeView instanceof final AbstractSequencerView<?, ?> sequencerView)
            sequencerView.setNoteEdited ();
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        return this.fixedParameterProvider.get (index).getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public INoteClip getClip ()
    {
        return this.noteEditor.getClip ();
    }


    /** {@inheritDoc} */
    @Override
    public void clearNotes ()
    {
        this.noteEditor.clearNotes ();
    }


    /** {@inheritDoc} */
    @Override
    public void setNote (final INoteClip clip, final NotePosition notePosition)
    {
        this.noteEditor.setNote (clip, notePosition);
    }


    /** {@inheritDoc} */
    @Override
    public void addNote (final INoteClip clip, final NotePosition notePosition)
    {
        this.noteEditor.addNote (clip, notePosition);
    }


    /** {@inheritDoc} */
    @Override
    public List<NotePosition> getNotes ()
    {
        return this.noteEditor.getNotes ();
    }


    /** {@inheritDoc} */
    @Override
    public List<NotePosition> getNotePosition (final int parameterIndex)
    {
        return this.noteEditor.getNotePosition (parameterIndex);
    }
}