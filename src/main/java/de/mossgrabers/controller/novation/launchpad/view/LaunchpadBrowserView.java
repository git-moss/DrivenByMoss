// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IScrollableView;
import de.mossgrabers.framework.utils.ScrollStates;
import de.mossgrabers.framework.view.BrowserView;


/**
 * Navigate the browser.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadBrowserView extends BrowserView<LaunchpadControlSurface, LaunchpadConfiguration> implements IScrollableView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public LaunchpadBrowserView (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateScrollStates (final ScrollStates scrollStates)
    {
        final IBrowser browser = this.model.getBrowser ();
        scrollStates.setCanScrollLeft (browser.hasPreviousContentType ());
        scrollStates.setCanScrollRight (browser.hasNextContentType ());
        scrollStates.setCanScrollUp (true);
        scrollStates.setCanScrollDown (true);
    }
}
