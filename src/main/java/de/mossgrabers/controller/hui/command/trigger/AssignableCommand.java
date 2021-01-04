// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.hui.command.trigger;

import de.mossgrabers.controller.hui.HUIConfiguration;
import de.mossgrabers.controller.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.command.continuous.FootswitchCommand;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command for assignable functions.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AssignableCommand extends FootswitchCommand<HUIControlSurface, HUIConfiguration>
{
    private final int index;


    /**
     * Constructor.
     *
     * @param index The index of the assignable button
     * @param model The model
     * @param surface The surface
     */
    public AssignableCommand (final int index, final IModel model, final HUIControlSurface surface)
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
