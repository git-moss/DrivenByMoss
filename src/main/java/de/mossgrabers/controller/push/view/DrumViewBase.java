// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView;
import de.mossgrabers.framework.view.AbstractSequencerView;


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
            return this.model.getHost ().hasRepeat ();
        return !this.surface.getConfiguration ().isPush2 () || buttonID != PushControlSurface.PUSH_BUTTON_USER_MODE;
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || !this.model.canSelectedTrackHoldNotes ())
            return;

        if (this.surface.isShiftPressed ())
        {
            final ITrack selectedTrack = this.model.getSelectedTrack ();
            if (selectedTrack != null)
                this.onLowerScene (index);
            return;
        }

        super.onScene (index, event);
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
        this.surface.updateTrigger (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, this.scales.canScrollDrumOctaveUp () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        this.surface.updateTrigger (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, this.scales.canScrollDrumOctaveDown () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final ColorManager colorManager = this.model.getColorManager ();

        if (this.surface.isShiftPressed ())
        {
            final int colorOff = colorManager.getColor (AbstractSequencerView.COLOR_RESOLUTION_OFF);
            for (int i = 4; i < 8; i++)
                this.surface.updateTrigger (this.surface.getSceneTrigger (i), colorOff);
            this.updateLowerSceneButtons ();
            return;
        }

        final int colorResolution = colorManager.getColor (AbstractSequencerView.COLOR_RESOLUTION);
        final int colorSelectedResolution = colorManager.getColor (AbstractSequencerView.COLOR_RESOLUTION_SELECTED);
        for (int i = PushControlSurface.PUSH_BUTTON_SCENE1; i <= PushControlSurface.PUSH_BUTTON_SCENE8; i++)
            this.surface.updateTrigger (i, i == PushControlSurface.PUSH_BUTTON_SCENE1 + this.selectedIndex ? colorSelectedResolution : colorResolution);
    }


    /**
     * Update the lower scene button LEDs.
     */
    protected void updateLowerSceneButtons ()
    {
        final int colorOff = this.model.getColorManager ().getColor (AbstractSequencerView.COLOR_RESOLUTION_OFF);
        for (int i = 0; i < 4; i++)
            this.surface.updateTrigger (this.surface.getSceneTrigger (i), colorOff);
    }
}
