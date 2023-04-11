package de.mossgrabers.controller.novation.launchcontrol.mode.buttons;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.track.TrackRecArmMode;


/**
 * The track mute mode. Adds specific button coloring.
 *
 * @author Jürgen Moßgraber
 */
public class XLTrackRecArmMode extends TrackRecArmMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public XLTrackRecArmMode (final LaunchControlXLControlSurface surface, final IModel model)
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

        final ITrack track = this.model.getTrackBank ().getItem (index);
        if (!track.doesExist ())
            return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

        return track.isRecArm () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_RED_LO;
    }
}
