// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.command.trigger;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * A select track command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectCommand extends AbstractTriggerCommand<APCControlSurface, APCConfiguration>
{
    private int index;


    /**
     * Constructor.
     *
     * @param index The channel index
     * @param model The model
     * @param surface The surface
     */
    public SelectCommand (final int index, final IModel model, final APCControlSurface surface)
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

        if (this.surface.isPressed (APCControlSurface.APC_BUTTON_SEND_A))
        {
            this.surface.getModeManager ().setActiveMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + this.index));
            this.surface.getDisplay ().notify ("Send " + (this.index + 1));
            return;
        }

        this.model.getCurrentTrackBank ().getItem (this.index).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.surface.getDisplay ().notify (AbstractConfiguration.getNewClipLengthValue (this.index));
        this.surface.getConfiguration ().setNewClipLength (this.index);
    }
}
