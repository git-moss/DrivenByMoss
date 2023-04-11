package de.mossgrabers.controller.novation.launchcontrol.mode.buttons;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.mode.layer.LayerMuteMode;


/**
 * The layer mute mode. Adds specific button coloring.
 *
 * @author Jürgen Moßgraber
 */
public class XLLayerMuteMode extends LayerMuteMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public XLLayerMuteMode (final LaunchControlXLControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.ROW2_1.ordinal ();
        if (index < 0 || index >= 8)
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        final ILayer layer = this.bank.getItem (index);
        if (!layer.doesExist ())
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        return layer.isMute () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_AMBER_LO;
    }
}
