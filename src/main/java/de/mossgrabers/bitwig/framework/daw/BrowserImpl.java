// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.BrowserColumnImpl;
import de.mossgrabers.bitwig.framework.daw.data.BrowserColumnItemImpl;
import de.mossgrabers.framework.daw.AbstractBrowser;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;

import com.bitwig.extension.controller.api.BrowserFilterColumn;
import com.bitwig.extension.controller.api.BrowserResultsItemBank;
import com.bitwig.extension.controller.api.CursorBrowserResultItem;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.PopupBrowser;


/**
 * Provides access to the device, preset, sample, ... browser.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserImpl extends AbstractBrowser
{
    private CursorTrack             cursorTrack;
    private PopupBrowser            browser;
    final BrowserFilterColumn []    filterColumns;

    private CursorBrowserResultItem cursorResult;
    private BrowserResultsItemBank  resultsItemBank;


    /**
     * Constructor.
     *
     * @param browser The browser
     * @param cursorTrack The cursor track
     * @param cursorDevice The cursor device
     * @param numFilterColumnEntries The number of entries in a filter column page
     * @param numResults The number of entries in a results column page
     */
    public BrowserImpl (final PopupBrowser browser, final CursorTrack cursorTrack, final ICursorDevice cursorDevice, final int numFilterColumnEntries, final int numResults)
    {
        super (cursorDevice, numFilterColumnEntries, numResults);

        this.cursorTrack = cursorTrack;

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

        this.cursorResult = (CursorBrowserResultItem) this.browser.resultsColumn ().createCursorItem ();
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

        for (final IBrowserColumn column: this.columnData)
            column.enableObservers (enable);

        this.cursorResult.name ().setIsSubscribed (enable);

        for (final IBrowserColumnItem item: this.resultData)
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
            this.cursorTrack.startOfDeviceChainInsertionPoint ().browse ();
    }


    /** {@inheritDoc} */
    @Override
    public void browseToInsertAfterDevice ()
    {
        this.stopBrowsing (false);

        if (this.cursorDevice.hasSelectedDevice ())
            this.cursorDevice.browseToInsertAfterDevice ();
        else
            this.cursorTrack.endOfDeviceChainInsertionPoint ().browse ();
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


    private IBrowserColumn [] createFilterColumns (final int count, final int numFilterColumnEntries)
    {
        final IBrowserColumn [] columns = new IBrowserColumn [count];
        for (int i = 0; i < count; i++)
            columns[i] = new BrowserColumnImpl (this.filterColumns[i], i, numFilterColumnEntries);
        return columns;
    }


    private IBrowserColumnItem [] createResultData (final int count)
    {
        final IBrowserColumnItem [] items = new IBrowserColumnItem [count];
        for (int i = 0; i < count; i++)
            items[i] = new BrowserColumnItemImpl (this.resultsItemBank.getItemAt (i), i);
        return items;
    }
}