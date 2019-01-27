// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.controller.mcu.mode.Modes;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to move the window of the track bank by 1 or 8.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MoveTrackBankCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final boolean moveLeft;
    private final boolean moveBy1;
    private final Integer deviceMode;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param deviceMode The ID of the device mode
     * @param moveBy1 If true the bank window moves by 1 otherwise by 8
     * @param moveLeft If true the bank window is moved left otherwise to the right
     */
    public MoveTrackBankCommand (final IModel model, final S surface, final Integer deviceMode, final boolean moveBy1, final boolean moveLeft)
    {
        super (model, surface);
        this.deviceMode = deviceMode;
        this.moveBy1 = moveBy1;
        this.moveLeft = moveLeft;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveOrTempMode (this.deviceMode))
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (this.moveBy1)
            {
                this.handleBankMovement (cursorDevice.getParameterBank ());
                return;
            }

            if (this.moveLeft)
                cursorDevice.selectPrevious ();
            else
                cursorDevice.selectNext ();
            return;
        }

        if (modeManager.isActiveOrTempMode (Modes.MODE_MARKER))
        {
            this.handleBankMovement (this.model.getMarkerBank ());
            return;
        }

        this.handleBankMovement (this.model.getCurrentTrackBank ());
    }


    /**
     *
     * @param bank
     */
    private void handleBankMovement (final IBank<?> bank)
    {
        if (this.moveBy1)
        {
            if (this.moveLeft)
                bank.scrollBackwards ();
            else
                bank.scrollForwards ();
        }
        else
        {
            if (this.moveLeft)
                bank.scrollPageBackwards ();
            else
                bank.scrollPageForwards ();
        }
    }
}
