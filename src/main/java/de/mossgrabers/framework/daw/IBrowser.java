// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.observer.IObserverManagement;
import de.mossgrabers.framework.observer.IValueObserver;


/**
 * Provides access to the device, preset, sample, ... browser.
 *
 * @author Jürgen Moßgraber
 */
public interface IBrowser extends IObserverManagement
{
    /**
     * Add an observer for the activation state. Called when the state changes.
     *
     * @param activeObserver The observer to register
     */
    void addActiveObserver (IValueObserver<Boolean> activeObserver);


    /**
     * Returns true of the browser displays presets.
     *
     * @return True of the browser displays presets.
     */
    boolean isPresetContentType ();


    /**
     * Get the index of the content type (selection tab).
     *
     * @return The index
     */
    int getSelectedContentTypeIndex ();


    /**
     * Returns true if there is a previous content type.
     *
     * @return True if there is a previous content type
     */
    boolean hasPreviousContentType ();


    /**
     * Returns true if there is a next content type.
     *
     * @return True if there is a next content type
     */
    boolean hasNextContentType ();


    /**
     * Select the previous selection tab, if any.
     */
    void previousContentType ();


    /**
     * Select the next selection tab, if any.
     */
    void nextContentType ();


    /**
     * Get the selected content type.
     *
     * @return The selected content type.
     */
    String getSelectedContentType ();


    /**
     * Get the names of all content types (panes).
     *
     * @return The names
     */
    String [] getContentTypeNames ();


    /**
     * Is browser preview enabled?
     *
     * @return True, if enabled
     */
    boolean isPreviewEnabled ();


    /**
     * Toggle browser preview on/off.
     */
    void togglePreviewEnabled ();


    /**
     * Set browser preview on/off.
     *
     * @param isEnabled True to enable
     */
    void setPreviewEnabled (boolean isEnabled);


    /**
     * Open the browser to browse for items replacing the given one.
     *
     * @param item The item to replace
     */
    void replace (IItem item);


    /**
     * Browse to add a new device to the given channel.
     *
     * @param channel The channel to which to add a new device
     */
    void addDevice (IChannel channel);


    /**
     * Open the browser to browse for an item which will be inserted before the given one.
     */
    void insertBeforeCursorDevice ();


    /**
     * Open the browser to browse for an item which will be inserted after the given one.
     */
    void insertAfterCursorDevice ();


    /**
     * Stop browsing.
     *
     * @param commitSelection Commits the selection if true otherwise it is discarded.
     */
    void stopBrowsing (final boolean commitSelection);


    /**
     * Check if the browser is active.
     *
     * @return True if active
     */
    boolean isActive ();


    /**
     * Reset a filter to the default (all) value.
     *
     * @param column The index of the column to reset
     */
    void resetFilterColumn (final int column);


    /**
     * Get a filter column.
     *
     * @param column The index of the column to get
     * @return The column
     */
    IBrowserColumn getFilterColumn (final int column);


    /**
     * Get selected filter column.
     *
     * @return The column
     */
    IBrowserColumn getSelectedFilterColumn ();


    /**
     * Get the number of filter columns.
     *
     * @return The number of filter columns
     */
    int getFilterColumnCount ();


    /**
     * Get the names of the filter columns.
     *
     * @return The names of the filter columns
     */
    String [] getFilterColumnNames ();


    /**
     * Get the result columns items.
     *
     * @return The item data
     */
    IBrowserColumnItem [] getResultColumnItems ();


    /**
     * Select the previous filter column.
     */
    void selectPreviousFilterColumn ();


    /**
     * Select the next filter column.
     */
    void selectNextFilterColumn ();


    /**
     * Select the previous item of a filter column.
     *
     * @param columnIndex The index of the column
     */
    void selectPreviousFilterItem (final int columnIndex);


    /**
     * Select the next item of a filter column.
     *
     * @param columnIndex The index of the column
     */
    void selectNextFilterItem (final int columnIndex);


    /**
     * Select the previous item page of a filter column.
     *
     * @param columnIndex The index of the column
     */
    void previousFilterItemPage (final int columnIndex);


    /**
     * Select the next item page of a filter column.
     *
     * @param columnIndex The index of the column
     */
    void nextFilterItemPage (final int columnIndex);


    /**
     * Get the index of the select filter item of a column.
     *
     * @param columnIndex The index of the column
     * @return The index of the item
     */
    int getSelectedFilterItemIndex (final int columnIndex);


    /**
     * Select the previous results item.
     */
    void selectPreviousResult ();


    /**
     * Select the next results item.
     */
    void selectNextResult ();


    /**
     * Get the selected result item.
     *
     * @return The result
     */
    String getSelectedResult ();


    /**
     * Get the number of results to display on a page.
     *
     * @return The number of results.
     */
    int getNumResults ();


    /**
     * Get the number of filter items to display on a page.
     *
     * @return The number of results.
     */
    int getNumFilterColumnEntries ();


    /**
     * Get an info text about what the browser is currently itendended to browse. E.g. 'Insert
     * device'.
     *
     * @return The info text
     */
    String getInfoText ();
}