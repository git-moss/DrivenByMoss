package de.mossgrabers.controller.novation.launchcontrol.mode.buttons;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode to select device parameter pages.
 *
 * @author Jürgen Moßgraber
 */
public class XLSelectDeviceParamsPageMode extends XLTemporaryButtonMode
{
    private final ISpecificDevice device;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param device The device for which to select the page
     */
    public XLSelectDeviceParamsPageMode (final LaunchControlXLControlSurface surface, final IModel model, final ISpecificDevice device)
    {
        super ("Select Device Page", surface, model);

        this.device = device;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || row != 0)
            return;

        this.setHasBeenUsed ();

        final IParameterPageBank parameterPageBank = this.device.getParameterPageBank ();
        if (index < parameterPageBank.getItemCount ())
        {
            parameterPageBank.selectPage (index);
            this.mvHelper.notifySelectedDeviceAndParameterPage ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.ROW2_1.ordinal ();
        if (index < 0 || index >= 8)
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        final IParameterPageBank parameterPageBank = this.device.getParameterPageBank ();
        if (index >= parameterPageBank.getItemCount ())
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        return index == parameterPageBank.getSelectedItemIndex () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW_LO;
    }
}
