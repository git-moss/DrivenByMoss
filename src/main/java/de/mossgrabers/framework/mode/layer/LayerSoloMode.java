// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.layer;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ILayer;


/**
 * The layer solo mode.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayerSoloMode<S extends IControlSurface<C>, C extends Configuration> extends DefaultLayerMode<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public LayerSoloMode (final S surface, final IModel model)
    {
        super ("Layer Solo", surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeMethod (final ILayer layer)
    {
        layer.toggleSolo ();
    }
}
