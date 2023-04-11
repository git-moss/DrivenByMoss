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
 * @author Jürgen Moßgraber
 */
public class TapTempoMkICommand extends AbstractContinuousCommand<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TapTempoMkICommand (final IModel model, final SLControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        int iter = this.surface.getLastCC94Value ();
        int tempo = 0;
        while (iter > 0)
        {
            tempo += 128;
            iter--;
        }
        tempo += value;
        this.model.getTransport ().setTempo (tempo);
    }
}
