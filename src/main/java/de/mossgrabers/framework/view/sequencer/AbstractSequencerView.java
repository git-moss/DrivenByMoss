// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view.sequencer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.IStepInfo;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.daw.clip.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.INoteMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


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
        this.numSequencerRows = numSequencerRows;

        this.configuration = this.surface.getConfiguration ();

        this.getClip ();
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
        final int resolutionIndex = Math.min (Math.max (0, selectedResolutionIndex), 7);
        final Resolution resolution = Resolution.values ()[resolutionIndex];
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
        return Resolution.getMatch (this.getClip ().getStepLength ());
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        if (!this.isActive ())
            return AbstractSequencerView.COLOR_RESOLUTION_OFF;

        if (!ButtonID.isSceneButton (buttonID))
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;

        return buttonID == ButtonID.get (ButtonID.SCENE1, 7 - this.getResolutionIndex ()) ? AbstractSequencerView.COLOR_RESOLUTION_SELECTED : AbstractSequencerView.COLOR_RESOLUTION;
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
        return (int) Math.floor (numOfSteps * Resolution.getValueAt (this.getResolutionIndex ()));
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
     * @param notePosition The position of the note
     * @param velocity The velocity
     * @param increase True to increase otherwise decrease
     */
    protected void handleSequencerAreaRepeatOperator (final INoteClip clip, final NotePosition notePosition, final int velocity, final boolean increase)
    {
        final IStepInfo stepInfo = clip.getStep (notePosition);
        if (stepInfo.getState () == StepState.OFF)
            clip.toggleStep (notePosition, velocity);
        final boolean isOff = !stepInfo.isRepeatEnabled ();
        if (isOff)
            clip.updateStepIsRepeatEnabled (notePosition, true);
        int repeatCount = stepInfo.getRepeatCount ();
        repeatCount = increase ? Math.min (127, repeatCount + 1) : Math.max (-127, repeatCount - 1);
        clip.updateStepRepeatCount (notePosition, repeatCount);
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
     * @param notePosition The position of the note
     * @param addNote Add the note to the edited notes otherwise clear the already selected and add
     *            only the new one
     */
    protected void editNote (final INoteClip clip, final NotePosition notePosition, final boolean addNote)
    {
        final StepState state = clip.getStep (notePosition).getState ();
        if (state != StepState.START)
            return;

        final INoteMode noteMode = this.getNoteEditor ();
        if (noteMode == null)
            return;
        if (addNote)
            noteMode.addNote (clip, notePosition);
        else
            noteMode.setNote (clip, notePosition);
    }


    private INoteMode getNoteEditor ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final IMode mode = modeManager.get (Modes.NOTE);
        if (mode instanceof final INoteMode noteMode)
        {
            modeManager.setActive (Modes.NOTE);
            return noteMode;
        }
        final ViewManager viewManager = this.surface.getViewManager ();
        final IView view = viewManager.get (Views.NOTE_EDIT_VIEW);
        if (view instanceof final INoteMode noteMode)
        {
            viewManager.setTemporary (Views.NOTE_EDIT_VIEW);
            return noteMode;
        }
        return null;
    }


    /**
     * Clear all edit notes.
     */
    protected void clearEditNotes ()
    {
        INoteMode noteMode = null;
        if (this.surface.getModeManager ().get (Modes.NOTE) instanceof final INoteMode noteMode1)
            noteMode = noteMode1;
        else if (this.surface.getViewManager ().get (Views.NOTE_EDIT_VIEW) instanceof final INoteMode noteMode2)
            noteMode = noteMode2;
        if (noteMode != null)
            noteMode.clearNotes ();
    }


    /**
     * Get the position of the notes which are currently selected for editing.
     *
     * @return THe note positions
     */
    protected List<NotePosition> getEditNotes ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final IMode mode = modeManager.get (Modes.NOTE);
        if (mode instanceof final INoteMode noteMode)
            return noteMode.getNotes ();
        return Collections.emptyList ();
    }


    /**
     * Test if at the given channel / step / note is a note which is selected for editing.
     *
     * @param channel A MIDI channel
     * @param step A sequencer step
     * @param note A note
     * @param editNotes The currently edited notes
     * @return True if there is a note which is edited
     */
    protected static boolean isEdit (final int channel, final int step, final int note, final List<NotePosition> editNotes)
    {
        for (final NotePosition editNote: editNotes)
        {
            if (editNote.getChannel () == channel && editNote.getStep () == step && editNote.getNote () == note)
                return true;
        }
        return false;
    }


    /**
     * Get the color for a step.
     *
     * @param stepInfo The information about the step
     * @param highlight The step should be highlighted
     * @param channel The MIDI channel
     * @param step The step of the note
     * @param pad The pad
     * @param note The note of the step
     * @param editNotes The currently edited notes
     * @return The color
     */
    protected String getStepColor (final IStepInfo stepInfo, final boolean highlight, final int channel, final int step, final int pad, final int note, final List<NotePosition> editNotes)
    {
        if (stepInfo == null || stepInfo.getState () == StepState.OFF)
            return this.getPadColor (pad, this.useDawColors ? this.model.getCursorTrack () : null);

        return this.getStepColor (stepInfo, highlight, Optional.empty (), channel, step, note, editNotes);
    }


    /**
     * Get the color for a step.
     *
     * @param stepInfo The information about the step
     * @param highlight The step should be highlighted
     * @param channel The MIDI channel
     * @param step The step of the note
     * @param rowColor The color to use for content notes
     * @param note The note of the step
     * @param editNotes The currently edited notes
     * @return The color
     */
    protected String getStepColor (final IStepInfo stepInfo, final boolean highlight, final Optional<ColorEx> rowColor, final int channel, final int step, final int note, final List<NotePosition> editNotes)
    {
        final StepState state = stepInfo == null ? StepState.OFF : stepInfo.getState ();
        switch (state)
        {
            case START:
                if (highlight)
                    return COLOR_STEP_HILITE_CONTENT;
                if (isEdit (channel, step, note, editNotes))
                    return COLOR_STEP_SELECTED;
                if (stepInfo != null && stepInfo.isMuted ())
                    return COLOR_STEP_MUTED;
                return rowColor.isPresent () && this.useDawColors ? DAWColor.getColorID (rowColor.get ()) : COLOR_CONTENT;

            case CONTINUE:
                if (highlight)
                    return COLOR_STEP_HILITE_CONTENT;
                if (isEdit (channel, step, note, editNotes))
                    return COLOR_STEP_SELECTED;
                if (stepInfo != null && stepInfo.isMuted ())
                    return COLOR_STEP_MUTED_CONT;
                return rowColor.isPresent () && this.useDawColors ? DAWColor.getColorID (ColorEx.darker (rowColor.get ())) : COLOR_CONTENT_CONT;

            default:
                if (highlight)
                    return COLOR_STEP_HILITE_NO_CONTENT;
                return step / 4 % 2 == 1 ? COLOR_NO_CONTENT_4 : COLOR_NO_CONTENT;
        }
    }
}