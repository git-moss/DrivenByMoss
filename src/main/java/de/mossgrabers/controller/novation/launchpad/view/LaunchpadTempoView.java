// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.IScrollableView;
import de.mossgrabers.framework.utils.ScrollStates;
import de.mossgrabers.framework.view.TempoView;


/**
 * The tempo view.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadTempoView extends TempoView<LaunchpadControlSurface, LaunchpadConfiguration> implements IScrollableView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param textColor1 The color of the 1st and 3rd digit
     * @param textColor2 The color of 2nd digit
     * @param backgroundColor The background color
     */
    public LaunchpadTempoView (final LaunchpadControlSurface surface, final IModel model, final int textColor1, final int textColor2, final int backgroundColor)
    {
        super (surface, model, textColor1, textColor2, backgroundColor);
    }


    /** {@inheritDoc} */
    @Override
    public void updateScrollStates (final ScrollStates scrollStates)
    {
        scrollStates.setAll (true);
    }
}
