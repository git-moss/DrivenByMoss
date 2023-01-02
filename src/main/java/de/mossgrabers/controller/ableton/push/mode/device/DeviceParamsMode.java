// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.device;

import de.mossgrabers.controller.ableton.push.controller.Push1Display;
import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Optional;


/**
 * Mode for editing device remote control parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceParamsMode extends BaseMode<IParameter>
{
    private static final String [] MENU     =
    {
        "On",
        "Parameters",
        "Expanded",
        "Chains",
        "Banks",
        "Pin Device",
        "Window",
        "Up"
    };

    protected final String []      hostMenu = new String [MENU.length];
    protected boolean              showDevices;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceParamsMode (final PushControlSurface surface, final IModel model)
    {
        super ("Parameters", surface, model, model.getCursorDevice ().getParameterBank ());

        this.setParameterProvider (new BankParameterProvider (this.model.getCursorDevice ().getParameterBank ()));

        this.setShowDevices (true);

        System.arraycopy (MENU, 0, this.hostMenu, 0, MENU.length);
        final IHost host = this.model.getHost ();
        if (!host.supports (Capability.HAS_PARAMETER_PAGE_SECTION))
            this.hostMenu[1] = "";
        if (!host.supports (Capability.HAS_PINNING))
            this.hostMenu[5] = "";
        if (!host.supports (Capability.HAS_SLOT_CHAINS))
            this.hostMenu[3] = "";
    }


    /**
     * Show devices or the parameter banks of the cursor device for selection.
     *
     * @param enable True to enable
     */
    public final void setShowDevices (final boolean enable)
    {
        this.showDevices = enable;
    }


    /**
     * Returns true if devices are shown otherwise parameter banks.
     *
     * @return True if devices are shown otherwise parameter banks
     */
    public boolean isShowDevices ()
    {
        return this.showDevices;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        final ICursorDevice cd = this.model.getCursorDevice ();
        final IParameter param = cd.getParameterBank ().getItem (index);
        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            param.resetValue ();
        }
        param.touchValue (isTouched);
        this.checkStopAutomationOnKnobRelease (isTouched);
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

            // Select parameter bank if parameter banks are visible
            if (!this.showDevices)
            {
                cd.getParameterPageBank ().selectPage (index);
                return;
            }

            // Duplicate device
            if (this.isButtonCombination (ButtonID.DUPLICATE))
            {
                cd.duplicate ();
                return;
            }

            // Delete device
            if (this.isButtonCombination (ButtonID.DELETE))
            {
                cd.getDeviceBank ().getItem (index).remove ();
                return;
            }

            // Disable/Enable device
            if (this.isButtonCombination (ButtonID.MUTE))
            {
                cd.getDeviceBank ().getItem (index).toggleEnabledState ();
                return;
            }

            // Select device
            if (cd.getIndex () != index)
            {
                cd.getDeviceBank ().getItem (index).select ();
                return;
            }

            // No layers, show devices
            final ModeManager modeManager = this.surface.getModeManager ();
            if (!cd.hasLayers ())
            {
                ((DeviceParamsMode) modeManager.get (Modes.DEVICE_PARAMS)).setShowDevices (false);
                return;
            }

            // If there are layers, make sure one is selected
            final Optional<?> layer = cd.getLayerBank ().getSelectedItem ();
            if (layer.isEmpty ())
                cd.getLayerBank ().getItem (0).select ();
            modeManager.setActive (this.surface.getConfiguration ().getCurrentLayerMixMode ());

            return;
        }

        // LONG press - move upwards
        this.surface.setTriggerConsumed (ButtonID.get (ButtonID.ROW1_1, index));
        this.moveUp ();
    }


    /**
     * Move up the hierarchy.
     */
    protected void moveUp ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.DEVICE_CHAINS))
        {
            modeManager.setActive (Modes.DEVICE_PARAMS);
            return;
        }

        // There is no device on the track move upwards to the track view
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
        {
            this.surface.getButton (ButtonID.TRACK).trigger (ButtonEvent.DOWN);
            return;
        }

        // Parameter banks are shown -> show devices
        final DeviceParamsMode deviceParamsMode = (DeviceParamsMode) modeManager.get (Modes.DEVICE_PARAMS);
        if (!deviceParamsMode.isShowDevices ())
        {
            deviceParamsMode.setShowDevices (true);
            return;
        }

        // Devices are shown, if nested show the layers otherwise move up to the tracks
        if (cd.isNested ())
        {
            cd.selectParent ();
            this.model.getHost ().scheduleTask ( () -> {
                if (cd.hasLayers ())
                    modeManager.setActive (this.surface.getConfiguration ().getCurrentLayerMixMode ());
                else
                    modeManager.setActive (Modes.DEVICE_PARAMS);
                deviceParamsMode.setShowDevices (false);
                cd.selectChannel ();
            }, 300);
            return;
        }

        // Move up to the track
        if (this.model.isCursorDeviceOnMasterTrack ())
            this.surface.getButton (ButtonID.MASTERTRACK).trigger (ButtonEvent.DOWN);
        else
            this.surface.getButton (ButtonID.TRACK).trigger (ButtonEvent.DOWN);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();

        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            if (!cd.doesExist ())
                return super.getButtonColor (buttonID);

            final int selectedColor = this.colorManager.getColorIndex (PushColorManager.PUSH_ORANGE_HI);
            final int existsColor = this.colorManager.getColorIndex (PushColorManager.PUSH_YELLOW_LO);
            final int offColor = this.colorManager.getColorIndex (PushColorManager.PUSH_BLACK);

            if (this.showDevices)
            {
                final IDeviceBank bank = cd.getDeviceBank ();
                if (!bank.getItem (index).doesExist ())
                    return offColor;
                return index == cd.getIndex () ? selectedColor : existsColor;
            }
            final IParameterPageBank bank = cd.getParameterPageBank ();
            final int selectedItemIndex = bank.getSelectedItemIndex ();
            if (bank.getItem (index).isEmpty ())
                return offColor;
            return index == selectedItemIndex ? selectedColor : existsColor;
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            final int white = this.isPush2 ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH1_COLOR2_WHITE;
            if (!cd.doesExist ())
                return index == 7 ? white : super.getButtonColor (buttonID);

            final int green = this.isPush2 ? PushColorManager.PUSH2_COLOR2_GREEN : PushColorManager.PUSH1_COLOR2_GREEN;
            final int grey = this.isPush2 ? PushColorManager.PUSH2_COLOR2_GREY_LO : PushColorManager.PUSH1_COLOR2_GREY_LO;
            final int orange = this.isPush2 ? PushColorManager.PUSH2_COLOR2_ORANGE : PushColorManager.PUSH1_COLOR2_ORANGE;
            final int off = this.isPush2 ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;
            final int turquoise = this.isPush2 ? PushColorManager.PUSH2_COLOR2_TURQUOISE_HI : PushColorManager.PUSH1_COLOR2_TURQUOISE_HI;

            switch (index)
            {
                case 0:
                    return cd.isEnabled () ? green : grey;
                case 1:
                    return cd.isParameterPageSectionVisible () ? orange : white;
                case 2:
                    return cd.isExpanded () ? orange : white;
                case 3:
                    return this.surface.getModeManager ().isActive (Modes.DEVICE_CHAINS) ? orange : white;
                case 4:
                    return this.showDevices ? white : orange;
                case 5:
                    if (!this.model.getHost ().supports (Capability.HAS_PINNING))
                        return off;
                    return cd.isPinned () ? turquoise : grey;
                case 6:
                    return cd.isWindowOpen () ? turquoise : grey;
                default:
                case 7:
                    return white;
            }
        }

        return super.getButtonColor (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final ICursorDevice device = this.model.getCursorDevice ();
        final ModeManager modeManager = this.surface.getModeManager ();
        switch (index)
        {
            case 0:
                if (device.doesExist ())
                    device.toggleEnabledState ();
                break;
            case 1:
                if (device.doesExist ())
                    device.toggleParameterPageSectionVisible ();
                break;
            case 2:
                if (device.doesExist ())
                    device.toggleExpanded ();
                break;
            case 3:
                if (!this.model.getHost ().supports (Capability.HAS_SLOT_CHAINS))
                    return;
                if (modeManager.isActive (Modes.DEVICE_CHAINS))
                    modeManager.setActive (Modes.DEVICE_PARAMS);
                else
                    modeManager.setActive (Modes.DEVICE_CHAINS);
                break;
            case 4:
                if (!device.doesExist ())
                    return;
                if (!modeManager.isActive (Modes.DEVICE_PARAMS))
                    modeManager.setActive (Modes.DEVICE_PARAMS);
                this.setShowDevices (!this.showDevices);
                break;
            case 5:
                if (device.doesExist ())
                    device.togglePinned ();
                break;
            case 6:
                if (device.doesExist ())
                    device.toggleWindowOpen ();
                break;
            case 7:
                this.moveUp ();
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!this.checkExists1 (display, cd))
            return;

        // Row 1 & 2
        final IParameterBank parameterBank = cd.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter param = parameterBank.getItem (i);
            display.setCell (0, i, param.doesExist () ? StringUtils.fixASCII (param.getName ()) : "").setCell (1, i, StringUtils.fixASCII (param.getDisplayedValue (8)));
        }

        // Row 3
        display.setBlock (2, 0, "Selected Device:").setBlock (2, 1, cd.getName ());

        // Row 4
        if (this.showDevices)
        {
            final IDeviceBank deviceBank = cd.getDeviceBank ();
            for (int i = 0; i < 8; i++)
            {
                final IDevice device = deviceBank.getItem (i);
                final StringBuilder sb = new StringBuilder ();
                if (device.doesExist ())
                {
                    if (i == cd.getIndex ())
                        sb.append (Push1Display.SELECT_ARROW);
                    if (!device.isEnabled ())
                        sb.append (Push1Display.DIVISION);
                    sb.append (device.getName ());
                }
                display.setCell (3, i, sb.toString ());
            }
            return;
        }
        final IParameterPageBank bank = cd.getParameterPageBank ();
        final int selectedItemIndex = bank.getSelectedItemIndex ();
        for (int i = 0; i < bank.getPageSize (); i++)
        {
            final String item = bank.getItem (i);
            final String selectedStr = i == selectedItemIndex ? Push1Display.SELECT_ARROW : "";
            display.setCell (3, i, item.isEmpty () ? "" : selectedStr + item);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!this.checkExists2 (display, cd))
            return;

        final String channelColor = this.model.getCurrentTrackBank ().getSelectedChannelColorEntry ();
        final ColorEx bottomMenuColor = DAWColor.getColorEntry (channelColor);
        final ColorEx colorBackground = this.surface.getConfiguration ().getColorBackground ();

        final IDeviceBank deviceBank = cd.getDeviceBank ();
        final IParameterBank parameterBank = cd.getParameterBank ();
        final IParameterPageBank parameterPageBank = cd.getParameterPageBank ();
        final int selectedPage = parameterPageBank.getSelectedItemIndex ();
        final boolean hasPinning = this.model.getHost ().supports (Capability.HAS_PINNING);
        final IValueChanger valueChanger = this.model.getValueChanger ();
        for (int i = 0; i < parameterBank.getPageSize (); i++)
        {
            final boolean isTopMenuOn = this.getTopMenuEnablement (cd, hasPinning, i);

            String bottomMenu;
            final String bottomMenuIcon;
            boolean isBottomMenuOn;
            ColorEx color = bottomMenuColor;
            if (this.showDevices)
            {
                final IDevice device = deviceBank.getItem (i);
                bottomMenuIcon = device.getName ();
                bottomMenu = device.doesExist () ? device.getName (12) : "";
                isBottomMenuOn = i == cd.getIndex ();
                if (!device.isEnabled ())
                    color = colorBackground;
            }
            else
            {
                bottomMenuIcon = cd.getName ();
                bottomMenu = parameterPageBank.getItem (i);

                if (bottomMenu.length () > 12)
                    bottomMenu = bottomMenu.substring (0, 12);
                isBottomMenuOn = i == selectedPage;
            }

            final IParameter param = parameterBank.getItem (i);
            final boolean exists = param.doesExist ();
            final String parameterName = exists ? param.getName (9) : "";
            final int parameterValue = valueChanger.toDisplayValue (exists ? param.getValue () : 0);
            final String parameterValueStr = exists ? param.getDisplayedValue (8) : "";
            final boolean parameterIsActive = this.isKnobTouched (i);
            final int parameterModulatedValue = valueChanger.toDisplayValue (exists ? param.getModulatedValue () : -1);

            display.addParameterElement (this.hostMenu[i], isTopMenuOn, bottomMenu, bottomMenuIcon, color, isBottomMenuOn, parameterName, parameterValue, parameterValueStr, parameterIsActive, parameterModulatedValue);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        if (this.showDevices)
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            cursorDevice.getDeviceBank ().selectPreviousItem ();
            return;
        }
        super.selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        if (this.showDevices)
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            cursorDevice.getDeviceBank ().selectNextItem ();
            return;
        }
        super.selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        if (this.showDevices)
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (this.surface.isShiftPressed ())
                cursorDevice.swapWithPrevious ();
            else
                cursorDevice.getDeviceBank ().selectPreviousPage ();
            return;
        }
        super.selectPreviousItemPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        if (this.showDevices)
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (this.surface.isShiftPressed ())
                cursorDevice.swapWithNext ();
            else
                cursorDevice.getDeviceBank ().selectNextPage ();
            return;
        }
        super.selectNextItemPage ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        if (this.showDevices)
            return this.model.getCursorDevice ().getDeviceBank ().canScrollBackwards ();
        return super.hasPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        if (this.showDevices)
            return this.model.getCursorDevice ().getDeviceBank ().canScrollForwards ();
        return super.hasNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        if (this.showDevices)
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (this.surface.isShiftPressed ())
                return cursorDevice.getIndex () > 0;
            return cursorDevice.getDeviceBank ().canScrollPageBackwards ();
        }
        return super.hasPreviousItemPage ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        if (this.showDevices)
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (this.surface.isShiftPressed ())
                return cursorDevice.getIndex () < 7;
            return cursorDevice.getDeviceBank ().canScrollPageForwards ();
        }
        return super.hasNextItemPage ();
    }


    protected boolean checkExists1 (final ITextDisplay display, final ICursorDevice cd)
    {
        if (cd.doesExist ())
            return true;
        display.setBlock (1, 0, "           Select").setBlock (1, 1, "a device or press").setBlock (1, 2, "'Add Effect'...  ").allDone ();
        return false;
    }


    protected boolean checkExists2 (final IGraphicDisplay display, final ICursorDevice cd)
    {
        if (cd.doesExist ())
            return true;
        for (int i = 0; i < 8; i++)
            display.addOptionElement (i == 2 ? "Please select a device or press 'Add Device'..." : "", i == 7 ? "Up" : "", true, "", "", false, true);
        return false;
    }


    protected boolean getTopMenuEnablement (final ICursorDevice cd, final boolean hasPinning, final int index)
    {
        switch (index)
        {
            case 0:
                return cd.isEnabled ();
            case 1:
                return cd.isParameterPageSectionVisible ();
            case 2:
                return cd.isExpanded ();
            case 3:
                return this.surface.getModeManager ().isActive (Modes.DEVICE_CHAINS);
            case 4:
                return !this.surface.getModeManager ().isActive (Modes.DEVICE_CHAINS) && !this.showDevices;
            case 5:
                return hasPinning && cd.isPinned ();
            case 6:
                return cd.isWindowOpen ();
            case 7:
                return true;
            default:
                // Not used
                return false;
        }
    }
}