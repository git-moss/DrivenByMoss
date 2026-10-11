// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.trigger;

import de.mossgrabers.controller.ni.maschine.Maschine;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.trigger.view.ToggleShiftViewCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Show/hide the shift view. Furthermore, it allows button combinations in session view.
 * Additionally, toggles the knob speed. This command requires that a session and shift view is
 * registered.
 *
 * @author Jürgen Moßgraber
 */
public class MaschineToggleShiftViewCommand extends ToggleShiftViewCommand<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MaschineToggleShiftViewCommand (final IModel model, final MaschineControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (this.surface.isPressed (ButtonID.ALT))
            return;

        switch (event)
        {
            case DOWN:
                if (this.surface.getMaschine () != Maschine.MK3)
                    this.surface.setKnobSensitivityIsSlow (true);
                this.handleOnDown ();
                break;

            case LONG:
                // Not used
                return;

            case UP:
                if (this.surface.getMaschine () != Maschine.MK3)
                    this.surface.setKnobSensitivityIsSlow (false);
                this.handleUp ();
                break;
        }
    }
}
