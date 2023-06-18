// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.mode;

import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;


/**
 * User knob mode.
 *
 * @author Jürgen Moßgraber
 */
public class UserMode extends BaseMode<IParameter>
{
    private final BankParameterProvider projectParameterProvider;
    private final BankParameterProvider trackParameterProvider;
    private boolean                     isProjectMode = true;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public UserMode (final APCControlSurface surface, final IModel model)
    {
        super ("User", surface, model, APCControlSurface.LED_MODE_VOLUME, model.getProject ().getParameterBank ());

        this.projectParameterProvider = new BankParameterProvider (model.getProject ().getParameterBank ());
        this.trackParameterProvider = new BankParameterProvider (model.getCursorTrack ().getParameterBank ());
        this.setParameterProvider (this.projectParameterProvider);
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
     * Test if the given parameter page is currently selected.
     *
     * @param pageIndex The index of the page
     * @return True if selected
     */
    public boolean isPageSelected (final int pageIndex)
    {
        final IParameterBank userParameterBank = this.isProjectMode ? this.model.getProject ().getParameterBank () : this.model.getCursorTrack ().getParameterBank ();
        return userParameterBank.getPageBank ().getSelectedItemIndex () == pageIndex;
    }


    /**
     * Display the page name.
     */
    public void displayPageName ()
    {
        if (this.isProjectMode)
            this.mvHelper.notifySelectedProjectParameterPage ();
        else
            this.mvHelper.notifySelectedTrackParameterPage ();
    }


    /**
     * Toggle between the project and track parameters.
     */
    public void toggleMode ()
    {
        this.isProjectMode = !this.isProjectMode;
        this.switchBanks (this.isProjectMode ? this.model.getProject ().getParameterBank () : this.model.getCursorTrack ().getParameterBank ());
        this.setParameterProvider (this.isProjectMode ? this.projectParameterProvider : this.trackParameterProvider);
        this.bindControls ();

        this.displayPageName ();
    }
}