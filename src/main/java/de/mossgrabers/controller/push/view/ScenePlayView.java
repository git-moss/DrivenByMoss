// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;


/**
 * The scene play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScenePlayView extends AbstractView<PushControlSurface, PushConfiguration> implements SceneView
{
    private ITrackBank trackBank;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ScenePlayView (final PushControlSurface surface, final IModel model)
    {
        super ("Scene Play", surface, model);
        this.trackBank = model.createSceneViewTrackBank (8, 64);
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        switch (buttonID)
        {
            case PushControlSurface.PUSH_BUTTON_REPEAT:
                return this.model.getHost ().hasRepeat ();

            case PushControlSurface.PUSH_BUTTON_OCTAVE_UP:
            case PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN:
                return false;

            default:
                return !this.surface.getConfiguration ().isPush2 () || buttonID != PushControlSurface.PUSH_BUTTON_USER_MODE;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ISceneBank sceneBank = this.trackBank.getSceneBank ();
        final PadGrid padGrid = this.surface.getPadGrid ();
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        for (int i = 0; i < 64; i++)
        {
            final IScene scene = sceneBank.getItem (i);
            if (scene.isSelected ())
                padGrid.light (36 + i, isPush2 ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH1_COLOR2_WHITE);
            else
            {
                final String color = scene.doesExist () ? DAWColors.getColorIndex (scene.getColor ()) : PadGrid.GRID_OFF;
                padGrid.light (36 + i, color);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final IScene scene = this.trackBank.getSceneBank ().getItem (note - 36);

        if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_DUPLICATE))
        {
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_DUPLICATE);
            scene.duplicate ();
            return;
        }

        scene.launch ();
        scene.select ();
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int scene, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final int colorOff = this.model.getColorManager ().getColor (AbstractSequencerView.COLOR_RESOLUTION_OFF);
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (this.surface.getSceneButton (i), colorOff);
    }
}