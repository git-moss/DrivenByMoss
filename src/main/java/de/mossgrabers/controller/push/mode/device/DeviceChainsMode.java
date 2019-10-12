// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.device;

import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode for selecting the device slot chains.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
            this.surface.getModeManager ().setActiveMode (Modes.DEVICE_PARAMS);
            return;
        }

        // LONG press - move upwards
        this.surface.setTriggerConsumed (PushControlSurface.PUSH_BUTTON_ROW1_1 + index);
        this.moveUp ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final int existsColor = this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_LO : PushColors.PUSH1_COLOR_YELLOW_LO;
        final int offColor = this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        final String [] slotChains = this.model.getCursorDevice ().getSlotChains ();
        for (int i = 0; i < 8; i++)
            this.surface.updateTrigger (20 + i, i < slotChains.length ? existsColor : offColor);
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
        final double [] bottomMenuColor = DAWColors.getColorEntry (color);
        final boolean hasPinning = this.model.getHost ().hasPinning ();
        final String [] slotChains = cd.getSlotChains ();
        for (int i = 0; i < 8; i++)
        {
            final boolean isTopMenuOn = this.getTopMenuEnablement (cd, hasPinning, i);
            String bottomMenu = i < slotChains.length ? slotChains[i] : "";
            final String bottomMenuIcon = "";
            boolean isBottomMenuOn = i < slotChains.length;
            display.addParameterElement (this.hostMenu[i], isTopMenuOn, bottomMenu, bottomMenuIcon, bottomMenuColor, isBottomMenuOn, "", 0, "", false, -1);
        }
    }
}