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
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to delegate the button pushes of a button row to the active mode.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class ButtonRowViewCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final int index;
    private final int row;


    /**
     * Constructor.
     *
     * @param row The number of the button row
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public ButtonRowViewCommand (final int row, final int index, final IModel model, final S surface)
    {
        super (model, surface);
        this.row = row;
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final IView view = this.surface.getViewManager ().getActive ();
        if (view == null)
            return;

        switch (this.row)
        {
            case 0:
                ((SLView) view).onButtonRow1 (this.index, event);
                break;
            case 1:
                ((SLView) view).onButtonRow2 (this.index, event);
                break;
            case 2:
                ((SLView) view).onButtonRow3 (this.index, event);
                break;
            case 3:
                ((SLView) view).onButtonRow4 (this.index, event);
                break;
            case 4:
                ((SLView) view).onButtonRow5 (this.index, event);
                break;
            default:
                // Intentionally empty
                break;
        }
    }
}
