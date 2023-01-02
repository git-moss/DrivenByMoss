// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.mode.device;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.controller.novation.sl.controller.SLDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Browser mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DevicePresetsMode extends AbstractParameterMode<SLControlSurface, SLConfiguration, IItem>
{
    /** No selection. */
    public static final int  SELECTION_OFF    = 0;
    private static final int SELECTION_PRESET = 1;
    private static final int SELECTION_FILTER = 2;

    private int              selectionMode;
    private int              filterColumn;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DevicePresetsMode (final SLControlSurface surface, final IModel model)
    {
        super ("Browser", surface, model);

        this.selectionMode = SELECTION_OFF;
        this.filterColumn = 0;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        switch (this.selectionMode)
        {
            case DevicePresetsMode.SELECTION_OFF:
                if (index < 2)
                    this.navigatePresets (index == 1);
                else
                {
                    this.selectionMode = DevicePresetsMode.SELECTION_FILTER;
                    this.filterColumn = index - 2;
                }
                break;

            case DevicePresetsMode.SELECTION_PRESET:
                switch (index)
                {
                    // Down
                    case 0:
                        this.navigatePresets (false);
                        break;
                    // Up
                    case 1:
                        this.navigatePresets (true);
                        break;
                    // Commit
                    case 7:
                        this.model.getBrowser ().stopBrowsing (true);
                        this.surface.getModeManager ().setActive (Modes.TRACK_DETAILS);
                        this.selectionMode = DevicePresetsMode.SELECTION_OFF;
                        break;
                    // All other buttons return to Browse
                    default:
                        this.selectionMode = DevicePresetsMode.SELECTION_OFF;
                        break;
                }
                break;

            case DevicePresetsMode.SELECTION_FILTER:
                switch (index)
                {
                    // Down
                    case 0:
                        this.navigateFilters (this.filterColumn, false);
                        break;
                    // Up
                    case 1:
                        this.navigateFilters (this.filterColumn, true);
                        break;
                    // All other buttons return to Browse
                    default:
                        this.selectionMode = DevicePresetsMode.SELECTION_OFF;
                        break;
                }
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        if (!this.model.hasSelectedDevice ())
        {
            d.setRow (0, "                       Please select a device...                       ");
            d.done (0).done (1);
            return;
        }

        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
        {
            d.setRow (0, "                     No active Browsing Session.                       ");
            d.setRow (1, "                        Press Browse again...                          ");
            d.done (0).done (1);
            return;
        }

        String selectedResult;
        switch (this.selectionMode)
        {
            case DevicePresetsMode.SELECTION_OFF:
                selectedResult = browser.getSelectedResult ();
                d.setBlock (0, 0, "Preset:").setBlock (1, 0, selectedResult == null ? "None" : selectedResult);
                for (int i = 0; i < 6; i++)
                {
                    final IBrowserColumn column = browser.getFilterColumn (i);
                    final String columnName = column.doesExist () ? StringUtils.shortenAndFixASCII (column.getName () + ":", 8) : "";
                    d.setCell (0, 2 + i, columnName).setCell (1, 2 + i, column.doesCursorExist () ? column.getCursorName () : "");
                }
                break;

            case DevicePresetsMode.SELECTION_PRESET:
                final IBrowserColumnItem [] results = browser.getResultColumnItems ();
                for (int i = 0; i < 16; i++)
                    d.setCell (i % 2, i / 2, (results[i].isSelected () ? SLDisplay.RIGHT_ARROW : " ") + results[i].getName ());
                break;

            case DevicePresetsMode.SELECTION_FILTER:
                final IBrowserColumnItem [] items = browser.getFilterColumn (this.filterColumn).getItems ();
                for (int i = 0; i < 16; i++)
                {
                    final String name = StringUtils.fixASCII (items[i].getName ());
                    String text = (items[i].isSelected () ? SLDisplay.RIGHT_ARROW : " ") + name + "                ";
                    if (!name.isEmpty ())
                    {
                        final String hitStr = "(" + items[i].getHitCount () + ")";
                        text = text.substring (0, 17 - hitStr.length ()) + hitStr;
                    }
                    d.setCell (i % 2, i / 2, text);
                }
                break;

            default:
                // Not used
                break;
        }
        d.done (0).done (1);
    }


    /**
     * Navigate to the next or previous result item.
     *
     * @param moveUp True to move up
     */
    public void navigatePresets (final boolean moveUp)
    {
        this.selectionMode = DevicePresetsMode.SELECTION_PRESET;
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;
        if (moveUp)
            browser.selectNextResult ();
        else
            browser.selectPreviousResult ();
    }


    /**
     * Navigate to the next or previous filter item.
     *
     * @param filterNumber The number of the filter column
     * @param moveUp True to move up
     */
    public void navigateFilters (final int filterNumber, final boolean moveUp)
    {
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;
        if (moveUp)
            browser.selectNextFilterItem (this.filterColumn);
        else
            browser.selectPreviousFilterItem (this.filterColumn);
    }


    /**
     * Get the current selection mode.
     *
     * @return The current selection mode
     */
    public int getSelectionMode ()
    {
        return this.selectionMode;
    }
}