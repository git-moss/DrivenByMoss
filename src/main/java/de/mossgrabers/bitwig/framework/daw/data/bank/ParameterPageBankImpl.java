// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.daw.data.bank.AbstractBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.observer.IItemSelectionObserver;

import com.bitwig.extension.controller.api.CursorRemoteControlsPage;
import com.bitwig.extension.controller.api.SettableIntegerValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Encapsulates the data of parameter pages. Bitwig pages have no banking, we need to do it
 * ourselves.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterPageBankImpl extends AbstractBank<String> implements IParameterPageBank
{
    private final CursorRemoteControlsPage remoteControls;


    /**
     * Constructor.
     *
     * @param remoteControlsPage The remote controls bank
     * @param numParameterPages The number of parameter pages in the page of the bank
     */
    public ParameterPageBankImpl (final CursorRemoteControlsPage remoteControlsPage, final int numParameterPages)
    {
        super (null, numParameterPages);

        this.remoteControls = remoteControlsPage;

        this.remoteControls.selectedPageIndex ().markInterested ();
        this.remoteControls.pageNames ().addValueObserver (this::handlePageNames);
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.remoteControls.selectedPageIndex (), enable);
        Util.setIsSubscribed (this.remoteControls.pageNames (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return this.canScrollPageBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return this.getSelectedItemPosition () < this.getItemCount ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        final int selectedItemPosition = this.getSelectedItemPosition ();
        return selectedItemPosition > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        final int sel = this.getSelectedItemPosition ();
        final int ps = this.getPageSize ();
        final int end = sel / ps * ps + ps;
        return end < this.getItemCount ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        final SettableIntegerValue index = this.remoteControls.selectedPageIndex ();
        index.set (Math.max (this.getSelectedItemPosition () - 1, 0));
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        this.scrollTo (this.getSelectedItemPosition () + 1);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        this.scrollTo (this.getSelectedItemPosition () - this.getPageSize ());
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextPage ()
    {
        this.scrollTo (this.getSelectedItemPosition () + this.getPageSize ());
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        this.remoteControls.selectedPageIndex ().set (Math.max (Math.min (position, this.getItemCount () - 1), 0));
        this.firePageObserver ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public String getItem (final int index)
    {
        final int start = this.getScrollPosition () + index;
        return start >= 0 && start < this.getItemCount () ? this.items.get (start) : "";
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedItemPosition ()
    {
        return this.remoteControls.selectedPageIndex ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedItemIndex ()
    {
        return this.getSelectedItemPosition () % this.getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public Optional<String> getSelectedItem ()
    {
        final int sel = this.getSelectedItemPosition ();
        return Optional.of (sel >= 0 && sel < this.getItemCount () ? this.items.get (sel) : "");
    }


    /** {@inheritDoc} */
    @Override
    public List<String> getSelectedItems ()
    {
        final Optional<String> selectedItem = this.getSelectedItem ();
        return Collections.singletonList (selectedItem.isPresent () ? selectedItem.get () : "");
    }


    /** {@inheritDoc} */
    @Override
    public void selectPage (final int index)
    {
        this.scrollTo (this.getScrollPosition () + index);
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final IItemSelectionObserver observer)
    {
        // Not selected
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        final int selectedItemPosition = this.getSelectedItemPosition ();
        return selectedItemPosition / this.getPageSize () * this.getPageSize ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemAtPosition (final int position)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionOfLastItem ()
    {
        return Math.min (this.getScrollPosition () + this.getPageSize (), this.getItemCount ()) - 1;
    }


    private void handlePageNames (final String [] pageNames)
    {
        this.items.clear ();
        this.items.addAll (Arrays.asList (pageNames));
    }


    /** {@inheritDoc} */
    @Override
    public void setSkipDisabledItems (final boolean shouldSkip)
    {
        // Not supported
    }
}