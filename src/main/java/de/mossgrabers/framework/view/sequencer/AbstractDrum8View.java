// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view.sequencer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.view.Views;


/**
 * Abstract implementation for a drum sequencer with 8 lanes (sounds).
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractDrum8View<S extends IControlSurface<C>, C extends Configuration> extends AbstractDrumLaneView<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param useDawColors True to use the drum machine pad colors for coloring the octaves
     */
    protected AbstractDrum8View (final S surface, final IModel model, final boolean useDawColors)
    {
        super (Views.NAME_DRUM8, surface, model, 8, 8, useDawColors);
    }
}
