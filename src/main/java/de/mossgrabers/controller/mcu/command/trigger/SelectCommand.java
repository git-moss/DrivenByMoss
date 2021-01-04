// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.command.trigger;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * A select track command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    private final boolean useFxBank;

    protected final int   index;
    protected final int   channel;


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

        final MCUConfiguration configuration = this.surface.getConfiguration ();
        this.useFxBank = configuration.shouldPinFXTracksToLastController () && this.surface.getSurfaceID () == configuration.getNumMCUDevices () - 1;

        this.index = index;
        this.channel = this.useFxBank ? this.index : this.surface.getExtenderOffset () + this.index;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ITextDisplay display = this.surface.getTextDisplay ();
        final ModeManager modeManager = this.surface.getModeManager ();

        // Select Send channels if Send button is pressed
        if (this.surface.isPressed (ButtonID.SENDS))
        {
            final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
            if (effectTrackBank != null && effectTrackBank.getItem (this.channel).doesExist ())
            {
                modeManager.setActive (Modes.get (Modes.SEND1, this.index));
                display.notify ("Send channel " + (this.channel + 1) + " selected.");
            }
            else
                display.notify ("Send channel " + (this.channel + 1) + " does not exist.");
            this.surface.setTriggerConsumed (ButtonID.SENDS);
            return;
        }

        // Execute stop if Select is pressed
        if (this.surface.isSelectPressed ())
        {
            this.getTrackBank ().getItem (this.channel).stop ();
            return;
        }

        if (this.surface.getConfiguration ().hasOnly1Fader ())
        {
            // Select marker if marker mode is active
            if (modeManager.isActive (Modes.MARKERS))
            {
                this.model.getMarkerBank ().getItem (this.channel).select ();
                return;
            }

            // Select parameter if device mode is active
            if (modeManager.isActive (Modes.DEVICE_PARAMS))
            {
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                if (cursorDevice.doesExist ())
                    cursorDevice.getParameterBank ().getItem (this.channel).select ();
                return;
            }
        }

        this.getTrackBank ().getItem (this.channel).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || this.index >= 8)
            return;
        this.surface.getDisplay ().notify (AbstractConfiguration.getNewClipLengthValue (this.index));
        this.surface.getConfiguration ().setNewClipLength (this.index);
    }


    protected ITrackBank getTrackBank ()
    {
        if (this.useFxBank)
        {
            final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
            if (effectTrackBank != null)
                return effectTrackBank;
        }
        return this.model.getCurrentTrackBank ();
    }


    protected int getExtenderOffset ()
    {
        return this.useFxBank ? 0 : this.surface.getExtenderOffset ();
    }
}
