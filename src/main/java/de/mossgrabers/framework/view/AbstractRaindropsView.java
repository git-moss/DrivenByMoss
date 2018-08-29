// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;


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

    protected int              numDisplayRows   = 8;
    protected boolean          ongoingResolutionChange;
    private boolean            useTrackColor;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public AbstractRaindropsView (final String name, final S surface, final IModel model, final boolean useTrackColor)
    {
        super (name, surface, model, 128, 32 * 16 /* Biggest number in Fixed Length */);

        this.useTrackColor = useTrackColor;

        this.offsetY = AbstractRaindropsView.START_KEY;
        this.getClip ().scrollTo (0, AbstractRaindropsView.START_KEY);

        this.canScrollUp = false;
        this.canScrollDown = false;
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
        if (!this.model.canSelectedTrackHoldNotes ())
            return;
        if (velocity == 0)
            return;
        final int index = note - 36;
        final int x = index % 8;
        final int y = index / 8;
        final int stepSize = y == 0 ? 1 : 2 * y;

        final ICursorClip clip = this.getClip ();
        final int length = (int) Math.floor (clip.getLoopLength () / RESOLUTIONS[this.selectedIndex]);
        final int distance = this.getNoteDistance (this.keyManager.map (x), length);
        clip.clearRow (this.keyManager.map (x));
        if (distance == -1 || distance != (y == 0 ? 1 : y * 2))
        {
            final int offset = clip.getCurrentStep () % stepSize;
            for (int i = offset; i < length; i += stepSize)
                clip.setStep (i, this.keyManager.map (x), this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity, RESOLUTIONS[this.selectedIndex]);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        if (!this.model.canSelectedTrackHoldNotes ())
        {
            this.surface.getPadGrid ().turnOff ();
            return;
        }

        if (this.ongoingResolutionChange)
            return;

        final ITrack selectedTrack = this.useTrackColor ? this.model.getSelectedTrack () : null;

        final int length = (int) Math.floor (this.getClip ().getLoopLength () / RESOLUTIONS[this.selectedIndex]);
        final int step = this.getClip ().getCurrentStep ();
        for (int x = 0; x < AbstractRaindropsView.NUM_DISPLAY_COLS; x++)
        {
            final int left = this.getNoteDistanceToTheLeft (this.keyManager.map (x), step, length);
            final int right = this.getNoteDistanceToTheRight (this.keyManager.map (x), step, length);
            final boolean isOn = left >= 0 && right >= 0;
            final int sum = left + right;
            final int distance = sum == 0 ? 0 : (sum + 1) / 2;

            for (int y = 0; y < this.numDisplayRows; y++)
            {
                String colorID = y == 0 ? this.getColor (x, selectedTrack) : AbstractSequencerView.COLOR_NO_CONTENT;
                if (isOn)
                {
                    if (y == distance)
                        colorID = AbstractSequencerView.COLOR_CONTENT;
                    if (left <= distance && y == left || left > distance && y == sum - left)
                        colorID = AbstractSequencerView.COLOR_STEP_HILITE_NO_CONTENT;
                }
                this.surface.getPadGrid ().lightEx (x, this.numDisplayRows - 1 - y, colorID);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.ongoingResolutionChange = true;
        super.onScene (index, event);
        this.ongoingResolutionChange = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveDown (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.offsetY = Math.max (0, this.offsetY - AbstractRaindropsView.NUM_OCTAVE);
        this.updateScale ();
        this.surface.scheduleTask ( () -> {
            this.surface.getDisplay ().notify (Scales.getSequencerRangeText (this.keyManager.map (0), this.keyManager.map (7)));
        }, 10);
    }


    /** {@inheritDoc} */
    @Override
    public void onOctaveUp (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final int numRows = this.getClip ().getNumRows ();
        this.offsetY = Math.min (numRows - AbstractRaindropsView.NUM_OCTAVE, this.offsetY + AbstractRaindropsView.NUM_OCTAVE);
        this.updateScale ();
        this.surface.scheduleTask ( () -> {
            this.surface.getDisplay ().notify (Scales.getSequencerRangeText (this.keyManager.map (0), this.keyManager.map (7)));
        }, 10);
    }


    protected int getNoteDistance (final int row, final int length)
    {
        int step;
        for (step = 0; step < length; step++)
        {
            if (this.getClip ().getStep (step, row) > 0)
                break;
        }
        if (step >= length)
            return -1;
        for (int step2 = step + 1; step2 < length; step2++)
        {
            if (this.getClip ().getStep (step2, row) > 0)
                return step2 - step;
        }
        return -1;
    }


    protected int getNoteDistanceToTheRight (final int row, final int start, final int length)
    {
        if (start < 0 || start >= length)
            return -1;
        int step = start;
        int counter = 0;
        do
        {
            if (this.getClip ().getStep (step, row) > 0)
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
        if (start < 0 || start >= length)
            return -1;
        final int s = start == 0 ? length - 1 : start - 1;
        int step = s;
        int counter = 0;
        do
        {
            if (this.getClip ().getStep (step, row) > 0)
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