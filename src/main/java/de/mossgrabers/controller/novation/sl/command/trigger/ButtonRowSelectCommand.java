// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.command.trigger;

import de.mossgrabers.controller.novation.sl.view.SLView;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to delegate the button pushes of a button row to the active mode.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ButtonRowSelectCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final int row;


    /**
     * Constructor.
     *
     * @param row The number of the button row
     * @param model The model
     * @param surface The surface
     */
    public ButtonRowSelectCommand (final int row, final IModel model, final S surface)
    {
        super (model, surface);

        this.row = row;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final IView view = this.surface.getViewManager ().getActive ();
        if (view == null)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();

        switch (this.row)
        {
            case 0:
                ((SLView) view).onButtonRow1Select ();
                break;

            case 1:
                modeManager.setActive (Modes.DEVICE_PARAMS);
                this.model.getHost ().showNotification ("Device Parameters");
                break;

            case 2:
                ((SLView) view).onButtonRow2Select ();
                break;

            case 3:
                modeManager.setActive (Modes.TRACK);
                this.model.getHost ().showNotification ("Track");
                break;

            case 4:
                break;

            case 5:
            case 6:
            case 7:
                modeManager.setActive (Modes.VOLUME);
                this.model.getHost ().showNotification ("Volume");
                break;

            default:
                // Intentionally empty
                break;
        }
    }
}
