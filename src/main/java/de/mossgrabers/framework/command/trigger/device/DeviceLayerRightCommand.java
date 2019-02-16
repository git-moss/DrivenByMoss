// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.device;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Selects the next layer or device. If shifted, enters the layers bank or a grouped device.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLayerRightCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public DeviceLayerRightCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ICursorDevice cd = this.model.getCursorDevice ();
        final IChannelBank<?> bank = cd.getLayerOrDrumPadBank ();
        final IChannel sel = bank.getSelectedItem ();
        if (!cd.hasLayers () || sel == null)
            cd.selectNext ();
        else
        {
            final int index = sel.getIndex () + 1;
            if (index < bank.getPageSize ())
                bank.getItem (index).select ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        // Enter layer
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.hasLayers ())
            return;

        final IChannelBank<?> bank = cd.getLayerOrDrumPadBank ();
        final IChannel layer = bank.getSelectedItem ();
        if (layer == null)
            bank.getItem (0).select ();
        else
            layer.enter ();
    }
}
