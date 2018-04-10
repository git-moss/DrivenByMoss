// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;


/**
 * The base class for drum views.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class DrumViewBase extends AbstractDrumView<PushControlSurface, PushConfiguration>
{
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
    public DrumViewBase (final String name, final PushControlSurface surface, final IModel model, final int numSequencerLines, final int numPlayLines)
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

        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedTrack ();
        if (selectedTrack != null)
            this.onLowerScene (index);
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
        if (this.surface.isShiftPressed ())
        {
            final int off = isPush2 ? PushColors.PUSH2_COLOR2_BLACK : PushColors.PUSH1_COLOR2_BLACK;
            for (int i = 4; i < 8; i++)
                this.surface.updateButton (this.surface.getSceneButton (i), off);
            this.updateLowerSceneButtons ();
            return;
        }

        final int yellow = isPush2 ? PushColors.PUSH2_COLOR_SCENE_YELLOW : PushColors.PUSH1_COLOR_SCENE_YELLOW;
        final int green = isPush2 ? PushColors.PUSH2_COLOR_SCENE_GREEN : PushColors.PUSH1_COLOR_SCENE_GREEN;
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
}
