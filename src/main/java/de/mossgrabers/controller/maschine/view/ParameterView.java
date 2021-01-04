// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.view;

import de.mossgrabers.controller.maschine.controller.MaschineColorManager;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.device.SelectedDeviceMode;


/**
 * The Parameter view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParameterView extends BaseView
{
    private static final int [] PARAM_COLORS =
    {
        MaschineColorManager.COLOR_RED,
        MaschineColorManager.COLOR_AMBER,
        MaschineColorManager.COLOR_YELLOW,
        MaschineColorManager.COLOR_GREEN,
        MaschineColorManager.COLOR_LIME,
        MaschineColorManager.COLOR_SKY,
        MaschineColorManager.COLOR_PURPLE,
        MaschineColorManager.COLOR_PINK
    };


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public ParameterView (final MaschineControlSurface surface, final IModel model)
    {
        super ("Parameter", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeFunction (final int padIndex)
    {
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IHost host = this.model.getHost ();
        if (!cursorDevice.doesExist ())
        {
            host.showNotification ("No device selected.");
            return;
        }

        switch (padIndex)
        {
            case 12:
                cursorDevice.selectPrevious ();
                break;
            case 13:
                cursorDevice.selectNext ();
                break;
            case 14:
                cursorDevice.getParameterBank ().scrollBackwards ();
                this.mvHelper.notifySelectedParameterPage ();
                break;
            case 15:
                cursorDevice.getParameterBank ().scrollForwards ();
                this.mvHelper.notifySelectedParameterPage ();
                break;
            default:
                // Not used
                break;
        }

        if (padIndex >= 8)
            return;

        // Flip row 2 and 1 to look the same as in the Bitwig device display
        final int selectedParameter = padIndex < 4 ? padIndex + 4 : padIndex - 4;

        ((SelectedDeviceMode<?, ?>) this.surface.getModeManager ().get (Modes.DEVICE_PARAMS)).selectParameter (selectedParameter);
        this.model.getHost ().scheduleTask ( () -> {

            final StringBuilder message = new StringBuilder ();
            final String selectedPage = cursorDevice.getParameterPageBank ().getSelectedItem ();
            if (selectedPage == null)
                message.append ("No parameters available.");
            else
            {
                message.append (selectedPage).append (": ");
                final IParameter item = cursorDevice.getParameterBank ().getItem (selectedParameter);
                if (item.doesExist ())
                    message.append (item.getName ());
                else
                    message.append ("None");
            }
            host.showNotification (message.toString ());

        }, 200);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        for (int i = 8; i < 12; i++)
            padGrid.lightEx (i % 4, 3 - i / 4, AbstractFeatureGroup.BUTTON_COLOR_OFF);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final boolean doesExist = cursorDevice.doesExist ();
        padGrid.lightEx (0, 0, doesExist && cursorDevice.canSelectPreviousFX () ? MaschineColorManager.COLOR_ROSE : MaschineColorManager.COLOR_BLACK);
        padGrid.lightEx (1, 0, doesExist && cursorDevice.canSelectNextFX () ? MaschineColorManager.COLOR_ROSE : MaschineColorManager.COLOR_BLACK);

        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        padGrid.lightEx (2, 0, doesExist && parameterBank.canScrollBackwards () ? MaschineColorManager.COLOR_SKIN : MaschineColorManager.COLOR_BLACK);
        padGrid.lightEx (3, 0, doesExist && parameterBank.canScrollForwards () ? MaschineColorManager.COLOR_SKIN : MaschineColorManager.COLOR_BLACK);

        final SelectedDeviceMode<?, ?> deviceMode = (SelectedDeviceMode<?, ?>) this.surface.getModeManager ().get (Modes.DEVICE_PARAMS);
        for (int i = 0; i < 8; i++)
        {
            final int x = i % 4;
            final int y = 2 + i / 4;

            final IParameter item = parameterBank.getItem (i);
            if (item.doesExist ())
            {
                if (i == deviceMode.getSelectedParameter ())
                    padGrid.lightEx (x, y, PARAM_COLORS[i], MaschineColorManager.COLOR_WHITE, false);
                else
                    padGrid.lightEx (x, y, PARAM_COLORS[i]);
            }
            else
                padGrid.lightEx (x, y, AbstractFeatureGroup.BUTTON_COLOR_OFF);
        }
    }
}