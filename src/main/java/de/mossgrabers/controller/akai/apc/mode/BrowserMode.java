// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.mode;

import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;


/**
 * Browser mode.
 *
 * @author Jürgen Moßgraber
 */
public class BrowserMode extends BaseMode<IItem>
{
    private static final int [] COLUMN_ORDER =
    {
        0,
        6,
        1,
        2,
        3,
        4,
        5
    };
    private int                 lastValue;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public BrowserMode (final APCControlSurface surface, final IModel model)
    {
        super ("Browser", surface, model, APCControlSurface.LED_MODE_PAN, null);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int index, final int value)
    {
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        final int diff = value - this.lastValue;
        final boolean isLeft = value == 0 || diff < 0;
        this.lastValue = value;

        switch (index)
        {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                if (isLeft)
                    browser.selectPreviousFilterItem (BrowserMode.COLUMN_ORDER[index]);
                else
                    browser.selectNextFilterItem (BrowserMode.COLUMN_ORDER[index]);
                break;

            case 7:
                if (isLeft)
                    browser.selectPreviousResult ();
                else
                    browser.selectNextResult ();
                break;

            default:
                // Not used
                break;
        }
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
        return this.model.getBrowser ().hasPreviousContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        return this.model.getBrowser ().hasNextContentType ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        return this.hasPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        return this.hasNextItem ();
    }
}
