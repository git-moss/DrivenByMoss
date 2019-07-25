// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.mode.device;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.controller.slmkiii.mode.BaseMode;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDeviceBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.IParameterPageBank;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing device remote control parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParametersMode extends BaseMode
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
        super ("Parameters", surface, model);

        this.isTemporary = false;
        this.showDevices = true;

        this.browserCommand = new BrowserCommand<> (Modes.MODE_BROWSER, model, surface);
    }


    /**
     * Toggle showing devices or the parameter banks of the cursor device for selection.
     */
    public void toggleShowDevices ()
    {
        this.showDevices = !this.showDevices;
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
    public void onKnobValue (final int index, final int value)
    {
        final IParameter param = this.model.getCursorDevice ().getParameterBank ().getItem (index);
        if (this.surface.isDeletePressed ())
            param.resetValue ();
        else
            param.changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final ICursorDevice cd = this.model.getCursorDevice ();

        if (this.surface.isShiftPressed ())
        {
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
                    if (this.model.getSelectedTrack () != null)
                        this.browserCommand.startBrowser (true, false);
                    break;
                default:
                    // Not used
                    break;
            }
            return;
        }

        if (!cd.doesExist ())
            return;

        if (!this.showDevices)
        {
            final IParameterPageBank parameterPageBank = cd.getParameterPageBank ();
            if (parameterPageBank.getSelectedItemIndex () == index)
                this.toggleShowDevices ();
            else
                parameterPageBank.selectPage (index);
            return;
        }

        if (this.surface.isPressed (SLMkIIIControlSurface.MKIII_DUPLICATE))
        {
            this.surface.setTriggerConsumed (SLMkIIIControlSurface.MKIII_DUPLICATE);
            cd.duplicate ();
            return;
        }

        if (this.surface.isPressed (SLMkIIIControlSurface.MKIII_CLEAR))
        {
            this.surface.setTriggerConsumed (SLMkIIIControlSurface.MKIII_CLEAR);
            cd.getDeviceBank ().getItem (index).remove ();
            return;
        }

        if (cd.getIndex () == index)
            this.toggleShowDevices ();
        else
            cd.getDeviceBank ().getItem (index).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final ICursorDevice cd = this.model.getCursorDevice ();

        if (this.surface.isShiftPressed ())
        {
            if (!cd.doesExist ())
            {
                for (int i = 0; i < 7; i++)
                    this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_1 + i, SLMkIIIColors.SLMKIII_BLACK);
                if (this.model.getSelectedTrack () != null)
                    this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_8, SLMkIIIColors.SLMKIII_RED_HALF);
            }
            else
            {
                this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_1, cd.isEnabled () ? SLMkIIIColors.SLMKIII_RED : SLMkIIIColors.SLMKIII_RED_HALF);
                this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_2, cd.isParameterPageSectionVisible () ? SLMkIIIColors.SLMKIII_RED : SLMkIIIColors.SLMKIII_RED_HALF);
                this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_3, cd.isExpanded () ? SLMkIIIColors.SLMKIII_RED : SLMkIIIColors.SLMKIII_RED_HALF);
                this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_4, cd.isWindowOpen () ? SLMkIIIColors.SLMKIII_RED : SLMkIIIColors.SLMKIII_RED_HALF);
                this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_5, cd.isPinned () ? SLMkIIIColors.SLMKIII_RED : SLMkIIIColors.SLMKIII_RED_HALF);
                this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_6, SLMkIIIColors.SLMKIII_RED_HALF);
                this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_7, SLMkIIIColors.SLMKIII_RED_HALF);
                this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_8, SLMkIIIColors.SLMKIII_RED_HALF);
            }

            return;
        }

        if (!cd.doesExist ())
        {
            this.disableFirstRow ();
            return;
        }

        if (this.showDevices)
        {
            final int selectedColor = SLMkIIIColors.SLMKIII_MINT;
            final int existsColor = SLMkIIIColors.SLMKIII_MINT_HALF;
            final int offColor = SLMkIIIColors.SLMKIII_BLACK;

            final IDeviceBank bank = cd.getDeviceBank ();
            for (int i = 0; i < bank.getPageSize (); i++)
            {
                final IDevice item = bank.getItem (i);
                this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_1 + i, item.doesExist () ? i == cd.getIndex () ? selectedColor : existsColor : offColor);
            }
        }
        else
        {
            final int selectedColor = SLMkIIIColors.SLMKIII_PURPLE;
            final int existsColor = SLMkIIIColors.SLMKIII_PURPLE_HALF;
            final int offColor = SLMkIIIColors.SLMKIII_BLACK;

            final IParameterPageBank bank = cd.getParameterPageBank ();
            final int selectedItemIndex = bank.getSelectedItemIndex ();
            for (int i = 0; i < bank.getPageSize (); i++)
            {
                final String item = bank.getItem (i);
                this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_1 + i, !item.isEmpty () ? i == selectedItemIndex ? selectedColor : existsColor : offColor);
            }
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
            final IValueChanger valueChanger = this.model.getValueChanger ();
            final IParameterPageBank parameterPageBank = cd.getParameterPageBank ();
            final String selectedPage = parameterPageBank.getSelectedItem ();
            d.setCell (0, 8, cd.getName (9)).setCell (1, 8, selectedPage);

            // Row 1 & 2
            for (int i = 0; i < 8; i++)
            {
                final IParameterBank parameterBank = cd.getParameterBank ();
                final IParameter param = parameterBank.getItem (i);
                d.setCell (0, i, param.doesExist () ? StringUtils.fixASCII (param.getName (9)) : "").setCell (1, i, param.getDisplayedValue (9));

                this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_KNOB_1 + i, 15, valueChanger.toMidiValue (param.getValue ()));

                final int color = param.doesExist () ? SLMkIIIColors.SLMKIII_PURPLE : SLMkIIIColors.SLMKIII_BLACK;
                d.setPropertyColor (i, 0, color);
                d.setPropertyColor (i, 1, color);
            }

            // Row 4
            this.drawRow4 (d, cd, parameterPageBank);
        }

        d.setPropertyColor (8, 0, SLMkIIIColors.SLMKIII_PURPLE);

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

        if (!cd.doesExist ())
        {
            for (int i = 0; i < 8; i++)
            {
                d.setPropertyColor (i, 2, SLMkIIIColors.SLMKIII_BLACK);
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

                d.setPropertyColor (i, 2, device.doesExist () ? SLMkIIIColors.SLMKIII_MINT : SLMkIIIColors.SLMKIII_BLACK);
                d.setPropertyValue (i, 1, device.doesExist () && i == cd.getIndex () ? 1 : 0);
            }
        }
        else
        {
            for (int i = 0; i < parameterPageBank.getPageSize (); i++)
            {
                final String item = parameterPageBank.getItem (i);
                d.setCell (3, i, item.isEmpty () ? "" : item);

                d.setPropertyColor (i, 2, item.isEmpty () ? SLMkIIIColors.SLMKIII_BLACK : SLMkIIIColors.SLMKIII_PURPLE);
                d.setPropertyValue (i, 1, parameterPageBank.getSelectedItemIndex () == i ? 1 : 0);
            }
        }
    }


    private void drawRow4Shifted (final SLMkIIIDisplay d, final ICursorDevice cd)
    {
        if (cd.doesExist ())
        {
            d.setCell (3, 0, "On/Off");
            d.setPropertyColor (0, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (0, 1, cd.isEnabled () ? 1 : 0);

            d.setCell (3, 1, "Params");
            d.setPropertyColor (1, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (1, 1, cd.isParameterPageSectionVisible () ? 1 : 0);

            d.setCell (3, 2, "Expanded");
            d.setPropertyColor (2, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (2, 1, cd.isExpanded () ? 1 : 0);

            d.setCell (3, 3, "Window");
            d.setPropertyColor (3, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (3, 1, cd.isWindowOpen () ? 1 : 0);

            d.setCell (3, 4, "Pin");
            d.setPropertyColor (4, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (4, 1, cd.isPinned () ? 1 : 0);

            d.setCell (3, 5, "<< Insert");
            d.setPropertyColor (5, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (5, 1, 0);

            d.setCell (3, 6, "Replace");
            d.setPropertyColor (6, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (6, 1, 0);
        }
        else
        {
            for (int i = 0; i < 7; i++)
            {
                d.setPropertyColor (i, 2, SLMkIIIColors.SLMKIII_BLACK);
                d.setPropertyValue (i, 1, 0);
            }
        }

        if (this.model.getSelectedTrack () != null)
        {
            d.setCell (3, 7, "Insert >>");
            d.setPropertyColor (7, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (7, 1, 0);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected IBank<? extends IItem> getBank ()
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (cursorDevice == null)
            return null;
        return this.showDevices ? cursorDevice.getDeviceBank () : cursorDevice.getParameterBank ();
    }
}