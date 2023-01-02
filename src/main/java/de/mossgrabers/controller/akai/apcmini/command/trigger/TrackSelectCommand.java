// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.command.trigger;

import de.mossgrabers.controller.akai.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.controller.akai.apcmini.view.APCminiView;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to show/hide the shift view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackSelectCommand extends AbstractTriggerCommand<APCminiControlSurface, APCminiConfiguration>
{
    private final int index;


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
        final IView view = this.surface.getViewManager ().getActive ();
        if (view != null)
            ((APCminiView) view).onSelectTrack (this.index, event);
    }
}
