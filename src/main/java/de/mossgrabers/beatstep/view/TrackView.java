// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.view;

import de.mossgrabers.beatstep.BeatstepConfiguration;
import de.mossgrabers.beatstep.controller.BeatstepColors;
import de.mossgrabers.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.view.AbstractView;


/**
 * The track view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private TrackEditing extensions;


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
    public void onKnob (final int index, final int value)
    {
        if (index < 12)
        {
            this.extensions.onTrackKnob (index, value);
            return;
        }

        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        switch (index)
        {
            // Send 5 - 6
            case 12:
            case 13:
                if (tb instanceof ITrackBank)
                    selectedTrack.getSend (index - 8).changeValue (value);
                break;

            case 14:
                // Not used
                break;

            // Crossfader
            case 15:
                this.model.getTransport ().changeCrossfade (value);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final IChannelBank tb = this.model.getCurrentTrackBank ();

        int track;
        ITrack selectedTrack;
        ITrack sel;
        int index;
        int newSel;
        switch (note - 36)
        {
            // Toggle Activate
            case 0:
                selectedTrack = tb.getSelectedTrack ();
                if (selectedTrack != null)
                    selectedTrack.toggleIsActivated ();
                break;

            // Track left
            case 1:
                sel = tb.getSelectedTrack ();
                index = sel == null ? 0 : sel.getIndex () - 1;
                if (index == -1 || this.surface.isShiftPressed ())
                {
                    if (!tb.canScrollTracksUp ())
                        return;
                    tb.scrollTracksPageUp ();
                    newSel = index == -1 || sel == null ? 7 : sel.getIndex ();
                    this.surface.scheduleTask ( () -> this.selectTrack (newSel), 75);
                    return;
                }
                this.selectTrack (index);
                break;

            // Track right
            case 2:
                sel = tb.getSelectedTrack ();
                index = sel == null ? 0 : sel.getIndex () + 1;
                if (index == 8 || this.surface.isShiftPressed ())
                {
                    if (!tb.canScrollTracksDown ())
                        return;
                    tb.scrollTracksPageDown ();
                    newSel = index == 8 || sel == null ? 0 : sel.getIndex ();
                    this.surface.scheduleTask ( () -> this.selectTrack (newSel), 75);
                    return;
                }
                this.selectTrack (index);
                break;

            // Move down
            case 3:
                if (tb instanceof ITrackBank)
                    ((ITrackBank) tb).selectChildren ();
                break;

            // Move up
            case 4:
                if (tb instanceof ITrackBank)
                    ((ITrackBank) tb).selectParent ();
                break;

            // Unused
            case 5:
                break;

            // Track Page down
            case 6:
                this.scrollTracksLeft ();
                break;

            // Track Page up
            case 7:
                this.scrollTracksRight ();
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
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final PadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 0; i < 8; i++)
            padGrid.light (44 + i, tb.getTrack (i).isSelected () ? BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);

        final ITrack sel = tb.getSelectedTrack ();
        padGrid.light (36, sel != null && sel.isActivated () ? BeatstepColors.BEATSTEP_BUTTON_STATE_RED : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (37, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (38, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (39, BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (40, BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (41, BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (42, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (43, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
    }


    private void scrollTracksLeft ()
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        if (!tb.canScrollTracksUp ())
            return;
        final ITrack sel = tb.getSelectedTrack ();
        final int index = sel == null ? 0 : sel.getIndex () - 1;
        tb.scrollTracksPageUp ();
        final int newSel = index == -1 || sel == null ? 7 : sel.getIndex ();
        this.surface.scheduleTask ( () -> this.selectTrack (newSel), 100);
    }


    private void scrollTracksRight ()
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        if (!tb.canScrollTracksDown ())
            return;
        final ITrack sel = tb.getSelectedTrack ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        tb.scrollTracksPageDown ();
        final int newSel = index == 8 || sel == null ? 0 : sel.getIndex ();
        this.surface.scheduleTask ( () -> this.selectTrack (newSel), 100);
    }

}