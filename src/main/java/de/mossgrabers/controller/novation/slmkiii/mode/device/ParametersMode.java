// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
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

import java.util.Optional;


/**
 * Mode for editing device remote control parameters.
 *
 * @author Jürgen Moßgraber
 */
public class ParametersMode extends AbstractParametersMode<IItem>
{
    private boolean                                                           showDevices;

    private final BrowserCommand<SLMkIIIControlSurface, SLMkIIIConfiguration> browserCommand;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ParametersMode (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Parameters", surface, model, null);

        this.setShowDevices (true);

        this.browserCommand = new BrowserCommand<> (model, surface);

        final IParameterProvider parameterProvider = new BankParameterProvider (this.model.getCursorDevice ().getParameterBank ());
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
        this.showDevices = showDevices;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        this.switchBanks ((IBank<IItem>) (this.showDevices ? cursorDevice.getDeviceBank () : cursorDevice.getParameterBank ()));
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

        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
            return;

        // Normal behavior - parameters
        if (!this.showDevices)
        {
            final IParameterPageBank parameterPageBank = cd.getParameterPageBank ();
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
            cd.duplicate ();
            return;
        }

        // Combination with Delete
        if (this.surface.isPressed (ButtonID.DELETE))
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            cd.getDeviceBank ().getItem (index).remove ();
            return;
        }

        // Normal behavior - devices
        if (cd.getIndex () == index)
            this.setShowDevices (!this.isShowDevices ());
        else
            cd.getDeviceBank ().getItem (index).select ();
    }


    /**
     * Handle button presses in combination with Shift.
     *
     * @param index The index of the button
     */
    private void onButtonShifted (final int index)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        switch (index)
        {
            case 0:
                if (cd.doesExist ())
                    cd.toggleEnabledState ();
                break;
            case 1:
                if (cd.doesExist ())
                    cd.toggleParameterPageSectionVisible ();
                break;
            case 2:
                if (cd.doesExist ())
                    cd.toggleExpanded ();
                break;
            case 3:
                if (cd.doesExist ())
                    cd.toggleWindowOpen ();
                break;
            case 4:
                if (cd.doesExist ())
                    cd.togglePinned ();
                break;
            case 5:
                if (cd.doesExist ())
                    this.browserCommand.startBrowser (true, true);
                break;
            case 6:
                if (cd.doesExist ())
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

        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
            return 0;

        // Colors normal behavior
        final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();
        if (this.showDevices)
        {
            final IDeviceBank bank = cd.getDeviceBank ();
            final IDevice item = bank.getItem (index);
            if (!item.doesExist ())
                return SLMkIIIColorManager.SLMKIII_BLACK;
            return index == cd.getIndex () ? SLMkIIIColorManager.SLMKIII_MINT : SLMkIIIColorManager.SLMKIII_MINT_HALF;
        }

        final IParameterPageBank bank = cd.getParameterPageBank ();
        final int selectedItemIndex = bank.getSelectedItemIndex ();
        if (bank.getItem (index).isEmpty ())
            return SLMkIIIColorManager.SLMKIII_BLACK;
        return index == selectedItemIndex ? SLMkIIIColorManager.SLMKIII_PURPLE : SLMkIIIColorManager.SLMKIII_PURPLE_HALF;
    }


    /**
     * Get the button color if Shift is pressed.
     *
     * @param buttonID The button ID
     * @return The button color
     */
    private int getButtonColorShifted (final ButtonID buttonID)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
        {
            if (buttonID == ButtonID.ROW1_8 && this.model.getCursorTrack ().doesExist ())
                return SLMkIIIColorManager.SLMKIII_RED_HALF;
            return SLMkIIIColorManager.SLMKIII_BLACK;
        }

        switch (buttonID)
        {
            case ROW1_1:
                return cd.isEnabled () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
            case ROW1_2:
                return cd.isParameterPageSectionVisible () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
            case ROW1_3:
                return cd.isExpanded () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
            case ROW1_4:
                return cd.isWindowOpen () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
            case ROW1_5:
                return cd.isPinned () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
            case ROW1_6:
            case ROW1_7:
            case ROW1_8:
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

        final ICursorDevice cd = this.model.getCursorDevice ();
        if (!cd.doesExist ())
        {
            d.setBlock (1, 1, " Please  select or").setBlock (1, 2, "add a    device.");
            d.setCell (0, 8, "No device");

            d.hideAllElements ();

            // Row 4
            this.drawRow4 (d, cd, null);
        }
        else
        {
            final IParameterPageBank parameterPageBank = cd.getParameterPageBank ();
            final Optional<String> selectedPage = parameterPageBank.getSelectedItem ();
            d.setCell (0, 8, cd.getName (9)).setCell (1, 8, selectedPage.isPresent () ? selectedPage.get () : "");

            // Row 1 & 2
            for (int i = 0; i < 8; i++)
            {
                final IParameterBank parameterBank = cd.getParameterBank ();
                final IParameter param = parameterBank.getItem (i);
                d.setCell (0, i, param.doesExist () ? StringUtils.fixASCII (param.getName (9)) : "").setCell (1, i, param.getDisplayedValue (9));

                final int color = param.doesExist () ? SLMkIIIColorManager.SLMKIII_PURPLE : SLMkIIIColorManager.SLMKIII_BLACK;
                d.setPropertyColor (i, 0, color);
                d.setPropertyColor (i, 1, color);
            }

            // Row 4
            this.drawRow4 (d, cd, parameterPageBank);
        }

        this.setButtonInfo (d);
        d.allDone ();
    }


    private void drawRow4 (final SLMkIIIDisplay d, final ICursorDevice cd, final IParameterPageBank parameterPageBank)
    {
        if (this.surface.isShiftPressed ())
        {
            this.drawRow4Shifted (d, cd);
            return;
        }

        if (this.surface.isLongPressed (ButtonID.ARROW_UP))
        {
            this.drawRow4ArrowUp (d);
            return;
        }

        if (!cd.doesExist ())
        {
            for (int i = 0; i < 8; i++)
            {
                d.setPropertyColor (i, 2, SLMkIIIColorManager.SLMKIII_BLACK);
                d.setPropertyValue (i, 1, 0);
            }
            return;
        }

        if (this.showDevices)
        {
            final IDeviceBank deviceBank = cd.getDeviceBank ();
            for (int i = 0; i < 8; i++)
            {
                final IDevice device = deviceBank.getItem (i);
                final StringBuilder sb = new StringBuilder ();
                if (device.doesExist ())
                    sb.append (device.getName (9));
                d.setCell (3, i, sb.toString ());

                d.setPropertyColor (i, 2, device.doesExist () ? SLMkIIIColorManager.SLMKIII_MINT : SLMkIIIColorManager.SLMKIII_BLACK);
                d.setPropertyValue (i, 1, device.doesExist () && i == cd.getIndex () ? 1 : 0);
            }
        }
        else
        {
            for (int i = 0; i < parameterPageBank.getPageSize (); i++)
            {
                final String item = parameterPageBank.getItem (i);
                d.setCell (3, i, item.isEmpty () ? "" : item);

                d.setPropertyColor (i, 2, item.isEmpty () ? SLMkIIIColorManager.SLMKIII_BLACK : SLMkIIIColorManager.SLMKIII_PURPLE);
                d.setPropertyValue (i, 1, parameterPageBank.getSelectedItemIndex () == i ? 1 : 0);
            }
        }
    }


    private void drawRow4Shifted (final SLMkIIIDisplay d, final ICursorDevice cd)
    {
        if (cd.doesExist ())
        {
            d.setCell (3, 0, "On/Off");
            d.setPropertyColor (0, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (0, 1, cd.isEnabled () ? 1 : 0);

            d.setCell (3, 1, "Params");
            d.setPropertyColor (1, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (1, 1, cd.isParameterPageSectionVisible () ? 1 : 0);

            d.setCell (3, 2, "Expanded");
            d.setPropertyColor (2, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (2, 1, cd.isExpanded () ? 1 : 0);

            d.setCell (3, 3, "Window");
            d.setPropertyColor (3, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (3, 1, cd.isWindowOpen () ? 1 : 0);

            d.setCell (3, 4, "Pin");
            d.setPropertyColor (4, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (4, 1, cd.isPinned () ? 1 : 0);

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
        return SLMkIIIColorManager.SLMKIII_PURPLE;
    }
}