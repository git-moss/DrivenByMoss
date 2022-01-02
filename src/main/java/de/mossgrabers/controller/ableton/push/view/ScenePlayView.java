// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.view.AbstractSequencerView;


/**
 * The scene play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScenePlayView extends AbstractView<PushControlSurface, PushConfiguration>
{
    private final ISceneBank sceneBank;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param sceneBank The scene bank to monitor
     */
    public ScenePlayView (final PushControlSurface surface, final IModel model, final ISceneBank sceneBank)
    {
        super ("Scene Play", surface, model);

        this.sceneBank = sceneBank;
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        for (int i = 0; i < 64; i++)
        {
            final IScene scene = this.sceneBank.getItem (i);
            if (scene.isSelected ())
                padGrid.light (36 + i, isPush2 ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH1_COLOR2_WHITE);
            else
            {
                final String color = scene.doesExist () ? DAWColor.getColorIndex (scene.getColor ()) : IPadGrid.GRID_OFF;
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

        final IScene scene = this.sceneBank.getItem (note - 36);

        if (this.isButtonCombination (ButtonID.DUPLICATE))
        {
            scene.duplicate ();
            return;
        }

        if (this.isButtonCombination (ButtonID.DELETE))
        {
            scene.remove ();
            return;
        }

        scene.launch ();
        scene.select ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (scene < 0 || scene >= 8)
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;

        return AbstractSequencerView.COLOR_RESOLUTION_OFF;
    }
}