// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractRaindropsView;


/**
 * The Raindrops Sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RaindropsView extends AbstractRaindropsView<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public RaindropsView (final PushControlSurface surface, final IModel model)
    {
        super (Views.NAME_RAINDROPS, surface, model, true);
    }
}