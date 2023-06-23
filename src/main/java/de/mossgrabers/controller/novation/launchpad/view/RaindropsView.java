// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.featuregroup.IScrollableView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ScrollStates;
import de.mossgrabers.framework.view.sequencer.AbstractRaindropsView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * The rain drops sequencer.
 *
 * @author Jürgen Moßgraber
 */
public class RaindropsView extends AbstractRaindropsView<LaunchpadControlSurface, LaunchpadConfiguration> implements IScrollableView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public RaindropsView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Raindrops", surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (!this.isActive ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;

        final int ordinal = buttonID.ordinal ();
        if (ordinal < ButtonID.SCENE1.ordinal () || ordinal > ButtonID.SCENE8.ordinal ())
            return 0;

        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        return scene == 7 - this.getResolutionIndex () ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;
    }


    /** {@inheritDoc} */
    @Override
    public void updateScrollStates (final ScrollStates scrollStates)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        final INoteClip clip = AbstractSequencerView.class.cast (viewManager.getActive ()).getClip ();
        final int seqOctave = this.scales.getOctave ();
        scrollStates.setCanScrollLeft (clip.canScrollStepsBackwards ());
        scrollStates.setCanScrollRight (clip.canScrollStepsForwards ());
        scrollStates.setCanScrollUp (seqOctave < Scales.OCTAVE_RANGE);
        scrollStates.setCanScrollDown (seqOctave > -Scales.OCTAVE_RANGE);
    }
}