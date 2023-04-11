// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.mode;

import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.mode.INoteMode;
import de.mossgrabers.framework.mode.NoteEditor;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.NoteAttribute;
import de.mossgrabers.framework.parameter.NoteParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


/**
 * The edit note mode.
 *
 * @author Jürgen Moßgraber
 */
public class EditNoteMode extends BaseMode implements INoteMode
{
    private enum Page
    {
        NOTE,
        EXPRESSIONS,
        REPEAT
    }


    private final IHost                          host;
    private final NoteEditor                     noteEditor;

    // Maschine 8 knob editing
    private final Map<Page, IParameterProvider>  pageParamProviders  = new EnumMap<> (Page.class);
    private Page                                 page                = Page.NOTE;

    // Maschine Mikro 1 knob editing
    private final Map<NoteAttribute, IParameter> notePartameters     = new EnumMap<> (NoteAttribute.class);
    private NoteAttribute                        activeNoteAttribute = NoteAttribute.VELOCITY;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public EditNoteMode (final MaschineControlSurface surface, final IModel model)
    {
        super ("Edit note", surface, model, surface.getMaschine ().hasMCUDisplay () ? DEFAULT_KNOB_IDS : null);

        this.host = this.model.getHost ();
        this.noteEditor = new NoteEditor ();
        this.selectedParam = 2;

        final IValueChanger valueChanger = model.getValueChanger ();
        final IDisplay display = surface.getMaschine ().hasMCUDisplay () ? null : surface.getDisplay ();

        final NoteParameter durationParameter = new NoteParameter (NoteAttribute.DURATION, display, model, this, valueChanger);
        final NoteParameter velocityParameter = new NoteParameter (NoteAttribute.VELOCITY, display, model, this, valueChanger);
        final NoteParameter gainParameter = new NoteParameter (NoteAttribute.GAIN, display, model, this, valueChanger);
        final NoteParameter panParameter = new NoteParameter (NoteAttribute.PANORAMA, display, model, this, valueChanger);
        final NoteParameter transposeParameter = new NoteParameter (NoteAttribute.TRANSPOSE, display, model, this, valueChanger);
        final NoteParameter pressureParameter = new NoteParameter (NoteAttribute.PRESSURE, display, model, this, valueChanger);

        this.notePartameters.put (NoteAttribute.DURATION, durationParameter);
        this.notePartameters.put (NoteAttribute.VELOCITY, velocityParameter);
        this.notePartameters.put (NoteAttribute.GAIN, gainParameter);
        this.notePartameters.put (NoteAttribute.PANORAMA, panParameter);
        this.notePartameters.put (NoteAttribute.TRANSPOSE, transposeParameter);
        this.notePartameters.put (NoteAttribute.PRESSURE, pressureParameter);

        this.pageParamProviders.put (Page.NOTE, new FixedParameterProvider (
                // -
                EmptyParameter.INSTANCE,
                // Duration
                durationParameter,
                // Duration
                durationParameter,
                // Velocity
                velocityParameter,
                // Velocity Spread
                new NoteParameter (NoteAttribute.VELOCITY_SPREAD, null, model, this, valueChanger),
                // Release Velocity
                new NoteParameter (NoteAttribute.RELEASE_VELOCITY, null, model, this, valueChanger),
                // Chance
                new NoteParameter (NoteAttribute.CHANCE, null, model, this, valueChanger),
                // Occurrence
                new NoteParameter (NoteAttribute.OCCURRENCE, null, model, this, valueChanger)));

        this.pageParamProviders.put (Page.EXPRESSIONS, new FixedParameterProvider (
                // -
                EmptyParameter.INSTANCE,
                // Duration
                durationParameter,
                // Duration
                durationParameter,
                // Gain
                gainParameter,
                // Panorama
                panParameter,
                // Transpose
                transposeParameter,
                // Timbre
                new NoteParameter (NoteAttribute.TIMBRE, null, model, this, valueChanger),
                // Pressure
                pressureParameter));

        this.pageParamProviders.put (Page.REPEAT, new FixedParameterProvider (
                // -
                EmptyParameter.INSTANCE,
                // Duration
                durationParameter,
                // Duration
                durationParameter,
                // Velocity
                velocityParameter,
                // Repeat
                new NoteParameter (NoteAttribute.REPEAT, null, model, this, valueChanger),
                // Repeat Curve
                new NoteParameter (NoteAttribute.REPEAT_CURVE, null, model, this, valueChanger),
                // Repeat Velocity Curve
                new NoteParameter (NoteAttribute.REPEAT_VELOCITY_CURVE, null, model, this, valueChanger),
                // Repeat Velocity End
                new NoteParameter (NoteAttribute.REPEAT_VELOCITY_END, null, model, this, valueChanger)));

        this.rebind ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (this.controls.isEmpty ())
        {
            this.notePartameters.get (this.activeNoteAttribute).changeValue (value);
            return;
        }

        final int idx = index < 0 ? this.selectedParam : index;
        this.defaultParameterProvider.get (idx).changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final List<NotePosition> notes = this.noteEditor.getNotes ();
        if (notes.isEmpty ())
            return;

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            final int idx = index < 0 ? this.selectedParam : index;
            this.defaultParameterProvider.get (idx).resetValue ();
            return;
        }

        final INoteClip clip = this.getClip ();
        if (isTouched)
            clip.startEdit (notes);
        else
            clip.stopEdit ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        final List<NotePosition> notes = this.noteEditor.getNotes ();
        if (notes.isEmpty ())
        {
            d.setBlock (0, 0, "Please select");
            d.setBlock (0, 1, "a note...");
            d.allDone ();
            return;
        }

        final NotePosition notePosition = notes.get (0);

        final IStepInfo stepInfo = this.getClip ().getStep (notePosition);
        d.setCell (0, 0, "Note");

        if (notes.size () > 1)
            d.setCell (1, 0, "*:" + notes.size ());
        else
            d.setCell (1, 0, stepInfo == null ? "-" : Integer.toString (notePosition.getStep () + 1) + ":" + Scales.formatNoteAndOctave (notePosition.getNote (), -3));

        d.setCell (0, 1, this.mark ("Length", 2));
        if (stepInfo == null)
            d.setCell (1, 2, "-");
        else
        {
            final String [] formatLength = this.formatLength (stepInfo.getDuration ()).split (":");
            d.setCell (1, 1, formatLength[0]);
            d.setCell (1, 2, ":" + formatLength[1]);
        }

        switch (this.page)
        {
            default:
            case NOTE:
                d.setCell (0, 3, this.mark ("Velocity", 3)).setCell (1, 3, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getVelocity ()));

                if (this.host.supports (NoteAttribute.VELOCITY_SPREAD))
                    d.setCell (0, 4, this.mark ("Spread", 4)).setCell (1, 4, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getVelocitySpread ()));

                if (this.host.supports (NoteAttribute.RELEASE_VELOCITY))
                    d.setCell (0, 5, this.mark ("R-Vel", 5)).setCell (1, 5, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getReleaseVelocity ()));

                if (this.host.supports (NoteAttribute.CHANCE))
                    d.setCell (0, 6, this.mark ("Chance", 6)).setCell (1, 6, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getChance ()));

                if (this.host.supports (NoteAttribute.OCCURRENCE))
                    d.setCell (0, 7, this.mark ("Occurnce", 7)).setCell (1, 7, stepInfo == null ? "-" : StringUtils.optimizeName (stepInfo.getOccurrence ().getName (), 8));

                break;

            case EXPRESSIONS:
                d.setCell (0, 3, this.mark ("Gain", 3)).setCell (1, 3, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getGain ()));
                d.setCell (0, 4, this.mark ("Pan", 4)).setCell (1, 4, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getPan ()));
                d.setCell (0, 5, this.mark ("Pitch", 5)).setCell (1, 5, stepInfo == null ? "-" : String.format ("%.1f", Double.valueOf (stepInfo.getTranspose ())));
                d.setCell (0, 6, this.mark ("Timbre", 6)).setCell (1, 6, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getTimbre ()));
                d.setCell (0, 7, this.mark ("Pressure", 7)).setCell (1, 7, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getPressure ()));
                break;

            case REPEAT:
                d.setCell (0, 3, this.mark ("Velocity", 3)).setCell (1, 3, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getVelocity ()));
                d.setCell (0, 4, this.mark ("Count", 4)).setCell (1, 4, stepInfo == null ? "-" : stepInfo.getFormattedRepeatCount ());
                d.setCell (0, 5, this.mark ("Curve", 5)).setCell (1, 5, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getRepeatCurve ()));
                d.setCell (0, 6, this.mark ("V-Curve", 6)).setCell (1, 6, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getRepeatVelocityCurve ()));
                d.setCell (0, 7, this.mark ("V-End", 7)).setCell (1, 7, stepInfo == null ? "-" : StringUtils.formatPercentage (stepInfo.getRepeatVelocityEnd ()));
                break;
        }

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        if (this.hasNextItem ())
            this.selectedParam++;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        if (this.hasPreviousItem ())
            this.selectedParam--;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        return this.selectedParam > 2;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        return this.selectedParam < 7;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        if (this.host.supports (NoteAttribute.TIMBRE))
        {
            if (this.page == Page.EXPRESSIONS)
                this.page = Page.NOTE;
            else if (this.page == Page.REPEAT)
                this.page = Page.EXPRESSIONS;
            this.rebind ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        if (this.host.supports (NoteAttribute.TIMBRE))
        {
            if (this.page == Page.NOTE)
                this.page = Page.EXPRESSIONS;
            else if (this.page == Page.EXPRESSIONS)
                this.page = Page.REPEAT;
            this.rebind ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        return this.host.supports (NoteAttribute.TIMBRE) && this.page.ordinal () > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        return this.host.supports (NoteAttribute.TIMBRE) && this.page.ordinal () < Page.REPEAT.ordinal ();
    }


    /**
     * Format the duration of the current note.
     *
     * @param duration The note duration
     * @return The formatted value
     */
    private String formatLength (final double duration)
    {
        return StringUtils.formatMeasures (this.model.getTransport ().getQuartersPerMeasure (), duration, 0, true);
    }


    /**
     * Get the active note parameter to edit with the main knob.
     *
     * @return The parameter
     */
    public NoteAttribute getActiveParameter ()
    {
        return this.activeNoteAttribute;
    }


    /**
     * Set the active note parameter to edit with the main knob.
     *
     * @param noteAttribute The note parameter
     */
    public void selectActiveParameter (final NoteAttribute noteAttribute)
    {
        this.activeNoteAttribute = noteAttribute;
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


    private void rebind ()
    {
        if (this.controls.isEmpty ())
            return;

        this.setParameterProvider (this.pageParamProviders.get (this.page));
        this.bindControls ();
    }
}