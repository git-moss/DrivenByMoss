// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ITrack;
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
        if (this.handleButtonCombinations (event))
            return;

        final MCUConfiguration configuration = this.surface.getConfiguration ();

        final ITrackBank trackBank = this.getTrackBank ();
        if (event == ButtonEvent.UP)
        {
            final ITrack track = trackBank.getItem (this.channel);
            if (!track.isSelected ())
            {
                track.select ();
                return;
            }

            if (configuration.isTrackNavigationFlat ())
            {
                if (track.isGroup ())
                    track.toggleGroupExpanded ();
            }
            else
                track.enter ();
        }
        else if (event == ButtonEvent.LONG && !configuration.isTrackNavigationFlat ())
        {
            trackBank.selectParent ();
            for (int i = 0; i < 8; i++)
                this.surface.setTriggerConsumed (ButtonID.get (ButtonID.ROW_SELECT_1, i));
        }
    }


    private boolean handleButtonCombinations (final ButtonEvent event)
    {
        // Select Send channels if Send button is pressed
        if (this.surface.isPressed (ButtonID.SENDS))
        {
            if (event != ButtonEvent.DOWN)
                return true;

            final ITextDisplay display = this.surface.getTextDisplay ();
            final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
            if (effectTrackBank != null && effectTrackBank.getItem (this.channel).doesExist ())
            {
                this.surface.getModeManager ().setActive (Modes.get (Modes.SEND1, this.index));
                display.notify ("Send channel " + (this.channel + 1) + " selected.");
            }
            else
                display.notify ("Send channel " + (this.channel + 1) + " does not exist.");
            this.surface.setTriggerConsumed (ButtonID.SENDS);
            return true;
        }

        // Execute stop if Select is pressed
        if (this.surface.isSelectPressed ())
        {
            if (event == ButtonEvent.DOWN)
                this.getTrackBank ().getItem (this.channel).stop ();
            return true;
        }

        if (this.surface.getConfiguration ().hasOnly1Fader ())
        {
            if (event != ButtonEvent.DOWN)
                return true;

            final ModeManager modeManager = this.surface.getModeManager ();

            // Select marker if marker mode is active
            if (modeManager.isActive (Modes.MARKERS))
            {
                this.model.getMarkerBank ().getItem (this.channel).select ();
                return true;
            }

            // Select parameter if device mode is active
            if (modeManager.isActive (Modes.DEVICE_PARAMS))
            {
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                if (cursorDevice.doesExist ())
                    cursorDevice.getParameterBank ().getItem (this.channel).select ();
                return true;
            }
        }

        return false;
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
}
