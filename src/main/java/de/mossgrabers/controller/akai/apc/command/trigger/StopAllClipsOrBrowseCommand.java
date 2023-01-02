// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.command.trigger;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Stop all playing clips. Activate the browser if shifted.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class StopAllClipsOrBrowseCommand extends AbstractTriggerCommand<APCControlSurface, APCConfiguration>
{
    private final APCBrowserCommand apcBrowserCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public StopAllClipsOrBrowseCommand (final IModel model, final APCControlSurface surface)
    {
        super (model, surface);

        this.apcBrowserCommand = new APCBrowserCommand (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.model.getCurrentTrackBank ().stop ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.apcBrowserCommand.startBrowser (false, false);
    }
}
