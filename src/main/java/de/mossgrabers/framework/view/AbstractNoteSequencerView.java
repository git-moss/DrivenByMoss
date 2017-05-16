// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.scale.Scales;


/**
 * Abstract implementation for a note sequencer.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractNoteSequencerView<S extends ControlSurface<C>, C extends Configuration> extends AbstractSequencerView<S, C> implements TransposeView
{
    protected int   numDisplayRows = 8;
    protected int   numDisplayCols;
    protected int   startKey       = 36;
    protected int   loopPadPressed = -1;

    private boolean useTrackColor;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public AbstractNoteSequencerView (final String name, final S surface, final Model model, final boolean useTrackColor)
    {
        this (name, surface, model, 8, useTrackColor);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param numDisplayCols The number of grid columns
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public AbstractNoteSequencerView (final String name, final S surface, final Model model, final int numDisplayCols, final boolean useTrackColor)
    {
        super (name, surface, model, 128, numDisplayCols, 7);

        this.useTrackColor = useTrackColor;
        this.numDisplayCols = numDisplayCols;
        this.offsetY = this.startKey;

        this.getClip ().scrollTo (0, this.startKey);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.updateScale ();
        super.onActivate ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        super.updateNoteMapping ();
        this.updateScale ();
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;
        final int index = note - 36;
        final int x = index % 8;
        final int y = index / 8;

        if (y < this.numSequencerRows)
        {
            if (velocity != 0)
                this.getClip ().toggleStep (x, this.noteMap[y], this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
            return;
        }

        // Clip length/loop area
        final int pad = x;
        if (velocity > 0) // Button pressed
        {
            if (this.loopPadPressed == -1) // Not yet a button pressed, store it
                this.loopPadPressed = pad;
        }
        else if (this.loopPadPressed != -1)
        {
            final int start = this.loopPadPressed < pad ? this.loopPadPressed : pad;
            final int end = (this.loopPadPressed < pad ? pad : this.loopPadPressed) + 1;
            final int quartersPerPad = this.model.getQuartersPerMeasure () / 2;

            // Set a new loop between the 2 selected pads
            final int newStart = start * quartersPerPad;
            this.getClip ().setLoopStart (newStart);
            this.getClip ().setLoopLength ((end - start) * quartersPerPad);
            this.getClip ().setPlayRange (newStart, (double) end * quartersPerPad);

            this.loopPadPressed = -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData selectedTrack = tb.getSelectedTrack ();

        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        final int step = this.getClip ().getCurrentStep ();
        final int hiStep = this.isInXRange (step) ? step % this.numDisplayCols : -1;
        final PadGrid gridPad = this.surface.getPadGrid ();
        for (int x = 0; x < this.numDisplayCols; x++)
        {
            for (int y = 0; y < this.numSequencerRows; y++)
            {
                // 0: not set, 1: note continues playing, 2: start of note
                final int isSet = this.getClip ().getStep (x, this.noteMap[y]);
                gridPad.lightEx (x, this.numDisplayRows - 1 - y, this.getStepColor (isKeyboardEnabled, isSet, x == hiStep, y, selectedTrack));
            }
        }

        if (this.numDisplayRows - this.numSequencerRows <= 0)
            return;

        // Clip length/loop area
        final int quartersPerPad = this.model.getQuartersPerMeasure () / 2;
        final int stepsPerMeasure = (int) Math.round (quartersPerPad / AbstractSequencerView.RESOLUTIONS[this.selectedIndex]);
        final int currentMeasure = step / stepsPerMeasure;
        final int maxQuarters = quartersPerPad * 8;
        final double start = this.getClip ().getLoopStart ();
        final double loopStartPad = Math.floor (Math.max (0, start) / quartersPerPad);
        final double loopEndPad = Math.ceil (Math.min (maxQuarters, start + this.getClip ().getLoopLength ()) / quartersPerPad);
        for (int pad = 0; pad < 8; pad++)
        {
            if (isKeyboardEnabled)
                gridPad.lightEx (pad, 0, pad >= loopStartPad && pad < loopEndPad ? pad == currentMeasure ? AbstractSequencerView.COLOR_ACTIVE_MEASURE : AbstractSequencerView.COLOR_MEASURE : AbstractSequencerView.COLOR_NO_CONTENT);
            else
                gridPad.lightEx (pad, 0, AbstractSequencerView.COLOR_NO_CONTENT);
        }
    }


    /**
     * Get the color for a step.
     *
     * @param isKeyboardEnabled Can we play?
     * @param isSet The step has content
     * @param hilite The step should be highlighted
     * @param note The note of the step
     * @param track A track from which to use the color
     * @return The color
     */
    protected String getStepColor (final boolean isKeyboardEnabled, final int isSet, final boolean hilite, final int note, final TrackData track)
    {
        if (!isKeyboardEnabled)
            return COLOR_NO_CONTENT;

        switch (isSet)
        {
            // Note continues
            case 1:
                return hilite ? COLOR_STEP_HILITE_CONTENT : COLOR_CONTENT_CONT;
            // Note starts
            case 2:
                return hilite ? COLOR_STEP_HILITE_CONTENT : COLOR_CONTENT;
            // Empty
            default:
                return hilite ? COLOR_STEP_HILITE_NO_CONTENT : this.getColor (note, this.useTrackColor ? track : null);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.updateOctave (Math.max (0, this.offsetY - this.getScrollOffset ()));
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final int offset = this.getScrollOffset ();
        if (this.offsetY + offset < this.getClip ().getRowSize ())
            this.updateOctave (this.offsetY + offset);
    }


    protected void updateScale ()
    {
        this.noteMap = this.model.canSelectedTrackHoldNotes () ? this.scales.getSequencerMatrix (8, this.offsetY) : Scales.getEmptyMatrix ();
    }


    protected void updateOctave (final int value)
    {
        this.offsetY = value;
        this.updateScale ();
        this.surface.getDisplay ().notify (Scales.getSequencerRangeText (this.noteMap[0], this.noteMap[this.numSequencerRows - 1]), true, true);
    }
}