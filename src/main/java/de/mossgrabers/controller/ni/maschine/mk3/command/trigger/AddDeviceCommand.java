// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.command.trigger;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.trigger.device.AddEffectCommand;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IBrowserColumn;
import de.mossgrabers.framework.daw.data.IBrowserColumnItem;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to add a device. If the browser is open it toggles the favorites.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AddDeviceCommand extends AddEffectCommand<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public AddDeviceCommand (final IModel model, final MaschineControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP)
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (browser.isActive ())
        {
            final IBrowserColumn collectionsColumn = browser.getFilterColumn (0);
            final IBrowserColumnItem first = collectionsColumn.getItems ()[0];
            if (first.getName ().equals (collectionsColumn.getWildcard ()) && first.isSelected ())
                collectionsColumn.selectNextItem ();
            else
                collectionsColumn.resetFilter ();
        }
        else
            super.executeNormal (event);
    }
}
