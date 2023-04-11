// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.device;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Optional;


/**
 * Mode for navigating the browser.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceBrowserMode extends BaseMode<IItem>
{
    private static final int        SELECTION_OFF    = 0;
    private static final int        SELECTION_PRESET = 1;
    private static final int        SELECTION_FILTER = 2;

    private static final ColorEx [] COLORS           =
    {
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE,
        ColorEx.SKY_BLUE
    };

    private int                     selectionMode    = SELECTION_OFF;
    private int                     filterColumn     = -1;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DeviceBrowserMode (final MCUControlSurface surface, final IModel model)
    {
        super ("Browser", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        this.model.getBrowser ().stopBrowsing (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        int speed = this.model.getValueChanger ().calcSteppedKnobChange (value);
        final boolean direction = speed > 0;
        if (this.surface.isShiftPressed ())
            speed = speed * 4;

        speed = Math.abs (speed);
        if (direction)
            this.selectNext (index, speed);
        else
            this.selectPrevious (index, speed);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.surface.sendDisplayColor (COLORS);

        final IBrowser browser = this.model.getBrowser ();
        final ITextDisplay d = this.surface.getTextDisplay ();
        final boolean isPresetSession = browser.isPresetContentType ();
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (isPresetSession && !(browser.isActive () && cd.doesExist ()))
        {
            d.notify ("No active Browsing Session. Select device and press Browser...");
            return;
        }

        d.clear ();

        final int textLength = this.getTextLength ();
        switch (this.selectionMode)
        {
            case SELECTION_OFF:
                for (int i = 0; i < 7; i++)
                {
                    final Optional<IBrowserColumn> column = this.getFilterColumn (i);
                    String value = "";
                    String name = "";
                    if (column.isPresent ())
                    {
                        final IBrowserColumn browserColumn = column.get ();
                        if (browserColumn.doesCursorExist ())
                            value = browserColumn.getCursorName ().equals (browserColumn.getWildcard ()) ? "-" : browserColumn.getCursorName ();
                        name = StringUtils.shortenAndFixASCII (browserColumn.getName (), textLength);
                    }
                    d.setCell (0, i, name).setCell (1, i, value);
                }
                final String selectedResult = browser.getSelectedResult ();
                d.setCell (0, 7, browser.getSelectedContentType ()).setCell (1, 7, selectedResult == null || selectedResult.length () == 0 ? "-" : selectedResult);
                break;

            case SELECTION_PRESET:
                final IBrowserColumnItem [] results = browser.getResultColumnItems ();
                for (int i = 0; i < browser.getNumFilterColumnEntries (); i++)
                {
                    if (i < results.length)
                        d.setBlock (i / 4, i % 4, (results[i].isSelected () ? ">" : " ") + StringUtils.fixASCII (results[i].getName ()));
                    else
                        break;
                }
                break;

            case SELECTION_FILTER:
                final IBrowserColumnItem [] items = browser.getFilterColumn (this.filterColumn).getItems ();
                for (int i = 0; i < browser.getNumResults (); i++)
                    d.setBlock (i / 4, i % 4, (items[i].isSelected () ? ">" : " ") + StringUtils.fixASCII (items[i].getName ()));
                break;

            default:
                // Not used
                break;
        }
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    protected void resetParameter (final int index)
    {
        if (this.selectionMode != SELECTION_OFF)
        {
            this.selectionMode = SELECTION_OFF;
            return;
        }

        if (index == 7)
        {
            this.selectionMode = SELECTION_PRESET;
            this.filterColumn = -1;
        }
        else
        {
            final Optional<IBrowserColumn> fc = this.getFilterColumn (index);
            if (fc.isPresent () && fc.get ().doesExist ())
            {
                this.selectionMode = SELECTION_FILTER;
                this.filterColumn = fc.get ().getIndex ();
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        for (int i = 0; i < 8; i++)
            this.surface.setKnobLED (i, MCUControlSurface.KNOB_LED_MODE_WRAP, 0, 1);
    }


    private Optional<IBrowserColumn> getFilterColumn (final int index)
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
                    return Optional.of (browser.getFilterColumn (i));
            }
        }
        return Optional.empty ();
    }


    private void selectNext (final int index, final int count)
    {
        final IBrowser browser = this.model.getBrowser ();
        if (index < 7)
        {
            final Optional<IBrowserColumn> fc = this.getFilterColumn (index);
            if (fc.isPresent () && fc.get ().doesExist ())
            {
                this.filterColumn = fc.get ().getIndex ();
                for (int i = 0; i < count; i++)
                    browser.selectNextFilterItem (this.filterColumn);
                if (browser.getSelectedFilterItemIndex (this.filterColumn) == -1)
                    browser.nextFilterItemPage (this.filterColumn);
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
                final Optional<IBrowserColumn> fc = this.getFilterColumn (index);
                if (fc.isPresent () && fc.get ().doesExist ())
                {
                    this.filterColumn = fc.get ().getIndex ();
                    for (int j = 0; j < count; j++)
                        browser.selectPreviousFilterItem (this.filterColumn);
                    if (browser.getSelectedFilterItemIndex (this.filterColumn) == -1)
                        browser.previousFilterItemPage (this.filterColumn);
                }
            }
            else
            {
                for (int j = 0; j < count; j++)
                    browser.selectPreviousResult ();
            }
        }
    }
}