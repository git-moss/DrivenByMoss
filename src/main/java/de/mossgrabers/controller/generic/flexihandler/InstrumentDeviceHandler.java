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
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.parameter.IParameter;


/**
 * The handler for the 1st instrument device commands.
 *
 * @author Jürgen Moßgraber
 */
public class InstrumentDeviceHandler extends AbstractHandler
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
    public InstrumentDeviceHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_WINDOW,
            FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_BYPASS,
            FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_EXPAND,
            FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_PARAMETERS,
            FlexiCommand.INSTRUMENT_DEVICE_SELECT_PREVIOUS_PARAMETER_PAGE,
            FlexiCommand.INSTRUMENT_DEVICE_SELECT_NEXT_PARAMETER_PAGE,
            FlexiCommand.INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_1,
            FlexiCommand.INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_2,
            FlexiCommand.INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_3,
            FlexiCommand.INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_4,
            FlexiCommand.INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_5,
            FlexiCommand.INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_6,
            FlexiCommand.INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_7,
            FlexiCommand.INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_8,
            FlexiCommand.INSTRUMENT_DEVICE_SCROLL_PARAMETER_PAGES,
            FlexiCommand.INSTRUMENT_DEVICE_SELECT_PREVIOUS_PARAMETER_BANK,
            FlexiCommand.INSTRUMENT_DEVICE_SELECT_NEXT_PARAMETER_BANK,
            FlexiCommand.INSTRUMENT_DEVICE_SCROLL_PARAMETER_BANKS,
            FlexiCommand.INSTRUMENT_DEVICE_SET_PARAMETER_1,
            FlexiCommand.INSTRUMENT_DEVICE_SET_PARAMETER_2,
            FlexiCommand.INSTRUMENT_DEVICE_SET_PARAMETER_3,
            FlexiCommand.INSTRUMENT_DEVICE_SET_PARAMETER_4,
            FlexiCommand.INSTRUMENT_DEVICE_SET_PARAMETER_5,
            FlexiCommand.INSTRUMENT_DEVICE_SET_PARAMETER_6,
            FlexiCommand.INSTRUMENT_DEVICE_SET_PARAMETER_7,
            FlexiCommand.INSTRUMENT_DEVICE_SET_PARAMETER_8,
            FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_PARAMETER_1,
            FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_PARAMETER_2,
            FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_PARAMETER_3,
            FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_PARAMETER_4,
            FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_PARAMETER_5,
            FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_PARAMETER_6,
            FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_PARAMETER_7,
            FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_PARAMETER_8,
            FlexiCommand.INSTRUMENT_DEVICE_RESET_PARAMETER_2,
            FlexiCommand.INSTRUMENT_DEVICE_RESET_PARAMETER_3,
            FlexiCommand.INSTRUMENT_DEVICE_RESET_PARAMETER_4,
            FlexiCommand.INSTRUMENT_DEVICE_RESET_PARAMETER_5,
            FlexiCommand.INSTRUMENT_DEVICE_RESET_PARAMETER_6,
            FlexiCommand.INSTRUMENT_DEVICE_RESET_PARAMETER_7,
            FlexiCommand.INSTRUMENT_DEVICE_RESET_PARAMETER_8
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final ISpecificDevice device = this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);

        switch (command)
        {
            case INSTRUMENT_DEVICE_TOGGLE_WINDOW:
                return toMidiValue (device.isWindowOpen ());

            case INSTRUMENT_DEVICE_TOGGLE_BYPASS:
                return toMidiValue (!device.isEnabled ());

            case INSTRUMENT_DEVICE_TOGGLE_EXPAND:
                return toMidiValue (device.isExpanded ());

            case INSTRUMENT_DEVICE_TOGGLE_PARAMETERS:
                return toMidiValue (device.isParameterPageSectionVisible ());

            case INSTRUMENT_DEVICE_SET_PARAMETER_1, INSTRUMENT_DEVICE_SET_PARAMETER_2, INSTRUMENT_DEVICE_SET_PARAMETER_3, INSTRUMENT_DEVICE_SET_PARAMETER_4, INSTRUMENT_DEVICE_SET_PARAMETER_5, INSTRUMENT_DEVICE_SET_PARAMETER_6, INSTRUMENT_DEVICE_SET_PARAMETER_7, INSTRUMENT_DEVICE_SET_PARAMETER_8:
                return device.getParameterBank ().getItem (command.ordinal () - FlexiCommand.INSTRUMENT_DEVICE_SET_PARAMETER_1.ordinal ()).getValue ();

            case INSTRUMENT_DEVICE_TOGGLE_PARAMETER_1, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_2, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_3, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_4, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_5, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_6, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_7, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_8:
                final int value = device.getParameterBank ().getItem (command.ordinal () - FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_PARAMETER_1.ordinal ()).getValue ();
                return toMidiValue (value > 0);

            case INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_1, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_2, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_3, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_4, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_5, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_6, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_7, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_8:
                return device.getParameterBank ().getItem (command.ordinal () - FlexiCommand.INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_1.ordinal ()).isSelected () ? 127 : 0;

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final ISpecificDevice device = this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);
        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);

        switch (command)
        {
            // Device: Toggle Window
            case INSTRUMENT_DEVICE_TOGGLE_WINDOW:
                if (isButtonPressed)
                    device.toggleWindowOpen ();
                break;
            // Device: Bypass
            case INSTRUMENT_DEVICE_TOGGLE_BYPASS:
                if (isButtonPressed)
                    device.toggleEnabledState ();
                break;
            // Device: Expand
            case INSTRUMENT_DEVICE_TOGGLE_EXPAND:
                if (isButtonPressed)
                    device.toggleExpanded ();
                break;
            // Device: Parameters
            case INSTRUMENT_DEVICE_TOGGLE_PARAMETERS:
                if (isButtonPressed)
                    device.toggleParameterPageSectionVisible ();
                break;

            case INSTRUMENT_DEVICE_SELECT_PREVIOUS_PARAMETER_PAGE:
                if (isButtonPressed)
                    device.getParameterBank ().scrollBackwards ();
                break;
            case INSTRUMENT_DEVICE_SELECT_NEXT_PARAMETER_PAGE:
                if (isButtonPressed)
                    device.getParameterBank ().scrollForwards ();
                break;

            case INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_1, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_2, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_3, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_4, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_5, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_6, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_7, INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_8:
                if (isButtonPressed)
                {
                    device.getParameterBank ().getPageBank ().selectPage (command.ordinal () - FlexiCommand.INSTRUMENT_DEVICE_SELECT_PARAMETER_PAGE_1.ordinal ());
                    this.mvHelper.notifySelectedDeviceAndParameterPage ();
                }
                break;

            case INSTRUMENT_DEVICE_SCROLL_PARAMETER_PAGES:
                this.scrollParameterPage (knobMode, value);
                break;

            // Device: Select Previous Parameter Bank
            case INSTRUMENT_DEVICE_SELECT_PREVIOUS_PARAMETER_BANK:
                if (isButtonPressed)
                    device.getParameterBank ().selectPreviousPage ();
                break;
            // Device: Select Next Parameter Bank
            case INSTRUMENT_DEVICE_SELECT_NEXT_PARAMETER_BANK:
                if (isButtonPressed)
                    device.getParameterBank ().selectNextPage ();
                break;

            case INSTRUMENT_DEVICE_SCROLL_PARAMETER_BANKS:
                this.scrollParameterBank (knobMode, value);
                break;

            // Device: Set Parameter 1-8
            case INSTRUMENT_DEVICE_SET_PARAMETER_1, INSTRUMENT_DEVICE_SET_PARAMETER_2, INSTRUMENT_DEVICE_SET_PARAMETER_3, INSTRUMENT_DEVICE_SET_PARAMETER_4, INSTRUMENT_DEVICE_SET_PARAMETER_5, INSTRUMENT_DEVICE_SET_PARAMETER_6, INSTRUMENT_DEVICE_SET_PARAMETER_7, INSTRUMENT_DEVICE_SET_PARAMETER_8:
                this.handleParameter (knobMode, command.ordinal () - FlexiCommand.INSTRUMENT_DEVICE_SET_PARAMETER_1.ordinal (), value);
                break;

            // Device: Toggle Parameter 1-8
            case INSTRUMENT_DEVICE_TOGGLE_PARAMETER_1, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_2, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_3, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_4, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_5, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_6, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_7, INSTRUMENT_DEVICE_TOGGLE_PARAMETER_8:
                final IParameter toggleParam = device.getParameterBank ().getItem (command.ordinal () - FlexiCommand.INSTRUMENT_DEVICE_TOGGLE_PARAMETER_1.ordinal ());
                if (isButtonPressed)
                {
                    final int v = toggleParam.getValue ();
                    toggleParam.setValue (v > 0 ? 0 : this.model.getValueChanger ().getUpperBound () - 1);
                }
                break;

            // Device: Reset Parameter 1-8
            case INSTRUMENT_DEVICE_RESET_PARAMETER_1, INSTRUMENT_DEVICE_RESET_PARAMETER_2, INSTRUMENT_DEVICE_RESET_PARAMETER_3, INSTRUMENT_DEVICE_RESET_PARAMETER_4, INSTRUMENT_DEVICE_RESET_PARAMETER_5, INSTRUMENT_DEVICE_RESET_PARAMETER_6, INSTRUMENT_DEVICE_RESET_PARAMETER_7, INSTRUMENT_DEVICE_RESET_PARAMETER_8:
                if (isButtonPressed)
                    device.getParameterBank ().getItem (command.ordinal () - FlexiCommand.INSTRUMENT_DEVICE_RESET_PARAMETER_1.ordinal ()).resetValue ();
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private void scrollParameterPage (final KnobMode knobMode, final MidiValue value)
    {
        if (isAbsolute (knobMode) || !this.increaseKnobMovement ())
            return;

        final ISpecificDevice device = this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);
        final IParameterBank parameterBank = device.getParameterBank ();
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

        final ISpecificDevice device = this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);
        final IParameterBank parameterBank = device.getParameterBank ();
        if (this.isIncrease (knobMode, value))
            parameterBank.selectNextPage ();
        else
            parameterBank.selectPreviousPage ();
        this.mvHelper.notifySelectedParameterPage ();
    }


    private void handleParameter (final KnobMode knobMode, final int index, final MidiValue value)
    {
        final IParameter fxParam = this.model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT).getParameterBank ().getItem (index);
        final int val = value.getValue ();
        if (isAbsolute (knobMode))
            fxParam.setValue (this.getAbsoluteValueChanger (value), val);
        else
            fxParam.changeValue (this.getRelativeValueChanger (knobMode), val);
    }
}
