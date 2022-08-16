package de.mossgrabers.controller.novation.launchcontrol.mode;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode to select device parameter pages.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLSelectDeviceParamsPageMode extends AbstractMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration, ITrack>
{
    private boolean pageHasBeenSelected = false;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public XLSelectDeviceParamsPageMode (final LaunchControlXLControlSurface surface, final IModel model)
    {
        super ("Select Device Page", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || row != 0)
            return;

        final IParameterPageBank parameterPageBank = this.model.getCursorDevice ().getParameterPageBank ();
        if (index < parameterPageBank.getItemCount ())
        {
            parameterPageBank.selectPage (index);
            this.pageHasBeenSelected = true;
            this.mvHelper.notifySelectedDeviceAndParameterPage ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.pageHasBeenSelected = false;
    }


    /**
     * Has a page been selected during the last active phase of the mode?
     *
     * @return True if a page has been selected
     */
    public boolean hasPageBeenSelected ()
    {
        return this.pageHasBeenSelected;
    }
}
