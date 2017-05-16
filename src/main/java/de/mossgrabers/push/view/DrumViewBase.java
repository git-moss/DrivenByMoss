// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.Pair;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;

import java.util.HashMap;
import java.util.Map;


/**
 * The base class for drum views.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class DrumViewBase extends AbstractDrumView<PushControlSurface, PushConfiguration>
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
    public DrumViewBase (final String name, final PushControlSurface surface, final Model model, final int numSequencerLines, final int numPlayLines)
    {
        super (name, surface, model, numSequencerLines, numPlayLines);
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        if (buttonID == PushControlSurface.PUSH_BUTTON_REPEAT)
            return false;
        return !this.surface.getConfiguration ().isPush2 () || buttonID != PushControlSurface.PUSH_BUTTON_USER_MODE;
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
    public void updateButtons ()
    {
        final int octave = this.scales.getDrumOctave ();
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, octave < Scales.DRUM_OCTAVE_UPPER ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, octave > Scales.DRUM_OCTAVE_LOWER ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int yellow = isPush2 ? PushColors.PUSH2_COLOR_SCENE_YELLOW : PushColors.PUSH1_COLOR_SCENE_YELLOW;
        final int green = isPush2 ? PushColors.PUSH2_COLOR_SCENE_GREEN : PushColors.PUSH1_COLOR_SCENE_GREEN;

        if (this.surface.isShiftPressed ())
        {
            final int red = isPush2 ? PushColors.PUSH2_COLOR_SCENE_RED : PushColors.PUSH1_COLOR_SCENE_RED;
            final int orange = isPush2 ? PushColors.PUSH2_COLOR_SCENE_ORANGE : PushColors.PUSH1_COLOR_SCENE_ORANGE;
            final ViewManager viewManager = this.surface.getViewManager ();
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE5, viewManager.isActiveView (Views.VIEW_DRUM64) ? red : orange);
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE6, viewManager.isActiveView (Views.VIEW_DRUM8) ? red : orange);
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE7, viewManager.isActiveView (Views.VIEW_DRUM4) ? red : orange);
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE8, viewManager.isActiveView (Views.VIEW_DRUM) ? red : orange);

            this.updateLowerSceneButtons ();
            return;
        }

        for (int i = PushControlSurface.PUSH_BUTTON_SCENE1; i <= PushControlSurface.PUSH_BUTTON_SCENE8; i++)
            this.surface.updateButton (i, i == PushControlSurface.PUSH_BUTTON_SCENE1 + this.selectedIndex ? yellow : green);
    }


    /**
     * Update the lower scene button LEDs.
     */
    protected void updateLowerSceneButtons ()
    {
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int off = isPush2 ? PushColors.PUSH2_COLOR2_BLACK : PushColors.PUSH1_COLOR2_BLACK;
        for (int i = 0; i < 4; i++)
            this.surface.updateButton (this.surface.getSceneButton (i), off);
    }


    /**
     * Get the drum modes.
     *
     * @return The drum modes
     */
    public static Map<Integer, Pair<Integer, String>> getDrumModes ()
    {
        return DRUM_MODES;
    }
}
