// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc.command.continuous;

import de.mossgrabers.apc.APCConfiguration;
import de.mossgrabers.apc.controller.APCControlSurface;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;


/**
 * Command to change the crossfader.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CrossfaderCommand extends AbstractContinuousCommand<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public CrossfaderCommand (final Model model, final APCControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        this.model.getTransport ().setCrossfade (value);
    }
}
