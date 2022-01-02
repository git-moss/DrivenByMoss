// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
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
 * @author J&uuml;rgen Mo&szlig;graber
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
            case 12:
            case 13:
                if (!this.model.isEffectTrackBankActive ())
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

        int track;
        final Optional<ITrack> selectedTrack = tb.getSelectedItem ();
        int index;
        switch (note - 36)
        {
            // Toggle Activate
            case 0:

                if (selectedTrack.isPresent ())
                    selectedTrack.get ().toggleIsActivated ();
                break;

            // Track left
            case 1:
                index = selectedTrack.isEmpty () ? 0 : selectedTrack.get ().getIndex () - 1;
                if (index == -1 || this.surface.isShiftPressed ())
                    tb.selectPreviousPage ();
                else
                    this.selectTrack (index);
                break;

            // Track right
            case 2:
                index = selectedTrack.isEmpty () ? 0 : selectedTrack.get ().getIndex () + 1;
                if (index == 8 || this.surface.isShiftPressed ())
                    tb.selectNextPage ();
                else
                    this.selectTrack (index);
                break;

            // Move down
            case 3:
                if (selectedTrack.isPresent ())
                    selectedTrack.get ().enter ();
                break;

            // Move up
            case 4:
                tb.selectParent ();
                break;

            // Unused
            case 5:
                break;

            // Track Page down
            case 6:
                tb.selectPreviousPage ();
                break;

            // Track Page up
            case 7:
                tb.selectNextPage ();
                break;

            default:
                track = note - 36 - 8;
                this.selectTrack (track);
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
        padGrid.light (36, sel.isPresent () && sel.get ().isActivated () ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED : BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (37, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (38, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (39, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (40, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (41, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (42, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (43, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
    }
}