// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.Pair;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadColors;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;

import java.util.HashMap;
import java.util.Map;


/**
 * The base class for drum views.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class DrumViewBase extends AbstractDrumView<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private static final Map<Integer, Pair<Integer, String>> DRUM_MODES = new HashMap<> ();
    static
    {
        DRUM_MODES.put (Integer.valueOf (0), new Pair<> (Views.VIEW_DRUM, "Drum 1"));
        DRUM_MODES.put (Integer.valueOf (1), new Pair<> (Views.VIEW_DRUM4, "Drum 4"));
        DRUM_MODES.put (Integer.valueOf (2), new Pair<> (Views.VIEW_DRUM8, "Drum 8"));
        DRUM_MODES.put (Integer.valueOf (3), new Pair<> (Views.VIEW_DRUM64, "Drum 64"));
    }

    protected int soundOffset;


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param numSequencerLines The number of rows to use for the sequencer
     * @param numPlayLines The number of rows to use for playing
     */
    public DrumViewBase (final String name, final LaunchpadControlSurface surface, final Model model, final int numSequencerLines, final int numPlayLines)
    {
        super (name, surface, model, numSequencerLines, numPlayLines);
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || !this.model.canSelectedTrackHoldNotes ())
            return;

        if (!this.surface.isShiftPressed ())
        {
            super.onScene (index, event);
            return;
        }

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData selectedTrack = tb.getSelectedTrack ();
        if (selectedTrack != null)
        {
            final ViewManager viewManager = this.surface.getViewManager ();
            if (index < 4)
            {
                final Pair<Integer, String> drumMode = DRUM_MODES.get (Integer.valueOf (index));
                final Integer viewID = drumMode.getKey ();
                viewManager.setPreferredView (selectedTrack.getPosition (), viewID);
                this.surface.getViewManager ().setActiveView (viewID);
                this.surface.getDisplay ().notify (drumMode.getValue (), true, true);
            }
            else
                this.onLowerScene (index);
        }
    }


    /**
     * Handle the functionality in sub-classes.
     *
     * @param index The scene index
     */
    protected void onLowerScene (final int index)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        if (this.surface.isShiftPressed ())
        {
            final ViewManager viewManager = this.surface.getViewManager ();
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, viewManager.isActiveView (Views.VIEW_DRUM) ? LaunchpadColors.LAUNCHPAD_COLOR_RED : LaunchpadColors.LAUNCHPAD_COLOR_AMBER);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, viewManager.isActiveView (Views.VIEW_DRUM4) ? LaunchpadColors.LAUNCHPAD_COLOR_RED : LaunchpadColors.LAUNCHPAD_COLOR_AMBER);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, viewManager.isActiveView (Views.VIEW_DRUM8) ? LaunchpadColors.LAUNCHPAD_COLOR_RED : LaunchpadColors.LAUNCHPAD_COLOR_AMBER);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, viewManager.isActiveView (Views.VIEW_DRUM64) ? LaunchpadColors.LAUNCHPAD_COLOR_RED : LaunchpadColors.LAUNCHPAD_COLOR_AMBER);

            this.updateLowerSceneButtons ();
            return;
        }

        for (int i = 0; i < 8; i++)
        {
            final int sceneButton = this.surface.getSceneButton (i);
            this.surface.setButton (sceneButton, i == 7 - this.selectedIndex ? LaunchpadColors.LAUNCHPAD_COLOR_YELLOW : LaunchpadColors.LAUNCHPAD_COLOR_GREEN);
        }
    }


    /**
     * Update the lower scene button LEDs.
     */
    protected void updateLowerSceneButtons ()
    {
        for (int i = 4; i < 8; i++)
            this.surface.setButton (this.surface.getSceneButton (i), LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
    }
}
