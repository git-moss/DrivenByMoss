// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.command.continuous;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.sl.SLConfiguration;
import de.mossgrabers.sl.controller.SLControlSurface;


/**
 * Dedicated Tap Tempo and Tempo Data Input Knob on MKI.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TapTempoMkICommand extends AbstractContinuousCommand<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TapTempoMkICommand (final Model model, final SLControlSurface surface)
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
