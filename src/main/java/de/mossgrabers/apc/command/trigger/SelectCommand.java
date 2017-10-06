// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.command.trigger;

import de.mossgrabers.apc.APCConfiguration;
import de.mossgrabers.apc.controller.APCControlSurface;
import de.mossgrabers.apc.mode.Modes;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;


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
    public SelectCommand (final int index, final Model model, final APCControlSurface surface)
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
            this.surface.getModeManager ().setActiveMode (Modes.MODE_SEND1.intValue() + this.index);
            this.surface.getDisplay ().notify ("Send " + (this.index + 1));
            return;
        }

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        tb.select (this.index);
        tb.makeVisible (this.index);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.surface.getDisplay ().notify (AbstractConfiguration.NEW_CLIP_LENGTH_VALUES[this.index]);
        this.surface.getConfiguration ().setNewClipLength (this.index);
    }
}
