// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.controller.generic.flexihandler.utils.FlexiHandlerException;
import de.mossgrabers.controller.generic.flexihandler.utils.KnobMode;
import de.mossgrabers.controller.generic.flexihandler.utils.MidiValue;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * The handler for device commands.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceHandler extends AbstractHandler
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param absoluteLowResValueChanger The default absolute value changer in low res mode
     * @param signedBitRelativeValueChanger The signed bit relative value changer
     * @param offsetBinaryRelativeValueChanger The offset binary relative value changer
     */
    public DeviceHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.DEVICE_TOGGLE_WINDOW,
            FlexiCommand.DEVICE_TOGGLE_BYPASS,
            FlexiCommand.DEVICE_TOGGLE_PIN,
            FlexiCommand.DEVICE_TOGGLE_EXPAND,
            FlexiCommand.DEVICE_TOGGLE_PARAMETERS,
            FlexiCommand.DEVICE_SELECT_PREVIOUS,
            FlexiCommand.DEVICE_SELECT_NEXT,
            FlexiCommand.DEVICE_SCROLL_DEVICES,
            FlexiCommand.DEVICE_SELECT_PREVIOUS_PARAMETER_PAGE,
            FlexiCommand.DEVICE_SELECT_NEXT_PARAMETER_PAGE,
            FlexiCommand.DEVICE_SELECT_PARAMETER_PAGE_1,
            FlexiCommand.DEVICE_SELECT_PARAMETER_PAGE_2,
            FlexiCommand.DEVICE_SELECT_PARAMETER_PAGE_3,
            FlexiCommand.DEVICE_SELECT_PARAMETER_PAGE_4,
            FlexiCommand.DEVICE_SELECT_PARAMETER_PAGE_5,
            FlexiCommand.DEVICE_SELECT_PARAMETER_PAGE_6,
            FlexiCommand.DEVICE_SELECT_PARAMETER_PAGE_7,
            FlexiCommand.DEVICE_SELECT_PARAMETER_PAGE_8,
            FlexiCommand.DEVICE_SCROLL_PARAMETER_PAGES,
            FlexiCommand.DEVICE_SELECT_PREVIOUS_PARAMETER_BANK,
            FlexiCommand.DEVICE_SELECT_NEXT_PARAMETER_BANK,
            FlexiCommand.DEVICE_SCROLL_PARAMETER_BANKS,
            FlexiCommand.DEVICE_SET_PARAMETER_1,
            FlexiCommand.DEVICE_SET_PARAMETER_2,
            FlexiCommand.DEVICE_SET_PARAMETER_3,
            FlexiCommand.DEVICE_SET_PARAMETER_4,
            FlexiCommand.DEVICE_SET_PARAMETER_5,
            FlexiCommand.DEVICE_SET_PARAMETER_6,
            FlexiCommand.DEVICE_SET_PARAMETER_7,
            FlexiCommand.DEVICE_SET_PARAMETER_8,
            FlexiCommand.DEVICE_TOGGLE_PARAMETER_1,
            FlexiCommand.DEVICE_TOGGLE_PARAMETER_2,
            FlexiCommand.DEVICE_TOGGLE_PARAMETER_3,
            FlexiCommand.DEVICE_TOGGLE_PARAMETER_4,
            FlexiCommand.DEVICE_TOGGLE_PARAMETER_5,
            FlexiCommand.DEVICE_TOGGLE_PARAMETER_6,
            FlexiCommand.DEVICE_TOGGLE_PARAMETER_7,
            FlexiCommand.DEVICE_TOGGLE_PARAMETER_8,
            FlexiCommand.DEVICE_RESET_PARAMETER_2,
            FlexiCommand.DEVICE_RESET_PARAMETER_3,
            FlexiCommand.DEVICE_RESET_PARAMETER_4,
            FlexiCommand.DEVICE_RESET_PARAMETER_5,
            FlexiCommand.DEVICE_RESET_PARAMETER_6,
            FlexiCommand.DEVICE_RESET_PARAMETER_7,
            FlexiCommand.DEVICE_RESET_PARAMETER_8
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();

        switch (command)
        {
            case DEVICE_TOGGLE_WINDOW:
                return cursorDevice.isWindowOpen () ? 127 : 0;

            case DEVICE_TOGGLE_BYPASS:
                return cursorDevice.isEnabled () ? 0 : 127;

            case DEVICE_TOGGLE_PIN:
                return cursorDevice.isPinned () ? 127 : 0;

            case DEVICE_TOGGLE_EXPAND:
                return cursorDevice.isExpanded () ? 127 : 0;

            case DEVICE_TOGGLE_PARAMETERS:
                return cursorDevice.isParameterPageSectionVisible () ? 127 : 0;

            case DEVICE_SET_PARAMETER_1, DEVICE_SET_PARAMETER_2, DEVICE_SET_PARAMETER_3, DEVICE_SET_PARAMETER_4, DEVICE_SET_PARAMETER_5, DEVICE_SET_PARAMETER_6, DEVICE_SET_PARAMETER_7, DEVICE_SET_PARAMETER_8:
                return cursorDevice.getParameterBank ().getItem (command.ordinal () - FlexiCommand.DEVICE_SET_PARAMETER_1.ordinal ()).getValue ();

            case DEVICE_TOGGLE_PARAMETER_1, DEVICE_TOGGLE_PARAMETER_2, DEVICE_TOGGLE_PARAMETER_3, DEVICE_TOGGLE_PARAMETER_4, DEVICE_TOGGLE_PARAMETER_5, DEVICE_TOGGLE_PARAMETER_6, DEVICE_TOGGLE_PARAMETER_7, DEVICE_TOGGLE_PARAMETER_8:
                final int value = cursorDevice.getParameterBank ().getItem (command.ordinal () - FlexiCommand.DEVICE_TOGGLE_PARAMETER_1.ordinal ()).getValue ();
                return value > 0 ? 127 : 0;

            case DEVICE_SELECT_PARAMETER_PAGE_1, DEVICE_SELECT_PARAMETER_PAGE_2, DEVICE_SELECT_PARAMETER_PAGE_3, DEVICE_SELECT_PARAMETER_PAGE_4, DEVICE_SELECT_PARAMETER_PAGE_5, DEVICE_SELECT_PARAMETER_PAGE_6, DEVICE_SELECT_PARAMETER_PAGE_7, DEVICE_SELECT_PARAMETER_PAGE_8:
                return cursorDevice.getParameterBank ().getItem (command.ordinal () - FlexiCommand.DEVICE_SELECT_PARAMETER_PAGE_1.ordinal ()).isSelected () ? 127 : 0;

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);

        switch (command)
        {
            // Device: Toggle Window
            case DEVICE_TOGGLE_WINDOW:
                if (isButtonPressed)
                    cursorDevice.toggleWindowOpen ();
                break;
            // Device: Bypass
            case DEVICE_TOGGLE_BYPASS:
                if (isButtonPressed)
                    cursorDevice.toggleEnabledState ();
                break;
            // Device: Toggle Pinned
            case DEVICE_TOGGLE_PIN:
                if (isButtonPressed)
                    cursorDevice.togglePinned ();
                break;
            // Device: Expand
            case DEVICE_TOGGLE_EXPAND:
                if (isButtonPressed)
                    cursorDevice.toggleExpanded ();
                break;
            // Device: Parameters
            case DEVICE_TOGGLE_PARAMETERS:
                if (isButtonPressed)
                    cursorDevice.toggleParameterPageSectionVisible ();
                break;
            // Device: Select Previous
            case DEVICE_SELECT_PREVIOUS:
                if (isButtonPressed)
                    cursorDevice.selectPrevious ();
                break;
            // Device: Select Next
            case DEVICE_SELECT_NEXT:
                if (isButtonPressed)
                    cursorDevice.selectNext ();
                break;

            case DEVICE_SCROLL_DEVICES:
                this.scrollDevice (knobMode, value);
                break;

            case DEVICE_SELECT_PREVIOUS_PARAMETER_PAGE:
                if (isButtonPressed)
                    cursorDevice.getParameterBank ().scrollBackwards ();
                break;
            case DEVICE_SELECT_NEXT_PARAMETER_PAGE:
                if (isButtonPressed)
                    cursorDevice.getParameterBank ().scrollForwards ();
                break;

            case DEVICE_SELECT_PARAMETER_PAGE_1, DEVICE_SELECT_PARAMETER_PAGE_2, DEVICE_SELECT_PARAMETER_PAGE_3, DEVICE_SELECT_PARAMETER_PAGE_4, DEVICE_SELECT_PARAMETER_PAGE_5, DEVICE_SELECT_PARAMETER_PAGE_6, DEVICE_SELECT_PARAMETER_PAGE_7, DEVICE_SELECT_PARAMETER_PAGE_8:
                if (isButtonPressed)
                {
                    cursorDevice.getParameterBank ().getPageBank ().selectPage (command.ordinal () - FlexiCommand.DEVICE_SELECT_PARAMETER_PAGE_1.ordinal ());
                    this.mvHelper.notifySelectedDeviceAndParameterPage ();
                }
                break;

            case DEVICE_SCROLL_PARAMETER_PAGES:
                this.scrollParameterPage (knobMode, value);
                break;

            // Device: Select Previous Parameter Bank
            case DEVICE_SELECT_PREVIOUS_PARAMETER_BANK:
                if (isButtonPressed)
                    cursorDevice.getParameterBank ().selectPreviousPage ();
                break;
            // Device: Select Next Parameter Bank
            case DEVICE_SELECT_NEXT_PARAMETER_BANK:
                if (isButtonPressed)
                    cursorDevice.getParameterBank ().selectNextPage ();
                break;

            case DEVICE_SCROLL_PARAMETER_BANKS:
                this.scrollParameterBank (knobMode, value);
                break;

            // Device: Set Parameter 1-8
            case DEVICE_SET_PARAMETER_1, DEVICE_SET_PARAMETER_2, DEVICE_SET_PARAMETER_3, DEVICE_SET_PARAMETER_4, DEVICE_SET_PARAMETER_5, DEVICE_SET_PARAMETER_6, DEVICE_SET_PARAMETER_7, DEVICE_SET_PARAMETER_8:
                this.handleParameter (knobMode, command.ordinal () - FlexiCommand.DEVICE_SET_PARAMETER_1.ordinal (), value);
                break;

            // Device: Toggle Parameter 1-8
            case DEVICE_TOGGLE_PARAMETER_1, DEVICE_TOGGLE_PARAMETER_2, DEVICE_TOGGLE_PARAMETER_3, DEVICE_TOGGLE_PARAMETER_4, DEVICE_TOGGLE_PARAMETER_5, DEVICE_TOGGLE_PARAMETER_6, DEVICE_TOGGLE_PARAMETER_7, DEVICE_TOGGLE_PARAMETER_8:
                final IParameter toggleParam = cursorDevice.getParameterBank ().getItem (command.ordinal () - FlexiCommand.DEVICE_TOGGLE_PARAMETER_1.ordinal ());
                if (isButtonPressed)
                {
                    final int v = toggleParam.getValue ();
                    toggleParam.setValue (v > 0 ? 0 : this.model.getValueChanger ().getUpperBound () - 1);
                }
                break;

            // Device: Reset Parameter 1-8
            case DEVICE_RESET_PARAMETER_1, DEVICE_RESET_PARAMETER_2, DEVICE_RESET_PARAMETER_3, DEVICE_RESET_PARAMETER_4, DEVICE_RESET_PARAMETER_5, DEVICE_RESET_PARAMETER_6, DEVICE_RESET_PARAMETER_7, DEVICE_RESET_PARAMETER_8:
                if (isButtonPressed)
                    cursorDevice.getParameterBank ().getItem (command.ordinal () - FlexiCommand.DEVICE_RESET_PARAMETER_1.ordinal ()).resetValue ();
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private void scrollParameterPage (final KnobMode knobMode, final MidiValue value)
    {
        if (isAbsolute (knobMode) || !this.increaseKnobMovement ())
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        if (this.isIncrease (knobMode, value))
            parameterBank.scrollForwards ();
        else
            parameterBank.scrollBackwards ();
        this.mvHelper.notifySelectedParameterPage ();
    }


    private void scrollParameterBank (final KnobMode knobMode, final MidiValue value)
    {
        if (isAbsolute (knobMode) || !this.increaseKnobMovement ())
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        if (this.isIncrease (knobMode, value))
            parameterBank.selectNextPage ();
        else
            parameterBank.selectPreviousPage ();
        this.mvHelper.notifySelectedParameterPage ();
    }


    private void handleParameter (final KnobMode knobMode, final int index, final MidiValue value)
    {
        final IParameter fxParam = this.model.getCursorDevice ().getParameterBank ().getItem (index);
        final int val = value.getValue ();
        if (isAbsolute (knobMode))
            fxParam.setValue (this.getAbsoluteValueChanger (value), val);
        else
            fxParam.changeValue (this.getRelativeValueChanger (knobMode), val);
    }


    private void scrollDevice (final KnobMode knobMode, final MidiValue value)
    {
        if (isAbsolute (knobMode) || !this.increaseKnobMovement ())
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (this.isIncrease (knobMode, value))
            cursorDevice.selectNext ();
        else
            cursorDevice.selectPrevious ();
        this.mvHelper.notifySelectedDevice ();
    }
}
