// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.device;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.PushDisplay;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for navigating the browser.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceBrowserMode extends BaseMode
{
    private static final int SELECTION_OFF    = 0;
    private static final int SELECTION_PRESET = 1;
    private static final int SELECTION_FILTER = 2;

    private int              selectionMode;
    private int              filterColumn;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceBrowserMode (final PushControlSurface surface, final IModel model)
    {
        super ("Browser", surface, model);

        this.isTemporary = false;

        this.selectionMode = SELECTION_OFF;
        this.filterColumn = -1;
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        this.model.getBrowser ().stopBrowsing (true);
    }


    /**
     * Change the value of the last selected column.
     *
     * @param value The change value
     */
    public void changeSelectedColumnValue (final int value)
    {
        final int index = this.filterColumn == -1 ? 7 : this.filterColumn;
        this.changeValue (index, value);
    }


    /**
     * Set the last selected column to the selection column.
     */
    public void resetFilterColumn ()
    {
        this.filterColumn = -1;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (!this.isKnobTouched[index])
            return;

        if (this.increaseKnobMovement ())
            this.changeValue (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        // Make sure that only 1 knob gets changed in browse mode to prevent weird behaviour
        for (int i = 0; i < this.isKnobTouched.length; i++)
            if (this.isKnobTouched[i] && i != index)
                return;

        this.isKnobTouched[index] = isTouched;

        IBrowserColumn fc;
        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
                fc = this.getFilterColumn (index);
                if (fc != null && fc.doesExist ())
                    this.model.getBrowser ().resetFilterColumn (fc.getIndex ());
                return;
            }
        }
        else
        {
            this.selectionMode = DeviceBrowserMode.SELECTION_OFF;
            return;
        }

        if (index == 7)
        {
            this.selectionMode = DeviceBrowserMode.SELECTION_PRESET;
            this.filterColumn = -1;
        }
        else
        {
            fc = this.getFilterColumn (index);
            if (fc != null && fc.doesExist ())
            {
                this.selectionMode = DeviceBrowserMode.SELECTION_FILTER;
                this.filterColumn = fc.getIndex ();
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (this.isPush2)
            this.selectNext (index, 1);
        else
            this.selectPrevious (index, 1);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (this.isPush2)
            this.selectPrevious (index, 1);
        else
            this.selectNext (index, 1);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
        {
            this.surface.getModeManager ().restoreMode ();
            return;
        }

        final Display d = this.surface.getDisplay ().clear ();

        switch (this.selectionMode)
        {
            case DeviceBrowserMode.SELECTION_OFF:
                final String selectedResult = browser.getSelectedResult ();
                final String deviceName = this.model.getCursorDevice ().getName ();
                String selectedContentType = browser.getSelectedContentType ();
                if (this.filterColumn == -1)
                    selectedContentType = PushDisplay.SELECT_ARROW + selectedContentType;

                d.setCell (0, 7, selectedContentType).setBlock (3, 0, " Selected Device:").setBlock (3, 1, deviceName.length () == 0 ? "None" : deviceName);
                final boolean isPresetSession = browser.isPresetContentType ();
                d.setBlock (3, 2, isPresetSession ? " Selected Preset:" : "").setBlock (3, 3, isPresetSession ? selectedResult == null || selectedResult.length () == 0 ? "None" : selectedResult : "");

                for (int i = 0; i < 7; i++)
                {
                    final IBrowserColumn column = this.getFilterColumn (i);
                    String name = column == null ? "" : StringUtils.shortenAndFixASCII (column.getName (), 8);
                    if (i == this.filterColumn)
                        name = PushDisplay.SELECT_ARROW + name;
                    d.setCell (0, i, name).setCell (1, i, getColumnName (column));
                }
                break;

            case DeviceBrowserMode.SELECTION_PRESET:
                final IBrowserColumnItem [] results = browser.getResultColumnItems ();

                if (!results[0].doesExist ())
                {
                    d.clear ().setBlock (1, 1, "       No results").setBlock (1, 2, "available...").allDone ();
                    return;
                }

                for (int i = 0; i < 16; i++)
                {
                    if (i < results.length)
                        d.setBlock (i % 4, i / 4, (results[i].isSelected () ? PushDisplay.SELECT_ARROW : " ") + results[i].getName (16));
                    else
                        d.setBlock (i % 4, i / 4, "");
                }
                break;

            case DeviceBrowserMode.SELECTION_FILTER:
                final IBrowserColumnItem [] items = browser.getFilterColumn (this.filterColumn).getItems ();
                for (int i = 0; i < 16; i++)
                {
                    String text = (items[i].isSelected () ? PushDisplay.SELECT_ARROW : " ") + items[i].getName () + "                ";
                    if (!items[i].getName ().isEmpty ())
                    {
                        final String hitStr = "(" + items[i].getHitCount () + ")";
                        text = text.substring (0, 17 - hitStr.length ()) + hitStr;
                    }
                    d.setBlock (i % 4, i / 4, text);
                }
                break;

            default:
                // Not used
                break;
        }
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
        {
            this.surface.getModeManager ().restoreMode ();
            return;
        }

        final DisplayModel message = this.surface.getDisplay ().getModel ();
        switch (this.selectionMode)
        {
            case DeviceBrowserMode.SELECTION_OFF:
                String selectedResult = browser.getSelectedResult ();
                if (selectedResult == null || selectedResult.length () == 0)
                    selectedResult = "None";
                final boolean isPresetSession = browser.isPresetContentType ();
                final String deviceName = this.model.getCursorDevice ().getName ();
                for (int i = 0; i < 7; i++)
                {
                    final IBrowserColumn column = this.getFilterColumn (i);
                    final String headerTopName = i == 0 ? "Device: " + (deviceName.isEmpty () ? "None" : deviceName) : "";
                    final String headerBottomName = i == 0 && isPresetSession ? "Preset: " + selectedResult : "";
                    final String menuBottomName = getColumnName (column);
                    message.addOptionElement (headerTopName, column == null ? "" : column.getName (), i == this.filterColumn, headerBottomName, menuBottomName, !menuBottomName.equals (" "), false);
                }
                message.addOptionElement ("", browser.getSelectedContentType (), this.filterColumn == -1, "", "", false, false);
                break;

            case DeviceBrowserMode.SELECTION_PRESET:
                final IBrowserColumnItem [] results = browser.getResultColumnItems ();

                if (!results[0].doesExist ())
                {
                    for (int i = 0; i < 8; i++)
                        message.addOptionElement (i == 3 ? "No results available..." : "", "", false, "", "", false, false);
                    message.send ();
                    return;
                }

                for (int i = 0; i < 8; i++)
                {
                    final String [] items = new String [6];
                    final boolean [] selected = new boolean [6];
                    for (int item = 0; item < 6; item++)
                    {
                        final int pos = i * 6 + item;
                        items[item] = pos < results.length ? results[pos].getName (16) : "";
                        selected[item] = pos < results.length && results[pos].isSelected ();
                    }
                    message.addListElement (items, selected);
                }
                break;

            case DeviceBrowserMode.SELECTION_FILTER:
                final IBrowserColumnItem [] item = browser.getFilterColumn (this.filterColumn).getItems ();
                for (int i = 0; i < 8; i++)
                {
                    final String [] items = new String [6];
                    final boolean [] selected = new boolean [6];
                    for (int itemIndex = 0; itemIndex < 6; itemIndex++)
                    {
                        final int pos = i * 6 + itemIndex;
                        String text = StringUtils.optimizeName (item[pos].getName (), 10);
                        if (!text.isEmpty ())
                            text = text + " (" + item[pos].getHitCount () + ")";
                        items[itemIndex] = text;
                        selected[itemIndex] = item[pos].isSelected ();
                    }
                    message.addListElement (items, selected);
                }
                break;

            default:
                // Not used
                break;
        }

        message.send ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        for (int i = 0; i < 7; i++)
        {
            final IBrowserColumn col = this.getFilterColumn (i);
            this.surface.updateButton (20 + i, col != null && col.doesExist () ? AbstractMode.BUTTON_COLOR_ON : AbstractMode.BUTTON_COLOR_OFF);
        }
        this.surface.updateButton (27, AbstractMode.BUTTON_COLOR_ON);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        for (int i = 0; i < 7; i++)
        {
            final IBrowserColumn col = this.getFilterColumn (i);
            this.surface.updateButton (102 + i, col != null && col.doesExist () ? AbstractMode.BUTTON_COLOR2_ON : AbstractMode.BUTTON_COLOR_OFF);
        }
        this.surface.updateButton (109, AbstractMode.BUTTON_COLOR2_ON);
    }


    private IBrowserColumn getFilterColumn (final int index)
    {
        final IBrowser browser = this.model.getBrowser ();
        int column = -1;
        final boolean [] browserDisplayFilter = this.surface.getConfiguration ().getBrowserDisplayFilter ();
        for (int i = 0; i < browser.getFilterColumnCount (); i++)
        {
            if (browserDisplayFilter[i])
            {
                column++;
                if (column == index)
                    return browser.getFilterColumn (i);
            }
        }
        return null;
    }


    private void selectNext (final int index, final int count)
    {
        final IBrowser browser = this.model.getBrowser ();
        if (index < 7)
        {
            final IBrowserColumn fc = this.getFilterColumn (index);
            if (fc != null && fc.doesExist ())
            {
                this.filterColumn = fc.getIndex ();
                for (int i = 0; i < count; i++)
                    browser.selectNextFilterItem (this.filterColumn);
                // TODO Bugfix required: getSelectedFilterItemIndex gets -1
                // https://github.com/teotigraphix/Framework4Bitwig/issues/220
                this.model.getHost ().scheduleTask ( () -> {
                    if (browser.getSelectedFilterItemIndex (this.filterColumn) == -1)
                        browser.nextFilterItemPage (this.filterColumn);
                }, 600);
            }
        }
        else
        {
            for (int i = 0; i < count; i++)
                browser.selectNextResult ();
        }
    }


    private void selectPrevious (final int index, final int count)
    {
        final IBrowser browser = this.model.getBrowser ();
        for (int i = 0; i < count; i++)
        {
            if (index < 7)
            {
                final IBrowserColumn fc = this.getFilterColumn (index);
                if (fc != null && fc.doesExist ())
                {
                    this.filterColumn = fc.getIndex ();
                    for (int j = 0; j < count; j++)
                        browser.selectPreviousFilterItem (this.filterColumn);
                    // TODO Bugfix required: getSelectedFilterItemIndex gets -1
                    // https://github.com/teotigraphix/Framework4Bitwig/issues/220
                    this.model.getHost ().scheduleTask ( () -> {
                        if (browser.getSelectedFilterItemIndex (this.filterColumn) == -1)
                            browser.previousFilterItemPage (this.filterColumn);
                    }, 600);
                }
            }
            else
            {
                for (int j = 0; j < count; j++)
                    browser.selectPreviousResult ();
            }
        }
    }


    private void changeValue (final int index, final int value)
    {
        int speed = (int) this.model.getValueChanger ().calcKnobSpeed (value, 1);
        final boolean direction = speed > 0;
        if (this.surface.isShiftPressed ())
            speed = speed * 4;

        speed = Math.abs (speed);
        if (direction)
            this.selectNext (index, speed);
        else
            this.selectPrevious (index, speed);
    }


    private static String getColumnName (final IBrowserColumn column)
    {
        if (column == null || !column.doesCursorExist ())
            return "";
        return column.getCursorName ().equals (column.getWildcard ()) ? " " : column.getCursorName (12);
    }
}