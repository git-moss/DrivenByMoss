// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.daw.CursorClipProxy;


/**
 * Abstract implementation for a view which provides a sequencer.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractSequencerView<S extends ControlSurface<C>, C extends Configuration> extends AbstractView<S, C> implements SceneView
{
    /** The color for highlighting a step with no content. */
    public static final String       COLOR_STEP_HILITE_NO_CONTENT = "COLOR_STEP_HILITE_NO_CONTENT";
    /** The color for highlighting a step with with content. */
    public static final String       COLOR_STEP_HILITE_CONTENT    = "COLOR_STEP_HILITE_CONTENT";
    /** The color for a step with no content. */
    public static final String       COLOR_NO_CONTENT             = "COLOR_NO_CONTENT";
    /** The color for a step with content. */
    public static final String       COLOR_CONTENT                = "COLOR_CONTENT";
    /** The color for a step with content which is not the start of the note. */
    public static final String       COLOR_CONTENT_CONT           = "COLOR_CONTENT_CONT";
    /** The color for a measure. */
    public static final String       COLOR_MEASURE                = "COLOR_MEASURE";
    /** The color for an active measure. */
    public static final String       COLOR_ACTIVE_MEASURE         = "COLOR_ACTIVE_MEASURE";

    protected static final double [] RESOLUTIONS                  =
    {
        1,
        2.0 / 3.0,
        1.0 / 2.0,
        1.0 / 3.0,
        1.0 / 4.0,
        1.0 / 6.0,
        1.0 / 8.0,
        1.0 / 12.0
    };

    protected static final String [] RESOLUTION_TEXTS             =
    {
        "1/4",
        "1/4t",
        "1/8",
        "1/8t",
        "1/16",
        "1/16t",
        "1/32",
        "1/32t"
    };

    protected int                    numSequencerRows;
    protected int                    selectedIndex;
    protected int                    offsetX;
    protected int                    offsetY;
    protected CursorClipProxy        clip;
    protected final Configuration    configuration;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param clipRows The rows of the monitored clip
     * @param clipCols The cols of the monitored clip
     */
    public AbstractSequencerView (final String name, final S surface, final Model model, final int clipRows, final int clipCols)
    {
        this (name, surface, model, clipRows, clipCols, clipRows);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param clipRows The rows of the monitored clip
     * @param clipCols The cols of the monitored clip
     * @param numSequencerRows The number of displayed rows of the sequencer
     */
    public AbstractSequencerView (final String name, final S surface, final Model model, final int clipRows, final int clipCols, final int numSequencerRows)
    {
        super (name, surface, model);
        this.configuration = this.surface.getConfiguration ();

        this.selectedIndex = 4;
        this.scales = this.model.getScales ();

        this.offsetX = 0;
        this.offsetY = 0;

        this.numSequencerRows = numSequencerRows;
        this.clip = this.model.createCursorClip (clipCols, clipRows);
        this.clip.setStepLength (RESOLUTIONS[this.selectedIndex]);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();
        this.clip.enableObservers (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();
        this.clip.enableObservers (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || !this.model.canSelectedTrackHoldNotes ())
            return;
        this.selectedIndex = 7 - index;
        this.clip.setStepLength (RESOLUTIONS[this.selectedIndex]);
        this.surface.getDisplay ().notify (RESOLUTION_TEXTS[this.selectedIndex]);
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
        final int newOffset = this.offsetX - this.clip.getStepSize ();
        if (newOffset < 0)
            this.offsetX = 0;
        else
        {
            this.offsetX = newOffset;
            this.clip.scrollStepsPageBackwards ();
        }
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
        this.offsetX = this.offsetX + this.clip.getStepSize ();
        this.clip.scrollStepsPageForward ();
    }


    /**
     * Get the clip.
     *
     * @return The clip
     */
    public CursorClipProxy getClip ()
    {
        return this.clip;
    }


    /**
     * Checks if the given number is in the current display.
     *
     * @param x The index to check
     * @return True if it should be displayed
     */
    protected boolean isInXRange (final int x)
    {
        return x >= this.offsetX && x < this.offsetX + this.clip.getStepSize ();
    }


    /**
     * Calculates how many semi-notes are between the first and last 'pad'.
     *
     * @return The number of semi-notes
     */
    protected int getScrollOffset ()
    {
        final int pos = this.numSequencerRows;
        return pos / 7 * 12 + this.noteMap[pos % 7] - this.noteMap[0];
    }


    /**
     * Get the offset in x direction.
     *
     * @return The offset
     */
    public int getOffsetX ()
    {
        return this.offsetX;
    }
}