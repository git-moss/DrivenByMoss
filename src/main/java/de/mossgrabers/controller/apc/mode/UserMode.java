// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.mode;

import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;


/**
 * User knob mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public UserMode (final APCControlSurface surface, final IModel model)
    {
        super ("User", surface, model, APCControlSurface.LED_MODE_VOLUME, 0, model.getUserParameterBank ());
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Not used - IParameter is directly mapped
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final int index, final int value)
    {
        // Not used - IParameter is directly mapped
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final IParameter item = this.model.getUserParameterBank ().getItem (index);
        return item != null && item.doesExist () ? item.getValue () : -1;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        super.selectPreviousItem ();
        this.displayPageName ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        super.selectNextItem ();
        this.displayPageName ();
    }


    private void displayPageName ()
    {
        this.surface.scheduleTask ( () -> {
            final int pageSize = this.bank.getPageSize ();
            final int selectedPage = this.bank.getScrollPosition () / pageSize;
            this.surface.getDisplay ().notify ("User Page " + (selectedPage + 1));
        }, 200);
    }
}