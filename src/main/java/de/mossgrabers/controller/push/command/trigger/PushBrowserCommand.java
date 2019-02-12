// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.command.trigger;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.view.DrumView;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Command to open the browser.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushBrowserCommand extends BrowserCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param browserMode The ID of the mode to activate for browsing
     * @param model The model
     * @param surface The surface
     */
    public PushBrowserCommand (final Integer browserMode, final IModel model, final PushControlSurface surface)
    {
        super (browserMode, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void discardBrowser (final boolean commit)
    {
        super.discardBrowser (commit);

        if (!commit)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.isActiveView (Views.VIEW_DRUM))
            ((DrumView) viewManager.getView (Views.VIEW_DRUM)).repositionBankPage ();
    }
}
