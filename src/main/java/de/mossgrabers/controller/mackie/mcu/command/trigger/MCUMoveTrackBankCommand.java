// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.trigger.track.MoveTrackBankCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to move the window of the track bank by 1 or 8.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUMoveTrackBankCommand extends MoveTrackBankCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param moveBy1 If true the bank window moves by 1 otherwise by 8
     * @param moveLeft If true the bank window is moved left otherwise to the right
     */
    public MCUMoveTrackBankCommand (final IModel model, final MCUControlSurface surface, final boolean moveBy1, final boolean moveLeft)
    {
        super (model, surface, Modes.DEVICE_PARAMS, moveBy1, moveLeft);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final boolean isEqMode = modeManager.isActive (Modes.EQ_DEVICE_PARAMS);
        if (isEqMode || modeManager.isActive (Modes.INSTRUMENT_DEVICE_PARAMS))
        {
            final ISpecificDevice device = this.model.getSpecificDevice (isEqMode ? DeviceID.EQ : DeviceID.FIRST_INSTRUMENT);
            if (this.moveBy1)
            {
                this.handleBankMovement (device.getParameterBank ());
            }
            return;
        }

        super.executeNormal (event);
    }


    /** {@index} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || !this.surface.getConfiguration ().shouldPinFXTracksToLastController ())
            return;

        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            this.handleBankMovement (effectTrackBank);
    }
}
