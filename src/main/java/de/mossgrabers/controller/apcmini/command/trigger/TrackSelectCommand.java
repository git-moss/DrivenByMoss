// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini.command.trigger;

import de.mossgrabers.controller.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.controller.apcmini.view.APCminiView;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.View;


/**
 * Command to show/hide the shift view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackSelectCommand extends AbstractTriggerCommand<APCminiControlSurface, APCminiConfiguration>
{
    private int index;


    /**
     * Constructor.
     *
     * @param index The channel index
     * @param model The model
     * @param surface The surface
     */
    public TrackSelectCommand (final int index, final IModel model, final APCminiControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        final View view = this.surface.getViewManager ().getActiveView ();
        if (view != null)
            ((APCminiView) view).onSelectTrack (this.index, event);
    }
}
