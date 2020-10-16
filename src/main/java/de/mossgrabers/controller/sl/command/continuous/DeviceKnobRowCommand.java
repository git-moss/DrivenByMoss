// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.command.continuous;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;


/**
 * Command to change a device parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceKnobRowCommand extends AbstractContinuousCommand<SLControlSurface, SLConfiguration>
{
    private int index;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public DeviceKnobRowCommand (final int index, final IModel model, final SLControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        if (!this.model.hasSelectedDevice ())
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        if (!modeManager.isActive (Modes.DEVICE_PARAMS))
            modeManager.setActive (Modes.DEVICE_PARAMS);
        modeManager.get (Modes.DEVICE_PARAMS).onKnobValue (this.index, value);
    }
}
