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
 * The device left command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceLeftCommand extends AbstractTriggerCommand<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public DeviceLeftCommand (final IModel model, final APCControlSurface surface)
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
        final IChannel layer = bank.getSelectedItem ();
        if (!cd.hasLayers () || layer == null)
            cd.selectPrevious ();
        else
            bank.getItem (layer.getIndex () == 0 ? 0 : layer.getIndex () - 1).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        // Exit layer
        final ICursorDevice cd = this.model.getCursorDevice ();
        final IChannelBank<?> bank = cd.getLayerOrDrumPadBank ();
        final IChannel layer = bank.getSelectedItem ();
        if (!cd.hasLayers () || layer == null)
        {
            if (cd.isNested ())
            {
                cd.selectParent ();
                cd.selectChannel ();
            }
        }
        else
            layer.setSelected (false);
    }
}
