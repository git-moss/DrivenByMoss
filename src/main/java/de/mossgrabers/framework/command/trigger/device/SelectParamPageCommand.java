// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.device;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Selects a parameter page.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class SelectParamPageCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final int index;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param index The index of the page
     */
    public SelectParamPageCommand (final IModel model, final S surface, final int index)
    {
        super (model, surface);

        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final IParameterPageBank parameterBank = this.model.getCursorDevice ().getParameterPageBank ();
        parameterBank.selectPage (this.index);
        this.mvHelper.notifySelectedDeviceAndParameterPage ();
    }
}
