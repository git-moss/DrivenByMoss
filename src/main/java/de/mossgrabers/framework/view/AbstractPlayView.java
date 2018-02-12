// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Abstract implementation for a play grid.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractPlayView<S extends ControlSurface<C>, C extends Configuration> extends AbstractView<S, C> implements TransposeView
{
    /** ID of the color to use when a pad is played. */
    public static final String COLOR_PLAY   = "PLAY_VIEW_COLOR_PLAY";
    /** ID of the color to use when a pad is played and recording is enabled. */
    public static final String COLOR_RECORD = "PLAY_VIEW_COLOR_RECORD";
    /** ID of the color to use when a pad does not contain a note. */
    public static final String COLOR_OFF    = "PLAY_VIEW_COLOR_OFF";

    protected int []           pressedKeys;
    protected int []           defaultVelocity;
    private boolean            useTrackColor;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public AbstractPlayView (final S surface, final Model model, final boolean useTrackColor)
    {
        this ("Play", surface, model, useTrackColor);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public AbstractPlayView (final String name, final S surface, final Model model, final boolean useTrackColor)
    {
        super (name, surface, model);

        this.useTrackColor = useTrackColor;
        this.scales = model.getScales ();
        this.noteMap = Scales.getEmptyMatrix ();

        this.pressedKeys = new int [128];
        Arrays.fill (this.pressedKeys, 0);
        this.defaultVelocity = new int [128];
        for (int i = 0; i < 128; i++)
            this.defaultVelocity[i] = i;

        final ITrackBank tb = model.getTrackBank ();
        // Light notes sent from the sequencer
        tb.addNoteObserver (this::setPressedKeys);
        tb.addTrackSelectionObserver ( (index, isSelected) -> this.clearPressedKeys ());
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        final boolean isRecording = this.model.hasRecordingState ();

        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedTrack ();
        final PadGrid gridPad = this.surface.getPadGrid ();
        for (int i = this.scales.getStartNote (); i < this.scales.getEndNote (); i++)
            gridPad.light (i, this.getGridColor (isKeyboardEnabled, isRecording, selectedTrack, i));
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
            if (this.pressedKeys[note] > 0)
                return isRecording ? AbstractPlayView.COLOR_RECORD : AbstractPlayView.COLOR_PLAY;
            return this.getColor (note, this.useTrackColor ? track : null);
        }
        return AbstractPlayView.COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes () || this.noteMap[note] == -1)
            return;
        // Mark selected notes
        this.setPressedKeys (this.noteMap[note], velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.clearPressedKeys ();
        this.scales.decOctave ();
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getRangeText (), true, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.clearPressedKeys ();
        this.scales.incOctave ();
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getRangeText (), true, true);
    }


    /**
     * Get the midi note from the grid.
     *
     * @param note The note on the grid
     * @return The translated note depending on applied scales, etc.
     */
    public int getMidiNoteFromGrid (final int note)
    {
        final PadGrid padGrid = this.surface.getPadGrid ();
        if (padGrid == null)
            return -1;
        final int translated = padGrid.translateToGrid (note);
        return translated < 0 ? -1 : this.noteMap[translated];
    }


    protected void clearPressedKeys ()
    {
        for (int i = 0; i < 128; i++)
            this.pressedKeys[i] = 0;
    }


    protected void setPressedKeys (final int note, final int velocity)
    {
        // Loop over all pads since the note can be present multiple time!
        for (int i = 0; i < 128; i++)
        {
            if (this.noteMap[i] == note)
                this.pressedKeys[i] = velocity;
        }
    }


    /**
     * Get the currently pressed keys.
     *
     * @return The list with the keys
     */
    public List<Integer> getPressedKeys ()
    {
        final List<Integer> keys = new ArrayList<> ();
        for (int i = 0; i < 128; i++)
        {
            if (this.pressedKeys[i] != 0)
                keys.add (Integer.valueOf (i));
        }
        return keys;
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        // Workaround: https://github.com/git-moss/Push4Bitwig/issues/7
        this.surface.scheduleTask (this::delayedUpdateNoteMapping, 100);
    }


    private void delayedUpdateNoteMapping ()
    {
        this.noteMap = this.model.canSelectedTrackHoldNotes () ? this.scales.getNoteMatrix () : Scales.getEmptyMatrix ();
        this.surface.setKeyTranslationTable (this.scales.translateMatrixToGrid (this.noteMap));
    }
}