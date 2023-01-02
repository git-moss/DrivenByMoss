// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.BrowserColumnImpl;
import de.mossgrabers.bitwig.framework.daw.data.BrowserColumnItemImpl;
import de.mossgrabers.bitwig.framework.daw.data.ChannelImpl;
import de.mossgrabers.bitwig.framework.daw.data.CursorDeviceImpl;
import de.mossgrabers.bitwig.framework.daw.data.DrumPadImpl;
import de.mossgrabers.bitwig.framework.daw.data.SlotImpl;
import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.daw.AbstractBrowser;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IItem;

import com.bitwig.extension.controller.api.BrowserFilterColumn;
import com.bitwig.extension.controller.api.BrowserResultsItemBank;
import com.bitwig.extension.controller.api.CursorBrowserResultItem;
import com.bitwig.extension.controller.api.CursorDevice;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.InsertionPoint;
import com.bitwig.extension.controller.api.PopupBrowser;


/**
 * Provides access to the device, preset, sample, ... browser.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserImpl extends AbstractBrowser
{
    private final IHost                   host;
    private final CursorDevice            cursorDevice;
    private final CursorTrack             cursorTrack;
    private final PopupBrowser            browser;
    private final BrowserFilterColumn []  filterColumns;
    private final CursorBrowserResultItem cursorResult;
    private final BrowserResultsItemBank  resultsItemBank;


    /**
     * Constructor.
     *
     * @param host The host
     * @param browser The browser
     * @param cursorTrack The cursor track
     * @param cursorDevice The cursor device
     * @param numFilterColumnEntries The number of entries in a filter column page
     * @param numResults The number of entries in a results column page
     */
    public BrowserImpl (final IHost host, final PopupBrowser browser, final CursorTrack cursorTrack, final CursorDevice cursorDevice, final int numFilterColumnEntries, final int numResults)
    {
        super (numFilterColumnEntries, numResults);

        this.host = host;
        this.cursorTrack = cursorTrack;
        this.cursorDevice = cursorDevice;

        this.browser = browser;

        this.browser.exists ().addValueObserver (this::fireActiveObserver);
        this.browser.selectedContentTypeIndex ().markInterested ();
        this.browser.selectedContentTypeName ().markInterested ();
        this.browser.contentTypeNames ().markInterested ();
        this.browser.shouldAudition ().markInterested ();

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
        Util.setIsSubscribed (this.browser.exists (), enable);
        Util.setIsSubscribed (this.browser.selectedContentTypeIndex (), enable);
        Util.setIsSubscribed (this.browser.selectedContentTypeName (), enable);
        Util.setIsSubscribed (this.browser.contentTypeNames (), enable);
        Util.setIsSubscribed (this.browser.shouldAudition (), enable);

        for (final IBrowserColumn column: this.columnData)
            column.enableObservers (enable);

        Util.setIsSubscribed (this.cursorResult.name (), enable);

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
    public boolean isPreviewEnabled ()
    {
        return this.browser.shouldAudition ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void togglePreviewEnabled ()
    {
        this.browser.shouldAudition ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setPreviewEnabled (final boolean isEnabled)
    {
        this.browser.shouldAudition ().set (isEnabled);
    }


    /** {@inheritDoc} */
    @Override
    public void replace (final IItem item)
    {
        final InsertionPoint insertionPoint;
        if (item instanceof final CursorDeviceImpl cursorDeviceImpl)
            insertionPoint = cursorDeviceImpl.getCursorDevice ().replaceDeviceInsertionPoint ();
        else if (item instanceof final SlotImpl slot)
            insertionPoint = slot.getSlot ().replaceInsertionPoint ();
        else if (item instanceof final DrumPadImpl drumPad)
            insertionPoint = drumPad.getDrumPad ().insertionPoint ();
        else
            return;

        final String name = item.getName ();
        this.infoText = "Replace: " + (name.length () == 0 ? "Empty" : name);

        this.browse (insertionPoint);
    }


    /** {@inheritDoc} */
    @Override
    public void addDevice (final IChannel channel)
    {
        this.infoText = "Add device to: " + channel.getName ();

        this.browse (((ChannelImpl) channel).getDeviceChain ().startOfDeviceChainInsertionPoint ());
    }


    /** {@inheritDoc} */
    @Override
    public void insertBeforeCursorDevice ()
    {
        this.infoText = "Insert device before: " + this.cursorDevice.name ().get ();

        this.browse (this.cursorDevice.exists ().get () ? this.cursorDevice.beforeDeviceInsertionPoint () : this.cursorTrack.startOfDeviceChainInsertionPoint ());
    }


    /** {@inheritDoc} */
    @Override
    public void insertAfterCursorDevice ()
    {
        this.infoText = "Insert device after: " + this.cursorDevice.name ().get ();

        this.browse (this.cursorDevice.exists ().get () ? this.cursorDevice.afterDeviceInsertionPoint () : this.cursorTrack.endOfDeviceChainInsertionPoint ());
    }


    private void browse (final InsertionPoint insertionPoint)
    {
        this.stopBrowsing (false);

        if (insertionPoint == null)
            return;

        // Delay a bit to give the previous browser the chance to shutdown
        this.host.scheduleTask (insertionPoint::browse, 100);
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