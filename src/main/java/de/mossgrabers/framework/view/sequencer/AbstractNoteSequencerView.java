// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view.sequencer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.TransposeView;

import java.util.List;


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
    private static final int OCTAVE         = 12;

    protected int            numDisplayRows = 8;
    protected int            numDisplayCols;
    protected int            startKey       = 36;
    protected int            loopPadPressed = -1;
    protected int            offsetY;
    protected IStepInfo      copyNote;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param useDawColors True to use the color of the current track for coloring the octaves
     */
    protected AbstractNoteSequencerView (final String name, final S surface, final IModel model, final boolean useDawColors)
    {
        this (name, surface, model, 8, useDawColors);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param numDisplayCols The number of grid columns
     * @param useDawColors True to use the color of the current track for coloring the octaves
     */
    protected AbstractNoteSequencerView (final String name, final S surface, final IModel model, final int numDisplayCols, final boolean useDawColors)
    {
        this (name, surface, model, numDisplayCols, 7, useDawColors);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param numDisplayCols The number of grid columns
     * @param numSequencerRows The number of sequencer rows
     * @param useDawColors True to use the color of the current track for coloring the octaves
     */
    protected AbstractNoteSequencerView (final String name, final S surface, final IModel model, final int numDisplayCols, final int numSequencerRows, final boolean useDawColors)
    {
        super (name, surface, model, 128, numDisplayCols, numSequencerRows, useDawColors);

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
        final int x = index % this.numDisplayCols;
        final int y = index / this.numDisplayCols;

        if (y < this.numSequencerRows)
        {
            this.handleSequencerArea (index, x, y, velocity);
            return;
        }

        // Clip length/loop area
        this.handleLoopArea (x, velocity);
    }


    /**
     * Handle button presses in the sequencer area of the note sequencer.
     *
     * @param index The index of the pad
     * @param x The x position of the pad in the sequencer grid
     * @param y The y position of the pad in the sequencer grid
     * @param velocity The velocity
     */
    protected void handleSequencerArea (final int index, final int x, final int y, final int velocity)
    {
        // Toggle the note on up, so we can intercept the long presses
        if (velocity != 0)
            return;

        final INoteClip clip = this.getClip ();
        final int mappedY = this.keyManager.map (y);
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), x, mappedY);
        final int vel = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).getPressedVelocity ();

        if (this.handleSequencerAreaButtonCombinations (clip, notePosition, y, vel))
            return;

        if (mappedY >= 0)
            clip.toggleStep (notePosition, vel);
    }


    /**
     * Handle button combinations on the note area of the sequencer.
     *
     * @param clip The sequenced MIDI clip
     * @param notePosition The position of the note
     * @param row The row in the current page in the clip
     * @param velocity The velocity
     * @return True if handled
     */
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int row, final int velocity)
    {
        // Handle note duplicate function
        if (this.isButtonCombination (ButtonID.DUPLICATE))
        {
            final IStepInfo noteStep = clip.getStep (notePosition);
            if (noteStep.getState () == StepState.START)
                this.copyNote = noteStep;
            else if (this.copyNote != null)
                clip.setStep (notePosition, this.copyNote);
            return true;
        }

        if (this.isButtonCombination (ButtonID.MUTE))
        {
            final IStepInfo stepInfo = clip.getStep (notePosition);
            final StepState isSet = stepInfo.getState ();
            if (isSet == StepState.START)
                clip.updateStepMuteState (notePosition, !stepInfo.isMuted ());
            return true;
        }

        // Change length of a note or create a new one with a length
        final int offset = row * clip.getNumSteps ();
        final NotePosition np = new NotePosition (notePosition);
        final int step = np.getStep ();
        final int note = np.getNote ();
        for (int s = 0; s < step; s++)
        {
            final IHwButton button = this.surface.getButton (ButtonID.get (ButtonID.PAD1, offset + s));
            if (button.isLongPressed ())
            {
                np.setStep (s);
                button.setConsumed ();
                final int length = step - s + 1;
                final double duration = length * Resolution.getValueAt (this.getResolutionIndex ());
                final StepState state = note < 0 ? StepState.OFF : clip.getStep (np).getState ();
                if (state == StepState.START)
                    clip.updateStepDuration (np, duration);
                else
                    clip.setStep (np, velocity, duration);
                return true;
            }
        }

        return false;
    }


    /**
     * Handle button presses in the loop area of the note sequencer.
     *
     * @param pad The pressed pad
     * @param velocity The velocity
     */
    protected void handleLoopArea (final int pad, final int velocity)
    {
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

        final INoteClip clip = this.getClip ();
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

        // Steps with notes
        final INoteClip clip = this.getClip ();
        final int step = clip.getCurrentStep ();
        final int hiStep = this.isInXRange (step) ? step % this.numDisplayCols : -1;
        final List<NotePosition> editNotes = this.getEditNotes ();
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), 0, 0);
        for (int x = 0; x < this.numDisplayCols; x++)
        {
            notePosition.setStep (x);
            for (int y = 0; y < this.numSequencerRows; y++)
            {
                final int map = this.keyManager.map (y);
                notePosition.setNote (map);
                final IStepInfo stepInfo = map < 0 ? null : clip.getStep (notePosition);
                gridPad.lightEx (x, this.numDisplayRows - 1 - y, this.getStepColor (stepInfo, x == hiStep, notePosition.getChannel (), x, y, map, editNotes));
            }
        }

        if (this.numDisplayRows - this.numSequencerRows <= 0)
            return;

        // Clip Pages on the top
        final int lengthOfOnePad = this.getLengthOfOnePage (this.numDisplayCols);
        final double loopStart = clip.getLoopStart ();
        final int loopStartPad = (int) Math.ceil (loopStart / lengthOfOnePad);
        final int loopEndPad = (int) Math.ceil ((loopStart + clip.getLoopLength ()) / lengthOfOnePad);
        final int currentPage = step / this.numDisplayCols;
        for (int pad = 0; pad < this.numDisplayCols; pad++)
            gridPad.lightEx (pad, 0, this.getPageColor (loopStartPad, loopEndPad, currentPage, clip.getEditPage (), pad));
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (!this.isActive () || event != ButtonEvent.DOWN)
            return;

        if (this.surface.isShiftPressed ())
        {
            this.getClip ().transpose (-1);
            return;
        }

        if (this.surface.isSelectPressed ())
        {
            this.getClip ().transpose (-12);
            return;
        }

        this.updateOctave (Math.max (0, this.offsetY - this.getScrollOffset ()));
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (!this.isActive () || event != ButtonEvent.DOWN)
            return;

        if (this.surface.isShiftPressed ())
        {
            this.getClip ().transpose (1);
            return;
        }

        if (this.surface.isSelectPressed ())
        {
            this.getClip ().transpose (12);
            return;
        }

        final int offset = this.getScrollOffset ();
        if (this.offsetY + offset < this.getClip ().getNumRows ())
            this.updateOctave (this.offsetY + offset);
    }


    /**
     * Calculates how many seminotes are between the first and last 'pad'.
     *
     * @return The number of seminotes
     */
    protected int getScrollOffset ()
    {
        // In chromatic mode all seminotes are present
        if (this.scales.isChromatic ())
            return this.numSequencerRows;

        final Scale scale = this.scales.getScale ();
        int lower = scale.getIndexInScale (this.offsetY);
        if (lower < 0)
            lower = 0;
        final int upper = lower + this.numSequencerRows;
        final int [] intervals = scale.getIntervals ();

        final int lowerNote = intervals[lower];
        final int upperNote = upper / intervals.length * OCTAVE + intervals[upper % intervals.length];

        return upperNote - lowerNote;
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
        this.delayedUpdateNoteMapping (this.model.canSelectedTrackHoldNotes () ? this.scales.getSequencerMatrix (this.numSequencerRows + 1, this.offsetY) : EMPTY_TABLE);
    }


    protected void updateOctave (final int value)
    {
        this.offsetY = value;
        this.updateScale ();
        this.clearEditNotes ();
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify (Scales.getSequencerRangeText (this.keyManager.map (0), this.keyManager.map (this.numSequencerRows - 1))), 10);
    }
}