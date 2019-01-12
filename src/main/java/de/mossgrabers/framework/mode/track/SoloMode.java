// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.AbstractMode;


/**
 * The track solo mode.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SoloMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractMode<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SoloMode (final S surface, final IModel model)
    {
        super ("Solo", surface, model, true);
        this.isTemporary = false;
    }
}
