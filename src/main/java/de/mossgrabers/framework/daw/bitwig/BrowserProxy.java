// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig;

import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.data.BrowserColumnData;
import de.mossgrabers.framework.daw.data.BrowserColumnItemData;

import com.bitwig.extension.controller.api.BrowserFilterColumn;
import com.bitwig.extension.controller.api.BrowserResultsColumn;
import com.bitwig.extension.controller.api.BrowserResultsItemBank;
import com.bitwig.extension.controller.api.CursorBrowserResultItem;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.PopupBrowser;


/**
 * Provides access to the device, preset, sample, ... browser.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserProxy implements IBrowser
{
    private CursorTrack              cursorTrack;
    private CursorDeviceProxy        cursorDevice;
    private PopupBrowser             browser;

    final BrowserFilterColumn []     filterColumns;
    final BrowserColumnData []       columnData;

    private int                      numResults;
    private BrowserResultsColumn     resultsColumn;
    private CursorBrowserResultItem  cursorResult;
    private BrowserResultsItemBank   resultsItemBank;
    private BrowserColumnItemData [] resultData;
    private int                      numFilterColumnEntries;


    /**
     * Constructor.
     *
     * @param browser The browser
     * @param cursorTrack The cursor track
     * @param cursorDevice The cursor device
     * @param numFilterColumnEntries The number of entries in a filter column page
     * @param numResults The number of entries in a results column page
     */
    public BrowserProxy (final PopupBrowser browser, final CursorTrack cursorTrack, final CursorDeviceProxy cursorDevice, final int numFilterColumnEntries, final int numResults)
    {
        this.cursorTrack = cursorTrack;
        this.cursorDevice = cursorDevice;
        this.numFilterColumnEntries = numFilterColumnEntries;
        this.numResults = numResults;

        this.browser = browser;
        this.browser.exists ().markInterested ();
        this.browser.selectedContentTypeIndex ().markInterested ();
        this.browser.selectedContentTypeName ().markInterested ();
        this.browser.contentTypeNames ().markInterested ();

        this.filterColumns = new BrowserFilterColumn []
        {
            this.browser.smartCollectionColumn (),
            this.browser.locationColumn (),
            this.browser.fileTypeColumn (),
            this.browser.categoryColumn (),
            this.browser.tagColumn (),
            this.browser.creatorColumn (),
            this.browser.deviceTypeColumn (),
            this.browser.deviceColumn ()
        };

        this.columnData = this.createFilterColumns (this.filterColumns.length, numFilterColumnEntries);

        this.resultsColumn = this.browser.resultsColumn ();
        this.cursorResult = (CursorBrowserResultItem) this.resultsColumn.createCursorItem ();
        this.cursorResult.name ().markInterested ();

        this.resultsItemBank = (BrowserResultsItemBank) this.cursorResult.createSiblingsBank (this.numResults);
        this.resultData = this.createResultData (this.numResults);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.browser.exists ().setIsSubscribed (enable);
        this.browser.selectedContentTypeIndex ().setIsSubscribed (enable);
        this.browser.selectedContentTypeName ().setIsSubscribed (enable);
        this.browser.contentTypeNames ().setIsSubscribed (enable);

        for (final BrowserColumnData column: this.columnData)
            column.enableObservers (enable);

        this.cursorResult.name ().setIsSubscribed (enable);

        for (final BrowserColumnItemData item: this.resultData)
            item.enableObservers (enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPresetContentType ()
    {
        return this.getSelectedContentTypeIndex () == 1;
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedContentTypeIndex ()
    {
        return this.browser.selectedContentTypeIndex ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void previousContentType ()
    {
        this.browser.selectedContentTypeIndex ().inc (-1);
    }


    /** {@inheritDoc} */
    @Override
    public void nextContentType ()
    {
        this.browser.selectedContentTypeIndex ().inc (1);
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedContentType ()
    {
        return this.browser.selectedContentTypeName ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String [] getContentTypeNames ()
    {
        return this.browser.contentTypeNames ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void browseForPresets ()
    {
        this.stopBrowsing (false);
        this.cursorDevice.browseToReplaceDevice ();
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertBeforeDevice ()
    {
        this.stopBrowsing (false);
        if (this.cursorDevice.hasSelectedDevice ())
            this.cursorDevice.browseToInsertBeforeDevice ();
        else
            this.cursorTrack.browseToInsertAtStartOfChain ();
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertAfterDevice ()
    {
        this.stopBrowsing (false);

        if (this.cursorDevice.hasSelectedDevice ())
            this.cursorDevice.browseToInsertAfterDevice ();
        else
            this.cursorTrack.browseToInsertAtEndOfChain ();
    }


    /** {@inheritDoc} */
    @Override
    public void stopBrowsing (final boolean commitSelection)
    {
        if (commitSelection)
            this.browser.commit ();
        else
            this.browser.cancel ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActive ()
    {
        return this.browser.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void resetFilterColumn (final int column)
    {
        this.columnData[column].resetFilter ();
    }


    /** {@inheritDoc} */
    @Override
    public BrowserColumnData getFilterColumn (final int column)
    {
        return this.columnData[column];
    }


    /** {@inheritDoc} */
    @Override
    public int getFilterColumnCount ()
    {
        return this.columnData.length;
    }


    /** {@inheritDoc} */
    @Override
    public String [] getFilterColumnNames ()
    {
        final String [] names = new String [this.columnData.length];
        for (int i = 0; i < this.columnData.length; i++)
            names[i] = this.columnData[i].getName ();
        return names;
    }


    /** {@inheritDoc} */
    @Override
    public BrowserColumnItemData [] getResultColumnItems ()
    {
        return this.resultData;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousFilterItem (final int columnIndex)
    {
        this.columnData[columnIndex].selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextFilterItem (final int columnIndex)
    {
        this.columnData[columnIndex].selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void previousFilterItemPage (final int columnIndex)
    {
        this.columnData[columnIndex].scrollItemPageUp ();

    }


    /** {@inheritDoc} */
    @Override
    public void nextFilterItemPage (final int columnIndex)
    {
        this.columnData[columnIndex].scrollItemPageDown ();
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedFilterItemIndex (final int columnIndex)
    {
        return this.columnData[columnIndex].getCursorIndex ();
    }


    /**
     * Set the index of the selected filter item of a column.
     *
     * @param columnIndex The index of the column
     * @param index The index of the item
     */
    public void setSelectedFilterItemIndex (final int columnIndex, final int index)
    {
        this.columnData[columnIndex].setCursorIndex (index);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousResult ()
    {
        this.cursorResult.selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextResult ()
    {
        this.cursorResult.selectNext ();
    }


    /** {@inheritDoc} */
    @Override
    public String getSelectedResult ()
    {
        return this.cursorResult.name ().get ();
    }


    /**
     * Get the index of the selected result item.
     *
     * @return The index of the result
     */
    public int getSelectedResultIndex ()
    {
        for (int i = 0; i < this.numResults; i++)
        {
            if (this.resultData[i].isSelected ())
                return i;
        }
        return -1;
    }


    /**
     * Select the previous result page.
     */
    public void previousResultPage ()
    {
        this.resultsItemBank.scrollPageBackwards ();
    }


    /**
     * Select the next result page.
     */
    public void nextResultPage ()
    {
        this.resultsItemBank.scrollPageForwards ();
    }


    private BrowserColumnData [] createFilterColumns (final int count, final int numFilterColumnEntries)
    {
        final BrowserColumnData [] columns = new BrowserColumnData [count];
        for (int i = 0; i < count; i++)
            columns[i] = new BrowserColumnData (this.filterColumns[i], i, numFilterColumnEntries);
        return columns;
    }


    private BrowserColumnItemData [] createResultData (final int count)
    {
        final BrowserColumnItemData [] items = new BrowserColumnItemData [count];
        for (int i = 0; i < count; i++)
            items[i] = new BrowserColumnItemData (this.resultsItemBank.getItemAt (i), i);
        return items;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumResults ()
    {
        return this.numResults;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumFilterColumnEntries ()
    {
        return this.numFilterColumnEntries;
    }
}