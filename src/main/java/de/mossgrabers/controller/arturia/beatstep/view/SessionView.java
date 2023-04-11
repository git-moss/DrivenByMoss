// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.view;

import de.mossgrabers.controller.arturia.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * The Session view.
 *
 * @author Jürgen Moßgraber
 */
public class SessionView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private final TrackEditing extensions;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public SessionView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Session", surface, model);
        this.extensions = new TrackEditing (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value, final boolean isTurnedRight)
    {
        // Knob 12-15 are currently not used
        if (index < 12)
            this.extensions.onTrackKnob (index, value, isTurnedRight);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();

        final int index = note - 36;
        switch (index)
        {
            case 0, 1, 2, 3, 4, 5:
                // Not used
                break;

            case 6:
                if (velocity > 0)
                    sceneBank.selectPreviousPage ();
                break;

            case 7:
                if (velocity > 0)
                    sceneBank.selectNextPage ();
                break;

            case 8, 9, 10, 11, 12, 13, 14, 15:
                sceneBank.getItem (index - 8).launch (velocity > 0, false);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 0; i < 6; i++)
            padGrid.light (36 + i, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        for (int i = 6; i < 8; i++)
            padGrid.light (36 + i, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        for (int i = 8; i < 16; i++)
            padGrid.light (36 + i, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
    }
}