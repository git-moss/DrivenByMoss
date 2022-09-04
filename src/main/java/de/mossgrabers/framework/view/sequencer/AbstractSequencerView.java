// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view.sequencer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.GridStep;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.INoteMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Collections;
import java.util.List;


/**
 * Abstract implementation for a view which provides a sequencer.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractSequencerView<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C>
{
    /** The color for highlighting a step with no content. */
    public static final String    COLOR_STEP_HILITE_NO_CONTENT = "COLOR_STEP_HILITE_NO_CONTENT";
    /** The color for highlighting a step with content. */
    public static final String    COLOR_STEP_HILITE_CONTENT    = "COLOR_STEP_HILITE_CONTENT";
    /** The color for a muted step. */
    public static final String    COLOR_STEP_MUTED             = "COLOR_STEP_MUTED";
    /** The color for a continued muted step. */
    public static final String    COLOR_STEP_MUTED_CONT        = "COLOR_STEP_MUTED_CONT";
    /** The color for a selected step. */
    public static final String    COLOR_STEP_SELECTED          = "COLOR_STEP_SELECTED";
    /** The color for a step with no content. */
    public static final String    COLOR_NO_CONTENT             = "COLOR_NO_CONTENT";
    /** The color for a step with no content (2nd four group). */
    public static final String    COLOR_NO_CONTENT_4           = "COLOR_NO_CONTENT_4";
    /** The color for a step with content. */
    public static final String    COLOR_CONTENT                = "COLOR_CONTENT";
    /** The color for a step with content which is not the start of the note. */
    public static final String    COLOR_CONTENT_CONT           = "COLOR_CONTENT_CONT";
    /** The color for a page. */
    public static final String    COLOR_PAGE                   = "COLOR_PAGE";
    /** The color for an active page. */
    public static final String    COLOR_ACTIVE_PAGE            = "COLOR_ACTIVE_PAGE";
    /** The color for a selected page. */
    public static final String    COLOR_SELECTED_PAGE          = "COLOR_SELECTED_PAGE";
    /** The color for resolution off. */
    public static final String    COLOR_RESOLUTION_OFF         = "COLOR_RESOLUTION_OFF";
    /** The color for resolution. */
    public static final String    COLOR_RESOLUTION             = "COLOR_RESOLUTION";
    /** The color for selected resolution. */
    public static final String    COLOR_RESOLUTION_SELECTED    = "COLOR_RESOLUTION_SELECTED";
    /** The color for transposition. */
    public static final String    COLOR_TRANSPOSE              = "COLOR_TRANSPOSE";
    /** The color for selected transposition. */
    public static final String    COLOR_TRANSPOSE_SELECTED     = "COLOR_TRANSPOSE_SELECTED";

    protected final int           clipRows;
    protected final int           clipCols;
    protected final boolean       useDawColors;

    protected int                 numSequencerRows;
    protected int                 selectedResolutionIndex;
    protected final Configuration configuration;
    protected boolean             isNoteEdited                 = false;
    private boolean               isSequencerActive;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param clipRows The rows of the monitored clip
     * @param clipCols The columns of the monitored clip
     * @param useDawColors True to use the DAW color of items for coloring (for full RGB devices)
     */
    protected AbstractSequencerView (final String name, final S surface, final IModel model, final int clipRows, final int clipCols, final boolean useDawColors)
    {
        this (name, surface, model, clipRows, clipCols, clipRows, useDawColors);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param clipRows The rows of the monitored clip
     * @param clipCols The columns of the monitored clip
     * @param numSequencerRows The number of displayed rows of the sequencer
     * @param useDawColors True to use the DAW color of items for coloring (for full RGB devices)
     */
    protected AbstractSequencerView (final String name, final S surface, final IModel model, final int clipRows, final int clipCols, final int numSequencerRows, final boolean useDawColors)
    {
        super (name, surface, model);

        this.clipRows = clipRows;
        this.clipCols = clipCols;
        this.useDawColors = useDawColors;

        this.configuration = this.surface.getConfiguration ();

        this.selectedResolutionIndex = 4;

        this.numSequencerRows = numSequencerRows;

        this.getClip ();
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.getClip ().setStepLength (Resolution.getValueAt (this.selectedResolutionIndex));
    }


    /** {@inheritDoc} */
    @Override
    public void updateControlSurface ()
    {
        this.setSequencerActive (this.model.canSelectedTrackHoldNotes () && this.getClip ().doesExist ());

        super.updateControlSurface ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN || !this.isActive ())
            return;
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        this.setResolutionIndex (7 - index);
    }


    /**
     * Scroll clip left.
     *
     * @param event The event
     */
    public void onLeft (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final INoteClip clip = this.getClip ();
        clip.scrollStepsPageBackwards ();
        this.mvHelper.notifyEditPage (clip);
        this.clearEditNotes ();
    }


    /**
     * Scroll clip right.
     *
     * @param event The event
     */
    public void onRight (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final INoteClip clip = this.getClip ();
        clip.scrollStepsPageForward ();
        this.mvHelper.notifyEditPage (clip);
        this.clearEditNotes ();
    }


    /**
     * Get the clip.
     *
     * @return The clip
     */
    public final INoteClip getClip ()
    {
        return this.model.getNoteClip (this.clipCols, this.clipRows);
    }


    /**
     * Set the resolution index.
     *
     * @param selectedResolutionIndex The index 0-7
     */
    public void setResolutionIndex (final int selectedResolutionIndex)
    {
        this.selectedResolutionIndex = Math.min (Math.max (0, selectedResolutionIndex), 7);
        final Resolution resolution = Resolution.values ()[this.selectedResolutionIndex];
        this.getClip ().setStepLength (resolution.getValue ());
        this.surface.getDisplay ().notify ("Grid res.: " + resolution.getName ());
    }


    /**
     * Get the resolution index.
     *
     * @return The index 0-7
     */
    public int getResolutionIndex ()
    {
        return this.selectedResolutionIndex;
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        if (!this.isActive ())
            return AbstractSequencerView.COLOR_RESOLUTION_OFF;

        if (!ButtonID.isSceneButton (buttonID))
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;

        return buttonID == ButtonID.get (ButtonID.SCENE1, 7 - this.selectedResolutionIndex) ? AbstractSequencerView.COLOR_RESOLUTION_SELECTED : AbstractSequencerView.COLOR_RESOLUTION;
    }


    /**
     * Signal that a note has been edited.
     */
    public void setNoteEdited ()
    {
        this.isNoteEdited = true;
    }


    /**
     * Checks if the given number is in the current display.
     *
     * @param x The index to check
     * @return True if it should be displayed
     */
    protected boolean isInXRange (final int x)
    {
        final INoteClip clip = this.getClip ();
        final int stepSize = clip.getNumSteps ();
        final int start = clip.getEditPage () * stepSize;
        return x >= start && x < start + stepSize;
    }


    /**
     * Calculates the length of one sequencer page which is the number of displayed steps multiplied
     * with the current grid resolution.
     *
     * @param numOfSteps The number of displayed steps
     * @return The floor of the length
     */
    protected int getLengthOfOnePage (final int numOfSteps)
    {
        return (int) Math.floor (numOfSteps * Resolution.getValueAt (this.selectedResolutionIndex));
    }


    /**
     * Get the color for a sequencer page.
     *
     * @param loopStartPage The page where the loop starts
     * @param loopEndPage The page where the loop ends
     * @param playPage The page which contains the currently played step
     * @param selectedPage The page selected fpr editing
     * @param page The page for which to get the color
     * @return The color to use
     */
    protected String getPageColor (final int loopStartPage, final int loopEndPage, final int playPage, final int selectedPage, final int page)
    {
        if (page == playPage)
            return AbstractSequencerView.COLOR_ACTIVE_PAGE;

        if (page == selectedPage)
            return AbstractSequencerView.COLOR_SELECTED_PAGE;

        if (page < loopStartPage || page >= loopEndPage)
            return AbstractSequencerView.COLOR_NO_CONTENT;

        return AbstractSequencerView.COLOR_PAGE;
    }


    /**
     * Handle repeat operator quick change. If repeat count is off it is set to 4 otherwise it is
     * increased or decreased depending on the parameter.
     *
     * @param clip The sequenced MIDI clip
     * @param channel The MIDI channel of the note
     * @param step The step in the current page in the clip
     * @param note The note in the current page of the pad in the clip
     * @param velocity The velocity
     * @param increase True to increase otherwise decrease
     */
    protected void handleSequencerAreaRepeatOperator (final INoteClip clip, final int channel, final int step, final int note, final int velocity, final boolean increase)
    {
        final IStepInfo stepInfo = clip.getStep (channel, step, note);
        if (stepInfo.getState () == StepState.OFF)
            clip.toggleStep (channel, step, note, velocity);
        final boolean isOff = !stepInfo.isRepeatEnabled ();
        if (isOff)
            clip.updateStepIsRepeatEnabled (channel, step, note, true);
        int repeatCount = stepInfo.getRepeatCount ();
        repeatCount = increase ? Math.min (127, repeatCount + 1) : Math.max (-127, repeatCount - 1);
        clip.updateStepRepeatCount (channel, step, note, repeatCount);
        String repeatCountStr;
        if (repeatCount > 0)
            repeatCountStr = Integer.toString (repeatCount + 1);
        else if (repeatCount == 0)
            repeatCountStr = "Off";
        else
            repeatCountStr = "1/" + Integer.toString (1 - repeatCount);
        this.surface.getDisplay ().notify ("Note repeat: " + repeatCountStr);
    }


    /**
     * Check if there is a note clip to edit.
     *
     * @return Returns true if the selected track can hold notes and the selected clip exists
     */
    public boolean isActive ()
    {
        return this.isSequencerActive;
    }


    /**
     * Set the sequencer active.
     *
     * @param isSequencerActive True to set active
     */
    public void setSequencerActive (final boolean isSequencerActive)
    {
        this.isSequencerActive = isSequencerActive;
    }


    /**
     * Show edit mode and set or add note.
     *
     * @param clip The MIDI clip
     * @param channel The MIDI channel of the note
     * @param step The step of the note
     * @param mappedNote The real note
     * @param addNote Add the note to the edited notes otherwise clear the already selected and add
     *            only the new one
     */
    protected void editNote (final INoteClip clip, final int channel, final int step, final int mappedNote, final boolean addNote)
    {
        final StepState state = clip.getStep (channel, step, mappedNote).getState ();
        if (state != StepState.START)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final IMode mode = modeManager.get (Modes.NOTE);
        if (mode instanceof final INoteMode noteMode)
        {
            if (addNote)
                noteMode.addNote (clip, channel, step, mappedNote);
            else
                noteMode.setNote (clip, channel, step, mappedNote);
            modeManager.setActive (Modes.NOTE);
        }
    }


    /**
     * Clear all edit notes.
     */
    protected void clearEditNotes ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final IMode mode = modeManager.get (Modes.NOTE);
        if (mode instanceof final INoteMode noteMode)
            noteMode.clearNotes ();
    }


    protected List<GridStep> getEditNotes ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final IMode mode = modeManager.get (Modes.NOTE);
        if (mode instanceof final INoteMode noteMode)
            return noteMode.getNotes ();
        return Collections.emptyList ();
    }


    protected static boolean isEdit (final int channel, final int step, final int note, final List<GridStep> editNotes)
    {
        for (final GridStep editNote: editNotes)
        {
            if (editNote.channel () == channel && editNote.step () == step && editNote.note () == note)
                return true;
        }
        return false;
    }
}