// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;


/**
 * The scene play view.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScenePlayView<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C>
{
    /** The color for a selected scene. */
    public static final String COLOR_SELECTED_PLAY_SCENE = "COLOR_SELECTED_PLAY_SCENE";

    private final ISceneBank   sceneBank;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ScenePlayView (final S surface, final IModel model)
    {
        super (Views.NAME_SCENE_PLAY, surface, model);

        final IPadGrid padGrid = this.surface.getPadGrid ();
        this.sceneBank = model.getSceneBank (padGrid.getRows () * padGrid.getCols ());
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final int rows = padGrid.getRows ();
        final int cols = padGrid.getCols ();

        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                final int pad = row * cols + col;
                final IScene scene = this.sceneBank.getItem (pad);
                final String colorID;
                if (scene.isSelected ())
                    colorID = COLOR_SELECTED_PLAY_SCENE;
                else
                    colorID = scene.doesExist () ? DAWColor.getColorID (scene.getColor ()) : IPadGrid.GRID_OFF;
                padGrid.lightEx (col, rows - row - 1, colorID);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int sceneIndex = note - 36;
        final IScene scene = this.sceneBank.getItem (sceneIndex);

        if (this.handleButtonCombinations (scene))
            return;

        scene.launch ();
        scene.select ();
    }


    /**
     * Handle buttons combinations on the grid, e.g. delete, duplicate.
     *
     * @param scene The scene which was pressed
     * @return True if handled
     */
    protected boolean handleButtonCombinations (final IScene scene)
    {
        if (!scene.doesExist ())
            return true;

        if (this.isButtonCombination (ButtonID.DUPLICATE))
        {
            scene.duplicate ();
            return true;
        }

        if (this.isButtonCombination (ButtonID.DELETE))
        {
            scene.remove ();
            return true;
        }

        return false;
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