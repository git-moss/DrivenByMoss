// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;

import java.util.List;


/**
 * Mode that does nothing.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class DummyMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractParameterMode<S, C, IItem>
{
    /**
     * Constructor. Does not map any parameters.
     *
     * @param surface The control surface
     * @param model The model
     */
    public DummyMode (final S surface, final IModel model)
    {
        this (surface, model, null);
    }


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode, each control gets
     *            assigned an empty parameter
     */
    public DummyMode (final S surface, final IModel model, final List<ContinuousID> controls)
    {
        super ("Dummy", surface, model, true, null, controls);

        if (controls != null)
            this.setParameterProvider (new EmptyParameterProvider (controls.size ()));
    }
}