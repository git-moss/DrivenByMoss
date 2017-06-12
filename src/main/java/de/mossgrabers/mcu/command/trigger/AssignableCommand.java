// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.command.trigger;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.continuous.FootswitchCommand;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;


/**
 * Command for assignable functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AssignableCommand extends FootswitchCommand<MCUControlSurface, MCUConfiguration>
{
    private int index;


    /**
     * Constructor.
     * 
     * @param index The index of the assignable button
     * @param model The model
     * @param surface The surface
     */
    public AssignableCommand (final int index, final Model model, final MCUControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    protected int getSetting ()
    {
        return this.surface.getConfiguration ().getAssignable (this.index);
    }
}
