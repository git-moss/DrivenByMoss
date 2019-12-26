// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Abstract implementation for a note sequencer.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractNoteSequencerView<S extends IControlSurface<C>, C extends Configuration> extends AbstractSequencerView<S, C> implements TransposeView
{
    protected int   numDisplayRows = 8;
    protected int   numDisplayCols;
    protected int   startKey       = 36;
    protected int   loopPadPressed = -1;
    protected int   offsetY;

    private boolean useTrackColor;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public AbstractNoteSequencerView (final String name, final S surface, final IModel model, final boolean useTrackColor)
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
    public AbstractNoteSequencerView (final String name, final S surface, final IModel model, final int numDisplayCols, final boolean useTrackColor)
    {
        super (name, surface, model, 128, numDisplayCols, 7);

        this.useTrackColor = useTrackColor;
        this.numDisplayCols = numDisplayCols;
        this.offsetY = this.startKey;
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
        if (!this.isActive ())
            return;

        final int index = note - 36;
        final int x = index % 8;
        final int y = index / 8;

        final INoteClip clip = this.getClip ();
        if (y < this.numSequencerRows)
        {
            if (velocity != 0)
                clip.toggleStep (this.surface.getConfiguration ().getMidiEditChannel (), x, this.keyManager.map (y), this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
            return;
        }

        // Clip length/loop area
        final int pad = x;

        // Button pressed?
        if (velocity > 0)
        {
            // Not yet a button pressed, store it
            if (this.loopPadPressed == -1)
                this.loopPadPressed = pad;
            return;
        }

        if (this.loopPadPressed == -1)
            return;

        if (pad == this.loopPadPressed && pad != clip.getEditPage ())
        {
            // Only single pad pressed -> page selection
            clip.scrollToPage (pad);
        }
        else
        {
            // Set a new loop between the 2 selected pads
            final int start = this.loopPadPressed < pad ? this.loopPadPressed : pad;
            final int end = (this.loopPadPressed < pad ? pad : this.loopPadPressed) + 1;
            final int lengthOfOnePad = this.getLengthOfOnePage (this.numDisplayCols);
            final double newStart = (double) start * lengthOfOnePad;
            clip.setLoopStart (newStart);
            clip.setLoopLength ((end - start) * lengthOfOnePad);
            clip.setPlayRange (newStart, (double) end * lengthOfOnePad);
        }

        this.loopPadPressed = -1;
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid gridPad = this.surface.getPadGrid ();
        if (!this.isActive ())
        {
            gridPad.turnOff ();
            return;
        }

        final ITrack selectedTrack = this.model.getSelectedTrack ();

        // Steps with notes
        final INoteClip clip = this.getClip ();
        final int step = clip.getCurrentStep ();
        final int hiStep = this.isInXRange (step) ? step % this.numDisplayCols : -1;
        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        for (int x = 0; x < this.numDisplayCols; x++)
        {
            for (int y = 0; y < this.numSequencerRows; y++)
            {
                // 0: not set, 1: note continues playing, 2: start of note
                final int map = this.keyManager.map (y);
                final int isSet = map < 0 ? 0 : clip.getStep (editMidiChannel, x, map).getState ();
                gridPad.lightEx (x, this.numDisplayRows - 1 - y, this.getStepColor (isSet, x == hiStep, y, selectedTrack));
            }
        }

        if (this.numDisplayRows - this.numSequencerRows <= 0)
            return;

        final int lengthOfOnePad = this.getLengthOfOnePage (this.numDisplayCols);
        final double loopStart = clip.getLoopStart ();
        final int loopStartPad = (int) Math.ceil (loopStart / lengthOfOnePad);
        final int loopEndPad = (int) Math.ceil ((loopStart + clip.getLoopLength ()) / lengthOfOnePad);
        final int currentPage = step / this.numDisplayCols;
        for (int pad = 0; pad < 8; pad++)
            gridPad.lightEx (pad, 0, this.getPageColor (loopStartPad, loopEndPad, currentPage, clip.getEditPage (), pad));
    }


    /**
     * Get the color for a step.
     *
     * @param isSet The step has content
     * @param hilite The step should be highlighted
     * @param note The note of the step
     * @param track A track from which to use the color
     * @return The color
     */
    protected String getStepColor (final int isSet, final boolean hilite, final int note, final ITrack track)
    {
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
                if (hilite)
                    return COLOR_STEP_HILITE_NO_CONTENT;
                return this.getPadColor (note, this.useTrackColor ? track : null);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (!this.isActive ())
            return;

        if (event == ButtonEvent.DOWN)
            this.updateOctave (Math.max (0, this.offsetY - this.getScrollOffset ()));
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (!this.isActive ())
            return;

        if (event != ButtonEvent.DOWN)
            return;
        final int offset = this.getScrollOffset ();
        if (this.offsetY + offset < this.getClip ().getNumRows ())
            this.updateOctave (this.offsetY + offset);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveUpButtonOn ()
    {
        return this.isActive ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveDownButtonOn ()
    {
        return this.isActive ();
    }


    protected void updateScale ()
    {
        this.delayedUpdateNoteMapping (this.model.canSelectedTrackHoldNotes () ? this.scales.getSequencerMatrix (8, this.offsetY) : EMPTY_TABLE);
    }


    protected void updateOctave (final int value)
    {
        this.offsetY = value;
        this.updateScale ();
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify (Scales.getSequencerRangeText (this.keyManager.map (0), this.keyManager.map (this.numSequencerRows - 1))), 10);
    }
}