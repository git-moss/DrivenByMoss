// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.command.trigger;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The device right command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceRightCommand extends AbstractTriggerCommand<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public DeviceRightCommand (final IModel model, final APCControlSurface surface)
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
            bank.getItem (index > 7 ? 7 : index).select ();
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
