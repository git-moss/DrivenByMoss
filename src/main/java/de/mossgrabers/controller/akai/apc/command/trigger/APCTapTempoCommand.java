// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.command.trigger;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.trigger.transport.TapTempoCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Timeout;
import de.mossgrabers.framework.view.Views;


/**
 * Additionally, display BPM on the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCTapTempoCommand extends TapTempoCommand<APCControlSurface, APCConfiguration>
{
    private final Timeout timeout;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public APCTapTempoCommand (final IModel model, final APCControlSurface surface)
    {
        super (model, surface);

        this.timeout = new Timeout (model.getHost (), 500);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        super.executeNormal (event);

        final ViewManager viewManager = this.surface.getViewManager ();
        if (!viewManager.isActive (Views.TEMPO))
            viewManager.setTemporary (Views.TEMPO);
        this.timeout.delay (viewManager::restore);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.model.getMarkerBank ().addMarker ();
    }


    /**
     * Get the timeout object.
     *
     * @return The timeout object.
     */
    public Timeout getTimeout ()
    {
        return this.timeout;
    }
}
