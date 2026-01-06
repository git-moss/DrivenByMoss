// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.trigger.clip.DoubleCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to toggle automation modes.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineJamDoubleCommand extends DoubleCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MaschineJamDoubleCommand (final IModel model, final MaschineJamControlSurface surface)
    {
        super (model, surface, true);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        super.executeShifted (event);

        if (event == ButtonEvent.DOWN && this.surface.isShiftPressed ())
            this.surface.setTriggerConsumed (ButtonID.SHIFT);
    }
}
