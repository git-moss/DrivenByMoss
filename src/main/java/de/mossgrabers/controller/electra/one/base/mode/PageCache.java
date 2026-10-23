// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.base.mode;

import java.util.Arrays;

import de.mossgrabers.controller.electra.one.base.AbstractElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.base.controller.AbstractElectraOneControlSurface;
import de.mossgrabers.controller.electra.one.normal.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;


/**
 * Caches the values and UI information of column * row controls of a mode display page.
 *
 * @param <S> The specific type of the control surface
 * @param <C> The specific type of the configuration
 * 
 * @author Jürgen Moßgraber
 */
public class PageCache<S extends AbstractElectraOneControlSurface<C>, C extends AbstractElectraOneConfiguration>
{
    private static final int    NUM_GROUPS        = 500;

    private final int           numRows;
    private final int           numCols;
    private final int           pageSize;

    private final int [] []     ctrlValueCache;
    private final int [] []     currentCtrlValueCache;
    private final String [] []  ctrlValueLabelCache;
    private final String [] []  currentCtrlValueLabelCache;
    private final String [] []  ctrlLabelCache;
    private final String [] []  currentCtrlLabelCache;
    private final ColorEx [] [] ctrlColorCache;
    private final ColorEx [] [] currentCtrlColorCache;
    private final Boolean [] [] ctrlExistsCache;
    private final Boolean [] [] currentCtrlExistsCache;

    private final String []     groupCache        = new String [NUM_GROUPS];
    private final String []     currentGroupCache = new String [NUM_GROUPS];

    private final Object        dataLock          = new Object ();
    private final S             surface;
    private int                 groupOffset;
    private final int           page;
    private boolean             isDirty           = false;


    /**
     * Constructor.
     *
     * @param page The index of the page to cache
     * @param numRows The number of rows to cache
     * @param numCols The number of columns to cache
     * @param surface The surface
     * @param groupOffset The ID of the first group
     */
    public PageCache (final int page, final int numRows, final int numCols, final S surface, final int groupOffset)
    {
        this.page = page;
        this.numRows = numRows;
        this.numCols = numCols;
        this.pageSize = this.numCols * this.numRows;
        this.surface = surface;
        this.groupOffset = groupOffset;

        this.ctrlValueCache = new int [this.numRows] [this.numCols];
        this.ctrlValueLabelCache = new String [this.numRows] [this.numCols];
        this.ctrlLabelCache = new String [this.numRows] [this.numCols];
        this.ctrlColorCache = new ColorEx [this.numRows] [this.numCols];
        this.ctrlExistsCache = new Boolean [this.numRows] [this.numCols];

        this.currentCtrlValueCache = new int [this.numRows] [this.numCols];
        this.currentCtrlValueLabelCache = new String [this.numRows] [this.numCols];
        this.currentCtrlLabelCache = new String [this.numRows] [this.numCols];
        this.currentCtrlColorCache = new ColorEx [this.numRows] [this.numCols];
        this.currentCtrlExistsCache = new Boolean [this.numRows] [this.numCols];
    }


    /**
     * Update a value for a control. If it is different then the value in the cache an update is
     * sent to the controller.
     *
     * @param row The row of the control (0-5)
     * @param column The column of the control (0-5)
     * @param value The value to update (0-127)
     * @param label The label of the value to update (0-127)
     */
    public void updateValue (final int row, final int column, final int value, final String label)
    {
        synchronized (this.dataLock)
        {
            if (value != this.ctrlValueCache[row][column])
            {
                this.isDirty = true;
                this.ctrlValueCache[row][column] = value;
            }

            if (label != null && !label.equals (this.ctrlValueLabelCache[row][column]))
            {
                this.isDirty = true;
                this.ctrlValueLabelCache[row][column] = label;
            }
        }
    }


    /**
     * Update the color of a control. If it is different then the value in the cache an update is
     * sent to the controller.
     *
     * @param row The row of the control (0-5)
     * @param column The column of the control (0-5)
     * @param color The color of the control. Only colors from the Electra.One color palette do
     *            work!
     */
    public void updateColor (final int row, final int column, final ColorEx color)
    {
        this.updateElement (row, column, null, color, null);
    }


    /**
     * Update the label, color and exists state of a control. If it is different then the value in
     * the cache an update is sent to the controller.
     *
     * @param row The row of the control (0-5)
     * @param column The column of the control (0-5)
     * @param label The label of the control
     * @param color The color of the control. Only colors from the Electra.One color palette do
     *            work!
     * @param exists Show or hide the control
     */
    public void updateElement (final int row, final int column, final String label, final ColorEx color, final Boolean exists)
    {
        synchronized (this.dataLock)
        {
            if (label != null && !label.equals (this.ctrlLabelCache[row][column]))
            {
                this.isDirty = true;
                this.ctrlLabelCache[row][column] = label;
            }

            if (color != null && !color.equals (this.ctrlColorCache[row][column]))
            {
                this.isDirty = true;
                this.ctrlColorCache[row][column] = color;
            }

            if (exists != null && !exists.equals (this.ctrlExistsCache[row][column]))
            {
                this.isDirty = true;
                this.ctrlExistsCache[row][column] = exists;
            }
        }
    }


    /**
     * Update the label of a group. If it is different then the value in the cache an update is sent
     * to the controller.
     *
     * @param groupID The ID of the group
     * @param label The label to set
     */
    public void updateGroupLabel (final int groupID, final String label)
    {
        synchronized (this.dataLock)
        {
            final int gID = groupID - this.groupOffset;
            if (label != null && !label.equals (this.groupCache[gID]))
            {
                this.isDirty = true;
                this.groupCache[gID] = label;
            }
        }
    }


    /**
     * Flush the page cache to the controller.
     */
    public void flush ()
    {
        synchronized (this.dataLock)
        {
            if (!this.isDirty)
                return;

            this.surface.setRepaintEnabled (false);

            // Flush values and their labels
            for (int row = 0; row < this.numRows; row++)
            {
                for (int column = 0; column < this.numCols; column++)
                {
                    // Only update values if value is not currently edited
                    if (this.ctrlValueCache[row][column] != this.currentCtrlValueCache[row][column])
                    {
                        this.currentCtrlValueCache[row][column] = this.ctrlValueCache[row][column];
                        final int midiCC = ElectraOneControlSurface.ELECTRA_CTRL_1 + 10 * row + column;
                        this.surface.updateValue (midiCC, this.currentCtrlValueCache[row][column]);
                    }

                    if (this.ctrlValueLabelCache[row][column] != null && !this.ctrlValueLabelCache[row][column].equals (this.currentCtrlValueLabelCache[row][column]))
                    {
                        this.currentCtrlValueLabelCache[row][column] = this.ctrlValueLabelCache[row][column];
                        final int controlID = this.getControlID (row, column);
                        this.surface.updateValueLabel (controlID, this.currentCtrlValueLabelCache[row][column]);
                    }
                }
            }

            // Flush control label, color and exists state
            String label = null;
            ColorEx color = null;
            Boolean exists = null;
            for (int row = 0; row < this.numRows; row++)
            {
                for (int column = 0; column < this.numCols; column++)
                {
                    if (this.ctrlLabelCache[row][column] != null && !this.ctrlLabelCache[row][column].equals (this.currentCtrlLabelCache[row][column]))
                    {
                        this.currentCtrlLabelCache[row][column] = this.ctrlLabelCache[row][column];
                        label = this.currentCtrlLabelCache[row][column];
                    }

                    if (this.ctrlColorCache[row][column] != null && !this.ctrlColorCache[row][column].equals (this.currentCtrlColorCache[row][column]))
                    {
                        this.currentCtrlColorCache[row][column] = this.ctrlColorCache[row][column];
                        color = this.currentCtrlColorCache[row][column];
                    }

                    if (this.ctrlExistsCache[row][column] != null && !this.ctrlExistsCache[row][column].equals (this.currentCtrlExistsCache[row][column]))
                    {
                        this.currentCtrlExistsCache[row][column] = this.ctrlExistsCache[row][column];
                        exists = this.currentCtrlExistsCache[row][column];
                    }

                    if (label != null || color != null || exists != null)
                    {
                        final int controlID = this.getControlID (row, column);
                        this.surface.updateLabel (controlID, label, color, exists);

                        label = null;
                        color = null;
                        exists = null;
                    }
                }
            }

            // Flush group labels
            for (int group = 0; group < NUM_GROUPS; group++)
            {
                if (this.groupCache[group] != null && !this.groupCache[group].equals (this.currentGroupCache[group]))
                {
                    this.currentGroupCache[group] = this.groupCache[group];
                    this.surface.updateGroupLabel (this.groupOffset + group, this.currentGroupCache[group]);
                }
            }

            this.isDirty = false;

            this.surface.setRepaintEnabled (true);
        }
    }


    /**
     * Get the ID of the control located at the given row/column.
     *
     * @param row The row where the control is located on the page
     * @param column The column where the control is located on the page
     * @return The ID of the control
     */
    private int getControlID (final int row, final int column)
    {
        return this.page * this.pageSize + this.numCols * row + column + 1;
    }


    /**
     * Get the index of the control for the control ID.
     *
     * @param controlID The ID of a control on the page
     * @return The index (0-35)
     */
    public int getIndex (final int controlID)
    {
        return (controlID - 1) % this.pageSize;
    }


    /**
     * Clear the cached values.
     */
    public void reset ()
    {
        synchronized (this.dataLock)
        {
            for (final int [] row: this.currentCtrlValueCache)
                Arrays.fill (row, -1);
            for (final String [] row: this.currentCtrlLabelCache)
                Arrays.fill (row, null);
            for (final ColorEx [] row: this.currentCtrlColorCache)
                Arrays.fill (row, null);
            for (final Boolean [] row: this.currentCtrlExistsCache)
                Arrays.fill (row, null);
            for (final String [] row: this.currentCtrlValueLabelCache)
                Arrays.fill (row, null);
            Arrays.fill (this.currentGroupCache, null);

            for (final int [] row: this.ctrlValueCache)
                Arrays.fill (row, -1);
            for (final String [] row: this.ctrlLabelCache)
                Arrays.fill (row, null);
            for (final ColorEx [] row: this.ctrlColorCache)
                Arrays.fill (row, null);
            for (final Boolean [] row: this.ctrlExistsCache)
                Arrays.fill (row, null);
            for (final String [] row: this.ctrlValueLabelCache)
                Arrays.fill (row, null);
            Arrays.fill (this.groupCache, null);
        }
    }


    /**
     * Get the page size.
     * 
     * @return The page size
     */
    public int getPageSize ()
    {
        return this.pageSize;
    }
}
