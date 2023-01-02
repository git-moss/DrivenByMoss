// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mki.mode.device;

import de.mossgrabers.controller.ni.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.controller.ni.kontrol.mki.mode.AbstractKontrol1Mode;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Locale;


/**
 * Browser mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowseMode extends AbstractKontrol1Mode<IItem>
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
        super ("Browse", surface, model);

        this.selectionMode = SELECTION_OFF;
        this.filterColumn = 0;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
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
        final ITextDisplay d = this.surface.getTextDisplay ();
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        d.clear ();

        String selectedResult;
        switch (this.selectionMode)
        {
            case BrowseMode.SELECTION_OFF:
                d.setCell (0, 0, "BROWSE").setCell (1, 0, browser.getSelectedContentType ().toUpperCase (Locale.US));
                selectedResult = browser.getSelectedResult ();
                d.setCell (0, 8, "SELECTED").setCell (1, 8, selectedResult == null ? "NONE" : selectedResult);
                for (int i = 0; i < 7; i++)
                {
                    final IBrowserColumn column = browser.getFilterColumn (i);
                    d.setCell (0, 1 + i, StringUtils.shortenAndFixASCII (column.getName () + ":", 8).toUpperCase (Locale.US)).setCell (1, 1 + i, column.doesCursorExist () ? column.getCursorName ().toUpperCase (Locale.US) : "");
                }
                break;

            case BrowseMode.SELECTION_PRESET:
                d.setCell (0, 0, "SELECTED");
                final IBrowserColumnItem [] results = browser.getResultColumnItems ();
                for (int i = 0; i < 16; i++)
                    d.setCell (i % 2, 1 + i / 2, (results[i].isSelected () ? ">" : " ") + results[i].getName ().toUpperCase (Locale.US));
                break;

            case BrowseMode.SELECTION_FILTER:
                final IBrowserColumn fc = browser.getFilterColumn (this.filterColumn);
                d.setCell (0, 0, fc.getName ().toUpperCase (Locale.US));
                final IBrowserColumnItem [] items = fc.getItems ();
                for (int i = 0; i < 16; i++)
                {
                    final String name = items[i].getName ().toUpperCase (Locale.US);
                    final String text = (items[i].isSelected () ? ">" : " ") + name;
                    d.setCell (i % 2, 1 + i / 2, text);
                }
                break;

            default:
                // No more modes
                break;
        }
        d.allDone ();
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
    public String getButtonColorID (final ButtonID buttonID)
    {
        switch (buttonID)
        {
            case MUTE:
            case SOLO:
                return ColorManager.BUTTON_STATE_ON;
            case BROWSE:
                return ColorManager.BUTTON_STATE_HI;
            default:
                return ColorManager.BUTTON_STATE_OFF;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onMainKnob (final int value)
    {
        if (value > 64)
            this.selectPrevious (1);
        else
            this.selectNext (1);
    }


    /** {@inheritDoc} */
    @Override
    public void onMainKnobPressed ()
    {
        this.onKnobTouch (7, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onBack ()
    {
        ((BrowserCommand<?, ?>) this.surface.getButton (ButtonID.BROWSE).getCommand ()).startBrowser (true, true);
    }


    /** {@inheritDoc} */
    @Override
    public void onEnter ()
    {
        ((BrowserCommand<?, ?>) this.surface.getButton (ButtonID.BROWSE).getCommand ()).startBrowser (false, false);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.model.getBrowser ().previousContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.model.getBrowser ().nextContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        // Intentionally empty
    }
}