// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.command.trigger;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.controller.maschine.view.PadButtons;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.View;


/**
 * Command for using one of the 4 keys above the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GridButtonCommand extends AbstractTriggerCommand<MaschineControlSurface, MaschineConfiguration>
{
    private final int index;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public GridButtonCommand (final int index, final IModel model, final MaschineControlSurface surface)
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
