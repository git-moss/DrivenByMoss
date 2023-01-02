// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.mode;

import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;


/**
 * User knob mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class UserMode extends BaseMode<IParameter>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public UserMode (final APCControlSurface surface, final IModel model)
    {
        super ("User", surface, model, APCControlSurface.LED_MODE_VOLUME, model.getUserParameterBank ());

        this.setParameterProvider (new BankParameterProvider (model.getUserParameterBank ()));
    }


    /** {@inheritDoc} */
    @Override
    public void selectItemPage (final int page)
    {
        super.selectItemPage (page);
        this.displayPageName ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.model.getCurrentTrackBank ().selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.model.getCurrentTrackBank ().selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        this.model.getCurrentTrackBank ().selectNextPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        this.model.getCurrentTrackBank ().selectPreviousPage ();
    }


    /**
     * Display the page name.
     */
    public void displayPageName ()
    {
        this.surface.scheduleTask ( () -> {
            final int pageSize = this.bank.getPageSize ();
            final int selectedPage = this.bank.getScrollPosition () / pageSize;
            this.model.getHost ().showNotification ("User: Page " + (selectedPage + 1));
        }, 200);
    }
}