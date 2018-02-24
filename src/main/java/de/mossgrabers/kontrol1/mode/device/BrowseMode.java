// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1.mode.device;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.kontrol1.Kontrol1Configuration;
import de.mossgrabers.kontrol1.controller.Kontrol1ControlSurface;


/**
 * Browser mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowseMode extends AbstractMode<Kontrol1ControlSurface, Kontrol1Configuration>
{
    /** No selection. */
    public static final int  SELECTION_OFF    = 0;
    private static final int SELECTION_PRESET = 1;
    private static final int SELECTION_FILTER = 2;

    private int              selectionMode;
    private int              filterColumn;
    protected int            selectedColumn   = -1;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public BrowseMode (final Kontrol1ControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.isTemporary = false;
        this.selectionMode = SELECTION_OFF;
        this.filterColumn = 0;
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        if (!isTouched)
            return;

        this.selectedColumn = this.selectedColumn != -1 ? -1 : index;

        if (this.selectedColumn == -1)
        {
            this.selectionMode = BrowseMode.SELECTION_OFF;
            return;
        }

        if (index == 7)
        {
            this.selectionMode = BrowseMode.SELECTION_PRESET;
            this.filterColumn = -1;
        }
        else
        {
            final IBrowser browser = this.model.getBrowser ();
            final IBrowserColumn fc = browser.getFilterColumn (index);
            if (fc != null && fc.doesExist ())
            {
                this.selectionMode = BrowseMode.SELECTION_FILTER;
                this.filterColumn = fc.getIndex ();
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (!this.model.hasSelectedDevice ())
        {
            this.surface.getModeManager ().restoreMode ();
            return;
        }

        final Display d = this.surface.getDisplay ();
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
        {
            this.surface.getModeManager ().restoreMode ();
            return;
        }

        d.clear ();

        String selectedResult;
        switch (this.selectionMode)
        {
            case BrowseMode.SELECTION_OFF:
                d.setCell (0, 0, "BROWSE").setCell (1, 0, browser.getSelectedContentType ().toUpperCase ());
                selectedResult = browser.getSelectedResult ();
                d.setCell (0, 8, "SELECTED").setCell (1, 8, selectedResult == null ? "NONE" : selectedResult);
                for (int i = 0; i < 7; i++)
                {
                    final IBrowserColumn column = browser.getFilterColumn (i);
                    d.setCell (0, 1 + i, this.optimizeName (column.getName () + ":", 8).toUpperCase ()).setCell (1, 1 + i, column.doesCursorExist () ? column.getCursorName ().toUpperCase () : "");
                }
                break;

            case BrowseMode.SELECTION_PRESET:
                d.setCell (0, 0, "SELECTED");
                final IBrowserColumnItem [] results = browser.getResultColumnItems ();
                for (int i = 0; i < 16; i++)
                    d.setCell (i % 2, 1 + i / 2, (results[i].isSelected () ? ">" : " ") + results[i].getName ().toUpperCase ());
                break;

            case BrowseMode.SELECTION_FILTER:
                final IBrowserColumn fc = browser.getFilterColumn (this.filterColumn);
                d.setCell (0, 0, fc.getName ().toUpperCase ());
                final IBrowserColumnItem [] items = fc.getItems ();
                for (int i = 0; i < 16; i++)
                {
                    final String name = items[i].getName ().toUpperCase ();
                    final String text = (items[i].isSelected () ? ">" : " ") + name;
                    d.setCell (i % 2, 1 + i / 2, text);
                }
                break;
        }
        d.allDone ();
    }


    /**
     * Navigate to the next or previous result item.
     *
     * @param moveUp True to move up
     */
    public void navigatePresets (final boolean moveUp)
    {
        this.selectionMode = BrowseMode.SELECTION_PRESET;
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


    /**
     * Select the next filter or preset.
     *
     * @param count The number of items to increase
     */
    public void selectNext (final int count)
    {
        final int index = this.selectedColumn == -1 ? 7 : this.selectedColumn;

        final IBrowser browser = this.model.getBrowser ();
        if (index < 7)
        {
            final IBrowserColumn fc = browser.getFilterColumn (index);
            if (fc != null && fc.doesExist ())
            {
                this.filterColumn = fc.getIndex ();
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


    /**
     * Select the previous filter or preset.
     *
     * @param count The number of items to decrease
     */
    public void selectPrevious (final int count)
    {
        final int index = this.selectedColumn == -1 ? 7 : this.selectedColumn;

        final IBrowser browser = this.model.getBrowser ();
        for (int i = 0; i < count; i++)
        {
            if (index < 7)
            {
                final IBrowserColumn fc = browser.getFilterColumn (index);
                if (fc != null && fc.doesExist ())
                {
                    this.filterColumn = fc.getIndex ();
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


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}