// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.view;

import de.mossgrabers.beatstep.BeatstepConfiguration;
import de.mossgrabers.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractSequencerView;

import java.util.Arrays;


/**
 * Base class for Beatstep sequencers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseSequencerView extends AbstractSequencerView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    protected TrackEditing extensions;
    protected int []       pressedKeys;
    protected int          selectedPad;
    protected boolean      isPlayMode = true;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The controller
     * @param model The model
     * @param rows The number of rows of the sequencer
     * @param cols The number of columns of the sequencer
     */
    public BaseSequencerView (final String name, final BeatstepControlSurface surface, final Model model, final int rows, final int cols)
    {
        super (name, surface, model, rows, cols);

        this.extensions = new TrackEditing (surface, model);
        this.noteMap = Scales.getEmptyMatrix ();
        this.pressedKeys = new int [128];
        Arrays.fill (this.pressedKeys, 0);
    }


    protected void changeScrollPosition (final int value)
    {
        final boolean isInc = value >= 65;
        if (isInc)
            this.clip.scrollStepsPageForward ();
        else
            this.clip.scrollStepsPageBackwards ();
    }


    protected void changeResolution (final int value)
    {
        final boolean isInc = value >= 65;
        this.selectedIndex = Math.max (0, Math.min (RESOLUTIONS.length - 1, isInc ? this.selectedIndex + 1 : this.selectedIndex - 1));
        this.clip.setStepLength (RESOLUTIONS[this.selectedIndex]);
    }


    protected void clearPressedKeys ()
    {
        for (int i = 0; i < 128; i++)
            this.pressedKeys[i] = 0;
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        // Intentionally empty
    }
}