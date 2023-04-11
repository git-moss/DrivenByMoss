// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.command.continuous;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command to change a device parameter.
 *
 * @author Jürgen Moßgraber
 */
public class TouchpadCommand extends AbstractContinuousCommand<SLControlSurface, SLConfiguration>
{
    private final boolean isXDirection;


    /**
     * Constructor.
     *
     * @param isXDirection X-direction if true otherwise Y-direction
     * @param model The model
     * @param surface The surface
     */
    public TouchpadCommand (final boolean isXDirection, final IModel model, final SLControlSurface surface)
    {
        super (model, surface);
        this.isXDirection = isXDirection;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        switch (this.surface.getConfiguration ().getTouchpadMode ())
        {
            case SLConfiguration.TOUCHPAD_MODE_CROSSFADER:
                if (this.isXDirection)
                    this.model.getTransport ().setCrossfade (value);
                // Cross-fade only in X direction
                break;

            case SLConfiguration.TOUCHPAD_MODE_PARAMETER:
                this.model.getCursorDevice ().getParameterBank ().getItem (this.isXDirection ? 0 : 1).setValue (value);
                break;

            default:
                // Not used
                break;
        }
    }
}
