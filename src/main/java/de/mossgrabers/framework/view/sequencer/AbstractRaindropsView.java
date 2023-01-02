// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view.sequencer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.TransposeView;


/**
 * Abstract implementation for a raindrops sequencer.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractRaindropsView<S extends IControlSurface<C>, C extends Configuration> extends AbstractSequencerView<S, C> implements TransposeView
{
    protected static final int NUM_DISPLAY_COLS = 8;
    protected static final int NUM_OCTAVE       = 12;
    protected static final int START_KEY        = 36;

    // 32 = biggest number of measures in Fixed Length
    protected static final int MAX_STEPS        = (int) Math.floor (32 * 4 / Resolution.RES_1_32T.getValue ());

    protected int              numDisplayRows   = 8;
    protected boolean          ongoingResolutionChange;
    protected int              offsetY;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param useDawColors True to use the color of the current track for coloring the octaves
     */
    protected AbstractRaindropsView (final String name, final S surface, final IModel model, final boolean useDawColors)
    {
        super (name, surface, model, 128, MAX_STEPS, useDawColors);

        this.offsetY = AbstractRaindropsView.START_KEY;

        this.ongoingResolutionChange = false;
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
        if (!this.isActive () || velocity == 0)
            return;

        final int index = note - 36;
        final int x = index % 8;
        final int y = index / 8;
        final int stepSize = y == 0 ? 1 : 2 * y;

        final INoteClip clip = this.getClip ();
        final double resolutionLength = Resolution.getValueAt (this.getResolutionIndex ());
        final int length = (int) Math.floor (clip.getLoopLength () / resolutionLength);
        final int distance = this.getNoteDistance (this.keyManager.map (x), length);
        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        clip.clearRow (editMidiChannel, this.keyManager.map (x));

        final NotePosition notePosition = new NotePosition (editMidiChannel, 0, 0);

        if (distance == -1 || distance != (y == 0 ? 1 : y * 2))
        {
            final int offset = clip.getCurrentStep () % stepSize;
            if (offset < 0)
                return;
            for (int i = offset; i < length; i += stepSize)
            {
                // Only support 32 measures at 1/32t
                if (i < MAX_STEPS)
                {
                    notePosition.setStep (i);
                    notePosition.setNote (this.keyManager.map (x));
                    clip.setStep (notePosition, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity, resolutionLength);
                }
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        if (!this.isActive ())
        {
            padGrid.turnOff ();
            return;
        }

        if (this.ongoingResolutionChange)
            return;

        final ITrack cursorTrack = this.useDawColors ? this.model.getCursorTrack () : null;

        final INoteClip clip = this.getClip ();
        final int length = (int) Math.floor (clip.getLoopLength () / Resolution.getValueAt (this.getResolutionIndex ()));
        final int step = clip.getCurrentStep ();
        for (int x = 0; x < AbstractRaindropsView.NUM_DISPLAY_COLS; x++)
        {
            final int mappedKey = this.keyManager.map (x);
            if (mappedKey == -1)
                continue;
            final int left = this.getNoteDistanceToTheLeft (mappedKey, step, length);
            final int right = this.getNoteDistanceToTheRight (mappedKey, step, length);
            final boolean isOn = left >= 0 && right >= 0;
            final int sum = left + right;
            final int distance = sum == 0 ? 0 : (sum + 1) / 2;

            for (int y = 0; y < this.numDisplayRows; y++)
            {
                String colorID = y == 0 ? this.getPadColor (x, cursorTrack) : AbstractSequencerView.COLOR_NO_CONTENT;
                if (isOn)
                {
                    if (y == distance)
                        colorID = AbstractSequencerView.COLOR_CONTENT;
                    if (left <= distance && y == left || left > distance && y == sum - left)
                        colorID = AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT;
                }
                padGrid.lightEx (x, this.numDisplayRows - 1 - y, colorID);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        this.ongoingResolutionChange = true;
        super.onButton (buttonID, event, velocity);
        this.ongoingResolutionChange = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || !this.isActive ())
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

        this.offsetY = Math.max (0, this.offsetY - AbstractRaindropsView.NUM_OCTAVE);
        this.updateScale ();
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify (Scales.getSequencerRangeText (this.keyManager.map (0), this.keyManager.map (7))), 10);
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || !this.isActive ())
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

        final int numRows = this.getClip ().getNumRows ();
        this.offsetY = Math.min (numRows - AbstractRaindropsView.NUM_OCTAVE, this.offsetY + AbstractRaindropsView.NUM_OCTAVE);
        this.updateScale ();
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify (Scales.getSequencerRangeText (this.keyManager.map (0), this.keyManager.map (7))), 10);
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


    protected int getNoteDistance (final int row, final int length)
    {
        if (row < 0)
            return -1;
        int step;
        final INoteClip clip = this.getClip ();
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), 0, row);
        for (step = 0; step < length; step++)
        {
            notePosition.setStep (step);
            if (clip.getStep (notePosition).getState () != StepState.OFF)
                break;
        }
        if (step >= length)
            return -1;
        for (int step2 = step + 1; step2 < length; step2++)
        {
            notePosition.setStep (step2);
            if (clip.getStep (notePosition).getState () != StepState.OFF)
                return step2 - step;
        }
        return -1;
    }


    protected int getNoteDistanceToTheRight (final int row, final int start, final int length)
    {
        if (row < 0 || start < 0 || start >= length)
            return -1;
        int step = start;
        int counter = 0;
        final INoteClip clip = this.getClip ();
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), 0, row);
        do
        {
            notePosition.setStep (step);
            if (clip.getStep (notePosition).getState () != StepState.OFF)
                return counter;
            step++;
            counter++;
            if (step >= length)
                step = 0;
        } while (step != start);
        return -1;
    }


    protected int getNoteDistanceToTheLeft (final int row, final int start, final int length)
    {
        if (row < 0 || start < 0 || start >= length)
            return -1;
        final int s = start == 0 ? length - 1 : start - 1;
        int step = s;
        int counter = 0;
        final INoteClip clip = this.getClip ();
        final NotePosition notePosition = new NotePosition (this.configuration.getMidiEditChannel (), 0, row);
        do
        {
            notePosition.setStep (step);
            if (clip.getStep (notePosition).getState () != StepState.OFF)
                return counter;
            step--;
            counter++;
            if (step < 0)
                step = length - 1;
        } while (step != s);
        return -1;
    }


    protected void updateScale ()
    {
        this.delayedUpdateNoteMapping (this.model.canSelectedTrackHoldNotes () ? this.scales.getSequencerMatrix (AbstractRaindropsView.NUM_DISPLAY_COLS, this.offsetY) : EMPTY_TABLE);
    }
}