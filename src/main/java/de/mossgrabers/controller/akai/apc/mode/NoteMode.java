// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
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
import de.mossgrabers.framework.mode.INoteEditor;
import de.mossgrabers.framework.mode.INoteEditorMode;
import de.mossgrabers.framework.mode.NoteEditor;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.parameter.NoteParameter;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * Note edit knob mode.
 *
 * @author Jürgen Moßgraber
 */
public class NoteMode extends BaseMode<IItem> implements INoteEditorMode
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
                new NoteParameter (NoteAttribute.DURATION, display, model, this.noteEditor, valueChanger),
                // Velocity
                new NoteParameter (NoteAttribute.VELOCITY, display, model, this.noteEditor, valueChanger),
                // Velocity Spread
                new NoteParameter (NoteAttribute.VELOCITY_SPREAD, display, model, this.noteEditor, valueChanger),
                // Gain
                new NoteParameter (NoteAttribute.GAIN, display, model, this.noteEditor, valueChanger),
                // Panorama
                new NoteParameter (NoteAttribute.PANORAMA, display, model, this.noteEditor, valueChanger),
                // Chance
                new NoteParameter (NoteAttribute.CHANCE, display, model, this.noteEditor, valueChanger),
                // Timbre
                new NoteParameter (NoteAttribute.TIMBRE, display, model, this.noteEditor, valueChanger),
                // Pressure
                new NoteParameter (NoteAttribute.PRESSURE, display, model, this.noteEditor, valueChanger));
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
    public INoteEditor getNoteEditor ()
    {
        return this.noteEditor;
    }
}