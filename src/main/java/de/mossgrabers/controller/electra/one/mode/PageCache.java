// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
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
    private final int [] []                ctrlCache    = new int [6] [6];
    private final String []                elementCache = new String [36];
    private final String []                groupCache   = new String [500];

    private final ElectraOneControlSurface surface;
    private final int                      page;


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
        if (this.ctrlCache[row][column] != value)
        {
            this.ctrlCache[row][column] = value;
            this.surface.updateValue (ElectraOneControlSurface.ELECTRA_CTRL_1 + 10 * row + column, value);
        }
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
    public void updateLabel (final int row, final int column, final String label, final ColorEx color, final Boolean exists)
    {
        final int controlID = this.page * 36 + 6 * row + column + 1;
        this.surface.updateLabel (controlID, this.elementCache, label, color, exists);
    }


    /**
     * Update the label of a group. If it is different then the value in the cache an update is sent
     * to the controller.
     *
     * @param groupID
     * @param title
     */
    public void updateGroupLabel (final int groupID, final String title)
    {
        this.surface.updateGroupLabel (groupID, this.groupCache, title);
    }


    /**
     * Clear the cached values.
     */
    public void reset ()
    {
        for (final int [] row: this.ctrlCache)
            Arrays.fill (row, -1);
        Arrays.fill (this.elementCache, null);
        Arrays.fill (this.groupCache, null);
    }
}
