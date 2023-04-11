// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.ILightGuide;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Arrays;


/**
 * Abstract implementation for a play grid.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractPlayView<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C> implements TransposeView
{
    /** ID of the color to use when a pad is played. */
    public static final String COLOR_PLAY   = "PLAY_VIEW_COLOR_PLAY";
    /** ID of the color to use when a pad is played and recording is enabled. */
    public static final String COLOR_RECORD = "PLAY_VIEW_COLOR_RECORD";
    /** ID of the color to use when a pad does not contain a note. */
    public static final String COLOR_OFF    = "PLAY_VIEW_COLOR_OFF";

    protected final int []     defaultVelocity;
    protected final boolean    useTrackColor;

    private int                blockNotes   = 0;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    protected AbstractPlayView (final S surface, final IModel model, final boolean useTrackColor)
    {
        this (Views.NAME_PLAY, surface, model, useTrackColor);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    protected AbstractPlayView (final String name, final S surface, final IModel model, final boolean useTrackColor)
    {
        super (name, surface, model);

        this.useTrackColor = useTrackColor;

        this.defaultVelocity = new int [128];
        for (int i = 0; i < 128; i++)
            this.defaultVelocity[i] = i;

        final ITrackBank tb = model.getTrackBank ();
        tb.addSelectionObserver ( (index, isSelected) -> this.keyManager.clearPressedKeys ());
        tb.addNoteObserver (this.keyManager::call);
    }


    /**
     * Blocks the number of notes from the bottom and shifts the rest up by this number.
     *
     * @param blockNotes The number of notes to block
     */
    public void setBlockedNotes (final int blockNotes)
    {
        this.blockNotes = blockNotes;
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        this.drawLightGuide (this.surface.getPadGrid ());
    }


    protected void drawLightGuide (final ILightGuide lightGuide)
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        final boolean isRecording = this.model.hasRecordingState ();

        final ITrack cursorTrack = this.model.getCursorTrack ();
        final int startNote = this.scales.getStartNote ();
        final int endNote = this.scales.getEndNote ();
        for (int i = startNote; i < endNote; i++)
            lightGuide.light (i, this.getGridColor (isKeyboardEnabled, isRecording, cursorTrack, i));
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int key, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        // Mark selected notes immediately for better performance
        final int note = this.keyManager.map (key);
        if (note != -1)
            this.keyManager.setAllKeysPressed (note, velocity);

        this.playNote (note, velocity);
    }


    /**
     * Hook for playing notes with grids which do not use MIDI notes.
     *
     * @param note The note to play (or stop)
     * @param velocity The velocity of the note (0 = off)
     */
    protected void playNote (final int note, final int velocity)
    {
        // Intentionally empty
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


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
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
        this.keyManager.clearPressedKeys ();
        this.scales.incOctave ();
        this.updateNoteMapping ();
        this.surface.getDisplay ().notify (this.scales.getRangeText ());
    }


    /**
     * Reset octave.
     */
    public void resetOctave ()
    {
        this.keyManager.clearPressedKeys ();
        this.scales.setOctave (0);
        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveDownButtonOn ()
    {
        return this.scales.getOctave () > -Scales.OCTAVE_RANGE;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isOctaveUpButtonOn ()
    {
        return this.scales.getOctave () < Scales.OCTAVE_RANGE;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();
        this.initMaxVelocity ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.surface.scheduleTask ( () -> this.delayedUpdateNoteMapping (this.getMapping ()), 100);
    }


    /**
     * Get the scale matrix to apply to the mapping table. Allows to block specific notes.
     *
     * @return The matrix (size of 127)
     */
    protected int [] getMapping ()
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return EMPTY_TABLE;

        final int [] noteMatrix = this.getScaleMatrix ();

        if (this.blockNotes > 0)
        {
            final int startNote = this.scales.getStartNote ();
            final int endNote = this.scales.getEndNote ();
            final int length = endNote - startNote - this.blockNotes;
            System.arraycopy (noteMatrix, startNote, noteMatrix, startNote + this.blockNotes, length);
            Arrays.fill (noteMatrix, startNote, startNote + 8, -1);
        }

        return noteMatrix;
    }


    /**
     * Get the note mapping matrix as a table based on the current scale settings.
     *
     * @return The mapping table
     */
    protected int [] getScaleMatrix ()
    {
        return this.scales.getNoteMatrix ();
    }


    /**
     * Updates the velocity transition table based on the fixed accent value.
     */
    protected void initMaxVelocity ()
    {
        final int [] maxVelocity = new int [128];
        final Configuration config = this.surface.getConfiguration ();
        Arrays.fill (maxVelocity, Math.min (127, Math.max (0, config.getFixedAccentValue ())));
        maxVelocity[0] = 0;
        this.surface.setVelocityTranslationTable (config.isAccentActive () ? maxVelocity : this.defaultVelocity);
    }


    protected void updateScale ()
    {
        this.updateNoteMapping ();
        final C config = this.surface.getConfiguration ();
        config.setScale (this.scales.getScale ().getName ());
        config.setScaleBase (Scales.BASES.get (this.scales.getScaleOffsetIndex ()));
        config.setScaleInKey (!this.scales.isChromatic ());
        config.setScaleLayout (this.scales.getScaleLayout ().getName ());
    }
}