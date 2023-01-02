// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.command.continuous;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.IModel;


/**
 * Dedicated Tap Tempo and Tempo Input Knob on MKI.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TapTempoInitMkICommand extends AbstractContinuousCommand<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TapTempoInitMkICommand (final IModel model, final SLControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        this.surface.setLastCC94Value (value);
    }
}
