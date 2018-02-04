// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode.device;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.BitwigColors;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.data.ChannelData;
import de.mossgrabers.framework.daw.data.ParameterData;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;
import de.mossgrabers.push.mode.BaseMode;
import de.mossgrabers.push.mode.Modes;


/**
 * Mode for editing device remote control parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceParamsMode extends BaseMode
{
    private static final String [] MENU =
    {
        "On",
        "Parameters",
        "Expanded",
        null,
        "Banks",
        null,
        "Window",
        "Up"
    };

    private boolean                showDevices;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceParamsMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);

        this.isTemporary = false;
        this.showDevices = true;
    }


    /**
     * Show devices or the parameter banks of the cursor device for selection.
     *
     * @param enable True to enable
     */
    public void setShowDevices (final boolean enable)
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
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCursorDevice ().changeParameter (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
                cd.resetParameter (index);
                return;
            }

            final ParameterData param = cd.getFXParam (index);
            if (!param.getName ().isEmpty ())
                this.surface.getDisplay ().notify (param.getName () + ": " + param.getDisplayedValue ());
        }
        cd.touchParameter (index, isTouched);
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
            final CursorDeviceProxy cd = this.model.getCursorDevice ();
            if (!cd.hasSelectedDevice ())
                return;

            if (!this.showDevices)
            {
                cd.setSelectedParameterPageInBank (index);
                return;
            }

            if (cd.getPositionInBank () != index)
            {
                cd.selectSibling (index);
                return;
            }

            final ModeManager modeManager = this.surface.getModeManager ();
            if (!cd.hasLayers ())
            {
                ((DeviceParamsMode) modeManager.getMode (Modes.MODE_DEVICE_PARAMS)).setShowDevices (false);
                return;
            }

            final ChannelData layer = cd.getSelectedLayerOrDrumPad ();
            if (layer == null)
                cd.selectLayerOrDrumPad (0);
            modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER);
            return;
        }

        // LONG press - move upwards
        this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_ROW1_1 + index);
        this.moveUp ();
    }


    /**
     * Move up the hierarchy.
     */
    protected void moveUp ()
    {
        // There is no device on the track move upwards to the track view
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        final View activeView = this.surface.getViewManager ().getActiveView ();
        if (!cd.hasSelectedDevice ())
        {
            activeView.executeTriggerCommand (Commands.COMMAND_TRACK, ButtonEvent.DOWN);
            return;
        }

        // Parameter banks are shown -> show devices
        final ModeManager modeManager = this.surface.getModeManager ();
        final DeviceParamsMode deviceParamsMode = (DeviceParamsMode) modeManager.getMode (Modes.MODE_DEVICE_PARAMS);
        if (!deviceParamsMode.isShowDevices ())
        {
            deviceParamsMode.setShowDevices (true);
            return;
        }

        // Devices are shown, if nested show the layers otherwise move up to the tracks
        if (cd.isNested ())
        {
            cd.selectParent ();
            modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER);
            deviceParamsMode.setShowDevices (false);
            cd.selectChannel ();
            return;
        }

        // Move up to the track
        if (this.model.isCursorDeviceOnMasterTrack ())
        {
            activeView.executeTriggerCommand (Commands.COMMAND_MASTERTRACK, ButtonEvent.DOWN);
            activeView.executeTriggerCommand (Commands.COMMAND_MASTERTRACK, ButtonEvent.UP);
        }
        else
            activeView.executeTriggerCommand (Commands.COMMAND_TRACK, ButtonEvent.DOWN);
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (!cd.hasSelectedDevice ())
        {
            this.disableFirstRow ();
            return;
        }

        final int selectedColor = this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI;
        final int existsColor = this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_LO : PushColors.PUSH1_COLOR_YELLOW_LO;
        final int offColor = this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;

        if (this.showDevices)
        {
            for (int i = 0; i < 8; i++)
                this.surface.updateButton (20 + i, cd.doesSiblingExist (i) ? i == cd.getPositionInBank () ? selectedColor : existsColor : offColor);
        }
        else
        {
            final String [] pages = cd.getParameterPageNames ();
            final int page = Math.min (Math.max (0, cd.getSelectedParameterPage ()), pages.length - 1);
            final int start = page / 8 * 8;
            for (int i = 0; i < 8; i++)
            {
                final int index = start + i;
                this.surface.updateButton (20 + i, index < pages.length ? index == page ? selectedColor : existsColor : offColor);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final CursorDeviceProxy device = this.model.getCursorDevice ();
        switch (index)
        {
            case 0:
                if (device.hasSelectedDevice ())
                    device.toggleEnabledState ();
                break;
            case 1:
                if (device.hasSelectedDevice ())
                    device.toggleParameterPageSectionVisible ();
                break;
            case 2:
                if (device.hasSelectedDevice ())
                    device.toggleExpanded ();
                break;
            case 4:
                if (device.hasSelectedDevice ())
                    this.showDevices = !this.showDevices;
                break;
            case 6:
                if (device.hasSelectedDevice ())
                    device.toggleWindowOpen ();
                break;
            case 7:
                this.moveUp ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final int white = this.isPush2 ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH1_COLOR2_WHITE;

        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (!cd.hasSelectedDevice ())
        {
            this.disableSecondRow ();
            this.surface.updateButton (109, white);
            return;
        }

        final int green = this.isPush2 ? PushColors.PUSH2_COLOR2_GREEN : PushColors.PUSH1_COLOR2_GREEN;
        final int grey = this.isPush2 ? PushColors.PUSH2_COLOR2_GREY_LO : PushColors.PUSH1_COLOR2_GREY_LO;
        final int orange = this.isPush2 ? PushColors.PUSH2_COLOR2_ORANGE : PushColors.PUSH1_COLOR2_ORANGE;
        final int off = this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        final int turquoise = this.isPush2 ? PushColors.PUSH2_COLOR2_TURQUOISE_HI : PushColors.PUSH1_COLOR2_TURQUOISE_HI;

        this.surface.updateButton (102, cd.isEnabled () ? green : grey);
        this.surface.updateButton (103, cd.isParameterPageSectionVisible () ? orange : white);
        this.surface.updateButton (104, cd.isExpanded () ? orange : white);
        this.surface.updateButton (105, off);
        this.surface.updateButton (106, this.showDevices ? white : orange);
        this.surface.updateButton (107, off);
        this.surface.updateButton (108, cd.isWindowOpen () ? turquoise : grey);

        this.surface.updateButton (109, white);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ().clear ();

        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (!cd.hasSelectedDevice ())
        {
            d.clear ().setBlock (1, 0, "           Select").setBlock (1, 1, "a device or press").setBlock (1, 2, "'Add Effect'...  ").allDone ();
            return;
        }

        // Row 1 & 2
        for (int i = 0; i < 8; i++)
        {
            final ParameterData param = cd.getFXParam (i);
            final boolean exists = param.doesExist ();
            d.setCell (0, i, exists ? param.getName () : "").setCell (1, i, param.getDisplayedValue (8));
        }

        // Row 3
        d.setBlock (2, 0, "Selected Device:").setBlock (2, 1, this.model.getCursorDevice ().getName ());

        // Row 4
        if (this.showDevices)
        {
            for (int i = 0; i < 8; i++)
                d.setCell (3, i, cd.doesSiblingExist (i) ? (i == cd.getPositionInBank () ? PushDisplay.RIGHT_ARROW : "") + cd.getSiblingDeviceName (i) : "");
        }
        else
        {
            final String [] pages = cd.getParameterPageNames ();
            final int page = Math.min (Math.max (0, cd.getSelectedParameterPage ()), pages.length - 1);
            final int start = page / 8 * 8;
            for (int i = 0; i < 8; i++)
            {
                final int index = start + i;
                d.setCell (3, i, index < pages.length ? (index == page ? PushDisplay.RIGHT_ARROW : "") + pages[index] : "");
            }
        }

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final PushDisplay pushDisplay = (PushDisplay) this.surface.getDisplay ();
        final DisplayMessage message = pushDisplay.createMessage ();

        if (!this.model.getCursorDevice ().hasSelectedDevice ())
        {
            for (int i = 0; i < 8; i++)
                message.addOptionElement (i == 2 ? "Please select a device or press 'Add Device'..." : "", i == 7 ? "Up" : "", true, "", "", false, true);
            message.send ();
            return;
        }

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final String color = tb.getSelectedTrackColorEntry ();

        final ValueChanger valueChanger = this.model.getValueChanger ();

        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        final String [] pages = cd.getParameterPageNames ();
        final int page = Math.min (Math.max (0, cd.getSelectedParameterPage ()), pages.length - 1);
        final int start = page / 8 * 8;

        for (int i = 0; i < 8; i++)
        {
            boolean isTopMenuOn;
            switch (i)
            {
                case 0:
                    isTopMenuOn = cd.isEnabled ();
                    break;
                case 1:
                    isTopMenuOn = cd.isParameterPageSectionVisible ();
                    break;
                case 2:
                    isTopMenuOn = cd.isExpanded ();
                    break;
                case 4:
                    isTopMenuOn = !this.showDevices;
                    break;
                case 6:
                    isTopMenuOn = cd.isWindowOpen ();
                    break;
                case 7:
                    isTopMenuOn = true;
                    break;
                default:
                    // Not used
                    isTopMenuOn = false;
                    break;
            }

            String bottomMenu;
            // TODO API extension required, add an icon if the type of the plugin is known
            // https://github.com/teotigraphix/Framework4Bitwig/issues/138
            final String bottomMenuIcon = "";
            final double [] bottomMenuColor = BitwigColors.getColorEntry (color);
            boolean isBottomMenuOn;
            if (this.showDevices)
            {
                bottomMenu = cd.doesSiblingExist (i) ? cd.getSiblingDeviceName (i) : "";
                isBottomMenuOn = i == cd.getPositionInBank ();
            }
            else
            {
                final int index = start + i;
                bottomMenu = index < pages.length ? pages[index] : "";
                isBottomMenuOn = index == page;
            }

            final ParameterData param = this.model.getCursorDevice ().getFXParam (i);
            final boolean exists = param.doesExist ();
            final String parameterName = exists ? param.getName (9) : "";
            final int parameterValue = valueChanger.toDisplayValue (exists ? param.getValue () : 0);
            final String parameterValueStr = exists ? param.getDisplayedValue (8) : "";
            final boolean parameterIsActive = this.isKnobTouched[i];
            final int parameterModulatedValue = valueChanger.toDisplayValue (exists ? param.getModulatedValue () : -1);

            message.addParameterElement (MENU[i], isTopMenuOn, bottomMenu, bottomMenuIcon, bottomMenuColor, isBottomMenuOn, parameterName, parameterValue, parameterValueStr, parameterIsActive, parameterModulatedValue);
        }

        message.send ();
    }


    /**
     * Is there a previous page?
     *
     * @return True if there is
     */
    public boolean canSelectPreviousPage ()
    {
        final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
        return this.showDevices ? cursorDevice.canSelectPreviousFX () : cursorDevice.hasPreviousParameterPage ();
    }


    /**
     * Is there a next page?
     *
     * @return True if there is
     */
    public boolean canSelectNextPage ()
    {
        final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
        return this.showDevices ? cursorDevice.canSelectNextFX () : cursorDevice.hasNextParameterPage ();
    }


    /**
     * Select the previous device or parameter page depending on the mode.
     */
    public void selectPreviousPage ()
    {
        final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
        if (this.showDevices)
            cursorDevice.selectPrevious ();
        else
            cursorDevice.previousParameterPage ();
    }


    /**
     * Select the next device or parameter page depending on the mode.
     */
    public void selectNextPage ()
    {
        final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
        if (this.showDevices)
            cursorDevice.selectNext ();
        else
            cursorDevice.nextParameterPage ();
    }


    /**
     * Select the previous device bank or parameter page bank depending on the mode.
     */
    public void selectPreviousPageBank ()
    {
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (this.showDevices)
            cd.selectPreviousBank ();
        else
            cd.previousParameterPageBank ();
    }


    /**
     * Select the next device bank or parameter page bank depending on the mode.
     */
    public void selectNextPageBank ()
    {
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (this.showDevices)
            cd.selectNextBank ();
        else
            cd.nextParameterPageBank ();
    }
}