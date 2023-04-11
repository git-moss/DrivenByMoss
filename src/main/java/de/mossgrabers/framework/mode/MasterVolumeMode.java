// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;

import java.util.Collections;


/**
 * Mode for changing the master volume and metronome volume.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class MasterVolumeMode<S extends IControlSurface<C>, C extends Configuration> extends AbstractParameterMode<S, C, IItem>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param masterID The ID of the master control
     */
    public MasterVolumeMode (final S surface, final IModel model, final ContinuousID masterID)
    {
        super ("Master Volume", surface, model, true, null, Collections.singletonList (masterID));

        this.setParameterProvider (new FixedParameterProvider (this.model.getMasterTrack ().getVolumeParameter ()));
        this.setParameterProvider (ButtonID.SHIFT, new FixedParameterProvider (this.model.getTransport ().getMetronomeVolumeParameter ()));
        this.setParameterProvider (ButtonID.SELECT, new FixedParameterProvider (this.model.getApplication ().getZoomParameter ()));
    }
}
