// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;


/**
 * The handler for device commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceHandler extends AbstractHandler
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param relative2ValueChanger The relative value changer variant 2
     * @param relative3ValueChanger The relative value changer variant 3
     */
    public DeviceHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger relative2ValueChanger, final IValueChanger relative3ValueChanger)
    {
        super (model, surface, configuration, relative2ValueChanger, relative3ValueChanger);
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
            FlexiCommand.DEVICE_SET_PARAMETER_8
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

            case DEVICE_SET_PARAMETER_1:
            case DEVICE_SET_PARAMETER_2:
            case DEVICE_SET_PARAMETER_3:
            case DEVICE_SET_PARAMETER_4:
            case DEVICE_SET_PARAMETER_5:
            case DEVICE_SET_PARAMETER_6:
            case DEVICE_SET_PARAMETER_7:
            case DEVICE_SET_PARAMETER_8:
                return cursorDevice.getParameterBank ().getItem (command.ordinal () - FlexiCommand.DEVICE_SET_PARAMETER_1.ordinal ()).getValue ();

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final int knobMode, final int value)
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
            case DEVICE_SET_PARAMETER_1:
            case DEVICE_SET_PARAMETER_2:
            case DEVICE_SET_PARAMETER_3:
            case DEVICE_SET_PARAMETER_4:
            case DEVICE_SET_PARAMETER_5:
            case DEVICE_SET_PARAMETER_6:
            case DEVICE_SET_PARAMETER_7:
            case DEVICE_SET_PARAMETER_8:
                this.handleParameter (knobMode, command.ordinal () - FlexiCommand.DEVICE_SET_PARAMETER_1.ordinal (), value);
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private void scrollParameterPage (final int knobMode, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        if (!this.increaseKnobMovement ())
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        if (this.getRelativeSpeed (knobMode, value) > 0)
            parameterBank.scrollForwards ();
        else
            parameterBank.scrollBackwards ();
        this.mvHelper.notifySelectedParameterPage ();
    }


    private void scrollParameterBank (final int knobMode, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        if (!this.increaseKnobMovement ())
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        if (this.getRelativeSpeed (knobMode, value) > 0)
            parameterBank.selectNextPage ();
        else
            parameterBank.selectPreviousPage ();
        this.mvHelper.notifySelectedParameterPage ();
    }


    private void handleParameter (final int knobMode, final int index, final int value)
    {
        final IParameter fxParam = this.model.getCursorDevice ().getParameterBank ().getItem (index);
        if (isAbsolute (knobMode))
            fxParam.setValue (value);
        else
            fxParam.setValue (this.limit (fxParam.getValue () + this.getRelativeSpeed (knobMode, value)));
    }


    private void scrollDevice (final int knobMode, final int value)
    {
        if (isAbsolute (knobMode))
            return;

        if (!this.increaseKnobMovement ())
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (this.getRelativeSpeed (knobMode, value) > 0)
            cursorDevice.selectNext ();
        else
            cursorDevice.selectPrevious ();
        this.mvHelper.notifySelectedDevice ();
    }
}
