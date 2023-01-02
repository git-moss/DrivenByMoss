// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.view;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractRaindropsView;


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
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN || !this.isActive ())
            return;

        this.ongoingResolutionChange = true;

        switch (buttonID)
        {
            case SCENE1:
                this.scales.nextScale ();
                this.notifyScale ();
                break;

            case SCENE2:
                this.scales.prevScale ();
                this.notifyScale ();
                break;

            case SCENE3:
                this.scales.toggleChromatic ();
                final boolean isChromatic = this.scales.isChromatic ();
                this.surface.getConfiguration ().setScaleInKey (!isChromatic);
                this.surface.getDisplay ().notify (isChromatic ? "Chromatic" : "In Key");
                break;

            case SCENE4:
                this.onOctaveUp (event);
                break;

            case SCENE5:
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
    public String getButtonColorID (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.SCENE3)
            return ColorManager.BUTTON_STATE_OFF;
        return this.isActive () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
    }


    private void notifyScale ()
    {
        final String name = this.scales.getScale ().getName ();
        this.surface.getConfiguration ().setScale (name);
        this.surface.getDisplay ().notify (name);
    }
}