// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.layer;

import de.mossgrabers.controller.mackie.mcu.MCUControllerSetup;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Abstract base mode for all layer modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractLayerMode extends BaseMode<ILayer>
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected AbstractLayerMode (final String name, final MCUControlSurface surface, final IModel model)
    {
        super (name, surface, model, model.getCursorDevice ().getLayerBank ());
    }


    /** {@inheritDoc} */
    @Override
    protected void drawTrackNameHeader ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IChannelBank<? extends IChannel> layerBank = cursorDevice.hasDrumPads () ? cursorDevice.getDrumPadBank () : cursorDevice.getLayerBank ();

        final int extenderOffset = this.getExtenderOffset ();

        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        // Format track names
        final int textLength = this.getTextLength ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel c = layerBank.getItem (extenderOffset + i);
            d.setCell (0, i, StringUtils.shortenAndFixASCII (c.getName (), textLength));
        }
        d.done (0);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        // Mode specific
        if (row == 0)
        {
            this.resetParameter (index);
            return;
        }

        // Record Arm, Solo, Mute
        if (this.pinFXtoLastDevice)
        {
            super.onButton (row, index, event);
            return;
        }

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IChannelBank<? extends IChannel> layerBank = cursorDevice.hasDrumPads () ? cursorDevice.getDrumPadBank () : cursorDevice.getLayerBank ();
        final IChannel channel = layerBank.getItem (this.getExtenderOffset () + index);
        if (row == 2)
        {
            if (this.surface.isSelectPressed ())
                this.model.getProject ().clearSolo ();
            else
                channel.toggleSolo ();
        }
        else if (row == 3)
        {
            if (this.surface.isSelectPressed ())
                this.model.getProject ().clearMute ();
            else
                channel.toggleMute ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (this.pinFXtoLastDevice)
            return super.getButtonColor (buttonID);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IChannelBank<? extends IChannel> layerBank = cursorDevice.hasDrumPads () ? cursorDevice.getDrumPadBank () : cursorDevice.getLayerBank ();

        final int extenderOffset = this.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final IChannel channel = layerBank.getItem (extenderOffset + i);

            final boolean exists = channel.doesExist ();
            if (buttonID == ButtonID.get (ButtonID.ROW_SELECT_1, i))
                return exists && channel.isSelected () ? MCUControllerSetup.MCU_BUTTON_STATE_ON : MCUControllerSetup.MCU_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW2_1, i))
                return MCUControllerSetup.MCU_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW3_1, i))
                return exists && channel.isSolo () ? MCUControllerSetup.MCU_BUTTON_STATE_ON : MCUControllerSetup.MCU_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW4_1, i))
                return exists && channel.isMute () ? MCUControllerSetup.MCU_BUTTON_STATE_ON : MCUControllerSetup.MCU_BUTTON_STATE_OFF;
        }

        return MCUControllerSetup.MCU_BUTTON_STATE_OFF;
    }
}