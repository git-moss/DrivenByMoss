// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.device;

import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode for selecting the device slot chains.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceChainsMode extends DeviceParamsMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceChainsMode (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            return;

        if (event == ButtonEvent.UP)
        {
            final ICursorDevice cd = this.model.getCursorDevice ();
            if (!cd.doesExist ())
                return;
            final String [] slotChains = cd.getSlotChains ();
            if (index >= slotChains.length)
                return;
            cd.selectSlotChain (slotChains[index]);
            this.surface.getModeManager ().setActive (Modes.DEVICE_PARAMS);
            return;
        }

        // LONG press - move upwards
        this.surface.setTriggerConsumed (ButtonID.get (ButtonID.ROW1_1, index));
        this.moveUp ();
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
            return super.getButtonColor (buttonID);

        final int existsColor = this.isPush2 ? PushColorManager.PUSH2_COLOR_YELLOW_LO : PushColorManager.PUSH1_COLOR_YELLOW_LO;
        final int offColor = this.isPush2 ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;
        final String [] slotChains = this.model.getCursorDevice ().getSlotChains ();
        return index < slotChains.length ? existsColor : offColor;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!this.checkExists1 (display, cd))
            return;

        // Row 3
        display.setBlock (2, 0, "Device Chains:").setBlock (2, 1, cd.getName ());

        // Row 4
        final String [] slotChains = cd.getSlotChains ();
        for (int i = 0; i < 8; i++)
        {
            final String bottomMenu = i < slotChains.length ? slotChains[i] : "";
            display.setCell (3, i, bottomMenu);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!this.checkExists2 (display, cd))
            return;

        final String color = this.model.getCurrentTrackBank ().getSelectedChannelColorEntry ();
        final ColorEx bottomMenuColor = DAWColor.getColorEntry (color);
        final boolean hasPinning = this.model.getHost ().supports (Capability.HAS_PINNING);
        final String [] slotChains = cd.getSlotChains ();
        for (int i = 0; i < 8; i++)
        {
            final boolean isTopMenuOn = this.getTopMenuEnablement (cd, hasPinning, i);
            final String bottomMenu = i < slotChains.length ? slotChains[i] : "";
            final String bottomMenuIcon = "";
            final boolean isBottomMenuOn = i < slotChains.length;
            display.addParameterElement (this.hostMenu[i], isTopMenuOn, bottomMenu, bottomMenuIcon, bottomMenuColor, isBottomMenuOn, "", 0, "", false, -1);
        }
    }
}