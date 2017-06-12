// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;
import de.mossgrabers.mcu.mode.Modes;


/**
 * A select track command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    private int index;


    /**
     * Constructor.
     *
     * @param index The channel index
     * @param model The model
     * @param surface The surface
     */
    public SelectCommand (final int index, final Model model, final MCUControlSurface surface)
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

        if (this.index < 8 && this.surface.isPressed (MCUControlSurface.MCU_MODE_SENDS))
        {
            if (this.model.getEffectTrackBank ().getTrack (this.index).doesExist ())
            {
                this.surface.getModeManager ().setActiveMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + this.index));
                this.surface.getDisplay ().notify ("Send channel " + (this.index + 1) + " selected.");
            }
            else
                this.surface.getDisplay ().notify ("Send channel " + (this.index + 1) + " does not exist.");
            this.surface.setButtonConsumed (MCUControlSurface.MCU_MODE_SENDS);
            return;
        }

        if (this.index < 8)
        {
            final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
            tb.select (this.index);
            tb.makeVisible (this.index);
        }
        else
            this.model.getMasterTrack ().select ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || this.index >= 8)
            return;
        this.surface.getDisplay ().notify (AbstractConfiguration.NEW_CLIP_LENGTH_VALUES[this.index]);
        this.surface.getConfiguration ().setNewClipLength (this.index);
    }
}
