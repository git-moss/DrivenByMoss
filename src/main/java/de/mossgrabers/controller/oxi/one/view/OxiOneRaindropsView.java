// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.view;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractRaindropsView;


/**
 * The Raindrops Sequencer view.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneRaindropsView extends AbstractRaindropsView<OxiOneControlSurface, OxiOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public OxiOneRaindropsView (final OxiOneControlSurface surface, final IModel model)
    {
        super (Views.NAME_RAINDROPS, surface, model, true, 16);
    }
}