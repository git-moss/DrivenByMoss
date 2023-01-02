// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.command.continuous;

import de.mossgrabers.controller.akai.apc.APCConfiguration;
import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.command.TempoCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.Timeout;
import de.mossgrabers.framework.view.Views;


/**
 * Additionally, display BPM on the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCTempoCommand extends TempoCommand<APCControlSurface, APCConfiguration>
{
    private final Timeout timeout;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param timeout The timeout object
     */
    public APCTempoCommand (final IModel model, final APCControlSurface surface, final Timeout timeout)
    {
        super (model, surface);

        this.timeout = timeout;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        super.execute (value);

        final ViewManager viewManager = this.surface.getViewManager ();
        if (!viewManager.isActive (Views.TEMPO))
            viewManager.setTemporary (Views.TEMPO);
        this.timeout.delay (viewManager::restore);
    }
}
