// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.view;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.ISessionAlternative;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The Scene view.
 *
 * @author Jürgen Moßgraber
 */
public class SceneView extends BaseView implements ISessionAlternative
{
    private boolean wasAlternateInteractionUsed = false;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public SceneView (final MaschineControlSurface surface, final IModel model)
    {
        super ("Scene", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        super.onGridNote (note, velocity);

        if (this.surface.isShiftPressed ())
            this.wasAlternateInteractionUsed = true;
    }


    /** {@inheritDoc} */
    @Override
    public void executeFunction (final int padIndex, final ButtonEvent buttonEvent)
    {
        final boolean isDown = buttonEvent == ButtonEvent.DOWN;

        final IScene scene = this.model.getSceneBank ().getItem (padIndex);

        if (isDown)
        {
            // Stop all clips
            if (this.isButtonCombination (ButtonID.SCENE1))
            {
                this.model.getTrackBank ().stop (this.surface.isShiftPressed ());
                return;
            }

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

            if (this.isButtonCombination (ButtonID.SELECT))
            {
                scene.select ();
                return;
            }

            if (this.surface.getConfiguration ().isSelectClipOnLaunch ())
                scene.select ();
        }

        scene.launch (isDown, this.surface.isShiftPressed ());
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        final ISceneBank sceneBank = this.model.getSceneBank ();
        for (int i = 0; i < 16; i++)
        {
            final IScene item = sceneBank.getItem (i);
            final int x = i % 4;
            final int y = 3 - i / 4;
            if (item.doesExist ())
            {
                if (item.isSelected ())
                    padGrid.lightEx (x, y, MaschineColorManager.COLOR_WHITE);
                else
                    padGrid.lightEx (x, y, DAWColor.getColorID (item.getColor ()));
            }
            else
                padGrid.lightEx (x, y, AbstractFeatureGroup.BUTTON_COLOR_OFF);
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean wasAlternateInteractionUsed ()
    {
        return this.wasAlternateInteractionUsed;
    }


    /** {@inheritDoc} */
    @Override
    public void setAlternateInteractionUsed (final boolean wasUsed)
    {
        this.wasAlternateInteractionUsed = wasUsed;
    }
}