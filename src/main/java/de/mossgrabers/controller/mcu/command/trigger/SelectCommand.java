// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.command.trigger;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mcu.mode.Modes;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * A select track command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    protected int index;
    protected int channel;


    /**
     * Constructor.
     *
     * @param index The channel index
     * @param model The model
     * @param surface The surface
     */
    public SelectCommand (final int index, final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);
        this.index = index;
        this.channel = this.surface.getExtenderOffset () + this.index;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.index == 8)
        {
            this.model.getMasterTrack ().select ();
            return;
        }

        final Display display = this.surface.getDisplay ();

        // Select Send channels when Send button is additionally pressed
        if (this.surface.isPressed (MCUControlSurface.MCU_MODE_SENDS))
        {
            final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
            if (effectTrackBank != null && effectTrackBank.getItem (this.channel).doesExist ())
            {
                this.surface.getModeManager ().setActiveMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + this.index));
                display.notify ("Send channel " + (this.channel + 1) + " selected.");
            }
            else
                display.notify ("Send channel " + (this.channel + 1) + " does not exist.");
            this.surface.setButtonConsumed (MCUControlSurface.MCU_MODE_SENDS);
            return;
        }

        if (this.surface.isShiftPressed ())
        {
            final MCUConfiguration configuration = this.surface.getConfiguration ();
            configuration.setNewClipLength (this.index);
            display.notify ("New clip length: " + AbstractConfiguration.NEW_CLIP_LENGTH_VALUES[configuration.getNewClipLength ()]);
            return;
        }

        if (this.surface.isSelectPressed ())
        {
            this.model.getCurrentTrackBank ().getItem (this.channel).stop ();
            return;
        }

        this.model.getCurrentTrackBank ().getItem (this.channel).select ();
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
