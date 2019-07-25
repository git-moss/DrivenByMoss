// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.view;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractRaindropsView;


/**
 * The Raindrops Sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RaindropsView extends AbstractRaindropsView<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public RaindropsView (final APCControlSurface surface, final IModel model)
    {
        super ("Raindrops", surface, model, surface.isMkII ());
        this.numDisplayRows = 5;
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        this.ongoingResolutionChange = true;

        switch (index)
        {
            case 0:
                this.scales.nextScale ();
                this.notifyScale ();
                break;

            case 1:
                this.scales.prevScale ();
                this.notifyScale ();
                break;

            case 2:
                this.scales.toggleChromatic ();
                final boolean isChromatic = this.scales.isChromatic ();
                this.surface.getConfiguration ().setScaleInKey (!isChromatic);
                this.surface.getDisplay ().notify (isChromatic ? "Chromatic" : "In Key");
                break;

            case 3:
                this.onOctaveUp (event);
                break;

            case 4:
                this.onOctaveDown (event);
                break;

            default:
                // Not used
                break;
        }
        this.updateNoteMapping ();

        this.ongoingResolutionChange = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        this.surface.updateTrigger (APCControlSurface.APC_BUTTON_SCENE_LAUNCH_1, ColorManager.BUTTON_STATE_ON);
        this.surface.updateTrigger (APCControlSurface.APC_BUTTON_SCENE_LAUNCH_2, ColorManager.BUTTON_STATE_ON);
        this.surface.updateTrigger (APCControlSurface.APC_BUTTON_SCENE_LAUNCH_3, ColorManager.BUTTON_STATE_OFF);
        this.surface.updateTrigger (APCControlSurface.APC_BUTTON_SCENE_LAUNCH_4, ColorManager.BUTTON_STATE_ON);
        this.surface.updateTrigger (APCControlSurface.APC_BUTTON_SCENE_LAUNCH_5, ColorManager.BUTTON_STATE_ON);
    }


    private void notifyScale ()
    {
        final String name = this.scales.getScale ().getName ();
        this.surface.getConfiguration ().setScale (name);
        this.surface.getDisplay ().notify (name);
    }
}