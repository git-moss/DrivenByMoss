// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.mode.device;

import de.mossgrabers.controller.novation.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.ResetParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing device remote control parameters of a device.
 *
 * @author Jürgen Moßgraber
 */
public class ParametersMode extends AbstractParametersMode<IItem>
{
    private boolean                                                           showDevices;

    private final BrowserCommand<SLMkIIIControlSurface, SLMkIIIConfiguration> browserCommand;

    private final ISpecificDevice                                             device;

    private final int                                                         color;
    private final int                                                         halfColor;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param deviceName The name of the device
     * @param device The device
     * @param color The color to use for the mode controls
     * @param halfColor The lighter color to use for the mode controls
     */
    public ParametersMode (final SLMkIIIControlSurface surface, final IModel model, final String deviceName, final ISpecificDevice device, final int color, final int halfColor)
    {
        super (deviceName + " Parameters", surface, model, null);

        this.device = device;
        this.color = color;
        this.halfColor = halfColor;

        this.setShowDevices (true);

        this.browserCommand = new BrowserCommand<> (model, surface);

        final IParameterProvider parameterProvider = new BankParameterProvider (device.getParameterBank ());
        this.setParameterProvider (parameterProvider);
        this.setParameterProvider (ButtonID.DELETE, new ResetParameterProvider (parameterProvider));
    }


    /**
     * Show devices or the parameter banks of the cursor device for selection.
     *
     * @param showDevices True to show devices otherwise parameters
     */
    @SuppressWarnings("unchecked")
    public final void setShowDevices (final boolean showDevices)
    {
        if (this.device instanceof final ICursorDevice cursorDevice)
        {
            this.showDevices = showDevices;
            this.switchBanks ((IBank<IItem>) (this.showDevices ? cursorDevice.getDeviceBank () : this.device.getParameterBank ()));
        }
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
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        // Combination with Shift
        if (this.surface.isShiftPressed ())
        {
            this.onButtonShifted (index);
            return;
        }

        // Combination with Arrow Up
        if (this.surface.isLongPressed (ButtonID.ARROW_UP))
        {
            this.onButtonArrowUp (index);
            return;
        }

        if (!this.device.doesExist ())
            return;

        // Normal behavior - parameters
        if (!this.showDevices)
        {
            final IParameterPageBank parameterPageBank = this.device.getParameterBank ().getPageBank ();
            if (parameterPageBank.getSelectedItemIndex () == index)
                this.setShowDevices (!this.isShowDevices ());
            else
                parameterPageBank.selectPage (index);
            return;
        }

        // Combination with Duplicate
        if (this.surface.isPressed (ButtonID.DUPLICATE))
        {
            this.surface.setTriggerConsumed (ButtonID.DUPLICATE);
            this.device.duplicate ();
            return;
        }

        // Combination with Delete
        if (this.surface.isPressed (ButtonID.DELETE))
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            if (this.device instanceof final ICursorDevice cursorDevice)
                cursorDevice.getDeviceBank ().getItem (index).remove ();
            return;
        }

        // Normal behavior - devices
        if (this.device.getIndex () == index)
            this.setShowDevices (!this.isShowDevices ());
        else if (this.device instanceof final ICursorDevice cursorDevice)
            cursorDevice.getDeviceBank ().getItem (index).select ();
    }


    /**
     * Handle button presses in combination with Shift.
     *
     * @param index The index of the button
     */
    private void onButtonShifted (final int index)
    {
        switch (index)
        {
            case 0:
                if (this.device.doesExist ())
                    this.device.toggleEnabledState ();
                break;
            case 1:
                if (this.device.doesExist ())
                    this.device.toggleParameterPageSectionVisible ();
                break;
            case 2:
                if (this.device.doesExist ())
                    this.device.toggleExpanded ();
                break;
            case 3:
                if (this.device.doesExist ())
                    this.device.toggleWindowOpen ();
                break;
            case 4:
                if (this.device.doesExist () && this.device instanceof final ICursorDevice cursorDevice)
                    cursorDevice.togglePinned ();
                break;
            case 5:
                if (this.device.doesExist ())
                    this.browserCommand.startBrowser (true, true);
                break;
            case 6:
                if (this.device.doesExist ())
                    this.browserCommand.startBrowser (false, false);
                break;
            case 7:
                if (this.model.getCursorTrack ().doesExist ())
                    this.browserCommand.startBrowser (true, false);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        // Colors in combination with Shift
        if (this.surface.isShiftPressed ())
            return this.getButtonColorShifted (buttonID);

        // Colors in combination with Arrow Up
        if (this.surface.isLongPressed (ButtonID.ARROW_UP))
            return this.getButtonColorArrowUp (buttonID);

        if (!this.device.doesExist ())
            return 0;

        // Colors normal behavior
        final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();
        if (this.showDevices && this.device instanceof final ICursorDevice cursorDevice)
        {
            final IDeviceBank bank = cursorDevice.getDeviceBank ();
            final IDevice item = bank.getItem (index);
            if (!item.doesExist ())
                return SLMkIIIColorManager.SLMKIII_BLACK;
            return index == this.device.getIndex () ? SLMkIIIColorManager.SLMKIII_MINT : SLMkIIIColorManager.SLMKIII_MINT_HALF;
        }

        final IParameterPageBank bank = this.device.getParameterBank ().getPageBank ();
        final int selectedItemIndex = bank.getSelectedItemIndex ();
        if (bank.getItem (index).isEmpty ())
            return SLMkIIIColorManager.SLMKIII_BLACK;
        return index == selectedItemIndex ? this.color : this.halfColor;
    }


    /**
     * Get the button color if Shift is pressed.
     *
     * @param buttonID The button ID
     * @return The button color
     */
    private int getButtonColorShifted (final ButtonID buttonID)
    {
        if (!this.device.doesExist ())
        {
            if (buttonID == ButtonID.ROW1_8 && this.model.getCursorTrack ().doesExist ())
                return SLMkIIIColorManager.SLMKIII_RED_HALF;
            return SLMkIIIColorManager.SLMKIII_BLACK;
        }

        switch (buttonID)
        {
            case ROW1_1:
                return this.device.isEnabled () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
            case ROW1_2:
                return this.device.isParameterPageSectionVisible () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
            case ROW1_3:
                return this.device.isExpanded () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
            case ROW1_4:
                return this.device.isWindowOpen () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
            case ROW1_5:
                return this.device instanceof final ICursorDevice cursorDevice && cursorDevice.isPinned () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
            case ROW1_6, ROW1_7, ROW1_8:
            default:
                return SLMkIIIColorManager.SLMKIII_RED_HALF;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();

        if (!this.device.doesExist ())
        {
            d.setBlock (1, 1, " Please  select or").setBlock (1, 2, "add a    device.");
            d.setCell (0, 8, this.model.getCursorTrack ().getName (8)).setCell (1, 8, "No device");

            d.hideAllElements ();

            // Row 4
            this.drawRow4 (d, null);
        }
        else
        {
            final IParameterBank parameterBank = this.device.getParameterBank ();
            final IParameterPageBank parameterPageBank = parameterBank.getPageBank ();
            d.setCell (0, 8, this.model.getCursorTrack ().getName (8)).setCell (1, 8, this.device.getName (9));

            // Row 1 & 2
            for (int i = 0; i < 8; i++)
            {
                final IParameter param = parameterBank.getItem (i);
                d.setCell (0, i, param.doesExist () ? StringUtils.fixASCII (param.getName (9)) : "").setCell (1, i, StringUtils.fixASCII (param.getDisplayedValue (9)));
                final int c = param.doesExist () ? this.color : SLMkIIIColorManager.SLMKIII_BLACK;
                d.setPropertyColor (i, 0, c);
                d.setPropertyColor (i, 1, c);
            }

            // Row 4
            this.drawRow4 (d, parameterPageBank);
        }

        this.setButtonInfo (d);
        d.allDone ();
    }


    private void drawRow4 (final SLMkIIIDisplay d, final IParameterPageBank parameterPageBank)
    {
        if (this.surface.isShiftPressed ())
        {
            this.drawRow4Shifted (d);
            return;
        }

        if (this.surface.isLongPressed (ButtonID.ARROW_UP))
        {
            this.drawRow4ArrowUp (d);
            return;
        }

        if (!this.device.doesExist ())
        {
            for (int i = 0; i < 8; i++)
            {
                d.setPropertyColor (i, 2, SLMkIIIColorManager.SLMKIII_BLACK);
                d.setPropertyValue (i, 1, 0);
            }
            return;
        }

        if (this.showDevices && this.device instanceof final ICursorDevice cursorDevice)
        {
            final IDeviceBank deviceBank = cursorDevice.getDeviceBank ();
            for (int i = 0; i < 8; i++)
            {
                final IDevice device = deviceBank.getItem (i);
                final StringBuilder sb = new StringBuilder ();
                if (device.doesExist ())
                    sb.append (device.getName (9));
                d.setCell (3, i, sb.toString ());

                d.setPropertyColor (i, 2, device.doesExist () ? SLMkIIIColorManager.SLMKIII_MINT : SLMkIIIColorManager.SLMKIII_BLACK);
                d.setPropertyValue (i, 1, device.doesExist () && i == cursorDevice.getIndex () ? 1 : 0);
            }
        }
        else
        {
            for (int i = 0; i < parameterPageBank.getPageSize (); i++)
            {
                final String item = parameterPageBank.getItem (i);
                d.setCell (3, i, item.isEmpty () ? "" : item);

                d.setPropertyColor (i, 2, item.isEmpty () ? SLMkIIIColorManager.SLMKIII_BLACK : this.color);
                d.setPropertyValue (i, 1, parameterPageBank.getSelectedItemIndex () == i ? 1 : 0);
            }
        }
    }


    private void drawRow4Shifted (final SLMkIIIDisplay d)
    {
        if (this.device.doesExist ())
        {
            d.setCell (3, 0, "On/Off");
            d.setPropertyColor (0, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (0, 1, this.device.isEnabled () ? 1 : 0);

            d.setCell (3, 1, "Params");
            d.setPropertyColor (1, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (1, 1, this.device.isParameterPageSectionVisible () ? 1 : 0);

            d.setCell (3, 2, "Expanded");
            d.setPropertyColor (2, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (2, 1, this.device.isExpanded () ? 1 : 0);

            d.setCell (3, 3, "Window");
            d.setPropertyColor (3, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (3, 1, this.device.isWindowOpen () ? 1 : 0);

            d.setCell (3, 4, "Pin");
            d.setPropertyColor (4, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (4, 1, this.device instanceof final ICursorDevice cursorDevice && cursorDevice.isPinned () ? 1 : 0);

            d.setCell (3, 5, "<< Insert");
            d.setPropertyColor (5, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (5, 1, 0);

            d.setCell (3, 6, "Replace");
            d.setPropertyColor (6, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (6, 1, 0);
        }
        else
        {
            for (int i = 0; i < 7; i++)
            {
                d.setPropertyColor (i, 2, SLMkIIIColorManager.SLMKIII_BLACK);
                d.setPropertyValue (i, 1, 0);
            }
        }

        if (this.model.getCursorTrack ().doesExist ())
        {
            d.setCell (3, 7, "Insert >>");
            d.setPropertyColor (7, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (7, 1, 0);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getModeColor ()
    {
        return this.color;
    }
}