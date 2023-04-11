// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.mode;

import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Optional;


/**
 * The browse mode.
 *
 * @author Jürgen Moßgraber
 */
public class BrowseMode extends BaseMode
{
    private int filterColumn = -1;
    private int selColumn    = 7;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public BrowseMode (final MaschineControlSurface surface, final IModel model)
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
        final int speed = this.model.getValueChanger ().calcSteppedKnobChange (value);
        if (speed < 0)
            this.selectPrevious (index < 0 ? this.selColumn : index, Math.abs (speed));
        else
            this.selectNext (index < 0 ? this.selColumn : index, Math.abs (speed));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
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
                name = StringUtils.shortenAndFixASCII (browserColumn.getName (), 6);
            }
            if (i == this.getSelectedParameter ())
                name = ">" + name;
            d.setCell (0, i, name).setCell (1, i, value);
        }
        final String selectedResult = browser.getSelectedResult ();
        String selectedContentType = browser.getSelectedContentType ();
        if (this.getSelectedParameter () == 7)
            selectedContentType = ">" + selectedContentType;
        d.setCell (0, 7, selectedContentType).setCell (1, 7, selectedResult == null || selectedResult.length () == 0 ? "-" : selectedResult);
        d.allDone ();
    }


    /**
     * Set the selected parameter.
     *
     * @param index The index of the parameter (0-15)
     */
    public void selectParameter (final int index)
    {
        this.selColumn = index;
    }


    /**
     * Get the index of the selected parameter.
     *
     * @return The index 0-15
     */
    public int getSelectedParameter ()
    {
        return this.selColumn == -1 ? 0 : this.selColumn;
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


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        if (this.surface.isShiftPressed ())
        {
            this.selectPreviousItemPage ();
            return;
        }

        final int selectedParameter = this.getSelectedParameter ();
        if (selectedParameter > 0)
            this.selectParameter (selectedParameter - 1);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        if (this.surface.isShiftPressed ())
        {
            this.selectNextItemPage ();
            return;
        }

        final int selectedParameter = this.getSelectedParameter ();
        if (selectedParameter < 7)
            this.selectParameter (selectedParameter + 1);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        this.model.getBrowser ().previousContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        this.model.getBrowser ().nextContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        return this.getSelectedParameter () > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        return this.getSelectedParameter () < 7;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        return this.model.getBrowser ().hasPreviousContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        return this.model.getBrowser ().hasNextContentType ();
    }
}