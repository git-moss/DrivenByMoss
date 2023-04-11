// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractRaindropsView;


/**
 * The Raindrops Sequencer view.
 *
 * @author Jürgen Moßgraber
 */
public class RaindropsView extends AbstractRaindropsView<MaschineJamControlSurface, MaschineJamConfiguration> implements IViewNavigation
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public RaindropsView (final MaschineJamControlSurface surface, final IModel model)
    {
        super (Views.NAME_RAINDROPS, surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        switch (buttonID)
        {
            case ARROW_RIGHT:
            case ARROW_UP:
                this.onOctaveUp (event);
                break;

            case ARROW_LEFT:
            case ARROW_DOWN:
                this.onOctaveDown (event);
                break;

            default:
                super.onButton (buttonID, event, velocity);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScroll (final Direction direction)
    {
        switch (direction)
        {
            case UP:
            case LEFT:
                return this.isOctaveUpButtonOn ();
            case DOWN:
            case RIGHT:
                return this.isOctaveDownButtonOn ();
        }
        return false;
    }
}