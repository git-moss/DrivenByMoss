// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.view;

import de.mossgrabers.beatstep.BeatstepConfiguration;
import de.mossgrabers.beatstep.controller.BeatstepColors;
import de.mossgrabers.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.trigger.PlayCommand;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.TransportProxy;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;


/**
 * The Shift view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShiftView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private PlayCommand<BeatstepControlSurface, BeatstepConfiguration> playCommand;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public ShiftView (final BeatstepControlSurface surface, final Model model)
    {
        super ("Shift", surface, model);
        this.playCommand = new PlayCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final TransportProxy t = this.model.getTransport ();
        final PadGrid padGrid = this.surface.getPadGrid ();
        padGrid.light (36, t.isPlaying () ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (37, t.isRecording () ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (38, t.isLoop () ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (39, t.isMetronomeOn () ? BeatstepColors.BEATSTEP_BUTTON_STATE_PINK : BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (40, BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (41, BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (42, BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (43, BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (44, BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (45, BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (46, BeatstepColors.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (47, BeatstepColors.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (48, BeatstepColors.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (49, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (50, BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (51, BeatstepColors.BEATSTEP_BUTTON_STATE_PINK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        int viewIndex;
        View view;
        switch (note - 36)
        {
            // Play
            case 0:
                this.playCommand.executeNormal (ButtonEvent.DOWN);
                break;

            // Record
            case 1:
                this.model.getTransport ().record ();
                break;

            // Repeat
            case 2:
                this.model.getTransport ().toggleLoop ();
                break;

            // Click
            case 3:
                this.model.getTransport ().toggleMetronome ();
                break;

            // Tap Tempo
            case 4:
                this.model.getTransport ().tapTempo ();
                break;

            // Insert device before current
            case 5:
                this.model.getBrowser ().browseToInsertBeforeDevice ();
                this.activateBrowserView ();
                break;

            // Insert device after current
            case 6:
                this.model.getBrowser ().browseToInsertAfterDevice ();
                this.activateBrowserView ();
                break;

            // Open the browser
            case 7:
                this.model.getBrowser ().browseForPresets ();
                this.activateBrowserView ();
                break;

            // Toggle window of VSTs
            case 15:
                this.model.getCursorDevice ().toggleWindowOpen ();
                break;

            default:
                viewIndex = note - 44;
                if (viewIndex < 0 || viewIndex >= 6)
                    return;

                final ViewManager viewManager = this.surface.getViewManager ();
                final Integer viewId = viewIndex;
                viewManager.setPreviousView (viewId);
                view = viewManager.getView (viewId);
                this.surface.getDisplay ().notify (view.getName ());
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value)
    {
        // Knobs not used in Shift view
    }


    private void activateBrowserView ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        final Integer previousViewId = viewManager.getPreviousViewId ();
        viewManager.setActiveView (Views.VIEW_BROWSER);
        viewManager.setPreviousView (previousViewId);
    }
}