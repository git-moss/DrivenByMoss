// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data.empty;

import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.observer.IBankPageObserver;
import de.mossgrabers.framework.observer.IItemSelectionObserver;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Default data for an empty parameter page bank.
 *
 * @author Jürgen Moßgraber
 */
public class EmptyParameterPageBank implements IParameterPageBank
{
    /** The singleton. */
    public static final IParameterPageBank INSTANCE = new EmptyParameterPageBank ();


    /**
     * Constructor.
     */
    private EmptyParameterPageBank ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public String getItem (final int index)
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public int getPageSize ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getItemCount ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasExistingItems ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public Optional<String> getSelectedItem ()
    {
        return Optional.empty ();
    }


    /** {@inheritDoc} */
    @Override
    public List<String> getSelectedItems ()
    {
        return Collections.emptyList ();
    }


    /** {@inheritDoc} */
    @Override
    public void addSelectionObserver (final IItemSelectionObserver observer)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void removeSelectionObserver (final IItemSelectionObserver observer)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void addPageObserver (final IBankPageObserver observer)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void removePageObserver (final IBankPageObserver observer)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollBackwards ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollForwards ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageBackwards ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollPageForwards ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollBackwards ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void scrollForwards ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int position, final boolean adjustPage)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getScrollPosition ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getPositionOfLastItem ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemAtPosition (final int position)
    {
        // Intentionally empty
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
    public void selectNextPage ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousPage ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setSkipDisabledItems (final boolean shouldSkip)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedItemPosition ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getSelectedItemIndex ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPage (final int index)
    {
        // Intentionally empty
    }
}