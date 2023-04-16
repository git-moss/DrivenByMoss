// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.trigger.clip.StartSceneCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Command to start the currently selected scene.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamStartSceneCommand extends StartSceneCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    private static final Views [] SEQUENCER_VIEWS =
    {
        Views.SEQUENCER,
        Views.POLY_SEQUENCER,
        Views.RAINDROPS,
        Views.DRUM,
        Views.DRUM4,
        Views.DRUM8,
        null,
        null
    };

    private static final Views [] PLAY_VIEWS      =
    {
        Views.PLAY,
        Views.CHORDS,
        Views.PIANO,
        Views.DRUM64,
        null,
        null,
        null,
        null
    };


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param index The index of the scene in the page
     */
    public MaschineJamStartSceneCommand (final IModel model, final MaschineJamControlSurface surface, final int index)
    {
        super (model, surface, index);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();

        if (this.surface.isSelectPressed ())
        {
            final IScene scene = this.getScene ();
            if (scene.doesExist ())
                scene.select ();
            return;
        }

        if (this.surface.isPressed (ButtonID.SEQUENCER))
        {
            this.surface.setTriggerConsumed (ButtonID.SEQUENCER);
            if (SEQUENCER_VIEWS[this.index] != null)
            {
                this.activatePreferredView (SEQUENCER_VIEWS[this.index]);
                this.mvHelper.delayDisplay ( () -> viewManager.getActive ().getName ());
            }
            return;
        }

        if (this.surface.isPressed (ButtonID.NOTE))
        {
            this.surface.setTriggerConsumed (ButtonID.NOTE);
            if (PLAY_VIEWS[this.index] != null)
            {
                this.activatePreferredView (PLAY_VIEWS[this.index]);
                this.mvHelper.delayDisplay ( () -> viewManager.getActive ().getName ());
            }
            return;
        }

        super.executeNormal (event);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.LONG)
            return;

        final IScene scene = this.getScene ();
        if (scene.doesExist ())
            scene.launch (event == ButtonEvent.DOWN, true);

        if (event == ButtonEvent.DOWN)
            this.surface.setTriggerConsumed (ButtonID.SHIFT);
    }


    /**
     * Get the index of the color to use for current state.
     *
     * @return The index of the button color
     */
    public int getButtonColor ()
    {
        final MaschineColorManager colorManager = (MaschineColorManager) this.model.getColorManager ();

        if (this.surface.isPressed (ButtonID.SEQUENCER))
        {
            if (SEQUENCER_VIEWS[this.index] == null)
                return colorManager.getColorIndex (ColorManager.BUTTON_STATE_OFF);
            return this.surface.getViewManager ().isActive (SEQUENCER_VIEWS[this.index]) ? MaschineColorManager.COLOR_GREEN : MaschineColorManager.COLOR_GREY;
        }

        if (this.surface.isPressed (ButtonID.NOTE))
        {
            if (PLAY_VIEWS[this.index] == null)
                return colorManager.getColorIndex (ColorManager.BUTTON_STATE_OFF);
            return this.surface.getViewManager ().isActive (PLAY_VIEWS[this.index]) ? MaschineColorManager.COLOR_BLUE : MaschineColorManager.COLOR_GREY;
        }

        final IScene scene = this.getScene ();
        if (scene.doesExist ())
            return colorManager.dimOrHighlightColor (scene.getColor (), scene.isSelected ());
        return 0;
    }
}
