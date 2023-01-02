// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchcontrol.mode.faders;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;

import java.util.List;


/**
 * The 8 faders control the 8 gains of the EQ bands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLEqGainMode extends AbstractParameterMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration, IParameter>
{
    private final IEqualizerDevice eqDevice;
    private final List<IParameter> parameters;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public XLEqGainMode (final LaunchControlXLControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super ("EQ Gain", surface, model, true, model.getCursorDevice ().getParameterBank (), controls);

        this.eqDevice = (IEqualizerDevice) model.getSpecificDevice (DeviceID.EQ);
        this.parameters = this.eqDevice.getGainParameters ();

        this.setParameterProvider (new FixedParameterProvider (this.parameters));
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        if (!this.eqDevice.doesExist ())
            return -1;
        final IParameter item = this.parameters.get (index);
        return item.doesExist () ? item.getValue () : -1;
    }
}