// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.command.continuous;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.sl.SLConfiguration;
import de.mossgrabers.sl.controller.SLControlSurface;


/**
 * Command to change the volumes of the current track bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FaderCommand extends AbstractContinuousCommand<SLControlSurface, SLConfiguration>
{
    private int index;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public FaderCommand (final int index, final Model model, final SLControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        this.model.getCurrentTrackBank ().setVolume (this.index, value);
    }
}
