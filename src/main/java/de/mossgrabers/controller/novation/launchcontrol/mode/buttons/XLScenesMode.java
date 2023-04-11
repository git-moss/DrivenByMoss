package de.mossgrabers.controller.novation.launchcontrol.mode.buttons;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode to select scenes.
 *
 * @author Jürgen Moßgraber
 */
public class XLScenesMode extends XLTemporaryButtonMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public XLScenesMode (final LaunchControlXLControlSurface surface, final IModel model)
    {
        super ("Select Scene", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row != 0 || event == ButtonEvent.LONG)
            return;

        final IScene item = this.model.getSceneBank ().getItem (index);
        if (!item.doesExist ())
            return;

        final boolean isDown = event == ButtonEvent.DOWN;
        if (isDown)
        {
            this.setHasBeenUsed ();
            item.select ();
        }
        item.launch (isDown, false);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.ROW2_1.ordinal ();
        if (index < 0 || index >= 8)
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        final IScene item = this.model.getSceneBank ().getItem (index);
        if (!item.doesExist ())
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
        return item.isSelected () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW_LO;
    }
}
