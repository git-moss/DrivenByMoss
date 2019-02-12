// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.Views;


/**
 * The play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractPlayView<PushControlSurface, PushConfiguration> implements SceneView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final PushControlSurface surface, final IModel model)
    {
        this (Views.VIEW_NAME_PLAY, surface, model);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final String name, final PushControlSurface surface, final IModel model)
    {
        super (name, surface, model, true);

        final Configuration configuration = this.surface.getConfiguration ();
        configuration.addSettingObserver (AbstractConfiguration.ACTIVATE_FIXED_ACCENT, this::initMaxVelocity);
        configuration.addSettingObserver (AbstractConfiguration.FIXED_ACCENT_VALUE, this::initMaxVelocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();
        this.initMaxVelocity ();
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
    public void updateButtons ()
    {
        final int octave = this.scales.getOctave ();
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, octave < Scales.OCTAVE_RANGE ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, octave > -Scales.OCTAVE_RANGE ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int sceneIndex, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final IScene scene = this.model.getCurrentTrackBank ().getSceneBank ().getItem (sceneIndex);
        scene.select ();
        scene.launch ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final int colorScene = colorManager.getColor (AbstractSessionView.COLOR_SCENE);
        final int colorSceneSelected = colorManager.getColor (AbstractSessionView.COLOR_SELECTED_SCENE);
        final int colorSceneOff = colorManager.getColor (AbstractSessionView.COLOR_SCENE_OFF);

        final ISceneBank sceneBank = this.model.getSceneBank ();
        for (int i = 0; i < 8; i++)
        {
            final IScene scene = sceneBank.getItem (7 - i);
            final int color = scene.doesExist () ? scene.isSelected () ? colorSceneSelected : colorScene : colorSceneOff;
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE1 + i, color);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (this.surface.isDeletePressed ())
        {
            this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
            this.model.getNoteClip (8, 128).clearRow (this.keyManager.map (note));
            return;
        }
        super.onGridNote (note, velocity);
    }
}