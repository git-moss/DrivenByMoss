// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.command.trigger;

import de.mossgrabers.apc.APCConfiguration;
import de.mossgrabers.apc.controller.APCControlSurface;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.data.ChannelData;


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
    public DeviceRightCommand (final Model model, final APCControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        final ChannelData sel = cd.getSelectedLayer ();
        if (!cd.hasLayers () || sel == null)
            cd.selectNext ();
        else
        {
            final int index = sel.getIndex () + 1;
            cd.selectLayer (index > 7 ? 7 : index);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        // Enter layer
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (!cd.hasLayers ())
            return;

        final ChannelData layer = cd.getSelectedLayerOrDrumPad ();
        if (layer == null)
            cd.selectLayerOrDrumPad (0);
        else
        {
            final ChannelData dl = cd.getSelectedLayer ();
            if (dl != null)
            {
                final int index = dl.getIndex ();
                cd.enterLayer (index);
                cd.selectFirstDeviceInLayer (index);
            }
        }
    }
}
