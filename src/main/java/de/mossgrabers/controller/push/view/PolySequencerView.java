// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.TransposeView;
import de.mossgrabers.framework.view.Views;

import java.util.HashMap;
import java.util.Map;


/**
 * The Poly Sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PolySequencerView extends AbstractSequencerView<PushControlSurface, PushConfiguration> implements TransposeView
{
    private static final int            GRID_COLUMNS        = 8;
    private static final int            NUM_LINES           = 8;
    private static final int            NUM_SEQUENCER_LINES = 4;

    private final int                   sequencerSteps;
    private final boolean               useTrackColor;
    private final Map<Integer, Integer> noteMemory          = new HashMap<> ();


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public PolySequencerView (final PushControlSurface surface, final IModel model, final boolean useTrackColor)
    {
        super (Views.VIEW_NAME_POLY_SEQUENCER, surface, model, 128, NUM_SEQUENCER_LINES * GRID_COLUMNS);

        this.sequencerSteps = NUM_SEQUENCER_LINES * GRID_COLUMNS;
        this.useTrackColor = useTrackColor;
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
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

        this.keyManager.clearPressedKeys ();
        this.scales.decOctave ();
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getRangeText ());
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
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

        this.keyManager.clearPressedKeys ();
        this.scales.incOctave ();
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getRangeText ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveUpButtonOn ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveDownButtonOn ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int key, final int velocity)
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        if (!isKeyboardEnabled)
            return;

        final int index = key - 36;
        final int x = index % 8;
        final int y = index / 8;

        if (y < GRID_COLUMNS - NUM_SEQUENCER_LINES)
        {
            // No pressed keys? Clear up the note memory for programming the sequencer...
            if (!this.keyManager.hasPressedKeys ())
                this.noteMemory.clear ();

            // Mark selected notes immediately for better performance
            final int note = this.keyManager.map (key);
            if (note != -1)
            {
                this.keyManager.setAllKeysPressed (note, velocity);
                if (velocity > 0)
                    this.noteMemory.put (Integer.valueOf (note), Integer.valueOf (velocity));
            }

            return;
        }

        // Only allow playing the notes if there is no clip
        if (!this.isActive ())
            return;

        // Toggle the note on up, so we can intercept the long presses (but not yet used)
        if (velocity > 0)
            return;

        final INoteClip clip = this.getClip ();
        final int col = GRID_COLUMNS * (NUM_LINES - 1 - y) + x;
        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        if (this.getStep (clip, col) > 0)
        {
            for (int row = 0; row < 128; row++)
            {
                if (clip.getStep (editMidiChannel, col, row).getState () > 0)
                    clip.clearStep (editMidiChannel, col, row);
            }
        }
        else
        {
            for (int row = 0; row < 128; row++)
            {
                final Integer k = Integer.valueOf (row);
                if (this.noteMemory.containsKey (k))
                {
                    final Integer vel = this.noteMemory.get (k);
                    clip.toggleStep (editMidiChannel, col, row, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : vel.intValue ());
                }
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        if (!isKeyboardEnabled)
        {
            padGrid.turnOff ();
            return;
        }

        final INoteClip clip = this.getClip ();
        final boolean isActive = this.isActive ();
        final int step = clip.getCurrentStep ();

        // Paint the sequencer steps
        final int hiStep = this.isInXRange (step) ? step % this.sequencerSteps : -1;
        for (int col = 0; col < this.sequencerSteps; col++)
        {
            final int isSet = this.getStep (clip, col);
            final boolean hilite = col == hiStep;
            final int x = col % GRID_COLUMNS;
            final int y = col / GRID_COLUMNS;
            padGrid.lightEx (x, y, isActive ? this.getStepColor (isSet, hilite) : AbstractSequencerView.COLOR_NO_CONTENT);
        }

        // Paint the play part
        final boolean isRecording = this.model.hasRecordingState ();
        final ITrack selectedTrack = this.model.getSelectedTrack ();
        final int startNote = this.scales.getStartNote ();
        for (int i = startNote; i < startNote + this.sequencerSteps; i++)
            padGrid.light (i, this.getGridColor (isKeyboardEnabled, isRecording, selectedTrack, i));

    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        final int [] matrix = this.scales.getNoteMatrix ();
        for (int i = this.scales.getStartNote () + this.sequencerSteps; i < this.scales.getEndNote (); i++)
            matrix[i] = -1;
        this.delayedUpdateNoteMapping (matrix);
    }


    /**
     * Check if any note is set at the current step.
     *
     * @param clip The clip which contains the notes
     * @param col The column/step to check
     * @return 0: All notes are off, 1: at least 1 note continues playing, 2: at least 1 note starts
     *         at this step, see the defined constants
     */
    private int getStep (final INoteClip clip, final int col)
    {
        int result = IStepInfo.NOTE_OFF;
        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        for (int row = 0; row < 128; row++)
        {
            result = clip.getStep (editMidiChannel, col, row).getState ();
            if (result == IStepInfo.NOTE_START)
                return result;
            if (result == IStepInfo.NOTE_CONTINUE)
                result = IStepInfo.NOTE_CONTINUE;
        }
        return result;
    }


    /**
     * Get the step color.
     *
     * @param isSet True if the note is set
     * @param hilite True if note should be highlighted
     * @return The color identifier
     */
    protected String getStepColor (final int isSet, final boolean hilite)
    {
        switch (isSet)
        {
            // Note continues
            case IStepInfo.NOTE_CONTINUE:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_CONTENT : AbstractSequencerView.COLOR_CONTENT_CONT;
            // Note starts
            case IStepInfo.NOTE_START:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_CONTENT : AbstractSequencerView.COLOR_CONTENT;
            // Empty
            default:
                return hilite ? AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT : AbstractSequencerView.COLOR_NO_CONTENT;
        }
    }


    /**
     * Get the color for a pad.
     *
     * @param isKeyboardEnabled Can we play?
     * @param isRecording Is recording enabled?
     * @param track The track to use the color for octaves
     * @param note The note of the pad
     * @return The ID of the color
     */
    protected String getGridColor (final boolean isKeyboardEnabled, final boolean isRecording, final ITrack track, final int note)
    {
        if (isKeyboardEnabled)
        {
            if (this.keyManager.isKeyPressed (note))
                return isRecording ? AbstractPlayView.COLOR_RECORD : AbstractPlayView.COLOR_PLAY;
            return this.getPadColor (note, this.useTrackColor ? track : null);
        }
        return AbstractPlayView.COLOR_OFF;
    }
}