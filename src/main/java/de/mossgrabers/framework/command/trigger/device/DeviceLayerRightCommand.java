// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.device;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


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
        final IChannelBank<ILayer> bank = cd.getLayerBank ();
        final Optional<ILayer> sel = bank.getSelectedItem ();
        if (!cd.hasLayers () || sel.isEmpty ())
            cd.selectNext ();
        else
            bank.selectNextItem ();
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

        final IChannelBank<ILayer> bank = cd.getLayerBank ();
        final Optional<ILayer> layer = bank.getSelectedItem ();
        if (layer.isEmpty ())
            bank.getItem (0).select ();
        else
            layer.get ().enter ();
    }


    /**
     * Check if the command can be executed.
     *
     * @return True if it can
     */
    public boolean canExecute ()
    {
        if (this.surface.isShiftPressed ())
            return true;

        final ICursorDevice cd = this.model.getCursorDevice ();
        final IChannelBank<ILayer> bank = cd.getLayerBank ();
        final Optional<ILayer> layer = bank.getSelectedItem ();
        return cd.hasLayers () && layer.isPresent () ? bank.canScrollForwards () : cd.canSelectNext ();
    }
}
