// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.view;

import de.mossgrabers.controller.arturia.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;

import java.util.Optional;


/**
 * The track view.
 *
 * @author Jürgen Moßgraber
 */
public class TrackView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private final TrackEditing extensions;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public TrackView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Track", surface, model);
        this.extensions = new TrackEditing (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value, final boolean isTurnedRight)
    {
        if (index < 12)
        {
            this.extensions.onTrackKnob (index, value, isTurnedRight);
            return;
        }

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final Optional<ITrack> selectedTrack = tb.getSelectedItem ();
        if (selectedTrack.isEmpty ())
            return;

        switch (index)
        {
            // Send 5 - 6
            case 12, 13:
                selectedTrack.get ().getSendBank ().getItem (index - 8).changeValue (value);
                break;

            case 14:
                // Not used
                break;

            // Crossfader
            case 15:
                this.model.getTransport ().changeCrossfade (value);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final Optional<ITrack> selectedTrack = tb.getSelectedItem ();
        final int index = note - 36;
        switch (index)
        {
            case 0:
                if (selectedTrack.isPresent ())
                    selectedTrack.get ().toggleIsActivated ();
                break;

            case 1:
                if (selectedTrack.isPresent ())
                    selectedTrack.get ().toggleRecArm ();
                break;

            case 2:
                if (selectedTrack.isPresent ())
                    selectedTrack.get ().toggleGroupExpanded ();
                break;

            case 3:
                this.model.getApplication ().addInstrumentTrack ();
                break;

            case 4:
                this.model.getApplication ().addAudioTrack ();
                break;

            case 5:
                this.model.getApplication ().addEffectTrack ();
                break;

            case 6:
                tb.selectPreviousPage ();
                break;

            case 7:
                tb.selectNextPage ();
                break;

            // 8-15
            default:
                this.selectTrack (index - 8);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 0; i < 8; i++)
            padGrid.light (44 + i, tb.getItem (i).isSelected () ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE : BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);

        final Optional<ITrack> sel = tb.getSelectedItem ();
        final boolean isPresent = sel.isPresent ();
        padGrid.light (36, isPresent && sel.get ().isActivated () ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE : BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (37, isPresent && sel.get ().isRecArm () ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED : BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);

        int groupColor = BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF;
        if (isPresent && sel.get ().isGroup ())
            groupColor = sel.get ().isGroupExpanded () ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK : BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE;
        padGrid.light (38, groupColor);

        padGrid.light (39, BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (40, BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (41, BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (42, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (43, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
    }
}