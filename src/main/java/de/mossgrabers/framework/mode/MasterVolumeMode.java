// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import java.util.Collections;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;


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
    private boolean                      controlLastParamActive = false;
    private final FixedParameterProvider masterVolumeProvider;
    private final FixedParameterProvider focusedParameterProvider;


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

        this.masterVolumeProvider = new FixedParameterProvider (this.model.getMasterTrack ().getVolumeParameter ());
        this.focusedParameterProvider = new FixedParameterProvider (this.model.getFocusedParameter ().get ());
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.setParameterProvider (this.masterVolumeProvider);
        this.setParameterProvider (ButtonID.SHIFT, new FixedParameterProvider (this.model.getTransport ().getMetronomeVolumeParameter ()));
        this.setParameterProvider (ButtonID.SELECT, new FixedParameterProvider (this.model.getApplication ().getZoomParameter ()));

        super.onActivate ();
    }


    /**
     * Dis-/enable controlling the last touched/clicked parameter.
     */
    public void toggleControlLastParamActive ()
    {
        this.setControlLastParamActive (!this.controlLastParamActive);
    }


    /**
     * De-/activate controlling the last touched/clicked parameter .
     *
     * @param active True to activate
     */
    public void setControlLastParamActive (final boolean active)
    {
        this.controlLastParamActive = active;

        this.setParameterProvider (this.controlLastParamActive ? this.focusedParameterProvider : this.masterVolumeProvider);
        this.bindControls ();
    }


    /**
     * Is controlling the last touched/clicked parameter active?
     *
     * @return True if active
     */
    public boolean isControlLastParamActive ()
    {
        return this.controlLastParamActive;
    }
}
