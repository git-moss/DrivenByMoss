// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;

import java.util.Arrays;


/**
 * Caches the values and UI information of 36 controls of a mode display page.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
class PageCache
{
    private static final int               NUM_ROWS               = 6;
    private static final int               NUM_COLS               = 6;
    private static final int               NUM_GROUPS             = 500;
    private static final int               GROUP_OFFSET           = 500;
    private final static long              TIME_TILL_LAST_EDIT    = 200;

    private final int [] []                ctrlValueCache         = new int [NUM_ROWS] [NUM_COLS];
    private final String [] []             ctrlLabelCache         = new String [NUM_ROWS] [NUM_COLS];
    private final ColorEx [] []            ctrlColorCache         = new ColorEx [NUM_ROWS] [NUM_COLS];
    private final Boolean [] []            ctrlExistsCache        = new Boolean [NUM_ROWS] [NUM_COLS];
    private final String []                groupCache             = new String [NUM_GROUPS];

    private final int [] []                currentCtrlValueCache  = new int [NUM_ROWS] [NUM_COLS];
    private final String [] []             currentCtrlLabelCache  = new String [NUM_ROWS] [NUM_COLS];
    private final ColorEx [] []            currentCtrlColorCache  = new ColorEx [NUM_ROWS] [NUM_COLS];
    private final Boolean [] []            currentCtrlExistsCache = new Boolean [NUM_ROWS] [NUM_COLS];
    private final String []                currentGroupCache      = new String [NUM_GROUPS];

    private final Object                   dataLock               = new Object ();
    private final ElectraOneControlSurface surface;
    private final int                      page;
    private long                           lastValueEdit          = 0;
    private boolean                        isDirty                = false;


    /**
     * Constructor.
     *
     * @param page The index of the page to cache
     * @param surface The surface
     */
    public PageCache (final int page, final ElectraOneControlSurface surface)
    {
        this.page = page;
        this.surface = surface;
    }


    /**
     * Update a value for a control. If it is different then the value in the cache an update is
     * sent to the controller.
     *
     * @param row The row of the control (0-5)
     * @param column The column of the control (0-5)
     * @param value The value to update (0-127)
     */
    public void updateValue (final int row, final int column, final int value)
    {
        synchronized (this.dataLock)
        {
            if (value != this.ctrlValueCache[row][column])
            {
                this.isDirty = true;
                this.ctrlValueCache[row][column] = value;
                this.lastValueEdit = System.currentTimeMillis ();
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
            final int gID = groupID - GROUP_OFFSET;
            if (label != null && !label.equals (this.groupCache[gID]))
            {
                this.isDirty = true;
                this.groupCache[gID] = label;
            }
        }
    }


    public void flush ()
    {
        synchronized (this.dataLock)
        {
            if (!this.isDirty)
                return;

            // Only update values if the last value edit was some time ago
            final long now = System.currentTimeMillis ();
            if (now - this.lastValueEdit < TIME_TILL_LAST_EDIT)
                return;

            this.surface.setRepaintEnabled (false);

            // Flush values
            for (int row = 0; row < NUM_ROWS; row++)
            {
                for (int column = 0; column < NUM_COLS; column++)
                {
                    if (this.ctrlValueCache[row][column] != this.currentCtrlValueCache[row][column])
                    {
                        this.currentCtrlValueCache[row][column] = this.ctrlValueCache[row][column];
                        this.surface.updateValue (ElectraOneControlSurface.ELECTRA_CTRL_1 + 10 * row + column, this.currentCtrlValueCache[row][column]);
                    }
                }
            }

            // Flush control label, color and exists state
            String label = null;
            ColorEx color = null;
            Boolean exists = null;
            for (int row = 0; row < NUM_ROWS; row++)
            {
                for (int column = 0; column < NUM_COLS; column++)
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
                        final int controlID = this.page * 36 + 6 * row + column + 1;
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
                    this.surface.updateGroupLabel (GROUP_OFFSET + group, this.currentGroupCache[group]);
                }
            }

            this.isDirty = false;

            this.surface.setRepaintEnabled (true);
        }
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
            Arrays.fill (this.currentGroupCache, null);
        }
    }
}
