// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.command.trigger;

import de.mossgrabers.controller.maschine.mikro.mk3.MaschineMikroMk3Configuration;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.controller.maschine.mikro.mk3.view.PadButtons;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.View;


/**
 * Command for using one of the 4 keys above the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GridButtonCommand extends AbstractTriggerCommand<MaschineMikroMk3ControlSurface, MaschineMikroMk3Configuration>
{
    private final int index;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public GridButtonCommand (final int index, final IModel model, final MaschineMikroMk3ControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final View activeView = this.surface.getViewManager ().getActiveView ();
        if (activeView instanceof PadButtons)
            ((PadButtons) activeView).onButton (this.index, event);
    }
}
