// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.controller.ni.maschine.jam.view.IViewNavigation;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IView;


/**
 * Support for the NI Maschine controller series.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamViewButtonCommand extends ViewButtonCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    private final Direction direction;


    /**
     * Constructor.
     *
     * @param buttonID The button which events to relay
     * @param model The model
     * @param surface The surface
     */
    public MaschineJamViewButtonCommand (final ButtonID buttonID, final IModel model, final MaschineJamControlSurface surface)
    {
        super (buttonID, model, surface);

        switch (buttonID)
        {
            case ARROW_LEFT:
                this.direction = Direction.LEFT;
                break;
            case ARROW_RIGHT:
                this.direction = Direction.RIGHT;
                break;
            case ARROW_UP:
                this.direction = Direction.UP;
                break;
            case ARROW_DOWN:
            default:
                this.direction = Direction.DOWN;
                break;
        }
    }


    /**
     * Can be scrolled?
     *
     * @return True if it can be scrolled
     */
    public boolean canScroll ()
    {
        final IView view = this.viewManager.getActive ();
        return view instanceof final IViewNavigation viewNavigation && viewNavigation.canScroll (this.direction);
    }
}
