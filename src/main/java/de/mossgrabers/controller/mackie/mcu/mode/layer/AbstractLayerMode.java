// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.layer;

import de.mossgrabers.controller.mackie.mcu.MCUControllerSetup;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Abstract base mode for all layer modes.
 *
 * @author Jürgen Moßgraber
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
        super (name, surface, model, getDevice (model).getLayerBank ());

        final ISpecificDevice device = getDevice (model);
        device.addHasDrumPadsObserver (hasDrumPads -> this.switchBanks (device.hasDrumPads () ? device.getDrumPadBank () : device.getLayerBank ()));
    }


    /** {@inheritDoc} */
    @Override
    protected void drawTrackNameHeader ()
    {
        final ISpecificDevice device = getDevice (this.model);
        final IChannelBank<? extends IChannel> layerBank = device.hasDrumPads () ? device.getDrumPadBank () : device.getLayerBank ();

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

        final ISpecificDevice device = getDevice (this.model);
        final IChannelBank<? extends IChannel> layerBank = device.hasDrumPads () ? device.getDrumPadBank () : device.getLayerBank ();
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

        final ISpecificDevice device = getDevice (this.model);
        final IChannelBank<? extends IChannel> layerBank = device.hasDrumPads () ? device.getDrumPadBank () : device.getLayerBank ();

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


    protected static final ISpecificDevice getDevice (final IModel model)
    {
        return model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        super.updateDisplay ();
        this.updateItemIndices ();
    }


    protected void updateItemIndices ()
    {
        final ISpecificDevice device = getDevice (this.model);
        final IChannelBank<? extends IChannel> layerBank = device.hasDrumPads () ? device.getDrumPadBank () : device.getLayerBank ();
        final int extenderOffset = this.getExtenderOffset ();
        final int [] indices = new int [8];
        for (int i = 0; i < 8; i++)
        {
            final IChannel item = layerBank.getItem (extenderOffset + i);
            if (item.doesExist ())
                indices[i] = item.getPosition () + (device.hasDrumPads () ? 0 : 1);
            else
                indices[i] = 0;
        }
        this.surface.setItemIndices (indices);
    }
}