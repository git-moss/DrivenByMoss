// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractMode;


/**
 * Mode that does nothing.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DummyMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractMode<S, C, IItem>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DummyMode (final S surface, final IModel model)
    {
        super ("Dummy", surface, model, true, null, null);
    }
}