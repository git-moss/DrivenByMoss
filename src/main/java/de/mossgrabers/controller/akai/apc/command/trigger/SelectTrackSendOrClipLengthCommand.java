// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.command.trigger;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Selects a track. Sets the new clip length if shifted. If "Send A" button is pressed selects a
 * send.
 *
 * @author Jürgen Moßgraber
 */
public class SelectTrackSendOrClipLengthCommand extends AbstractTriggerCommand<APCControlSurface, APCConfiguration>
{
    private final int index;


    /**
     * Constructor.
     *
     * @param index The channel / clip length / send index
     * @param model The model
     * @param surface The surface
     */
    public SelectTrackSendOrClipLengthCommand (final int index, final IModel model, final APCControlSurface surface)
    {
        super (model, surface);

        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        if (this.surface.isPressed (ButtonID.SEND1))
        {
            final Modes sendModeId = Modes.get (Modes.SEND1, this.index);
            this.surface.setTriggerConsumed (ButtonID.SEND1);

            // Keep the send step selection in sync
            if (this.surface.isMkII ())
                ((ModeMultiSelectCommand<?, ?>) this.surface.getButton (ButtonID.SEND1).getCommand ()).activateMode (sendModeId);
            else
                modeManager.setActive (sendModeId);

            // Display the sends name
            String modeName = "Send " + (this.index + 1) + ": ";
            final ITrackBank trackBank = this.model.getTrackBank ();
            Optional<ITrack> selectedTrack = trackBank.getSelectedItem ();
            if (selectedTrack.isEmpty ())
            {
                final ITrack item = trackBank.getItem (0);
                selectedTrack = item.doesExist () ? Optional.of (item) : Optional.empty ();
            }
            if (selectedTrack.isEmpty ())
                modeName += "-";
            else
                modeName += selectedTrack.get ().getSendBank ().getItem (this.index).getName ();
            this.surface.getDisplay ().notify (modeName);
            return;
        }

        if (this.surface.isMkII () && this.surface.isPressed (ButtonID.SEND2))
        {
            this.surface.setTriggerConsumed (ButtonID.SEND2);
            modeManager.get (Modes.USER).selectItemPage (this.index);
            return;
        }

        this.model.getCurrentTrackBank ().getItem (this.index).selectOrExpandGroup ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        this.surface.getDisplay ().notify (AbstractConfiguration.getNewClipLengthValue (this.index));
        this.surface.getConfiguration ().setNewClipLength (this.index);
    }
}
